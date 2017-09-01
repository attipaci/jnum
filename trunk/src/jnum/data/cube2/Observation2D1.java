/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube2;

import jnum.ExtraMath;
import jnum.Unit;
import jnum.data.IndexedObservations;
import jnum.data.Observations;
import jnum.data.cube.Data3D;
import jnum.data.cube.Index3D;
import jnum.data.cube.Values3D;
import jnum.data.cube.overlay.Overlay3D;
import jnum.data.image.Data2D;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Observation2D;
import jnum.math.Vector3D;
import jnum.parallel.ParallelPointOp;

public class Observation2D1 extends AbstractMap2D1<Observation2D> implements Observations<Data3D>, IndexedObservations<Index3D> {
 
    public Observation2D1(Class<? extends Number> dataType, Class<? extends Number> weightType, int flagType) {
        super(dataType, flagType);
    }
    
    
    public Observation2D1 copy(boolean withContents) {
        Observation2D1 copy = (Observation2D1) super.clone();
        for(int k=sizeZ(); --k >= 0; ) copy.setPlane(k, getPlane(k).copy());
        return copy;
    }
    
    @Override
    public Observation2D getPlaneInstance() { return new Observation2D(getElementType(), getElementType(), getFlagType()); }
  
    @Override
    public Data2D1<Data2D> getWeights() {
        Data2D1<Data2D> weight = new Data2D1<Data2D>() {
            @Override
            public Data2D getImage2DInstance(int sizeX, int sizeY) { return null; }
        };
        for(int i=0; i<sizeZ(); i++) weight.addPlane(getPlane(i).getWeights());
        return weight;
    }
    
    @Override
    public Data2D1<Data2D> getExposures() {
        Data2D1<Data2D> weight = new Data2D1<Data2D>() {
            @Override
            public Data2D getImage2DInstance(int sizeX, int sizeY) { return null; }
        };
        for(int i=0; i<sizeZ(); i++) weight.addPlane(getPlane(i).getExposures());
        return weight;
    }
    

    @Override
    public Overlay3D getNoise() {
          
        return new Overlay3D(this) {   
         
            @Override
            public Number get(int i, int j, int k) {
                return getPlane(k).noiseAt(i, j);
            }

            @Override
            public void set(int i, int j, int k, Number value) {
                super.set(i, j, k, 1.0 / (value.doubleValue() * value.doubleValue()));
            }    
            
            @Override
            public void add(int i, int j, int k, Number value) {
                set(i, j, k, get(i, j, k).doubleValue() + value.doubleValue());
            }
            
            @Override
            protected void setDefaultUnit() { setUnit(Observation2D1.this.getUnit()); }
        

        };
       
    }

    @Override
    public Overlay3D getSignificance() {
        return new Overlay3D(this) {

            @Override
            public Number get(int i, int j, int k) {
                return getPlane(k).significanceAt(i, j);
            }

            @Override
            public void set(int i, int j, int k, Number value) {
                super.set(i, j, k, value.doubleValue() * getPlane(k).noiseAt(i, j));
            }  
            
            @Override
            public void add(int i, int j, int k, Number value) {
                set(i, j, k, get(i, j, k).doubleValue() + value.doubleValue());
            }    
        
            @Override
            protected void setDefaultUnit() { super.setUnit(Unit.unity); }
          
            @Override
            public void setUnit(Unit u) {
                throw new UnsupportedOperationException("Cannot change units of S/N image.");
            }
        };
    }

    
    
    public void accumulate(final Observation2D1 cube, final double weight) {
        for(int k=sizeZ(); --k >= 0; ) getPlane(k).accumulate(cube.getPlane(k), weight);
    }
    
    
    @Override
    public void endAccumulation() {   
        for(int k=sizeZ(); --k >= 0; ) getPlane(k).endAccumulation();
    }


    public final void mergeAccumulate(final Observation2D1 cube) {
        super.add(cube);
        for(int k=sizeZ(); --k >= 0; ) getPlane(k).mergeAccumulate(cube.getPlane(k));
         
    }
    


    public final synchronized void accumulateAt(final Vector3D index, final double value, final double gain, final double w, final double time) {
        accumulateAt((int)Math.round(index.x()), (int)Math.round(index.y()), (int)Math.round(index.z()), value, gain, w, time);
    }


    public final synchronized void accumulateAt(final Index3D index, final double value, final double gain, final double w, final double time) {
        accumulateAt(index.i(), index.j(), index.k(), value, gain, w, time);
    }


    public final void accumulateAt(final int i, final int j, final int k, final double value, final double gain, double w, final double time) {
        getPlane(k).accumulateAt(i, j, value, gain, w, time);
    }


    @Override
    public double noiseAt(Index3D index) {
        return getPlane(index.k()).noiseAt(index.i(), index.j());
    }
    
    public double noiseAt(int i, int j, int k) {
        return getPlane(k).noiseAt(i, j);
        
    }
    
    @Override
    public double weightAt(Index3D index) {
        return getPlane(index.k()).weightAt(index.i(), index.j());
    }

    public double weightAt(int i, int j, int k) {
        return getPlane(k).weightAt(i, j);
        
    }

    @Override
    public double significanceAt(Index3D index) {
        return getPlane(index.k()).significanceAt(index.i(), index.j());
    }

    public double significanceAt(int i, int j, int k) {
        return getPlane(k).significanceAt(i, j);  
    }
    
    
    @Override
    public void setNoiseAt(Index3D index, double value) {
        getPlane(index.k()).setNoiseAt(index.i(), index.j(), value);
    }


    @Override
    public void setWeightAt(Index3D index, double value) {
        getPlane(index.k()).setWeightAt(index.i(), index.j(), value);
    }


    @Override
    public double exposureAt(Index3D index) {
        return getPlane(index.k()).exposureAt(index.i(), index.j());
    }

    
    // TODO move to IndexedObservations as default method.
    public void memCorrect(final Values3D model, final double lambda) {
        smartFork(new ParallelPointOp.Simple<Index3D>() {

         @Override
         public void process(Index3D index) {
             if(isValid(index)) {
                 final double noise = noiseAt(index);
                 final double target = model == null ? 0.0 : model.get(index).doubleValue();
                 final double memValue = ExtraMath.hypot(get(index).doubleValue(), noise) / ExtraMath.hypot(target, noise);
                 add(index, -Math.signum(get(index).doubleValue()) * lambda * noise * Math.log(memValue));
             }
         }            
        });    
        
    }   
   
    
    public void smooth2D(double FWHM) {
        for(Observation2D plane : getPlanes()) plane.smooth(FWHM);
    }
    
    public void smooth2DTo(double FWHM) {
        for(Observation2D plane : getPlanes()) plane.smoothTo(FWHM);
    }
    
    public void smooth2D(Gaussian2D psf) {
        for(Observation2D plane : getPlanes()) plane.smooth(psf);
    }
    
    public void smooth2DTo(Gaussian2D psf) {
        for(Observation2D plane : getPlanes()) plane.smoothTo(psf);
    }
    
    public void filterBeamCorrect() {
        for(Observation2D plane : getPlanes()) plane.filterBeamCorrect();
    }
    
    public void resetFiltering() {
        for(Observation2D plane : getPlanes()) plane.getProperties().resetFiltering();
    }
    
    public void crop2D(int fromi, int fromj, int toi, int toj) {
        for(Observation2D plane : getPlanes()) plane.crop(fromi, fromj, toi, toj);
    }
    
    public void crop2D(double fromX, double fromY, double toX, double toY) {
        for(Observation2D plane : getPlanes()) plane.crop(fromX, fromY, toX, toY);
    }
    
}

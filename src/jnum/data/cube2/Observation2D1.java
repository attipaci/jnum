/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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


import java.util.ArrayList;

import jnum.Unit;
import jnum.data.IndexedObservations;
import jnum.data.Observations;
import jnum.data.Statistics;
import jnum.data.WeightedPoint;
import jnum.data.cube.Data3D;
import jnum.data.cube.Index3D;
import jnum.data.cube.Values3D;
import jnum.data.cube.overlay.Overlay3D;
import jnum.data.image.Data2D;
import jnum.data.image.Observation2D;
import jnum.fits.FitsToolkit;
import jnum.math.Vector3D;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;


public class Observation2D1 extends AbstractMap2D1<Observation2D> implements Observations<Data3D>, IndexedObservations<Index3D> {
    private Class<? extends Number> weightType;
    
    
    public Observation2D1(Class<? extends Number> dataType, Class<? extends Number> weightType, int flagType) {
        super(dataType, flagType);
        this.weightType = weightType;
    }


    public Observation2D1 copy(boolean withContents) {
        Observation2D1 copy = (Observation2D1) super.clone();
        for(int k=sizeZ(); --k >= 0; ) copy.setPlane(k, getPlane(k).copy());
        return copy;
    }

    @Override
    public Observation2D newPlaneInstance() { 
        return new Observation2D(getElementType(), weightType == null ? getElementType() : weightType, getFlagType()); 
    }


    @Override
    public Data2D1<Data2D> getWeights() {
        Data2D1<Data2D> weight = new Data2D1<Data2D>(sizeZ()) {
            @Override
            public Data2D newPlaneInstance() { return null; }
        };

        for(int i=0; i<sizeZ(); i++) weight.addPlane(getPlane(i).getWeights());

        return weight;
    }

    @Override
    public Data2D1<Data2D> getExposures() {
        Data2D1<Data2D> exposure = new Data2D1<Data2D>(sizeZ()) {
            @Override
            public Data2D newPlaneInstance() { return null; }
        };

        for(int i=0; i<sizeZ(); i++) exposure.addPlane(getPlane(i).getExposures());

        return exposure;
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
        for(int k=sizeZ(); --k >= 0; ) getPlane(k).mergeAccumulate(cube.getPlane(k));
    }



    public final synchronized void accumulateAt(final Vector3D index, final double value, final double gain, final double w, final double time) {
        getPlane((int)Math.round(index.z())).accumulateAt((int)Math.round(index.x()), (int)Math.round(index.y()), value, gain, w, time);
    }


    public final synchronized void accumulateAt(final Index3D index, final double value, final double gain, final double w, final double time) {
        getPlane(index.k()).accumulateAt(index.i(), index.j(), value, gain, w, time);
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
   

    @Override
    public Observation2D getAverageZ() { 
        return getAverageZ(0, sizeZ());
    }

    @Override
    public Observation2D getAverageZ(int fromZ, int toZ) {
        final int fromk = Math.max(0, fromZ);
        final int tok = Math.min(sizeZ(), toZ);

        if(tok < fromk) return null;

        final Observation2D ave = getPlaneTemplate().copy(false);

        new ForkZ<Void>(fromk, tok) {
            @Override
            protected void processPlane(int k) {
                final Observation2D plane = getPlane(k);
                for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(plane.isValid(i, j))
                    ave.accumulateAt(i, j, plane.get(i, j).doubleValue(), 1.0, plane.weightAt(i, j), plane.exposureAt(i, j));                
            }
        }.process();

        ave.endAccumulation();

        return ave;
    }
    
    @Override
    public Observation2D getMedianZ() { 
        return getAverageZ(0, sizeZ());
    }


    @Override
    public Observation2D getMedianZ(int fromZ, int toZ) {
        final int fromk = Math.max(0, fromZ);
        final int tok = Math.min(sizeZ(), toZ);

        if(tok < fromk) return null;

        final Observation2D median = createPlane();

        median.new Fork<Void>() {
            private WeightedPoint[] sorter;

            @Override
            protected void process(int i, int j) {
                int m=0;
                double sumt = 0.0;

                if(sorter == null) sorter = WeightedPoint.createArray(tok - fromk);

                for(int k=fromk; k < tok; k++) {
                    final Observation2D plane = getPlane(k);
                    if(!plane.isValid(i, j)) continue;
                    
                    final WeightedPoint p = sorter[m++];
                    p.setValue(plane.get(i, j).doubleValue());
                    p.setWeight(plane.weightAt(i, j));
                    sumt += plane.exposureAt(i, j);
                }
                if(m > 0) {
                    WeightedPoint medianValue = Statistics.Inplace.median(sorter, 0, m);
                    median.set(i, j, medianValue.value());
                    median.setWeightAt(i, j, medianValue.weight());
                    median.setExposureAt(i, j, sumt);
                }
            }
        }.process();

        return median;   
    }


    @Override
    public WeightedPoint getMean() { return getWeightedMean(getWeights()); }

    @Override
    public WeightedPoint getMedian() { return getWeightedMedian(getWeights()); }


    public final void memCorrect(final Values3D model, final double lambda) {
        memCorrect(model, this.getNoise(), lambda);
    } 
    
    
    @Override
    public ArrayList<BasicHDU<?>> getHDUs(Class<? extends Number> dataType) throws FitsException {   
        ArrayList<BasicHDU<?>> hdus = super.getHDUs(dataType);
  
        BasicHDU<?> hdu = getExposures().createHDU(dataType);
        FitsToolkit.setName(hdu, "Exposure");
        editHeader(hdu.getHeader());
        hdus.add(hdu);
        
        hdu = getNoise().createHDU(dataType);
        editHeader(hdu.getHeader());
        FitsToolkit.setName(hdu, "Noise");
        hdus.add(hdu);

        hdu = getSignificance().createHDU(dataType);
        FitsToolkit.setName(hdu, "S/N");
        editHeader(hdu.getHeader());
        hdus.add(hdu);

        return hdus;
    }


}

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

import java.util.ArrayList;
import java.util.List;

import jnum.NonConformingException;
import jnum.PointOp;
import jnum.data.cube.Index3D;
import jnum.data.cube.Data3D;
import jnum.data.image.Data2D;

public abstract class Data2D1<ImageType extends Data2D> extends Data3D {
    
    // TODO 2D+1D split methods...
    /*
     * 
     * - collapseZ(int fromk, int tok); collapseZ()
     * - smoothXY(), filterXY()
     * 
     * 
     * - collapseXY(IndexBounds2D); collapseXY()
     * 
     */
    

    private ArrayList<ImageType> stack;
    
    
    public Data2D1() {
        stack = new ArrayList<ImageType>();
    }
    
    public Data2D1(int initialPlanesCapacity) {
        this();
        stack.ensureCapacity(initialPlanesCapacity);
    }
    
    public abstract ImageType getImage2DInstance(int sizeX, int sizeY);
   
    
    public ArrayList<ImageType> getPlanes() { return stack; }
   
    
    public final ImageType getPlane(int index) {
        return stack.get(index);
    }
    
    protected ImageType getPlane() { return stack.get(0); }
    
    
    public final void setPlane(int index, ImageType image) {
        stack.set(index, image);
    }
    
    public void addPlane(ImageType image) throws NonConformingException {
        if(!stack.isEmpty()) if(image.sizeX() != sizeX() || image.sizeY() != sizeY()) 
            throw new NonConformingException("irregular stack addition.");
        
        stack.add(image);
    }
    
    public void trim(int count) {
        for(int i=count; --i >= 0 && !stack.isEmpty(); ) stack.remove(stack.size() - 1);
    }
    
    public void trimToSize() {
        stack.trimToSize();
    }
    
    @Override
    public Class<? extends Number> getElementType() { return getPlane().getElementType(); }
    
    @Override
    public Number getLowestCompareValue() {
       return getPlane().getLowestCompareValue();
    }

    @Override
    public Number getHighestCompareValue() {
        return getPlane().getHighestCompareValue();
    }

    @Override
    public int compare(Number a, Number b) {
        return getPlane().compare(a, b);
    }

    
    @Override
    public int sizeX() { return stack.isEmpty() ? 0 : getPlane().sizeX(); }
    
    @Override
    public int sizeY() { return stack.isEmpty() ? 0 : getPlane().sizeY(); }
    
    @Override
    public int sizeZ() { return stack.size(); }
    
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        stack.clear();
        stack.ensureCapacity(sizeZ);
        for(int k=sizeZ; --k >= 0; ) stack.add(getImage2DInstance(sizeX, sizeY));
    }
    
    public void setPlanes(ImageType[] planes) {
        stack.clear();
        for(int i=0; i<planes.length; i++) addPlane(planes[i]);
    }
    
    public void setPlanes(List<ImageType> planes) {
        stack.clear();
        for(int i=0; i<planes.size(); i++) addPlane(planes.get(i));
    }
    
   
    @Override
    public boolean isValid(int i, int j, int k) { return getPlane(k).isValid(i, j); }
     
    
    @Override
    public void discard(int i, int j, int k) { getPlane(k).discard(i, j); }

    
    @Override
    public Number get(int i, int j, int k) {
        return getPlane(k).get(i, j);
    }
    
    
    @Override
    public void set(int i, int j, int k, Number value) {
        getPlane(k).set(i, j, value);
    }
    

    @Override
    public void clear(int i, int j, int k) { 
        getPlane(k).clear(i, j);
    }
    
    
    @Override
    public void add(int i, int j, int k, Number value) {
        getPlane(k).add(i, j, value);
    }
   
    @Override
    public void scale(int i, int j, int k, double factor) { 
        getPlane(k).scale(i, j, factor);
    }
    

    
    public Number nearestValueAtIndex(double i, double j, double k) {
        return getPlane((int) Math.round(k)).get((int) Math.round(i), (int) Math.round(j));   
    }
    
    
       
     
   
    
    @Override
    public double splineAtIndex(double ic, double jc, double kc, InterpolatorData ipolData) {
        ipolData.centerOn(ic, jc, kc);

     
        final int fromi = Math.max(0, ipolData.splineX.minIndex());
        final int toi = Math.min(sizeX(), ipolData.splineX.maxIndex());

        final int fromj = Math.max(0, ipolData.splineY.minIndex());
        final int toj = Math.min(sizeY(), ipolData.splineY.maxIndex());
        
        final int fromk = Math.max(0, ipolData.splineZ.minIndex());
        final int tok = Math.min(sizeZ(), ipolData.splineZ.maxIndex());
  
        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int k=tok; --k >= fromk; ) {
            final double wz = ipolData.splineZ.coefficientAt(k);
            final ImageType slice = getPlane(k);
            
            for(int i=toi; --i >= fromi; ) {
                final double wzx = wz * ipolData.splineX.coefficientAt(i);
                for(int j=toj; --j >= fromj; ) if(slice.isValid(i, j)) {
                    final double w = wzx * ipolData.splineY.coefficientAt(j);         
                    sum += w * slice.get(i, j).doubleValue();
                    sumw += w;
                }
            }
        }
        
        return sum / sumw;
        
        // ~800 ops...
    }
    

 
    
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op) {
        final Index3D index = new Index3D();
        for(int k=sizeY(); --k >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) {
            index.set(i, j, k);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    
    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op) {
        for(int k=sizeY(); --k >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; )
            if(isValid(i, j, k)) op.process(get(i, j, k));
        return op.getResult();
    }
    
   
    public abstract class ForkZ<ReturnType> extends Task<ReturnType> {           

        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=index; i<sizeZ(); i += threadCount) {
                processPlane(i);
                Thread.yield();
            }
        }

        protected abstract void processPlane(int i);

        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + capacity() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

        protected abstract void process(int i, int j);
    } 

    
}

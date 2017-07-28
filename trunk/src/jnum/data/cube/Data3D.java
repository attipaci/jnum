/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     jnum is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     jnum is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with jnum.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube;


import jnum.data.CubicSpline;
import jnum.data.Data;
import jnum.data.DataCrawler;
import jnum.data.WeightedPoint;
import jnum.math.Vector3D;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.parallel.PointOp;



public abstract class Data3D extends Data<Index3D> implements Value3D {

    private InterpolatorData reuseIpolData;

    

    // TODO 3D methods
    /*
     * - restrictRange(Range);
     * - discardRange(Range);
     * 
     * 
     * - level()
     */
    
    
    @Override
    public Data3D clone() {
        Data3D clone = (Data3D) super.clone();
        clone.reuseIpolData = new InterpolatorData();
        return clone;
    }
        
    
    @Override
    public Index3D copyOfIndex(Index3D index) { return new Index3D(index.i(), index.j(), index.k()); }
    
   
    @Override
    public int capacity() { 
        if(sizeX() <= 0) return 0;
        if(sizeY() <= 0) return 0;
        if(sizeZ() <= 0) return 0;   
        return sizeX() * sizeY() * sizeZ(); 
    }
    
    @Override
    public Index3D size() {
        return new Index3D(sizeX(), sizeY(), sizeZ());
    }
    
    
    @Override
    public String getSizeString() { return sizeX() + "x" + sizeY() + "x" + sizeZ(); }
    
    @Override
    public final boolean conformsTo(Index3D size) { return conformsTo(size.i(), size.j(), size.k()); }
    
    public boolean conformsTo(int sizeX, int sizeY, int sizeZ) {
        if(sizeX() != sizeX) return false;
        if(sizeY() != sizeY) return false;
        if(sizeZ() != sizeZ) return false;
        return true;
    }
  
    
    @Override
    public final Number get(Index3D index) { return get(index.i(), index.j(), index.k()); }
     
    public final Number getValid(final int i, final int j, final int k, final Number defaultValue) {
        if(!isValid(i, j, k)) return defaultValue;
        return get(i, j, k);
    }

    @Override
    public final Number getValid(final Index3D index, final Number defaultValue) { 
        return getValid(index.i(), index.j(), index.k(), defaultValue); 
    }

    
    @Override
    public final void set(Index3D index, Number value) { set(index.i(), index.j(), index.k(), value); }
     
    @Override
    public final boolean isValid(Index3D index) { return isValid(index.i(), index.j(), index.k()); }
      
    @Override
    public final void discard(Index3D index) { discard(index.i(), index.j(), index.k()); }
     
    @Override
    public final void clear(Index3D index) { clear(index.i(), index.j(), index.k()); }
    
    public abstract void clear(int i, int j, int k);
    
    @Override
    public final void add(Index3D index, Number value) { add(index.i(), index.j(), index.k(), value); }
    
    @Override
    public final void scale(Index3D index, double factor) { scale(index.i(), index.j(), index.k(), factor); }
    
       
    public void scale(int i, int j, int k, double factor) {
        set(i, j, k, get(i, j, k).doubleValue() * factor);
    }

    
    
    @Override
    public double valueAtIndex(double ic, double jc, double kc) {
        return valueAtIndex(ic, jc, kc, null);
    }
    
    
    public double valueAtIndex(double ic, double jc, double kc, InterpolatorData ipolData) {
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        if(i < 0) return Double.NaN;
        else if(i >= sizeX()) return Double.NaN;

        final int j = (int) Math.round(jc);
        if(j < 0) return Double.NaN;
        else if(j >= sizeY()) return Double.NaN;

        final int k = (int) Math.round(kc);
        if(k < 0) return Double.NaN;
        else if(k >= sizeZ()) return Double.NaN;
        
        if(!isValid(i, j, k)) return Double.NaN;

        if(i == ic) if(j == jc) if(k == kc) return get(i, j, k).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i, j, k).doubleValue();
        case LINEAR : return trilinearAt(ic, jc, kc);
        case QUADRATIC : return piecewiseQuadraticAt(ic, jc, kc);
        case SPLINE : return ipolData == null ? splineAt(ic, jc, kc) : splineAt(ic, jc, kc, ipolData);
        }

        return Double.NaN;
        
    }
    
    public final double valueAtIndex(Index3D index) {
        return valueAtIndex(index.i(), index.j(), index.k(), null);
    }
    
    public final double valueAtIndex(Index3D index, InterpolatorData ipolData) {
        return valueAtIndex(index.i(), index.j(), index.k(), ipolData);
    }
    
    
    public Number nearestValueAtIndex(Vector3D index) {
        return nearestValueTo(index.x(), index.y(), index.z());
    }
    
    public Number nearestValueTo(double ic, double jc, double kc) {
        return get((int) Math.round(ic), (int) Math.round(jc), (int) Math.round(kc));
    }
   
    public double trilinearAt(double ic, double jc, double kc) {
        int i0 = (int)Math.floor(ic);
        int j0 = (int)Math.floor(jc);
        int k0 = (int)Math.floor(kc);

        int toi = Math.min(sizeX()-1, i0+1);
        int toj = Math.min(sizeY()-1, j0+1);   
        int tok = Math.min(sizeZ()-1, k0+1);
        
        final double di = ic - i0;
        final double dj = jc - j0;
        final double dk = kc - k0;

        double sum = 0.0, sumw = 0.0;

        
        for(int k=tok; --k >= k0; ) for(int j=toj; --j >= j0; ) for(int i=toi; --i >= i0; ) if(isValid(i, j, k)) {
            double w = (i == i0 ? (1.0 - di) : di) * (j == j0 ? (1.0 - dj) : dj) * (k == k0 ? (1.0 - dk) : dk);
            sum += w * get(i, j, k).doubleValue();
            sumw += w;          
        }
        
        return sum / sumw;
        
        // ~90 ops
    }
    
    public final double trilinearAt(Index3D index) {
        return trilinearAt(index.i(), index.j(), index.k());
    }
    
    public double piecewiseQuadraticAt(double ic, double jc, double kc) {
        // TODO
        throw new UnsupportedOperationException("Not implemented.");
    }
    
    public final double piecewiseQuadraticAt(Index3D index) {
        return piecewiseQuadraticAt(index.i(), index.j(), index.k());
    }
    
    
    public double splineAt(double ic, double jc, double kc) {
        synchronized(reuseIpolData) { return splineAt(ic, jc, kc, reuseIpolData); }
    }
    
    public double splineAt(double ic, double jc, double kc, InterpolatorData ipolData) {
        ipolData.centerOn(ic, jc, kc);

        final int fromi = Math.max(0, ipolData.splineX.minIndex());
        final int toi = Math.min(sizeX(), ipolData.splineX.maxIndex());

        final int fromj = Math.max(0, ipolData.splineY.minIndex());
        final int toj = Math.min(sizeY(), ipolData.splineY.maxIndex());
        
        final int fromk = Math.max(0, ipolData.splineZ.minIndex());
        final int tok = Math.min(sizeZ(), ipolData.splineZ.maxIndex());
  
        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) {
            final double wx = ipolData.splineX.coefficientAt(i);
            for(int j=toj; --j >= fromj; ) {
                final double wxy = wx * ipolData.splineY.coefficientAt(j);
                for(int k=tok; --k >= fromk; ) if(isValid(i, j, k)) {
                    final double w = wxy * ipolData.splineZ.coefficientAt(k);         
                    sum += w * get(i, j, k).doubleValue();
                    sumw += w;
                }
            }
        }
        
        return sum / sumw;
        
        // ~800 ops...
    }
    
    public final double splineAt(Index3D index, InterpolatorData ipolData) {
        return splineAt(index.i(), index.j(), index.k(), ipolData);
    }
    
    

    @Override
    protected int getInterpolationOps(int type) {
        switch(type) {
        case NEAREST : return 15;
        case LINEAR : return 90;
        case QUADRATIC : return 100;
        case SPLINE : return 800;
        }
        return 1;
    }

    @Override
    public final boolean containsIndex(Index3D index) {
        return containsIndex(index.i(), index.j(), index.k());        
    }

    public boolean containsIndex(final int i, final int j, final int k) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(k < 0) return false;
        if(i >= sizeX()) return false;
        if(j >= sizeY()) return false;
        if(k >= sizeZ()) return false;
        return true;
    }


    public boolean containsIndex(final double i, final double j, final double k) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(k < 0) return false;
        if(i >= sizeX()-0.5) return false;
        if(j >= sizeY()-0.5) return false;
        if(k >= sizeZ()-0.5) return false;
        return true;
    }
    
    @Override
    public void despike(double level) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getInfo() {
        return "Datacube Size: " + getSizeString() + " voxels.";
    }

    
    @Override
    public Object getFitsData(Class<? extends Number> dataType) {
        // TODO...
        if(!Double.class.equals(getElementType())) throw new UnsupportedOperationException("not implemented.");
        
        final double[][][] transpose = new double[sizeZ()][sizeY()][sizeX()];
        final double scale = 1.0 / getUnit().value();
                      
        new Fork<Void>() {
            @Override
            protected void process(int i, int j, int k) {
                transpose[k][j][i] = isValid(i, j, k) ? scale * get(i, j, k).doubleValue() : getBlankingValue().doubleValue();
            }     
        }.process();
 
        return transpose;
    }
   
    
    
    @Override
    public Object getTableEntry(String name) {
        if(name.equals("sizeX")) return sizeX();
        else if(name.equals("sizeY")) return sizeY();
        else if(name.equals("sizeY")) return sizeZ();
        else return super.getTableEntry(name);
    }

  
    @Override
    public DataCrawler<Number> iterator() {
        return new DataCrawler<Number>() {
            int i = 0, j = 0, k = 0;
            
            @Override
            public final boolean hasNext() {
                if(i < sizeX()) return true;
                return k < (sizeZ()-1);
            }

            @Override
            public final Number next() {
                if(i >= sizeX()) return null;
                
                k++;
                if(k == sizeZ()) {
                    k = 0; j++; 
                    if(j == sizeY()) { j = 0; i++; }
                }
     
                return i < sizeX() ? get(i, j, k) : null;
            }

            @Override
            public final void remove() {
                discard(i, j, k);
            }

            @Override
            public final Object getData() {
                return Data3D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, j, k, value);
            }
            
            @Override
            public final boolean isValid() {
                return Data3D.this.isValid(i, j, k);
            }

            @Override
            public final void reset() {
                i = j = k = 0;
            }
            
        };
        
    }

    

    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op) {
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) for(int k=sizeY(); --k >= 0; )
            if(isValid(i, j, k)) op.process(get(i, j, k));
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op) {
        final Index3D index = new Index3D();
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) for(int k=sizeY(); --k >= 0; ) {
            index.set(i,  j,  k);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>() {
            private ParallelPointOp<Number, ReturnType> localOp;
            
            @Override
            public void init() {
                super.init();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j, int k) {
                if(isValid(i, j, k)) localOp.process(get(i, j, k));
            }
            
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Number, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index3D, ReturnType> op) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>() {
            private ParallelPointOp<Index3D, ReturnType> localOp;
            private Index3D index;
            
            @Override
            public void init() {
                super.init();
                index = new Index3D();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j, int k) {
                index.set(i, j, k);
                localOp.process(index);
            }
            
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Index3D, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static class InterpolatorData {
        
        public CubicSpline splineX, splineY, splineZ;
        
        public InterpolatorData() {
            splineX = new CubicSpline();
            splineY = new CubicSpline();
            splineZ = new CubicSpline();
        }
   
        public void centerOn(double deltax, double deltay, double deltaz) {
            splineX.centerOn(deltaz);
            splineY.centerOn(deltaz);
            splineZ.centerOn(deltaz);
        }

    }
    
    
    
    
    
    
    public abstract class Fork<ReturnType> extends Task<ReturnType> {

        @Override
        protected void processChunk(int index, int threadCount) {
            final int sizeX = sizeX();
            for(int i=index; i<sizeX; i += threadCount) {
                processX(i);
                Thread.yield();
            }
        }

        protected void processX(int i) {
            for(int j=sizeY(); --j >= 0; ) process(i, j);
        }
        
        protected void process(int i, int j) {
            for(int k=sizeY(); --k >= 0; ) process(i, j, k);
        }

        
        protected abstract void process(int i, int j, int k);
        
        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + sizeX() * sizeY() * sizeZ() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

       
    }
    
    

    public abstract class Loop<ReturnType> {
        public ReturnType process() {
            for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) for(int k=sizeZ(); --k >= 0; ) 
                process(i, j, k);
            return getResult();
        }

        protected abstract void process(int i, int j, int k);

        protected ReturnType getResult() { return null; }
    }



    public abstract class AveragingFork extends Fork<WeightedPoint> {
        @Override
        public WeightedPoint getResult() {
            WeightedPoint ave = new WeightedPoint();      
            for(ParallelTask<WeightedPoint> task : getWorkers()) ave.accumulate(task.getLocalResult(), 1.0);
            if(ave.weight() > 0.0) ave.endAccumulation();
            return ave;
        }
    }


    public abstract class InterpolatingFork extends Fork<Void> {
        private InterpolatorData ipolData;

        @Override
        protected void init() { ipolData = new InterpolatorData(); }

        public final InterpolatorData getInterpolatorData() { return ipolData; }
    }   



   
    
}

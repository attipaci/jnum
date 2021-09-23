/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.image;

import java.util.List;

import jnum.Constant;
import jnum.PointOp;
import jnum.data.DataPoint;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.DataCrawler;
import jnum.data.CubicSpline;
import jnum.data.WeightedPoint;
import jnum.data.index.Index2D;
import jnum.math.IntRange;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.util.HashCode;


/**
 * An abstract 2D image class that operates solely via element access, but does not allow changing size
 * or access the backup storage object directly.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class Data2D extends RegularData<Index2D, Vector2D> implements Values2D {


    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ sizeX() ^ sizeY(); 
        hash ^= HashCode.sampleFrom(this);
        return hash;
    }

    
    @Override
    public Index2D getIndexInstance() { return new Index2D(); }
    
    @Override
    public Vector2D getVectorInstance() { return new Vector2D(); }
    

    @Override
    public final int dimension() { return 2; }
    
    @Override
    public Index2D getSize() { return new Index2D(sizeX(), sizeY()); }
    
    @Override
    public int getSize(int i) {
        switch(i) {
        case 0: return sizeX();
        case 1: return sizeY();
        default: throw new IllegalArgumentException("there is no dimension " + i);
        }
    }
    
    @Override
    public final Index2D copyOfIndex(Index2D index) { return new Index2D(index.i(), index.j()); }
    
    @Override
    public Image2D newImage() {
        return Image2D.createType(getElementType(), sizeX(), sizeY());
    }
    
    @Override
    public Image2D newImage(Index2D size, Class<? extends Number> elementType) {
        return Image2D.createType(getElementType(), size.i(), size.j());
    }
   

    public Image2D getImage() {
        return getImage(getElementType(), getInvalidValue());
    }

    public final Image2D getImage(Number blankingValue) {
        return getImage(getElementType(), blankingValue);
    }

    public final Image2D getImage(Class<? extends Number> elementType) {
        return getImage(elementType, getInvalidValue());
    }

    public Image2D getImage(Class<? extends Number> elementType, Number blankingValue) {
        Image2D image = Image2D.createFrom(this, blankingValue, elementType);

        image.copyParallel(this);
        image.setInterpolationType(getInterpolationType());
        image.setVerbose(isVerbose());
        image.setUnit(getUnit());

        List<String> imageHistory = image.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return image;
    }
   
 
    @Override
    public final boolean isValid(Index2D index) {
        return isValid(index.i(), index.j());
    }


    @Override
    public boolean isValid(int i, int j) {
        return isValid(get(i,j));
    }

   
    @Override
    public void discard(int i, int j) {
        set(i, j, getInvalidValue());
    }

    @Override
    public final Number get(Index2D index) { return get(index.i(), index.j()); }

    public final Number getValid(final int i, final int j, final Number defaultValue) {
        if(!isValid(i, j)) return defaultValue;
        return get(i, j);
    }

    @Override
    public final Number getValid(final Index2D index, final Number defaultValue) { return getValid(index.i(), index.j(), defaultValue); }


    @Override
    public final void set(Index2D index, Number value) { set(index.i(), index.j(), value); }


    @Override
    public final void add(Index2D index, Number value) { add(index.i(), index.j(), value); }


    public void scale(int i, int j, double factor) {
        set(i, j, get(i, j).doubleValue() * factor);
    }

    @Override
    public final void scale(Index2D index, double factor) { scale(index.i(), index.j(), factor); }

    @Override
    public final void discard(Index2D index) { discard(index.i(), index.j()); }

    @Override
    public int capacity() {
        return sizeX() * sizeY();
    }

  
    public boolean conformsTo(int sizeX, int sizeY) {
        if(sizeX() != sizeX) return false;
        if(sizeY() != sizeY) return false;
        return true;
    }


    public IntRange getXIndexRange() {
        int min = sizeX(), max = -1;
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isValid(i, j)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public IntRange getYIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) if(isValid(i, j)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    
    @Override
    public final boolean containsIndex(Index2D index) {
        return containsIndex(index.i(), index.j());        
    }

    public boolean containsIndex(final int i, final int j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()) return false;
        if(j >= sizeY()) return false;
        return true;
    }

    @Override
    public final boolean containsIndex(Vector2D index) {
        return containsIndex(index.x(), index.y());
    }
    
    public boolean containsIndex(final double i, final double j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()-0.5) return false;
        if(j >= sizeY()-0.5) return false;
        return true;
    }

   
    @Override
    public final void clear(Index2D index) { clear(index.i(), index.j()); }
    
    public void clear(int i, int j) { set(i, j, 0); }


    public DataPoint getAsymmetry(final Grid2D<?> grid, final Vector2D centerIndex, final double angle, final Range radialRange) {    

        final Vector2D centerOffset = centerIndex.copy();
        grid.toOffset(centerOffset);

        class Moments {
            double m0 = 0.0, mc = 0.0, c2 = 0.0;
        }

        Fork<Moments> moments = new Fork<Moments>() {
            private Moments values;
            private Vector2D v;

            @Override
            public void init() {
                super.init();
                values = new Moments();
                v = new Vector2D();
            }

            @Override
            protected void process(int i, int j) {
                if(!isValid(i, j)) return;

                v.set(i,  j);
                grid.toOffset(v);
                v.subtract(centerOffset);

                double r = v.length();

                if(r > radialRange.max()) return;

                double p = get(i,j).doubleValue();

                values.m0 += Math.abs(p);

                if(r < radialRange.min()) return;

                // cos term is gain-like
                double c = Math.cos(v.angle() - angle);
                
                values.mc += p * c;
                values.c2 += c * c;
            }

            @Override
            public Moments getLocalResult() { return values; }

            @Override
            public Moments getResult() {
                Moments global = new Moments();
                for(ParallelTask<Moments> worker : getWorkers()) {
                    Moments local = worker.getLocalResult();
                    global.m0 += local.m0;
                    global.mc += local.mc;
                    global.c2 += local.c2;
                }
                return global;
            }  
        };

        moments.process();
        Moments values = moments.getResult();

        if(values.m0 > 0.0) return new DataPoint(values.mc / values.m0, Math.sqrt(values.c2) / values.m0);
        return new DataPoint(); 
    }


    public Asymmetry2D getAsymmetry2D(Grid2D<?> grid, Vector2D centerIndex, double angle, Range radialRange) {
        Asymmetry2D asym = new Asymmetry2D();
        asym.setX(getAsymmetry(grid, centerIndex, angle, radialRange));
        asym.setY(getAsymmetry(grid, centerIndex, angle + Constant.rightAngle, radialRange));
        return asym;
    }

 
  
    public Vector2D getRefinedPeakIndex(Index2D peakIndex) {
        final int i = peakIndex.i();
        final int j = peakIndex.j();

        double a=0.0,b=0.0,c=0.0,d=0.0;

        double y0 = get(i,j).doubleValue();

        if(i>0) if(i<sizeX()-1) if(isValid(i+1, j)) if(isValid(i-1, j)) {
            a = 0.5 * (get(i+1,j).doubleValue() + get(i-1,j).doubleValue()) - y0;
            c = 0.5 * (get(i+1,j).doubleValue() - get(i-1,j).doubleValue());
        }

        if(j>0) if(j<sizeY()-1) if(isValid(i, j+1)) if(isValid(i, j-1)) {
            b = 0.5 * (get(i,j+1).doubleValue() + get(i,j-1).doubleValue()) - y0;
            d = 0.5 * (get(i,j+1).doubleValue() - get(i,j-1).doubleValue());
        }

        double di = (a == 0.0) ? 0.0 : -0.5*c/a;
        double dj = (b == 0.0) ? 0.0 : -0.5*d/b;    

        if(Math.abs(di) > 0.5 || Math.abs(dj) > 0.5) 
            throw new IllegalStateException("Index argument does not mark a peak.");

        return new Vector2D(i + di, j + dj);
    }


    @Override
    public double valueAtIndex(Vector2D index, SplineSet<Vector2D> splines) {
        return valueAtIndex(index.x(), index.y(), splines);
    }

    @Override
    protected double valueAtIndex(Index2D numerator, Index2D denominator, SplineSet<Vector2D> splines) {
        return valueAtIndex((double) numerator.i() / denominator.i(), (double) numerator.j() / denominator.j(), splines);
    }

    

    @Override
    public double valueAtIndex(double ic, double jc) {
        return valueAtIndex(ic, jc, null);
    }


    public double valueAtIndex(double ic, double jc, SplineSet<Vector2D> splines) {  
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        final int j = (int) Math.round(jc);
        
        if(!containsIndex(i, j)) return Double.NaN;
        if(!isValid(i, j)) return Double.NaN;

        if(i == ic) if(j == jc) return get(i, j).doubleValue();

        switch(getInterpolationType()) {
        case INTERPOLATE_NEAREST : return get(i, j).doubleValue();
        case INTERPOLATE_LINEAR : return linearAtIndex(ic, jc);
        case INTERPOLATE_PIECEWISE_QUADRATIC : return quadraticAtIndex(ic, jc);
        case INTERPOLATE_SPLINE : return splines == null ? splineAtIndex(ic, jc) : splineAtIndex(ic, jc, splines);
        }

        return Double.NaN;
    }

    @Override
    public Number nearestValueAtIndex(Vector2D index) {
        return nearestValueAtIndex(index.x(), index.y());
    }
    
    public Number nearestValueAtIndex(double ic, double jc) {
        return get((int) Math.round(ic), (int) Math.round(jc));
    }
    
    @Override
    public final double linearAtIndex(Vector2D index) { return linearAtIndex(index.x(), index.y()); }

    // Bilinear interpolation
    public double linearAtIndex(double ic, double jc) {        
        final int i = (int)Math.floor(ic);
        final int j = (int)Math.floor(jc);

        final double di = ic - i;
        final double dj = jc - j;

        double sum = 0.0, sumw = 0.0;

        if(isValid(i, j)) {
            double w = (1.0 - di) * (1.0 - dj);
            sum += w * get(i, j).doubleValue();
            sumw += w;          
        }
        if(isValid(i+1, j)) {
            double w = di * (1.0 - dj);
            sum += w * get(i+1, j).doubleValue();
            sumw += w;  
        }
        if(isValid(i, j+1)) {
            double w = (1.0 - di) * dj;
            sum += w * get(i, j+1).doubleValue();
            sumw += w;  
        }
        if(isValid(i+1, j+1)) {
            double w = di * dj;
            sum += w * get(i+1, j+1).doubleValue();
            sumw += w;  
        }

        return sum / sumw;

        // ~ 45 ops...
    }

    
    
    @Override
    public final double quadraticAtIndex(Vector2D index) { return quadraticAtIndex(index.x(), index.y()); }


    // Interpolate (linear at edges, quadratic otherwise)   
    // Piecewise quadratic...
    /**
     * Piecewise quadratic at.
     *
     * @param ic the ic
     * @param jc the jc
     * @return the double
     */
    public double quadraticAtIndex(double ic, double jc) {
        // Find the nearest data point (i,j)
        final int i = (int)Math.round(ic);
        final int j = (int)Math.round(jc);

        final double y0 = get(i, j).doubleValue();
        double ax=0.0, ay=0.0, bx=0.0, by=0.0;

        if(isValid(i+1,j)) {
            if(isValid(i-1, j)) {
                ax = 0.5 * (get(i+1, j).doubleValue() + get(i-1, j).doubleValue()) - y0;
                bx = 0.5 * (get(i+1, j).doubleValue() - get(i-1, j).doubleValue());
            }
            else bx = get(i+1, j).doubleValue() - y0; // Fall back to linear...
        }
        else if(isValid(i-1, j)) bx = y0 - get(i-1, j).doubleValue();

        if(isValid(i,j+1)) {
            if(isValid(i,j-1)) {
                ay = 0.5 * (get(i, j+1).doubleValue() + get(i, j-1).doubleValue()) - y0;
                by = 0.5 * (get(i, j+1).doubleValue() - get(i, j-1).doubleValue());
            }
            else by = get(i, j+1).doubleValue() - y0; // Fall back to linear...
        }
        else if(isValid(i,j-1)) by = y0 - get(i, j-1).doubleValue();

        ic -= i;
        jc -= j;

        return (ax*ic+bx)*ic + (ay*jc+by)*jc + y0;

        // ~60 ops...
    }
    
    
    public double splineAtIndex(final double ic, final double jc) {
        synchronized(reuseIpolData) { return splineAtIndex(ic, jc, reuseIpolData); }
    }

    @Override
    public final double splineAtIndex(Vector2D index, SplineSet<Vector2D> splines) { 
        return splineAtIndex(index.x(), index.y(), splines); 
    }
 
    // Performs a bicubic spline interpolation...
    public double splineAtIndex(final double ic, final double jc, SplineSet<Vector2D> splines) {   
        splines.centerOn(ic, jc);

        final CubicSpline splineX = splines.getSpline(0);
        final CubicSpline splineY = splines.getSpline(1);

        final int fromi = Math.max(0, splineX.minIndex());
        final int toi = Math.min(sizeX(), splineX.maxIndex());

        final int fromj = Math.max(0, splineY.minIndex());
        final int toj = Math.min(sizeY(), splineY.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) {
            final double wx = splineX.coefficientAt(i);
            for(int j=toj; --j >= fromj; ) if(isValid(i, j)) {
                final double w = wx * splineY.coefficientAt(j);
                sum += w * get(i, j).doubleValue();
                sumw += w;
            }
        }

        return sum / sumw;

        // ~200 ops...
    }

 
    @Override
    protected int getInterpolationOps(int type) {
        switch(type) {
        case INTERPOLATE_NEAREST : return 10;
        case INTERPOLATE_LINEAR : return 45;
        case INTERPOLATE_PIECEWISE_QUADRATIC : return 60;
        case INTERPOLATE_SPLINE : return 200;
        }
        return 1;
    }


    
    
    @Override
    public int getPointSmoothOps(int beamPoints, int interpolationType) {
        return 25 + beamPoints * (16 + getInterpolationOps(interpolationType));
    }




    @Override
    public Object getTableEntry(String name) {
        if(name.equals("sizeX")) return sizeX();
        else if(name.equals("sizeY")) return sizeY();  
        else return super.getTableEntry(name);
    }

    @Override
    public String getInfo() {
        return "Image Size: " + getSizeString() + " pixels.";
    }
    
    
    // TODO generalize....
    @Override
    public DataCrawler<Number> iterator() {
        return new DataCrawler<Number>() {
            int i = 0, j = 0;
            
            @Override
            public final boolean hasNext() {
                if(i < sizeX()) return true;
                return j < (sizeY()-1);
            }

            @Override
            public final Number next() {
                if(i >= sizeX()) return null;
                j++;
                if(j == sizeY()) { j = 0; i++; }
                return i < sizeX() ? get(i, j) : null;
            }

            @Override
            public final void remove() {
                discard(i, j);
            }

            @Override
            public final Object getData() {
                return Data2D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, j, value);
            }

            @Override
            public final boolean isValid() {
                return Data2D.this.isValid(i, j);
            }
            
            @Override
            public final void reset() {
                i = j = 0;
            }
            
        };
        
    }
    

    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op, Index2D from, Index2D to) {
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) if(isValid(i, j)) {
                op.process(get(i, j));
                if(op.exception != null) return null;
            }
        }
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index2D, ReturnType> op, Index2D from, Index2D to) {
        final Index2D index = new Index2D();
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) {
                index.set(i, j);
                op.process(index);
                if(op.exception != null) return null;
            }
        }
        return op.getResult();
    }
    
    
    @Override
    public <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op, Index2D from, Index2D to) {
        
        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
            private ParallelPointOp<Number, ReturnType> localOp;
            
            @Override
            public void init() {
                super.init();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j) {
                if(isValid(i, j)) localOp.process(get(i, j));
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
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index2D, ReturnType> op, Index2D from, Index2D to) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
            private ParallelPointOp<Index2D, ReturnType> localOp;
            private Index2D index;
            
            @Override
            public void init() {
                super.init();
                index = new Index2D();
                localOp = op.newInstance();
            }
            
            @Override
            protected void process(int i, int j) {
                index.set(i, j); 
                localOp.process(index);
            }
          
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Index2D, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }

    



    public abstract class Fork<ReturnType> extends AbstractFork<ReturnType> {                   
        public Fork() {}
        
        public Fork(Index2D from, Index2D to) { super(from, to); }
        
        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=from.i() + index; i < to.i(); i += threadCount) {
                processX(i);
            }
        }

        protected void processX(int i) {
            for(int j=to.j(); --j >= from.j(); ) process(i, j);
        }

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




    public abstract class Loop<ReturnType> extends AbstractLoop<ReturnType> {     
        public Loop() {}
        
        public Loop(Index2D from, Index2D to) { super(from, to); }

        @Override
        public ReturnType process() {
            for(int i=to.i(); --i >= from.i(); ) for(int j=to.j(); --j >= from.j(); ) process(i, j);
            return getResult();
        }

        protected abstract void process(int i, int j);

        @Override
        protected ReturnType getResult() { return null; }
    }


    public abstract class AveragingFork extends Fork<WeightedPoint> {
        public AveragingFork() {}
        
        public AveragingFork(Index2D from, Index2D to) { super(from, to); }
        
        @Override
        public WeightedPoint getResult() {
            WeightedPoint ave = new WeightedPoint();      
            for(ParallelTask<WeightedPoint> task : getWorkers()) ave.accumulate(task.getLocalResult(), 1.0);
            if(ave.weight() > 0.0) ave.endAccumulation();
            return ave;
        }
    }




}

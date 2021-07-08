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

package jnum.data.cube;


import java.util.List;


import jnum.PointOp;
import jnum.Util;
import jnum.data.CubicSpline;
import jnum.data.DataCrawler;
import jnum.data.RegularData;
import jnum.data.SplineSet;
import jnum.data.WeightedPoint;
import jnum.data.index.Index3D;
import jnum.math.IntRange;
import jnum.math.Vector3D;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;



public abstract class Data3D extends RegularData<Index3D, Vector3D> implements Values3D {



    // TODO 3D methods
    /*
     * - restrictRange(Range);
     * - discardRange(Range);
     * 
     * 
     * - level()
     */

    @Override
    public Cube3D newImage() {
        return Cube3D.createType(getElementType(), sizeX(), sizeY(), sizeZ());
    }

    @Override
    public Cube3D newImage(Index3D size, Class<? extends Number> elementType) {
        return Cube3D.createType(getElementType(), size.i(), size.j(), size.k());
    }

    public Cube3D getCube() {
        return getCube(getElementType(), getBlankingValue());
    }

    public final Cube3D getCube(Number blankingValue) {
        return getCube(getElementType(), blankingValue);
    }

    public final Cube3D getCube(Class<? extends Number> elementType) {
        return getCube(elementType, getBlankingValue());
    }

    public Cube3D getCube(Class<? extends Number> elementType, Number blankingValue) {
        Cube3D image = Cube3D.createFrom(this, blankingValue, elementType);

        image.copyParallel(this);
        image.setInterpolationType(getInterpolationType());
        image.setVerbose(isVerbose());
        image.setUnit(getUnit());

        List<String> imageHistory = image.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return image;
    }

    @Override
    public final Index3D getIndexInstance() { return new Index3D(); }

    @Override
    public final Vector3D getVectorInstance() { return new Vector3D(); }

    @Override
    public final Index3D getSize() { return new Index3D(sizeX(), sizeY(), sizeZ()); }

    @Override
    public final Index3D copyOfIndex(Index3D index) { return new Index3D(index.i(), index.j(), index.k()); }

    @Override
    public final int dimension() { return 3; }

    @Override
    public final int capacity() {  
        return sizeX() * sizeY() * sizeZ(); 
    }

    @Override
    public final String toString(Index3D index) {
        return toString(index.i(), index.j(), index.k());
    }

    public String toString(int i, int j, int k) {
        return "[" + i + ", " + j + "," + k + "]=" + Util.S3.format(get(i,j, k));
    }



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

    @Override
    public final void add(Index3D index, Number value) { add(index.i(), index.j(), index.k(), value); }

    @Override
    public final void scale(Index3D index, double factor) { scale(index.i(), index.j(), index.k(), factor); }


    @Override
    public boolean isValid(int i, int j, int k) {
        return isValid(get(i, j, k));
    }


    @Override
    public void discard(int i, int j, int k) {
        set(i, j, k, getBlankingValue());
    }

    public void clear(int i, int j, int k) { set(i, j, k, 0); }

    public void scale(int i, int j, int k, double factor) {
        set(i, j, k, get(i, j, k).doubleValue() * factor);
    }

    @Override
    public double valueAtIndex(double ic, double jc, double kc) {
        return valueAtIndex(ic, jc, kc, null);
    }


    @Override
    public final double valueAtIndex(Vector3D index, SplineSet<Vector3D> splines) {
        return valueAtIndex(index.x(), index.y(), index.z(), splines);
    }

    @Override
    public final double valueAtIndex(Index3D numerator, Index3D denominator, SplineSet<Vector3D> splines) {
        return valueAtIndex((double) numerator.i() / denominator.i(), (double) numerator.j() / denominator.j(), (double) numerator.k() / denominator.k(), splines);
    }

    public double valueAtIndex(double ic, double jc, double kc, SplineSet<Vector3D> splines) {
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
        case LINEAR : return linearAtIndex(ic, jc, kc);
        case QUADRATIC : return quadraticAtIndex(ic, jc, kc);
        case SPLINE : return splines == null ? splineAtIndex(ic, jc, kc) : splineAtIndex(ic, jc, kc, splines);
        }

        return Double.NaN;

    }



    @Override
    public final Number nearestValueAtIndex(Vector3D index) {
        return nearestValueAtIndex(index.x(), index.y(), index.z());
    }

    public Number nearestValueAtIndex(double ic, double jc, double kc) {
        return get((int) Math.round(ic), (int) Math.round(jc), (int) Math.round(kc));
    }


    @Override
    public final double linearAtIndex(Vector3D index) {
        return linearAtIndex(index.x(), index.y(), index.z());
    }

    public double linearAtIndex(double ic, double jc, double kc) {
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

    @Override
    public final double quadraticAtIndex(Vector3D index) {
        return quadraticAtIndex(index.x(), index.y(), index.z());
    }


    public double quadraticAtIndex(double ic, double jc, double kc) {
        // TODO
        throw new UnsupportedOperationException("Not implemented.");
    }


    public double splineAtIndex(double ic, double jc, double kc) {
        synchronized(reuseIpolData) { return splineAtIndex(ic, jc, kc, reuseIpolData); }
    }

    @Override
    public final double splineAtIndex(Vector3D index, SplineSet<Vector3D> splines) {
        return splineAtIndex(index.x(), index.y(), index.z(), splines);
    }

    public double splineAtIndex(double ic, double jc, double kc, SplineSet<Vector3D> splines) {
        splines.centerOn(ic, jc, kc);

        final CubicSpline splineX = splines.getSpline(0);
        final CubicSpline splineY = splines.getSpline(1);
        final CubicSpline splineZ = splines.getSpline(2);

        final int fromi = Math.max(0, splineX.minIndex());
        final int toi = Math.min(sizeX(), splineX.maxIndex());

        final int fromj = Math.max(0, splineY.minIndex());
        final int toj = Math.min(sizeY(), splineY.maxIndex());

        final int fromk = Math.max(0, splineZ.minIndex());
        final int tok = Math.min(sizeZ(), splineZ.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) {
            final double wx = splineX.coefficientAt(i);
            for(int j=toj; --j >= fromj; ) {
                final double wxy = wx * splineY.coefficientAt(j);
                for(int k=tok; --k >= fromk; ) if(isValid(i, j, k)) {
                    final double w = wxy * splineZ.coefficientAt(k);         
                    sum += w * get(i, j, k).doubleValue();
                    sumw += w;
                }
            }
        }

        return sum / sumw;

        // ~800 ops...
    }

    public final double splineAt(Index3D index, SplineSet<Vector3D> splines) {
        return splineAtIndex(index.i(), index.j(), index.k(), splines);
    }

    @Override
    public int getPointSmoothOps(int beamPoints, int interpolationType) {
        return 36 + beamPoints * (16 + getInterpolationOps(interpolationType));
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
    public final boolean containsIndex(Vector3D index) {
        return containsIndex(index.x(), index.y(), index.z());        
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

    public IntRange getXIndexRange() {
        int min = sizeX(), max = -1;
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public IntRange getYIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public IntRange getZIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    @SuppressWarnings("cast")
    @Override
    public final Cube3D getCropped(Index3D from, Index3D to) {
        return (Cube3D) getCropped(from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }

    public Cube3D getCropped(int imin, int jmin, int kmin, int imax, int jmax, int kmax) {
        return getCropped(new Index3D(imin, jmin, kmin), new Index3D(imax, jmax, kmax));
    }   



    @Override
    public String getInfo() {
        return "Datacube Size: " + getSizeString() + " voxels.";
    }


    @Override
    public Object getTableEntry(String name) {
        if(name.equals("sizeX")) return sizeX();
        else if(name.equals("sizeY")) return sizeY();
        else if(name.equals("sizeZ")) return sizeZ();
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
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op, Index3D from, Index3D to) {
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) for(int k=to.k(); --k >= from.k(); )
                if(isValid(i, j, k)) op.process(get(i, j, k));
        }

        return op.getResult();
    }

    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op, Index3D from, Index3D to) {
        final Index3D index = new Index3D();
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) for(int k=to.k(); --k >= from.k(); ) {
                index.set(i,  j,  k);
                op.process(index);
                if(op.exception != null) return null;
            }
        }
        return op.getResult();
    }

    @Override
    public <ReturnType> ReturnType forkValid(final ParallelPointOp<Number, ReturnType> op, Index3D from, Index3D to) {

        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
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
    public <ReturnType> ReturnType fork(final ParallelPointOp<Index3D, ReturnType> op, Index3D from, Index3D to) {

        Fork<ReturnType> fork = new Fork<ReturnType>(from, to) {
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





    public abstract class Loop<ReturnType> extends AbstractLoop<ReturnType> {

        public Loop() {}

        public Loop(Index3D from, Index3D to) { super(from, to); }


        @Override
        public ReturnType process() {
            for(int i=to.i(); --i >= from.i(); ) for(int j=to.j(); --j >= from.j(); ) for(int k=to.k(); --k >= from.k(); ) 
                process(i, j, k);
            return getResult();
        }

        protected abstract void process(int i, int j, int k);

        @Override
        protected ReturnType getResult() { return null; }
    }



    public abstract class Fork<ReturnType> extends AbstractFork<ReturnType> {

        public Fork() {}

        public Fork(Index3D from, Index3D to) { super(from, to); }

        @Override
        protected void processChunk(int index, int threadCount) {
            for(int i=from.i() + index; i<to.i(); i += threadCount) {
                processX(i);
            }
        }

        protected void processX(int i) {
            for(int j=to.j(); --j >= from.j(); ) process(i, j);
        }

        protected void process(int i, int j) {
            for(int k=to.k(); --k >= from.k(); ) process(i, j, k);
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


    public abstract class AveragingFork extends Fork<WeightedPoint> {
        public AveragingFork() {}

        public AveragingFork(Index3D from, Index3D to) { super(from, to); }

        @Override
        public WeightedPoint getResult() {
            WeightedPoint ave = new WeightedPoint();      
            for(ParallelTask<WeightedPoint> task : getWorkers()) ave.accumulate(task.getLocalResult(), 1.0);
            if(ave.weight() > 0.0) ave.endAccumulation();
            return ave;
        }
    }



}

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

package jnum.data.samples;

import java.util.List;

import jnum.PointOp;
import jnum.data.CubicSpline;
import jnum.data.Data;
import jnum.data.DataCrawler;
import jnum.data.WeightedPoint;
import jnum.data.samples.overlay.Overlay1D;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.text.TableFormatter;
import jnum.util.HashCode;

public abstract class Data1D extends Data<Integer, Double, Double> implements Values1D, TableFormatter.Entries {

    private CubicSpline reuseSpline;
    
    @Override
    public Data1D clone() {
        Data1D clone = (Data1D) super.clone();
        clone.reuseSpline = new CubicSpline();
        return clone;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ size(); 
        hash ^= HashCode.sampleFrom(this);
        return hash;
    }

     
    @Override
    public final Integer copyOfIndex(Integer index) { return index; }

    
    public Samples1D getEmptySamples() {
        return Samples1D.createType(getElementType(), size());
    }

    public Samples1D getSamples() {
        return getSamples(getElementType(), getBlankingValue());
    }

    public final Samples1D getSamples(Number blankingValue) {
        return getSamples(getElementType(), blankingValue);
    }

    public final Samples1D getImage(Class<? extends Number> elementType) {
        return getSamples(elementType, getBlankingValue());
    }

    public Samples1D getSamples(Class<? extends Number> elementType, Number blankingValue) {
        Samples1D samples = Samples1D.createFrom(this, blankingValue, elementType);

        samples.copyParallel(this);
        samples.setInterpolationType(getInterpolationType());
        samples.setVerbose(isVerbose());
        samples.setUnit(getUnit());

        List<String> imageHistory = samples.getHistory();
        if(getHistory() != null) imageHistory.addAll(getHistory());

        return samples;
    }

    
    
    @Override
    public final int capacity() { return size(); }
    
    @Override
    public String getSizeString() { return size() + ""; }
    
    @Override
    public final boolean conformsTo(Integer size) {
        return size().intValue() == size.intValue();
    }
    
    @Override
    public boolean containsIndex(Integer i) {
        if(i < 0) return false;
        if(i >= size()) return false;
        return true;
    }
    
    @Override
    public final Number getValid(final Integer i, final Number defaultValue) {
        if(!isValid(i)) return defaultValue;
        return get(i);
    }

    
    @Override
    public void clear(Integer i) { set(i, 0); }

    @Override
    public void discard(Integer i) {
        set(i, getBlankingValue());
    }
    
    @Override
    public boolean isValid(Integer i) {
        return isValid(get(i));
    }
    
    @Override
    public void scale(Integer i, double factor) {
        set(i, get(i).doubleValue() * factor);
    }
    
    public final void paste(final Values1D source, boolean report) {
        paste(new Overlay1D(source), report);
    }

    public void paste(final Data1D source, boolean report) {
        if(source == this) return;

        source.new Fork<Void>() {
            @Override
            protected void processElementAt(int i) {
                if(source.isValid(i)) set(i, source.get(i));
                else discard(i);
            }
        }.process();

        if(report) addHistory("pasted new content: " + source.getSizeString());
    }
    
    @Override
    public double valueAtIndex(Double ic) { return valueAtIndex(ic, null); }
    
    public double valueAtIndex(Double ic, CubicSpline spline) {
        // The nearest data point (i,j)
        final int i = (int) Math.round(ic);
        if(i < 0) return Double.NaN;
        else if(i >= size()) return Double.NaN;

        
        if(!isValid(i)) return Double.NaN;

        if(i == ic) return get(i).doubleValue();

        switch(getInterpolationType()) {
        case NEAREST : return get(i).doubleValue();
        case LINEAR : return linearAtIndex(ic);
        case QUADRATIC : return quadraticAtIndex(ic);
        case SPLINE : return spline == null ? splineAtIndex(ic) : splineAtIndex(ic, spline);
        }

        return Double.NaN;
        
    }
    
    @Override
    public Number nearestValueAtIndex(Double ic) {
        int i = (int) Math.round(ic);
        if(!containsIndex(i)) return Double.NaN;
        return get(i);
    }
    
    // Bilinear interpolation
    @Override
    public double linearAtIndex(Double ic) {        
        final int i = (int)Math.floor(ic);
        final double di = ic - i;

        double sum = 0.0, sumw = 0.0;

        if(isValid(i)) {
            double w = (1.0 - di);
            sum += w * get(i).doubleValue();
            sumw += w;          
        }
        if(isValid(i+1)) {
            double w = di;
            sum += w * get(i+1).doubleValue();
            sumw += w;  
        }
        
        return sum / sumw;

        // ~ 25 ops...
    }
    

    @Override
    public double quadraticAtIndex(Double ic) {
        // Find the nearest data point (i)
        final int i = (int)Math.round(ic);
     
        final double y0 = get(i).doubleValue();
        double a=0.0, b=0.0;

        if(isValid(i+1)) {
                a = 0.5 * (get(i+1).doubleValue() + get(i-1).doubleValue()) - y0;
                b = 0.5 * (get(i+1).doubleValue() - get(i-1).doubleValue());
        }
        else if(isValid(i-1)) b = y0 - get(i-1).doubleValue();

       
        ic -= i;
       
        return (a * ic + b) * ic + y0;

        // ~30 ops...
    }

    
    @Override
    public final double splineAtIndex(final Double ic) {
        synchronized(reuseSpline) { return splineAtIndex(ic, reuseSpline); }
    }


    // Performs a bicubic spline interpolation...
    public double splineAtIndex(final Double ic, CubicSpline spline) {   
        spline.centerOn(ic);

     
        final int fromi = Math.max(0, spline.minIndex());
        final int toi = Math.min(size(), spline.maxIndex());

        // Do the spline convolution...
        double sum = 0.0, sumw = 0.0;
        for(int i=toi; --i >= fromi; ) if(isValid(i)) {
            final double w = spline.coefficientAt(i);
            sum += w * get(i).doubleValue();
            sumw += w;
        }

        return sum / sumw;

        // ~50 ops...
    }
    
    

    @Override
    protected int getInterpolationOps(int type) {
        switch(type) {
        case NEAREST : return 5;
        case LINEAR : return 25;
        case QUADRATIC : return 30;
        case SPLINE : return 50;
        }
        return 1;
    }

    
    public int[] getIndexRange() {
        int min = size(), max = -1;
        for(int i=size(); --i >= 0; ) if(isValid(i)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new int[] { min, max } : null;
    }

    
    @Override
    public Samples1D getCropped(Integer imin, Integer imax) {
        Samples1D cropped = getEmptySamples();

        final int fromi = Math.max(0, imin);
        final int toi = Math.min(imax, size()-1);     

        cropped.setSize(imax-imin+1);

        for(int i=fromi, i1=fromi-imin; i<=toi; i++, i1++)
            cropped.set(i1, get(i));

        return cropped;
    }   
    

    @Override
    public void despike(double level) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getInfo() {
        return "Sample size: " + size() + " bins.";
    }

    
    
    @Override
    public Object getFitsData(Class<? extends Number> dataType) {  
        final Samples1D copy = Samples1D.createType(dataType, size());
        
        smartFork(new ParallelPointOp.Simple<Integer>() {
            @Override
            public void process(Integer index) {
               copy.set(index, isValid(index) ? get(index) : getBlankingValue());
            }
            
        });
       
        if(getUnit().value() != 1.0) copy.scale(1.0 / getUnit().value());

        return copy.getData();
    }

    
    
    
    @Override
    public Object getTableEntry(String name) {
        if(name.equals("size")) return size();
        return super.getTableEntry(name);
    }
    
    
    
    @Override
    public DataCrawler<Number> iterator() {
        return new DataCrawler<Number>() {
            int i = 0;
            
            @Override
            public final boolean hasNext() {
                return i < (size() - 1);
            }

            @Override
            public final Number next() {
                if(i >= size()) return null;
                i++;
                return i < size() ? get(i) : null;
            }

            @Override
            public final void remove() {
                discard(i);
            }

            @Override
            public final Object getData() {
                return Data1D.this;
            }

            @Override
            public final void setCurrent(Number value) {
                set(i, value);
            }
            
            @Override
            public final boolean isValid() {
                return Data1D.this.isValid(i);
            }

            @Override
            public final void reset() {
                i = 0;
            }
            
        };
        
    }
    
    
    
    @Override
    public <ReturnType> ReturnType loopValid(final PointOp<Number, ReturnType> op) {
        for(int i=size(); --i >= 0; ) if(isValid(i)) op.process(get(i));
        return op.getResult();
    }
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Integer, ReturnType> op) {
        for(int i=size(); --i >= 0; ) {
            op.process(i);
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
            protected void processElementAt(int i) {
                if(isValid(i)) localOp.process(get(i));
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
    public <ReturnType> ReturnType fork(final ParallelPointOp<Integer, ReturnType> op) {
      
        Fork<ReturnType> fork = new Fork<ReturnType>() {
            private ParallelPointOp<Integer, ReturnType> localOp;
            
            @Override
            public void init() {
                super.init();
                localOp = op.newInstance();
            }
            
            @Override
            protected void processElementAt(int i) {
                localOp.process(i);
            }
                 
            @Override
            public ReturnType getLocalResult() { return localOp.getResult(); }
            

            @Override
            public ReturnType getResult() { 
                ParallelPointOp<Integer, ReturnType> globalOp = op.newInstance();
                
                for(ParallelTask<ReturnType> worker : getWorkers()) {
                    globalOp.mergeResult(worker.getLocalResult());
                }
                return globalOp.getResult();
            }
            
        };
        
        fork.process();
        return fork.getResult();
    }
    


    
    
    
    

    public abstract class Loop<ReturnType> {
        public ReturnType process() {
            for(int i=size(); --i >= 0; ) process(i);
            return getResult();
        }

        protected abstract void process(int i);

        protected ReturnType getResult() { return null; }
    }

    

    public abstract class Fork<ReturnType> extends Task<ReturnType> {           

        @Override
        protected void processChunk(int index, int threadCount) {
            final int sizeX = size();
            for(int i=index; i<sizeX; i += threadCount) {
                processElementAt(i);
            }
        }


        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @Override
        protected int getTotalOps() {
            return 3 + size() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  

        protected abstract void processElementAt(int i);
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
        private CubicSpline spline;

        @Override
        protected void init() { spline = new CubicSpline(); }


        public final CubicSpline getSpline() { return spline; }
    }   



   

    
}

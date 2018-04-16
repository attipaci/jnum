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

package jnum.data;


import jnum.PointOp;
import jnum.Util;
import jnum.math.TrueVector;
import jnum.parallel.ParallelPointOp;

public abstract class RegularData<IndexType extends Index<IndexType>, VectorType extends TrueVector<Double>> extends Data<IndexType> {
    protected SplineSet<VectorType> reuseIpolData;
    
    public RegularData() {
        reuseIpolData = new SplineSet<VectorType>(dimension());
        setInterpolationType(SPLINE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RegularData<IndexType, VectorType> clone() {
        RegularData<IndexType, VectorType> clone = (RegularData<IndexType, VectorType>) super.clone();
        clone.reuseIpolData = new SplineSet<VectorType>(dimension());  
        return clone;
    }

    public abstract VectorType getVectorInstance();
    
    public abstract boolean containsIndex(VectorType index);
    
    public abstract Number nearestValueAtIndex(VectorType index);
    
    public abstract double linearAtIndex(VectorType index);
    
    public abstract double quadraticAtIndex(VectorType index);
    
    public final double splineAtIndex(VectorType index) {
        synchronized(reuseIpolData) { return valueAtIndex(index, reuseIpolData); }
    }
    
    public abstract double splineAtIndex(VectorType index, SplineSet<VectorType> splines);
    
    public final double valueAtIndex(VectorType index) {
        synchronized(reuseIpolData) { return valueAtIndex(index, reuseIpolData); }
    }
    
    protected abstract double valueAtIndex(VectorType index, SplineSet<VectorType> splines);
    
    protected abstract double valueAtIndex(IndexType numerator, IndexType denominator, SplineSet<VectorType> splines);

    
    protected final int getInterpolationOps() { return getInterpolationOps(getInterpolationType()); }
    
    protected abstract int getInterpolationOps(int type);
    
    
    public abstract RegularData<IndexType, VectorType> getCropped(IndexType from, IndexType to);
    
    public RegularData<IndexType, VectorType> newImage() { return newImage(getSize(), getElementType()); }
    
    public abstract RegularData<IndexType, VectorType> newImage(IndexType size, Class<? extends Number> elementType);
    
    
    public abstract ReferencedValues<IndexType, VectorType> getNeighbors();
    
    
    public synchronized void smooth(ReferencedValues<IndexType, VectorType> beam) {
        paste(getSmoothed(beam, null, null), false);
        addHistory("smoothed");
    }

    
    public synchronized void fastSmooth(ReferencedValues<IndexType, VectorType> beam, IndexType step) {
        paste(getFastSmoothed(beam, step, null, null), false);
        addHistory("smoothed (fast method)");
    }
    
   
    // Beam fitting: I' = C * sum(wBI) / sum(wB2)
    // I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
    // I' = sum(wBI)/sum(wB)
    // rms = Math.sqrt(1 / sum(wB))
    protected void getSmoothedValueAtIndex(final VectorType index, final ReferencedValues<IndexType, VectorType> beam, 
            final IndexedValues<IndexType> weight, final SplineSet<VectorType> splines, final WeightedPoint result) {   
        
        final VectorType i0 = getVectorInstance();
        i0.setDifference(index, beam.getReferenceIndex());
   
        final IndexType size = getSize();
        final IndexType beamSize = beam.getSize();
        final IndexType from = getIndexInstance();
        final IndexType to = getIndexInstance();
          
        for(int i=from.dimension(); --i >= 0; ) {
            from.setValue(i, Math.max(0, (int) Math.ceil(i0.getComponent(i))));
            to.setValue(i, Math.min(size.getValue(i), (int) Math.floor(i0.getComponent(i)) + beamSize.getValue(i)));
        }
              
        PointOp.Simple<IndexType> op = new PointOp.Simple<IndexType>() {
            private VectorType delta = getVectorInstance();
             
            @Override
            public void process(IndexType i1) { 
                if(!isValid(i1)) return;
                final double w = (weight == null ? 1.0 : weight.get(i1).doubleValue());
                
                for(int i=dimension(); --i >= 0; ) delta.setComponent(i, i1.getValue(i) - i0.getComponent(i));
                
                final double wB = w * beam.valueAtIndex(delta, splines);
                result.add(wB * get(i1).doubleValue());
                result.addWeight(Math.abs(wB));   
            }
        };

        result.noData();
        loop(op, from, to); // Not threadsafe unless splines is recreated for all threads...
        
        result.scaleValue(1.0 / result.weight()); 
    }
 
    
    public abstract int getPointSmoothOps(int beamPoints, int interpolationType);
    
 
    public final RegularData<IndexType, VectorType> getSmoothed(final ReferencedValues<IndexType, VectorType> beam, 
            final IndexedValues<IndexType> weight, final IndexedValues<IndexType> smoothedWeights) {
        
        final RegularData<IndexType, VectorType> convolved = newImage();

        Interpolation op = new Interpolation() {
            private WeightedPoint result;
            private VectorType v;
            
            @Override
            protected void init() {
                super.init();
                result = new WeightedPoint();
                v = getVectorInstance();
            }
            @Override
            public void process(IndexType index) { 
                if(!isValid(index)) return;
                index.toVector(v);
                getSmoothedValueAtIndex(v, beam, weight, getSplines(), result);  
                convolved.set(index, result.value());
                if(smoothedWeights != null) smoothedWeights.set(index, result.weight());
            }
            @Override
            public int numberOfOperations() {
                return 5 + getPointSmoothOps(beam.capacity(), NEAREST);
            }
        };
        
        loop(op);
        
        return convolved;
    }
    
    
    public RegularData<IndexType, VectorType> getFastSmoothed(final ReferencedValues<IndexType, VectorType> beam, 
            final IndexType step, final IndexedValues<IndexType> weight, final IndexedValues<IndexType> smoothedWeights) {
        if(step.getVolume() == 1) return getSmoothed(beam, weight, smoothedWeights);

        final IndexType n = getIndexInstance();
        
        n.setRatio(getSize(), step);

        final RegularData<IndexType, VectorType> coarseSignal = newImage(n, getElementType());
        final RegularData<IndexType, VectorType> coarseWeight = (smoothedWeights == null) ? null : newImage(n, weight.getElementType());

        Interpolation op = new Interpolation() {
            WeightedPoint result;
            IndexType scaled;
            VectorType v;

            @Override
            public void init() {
                super.init();
                result = new WeightedPoint();
                scaled = getIndexInstance();
                v = getVectorInstance();
            }

            @Override
            public void process(IndexType index) {
                scaled.setProduct(index, step);
                scaled.toVector(v);
                getSmoothedValueAtIndex(v, beam, weight, getSplines(), result);
                coarseSignal.set(index, result.value());
                if(coarseWeight != null) coarseWeight.set(index, result.weight());
                if(result.weight() <= 0.0) coarseSignal.discard(index);
            }   

            @Override
            public int numberOfOperations() {
                return 5 + getPointSmoothOps(beam.capacity() / step.getVolume(), NEAREST);
            }
        };

        coarseSignal.smartFork(op); 
        
        final RegularData<IndexType, VectorType> convolved = newImage();


        Interpolation interpolation = new Interpolation() {
            @SuppressWarnings("null")
            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                
                final double value = coarseSignal.valueAtIndex(index, step, getSplines());

                if(!Double.isNaN(value)) {      
                    convolved.set(index, value);
                    if(smoothedWeights != null) smoothedWeights.set(index, coarseWeight.valueAtIndex(index, step, getSplines()));
                }
                else {
                    convolved.discard(index);
                    if(smoothedWeights != null) smoothedWeights.set(index, 0);
                } 
            }
            @Override
            public int numberOfOperations() {
                return 9 + (smoothedWeights == null ? 1 : 2) * getInterpolationOps();
            }
        };
        
        smartFork(interpolation); 
        
        return convolved; 
    }


 
 

    public synchronized void resampleFrom(final RegularData<IndexType, VectorType> image, final Transforming<VectorType> toSourceIndex, 
            final ReferencedValues<IndexType, VectorType> beam, final IndexedValues<IndexType> weight) {

        Interpolation interpolation = new Interpolation() {
            private VectorType v;
            private WeightedPoint smoothedValue;

            @Override
            protected void init() { 
                super.init();
                v = getVectorInstance();
                smoothedValue = new WeightedPoint();
            }

            @Override
            public void process(IndexType index) {
                for(int i=index.dimension(); --i >= 0; ) v.setComponent(i, (double) index.getValue(i));
               
                toSourceIndex.transform(v);

                if(!image.containsIndex(v)) {
                    discard(index);
                }
                else if(beam == null) {
                    double value = image.valueAtIndex(v, getSplines());
                    if(java.lang.Double.isNaN(value)) discard(index);
                    else set(index, value);
                }
                else {
                    image.getSmoothedValueAtIndex(v, beam, weight, getSplines(), smoothedValue);          
                    if(smoothedValue.weight() > 0.0) set(index, smoothedValue.value());
                    else discard(index);
                }
            }
            @Override
            public int numberOfOperations() {
                return 5 + (beam == null ? getInterpolationOps() : getPointSmoothOps(beam.capacity(), getInterpolationType()));
            }
        };
        
        smartFork(interpolation);

        clearHistory();
        addHistory("resampled " + getSizeString() + " from " + image.getSizeString());
    }


    public <V extends TrueVector<Double>> void addPatchAt(final VectorType index, final RegularData<IndexType, V> patch, final double scaling) {
        addPatchAt(index, patch, scaling, false);
    }


    public <V extends TrueVector<Double>> void addPatchAt(final VectorType index, final RegularData<IndexType, V> patch, final double scaling, boolean parallel) {
        IndexType size = getSize();
        IndexType patchSize = patch.getSize();
        IndexType min = getIndexInstance();
        IndexType max = getIndexInstance();   
        
        for(int i=dimension(); --i >= 0; ) {
            min.setValue(i, Math.max(0, (int) Math.floor(index.getComponent(i))));
            max.setValue(i, Math.min(size.getValue(i), (int) Math.ceil(index.getComponent(i)) + patchSize.getValue(i)));
        }
        
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private V d;
            private SplineSet<V> iPolData;
            
            @Override
            public void init() {
                super.init();
                d = patch.getVectorInstance();
                iPolData = new SplineSet<V>(dimension());
            }
            
            @Override
            public void process(IndexType i0) {
                for(int i=dimension(); --i >= 0; ) d.setComponent(i, i0.getValue(i) - index.getComponent(i));
                double patchValue = patch.valueAtIndex(d, iPolData);
                if(!Double.isNaN(patchValue)) add(i0, scaling * patchValue);
            }
        };
        
        smartFork(op, min, max);
    }
    
    
    public RegularData<IndexType, VectorType> clean(final RegularData<IndexType, VectorType> beam, final VectorType beamCenterIndex, final double gain, final double threshold) { 
        RegularData<IndexType, VectorType> clean = newImage();

        final int maxComponents = (int) Math.ceil(countPoints() / gain);         
        int components = 0;

        IndexType peakIndex = indexOfMaxDev();
        double peakValue = get(peakIndex).doubleValue();

        VectorType offset = getVectorInstance();

        while(Math.abs(peakValue) > threshold && components < maxComponents) { 
            final double componentValue = gain * peakValue;
            
            for(int i=dimension(); --i >= 0; ) offset.setComponent(i, peakIndex.getValue(i) - beamCenterIndex.getComponent(i));
 
            addPatchAt(offset, beam, -componentValue);
            clean.add(peakIndex, componentValue);

            components++;

            peakIndex = indexOfMaxDev();
            peakValue = get(peakIndex).doubleValue();
        }
        
        // Scale cleaned components by the beam area...
        clean.scale(beam.getAbsSum());

        addHistory("cleaned away " + components + " components.");
        clean.addHistory("created with " + components + " clean components.");

        return clean;
    }
    
    
    
    
    @Override
    public void despike(double threshold) {
        despike(threshold, null);
    }

    public synchronized void despike(final double significance, final IndexedValues<IndexType> noiseWeight) {
        final ReferencedValues<IndexType, VectorType> neighbours = getNeighbors();

        Interpolation op = new Interpolation() {  
            private WeightedPoint point, surrounding;
            private VectorType v;
            
            @Override
            protected void init() {
                point = new WeightedPoint();
                surrounding = new WeightedPoint();
                v = getVectorInstance();
            }
            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;

                point.setValue(get(index).doubleValue());
                point.setWeight(noiseWeight == null ? 1.0 : noiseWeight.get(index).doubleValue());

                index.toVector(v);
                getSmoothedValueAtIndex(v, neighbours, noiseWeight, getSplines(), surrounding);

                point.subtract(surrounding);

                if(DataPoint.significanceOf(point) > significance) discard(index);             
            }
        };
        
        smartFork(op);

        addHistory("despiked at " + Util.S3.format(significance));
    }


    public final void discardIsolated(final int minNeighbors) { 
        if(minNeighbors < 1) return;   // Nothing to do...
        validate(getNeighborValidator(minNeighbors));
    }

    
    public abstract Validating<IndexType> getNeighborValidator(final int minNeighbors);
    
    
    @Override
    public Object getFitsData(Class<? extends Number> dataType) {  
        final Data<IndexType> transpose = newImage(getSize().getReversed(), dataType);
        
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private IndexType reversed;
            
            @Override
            public void init() {
                super.init();
                reversed = getIndexInstance();
            }
            
            @Override
            public void process(IndexType index) {
                index.reverseTo(reversed);
                transpose.set(reversed, isValid(index) ? get(index) : getBlankingValue());
            }     
        };
        
        smartFork(op);
       
        if(getUnit().value() != 1.0) transpose.scale(1.0 / getUnit().value());

        return transpose.getUnderlyingData();
    }

    
     
    
    public abstract class Interpolation extends ParallelPointOp.Simple<IndexType> {
        private SplineSet<VectorType> splines = new SplineSet<VectorType>(dimension());
        
        @Override
        protected Interpolation clone() {
            Interpolation clone = (Interpolation) super.clone();
            clone.splines = new SplineSet<VectorType>(dimension());
            return clone;
        }

        public final SplineSet<VectorType> getSplines() { return splines; }
    }   
    
    
    
   
    public final static int NEAREST = 0;
    public final static int LINEAR = 1;
    public final static int QUADRATIC = 2;
    public final static int SPLINE = 3;
}

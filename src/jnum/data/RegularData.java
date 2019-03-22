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
import jnum.data.transform.Stretch;
import jnum.math.TrueVector;
import jnum.parallel.ParallelPointOp;

public abstract class RegularData<IndexType extends Index<IndexType>, VectorType extends TrueVector<Double>> extends Data<IndexType> {
    protected SplineSet<VectorType> reuseIpolData;


    public RegularData() {
        reuseIpolData = new SplineSet<>(dimension());
        setInterpolationType(SPLINE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RegularData<IndexType, VectorType> clone() {
        RegularData<IndexType, VectorType> clone = (RegularData<IndexType, VectorType>) super.clone();
        clone.reuseIpolData = new SplineSet<>(dimension());  
        return clone;
    }


    @Override
    public String getInfo() {
        return "Data Size: " + getSizeString() + " elements.";
    }
    
    @Override
    public final String getSizeString() {
        return getSize().toString("x");
    }
   
    @Override
    public String toString(IndexType index) {
        return "[" + index + "]=" + Util.S3.format(get(index));
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


    public RegularData<IndexType, VectorType> newImage() { return newImage(getSize(), getElementType()); }

    public abstract RegularData<IndexType, VectorType> newImage(IndexType size, Class<? extends Number> elementType);

    public final synchronized void smooth(Referenced<IndexType, VectorType> beam) {
        smooth(beam.getData(), beam.getReferenceIndex());
    }

    public synchronized void smooth(RegularData<IndexType, VectorType> beam, VectorType refIndex) {
        paste(getSmoothed(beam, refIndex, null, null), false);
        addHistory("smoothed");
    }


    public final synchronized void fastSmooth(Referenced<IndexType, VectorType> beam, IndexType step) {
        fastSmooth(beam.getData(), beam.getReferenceIndex(), step);
    }


    public synchronized void fastSmooth(RegularData<IndexType, VectorType> beam, VectorType refIndex, IndexType step) {
        paste(getFastSmoothed(beam, refIndex, step, null, null), false);
        addHistory("smoothed (fast method)");
    }


    // Beam fitting: I' = C * sum(wBI) / sum(wB2)
    // I(x) = I -> I' = I -> C = sum(wB2) / sum(wB)
    // I' = sum(wBI)/sum(wB)
    // rms = Math.sqrt(1 / sum(wB))
    public void getSmoothedValueAtIndex(final VectorType index, final RegularData<IndexType, VectorType> beam, final VectorType refIndex, 
            final IndexedValues<IndexType> weight, final SplineSet<VectorType> splines, final WeightedPoint result) {   

        final VectorType i0 = getVectorInstance();
        i0.setDifference(index, refIndex);

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

    public final RegularData<IndexType, VectorType> getSmoothed(final Referenced<IndexType, VectorType> beam, 
            final IndexedValues<IndexType> weight, final IndexedValues<IndexType> smoothedWeights) {
        return getSmoothed(beam.getData(), beam.getReferenceIndex(), weight, smoothedWeights);
    }

    public final RegularData<IndexType, VectorType> getSmoothed(final RegularData<IndexType, VectorType> beam, final VectorType refIndex, 
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
                getSmoothedValueAtIndex(v, beam, refIndex, weight, getSplines(), result);  
                convolved.set(index, result.value());
                if(smoothedWeights != null) smoothedWeights.set(index, result.weight());
            }
            @Override
            public int numberOfOperations() {
                return 5 + getPointSmoothOps(beam.capacity(), NEAREST);
            }
        };

        smartFork(op);
        
        convolved.addHistory("smoothed copy");

        return convolved;
    }


    public final RegularData<IndexType, VectorType> getFastSmoothed(final Referenced<IndexType, VectorType> beam,
            final IndexType step, final IndexedValues<IndexType> weight, final IndexedValues<IndexType> smoothedWeights) {
        return getFastSmoothed(beam.getData(), beam.getReferenceIndex(), step, weight, smoothedWeights);
    }

    public RegularData<IndexType, VectorType> getFastSmoothed(final RegularData<IndexType, VectorType> beam, final VectorType refIndex,
            final IndexType step, final IndexedValues<IndexType> weight, final IndexedValues<IndexType> smoothedWeights) {
        if(step.getVolume() == 1) return getSmoothed(beam, refIndex, weight, smoothedWeights);

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
                getSmoothedValueAtIndex(v, beam, refIndex, weight, getSplines(), result);
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
        
        convolved.addHistory("smoothed copy (fast method)");
        
        return convolved; 
    }


    public synchronized void resampleFrom(final RegularData<IndexType, VectorType> image, final Transforming<VectorType> toSourceIndex, 
            final Referenced<IndexType, VectorType> beam, final IndexedValues<IndexType> weight) {
        
        if(beam != null) resampleFrom(image, toSourceIndex, beam.getData(), beam.getReferenceIndex(), weight);
        else resampleFrom(image, toSourceIndex, null, null, weight);
    }


    public synchronized void resampleFrom(final RegularData<IndexType, VectorType> image, final Transforming<VectorType> toSourceIndex, 
            final RegularData<IndexType, VectorType> beam, final VectorType refIndex, final IndexedValues<IndexType> weight) {
           
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
                    if(Double.isNaN(value)) discard(index);
                    else set(index, value);
                }
                else {
                    image.getSmoothedValueAtIndex(v, beam, refIndex, weight, getSplines(), smoothedValue);          
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
                iPolData = new SplineSet<>(dimension());
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
        final Referenced<IndexType, VectorType> neighbours = getNeighbors();

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
                getSmoothedValueAtIndex(v, neighbours.getData(), neighbours.getReferenceIndex(), noiseWeight, getSplines(), surrounding);

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



    public final Validating<IndexType> getNeighborValidator(final int minNeighbors) {
        return new Validating<IndexType>() {
            private PointOp.Sum<IndexType> op = new PointOp.Sum<IndexType>() {
                @Override
                protected double getValue(IndexType index) {
                    return RegularData.this.isValid(index) ? 1.0 : 0.0; 
                }
            };

            @Override
            public boolean isValid(IndexType index) {
                int self = RegularData.this.isValid(index) ? 1 : 0;

                final IndexType size = getSize();
                final IndexType from = getIndexInstance();
                final IndexType to = getIndexInstance();
                
                for(int i=dimension(); --i >= 0; ) {
                    from.setValue(i, Math.max(0, index.getValue(i)-1));
                    to.setValue(i, Math.min(size.getValue(i), index.getValue(i) + 1));
                }
                
                return loop(op, from, to) >= minNeighbors + self;         
            }

            @Override
            public void discard(IndexType index) {
                RegularData.this.discard(index);
            }
        };
    }

    

    public final Referenced<IndexType, VectorType> getNeighbors() {
        final IndexType size = getIndexInstance();
        final IndexType center = getIndexInstance();

        size.fill(3);
        center.fill(1);

        final RegularData<IndexType, VectorType> neighbors = newImage(size, Double.class);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if(index.equals(center)) return;
                double d = index.distanceTo(center);
                neighbors.set(index, 1.0 / (d*d));
            }
        };

        neighbors.loop(op);

        VectorType refIndex = getVectorInstance();
        center.toVector(refIndex);

        return new ReferencedData<>(neighbors, refIndex);
    }


    public final void resampleFrom(final RegularData<IndexType, VectorType> image) {     
        VectorType scale = getVectorInstance();

        IndexType imageSize = image.getSize();
        IndexType size = getSize();

        boolean antiAlias = false;
        for(int i=dimension(); --i >= 0; ) {
            double scalar = (double) imageSize.getValue(i) / size.getValue(i);
            if(scalar > 1.0) antiAlias = true;
            scale.setComponent(i, scalar);
        }

        Referenced<IndexType, VectorType> beam = antiAlias ? getGaussianBeam(scale) : null;        
        resampleFrom(image, new Stretch<>(scale), beam, null);
    }


    public Referenced<IndexType, VectorType> getGaussianBeam(final VectorType pixelFWHMs) {
        final IndexType size = getIndexInstance();
        final VectorType center = getVectorInstance();

        for(int i=dimension(); --i >= 0; ) {
            size.setValue(i, 1 + 2 * (int) Math.ceil(3.0 * Math.abs(pixelFWHMs.getComponent(i))));
            center.setComponent(i, 0.5 * (size.getValue(i) - 1));
        }
        
        final RegularData<IndexType, VectorType> beam = newImage(size, Double.class);

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            private VectorType v;
            
            @Override
            public void init() {
                super.init();
                v = getVectorInstance();
            }
            
            @Override
            public void process(IndexType index) {
                index.toVector(v);
                v.subtract(center);
                for(int i=dimension(); --i >= 0; ) v.setComponent(i, v.getComponent(i) / pixelFWHMs.getComponent(i));
                beam.set(index, Math.exp(-0.5 * v.absSquared()));
            }
        };

        beam.smartFork(op);

        return new ReferencedData<>(beam, center);
    }



    public RegularData<IndexType, VectorType> getCropped(final IndexType from, final IndexType to) {
        final IndexType size = getIndexInstance();
        size.setDifference(to, from);

        final RegularData<IndexType, VectorType> cropped = newImage(size, getElementType());

        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                final boolean isValid = isValid(index);

                if(isValid) {
                    Number value = get(index);
                    index.subtract(from);
                    cropped.set(index, value);
                }
                else {
                    index.subtract(from);
                    cropped.discard(index);
                }     
            }        
        };

        smartFork(op, from, to);

        return cropped;
    }   




    public VectorType getCentroidIndex() {

        class Result {
            VectorType centroid = getVectorInstance();
            double sumw = 0.0;
        }

        ParallelPointOp<IndexType, Result> op = new ParallelPointOp<IndexType, Result>() {
            private Result localResult;

            @Override
            protected void init() {
                localResult = new Result();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return; 
                VectorType c = localResult.centroid;
                final double w = Math.abs(get(index).doubleValue());
                for(int i=dimension(); --i >= 0; ) c.setComponent(i, c.getComponent(i) + w * index.getValue(i));
                localResult.sumw += w;
            }

            @Override
            public Result getResult() {
                Result result = new Result();
                result.centroid = localResult.centroid;
                result.sumw = localResult.sumw;
                result.centroid.scale(1.0 / result.sumw);
                return result;
            }

            @Override
            public void mergeResult(Result localResult) {
                this.localResult.centroid.add(localResult.centroid);
                this.localResult.sumw += localResult.sumw;
            }
        };

        smartFork(op);

        return op.getResult().centroid;
    }




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

        return transpose.getCore();
    }






    public abstract class Interpolation extends ParallelPointOp.Simple<IndexType> {
        private SplineSet<VectorType> splines = new SplineSet<>(dimension());

        @Override
        protected Interpolation clone() {
            Interpolation clone = (Interpolation) super.clone();
            clone.splines = new SplineSet<>(dimension());
            return clone;
        }

        public final SplineSet<VectorType> getSplines() { return splines; }
    }   




    public final static int NEAREST = 0;
    public final static int LINEAR = 1;
    public final static int QUADRATIC = 2;
    public final static int SPLINE = 3;
}

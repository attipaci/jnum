/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

import java.util.concurrent.ExecutorService;

import jnum.Function;
import jnum.PointOp;
import jnum.data.index.Index;
import jnum.data.index.IndexedEntries;
import jnum.data.index.IndexedValues;
import jnum.fft.DoubleFFT;
import jnum.fft.FloatFFT;
import jnum.fft.MultiFFT;
import jnum.math.Complex;
import jnum.math.ComplexConjugate;
import jnum.math.Scalable;
import jnum.parallel.ParallelPointOp;

/**
 * Provides a complex view a real-valued regularly sampled data object, in which complex values are stored
 * as consecutive real/imaginary pairs inside a primitive array (or arrays of) of real numbers. Such representation
 * of complex numbers can arise from e.g. the in-place Fast Fourier Transform (FFT) of real-valued data. 
 * This class provides access to the real-number pairs as complex values.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   the generic type of (possibly multi-dimensional) index used for locating the 
 *                      complex-valued elements.
 */
public class ComplexView<IndexType extends Index<IndexType>> implements IndexedEntries<IndexType, Complex>, Scalable, ComplexConjugate {
    private RegularData<IndexType, ?> pairs;
    private int last; 
    
    public ComplexView(RegularData<IndexType, ?> pairs) {
        this.pairs = pairs;
        last = pairs.dimension() - 1;
    }
    
    public final RegularData<IndexType, ?> getData() {
        return pairs;
    }
    
    @Override
    public final IndexType getSize() {
        IndexType size = pairs.getSize();
        size.setValue(last, size.getValue(last) >>> 1);
        return size;
    }
    
    @Override
    public final Complex get(IndexType index) {
        Complex z = new Complex();
        get(index, z);
        return z;
    }
    
    public void get(IndexType index, Complex z) {
        index.setValue(last, index.getValue(last) << 1);
        z.setRealPart(pairs.get(index).doubleValue());
        index.increment(last);
        z.setImaginaryPart(pairs.get(index).doubleValue());
        index.setValue(last, index.getValue(last) >>> 1);
    }
    
    @Override
    public void set(IndexType index, Complex z) {
        index.setValue(last, index.getValue(last) << 1);
        pairs.set(index, z.re());
        index.increment(last);
        pairs.set(index, z.im());
        index.setValue(last, index.getValue(last) >>> 1);
    }

    public void discard(IndexType index) {
        index.setValue(last, index.getValue(last) << 1);
        pairs.discard(index);
        index.increment(last);
        pairs.discard(index);
        index.setValue(last, index.getValue(last) >>> 1);
    }
    
    @Override
    public final int capacity() {
        return pairs.capacity() >>> 1; 
    }

    @Override
    public final int dimension() { 
        return pairs.dimension();
    }

    @Override
    public final int getSize(int i) throws IllegalArgumentException {
        return i == last ? pairs.getSize(i) >>> 1 : pairs.getSize(i); 
    }

    @Override
    public final IndexType getIndexInstance() { 
        return pairs.getIndexInstance();
    }

    @Override
    public final boolean conformsTo(IndexType size) {
        return getSize().equals(size);
    }

    @Override
    public final boolean containsIndex(IndexType index) {
        IndexType size = getSize();
        for(int i=dimension(); --i >= 0; ) {
            int j = index.getValue(i);
            if(j < 0) return false;
            if(j > size.getValue(i)) return false;
        }
        return true;
    }

    @Override
    public void conjugate() {
        pairs.smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if((index.getValue(last) & 1) == 0) return;
                pairs.scale(index, -1.0);
            } 
        });
    }

    @Override
    public void scale(double factor) {
        pairs.scale(factor);
    }

    @Override
    public boolean isValid(IndexType index) throws IndexOutOfBoundsException {
        boolean valid = true;
        index.setValue(last, index.getValue(last) << 1);
        valid &= pairs.isValid(index);
        index.increment(last);
        valid &= pairs.isValid(index);
        index.setValue(last, index.getValue(last) >>> 1);
        return valid;
    }

    @Override
    public int getParallel() {
        return pairs.getParallel();
    }
    
    public final ExecutorService getExecutor() {
        return pairs.getExecutor();
    }

    @Override
    public <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        return from.loop(op,  to);
    }
    
    public RegularData<IndexType, ?> getRealMapped(final Function<Complex, Double> f) {
        RegularData<IndexType, ?> data = pairs.newImage();
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            Complex z;
            
            @Override
            public void init() {
                super.init();
                z = new Complex();
            }
            
            @Override
            public void process(IndexType point) {
                if (isValid(point)) {
                    get(point, z);
                    data.set(point, f.valueAt(z));
                }
                else data.discard(point);
            }
        });
        
        return data;
    }
    
    public ComplexView<IndexType> getMapped(final Function<Complex, Complex> f) {
        ComplexView<IndexType> view = new ComplexView<>(pairs.newImage());
        smartFork(new ParallelPointOp.Simple<IndexType>() {
            Complex z;
            
            @Override
            public void init() {
                super.init();
                z = new Complex();
            }
            
            @Override
            public void process(IndexType point) {
                if(isValid(point)) {
                    get(point, z);
                    view.set(point, f.valueAt(z));
                }
                else discard(point);
            }
        });
        
        return view;
    }
    
    public void apply(Function<Complex, Complex> f) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            Complex z;
            
            @Override
            protected void init() {
                super.init();
                z = new Complex();
            }

            @Override
            public void process(IndexType index) {
                if(!isValid(index)) return;
                get(index, z);
                set(index, f.valueAt(z));
            }
        };
        
        smartFork(op, getIndexInstance(), getSize());
    }    
    
    
    public RegularData<IndexType, ?> getRealPart() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.re(); }
        });
    }
    
    public RegularData<IndexType, ?> getImaginaryPart() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.im(); }
        });
    }
    
    public RegularData<IndexType, ?> getAmplitudes() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.abs(); }
        });
    }
    
    public RegularData<IndexType, ?> getPhases() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.angle(); }
        });
    }
    
    public RegularData<IndexType, ?> getSquareNorms() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.squareNorm(); }
        });
    }
    

    
    public void multiplyBy(ComplexView<IndexType> x) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            Complex z, factor;
            
            @Override
            protected void init() {
                super.init();
                z = new Complex();
                factor = new Complex();
            }

            @Override
            public void process(IndexType index) {
                if(x.isValid(index)) {
                    get(index, z);
                    x.get(index, factor);
                    z.multiplyBy(factor);
                    set(index, z);
                }
                else discard(index);
            }
        };
        
        smartFork(op, getIndexInstance(), getSize());
    }
   
    
    public void divideBy(ComplexView<IndexType> x) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            Complex z, factor;
            
            @Override
            protected void init() {
                super.init();
                z = new Complex();
                factor = new Complex();
            }

            @Override
            public void process(IndexType index) { 
                if(x.isValid(index)) {
                    get(index, z);
                    x.get(index, factor);
                    z.divideBy(factor);
                    set(index, z);
                }
                else discard(index);
            }
        };
        
        smartFork(op, getIndexInstance(), getSize());
    }
    
    public void multiplyByNormsOf(ComplexView<IndexType> x) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            Complex z;
            
            @Override
            protected void init() {
                super.init();
                z = new Complex();
            }

            @Override
            public void process(IndexType index) { 
                if(x.isValid(index)) {
                    x.get(index, z);
                    double norm = z.squareNorm();
                    get(index, z);
                    z.scale(norm);
                    set(index, z);
                }
                else discard(index);
            }
        };
        
        smartFork(op, getIndexInstance(), getSize());
    }
    
    public void divideByNormsOf(ComplexView<IndexType> x) {
        ParallelPointOp.Simple<IndexType> op = new ParallelPointOp.Simple<IndexType>() {
            Complex z;
            
            @Override
            protected void init() {
                super.init();
                z = new Complex();
            }

            @Override
            public void process(IndexType index) { 
                if(x.isValid(index)) {
                    x.get(index, z);
                    double norm = z.squareNorm();
                    get(index, z);
                    z.scale(1.0 / norm);
                    set(index, z);
                }
                else discard(index);
            }
        };
        
        smartFork(op, getIndexInstance(), getSize());
    }
    
    public RegularData<IndexType, ?> getBackFFT(boolean integralNorm) {
        return getBackFFT(getSize(), integralNorm);
    }
    
    public RegularData<IndexType, ?> getBackFFT(IndexType transformSize, boolean integralNorm) {
        return getBackFFT(transformSize, integralNorm, null, null);
    }

    public RegularData<IndexType, ?> getBackFFT(IndexType dataSize, boolean integralNorm, final IndexType refIndex, final IndexedValues<IndexType, ?> weight) {   
        final IndexType size = pairs.getSize().copy();
        
        // Assume the Nyquist frequency is unrolled into a complex pair after 2^N...
        size.setValue(dimension() - 1, size.getValue(dimension() - 1) - 2);
        
        // The last dimension must be a multiple of 2 (complex pairs)
        if(size.getValue(dimension() - 1) < 2) size.setValue(dimension() - 1, 2);
        
        size.toPaddedFFTSize();

        final double renorm = integralNorm ? 1.0 : 2.0 / size.getVolume();
        
        IndexType unrolledSize = size.copy();
        unrolledSize.setValue(dimension() - 1, unrolledSize.getValue(dimension() - 1) + 2);
        
        size.limit(getSize());

        RegularData<IndexType, ?> transformer = pairs.newImage(unrolledSize, Double.class);
        pairs.copyTruncatedTo(transformer);    
       
        Object core = transformer.getCore();
        
        if (core instanceof float[]) new FloatFFT.NyquistUnrolled(getExecutor()).realTransform((float[]) core, false);
        else if (core instanceof double[]) new DoubleFFT.NyquistUnrolled(getExecutor()).realTransform((double[]) core, false);
        else new MultiFFT(getExecutor()).realTransform((Object[]) core, false);
      
        RegularData<IndexType, ?> data = pairs.newImage(dataSize, pairs.getElementType());
        
        ParallelPointOp.Simple<IndexType> unloader = new ParallelPointOp.Simple<IndexType>() {
            IndexType packed;
            
            @Override
            protected void init() {
                if(refIndex != null) packed = getIndexInstance();
            }

            @Override
            public void process(IndexType index) {
                if(refIndex != null) {
                    packed.setDifference(index, refIndex);
                    packed.wrap(size);
                }
                else packed = index;
                
                if(!transformer.isValid(packed)) return;
                
                double value = renorm * transformer.get(packed).doubleValue();
                
                if(weight == null) data.set(index, value);
                else {
                    double w = weight.get(index).doubleValue();
                    if(w > 0.0) data.set(index, value / w); 
                }
            }
            
            @Override
            public int numberOfOperations() {
                return (3 + (refIndex == null ? 0 : 4) + (weight == null ? 0 : 2)) * dimension();
            }
        };
        
        data.smartFork(unloader);  
        
        return data;
    }
    
}

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
import jnum.NonConformingException;
import jnum.PointOp;
import jnum.data.index.Index;
import jnum.data.index.IndexedEntries;
import jnum.data.index.IndexedValues;
import jnum.fft.DoubleFFT;
import jnum.fft.FloatFFT;
import jnum.fft.MultiFFT;
import jnum.math.Complex;
import jnum.math.ComplexConjugate;
import jnum.math.LinearAlgebra;
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
public class ComplexView<IndexType extends Index<IndexType>> 
implements IndexedEntries<IndexType, Complex>, Scalable, ComplexConjugate, LinearAlgebra<ComplexView<IndexType>> {
    private RegularData<IndexType, ?> pairs;
    private int last; 
    
    /**
     * Instantiates a new view of pairwise data as complex numbers of the same dimensionality
     * 
     * @param pairs     the data that contains pairwise real numbers that constitute the complex components.
     */
    public ComplexView(RegularData<IndexType, ?> pairs) {
        this.pairs = pairs;
        last = pairs.dimension() - 1;
    }
    
    /**
     * Returns the underlying pairwise real data object
     * 
     * @return      the real-valued 1D or multi-dimensional data on which this comple view is based.
     */
    public final RegularData<IndexType, ?> getData() {
        return pairs;
    }
    
    @Override
    public final IndexType getSize() {
        IndexType size = pairs.getSize();
        size.setComponent(last, size.getComponent(last) >>> 1);
        return size;
    }
    
    @Override
    public final Complex get(IndexType index) {
        Complex z = new Complex();
        get(index, z);
        return z;
    }
    
    /**
     * Returns the complex value at the specified index, in the supplied complex number object
     * 
     * @param index     the index of the complex data point, as if the data were a true complex (multi-dimensional) array.
     * @param z         the complex value in which to return the complex element at the requested index.
     */
    public void get(IndexType index, Complex z) {
        index.setComponent(last, index.getComponent(last) << 1);
        z.setRealPart(pairs.get(index).doubleValue());
        index.increment(last);
        z.setImaginaryPart(pairs.get(index).doubleValue());
        index.setComponent(last, index.getComponent(last) >>> 1);
    }
    
    @Override
    public void set(IndexType index, Complex z) {
        index.setComponent(last, index.getComponent(last) << 1);
        pairs.set(index, z.re());
        index.increment(last);
        pairs.set(index, z.im());
        index.setComponent(last, index.getComponent(last) >>> 1);
    }
    
    @Override
    public Complex get(int... index) throws NonConformingException {
        index[last] <<= 1;
        Complex z = new Complex(pairs.get(index).doubleValue());
        index[last]++;
        z.setImaginaryPart(pairs.get(index).doubleValue());
        index[last] >>>= 1;
        return z;
    }
    
    /**
     * Discards the complex data point (real and imaginary part) at the specified index.
     * 
     * @param index     the index of the complex data point, as if the data were a true complex (multi-dimensional) array.
     */
    public void discard(IndexType index) {
        index.setComponent(last, index.getComponent(last) << 1);
        pairs.discard(index);
        index.increment(last);
        pairs.discard(index);
        index.setComponent(last, index.getComponent(last) >>> 1);
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
            int j = index.getComponent(i);
            if(j < 0) return false;
            if(j > size.getComponent(i)) return false;
        }
        return true;
    }

    @Override
    public void conjugate() {
        pairs.smartFork(new ParallelPointOp.Simple<IndexType>() {
            @Override
            public void process(IndexType index) {
                if((index.getComponent(last) & 1) == 0) return;
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
        index.setComponent(last, index.getComponent(last) << 1);
        boolean valid = pairs.isValid(index);
        index.increment(last);
        valid &= pairs.isValid(index);
        index.setComponent(last, index.getComponent(last) >>> 1);
        return valid;
    }

    @Override
    public int getParallel() {
        return pairs.getParallel();
    }
    
    /**
     * Returns the executor service that is used for (parallel) processing this data object.
     * 
     * @return      the executor service used or <code>null</code>.
     */
    public final ExecutorService getExecutor() {
        return pairs.getExecutor();
    }

    @Override
    public <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        return from.loop(op,  to);
    }
    
    /**
     * Returns a real-valued data object, of the same dimensionality and shape as the complex-valued data reprsented by this
     * instance, using the supplied function mapping complex data point to real values.
     * 
     * @param f     the function that maps each complex point in this instance to a real value.
     * @return      the real-valued data object that containes the result of this complex data mapped by the designated function.
     */
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
    
    /**
     * Returns a re-mapped version of this complex view of data, using the specified function.
     * 
     * @param f     the function that maps complex data from this instance into the return value.
     * @return      another complex view with the same size and dimension as this intance, containing values
     *              mapped from this instance by the specified function.
     *              
     * @see #apply(Function)
     * @see #getRealMapped(Function)
     */
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
    
    /**
     * Modifies this complex data view by applying the specified function rto every element of it, in place.
     * 
     * @param f     the function that is applied to every complex element of this instance.
     * 
     * @see #getMapped(Function)
     */
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
    
    /**
     * Returns the real part of every element in this complex data instance as a new real-valued data object of
     * the same dimensionality and shape as this instance.
     * 
     * @return  a new data object of the same size and shape as the complex data represented by this object, 
     *          containing the real parts of each complex element in this instance.
     *          
     * @see #getImaginaryPart()
     */
    public RegularData<IndexType, ?> getRealPart() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.re(); }
        });
    }
    
    /**
     * Returns the imaginary part of every element in this complex data instance as a new real-valued data object of
     * the same dimensionality and shape as this instance.
     * 
     * @return  a new data object of the same size and shape as the complex data represented by this object, 
     *          containing the imaginary parts of each complex element in this instance.
     *          
     * @see #getRealPart()
     */
    public RegularData<IndexType, ?> getImaginaryPart() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.im(); }
        });
    }
    
    /**
     * Returns the real amplitude (absolute value) of every element in this complex data instance as a new 
     * real-valued data object of the same dimensionality and shape as this instance.
     * 
     * @return  a new data object of the same size and shape as the complex data represented by this object, 
     *          containing the amplitudes (absolute values) of each complex element in this instance.
     *          
     * @see #getPhases()
     * @see #getSquareNorms()
     */
    public RegularData<IndexType, ?> getAmplitudes() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.abs(); }
        });
    }
    
    /**
     * Returns the real phase (angle) of every element in this complex data instance as a new 
     * real-valued data object of the same dimensionality and shape as this instance.
     * 
     * @return  (rad) a new data object of the same size and shape as the complex data represented by this object, 
     *          containing the phases (angles) of each complex element in this instance in the [-&pi;:&pi;] range.
     *          
     * @see #getAmplitudes()
     */
    public RegularData<IndexType, ?> getPhases() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.angle(); }
        });
    }
    
    /**
     * Returns the squared norms (<i>z</i> &rightarrow; |<i>z</i>|<sup>2</sup>) of every element in this complex data instance as a new 
     * real-valued data object of the same dimensionality and shape as this instance.
     * 
     * @return  a new data object of the same size and shape as the complex data represented by this object, 
     *          containing the amplitudes (absolute values) of each complex element in this instance.
     *          
     * @see #getAmplitudes()
     */
    public RegularData<IndexType, ?> getSquareNorms() {
        return getRealMapped(new Function<Complex, Double>() {
            @Override
            public Double valueAt(Complex z) { return z.squareNorm(); }
        });
    }
    

    /**
     * Multiplies every complex-valued element of this instance with the corresponding element of the argument.
     * 
     * @param x     the complex data view, whose elements contain the element-wise multiplicative factors.
     * 
     * @see #divideBy(ComplexView)
     * @see #multiplyByNormsOf(ComplexView)
     */
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
   
    /**
     * Divides every complex-valued element of this instance with the corresponding element of the argument.
     * 
     * @param x     the complex data view, whose elements contain the element-wise divisors.
     * 
     * @see #multiplyBy(ComplexView)
     */
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
    
    /**
     * Multiplies every complex-valued element of this instance with the square norm of the corresponding element of the argument.
     * 
     * @param x     the complex data view, whose elements contain the complex values whose norms are used as element-wise 
     *              multiplicative factors.
     * 
     * @see #multiplyBy(ComplexView)
     * @see #divideByNormsOf(ComplexView)
     */
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
    
    /**
     * Divides every complex-valued element of this instance with the square norm of the corresponding element of the argument.
     * 
     * @param x     the complex data view, whose elements contain the complex values whose norms are used as element-wise 
     *              divisors.
     * 
     * @see #divideBy(ComplexView)
     * @see #multiplyByNormsOf(ComplexView)
     */
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
    
    /**
     * Returns the backward Fast Fourier Transform (FFT) of the complex amplitudes represented by this instance,
     * as a real-valued data object.
     * 
     * @param integralNorm  if <code>false</code> the resulting FFT is normalzied as if the inverse of the forward FFT 
     *                      that would yield this instance. Otherwise, the FFT is un-normalized. In the latter case 
     *                      the retuned data is <i>N</i>/2 times larger (for <i>N</i> data points) than the data whose
     *                      forward FFT would result in the complex amplitudes of this instance. 
     * @return              a data object of the same size as the underlying pairwise data in this complex view
     *                      instance, containing the truncated or padded backward Fourier Transform of this instance.
     *                      
     *                      @see #getBackFFT(Index, boolean, Index, IndexedValues)
     * @see #getBackFFT(boolean)
     */
    public RegularData<IndexType, ?> getBackFFT(boolean integralNorm) {
        return getBackFFT(getSize(), integralNorm);
    }
    
    /**
     * Returns the backward Fast Fourier Transform (FFT) of the complex amplitudes represented by this instance,
     * as a real-valued data object.
     * 
     * @param dataSize      the requested size of the rfeal-valued data, which will be truncated or padded as necessary
     *                      to accommodate the FFT ass best as possible
     * @param integralNorm  if <code>false</code> the resulting FFT is normalzied as if the inverse of the forward FFT 
     *                      that would yield this instance. Otherwise, the FFT is un-normalized. In the latter case 
     *                      the retuned data is <i>N</i>/2 times larger (for <i>N</i> data points) than the data whose
     *                      forward FFT would result in the complex amplitudes of this instance. 
     * @return              a data object of the specified size, containing the truncated or padded backward
     *                      Fourier Transform of this instance.
     *                      
     *                      @see #getBackFFT(Index, boolean, Index, IndexedValues)
     * @see #getBackFFT(boolean)
     */
    public RegularData<IndexType, ?> getBackFFT(IndexType dataSize, boolean integralNorm) {
        return getBackFFT(dataSize, integralNorm, null, null);
    }

    /**
     * Returns the backward Fast Fourier Transform (FFT) of the complex amplitudes represented by this instance,
     * as a real-valued data object.
     * 
     * @param dataSize      the requested size of the rfeal-valued data, which will be truncated or padded as necessary
     *                      to accommodate the FFT ass best as possible
     * @param integralNorm  if <code>false</code> the resulting FFT is normalzied as if the inverse of the forward FFT 
     *                      that would yield this instance. Otherwise, the FFT is un-normalized. In the latter case 
     *                      the retuned data is <i>N</i>/2 times larger (for <i>N</i> data points) than the data whose
     *                      forward FFT would result in the complex amplitudes of this instance. 
     * @param refIndex      The index of the origin in the returned data, or <code>null</code> to place the origin
     *                      ar zero index(es).
     * @param weight        An optional weight image by which to divide the back-transform, point-by-point, such
     *                      as the same weight image that may have been used for the forward transform (if any),
     *                      or <code>null</code> to assume uniform weighting in configuration space.
     * @return              a data object of the specified size, containing the truncated or padded backward
     *                      Fourier Transform of this instance.
     *                      
     * @see #getBackFFT(Index, boolean)
     * @see #getBackFFT(boolean)
     */
    public RegularData<IndexType, ?> getBackFFT(IndexType dataSize, boolean integralNorm, final IndexType refIndex, final IndexedValues<IndexType, ?> weight) {   
        final IndexType size = pairs.getSize().copy();
        
        // Assume the Nyquist frequency is unrolled into a complex pair after 2^N...
        size.setComponent(dimension() - 1, size.getComponent(dimension() - 1) - 2);
        
        // The last dimension must be a multiple of 2 (complex pairs)
        if(size.getComponent(dimension() - 1) < 2) size.setComponent(dimension() - 1, 2);
        
        size.toPaddedFFTSize();

        final double renorm = integralNorm ? 1.0 : 2.0 / size.getVolume();
        
        IndexType unrolledSize = size.copy();
        unrolledSize.setComponent(dimension() - 1, unrolledSize.getComponent(dimension() - 1) + 2);
        
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

    @Override
    public void zero() {
        pairs.zero();
    }

    @Override
    public boolean isNull() {
        return pairs.isNull();
    }

    @Override
    public void add(ComplexView<IndexType> o) {
        pairs.add(o.pairs);
    }

    @Override
    public void subtract(ComplexView<IndexType> o) {
        pairs.subtract(o.pairs);
    }

    @Override
    public void setSum(ComplexView<IndexType> a, ComplexView<IndexType> b) {
        pairs.setSum(a.pairs, b.pairs);
    }

    @Override
    public void setDifference(ComplexView<IndexType> a, ComplexView<IndexType> b) {
        pairs.setDifference(a.pairs, b.pairs);
    }

    @Override
    public void addScaled(ComplexView<IndexType> o, double factor) {
        pairs.addScaled(o.pairs, factor);
    }
    
}

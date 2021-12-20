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

package jnum.data.index;

import java.io.Serializable;
import java.util.Arrays;

import jnum.Copiable;
import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.PointOp;
import jnum.math.Additive;
import jnum.math.Metric;
import jnum.math.Modulus;
import jnum.math.Multiplicative;
import jnum.math.Ratio;
import jnum.parallel.ParallelPointOp;
import jnum.parallel.ParallelTask;
import jnum.parallel.ParallelObject;
import jnum.util.HashCode;
import jnum.math.MathVector;

/**
 * A base class for multi-dimensional integer data/array indices.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   the generic type of the implementing object itself.
 */
public abstract class Index<T extends Index<T>> extends ParallelObject implements Serializable, Cloneable, Copiable<T>,
    Additive<T>, Multiplicative<T>, Ratio<T, T>, Modulus<T>, Metric<T> {
    
    private int[] value;
    
    /**
     * 
     */
    private static final long serialVersionUID = -2297049649014238073L;

    protected Index(int dim) {
        value = new int[dim];
    }

    @Override
    public int hashCode() {
        return HashCode.from(dimension()) ^ HashCode.from(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Index)) return false;
        
        Index<?> index = (Index<?>) o;
        if(index.dimension() != dimension()) return false;
        
        return Arrays.equals(index.value, value);
    }
   
    
    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        Index<T> copy = (Index<T>) super.clone();
        copy.value = Arrays.copyOf(value, value.length);
        return (T) copy;
    }
    
    @Override
    public T copy() {
        return clone();
    }

   
    @Override
    public String toString() {
       return toString(",");
    }
    
    /**
     * Returns the number of dimensions, or integer components, in this index. 
     * 
     * @return  the dimensionality of the index, that is the number of integer index components it contains.
     */
    public final int dimension() {
        return value.length;
    }
    
    /**
     * Returns the integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @return          the index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public final int getComponent(int dim) throws IndexOutOfBoundsException {
        return value[dim];
    }
    
    /**
     * Sets a new integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @param value     the new index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public final void setComponent(int dim, int value) throws IndexOutOfBoundsException {
        this.value[dim] = value; 
    }

    /**
     * Sets new values for this index, along all dimensions.
     * 
     * @param values        the array or list of index values along the dimensions
     * @throws NonConformingException
     *                      if the number of values specified does not match the dimensionality of this index instance 
     */
    public void set(int... values) throws NonConformingException {
        if(values.length != dimension()) throw new NonConformingException("Got " + values.length + " of " + dimension() + " components.");
        System.arraycopy(values, 0, this.value, 0, dimension());
    }
    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just above
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with padding added as necessary.
     * 
     * @see #toTruncatedFFTSize()
     */
    public void toPaddedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setComponent(i, ExtraMath.pow2ceil(getComponent(i)));
    }
    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just below
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with truncation as necessary.
     * 
     * @see #toPaddedFFTSize()
     */
    public void toTruncatedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setComponent(i, ExtraMath.pow2floor(getComponent(i)));
    }
    
    /**
     * Returns the product of the indices, that is the signed volume of the (multi-dimensional)
     * cube that is defined by the index and the origin.
     * 
     * @return      the volume under the index cube with one corner at the origin and the farthest
     *              other corner at the specified index location.
     */
    public int getVolume() {
        int vol = 1;
        for(int i=dimension(); --i >= 0; ) vol *= getComponent(i);
        return Math.abs(vol);
    }
    
    /**
     * Ensure that no component of this index exceeds the specified limit, by setting any components
     * larger to the limiting value. 
     * 
     * @param max       the limiting values for each component.
     * 
     * @see #ensure(Index)
     */
    public void limit(T max) {     
        for(int i=dimension(); --i >= 0; ) if(getComponent(i) > max.getComponent(i)) setComponent(i, max.getComponent(i));
    }
    
    /**
     * Ensure that no component of this index is below the specified lower bound, by setting any components
     * smaller to the bounding value. 
     * 
     * @param min       the lower bound for each component.
     * 
     * @see #limit(Index)
     */
    public void ensure(T min) {     
        for(int i=dimension(); --i >= 0; ) if(getComponent(i) < min.getComponent(i)) setComponent(i, min.getComponent(i));
    }

    
    /**
     * Wraps this index over the specified range. It is similar to
     * {@link #modulo(Index)}, except that in case where <code>modulo</code> would set a negative,
     * value, this method will set <code>size</code> + <code>modulo</code>. For example,
     * the index -1 with size 10, will be changed to 9.
     * 
     * @param size      the limiting range for each component. The corresponding component will
     *                  be strictly in the range of 0 to <code>abs(size)</code>.
     */
    public void wrap(T size) {
        for(int i=dimension(); --i >= 0; ) {
            int wrapped = getComponent(i) % size.getComponent(i);
            if(wrapped < 0) wrapped += size.getComponent(i);
            setComponent(i, wrapped);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void add(T o) {
        setSum((T) this, o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void subtract(T o) {
        setDifference((T) this, o);
    }

    @Override
    public void setSum(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, a.getComponent(i) + b.getComponent(i));
    }

    @Override
    public void setDifference(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, a.getComponent(i) - b.getComponent(i));
    }

    /**
     * Converts this index to a vector of the same dimensionality. 
     * 
     * @param v         the vector to set to the index components
     * @throws NonConformingException       if the vector has a different size (dimensionality) from this index.
     */
    public void toVector(MathVector<Double> v) throws NonConformingException {  
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension()); 
        for(int i=dimension(); --i >= 0; ) v.setComponent(i, (double) getComponent(i));
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public void multiplyBy(T factor) {
       setProduct((T) this, factor);
    }

    @Override
    public void modulo(T argument) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, getComponent(i) % argument.getComponent(i));
    }
    
    @Override
    public void setProduct(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, a.getComponent(i) * b.getComponent(i));
    }

    @Override
    public void setRatio(T numerator, T denominator) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, numerator.getComponent(i) / denominator.getComponent(i));
    }
    
    /**
     * Calculates component-by-component ratios of the two argument indices, as if each ratio
     * were calculated with real algebra (not integer math), and the result rounded as usual.
     * 
     * @param numerator     the index with the numerator components.
     * @param denominator   the index with the denominator components.
     * 
     * 
     * @see jnum.ExtraMath#roundedRatio(int, int)
     */
    public void setRoundedRatio(T numerator, T denominator) {
        for(int i=dimension(); --i >= 0; ) setComponent(i, ExtraMath.roundedRatio(numerator.getComponent(i), denominator.getComponent(i)));
    }

    
    /**
     * Sets the components of this index to the components of the argument index in reversed order.
     * 
     * @param other     the index whose componentas are used in reverse order
     */
    public void setReverseOrderOf(T other) {
        int last = dimension()-1;
        for(int i=last; i >= 0; i--) setComponent(i, other.getComponent(last-i));
    }
    
    @Override
    public double distanceTo(T index) {
        long sum = 0;
        
        for(int i=dimension(); --i >= 0; ) {
            int d = index.getComponent(i) - value[i];
            sum += d*d;
        }
        
        return Math.sqrt(sum);
    }
    
    /**
     * Sets all index components to the specified value.
     * 
     * @param value     the new index valie for all components.
     */
    public void fill(int value) {
        Arrays.fill(this.value, value);
    }
    
    /**
     * Increments the index along one of the dimensions. Can be useful for forward iterator
     * implementations.
     * 
     * @param dim   the dimension (counted from 0).
     * @return      the incremented index component in the selected dimension.
     */
    public int increment(int dim) {  
        return ++value[dim];
    }
    
    
    /**
     * Decrements the index along one of the dimensions. Can be useful for reverse iterator
     * implementations.
     * 
     * @param dim the component dimension.
     * @return the decremented index component in the selected dimension.
     */
    public int decrement(int dim) {
        return --value[dim];
    }
    
    /**
     * Zeroes all index components.
     * 
     * @see #fill(int)
     * 
     */
    public void zero() { fill(0); }

    /**
     * Returns the underlying integer array data that contains the indices along each dimension.
     * Midofications to the returned array will affect the state of this index instance and
     * vice versa.
     * 
     * @return      the backing array for this index.
     * 
     * @see #set(int...)
     */
    public final int[] getBackingArray() {
        return value;
    }
    
    /**
     * Returns a string representation of this index using the specified string as separator
     * between the components
     * 
     * @param separator     the string that separates component in the representation, e.g. ", ", or "][".
     * @return              the string representation of this index with the specified separator.
     */
    public String toString(String separator) {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<dimension(); i++) buf.append((i > 0 ? separator : "") + getComponent(i));
        return new String(buf);
    }
    
    /**
     * Loops over a block of indices atarting from this index to the specified end index, 
     * performing the specified point operation on each.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the point operation to perform on each data element
     * @param to            the ecxlusive ending index for the loop.
     * @return              the return value of the operation (if any).
     * 
     * @see #fork(ParallelPointOp, Index)
     */
    public abstract <ReturnType> ReturnType loop(PointOp<T, ReturnType> op, T to);
    
    
    /**
     * Processees a block of indexes (points), starting from this index and up to the specified ending
     * index, in this data object in parallel with the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp, Index)
     */
    public <ReturnType> ReturnType fork(final ParallelPointOp<T, ReturnType> op, T to) {
        Fork<ReturnType> f = new Fork<>(op, to);
        f.process();
        return f.getResult();
    }
    
    /**
     * Process a block of elements (points) efficiently, starting from this index and up to the specified 
     * ending index, using parallel or sequential processing, whichever is deemed fastest for the block size
     * and the specified point operation.
     * 
     * @param <ReturnType>  the generic return value type for the operation.
     * @param op            the (parallel) point operation to perform on each data element
     * @param to            the ecxlusive ending index for the block of data to process.
     * @return              the return value of the operation (if any).
     * 
     * @see #loop(PointOp, Index)
     * @see #fork(ParallelPointOp, Index)
     */
    public <ReturnType> ReturnType smartFork(final ParallelPointOp<T, ReturnType> op, T to) {
        if(getParallel() < 2) return loop(op, to);
        T span = copy();
        span.subtract(to);

        if(span.getVolume() * (2 + op.numberOfOperations()) < 2 * ParallelTask.minExecutorBlockSize) return loop(op, to);
        return fork(op, to);
    }
    
    
    public class Fork<ReturnType> extends Task<ReturnType> {  
        private T to;
        private ParallelPointOp<T, ReturnType> op;

        public Fork(ParallelPointOp<T, ReturnType> op, T to) { 
            this.to = to;
            this.op = op;
        }
        
        @Override
        protected void processChunk(int index, int threadCount) {
            T blockFrom = copy();
            T blockTo = to.copy();
            for(int i=getComponent(0) + index; i < to.getComponent(0); i += threadCount) {
                blockFrom.setComponent(0, i);
                blockTo.setComponent(0, i + 1);
                blockFrom.loop(op, blockTo);
            }
        }
        
        @Override
        protected int getRevisedChunks(int chunks, int minBlockSize) {
            return super.getRevisedChunks(getPointOps(), minBlockSize);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected int getTotalOps() {
            T block = to.copy();
            block.subtract((T) Index.this);
            return 3 + block.getVolume() * getPointOps();
        }

        protected int getPointOps() {
            return 10;
        }  
        
        @Override
        public ReturnType getResult() {
            return op.getResult();
        }

    } 

}

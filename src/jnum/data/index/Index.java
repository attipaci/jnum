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
    
    /**
     * 
     */
    private static final long serialVersionUID = -2297049649014238073L;


    @Override
    public int hashCode() {
        int hash = HashCode.from(dimension());
        for(int i=dimension(); --i >= 0; ) hash ^= HashCode.from(getValue(i));
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Index)) return false;
        
        Index<?> index = (Index<?>) o;
        if(index.dimension() != dimension()) return false;
        
        for(int i=dimension(); --i >= 0; ) if(index.getValue(i) != getValue(i)) return false;
        
        return true;        
    }
   
    
    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        return (T) super.clone();
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
    public abstract int dimension();
    
    /**
     * Returns the integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @return          the index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public abstract int getValue(int dim) throws IndexOutOfBoundsException;
    
    /**
     * Sets a new integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @param value     the new index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public abstract void setValue(int dim, int value) throws IndexOutOfBoundsException;


    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just above
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with padding added as necessary.
     * 
     * @see #toTruncatedFFTSize()
     */
    public void toPaddedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.pow2ceil(getValue(i)));
    }
    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just below
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with truncation as necessary.
     * 
     * @see #toPaddedFFTSize()
     */
    public void toTruncatedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.pow2floor(getValue(i)));
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
        for(int i=dimension(); --i >= 0; ) vol *= getValue(i);
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
        for(int i=dimension(); --i >= 0; ) if(getValue(i) > max.getValue(i)) setValue(i, max.getValue(i));
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
        for(int i=dimension(); --i >= 0; ) if(getValue(i) < min.getValue(i)) setValue(i, min.getValue(i));
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
            int wrapped = getValue(i) % size.getValue(i);
            if(wrapped < 0) wrapped += size.getValue(i);
            setValue(i, wrapped);
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
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) + b.getValue(i));
    }

    @Override
    public void setDifference(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) - b.getValue(i));
    }

    /**
     * Converts this index to a vector of the same dimensionality. 
     * 
     * @param v         the vector to set to the index components
     * @throws NonConformingException       if the vector has a different size (dimensionality) from this index.
     */
    public void toVector(MathVector<Double> v) throws NonConformingException {  
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension()); 
        for(int i=dimension(); --i >= 0; ) v.setComponent(i, (double) getValue(i));
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public void multiplyBy(T factor) {
       setProduct((T) this, factor);
    }

    @Override
    public void modulo(T argument) {
        for(int i=dimension(); --i >= 0; ) setValue(i, getValue(i) % argument.getValue(i));
    }
    
    @Override
    public void setProduct(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) * b.getValue(i));
    }

    @Override
    public void setRatio(T numerator, T denominator) {
        for(int i=dimension(); --i >= 0; ) setValue(i, numerator.getValue(i) / denominator.getValue(i));
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
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.roundedRatio(numerator.getValue(i), denominator.getValue(i)));
    }

    
    /**
     * Sets the components of this index to the components of the argument index in reversed order.
     * 
     * @param other     the index whose componentas are used in reverse order
     */
    public void setReverseOrderOf(T other) {
        int last = dimension()-1;
        for(int i=last; i >= 0; i--) setValue(i, other.getValue(last-i));
    }
    
    @Override
    public double distanceTo(T index) {
        long sum = 0;
        
        for(int i=dimension(); --i >= 0; ) {
            int d = index.getValue(i) - getValue(i);
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
        for(int i=dimension(); --i >= 0; ) setValue(i, value);
    }
    
    /**
     * Increments the index along one of the dimensions. Can be useful for forward iterator
     * implementations.
     * 
     * @param dim   the dimension (counted from 0).
     * @return      the incremented index component in the selected dimension.
     */
    public int increment(int dim) {
        int i = getValue(dim);
        setValue(dim, ++i);
        return i;
    }
    
    

    /**
     * Decrements the index along one of the dimensions. Can be useful for reverse iterator
     * implementations.
     * 
     * @param dim the component dimension.
     * @return the decremented index component in the selected dimension.
     */
    public int decrement(int dim) {
        int i = getValue(dim);
        setValue(dim, --i);
        return i;
    }
    
    /**
     * Zeroes all index components.
     * 
     * @see #fill(int)
     * 
     */
    public void zero() { fill(0); }

    
    
    /**
     * Returns a string representation of this index using the specified string as separator
     * between the components
     * 
     * @param separator     the string that separates component in the representation, e.g. ", ", or "][".
     * @return              the string representation of this index with the specified separator.
     */
    public String toString(String separator) {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<dimension(); i++) buf.append((i > 0 ? separator : "") + getValue(i));
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
            for(int i=getValue(0) + index; i < to.getValue(0); i += threadCount) {
                blockFrom.setValue(0, i);
                blockTo.setValue(0, i + 1);
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

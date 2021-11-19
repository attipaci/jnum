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
import jnum.math.Additive;
import jnum.math.Metric;
import jnum.math.Modulus;
import jnum.math.Multiplicative;
import jnum.math.Ratio;
import jnum.math.MathVector;

/**
 * Interface for integer indices in any dimensional space/grid.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   the generic type of the implementing object itself.
 */
public interface Index<T extends Index<T>> extends Serializable, Cloneable, Copiable<T> ,
    Additive<T>, Multiplicative<T>, Ratio<T, T>, Modulus<T>, Metric<T> {
    
    /**
     * Returns the number of dimensions, or integer components, in this index. 
     * 
     * @return  the dimensionality of the index, that is the number of integer index components it contains.
     */
    public int dimension();
    
    /**
     * Returns the integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @return          the index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public int getValue(int dim) throws IndexOutOfBoundsException;
    
    /**
     * Sets a new integer index value in the specified dimension.
     * 
     * @param dim       the dimension (counted from 0).
     * @param value     the new index along the specified dimension.
     * @throws IndexOutOfBoundsException    if the dimension specidied is outside the range supported by this index instance.
     * 
     * @see #dimension()
     */
    public void setValue(int dim, int value) throws IndexOutOfBoundsException;


    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just above
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with padding added as necessary.
     * 
     * @see #toTruncatedFFTSize()
     */
    public default void toPaddedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.pow2ceil(getValue(i)));
    }
    
    /**
     * Changes an index (size) s.t. all components are an exact power of 2, equal or just below
     * the original sizes contained. This is useful for calculating FFT sizes for arbitrary sized
     * data with truncation as necessary.
     * 
     * @see #toPaddedFFTSize()
     */
    public default void toTruncatedFFTSize() {
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.pow2floor(getValue(i)));
    }
    
    /**
     * Returns the product of the indices, that is the signed volume of the (multi-dimensional)
     * cube that is defined by the index and the origin.
     * 
     * @return      the volume under the index cube with one corner at the origin and the farthest
     *              other corner at the specified index location.
     */
    public default int getVolume() {
        int vol = 1;
        for(int i=dimension(); --i >= 0; ) vol *= getValue(i);
        return vol;
    }
    
    
    public default void limit(T max) {     
        for(int i=dimension(); --i >= 0; ) if(getValue(i) > max.getValue(i)) setValue(i, max.getValue(i));
    }
    
    public default void wrap(T size) {
        for(int i=dimension(); --i >= 0; ) {
            setValue(i, getValue(i) % getValue(i));
            if(getValue(i) < 0) setValue(i, getValue(i) + size.getValue(i));
        }
    }
    
    @Override
    public default void add(T o) {
        for(int i=dimension(); --i >= 0; ) setValue(i, getValue(i) + o.getValue(i));
    }

    @Override
    public default void subtract(T o) {
        for(int i=dimension(); --i >= 0; ) setValue(i, getValue(i) - o.getValue(i));
    }

    @Override
    public default void setSum(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) + b.getValue(i));
    }

    @Override
    public default void setDifference(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) - b.getValue(i));
    }

    /**
     * Converts this index to a vector of the same dimensionality. 
     * 
     * @param v         the vector to set to the index components
     * @throws NonConformingException       if the vector has a different size (dimensionality) from this index.
     */
    public default void toVector(MathVector<Double> v) throws NonConformingException {  
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension()); 
        for(int i=dimension(); --i >= 0; ) v.setComponent(i, (double) getValue(i));
    }
    

    @Override
    public default void multiplyBy(T factor) {
        for(int i=dimension(); --i >= 0; ) setValue(i, getValue(i) * factor.getValue(i));
    }

    @Override
    public default void modulo(T argument) {
        for(int i=dimension(); --i >= 0; ) setValue(i, getValue(i) % argument.getValue(i));
    }
    
    @Override
    public default void setProduct(T a, T b) {
        for(int i=dimension(); --i >= 0; ) setValue(i, a.getValue(i) * b.getValue(i));
    }

    @Override
    public default void setRatio(T numerator, T denominator) {
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
    public default void setRoundedRatio(T numerator, T denominator) {
        for(int i=dimension(); --i >= 0; ) setValue(i, ExtraMath.roundedRatio(numerator.getValue(i), denominator.getValue(i)));
    }

    
    /**
     * Sets the components of this index to the components of the argument index in reversed order.
     * 
     * @param other     the index whose componentas are used in reverse order
     */
    public default void setReverseOrderOf(T other) {
        int last = dimension()-1;
        for(int i=last; i >= 0; i--) setValue(i, other.getValue(last-i));
    }
    
    @Override
    public default double distanceTo(T index) {
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
    public default void fill(int value) {
        for(int i=dimension(); --i >= 0; ) setValue(i, value);
    }
    
    /**
     * Increments the index along one of the dimensions. Can be useful for forward iterator
     * implementations.
     * 
     * @param dim   the dimension (counted from 0).
     * @return      the incremented index component in the selected dimension.
     */
    public default int increment(int dim) {
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
    public default int decrement(int dim) {
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
    public default void zero() { fill(0); }

    
    
    /**
     * Returns a string representation of this index using the specified string as separator
     * between the components
     * 
     * @param separator     the string that separates component in the representation, e.g. ", ", or "][".
     * @return              the string representation of this index with the specified separator.
     */
    public default String toString(String separator) {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<dimension(); i++) buf.append((i > 0 ? separator : "") + getValue(i));
        return new String(buf);
    }
}

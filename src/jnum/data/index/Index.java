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
 * @param <T>   the generic type of the implemting object itself.
 */
public interface Index<T> extends Serializable, Cloneable, Copiable<T> ,
    Additive<T>, Multiplicative<T>, Ratio<T, T>, Modulus<T>, Metric<T> {
    
    /**
     * Returns the product of the indices, that is the signed volume of the (multi-dimensional)
     * cube that is defined by the index and the origin.
     * 
     * @return
     */
    public int getVolume();
    
    /**
     * Returns the number of dimensions, or integer components, in this index. 
     * 
     * @return
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
     * Sets all index components to the specified value.
     * 
     * @param value     the new index valie for all components.
     */
    public void fill(int value);
    
    /**
     * Zeroes all index components.
     * 
     * @see #fill(int)
     * 
     */
    public void zero(); // TODO default method: fill(0);
    
    /**
     * Increments the index along one of the dimensions. Can be useful for forward iterator
     * implementations.
     * 
     * @param dim   the dimension (counted from 0).
     * @return      the incremented index component in the selected dimension.
     */
    public int increment(int dim);
    
    /**
     * Decrements the index along one of the dimensions. Can be useful for reverse iterator
     * implementations.
     * 
     * @param dim the component dimension.
     * @return the decremented index component in the selected dimension.
     */
    public int decrement(int dim);
    
    /**
     * Sets the components of this index to the components of the argument index in reversed order.
     * 
     * @param other     the index whose componentas are used in reverse order
     */
    public void setReverseOrderOf(T other);
    
    /**
     * Converts this index to a vector of the same dimensionality. 
     * 
     * @param v         the vector to set to the index components
     * @throws NonConformingException       if the vector has a different size (dimensionality) from this index.
     */
    public void toVector(MathVector<Double> v) throws NonConformingException; 
    
    /**
     * Returns a string representation of this index using the specified string as separator
     * between the components
     * 
     * @param separator     the string that separates component in the representation, e.g. ", ", or "][".
     * @return              the string representation of this index with the specified separator.
     */
    public String toString(String separator);
    
}

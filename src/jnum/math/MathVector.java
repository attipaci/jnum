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

package jnum.math;

import java.util.stream.Collector;

import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;

/**
 * Mathematical vector interface.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   The generic type of the vector object itself.
 */
public interface MathVector<T> extends Coordinates<T>, AbsoluteValue, Normalizable, Inversion, Metric<MathVector<? extends T>>, LinearAlgebra<MathVector<? extends T>> {
     
    /**
     * Multiplies each component of this vector by the components of the argument as if
     * the argument were a diagonal matrix. 
     * 
     * @param v     The componet-by-component multiplying factors, like akin to multiplying with diagonal matrix
     *              constituted of those elements.
     *              
     * @throws NonConformingException if the argument is a vector of different size from this one.
     */
    public void multiplyByComponentsOf(Coordinates<? extends T> v) throws NonConformingException;
    
    /**
     * Adds an increment to a specific component of this vector.
     * 
     * @param idx           the index of the vector component
     * @param increment     the generic type value to add to the component.
     */
    public void incrementValue(int idx, T increment);
    
    /**
     * Gets the dot product of this vector with the argument. The two vectors might be of different
     * size. If so, it's treated as if the shorter vector had zeroes in the extra dimensions of
     * the larger vector.
     * 
     * @param v         the vector to dot this one with      
     * @return          the dot product of this vector with the argument.
     */
    public T dot(MathVector<? extends T> v);
    
    /**
     * Gets the dot product of this vector with the argument. The two vectors might be of different
     * size. If so, it's treated as if the shorter vector had zeroes in the extra dimensions of
     * the larger vector.
     * 
     * @param v         the vector to dot this one with      
     * @return          the dot product of this vector with the argument.
     */
    public T dot(T[] v);
    
    /**
     * Gets the dot product of this vector with the argument. The two vectors might be of different
     * size. If so, it's treated as if the shorter vector had zeroes in the extra dimensions of
     * the larger vector.
     * 
     * @param v         the vector to dot this one with      
     * @return          the dot product of this vector with the argument.
     */
    public T dot(double... v);
    
    /**
     * Gets the dot product of this vector with the argument. The two vectors might be of different
     * size. If so, it's treated as if the shorter vector had zeroes in the extra dimensions of
     * the larger vector.
     * 
     * @param v         the vector to dot this one with      
     * @return          the dot product of this vector with the argument.
     */
    public T dot(float... v);
    
    /**
     * Othogonalizes this vector to the argument, by subtracting its projection onto the argument
     * vector. 
     * 
     * @param v         another vector of the same size and compatible type to this.
     * 
     * @see #projectOn(MathVector)
     * @see #reflectOn(MathVector)
     */
    public void orthogonalizeTo(MathVector<? extends T> v);
    
    /**
     * Projects this vector on the argument, by discarding the part orthogonal to the the
     * argument. As a result this vector will point in the same direction as the argument
     * after the call.
     * 
     * @param v         another vector of the same size and compatible type to this.
     * 
     * @see #orthogonalizeTo(MathVector)
     * @see #reflectOn(MathVector)
     */
    public void projectOn(final MathVector<? extends T> v);
    
    /**
     * Reflects this vector on the argument, by flipping (inverting) the part orthogonal
     * to the argument. 
     * 
     * @param v         another vector of the same size and compatible type to this.
     */
    public void reflectOn(final MathVector<? extends T> v);
    
    /**
     * Gets a matrix representation of this vector as a row vector of the same element type.
     * 
     * @return  a new matrix that represents this vector as a row vector.
     */
    public AbstractMatrix<T> asRowVector();
    
    /**
     * Fills the vector with independent copies of the argument.
     * 
     * @param value     the value that all components of this vector will have a copy of.
     */
    public void fill(T value);
    
    /**
     * Sets the components of this vector to the specified list of arguments (or array argument).
     * 
     * @param values    The list of values to set.
     */
    public void setValues(@SuppressWarnings("unchecked") T ... values);  
    
    /**
     * The summing collector for classes that implement this interface, for use
     * with streams.
     * 
     * @param <T>   the generic type of a vector component.
     * @param <V>   the generic type of vector that can be accumulated
     * @param cl    the class of the generic type
     * @return      the collector that will give back the sum of vector elements from the stream
     *              on which it collects.
     *              
     */
    public static <T, V extends MathVector<T>> Collector<V, V, V> sum(Class<V> cl) {
        return Collector.of(
                () -> {
                    try { return cl.getDeclaredConstructor().newInstance(); }
                    catch(Exception e) { return null; }
                },
                (partial, point) -> partial.add(point),
                (sum, partial) -> { sum.add(partial); return sum; },
                Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH
                );
    }

}

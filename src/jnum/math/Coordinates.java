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

import java.text.NumberFormat;
import java.util.stream.IntStream;

import jnum.CopyCat;
import jnum.Util;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedEntries;
import jnum.data.index.IndexedValues;
import jnum.math.matrix.ShapeException;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;

/**
 * An interface representing coordinates of generic type.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   The generic type of a coordinate in this set
 */
public interface Coordinates<T> extends CopyCat<Coordinates<? extends T>>, IndexedEntries<Index1D, T>, NumberFormating, DecimalFormating  {

    /**
     * Checks if two coordinate instances are equal to one-another within some relative
     * precision. The two coordinates are only equals if they both have the same dimension
     * and all their components match within the specified precision.
     * 
     * @param coords        the coordinate instance to check equality to
     * @param precision     the relative precision, e.g. 1e-6 for 1 part per million.
     * @return              <code>true</code> if the two coordinates are the same within the
     *                      specified relative precision, or <code>false</code> if they differ.
     */
    public boolean equals(Coordinates<T> coords, double precision);
    
    /**
     * Gets the number of coordinate components in this set.
     * 
     * @return  The number of elements constituting this set of coordinates.
     */
    public int size();
    
    /**
     * Gets the class of components in these coordinates. 
     * 
     * @return      the class of components contained in these set of coordinates.
     */
    public Class<T> getComponentType();
    
    /**
     * Gets one of the coordinate element.
     * 
     * @param index     Index of the coordinate element
     * @return          The coordinate element at the given index, or 0.0 if the index is beyonf the 
     *                  span of coordinate elements supported by this class.
     */
    public T getComponent(int index);
    
    /**
     * Sets one coordinate to the specified generic type value.
     * 
     * @param index     Index of the coordinate element
     * @param value     The new value for the coordinate element at the given index.
     */
    public void setComponent(int index, T value);
    
    /**
     * Gets the 'x' coordinate (index 0) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'x' coordinate.
     */
    public T x();
    
    /**
     * Gets the 'y' coordinate (index 1) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'y' coordinate, or 0.0 if this set of coodinates does not have a 'y' type element.
     */
    public T y();
    
    /**
     * Gets the 'z' coordinate (index 2) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'z' coordinate, or 0.0 if this set of coodinates does not have a 'z' type element.
     */
    public T z();

    
    @Override
    public default int capacity() {
        return size();
    }

    @Override
    public default int dimension() {
        return 1;
    }


    @Override
    public default Index1D getSize() {
        return new Index1D(size());
    }


    @Override
    public default int getSize(int i) {
        if(i != 0) throw new IllegalArgumentException("there is no dimension " + i);
        return size();
    }

    @Override
    public default T get(Index1D index) {
        return getComponent(index.i());
    }

    @Override
    public default void set(Index1D index, T value) {
        setComponent(index.i(), value);
    }


    @Override
    public default Index1D getIndexInstance() {
        return new Index1D();
    }


    @Override
    public default Index1D copyOfIndex(Index1D index) {
        return index.copy();
    }


    @Override
    public default boolean conformsTo(Index1D size) {
        return size.i() == size();
    }


    @Override
    public default boolean conformsTo(IndexedValues<Index1D, ?> data) {
        return data.getSize().i() == size();
    }

    


    @Override
    public default boolean containsIndex(Index1D index) {
        int i = index.getValue(0);
        if(i < 0) return false;
        if(i >= size()) return false;
        return true;
    }
    
    
    /**
     * Gets an independent copy of a component in this vector.
     * 
     * @param i        The index of the component
     * @return         A deep copy of the value at the specified location.
     */
    public T copyOf(int i);
 
    /**
     * Gets an independent copy of a component in this vector.
     * 
     * @param idx      The index of the component
     * @return         A deep copy of the value at the specified location.
     */
    public default T copyOf(Index1D idx) {
        return copyOf(idx.i());
    }

    @Override
    public default String toString(int decimals) {
        return toString(Util.s[decimals+1]);
    }

    @Override
    public default String toString(NumberFormat nf) {
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        IntStream.range(0, size()).forEach(i -> buf.append(toString(i, nf)));
        buf.append(')');
        return buf.toString();
    }
    
    /**
     * Same as {@link #toString(int, NumberFormat)} but with an {@link Index1D} specifying the
     * component index.
     * 
     * @param idx   Index of component
     * @param nf    Number formating specification. It can be null to simply call {@link Object#toString()} on the component.
     * @return      The string representation of the vector component.
     */
    public default String toString(Index1D idx, NumberFormat nf) {
        return toString(idx.i(), nf);
    }
   

    /**
     * Attempts to convert a component in this vector to a string of the specified number format.
     * If the component is not a {@link Number} type, or if it does not support 
     * {@link jnum.text.DecimalFormating} then then this method it will simply return the
     * component calling its default {@link Object#toString()} implementation.
     * 
     * @param i     Index of component
     * @param nf    Number formating specification. It can be null to simply call {@link Object#toString()} on the component.
     * @return      The string representation of the vector component.
     */
    public default String toString(int i, NumberFormat nf) {
        return nf.format(getComponent(i));
    }


    
    /**
     * Checks if the vector has the expected size for some operation. If not a {@link ShapeException} is thrown.
     * 
     * @param size
     * @throws ShapeException
     */
    public default void assertSize(int size) { 
        if(size() != size) throw new ShapeException(getClass().getSimpleName() + " has wrong size " + size() + ". Expected " + size + ".");    
    }
   
}

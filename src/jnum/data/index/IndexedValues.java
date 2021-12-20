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

import jnum.data.Data;
import jnum.data.Values;

/**
 * Provides index-based access to numerical values.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>
 * @param <NumberType>
 */
public interface IndexedValues<IndexType extends Index<IndexType>, NumberType extends Number> extends Values, IndexedEntries<IndexType, NumberType> {

    /**
     * Clears (zeroes) the value at the specified index location
     * 
     * @param index     the location index.
     */
    default void clear(IndexType index) {
        set(index, 0);
    }
    /**
     * Scales the value at the specified index location with the supplied scaling factor.
     * 
     * @param index     the location index.
     * @param factor    the scaling factor.
     */
    public default void scale(IndexType index, double factor) {
        set(index, factor * get(index).doubleValue());
    }
    
    /**
     * Sets a new value at the specified index location
     * 
     * @param index     the location index.
     * @param value     the new value.
     */
    void set(IndexType index, Number value);

    /**
     * Adds a number to the value at the specified index location.
     * 
     * @param index     the location index.
     * @param value     the increment.
     */
    public void add(IndexType index, Number value);
    
    /**
     * Returns a new empty (zeroed) data object of the same type and size as this object.
     * 
     * @return              a new data object of the same element type and size as this one.
     * 
     * @see #newImage(Index, Class)
     */
    default Data<IndexType> newImage() { return newImage(getSize(), getElementType()); }

    /**
     * Returns a new empty (zeroed) data object of the same class as this one but with
     * the specified size and element type.
     * 
     * @param size          the size of the new data.
     * @param elementType   the type of elements in the new data, such as <code>Float.class</code>.
     * @return              a new data object with the specified size and element type.
     * 
     * @see #newImage()
     */
    public Data<IndexType> newImage(IndexType size, Class<? extends Number> elementType);

}

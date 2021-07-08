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

/**
 * Access to data entries using an index.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>
 * @param <DataType>
 */
public interface IndexedEntries<IndexType, DataType> {
    
    /**
     * Gets the number of data elements being held in this object (populated or not).
     * 
     * @return      the total number of data elements in this objects.
     */
    public int capacity();
    
    /**
     * Gets the dimensionality of this data, i.e. the number of independent 
     * components in its data indices. For example a image plane with i and j
     * data indices would have a dimension of 2.
     * 
     * @return  the dimension of this data.
     */
    public int dimension();
    
    /**
     * Gets the size of this data, returning the number of elements contained
     * along each dimension. The retuned value should be treated as read-only, such that
     * implementation may return a static object. If you do need to operate on the
     * returned index, you should make a copy using either {@link #copyOfIndex(Object)},
     * or the {@link jnum.Copiable#copy()} implementation of the index itself provided
     * it has support for it.
     * 
     * @return  the number of data elements along each dimension.
     * 
     * @see #copyOfIndex(Object)
     * @see jnum.Copiable#copy()
     */
    public IndexType getSize();
    
    /**
     * Gets the data stored at the specified index.
     * 
     * @param index     the index of the data
     * @return          the data element at that index. It is a reference to an object, or a
     *                  primitive value.
     * @throws ArrayIndexOutOfBoundsException   if the index it outside the range of this data.
     */
    public DataType get(IndexType index) throws ArrayIndexOutOfBoundsException;
    
    /**
     * Sets a new data element at the specified data index.
     * 
     * @param index     the index of the data
     * @param value     the new elements to set. The data object will reference that value
     *                  or set a primitive type equal to the supplieed value.
     * @throws ArrayIndexOutOfBoundsException   if the index it outside the range of this data.
     */
    public void set(IndexType index, DataType value) throws ArrayIndexOutOfBoundsException;
    
    /**
     * Gets a new generic type index instance for this data object.
     * 
     * @return      A new index object for this data.
     */
    public IndexType getIndexInstance();
    
    /**
     * Gets a deep copy of the specified data index.
     * 
     * @param index The index to copy.
     * @return      A deep copy of the argument, as the same type of index object
     */
    public abstract IndexType copyOfIndex(IndexType index);
    
    /**
     * Checks if this data has a matching size, along all dimensions, as the size
     * specified.
     * 
     * @param size      the expected size along each dimention
     * @return          <code>true</code> if the object has the expected size. Otherwise <code>false</code>.
     */
    public abstract boolean conformsTo(IndexType size);
         
    /**
     * Checks if this data object has a matching size, in all dimensions, as another data object
     * with the same generic type of index.
     * 
     * @param data      The other indexed data object.
     * @return          <code>true</code> if this object has the same size as the argument. Otherwise <code>false</code>.
     */
    public boolean conformsTo(IndexedValues<IndexType, ?> data);
    
    /**
     * Gets a string representation of the object's size in all dimensions, usually in square brackets.
     * 
     * @return  A string representation of this object's size.
     */
    public abstract String getSizeString();
    
    /**
     * Checks if the supplied index is contained within the bounds of this object.
     * 
     * @param index The index of an element we may try to access.
     * @return      <code>true</code> if the index is within the supported bounds of this object. Otherwise <code>false</code>.
     */
    public abstract boolean containsIndex(IndexType index);
    
}

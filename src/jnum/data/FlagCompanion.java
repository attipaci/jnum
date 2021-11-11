/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.Util;
import jnum.data.index.Index;
import jnum.data.index.IndexedValues;
import jnum.parallel.ParallelObject;

/** 
 * A set of bitwise flags that may accompany every point in some data object.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   the generic type of index used to access elements in the data and the flags that accompany them.
 */
public abstract class FlagCompanion<IndexType extends Index<IndexType>> extends ParallelObject implements Cloneable, Serializable,
CopiableContent<FlagCompanion<? extends IndexType>> {
    /**
     * 
     */
    private static final long serialVersionUID = -3515015800957215451L;
    
    private Type type;
    
    /**
     * Instantiates a new bitwise data flags that accompany points in a data objects.
     * 
     * @param type  the type of bitwise flags that accompany data points.
     */
    protected FlagCompanion(Type type) {
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ type.hashCode() ^ getData().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FlagCompanion)) return false;
         
        FlagCompanion<?> f = (FlagCompanion<?>) o;
        if(type != f.type) return false;
        if(!Util.equals(getData(), f.getData())) return false;
        
        return true;
    }
    
    /**
     * Returns the type the flag element associated to each data point.
     * 
     * @return      the type of bitwise flags that accompany data points.
     */
    public final Type type() {
        return type;
    }
    
    /**
     * Returns the number of flag elements, which is the same as the number of data
     * points that the flags accompany.
     * 
     * @return      the number of data points.
     * 
     * @see Data#getSize()
     */
    public final IndexType size() { return getData().getSize(); }
    
    /**
     * Returns the maximum number of flag elements, that is the same as the maximum
     * number of data points that the data that is accompanied by these flags may contain.
     * 
     * @return      the maximum number of flag / data elements possible.
     * 
     * @see Data#capacity()
     */
    public final int capacity() { return getData().capacity(); }

    /**
     * Checks if the flags match the specified size and shape. 
     * 
     * @param size      the size we expect or need.
     * @return          <code>true</code> if this flagging companion is of the same size and shape, otherwise <code>false</code>.
     * 
     * @see #size()
     */
    public final boolean conformsTo(IndexType size) {
        return getData().conformsTo(size);
    }
    
    /**
     * Checks if the flags match the size and shape of the specified data. If so, this flagging companion may be used with that
     * data. 
     * 
     * @param data      the data for which we want to use these flags for.
     * @return          <code>true</code> if this flagging companion is of the same size and shape as the data, otherwise <code>false</code>.
     *
     * @see #conformsTo(Index)
     * @see #size()
     */
    public final boolean conformsTo(IndexedValues<IndexType, ?> data) {
        return conformsTo(data.getSize());
    }
    
    /**
     * Checks if the flags match the size and shape of another instance. If so, the two flaggig companions may be used with
     * the same data interchangeably.
     * 
     * @param flags     the other flagging companion.
     * @return          <code>true</code> if this flagging companion is of the same size and shape as the other one, otherwise <code>false</code>.
     *
     * @see #conformsTo(Index)
     * @see #size()
     */
    public final boolean conformsTo(FlagCompanion<IndexType> flags) {
        return conformsTo(flags.getData());
    }
  
    /**
     * Clears all bitwise flags for all data points, such that note of the data has any
     * associated flag bits set to 1.
     *
     * @see #fill(long)
     */
    public void clear() {
        fill(0L);
    }
    
    /**
     * Sets the flags for all data points to the specified value.
     * 
     * @param pattern       the bitwise flag pattern to set for all data points.
     */
    public void fill(long pattern) {
        getData().fill(pattern);
    }

    /**
     * Returns the data object that these bitwise flags are being used for.
     * 
     * @return      the associated data object.
     */
    public abstract Data<IndexType> getData();
    
    /**
     * Returns the class of numbers that the associated data object contains. It is shorthand
     * for <code>getData().getElementType()</code>
     * 
     * @return      the class of number elements in the associated data object
     * 
     * @see Data#getElementType()
     */
    public final Class<? extends Number> getElementType() { return getData().getElementType(); }
    
    /**
     * Returns the bitwise flag value at a given data location.
     * 
     * @param index     the location of the data point
     * @return          the bitwise flag value at the specified location.
     * 
     * @see #set(Index, long)
     * @see #setBits(Index, long)
     * @see #clearBits(Index, long)
     * @see #clear(Index)
     * @see #isClear(Index)
     */
    public final long get(IndexType index) { return getData().get(index).longValue(); }
    
    /**
     * Sets a new bitwise flag value for a given data location.
     * 
     * @param index     the location of the data point
     * @param value     the new bitwise flag value at the specified location.
     * 
     * @see #setBits(Index, long)
     * @see #clearBits(Index, long)
     * @see #get(Index)
     * @see #clear(Index)
     */
    public final void set(IndexType index, long value) { getData().set(index, value); }
    
    /**
     * Sets the selected flag bits for a given data location, leaving the flag values in
     * other bits unchanged.
     * 
     * @param index     the location of the data point
     * @param pattern   the flag bits that will be set to 1. 
     * 
     * @see #clearBits(Index, long)
     * @see #set(Index, long)
     * @see #get(Index)
     * @see #clear(Index)
     */
    public final void setBits(IndexType index, long pattern) { 
        getData().set(index, getData().get(index).longValue() | pattern); 
    }
    
    /**
     * Clears the selected flag bits for a given data location, leaving the flag values in
     * other bits unchanged.
     * 
     * @param index     the location of the data point
     * @param pattern   the flag bits that will be set to 0. 
     * 
     * @see #setBits(Index, long)
     * @see #isClear(Index, long)
     * @see #set(Index, long)
     * @see #get(Index)
     * @see #clear(Index)
     */
    public final void clearBits(IndexType index, long pattern) { getData().set(index, 0L); }
    
    /**
     * Checks if all of the specified flag bits are cleared (0) at the specified data location.
     * 
     * @param index     the location of the data point
     * @param pattern   the flag bits to check
     * @return          <code>true</code> if the data point has all the specified flag bits set to 0, 
     *                  otherwise <code>false</code>.
     *                 
     * @see #isClear(Index)
     * @see #set(Index, long)
     * @see #setBits(Index, long)
     * @see #clear(Index)
     * @se  #clearBits(Index, long)
     */
    public final boolean isClear(IndexType index, long pattern) {
        return (getData().get(index).longValue() & pattern) == 0L;
    }
    
    /**
     * Reset all flag bit at the specified data location to zero (clear).
     * 
     * @param index     the location of the data point
     * 
     * @see #clearBits(Index, long)
     * @see #isClear(Index)
     */
    public final void clear(IndexType index) { getData().set(index, 0L); }
    
    /**
     * Checks if all flags are in a cleared state (0) at a given data location.
     * 
     * @param index     the location of the data point
     * @return          <code>true</code> if no flag is set at the specified location, otherwise <code>false</code>.
     * 
     * @see #clear(Index)
     * @see #clearBits(Index, long)
     * @see #set(Index, long)
     * @see #setBits(Index, long)
     */
    public final boolean isClear(IndexType index) { return getData().get(index).longValue() == 0L; }
    
    /**
     * An enumeration of types of bitwise flags we provide. Any built-in integer type may be used for
     * providing bitwise flags, and so accordingly we have 4 flag types, corresponding to the
     * 4 primitive integer types in Java.
     * 
     * @author Attila Kovacs
     *
     */
    public static enum Type {
        /** 8-bit flags */
        BYTE(),
        
        /** 16-bit flags */
        SHORT(),
        
        /** 32-bit flags */
        INT(),
        
        /** 64-bit flags */
        LONG();
        
        /**
         * Returns the flag type for a given class of integer.
         * 
         * @param cl        the class of integer, such as <code>Integer.class</code>.
         * @return          the corresponding flagging type
         * @throws IllegalArgumentException
         *                  if the argument is not an integer type.
         *                  
         * @see #forPrimitiveType(Class)
         */
        public static Type forNumberType(Class<? extends Number> cl) throws IllegalArgumentException {
            if (Byte.class.isAssignableFrom(cl)) return BYTE;
            if (Short.class.isAssignableFrom(cl)) return SHORT;
            if (Integer.class.isAssignableFrom(cl)) return INT;
            if (Long.class.isAssignableFrom(cl)) return LONG;
            throw new IllegalArgumentException("No bitwise flagging for type: " + cl.getName());
        }
        
        /**
         * Returns the flag type for a given primitive integer class.
         * 
         * @param cl        the primitive class of integer, such as <code>int.class</code>
         * @return          the corresponding flagging type
         * @throws IllegalArgumentException
         *                  if the argument is not a primitive integer type.
         *                  
         * @see #forPrimitiveType(Class)
         */
        public static Type forPrimitiveType(Class<?> cl) throws IllegalArgumentException {
            if (cl == byte.class) return BYTE;
            if (cl == short.class) return SHORT;
            if (cl == int.class) return INT;
            if (cl == long.class) return LONG;
            throw new IllegalArgumentException("No bitwise flagging for type: " + cl.getName());
        }
    }
    
}

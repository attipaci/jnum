/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.util;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class representing a bitwise flag in a flag-space of an integer-type container. The flag has a unique letter
 * code identifying it in the flag-space it belongs to, and has a human-readable descriptive name. These allow
 * for human tracking, if desired, of which flags have been set.
 */
public class Flag implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6666953592077360485L;
    
    /** The space. */
    private FlagSpace space;
    
    private int value;
    
    /** The letter code. */
    private char letterCode;
    
    /** The name. */
    private String name;
    
    /**
     * Instantiates a new bitwise flag, and adds it to the specified flag space. The flag will be identified with a
     * unique (in the flag space) letter code, and will have the specified human-readable descriptive name.
     *
     * @param space the flag-space (container) in which the flag lives.
     * @param value the integer value of the flag. It should have exactly one non-zero bit, i.e. equal to 2^p.
     * @param letterCode a unique letter code in the flag-space identifyng this flag
     * @param name a descriptive human-readable name for the flag.
     * @throws IllegalArgumentException if the flag value is not a single bit in the flag space...
     * @throws FlagConflictException the flag conflict exception
     */
    protected Flag(FlagSpace space, int value, char letterCode, String name) throws IllegalArgumentException, FlagConflictException {
        this.space = space;
        this.value = checkValue(value, space);
        this.letterCode = letterCode;
        this.name = name;
        
        space.put(this);
    }
    
    /**
     * Checks the flag value to ensure it sets only one bit, and in the specified flag-space...
     *
     * @param value the intended flag value
     * @param value the flag-space in which the value will exist.
     * @throws IllegalArgumentException if the number of set bits is not exactly one, or not inside the flag-space. 
     */
    private int checkValue(int value, FlagSpace space) throws IllegalArgumentException {
        if(value == 0L) throw new IllegalArgumentException("null flag value.");
        
        int bits = 0;
        
        for(int i=32; --i >= 0; ) {
            if((value & (1L<<i)) != 0) bits++;
            if(bits > 1) throw new IllegalArgumentException("flag value specifies more than one bit: 0x" + Integer.toHexString(value));
        }     
        
        if((value & space.getMask()) == 0) 
            throw new IllegalArgumentException("flag value outside of flag-space: 0x" + Integer.toHexString(value) + "in " + space.getName() + ": 0x" + Integer.toHexString(space.getMask()));
        return value;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flag)) return false;
        if(!super.equals(o)) return false;
        
        Flag f = (Flag) o;
        if(f.value != value) return false;
        if(f.letterCode != letterCode) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(value) ^ HashCode.from(letterCode); }
    
    /**
     * Gets the flag space to which this flag belongs to.
     *
     * @return the flag space
     */
    public final FlagSpace getFlagSpace() { return space; }
    
    /**
     * Gets the integer value associated with this flag, which has exactly one bit set.
     *
     * @return the flag value
     */
    public final int value() { return value; }
   
    /**
     * Gets the unique letter code that identifies this flag in the flag-space it belongs to.
     *
     * @return the unique letter code (ASCII).
     */
    public final char letterCode() { return letterCode; }
    
    /**
     * Gets the human-readable descriptive name that specifies the 'meaning' of this flag.
     *
     * @return the name of this flag.
     */
    public final String name() { return name; }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "'" + letterCode + "' - 0x" + Integer.toHexString(value) + " - " + name;
    }

   
    
}

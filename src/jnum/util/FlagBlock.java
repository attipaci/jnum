/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/


package jnum.util;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class FlagBlock.
 *
 * @param <Type> the generic type
 */
public class FlagBlock<Type extends Number> implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3475889288391954525L;
    
    /** The space. */
    private FlagSpace<Type> space;
    
    /** The from bit. */
    private int fromBit;
    
    /** The to bit. */
    private int toBit;
    
    /** The next bit. */
    private int nextBit;
    
    /** The mask. */
    private long mask = 0;
    
    /**
     * Instantiates a new flag block.
     *
     * @param space the group
     * @param fromBit the from bit
     * @param toBit the to bit
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    public FlagBlock(FlagSpace<Type> space, int fromBit, int toBit) throws IndexOutOfBoundsException {
        this.space = space;
        setBits(fromBit, toBit);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
        return super.hashCode() ^ space.hashCode() ^ HashCode.from(mask) ^ HashCode.from(nextBit);  
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof FlagBlock)) return false;
        if(!super.equals(o)) return false;
        FlagBlock<?> r = (FlagBlock<?>) o;
        if(r.mask != mask) return false;
        if(r.nextBit != nextBit) return false;
        if(!r.space.equals(space)) return false;
        return true;
    }
    
    /**
     * Gets the flag space.
     *
     * @return the flag space
     */
    public final FlagSpace<?> getFlagSpace() { return space; } 
    
    /**
     * Sets the bits.
     *
     * @param startBit the start bit
     * @param endBit the end bit
     * @return true, if successful
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    private boolean setBits(int startBit, int endBit) throws IndexOutOfBoundsException {
        if(startBit < 0) throw new IndexOutOfBoundsException("negative flag space start: " + startBit);
        if(endBit <= startBit) throw new IndexOutOfBoundsException("empty flag space.");
        
        boolean isUnchanged = true;
        if(endBit > space.getBits()) {
            endBit = space.getBits();
            isUnchanged = false;
        }
        this.nextBit = this.fromBit = startBit;
        this.toBit = endBit;
            
        mask = 0L;
        for(int bit = startBit; bit < endBit; bit++) mask |= 1L<<bit;
        
        return isUnchanged;
    }
  
    /**
     * Create a new flag using the next available flag bit in the block of bits represented by this object. The flag
     * will be identified with the unique letter code (in the flag space of this block), and will have a descriptive
     * human-readable name.
     *
     * @param letterCode the unique letter code identifying this flag in the flag space of this block.
     * @param name the descriptive human-readable name of this flag.
     * @return the flag object.
     * @throws IndexOutOfBoundsException if no more flags are available inside the block of bits represented by this object.
     */
    public synchronized Flag<Type> next(char letterCode, String name) throws IndexOutOfBoundsException {
        if(nextBit >= toBit) throw new IndexOutOfBoundsException("ran out of flag space: " + (toBit - fromBit) + "bits");
        long value = 1L << (nextBit++);
        if(space.contains(value)) return next(letterCode, name);
        return space.createFlag(value, letterCode, name);
    }
    
    /**
     * Gets the mask.
     *
     * @return the mask
     */
    public final long getMask() { return mask; }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return space.toString() + "[" + fromBit + ":" + toBit + "]";
    }
    
}

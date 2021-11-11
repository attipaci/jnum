/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.util;

import java.io.Serializable;


/**
 * A block of bits in a flag-space constituting some distinct flagging subspace. For example you can imagine
 * having a 32-bit integer flag space, in which te first 4 bits mark an error, the next 12 bits indicate
 * different boolean properties etc. Each of block of bits in the flag space that serves a different 
 * generic purpose can be distinguished as blocks for easier processing
 * 
 * @author Attila Kovacs
 *
 * @param <Type>    The generic type of integer element that defines the flag space. E.g. {@link java.lang.Integer}
 *                  for a 3-bit wide flag space.
 */
public class FlagBlock<Type extends Number> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3475889288391954525L;

    /**
     * The flagging space in which this block is defined.
     * 
     */
    private FlagSpace<Type> space;

    /**
     * The starting bit of this flagging block (inclusive).
     * 
     */
    private int fromBit;

    /**
     * The ending bit in this flagging blokc (exclusive), that is the bit that comes after this block.
     * 
     */
    private int toBit;

    /**
     * The next unpopulated bit in this block.
     */
    private int nextBit;

    private long mask = 0;
    
    /**
     * Creates a new block of flags inside a flagging space.
     * 
     * @param space         the parent flagging space in which this block of flags are defined.
     * @param fromBit       the first bit in the flagging space that belongs to this block
     * @param toBit         the ending bit of this block of flags, that is the next bit after this block.
     * @throws IndexOutOfBoundsException    If the starting bit argument is outside the range of
     *                                      bits supported by the parent flagging space.
     */
    public FlagBlock(FlagSpace<Type> space, int fromBit, int toBit) throws IndexOutOfBoundsException {
        this.space = space;
        setBits(fromBit, toBit);
    }

    @Override
    public int hashCode() { 
        return space.hashCode() ^ HashCode.from(mask) ^ HashCode.from(nextBit);  
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof FlagBlock)) return false;

        FlagBlock<?> r = (FlagBlock<?>) o;
        if(r.mask != mask) return false;
        if(r.nextBit != nextBit) return false;
        if(!r.space.equals(space)) return false;
        return true;
    }

    public final FlagSpace<?> getFlagSpace() { return space; } 
    
    /**
     * Set the span of bits to be used by this block of flags in the parent flag space.
     * 
     * @param startBit      The first bit in this flag space.
     * @param endBit        The end of the flag space (exclusive), that is the flag that follows this space.
     * @return              <code>true</code> if the ending index was adjusted to fit within the flagging space. 
     *                      Otherwise <code>false</code>
     * @throws IndexOutOfBoundsException    If the starting index is outside the range of bits suppoirted by the 
     *                                      parent flagging space.
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
     * Gets the mask for this block of bits.
     * 
     * @return      The long value, in which bits belonging to this block of flags are set to 1, and bits
     *              outside of this block set to zero.
     */
    public final long getMask() { return mask; }

    @Override
    public String toString() {
        return space.toString() + "[" + fromBit + ":" + toBit + "]";
    }
    
}

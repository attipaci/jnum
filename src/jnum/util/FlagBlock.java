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

public class FlagBlock implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3475889288391954525L;
    private FlagSpace space;
    private int fromBit;
    private int toBit;
    private int nextBit;
    private int mask = 0;
    
    public FlagBlock(FlagSpace group, int fromBit, int toBit) throws IndexOutOfBoundsException {
        this.space = group;
        setBits(fromBit, toBit);
    }
    
    @Override
    public int hashCode() { 
        return super.hashCode() ^ space.hashCode() ^ HashCode.get(mask) ^ HashCode.get(nextBit);  
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof FlagBlock)) return false;
        if(!super.equals(o)) return false;
        FlagBlock r = (FlagBlock) o;
        if(r.mask != mask) return false;
        if(r.nextBit != nextBit) return false;
        if(!r.space.equals(space)) return false;
        return true;
    }
    
    public final FlagSpace getFlagSpace() { return space; } 
    
    private void setBits(int startBit, int endBit) throws IndexOutOfBoundsException {
        if(startBit < 0) throw new IndexOutOfBoundsException("negative flag space start: " + startBit);
        if(endBit <= startBit) throw new IndexOutOfBoundsException("empty flag space.");
        if(endBit > space.getBits()) throw new IndexOutOfBoundsException("out of range flag space: " + endBit);
        this.nextBit = this.fromBit = startBit;
        this.toBit = endBit;
            
        mask = 0;
        for(int bit = startBit; bit < endBit; bit++) mask |= 1<<bit;
    }
  
    public Flag next(char letterCode, String name) throws IndexOutOfBoundsException {
        if(nextBit >= toBit) throw new IndexOutOfBoundsException("ran out of flag space: " + (toBit - fromBit) + "bits");
        return new Flag(space, 1<<(nextBit++), letterCode, name);
    }
    
    public final int getMask() { return mask; }
    
    @Override
    public String toString() {
        return space.toString() + "[" + fromBit + ":" + toBit + "]";
    }
    
}

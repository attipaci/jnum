/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.math;

import jnum.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class Offset2D.
 */
public class Offset2D extends Vector2D {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2072131491069589417L;
    
    /** The reference. */
    private Coordinate2D reference;
    
    /**
     * Instantiates a new offset 2 D.
     *
     * @param reference the reference
     */
    public Offset2D(Coordinate2D reference) {
        this.reference = reference;
    }
    
    /**
     * Instantiates a new offset 2 D.
     *
     * @param reference the reference
     * @param offset the offset
     */
    public Offset2D(Coordinate2D reference, Vector2D offset) {
        this(reference);
        copy(offset);
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Coordinate2D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Offset2D)) return false;
        Offset2D offset = (Offset2D) o;
        if(!Util.equals(offset.getCoordinateClass(), getCoordinateClass())) return false;
        return super.equals(o);
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Coordinate2D#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ reference.hashCode(); }
    
    /**
     * Gets the coordinate class.
     *
     * @return the coordinate class
     */
    public Class<?> getCoordinateClass() { return reference.getClass(); }
    
    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public Coordinate2D getReference() { return reference; }
    
}

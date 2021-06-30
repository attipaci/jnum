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

package jnum.math;

import java.util.stream.IntStream;

import jnum.Util;

/**
 * A vector class representing a 2D offset from some reference coordinate.
 * 
 * @author Attila Kovacs
 *
 */
public class Offset2D extends Vector2D {

    private static final long serialVersionUID = -2072131491069589417L;

    private Coordinate2D reference;

    public Offset2D(Coordinate2D reference) {
        this.reference = reference;
    }

    public Offset2D(Coordinate2D reference, Vector2D offset) {
        this(reference);
        copy(offset);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Offset2D)) return false;
        Offset2D offset = (Offset2D) o;
        if(!Util.equals(offset.getCoordinateClass(), getCoordinateClass())) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() { return super.hashCode() ^ reference.hashCode(); }


    public Class<?> getCoordinateClass() { return reference.getClass(); }

    public Coordinate2D getReference() { return reference; }


    public static Vector2D[] copyOf(Vector2D[] array) {
        Vector2D[] copy = new Vector2D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }

}

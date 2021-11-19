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

import java.io.Serializable;
import java.util.stream.IntStream;

import jnum.Copiable;
import jnum.Unit;
import jnum.Util;
import jnum.data.index.Index1D;

/**
 * A base class for 3D coordinates of all types. That is basically anything with a triplet of real values.
 * 
 * @author Attila Kovacs
 *
 */
public class Coordinate3D implements RealCoordinates, Serializable, Cloneable, Copiable<Coordinate3D> {
    /** */
    private static final long serialVersionUID = 4670218761839380720L;

    private double x;
    private double y;
    private double z;

    /**
     * Constructs new 3D coordinates initialized to zeroes.
     * 
     */
    public Coordinate3D() {}

    /**
     * Constructs new 3D coordinates with the specified component values.
     * 
     * @param x    the first (x-type) coordinate value
     * @param y    the second (y-type) coordinate value
     * @param z    the third (z-type) coordinate value
     */
    public Coordinate3D(double x, double y, double z) {
        this();
        set(x, y, z);
    }

    /**
     * Constructs new 3D coordinates based on some other coordinates. The argument
     * may represent coordinates of any type or dimension. Only up to the first three coordinate 
     * components of the argument are used for initializing the new 3D coordinate instance.
     * 
     * @param v     Coordinates whose first 1 to 3 components will define the new 3D coordinates.
     */
    public Coordinate3D(Coordinates<Double> v) {
        this(v.x(), v.y(), v.z());
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y) ^ Double.hashCode(z);
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        if(!(o instanceof Coordinate3D)) return false;

        Coordinate3D coords = (Coordinate3D) o;
        if(coords.x != x) return false;
        if(coords.y != y) return false;
        if(coords.z != z) return false;
        
        return true;
    }
    
    @Override
    public boolean equals(Coordinates<Double> coords, double precision) {
        if(coords == null) return false;
        
        if(coords.dimension() != dimension()) return false;
        if(!Util.equals(coords.x(), x, precision)) return false;
        if(!Util.equals(coords.y(), y, precision)) return false;
        if(!Util.equals(coords.z(), z, precision)) return false;
        
        return true;
    }

    @Override
    public final Class<Double> getComponentType() {
        return Double.class;
    }


    @Override
    public Coordinate3D clone() {
        try { return (Coordinate3D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Coordinate3D copy() {
        return clone();
    }

    @Override
    public void copy(Coordinates<? extends Double> other) {
        set(other.x(), other.y(), other.z());
    }

    /**
     * Flips the sign of the <i>x</i>-type coordinate.
     * 
     */
    public void flipX() { x = -x; }

    /**
     * Flips the sign of the <i>y</i>-type coordinate.
     * 
     */
    public void flipY() { y = -y; }

    /**
     * Flips the sign of the <i>z</i>-type coordinate.
     * 
     */
    public void flipZ() { z = -z; }


    @Override
    public final Double x() { return x; }

    @Override
    public final Double y() { return y; }

    @Override
    public final Double z() { return z; }

    /**
     * Sets the <i>x</i>-type coordinate to the specified value.
     * 
     * @param value    the new <i>x</i>-type coordinate value.
     */
    public final void setX(final double value) { this.x = value; }

    /**
     * Sets the <i>y</i>-type coordinate to the specified value.
     * 
     * @param value    the new <i>y</i>-type coordinate value.
     */
    public final void setY(final double value) { this.y = value; }

    /**
     * Sets the <i>z</i>-type coordinate to the specified value.
     * 
     * @param value    the new <i>z</i>-type coordinate value.
     */
    public final void setZ(final double value) { this.z = value; }

    /**
     * Increments the <i>x</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>x</i>-type coordinate increment.
     */
    public final void addX(final double value) { this.x += value; }

    /**
     * Increments the <i>y</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>y</i>-type coordinate increment.
     */
    public final void addY(final double value) { this.y += value; }

    /**
     * Increments the <i>z</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>z</i>-type coordinate increment.
     */
    public final void addZ(final double value) { this.z += value; }

    /**
     * Decrements the <i>x</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>x</i>-type coordinate decrement.
     */
    public final void subtractX(final double value) { this.x -= value; }

    /**
     * Decrements the <i>y</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>y</i>-type coordinate decrement.
     */
    public final void subtractY(final double value) { this.y -= value; }

    /**
     * Decrements the <i>z</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>z</i>-type coordinate decrement.
     */
    public final void subtractZ(final double value) { this.z -= value; }

    /**
     * Scales the <i>x</i>-type coordinate by the specified value.
     * 
     * @param factor    the scalar factor to apply to the <i>x</i>-type coordinate.
     */
    public final void scaleX(double factor) { x *= factor; }

    /**
     * Scales the <i>y</i>-type coordinate by the specified value.
     * 
     * @param factor    the scalar factor to apply to the <i>y</i>-type coordinate.
     */
    public final void scaleY(double factor) { y *= factor; }

    /**
     * Scales the <i>z</i>-type coordinate by the specified value.
     * 
     * @param factor    the scalar factor to apply to the <i>z</i>-type coordinate.
     */
    public final void scaleZ(double factor) { z *= factor; }


    @Override
    public void parseComponent(int index, String text) throws NumberFormatException {
        switch(index) {
        case X: parseX(text); break;
        case Y: parseY(text); break;
        case Z: parseZ(text); break;
        default: throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no component " + index);
        }
    }

    protected void parseX(String token) throws NumberFormatException {
        setX(Double.parseDouble(token));
    }

    protected void parseY(String token) throws NumberFormatException {
        setY(Double.parseDouble(token));
    }

    protected void parseZ(String token) throws NumberFormatException {
        setY(Double.parseDouble(token));
    }


    @Override
    public final int size() {
        return 3;
    }


    @Override
    public final Index1D getSize() {
        size.set(3);
        return size;
    }

    @Override
    public final Double getComponent(final int index) {
        switch(index) {
        case X: return x;
        case Y: return y;
        case Z: return z;
        default: return 0.0;
        }
    }

    @Override
    public final void setComponent(final int index, final Double value) {
        switch(index) {
        case X: x = value; break;
        case Y: y = value; break;
        case Z: z = value; break;
        default: throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no component " + index);
        }
    }
    
    /**
     * Creates an array of 3D coordinates, with each element initialized to a default (zero) coordinate
     * intance.
     * 
     * @param size  the array size
     * @return      a new array of the desired size with all elements initialized to 3D coordinates
     *              with zero components.
     */
    public static Coordinate3D[] createArray(int size) {
        Coordinate3D[] array = new Coordinate3D[size];
        IntStream.range(0, size).parallel().forEach(i -> array[i] = new Coordinate3D());
        return array;
    }
    
    /**
     * Return a fully independent copy of an array of 3D coordinates. Modifications to either
     * the original or the copy will be guaranteed to not impact the other.
     * 
     * @param array     the array of 3D coordinates to copy
     * @return          an independent copy of the input array, in which all elements are themselves
     *                  copies of the original elements.
     */
    public static Coordinate3D[] copyOf(Coordinate3D[] array) {
        Coordinate3D[] copy = new Coordinate3D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
    
    /**
     * Returns a string representation of 3D coordinates in the specified physical units.
     * 
     * @param coords    the 3D coordinates
     * @param unit      the physical unit in which to represent
     * @return          a string representation of the coordinates, including the physical unit name.
     * 
     * @see #toString(Coordinate3D, Unit, int)
     */
    public static String toString(Coordinate3D coords, Unit unit) {
        return toString(coords, unit, 3);
    }

    /**
     * Returns a string representation of 3D coordinates in the specified physical units.
     * 
     * @param coords    the 3D coordinates
     * @param unit      the physical unit in which to represent
     * @param decimals  the decimal places to show.
     * 
     * @return          a string representation of the coordinates, including the physical unit name.
     * 
     * @see #toString(Coordinate3D, Unit)
     */
    public static String toString(Coordinate3D coords, Unit unit, int decimals) {
        return Util.s[decimals].format(coords.x / unit.value()) +  ", " 
                + Util.s[decimals].format(coords.y / unit.value()) + ", "
                + Util.s[decimals].format(coords.z / unit.value()) + " " + unit.name();
    }

    private static final Index1D size = new Index1D(3);

    /** the index of the <i>x</i>-type (first) coordinate */
    public static final int X = 0;    

    /** the index of the <i>y</i>-type (second) coordinate */
    public static final int Y = 1;

    /** the index of the <i>z</i>-type (third) coordinate */
    public static final int Z = 2;    

}

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

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.stream.IntStream;

import jnum.Copiable;
import jnum.IncompatibleTypesException;
import jnum.Unit;
import jnum.Util;
import jnum.data.index.Index1D;
import jnum.text.StringParser;



/**
 * A base class for 2D coordinates of all types. That is basically anything with a pair of real values.
 * 
 * @author Attila Kovacs
 * 
 * @see Coordinate3D
 *
 */
public class Coordinate2D implements RealCoordinates, Serializable, Cloneable, Copiable<Coordinate2D> {
    /** */
    private static final long serialVersionUID = -3978373428597134906L;

    private double x, y;


    /**
     * Constructs a new pair of coordinates initialized to zeroes.
     * 
     */
    public Coordinate2D() {}

    /**
     * Constructs a new pair of coordinates with the specified pair of values.
     * 
     * @param X    the first (x-type) coordinate value
     * @param Y    the second (y-type) coordinate value
     */
    public Coordinate2D(double X, double Y) { 
        this();
        set(X, Y);  
    }

    /**
     * Constructs a new pair of coordinates based on some other coordinates. The argument
     * may represent coordinates of any type or dimension. Only up to the first two coordinate 
     * components of the argument are used for initializing the new coordinate pair.
     * 
     * @param v     Coordinates whose first 1 or 2 components will define the new coordinate pair.
     */
    public Coordinate2D(Coordinates<Double> v) {
        this(v.x(), v.y());
    }


    /**
     * Constructs a new pair of coordinates from a Java {@link Point2D} object. The
     * <code>Coordinate2D</code> class is closely resembling the Java {@link Point2D}
     * class but unlike that one, we allow changing the coordinate values at a later
     * time, something that {@link Point2D} does not support.
     * 
     * @param point     The {@link Point2D} representation of the same two coordinates.
     */
    public Coordinate2D(Point2D point) { this(point.getX(), point.getY()); }

    /**
     * Constructs a new pair of coordinates based on another pair of coordinates. The newly
     * created coordinates will be initialized with the same coordinate values, but may
     * otherwise represent anentirely distinct class of coordinates from those of the
     * argument.
     * 
     * @param template  The pair of coordinates to mimic, but the argument may be of an entirely
     *                  different (and even incopatible type) of coordinates. 
     */
    public Coordinate2D(Coordinate2D template) { this(template.x, template.y); }

    /**
     * Constructs a new pair of coordinates based on its textual representation, if
     * possible.
     * 
     * @param text     The text representation of the coordinates, normally two comma
     *                 separated values, possible in brackets.
     * @throws NumberFormatException  If the coordinates could not be parse from the text.
     */
    public Coordinate2D(String text) throws NumberFormatException { parse(text); }

    @Override
    public final Class<Double> getComponentType() {
        return Double.class;
    }

    @Override
    public void copy(Coordinates<? extends Double> other) {
        setX(other.x());
        setY(other.y());
    }
    
    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        if(!(o instanceof Coordinate2D)) return false;
        
        Coordinate2D coords = (Coordinate2D) o;
        if(coords.x != x) return false;
        if(coords.y != y) return false;
        
        return true;
    }
    
    @Override
    public boolean equals(Coordinates<Double> coords, double precision) {
        if(coords == null) return false;
        
        if(coords.dimension() != dimension()) return false;
        if(!Util.equals(coords.x(), x, precision)) return false;
        if(!Util.equals(coords.y(), y, precision)) return false;
        
        return true;
    }

    @Override
    public final Double x() { return x; }

    @Override
    public final Double y() { return y; }

    @Override
    public final Double z() { return 0.0; }

    /**
     * Sets the <i>x</i>-type coordinate to the specified value.
     * 
     * @param value    the new <i>x</i>-type coordinate value.
     */
    public void setX(final double value) { x = value; }

    /**
     * Sets the <i>y</i>-type coordinate to the specified value.
     * 
     * @param value    the new <i>y</i>-type coordinate value.
     */
    public void setY(final double value) { y = value; }

    /**
     * Increments the <i>x</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>x</i>-type coordinate increment.
     */
    public void addX(final double value) { x += value; }

    /**
     * Increments the <i>y</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>y</i>-type coordinate increment.
     */
    public void addY(final double value) { y += value; }

    /**
     * Decrements the <i>x</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>x</i>-type coordinate decrement.
     */
    public void subtractX(final double value) { x -= value; }

    /**
     * Decrements the <i>y</i>-type coordinate by the specified value.
     * 
     * @param value    the <i>y</i>-type coordinate decrement.
     */
    public void subtractY(final double value) { y -= value; }

    /**
     * Scales the <i>x</i>-type coordinate by the specified value.
     * 
     * @param value    the scalar factor to apply to the <i>x</i>-type coordinate.
     */
    public final void scaleX(final double value) { x *= value; }

    /**
     * Scales the <i>y</i>-type coordinate by the specified value.
     * 
     * @param value    the scalar factor to apply to the <i>y</i>-type coordinate.
     */
    public final void scaleY(final double value) { y *= value; }

  
    @Override
    public Coordinate2D clone() {
        try { return (Coordinate2D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Coordinate2D copy() { return clone(); }

    /**
     * Converts these coordinates to and AWT {@link Point2D} object with the same components.
     * 
     * @return  the equivalent new AWT <code>Point2D</code> object.
     * 
     * @see #toPoint2D(Point2D)
     */
    public Point2D getPoint2D() {
        return new Point2D.Double(x, y);
    }

    /**
     * Converts these coordinates to and AWT {@link Point2D} object with the same components.
     * 
     * @param point     the AWT <code>Point2D</code> object to set to the location of these coordinates.
     * 
     * @see #fromPoint2D(Point2D)
     * @see #getPoint2D()
     */
    public void toPoint2D(Point2D point) {
        point.setLocation(x, y);
    }

    /**
     * Sets these coordinates to the location of an AWT {@link Point2D} object.
     * 
     * @param point     the AWT <code>Point2D</code> object providing the location for these coordinates.
     * 
     * @see #toPoint2D(Point2D)
     */
    public void fromPoint2D(Point2D point) {
        set(point.getX(), point.getY());
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

    

    @Override
    public void parseComponent(int index, String text) throws NumberFormatException {
        switch(index) {
        case X: parseX(text); break;
        case Y: parseY(text); break;
        default: throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no component " + index);
        }
    }
    
    /**
     * Parses the <i>x</i> coordinates from the spefified string token.
     * 
     * @param token     the string description of the <i>x</i> coordinate.
     * @throws NumberFormatException    if the string does not seem to contain a suitable
     *                                  representation for the <i>x</i> coordinate.
     *                                  
     * @see #parseY(String)
     * @see #parse(StringParser)
     */
    protected void parseX(String token) throws NumberFormatException {
        setX(Double.parseDouble(token));
    }

    /**
     * Parses the <i>y</i> coordinates from the spefified string token.
     * 
     * @param token     the string description of the <i>y</i> coordinate.
     * @throws NumberFormatException    if the string does not seem to contain a suitable
     *                                  representation for the <i>y</i> coordinate.
     *                                  
     * @see #parseX(String)
     * @see #parse(StringParser)
     */
    protected void parseY(String token) throws NumberFormatException {
        setY(Double.parseDouble(token));
    }


    
    public void convertFrom(Coordinate2D coords) throws IncompatibleTypesException {
        if(getClass().isAssignableFrom(coords.getClass())) copy(coords);
        else throw new IncompatibleTypesException(coords, this);
    }

    public final void convertTo(Coordinate2D coords) {
        coords.convertFrom(this);
    }


    @Override
    public final int size() {
        return 2;
    }


    @Override
    public final Index1D getSize() {
        return new Index1D(2);
    }
    
    @Override
    public final Double getComponent(final int index) {
        switch(index) {
        case X: return x();
        case Y: return y();
        default: return 0.0;
        }
    }

    @Override
    public final void setComponent(final int index, final Double value) {
        switch(index) {
        case X: setX(value); break;
        case Y: setY(value); break;
        default: throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no component " + index);
        }
    }
    

    @Override
    public Double copyOf(int i) { 
        return getComponent(i);
    }

    @Override
    public String toString() {
        return toDefaultString();
    }
    
    @Override
    public String toString(int i, NumberFormat nf) {
        return nf.format(getComponent(i));
    }

    /**
     * Creates an array of 2D coordinates, with each element initialized to a default (zero) coordinate
     * intance.
     * 
     * @param size  the array size
     * @return      a new array of the desired size with all elements initialized to 2D coordinates
     *              with zero components.
     */
    public static Coordinate2D[] createArray(int size) {
        Coordinate2D[] array = new Coordinate2D[size];
        IntStream.range(0, size).parallel().forEach(i -> array[i] = new Coordinate2D());
        return array;
    }
    
    /**
     * Return a fully independent copy of an array of coordinate pairs. Modifications to either
     * the original or the copy will be guaranteed to not impact the other.
     * 
     * @param array     the array of 2D coordinates to copy
     * @return          an independent copy of the input array, in which all elements are themselves
     *                  copies of the original elements.
     */
    public static Coordinate2D[] copyOf(Coordinate2D[] array) {
        Coordinate2D[] copy = new Coordinate2D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
    
    /**
     * Returns a string representation of 2D coordinates in the specified physical units.
     * 
     * @param coords    the 2D coordinates
     * @param unit      the physical unit in which to represent
     * @return          a string representation of the coordinates, including the physical unit name.
     * 
     * @see #toString(Coordinate2D, Unit, int)
     */
    public static String toString(Coordinate2D coords, Unit unit) {
        return toString(coords, unit, 3);
    }

    /**
     * Returns a string representation of 2D coordinates in the specified physical units.
     * 
     * @param coords    the 2D coordinates
     * @param unit      the physical unit in which to represent
     * @param decimals  the decimal places to show.
     * 
     * @return          a string representation of the coordinates, including the physical unit name.
     * 
     * @see #toString(Coordinate2D, Unit)
     */
    public static String toString(Coordinate2D coords, Unit unit, int decimals) {
        return Util.s[decimals].format(coords.x / unit.value()) +  ", " 
                + Util.s[decimals].format(coords.y / unit.value()) + " " + unit.name();
    }
    

    /** the index of the <i>x</i>-type (first) coordinate */
    public static final int X = 0;

    /** the index of the <i>y</i>-type (first) coordinate */
    public static final int Y = 1;



}

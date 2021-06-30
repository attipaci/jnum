/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.stream.IntStream;

import jnum.Copiable;
import jnum.IncompatibleTypesException;
import jnum.Unit;
import jnum.Util;
import jnum.ViewableAsDoubles;
import jnum.data.IndexedValues;
import jnum.data.samples.Index1D;
import jnum.fits.FitsToolkit;
import jnum.text.NumberFormating;
import jnum.text.Parser;
import jnum.text.StringParser;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


/**
 * A base class for 2D coordinates of all types. That is basically anything with a pair of real values.
 * 
 * @author Attila Kovacs
 *
 */
public class Coordinate2D implements Coordinates<Double>, Serializable, Cloneable, Copiable<Coordinate2D>, 
ViewableAsDoubles, Parser, NumberFormating {
	
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
    public final Double x() { return x; }
	
	@Override
    public final Double y() { return y; }
	
	@Override
    public final Double z() { return 0.0; }
	
	
	public void setX(final double value) { x = value; }
	
	public void setY(final double value) { y = value; }
	
	public void addX(final double value) { x += value; }
	
	public void addY(final double value) { y += value; }
	
	public void subtractX(final double value) { x -= value; }
	
	public void subtractY(final double value) { y -= value; }
	
	public final void scaleX(final double value) { x *= value; }
	
	public final void scaleY(final double value) { y *= value; }
	
	@Override
	public int hashCode() {
		return HashCode.from(x) ^ ~HashCode.from(y);
	}

	@Override
	public Coordinate2D clone() {
		try { return (Coordinate2D) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	@Override
	public Coordinate2D copy() { return clone(); }
	
	public Point2D getPoint2D() {
		return new Point2D.Double(x, y);
	}
	
	public void toPoint2D(Point2D point) {
		point.setLocation(x, y);
	}
	

	public void fromPoint2D(Point2D point) {
		set(point.getX(), point.getY());
	}
	
	public void set(final double X, final double Y) { setX(X); setY(Y); }

	public void invertX() { x *= -1.0; }
	
	public void invertY() { y *= -1.0; }
	
	public void zero() { x = y = 0.0; }

	
	public boolean isNull() { 
		if(x != 0.0) return false;
		if(y != 0.0) return false; 
		return true;
	}	


	public void NaN() { x = Double.NaN; y = Double.NaN; }


	public final boolean isNaN() { 
		if(Double.isNaN(x)) return true;
		if(Double.isNaN(y)) return true;
		return false;
	}
	

	public final boolean isInfinite() { 
		if(Double.isInfinite(x)) return true;
		if(Double.isInfinite(y)) return true;
		return false;
	}


	public final void weightedAverageWith(double w1, final Coordinate2D coord, double w2) {
		final double isumw = 1.0 / (w1 + w2);
		w1 *= isumw; w2 *= isumw;
		x = w1 * x + w2 * coord.x;
		y = w1 * y + w2 * coord.y;
	}
	
	public final void parse(String spec) {
	    parse(spec, new ParsePosition(0));
	}


	@Override
	public final void parse(String text, ParsePosition pos) throws IllegalArgumentException {
	    parse(new StringParser(text, pos));
	}
	
	/**
	 * Parses text x,y 2D coordinate representations that are in the format(s) of:
	 * 
	 * <pre>
	 * {@code
	 *     x,y / x y
	 * }
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 * {@code
	 *     (x,y) / (x y)
	 * }
	 * </pre>
	 * 
	 * More specifically, the x and y values may be separated either by comma(s) or white space(s) (including 
	 * tabs line breaks, carriage returns), or a combination of both. The pair of values may be bracketed (or 
	 * not). Any number of white spaces may exists between the elements (brackets and pair of values), or 
	 * precede the text element. Thus, the following will parse as a proper x,y (1.0,-2.0) pair:
	 * 
	 * <pre>
	 * {@code
	 *     (  \t\n 1.0 ,,, \r , \t -2.0    \n  )
	 * }
	 * </pre>
	 * 
	 * @param parser  The string parsing helper object.
	 * @throws NumberFormatException
	 */
	public void parse(StringParser parser) throws NumberFormatException {
	    boolean isBracketed = false;
	    
	    zero();
	    
	    parser.skipWhiteSpaces();
	       
	    if(parser.peek() == '(') {
	        isBracketed = true;
	        parser.skip(1);
	        parser.skipWhiteSpaces();
	    }
	        
	    parseX(parser.nextToken(Util.getWhiteSpaceChars() + ","));
	    parseY(parser.nextToken(Util.getWhiteSpaceChars() + "," + (isBracketed ? "" : ")")));
	}

	protected void parseX(String token) throws NumberFormatException {
	    setX(Double.parseDouble(token));
	}
	
	protected void parseY(String token) throws NumberFormatException {
	    setY(Double.parseDouble(token));
	}
	
	@Override
	public String toString(NumberFormat nf) {
		return "(" + nf.format(x) + "," + nf.format(y) + ")";
	}

	@Override
	public String toString() { 
		return "(" + x + "," + y + ")";
	}
	

	@Override
	public final void createFromDoubles(Object array) throws IllegalArgumentException {
		if(!(array instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
		double[] components = (double[]) array;
		if(components.length != 2) throw new IllegalArgumentException("argument double[] array is to small.");
		x = components[0];
		y = components[1];
	}


	@Override
	public final void viewAsDoubles(Object view) throws IllegalArgumentException {
		if(!(view instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
		double[] components = (double[]) view;
		if(components.length != 2) throw new IllegalArgumentException("argument double[] array is to small.");
		components[0] = x;
		components[1] = y;
	}
	
	@Override
	public final double[] viewAsDoubles() {
		return new double[] { x, y };		
	}
	
	
	public double getValue(int field) throws NoSuchFieldException {
		switch(field) {
		case X: return x;
		case Y: return y;
		default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
		}
	}
	

	public void setValue(int field, double value) throws NoSuchFieldException {
		switch(field) {
		case X: x = value; break;
		case Y: y = value; break; 
		default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
		}
	}
	
	
	public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {
	    Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard(keyStem + "1" + alt, x, "The reference x coordinate in SI units."));
		c.add(new HeaderCard(keyStem + "2" + alt, y, "The reference y coordinate in SI units."));
	}
	

	public void parseHeader(Header header, String keyStem, String alt, Coordinate2D defaultValue) {
		x = header.getDoubleValue(keyStem + "1" + alt, defaultValue == null ? 0.0 : defaultValue.x());
		y = header.getDoubleValue(keyStem + "2" + alt, defaultValue == null ? 0.0 : defaultValue.y());
	}
	

	public static String toString(Coordinate2D coords, Unit unit) {
		return toString(coords, unit, 3);
	}
	

	public static String toString(Coordinate2D coords, Unit unit, int decimals) {
		return Util.f[decimals].format(coords.x / unit.value()) +  ", " 
			+ Util.f[decimals].format(coords.y / unit.value()) + " " + unit.name();
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
    public final int capacity() {
        return 2;
    }


    @Override
    public final int dimension() {
        return 1;
    }


    @Override
    public final Index1D getSize() {
        return size;
    }


    @Override
    public final Double get(Index1D index) {
        return getComponent(index.i());
    }
    
    @Override
    public void set(Index1D index, Double value) {
        setComponent(index.i(), value);
    }


    @Override
    public final Index1D getIndexInstance() {
        return new Index1D();
    }


    @Override
    public final Index1D copyOfIndex(Index1D index) {
        return index.copy();
    }


    @Override
    public final boolean conformsTo(Index1D size) {
        return size.i() == 2;
    }


    @Override
    public final boolean conformsTo(IndexedValues<Index1D, ?> data) {
        return data.getSize().i() == 2;
    }


    @Override
    public final String getSizeString() {
        return "[2]";
     }


    @Override
    public final boolean containsIndex(Index1D index) {
        int i = index.getValue(0);
        if(i == X) return true;
        if(i == Y) return true;
        return false;
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
    
    public static Coordinate2D[] copyOf(Coordinate2D[] array) {
        Coordinate2D[] copy = new Coordinate2D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
	
    private final static Index1D size = new Index1D(2);
   
	public static final int X = 0;
	public static final int Y = 1;
}

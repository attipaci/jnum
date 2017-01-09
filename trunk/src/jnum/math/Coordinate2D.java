/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.Unit;
import jnum.Util;
import jnum.ViewableAsDoubles;
import jnum.text.NumberFormating;
import jnum.text.Parser;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
//Add parsing

/**
 * The Class Coordinate2D.
 */
public class Coordinate2D implements Serializable, Cloneable, Copiable<Coordinate2D>, CopyCat<Coordinate2D>, ViewableAsDoubles, Parser, NumberFormating {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3978373428597134906L;

	/** The y. */
	private double x, y;

	/**
	 * Instantiates a new coordinate2 d.
	 */
	public Coordinate2D() {}
	
	/**
	 * Instantiates a new coordinate2 d.
	 *
	 * @param point the point
	 */
	public Coordinate2D(Point2D point) { setX(point.getX()); setY(point.getY()); }
	
	/**
	 * Instantiates a new coordinate2 d.
	 *
	 * @param text the text
	 */
	public Coordinate2D(String text) { parse(text); }

	/**
	 * Instantiates a new coordinate2 d.
	 *
	 * @param X the x
	 * @param Y the y
	 */
	public Coordinate2D(double X, double Y) { setX(X); setY(Y); }

	/**
	 * Instantiates a new coordinate2 d.
	 *
	 * @param template the template
	 */
	public Coordinate2D(Coordinate2D template) { setX(template.x); setY(template.y); }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return equals(o, 0.0);
	}
	
	/**
	 * Equals.
	 *
	 * @param o the o
	 * @param precision the precision
	 * @return true, if successful
	 */
	public boolean equals(Object o, double precision) {
		if(o == this) return true;
		if(!(o instanceof Coordinate2D)) return false;
		if(!super.equals(o)) return false;
		
		final Coordinate2D coord = (Coordinate2D) o;
	
		if(Math.abs(x - coord.x) > precision) return false;
		if(Math.abs(y - coord.y) > precision) return false;
		
		return true;
	}
	
	
	/**
	 * Copy.
	 *
	 * @param template the template
	 */
	@Override
	public void copy(final Coordinate2D template) { setX(template.x); setY(template.y); }
	
	
	// Access methods...
	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public final double x() { return x; }
	
	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public final double y() { return y; }
	
	/**
	 * Sets the x.
	 *
	 * @param value the new x
	 */
	public void setX(final double value) { x = value; }
	
	/**
	 * Sets the y.
	 *
	 * @param value the new y
	 */
	public void setY(final double value) { y = value; }
	
	/**
	 * Adds the x.
	 *
	 * @param value the value
	 */
	public void addX(final double value) { x += value; }
	
	/**
	 * Adds the y.
	 *
	 * @param value the value
	 */
	public void addY(final double value) { y += value; }
	
	/**
	 * Subtract x.
	 *
	 * @param value the value
	 */
	public void subtractX(final double value) { x -= value; }
	
	/**
	 * Subtract y.
	 *
	 * @param value the value
	 */
	public void subtractY(final double value) { y -= value; }
	
	/**
	 * Scale x.
	 *
	 * @param value the value
	 */
	public final void scaleX(final double value) { x *= value; }
	
	/**
	 * Scale y.
	 *
	 * @param value the value
	 */
	public final void scaleY(final double value) { y *= value; }
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCode.from(x) ^ ~HashCode.from(y);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public Coordinate2D copy() { return (Coordinate2D) clone(); }
	
	/**
	 * Gets the point2 d.
	 *
	 * @return the point2 d 
	 */
	public Point2D getPoint2D() {
		return new Point2D.Double(x, y);
	}
	
	/**
	 * To point2 d.
	 *
	 * @param point the point
	 */
	public void toPoint2D(Point2D point) {
		point.setLocation(x, y);
	}
	
	/**
	 * From point2 d.
	 *
	 * @param point the point
	 */
	public void fromPoint2D(Point2D point) {
		set(point.getX(), point.getY());
	}
	
	/**
	 * Sets the.
	 *
	 * @param X the x
	 * @param Y the y
	 */
	public void set(final double X, final double Y) { setX(X); setY(Y); }

	/**
	 * Invert x.
	 */
	public void invertX() { x *= -1.0; }
	
	/**
	 * Invert y.
	 */
	public void invertY() { y *= -1.0; }
	
	/**
	 * Zero.
	 */
	public void zero() { x = y = 0.0; }

	/**
	 * Checks if is null.
	 *
	 * @return true, if is null
	 */
	public boolean isNull() { 
		if(x != 0.0) return false;
		if(y != 0.0) return false; 
		return true;
	}	

	/**
	 * Na n.
	 */
	public void NaN() { x = Double.NaN; y = Double.NaN; }

	/**
	 * Checks if is na n.
	 *
	 * @return true, if is na n
	 */
	public final boolean isNaN() { 
		if(Double.isNaN(x)) return true;
		if(Double.isNaN(y)) return true;
		return false;
	}
	
	/**
	 * Checks if is infinite.
	 *
	 * @return true, if is infinite
	 */
	public final boolean isInfinite() { 
		if(Double.isInfinite(x)) return true;
		if(Double.isInfinite(y)) return true;
		return false;
	}

	/**
	 * Weighted average with.
	 *
	 * @param w1 the w1
	 * @param coord the coord
	 * @param w2 the w2
	 */
	public final void weightedAverageWith(double w1, final Coordinate2D coord, double w2) {
		final double isumw = 1.0 / (w1 + w2);
		w1 *= isumw; w2 *= isumw;
		x = w1 * x + w2 * coord.x;
		y = w1 * y + w2 * coord.y;
	}

	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @throws NumberFormatException the number format exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@Override
	public void parse(String text) throws NumberFormatException, IllegalArgumentException {
		int from = 0, to = text.length();
		
		// NOTE: Written for performance. The routine does not create any intermediate objects...
		
		// Ignore outer set of brackets, if exist...
		if(text.contains("(")) {
			from = text.indexOf('(') + 1;
			to = text.lastIndexOf(')');
			if(to < from) throw new IllegalArgumentException("Unmatched brackets: " + text);
		}
		
		// Find the comma that separating the coordinates...
		int i = text.indexOf(',');
		
		// Check that the comma falls between the limits and that 
		// at least one character is available for both coordinates.
		if(i == from || i+1 >= to) throw new IllegalArgumentException("Not a comma-separated pair of coordinates: " + text.substring(from, to));
		
		// Parse the coordinates here...
		if(i>0 && i<text.length()) {
			x = Double.parseDouble(text.substring(from, i));	
			y = Double.parseDouble(text.substring(i+1, to));
		}
	}
	
	/**
	 * Parses the.
	 *
	 * @param tokens the tokens
	 */
	public void parse(StringTokenizer tokens) {
		set(Double.parseDouble(tokens.nextToken()), Double.parseDouble(tokens.nextToken())); 
	}

	/**
	 * To string.
	 *
	 * @param nf the nf
	 * @return the string
	 */
	@Override
	public String toString(NumberFormat nf) {
		return "(" + nf.format(x) + "," + nf.format(y) + ")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { 
		return "(" + x + "," + y + ")";
	}
	
	/**
	 * Creates the from doubles.
	 *
	 * @param array the array
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@Override
	public final void createFromDoubles(Object array) throws IllegalArgumentException {
		if(!(array instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
		double[] components = (double[]) array;
		if(components.length != 2) throw new IllegalArgumentException("argument double[] array is to small.");
		x = components[0];
		y = components[1];
	}

	/**
	 * View as doubles.
	 *
	 * @param view the view
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@Override
	public final void viewAsDoubles(Object view) throws IllegalArgumentException {
		if(!(view instanceof double[])) throw new IllegalArgumentException("argument is not a double[].");
		double[] components = (double[]) view;
		if(components.length != 2) throw new IllegalArgumentException("argument double[] array is to small.");
		components[0] = x;
		components[1] = y;
	}
	
	/**
	 * View as doubles.
	 *
	 * @return the object
	 */
	@Override
	public final Object viewAsDoubles() {
		return new double[] { x, y };		
	}
	
	
	/**
	 * Gets the value.
	 *
	 * @param field the field
	 * @return the value
	 * @throws NoSuchFieldException the no such field exception
	 */
	public double getValue(int field) throws NoSuchFieldException {
		switch(field) {
		case X: return x;
		case Y: return y;
		default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
		}
	}
	
	/**
	 * Sets the value.
	 *
	 * @param field the field
	 * @param value the value
	 * @throws NoSuchFieldException the no such field exception
	 */
	public void setValue(int field, double value) throws NoSuchFieldException {
		switch(field) {
		case X: x = value; break;
		case Y: y = value; break; 
		default: throw new NoSuchFieldException(getClass().getSimpleName() + " has no field for " + field);
		}
	}
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @throws HeaderCardException the header card exception
	 */
	public void edit(Cursor<String, HeaderCard> cursor) throws HeaderCardException { edit(cursor, ""); }
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @param alt the alt
	 * @throws HeaderCardException the header card exception
	 */
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {
		cursor.add(new HeaderCard("CRVAL1" + alt, x, "The reference x coordinate in SI units."));
		cursor.add(new HeaderCard("CRVAL2" + alt, y, "The reference y coordinate in SI units."));
	}
	
	/**
	 * Parses the.
	 *
	 * @param header the header
	 */
	public void parse(Header header) { parse(header, ""); }
	
	/**
	 * Parses the.
	 *
	 * @param header the header
	 * @param alt the alt
	 */
	public void parse(Header header, String alt) {
		x = header.getDoubleValue("CRVAL1" + alt, 0.0);
		y = header.getDoubleValue("CRVAL2" + alt, 0.0);
	}
	
	/**
	 * To string.
	 *
	 * @param coords the coords
	 * @param unit the unit
	 * @return the string
	 */
	public static String toString(Coordinate2D coords, Unit unit) {
		return toString(coords, unit, 3);
	}
	
	/**
	 * To string.
	 *
	 * @param coords the coords
	 * @param unit the unit
	 * @param decimals the decimals
	 * @return the string
	 */
	public static String toString(Coordinate2D coords, Unit unit, int decimals) {
		return Util.f[decimals].format(coords.x / unit.value()) +  ", " 
			+ Util.f[decimals].format(coords.y / unit.value()) + " " + unit.name();
	}

	
	
	/** The Constant X. */
	public static final int X = 0;
	
	/** The Constant Y. */
	public static final int Y = 1;
}

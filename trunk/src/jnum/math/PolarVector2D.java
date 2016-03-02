/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

//package crush.util;

import java.text.NumberFormat;

import jnum.ExtraMath;


// TODO: Auto-generated Javadoc
//Add parsing

/**
 * The Class PolarVector2D.
 */
public class PolarVector2D extends Coordinate2D implements Scalable, Inversion, Normalizable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6615579007848120214L;

	/**
	 * Instantiates a new polar vector2 d.
	 */
	public PolarVector2D() { super(); }

	/**
	 * Instantiates a new polar vector2 d.
	 *
	 * @param r the r
	 * @param phi the phi
	 */
	public PolarVector2D(double r, double phi) { super(r, phi); }

	/**
	 * Instantiates a new polar vector2 d.
	 *
	 * @param template the template
	 */
	public PolarVector2D(PolarVector2D template) { super(template); }

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#isNull()
	 */
	@Override
	public boolean isNull() { return x() == 0.0; }

	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	@Override
	public final void scale(double value) { scaleX(value); }

	/**
	 * Rotate.
	 *
	 * @param angle the angle
	 */
	public final void rotate(double angle) { addY(angle); }

	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the double
	 */
	public final double dot(PolarVector2D v) { return dot(this, v); }

	/**
	 * Dot.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the double
	 */
	public static double dot(PolarVector2D v1, PolarVector2D v2) {
		if(v1.isNull() || v2.isNull()) return 0.0;
		return Math.sqrt(v1.x()*v1.x() + v2.x()*v2.x() - 2.0 * Math.abs(v1.x() * v2.x()) * Math.cos(v1.y() - v2.y())); 
	}

	/**
	 * Norm.
	 *
	 * @return the double
	 */
	public final double norm() { return x() * x(); }

	/**
	 * Length.
	 *
	 * @return the double
	 */
	public final double length() { return Math.abs(x()); }

	/**
	 * Angle.
	 *
	 * @return the double
	 */
	public final double angle() { return y(); }

	/**
	 * Normalize.
	 *
	 * @throws IllegalStateException the illegal state exception
	 */
	@Override
	public final void normalize() throws IllegalStateException {
		if(x() == 0.0) throw new IllegalStateException("Null Vector");
		setX(1.0);
	}

	/**
	 * Normalized.
	 *
	 * @param v the v
	 * @return the polar vector2 d
	 */
	public static PolarVector2D normalized(PolarVector2D v) {
		PolarVector2D n = new PolarVector2D(v);
		n.normalize();
		return n;
	}

	/**
	 * Invert.
	 */
	@Override
	public final void invert() { addY(Math.PI); }

	/**
	 * Project.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the polar vector2 d
	 */
	public static PolarVector2D project(PolarVector2D v1, PolarVector2D v2) {
		PolarVector2D v = new PolarVector2D(v1);
		v.setX(dot(v1,v2) / v2.x());
		v.setY(v2.y());
		return v;
	}

	/**
	 * Project on.
	 *
	 * @param v the v
	 */
	public final void projectOn(PolarVector2D v) {
		setX(dot(v) / v.x());
		setY(v.y());
	}

	/**
	 * Reflect.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the polar vector2 d
	 */
	public static PolarVector2D reflect(PolarVector2D v1, PolarVector2D v2) {
		PolarVector2D v = new PolarVector2D(v1);
		v.setX(v1.x());
		v.setY(2.0 * v2.y() - v1.y());
		return v;
	}

	/**
	 * Reflect on.
	 *
	 * @param v the v
	 */
	public final void reflectOn(PolarVector2D v) {
		setY(2*v.y() - y());
	}
	
	/**
	 * Sets the cartesian.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public final void setCartesian(final double x, final double y) {
		set(ExtraMath.hypot(x, y), Math.atan2(y, x));
	}

	/**
	 * Cartesian.
	 *
	 * @return the vector2 d
	 */
	public final Vector2D cartesian() { 
		Vector2D v = new Vector2D();
		v.setX(x() * Math.cos(y()));
		v.setY(x() * Math.sin(y()));
		return v;
	}

	/**
	 * Math.
	 *
	 * @param op the op
	 * @param b the b
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void math(char op, double b) throws IllegalArgumentException {
		switch(op) {
		case '*': scaleX(b); break;
		case '/': scaleX(1.0 / b); break;
		case '^': setX(Math.pow(x(), b)); scaleY(b); break;
		default: throw new IllegalArgumentException("Illegal Operation: "+ op);
		}
	}

	/**
	 * Math.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the polar vector2 d
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static PolarVector2D math(PolarVector2D a, char op, double b) throws IllegalArgumentException {
		PolarVector2D result = new PolarVector2D();

		switch(op) {
		case '*': result.setX(a.x() * b); result.setY(a.y()); break;
		case '/': result.setX(a.x() / b); result.setY(a.y()); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);
		}

		return result;
	}

	/**
	 * To string.
	 *
	 * @param nf the nf
	 * @return the string
	 */
	@Override
	public String toString(NumberFormat nf) { return "(" + nf.format(x()) + " cis " + nf.format(y()) + ")"; }

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#toString()
	 */
	@Override
	public String toString() { return "(" + x() + " cis " + y() + ")"; }

}

/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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

	
    @Override
    public PolarVector2D copy() {
        return (PolarVector2D) super.copy();
    }
	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#isNull()
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
	public final double dot(PolarVector2D v) { 
	    if(isNull()) return 0.0;
	    if(v.isNull()) return 0.0;
        return Math.sqrt(x()*x() + v.x()*v.x() - 2.0 * Math.abs(x() * v.x()) * Math.cos(y() - v.y()));    
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
	 * Invert.
	 */
	@Override
	public final void invert() { addY(Math.PI); }

	
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

	
    public static PolarVector2D[] copyOf(PolarVector2D[] array) {
        PolarVector2D[] copy = new PolarVector2D[array.length];
        for(int i=array.length; --i >= 0; ) copy[i] = array[i].copy();
        return copy;
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
	 * @see jnum.Coordinate2D#toString()
	 */
	@Override
	public String toString() { return "(" + x() + " cis " + y() + ")"; }

}

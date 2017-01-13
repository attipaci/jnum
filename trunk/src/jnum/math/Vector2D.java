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

import jnum.ExtraMath;

// TODO: Auto-generated Javadoc
//Add parsing

/**
 * The Class Vector2D.
 */
public class Vector2D extends Coordinate2D implements Metric<Vector2D>, LinearAlgebra<Vector2D>, Inversion, Normalizable, AbsoluteValue {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7319941007342696348L;

	/**
	 * Instantiates a new 2D vector.
	 */
	public Vector2D() {}

	/**
	 * Instantiates a new 2D vector.
	 *
	 * @param text the text
	 */
	public Vector2D(String text) { parse(text); }

	/**
	 * Instantiates a new 2D vector.
	 *
	 * @param X the x
	 * @param Y the y
	 */
	public Vector2D(double X, double Y) { super(X, Y); }

	/**
	 * Instantiates a new 2D vector.
	 *
	 * @param template the template
	 */
	public Vector2D(Vector2D template) { super(template); }

	/**
	 * Instantiates a new 2D vector.
	 *
	 * @param point the point
	 */
	public Vector2D(Point2D point) { super(point); }
	
	
	/**
	 * Absolute value (radius) of the complex number. Same as {@link jnum.math.Vector2D#length()}.
	 *
	 * @return the absolute
	 * @see jnum.math.Vector2D#length()
	 */
	@Override
	public final double abs() { return length(); }


	
	/**
	 * Sets the sum.
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public final void setSum(final Vector2D a, final Vector2D b) {
		set(a.x() + b.x(), a.y() + b.y());		
	}
	
	/**
	 * Sets the difference.
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public final void setDifference(final Vector2D a, final Vector2D b) {
		set(a.x() - b.x(), a.y() - b.y());		
	}
	
	/**
	 * Sets the.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 */
	public void set(final Vector2D a, final char op, final Vector2D b) {
		switch(op) {
		case '+' : setSum(a, b); break;
		case '-' : setDifference(a, b); break;
		default: throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " operation: '" + op + "'.");
		}
		
	}
	
	/**
	 * Sum.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the vector2 d
	 */
	public static Vector2D sum(Vector2D a, Vector2D b) {
		return new Vector2D(a.x() + b.x(), a.y() + b.y());
	}
	
	/**
	 * Difference.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the vector2 d
	 */
	public static Vector2D difference(final Vector2D a, final Vector2D b) {
		return new Vector2D(a.x() - b.x(), a.y() - b.y());
	}
	
	/**
	 * Adds the.
	 *
	 * @param v the v
	 */
	@Override
	public final void add(final Vector2D v) { addX(v.x()); addY(v.y()); }
	
	/**
	 * Subtract.
	 *
	 * @param v the v
	 */
	@Override
	public final void subtract(final Vector2D v) { subtractX(v.x()); subtractY(v.y()); }
	
	/**
	 * Adds the multiple of.
	 *
	 * @param vector the vector
	 * @param factor the factor
	 */
	@Override
	public final void addScaled(final Vector2D vector, final double factor) {
		addX(factor * vector.x());
		addY(factor * vector.y());
	}
	
	/**
	 * Sets the multiple of.
	 *
	 * @param v the v
	 * @param factor the factor
	 */
	public final void setMultipleOf(final Vector2D v, final double factor) {
		set(factor * v.x(), factor * v.y());
	}
	

	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	@Override
	public final void scale(final double value) { scaleX(value); scaleY(value); }    

	/**
	 * Rotate.
	 *
	 * @param alpha the alpha
	 */
	public final void rotate(final double alpha) {
		final double sinA = Math.sin(alpha);
		final double cosA = Math.cos(alpha);
		set(x() * cosA - y() * sinA, x() * sinA + y() * cosA);
	}
	
	/**
	 * Rotate.
	 *
	 * @param theta the theta
	 */
	public final void rotate(Angle theta) {
	    set(x() * theta.cos() - y() * theta.sin(), x() * theta.sin() + y() * theta.cos());
	}
	
	/**
	 * Derotate.
	 *
	 * @param theta the theta
	 */
	public final void derotate(Angle theta) {
        set(x() * theta.cos() + y() * theta.sin(), y() * theta.cos() - x() * theta.sin());
    }
	
	/**
	 * Sets the polar.
	 *
	 * @param r the r
	 * @param angle the angle
	 */
	public void setPolar(double r, double angle) {
		set(r * Math.cos(angle), r * Math.sin(angle));
	}
	
	/**
	 * Sets the unit vector at.
	 *
	 * @param angle the new unit vector at
	 */
	public void setUnitVectorAt(double angle) {
		set(Math.cos(angle), Math.sin(angle));
	}

	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the double
	 */
	public final double dot(Vector2D v) { return dot(this, v); }

	/**
	 * Dot.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the double
	 */
	public static double dot(Vector2D v1, Vector2D v2) {
		return v1.x() * v2.x() + v1.y() * v2.y();
	}

	/**
	 * Norm.
	 *
	 * @return the double
	 */
	@Override
	public final double asquare() { return x() * x() + y() * y(); }

	/**
	 * Length.
	 *
	 * @return the double
	 */
	public final double length() { return ExtraMath.hypot(x(), y()); }

	/**
	 * Angle.
	 *
	 * @return the double
	 */
	public final double angle() {
		if(isNull()) return Double.NaN;
		return Math.atan2(y(), x());
	}
	
	/**
	 * Cos angle.
	 *
	 * @return the double
	 */
	public final double cosAngle() {
		return ExtraMath.cos(x(), y());
	}
	
	/**
	 * Sin angle.
	 *
	 * @return the double
	 */
	public final double sinAngle() {
		return ExtraMath.sin(x(), y());
	}
	
	/**
	 * Tan angle.
	 *
	 * @return the double
	 */
	public final double tanAngle() {
		return ExtraMath.tan(x(), y());
	}
	
	/**
	 * Normalize.
	 *
	 * @throws IllegalStateException the illegal state exception
	 */
	@Override
	public final void normalize() throws IllegalStateException { 
		if(isNull()) throw new IllegalStateException("Null Vector");
		scale(1.0 / asquare()); 
	}

	/**
	 * Normalized.
	 *
	 * @param v the v
	 * @return the vector2 d
	 */
	public final Vector2D normalized(Vector2D v) {
		Vector2D n = new Vector2D(v);
		n.normalize();
		return n;
	}

	/**
	 * Invert.
	 */
	@Override
	public final void invert() { scale(-1.0); }	

	/**
	 * Inverse of.
	 *
	 * @param v the v
	 * @return the vector2 d
	 */
	public static Vector2D inverseOf(Vector2D v) { return new Vector2D(-v.x(), -v.y()); }

	/**
	 * Project.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the vector2 d
	 */
	public static Vector2D project(final Vector2D v1, final Vector2D v2) {
		Vector2D v = new Vector2D(v1);
		double alpha = v2.angle();
		v.rotate(-alpha);
		v.setY(0.0);
		v.rotate(alpha);
		return v;
	}

	/**
	 * Project on.
	 *
	 * @param v the v
	 */
	public final void projectOn(final Vector2D v) {
		double alpha = v.angle();
		rotate(-alpha);
		setY(0.0);
		rotate(alpha);
	}

	/**
	 * Reflect.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the vector2 d
	 */
	public static Vector2D reflect(final Vector2D v1, final Vector2D v2) {
		Vector2D v = new Vector2D(v1);
		double alpha = v2.angle();
		v.rotate(-alpha);
		v.scaleY(-1.0);
		v.rotate(alpha);
		return v;
	}

	/**
	 * Reflect on.
	 *
	 * @param v the v
	 */
	public final void reflectOn(final Vector2D v) {
		double alpha = v.angle();
		rotate(-alpha);
		scaleY(-1.0);
		rotate(alpha);
	}


	/**
	 * Polar.
	 *
	 * @return the polar vector2 d
	 */
	public final PolarVector2D polar() {
		PolarVector2D p = new PolarVector2D();
		p.set(length(), angle());
		return p;
	}


	/**
	 * Math.
	 *
	 * @param op the op
	 * @param v the v
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void math(char op, Vector2D v) throws IllegalArgumentException {
		switch(op) {
		case '+': add(v); break;
		case '-': subtract(v); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);
		}
	}


	/**
	 * Math.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the vector2 d
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Vector2D math(Vector2D a, char op, Vector2D b) throws IllegalArgumentException {
		final Vector2D result = (Vector2D) a.clone();
		result.math(op, b);
		return result;
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
		case '/': scaleX(1.0/b); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);	    
		}
	}


	/**
	 * Math.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the vector2 d
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Vector2D math(Vector2D a, char op, double b) throws IllegalArgumentException {
		final Vector2D result = (Vector2D) a.clone();
		result.math(op, b);
		return result;
	}
	

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#getValue(int)
	 */
	@Override
	public double getValue(int field) throws NoSuchFieldException {
		switch(field) {
		case LENGTH: return length();
		case NORM: return asquare();
		case ANGLE: return angle();
		default: return super.getValue(field);
		}
	}
	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#setValue(int, double)
	 */
	@Override
	public void setValue(int field, double value) throws NoSuchFieldException {
		switch(field) {
		case LENGTH: scale(value/length()); break;
		case NORM: scale(Math.sqrt(value/asquare())); break;
		case ANGLE: rotate(value - angle()); break;
		default: super.setValue(field, value);
		}
	}

	/* (non-Javadoc)
	 * @see jnum.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public final double distanceTo(Vector2D point) {
		return ExtraMath.hypot(point.x() - x(), point.y() - y());
	}
	
	/**
	 * Creates the array.
	 *
	 * @param size the size
	 * @return the vector2 d[]
	 */
	public static Vector2D[] createArray(int size) {
		Vector2D[] v = new Vector2D[size];
		for(int i=size; --i >= 0; ) v[i] = new Vector2D();
		return v;
	}
	

	/** The Constant LENGTH. */
	public static final int LENGTH = 2;
	
	/** The Constant NORM. */
	public static final int NORM = 3;
	
	/** The Constant ANGLE. */
	public static final int ANGLE = 4;
	
	/** The Constant NaN. */
	public static final Vector2D NaN = new Vector2D(Double.NaN, Double.NaN);


	
}

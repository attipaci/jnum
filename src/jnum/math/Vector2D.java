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
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

import java.awt.geom.Point2D;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;


/**
 * The Class Vector2D.
 */
public class Vector2D extends Coordinate2D implements TrueVector<Double> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7319941007342696348L;

	/**
	 * Instantiates a new 2D vector.
	 */
	public Vector2D() {}

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
     * Instantiates a new 2D vector.
     *
     * @param text the text
     */
    public Vector2D(String text) { super(text); }
	    
    @Override
    public Vector2D copy() {
        return (Vector2D) super.copy();
    }
   
    
    /**
     * Length.
     *
     * @return the double
     */
    public final double length() { return ExtraMath.hypot(x(), y()); }

	
	/**
	 * Absolute value (radius) of the complex number. Same as {@link jnum.math.Vector2D#length()}.
	 *
	 * @return the absolute
	 * @see jnum.math.Vector2D#length()
	 */
	@Override
	public final double abs() { return length(); }
	
	   /**
     * Norm.
     *
     * @return the double
     */
    @Override
    public final double absSquared() { return x() * x() + y() * y(); }

 
    /**
     * Angle.
     *
     * @return the double
     */
    public final double angle() {
        if(isNull()) return Double.NaN;
        return Math.atan2(y(), x());
    }
    

	public final PolarVector2D polar() { return new PolarVector2D(length(), angle()); }
	
	
	
	
		
	/**
	 * Adds the.
	 *
	 * @param v the v
	 */
	@Override
	public final void add(final TrueVector<? extends Double> v) { addX(v.x()); addY(v.y()); }
	
	/**
	 * Subtract.
	 *
	 * @param v the v
	 */
	@Override
	public final void subtract(final TrueVector<? extends Double> v) { subtractX(v.x()); subtractY(v.y()); }
	
	/**
	 * Adds the multiple of.
	 *
	 * @param vector the vector
	 * @param factor the factor
	 */
	@Override
	public final void addScaled(final TrueVector<? extends Double> vector, final double factor) {
		addX(factor * vector.x());
		addY(factor * vector.y());
	}
	
	/**
	 * Sets the multiple of.
	 *
	 * @param v the v
	 * @param factor the factor
	 */
	public final void setMultipleOf(final TrueVector<? extends Double> v, final double factor) {
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
     * Sets the sum.
     *
     * @param a the a
     * @param b the b
     */
    @Override
    public final void setSum(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() + b.x(), a.y() + b.y());      
    }
    
    /**
     * Sets the difference.
     *
     * @param a the a
     * @param b the b
     */
    @Override
    public final void setDifference(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() - b.x(), a.y() - b.y());      
    }
    
    /**
     * Sets the.
     *
     * @param a the a
     * @param op the op
     * @param b the b
     */
    public void set(final TrueVector<? extends Double> a, final char op, final TrueVector<? extends Double> b) {
        switch(op) {
        case '+' : setSum(a, b); break;
        case '-' : setDifference(a, b); break;
        default: throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " operation: '" + op + "'.");
        }
        
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
		scale(1.0 / absSquared()); 
	}


	/**
	 * Invert.
	 */
	@Override
	public final void invert() { scale(-1.0); }	

	
	@Override
	public void reflectOn(final TrueVector<? extends Double> v) {
	    Vector2D ortho = copy();
	    ortho.orthogonalizeTo(v);
	    addScaled(ortho, -2.0);        
	}
	
	@Override
	public final void projectOn(final TrueVector<? extends Double> v) {
	    double scaling = dot(v) / v.abs();
	    copy(v);
	    scale(scaling);
	}
	

	/**
	 * Math.
	 *
	 * @param op the op
	 * @param v the v
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void math(char op, TrueVector<? extends Double> v) throws IllegalArgumentException {
		switch(op) {
		case '+': add(v); break;
		case '-': subtract(v); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);
		}
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


	

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#getValue(int)
	 */
	@Override
	public double getValue(int field) throws NoSuchFieldException {
		switch(field) {
		case LENGTH: return length();
		case NORM: return absSquared();
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
		case NORM: scale(Math.sqrt(value/absSquared())); break;
		case ANGLE: rotate(value - angle()); break;
		default: super.setValue(field, value);
		}
	}
	

    @Override
    public AbstractMatrix<Double> asRowVector() { 
        return new Matrix(new double[][] {{ x(), y() }});
    }
    

    @Override
    public AbstractMatrix<Double> asColumnVector() {
        return new Matrix(new double[][] { {x()}, {y()} });
    }
    
  
    @Override
    public final Double dot(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 2) throw new NonConformingException("dot product with vector of different size.");
        return x() * v.getComponent(0) + y() * v.getComponent(1);
    }

    

    @Override
    public void orthogonalizeTo(TrueVector<? extends Double> v) {
        addScaled(v, -dot(v) / (abs() * v.abs()));
    }


	@Override
	public final double distanceTo(final TrueVector<? extends Double> point) {
		return ExtraMath.hypot(point.x() - x(), point.y() - y());
	}
	
	
	
	public final static Vector2D sumOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector2D(a.x() + b.x(), a.y() + b.y());
    }
    

    public final static Vector2D differenceOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector2D(a.x() - b.x(), a.y() - b.y());
    }


	public static Vector2D[] createArray(int size) {
		final Vector2D[] v = new Vector2D[size];
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

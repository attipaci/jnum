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
import java.util.stream.IntStream;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;


/**
 * A 2D Cartesian vector, with <i>x</i>, and <i>y</i> components.
 * 
 * @author Attila Kovacs
 *
 */
public class Vector2D extends Coordinate2D implements MathVector<Double> {

    /** */
    private static final long serialVersionUID = 7319941007342696348L;

    /**
     * Instantiates a new 2D vector with zero coordinate components.
     */
    public Vector2D() {}

    /**
     * Instantiates a new 2D vector with the specified coorinate components
     * 
     * @param X     the <i>x</i>-type coordinate.
     * @param Y     the <i>y</i>-type coordinate.
     */
    public Vector2D(double X, double Y) { super(X, Y); }

    /**
     * Instantiates a new 2D vector with the components of the specified coordinates. The
     * spplied coordinates need not have be 2-dimensional, and may reside in a space with
     * lower or higher dimensionality. Only up to the first two components of the input 
     * vector are used to define the new 2D vector.
     * 
     * @param template      the coordinates that define the components the new vector.
     */
    public Vector2D(Coordinates<Double> template) { super(template); }

    /**
     * Instantiates a new 2D vector with the components of a Java AWT {@link Point2D} object.
     * 
     * @param point     the AWT point that defines the new vector.
     */
    public Vector2D(Point2D point) { super(point); }

    /**
     * Instantiates a new 2D vector from a string representation of it.
     * 
     * @param text              the string representation of a 2D vector, usually a pair of comma-separated
     *                          values, possibly bracketed.
     * @throws NumberFormatException    if the argument does not seem to begin with a 2D vector representation.
     */
    public Vector2D(String text) throws NumberFormatException { super(text); }

    @Override
    public Vector2D copy() {
        return (Vector2D) super.copy();
    }
    

    /**
     * The length of the vector. Same as {@link #abs()}.
     * 
     * @return  the length of the vector.
     * 
     * @see #abs()
     * @see #lengthSquared()
     * @see #angle()
     */
    public final double length() { return abs(); }

    /**
     * The squared length of the vector. Same as {@link #squareNorm()}.
     * 
     * @return  the length squared.
     * 
     * @see #squareNorm()
     * @see #length()
     */
    public final double lengthSquared() { return squareNorm(); }
    
    /**
     * Absolute value (radius) of the complex number. Same as {@link #length()}.
     *
     * @return the absolute value (i.e. length) of the vector.
     * 
     * @see #length()
     */
    @Override
    public final double abs() { return Math.sqrt(squareNorm()); }

    @Override
    public final double squareNorm() { return x() * x() + y() * y(); }

    /**
     * Returns the angle of this vector, measured counter-clockwise from the <i>x</i> axis.
     * 
     * @return      (rad) the angle of the vector, counter-clockwise from the <i>x</i> axis.
     * 
     * @see #length()
     */
    public final double angle() {
        if(isNull()) return Double.NaN;
        return Math.atan2(y(), x());
    }

    /**
     * Returns this vector as a polar vector, of (<i>r</i>, &theta;).
     * 
     * @return  The polar vector representation of the same vector.
     * 
     * @see #length()
     * @see #angle()
     * @see #setPolar(double, double)
     * @see PolarVector2D#cartesian()
     */
    public final PolarVector2D polar() { return new PolarVector2D(length(), angle()); }
   

    @Override
    public final void add(final MathVector<? extends Double> v) { addX(v.x()); addY(v.y()); }

    @Override
    public final void subtract(final MathVector<? extends Double> v) { subtractX(v.x()); subtractY(v.y()); }


    @Override
    public final void addScaled(final MathVector<? extends Double> vector, final double factor) {
        addX(factor * vector.x());
        addY(factor * vector.y());
    }

    /**
     * Sets this vector to be a scaled version of another 2D vector.
     * 
     * @param v         the vector that defines this one.
     * @param factor    the scaling factor
     */
    public final void setMultipleOf(final MathVector<? extends Double> v, final double factor) {
        set(factor * v.x(), factor * v.y());
    }

    @Override
    public void scale(final double value) { scaleX(value); scaleY(value); }    

    /**
     * Rotates this vector counter-clockwise by the specified angle.
     * 
     * @param alpha     (rad) the counter-clockwise rotation angle.
     * 
     * @see #rotate(Angle)
     * @see #derotate(Angle)
     */
    public final void rotate(final double alpha) {
        final double sinA = Math.sin(alpha);
        final double cosA = Math.cos(alpha);
        set(x() * cosA - y() * sinA, x() * sinA + y() * cosA);
    }

    /**
     * Rotates this vector counter-clockwise by the specified angle. This can be more efficient
     * than {@link #rotate(double)}, because the angle has readily available sine and cosine
     * terms, which need not be calculated here.
     * 
     * @param theta     the counter-clockwise rotation angle.
     * 
     * @see #derotate(Angle)
     * @see #rotate(double)
     */
    public final void rotate(Angle theta) {
        set(x() * theta.cos() - y() * theta.sin(), x() * theta.sin() + y() * theta.cos());
    }

    /**
     * Derotates this vector by the specified angle, that is it rotates this vector clockwise 
     * by the specified angle. This can be more efficient than {@link #rotate(double)}, because 
     * the angle has readily available sine and cosine terms, which need not be calculated here.
     * 
     * @param theta     the clockwise rotation angle.
     * 
     * @see #rotate(Angle)
     * @see #rotate(double)
     */
    public final void derotate(Angle theta) {
        set(x() * theta.cos() + y() * theta.sin(), y() * theta.cos() - x() * theta.sin());
    }


    @Override
    public final void setSum(final MathVector<? extends Double> a, final MathVector<? extends Double> b) {
        set(a.x() + b.x(), a.y() + b.y());      
    }

    @Override
    public final void setDifference(final MathVector<? extends Double> a, final MathVector<? extends Double> b) {
        set(a.x() - b.x(), a.y() - b.y());      
    }


    /**
     * Sets this vector to the same location as the specified polar components
     * 
     * @param r         the length (or radius) of the vector
     * @param angle     (rad) the position angle of the vector, measured counter-clockwise from the <i>x</i> axis.
     * 
     * @see #polar()
     * @see #setUnitVectorAt(double)
     */
    public void setPolar(double r, double angle) {
        set(r * Math.cos(angle), r * Math.sin(angle));
    }

    /**
     * Sets this vector to be a unit-length vector in the specified direction.
     * 
     * @param angle     (rad) the position angle of the vector, measured counter-clockwise from the <i>x</i> axis.
     * 
     * @see #setPolar(double, double)
     */
    public void setUnitVectorAt(double angle) {
        set(Math.cos(angle), Math.sin(angle));
    }

    /**
     * Returns the cosine of this vector's position angle. This is much faster than calling
     * {@link Math#cos(double)} with the return value of {@link #angle()}, since it uses
     * a purely arithmetic calculation without any calls to trigonometric functions. 
     * 
     * @return  the cosine of this vectors position angle, calculated fast, arithmetically.
     * 
     * @see #sinAngle()
     * @see #tanAngle()
     * @see #angle()
     */
    public final double cosAngle() {
        return ExtraMath.cos(x(), y());
    }

    /**
     * Returns the sine of this vector's position angle. This is much faster than calling
     * {@link Math#sin(double)} with the return value of {@link #angle()}, since it uses
     * a purely arithmetic calculation without any calls to trigonometric functions. 
     * 
     * @return  the sine of this vectors position angle, calculated fast, arithmetically.
     * 
     * @see #cosAngle()
     * @see #tanAngle()
     * @see #angle()
     */
    public final double sinAngle() {
        return ExtraMath.sin(x(), y());
    }

    /**
     * Returns the tangent of this vector's position angle. This is much faster than calling
     * {@link Math#tan(double)} with the return value of {@link #angle()}, since it uses
     * a purely arithmetic calculation without any calls to trigonometric functions. 
     * 
     * @return  the tangent of this vectors position angle, calculated fast, arithmetically.
     * 
     * @see #sinAngle()
     * @see #cosAngle()
     * @see #angle()
     */
    public final double tanAngle() {
        return ExtraMath.tan(x(), y());
    }

    @Override
    public final double normalize() throws IllegalStateException { 
        if(isNull()) throw new IllegalStateException("Null Vector");
        double l = length();
        scale(1.0 / l);
        return l;
    }

    @Override
    public void reflectOn(final MathVector<? extends Double> v) {
        Vector2D ortho = copy();
        ortho.orthogonalizeTo(v);
        addScaled(ortho, -2.0);        
    }

    @Override
    public final void projectOn(final MathVector<? extends Double> v) {
        double scaling = dot(v) / v.abs();
        copy(v);
        scale(scaling);
    }

    @Override
    public void incrementValue(int idx, Double increment) {
        switch(idx) {
        case X: addX(increment); break;
        case Y: addY(increment); break;
        }
    }
    

    @Override
    public AbstractMatrix<Double> asRowVector() { 
        return new Matrix(new double[][] {{ x(), y() }});
    }
    
    @Override
    public final void multiplyByComponentsOf(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 2) throw new NonConformingException("dot product with vector of different size.");
        scaleX(v.x());
        scaleY(v.y());
    }


    @Override
    public final Double dot(MathVector<? extends Double> v) throws NonConformingException {
        double sum = 0.0;
        
        switch(Math.min(v.size(), 2)) {
        case 2: sum += y() * v.getComponent(Y);
        case 1: sum += x() * v.getComponent(X);
        }
        return sum;
    }
    
    
    @Override
    public final Double dot(Double[] v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 2)) {
        case 2: sum += y() * v[Y];
        case 1: sum += x() * v[X];
        }
        return sum;
    }

    
    @Override
    public final Double dot(double... v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 2)) {
        case 2: sum += y() * v[Y];
        case 1: sum += x() * v[X];
        }
        return sum;
    }
    
    @Override
    public final Double dot(float... v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 2)) {
        case 2: sum += y() * v[Y];
        case 1: sum += x() * v[X];
        }
        return sum;
    }
    

    @Override
    public void orthogonalizeTo(MathVector<? extends Double> v) {
        addScaled(v, -dot(v) / (abs() * v.abs()));
    }


    @Override
    public final double distanceTo(final MathVector<? extends Double> point) {
        return ExtraMath.hypot(point.x() - x(), point.y() - y());
    }


    @Override
    public void fill(Double value) {
        setX(value);
        setY(value);
    }

    @Override
    public void setValues(Double... values) {
        IntStream.range(0, values.length).parallel().forEach(i -> setComponent(i, values[i]));
    }

    /**
     * Returns a new 2D vector that is the sum of two 2D vectors
     * 
     * @param a     one of the 2D vector
     * @param b     the other 2D vector
     * @return      a new 2D vector that is the sum of the arguments.
     * 
     * @see #differenceOf(MathVector, MathVector)
     */
    public static final Vector2D sumOf(final MathVector<Double> a, final MathVector<Double> b) {
        return new Vector2D(a.x() + b.x(), a.y() + b.y());
    }

    /**
     * Returns a new 2D vector that is the difference of two 2D vectors
     * 
     * @param a     the base 2D vector
     * @param b     the 2D vector that is subtracted from the base
     * @return      a new 2D vector that is the difference of the arguments.
     * 
     * @see #sumOf(MathVector, MathVector)
     */
    public static final Vector2D differenceOf(final MathVector<Double> a, final MathVector<Double> b) {
        return new Vector2D(a.x() - b.x(), a.y() - b.y());
    }

    /**
     * Creates an array of 2D vectors, with each element initialized to a default (zero) vector
     * intance.
     * 
     * @param size  the array size
     * @return      a new array of the desired size with all elements initialized to 2D vectors
     *              with zero components.
     */
    public static Vector2D[] createArray(int size) {
        final Vector2D[] v = new Vector2D[size];
        IntStream.range(0,  v.length).parallel().forEach(i -> v[i] = new Vector2D()); 
        return v;
    }

    /**
     * Return a fully independent copy of an array of 2D vectors. Modifications to either
     * the original or the copy will be guaranteed to not impact the other.
     * 
     * @param array     the array of 2D vectors to copy
     * @return          an independent copy of the input array, in which all elements are themselves
     *                  copies of the original elements.
     */
    public static Vector2D[] copyOf(Vector2D[] array) {
        Vector2D[] copy = new Vector2D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
    
    /**
     * The zero vector
     * 
     */
    public static final Vector2D ZERO = new Vector2D();
   
}

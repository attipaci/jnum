/*******************************************************************************
 * Copyright (c) 2020 Attila Kovacs <attila[AT]sigmyne.com>.
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
import java.util.stream.IntStream;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;



public class Vector2D extends Coordinate2D implements TrueVector<Double> {


    private static final long serialVersionUID = 7319941007342696348L;


    public Vector2D() {}

    public Vector2D(double X, double Y) { super(X, Y); }


    public Vector2D(Coordinate2D template) { super(template); }

    public Vector2D(Point2D point) { super(point); }


    public Vector2D(String text) throws NumberFormatException { super(text); }

    @Override
    public Vector2D copy() {
        return (Vector2D) super.copy();
    }


    public final double length() { return ExtraMath.hypot(x(), y()); }

    public final double lengthSquared() { return absSquared(); }
    
    /**
     * Absolute value (radius) of the complex number. Same as {@link #length()}.
     *
     * @return the absolute value (i.e. length) of the vector.
     * 
     * @see #length()
     */
    @Override
    public final double abs() { return length(); }

    @Override
    public final double absSquared() { return x() * x() + y() * y(); }


    public final double angle() {
        if(isNull()) return Double.NaN;
        return Math.atan2(y(), x());
    }


    public final PolarVector2D polar() { return new PolarVector2D(length(), angle()); }


    @Override
    public final void add(final TrueVector<? extends Double> v) { addX(v.x()); addY(v.y()); }

    @Override
    public final void subtract(final TrueVector<? extends Double> v) { subtractX(v.x()); subtractY(v.y()); }


    @Override
    public final void addScaled(final TrueVector<? extends Double> vector, final double factor) {
        addX(factor * vector.x());
        addY(factor * vector.y());
    }

    public final void setMultipleOf(final TrueVector<? extends Double> v, final double factor) {
        set(factor * v.x(), factor * v.y());
    }

    @Override
    public void scale(final double value) { scaleX(value); scaleY(value); }    

    public final void rotate(final double alpha) {
        final double sinA = Math.sin(alpha);
        final double cosA = Math.cos(alpha);
        set(x() * cosA - y() * sinA, x() * sinA + y() * cosA);
    }

    public final void rotate(Angle theta) {
        set(x() * theta.cos() - y() * theta.sin(), x() * theta.sin() + y() * theta.cos());
    }


    public final void derotate(Angle theta) {
        set(x() * theta.cos() + y() * theta.sin(), y() * theta.cos() - x() * theta.sin());
    }


    @Override
    public final void setSum(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() + b.x(), a.y() + b.y());      
    }

    @Override
    public final void setDifference(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() - b.x(), a.y() - b.y());      
    }

    public void set(final TrueVector<? extends Double> a, final char op, final TrueVector<? extends Double> b) {
        switch(op) {
        case '+' : setSum(a, b); break;
        case '-' : setDifference(a, b); break;
        default: throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " operation: '" + op + "'.");
        }

    }

    public void setPolar(double r, double angle) {
        set(r * Math.cos(angle), r * Math.sin(angle));
    }


    public void setUnitVectorAt(double angle) {
        set(Math.cos(angle), Math.sin(angle));
    }


    public final double cosAngle() {
        return ExtraMath.cos(x(), y());
    }

    public final double sinAngle() {
        return ExtraMath.sin(x(), y());
    }

    public final double tanAngle() {
        return ExtraMath.tan(x(), y());
    }

    @Override
    public final void normalize() throws IllegalStateException { 
        if(isNull()) throw new IllegalStateException("Null Vector");
        scale(1.0 / absSquared()); 
    }

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

    public void math(char op, TrueVector<? extends Double> v) throws IllegalArgumentException {
        switch(op) {
        case '+': add(v); break;
        case '-': subtract(v); break;
        default: throw new IllegalArgumentException("Illegal Operation: " + op);
        }
    }


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
    public final void multiplyByComponentsOf(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 2) throw new NonConformingException("dot product with vector of different size.");
        scaleX(v.x());
        scaleY(v.y());
    }


    @Override
    public final Double dot(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 2) throw new NonConformingException("dot product with vector of different size.");
        return x() * v.x() + y() * v.y();
    }


    @Override
    public void orthogonalizeTo(TrueVector<? extends Double> v) {
        addScaled(v, -dot(v) / (abs() * v.abs()));
    }


    @Override
    public final double distanceTo(final TrueVector<? extends Double> point) {
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





    public final static Vector2D sumOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector2D(a.x() + b.x(), a.y() + b.y());
    }


    public final static Vector2D differenceOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector2D(a.x() - b.x(), a.y() - b.y());
    }


    public static Vector2D[] createArray(int size) {
        final Vector2D[] v = new Vector2D[size];
        IntStream.range(0,  v.length).parallel().forEach(i -> v[i] = new Vector2D()); 
        return v;
    }


    public static Vector2D[] copyOf(Vector2D[] array) {
        Vector2D[] copy = new Vector2D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
    

    public static final int LENGTH = 2;

    public static final int NORM = 3;

    public static final int ANGLE = 4;

    public static final Vector2D NaN = new Vector2D(Double.NaN, Double.NaN);

}

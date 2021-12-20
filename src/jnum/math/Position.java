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

import jnum.Util;
import jnum.data.RealValue;
import jnum.data.index.Index1D;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;
import jnum.util.HashCode;

/**
 * A position on the real number line, essentially a 1D vector type.
 * 
 * @author Attila Kovacs
 *
 */
public class Position extends RealValue implements MathVector<Double> {

    /**
     * 
     */
    private static final long serialVersionUID = -7988260864770770923L;

    /**
     * Instantiates a new position on the real number line at the origin.
     */
    public Position() { this (0.0); }
    
    /**
     * Instantiates a new position at the specified location on the real number line.
     * 
     * @param value     the location of this position.
     */
    public Position(double value) { super(value); }
    
    @Override
    public Position clone() {
        return (Position) super.clone();
    }
    
    @Override
    public Position copy() {
        return (Position) super.copy();
    }
    
    @Override
    public Class<Double> getComponentType() {
        return Double.class;
    }
    
    @Override
    public int hashCode() {
        return HashCode.from(value());
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        if(!(o instanceof Position)) return false;
        return super.equals(o);
    }
    
    @Override
    public boolean equals(Coordinates<Double> coords, double precision) {
        if(coords == null) return false;
        
        if(coords.dimension() != dimension()) return false;
        if(!Util.equals(coords.x(), value(), precision)) return false;
        
        return true;
    }
    
    @Override
    public final int size() { return 1; }
    
    @Override
    public final Double getComponent(int index) {
        if(index == 0) return value();
        return null;
    }

    @Override
    public void setComponent(int index, Double value) {
        if(index == 0) setValue(value);
    }

    @Override
    public final Double x() { return value(); }

    @Override
    public final Double y() { return null; }

    @Override
    public final Double z() { return null; }

    @Override
    public final void copy(Coordinates<? extends Double> template) { 
        setValue(template.x()); 
    }

    @Override
    public double normalize() { 
        double old = value();
        setValue(1.0); 
        return Math.abs(old);
    }

    @Override
    public void flip() { setValue(-value()); }

    @Override
    public final double distanceTo(MathVector<? extends Double> point) {
        return Math.abs(point.x() - value());
    }

    @Override
    public final void addScaled(MathVector<? extends Double> o, double factor) {
        add(factor * o.x());
    }


    @Override
    public void add(MathVector<? extends Double> o) { add(o.x()); }

    @Override
    public void subtract(MathVector<? extends Double> o) { add(-o.x()); }

    @Override
    public void setSum(MathVector<? extends Double> a, MathVector<? extends Double> b) { setValue(a.x() + b.x()); }

    @Override
    public void setDifference(MathVector<? extends Double> a, MathVector<? extends Double> b) { setValue(a.x() - b.x()); }

    @Override
    public void multiplyByComponentsOf(Coordinates<? extends Double> v) {
        scale(v.x());
    }
    
    @Override
    public Double dot(MathVector<? extends Double> v) { return value() * v.x(); }
    
    @Override
    public Double dot(Double[] v) { return value() * v[0]; }
    
    @Override
    public Double dot(double... v) { return value() * v[0]; }
    
    @Override
    public Double dot(float... v) { return value() * v[0]; }

    @Override
    public void orthogonalizeTo(MathVector<? extends Double> v) { if(v.x() != 0.0) zero(); }

    @Override
    public void projectOn(MathVector<? extends Double> v) { if(v.getComponent(0) == 0.0) zero(); }

    @Override
    public void reflectOn(MathVector<? extends Double> v) {}


    @Override
    public final void fill(Double value) {
        setValue(value);
    }

    @Override
    public void setValues(Double... values) {
        setComponent(0, values[0]);
    }
    
    @Override
    public AbstractMatrix<Double> asRowVector() {
        return new Matrix(new double[][] {{ value() }});
    }

    @Override
    public void incrementValue(int idx, Double increment) {
        if(idx == 0) add(increment);
    }


    @Override
    public int dimension() {
        return 1;
    }

    @Override
    public Index1D getSize() {
        return new Index1D(1);
    }
    
    @Override
    public int getSize(int i) {
        if(i != 0) throw new IllegalArgumentException("there is no dimension " + i);
        return 1;
    }

    @Override
    public Double copyOf(int i) {
        if(i != 0) throw new IllegalArgumentException("there is no dimension " + i);
        return value();
    }

}

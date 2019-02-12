/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.samples;

import jnum.math.Coordinates;
import jnum.math.TrueVector;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;
import jnum.util.HashCode;

public class Offset1D implements TrueVector<Double> {
    private double x;

    public Offset1D() { this (0.0); }
    
    public Offset1D(double value) { this.x = value; }
    
    // TODO hashCode and equals and compare...
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(x);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Offset1D)) return false;
        
        Offset1D r = (Offset1D) o;
        if(x != r.x) return false;
        
        return true;
    }
    
    public double value() { return x; }
    
    public void setValue(double value) { this.x = value; } 
    
    @Override
    public final int size() { return 1; }
    
    @Override
    public final Double getComponent(int index) {
        if(index == 0) return x;
        return null;
    }

    @Override
    public void setComponent(int index, Double value) {
        if(index == 0) x = value;
    }

    @Override
    public final Double x() { return x; }

    @Override
    public final Double y() { return null; }

    @Override
    public final Double z() { return null; }

    @Override
    public final void copy(Coordinates<? extends Double> template) { x = template.x(); }

    @Override
    public final double abs() { return Math.abs(x); }

    @Override
    public final double absSquared() { return x*x; }

    @Override
    public void normalize() { x = 1.0; }

    @Override
    public void invert() { x *= -1.0; }

    @Override
    public final double distanceTo(TrueVector<? extends Double> point) {
        return Math.abs(point.x() - x);
    }

    @Override
    public final void addScaled(TrueVector<? extends Double> o, double factor) {
        x += factor * o.x();
    }

    @Override
    public void zero() { x = 0.0; }

    @Override
    public final boolean isNull() { return x == 0.0; }

    @Override
    public void scale(double factor) { x *= factor; }

    @Override
    public void add(TrueVector<? extends Double> o) { x += o.x(); }

    @Override
    public void subtract(TrueVector<? extends Double> o) { x -= o.x(); }

    @Override
    public void setSum(TrueVector<? extends Double> a, TrueVector<? extends Double> b) { x = a.x() + b.x(); }

    @Override
    public void setDifference(TrueVector<? extends Double> a, TrueVector<? extends Double> b) { x = a.x() + b.x(); }

    @Override
    public void multiplyByComponentsOf(Coordinates<? extends Double> v) {
        x *= v.x();
    }
    
    @Override
    public Double dot(Coordinates<? extends Double> v) { return x * v.x(); }

    @Override
    public void orthogonalizeTo(TrueVector<? extends Double> v) { x = 0.0; }

    @Override
    public void projectOn(TrueVector<? extends Double> v) {}

    @Override
    public void reflectOn(TrueVector<? extends Double> v) {}


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
        return new Matrix(new double[][] {{ x }});
    }

    @Override
    public AbstractMatrix<Double> asColumnVector() {
        return new Matrix(new double[][] {{ x }});
    }

   

}

/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;

public class Vector3D extends Coordinate3D implements TrueVector<Double>, Inversion { 
    /**
     * 
     */
    private static final long serialVersionUID = -6867315174438035749L;

    public Vector3D() {}
    
    public Vector3D(double x, double y, double z) { super(x, y, z); }
    
    public Vector3D(Coordinates<? extends Double> v) { super(v); } 
    
    
    public void rotateX(double angle) {
        final double s = Math.sin(angle);
        final double c = Math.cos(angle);
        set(x(), c * y() - s * z(), s * y() + c * z());
    }
    
    public void rotateY(double angle) {
        final double s = Math.sin(angle);
        final double c = Math.cos(angle);
        set(s * z() + c * x(), y(), c * z() - s * x());   
    }
    
    public void rotateZ(double angle) {
        final double s = Math.sin(angle);
        final double c = Math.cos(angle);
        set(c * x() - s * y(), s * x() + c * y(), z());
    }
    
    public double length() {
        return ExtraMath.hypot(x(), y(), z());
    }
    
    @Override
    public final double abs() { return length(); }
    
    @Override
    public final double absSquared() { return x() * x() + y() * y() + z() * z(); }
    
    public double theta() {
        return Math.atan2(z(), ExtraMath.hypot(x(),  y()));
    }
    
    public double phi() {
        return Math.atan2(y(), x());
    }
    
    public double dot(Vector3D v) {
        return x() * v.x() + y() * v.y() + z() * v.z();
    }
    
    @Override
    public double distanceTo(final TrueVector<? extends Double> v) {
        return ExtraMath.hypot(v.x() - x(), v.y() - y(), v.z() - z());
    }
    
    public void setUnitVectorAt(double theta, double phi) {
        set(Math.sin(theta), 0.0, Math.cos(theta));
        rotateZ(phi);
    }
    
    @Override
    public void add(final TrueVector<? extends Double> v) {
        set(x() + v.x(), y() + v.y(), z() + v.z());
    }
    
    @Override
    public void subtract(final TrueVector<? extends Double> v) {
        set(x() - v.x(), y() - v.y(), z() - v.z());
    }
    
    @Override
    public void addScaled(final TrueVector<? extends Double> v, final double factor) {
        set(x() + factor * v.x(), y() + factor * v.y(), z() + factor * v.z());
    }
    
    @Override
    public void scale(final double factor) {
        set(factor * x(), factor * y(), factor * z());
    }
    
    @Override
    public void setSum(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    
    @Override
    public void setDifference(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        set(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
    }
    
    @Override
    public void invert() {
        set(-x(), -y(), -z());
    }
    
    @Override
    public void normalize() {
        scale(1.0 / length());
    }
    
    @Override
    public AbstractMatrix<Double> asRowVector() { 
        return new Matrix(new double[][] {{ x(), y(), z() }});
    }
    

    @Override
    public AbstractMatrix<Double> asColumnVector() {
        return new Matrix(new double[][] { {x()}, {y()}, {z()} });
    }
    
  

    @Override
    public final Double dot(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 3) throw new NonConformingException("dot product with vector of different size.");
        return x() * v.getComponent(0) + y() * v.getComponent(1) + z() * v.getComponent(2);
    }
    
    

    public static Vector3D sumOf(TrueVector<? extends Double> a, TrueVector<? extends Double> b) {
        return new Vector3D(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    

    public static Vector3D differenceOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector3D(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
    }


    public static Vector3D[] createArray(int size) {
        Vector3D[] v = new Vector3D[size];
        for(int i=size; --i >= 0; ) v[i] = new Vector3D();
        return v;
    }
  
    
}

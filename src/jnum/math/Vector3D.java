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

import java.util.stream.IntStream;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;

public class Vector3D extends Coordinate3D implements TrueVector<Double> { 
    /**
     * 
     */
    private static final long serialVersionUID = -6867315174438035749L;

    public Vector3D() {}
    
    public Vector3D(double x, double y, double z) { super(x, y, z); }
    
    public Vector3D(Coordinates<? extends Double> v) { super(v); } 
    
    
    @Override
    public Vector3D copy() { return (Vector3D) super.copy(); }
    
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
        set(c * x() + s * y(), s * x() + c * y(), z());
    }
    
    public void rotateX(Angle angle) {
        set(x(), angle.cos() * y() - angle.sin() * z(), angle.sin() * y() + angle.cos() * z());
    }
    
    public void rotateY(Angle angle) {
        set(angle.sin() * z() + angle.cos() * x(), y(), angle.cos() * z() - angle.sin() * x());   
    }
    
    public void rotateZ(Angle angle) {
        set(angle.cos() * x() + angle.sin() * y(), angle.sin() * x() + angle.cos() * y(), z());
    }
   
   
    public void derotateX(Angle angle) {
        set(x(), angle.cos() * y() + angle.sin() * z(), -angle.sin() * y() + angle.cos() * z());
    }
    
    public void derotateY(Angle angle) {
        set(-angle.sin() * z() + angle.cos() * x(), y(), angle.cos() * z() + angle.sin() * x());   
    }
    
    public void derotateZ(Angle angle) {
        set(angle.cos() * x() - angle.sin() * y(), -angle.sin() * x() + angle.cos() * y(), z());
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
    public final void multiplyByComponents(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 3) throw new NonConformingException("dot product with vector of different size.");
        scaleX(v.x());
        scaleY(v.y());
        scaleZ(v.z());
    }
  
    @Override
    public final Double dot(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 3) throw new NonConformingException("dot product with vector of different size.");
        return x() * v.getComponent(0) + y() * v.getComponent(1) + z() * v.getComponent(2);
    }
    
     
    @Override
    public void orthogonalizeTo(TrueVector<? extends Double> v) {
        addScaled(v, -dot(v) / (abs() * v.abs()));
    }
    
    
    @Override
    public void reflectOn(final TrueVector<? extends Double> v) {
        Vector3D ortho = copy();
        ortho.orthogonalizeTo(v);
        addScaled(ortho, -2.0);        
    }
    
    @Override
    public final void projectOn(final TrueVector<? extends Double> v) {
        double scaling = dot(v) / v.abs();
        copy(v);
        scale(scaling);
    }
    
    

    @Override
    public void fill(Double value) {
        setX(value);
        setY(value);
        setZ(value);
    }

    @Override
    public void setValues(Double... values) {
        for(int i=values.length; --i >= 0; ) setComponent(i, values[i]);
    }
    

    public static Vector3D sumOf(TrueVector<? extends Double> a, TrueVector<? extends Double> b) {
        return new Vector3D(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    

    public static Vector3D differenceOf(final TrueVector<? extends Double> a, final TrueVector<? extends Double> b) {
        return new Vector3D(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
    }


    public static Vector3D[] createArray(int size) {
        Vector3D[] v = new Vector3D[size];
        IntStream.range(0,  v.length).parallel().forEach(i -> v[i] = new Vector3D());
        return v;
    }
    
    public static Vector3D[] copyOf(Vector3D[] array) {
        Vector3D[] copy = new Vector3D[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
  
    
    
}

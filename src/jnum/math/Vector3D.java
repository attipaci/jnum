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

import java.util.stream.IntStream;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.Matrix;

/**
 * A class representing a Cartesian vector in 3D space, i.e. with <i>x</i>, <i>y</i>, and <i>z</i> components.
 * 
 * @author Attila Kovacs
 *
 */
public class Vector3D extends Coordinate3D implements MathVector<Double> { 
    /**
     * 
     */
    private static final long serialVersionUID = -6867315174438035749L;

    public Vector3D() {}
    
    public Vector3D(double x, double y, double z) { super(x, y, z); }
    
    public Vector3D(SphericalCoordinates coords) {
        this();
        coords.toCartesian(this);
    }
    
    public Vector3D(Coordinates<Double> v) { super(v); } 
    
    
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
   
    /**
     * Computationally efficient implementation of 3D rotation with small angles. 
     * All angles must be much smaller than 1 for this small angle approximation to work.
     * Angles around 1 arcsecond will result in errors in about 1 part per 10<sup>12</sup>.
     * 
     * @param ax    (radian) rotation angle around X. 
     * @param ay    (radian) rotation angle around Y. 
     * @param az    (radian) rotation angle around Z. 
     */
    public void smallRotate3D(final double ax, final double ay, final double az) {
        final double fx = x(), fy = y(), fz = z();              ///< Copy of the original vector (from)
        final double Ax = ax * ax, Ay = ay * ay, Az = az * az;  ///< Squares of the rotation angles

        setX(fx - 0.5 * (Ay + Az) * fx - az * fy + ay * fz);
        setY(fy - 0.5 * (Ax + Az) * fy + az * fx - ax * fz);
        setZ(fz - 0.5 * (Ax + Ay) * fz - ay * fx + ax * fy);
    }
    
    /**
     * Computationally efficient implementation of 3D rotation with small angles.
     * 
     * @param a     (radian) 3D rotation angles around X,Y,Z. All of them must be much smaller than 1 for this small 
     *              angle approximation to work. Angles around 1 arcsecond will result in errors in about 1 part
     *              per 10<sup>12</sup>.
     */
    public void smallRotate3D(double[] a) {
        smallRotate3D(a[0], a[1], a[2]);
    }

    /**
     * Computationally efficient implementation of 3D rotation with small angles.
     * 
     * @param a     (radian) 3D rotation angles around X,Y,Z. All of them must be much smaller 1 for this small 
     *              angle approximation to work. Angles around 1 arcsecond will result in errors in about 1 part
     *              per 10<sup>12</sup>.
     */
    public void smallRotate3D(MathVector<Double> a) {
        smallRotate3D(a.x(), a.y(), a.z());
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
    public double distanceTo(final MathVector<? extends Double> v) {
        return ExtraMath.hypot(v.x() - x(), v.y() - y(), v.z() - z());
    }
    
    public void setUnitVectorAt(double theta, double phi) {
        set(Math.sin(theta), 0.0, Math.cos(theta));
        rotateZ(phi);
    }
    
    public void add(double x, double y, double z) {
        addX(x);
        addY(y);
        addZ(z);
    }
    
    @Override
    public void add(final MathVector<? extends Double> v) {
        addX(v.x());
        addY(v.y());
        addZ(v.z());
    }
    
    @Override
    public void subtract(final MathVector<? extends Double> v) {
        addX(-v.x());
        addY(-v.y());
        addZ(-v.z());
    }
    
    @Override
    public void addScaled(final MathVector<? extends Double> v, final double factor) {
        addX(factor * v.x());
        addY(factor * v.y());
        addZ(factor * v.z());
    }
    
    @Override
    public void scale(final double factor) {
        set(factor * x(), factor * y(), factor * z());
    }
  
    
    @Override
    public void setSum(final MathVector<? extends Double> a, final MathVector<? extends Double> b) {
        set(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    
    @Override
    public void setDifference(final MathVector<? extends Double> a, final MathVector<? extends Double> b) {
        set(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
    }
    
    @Override
    public void flip() {
        set(-x(), -y(), -z());
    }
    
    @Override
    public double normalize() {
        double l = length();
        scale(1.0 / l);
        return l;
    }
    
    @Override
    public AbstractMatrix<Double> asRowVector() { 
        return new Matrix(new double[][] {{ x(), y(), z() }});
    }
       
    @Override
    public final void multiplyByComponentsOf(Coordinates<? extends Double> v) throws NonConformingException {
        if(v.size() != 3) throw new NonConformingException("dot product with vector of different size.");
        scaleX(v.x());
        scaleY(v.y());
        scaleZ(v.z());
    }
  
    @Override
    public final Double dot(MathVector<? extends Double> v) {
        double sum = 0.0;
        
        switch(Math.min(v.size(), 3)) {
        case 3: sum += z() * v.getComponent(Z);
        case 2: sum += y() * v.getComponent(Y);
        case 1: sum += x() * v.getComponent(X);
        }
        return sum;
    }
    
    @Override
    public final Double dot(Double[] v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 3)) {
        case 3: sum += z() * v[Z];
        case 2: sum += y() * v[Y];
        case 1: sum += x() * v[X];
        }
        return sum;
    } 
    
    @Override
    public final Double dot(double... v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 3)) {
        case 3: sum += z() * v[Z];
        case 2: sum += y() * v[Y];
        case 1: sum += x() * v[X];
        }
        return sum;
    } 
    
    @Override
    public final Double dot(float... v) {
        double sum = 0.0;
        
        switch(Math.min(v.length, 3)) {
        case 3: sum += z() * v[Z];
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
    public void reflectOn(final MathVector<? extends Double> v) {
        Vector3D ortho = copy();
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
    public void fill(Double value) {
        setX(value);
        setY(value);
        setZ(value);
    }

    @Override
    public void incrementValue(int idx, Double increment) {
        switch(idx) {
        case X: addX(increment); break;
        case Y: addY(increment); break;
        case Z: addZ(increment); break;
        }
    }
    
    @Override
    public void setValues(Double... values) {
        for(int i=values.length; --i >= 0; ) setComponent(i, values[i]);
    }
   
    
    public static Vector3D sumOf(final MathVector<Double> a, final MathVector<Double> b) {
        return new Vector3D(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    

    public static Vector3D differenceOf(final MathVector<Double> a, final MathVector<Double> b) {
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

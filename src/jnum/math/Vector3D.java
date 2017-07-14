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

public class Vector3D extends Coordinate3D implements Metric<Vector3D>, LinearAlgebra<Vector3D>, Inversion, 
Normalizable, AbsoluteValue { 
    /**
     * 
     */
    private static final long serialVersionUID = -6867315174438035749L;

    
    
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
    public final double asquare() { return x() * x() + y() * y() + z() * z(); }
    
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
    public double distanceTo(final Vector3D v) {
        return ExtraMath.hypot(v.x() - x(), v.y() - y(), v.z() - z());
    }
    
    public void setUnitVectorAt(double theta, double phi) {
        set(Math.sin(theta), 0.0, Math.cos(theta));
        rotateZ(phi);
    }
    
    @Override
    public void add(final Vector3D v) {
        set(x() + v.x(), y() + v.y(), z() + v.z());
    }
    
    @Override
    public void subtract(final Vector3D v) {
        set(x() - v.x(), y() - v.y(), z() - v.z());
    }
    
    @Override
    public void addScaled(final Vector3D v, final double factor) {
        set(x() + factor * v.x(), y() + factor * v.y(), z() + factor * v.z());
    }
    
    @Override
    public void scale(final double factor) {
        set(factor * x(), factor * y(), factor * z());
    }
    
    @Override
    public void setSum(final Vector3D a, final Vector3D b) {
        set(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
    }
    
    @Override
    public void setDifference(final Vector3D a, final Vector3D b) {
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
  
    
}

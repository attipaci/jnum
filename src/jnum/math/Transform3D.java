/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math;

import java.io.Serializable;

import jnum.math.matrix.Matrix;


public class Transform3D<T extends SphericalCoordinates> implements Cloneable, Serializable, InverseValue<Transform3D<T>> {
    /**
     * 
     */
    private static final long serialVersionUID = 15536638108689938L;
   
	private Matrix M;
	
	public Transform3D() {
	    M = Matrix.identity(3);
	}

	@SuppressWarnings("unchecked")
    @Override
    protected Transform3D<T> clone() {
	    try { return (Transform3D<T>) super.clone(); }
	    catch(CloneNotSupportedException e) { return null; }
	}
	
	public Matrix getMatrix() { return M; }
	
	public final Transform3D<T> Rx(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ 1.0, 0.0, 0.0}, {0.0, c, -s}, {0.0, s, c}});
	    M = R.dot(M);
	    return this;
	}

	public final Transform3D<T> Ry(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ c, 0.0, s}, {0.0, 1.0, 0.0}, {-s, 0.0, c}});
	    M = R.dot(M);
	    return this;
	}

	public final Transform3D<T> Rz(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ c, -s, 0.0}, {s, c, 0.0}, {0.0, 0.0, 1.0}});
	    M = R.dot(M);
	    return this;
	}
	
	
	protected final Transform3D<T> smallRotate(double ax, double ay, double az) {
	    final double[][] cols = M.getData();
        final double Ax = ax * ax, Ay = ay * ay, Az = az * az;  ///< Squares of the rotation angles
        
        for(int i=3; --i >=0; ) {
            double[] col = cols[i];
            double x = col[0], y = col[1], z = col[2];
            col[0] = x - 0.5 * (Ay + Az) * x - az * y + ay * z;
            col[1] = y - 0.5 * (Ax + Az) * y + az * x - ax * z;
            col[2] = z - 0.5 * (Ax + Ay) * z - ay * x + ax * y;
        }
	    return this;
	}
	
	public Vector3D getTransformed(Vector3D v) {
	    Vector3D result = new Vector3D();
	    M.dot(v, result);
	    return result;
	}
	
	public final T getTransformed(final T coords) {
	    @SuppressWarnings("unchecked")
        T result = (T) coords.copy();
	    transform(result);
	    return result;	    
	}
	
	public void transform(final T coords) {	
	    coords.fromCartesian(getTransformed(coords.toCartesian()));
	}
	
	@Override
    public Transform3D<T> getInverse() {
	    Transform3D<T> I = clone();
	    I.inverse();
	    return I;
	}

    @Override
    public void inverse() {
       M = M.getInverse();
    }
}

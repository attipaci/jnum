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

import java.io.Serializable;

import jnum.math.matrix.Matrix;

/**
 * A class representing transfotmations for 3D Cartesian vectors, and shperical coordinates. This base class implements 
 * linear algebraic (matrix) transformations, powered by a square {@link Matrix} of size 3 doing all the work.
 * 
 * 
 * @author Attila Kovacs
 *
 * @param <T>       The generic type spherical coordinates on thich this transform may operate.
 */
public class Transform3D<T extends SphericalCoordinates> implements Transforming<T>, Cloneable, Serializable, InverseValue<Transform3D<T>> {
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
	
	/**
	 * Aggregates a counter clockwise around the <i>x</i> axis, as seen looking in towards the origin from the direction 
	 * of that axis. This is the usual convention for the Euler rotation about <i>x</i>. Note, however,that it has an opposite
	 * sign convention from the <i>R</i><sub>1</sub> convention used widely in astronomy and geodesy.
	 * 
	 * @param angle    (rad) Angle by which to rotate around the <i>x</i> axis.
	 * @return         itself.
	 */
	public final Transform3D<T> Rx(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ 1.0, 0.0, 0.0}, {0.0, c, -s}, {0.0, s, c}});
	    M = R.dot(M);
	    return this;
	}

	/**
     * Aggregates a counter clockwise around the <i>y</i> axis, as seen looking in towards the origin from the direction 
     * of that axis. This is the usual convention for the Euler rotation about <i>y</i>. Note, however,that it has an opposite
     * sign convention from the <i>R</i><sub>2</sub> convention used widely in astronomy and geodesy.
     * 
     * @param angle    (rad) Angle by which to rotate around the <i>y</i> axis.
     * @return         itself.
     */
	public final Transform3D<T> Ry(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ c, 0.0, s}, {0.0, 1.0, 0.0}, {-s, 0.0, c}});
	    M = R.dot(M);
	    return this;
	}

	/**
     * Aggregates a counter clockwise around the <i>z</i> axis, as seen looking in towards the origin from the direction 
     * of that axis. This is the usual convention for the Euler rotation about <i>z</i>. Note, however,that it has an opposite
     * sign convention from the <i>R</i><sub>3</sub> convention used widely in astronomy and geodesy.
     * 
     * @param angle    (rad) Angle by which to rotate around the <i>z</i> axis.
     * @return         itself.
     */
	public final Transform3D<T> Rz(double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    Matrix R = new Matrix(new double[][] {{ c, -s, 0.0}, {s, c, 0.0}, {0.0, 0.0, 1.0}});
	    M = R.dot(M);
	    return this;
	}
	
	
	/**
	 * Applies a small angle rotation about all three axes. The angles must be sufficiently tiny that the
	 * terms containing the products of two sines can be neglected without harm. I.e. to maintain close to
	 * full double precision accuracy (to at least ~10 significant figures, the argument angles should be 
	 * typically at the 1 arcsecond level or below).
	 * 
	 * @param ax   (rad) rotation angle around <i>x</i> (counter clockwise when looking in from <i>x</i>).
	 * @param ay   (rad) rotation angle around <i>y</i> (counter clockwise when looking in from <i>y</i>).
	 * @param az   (rad) rotation angle around <i>z</i> (counter clockwise when looking in from <i>z</i>).
	 * @return     itself.
	 */
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
	
	/**
	 * Gets a transformed Cartesian 3D vector
	 * 
	 * @param v    3D input Cartesian vector.
	 * @return     a new 3D Cartesian vector containing the transformed input vector.
	 */
	public Vector3D getTransformed(Vector3D v) {
	    Vector3D result = new Vector3D();
	    M.dot(v, result);
	    return result;
	}
	
	/**
	 * Gets transformed spherical coordinates of the same generic type as the input.
	 * 
	 * @param coords   The input spherical coordinates of the supported type.
	 * @return         transfomed new spherical coordinates of the same type as the input 
	 */
	public final T getTransformed(final T coords) {
	    @SuppressWarnings("unchecked")
        T result = (T) coords.copy();
	    transform(result);
	    return result;	    
	}
	

	@Override
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

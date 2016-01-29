/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// (C)2007 Attila Kovacs <attila@submm.caltech.edu>

package jnum.math;


import java.util.*;

import jnum.ExtraMath;
import jnum.data.ArrayUtil;
import jnum.util.ConvergenceException;

import java.text.*;

// TODO: Auto-generated Javadoc
//TODO Various decompositions.
// TODO implement fast multiplication?

/**
 * The Class Matrix.
 */
public class Matrix extends AbstractMatrix<Double> {
	
	/** The entry. */
	public double[][] entry; 

	/**
	 * Instantiates a new matrix.
	 */
	public Matrix() {}

	// Check for rectangular shape
	/**
	 * Instantiates a new matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public Matrix(double[][] a) throws IllegalArgumentException { 
		this();
		entry = a; 
		validate();
	}

	/**
	 * Instantiates a new matrix.
	 *
	 * @param rows the rows
	 * @param cols the cols
	 */
	public Matrix(int rows, int cols) { 
		this();
		entry = new double[rows][cols];
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getType()
	 */
	@Override
	public final Class<Double> getType() { return double.class; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#noData()
	 */
	@Override
	public void noData() { entry = null; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getData()
	 */
	@Override
	public Object getData() { return entry; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) {
		if(data instanceof double[][]) setData((double[][]) data);
		else if(data instanceof float[][]) setData((float[][]) data);
		else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + data.getClass().getSimpleName() + ".");	
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(double[][] data) {
		entry = data;
		validate();
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(float[][] data) {
		setData((double[][]) ArrayUtil.asDouble(data));		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getValue(int, int)
	 */
	@Override
	public final Double getValue(int row, int col) { return entry[row][col]; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setValue(int, int, java.lang.Object)
	 */
	@Override
	public final void setValue(int row, int col, Double v) { entry[row][col] = v; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#checkShape()
	 */
	@Override
	protected void checkShape() throws IllegalStateException {
		if(getData() == null) return;
		if(rows() == 0) return;
		int m = cols();
		for(int i=rows(); --i > 0; ) if(entry[i].length != m) throw new IllegalStateException("Matrix has non-rectangular shape!");	
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#calcProduct(kovacs.math.AbstractMatrix, kovacs.math.AbstractMatrix, boolean)
	 */
	@Override
	protected void calcProduct(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B, boolean clearFirst) {				
		if(clearFirst) zero();	
		
		for(int i=A.rows(); --i >= 0; ) for(int j=B.cols(); --j >= 0; ) for(int k=A.cols(); --k >= 0; ) 
			entry[i][j] += A.getValue(i, k) * B.getValue(k, j);
	}
	
	/**
	 * Product.
	 *
	 * @param A the a
	 * @param B the b
	 * @return the matrix
	 */
	public static Matrix product(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {
		Matrix product = new Matrix();
		product.setProduct(A, B);
		return product;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the double[]
	 */
	public double[] dot(double[] v) {
		double[] result = new double[rows()];
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(double[] v, double[] result) {
		if(v.length != cols()) throw new IllegalArgumentException("Mismatched matrix/input-vector sizes.");
		if(result.length != rows()) throw new IllegalArgumentException("Mismatched matrix/output-vector sizes.");
		Arrays.fill(result, 0.0);
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) result[i] += entry[i][j] * v[j];
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the float[]
	 */
	public float[] dot(float[] v) {
		float[] result = new float[rows()];
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(float[] v, float[] result) {
		if(v.length != cols()) throw new IllegalArgumentException("Mismatched matrix/input-vector sizes.");
		if(result.length != rows()) throw new IllegalArgumentException("Mismatched matrix/output-vector sizes.");
		Arrays.fill(result, 0.0F);
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) result[i] += entry[i][j] * v[j];
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the real vector
	 */
	public RealVector dot(RealVector v) {
		RealVector result = new RealVector(rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(RealVector v, RealVector result) {
		if(v.size() != cols()) throw new IllegalArgumentException("Mismatched matrix/input-vector sizes.");
		if(result.component == null) result.setSize(rows());
		else if(result.size() != rows()) result.setSize(rows());
		else result.zero();
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) result.component[i] += entry[i][j] * v.component[j];
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getTransposed()
	 */
	@Override
	public Matrix getTransposed() {		
		int n = rows();
		int m = cols();

		Matrix M = new Matrix(m, n);

		for(int i=n; --i >= 0; ) for(int j=m; --j >= 0; ) M.entry[j][i] = entry[i][j];

		return M;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#zero()
	 */
	@Override
	public void zero() {
		if(entry != null) for(int i=entry.length; --i >= 0; ) Arrays.fill(entry[i], 0.0);
	}
	

	

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addMultipleOf(AbstractMatrix<? extends Double> o, double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] += o.getValue(i, j) * factor;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#subtract(java.lang.Object)
	 */
	@Override
	public void subtract(AbstractMatrix<? extends Double> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] -= o.getValue(i, j);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public void add(AbstractMatrix<? extends Double> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] += o.getValue(i, j);		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Scalable#scale(double)
	 */
	@Override
	public void scale(double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] *= factor;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(AbstractMatrix<? extends Double> o) {
		double d2 = 0.0;
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			double d = entry[i][j] - o.getValue(i, j);
			d2 += d*d;
		}
		return Math.sqrt(d2);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#cols()
	 */
	@Override
	public final int cols() { return entry[0].length; }

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#rows()
	 */
	@Override
	public final int rows() { return entry.length; }

	/* (non-Javadoc)
	 * @see kovacs.math.IdentityValue#setIdentity()
	 */
	@Override
	public void setIdentity() {		
		entry = new double[][] {{ 1.0 }};		
	}

	// The b[] are vectors for which to solve for Ax = b
	// the matrix is inverted, and the solution vectors are returned in their place
	 
	// Based on Numerical Recipes in C (Press et al. 1989)
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#gaussJordan()
	 */
	@Override
	public void gaussJordan() {
		final int rows = rows();
		final int cols = cols();
		
		final int[] indxc = new int[rows];
		final int[] indxr = new int[rows];
		final int[] ipiv = new int[rows];
		
		Arrays.fill(ipiv, -1);

		for(int i=rows; --i >= 0; ) {
			int icol=-1, irow=-1;
			double big=0.0;
			for(int j=rows; --j >= 0; ) if(ipiv[j] != 0) for(int k=rows; --k >= 0; ) {
				if(ipiv[k] == -1) {
					if(Math.abs(entry[j][k]) >= big) {
						big=Math.abs(entry[j][k]);
						irow=j;
						icol=k;
					}
				} 
				else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular PrimitiveMatrix-1 during Gauss-Jordan elimination.");
			}
			++(ipiv[icol]);
			if(irow != icol) for(int l=cols; --l >= 0; ) {
				double temp = entry[irow][l];
				entry[irow][l] = entry[icol][l];
				entry[icol][l] = temp;
			}

			indxr[i]=irow;
			indxc[i]=icol;
			if(entry[icol][icol] == 0.0) throw new IllegalArgumentException("Singular PrimitiveMatrix-2 during Gauss-Jordan elimination.");
			double pivinv=1.0 / entry[icol][icol];
			entry[icol][icol] = 1.0;
			scaleRow(icol, pivinv);
			
			for(int ll=rows; --ll >= 0; ) if(ll != icol) {
				double temp=entry[ll][icol];
				entry[ll][icol] = 0.0;
				addMultipleOfRow(icol, ll, -temp);
			}
		}
		
		for(int l=rows; --l >=0; ) if(indxr[l] != indxc[l]) for(int k=rows; --k >= 0; ) {
			double temp = entry[k][indxr[l]];
			entry[k][indxr[l]] = entry[k][indxc[l]];
			entry[k][indxc[l]] = temp;
		}
	}

	
	// A = U * diag(w) * V^T
	// square A --> A^-1 = V * diag(1/w) * U^T
	// Based on Numerical Recipes in C (Press et al. 1989)
	/**
	 * Svd.
	 *
	 * @param w the w
	 * @param V the v
	 */
	protected void SVD(double[] w, Matrix V) {
		SVD(w, V, 100);
	}
	
	/**
	 * Svd.
	 *
	 * @param w the w
	 * @param V the v
	 * @param maxIterations the max iterations
	 */
	protected void SVD(double[] w, Matrix V, int maxIterations) {
		final int m = rows();
		final int n = cols();
		
		double[] rv1 = new double[n];
		double g = 0.0, scale = 0.0, anorm = 0.0, s = 0.0;
		int l = -1;
		
		for(int i=0; i<n; i++) {
			l = i+1;
			rv1[i] = scale * g;
			g = s = scale = 0.0;
			if(i < m) {
				for(int k=i; k<m; k++) scale += Math.abs(entry[k][i]);
				if(scale != 0.0) {
					for(int k=i; k<m; k++) {
						entry[k][i] /= scale;
						s += entry[k][i]*entry[k][i];
					}
					double f = entry[i][i];
					g = -Math.signum(f) * Math.sqrt(s);
					double h = f*g-s;
					entry[i][i] = f - g;
					for(int j=l; j<n; j++) {
						s = 0.0;
						for(int k=i; k<m; k++) s += entry[k][i] * entry[k][j];
						f = s / h;
						for(int k=i; k<m; k++) entry[k][j] += f * entry[k][i];
					}
					for(int k=i; k<m; k++) entry[k][i] *= scale;
				}
			}
			w[i] = scale * g;
			g = s = scale = 0.0;
			if(i < m && i != n-1) {
				for(int k=l; k<n; k++) scale += Math.abs(entry[i][k]);
				if(scale != 0.0) {
					for(int k=l; k<n; k++) {
						entry[i][k] /= scale;
						s += entry[i][k]*entry[i][k];
					}
					double f = entry[i][l];
					g = -Math.signum(f) * Math.sqrt(s);
					double h = f * g - s;
					entry[i][l] = f - g;
					for(int k=l; k<n; k++) rv1[k]=entry[i][k] / h;
					for(int j=l; j<m; j++) {
						s = 0.0;
						for(int k=l; k<n; k++) s += entry[j][k] * entry[i][k];
						for(int k=l; k<n; k++) entry[j][k] += s * rv1[k];
					}
					for(int k=l; k<n; k++) entry[i][k] *= scale;
				}
			}
			anorm = Math.max(anorm, Math.abs(w[i]) + Math.abs(rv1[i]));
		}
		
		for(int i=n; --i >= 0; ) {
			if(i < n-1) {
				if(g != 0.0) {
					for(int j=l; j<n; j++) V.entry[j][i] = (entry[i][j] / entry[i][l]) / g;
					for(int j=l; j<n; j++) {
						s = 0.0;
						for(int k=l; k<n; k++) s += entry[i][k] * V.entry[k][j];
						for(int k=l; k<n; k++) V.entry[k][j] += s * V.entry[k][i];
					}
				}
				for(int j=l; j<n; j++) V.entry[i][j] = V.entry[j][i] = 0.0;
			}
			V.entry[i][i] = 1.0;
			g = rv1[i];
			l = i;
		}
		for(int i = Math.min(m,n); --i >= 0; ) {
			l = i+1;
			g = w[i];
			for(int j=l; j<n; j++) entry[i][j] = 0.0;
			if(g != 0.0) {
				g=1.0/g;
				for(int j=l; j<n; j++) {
					s = 0.0;
					for(int k=l; k<m; k++) s += entry[k][i] * entry[k][j];
					double f=(s / entry[i][i]) * g;
					for(int k=i; k<m; k++) entry[k][j] += f * entry[k][i];
				}
				for(int j=i; j<m; j++) entry[j][i] *= g;
			} 
			else for(int j=i; j<m; j++) entry[j][i] = 0.0;
			entry[i][i]++;
		}
		for(int k=n; --k >= 0; ) {
			for(int its=1; its <= maxIterations; its++) {
				int flag = 1;
				int nm = -1;
				for(l=k; l >= 0; l--) {
					nm = l-1;
					if(Math.abs(rv1[l]) + anorm == anorm) {
						flag = 0;
						break;
					}
					if(Math.abs(w[nm]) + anorm == anorm) break;
				}
				if(flag != 0) {
					double c=0.0;
					s=1.0;
					for(int i=l; i<=k; i++) {
						double f = s * rv1[i];
						rv1[i] = c * rv1[i];
						if(Math.abs(f) + anorm == anorm) break;
						g = w[i];
						double h = ExtraMath.hypot(f,g);
						w[i] = h;
						h = 1.0 / h;
						c = g * h;
						s = -f * h;
						for(int j=m; --j >= 0; ) {
							double y = entry[j][nm];
							double z = entry[j][i];
							entry[j][nm] = y*c + z*s;
							entry[j][i] = z*c - y*s;
						}
					}
				}
				double z = w[k];
				if(l == k) {
					if(z < 0.0) {
						w[k] = -z;
						for(int j=n; --j >= 0; ) V.entry[j][k] *= -1;
					}
					break;
				}
				if(its >= maxIterations) throw new ConvergenceException("SVD", its);
				double x = w[l];
				nm = k-1;
				double y = w[nm];
				g = rv1[nm];
				double h = rv1[k];
				double f = ((y-z)*(y+z) + (g-h)*(g+h)) / (2.0 * h * y);
				g = ExtraMath.hypot(f, 1.0);
				f = ((x-z)*(x+z) + h*((y / (f + Math.signum(f)*g)) - h)) / x;
				double c = s = 1.0;
				
				for (int j=l; j<=nm; j++) {
					int i = j+1;
					g = rv1[i];
					y = w[i];
					h = s * g;
					g = c * g;
					z = ExtraMath.hypot(f,h);
					rv1[j] = z;
					c = f / z;
					s = h / z;
					f = x*c + g*s;
					g = g*c - x*s;
					h = y * s;
					y *= c;
					
					for(int jj=n; --jj >= 0; ) {
						x = V.entry[jj][j];
						z = V.entry[jj][i];
						V.entry[jj][j] = x*c + z*s;
						V.entry[jj][i] = z*c - x*s;
					}
					
					z = ExtraMath.hypot(f,h);
					w[j] = z;
					
					if(z != 0.0) {
						z = 1.0 / z;
						c = f * z;
						s = h * z;
					}
					
					f = c*g + s*y;
					x = c*y - s*g;
					
					for(int jj=m; --jj >= 0; ) {
						y=entry[jj][j];
						z=entry[jj][i];
						entry[jj][j] = y*c + z*s;
						entry[jj][i] = z*c - y*s;
					}
				}
				rv1[l] = 0.0;
				rv1[k] = f;
				w[k] = x;
			}
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getColumn(int, java.lang.Object)
	 */
	@Override
	public void getColumn(int j, Object buffer) {
		if(buffer instanceof double[]) getColumn(j, (double[]) buffer);
		else if(buffer instanceof float[]) getColumn(j, (double[]) buffer);
		else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
	}
	
	/**
	 * Gets the column.
	 *
	 * @param j the j
	 * @param buffer the buffer
	 * @return the column
	 */
	public void getColumn(int j, double[] buffer) {
		for(int i=rows(); --i >= 0; ) buffer[i] = entry[i][j];		
	}
	
	/**
	 * Gets the column.
	 *
	 * @param j the j
	 * @param buffer the buffer
	 * @return the column
	 */
	public void getColumn(int j, float[] buffer) {
		for(int i=rows(); --i >=0; ) buffer[i] = (float) entry[i][j];		
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRow(int, java.lang.Object)
	 */
	@Override
	public void getRow(int j, Object buffer) {
		if(buffer instanceof double[]) getRow(j, (double[]) buffer);
		else if(buffer instanceof float[]) getRow(j, (double[]) buffer);
		else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
	}
	
	/**
	 * Gets the row.
	 *
	 * @param i the i
	 * @param buffer the buffer
	 * @return the row
	 */
	public void getRow(int i, double[] buffer) {
		for(int j=cols(); --j >= 0; ) buffer[j] = entry[i][j];		
	}
	
	/**
	 * Gets the row.
	 *
	 * @param i the i
	 * @param buffer the buffer
	 * @return the row
	 */
	public void getRow(int i, float[] buffer) {
		for(int j=cols(); --j >= 0; ) buffer[j] = (float) entry[i][j];		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setColumn(int, java.lang.Object)
	 */
	@Override
	public void setColumn(int j, Object value) throws IllegalArgumentException {		
		if(value instanceof double[][]) setColumn(j, (double[]) value);
		else if(value instanceof float[][]) setColumn(j, (float[]) value);
		else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " column.");
	}
	
	/**
	 * Sets the column.
	 *
	 * @param j the j
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void setColumn(int j, double[] value) throws IllegalArgumentException {
		if(value.length != rows()) throw new IllegalArgumentException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
		for(int i=rows(); --i >= 0; ) entry[i][j] = value[i];		
	}
	
	/**
	 * Sets the column.
	 *
	 * @param j the j
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void setColumn(int j, float[] value) throws IllegalArgumentException {
		if(value.length != rows()) throw new IllegalArgumentException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
		for(int i=rows(); --i >= 0; ) entry[i][j] = value[i];		
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setRow(int, java.lang.Object)
	 */
	@Override
	public void setRow(int j, Object value) throws IllegalArgumentException {		
		if(value instanceof double[][]) setRow(j, (double[]) value);
		else if(value instanceof float[][]) setRow(j, (float[]) value);
		else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " row.");
	}
	
	
	/**
	 * Sets the row.
	 *
	 * @param i the i
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void setRow(int i, double[] value) throws IllegalArgumentException {
		if(value.length != cols()) throw new IllegalArgumentException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
		entry[i] = value;
	}
	
	/**
	 * Sets the row.
	 *
	 * @param i the i
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void setRow(int i, float[] value) throws IllegalArgumentException {
		if(value.length != cols()) throw new IllegalArgumentException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
		for(int j=cols(); --j >= 0; ) entry[i][j] = value[j];
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#switchRows(int, int)
	 */
	@Override
	public final void switchRows(int i, int j) {
		double[] temp = entry[i];
		entry[i] = entry[j];
		entry[j] = temp;	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#switchElements(int, int, int, int)
	 */
	@Override
	public final void switchElements(int i1, int j1, int i2, int j2) {
		double temp = entry[i1][j1];
		entry[i1][j1] = entry[i2][j2];
		entry[i2][j2] = temp;	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addMultipleOfRow(int, int, double)
	 */
	@Override
	public void addMultipleOfRow(int row, int toRow, double scaling) {
		for(int j=cols(); --j >= 0; ) entry[toRow][j] += scaling * entry[row][j];
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addMultipleOfRow(int, int, java.lang.Object)
	 */
	@Override
	public void addMultipleOfRow(int row, int toRow, Double scaling) {
		addMultipleOfRow(row, toRow, scaling.doubleValue());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addRow(int, int)
	 */
	@Override
	public void addRow(int row, int toRow) {
		for(int j=cols(); --j >= 0; ) entry[toRow][j] += entry[row][j];	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#subtractRow(int, int)
	 */
	@Override
	public void subtractRow(int row, int fromRow) {
		for(int j=cols(); --j >= 0; ) entry[fromRow][j] -= entry[row][j];		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#zeroRow(int)
	 */
	@Override
	public void zeroRow(int i) {
		Arrays.fill(entry[i], 0.0);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#scaleRow(int, double)
	 */
	@Override
	public void scaleRow(int i, double factor) {
		for(int j=cols(); --j >= 0; ) entry[i][j] *= factor;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#scaleRow(int, java.lang.Object)
	 */
	@Override
	public void scaleRow(int i, Double factor) {
		scaleRow(i, factor.doubleValue());
	}

	/**
	 * Offset.
	 *
	 * @param value the value
	 */
	public void offset(Double value) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] += value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setScalar(java.lang.Object)
	 */
	@Override
	public void setScalar(Double value) {
		entry = new double[][] {{ value }};
	}
	

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRank()
	 */
	@Override
	public int getRank() {
		Matrix copy = (Matrix) copy();
		copy.gauss();
		int rank = 0;
		int col = 0;
		for(int i=0; i<rows(); i++) {
			double[] row = copy.entry[i];
			for(int j=col; j<cols(); j++) {
				if(row[j] != 0.0) {
					col = j+1;
					break;
				}
			}
		}
			
		return rank;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getBasis()
	 */
	@Override
	public AbstractVectorBasis<Double> getBasis() {
		VectorBasis basis = new VectorBasis();
		Matrix copy = (Matrix) copy();
		copy.gauss();
		int col = 0;
		for(int i=0; i<rows(); i++) {
			double[] row = copy.entry[i];
			for(int j=col; j < cols(); j++) {
				if(row[j] != 0.0) {
					RealVector v = new RealVector(cols());
					getColumn(j, v.component);
					basis.add(v);
					col = j+1;
					break;
				}
			}
		}
		return basis;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#isNullRow(int)
	 */
	@Override
	public boolean isNullRow(int i) {
		for(int j=cols(); --j >= 0; ) if(entry[i][j] != 0.0) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#toString()
	 */
	@Override
	public String toString() {
		return getShortString() + ":\n" + ArrayUtil.toString(entry);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return getShortString() + ":\n" + ArrayUtil.toString(entry, nf);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#toString(int)
	 */
	@Override
	public String toString(int decimals) {
		return getShortString() + ":\n" + ArrayUtil.toString(entry, decimals);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
		if(!a.isEqualSize(b)) throw new IllegalArgumentException("different size matrices.");			
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] = a.getValue(i, j) + b.getValue(i,  j);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
		if(!a.isEqualSize(b)) throw new IllegalArgumentException("different size matrices.");
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] = a.getValue(i, j) + b.getValue(i,  j);
	}
	
	
	
	
}

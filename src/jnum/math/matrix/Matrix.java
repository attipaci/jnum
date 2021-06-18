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

package jnum.math.matrix;


import java.util.*;

import jnum.ExtraMath;
import jnum.ShapeException;
import jnum.data.ArrayUtil;
import jnum.data.fitting.ConvergenceException;
import jnum.math.MathVector;
import jnum.util.HashCode;

import java.text.*;

// TODO: Auto-generated Javadoc
//TODO Various decompositions.
// TODO implement fast multiplication?

public class Matrix extends AbstractMatrix<Double> {

	private static final long serialVersionUID = 1648081664701964671L;

	private double[][] entry; 

	public Matrix() {}

	// Check for rectangular shape
	public Matrix(double[][] a) throws IllegalArgumentException { 
		this();
		entry = a; 
		validate();
	}
		
	public Matrix(int size) {
	    this(size, size);
	}

	public Matrix(int rows, int cols) { 
		this();
		entry = new double[rows][cols];
	}

	@Override
    public Matrix clone() {
        return (Matrix) super.clone();
    }
	
	@Override
    public Matrix copy() {
	    return (Matrix) super.copy();
	}
	
    public Matrix dot(Matrix B) {
	    return (Matrix) super.dot(B);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.sampleFrom(entry);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Matrix)) return false;
	
		return Arrays.equals(entry, ((Matrix) o).entry);
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
	

	public void setData(double[][] data) {
		entry = data;
		validate();
	}
	

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
	
	public void setValue(int i, int j, double value) { entry[i][j] = value; }
	
	public void addValue(int i, int j, double increment) { entry[i][j] += increment; }
	
	@Override
    public void addValue(int i, int j, Double increment) { entry[i][j] += increment; }
	
	@Override
    public void addScaledValue(int i, int j, Double increment, double scaling) { entry[i][j] += increment * scaling; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#checkShape()
	 */
	@Override
	protected void checkShape() throws ShapeException {
		if(getData() == null) return;
		if(rows() == 0) return;
		int m = cols();
		for(int i=rows(); --i > 0; ) if(entry[i].length != m) throw new ShapeException("Matrix has an irregular non-rectangular shape!");	
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#calcProduct(kovacs.math.AbstractMatrix, kovacs.math.AbstractMatrix, boolean)
	 */
	@Override
	protected void addProduct(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {	
	    // TODO parallelize on i.
	    
		for(int i=A.rows(); --i >= 0; ) {
		    final double[] row = entry[i];
		    
		    for(int k=A.cols(); --k >= 0; ) {
		        final double a = A.getValue(i, k);
		        if(a == 0.0) continue;
		        
		        for(int j=B.cols(); --j >= 0; ) {
		            final double b = B.getValue(k, j);
		            if(b == 0.0) continue;
		            row[j] += a * b;
		        }
		    }
	            
		}
	}
	
	public static Matrix product(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {
		Matrix product = new Matrix();
		product.setProduct(A, B);
		return product;
	}
	

	public double[] dot(double[] v) {
		double[] result = new double[rows()];
		dot(v, result);
		return result;
	}
	
	
	public void dot(double[] v, double[] result) {
		if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
		
		// TODO parallelize on i;
		for(int i=rows(); --i >= 0; ) {
		    final double[] row = entry[i];
		    double sum = 0.0;
		    for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
		    result[i] = sum;
		}
	}
	

	public float[] dot(float[] v) {
		float[] result = new float[rows()];
		dot(v, result);
		return result;
	}
	

	public void dot(float[] v, float[] result) {
		if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

		for(int i=rows(); --i >= 0; ) {
		    final double[] row = entry[i];
		    double sum = 0.0;
		    for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
		    result[i] = (float) sum;
		}
	}

	
	public RealVector dot(MathVector<Double> v) {
		RealVector result = new RealVector(rows());
		dot(v, result);
		return result;
	}
    

	public void dot(MathVector<Double> v, MathVector<Double> result) {
		if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

		for(int i=rows(); --i >= 0; ) {
		    final double[] row = entry[i];
		    double sum = 0.0;
		    for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) {
		        double c = v.getComponent(j);
		        if(c != 0.0) sum += row[j] * v.getComponent(j);
		    }
		    result.setComponent(i, sum);
		}
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
		if(entry != null) for(double[] row : entry) Arrays.fill(row, 0.0);
	}
	

	

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addScaled(AbstractMatrix<? extends Double> o, double factor) {
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
		for(double[] row : entry) for(int j=cols(); --j >= 0; ) row[j] *= factor;
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
	protected void SVD(double[] w, Matrix V) {
		SVD(w, V, 100);
	}
	

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
				if(its >= maxIterations) throw new ConvergenceException("SVD did not converge in " + its + " steps.");
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
		else if(buffer instanceof float[]) getColumn(j, (float[]) buffer);
		else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
	}
	

	public void getColumn(int j, double[] buffer) {
		for(int i=rows(); --i >= 0; ) buffer[i] = entry[i][j];		
	}
	

	public void getColumn(int j, float[] buffer) {
		for(int i=rows(); --i >=0; ) buffer[i] = (float) entry[i][j];		
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRow(int, java.lang.Object)
	 */
	@Override
	public void getRow(int j, Object buffer) {
		if(buffer instanceof double[]) getRow(j, (double[]) buffer);
		else if(buffer instanceof float[]) getRow(j, (float[]) buffer);
		else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
	}
	

	public void getRow(int i, double[] buffer) {
		for(int j=cols(); --j >= 0; ) buffer[j] = entry[i][j];		
	}
	

	public void getRow(int i, float[] buffer) {
		for(int j=cols(); --j >= 0; ) buffer[j] = (float) entry[i][j];		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setColumn(int, java.lang.Object)
	 */
	@Override
	public void setColumn(int j, Object value) throws IllegalArgumentException {		
		if(value instanceof double[]) setColumn(j, (double[]) value);
		else if(value instanceof float[]) setColumn(j, (float[]) value);
		else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " column.");
	}

	public void setColumn(int j, double[] value) throws ShapeException {
		if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
		for(int i=rows(); --i >= 0; ) entry[i][j] = value[i];		
	}
	

	public void setColumn(int j, float[] value) throws ShapeException {
		if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
		for(int i=rows(); --i >= 0; ) entry[i][j] = value[i];		
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setRow(int, java.lang.Object)
	 */
	@Override
	public void setRow(int j, Object value) throws IllegalArgumentException {		
		if(value instanceof double[]) setRow(j, (double[]) value);
		else if(value instanceof float[]) setRow(j, (float[]) value);
		else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " row.");
	}
	

	public void setRow(int i, double[] value) throws ShapeException {
		if(value.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
		entry[i] = value;
	}
	

	public void setRow(int i, float[] value) throws ShapeException {
		if(value.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
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


	public void offset(Double value) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] += value;
	}


	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRank()
	 */
	@Override
	public int getRank() {
		Matrix copy = copy();
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
	public VectorBasis getBasis() {
		VectorBasis basis = new VectorBasis();
		Matrix copy = copy();
		copy.gauss();
		int col = 0;
		for(int i=0; i<rows(); i++) {
			double[] row = copy.entry[i];
			for(int j=col; j < cols(); j++) {
				if(row[j] != 0.0) {
					RealVector v = new RealVector(cols());
					getColumn(j, v.getData());
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
		if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");			
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] = a.getValue(i, j) + b.getValue(i,  j);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
		if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j] = a.getValue(i, j) + b.getValue(i,  j);
	}
	
	

	
	public Matrix getInverse() {
        return getLUInverse();
    }
    

    public Matrix getLUInverse() {
        return new LUDecomposition(this).getInverse();
    }
    

    public Matrix getSVDInverse() {
        return new SVD(this).getInverse().copy();
    }
    
    // Invert via Gauss-Jordan elimination
    public Matrix getGaussInverse() {
        if(!isSquare()) throw new SquareMatrixException();
        
        int size = rows();
        Matrix combo = new Matrix(size, 2*size);
        for(int i=size; --i >= 0; ) combo.entry[i][i+size] = 1.0;
        combo.paste(this, 0, 0);
        combo.gaussJordan();
        Matrix inverse = new Matrix((double[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, 2*size }));
        return inverse;
    }
    

    // indx is the row permutation, returns +/-1 for even/odd row exchanges...
    // Based on Numerical Recipes in C (Press et al. 1989)
    protected boolean decomposeLU(int[] index) { return decomposeLU(index, 1e-30); }
    

    protected boolean decomposeLU(int[] index, double tinyValue) {
        if(!isSquare()) throw new SquareMatrixException();
        
        final int n = rows();

        double[] v = new double[n];
        boolean evenChanges = true;
        
        for(int i=n; --i >= 0; ) {
            double big = 0.0;
            for(int j=n; --j >= 0; ) {
                final double temp = Math.abs(entry[i][j]);
                if(temp > big) big = temp;
            }
            if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
            v[i] = 1.0 / big;
        }
        for(int j=0; j<n; j++) {
            int imax = -1;
            
            for(int i=j; --i >= 0; ) {
                double sum = entry[i][j];
                for(int k=i; --k >= 0; ) sum -= entry[i][k] * entry[k][j];
                entry[i][j] = sum;
            }
            double big = 0.0;
            for(int i=n; --i >= j; ) {
                double sum = entry[i][j];
                for(int k=j; --k >= 0; ) sum -= entry[i][k] * entry[k][j];
                entry[i][j] = sum;
                final double temp = v[i] * Math.abs(sum);
                if (temp >= big) {
                    big=temp;
                    imax=i;
                }
            }
            if(j != imax) {
                for(int k=n; --k >= 0; ) {
                    double temp = entry[imax][k];
                    entry[imax][k] = entry[j][k];
                    entry[j][k] = temp;
                }
                evenChanges = !evenChanges;
                v[imax] = v[j];
            }
            index[j] = imax;
            if(entry[j][j] == 0.0) entry[j][j] = tinyValue;
            
            if(j != n-1) {
                double temp = 1.0 / entry[j][j];
                for(int i=n; --i > j; ) entry[i][j] *= temp;
            }
        }
        return evenChanges;
    }
    
    // TODO Solving with and without inversion...
    public void solve(double[] b) {
        new LUDecomposition(this).solve(b);
    }
    

    // TODO ...
    public void invertAndSolve(double[] b) {    
        
    }
    
    
    public void solve(Matrix inputVectors) {
        inputVectors.entry = getSolutionsTo(inputVectors.entry);
    }
    

    @Override
    public void solve(AbstractVector<Double>[] inputVectors) {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        Matrix combo = new Matrix(size, size + inputVectors.length);
        combo.paste(this, 0, 0);
        
        for(int col=inputVectors.length; --col >= 0; ) {
            AbstractVector<Double> v = inputVectors[col];
            for(int row=size; --row >= 0; ) combo.setValue(row, size + col, v.getComponent(row));
        }

        combo.gaussJordan();
        
        for(int col=inputVectors.length; --col >= 0; ) {
            AbstractVector<Double> v = inputVectors[col];
            for(int row=size; --row >= 0; ) v.setComponent(row, combo.getValue(row, size + col));
        }

    }
    
    
    public double[][] getSolutionsTo(double[][] inputMatrix) {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        Matrix combo = new Matrix(size, size + inputMatrix[0].length);
        combo.paste(this, 0, 0);
        ArrayUtil.paste(inputMatrix, entry, new int[] { 0, size });
        combo.gaussJordan();
        return (double[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, combo.cols() });
    }
    

    @Override
    public void invert() {
        entry = getInverse().entry;
    }

   
    public void setSize(int size) {
        setSize(size, size);
    }
    
    @Override
    public void addIdentity(double scaling) {
        if(!isSquare()) throw new SquareMatrixException();
        for(int i=rows(); --i >= 0; ) entry[i][i] += scaling;
    }
    
    
    public final static Matrix identity() {
        return new Matrix(new double[][] {{1.0}});
    }
    
    public final static Matrix identity(int size) {
        Matrix I = new Matrix(size);
        I.addIdentity(1.0);
        return I;
    }
    
	
}

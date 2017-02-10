/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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

// TODO: Auto-generated Javadoc
// Decomposes Matrix A as:
// A = U * diag(w) * V^T
// square A --> A^-1 = V * diag(1/w) * U^T 
// where U is column-orthogonal and V is orthogonal
/**
 * The Class SVD.
 */
public class SVD {
	
	/** The u. */
	private Matrix u;
	
	/** The v. */
	private SquareMatrix v;
	
	/** The w. */
	private double[] w;
	
	/**
	 * Instantiates a new svd.
	 */
	public SVD() {}
	
	/**
	 * Instantiates a new svd.
	 *
	 * @param M the m
	 */
	public SVD(Matrix M) {
		decompose(M);
	}
	
	/**
	 * Gets the u.
	 *
	 * @return the u
	 */
	public Matrix getU() { return u; }
	
	/**
	 * Gets the v.
	 *
	 * @return the v
	 */
	public SquareMatrix getV() { return v; }
	
	/**
	 * Gets the w.
	 *
	 * @return the w
	 */
	public double[] getW() { return w; }
	
	/**
	 * Decompose.
	 *
	 * @param M the m
	 */
	public void decompose(Matrix M) {
		int n = M.cols();
		u = (Matrix) M.copy();
		v = new SquareMatrix(n);
		w = new double[n];
		u.SVD(w, v);
	}
	
	/**
	 * Solve.
	 *
	 * @param b the b
	 * @return the double[]
	 */
	public double[] solve(double b[]) {
		double[] x = new double[w.length];
		solve(b, x);
		return x;
	}
		
	/**
	 * Solve.
	 *
	 * @param b the b
	 * @param x the x
	 */
	public void solve(double b[], double x[]) {
		final int n = x.length;
		final int m = b.length;
		
		double[] tmp = new double[n];
		
		for (int j=n; --j >= 0; ) {
			double s = 0.0;
			if(w[j] != 0.0) {
				for(int i=m; --i >= 0; ) s += u.entry[i][j] * b[i];
				s /= w[j];
			}
			tmp[j] = s;
		}
		for(int j=n; --j >= 0; ) {
			double s = 0.0;
			for(int jj=n; --jj >= 0; ) s += v.entry[j][jj] * tmp[jj];
			x[j] = s;
		}
		
	}
	
	/**
	 * Gets the matrix.
	 *
	 * @return the matrix
	 */
	public Matrix getMatrix() {
		Matrix M = new Matrix();
		getMatrixTo(M);
		return M;
	}
	
	
	/**
	 * Gets the matrix to.
	 *
	 * @param M the m
	 * @return the matrix to
	 */
	public void getMatrixTo(Matrix M) {
		final int n = w.length;
		SquareMatrix wvT = new SquareMatrix(n);
		for(int i=n; --i >= 0; ) for(int j=n; --j >= 0; ) wvT.entry[i][j] = w[i] * v.entry[j][i];
		M.setProduct(u, wvT);
	}
	
	// square A --> A^-1 = V * diag(1/w) * U^T 
	/**
	 * Gets the inverse.
	 *
	 * @return the inverse
	 */
	
	public SquareMatrix getInverse() {
		SquareMatrix inverse = new SquareMatrix();
		getInverseTo(inverse);
		return inverse;		
	}
	
	
	/**
	 * Gets the inverse to.
	 *
	 * @param inverse the inverse
	 * @return the inverse to
	 */
	public void getInverseTo(SquareMatrix inverse) {	
		final int n = w.length;
		if(u.rows() != n) throw new IllegalStateException("Cannot invert non-square matrix.");
		SquareMatrix iwuT = new SquareMatrix(n);
		for(int i=n; --i >= 0; ) for(int j=n; --j >= 0; ) iwuT.entry[i][j] = 1.0 / w[i] * u.entry[j][i];
		inverse.setProduct(v, iwuT);
	}
	
}

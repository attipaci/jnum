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

import jnum.Copiable;
import jnum.data.ArrayUtil;



// TODO: Auto-generated Javadoc
// TODO Overwrite all methods from Matrix that are not fully supported by square-matrices...

/**
 * The Class GenericSquareMatrix.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("unchecked")
public class GenericSquareMatrix<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue & Copiable<? super T>> extends GenericMatrix<T> implements SquareMatrixAlgebra<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 276116236966727928L;

	/**
	 * Instantiates a new generic square matrix.
	 *
	 * @param type the type
	 */
	public GenericSquareMatrix(Class<T> type) {
		super(type);
	}
	
	/**
	 * Instantiates a new generic square matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public GenericSquareMatrix(T[][] a) throws IllegalArgumentException {
		super(a);
	}

	/**
	 * Instantiates a new generic square matrix.
	 *
	 * @param type the type
	 * @param size the size
	 */
	public GenericSquareMatrix(Class<T> type, int size) {
		super(type, size, size);
	}
	
	/**
	 * Instantiates a new generic square matrix.
	 *
	 * @param a the a
	 */
	public GenericSquareMatrix(GenericMatrix<T> a) {
		entry = a.entry;
		validate();
	}

	/* (non-Javadoc)
	 * @see kovacs.math.GenericMatrix#checkShape()
	 */
	@Override
	public void checkShape() throws IllegalStateException{
		if(rows() != cols()) throw new IllegalStateException(getDimensionString() + " is not a square matrix!");
		super.checkShape();
	}
	
	/**
	 * Adds the rows.
	 *
	 * @param b the b
	 */
	public void addRows(GenericMatrix<? extends T> b) {
		throw new UnsupportedOperationException("Cannot add rows to a " + getClass().getSimpleName());
	}
	
	/**
	 * Adds the columns.
	 *
	 * @param b the b
	 */
	public void addColumns(GenericMatrix<? extends T> b) {
		throw new UnsupportedOperationException("Cannot add columns to a " + getClass().getSimpleName());
	}
	
	/**
	 * Gets the inverse.
	 *
	 * @return the inverse
	 */
	public GenericSquareMatrix<T> getInverse() {
		return getLUInverse();
	}
	
	// Invert via Gauss-Jordan elimination
	/**
	 * Gets the gauss inverse.
	 *
	 * @return the gauss inverse
	 */
	public GenericSquareMatrix<T> getGaussInverse() {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, 2*size);
		for(int i=size; --i >= 0; ) combo.entry[i][i+size].setIdentity();
		combo.paste(this, 0, 0);
		combo.gaussJordan();
		GenericSquareMatrix<T> inverse = new GenericSquareMatrix<T>((T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, 2*size }));
		return inverse;
	}
	
	/**
	 * Gets the lU inverse.
	 *
	 * @return the lU inverse
	 */
	public GenericSquareMatrix<T> getLUInverse() {
		return new GenericLUDecomposition<T>(this).getInverse();
	}
	
	/**
	 * Solve.
	 *
	 * @param inputVectors the input vectors
	 */
	public void solve(GenericMatrix<T> inputVectors) {
		inputVectors.entry = getSolutionsTo(inputVectors.entry);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.SquareMatrixAlgebra#solve(kovacs.math.AbstractVector[])
	 */
	@Override
	public void solve(AbstractVector<T>[] inputVectors) {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, size + inputVectors.length);
		combo.paste(this, 0, 0);
		
		for(int col=inputVectors.length; --col >= 0; ) {
			AbstractVector<T> v = inputVectors[col];
			for(int row=size; --row >= 0; ) combo.setValue(row, size + col, v.getComponent(row));
		}

		combo.gaussJordan();
		
		for(int col=inputVectors.length; --col >= 0; ) {
			AbstractVector<T> v = inputVectors[col];
			for(int row=size; --row >= 0; ) v.setComponent(row, combo.getValue(row, size + col));
		}
	}
	
	/**
	 * Gets the solutions to.
	 *
	 * @param inputMatrix the input matrix
	 * @return the solutions to
	 */
	public T[][] getSolutionsTo(T[][] inputMatrix) {
		int size = size();
		GenericMatrix<T> combo = new GenericMatrix<T>(type, size, size + inputMatrix[0].length);
		combo.paste(this, 0, 0);
		ArrayUtil.paste(inputMatrix, entry, new int[] { 0, size });
		combo.gaussJordan();
		return (T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, combo.cols() });
	}
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.Inversion#invert()
	 */
	@Override
	public void invert() {
		entry = getInverse().entry;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.SquareMatrixAlgebra#size()
	 */
	@Override
	public final int size() { return rows(); }

	/* (non-Javadoc)
	 * @see kovacs.math.GenericMatrix#setIdentity()
	 */
	@Override
	public void setIdentity() {
		zero();
		for(int i=entry.length; --i >= 0; ) entry[i][i].setIdentity();
	}
	
	// indx is the row permutation, returns true/false for even/odd row exchanges...
		
	/**
	 * Decompose lu.
	 *
	 * @param index the index
	 * @return true, if successful
	 */
	protected boolean decomposeLU(int[] index) { return decomposeLU(index, 1e-20); }
	
	/**
	 * Decompose lu.
	 *
	 * @param index the index
	 * @param tinyValue the tiny value
	 * @return true, if successful
	 */
	protected boolean decomposeLU(int[] index, double tinyValue) {
		final int n = size();

		double[] v = new double[n];
		boolean evenChanges = true;
		
		T product = newEntry();
		
		for(int i=n; --i >= 0; ) {
			double big = 0.0;
			for(int j=n; --j >= 0; ) {
				final double tmp = entry[i][j].abs();
				if(tmp > big) big = tmp;
			}
			if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
			v[i] = 1.0 / big;
		}
		for(int j=0; j<n; j++ ) {
			int imax = -1;
			
			for(int i=j; --i >= 0; ) {
				T sum = (T) entry[i][j].copy();
				for(int k=i; --k >= 0; ) {
					product.setProduct(entry[i][k], entry[k][j]);
					sum.subtract(product);
				}
				entry[i][j] = sum;
			}
			double big = 0.0;
			for(int i=n; --i >= j; ) {
				T sum = (T) entry[i][j].copy();
				for(int k=j; --k >= 0; ) {
					product.setProduct(entry[i][k], entry[k][j]);
					sum.subtract(product);
				}
				entry[i][j] = sum;
				final double tmp = v[i] * sum.abs();
				if (tmp >= big) {
					big=tmp;
					imax=i;
				}
			}
			if(j != imax) {
				for(int k=n; --k >= 0; ) {
					T tmp = entry[imax][k];
					entry[imax][k] = entry[j][k];
					entry[j][k] = tmp;
				}
				evenChanges = !evenChanges;
				v[imax] = v[j];
			}
			index[j] = imax;
			
			T diag = entry[j][j];
			
			if(diag.isNull()) {
				diag.setIdentity();
				diag.scale(tinyValue);
			}
			
			if(j != n-1) {
				T tmp = (T) diag.getInverse();
				for(int i=n; --i > j; ) entry[i][j].multiplyBy(tmp);
			}
		}
		return evenChanges;
	}
	
	
}

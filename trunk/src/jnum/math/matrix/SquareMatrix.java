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

import jnum.data.ArrayUtil;




// TODO: Auto-generated Javadoc
/**
 * The Class SquareMatrix.
 */
public class SquareMatrix extends Matrix implements SquareMatrixAlgebra<Double> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5786886148000000230L;

	/**
	 * Instantiates a new square matrix.
	 */
	public SquareMatrix() {
		super();
	}
	
	/**
	 * Instantiates a new square matrix.
	 *
	 * @param M the m
	 */
	public SquareMatrix(Matrix M) {
		this(M.entry);
		validate();
	}

	/**
	 * Instantiates a new square matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public SquareMatrix(double[][] a) throws IllegalArgumentException {
		super(a);
	}

	/**
	 * Instantiates a new square matrix.
	 *
	 * @param size the size
	 */
	public SquareMatrix(int size) {
		super(size, size);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Matrix#checkShape()
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
	public void addRows(Matrix b) {
		throw new UnsupportedOperationException("Cannot add rows to a " + getClass().getSimpleName());
	}
	
	/**
	 * Adds the columns.
	 *
	 * @param b the b
	 */
	public void addColumns(Matrix b) {
		throw new UnsupportedOperationException("Cannot add columns to a " + getClass().getSimpleName());
	}
	
	/**
	 * Gets the inverse.
	 *
	 * @return the inverse
	 */
	public SquareMatrix getInverse() {
		return getLUInverse();
	}
	
	/**
	 * Gets the lU inverse.
	 *
	 * @return the lU inverse
	 */
	public SquareMatrix getLUInverse() {
		return new LUDecomposition(this).getInverse();
	}
	
	
	/**
	 * Gets the SVD inverse.
	 *
	 * @return the SVD inverse
	 */
	public SquareMatrix getSVDInverse() {
		return new SquareMatrix(new SVD(this).getInverse());
	}
	
	// Invert via Gauss-Jordan elimination
	/**
	 * Gets the gauss inverse.
	 *
	 * @return the gauss inverse
	 */
	public SquareMatrix getGaussInverse() {
		int size = size();
		Matrix combo = new Matrix(size, 2*size);
		for(int i=size; --i >= 0; ) combo.entry[i][i+size] = 1.0;
		combo.paste(this, 0, 0);
		combo.gaussJordan();
		SquareMatrix inverse = new SquareMatrix((double[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, 2*size }));
		return inverse;
	}
	

	// indx is the row permutation, returns +/-1 for even/odd row exchanges...
	// Based on Numerical Recipes in C (Press et al. 1989)
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
	
	/**
	 * Solve.
	 *
	 * @param b the b
	 */
	public void solve(double[] b) {
		new LUDecomposition(this).solve(b);
	}
	
	/**
	 * Invert and solve.
	 *
	 * @param b the b
	 */
	public void invertAndSolve(double[] b) {
		
		
	}
	
	/**
	 * Solve.
	 *
	 * @param inputVectors the input vectors
	 */
	public void solve(Matrix inputVectors) {
		inputVectors.entry = getSolutionsTo(inputVectors.entry);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.SquareMatrixAlgebra#solve(kovacs.math.AbstractVector[])
	 */
	@Override
	public void solve(AbstractVector<Double>[] inputVectors) {
		int size = size();
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
	
	
	/**
	 * Gets the solutions to.
	 *
	 * @param inputMatrix the input matrix
	 * @return the solutions to
	 */
	public double[][] getSolutionsTo(double[][] inputMatrix) {
		int size = size();
		Matrix combo = new Matrix(size, size + inputMatrix[0].length);
		combo.paste(this, 0, 0);
		ArrayUtil.paste(inputMatrix, entry, new int[] { 0, size });
		combo.gaussJordan();
		return (double[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, combo.cols() });
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

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(int size) {
	    super.setSize(size, size);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Matrix#setIdentity()
	 */
	@Override
	public void setIdentity() {
		zero();
		for(int i=entry.length; --i >= 0; ) entry[i][i] = 1.0;
	}
	
	/**
	 * Rotation.
	 *
	 * @param angle the angle
	 * @return the square matrix
	 */
	public static SquareMatrix rotation(double angle) {	
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		SquareMatrix rotation = new SquareMatrix(2);
		rotation.entry = new double[][] {{ c, -s }, { s, c }};
		return rotation; 
	}
		
	
	/**
	 * Rotation.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @return the square matrix
	 */
	public static SquareMatrix rotation(double theta, double phi) {
		return rotation(new double[] { theta, phi } );	
	}
	
	
	/**
	 * Inverse rotation.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @return the square matrix
	 */
	public static SquareMatrix inverseRotation(double theta, double phi) {	
		return inverseRotation(new double[] { theta, phi } );
	}
	
	
	/**
	 * Rotation.
	 *
	 * @param angles the angles
	 * @return the square matrix
	 */
	public static SquareMatrix rotation(double[] angles) {
		int n = angles.length + 1;
		SquareMatrix rotation = new SquareMatrix(n);
		SquareMatrix element = new SquareMatrix(n);
		
		rotation.setIdentity();
		
		// rotate in xy, -xz, x.., -x.. planes...
		for(int i=angles.length; i>0; i++) {
			element.zero();
			
			double angle = angles[angles.length - i];
			
			double c = Math.cos(angle);
			double s = Math.sin(angle);
			if(i%2 == 0) s *= -1;
			
			element.entry[0][0] = c;
			element.entry[0][i] = -s;
			element.entry[i][0] = s;
			element.entry[i][i] = c;
			
			rotation.dot(element);
		}
	
		return rotation;
	}
	
	/**
	 * Inverse rotation.
	 *
	 * @param angles the angles
	 * @return the square matrix
	 */
	public static SquareMatrix inverseRotation(double[] angles) {
		int n = angles.length + 1;
		SquareMatrix rotation = new SquareMatrix(n);
		SquareMatrix element = new SquareMatrix(n);
		
		rotation.setIdentity();
		
		for(int i=1; i<=angles.length; i--) {
			element.zero();
			
			double angle = angles[angles.length - i];
			
			double c = Math.cos(angle);
			double s = Math.sin(-angle);
			
			if(i%2 == 0) s *= -1;
			
			element.entry[0][0] = c;
			element.entry[0][i] = -s;
			element.entry[i][0] = s;
			element.entry[i][i] = c;
			
			rotation.dot(element);
		}
	
		return rotation;
	}
}

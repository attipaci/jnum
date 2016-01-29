/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.util;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleMatrix.
 */
public class SimpleMatrix implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7486363505617096466L;

	/** The value. */
	public float[][] value; 

	/**
	 * Instantiates a new simple matrix.
	 */
	public SimpleMatrix() {}

	/**
	 * Instantiates a new simple matrix.
	 *
	 * @param v the v
	 * @param dir the dir
	 */
	public SimpleMatrix(float[] v, int dir) {
		int n = v.length;

		switch(dir) {
		case COLUMN_VECTOR:
			value = new float[n][1];
			for(int i=n; --i >= 0; ) value[i][0] = v[i];
			break;
		case ROW_VECTOR:
			value = new float[1][n];
			final float[] row = value[0];
			for(int i=n; --i >= 0; ) row[i] = v[i];
			break;
		}
	}

	/**
	 * Instantiates a new simple matrix.
	 *
	 * @param a the a
	 */
	public SimpleMatrix(float[][] a) { value = a; }

	/**
	 * Instantiates a new simple matrix.
	 *
	 * @param n the n
	 * @param m the m
	 */
	public SimpleMatrix(int n, int m) { value = new float[n][m]; }

	/**
	 * Dot.
	 *
	 * @param B the b
	 * @return the simple matrix
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public SimpleMatrix dot(final SimpleMatrix B) throws IllegalArgumentException {	
		final int l = value[0].length;
		
		if(l != B.value.length) 
			throw new IllegalArgumentException("Incompatible Matrix Dimensions: " + getSizeString() + "," + B.getSizeString());

		final int n = value.length;
		final int m = B.value[0].length;
		final SimpleMatrix M = new SimpleMatrix(n, m);

		for(int i=n; --i >= 0; ) for(int j=m; --j >= 0; )
			for(int k=l; --k >= 0; ) M.value[i][j] += value[i][k] * B.value[k][j];

		return M;
	}

	/**
	 * Transpose.
	 *
	 * @return the simple matrix
	 */
	public SimpleMatrix transpose() {
		final int n = value.length;
		final int m = value[0].length;

		final SimpleMatrix M = new SimpleMatrix(m, n);

		for(int i=n; --i >= 0; ) for(int j=m; --j >= 0; ) M.value[j][i] = value[i][j];

		return M;
	}

	/**
	 * Gets the size string.
	 *
	 * @return the size string
	 */
	final String getSizeString() {
		return "[" + value.length + "x" + value[0].length + "]";
	}

	/** The Constant ROW_VECTOR. */
	public final static int ROW_VECTOR = 0;
	
	/** The Constant COLUMN_VECTOR. */
	public final static int COLUMN_VECTOR = 1;
}

/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.util;

import java.io.Serializable;


public class SimpleMatrix implements Serializable {

	private static final long serialVersionUID = 7486363505617096466L;

	public float[][] value; 

	public SimpleMatrix() {}


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


	public SimpleMatrix(float[][] a) { value = a; }


	public SimpleMatrix(int n, int m) { value = new float[n][m]; }


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


	public SimpleMatrix transpose() {
		final int n = value.length;
		final int m = value[0].length;

		final SimpleMatrix M = new SimpleMatrix(m, n);

		for(int i=n; --i >= 0; ) for(int j=m; --j >= 0; ) M.value[j][i] = value[i][j];

		return M;
	}


	final String getSizeString() {
		return "[" + value.length + "x" + value[0].length + "]";
	}

	public final static int ROW_VECTOR = 0;

	public final static int COLUMN_VECTOR = 1;
}

/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.math.matrix;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class LUDecomposition.
 */
public class LUDecomposition {
	
	/** The lu. */
	SquareMatrix LU;
	
	/** The index. */
	int[] index;
	
	/** The even changes. */
	boolean evenChanges;
	
	/**
	 * Instantiates a new lU decomposition.
	 */
	public LUDecomposition() {}
	
	/**
	 * Instantiates a new lU decomposition.
	 *
	 * @param M the m
	 */
	public LUDecomposition(SquareMatrix M) {
		decompose(M);
	}
	
	/**
	 * Decompose.
	 *
	 * @param M the m
	 */
	public void decompose(SquareMatrix M) {
		LU = (SquareMatrix) M.copy();
		index = new int[LU.size()];
		evenChanges = LU.decomposeLU(index);
	}
	
	/**
	 * Solve.
	 *
	 * @param b the b
	 */
	public void solve(double b[]) {
		int ii=-1;
		int n = LU.size();
		
		for(int i=0; i<n; i++) {
			int ip = index[i];
			double sum = b[ip];
			b[ip] = b[i];
			if(ii != -1) for(int j=ii; j< i; j++) sum -= LU.entry[i][j] * b[j];
			else if(sum != 0.0) ii = i;
			b[i] = sum;
		}
		for(int i=n; --i >= 0; ) {
			double sum = b[i];
			for(int j=i+1; j<n; j++) sum -= LU.entry[i][j] * b[j];
			b[i] = sum / LU.entry[i][i];
		}
	}

	/**
	 * Gets the inverse.
	 *
	 * @return the inverse
	 */
	public SquareMatrix getInverse() {
		SquareMatrix inverse = new SquareMatrix(LU.size());
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
		final int n = LU.size();
		
		if(inverse.size() != n) throw new IllegalArgumentException("mismatched inverse matrix size.");
		
		double[] v = new double[n];
		
		for(int i=0; i<n; i++) {
			if(i > 0) Arrays.fill(v, 0.0);
			v[i] = 1.0;
			solve(v);
			for(int j=n; --j >= 0; ) inverse.entry[j][i] = v[j];
		}
	}
	
}

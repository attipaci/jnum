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

import java.util.Arrays;


public class LUDecomposition {

	private Matrix LU;
	private int[] index;
	private boolean evenChanges;
	
	public LUDecomposition() {}
	

	public LUDecomposition(Matrix M) {
		decompose(M);
	}
	
	public Matrix getMatrix() { return LU; }
	

	public void decompose(Matrix M) {
	    if(!M.isSquare()) throw new SquareMatrixException();
	    
		LU = M.copy();
		index = new int[LU.rows()];
		evenChanges = LU.decomposeLU(index);
	}
	

	public void solve(double b[]) {
	    if(!LU.isSquare()) throw new SquareMatrixException();
	    
		int ii=-1;
		int n = LU.rows();
		
		for(int i=0; i<n; i++) {
			int ip = index[i];
			double sum = b[ip];
			b[ip] = b[i];
			if(ii != -1) for(int j=ii; j< i; j++) sum -= LU.getValue(i,j) * b[j];
			else if(sum != 0.0) ii = i;
			b[i] = sum;
		}
		for(int i=n; --i >= 0; ) {
			double sum = b[i];
			for(int j=i+1; j<n; j++) sum -= LU.getValue(i, j) * b[j];
			b[i] = sum / LU.getValue(i, i);
		}
	}


	public Matrix getInverse() {
	    if(!LU.isSquare()) throw new SquareMatrixException();
	    
		Matrix inverse = new Matrix(LU.rows());
		getInverseTo(inverse);
		return inverse;
	}
	

	public void getInverseTo(Matrix inverse) {
	    if(!inverse.isSquare()) throw new SquareMatrixException();
	    
		final int n = LU.rows();
		
		if(inverse.rows() != n) throw new IllegalArgumentException("mismatched inverse matrix size.");
		
		double[] v = new double[n];
		
		for(int i=0; i<n; i++) {
			if(i > 0) Arrays.fill(v, 0.0);
			v[i] = 1.0;
			solve(v);
			for(int j=n; --j >= 0; ) inverse.setValue(j, i, v[j]);
		}
	}
	
}

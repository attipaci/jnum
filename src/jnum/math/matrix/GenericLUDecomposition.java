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

import java.lang.reflect.Array;

import jnum.Copiable;
import jnum.ShapeException;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;


public class GenericLUDecomposition<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue & Copiable<? super T>> {

	private GenericMatrix<T> LU;
	private int[] index;
	private boolean evenChanges;
	

	public GenericLUDecomposition() {}
	

	public GenericLUDecomposition(GenericMatrix<T> M) {
		decompose(M);
	}

	
	public GenericMatrix<T> getMatrix() { return LU; }
	
	public void decompose(GenericMatrix<T> M) {
	    if(!M.isSquare()) throw new SquareMatrixException();
		LU = M.copy();
		index = new int[LU.rows()];
		evenChanges = LU.decomposeLU(index);
	}
	

	@SuppressWarnings("unchecked")
	public void solve(T b[]) {
	    if(!LU.isSquare()) throw new SquareMatrixException();
	    
		int ii=-1;
		int n = LU.rows();
		
		T term = LU.createEntry();
		
		for(int i=0; i<n; i++) {
			int ip = index[i];
			T sum = (T) b[ip].copy();
			b[ip] = b[i];
			if(ii != -1) for(int j=ii; j < i; j++) {
				term.setProduct(LU.entry[i][j], b[j]);
				sum.subtract(term);
			}
			else if(!sum.isNull()) ii = i;
			b[i] = sum;
		}
		for(int i=n; --i >= 0; ) {
			T sum = (T) b[i].copy();
			for(int j=i+1; j<n; j++) {
				term.setProduct(LU.entry[i][j], b[j]);
				sum.subtract(term);
			}
			sum.multiplyBy((T) LU.entry[i][i].getInverse());
			b[i] = sum;
		}
	}


	public GenericMatrix<T> getInverse() {
		GenericMatrix<T> inverse = (GenericMatrix<T>) LU.copy(false);
		getInverseTo(inverse);
		return inverse;
	}
	

	@SuppressWarnings("unchecked")
	public void getInverseTo(GenericMatrix<T> inverse) {		
	    if(!inverse.isSquare()) throw new SquareMatrixException();
		final int n = LU.rows();
		
		if(inverse.rows() != n) throw new ShapeException("mismatched inverse matrix size.");
		
		T[] v = (T[]) Array.newInstance(LU.getType(), n);
		
		for(int i=0; i<n; i++) {
			v[i] = LU.createEntry();
			
			if(i > 0) for(int k=n; --k >=0; ) v[k].zero();
			v[i].setIdentity();
			solve(v);
			for(int j=n; --j >= 0; ) inverse.entry[j][i] = v[j];
		}

	}
	
}

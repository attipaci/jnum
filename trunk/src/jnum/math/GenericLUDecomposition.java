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

package jnum.math;

import java.lang.reflect.Array;

import jnum.util.Copiable;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericLUDecomposition.
 *
 * @param <T> the generic type
 */
public class GenericLUDecomposition<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue & Copiable<? super T>> {
	
	/** The lu. */
	GenericSquareMatrix<T> LU;
	
	/** The index. */
	int[] index;
	
	/** The even changes. */
	boolean evenChanges;
	
	/**
	 * Instantiates a new generic lu decomposition.
	 */
	public GenericLUDecomposition() {}
	
	/**
	 * Instantiates a new generic lu decomposition.
	 *
	 * @param M the m
	 */
	public GenericLUDecomposition(GenericSquareMatrix<T> M) {
		decompose(M);
	}

	/**
	 * Decompose.
	 *
	 * @param M the m
	 */
	public void decompose(GenericSquareMatrix<T> M) {
		LU = (GenericSquareMatrix<T>) M.copy();
		index = new int[LU.size()];
		evenChanges = LU.decomposeLU(index);
	}
	
	/**
	 * Solve.
	 *
	 * @param b the b
	 */
	@SuppressWarnings("unchecked")
	public void solve(T b[]) {
		int ii=-1;
		int n = LU.size();
		
		T term = LU.newEntry();
		
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

	/**
	 * Gets the inverse.
	 *
	 * @return the inverse
	 */
	public GenericSquareMatrix<T> getInverse() {
		GenericSquareMatrix<T> inverse = (GenericSquareMatrix<T>) LU.copy(false);
		getInverseTo(inverse);
		return inverse;
	}
	
	@SuppressWarnings("unchecked")
	public void getInverseTo(GenericSquareMatrix<T> inverse) {		
		final int n = LU.size();
		
		if(inverse.size() != n) throw new IllegalArgumentException("mismatched inverse matrix size.");
		
		T[] v = (T[]) Array.newInstance(LU.getType(), n);
		
		for(int i=0; i<n; i++) {
			v[i] = LU.newEntry();
			
			if(i > 0) for(int k=n; --k >=0; ) v[k].zero();
			v[i].setIdentity();
			solve(v);
			for(int j=n; --j >= 0; ) inverse.entry[j][i] = v[j];
		}

	}
	
}

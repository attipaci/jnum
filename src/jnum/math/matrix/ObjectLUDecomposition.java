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
import jnum.math.MathVector;
import jnum.math.Metric;


public class ObjectLUDecomposition<T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue> 
implements MatrixInverter<T> {
    
    private ObjectMatrix<T> LU, inverse;
    private int[] index;
    private boolean evenChanges;

    public ObjectLUDecomposition(ObjectMatrix<T> M) {
        this(M, 1e-100);
    }
    
    public ObjectLUDecomposition(ObjectMatrix<T> M, double tinyValue) {
        if(!M.isSquare()) throw new SquareMatrixException();
        LU = M.copy();
        decompose(tinyValue);
    }
    
    public ObjectMatrix<T> getLU() {
        return LU.copy();
    }
	
    public final int size() { return LU.rows(); }

    
	@SuppressWarnings("unchecked")
    @Override
	public T[] solveFor(T[] y) {
	    LU.assertSize(y.length, y.length);
        T[] x = (T[]) Array.newInstance(LU.getElementType(), y.length);
        solveFor(y, x);
        return x;
	}
	
	@Override
    @SuppressWarnings("unchecked")
    public void solveFor(T[] y, T[] x) {
	    LU.assertSize(x.length, y.length);
	    for(int i=y.length; --i >=0; ) x[i] = (T) y[i].copy();
        solve(x);
	}
	   

    @Override
    public ObjectVector<T> solveFor(MathVector<T> y) {
        LU.assertSize(y.size(), y.size());
        ObjectVector<T> x = new ObjectVector<>(LU.getElementType(), y.size());
        solveFor(x.getData());
        return x;
    }
     
    @SuppressWarnings("unchecked")
    @Override
    public void solveFor(MathVector<T> y, MathVector<T> x) {
        LU.assertSize(x.size(), y.size());
        T[] v = (T[]) Array.newInstance(LU.getElementType(), y.size());
        for(int i=size(); --i >=0; ) v[i] = (T) y.getComponent(i).copy();
        solve(v);
        for(int i=size(); --i >=0; ) x.setComponent(i, v[i]);
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    private void solve(T[] v) {
        int ii=-1;
        int n = size();
        
        
        T term = LU.createElement();
        
        for(int i=0; i<n; i++) {
            int ip = index[i];
            T e = (T) v[ip].copy();
            v[ip] = v[i];
            if(ii != -1) for(int j=ii; j < i; j++) {
                term.setProduct(LU.get(i, j), v[j]);
                e.subtract(term);
            }
            else if(!e.isNull()) ii = i;
            v[i] = e;
        }
        for(int i=n; --i >= 0; ) {
            T e = v[i];
            for(int j=i+1; j<n; j++) {
                term.setProduct(LU.get(i, j), v[j]);
                e.subtract(term);
            }
            e.multiplyBy((T) LU.get(i, i).getInverse());
        }
    }

	
    @Override
    public ObjectMatrix<T> getInverseMatrix() {
        if(inverse == null) {
            inverse = LU.createMatrix(size(), size(), false);
            getInverseTo(inverse);
        }
		return inverse.copy();
	}
	

	@Override
    @SuppressWarnings("unchecked")
	public void getInverseTo(AbstractMatrix<T> inverse) {		
	    if(!inverse.isSquare()) throw new SquareMatrixException();
		final int n = size();
		
		if(inverse.rows() != n) throw new ShapeException("mismatched inverse matrix size.");
		
		T[] v = (T[]) Array.newInstance(LU.getElementType(), n);
		
		for(int i=0; i<n; i++) {
			v[i] = LU.createElement();
			
			if(i > 0) for(int k=n; --k >=0; ) v[k].zero();
			v[i].setIdentity();
			solve(v);
			for(int j=n; --j >= 0; ) inverse.set(j, i, v[j]);
		}

	}
	

    private void decompose(double tinyValue) {
        final int n = size();

        index = new int[n];
        evenChanges = true;
        
        final double[] v = new double[n];
        
        final T product = LU.createElement();
        
        for(int i=n; --i >= 0; ) {
            double big = 0.0;
            for(int j=n; --j >= 0; ) {
                final double tmp = LU.get(i, j).abs();
                if(tmp > big) big = tmp;
            }
            if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
            v[i] = 1.0 / big;
        }
        for(int j=0; j<n; j++ ) {
            int imax = -1;
            
            for(int i=j; --i >= 0; ) {
                final T e = LU.get(i, j);
                for(int k=i; --k >= 0; ) {
                    product.setProduct(LU.get(i, k), LU.get(k, j));
                    e.subtract(product);
                }
            }
            double big = 0.0;
            for(int i=n; --i >= j; ) {
                final T e = LU.get(i, j);
                for(int k=j; --k >= 0; ) {
                    product.setProduct(LU.get(i, k), LU.get(k, j));
                    e.subtract(product);
                }
                final double tmp = v[i] * e.abs();
                if (tmp >= big) {
                    big=tmp;
                    imax=i;
                }
            }
            if(j != imax) {
                LU.swapRows(imax, j);
                evenChanges = !evenChanges;
                v[imax] = v[j];
            }
            index[j] = imax;
            
            T diag = LU.get(j, j);
            
            if(diag.isNull()) {
                diag.setIdentity();
                diag.scale(tinyValue);
            }
            
            if(j != n-1) {
                @SuppressWarnings("unchecked")
                T tmp = (T) diag.getInverse();
                for(int i=n; --i > j; ) LU.get(i, j).multiplyBy(tmp);
            }
        }
    }   
}

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


public abstract class LUDecomposition<T> implements MatrixInverter<T>, MatrixSolver<T> {
    protected AbstractMatrix<T> LU, inverse;
    protected int[] index;
    protected boolean evenChanges;
    
    public static double defaultTinyValue = 1e-200;
    
    protected LUDecomposition(AbstractMatrix<T> M) {
        this(M, defaultTinyValue);
    }
    
    protected LUDecomposition(AbstractMatrix<T> M, double tinyValue) throws SquareMatrixException {
        if(!M.isSquare()) throw new SquareMatrixException();
        LU = M.copy();
        index = new int[size()];
        evenChanges = true;
        decompose(tinyValue);
    }
    
    
    public AbstractMatrix<T> getMatrix() { return LU; }
    
    protected final int size() { return LU.rows(); }
    
    private void decompose(double tinyValue) {
        final int n = size();
        
        MatrixElement<T> e = LU.getElementInstance();
        MatrixElement<T> product = LU.getElementInstance();
        
        index = new int[n];
        evenChanges = true;
        
        final double[] v = new double[n];
        
        for(int i=n; --i >= 0; ) {
            double big = 0.0;
            for(int j=n; --j >= 0; ) {
                final double mag = e.from(i, j).abs();
                if(mag > big) big = mag;
            }
            if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
            v[i] = 1.0 / big;
        }
        for(int j=0; j<n; j++ ) {
            int imax = -1;
            
            for(int i=j; --i >= 0; ) {
                e.from(i, j);
                for(int k=i; --k >= 0; ) {
                    product.setProduct(LU.get(i, k), LU.get(k, j));
                    e.subtract(product.value());
                }
                e.applyTo(i, j);
            }
            double big = 0.0;
            for(int i=n; --i >= j; ) {
                e.from(i, j);
                for(int k=j; --k >= 0; ) {
                    product.setProduct(LU.get(i, k), LU.get(k, j));
                    e.subtract(product.value());
                }
                e.applyTo(i,  j);
              
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
            
            if(e.from(j, j).isNull()) {
                e.setIdentity();
                e.scale(tinyValue);
                e.applyTo(j, j);
            }
            
            if(j != n-1) {
                T inv = e.getInverse();
                for(int i=n; --i > j; ) {
                    product.setProduct(inv, LU.get(i, j));
                    product.applyTo(i, j);
                }
            }
        }
    }   
    
    
    @Override
    public AbstractMatrix<T> getInverseMatrix() {
        if(inverse == null) {
            inverse = LU.getMatrixInstance(size(), size(), false);
            getInverseTo(inverse);
        }
        return inverse.copy();
    }

    @Override
    public abstract void getInverseTo(AbstractMatrix<T> inverse);

}
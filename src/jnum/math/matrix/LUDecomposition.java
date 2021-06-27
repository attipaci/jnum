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


/**
 * A class for handling the LU decomposition of full 2D matrices of any generic type.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <T>       The generic type of matrix element
 */
public abstract class LUDecomposition<T> implements MatrixInverter<T>, MatrixSolver<T> {
    protected AbstractMatrix<T> LU;
    protected int[] index;
    protected boolean evenChanges;
    
    /**
     * A very small numerical value, much smaller than what any reasonable matrix element would
     * otherwise be.
     * 
     */
    public static double defaultTinyValue = 1e-200;
    
    /**
     * Constructs a new LU decomposition of a matrix.
     * 
     * @param M                         The 2D matrix that is to be decomposed.
     * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
     * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
     */
    protected LUDecomposition(AbstractMatrix<T> M) throws SquareMatrixException, SingularMatrixException {
        this(M, defaultTinyValue);
    }
    
    /**
     * Constructs a new LU decomposition of a matrix, specifying what very small numerical value should be used
     * to substitute instead of zeroes for the arithmetic
     * 
     * @param M                         The 2D matrix that is to be decomposed.
     * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
     * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
     */
    protected LUDecomposition(AbstractMatrix<T> M, double tinyValue) throws SquareMatrixException, SingularMatrixException {
        if(!M.isSquare()) throw new SquareMatrixException();
        LU = M.copy();
        index = new int[size()];
        evenChanges = true;
        decompose(tinyValue);
        LU.sanitize();
    }
    
    /**
     * Gets the decomposed L and U matrices co-existing in the same LU representation.
     * 
     * @return  The LU matrix
     */
    public AbstractMatrix<T> getMatrix() { return LU; }
    
    /**
     * Gets the square size of the parent matrix and its decomposition.
     * 
     * @return  The size (rows and cols) of the original and LU matrices.
     */
    protected final int size() { return LU.rows(); }
    
    /**
     * Gets the determinant of the parent matrix, easily calculated from its decomposition.
     * 
     * @return  The determinant of the parent matrix.
     */
    public abstract T getDeterminant();
    
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
            if(big == 0.0) throw new SingularMatrixException();
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
    
}
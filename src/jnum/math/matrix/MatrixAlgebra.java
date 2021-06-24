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

import jnum.data.IndexedEntries;
import jnum.data.image.Index2D;
import jnum.math.DotProduct;
import jnum.math.IdentityValue;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;


public interface MatrixAlgebra<M, E> extends IndexedEntries<Index2D, E>, LinearAlgebra<M>, DotProduct<M, M>, Metric<MatrixAlgebra<?, ?>>, IdentityValue {
    
    /**
     * Gets the class of elements contained in this matrix.
     * 
     * @return     The class of elements contained in this matrix.
     */
    public Class<E> getElementType();
    
    /**
     * Gets the number of rows in this matrix.
     * 
     * @return     Nmber of rows in this matrix.
     */
    public int rows();
    
    
    /**
     * Gets the number of columns in this matrix
     * 
     * @return     Number of columns in this matrix.
     */
    public int cols();
    
    /** 
     * Checks if the size matches what is expected
     * 
     * @param rows     Number of expected rows.
     * @param cols     Number of expected columns
     * @return         true if the matrix contains the same number of rows and columns as specified. Otherwise false.
     */
    public boolean isSize(int rows, int cols);
    
    
    /**
     * Checks if this matrix is equal in size to another matrix. I.e. both matrices have the same number of rows
     * and columns. (But the two matrices could have elements of very different types...)
     * 
     * @param M
     * @return
     */
    public boolean conformsTo(MatrixAlgebra<?, ?> M);
    
    /**
     * Checks if this is a square matrix (i.e. it has the same number of rows and columns.).
     * 
     * @return     <code>true</code> if this is a square matrix otherwise <code>false</code>.
     */
    public boolean isSquare();
    
    
    public boolean isDiagonal();
    
    /**
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    public E get(int i, int j);
    
    
    /**
     * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @param v        The new matrix element to set. (For object types the matrix will hold a reference
     *                 to the specified value).
     */
    public void set(int i, int j, E value);
    

   
    public MathVector<E> dot(MathVector<? extends E> v) throws ShapeException;
    
    public void dot(MathVector<? extends E> v, MathVector<E> result) throws ShapeException;
    
    public E[] dot(E[] v);
    
    public void dot(E[] v, E[] result);
    
    public void dot(double[] v, MathVector<E> result);
    
    public void dot(float[] v, MathVector<E> result);
    
    public Object dot(double[] v);
    
    public Object dot(float[] v);
    
    public void dot(RealVector v, MathVector<E> result);
    
    public MathVector<E> dot(RealVector v);
       
    
}

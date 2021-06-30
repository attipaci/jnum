/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.math.matrix;

import jnum.data.IndexedEntries;
import jnum.data.image.Index2D;
import jnum.math.DotProduct;
import jnum.math.IdentityValue;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;

/**
 * A base interface for matrix representation. It specifies a set of basic matrix operations. In other
 * words, it provides the core matrix API that all matrix implementation will provide.
 * 
 * @author Attila Kovacs
 *
 * @param <MatrixType>    The generic type of the matrix object in this algebra
 * @param <ElementType>   The generic type of the matrix element in this algebra.
 */
public interface MatrixAlgebra<MatrixType, ElementType> extends IndexedEntries<Index2D, ElementType>, LinearAlgebra<MatrixType>, DotProduct<MatrixType, MatrixType>, Metric<MatrixAlgebra<?, ?>>, IdentityValue {
    
    /**
     * Gets the class of elements contained in this matrix.
     * 
     * @return     The class of elements contained in this matrix.
     */
    public Class<ElementType> getElementType();
    
    
    /**
     * Gets a new vector of the same generic type as the element type supported by this matrix.
     * The returned vector is initialized to contain all zero valued components.
     * 
     * @param size  The size (number of component) of the vector
     * @return      A new vector of the requested size and of the same generic type as this matrix. 
     */
    public AbstractVector<ElementType> getVectorInstance(int size);
    
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
    
    
    /**
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param i       row index of matrix element
     * @param j        column index of matrix element.
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    public ElementType get(int i, int j);
    
    
    /**
     * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
     * 
     * @param i        row index of matrix element
     * @param j        column index of matrix element.
     * @param value    The new matrix element to set. (For object types the matrix will hold a reference
     *                 to the specified value).
     */
    public void set(int i, int j, ElementType value);
 
    /**
     * Checks if this matrix is a diagonal matrix, with all off-diagonal elements being zeroes.
     * 
     * @return  <code>true</code> if this matrix is of diagonal form, otherwise <code>false</code>.
     */
    public boolean isDiagonal();
    

    /**
     * Gets the largest absolute value from among all the matrix elements in this matrix.
     * 
     * @return      The largest absolute value among the matrix elements.
     */
    public double getMagnitude();
    
    /**
     * Gets the rank of the matrix, that is the dimension of the space the matrix spans, that
     * is also the number of independent rows in the matrix that cannot b expressed as a linear 
     * combination of other rows.
     * 
     * @return      the rank of this matrix
     */
    public int getRank();
    
    /**
     * Replace relatively tiny matrix element values (relative to the highest magnitude matrix element
     * with zeroes, since these tiny values may just be rounding errors...
     * 
     */
    public void sanitize();
      

    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. I.e. it returns <b>M</b> <i>dot</i> <b>v</b>.
     * 
     * @param v         Vector to operate on.
     * @return          The vector result of this matrix operated on the input vector. The returned
     *                  vector will be of the type returned by {@link #getVectorInstance(int)}.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix for the product to be calculated.
     */
    public AbstractVector<ElementType> dot(MathVector<? extends ElementType> v) throws ShapeException;
    

    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Vector to operate on.
     * @param result    The vector in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(MathVector<? extends ElementType> v, MathVector<ElementType> result) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. I.e. it returns <b>M</b> <i>dot</i> <b>v</b>.
     * 
     * @param v         Vector to operate on.
     * @return          The Java array containing the result of this matrix operated on the input vector. 
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix for the product to be calculated.
     */
    public AbstractVector<ElementType> dot(ElementType[] v) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Vector to operate on.
     * @param result    The array in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(ElementType[] v, ElementType[] result) throws ShapeException;
    
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Vector to operate on.
     * @param result    The array in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(ElementType[] v, MathVector<ElementType> result) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the real-valued vector (<b>v</b>) 
     * on the right-hand side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the 
     * specified second vector argument.
     * 
     * @param v         Real-valued vector to operate on.
     * @param result    The vector in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(double[] v, MathVector<ElementType> result) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the real-valued vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Real-valued vector to operate on.
     * @param result    The vector in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(float[] v, MathVector<ElementType> result) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the real-valued vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Real-valued vector to operate on.
     * @return          The vector result of this matrix operated on the input vector. The returned
     *                  vector will be of the type returned by {@link #getVectorInstance(int)}.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public AbstractVector<ElementType> dot(double[] v) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the real-valued vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Real-valued vector to operate on.
     * @return          The vector result of this matrix operated on the input vector. The returned
     *                  vector will be of the type returned by {@link #getVectorInstance(int)}.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public AbstractVector<ElementType> dot(float[] v) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. The result of <b>M</b> <i>dot</i> <b>v</b> is returned into the specified second
     * vector argument.
     * 
     * @param v         Real-valued vector to operate on.
     * @param result    The vector in which to return the result.
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix, or if the result vector's size does not match the number of 
     *                          columns in this matrix.
     */
    public void dot(RealVector v, MathVector<ElementType> result) throws ShapeException;
    
    /**
     * Gets the dot product of this matrix (<b>M</b>) applied to the vector (<b>v</b>) on the right-hand
     * side. I.e. it returns <b>M</b> <i>dot</i> <b>v</b>.
     * 
     * @param v         real-valued vector to operate on.
     * @return          The vector result of this matrix operated on the input vector. The returned
     *                  vector will be of the type returned by {@link #getVectorInstance(int)}
     * @throws ShapeException   If the input vector's size does not match the number of rows in this
     *                          matrix for the product to be calculated.
     */
    public AbstractVector<ElementType> dot(RealVector v) throws ShapeException;
       
    
}

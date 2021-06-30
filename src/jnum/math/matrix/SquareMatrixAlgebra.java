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

import jnum.math.IdentityValue;
import jnum.math.Inversion;


public interface SquareMatrixAlgebra<M, T> extends MatrixAlgebra<M, T>, Inversion, IdentityValue {
	
    /**
     * Gets the diagonal element at the given index. The returned value may be a primitive
     * or a reference to the Java object residing at the diagonal element. Thus, modifying
     * the returned element may or may not change the matrix itself, depending on its type.
     * In general therefore, it is dafest not to attempt modifying the returned value, unless
     * you specifically update the same diagonal element with {@link #setDiagonal(int, Object)} or
     * via {@link MatrixAlgebra#set(int, int, Object)} call later on.
     * 
     * If you do want to perform operations on the returned value, without affecting the
     * matrix itself, you should consider using {@link #copyOfDiagonal(int)} instead.
     * 
     * @param i     The diagonal element index.
     * @return      The matrix element at the requested diagonal position.
     */
    public T getDiagonal(int i);
    
    /**
     * Sets the diagonal element at the given index to the specified value.
     * 
     * @param i         The diagonal element index.
     * @param value     The new matrix element to set at the requested diagonal position.
     */
    public void setDiagonal(int i, T value);
    
    /**
     * Gets an independent (deep) copy the diagonal element at the given index.
     * Since that copy is guaranteed to be fully decoupled from the data contained
     * in the matrix, you are free to manipulate it without risking an undesired change
     * to the original matrix element.
     * 
     * @param i     The diagonal element index.
     * @return      The matrix element at the requested diagonal position.
     */
    public T copyOfDiagonal(int i);
    
    /**
     * Adds an increment value to a diagonal element of the same generic type in the matrix.
     * 
     * @param i     The diagonal element index.
     * @param value Increment value.
     */
    public void addDiagonal(int i, T value);
    
    /**
     * Scales a diagonal element in the matrix with the given factor.
     * 
     * @param i         The diagonal element index.
     * @param factor    scaling factor
     */
    public void scaleDiagonal(int i, double factor);

    /**
     * Sets a diagonal element in the matrix to zero value.
     * 
     * @param i     The diagonal element index.
     */
    public void zeroDiagonal(int i);
    
    /**
     * Checks if a diagonal element in the matrix is zero.
     * 
     * @param i     The diagonal element index.
     * @return      <code>true</code> if the specified diagonal element is zero valued. Otherwise <code>false</code>.
     */
    public boolean isNullDiagonal(int i);
    
    /**
     * Gets the trace of this matrix, i.e. the sum of its diagonal elements.
     * 
     * @return      The trace of this matrix.
     */
    public T getTrace();
    
    /**
     * Checks if the trace of this matrix is zero, within rounding errors.
     * 
     * @return  <code>true</code> if the trace is essentially zero. Otherwise <code>false</code>
     */
    public boolean isTraceless();
    
    
    /**
     * Gets the determinant of this matrix. For general matrices the determinant may be costly
     * to calculate. For example, the {@link AbstractMatrix} implementattion is to 
     * calculate it via an LU decomposition. Therefore, if there are other operations
     * that require LU decomposition, you could consider getting the LU decomposition
     * first via {@link AbstractMatrix#getLUDecomposition()}, and then get
     * the determinant from it via {@link LUDecomposition#getDeterminant()}. The
     * determinant can also be obtained using {@link EigenSystem#getDeterminant()}
     * once the eigenvalues and eigenvectors have been obtained otherwise (which 
     * is a costly calculation in itself, and not recommended for calculating the determiant
     * alone).
     * 
     * 
     * @return the determinant of this matrix.
     */
    public T getDeterminant();
    
    /**
     * Gets the inverse of this matrix if possible
     * 
     * @return      the inverse of this matrix
     * @throws SingularMatrixException  If the matrix cannot be inverted because it is singular (degenerate)
     */
    SquareMatrixAlgebra<M, T> getInverse() throws SingularMatrixException;
    
    /**
     * Adds the identity matrix scaled with the specified factor to this matrix.
     * 
     * @param scaling   Scaling of the identity matrix to be added to this one.
     */
	public void addIdentity(double scaling);

	/**
     * Adds the identity matrix to this matrix.
     */
	public void addIdentity();
	
	/**
     * Subtracys the identity matrix from this matrix.
     */
	public void subtractIdentity();

}

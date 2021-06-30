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

import jnum.math.MathVector;

/**
 * A class preresenting the eigenvalues and eigenvectors of a square matrix. The eigen representation
 * is equivalent to the diagonalized form of the matrix from which it is derived, with the change
 * of basis transformations to and from the orihinal basis and the diagonalized matrix basis.
 * 
 * @author Attila Kovacs
 *
 * @param <ElementType>   Generic matrix element type
 * @param <ValueType>     Generic eigenvalue type
 */
public interface EigenSystem<ElementType, ValueType> {
    
    /**
     * Gets the eigenvalues for this system.
     * 
     * @return  the eigenvalues (in matching order to the eigenvectors returned by {@link #getEigenVectors()}.
     */
    public AbstractVector<ValueType> getEigenValues();
    
    /**
     * Gets the unnormalized eigenvectors for this system.
     * 
     * @return  the unnormalized eigenvectors (in matching order to the eigenvectors returned by {@link #getEigenValues()}.
     */
    public AbstractVector<ElementType>[] getEigenVectors();
    
    /**
     * Gets the change of basis matrix that converts vectors from the original basis to the eigenbasis
     * of the diagonalized matrix.
     *  
     * @return      The change of basis matrix from the original to the eigenbasis.
     */
    public AbstractMatrix<ElementType> toEigenBasis();
    
    /**
     * Gets the change of basis matrix that converts vectors from the eigenbasis back to the basis
     * of the original matrix.
     *  
     * @return      The change of basis matrix from the eigenbasis to the basis of the original matrix.
     */
    public AbstractMatrix<ElementType> fromEigenBasis();
    
    /**
     * Converts a vector from the original basis to the eigenbasis.
     * 
     * @param v     Vector in the basis of the original matrics
     * @return      Same vector in the eigenbasis of the diagonalized matrix.
     */
    public AbstractVector<ElementType> toEigenBasis(MathVector<? extends ElementType> v);
    
    /**
     * Converts a vector from the eigenbasis to the original basis.
     * 
     * @param v     Vector in the eigenbasis.
     * @return      Same vector in the basis of the original matrix.
     */
    public AbstractVector<ElementType> fromEigenBasis(MathVector<? extends ElementType> v);
 
    /**
     * Gets the determiants, simply as the product of the eigenvalues.
     * 
     * @return      The matrix determinant.
     */
    public ValueType getDeterminant();
    
    /**
     * Gets the diagonalized matrix form, in which the eigenvalues populate
     * the diagonal matrix elements.
     * 
     * @return      The diagonalized matrix.
     */
    public DiagonalMatrix<ValueType> getDiagonalMatrix();
}

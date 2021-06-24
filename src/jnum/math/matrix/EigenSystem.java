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
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <E>   Generic matrix element type
 * @param <V>   Generic eigenvalue type
 */
public interface EigenSystem<E, V> {
    
    /**
     * Returns the eigenvalues for this system.
     * 
     * @return  the eigenvalues (in matching order to the eigenvectors returned by {@link #getEigenVectors()}.
     */
    public MathVector<V> getEigenValues();
    
    /**
     * Returns the unnormalized eigenvectors for this system.
     * 
     * @return  the unnormalized eigenvectors (in matching order to the eigenvectors returned by {@link #getEigenValues()}.
     */
    public MathVector<E>[] getEigenVectors();
    
    
    public AbstractMatrix<E> toEigenBasis();
    
    public AbstractMatrix<E> fromEigenBasis();
    
    public MathVector<E> toEigenBasis(MathVector<E> v);
    
    public MathVector<E> fromEigenBasis(MathVector<E> v);
 
    // TODO refine eigenvalues/vectors via reverse iteration method.
}

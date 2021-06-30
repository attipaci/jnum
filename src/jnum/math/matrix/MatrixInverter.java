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

/**
 * Interface for obtaining a matrix inverse via some method or another.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   The generic type matrix element (both in the matrix and its inverse).
 */
public interface MatrixInverter<T> extends MatrixSolver<T> {
    
    /**
     * Gets the inverse of a matrix as a new independent instance. If the implementing class has
     * a readyly calculated inverse matrix, it should return a copy of that, to ensure
     * that any operations performed on the returned value do not corrupt the internally
     * stored inverse.
     * 
     * @return  An independent instance of the inverse matrix, that the caller should be
     *          free to manipulate to their pleasure.
     */
    public AbstractMatrix<T> getInverseMatrix();
    
    
}

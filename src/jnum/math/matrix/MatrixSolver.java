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
 * Interface for solving matrix equations of the form y = M * x, finding vector x given vector y 
 * for a generic matrix M.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   Generic type of elements in the matrix and vectors. 
 */
public interface MatrixSolver<T> {
    
    /**
     * Returns the vector x given y, in y = M * x for some matrix M.
     * 
     * @param y     The vector y.
     * @return      The vector x as a simple array of elements.
     */
    public abstract T[] solveFor(T[] y);
    
    /**
     * Returns the vector x given y, in y = M * x for some matrix M.
     * 
     * @param y     The vector y.
     * @param x     a simple array of elements in which to return x.
     */
    public abstract void solveFor(T[] y, T[] x);
   
    /**
     * Returns the vector x given y, in y = M * x for some matrix M.
     * 
     * @param y     The vector y.
     * @return      The vector x.
     */
    public abstract AbstractVector<T> solveFor(MathVector<? extends T> y);
    
    /**
     * Returns the vector x given y, in y = M * x for some matrix M.
     * 
     * @param y     The vector y
     * @param x     generic type vector in which to return x.
     */
    public abstract void solveFor(MathVector<? extends T> y, MathVector<T> x);
       
}

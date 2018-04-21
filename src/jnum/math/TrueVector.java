/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

import jnum.math.matrix.AbstractMatrix;

public interface TrueVector<T> extends Coordinates<T>, AbsoluteValue, Normalizable, Inversion, Metric<TrueVector<? extends T>>, LinearAlgebra<TrueVector<? extends T>> {
     
    public T dot(Coordinates<? extends T> v); // TODO default method.
    
    public void orthogonalizeTo(TrueVector<? extends T> v); // TODO default method.
    
    public void projectOn(final TrueVector<? extends T> v); // TODO default method.
    
    public void reflectOn(final TrueVector<? extends T> v); // TODO default method. 
    
    public AbstractMatrix<T> asRowVector(); // TODO default method.
   
    public AbstractMatrix<T> asColumnVector(); // TODO default method.
    
    public void fill(T value); // TODO default method.
    
    public void setValues(T ... values); // TODO default method.
    
}

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

import jnum.math.DotProduct;
import jnum.math.IdentityValue;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;
import jnum.math.Product;


public interface MatrixAlgebra<M, E> extends LinearAlgebra<M>, Product<M, M>, DotProduct<M, M>, Metric<M>, IdentityValue {
    
    public MathVector<E> dot(MathVector<E> v) throws ShapeException;
    
    public void dot(MathVector<E> v, MathVector<E> result) throws ShapeException;
    
    public E[] dot(E[] v);
    
    public void dot(E[] v, E[] result);
    
    public void dot(double[] v, MathVector<E> result);
    
    public void dot(float[] v, MathVector<E> result);
    
    public Object dot(double[] v);
    
    public Object dot(float[] v);
    
    public void dot(RealVector v, MathVector<E> result);
    
    public MathVector<E> dot(RealVector v);
    
    
}

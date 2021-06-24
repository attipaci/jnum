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

package jnum.math;

import java.util.stream.Collector;

import jnum.math.matrix.AbstractMatrix;

public interface MathVector<T> extends Coordinates<T>, AbsoluteValue, Normalizable, Inversion, Metric<MathVector<? extends T>>, LinearAlgebra<MathVector<? extends T>> {
     
    public void multiplyByComponentsOf(Coordinates<? extends T> v); // TODO default method.
    
    public void incrementValue(int idx, T increment);
    
    public T dot(Coordinates<? extends T> v); // TODO default method.
    
    public T dot(T[] v); // TODO default method.
    
    public void orthogonalizeTo(MathVector<? extends T> v); // TODO default method.
    
    public void projectOn(final MathVector<? extends T> v); // TODO default method.
    
    public void reflectOn(final MathVector<? extends T> v); // TODO default method. 
    
    public AbstractMatrix<T> asRowVector(); // TODO default method.
   
    public AbstractMatrix<T> asColumnVector(); // TODO default method.
    
    public void fill(T value); // TODO default method.
    
    public void setValues(@SuppressWarnings("unchecked") T ... values); // TODO default method.   
    
    public static <T, V extends MathVector<T>> Collector<V, V, V> sum(Class<V> cl) {
        return Collector.of(
                () -> {
                    try { return cl.getDeclaredConstructor().newInstance(); }
                    catch(Exception e) { return null; }
                },
                (partial, point) -> partial.add(point),
                (sum, partial) -> { sum.add(partial); return sum; },
                Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH
                );
    }

}

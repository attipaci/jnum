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

package jnum.data;

import java.io.Serializable;

import jnum.Copiable;
import jnum.NonConformingException;
import jnum.math.Additive;
import jnum.math.Metric;
import jnum.math.Modulus;
import jnum.math.Multiplicative;
import jnum.math.Ratio;
import jnum.math.MathVector;

public interface Index<T extends Index<T>> extends Serializable, Cloneable, Copiable<T> ,
    Additive<T>, Multiplicative<T>, Ratio<T, T>, Modulus<T>, Metric<T> {
    
    public int getVolume();
    
    public int dimension();
    
    public int getValue(int dim) throws IndexOutOfBoundsException;
    
    public void setValue(int dim, int value) throws IndexOutOfBoundsException;
    
    public void fill(int value);
    
    public void zero(); // fill(0);
    
    /**
     * 
     * @param dim the component dimension.
     * @return the incremented index component in the selected dimension.
     */
    public int increment(int dim);
    
    /**
     * 
     * @param dim the component dimension.
     * @return the decremented index component in the selected dimension.
     */
    public int decrement(int dim);
    
    public void reverseTo(T other);
    
    public T getReversed();
    
    public void toVector(MathVector<Double> v) throws NonConformingException; 
    
    public String toString(String separator);
    
}

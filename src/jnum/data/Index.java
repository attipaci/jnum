/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data;

import java.io.Serializable;

import jnum.Copiable;
import jnum.NonConformingException;
import jnum.math.Additive;
import jnum.math.Modulus;
import jnum.math.Multiplicative;
import jnum.math.Ratio;
import jnum.math.TrueVector;

public interface Index<T extends Index<T>> extends Serializable, Cloneable, Copiable<T> ,
    Additive<T>, Multiplicative<T>, Ratio<T, T>, Modulus<T>{
    
    public int getVolume();
    
    public int dimension();
    
    public int getValue(int dim) throws IndexOutOfBoundsException;
    
    public void setValue(int dim, int value) throws IndexOutOfBoundsException;
    
    public void reverseTo(T other);
    
    public T getReversed();
    
    public void toVector(TrueVector<Double> v) throws NonConformingException; 
    
}

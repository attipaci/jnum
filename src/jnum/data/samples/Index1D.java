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

package jnum.data.samples;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.data.index.AbstractIndex;
import jnum.math.MathVector;

public class Index1D extends AbstractIndex<Index1D> {
    /**
     * 
     */
    private static final long serialVersionUID = 6394209570805373325L;
    
    private int i;
    
   
    public Index1D() { this(0); }
    
    public Index1D(int i) { set(i); }
    
    public void set(int i) { this.i = i; }
    
    public int i() { return i; }
    

    @Override
    public void multiplyBy(Index1D factor) {
        i *= factor.i();
    }

    @Override
    public void setProduct(Index1D a, Index1D b) {
        i = a.i() * b.i();
    }

    @Override
    public void setRatio(Index1D numerator, Index1D denominator) {
        i = ExtraMath.roundupRatio(numerator.i(), denominator.i());
    }

    @Override
    public void modulo(Index1D argument) {
        i %= argument.i();
    }

    @Override
    public int getVolume() {
        return i;
    }

    @Override
    public void add(Index1D o) {
        i += o.i;
    }

    @Override
    public void subtract(Index1D o) {
        i -= o.i;
    }

    @Override
    public void setSum(Index1D a, Index1D b) {
        i = a.i + b.i;
    }

    @Override
    public void setDifference(Index1D a, Index1D b) {
        i = a.i - b.i;
    }

    @Override
    public int dimension() {
        return 1;
    }

    @Override
    public int getValue(int dim) throws IndexOutOfBoundsException {
       if(dim == 0) return i;
       throw new IndexOutOfBoundsException(Integer.toString(dim));
    }

    @Override
    public void setValue(int dim, int value) {
        if(dim == 0) i = value;
        else throw new IndexOutOfBoundsException(Integer.toString(dim));
    }

    @Override
    public void toVector(MathVector<Double> v) throws NonConformingException {
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension());
        v.setComponent(0, (double) i);
    }


}

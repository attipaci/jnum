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

package jnum.data.cube;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.data.AbstractIndex;
import jnum.math.MathVector;

public class Index3D extends AbstractIndex<Index3D> {
    /**
     * 
     */
    private static final long serialVersionUID = -2705961475758088763L;
    
    private int i, j, k;
    
    public Index3D() { this(0, 0, 0); }
    
    public Index3D(int i, int j, int k) {
        set(i, j, k);
    }
    
    public final int i() { return i; }
    
    public final int j() { return j; }
    
    public final int k() { return k; }
    
    public final void set(final int i, final int j, final int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
    
    public final void setI(final int value) {
        i = value;
    }
    
    public final void setJ(final int value) {
        j = value;
    }
    
    public final void setK(final int value) {
        k = value;
    }

    @Override
    public void multiplyBy(Index3D factor) {
        i *= factor.i();
        j *= factor.j();
        k *= factor.k();
    }

    @Override
    public void setProduct(Index3D a, Index3D b) {
        i = a.i() * b.i();
        j = a.j() * b.j();
        k = a.k() * b.k();
    }

    @Override
    public void setRatio(Index3D numerator, Index3D denominator) {
        i = ExtraMath.roundupRatio(numerator.i(), denominator.i());
        j = ExtraMath.roundupRatio(numerator.j(), denominator.j());
        k = ExtraMath.roundupRatio(numerator.k(), denominator.k());
    }

    @Override
    public void modulo(Index3D argument) {
        i %= argument.i();
        j %= argument.j();
        k %= argument.k();
    }

    @Override
    public int getVolume() {
        return i * j * k;
    }
    
    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public int getValue(int dim) throws IndexOutOfBoundsException {
        switch(dim) {
        case 0 : return i;
        case 1 : return j;
        case 2 : return k;
        }
        throw new IndexOutOfBoundsException(Integer.toString(dim));
    }

    @Override
    public void setValue(int dim, int value) throws IndexOutOfBoundsException {
        switch(dim) {
        case 0 : i = value; break;
        case 1 : j = value; break;
        case 2 : k = value; break;        
        default: throw new IndexOutOfBoundsException(Integer.toString(dim));
        }
    }

    @Override
    public void add(Index3D o) {
        i += o.i;
        j += o.j;
        k += o.k;
    }

    @Override
    public void subtract(Index3D o) {
        i -= o.i;
        j -= o.j;
        k -= o.k;
    }

    @Override
    public void setSum(Index3D a, Index3D b) {
        i = a.i + b.i;
        j = a.j + b.j;
        k = a.k + b.k;
    }

    @Override
    public void setDifference(Index3D a, Index3D b) {
        i = a.i - b.i;
        j = a.j - b.j;
        k = a.k - b.k;
    }

    @Override
    public void toVector(MathVector<Double> v) throws NonConformingException {  
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension());  
        v.setComponent(0, (double) i);
        v.setComponent(1, (double) j);
        v.setComponent(3, (double) k);
    }
}

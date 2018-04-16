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


package jnum.data.image;


import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.data.AbstractIndex;
import jnum.math.TrueVector;
import jnum.math.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Index2D.
 */
public class Index2D extends AbstractIndex<Index2D> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -364862939591997831L;

    /** The j. */
    private int i,j;

    /**
     * Instantiates a new index2 d.
     */
    public Index2D() { this(0, 0); }

    /**
     * Instantiates a new index2 d.
     *
     * @param i the i
     * @param j the j
     */
    public Index2D(int i, int j) {
        set(i, j);
    }

    /**
     * Instantiates a new index2 d.
     *
     * @param index the index
     */
    public Index2D(Vector2D index) {
        this((int)Math.round(index.x()), (int)Math.round(index.y()));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ i ^ j;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Index2D)) return false;

        Index2D index = (Index2D) o;
        if(index.i != i) return false;
        if(index.j != j) return false;
        return true;		
    }

    /**
     * Sets the.
     *
     * @param i the i
     * @param j the j
     */
    public void set(int i, int j) { this.i = i; this.j = j; }

    
    /**
     * I.
     *
     * @return the int
     */
    public final int i() { return i; }

    /**
     * J.
     *
     * @return the int
     */
    public final int j() { return j; }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return i + "," + j;
    }

    @Override
    public void multiplyBy(Index2D factor) {
        i *= factor.i();
        j *= factor.j();
    }

    @Override
    public void setProduct(Index2D a, Index2D b) {
        i = a.i() * b.i();
        j = a.j() * b.j();
    }

    @Override
    public void setRatio(Index2D numerator, Index2D denominator) {
        i = ExtraMath.roundupRatio(numerator.i(), denominator.i());
        j = ExtraMath.roundupRatio(numerator.j(), denominator.j());
    }

    @Override
    public void modulo(Index2D argument) {
        i %= argument.i();
        j %= argument.j();
    }

    @Override
    public int getVolume() {
        return i * j;
    }

    @Override
    public int dimension() {
        return 2;
    }

    @Override
    public int getValue(int dim) throws IndexOutOfBoundsException {
        if(dim == 0) return i;
        else if(dim == 1) return j;
        else throw new IndexOutOfBoundsException(Integer.toString(dim));
        
    }

    @Override
    public void setValue(int dim, int value) throws IndexOutOfBoundsException {
        if(dim == 0) i = value;
        else if(dim == 1) j = value;
        else throw new IndexOutOfBoundsException(Integer.toString(dim));
        
    }

    @Override
    public void add(Index2D o) {
        i += o.i;
        j += o.j;
    }

    @Override
    public void subtract(Index2D o) {
        i -= o.i;
        j -= o.j;
    }

    @Override
    public void setSum(Index2D a, Index2D b) {
        i = a.i + b.i;
        j = a.j + b.j;
    }

    @Override
    public void setDifference(Index2D a, Index2D b) {
        i = a.i - b.i;
        j = a.j - b.j;
    }
    
    @Override
    public void toVector(TrueVector<Double> v) throws NonConformingException {  
        if(v.size() != dimension()) throw new NonConformingException("Size mismatch " + v.size() + " vs. " + dimension());  
        v.setComponent(0, (double) i);
        v.setComponent(1, (double) j);
    }

}

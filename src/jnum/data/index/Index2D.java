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


package jnum.data.index;

import jnum.math.Vector2D;

/**
 * An index in 2D space, such as (<i>i</i>, <i>j</i>).
 * 
 * @author Attila Kovacs
 *
 */
public class Index2D extends AbstractIndex<Index2D> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -364862939591997831L;

    /** the first index value */
    private int i;
    
    /** the second index value */
    private int j;

    /**
     * Instantiates a new 2D index with the default zero components.
     */
    public Index2D() { this(0, 0); }

    /**
     * Instantiates a new 2D index with the specified initial components.
     * 
     * @param i     the initial value for the index in the first dimension.
     * @param j     the initial value for the index in the second dimension.
     */
    public Index2D(int i, int j) {
        set(i, j);
    }

    /**
     * Instantiates a new 2D index that is nearest to the fractional index location represented by a 2D vector. 
     * 
     * @param index     the fractional 2D index that is rounded to create this integer 2D index instance.
     */
    public Index2D(Vector2D index) {
        this((int)Math.round(index.x()), (int)Math.round(index.y()));
    }

    /**
     * Sets a new index location.
     * 
     * @param i     the new index location in the first dimension.
     * @param j     the new index location in the second dimension.
     * 
     * @see #i()
     * @see #j()
     * @see #setI(int)
     * @see #setJ(int)
     */
    public void set(int i, int j) { this.i = i; this.j = j; }

    /**
     * Returns the index component in the first dimension.
     * 
     * @return      the index component in the first dimension.
     * 
     * @see #j()
     * @see #set(int, int)
     * @see #setI(int)
     */
    public final int i() { return i; }

    /**
     * Returns the index component in the second dimension.
     * 
     * @return      the index component in the second dimension.
     * 
     * @see #i()
     * @see #set(int, int)
     * @see #setJ(int)
     */
    public final int j() { return j; }

    /**
     * Sets a new value for the first component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the first index component.
     */
    public final void setI(final int value) {
        i = value;
    }
    
    /**
     * Sets a new value for the second component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the second index component.
     */
    public final void setJ(final int value) {
        j = value;
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

}

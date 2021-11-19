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

/**
 * An index in 3D space, such as (<i>i</i>, <i>j</i>, <i>k</i>).
 * 
 * @author Attila Kovacs
 *
 */
public class Index3D extends AbstractIndex<Index3D> {
    /**
     * 
     */
    private static final long serialVersionUID = -2705961475758088763L;
    
    /** the first index value */
    private int i;
    
    /** the second index value */
    private int j;
    
    /** the third index value */
    private int k;
    
    /**
     * Instantiates a new 3D index with the default zero components.
     */
    public Index3D() { this(0, 0, 0); }
    
    /**
     * Instantiates a new 3D index with the specified initial components.
     * 
     * @param i     the initial value for the index in the first dimension.
     * @param j     the initial value for the index in the second dimension.
     * @param k     the initial value for the index in the third dimension.
     */
    public Index3D(int i, int j, int k) {
        set(i, j, k);
    }
    
    /**
     * Returns the index component in the first dimension.
     * 
     * @return      the index component in the first dimension.
     * 
     * @see #j()
     * @see #k()
     * @see #set(int, int, int)
     * @see #setI(int)
     */
    public final int i() { return i; }
    
    /**
     * Returns the index component in the second dimension.
     * 
     * @return      the index component in the third dimension.
     * 
     * @see #i()
     * @see #k()
     * @see #set(int, int, int)
     * @see #setJ(int)
     */
    public final int j() { return j; }
    
    /**
     * Returns the index component in the third dimension.
     * 
     * @return      the index component in the third dimension.
     * 
     * @see #i()
     * @see #j()
     * @see #set(int, int, int)
     * @see #setK(int)
     */
    public final int k() { return k; }
    
    /**
     * Sets a new index location.
     * 
     * @param i     the new index location in the first dimension.
     * @param j     the new index location in the second dimension.
     * @param k     the new index location in the third dimension.
     * 
     * @see #i()
     * @see #j()
     * @see #k()
     * @see #setI(int)
     * @see #setJ(int)
     * @see #setK(int)
     */
    public final void set(final int i, final int j, final int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
    
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
    
    /**
     * Sets a new value for the third component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the third index component.
     */
    public final void setK(final int value) {
        k = value;
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

  
}

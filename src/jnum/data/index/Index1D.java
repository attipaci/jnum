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
 * An index in 1D space. Essentially a wrapped integer.
 * 
 * @author Attila Kovacs
 *
 */
public class Index1D extends AbstractIndex<Index1D> {
    /**
     * 
     */
    private static final long serialVersionUID = 6394209570805373325L;
    
    /**
     * The index value
     * 
     */
    private int i;
    
    /**
     * Instantiates a new 1D index with the default zero value.
     */
    public Index1D() { this(0); }
    
    /**
     * Instantiates a new 1D index with the specified initial value.
     * 
     * @param i     the initial value for the new index instance.
     */
    public Index1D(int i) { set(i); }
    
    /**
     * Sets a new index location.
     * 
     * @param i     the new index location.
     * 
     * @see #i()
     */
    public void set(int i) { this.i = i; }
    
    /**
     * Returns the index location, for the first (and only) component in this index.
     * 
     * @return  the index value
     * 
     * @see #set(int)
     */
    public int i() { return i; }
    

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

}
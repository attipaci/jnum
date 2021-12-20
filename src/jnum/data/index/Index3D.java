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

import jnum.PointOp;

/**
 * An index in 3D space, such as (<i>i</i>, <i>j</i>, <i>k</i>).
 * 
 * @author Attila Kovacs
 *
 */
public class Index3D extends Index<Index3D> {
    /**
     * 
     */
    private static final long serialVersionUID = -2705961475758088763L;
    
    /**
     * Instantiates a new 3D index with the default zero components.
     */
    public Index3D() { super(3); }
    
    /**
     * Instantiates a new 3D index with the specified initial components.
     * 
     * @param i     the initial value for the index in the first dimension.
     * @param j     the initial value for the index in the second dimension.
     * @param k     the initial value for the index in the third dimension.
     */
    public Index3D(int i, int j, int k) {
        this();
        set(i, j, k);
    }
    
    /**
     * Returns the index component in the first dimension.
     * 
     * @return      the index component in the first dimension.
     * 
     * @see #j()
     * @see #k()
     * @see #set(int...)
     * @see #setI(int)
     */
    public final int i() { return getComponent(0); }
    
    /**
     * Returns the index component in the second dimension.
     * 
     * @return      the index component in the third dimension.
     * 
     * @see #i()
     * @see #k()
     * @see #set(int...)
     * @see #setJ(int)
     */
    public final int j() { return getComponent(1); }
    
    /**
     * Returns the index component in the third dimension.
     * 
     * @return      the index component in the third dimension.
     * 
     * @see #i()
     * @see #j()
     * @see #set(int...)
     * @see #setK(int)
     */
    public final int k() { return getComponent(2); }
    
    /**
     * Sets a new value for the first component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the first index component.
     */
    public final void setI(final int value) {
        setComponent(0, value);
    }
    
    /**
     * Sets a new value for the second component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the second index component.
     */
    public final void setJ(final int value) {
        setComponent(1, value);
    }
    
    /**
     * Sets a new value for the third component only, leaving the other two components unchanged.
     * 
     * @param value     the new value for the third index component.
     */
    public final void setK(final int value) {
        setComponent(2, value);
    }

    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op, Index3D to) {
        final int i = i();
        final int j = j();
        final int k = k();
        
        final Index3D index = new Index3D();
        for(int i1=to.i(); --i1 >= i; ) for(int j1=to.j(); --j1 >= j; ) for(int k1=to.k(); --k1 >= k; ) {
            index.set(i1,  j1,  k1);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }
    
}

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
import jnum.math.Vector2D;


/**
 * An index in 2D space, such as (<i>i</i>, <i>j</i>).
 * 
 * @author Attila Kovacs
 *
 */
public class Index2D extends Index<Index2D> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -364862939591997831L;

    /**
     * Instantiates a new 2D index with the default zero components.
     */
    public Index2D() { 
        super(2);
    }

    /**
     * Instantiates a new 2D index with the specified initial components.
     * 
     * @param i     the initial value for the index in the first dimension.
     * @param j     the initial value for the index in the second dimension.
     */
    public Index2D(int i, int j) {
        this();
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
     * Returns the index component in the first dimension.
     * 
     * @return      the index component in the first dimension.
     * 
     * @see #j()
     * @see #set(int...)
     * @see #setI(int)
     */
    public final int i() { return getComponent(0); }

    /**
     * Returns the index component in the second dimension.
     * 
     * @return      the index component in the second dimension.
     * 
     * @see #i()
     * @see #set(int...)
     * @see #setJ(int)
     */
    public final int j() { return getComponent(1); }

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
    
    
    @Override
    public <ReturnType> ReturnType loop(final PointOp<Index2D, ReturnType> op, Index2D to) {
        final int i = i();
        final int j = j();
        final Index2D index = new Index2D();
        for(int i1=to.i(); --i1 >= i; ) for(int j1=to.j(); --j1 >= j; ) {
            index.set(i1, j1);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }

 
}

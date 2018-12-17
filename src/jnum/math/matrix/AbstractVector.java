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

package jnum.math.matrix;

import java.io.Serializable;

import jnum.Copiable;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.math.TrueVector;



public abstract class AbstractVector<T> implements TrueVector<T>, Serializable, 
Cloneable, Copiable<AbstractVector<T>> {

	private static final long serialVersionUID = 785522803183758105L;

	public AbstractVector() {}

	public abstract Class<T> getType();
	

	public abstract Object getData();
	

	public abstract void setData(Object data);
		
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
    @Override
	public AbstractVector<T> clone() {
		try { return (AbstractVector<T>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }		
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public AbstractVector<T> copy() {
		AbstractVector<T> copy = clone();
		try { 
			copy.setData(ArrayUtil.copyOf(getData())); 
			return copy;
		}
		catch(Exception e) { Util.error(this, e); }
		return null;
	}


	/* (non-Javadoc)
	 * @see kovacs.math.AbsoluteValue#abs()
	 */
	@Override
	public double abs() {
		return Math.sqrt(absSquared());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Normalizable#normalize()
	 */
	@Override
	public void normalize() {
		scale(1.0/abs());
	}
		

	public abstract void setSize(int size);
	

	public void assertSize(int size) { 
		if(size() != size) setSize(size);		
	}

	@Override
    public void invert() {
	    scale(-1.0);
	}
	
	
	   
    @Override
    public void reflectOn(final TrueVector<? extends T> v) {
        AbstractVector<T> ortho = copy();
        ortho.orthogonalizeTo(v);
        addScaled(ortho, -2.0);        
    }
    
	
}

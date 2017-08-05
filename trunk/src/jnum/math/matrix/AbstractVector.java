/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
import jnum.NonConformingException;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.math.Coordinates;
import jnum.math.TrueVector;



// TODO: Auto-generated Javadoc
/**
 * The Class AbstractVector.
 *
 * @param <T> the generic type
 */
public abstract class AbstractVector<T> implements TrueVector<T>, Serializable, 
Cloneable, Copiable<AbstractVector<T>> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 785522803183758105L;

	/**
	 * Instantiates a new abstract vector.
	 */
	public AbstractVector() {}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract Class<T> getType();
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public abstract Object getData();
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public abstract void setData(Object data);
		
	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(Exception e) { return null; }		
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AbstractVector<T> copy() {
		AbstractVector<T> copy = (AbstractVector<T>) clone();
		try { 
			copy.setData(ArrayUtil.copyOf(getData())); 
			return copy;
		}
		catch(Exception e) { Util.error(this, e); }
		return null;
	}

	/**
	 * Check matching.
	 *
	 * @param v the v
	 */
	protected void checkMatching(Coordinates<? extends T> v) throws NonConformingException {
		if(v.size() != size()) throw new NonConformingException("Mismatched " + getClass().getName() + "s.");		
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

	/**
	 * Orthogonalize to.
	 *
	 * @param v the v
	 */
	public abstract void orthogonalizeTo(TrueVector<? extends T> v);
	
	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public abstract void setSize(int size);
	
	/**
	 * Assert size.
	 *
	 * @param size the size
	 */
	public void assertSize(int size) { 
		if(size() != size) setSize(size);		
	}

	
	
	
}

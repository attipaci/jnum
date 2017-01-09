/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2010 Attila Kovacs 

package jnum.data;

import java.io.Serializable;

import jnum.Copiable;
import jnum.math.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Index2D.
 */
public class Index2D implements Serializable, Cloneable, Copiable<Index2D> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -364862939591997831L;
	
	/** The j. */
	private int i,j;
	
	/**
	 * Instantiates a new index2 d.
	 */
	public Index2D() {}
	
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
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public Index2D copy() { return (Index2D) clone(); } 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ i ^ ~j;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Index2D)) return false;
		if(!super.equals(o)) return false;
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

}

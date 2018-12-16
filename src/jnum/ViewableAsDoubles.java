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


package jnum;

/**
 * A simple interface for objects that can be represented as an array, of 1 or more dimensions, of primitive 
 * double-precission values.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 */
public interface ViewableAsDoubles {

    /**
     * Return a view of this object as an array of doubles, in 1 or more dimensions 
     * (e.g. <code>double[]</code> or <code>double[][]</code> or ...) that
     * 
     * @return  a primitive <code>double</code> array (1 or more dimensions) that represents the contents of the implementing object.
     */
	public Object viewAsDoubles();
	
	/**
	 * Populates the supplied <code>double</code> array (with 1 or more dimensions) with the representation of the
	 * implementing object.
	 * 
	 * 
	 * @param view         A pre-supplied <code>double</code> array that will hold the result.
	 * @throws IllegalArgumentException    If the supplied object is not a primitive <code>double</code> array,
	 *                                     or is of the wrong dimension, or is of the wrong size to hold the 
	 *                                     implementing object's representation.
	 */
	public void viewAsDoubles(Object view) throws IllegalArgumentException;
	
	/**
	 * Re-create the implementing object's data from the supplied primitive <code>double</code> array (of 1 or more dimensions).
	 * 
	 * 
	 * @param array        The <code>double</code> array that contains the new data for the implementing object.
	 * @throws IllegalArgumentException    If the supplied object is not a primitive <code>double</code> array,
     *                                     or is of the wrong dimension, or is of the wrong size to hold the 
     *                                     implementing object's representation.
	 */
	public void createFromDoubles(Object array) throws IllegalArgumentException;
	
}

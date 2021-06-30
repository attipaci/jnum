/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;

/**
 * The interface that allows copying the contents/data of one object into another object. The copied data must be
 * fully independent from the original object's data. I.e. changes to the impelenting object after the copying should
 * not affect the object whose data was copied from, and vice versa (see {@link Copiable}). 
 *
 * @param <T> the generic type of the object whose data can be copied into the implementing class.
 */
public interface CopyCat<T> {
	
	/**
	 * Copies the data from the specified <code>template</code> object into the caller. The copied data must be
	 * fully independent from the original object's data. I.e. changes to the caller object after the copying should
	 * not affect the object where data was copied from, and vice versa (see {@link Copiable#copy()}). 
	 * 
	 * 
	 * @param template the object whose data is to be copied into this object.
	 */
	public void copy(T template); 
}

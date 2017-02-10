/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.math;

// TODO: Auto-generated Javadoc
/**
 * An interface for all objects that implement an inverse value under some multiplication. Thus b is the inverse of a if
 * a*b = 1, i.e. the product of the object and its inverse is the identity value.
 *
 * @param <Type> the generic type
 */
public interface InverseValue<Type> {

	/**
	 * Get the inverse value.
	 *
	 * @return the inverse of this object
	 */
	public Type getInverse(); 
	
	/**
	 * Set this object to be its inverse.
	 */
	public void inverse();
	
}

/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data;

import java.util.Iterator;

// TODO: Auto-generated Javadoc
/**
 * The Interface DataManager.
 *
 * @param <T> the generic type
 */
public interface DataIterator<T> extends Iterator<T> {

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Object getData();
	
	/**
	 * Sets the element.
	 *
	 * @param value the new element
	 */
	public void setElement(T value);
	
	/**
	 * Reset.
	 */
	public void reset();
	
}

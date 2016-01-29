/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/


package jnum.util;

// TODO: Auto-generated Javadoc
/**
 * The Interface ViewableAsDoubles.
 */
public interface ViewableAsDoubles {

	/**
	 * View as doubles.
	 *
	 * @return the object
	 */
	public Object viewAsDoubles();
	
	/**
	 * View as doubles.
	 *
	 * @param view the view
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void viewAsDoubles(Object view) throws IllegalArgumentException;
	
	/**
	 * Creates the from doubles.
	 *
	 * @param array the array
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void createFromDoubles(Object array) throws IllegalArgumentException;
	
}
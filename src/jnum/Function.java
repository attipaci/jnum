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
package jnum;

// TODO: Auto-generated Javadoc
/**
 * A generic function interface for all things that take an argument (ArgType) from which it calculates
 * a return value (ReturnType).
 *
 * @param <ArgType> the generic type of the arguments
 * @param <ReturnType> the generic type of the return value.
 */
public interface Function<ArgType, ReturnType> {	
	// throw IllegalArgumentException if the dimension of the arguments is incorrect...
	/**
	 * Evaluates the function at the given set of parameters.
	 *
	 * @param parms the function parameters
	 * @return the return value
	 */
	public ReturnType valueAt(ArgType parms);	
}

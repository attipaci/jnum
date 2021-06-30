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
 * A generic function interface for all things that take an argument from which it generates
 * a return value.
 *
 * @param <ArgType> the generic type of the arguments
 * @param <ReturnType> the generic type of the return value.
 * 
 * @see ConsiderateFunction
 * @see jnum.math.ConsiderateFunctionAdapter
 */
public interface Function<ArgType, ReturnType> {
    
	/**
	 * Evaluates the function at the given set of parameters.
	 *
	 * @param parms    the function parameters (argument).
	 * @return         the return value
	 * 
	 * @throws IllegalArgumentException    if the input parameters (argument) is not valid for some reason.
	 */
	public ReturnType valueAt(ArgType parms) throws IllegalArgumentException;	
}

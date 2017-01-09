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


package jnum;

// TODO: Auto-generated Javadoc
// Functions that do not create a new object at every evaluation. 
// If such behaviour is desired, the ConsiderateFunctionAdapter class
// can be used to wrap this into a regular Function.

/**
 * The Interface ConsiderateFunction.
 *
 * @param <ArgType> the generic type
 * @param <ReturnType> the generic type
 */
public interface ConsiderateFunction<ArgType, ReturnType> {

	/**
	 * Evaluate.
	 *
	 * @param parms the parms
	 * @param toValue the to value
	 */
	public void evaluate(ArgType parms, ReturnType toValue);
	
}

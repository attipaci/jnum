/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.math;

// TODO: Auto-generated Javadoc
// Functions that do not create a new object at every evaluation. 
// If such behaviour is desired, the StaticFunctionAdapter class
// can be used to wrap this into a regular Function.

/**
 * An interface for performance evaluation of functions with a non-primitive return type. By supplying the return value, 
 * the evaluation does not need to create a new object on every call. Thus, a fair amount of computation time can be saved 
 * on repeated calls over methods that create a separate return value object on every call. Thus FillReturnValue type 
 * functions are great for performance oriented applications.
 *
 * @param <ParmType> the generic type of the function parameters
 * @param <ReturnType> the generic type of the return value
 */
public interface ReturnArgument<ParmType, ReturnType> {

	/**
	 * Evaluates the supplied parameters and places the result into the supplied ReturnType argument. 
	 *
	 * @param parms the function parameters
	 * @param toValue the return value to be filled.
	 */
	public void evaluate(ParmType parms, ReturnType toValue);
	
}

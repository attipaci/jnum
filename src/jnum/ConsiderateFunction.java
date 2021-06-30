/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum;

/**
 * An interface for considerate functions, that is functions that do not create new objects to return. Instead, considerate
 * functions place the result into a caller-supplied object. By avoiding the creation of return objects, considerate 
 * functions can offer superior performance compared to regular functions (see {@link Function}).
 * 
 * 
 * The practical recommendation is to use considerate functions, when possible, inside loops where the return value
 * is restricted in scope to the loop body. In such case, the loop can benefit from the performance boost offered
 * by the considerate function's design, without the possibility of mis-using the same return value outside of it.
 * 
 * 
 * considerate functions can be easily converted into the safer form of regular function by the 
 * {@link jnum.math.ConsiderateFunctionAdapter} class, without having to create a separate regular implementation.
 * Therefore, the preferred primary implementation of functions with non-primite return type should be their
 * considerate form.
 *
 * @param <ArgType> the generic type of the function's input argument
 * @param <ReturnType> the generic type of the function's return value
 * 
 * @see Function
 * @see jnum.math.ConsiderateFunctionAdapter
 * 
 */
public interface ConsiderateFunction<ArgType, ReturnType> {

	/**
	 * Evaluates the function for the given parameters (first argument), and places the result in the
	 * supplied return value object (second argument).
	 *
	 * @param parms the input parameters (or arguments) to the function
	 * @param toValue the return value object that will be populated with the result.
	 * 
	 * @throws IllegalArgumentException    If either the input parameters or the return object are not valid for some reason.
	 */
	public void evaluate(ArgType parms, ReturnType toValue) throws IllegalArgumentException;
	
}

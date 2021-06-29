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

package jnum.math;

import jnum.ConsiderateFunction;
import jnum.Function;
import jnum.Util;

/**
 * An adapter class that represents any considerate function as a regular function. Regular functions
 * take an argument object and return a new object with the result. Considerate functions, on the other
 * hand, return their result into a return object supplied by the caller (i.e. they do not create
 * a new object at each call -- that is why they are considerate). 
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <ArgType>
 * @param <ReturnType>
 */
public class ConsiderateFunctionAdapter<ArgType, ReturnType> implements Function<ArgType, ReturnType> {

	private ConsiderateFunction<ArgType, ReturnType> function;
	private Class<ReturnType> returnType;
	

	@SuppressWarnings("unchecked")
	public ConsiderateFunctionAdapter(ConsiderateFunction<ArgType, ReturnType> f, ReturnType template) {
		function = f;
		returnType = (Class<ReturnType>) template.getClass(); 
	}

	@Override
	public synchronized ReturnType valueAt(ArgType parms) {
		try { 
			ReturnType value = returnType.getConstructor().newInstance(); 
			function.evaluate(parms, value);
			return value;
		}
		catch(Exception e) { Util.error(this, e); }
		
		return null;
	}	
}

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

package jnum.math;

import jnum.ConsiderateFunction;
import jnum.Function;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsiderateFunctionAdapter.
 *
 * @param <ArgType> the generic type
 * @param <ReturnType> the generic type
 */
public class ConsiderateFunctionAdapter<ArgType, ReturnType> implements Function<ArgType, ReturnType> {
	
	/** The function. */
	ConsiderateFunction<ArgType, ReturnType> function;
	
	/** The return type. */
	Class<ReturnType> returnType;
	
	/**
	 * Instantiates a new considerate function adapter.
	 *
	 * @param f the f
	 * @param template the template
	 */
	@SuppressWarnings("unchecked")
	public ConsiderateFunctionAdapter(ConsiderateFunction<ArgType, ReturnType> f, ReturnType template) {
		function = f;
		returnType = (Class<ReturnType>) template.getClass(); 
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Function#valueAt(java.lang.Object)
	 */
	@Override
	public synchronized ReturnType valueAt(ArgType parms) {
		try { 
			ReturnType value = returnType.newInstance(); 
			function.evaluate(parms, value);
			return value;
		}
		catch(InstantiationException e) { e.printStackTrace(); }
		catch(IllegalAccessException e) { e.printStackTrace(); }
		
		return null;
	}	
}

/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.math;

import jnum.Function;

// TODO: Auto-generated Javadoc
/**
 * Numerical functions with tunable precision.
 *
 * @param <ArgType> the generic type of the function's parameters
 * @param <ReturnType> the generic type of the function's return value
 */
public interface NumericalFunction<ArgType, ReturnType> extends Function<ArgType, ReturnType> {

	/**
	 * Sets the precision.
	 *
	 * @param digits the new precision
	 */
	public void setPrecision(int digits);
	
	/**
	 * Gets the max precision at.
	 *
	 * @param x the x
	 * @return the max precision at
	 */
	public int getMaxPrecisionAt(ArgType x);
}

/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.math;

import jnum.Function;

/**
 * A base class for numerical functions with tunable precision.
 *
 * @param <ArgType> the generic type of the function's parameters
 * @param <ReturnType> the generic type of the function's return value
 */
public interface NumericalFunction<ArgType, ReturnType> extends Function<ArgType, ReturnType> {

	/**
	 * Sets the precision to which results are to be obtained..
	 *
	 * @param digits the new precision
	 */
	public void setPrecision(int digits);
	
	/**
	 * Gets the maximum number of significant figures that may be obtainable
	 * by this numerical evaluation at the given ordinate.
	 * 
	 * @param x    The ordinate this function may be evaluated at
	 * @return     The maximum number of significantfigures that may be available when the
	 *             numberical function is evaluated at the specified ordinate.
	 */
	public int getMaxPrecisionAt(ArgType x);
}

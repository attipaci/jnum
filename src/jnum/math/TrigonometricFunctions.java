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


/**
 * Trigonometric functions, which act on the object itself. 
 * The methods are acted on the object itself. For example, 
 * for object <code>x</code>, the call <code>x.asin()</code> will replace the contents of x with that of asin(x).
 */
public interface TrigonometricFunctions {

	/**
	 * Transforms this object to its sine. I.e. <i>x</i> becomes sin(<i>x</i>).
	 */
	public void sin();
	
	/**
     * Transforms this object to its cosine. I.e. <i>x</i> becomes cos(<i>x</i>).
     */
	public void cos();
	
	/**
     * Transforms this object to its tangent. I.e. <i>x</i> becomes tan(<i>x</i>).
     */
	public void tan();
	
}

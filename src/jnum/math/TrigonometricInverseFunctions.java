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

package jnum.math;

// TODO: Auto-generated Javadoc
//TODO: Auto-generated Javadoc
/**
* Interface for objects that implement trogonometric functions. The methods are acted on the object itself. For example, 
* for object x, the call x.sin() will replace x --> sin(x).
*/
public interface TrigonometricInverseFunctions {

	/**
	 * Inverse sine.
	 */
	public void asin();
	
	/**
	 * Inverse cosine.
	 */
	public void acos();
	
	/**
	 * Inverse tangent.
	 */
	public void atan();
	
}

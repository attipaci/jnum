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


/**
* Interface for objects that implement trigonometric functions on themselves. 
* The methods are acted on the object itself. For example, 
* for object <code>x</code>, the call <code>x.sin()</code> will replace the contents of x with that of sin(x).
*/
public interface TrigonometricInverseFunctions {

	/**
	 * Transform this object to its inverse sine. I.e. <i>x</i> becomes asin(<i>x</i>).
	 */
	public void asin();
	
	/**
	 * Transform this object to its inverse cosine. I.e. <i>x</i> becomes acos(<i>x</i>).
	 */
	public void acos();
	
	/**
	 * Transform this object to its inverse tangent. I.e. <i>x</i> becomes atan(<i>x</i>).
	 */
	public void atan();
	
}

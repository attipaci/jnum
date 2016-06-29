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
/**
 * Interface for objects that implement trogonometric functions. The methods are acted on the object itself. For example, 
 * for object x, the call x.sin() will replace x --> sin(x).
 */
public interface TrigonometricFunctions {

	/**
	 * Calculate the sine of this object.
	 */
	public void sin();
	
	/**
	 * Calculate the cosine of this object.
	 */
	public void cos();
	
	/**
	 * Calculate the tangent of this object.
	 */
	public void tan();
	
}
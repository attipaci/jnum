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
 * Support for adding and subtracting a Complex numbers.
 */
public interface ComplexAddition {

	/**
	 * Adds a complex value to this object.
	 *
	 * @param x    the complex value to be added.
	 */
	public void add(Complex x);
	
	/**
	 * Subtracts a complex value from this object.
	 *
	 * @param x    the complex value to be subtracted.
	 */
	public void subtract(Complex x);
	
	
	/**
     * Adds a complex value to this object.
     *
     * @param re    real value of complex argument
     * @param im    imaginary value of complex argument
     */
	public void add(double re, double im);
	
}

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
 * An interface for objects that allow adding and subtracting real values to themselves.
 * 
 * @author Attila Kovacs
 *
 */
public interface RealAddition {
    
	/**
	 * Adds the argument to this object.
	 *
	 * @param x the value to be added.
	 */
	public void add(double x);
	
	/**
	 * Subtracts the argument from this object.
	 *
	 * @param x the value to be subtracted.
	 */
	public default void subtract(double x) {
	    add(-x);
	}
	
}

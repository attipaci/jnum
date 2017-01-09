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

// TODO: Auto-generated Javadoc
/**
 * An interface for all objects that support addition (and subtraction) operations.
 *
 * @param <DataType> the generic type
 */
public interface Additive<DataType> {
	
	/**
	 * Add a value to this object.
	 *
	 * @param o the value to be added.
	 */
	public void add(DataType o);
	
	/**
	 * Subtract the argument.
	 *
	 * @param o the argument to be subtracted.
	 */
	public void subtract(DataType o);	
	
	/**
	 * Set this object to be the sum of the two arguments.
	 *
	 * @param a the a
	 * @param b the b
	 */
	public void setSum(DataType a, DataType b);
	
	/**
	 * Set this object to be the difference of a and b (i.e., a-b).
	 *
	 * @param a the a
	 * @param b the b
	 */
	public void setDifference(DataType a, DataType b);
	
	
}

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
 * An interface for all object that implement a linear algebra. I.e. objects X that support the operation a*X+Y, 
 * where a is a scalar and X,Y is of the supported generic type (DataType), and have a null value such that 
 * X + a * 0 = X for all a and X.
 *
 * @param <DataType> the generic type for which this algebra applies.
 */
public interface LinearAlgebra<DataType> extends Additive<DataType>, Scalable {

	/**
	 * Add a multiple of the argument.
	 *
	 * @param o the argument
	 * @param factor the multiple
	 */
	public void addMultipleOf(DataType o, double factor);
	
	
	/**
	 * Checks if the object null (zero).
	 *
	 * @return true, if the object is null (zero).
	 */
	public boolean isNull();
	
	/**
	 * Set this to be the zero value under this linear algebra.
	 */
	public void zero();
}
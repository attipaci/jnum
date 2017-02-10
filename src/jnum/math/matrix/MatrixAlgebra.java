/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.math.matrix;

import jnum.math.IdentityValue;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.math.Product;

// TODO: Auto-generated Javadoc
/**
 * The Interface MatrixAlgebra.
 *
 * @param <T> the generic type
 */
public interface MatrixAlgebra<T> extends LinearAlgebra<T>, Product<T, T>, Metric<T>, IdentityValue {

	/**
	 * Dot.
	 *
	 * @param B the b
	 * @return the t
	 */
	public T dot(T B);
	
	/**
	 * Transpose.
	 */
	public void transpose();
	
	/**
	 * Gets the rank.
	 *
	 * @return the rank
	 */
	public int getRank();
	
	/**
	 * Gauss.
	 */
	public void gauss();
	
	/**
	 * Gauss jordan.
	 */
	public void gaussJordan();
	
}

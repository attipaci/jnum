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
 * Interface for classes that can represent the product of sorts.
 * 
 * @author Attila Kovacs
 *
 * @param <LeftType>    the generic type of the argument on the lefthand-side of the product.
 * @param <RightType>   the generic type of the argument on the righthand-side of the product.
 */
public interface Product<LeftType, RightType> {

    /**
     * Sets the contents of this object to the product of the two arguments. That is, the
     * object will be set to contain the value of a*b.
     * 
     * @param a     The left-hand side argument in the product.
     * @param b     The right-hand side argument in the product.
     */
	public void setProduct(LeftType a, RightType b);
	
}

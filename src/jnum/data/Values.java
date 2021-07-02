/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

/**
 * An interface for objects that represent one or more number values.
 * 
 * @author Attila Kovacs
 *
 */
public interface Values {
    
    /**
     * Gets the class of elements that hold number values for this object.
     * 
     * @return
     */
    public Class<? extends Number> getElementType();

    /**
     * Compares two number values represented by the implementing class.
     * 
     * @param a     The first number
     * @param b     The second number
     * @return      0 if a == b, -1 if a is smaller, and +1 if a is bigger than b.
     */
    public int compare(Number a, Number b);
    
}

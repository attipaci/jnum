/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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
 * An object that is based on another object of the given generic type.
 * 
 * @author Attila Kovacs
 *
 * @param <BaseType>    the generic type of object on which this overlay is based.
 */
public interface Overlayed<BaseType> {

    /**
     * Returns the underlying object on which this overlay instance is based.
     * 
     * @return  the underlying object
     * 
     * @see #setBasis(Object)
     */
    BaseType getBasis();
    
    /**
     * Sets a new underlying object for this overlay.
     * 
     * @param basis     the new underlying object on which this overlay is to be based on.
     * 
     * @see #getBasis()
     */
    void setBasis(BaseType basis);

}

/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

import jnum.data.index.Index;

/**
 * An interface for data objects that can change their size dynamically.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   The type of index that can be used to specify the size of this object
 */
public interface Resizable<IndexType extends Index<IndexType>> {

    /**
     * Sets the size of the data contained in this object to this new size. Normally the
     * resizing is expected to discard any prior data, and create a freshly initialized
     * data object with default (zero) values. 
     * 
     * @param size  the new size for this object.
     */
    public void setSize(IndexType size);
    
}

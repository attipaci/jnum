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
 * An interface for classes that implement some sort of data validation on themselves.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   The type of index by which data is referenced in the implementing class.
 */
public interface Validating<IndexType> {

    /**
     * Checks if the data located at the specified index is valid.
     * 
     * @param index     the location of a datum in the implementing class.
     * @return          <code>true</code> if the data at the specified location is valid. Otherwise <code>false</code>.
     */
    public boolean isValid(IndexType index);
    
    /**
     * Discards the datum (marks is as invalid) at the specified index. 
     * 
     * @param index     the location of a datum in the implementing class.
     */
    public void discard(IndexType index);
    
}

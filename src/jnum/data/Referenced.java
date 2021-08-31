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

import jnum.data.index.Index;
import jnum.math.MathVector;

/**
 * Interface for regularly gridded data objects that have a reference index value/position. The reference index itself 
 * may need not match an index contained in the data, and can fall between points of the grid, i.e. can have
 * a location of at floating-point index values. Hence, the reference location is represented by a mathematical
 * vector type, of the same generic type as the offset vectors used by the regular data object to which 
 * the reference position is referred to.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>      the type of data indices on a regular grid
 * @param <VectorType>     the type of offset vectors on a regular grid, of the same dimensionality as the index.
 */
public interface Referenced<IndexType extends Index<IndexType>, VectorType extends MathVector<Double>> {
    
    /**
     * Gets the regularly gridded data object that is referenced by this implementation.
     * 
     * @return  the data object for which this interface provides a reference index location.
     */
    public RegularData<IndexType, VectorType> getData();
    
    /**
     * Gets the reference index value, which may fall between the regular grid cells represented by integers.
     * Hence, the returned value is a floating-point vector type.
     * 
     * @return          the reference index location.
     */
    public VectorType getReferenceIndex();
    
    /**
     * Sets the reference index value for the associated regularly gridded data.
     * 
     * @param index     the reference index location, which may fall between the regular grid cells represented by integers.
     *                  Hence, the returned value is a floating-point vector type.
     */
    public void setReferenceIndex(VectorType index);
    
}

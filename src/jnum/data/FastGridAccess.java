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
 * Efficient conversion between coordinates and data indices on a grid. The interface allows operating on
 * (modifying) ALL objects passed as its method arguments, such that the original content of 
 * input values may be altered in the conversion process. This model allows for fast conversion between 
 * coordinates and indices in general, without needing to create new objects that could slow the operation 
 * when processing large data volumes.
 * 
 * If the caller needs to retain the input objects, it should call the
 * methods of this interface with a disposable copy, such that the methods of this interface may mangle
 * it freely.
 * 
 * @author Attila Kovacs
 *
 * @param <CoordinateType>      The type of coordinates
 * @param <OffsetType>          The type of index offsets
 */
public interface FastGridAccess<CoordinateType, OffsetType> {

    /**
     * Calculates coordinates from the provided indices. The orginal input offsets may be lost (modified)
     * and the output coordinates are returned in the provided second argument. 
     * 
     * @param index         In input index offsets, whose contents are typically modified during the conversion.
     * @param toCoords      The object which is modified to contain the calculated output coordinates.       
     */
    public void coordsAt(OffsetType index, CoordinateType toCoords);
    
    
    /**
     * Calculates indices from the provided coordinates. The orginal input coordinates may be lost (modified)
     * and the output indices are returned in the provided second argument. 
     * 
     * @param value         In input coordinates, whose contents may also be modified during the conversion.
     * @param toIndex       The object which is modified to contain the calculated output coordinates.       
     */
    public void indexOf(CoordinateType value, OffsetType toIndex);
    
}

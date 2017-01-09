/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Interface Grid.
 *
 * @param <CoordinateType> the generic type
 * @param <OffsetType> the generic type
 */
public interface Grid<CoordinateType, OffsetType> extends Serializable, Cloneable {

    /**
     * Sets the reference.
     *
     * @param coords the new reference
     */
    public void setReference(CoordinateType coords);
    
    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public CoordinateType getReference();
    
    /**
     * Sets the reference index.
     *
     * @param index the new reference index
     */
    public void setReferenceIndex(OffsetType index);
    
    /**
     * Gets the reference index.
     *
     * @return the reference index
     */
    public OffsetType getReferenceIndex();
    
    /**
     * Sets the resolution.
     *
     * @param delta the new resolution
     */
    public void setResolution(OffsetType delta);
    
    /**
     * Gets the resolution.
     *
     * @return the resolution
     */
    public OffsetType getResolution();
       
    /**
     * Value at.
     *
     * @param index the index
     * @return the coordinate type
     */
    public CoordinateType valueAt(OffsetType index);
    
    /**
     * Index of.
     *
     * @param value the value
     * @return the offset type
     */
    public OffsetType indexOf(CoordinateType value);
    
}

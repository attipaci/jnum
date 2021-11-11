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

package jnum.math;

import jnum.CopyCat;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedEntries;

/**
 * An interface representing coordinates of generic type.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   The generic type of a coordinate in this set
 */
public interface Coordinates<T> extends CopyCat<Coordinates<? extends T>>, IndexedEntries<Index1D, T>  {

    /**
     * Checks if two coordinate instances are equal to one-another within some relative
     * precision. The two coordinates are only equals if they both have the same dimension
     * and all their components match within the specified precision.
     * 
     * @param coords        the coordinate instance to check equality to
     * @param precision     the relative precision, e.g. 1e-6 for 1 part per million.
     * @return              <code>true</code> if the two coordinates are the same within the
     *                      specified relative precision, or <code>false</code> if they differ.
     */
    public boolean equals(Coordinates<T> coords, double precision);
    
    /**
     * Gets the number of coordinate components in this set.
     * 
     * @return  The number of elements constituting this set of coordinates.
     */
    public int size();
    
    /**
     * Gets the class of components in these coordinates. 
     * 
     * @return      the class of components contained in these set of coordinates.
     */
    public Class<T> getComponentType();
    
    /**
     * Gets one of the coordinate element.
     * 
     * @param index     Index of the coordinate element
     * @return          The coordinate element at the given index, or 0.0 if the index is beyonf the 
     *                  span of coordinate elements supported by this class.
     */
    public T getComponent(int index);
    
    /**
     * Sets one coordinate to the specified generic type value.
     * 
     * @param index     Index of the coordinate element
     * @param value     The new value for the coordinate element at the given index.
     */
    public void setComponent(int index, T value);
    
    /**
     * Gets the 'x' coordinate (index 0) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'x' coordinate.
     */
    public T x();
    
    /**
     * Gets the 'y' coordinate (index 1) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'y' coordinate, or 0.0 if this set of coodinates does not have a 'y' type element.
     */
    public T y();
    
    /**
     * Gets the 'z' coordinate (index 2) from a usual set of x,y,z coordinates. 
     * 
     * @return  The 'z' coordinate, or 0.0 if this set of coodinates does not have a 'z' type element.
     */
    public T z();

   
}

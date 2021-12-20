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
 * Provides a windowed (partial) view of underlying data.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>     the generic type of data index used for locating elements in the underlyting data object
 */
public interface Windowed<IndexType> {

    /**
     * Returns the underlying data index at which this viewport window begins.
     * 
     * @return  the data index, in the undelying data, at which the viewport originates.
     * 
     * @see #getSize()
     * @see #setBounds(Object, Object)
     * @see #move(Object)
     */
    IndexType getOrigin();
    
    /**
     * Returns the size of this viewport window.
     * 
     * @return  the size of the viewport window.
     * 
     * @see #getOrigin()
     * @see #setBounds(Object, Object)
     */
    IndexType getSize();
    
    /**
     * Sets new parameters for this viewport instance.
     * 
     * @param from      the underlying data index at which the viewport window begins
     * @param to        the underlying data index (exclusive!) before which the viewport ends. It is the
     *                  location of the data point that is diagonally adjacent to the farthest
     *                  corner from the origin that is still inside the viewport.
     *                  
     * @see #getOrigin()
     * @see #getSize()
     * @see #move(Object)
     */
    void setBounds(IndexType from, IndexType to);
    
    /**
     * Moves the origin of this viewport by the specified offset, keeping its size intact.
     * 
     * @param delta     the offset by which to move the viewport window.
     * 
     * @see #getOrigin()
     * @see #setBounds(Object, Object)
     */
    void move(IndexType delta);
    
}

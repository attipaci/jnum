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

package jnum.math;

/**
 * Coordinate transformations.
 * 
 * @author Attila Kovacs
 *
 * @param <CoordinateType>  The generic type of coordinates operated on by this transform.
 */
public interface CoordinateTransform<CoordinateType> {

    /**
     * Transforms the supported generic type of coordinates in place. The is
     * the result replaces the original value in the input vector itself.
     * 
     * @param coords   The vector to be transformed in situ.
     */
    public void transform(CoordinateType coords);
    
    /**
     * Gets a new set of coordinates of the generic type as the input containing
     * the trasnformed input coordinates. Unlike {@link #transform(Object)}, this
     * call leaves the input coordinates unchanged.
     * 
     * @param coords    The input vector
     * @return          The transformed output vector as a new vector of the same generic type as the input.
     */
    public CoordinateType getTransformed(CoordinateType coords);
    
}

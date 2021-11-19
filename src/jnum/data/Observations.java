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
 * Data that includes associated uncertainties and exposure information.
 * 
 * @author Attila Kovacs
 *
 * @param <DataType>    the generic type of the data which with uncertainties and exposures.
 * 
 * @see Accumulating
 */
public interface Observations<DataType> extends Uncertainties<DataType>, Exposures<DataType> {
 
    /**
     * Ends an accumulation (summation) in this object, and converts sums into averages by 
     * renormalizing the summed values with the summed weights. The caller might implement 
     * the summing in any way they see fit prior to calling this method once (and only once!) 
     * per accumulation cycle.
     * 
     */
    public void endAccumulation();
     
}

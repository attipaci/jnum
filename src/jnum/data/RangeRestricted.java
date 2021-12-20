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

import jnum.math.Range;

/**
 * Data that is restricted to a range of real values.
 * 
 * @author Attila Kovacs
 *
 */
public interface RangeRestricted {

    /**
     * Sets a new range of real values to which the data should be restricted to. The implementing class
     * should not provide data outside of this range, either marking outliers as invalid or by forcing
     * them to lie within the specified range, whichever is appropriate for the given class.
     * 
     * @param r     the new range of acceptable real values.
     * 
     * @see #getValidRange()
     */
    void setValidRange(Range r);

    /**
     * Returns the range of real values to which the implementing data class is being restricted to.
     * The implementing class should not provide data outside of this range, either marking outliers as 
     * invalid or by forcing them to lie within the specified range, whichever is appropriate for the 
     * given class.
     * 
     * @return      the range of acceptable real values.
     * 
     * @see #setValidRange(Range)
     */
    Range getValidRange();
    
}

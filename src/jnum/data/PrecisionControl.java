/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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
 * An interface for numerical objects that have tunable precision. Adjustable precision
 * may offer superior calculation speeds when reduced precision can be tolerated.
 * 
 * @author Attila Kovacs
 *
 */
public interface PrecisionControl {

    /**
     * Sets the numerical precision to use.
     * 
     * @param x     A relative precision, such as 1e-12 to provide results with 12 significant figures.
     */
    public void setPrecision(double x);
    
    /**
     * Gets the relative (fractional) precision that this object currently uses.
     * 
     * @return      the fractional precision. For example 1e-12 would produce results with 12 significant figures.
     */
    public double getPrecision();
    
}

/* *****************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.text;

import java.text.NumberFormat;

/**
 * Interface for getting string representation of objects using the specified number format.
 * 
 * @author Attila Kovacs
 *
 */
public interface NumberFormating {

    /**
     * Gets a string representation of the implementing object, formatting relevant numerical values
     * with the specified number format.
     * 
     * @param nf        The number formatting to use when converting object to its string representation
     * @return          The string representation using the specified number format.
     */
	public String toString(NumberFormat nf);	
}

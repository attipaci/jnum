/* *****************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 * An interface for classes that can be set to contain the value of a division operation
 * on some generic type elements.
 * 
 * @author Attila Kovacs
 *
 * @param <NumeratorType>       the generic type of the numerator
 * @param <DenominatorType>     the generic type of the denominator
 */
public interface Ratio<NumeratorType, DenominatorType> {

    /**
     * Sets the contents of this object to contain the result of the division of the
     * specified arguments.
     * 
     * @param numerator     the numerator in the ratio.
     * @param denominator   the denominator in the ratio.
     */
	public void setRatio(NumeratorType numerator, DenominatorType denominator);
	
}

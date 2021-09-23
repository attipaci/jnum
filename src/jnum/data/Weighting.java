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
 * Handles weights assigned to an object. Weighting may typically be used to represent measurements.
 * 
 * @author Attila Kovacs
 *
 */
public interface Weighting {
    /**
     * Gets the weight associated with this value
     * 
     * @return      (arb. u.) the associated weight, such as a 1/&sigma;<sup>2</sup> noise weight.
     * 
     * @see #setWeight(double)
     * @see #addWeight(double)
     * @see #scaleWeight(double)
     */
    public double weight();

    /**
     * Sets a new weight to be associated with this value.
     * 
     * @param w     (arb. u.) the new weight to associate with the value, such as a 1/&sigma;<sup>2</sup> noise weight.
     * 
     * @see #weight()
     * @see #addWeight(double)
     * @see #scaleWeight(double)
     * @see #exact()
     */
    public void setWeight(final double w);
    
    /**
     * Increments the weight of this value, for example to account for aggregating 
     * new measurements into this value.
     * 
     * @param dw    the weight increment.
     * 
     * @see #weight()
     * @see #setWeight(double)
     * @see #scaleWeight(double)
     */
    public void addWeight(double dw);
    
    /**
     * Scales the value by the specified factor while leaving the weight as is.
     * 
     * @param factor    the scaling factor for the associated value only.
     * 
     * @see #scaleWeight(double)
     */
    public void scaleValue(double factor);

    /**
     * Scales the weight by the specified factor while leaving the value as is.
     * 
     * @param factor    the scaling factor for the weight only.
     * 
     * @see #scaleValue(double)
     */
    public void scaleWeight(double factor);

    /**
     * Makes this an exact value, that is a value with infinite weight.
     * 
     * @see #isExact()
     * @see #setWeight(double)
     */
    public void exact();

    /**
     * Checks if the value is an exact value, i.e. one that has infinite weight.
     * 
     * @return  <code>true</code> if the value is exact. Otherwise <code>false</code>.
     * 
     * @see #exact()
     * @see #weight()
     */
    public boolean isExact();

}

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

package jnum.data.index;

/**
 * Provides index-based noise/uncertainty type information.
 * 
 * @author Attila Kovacs
 *
 * @param <IndexType>   the generic type of the index used by the implementing class.
 */
public interface IndexedUncertainties<IndexType> {

    /**
     * Returns the noise value (1&sigma; uncertainty, or rms noise, or equivalent) at the specified index location.
     * 
     * @param index     the index location
     * @return          the noise/uncertainty value at the specified location.
     */
    public double noiseAt(IndexType index);
    
    /**
     * Returns the noise weight (<i>w</i> = 1/&sigma;<sup>2</sup>) at the specified index location.
     * 
     * @param index     the index location
     * @return          the noise/uncertainty value at the specified location.
     */
    public double weightAt(IndexType index);
    
    /**
     * Returns the (signed) signal-to-noise ration at the specified index location.
     * 
     * @param index     the index location
     * @return          the signal-to-noise value at the specified location (signed).
     */
    public double significanceAt(IndexType index);
    
    /**
     * Sets a new noise/uncertainty value at the specified index location.
     * 
     * @param index     the index location
     * @param value     the new noise/uncertainty value (rms, or 1&sigma;, or equivalent).
     */
    public void setNoiseAt(IndexType index, double value);
    
    /**
     * Sets a new noise weight that the specified index location. Same as setting {@link #setNoiseAt(Object, double)}
     * with the inverse square root of the weight value;
     * 
     * @param index     the index location
     * @param value     the new noise weight value (1/variance, or 1/&sigma;<sup>2</sup>, or equivalent).
     */
    public void setWeightAt(IndexType index, double value);
    
    
}

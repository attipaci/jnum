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
 * A value with a weight of the same type associated to it. For example an image, with a matching
 * image containing weight values.
 * 
 * @author Attila Kovacs
 *
 * @param <T>       the generic type of the value and the weight.
 */
public class WeightedSet<T> {
    private T value;
    private T weight;
    
    /** 
     * Construct a new empty weighted set.
     */
    public WeightedSet() {
        
    }
    
    /**
     * Constructs a new weighted set with the specified value and associated weights.
     * 
     * @param value     the value component
     * @param weight    the weight component.
     */
    public WeightedSet(T value, T weight) {
        setValue(value);
        setWeight(weight);
    }
    
    /**
     * Returns the value component of this weighted set.
     * 
     * @return      the value component
     * 
     * @see #weight()
     * @see #setValue(Object)
     */
    public final T value() {
        return value;
    }
    
    /**
     * Returns the weight component of this weighted set.
     * 
     * @return      the weight component
     * 
     * @see #value()
     * @see #setWeight(Object)
     */
    public final T weight() {
        return weight;
    }
    
    /**
     * Sets a new value component for this weighted set.
     * 
     * @param value     the new value component.
     * 
     * @see #setWeight(Object)
     * @see #value()
     */
    public void setValue(T value) {
        this.value = value;
    }
    
    /**
     * Sets a new weight component for this weighted set.
     * 
     * @param weight    the new weight component
     * 
     * @see #setValue(Object)
     * @see #weight()
     */
    public void setWeight(T weight) {
        this.weight = weight;
    }
}

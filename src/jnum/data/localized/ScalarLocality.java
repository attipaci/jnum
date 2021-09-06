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
package jnum.data.localized;

/**
 * A topological location in 1 dimension, that is a scalar location along the real number line. 
 * Using time as a marker is an example for such a scalar locality. 
 * The arbitrarry reference (origin) is chosen as the 0 value.
 * 
 * @author Attila Kovacs
 *
 */
public class ScalarLocality implements Locality, Comparable<ScalarLocality> {
    /** the scalar value representing the locality */
    private double value;
    
    /**
     * Construct as new scalar locality initially placed at the origin.
     * 
     */
    public ScalarLocality() {}
    
    /**
     * Constructs a new scalar locality for the given number value.
     * 
     * @param value     the location on the real number line.
     */
    public ScalarLocality(double value) { 
        this();
        set(value);
    }
    
    /**
     * Returns the scalar value defining this locality. For scalar localities
     * the sorting value is the same as the one returned here, it readily
     * being a scalar itself.
     * 
     * @return  the scalar locality value.
     * 
     * @see #set(Double)
     * @see #getSortingValue()
     */
    public Double get() { return value; }
    
    /**
     * Sets a new scalar locality value.
     * 
     * @param value     the new scalar locality value.
     * 
     * @see #get()
     */
    public void set(Double value) { this.value = value; }
    

    @Override
    public double distanceTo(Locality point) {
        if(!(point instanceof ScalarLocality)) throw new IllegalArgumentException("Incompatible localities.");
        return Math.abs(((ScalarLocality) point).value - value);
    }

    @Override
    public final double getSortingValue() {
        return Math.abs(value);
    }

    @Override
    public int compareTo(ScalarLocality other) {
        return Double.compare(value, other.value);
    }

    @Override
    public String toString() { return Double.toString(value); }

}

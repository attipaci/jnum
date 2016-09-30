/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.data;

// TODO: Auto-generated Javadoc
/**
 * The Class ScalarLocality.
 */
public class ScalarLocality extends Locality {
    
    /** The value. */
    public double value;
    
    /**
     * Instantiates a new scalar locality.
     */
    public ScalarLocality() {}
    
    /**
     * Instantiates a new scalar locality.
     *
     * @param value the value
     */
    public ScalarLocality(double value) { 
        this();
        set(value);
    }
    
    /**
     * Gets the.
     *
     * @return the double
     */
    public double get() { return value; }
    
    /**
     * Sets the.
     *
     * @param value the value
     */
    public void set(double value) { this.value = value; }
    
    /* (non-Javadoc)
     * @see jnum.math.Metric#distanceTo(java.lang.Object)
     */
    @Override
    public double distanceTo(Locality point) {
        if(!(point instanceof ScalarLocality)) throw new IllegalArgumentException("Incompatible localities.");
        return Math.abs(((ScalarLocality) point).value - value);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Locality other) {
        if(!(other instanceof ScalarLocality)) throw new IllegalArgumentException("Incompatible localities.");
        return Double.compare(value, ((ScalarLocality) other).value);
    }

    /* (non-Javadoc)
     * @see jnum.data.Locality#sortingDistanceTo(jnum.data.Locality)
     */
    @Override
    public double sortingDistanceTo(Locality other) {
        return distanceTo(other);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { return Double.toString(value); }

}

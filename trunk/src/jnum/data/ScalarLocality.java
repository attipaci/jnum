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

public class ScalarLocality extends Locality {
    public double value;
    
    public ScalarLocality() {}
    
    public ScalarLocality(double value) { 
        this();
        set(value);
    }
    
    public double get() { return value; }
    
    public void set(double value) { this.value = value; }
    
    @Override
    public double distanceTo(Locality point) {
        if(!(point instanceof ScalarLocality)) throw new IllegalArgumentException("Incompatible localities.");
        return Math.abs(((ScalarLocality) point).value - value);
    }

    @Override
    public int compareTo(Locality other) {
        if(!(other instanceof ScalarLocality)) throw new IllegalArgumentException("Incompatible localities.");
        return Double.compare(value, ((ScalarLocality) other).value);
    }

    @Override
    public double sortingDistanceTo(Locality other) {
        return distanceTo(other);
    }
    
    @Override
    public String toString() { return Double.toString(value); }

}

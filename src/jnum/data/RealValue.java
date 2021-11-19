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

package jnum.data;

import java.io.Serializable;

import jnum.Copiable;
import jnum.Util;
import jnum.math.AbsoluteValue;
import jnum.math.RealAlgebra;
import jnum.math.ZeroValue;
import jnum.util.HashCode;

/**
 * A real-value wrapped into a mutable object. Similar to {#link java.lang.Double}, but adds mutability as well well as some
 * basic algebra etc.
 * 
 * @author Attila Kovacs
 *
 */
public class RealValue implements Cloneable, Serializable, Copiable<RealValue>, Comparable<RealValue>, ZeroValue, RealAlgebra, AbsoluteValue {
    /**
     * 
     */
    private static final long serialVersionUID = 2417425199478452966L;
    
    /** the actual real value wrapped in this object */
    private double value;
    
    /**
     * Instantiates a new real value, initialized to 0.0.
     */
    public RealValue() {}
    
    /**
     * Instantiates a new real value object, with the specified primitive value.
     * 
     * @param x     the primitive real value that is initially wrapped by this object. 
     */
    public RealValue(double x) { this(); setValue(x); }
    
    @Override
    public RealValue clone() {
        try { return (RealValue) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public int hashCode() { return HashCode.from(value); }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof RealValue)) return false;
        
        RealValue real = (RealValue) o;
        return Util.equals(real.value, value);
    }
    
    @Override
    public int compareTo(RealValue o) {
        return Double.compare(value, o.value);
    }

    @Override
    public RealValue copy() {
        return clone();
    }
    
    /**
     * Sets a new value for this object.
     * 
     * @param x the new real value wrapped by this object.
     * 
     * @see #value()
     */
    public void setValue(double x) { this.value = x; }
    
    /**
     * Returns the current real value wrapped by this object.
     * 
     * @return  the current double-precision value.
     * 
     * @see #setValue(double)
     */
    public final double value() { return value; }
    
    @Override
    public void add(double x) { value += x; }
    
    @Override
    public void subtract(double x) { value -= x; }
    
    @Override
    public void scale(double x) { value *= x; }

    /**
     * Erases data from this value, (or sets it to zero).
     * 
     * @see #zero()
     */
    public void noData() { zero(); }
    
    @Override
    public void zero() { value = 0.0; }

    @Override
    public double abs() {
        return Math.abs(value);
    }
    
    @Override
    public double squareNorm() {
        return value * value;
    }
    
    @Override
    public boolean isNull() { return value == 0.0; }
    
    @Override
    public String toString() { return Double.toString(value); }

 
    
}

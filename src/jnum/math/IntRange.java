/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.Serializable;
import java.util.StringTokenizer;

import jnum.Copiable;
import jnum.util.HashCode;


public class IntRange implements Serializable, Scalable, Cloneable, Copiable<IntRange> {
    /**
     * 
     */
    private static final long serialVersionUID = -2479604076732082476L;
    
    private long min, max;
    

    public IntRange() { empty(); }
    
    public IntRange(long pointValue) {
        this(pointValue, pointValue);
    }
    

    public IntRange(long minValue, long maxValue) {
        setRange(minValue, maxValue);
    }
    
    @Override
    public IntRange clone() {
        try { return (IntRange) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
 
    @Override
    public IntRange copy() { return clone(); }
    

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof IntRange)) return false;
        
        IntRange range = (IntRange) o;
        
        if(range.isEmpty() && isEmpty()) return true; 
        if(Double.compare(range.min, min) != 0) return false;
        if(Double.compare(range.max, max) != 0) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(min) ^ HashCode.from(max);
    }
    

    public void flip() {
        if(isEmpty()) return;
        final long temp = min;
        min = max;
        max = temp;
    }
    

    public final long min() { return min; }
    

    public final long max() { return max; }
    
    public final double midPoint() { return 0.5 * (min + max); }
    

    public void setMin(long value) { min = value; }
    

    public void setMax(long value) { max = value; }
    

    public void intersectWith(IntRange r) {
        intersectWith(r.min, r.max);    
    }
    

    public synchronized void intersectWith(long min, long max) {
        if(min > this.min) this.min = min;
        if(max < this.max) this.max = max;
    }
    
    
    public synchronized void empty() {
        min=Long.MAX_VALUE; max=Long.MIN_VALUE;     
    }
    
    
    public boolean isEmpty() {
        return min > max;
    }
    

    public synchronized void full() {
        min=Long.MIN_VALUE; max=Long.MAX_VALUE; 
    }
    
    
    public void setRange(long minValue, long maxValue) {
        min = minValue;
        max = maxValue;
    }
    
    @Override
    public synchronized void scale(double value) {
        min *= value;
        max *= value;
    }
    
    public final boolean isBounded() {
        return isUpperBounded() && isLowerBounded();
    }
    

    public boolean isUpperBounded() {
        return max < Long.MAX_VALUE;
    }
    

    public boolean isLowerBounded() {
        return min > Long.MIN_VALUE;
    }
    

    public boolean contains(long value) {
        if(Double.isNaN(value)) return !isUpperBounded() && !isLowerBounded();
        return value >= min && value < max;
    }
    

    public final boolean contains(IntRange range) {
        return contains(range.min) && contains(range.max);
    }
    

    public boolean isIntersecting(IntRange range) {
        if(range.isEmpty()) return false;
        if(isEmpty()) return false;
        return contains(range.min) || contains(range.max) || range.contains(this);
    }
    

    public synchronized void include(long value) {
        if(Double.isNaN(value)) {
            full();
        }
        else if(isEmpty()) {
            min = max = value;
        }
        else {
            if(value < min) min = value;
            if(value > max) max = value;
        }
    }
    

    public final void include(IntRange range) {
        if(range.isEmpty()) return;
        include(range.min);
        include(range.max);
    }
    
    
    /**
     * Creates a new range from text specification.
     *
     * @param text the input text specifying the range. The minimim and maximum values must be separated by one
     *     or more colons ':'. E.g. "0:1" or "0:::1". 
     */
    public final void parse(String text) {
        from(text, false);
    }
    
    /**
     * Parses a new range from the text input.
     *
     * @param text the text input specifying the range
     * @param isPositive true if the range is for positive only values (allows hyphens as a separator between min and max values 
     *     -- otherwise only colon ':' is allowed. E.g. "0--1" vs the more standard "0:1").
     */
    public void parse(String text, boolean isPositive) {        
        StringTokenizer tokens = new StringTokenizer(text, " \t:" + (isPositive ? "-" : ""));
        
        if(tokens.countTokens() == 1) {
            String spec = tokens.nextToken();
            if(spec.equals("*")) full();
            else if(spec.startsWith("<")) {
                min = Long.MIN_VALUE;
                max = Long.parseLong(spec.substring(1));
            }
            else if(spec.startsWith(">")) {
                min = Long.parseLong(spec.substring(1));
                max = Long.MAX_VALUE;
            }
        }
        
        if(tokens.hasMoreTokens()) {    
            String spec = tokens.nextToken();
            if(spec.equals("*")) min = Long.MIN_VALUE;
            else min = Long.parseLong(spec);
        }
        if(tokens.hasMoreTokens()) {
            String spec = tokens.nextToken();
            if(spec.equals("*")) max = Long.MAX_VALUE;
            else max = Long.parseLong(spec);
        }   
    }
    
    

    public long span() {
        if(max <= min) return 0;
        return max - min;
    }
    

    public synchronized void grow(double factor) {
        if(!isBounded()) return;
        double span = span();
        min -= 0.5 * (factor-1.0) * span;
        max += 0.5 * (factor-1.0) * span;       
    }
    
    @Override
    public String toString() {
        return min + ":" + max;
    }

   
    public static IntRange getEmptyRange() {
        IntRange range = new IntRange();
        range.empty();
        return range;       
    }
    
    public static IntRange getFullRange() {
        IntRange range = new IntRange();
        range.full();
        return range;
    }
    

    public static IntRange getPositiveRange() {
        return new IntRange(0, Long.MAX_VALUE);
    }
    

    public static IntRange getNegativeRange() {
        return new IntRange(Long.MIN_VALUE, 0);
    }
    

    public static IntRange intersectionOf(IntRange a, IntRange b) {
        IntRange i = a.copy();
        i.intersectWith(b);
        return i;
    }
    

    public static IntRange compositeOf(IntRange a, IntRange b) {
        IntRange c = a.copy();
        c.include(b);
        return c;
    }
    
    public static IntRange from(String text) {
        return from(text, false);
    }
    
    public static IntRange from(String text, boolean isPositive) {
        IntRange range = new IntRange();
        range.parse(text, isPositive);
        return range;
    }
    
}

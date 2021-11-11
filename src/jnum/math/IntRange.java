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


/**
 * A range of integers with inclusive lower and exlusive upper bounds. Integer ranges have
 * similar functionality to their floating-point {@link Range} counterparts, except that
 * thei are restricted to the integer domain.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class IntRange implements Serializable, Scalable, Cloneable, Copiable<IntRange> {
    /**
     * 
     */
    private static final long serialVersionUID = -2479604076732082476L;
    
    private long from, to;
    
    /**
     * Instantiates a new empty integer range.
     * 
     */
    public IntRange() { empty(); }
    
    /**
     * Instantiates a new integer range containing just a sinlge integer point.
     * 
     * @param pointValue    the only integer contained in this range at instantiation. 
     */
    public IntRange(long pointValue) {
        this(pointValue, pointValue+1);
    }
    
    /**
     * Instantiates a new integer range with the given bounds.
     *  
     * @param startValue    inclusive lower bound.
     * @param endValue      exclusive upper bound.
     */
    public IntRange(long startValue, long endValue) {
        setRange(startValue, endValue);
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
        if(Double.compare(range.from, from) != 0) return false;
        if(Double.compare(range.to, to) != 0) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        return HashCode.from(from) ^ HashCode.from(to);
    }
    
    /**
     * Swaps the lower and upper bounds. The old lower bound becomes the new upper bound, and the old
     * upper bound becomes the new lower bound.
     * 
     */
    public void flip() {
        if(isEmpty()) return;
        final long temp = from;
        from = to;
        to = temp;
    }
    
    /**
     * Gets the inclusive lower bound of this integer range.
     * 
     * @return  the inclusive lower bound of this range.
     * @see #setLower(long)
     */
    public final long lower() { return from; }
   
    /**
     * Gets the exclusive upper bound of this integer range, that is the lowest integer greater
     * than the lower bound, but not included in the range itself.
     * 
     * @return  the exclusive upper bound of this range.
     * @see #setUpper(long)
     */
    public final long upper() { return to; }
    
    /**
     * Gets the mid point of this range, over the numbers contained in the range.
     * 
     * @return the middle value, which may be a half integer if the range contains even
     *         number of elements, or NaN if the range is empty.
     */
    public final double midPoint() { 
        if(to <= from) return Double.NaN;
        return 0.5 * (from + to - 1); 
    }

    /**
     * Sets a new inclusive lower bound for this range. 
     * 
     * @param value     the new inclusive lower bound.
     * @see #lower()
     */
    public void setLower(long value) { from = value; }
    
    /**
     * Sets a new exclusive lower bound for this range, that is the lowest integer greater
     * than the lower bound, that will not be included in the range itself.
     * 
     * @param value     the new exclusive upper bound.
     * @see #upper()
     */
    public void setUpper(long value) { to = value; }
    
    /**
     * Intersects this integer range with another, keeping only the range that is
     * common between this one and the argument.
     * 
     * @param r     the intersecting integer range.
     * @see #isIntersecting(IntRange)
     */
    public void intersectWith(IntRange r) {
        intersectWith(r.from, r.to);    
    }
    
    /**
     * Intersects this integer range with another, keeping only the range that is
     * common between this one and the argument.
     * 
     * @param from  the inclusive lower bound of the intersecting range.
     * @param to    the exlusive upper bound of the intersecting range.
     */
    public synchronized void intersectWith(long from, long to) {
        if(from > this.from) this.from = from;
        if(to < this.to) this.to = to;
    }
    
    /**
     * Empties this range such that it will not contain any integers. If afterward
     * an integer or another range is included via {@link #include(long)} or {@link #include(IntRange)},
     * then the range will contain exaclty that integer or range as a result.
     *
     */
    public synchronized void empty() {
        from=Long.MAX_VALUE; to=Long.MIN_VALUE;     
    }
    
    /**
     * Checks if this is an empty range, i.e. one that contains no integers at present.
     * 
     * @return  <code>true</code> if this range contains no integers. Otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return from > to;
    }
    
    /**
     * Sets the full 64-bit range of <code>long</code> integers (but not including {@link Long#MAX_VALUE}).
     * 
     */
    public synchronized void full() {
        from=Long.MIN_VALUE; to=Long.MAX_VALUE; 
    }
    
    /**
     * Sets a new integer range.
     * 
     * @param from  the inclusive lower bound of the new range.
     * @param to    the exclusive upper bound of the new range.
     */
    public void setRange(long from, long to) {
        this.from = from;
        this.to = to;
    }
    
    @Override
    public synchronized void scale(double value) {
        from *= value;
        to *= value;
    }
    
    /**
     * Checks if this range is bounded at both ends, that is it has a lower bound distinct from
     * the {@link Long#MIN_VALUE} and an upper bound distinct from {@link Long#MAX_VALUE}.
     * 
     * @return  <code>true</code> if the range is bounded on both ends by a non-extreme value. Otherwise <code>false</code>.
     */
    public final boolean isBounded() {
        return isUpperBounded() && isLowerBounded();
    }
    
    /**
     * Checks if this range has an upper bound distinct from {@link Long#MAX_VALUE}.
     * 
     * @return  <code>true</code> if the upper bound of this range is not {@link Long#MAX_VALUE}. Otherwise <code>false</code>.
     */
    public boolean isUpperBounded() {
        return to < Long.MAX_VALUE;
    }
    
    /**
     * Checks if this range has a lower bound distinct from {@link Long#MIN_VALUE}.
     * 
     * @return  <code>true</code> if the lower bound of this range is not {@link Long#MIN_VALUE}. Otherwise <code>false</code>.
     */
    public boolean isLowerBounded() {
        return from > Long.MIN_VALUE;
    }
    
    /**
     * Checks if this range contains a specific integer.
     * 
     * @param value     the integer to check.
     * @return          <code>true</code> if the integer is included in this range. Otherwise <code>false</code>.
     */
    public boolean contains(long value) {
        if(Double.isNaN(value)) return !isUpperBounded() && !isLowerBounded();
        return value >= from && value < to;
    }
    
    /**
     * Checks if this range fully encompasses the specified other range, that is if this range contains all elements
     * of the argument.
     * 
     * @param range     the argument range.
     * @return          <code>true</code> if this range includes all integers from the argument. Otherwise <code>false</code>.
     */
    public final boolean contains(IntRange range) {
        return contains(range.from) && contains(range.to);
    }
    
    /**
     * Checks if this range has any common integer content with the specified other range range, that if intersecting this
     * range with the other range would result in a non-emptry range.
     * 
     * @param range     the argument range.
     * @return          <code>true</code> if this range includes all integers from the argument. Otherwise <code>false</code>.
     * 
     * @see #intersectWith(IntRange)
     * @see #isEmpty()
     */
    public boolean isIntersecting(IntRange range) {
        if(range.isEmpty()) return false;
        if(isEmpty()) return false;
        return contains(range.from) || contains(range.to) || range.contains(this);
    }
    
    /**
     * Includes the specified integer in this range, adjusting the lower or upper bounds as necessary to
     * ensure that the specified value is included.
     * 
     * @param value     the integer to include in this range.
     */
    public synchronized void include(long value) {
        if(Double.isNaN(value)) {
            full();
        }
        else if(isEmpty()) {
            from = to = value;
        }
        else {
            if(value < from) from = value;
            if(value > to) to = value;
        }
    }
    
    /**
     * Includes the specified other range in this range. The lower or upper 
     * bounds of this range are adjusted as necessary ensure that all values from the argument range are included
     * also.
     * 
     * @param range    the integer range to include in this range.
     */
    public final void include(IntRange range) {
        if(range.isEmpty()) return;
        include(range.from);
        include(range.to);
    }
    
    
    /**
     * Creates a new range from text specification.
     *
     * @param text the input text specifying the range. The minimim and maximum values must be separated by one
     *     or more colons ':'. E.g. "0:1" or "0:::1". 
     * @throws NumberFormatException if the range could not be determined from the specified string.
     */
    public final void parse(String text) throws NumberFormatException {
        from(text, false);
    }
    
    /**
     * Parses a new range from the text input.
     *
     * @param text the text input specifying the range
     * @param isPositive true if the range is for positive only values (allows hyphens as a separator between min and max values 
     *     -- otherwise only colon ':' is allowed. E.g. "0--1" vs the more standard "0:1").
     * @throws NumberFormatException if the range could not be determined from the specified string.
     */
    public void parse(String text, boolean isPositive) throws NumberFormatException {        
        StringTokenizer tokens = new StringTokenizer(text, " \t:" + (isPositive ? "-" : ""));
        
        if(tokens.countTokens() == 1) {
            String spec = tokens.nextToken();
            if(spec.equals("*")) full();
            else if(spec.startsWith("<")) {
                from = Long.MIN_VALUE;
                to = Long.parseLong(spec.substring(1));
            }
            else if(spec.startsWith(">")) {
                from = Long.parseLong(spec.substring(1));
                to = Long.MAX_VALUE;
            }
        }
        
        if(tokens.hasMoreTokens()) {    
            String spec = tokens.nextToken();
            if(spec.equals("*")) from = Long.MIN_VALUE;
            else from = Long.parseLong(spec);
        }
        if(tokens.hasMoreTokens()) {
            String spec = tokens.nextToken();
            if(spec.equals("*")) to = Long.MAX_VALUE;
            else to = Long.parseLong(spec);
        }   
    }
    
    
    /**
     * Gets the span of this integer range, that is the number of integer values contained.
     * 
     * @return      the number of integers contained in this range.
     */
    public long span() {
        if(to <= from) return 0;
        return to - from;
    }
    
    /**
     * Grows this integer range symmetrically around its midpoint with the specified factor.
     * 
     * @param factor    the growth factor.
     */
    public synchronized void grow(double factor) {
        if(!isBounded()) return;
        double span = span();
        from -= 0.5 * (factor-1.0) * span;
        to += 0.5 * (factor-1.0) * span;       
    }
    
    @Override
    public String toString() {
        return from + ":" + to;
    }
    
    /**
     * Gets a new instance of a full integer range.
     * 
     * @return  a new full integer range instance.
     */
    public static IntRange getFullRange() {
        IntRange range = new IntRange();
        range.full();
        return range;
    }
    
    /**
     * Gets a new instance of a range containing positive integers only.
     * 
     * @return  a new range containing positive integers only.
     */
    public static IntRange getPositiveRange() {
        return new IntRange(0, Long.MAX_VALUE);
    }
    
    /**
     * Gets a new instance of a range containing negative integers only.
     * 
     * @return  a new range containing negative integers only.
     */
    public static IntRange getNegativeRange() {
        return new IntRange(Long.MIN_VALUE, 0);
    }
    
    /**
     * Gets a new integer range that is the intersection of two integer ranges.
     * 
     * @param a     one of the intersecting ranges.
     * @param b     the other intersecting range.
     * @return      a new integer range that contains the intersection of the arguments.
     */
    public static IntRange intersectionOf(IntRange a, IntRange b) {
        IntRange i = a.copy();
        i.intersectWith(b);
        return i;
    }
    
    /**
     * Gets a new integer range that is the composite of two integer ranges, containg all elements from both.
     * 
     * @param a     one of the composing ranges.
     * @param b     the other compoising range.
     * @return      a new integer range that contains the emcompassing composite of the arguments.
     */
    public static IntRange compositeOf(IntRange a, IntRange b) {
        IntRange c = a.copy();
        c.include(b);
        return c;
    }
    
    /**
     * Gets a new range instance based on its textual representation using {@link #parse(String)}.
     * 
     * @param text  the string representation of an integer range, such as "-10:25"
     * @return      a new range based on the string.
     * @throws NumberFormatException if the range could not be determined from the specified string.
     * 
     * @see #parse(String)
     */
    public static IntRange from(String text) {
        return from(text, false);
    }
    
    /**
     * Gets a new range instance based on its textual representation using {@link #parse(String, boolean)}.
     * 
     * @param text          the string representation of an integer range, such as "-10:25" (or e.g. "10-20" if
     *                      isPositive is set <code>true</code>).
     * @param isPositive    whether the range has positive only integers, and ant '-' characters are
     *                      interpreted as separators between the lower and upper bounds.
     * @return              a new range based on the string.
     * @throws NumberFormatException if the range could not be determined from the specified string.
     * 
     * @see #parse(String, boolean)
     */
    public static IntRange from(String text, boolean isPositive) {
        IntRange range = new IntRange();
        range.parse(text, isPositive);
        return range;
    }
    
}

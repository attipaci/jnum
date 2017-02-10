/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Copiable;
import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class Range.
 */
public class Range implements Serializable, Scalable, Cloneable, Copiable<Range> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7215369530550677188L;
	
	/** The max. */
	private double min, max;
	
	/**
	 * Instantiates a new range.
	 */
	public Range() { empty(); }
	
	/**
	 * Instantiates a new range.
	 *
	 * @param pointValue the point value
	 */
	public Range(double pointValue) {
	    this(pointValue, pointValue);
	}
	
	/**
	 * Instantiates a new range.
	 *
	 * @param minValue the min value
	 * @param maxValue the max value
	 */
	public Range(double minValue, double maxValue) {
		setRange(minValue, maxValue);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public Range copy() { return (Range) clone(); }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Range)) return false;
		if(!super.equals(o)) return false;

		Range range = (Range) o;
		
		if(range.isEmpty() && isEmpty()) return true; 
		if(Double.compare(range.min, min) != 0) return false;
		if(Double.compare(range.max, max) != 0) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(min) ^ HashCode.from(max);
	}
	
	/**
	 * Flip.
	 */
	public void flip() {
	    if(isEmpty()) return;
		final double temp = min;
		min = max;
		max = temp;
	}
	
	/**
	 * Min.
	 *
	 * @return the double
	 */
	public final double min() { return min; }
	
	/**
	 * Max.
	 *
	 * @return the double
	 */
	public final double max() { return max; }
	
	public final double midPoint() { return 0.5 * (min + max); }
	
	/**
	 * Sets the min.
	 *
	 * @param value the new min
	 */
	public void setMin(double value) { min = value; }
	
	/**
	 * Sets the max.
	 *
	 * @param value the new max
	 */
	public void setMax(double value) { max = value; }
	
	/**
	 * Intersect this range with the specified other range. The new range will be the intersection
	 * of the original range and the argument.
	 *
	 * @param r the range to intesect this range with.
	 */
	public void intersectWith(Range r) {
		intersectWith(r.min, r.max);	
	}
	
	/**
	 * Restrict.
	 *
	 * @param min the min
	 * @param max the max
	 */
	public synchronized void intersectWith(double min, double max) {
		if(min > this.min) this.min = min;
		if(max < this.max) this.max = max;
	}
	
	
	/**
	 * Make this range an empty range that contains no values (a null set).
	 */
	public synchronized void empty() {
		min=Double.POSITIVE_INFINITY; max=Double.NEGATIVE_INFINITY;		
	}
	
	/**
	 * Checks if this range is empty (a null set).
	 *
	 * @return true, if it is empty.
	 */
	public boolean isEmpty() {
	    return min > max;
	}
	
	/**
	 * Make this range represent all real values from -infinity to infinity.
	 */
	public synchronized void full() {
		min=Double.NEGATIVE_INFINITY; max=Double.POSITIVE_INFINITY;	
	}
	
	
	/**
	 * Sets the range to the specified lower and upper bounds (min/max values).
	 *
	 * @param minValue the lower bound value
	 * @param maxValue the uppper bound value
	 */
	public void setRange(double minValue, double maxValue) {
		min = minValue;
		max = maxValue;
	}
	
	/**
	 * Multiply the bounds (min/max values) of this range by the given factor.
	 *
	 * @param value the scaling factor
	 */
	@Override
	public synchronized void scale(double value) {
		min *= value;
		max *= value;
	}
	
	/**
	 * Checks if this range is bounded (both from below and above).
	 *
	 * @return true, if it is bounded
	 */
	public final boolean isBounded() {
	    return isUpperBounded() && isLowerBounded();
	}
	
	/**
	 * Checks if this range is upper bounded.
	 *
	 * @return true, if it is upper bounded.
	 */
	public boolean isUpperBounded() {
	    if(Double.isInfinite(max)) if(max > 0) return false;
	    return true;
	}
	
	/**
	 * Checks if this range is lower bounded.
	 *
	 * @return true, if it is lower bounded.
	 */
	public boolean isLowerBounded() {
	    if(Double.isInfinite(min)) if(min < 0) return false;
	    return true;
	}
	
	/**
	 * Checks if this range contains a specific real value.
	 *
	 * @param value the real value
	 * @return true, if this range contains the specified value. False for NaN.
	 */
	public boolean contains(double value) {
		if(Double.isNaN(value)) return !isUpperBounded() && !isLowerBounded();
		return value >= min && value < max;
	}
	
	/**
	 * Checks if this range contains all values of the specified other range.
	 *
	 * @param range the other range
	 * @return true, if all values of the specified range are contained within this range.
	 */
	public final boolean contains(Range range) {
		return contains(range.min) && contains(range.max);
	}
	
	/**
	 * Checks if this range intersects with the specified other range.
	 *
	 * @param range the other range
	 * @return true, if this range intersects the specified other range. 
	 */
	public boolean isIntersecting(Range range) {
	    if(range.isEmpty()) return false;
	    if(isEmpty()) return false;
		return contains(range.min) || contains(range.max) || range.contains(this);
	}
	
	/**
	 * Creates a new range from text specification.
	 *
	 * @param text the input text specifying the range. The minimim and maximum values must be separated by one
	 *     or more colons ':'. E.g. "0.0:1.0" or "0.0:::1.0". 
	 * @return the new range according to the specification
	 */
	public static Range parse(String text) {
		return parse(text, false);
	}
	
	/**
	 * Includes a real (non-NaN) value in this range, altering its boundaries as necessary.
	 *
	 * @param value the real (non-NaN) value to include.
	 */
	public synchronized void include(double value) {
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
	
	/**
	 * Includes all values in this range that are represented by another range.
	 *
	 * @param range the range of values to include in this range.
	 */
	public final void include(Range range) {
	    if(range.isEmpty()) return;
		include(range.min);
		include(range.max);
	}
	
	/**
	 * Parses a new range from the text input.
	 *
	 * @param text the text input specifying the range
	 * @param isPositive true if the range is for positive only values (allows hyphens as a separator between min and max values 
	 *     -- otherwise only colon ':' is allowed. E.g. "0.0--1.0" vs the more standard "0.0:1.0").
	 * @return a new range from the text specification.
	 */
	public static Range parse(String text, boolean isPositive) {
		Range range = new Range();
			
		StringTokenizer tokens = new StringTokenizer(text, " \t:" + (isPositive ? "-" : ""));
		
		if(tokens.countTokens() == 1) {
			String spec = tokens.nextToken();
			if(spec.equals("*")) range.full();
			else if(spec.startsWith("<")) {
				range.min = Double.NEGATIVE_INFINITY;
				range.max = Double.parseDouble(spec.substring(1));
			}
			else if(spec.startsWith(">")) {
				range.min = Double.parseDouble(spec.substring(1));
				range.max = Double.POSITIVE_INFINITY;
			}
		}
		
		if(tokens.hasMoreTokens()) {	
			String spec = tokens.nextToken();
			if(spec.equals("*")) range.min = Double.NEGATIVE_INFINITY;
			else range.min = Double.parseDouble(spec);
		}
		if(tokens.hasMoreTokens()) {
			String spec = tokens.nextToken();
			if(spec.equals("*")) range.max = Double.POSITIVE_INFINITY;
			else range.max = Double.parseDouble(spec);
		}
		return range;	
	}
	
	/**
	 * Gets the span of real values represented by this range.
	 *
	 * @return the real values spanned.
	 */
	public double span() {
		if(max <= min) return 0.0;
		return max - min;
	}
	
	/**
	 * Grows a bounded range, increasing its span by the specified factor while keeping its midpoint fixed.
	 *
	 * @param factor the factor
	 */
	public synchronized void grow(double factor) {
	    if(!isBounded()) return;
		double span = span();
		min -= 0.5 * (factor-1.0) * span;
		max += 0.5 * (factor-1.0) * span;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return min + ":" + max;
	}

	/**
	 * To string.
	 *
	 * @param nf the number format
	 * @return the string representation of this range, formatted to specification.
	 */
	public String toString(NumberFormat nf) {
		return nf.format(min) + ":" + nf.format(max);
	}
	
	/**
	 * Gets an empty range.
	 *
	 * @return a new empty range that does not contain any real value.
	 */
	public static Range getEmptyRange() {
		Range range = new Range();
		range.empty();
		return range;		
	}
	
	/**
	 * Gets the full range of real values.
	 *
	 * @return a new full range of real values (-infinity to infinity). 
	 */
	public static Range getFullRange() {
		Range range = new Range();
		range.full();
		return range;
	}
	
	/**
	 * Gets the standard range of positive real values.
	 *
	 * @return a new range of positive real values (0.0 to infinity)
	 */
	public static Range getPositiveRange() {
		return new Range(0.0, Double.POSITIVE_INFINITY);
	}
	
	/**
	 * Gets the standard range of negative real values.
	 *
	 * @return a new range of negative real values (-infinity to 0.0)
	 */
	public static Range getNegativeRange() {
		return new Range(Double.NEGATIVE_INFINITY, 0.0);
	}
	
	/**
	 * Creates a new range that is the intersection of the two ranges in the argument.
	 *
	 * @param a 
	 * @param b 
	 * @return the range that is the intersection of a and b.
	 */
	public static Range intersectionOf(Range a, Range b) {
	    Range i = a.copy();
	    i.intersectWith(b);
	    return i;
	}
	
	/**
	 * Creates a new range that is the composite of the two ranges in the argument.
	 *
	 * @param a
	 * @param b
	 * @return the smallest range that includes both a and b
	 */
	public static Range compositeOf(Range a, Range b) {
	    Range c = a.copy();
        c.include(b);
        return c;
	}
	
}

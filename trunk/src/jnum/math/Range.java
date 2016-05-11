/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
// Copyright (c) 2008 Attila Kovacs 

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
	 * @see kovacs.util.Copiable#copy()
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
		final double temp = min;
		min = max;
		max = temp;
	}
	
	/**
	 * Min.
	 *
	 * @return the double
	 */
	public double min() { return min; }
	
	/**
	 * Max.
	 *
	 * @return the double
	 */
	public double max() { return max; }
	
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
	 * Restrict.
	 *
	 * @param bounds the bounds
	 */
	public void restrict(Range bounds) {
		restrict(bounds.min, bounds.max);	
	}
	
	/**
	 * Restrict.
	 *
	 * @param min the min
	 * @param max the max
	 */
	public void restrict(double min, double max) {
		if(this.min < min) this.min = min;
		if(this.max > max) this.max = max;
	}
	
	
	/**
	 * Empty.
	 */
	public void empty() {
		min=Double.POSITIVE_INFINITY; max=Double.NEGATIVE_INFINITY;		
	}
	
	/**
	 * Full.
	 */
	public void full() {
		min=Double.NEGATIVE_INFINITY; max=Double.POSITIVE_INFINITY;	
	}
	
	/**
	 * Sets the range.
	 *
	 * @param minValue the min value
	 * @param maxValue the max value
	 */
	public void setRange(double minValue, double maxValue) {
		min = minValue;
		max = maxValue;
	}
	
	/**
	 * Scale.
	 *
	 * @param value the value
	 */
	@Override
	public void scale(double value) {
		min *= value;
		max *= value;
	}
	
	/**
	 * Contains.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean contains(double value) {
		if(Double.isNaN(value)) return min == Double.NEGATIVE_INFINITY && max == Double.POSITIVE_INFINITY;
		return value >= min && value < max;
	}
	
	/**
	 * Contains.
	 *
	 * @param range the range
	 * @return true, if successful
	 */
	public boolean contains(Range range) {
		return contains(range.min) && contains(range.max);
	}
	
	/**
	 * Intersects.
	 *
	 * @param range the range
	 * @return true, if successful
	 */
	public boolean intersects(Range range) {
		return contains(range.min) || contains(range.max) || range.contains(this);
	}
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @return the range
	 */
	public static Range parse(String text) {
		return parse(text, false);
	}
	
	/**
	 * Include.
	 *
	 * @param value the value
	 */
	public synchronized void include(double value) {
		if(value < min) min = value;
		if(value > max) max = value;		
	}
	
	/**
	 * Include.
	 *
	 * @param range the range
	 */
	public final void include(Range range) {
		include(range.min);
		include(range.max);
	}
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @param isPositive the is positive
	 * @return the range
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
	 * Span.
	 *
	 * @return the double
	 */
	public double span() {
		if(max <= min) return 0.0;
		return max - min;
	}
	
	/**
	 * Abs span.
	 *
	 * @return the double
	 */
	public double absSpan() {
		return Math.abs(span());
	}
	
	/**
	 * Grow.
	 *
	 * @param factor the factor
	 */
	public void grow(double factor) {
		double span = span();
		min -= factor * span;
		max += factor * span;		
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
	 * @param nf the nf
	 * @return the string
	 */
	public String toString(NumberFormat nf) {
		return nf.format(min) + ":" + nf.format(max);
	}
	
	/**
	 * Gets the empty range.
	 *
	 * @return the empty range
	 */
	public static Range getEmptyRange() {
		Range range = new Range();
		range.empty();
		return range;		
	}
	
	/**
	 * Gets the full range.
	 *
	 * @return the full range
	 */
	public static Range getFullRange() {
		Range range = new Range();
		range.full();
		return range;
	}
	
	/**
	 * Gets the positive range.
	 *
	 * @return the positive range
	 */
	public static Range getPositiveRange() {
		return new Range(0.0, Double.POSITIVE_INFINITY);
	}
	
	/**
	 * Gets the negative range.
	 *
	 * @return the negative range
	 */
	public static Range getNegativeRange() {
		return new Range(Double.NEGATIVE_INFINITY, 0.0);
	}
}

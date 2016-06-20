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
package jnum.data.fitting;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Util;
import jnum.data.DataPoint;
import jnum.data.WeightedPoint;
import jnum.math.Range;



// TODO: Auto-generated Javadoc
/**
 * The Class Parameter.
 */
public class Parameter extends DataPoint implements Penalty {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8118303849902647386L;
	
	
	/** The name. */
	private String name;
	
	/** Restrict the parameter to a specified range of values... */
	private Range range;
	
	/** The typical step-size when fitting the variable.
	 *  Values should produce a noticeable but small change when evaluating a function with
	 *  the parameter. E.g. for chi^2 evaluations, produce a delta chi^2 between ~0.001 and 1 
	 *  */
	private double stepSize = Double.NaN;
	
	/**
	 * Instantiates a new parameter.
	 *
	 * @param name the name
	 */
	public Parameter(String name) { 
		this.name = name; 
		setValue(Double.NaN);
		exact();
	}
	
	/**
	 * Instantiates a new parameter.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public Parameter(String name, double value) { this(name); setValue(value); }

	public Parameter(String name, double value, Range r) { 
	    this(name, value); 
	    if(!setRange(r)) throw new IllegalArgumentException("initial value outside of specified range.");
	}
	
	public Parameter(String name, double value, double stepSize) { 
	    this(name, value); 
	    setStepSize(stepSize);    
	}
	
	public Parameter(String name, double value, Range r, double stepSize) { 
	    this(name, value, r); 
	    setStepSize(stepSize); 
	}

	@Override
    public WeightedPoint copy() {
	    Parameter copy = (Parameter) super.copy();
	    if(name != null) copy.name = new String(name);
	    if(range != null) copy.range = range.copy();
	    return copy;
	}
	
	@Override
    public int hashCode() {
	    int hash = super.hashCode();
	    if(name != null) hash ^= name.hashCode();
	    if(range != null) hash ^= range.hashCode();
	    return hash;
	}
	
	@Override
    public boolean equals(Object o) {
	    if(!super.equals(o)) return false;
	    if(!(o instanceof Parameter)) return false;
	    Parameter p = (Parameter) o;
	    if(!Util.equals(name, p.name)) return false;
	    if(!Util.equals(range, p.range)) return false;
	    return true;
	}
	
	@Override
    public double penalty() {
	    if(range == null) return 0.0;
	    double value = value();
	    if(range.contains(value)) return 0.0;
	    double dev = (value < range.min() ? range.min() - value : value - range.max()) / getStepSize();
	    return dev * dev;
	}
	
	/**
     * Gets the name.
     *
     * @return 'true' if the current value is within the specified range, and 'false otherwise.
     */
	public boolean setRange(Range r) { 
	    this.range = r; 
	    if(r == null) return true;
	    if(range.span() <= 0.0) throw new IllegalArgumentException("zero or negative parameter range.");
	    if(value() < range.min()) setValue(range.min());
	    else if(value() > range.max()) setValue(range.max());
	    else return true;
	    return false;
	}
	
	public Range getRange() { return range; }
	
	public void setStepSize(double x) { stepSize = x; }
	
	public void setDefaultStepSize() { stepSize = Double.NaN; }
	
	public double getStepSize() {
	    if(!Double.isNaN(stepSize)) return stepSize;
	    if(range != null) if(range.isBounded()) return 1e-3 * range.span();
	    return value() == 0.0 ? 1e-6 : 1e-3 * value();
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String name() { return name; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.WeightedPoint#toString()
	 */
	@Override
	public String toString() {
		return name + " = " + (isExact() ? Double.toString(value()) : super.toString());		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.WeightedPoint#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat f) {
		return name + " = " + (isExact() ? f.format(value()) : super.toString(f));		
	}
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 */
	public void parse(String text) {
		StringTokenizer tokens = new StringTokenizer(text, " \t:");
		setValue(Double.parseDouble(tokens.nextToken()));
		if(tokens.hasMoreTokens()) {
			double rms = Double.parseDouble(tokens.nextToken());
			setWeight(1.0 / (rms * rms));
		}
		else exact();
	}
	
}

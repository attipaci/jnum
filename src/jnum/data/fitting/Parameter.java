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
package jnum.data.fitting;

import java.text.NumberFormat;

import jnum.Util;
import jnum.data.DataPoint;
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
	 *  
	 */
	private double stepSize = Double.NaN;
	
	/**
	 * Instantiates a new parameter with the specified identifier.
	 *
	 * @param name the name
	 */
	public Parameter(String name) { 
		this.name = name; 
		setValue(Double.NaN);
		exact();
	}
	
	/**
	 * Instantiates a new parameter with the specified identifier and initial value.
	 *
	 * @param name a name for easy identification of the parameter.
	 * @param value the initial value of the parameter.
	 */
	public Parameter(String name, double value) { this(name); setValue(value); }

	/**
	 * Instantiates a new parameter with the specified identifier, initial value, and a restricted range.
	 *
	 * @param name a name for easy identification of the parameter.
	 * @param value the initial value of the parameter.
	 * @param range a range of acceptable values for this parameter.
	 * 
	 * @throws IllegalArgumentException if the initial value is outside the specified range.
	 */
	public Parameter(String name, double value, Range range) { 
	    this(name, value); 
	    if(!setRange(range)) throw new IllegalArgumentException("initial value outside of specified range.");
	}
	
	/**
	 * Instantiates a new parameter with the specified identifier, initial value, and typical initial step size for 
	 * exploring the parameter space.
	 *
	 * @param name a name for easy identification of the parameter.
     * @param value the initial value of the parameter.
	 * @param stepSize a typical step size for this parameter. See @link{#setStepSize(double)}.
	 */
	public Parameter(String name, double value, double stepSize) { 
	    this(name, value); 
	    setStepSize(stepSize);    
	}
	
	/**
	 * Instantiates a new parameter with the specified identifier, initial value, restricted range, and a typical initial step size for 
     * exploring the parameter space.
	 *
	 * @param name a name for easy identification of the parameter.
     * @param value the initial value of the parameter.
	 * @param range a range of acceptable values for this parameter.
	 * @param stepSize a typical step size for this parameter. See @link{#setStepSize(double)}.
	 * 
	 * @throws IllegalArgumentException if the initial value is outside the specified range.
	 */
	public Parameter(String name, double value, Range range, double stepSize) { 
	    this(name, value, range); 
	    setStepSize(stepSize); 
	}
	
	@Override
    public Parameter clone() {
	    return (Parameter) super.clone();
	}

	/* (non-Javadoc)
	 * @see jnum.data.WeightedPoint#copy()
	 */
	@Override
    public Parameter copy() {
	    Parameter copy = (Parameter) super.copy();
	    if(range != null) copy.range = range.copy();
	    return copy;
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.WeightedPoint#hashCode()
	 */
	@Override
    public int hashCode() {
	    int hash = super.hashCode();
	    if(name != null) hash ^= name.hashCode();
	    if(range != null) hash ^= range.hashCode();
	    return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.WeightedPoint#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object o) {
	    if(this == o) return true;
	    if(!(o instanceof Parameter)) return false;
	    if(!super.equals(o)) return false;
	    
	    Parameter p = (Parameter) o;
	    if(!Util.equals(name, p.name)) return false;
	    if(!Util.equals(range, p.range)) return false;
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.fitting.Penalty#penalty()
	 */
	@Override
    public double penalty() {
	    if(range == null) return 0.0;
	    double value = value();
	    if(range.contains(value)) return 0.0;
	    double dev = (value < range.min() ? range.min() - value : value - range.max()) / getStepSize();
	    return dev * dev;
	}
	
	/**
	 * Restrict the parameter to a range of values.
	 *
	 * @param r the range of acceptable parameter values.
	 * @return 'true' if the current value is within the specified range, or false otherwise.
	 * 
	 * @see {@link #getRange()}
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
	
	/**
	 * Gets the range of acceptable parameter values.
	 *
	 * @return the range of acceptable parameter values or null if unrestricted.
	 * 
	 * @see {@link #setRange(Range)}
	 */
	public Range getRange() { return range; }
	
	/**
	 *  Set the typical step-size when fitting the variable.
     *  Values should produce a noticeable but small change when evaluating a function with
     *  the parameter. E.g. for chi^2 evaluations, produce a delta chi^2 between ~0.001 and 1 
     *  
	 * @param x the new step size
	 */
	public void setStepSize(double x) { stepSize = x; }
	
	/**
	 * Sets the default step size for this parameter, which is calculated either based on the restricted (and bounded)
	 * range of acceptable values (if any), or the current magnitude of the parameter.
	 * @see {@link #setStepSize(double)}
	 */
	public void setDefaultStepSize() { stepSize = Double.NaN; }
	
	/**
	 * Gets the step size, which may be explicitly set via {@link #setStepSize(double)}, or else calculated
	 * based either on the restricted (and bounded) range, or the magnitude of parameter. 
	 *
	 * @return the recommended step size for exploring the parameter space during fitting...
	 * 
	 * @see {@link #setStepSize(double)}, {@link #setDefaultStepSize()}
	 */
	public double getStepSize() {
	    if(!Double.isNaN(stepSize)) return stepSize;
	    if(range != null) if(range.isBounded()) return 1e-3 * range.span();    // 0.1% of the range
	    return value() == 0.0 ? 1e-6 : 1e-3 * value();                         // 0.1% of the value, or 1e-6 if zero
	}
	
	/**
	 * Gets the name identifier of this parameter.
	 *
	 * @return the name or identifier assigned to this parameter.
	 */
	public String name() { return name; }
	
	/* (non-Javadoc)
	 * @see jnum.data.WeightedPoint#toString()
	 */
	@Override
	public String toString() {
		return name + " = " + (isExact() ? Double.toString(value()) : super.toString());		
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.WeightedPoint#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat f) {
		return name + " = " + (isExact() ? f.format(value()) : super.toString(f));		
	}
	
	
}

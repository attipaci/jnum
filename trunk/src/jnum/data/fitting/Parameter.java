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
package jnum.data.fitting;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.data.DataPoint;



// TODO: Auto-generated Javadoc
/**
 * The Class Parameter.
 */
public class Parameter extends DataPoint {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8118303849902647386L;
	/** The name. */
	private String name;
	
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
	public Parameter(String name, double value) { this(name); this.setValue(value); }

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }
	
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

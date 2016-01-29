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
// Copyright (c) 2007 Attila Kovacs 

package jnum.data;

import jnum.util.Unit;
import jnum.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class DataPoint.
 */
public class DataPoint extends WeightedPoint {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7893241481449777111L;

	/**
	 * Instantiates a new data point.
	 */
	public DataPoint() { super(); }

	/**
	 * Instantiates a new data point.
	 *
	 * @param value the value
	 * @param rms the rms
	 */
	public DataPoint(double value, double rms) {
		super(value, 1.0/(rms*rms));
	}
	
	/**
	 * Instantiates a new data point.
	 *
	 * @param template the template
	 */
	public DataPoint(WeightedPoint template) {
		super(template);
	}
	
	/**
	 * Rms.
	 *
	 * @return the double
	 */
	public double rms() { return 1.0/Math.sqrt(weight()); }

	/**
	 * Sets the rms.
	 *
	 * @param value the new rms
	 */
	public void setRMS(final double value) { setWeight(1.0 / (value * value)); }
	
	/**
	 * To string.
	 *
	 * @param unit the unit
	 * @return the string
	 */
	public String toString(Unit unit) { return toString(this, unit); }

	/**
	 * Significance.
	 *
	 * @return the double
	 */
	public final double significance() { return significanceOf(this); }
	
	/**
	 * Significance of.
	 *
	 * @param point the point
	 * @return the double
	 */
	public static double significanceOf(final WeightedPoint point) {
		return Math.abs(point.value()) * Math.sqrt(point.weight());
	}
	
	/**
	 * To string.
	 *
	 * @param point the point
	 * @param unit the unit
	 * @return the string
	 */
	public static String toString(DataPoint point, Unit unit) {
		return toString(point, unit, " +- ", " ");
	}
	
	/**
	 * To string.
	 *
	 * @param point the point
	 * @param unit the unit
	 * @param before the before
	 * @param after the after
	 * @return the string
	 */
	public static String toString(DataPoint point, Unit unit, String before, String after) {
		double u = unit == null ? 1.0 : unit.value();
		double value = point.value() / u;
		double rms = point.rms() / u;
		double res = Math.pow(10.0, 2 - errorFigures + Math.floor(Math.log10(rms)));
		
		return Util.getDecimalFormat(Math.abs(value) / res, 6).format(point.value() / u) 
			+ before + Util.s[errorFigures].format(rms) + after + (unit == null ? "" : unit.name());   
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.WeightedPoint#toString(java.lang.String, java.lang.String)
	 */
	@Override
	public String toString(String before, String after) {
		return toString(this, null, before, after);
	}
	
	public static DataPoint[] createArray(int size) {
		DataPoint[] p = new DataPoint[size];
		for(int i=size; --i >= 0; ) p[i] = new DataPoint();
		return p;
	}
	
	/** The error figures. */
	public static int errorFigures = 2;
}

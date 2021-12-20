/* *****************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import jnum.Constant;
import jnum.Util;


/**
 * A base class for interpolating between data pointsin one dimension.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class Interpolator extends ArrayList<Interpolator.Point> {

    /** */
	private static final long serialVersionUID = -7962217110619389946L;

	private String fileName = "";
	
	protected Interpolator() {}

	
	protected Interpolator(String fileName) throws IOException {
	    this();
		read(fileName);
		Util.detail(this, getClass().getSimpleName() + "> " + size() + " records parsed.");	
	}
	
	@Override
	public int hashCode() { return fileName.hashCode(); }
	

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Interpolator)) return false;
		if(!super.equals(o)) return false;
		Interpolator i = (Interpolator) o;
		if(!fileName.equals(i.fileName)) return false;
		return true;
	}
	
	/**
	 * Sorts the interpolation data so it can be used. This is automatically done after reading in
	 * new data from a file, but the user may want to call it if adding data programatically in
	 * non-specific order to the table.
	 * 
	 */
	public void validate() {
	    Collections.sort(this);
	}
	
	/**
	 * Reads data from a file, and then validates it in preparation to interpolating.
	 * If the file name is the same as the prior one read, then it will return
	 * early, leaving the existing interpolation data untouched.
	 * 
	 * @param fileName
	 * @throws IOException
	 * 
	 * @see #readData(String)
	 * @see #validate()
	 */
	public void read(String fileName) throws IOException {
		if(fileName.equals(this.fileName)) return;
		readData(fileName);
		this.fileName = fileName;
		validate();
	}
	
	/**
	 * The actual reading of the interpolation data, which concrete subclasses must implement.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	protected abstract void readData(String fileName) throws IOException; 
	
	/**
	 * Returns the interpolated value at the specified location.
	 * 
	 * @param ordinate     the location of the point at which we want to interpolate
	 * @return             the interpolated value at the specified ordinate location.
	 */
	public double getValue(double ordinate) { return getTrapesoidValue(ordinate); }
	
	
	/**
	 * Returns the linearly interpolated value (trapesoid method) between the two nearest 
	 * known data surrounding the loication of the interpolation.
	 * 
	 * @param ordinate     the location of the point at which we want to interpolate
	 * @return             the linearly interpolated value (trapesoid method) at the specified ordinate location.
	 * @throws ArrayIndexOutOfBoundsException
	 *                     if the ordinate it outside the range covered by data points in this table 
	 *                     
	 * @see #getSmoothValue(double, double)
	 * @see #getTrapesoidValue(double)
	 */
	public double getTrapesoidValue(double ordinate) throws ArrayIndexOutOfBoundsException {
		int upper = getIndexAbove(ordinate);
		
		double dt1 = ordinate - get(upper-1).ordinate;
		double dt2 = get(upper).ordinate - ordinate;
		
		return (dt2 * get(upper-1).value + dt1 * get(upper).value) / (dt1 + dt2);	
	}	
	
	/**
	 * Returns the index of the known data point whose location is nearest above the specified
	 * ordinate location.
	 * 
	 * @param ordinate     the location of the point at which we want to interpolate
	 * @return             the data index that bracket the specified location from above.
	 * @throws ArrayIndexOutOfBoundsException
	 *                     if the ordinate it outside the range covered by data points in this table
	 *                     
	 * @see #getTrapesoidValue(double)
	 */
	public int getIndexAbove(double ordinate) throws ArrayIndexOutOfBoundsException {
		int lower = 0, upper = size()-1;
		
		if(ordinate < get(lower).ordinate || ordinate > get(upper).ordinate) 
			throw new ArrayIndexOutOfBoundsException(getClass().getSimpleName() + "> outside of interpolator range.");
		
		while(upper - lower > 1) {
			int i = (upper + lower) >>> 1;
			double x = get(i).ordinate;
			if(ordinate >= x) lower = i;
			if(ordinate <= x) upper = i;
		}
		
		return upper;
	}
	
	/**
	 * Returns a smoothed interpolated value at some location, using a Gaussian kernel to combine information
	 * from nearby data above and below the requested location.
	 * 
	 * @param ordinate     the location of the point at which we want to interpolate
	 * @param fwhm         the full-width half maximum (FWHM) of the Gaussian smoothing kernel.
	 * @return             the smoothed interpolated value at the requested location.
	 * @throws ArrayIndexOutOfBoundsException
	 * 
	 * @see #getTrapesoidValue(double)
	 */
	public double getSmoothValue(double ordinate, double fwhm) throws ArrayIndexOutOfBoundsException {
		int i0 = getIndexAbove(ordinate); 
		
		double sum = 0.0, sumw = 0.0;
		Point last = get(i0);
		
		double sigma = fwhm / Constant.sigmasInFWHM;
		double A = -0.5 / (sigma * sigma);
		double dt;
		
		int i = i0;
		while(i < size() && (dt = last.ordinate - ordinate) < 2 * fwhm) {
			double w = Math.exp(-A*dt*dt);
			sum += w * last.value;
			sumw += w;
			last = get(++i);
		}
		
		i = i0-1;
		last = get(i0);
		while(i >= 0 && (dt = ordinate - last.ordinate) < 2 * fwhm) {
			double w = Math.exp(A*dt*dt);
			sum += w * last.value;
			sumw += w;
			last = get(--i);
		}
		
		return sum / sumw;
	}
	

	/*
	public double getSplineValue(double ordinate) throws ArrayIndexOutOfBoundsException {
	    
	    int iAbove = getIndexAbove(ordinate);
	   
	    double sum = 0.0, sumw = 0.0;

	    for(int d=-2; d<=1; d++) {
	        int i = iAbove + d;

	        if(i < 0) continue;
	        else if(i >= size()) break;

	        Data p = get(i);
	        double c = BicubicSplineCoeffs.valueFor(p.ordinate - ordinate); // TODO how to deal with irregular samples...

	        sum += c * p.value;
	        sumw += c;
	    }

	    return sum / sumw;   
	}   
    */
	
	/**
	 * A measured or otherwise known point value, which can be used for interpolation around it.
	 * 
	 * @author Attila Kovacs
	 *
	 */
	public static class Point implements Comparable<Interpolator.Point> {
	    
		private double ordinate, value;

		/**
		 * Instantiates a new fixed-defined point to use by the interpolated.
		 * 
		 * @param ordinate    the 1D location of the point
		 * @param value       the known value at that point
		 */
		public Point(double ordinate, double value) {
		    this.ordinate = ordinate;
		    this.value = value;
		}

		/**
		 * Returns the location of the known fixed point.
		 * 
		 * @return        the ordinate location of this point.
		 */
		public final double ordinate() {
		    return ordinate;
		}
		
		/**
		 * Returns the value of this know fixed point.
		 * 
		 * @return        the known value at this point.
		 */
		public final double value() {
		    return value;
		}
		
		@Override
		public int compareTo(Point other) {
			return Double.compare(ordinate, other.ordinate);
		}
		
	}

	
	/**
	 * An enumeration of the interpolator types, with varying polynomial orders.
	 * 
	 * @author Attila Kovacs
	 *
	 */
	public static enum Type {
	    /** use the nearest value */
	    NEAREST(0),
	    
	    /** linear interpolation between the surrounding dat (trapesoid method) */
	    LINEAR(1),
	    
	    /** piecewise quadratic interpolation using 3 point that bracket the data */
	    PIECEWISE_QUADRATIC(2),
	    
	    /** cubic spline interpolation, using the 2 nearest data points on each side of the interpolated location */ 
	    CUBIC_SPLINE(3);
	    
	    /** the polynomial order of the interpolation type. */
	    private int order;
	    
	    Type(int order) {
	        this.order = order;
	    }
	    
	    /**
	     * Returns the polynomial order for this interpolation type.
	     * 
	     * @return     the polynomial order for this type of interpolator.
	     */
	    public int order() {
	        return order;
	    }
	}
	
}



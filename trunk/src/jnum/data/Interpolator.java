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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import jnum.Constant;


// TODO: Auto-generated Javadoc
/**
 * The Class Interpolator.
 */
public abstract class Interpolator extends ArrayList<Interpolator.Data> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7962217110619389946L;
	
	/** The verbose. */
	public boolean verbose = false;
	
	/** The file name. */
	public String fileName = "";
	
	/**
	 * Instantiates a new interpolator.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Interpolator(String fileName) throws IOException {
		read(fileName);
		if(verbose) System.err.println(getClass().getSimpleName() + "> " + size() + " records parsed.");	
		Collections.sort(this);
	}
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void read(String fileName) throws IOException {
		if(fileName.equals(this.fileName)) return;
		readData(fileName);
		this.fileName = fileName;
	}
	
	/**
	 * Read data.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected abstract void readData(String fileName) throws IOException; 
	
	// Linear interpolation.
	// Throws Exception if MJD is outside of the interpolator range.
	/**
	 * Gets the value.
	 *
	 * @param ordinate the ordinate
	 * @return the value
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public double getValue(double ordinate) throws ArrayIndexOutOfBoundsException {
		int upper = getIndexAbove(ordinate);
		
		double dt1 = ordinate - get(upper-1).ordinate;
		double dt2 = get(upper).ordinate - ordinate;
		
		return (dt2 * get(upper-1).value + dt1 * get(upper).value) / (dt1 + dt2);	
	}	
	
	/**
	 * Gets the index above.
	 *
	 * @param ordinate the ordinate
	 * @return the index above
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public int getIndexAbove(double ordinate) throws ArrayIndexOutOfBoundsException {
		int lower = 0, upper = size()-1;
		
		if(ordinate < get(lower).ordinate || ordinate > get(upper).ordinate) 
			throw new ArrayIndexOutOfBoundsException(getClass().getSimpleName() + "> outside of interpolator range.");
		
		while(upper - lower > 1) {
			int i = (upper + lower) >> 1;
			double x = get(i).ordinate;
			if(ordinate >= x) lower = i;
			if(ordinate <= x) upper = i;
		}
		
		return upper;
	}
	

	/**
	 * Gets the smooth value.
	 *
	 * @param ordinate the ordinate
	 * @param fwhm the fwhm
	 * @return the smooth value
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public double getSmoothValue(double ordinate, double fwhm) throws ArrayIndexOutOfBoundsException {
		int i0 = getIndexAbove(ordinate); 
		
		double sum = 0.0, sumw = 0.0;
		Data last = get(i0);
		
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

	/**
	 * The Class Data.
	 */
	public class Data implements Comparable<Interpolator.Data> {
		
		/** The value. */
		public double ordinate, value;
		
		/**
		 * Instantiates a new data.
		 */
		public Data() {}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Data other) {
			return Double.compare(ordinate, other.ordinate);
		}
		
	}

	
}



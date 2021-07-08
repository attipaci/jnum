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



public abstract class Interpolator extends ArrayList<Interpolator.Data> {

	private static final long serialVersionUID = -7962217110619389946L;

	public boolean verbose = false;

	public String fileName = "";

	
	public Interpolator() {}

	
	public Interpolator(String fileName) throws IOException {
	    this();
		read(fileName);
		if(verbose) Util.info(this, getClass().getSimpleName() + "> " + size() + " records parsed.");	
		validate();
	}
	
	@Override
	public int hashCode() { return super.hashCode() ^ fileName.hashCode() ^ (verbose ? 1 : 0); }
	

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Interpolator)) return false;
		if(!super.equals(o)) return false;
		Interpolator i = (Interpolator) o;
		if(verbose != i.verbose) return false;
		if(!fileName.equals(i.fileName)) return false;
		return true;
	}
	

	public void validate() {
	    Collections.sort(this);
	}
	

	public void read(String fileName) throws IOException {
		if(fileName.equals(this.fileName)) return;
		readData(fileName);
		this.fileName = fileName;
	}
	

	protected abstract void readData(String fileName) throws IOException; 
	

	public double getValue(double ordinate) { return getTrapesoidValue(ordinate); }
	
	// Linear interpolation.
	// Throws Exception if MJD is outside of the interpolator range.
	public double getTrapesoidValue(double ordinate) throws ArrayIndexOutOfBoundsException {
		int upper = getIndexAbove(ordinate);
		
		double dt1 = ordinate - get(upper-1).ordinate;
		double dt2 = get(upper).ordinate - ordinate;
		
		return (dt2 * get(upper-1).value + dt1 * get(upper).value) / (dt1 + dt2);	
	}	
	

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
	

	public static class Data implements Comparable<Interpolator.Data> {

		public double ordinate, value;

		public Data() {}
		
		@Override
		public int compareTo(Data other) {
			return Double.compare(ordinate, other.ordinate);
		}
		
	}

	
}



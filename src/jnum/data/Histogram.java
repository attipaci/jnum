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

package jnum.data;

import java.util.Hashtable;

import jnum.ExtraMath;
import jnum.Util;


public class Histogram implements Cloneable {

	private Hashtable<Integer, RealValue> bins = new Hashtable<>();

	private double resolution;
	

	public Histogram(double resolution) {
		if(resolution == 0.0 || Double.isNaN(resolution)) 
			throw new IllegalStateException("Illegal bin resolution.");
		this.resolution = Math.abs(resolution);	
	}
	

	@Override
	public Histogram clone() {
		try { return (Histogram) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }		
	}
	

    public double getResolution() { return resolution; }
	

	public void add(double value) {
		add(value, 1.0);
	}
	

	public void add(double value, double counts) {
		int binValue = binFor(value);
		if(bins.containsKey(binValue)) bins.get(binValue).add(counts);
		else bins.put(binValue, new RealValue(counts));
	}
	
	public double totalCounts() {
		double totalCounts = 0.0;
		for(int bin : bins.keySet()) totalCounts += bins.get(bin).value(); 
		return totalCounts;
	}
	

	public double countsFor(double value) {
		int binValue = binFor(value);
		if(bins.containsKey(binValue)) return bins.get(binValue).value();
		return 0;
	}
	

	public int binFor(double value) {
		return (int)Math.round(value/resolution);
	}
	

	public int size() { return bins.size(); }
	

	public void clear() { bins.clear(); }
	

	public boolean isEmpty() { return bins.isEmpty(); }
	

	public double getMinBinValue() {
		double min = Double.POSITIVE_INFINITY;
		for(int bin : bins.keySet()) if(bin < min) min = bin;
		return resolution * min;
	}
	

	public double getMaxBinValue() {
		double max = Double.NEGATIVE_INFINITY;
		for(int bin : bins.keySet()) if(bin > max) max = bin;
		return resolution * max;		
	}


	@Override
	public String toString() {
		String text = "# value\tcounts\terr\n";
		double min = getMinBinValue();
		double max = getMaxBinValue() + 0.5 * resolution;
		
		for(double binValue = min; binValue < max; binValue += resolution) {
			text += binValue + "\t" + Util.e3.format(countsFor(binValue)) + "\t" + Util.e3.format(Math.sqrt(countsFor(binValue))) + "\n";
		}
		
		return text;
	}
	

	public double getMaxDev() {
		return Math.max(Math.abs(getMaxBinValue()), Math.abs(getMinBinValue()));	
	}
	

	public static Histogram product(Histogram a, Histogram b) {
		if(a.resolution != b.resolution) 
			throw new IllegalArgumentException("Incompatible bin resolutions");
		
		Histogram product = a.clone();
		product.bins = new Hashtable<>();
		
		for(int binA : a.bins.keySet()) for(int binB : b.bins.keySet()) {
			product.add((binA + binB)*product.resolution, a.bins.get(binA).value() * b.bins.get(binB).value());	
		}
		
		return product;
	}


	public void add(Histogram histogram) {
		addMultipleOf(histogram, 1.0);		
	}
	

	public void addMultipleOf(Histogram histogram, double factor) {
		if(histogram.resolution != resolution) 
			throw new IllegalArgumentException("Incompatible bin resolutions");
		
		for(int bin : histogram.bins.keySet()) add(bin*resolution, factor*histogram.countsFor(bin*resolution));
	}
	

	public double[] toFFTArray() {
		return toFFTArray(2 * ExtraMath.pow2ceil((int)Math.ceil(getMaxDev() / resolution)));
		
	}
	

	public double[] toFFTArray(int N) {
		
		double[] data = new double[N];
		
		for(int bin : bins.keySet()) {
			double value = bins.get(bin).value();
			if(bin < 0) bin = N + bin;
			data[bin] = value;
		}
		
		return data;
	}
	
	
	public void add(Object data) throws IllegalArgumentException {   
	    if(data instanceof Number) add(((Number) data).doubleValue());
	    else if(data instanceof WeightedPoint) add(((WeightedPoint) data).value());
	    else if(data instanceof double[]) add((double[]) data);
	    else if(data instanceof float[]) add((float[]) data);
	    else if(data instanceof long[]) add((long[]) data);
	    else if(data instanceof int[]) add((int[]) data);
	    else if(data instanceof short[]) add((short[]) data);
	    else if(data instanceof byte[]) add((byte[]) data);
	    else if(data instanceof Object[]) {
	        for(Object value : (Object[]) data) add(value);
	    }
	    else throw new IllegalArgumentException("Cannot add type " + data.getClass().getSimpleName() + " to histogram.");
    }
	
    public void add(double[] data) {   
        for(double value : data) add(value);
    }
    
    public void add(float[] data) {   
        for(double value : data) add(value);
    }
    
    public void add(long[] data) {   
        for(double value : data) add(value);
    }
    
    public void add(int[] data) {   
        for(double value : data) add(value);
    }
    
    public void add(short[] data) {   
        for(double value : data) add(value);
    }
    
    public void add(byte[] data) {   
        for(double value : data) add(value);
    }
    
    
    public static Histogram createFrom(Object data, double binSize) {
        Histogram histogram = new Histogram(binSize);
        histogram.add(data);
        return histogram;
    }
    
}

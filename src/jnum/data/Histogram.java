/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.util.Hashtable;

import jnum.ExtraMath;
import jnum.NonConformingException;
import jnum.PointOp;
import jnum.Util;

/**
 * Simple binning of data, with some resolution. Each histogram bin typically contains the 
 * counts (occurrences) of data values falling in the value range represented by the bin.
 * 
 * @author Attila Kovacs
 *
 */
public class Histogram implements Cloneable {

	private Hashtable<Integer, RealValue> bins = new Hashtable<>();

	private double resolution;
	
	/** 
	 * Intantiates ar new histogram with a specified bin size (resolution).
	 * 
	 * @param resolution       the size of a bin in the histogram.
	 */
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
	
	/**
	 * Returns the resolution (bin size) used for this historgram.
	 * 
	 * @return     the resolution (bin size) of this histogram.
	 */
    public double getResolution() { return resolution; }
	
    /**
     * Adds a datum to this histogram. The bin count in which the specified value falls
     * is incremented by one.
     * 
     * @param value     the value to bin
     * 
     * @see #add(double, double)
     */
	public void add(double value) {
		add(value, 1.0);
	}
	
	/**
	 * Adds a value to bin to this histogram with the specified counts. The bin count
	 * for the value is incremented by the specified counts. Non-finite values are 
	 * ignored in the binning.
	 * 
	 * @param value
	 * @param counts
	 * 
	 * @see #add(double)
	 */
	public void add(double value, double counts) {
	    if (!Double.isFinite(value)) return;
		int binValue = binFor(value);
		if(bins.containsKey(binValue)) bins.get(binValue).add(counts);
		else bins.put(binValue, new RealValue(counts));
	}
	
	/**
	 * Returns the total counts over all bins in this histogram.
	 * 
	 * @return     the total counts over all bins.
	 * 
	 * @see #countsFor(double)
	 */
	public double totalCounts() {
		double totalCounts = 0.0;
		for(int bin : bins.keySet()) totalCounts += bins.get(bin).value(); 
		return totalCounts;
	}
	
	/**
	 * Returns the counts for the bin in which teh specified value belongs
	 * 
	 * @param value    the value, which determines the bin for which counts are reported/
	 * @return         the accumulated counts in the bin specified by the value.
	 */
	public double countsFor(double value) {
		int binValue = binFor(value);
		if(bins.containsKey(binValue)) return bins.get(binValue).value();
		return 0;
	}
	
	/**
	 * Returns the histogram bin index correspondin to the specified value.
	 * 
	 * @param value    the value to bin
	 * @return         the bin index corresponding to the value.
	 */
	public int binFor(double value) {
		return (int)Math.round(value/resolution);
	}
	
	/**
	 * Returns the number of bins in this histogram (populated or not).
	 * 
	 * @return     the total number of bins in this histogram.
	 */
	public int size() { return bins.size(); }
	
	/**
	 * Clears this histogram, by resetting the counts in all bins to zero. 
	 * 
	 */
	public void clear() { bins.clear(); }
	
	/**
	 * Checks if this histogram is empty, that is if it contains no binned data.
	 * 
	 * @return     <code>true</code> if the histogram contains no binned data, otherwise <code>false</code>
	 */
	public boolean isEmpty() { return bins.isEmpty(); }
	
	/**
	 * Returns the lowest bin value that was defined in this histogram. A histogram bin is 
     * defined if and only if it had a value explicitly added previously.
	 * 
	 * @return     the (ordinate) value of highest bin defined (that is containing data).
	 * 
	 * @see #getMaxBinValue()
	 * @see #getMaxDev()
	 */
	public double getMinBinValue() {
		double min = Double.POSITIVE_INFINITY;
		for(int bin : bins.keySet()) if(bin < min) min = bin;
		return resolution * min;
	}
	

	/**
     * Returns the highest bin value that was defined in this histogram. A histogram bin is 
     * defined if and only if it had a value explicitly added previously.
     * 
     * @return     the (ordinate) value of lowest bin defined (that is containing data).
     * 
     * @see #getMaxBinValue()
     * @see #getMaxDev()
     */
	public double getMaxBinValue() {
		double max = Double.NEGATIVE_INFINITY;
		for(int bin : bins.keySet()) if(bin > max) max = bin;
		return resolution * max;		
	}


	/**
     * Returns the bin value farthest from zero, from among all bins that was defined in this histogram. A histogram bin is 
     * defined if and only if it had a value explicitly added previously.
     * 
     * @return     the (ordinate) value of lowest bin defined (that is containing data).
     * 
     * @see #getMaxBinValue()
     * @see #getMaxDev()
     */
    public double getMaxDev() {
        double max = 0;
        for(int bin : bins.keySet()) if(Math.abs(bin) > max) max = Math.abs(bin);
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
	
	/**
	 * Returns a new histogram that is that contains the bin-for-bin products of two histograms with
	 * matching bin resolutions
	 * 
	 * @param a        one of the histograms
	 * @param b        the other histogram
	 * @return         a new histogram whose bins are set to the product of the values in the input histograms for the matching bins.
	 * @throws NonConformingException  if the two historgrams have different bin resolutions.
	 */
	public static Histogram product(Histogram a, Histogram b) throws NonConformingException {
		if(a.resolution != b.resolution) 
			throw new NonConformingException("Incompatible bin resolutions");
		
		Histogram product = a.clone();
		product.bins = new Hashtable<>();
		
		for(int binA : a.bins.keySet()) for(int binB : b.bins.keySet()) {
			product.add((binA + binB)*product.resolution, a.bins.get(binA).value() * b.bins.get(binB).value());	
		}
		
		return product;
	}

	/**
	 * Adds data from another histogram to this one, rebinning as required.
	 * 
	 * @param histogram        the histogram containing the bin counts to add.
	 * 
	 * @see #addMultipleOf(Histogram, double)
	 */
	public void add(Histogram histogram) {
		addMultipleOf(histogram, 1.0);		
	}

	/**
     * Adds scaled bin counts from another histogram to this one, rebinning the data as necessary.
     * 
     * @param histogram     the histogram containing the bin counts to add.
     * @param factor        the scaling factor to apply before adding the counts from the argument. 
     * 
     * @see #addMultipleOf(Histogram, double)
     */
	public void addMultipleOf(Histogram histogram, double factor) {
		for(int bin : histogram.bins.keySet()) add(bin*resolution, factor*histogram.countsFor(bin*resolution));
	}
	
	/**
	 * Converts this histogram to an array that can be Fast Fourier Transformed. The array will 
	 * be sized large enough to contain all populated histogram bins, and padded as necessary to
	 * make the length a power of 2 for the FFT. For an array of size <i>N</i> the array element at 
	 * index 0 will correspond the bin at value 0, with positive bin values following in order 
	 * up to and including <i>N</i>/2. Negative bin values are packed in from the end of the
	 * array, s.t. the first negative bin resides in the element at index <i>N</i>-1.   
	 * 
	 * @return     the histogram represented by a simple array, suitable for FFT use.
	 */
	public double[] toFFTArray() {
		return toWrappedArray(2 * ExtraMath.pow2ceil((int)Math.ceil(getMaxDev() / resolution)));
		
	}
	
	/**
     * Converts this histogram to an array representation, where the negative bin values
     * are wrapped to the end of the array. The array size is chosen s.t. the first half of the
     * array represents positive bin values, while the second half has the negative bin values. I.e., 
     * for an array of size <i>N</i> the array element at index 0 will correspond the bin at value 0, 
     * with positive bin values following in order up to and including <i>N</i>/2. Negative bin values 
     * are packed in from the end of the array, s.t. the first negative bin resides in the element 
     * at index <i>N</i>-1.
     * 
     * @return     the histogram represented by a simple array
     * 
     * @see #toWrappedArray(int)
     */
    public double[] toWrappedArray() {
        return toWrappedArray((int) Math.ceil(getMaxDev() / resolution) << 1);
    }
	
	/**
	 * Converts this histogram to an array representation, where the negative bin values
	 * are wrapped to the end of the array. For an array of size <i>N</i> the 
	 * array element at index 0 will correspond the bin at value 0, with positive bin values following in order 
     * up to and including <i>N</i>/2. Negative bin values are packed in from the end of the
     * array, s.t. the first negative bin resides in the element at index <i>N</i>-1.
	 * 
	 * @param N    the number of elements the array should have. The caller is responsibe to ensure
	 *             that it is properly chosen given the range of positive and negative bin values
	 *             in the histogram
	 * @return     the histogram represented by a simple array
	 * 
	 * @see #toWrappedArray()
	 * @see #getMaxDev()
	 */
	public double[] toWrappedArray(int N) {
		
		double[] data = new double[N];
		
		for(int bin : bins.keySet()) {
			double value = bins.get(bin).value();
			if(bin < 0) bin = N + bin;
			data[bin] = value;
		}
		
		return data;
	}

	/**
	 * Adds the values from an array to the histogram. For each finite value in the array
	 * the bin count for the corresponding bin is incremented by 1.
	 * 
	 * @param data     the array of values to bin into this histogram.
	 * 
	 * @see #add(Object)
	 */
    public void add(double[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from an array to the histogram. For each finite value in the array
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the array of values to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(float[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from an array to the histogram. For each value in the array
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the array of values to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(long[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from an array to the histogram. For each value in the array
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the array of values to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(int[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from an array to the histogram. For each value in the array
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the array of values to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(short[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from an array to the histogram. For each value in the array
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the array of values to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(byte[] data) {   
        for(double value : data) add(value);
    }
    
    /**
     * Adds the values from a data container. For each value in the data
     * the bin count for the corresponding bin is incremented by 1.
     * 
     * @param data     the data container whose content to bin into this histogram.
     * 
     * @see #add(Object)
     */
    public void add(Data<?> data) {
        data.loopValid(new PointOp.Simple<Number>() {
            @Override
            public void process(Number point) {
                add(point.doubleValue());
            }
        });
    }
    
    /**
     * Adds the values from an object to the histogram. For each finite value 
     * in the object the bin count for the corresponding bin is incremented by 1. The
     * object may be one of:
     * <ul>
     *  <li>a {@link Number} instance.</li>
     *  <li>a {@link RealValue} instance.</li>
     *  <li>a primitive array of numbers, such as <code>double[]</code>.
     *  <li>a {@link Data} instance.</li>
     *  <li>a generic array composed of any of the elements in this list.</li>
     *  <li>an {@link Iterable} instance composed of any of the elements in this list.</li>
     * </ul>
     * 
     * @param data     the object containing values to bin into this hisrogram.
     * @throws IllegalArgumentException     if the object is of an unsupported type for binning.
     * 
     * @see #add(Data)
     * @see #add(double[])
     * @see #add(float[])
     * @see #add(long[])
     * @see #add(int[])
     * @see #add(short[])
     * @see #add(byte[])
     * 
     */
    public void add(Object data) throws IllegalArgumentException {   
        if(data instanceof Number) add(((Number) data).doubleValue());
        else if(data instanceof RealValue) add(((RealValue) data).value());
        else if(data instanceof Data) add((Data<?>) data);
        else if(data instanceof double[]) add((double[]) data);
        else if(data instanceof float[]) add((float[]) data);
        else if(data instanceof long[]) add((long[]) data);
        else if(data instanceof int[]) add((int[]) data);
        else if(data instanceof short[]) add((short[]) data);
        else if(data instanceof byte[]) add((byte[]) data);
        else if(data instanceof Object[]) {
            for(Object value : (Object[]) data) add(value);
        }
        else if(data instanceof Iterable) {
            for(Object value : (Iterable<?>) data) add(value);
        }
        else throw new IllegalArgumentException("Cannot add type " + data.getClass().getSimpleName() + " to histogram.");
    }
    
    /**
     * Returns a new histogram with data binned from the specified object. See {@link #add(Object)}
     * to check what types of data object may be used for the argument.
     * 
     * @param data          the object containing values to bin into this hisrogram.
     * @param binSize       the histogram bin resolution, that is the width of each histogram bin.
     * @return              a new Histogram created with the specified data object.
     * @throws IllegalArgumentException     if the object is of an unsupported type for binning.
     * 
     * @see #add(Object)
     */
    public static Histogram createFrom(Object data, double binSize) throws IllegalArgumentException {
        Histogram histogram = new Histogram(binSize);
        histogram.add(data);
        return histogram;
    }
    
}

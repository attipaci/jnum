/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.fft;

import jnum.data.WindowFunction;

// TODO: Auto-generated Javadoc
/**
 * The Class FFT1D.
 *
 * @param <Type> the generic type
 */
public abstract class FFT1D<Type> extends FFT<Type> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1722639496940144592L;

	/**
	 * Average power.
	 *
	 * @param data the data
	 * @param w the w
	 * @return the double[]
	 */
	public abstract double[] averagePower(Type data, double[] w);
	
	
	/**
	 * Average power.
	 *
	 * @param data the data
	 * @param windowSize the window size
	 * @return the double[]
	 */
	public double[] averagePower(Type data, int windowSize) {
		if(sizeOf(data) < windowSize) return averagePower(getPadded(data, windowSize), windowSize);
		return averagePower(data, WindowFunction.getHamming(windowSize));			
	}
	
	/**
	 * Size of.
	 *
	 * @param data the data
	 * @return the int
	 */
	public abstract int sizeOf(Type data);

	/**
	 * Gets the padded.
	 *
	 * @param data the data
	 * @param n the n
	 * @return the padded
	 */
	public abstract Type getPadded(Type data, int n);

	/**
	 * Image2bin.
	 *
	 * @param imageIndex the image index
	 * @param addressBits the address bits
	 * @return the int
	 */
	public static int image2bin(final int imageIndex, final int addressBits) {
		final int nyquist = 1 << (addressBits-1);
		
		if(imageIndex < 0) {
			if(imageIndex < -nyquist) throw new ArrayIndexOutOfBoundsException(imageIndex);
			return imageIndex + (nyquist<<1);
		}
		
		if(imageIndex > nyquist) throw new ArrayIndexOutOfBoundsException(imageIndex);
		return imageIndex;
	}
	
	/**
	 * Bin2 image.
	 *
	 * @param bin the bin
	 * @param addressBits the address bits
	 * @return the int
	 */
	public static int bin2Image(int bin, final int addressBits) {
		final int n = 1 << addressBits;
		
		if(bin > (n>>>1)) {
			if(bin >= n) throw new ArrayIndexOutOfBoundsException(bin);
			return bin - n;
		}
		
		if(bin < 0) throw new ArrayIndexOutOfBoundsException(bin);
		return bin;
	}
	
}

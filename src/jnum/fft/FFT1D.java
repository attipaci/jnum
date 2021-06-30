/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.fft;

import java.util.concurrent.ExecutorService;

import jnum.data.WindowFunction;
import jnum.parallel.Parallelizable;

/**
 * Abstract base class for all 1D FFT implementations.
 * 
 * @author Attila Kovacs
 *
 * @param <Type>    The generic type of the data elements in Fourier Transforms
 */
public abstract class FFT1D<Type> extends FFT<Type> {

	private static final long serialVersionUID = -1722639496940144592L;

	protected FFT1D() { super(); }
	
    protected FFT1D(ExecutorService executor) {
        super(executor);
    }

    protected FFT1D(Parallelizable processing) {
        super(processing);
    }


	public abstract double[] averagePower(Type data, double[] w);
	

	public double[] averagePower(Type data, int windowSize) {
		if(sizeOf(data) < windowSize) return averagePower(getPadded(data, windowSize), windowSize);
		return averagePower(data, WindowFunction.getHamming(windowSize));			
	}
	

	public abstract int sizeOf(Type data);


	public abstract Type getPadded(Type data, int n);


	public static int image2bin(final int imageIndex, final int addressBits) {
		final int nyquist = 1 << (addressBits-1);
		
		if(imageIndex < 0) {
			if(imageIndex < -nyquist) throw new ArrayIndexOutOfBoundsException(imageIndex);
			return imageIndex + (nyquist<<1);
		}
		
		if(imageIndex > nyquist) throw new ArrayIndexOutOfBoundsException(imageIndex);
		return imageIndex;
	}


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

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

package jnum.data;

import java.io.Serializable;

import jnum.util.HashCode;

/**
 * A cubic spline in one dimension. This class provides only the relevant coeffcients for
 * spline interpolation, and requires the user to perform the relevant summation with 
 * whatever data object they want to interpolate on. Below is an example of using splines 
 * to get an interpolated value on data contained in a <code>double[]</code> array.
 * 
 * For example,
 * 
 * <pre>
 * 
 * 
 *   double[] data = getMyData();
 *   CubicSpline spline = new CubicSpline();
 *   
 *   // we want to obtain a spline interpolated data for data index 13.73453:
 *   spline.centerOn(13.73453)
 *   
 *   // Figure out the data index range we can use for the spline, making
 *   // sure that it's both within the range of the spline and the data indices.
 *   int fromIndex = Math.max(spline.minIndex(), 0);
 *   int toIndex = Math.min(spline.maxIndex(), data.length);
 *   
 *   // we now sum the relevant data with the appropriate spline coefficients 
 *   // to actually perform the interpolation.
 *   double sum = 0.0, sumw = 0.0;
 *   
 *   for(int i=fromIndex; i &lt; toIndex; i++) {
 *      double w = spline.coefficientAt(i);
 *      sum += w * data[i];
 *      sumw += w;
 *   }
 *   
 *   // While often not necessary, we should nevertheless renormalize the
 *   // obtained sum by the sum of weights, to get a well-behaved interpolated
 *   // value even if interpolating near the edges of the available data. 
 *   
 *   // And voila, this is our spline interpolated value: * 
 *   double interp = sum / sumw;
 * 
 * 
 * </pre>
 * 
 * @author Attila Kovacs
 *
 */
public class CubicSpline implements Serializable {

	private static final long serialVersionUID = 5533149637827653369L;

	private double centerIndex = Double.NaN;

	private double localCenter = Double.NaN; // should be between 1--2

	private int i0;
	
	private double[] coeffs = new double[NCOEFFS];


	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(centerIndex);
	}
	

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof CubicSpline)) return false;

		CubicSpline spline = (CubicSpline) o;
		if(centerIndex != spline.centerIndex) return false;
		return true;
	}

	/**
	 * Centers this cubic spline on the specified fractional data index, for obtaining
	 * cubic spline interpolation at that location.
	 * 
	 * @param i    The fractional data index at which one would like to evaluate the spline.
	 */
	public void centerOn(double i) {
		if(centerIndex == i) return;
		
		centerIndex = i;
		i0 = (int)Math.floor(i - 1.0);
		setLocalCenter(i - i0);
	}
	

	/**
	 * Gets the spline coefficient for data at the specified index.
	 * 
	 * @param i        the index of data, which is expected to be {@link #minIndex()} &lt;= i &lt;
	 *                 {@link #maxIndex()}.
	 * @return
	 * @throws ArrayIndexOutOfBoundsException if the index i is not in the range set
	 *                 by {@link #minIndex()} and {@link #maxIndex()}.
	 */
	public final double coefficientAt(int i) throws ArrayIndexOutOfBoundsException {
		return coeffs[i - i0];		
	}
	
	/**
	 * Return the minimum (inclusive) valid data index for which the spline can provide
	 * a coefficient with the current centering. The summing iterator can use it as its
	 * start index, or end
	 * 
	 * @return     The mininum data index for which the spline is currently configure to yield a coefficient.
	 * 
	 * @see #centerOn(double)
	 */
	public final int minIndex() { return i0; }
	
	/**
     * Return the maximum (exclusive) valid data index for which the spline can provide
     * a coefficient with the current centering. The summation should only include
     * indices smaller than the returned value.
     * 
     * @return     The maximum (exclusive) data index for which the spline is currently configure to yield a coefficient.
     * 
     * @see #centerOn(double)
     */
	public final int maxIndex() { return i0 + NCOEFFS; }
	

	private void setLocalCenter(final double delta) {
	  
		// Calculate the (bicubic) spline coefficients (as necessary)...
		// See: https://en.wikipedia.org/wiki/Bicubic_interpolation
		// using a=-0.5	
		//
		
		if(localCenter != delta) {
			for(int i=NCOEFFS; --i >= 0; ) coeffs[i] = valueFor(i - delta);
			localCenter = delta;
		}
		
		// ~45 ops...
	}
	

	public static final double valueFor(double dx) {
	    dx = Math.abs(dx);
        return dx > 1.0 ? ((-0.5 * dx + 2.5) * dx - 4.0) * dx + 2.0 : (1.5 * dx - 2.5) * dx * dx + 1.0;
        // ~ 9 ops..
    }

	
	private static final int NCOEFFS = 4;
}

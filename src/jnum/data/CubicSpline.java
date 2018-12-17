/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data;

import java.io.Serializable;

import jnum.util.HashCode;


public class CubicSpline implements Serializable {

	private static final long serialVersionUID = 5533149637827653369L;

	private double centerIndex = Double.NaN;

	private double localCenter = Double.NaN; // should be between 1--2

	private int i0;

	public double[] coeffs = new double[NCOEFFS];

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(centerIndex);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof CubicSpline)) return false;

		CubicSpline spline = (CubicSpline) o;
		if(centerIndex != spline.centerIndex) return false;
		return true;
	}


	public void centerOn(double i) {
		if(centerIndex == i) return;
		
		centerIndex = i;
		i0 = (int)Math.floor(i - 1.0);
		setLocalCenter(i - i0);
	}
	

	public final double coefficientAt(int i) {
		return coeffs[i - i0];		
	}
	

	public final int minIndex() { return i0; }
	

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
	

	public final static double valueFor(double dx) {
	    dx = Math.abs(dx);
        return dx > 1.0 ? ((-0.5 * dx + 2.5) * dx - 4.0) * dx + 2.0 : (1.5 * dx - 2.5) * dx * dx + 1.0;
        // ~ 9 ops..
    }

	
	private final static int NCOEFFS = 4;
}

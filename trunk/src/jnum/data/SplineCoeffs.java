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
package jnum.data;

import java.io.Serializable;
import java.util.Arrays;

import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class SplineCoeffs.
 */
public class SplineCoeffs implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5533149637827653369L;

	/** The center index. */
	private double centerIndex;
	
	/** The local center. */
	private double localCenter; // should be between 1--2
	
	/** The i0. */
	private int i0;
	
	/** The coeffs. */
	public double[] coeffs = new double[4];

	
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.get(coeffs) ^ HashCode.get(centerIndex);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof SplineCoeffs)) return false;
		if(!super.equals(o)) return false;
		SplineCoeffs spline = (SplineCoeffs) o;
		if(centerIndex != spline.centerIndex) return false;
		return Arrays.equals(coeffs, spline.coeffs);
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		Arrays.fill(coeffs, 0.0);
	}
	
	/**
	 * Center on.
	 *
	 * @param i the i
	 */
	public void centerOn(double i) {
		if(centerIndex == i) return;
		centerIndex = i;
		setLocalCenter(i % 1.0);
		i0 = (int)Math.floor(i - 1.0);
	}
	
	/**
	 * Value at.
	 *
	 * @param i the i
	 * @return the double
	 */
	public final double valueAt(int i) {
		return coeffs[i - i0];		
	}
	
	/**
	 * Min index.
	 *
	 * @return the int
	 */
	public final int minIndex() { return i0; }
	
	/**
	 * Max index.
	 *
	 * @return the int
	 */
	public final int maxIndex() { return i0 + 4; }
	
	// offset between 0--1;
	/**
	 * Sets the local center.
	 *
	 * @param delta the new local center
	 */
	private void setLocalCenter(final double delta) {
		final double ic = delta + 1.0;
		
		// Calculate the spline coefficients (as necessary)...
		if(localCenter != ic) {
			for(int i=4; --i >= 0; ) {
				final double dx = Math.abs(i - ic);
				coeffs[i] = dx > 1.0 ? 
					((-0.5 * dx + 2.5) * dx - 4.0) * dx + 2.0 : (1.5 * dx - 2.5) * dx * dx + 1.0;
			}
			localCenter = ic;
		}
		
	}
}

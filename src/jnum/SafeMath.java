/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum;


public final class SafeMath {

	// Safe asin and acos for when rounding errors make values fall outside of -1:1 range.
	/**
	 * Asin.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double asin(final double value) {
		if(value < -1.0) return value < minusOnePlus ? Double.NaN : -Constant.rightAngle;
		else if(value > 1.0) return value > onePlus ? Double.NaN : Constant.rightAngle;
		return Math.asin(value);
	}
	
	/**
	 * Acos.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double acos(final double value) {
		if(value < -1.0) return value < minusOnePlus ? Double.NaN : Math.PI;
		else if(value > 1.0) return value > onePlus ? Double.NaN : 0.0;
		return Math.acos(value);
	}
	
	public final static double sqrt(final double value) {
		if(value < 0.0) return value < minusOnePlus ? Double.NaN : 0.0;
		return Math.sqrt(value);
	}
	
	private final static double epsilon = 1e-5;			// The maximum tolerated rounding error assuming float precision.
	private final static double onePlus = 1.0 + epsilon;
	private final static double minusOnePlus = -onePlus;

}

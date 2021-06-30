/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;



/**
 * A collection of math functions that are tolerant to rounding errors. The tolerance is designed to accomodate
 * typical rounding errors propagated through a moderate number of single-precision floating point operations
 * to the arguments.
 * 
 * @author Attila Kovacs
 */
public final class SafeMath {

	
	/**
	 * Safe version of {@link Math#asin(double)} for when rounding errors might push the argument just outside
	 * the legal -1:1 range.
	 * Values within the tolerance (1e-5) of the limits will return the limit values (+/- Pi/2).
	 * 
	 *
	 * @param value the argument, usually in the -1:1 range.
	 * @return the inverse sin() function of the argument, or {@link Double#NaN} if the argument is invalid.
	 * 
	 * @see Math#asin(double)
	 */
	public final static double asin(final double value) {
		if(value < -1.0) return value < minusOnePlus ? Double.NaN : -Constant.rightAngle;
		else if(value > 1.0) return value > onePlus ? Double.NaN : Constant.rightAngle;
		return Math.asin(value);
	}
	
	/**
	 * Safe version of {@link Math#acos(double)} for when rounding errors might push the argument just outside 
	 * the legal -1:1 range.
	 * Values within the tolerance (1e-5) of the limits will return the limit values (0 or Pi).
	 *
	 * @param value the argument, usually in the -1:1 range.
     * @return the inverse cos() function of the argument, or {@link Double#NaN} if the argument is invalid.
     * 
     * @see Math#acos(double)
	 */
	public final static double acos(final double value) {
		if(value < -1.0) return value < minusOnePlus ? Double.NaN : Math.PI;
		else if(value > 1.0) return value > onePlus ? Double.NaN : 0.0;
		return Math.acos(value);
	}
	
	/**
	 * Safe version of {@link Math#sqrt(double)} near 0.0, when rounding errors might push the argment just below zero.
	 * Values within the tolerance (1e-5) below 0.0 will return 0.0
	 *
	 * @param value the argument
	 * @return the square root, or {@link Double#NaN} if the argument is negative and inconsistent with 0.0.
	 * 
	 * @see Math#sqrt(double)
	 */
	public final static double sqrt(final double value) {
		if(value < 0.0) return value < minusOnePlus ? Double.NaN : 0.0;
		return Math.sqrt(value);
	}
	
	/** Floating point relative precision. */
	private final static double floatPrecision = 1e-5;			// The maximum tolerated rounding error assuming float precision.
	
	/** 1 plus the tolerated floating point rounding error. */
	private final static double onePlus = 1.0 + floatPrecision;
	
	/** the negative of 1 plus the tolerated floating point rounding error */
	private final static double minusOnePlus = -onePlus;

}

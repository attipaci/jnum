/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum;

import jnum.math.specialfunctions.ErrorFunction;

/**
 * Some commonly used math functions that should be in {@link java.lang.Math} but are not.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public final class ExtraMath {

    /**
     * Gets the square of a number.
     * 
     * @param x     the number
     * @return      <i>x</i><sup>2</sup>
     */
	public static final double square(final double x) {
		return x*x;
	}
	
	/**
     * Gets the cuve of a number.
     * 
     * @param x     the number
     * @return      <i>x</i><sup>3</sup>
     */
	public static final double cube(final double x) {
		return x*x*x;
	}


	
	/**
	 * Gets the inverse hyperbolic sine of a number.
	 * 
	 * Inverse hyperbolic functions from: http://www.devx.com/vb2themax/Tip/19026
	 * 
	 * 
	 * @param value    the number
	 * @return         asinh() of the argument
	 */
	public static final double asinh(final double value) {
		return Math.log(value + Math.sqrt(value * value + 1.0));
	}
	
	/**
     * Gets the inverse hyperbolic cosine of a number.
     * 
     * Inverse hyperbolic functions from: http://www.devx.com/vb2themax/Tip/19026
     * 
     * 
     * @param value    the number
     * @return         acos() of the argument
     */
	public static final double acosh(final double value) {
		return Math.log(value + Math.sqrt(value * value - 1.0));
	}
	
	/**
     * Gets the inverse hyperbolic tangent of a number.
     * 
     * Inverse hyperbolic functions from: http://www.devx.com/vb2themax/Tip/19026
     * 
     * 
     * @param value    the number
     * @return         atanh() of the argument
     */
	public static final double atanh(final double value) {
		return acoth(1.0 / value);
	}
	
	/**
     * Gets the inverse hyperbolic cotangent of a number.
     * 
     * Inverse hyperbolic functions from: http://www.devx.com/vb2themax/Tip/19026
     * 
     * 
     * @param value    the number
     * @return         acoth() of the argument
     */
	public static final double acoth(final double value) {	
		return 0.5 * Math.log((value + 1.0) / (value - 1.0));
	}
	
	/**
	 * Gets the sinc function of the argument, i.e. sin(x)/x.
	 * 
	 * @param x    the argument
	 * @return     sin(x)/x
	 */
	public static final double sinc(final double x) { return x == 0.0 ? 1.0 : Math.sin(x) / x; }

	/**
	 * Gets the base 2 logarithm of the argument
	 * 
	 * @param x    the argument
	 * @return     log<sub>2</sub>(x)
	 */
	public static final double log2(final double x) { return Math.log(x) * Constant.ilog2; }

	/**
	 * Gets the tangent for and x,y coordinate pair. Normally simply y/x, except this
	 * routine will return {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY}
	 * as appropriate instead of {@link Double#NaN}.
	 * 
	 * @param x        the x coordinate
	 * @param y        the y coordinate
	 * @return         y/x without {@link Double#NaN} pitfall.
	 */
	public static final double tan(final double x, final double y) {
		if(x == 0.0) {
			if(y == 0.0) return Double.NaN;
			return y > 0.0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}
		return y / x;
	}

	/**
	 * Gets the cosine for and x,y coordinate pair.
	 * 
	 * @param x        the x coordinate
     * @param y        the y coordinate
	 * @return         cosine of the angle defined by x,y as tan(angle) = y/x.
	 */
	public static final double cos(final double x, final double y) {
		return x / ExtraMath.hypot(x,  y);
	}

	/**
     * Gets the sine for and x,y coordinate pair.
     * 
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @return         sine of the angle defined by x,y as tan(angle) = y/x.
     */
	public static final double sin(final double x, final double y) {
		return y / ExtraMath.hypot(x,  y);
	}

	
	/**
	 * Evaluates the error function at the argument.
	 *
	 * @param x    the argument
	 * @return     the value of the error function at the specified argument.
	 * 
	 * @see #ierf(double)
	 */
	public static final double erf(final double x) {
		return ErrorFunction.at(x);
	}
	
	/**
	 * Evaluates the inverse error function at the argument.
	 *
	 * @param y    the error function value
	 * @return     the value at which the error function produces the specified value
	 * 
	 * @see #erf(double)
	 */
	public static final double ierf(final double y) {
		return ErrorFunction.inverseAt(y);
	}
	
	/**
	 * Calculates sum of squares for a list (or array) of doubles
	 *
	 * @param a    list (or array) of values
	 * @return     sum<sub>i</sub>(a<sub>i</sub><sup>2</sup>
	 * 
	 * @see #hypot(double...)
	 */
	public static final double sumSquares(final double... a) {
		double sum = 0.0;
		for(double x : a) sum += x*x;
		return sum;
	}


	/**
	 * A quck and dirty hypot function, i.e. sqrt(sum<sub>i</sub>(a<sub>i</sub><sup>2</sup>), and calculated as such. It is less computationally intensive than 
	 * {@link Math#hypot(double, double)}, at the cost that it may overgflow. It also support any number of arguments or 
	 * an array of input values.
	 *
	 * @param a    list (or array) of values
     * @return     sqrt(sum<sub>i</sub>(a<sub>i</sub><sup>2</sup>)
	 * 
	 * @see #sumSquares(double...)
	 * @see Math#hypot(double, double)
	 * @see #hypot(double...)
	 */
	public static final double hypot(final double... a) {
		return Math.sqrt(sumSquares(a));
	}
	

	/**
	 * Gets the the exponent p for which 2<sup>p</sup> equals or is just below the argument. 
	 * For example, for 35 it will return 5.
	 * 
	 * @param value    the argument
	 * @return         p for which 2<sup>p</sup> equals or is just below the argument
	 */
	public static final int log2floor(final int value) {		
		return 31 - Integer.numberOfLeadingZeros(value);
	}
	
	/**
     * Gets the the exponent p for which 2<sup>p</sup> equals or is just below the argument. 
     * For example, for 35 it will return 5.
     * 
     * @param value    the argument
     * @return         p for which 2<sup>p</sup> equals or is just below the argument
     */
	public static final int log2floor(final long value) {	
	    return 63 - Long.numberOfLeadingZeros(value);
	}

	/**
     * Gets the the exponent p for which 2<sup>p</sup> equals or is just above the argument. 
     * For example, for 35 it will return 6.
     * 
     * @param value    the argument
     * @return         p for which 2<sup>p</sup> equals or is just above the argument
     */
	public static final int log2ceil(final int value) {
		return 32 - Integer.numberOfLeadingZeros(value-1);
	}
	
	/**
     * Gets the the exponent p for which 2<sup>p</sup> equals or is just above the argument. 
     * For example, for 35 it will return 6.
     * 
     * @param value    the argument
     * @return         p for which 2<sup>p</sup> equals or is just above the argument
     */
	public static final int log2ceil(final long value) {
	    return 64 - Long.numberOfLeadingZeros(value-1L);
	}


	/**
     * Gets the the exponent p for which 2<sup>p</sup> is nearest to the argument in log space. 
     * For example, for 35 it will return 5.
     * 
     * @param value    the argument
     * @return         p for which 2<sup>p</sup> is nearest to the argument
     */
	public static final int log2round(final int value) {
		int pfloor = log2floor(value);
		int floor = 1<<pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor << 1) / value ? pfloor : pfloor + 1;
	}
	
	/**
     * Gets the the exponent p for which 2<sup>p</sup> is nearest to the argument in log space. 
     * For example, for 35 it will return 5.
     * 
     * @param value    the argument
     * @return         p for which 2<sup>p</sup> is nearest to the argument
     */
	public static final int log2round(final long value) {
		int pfloor = log2floor(value);
		long floor = 1L << pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor << 1) / value ? pfloor : pfloor + 1;
	}


	/**
	 * Gets the power-of-2 value that equals or is just below the argument. 
	 * For example, for 35 it will return 32.
	 * 
	 * @param value    the argument
	 * @return         the power-of-2 value that equals or is just below the argument
	 */
	public static final int pow2floor(final int value) { return Integer.highestOneBit(value); }
	

	/**
     * Gets the power-of-2 value that equals or is just below the argument. 
     * For example, for 35 it will return 32.
     * 
     * @param value    the argument
     * @return         the power-of-2 value that equals or is just below the argument
     */
	public static final long pow2floor(final long value) { return Long.highestOneBit(value); }


	/**
     * Gets the power-of-2 value that equals or is just above the argument. 
     * For example, for 35 it will return 64.
     * 
     * @param value    the argument
     * @return         the power-of-2 value that equals or is just above the argument
     */
	public static final int pow2ceil(final int value) { 
	    int floor = pow2floor(value);
	    return value == floor ? floor : floor << 1;
	}
	

	/**
     * Gets the power-of-2 value that equals or is just above the argument. 
     * For example, for 35 it will return 64.
     * 
     * @param value    the argument
     * @return         the power-of-2 value that equals or is just above the argument
     */
	public static final long pow2ceil(final long value) {
	    long floor = pow2floor(value);
        return value == floor ? floor : floor << 1;
	}

	/**
     * Gets the power-of-2 value that equals or is nearest to the argument in log-space. 
     * For example, for 35 it will return 32.
     * 
     * @param value    the argument
     * @return         the power-of-2 value that equals or is nearest to the argument
     */
	public static final int pow2round(final int value) { return 1 << log2round(value); }
	

	/**
     * Gets the power-of-2 value that equals or is nearest to the argument in log-space. 
     * For example, for 35 it will return 32.
     * 
     * @param value    the argument
     * @return         the power-of-2 value that equals or is nearest to the argument
     */
	public static final long pow2round(final long value) { return 1L << log2round(value); }


	/**
	 * Gets the integer that equals to the fraction a/b, or is just above it.
	 * For example for 2/3, it returns 1.
	 * 
	 * @param a    numerator in fraction
	 * @param b    denominator in fraction
	 * @return     the integer equal to, or just above the fraction a/b.
	 */
	public static final int roundupRatio(int a, int b) { return (a + b - 1) / b; }
	
	/**
     * Gets the integer that equals to the fraction a/b, or is just above it.
     * For example for 2/3, it returns 1.
     * 
     * @param a    numerator in fraction
     * @param b    denominator in fraction
     * @return     the integer equal to, or just above the fraction a/b.
     */
	public static final long roundupRatio(long a, long b) { return (a + b - 1L) / b; }
	
	/**
	 * Standardizes an input angle (in radians) to the caninical [-Pi:Pi] range.
	 * 
	 * @param angle    (rad) input angle
	 * @return         Srtandardized angle in the [-Pi:Pi] range.
	 */
	public static final double standardAngle(final double angle) {
	    if(angle > -Math.PI) if(angle <= Math.PI) return angle;
		return Math.IEEEremainder(angle, Constant.twoPi);
	}

	/**
	 * Gets the n<sup>th</sup> Fibonacci number.
	 * 
	 * @param n    The Fibonacci index
	 * @return     The Fibonacci number for the specified index.
	 */
    public final double fibonacci(double n) {
    	return (Math.pow(Constant.goldenRatio, n) - Math.pow(1.0 - Constant.goldenRatio, n)) / Constant.sqrt5;
    }
    
}

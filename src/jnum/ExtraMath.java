/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package jnum;

import jnum.math.specialfunctions.ErrorFunction;


public final class ExtraMath {


	public final static double square(final double x) {
		return x*x;
	}
	

	public final static double cube(final double x) {
		return x*x*x;
	}


	// Inverse hyperbolic functions from:
	// http://www.devx.com/vb2themax/Tip/19026
	public final static double asinh(final double value) {
		return Math.log(value + Math.sqrt(value * value + 1.0));
	}
	
	public final static double acosh(final double value) {
		return Math.log(value + Math.sqrt(value * value - 1.0));
	}
	
	public final static double atanh(final double value) {
		return acoth(1.0 / value);
	}
	
	public final static double acoth(final double value) {	
		return 0.5 * Math.log((value + 1.0) / (value - 1.0));
	}
		
	public final static double sinc(final double x) { return x == 0.0 ? 1.0 : Math.sin(x) / x; }

	public final static double log2(final double x) { return Math.log(x) * Constant.ilog2; }

	public final static double tan(final double x, final double y) {
		if(x == 0.0) {
			if(y == 0.0) return Double.NaN;
			return y > 0.0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}
		return y / x;
	}

	public final static double cos(final double x, final double y) {
		return x / ExtraMath.hypot(x,  y);
	}

	public final static double sin(final double x, final double y) {
		return y / ExtraMath.hypot(x,  y);
	}

	
	/**
	 * Error function.
	 *
	 * @param x the argument
	 * @return the value of the error function at the specified argument.
	 * 
	 * @see #ierf(double)
	 */
	public final static double erf(final double x) {
		return ErrorFunction.at(x);
	}
	
	/**
	 * Inverse error function.
	 *
	 * @param y the error function value
	 * @return the value at which the error function produces the specified value
	 * 
	 * @see #erf(double)
	 */
	public final static double ierf(final double y) {
		return ErrorFunction.inverseAt(y);
	}
	
	/**
	 * Sum of squares.
	 *
	 * @param a the first of two values
	 * @param b the second value
	 * @return a*a + b*b
	 * 
	 * @see #sumSquares(double, double, double)
	 */
	public final static double sumSquares(final double a, final double b) {
		return a*a + b*b;
	}

	/**
	 * Sum of squares.
	 *
	 * @param a the first of three values
	 * @param b the second value
	 * @param c the third value
	 * @return a*a + b*b + c*c
	 * 
	 * @see #sumSquares(double, double)
	 */
	public final static double sumSquares(final double a, final double b, final double c) {
		return a*a + b*b + c*c;
	}

	/**
	 * A quck and dirty hypot function, i.e. sqrt(a*a + b*b), and calculated as such. It is less computationally intensive than 
	 * {@link Math#hypot(double, double)}, at the cost that it may overgflow.
	 *
	 * @param a the first of two values
	 * @param b the second value
	 * @return sqrt(a*a + b*b)
	 * 
	 * @see Math#hypot(double, double)
	 * @see #hypot(double, double, double)
	 */
	public final static double hypot(final double a, final double b) {
		return Math.sqrt(a*a + b*b);
	}
	
	
	/**
	 * Add three arguments in quadrature, i.e. sqrt(a*a + b*b + c*c), and calculated as such. 
	 *
	 * @param a the first of three values
	 * @param b the second value
	 * @param c the third value
	 * @return sqrt(a*a + b*b + c*c)
	 * 
	 * @see #hypot(double, double)
	 */
	public final static double hypot(final double a, final double b, final double c) {
		return Math.sqrt(a*a + b*b + c*c);
	}


	public final static int log2floor(final int value) {		
		return 31 - Integer.numberOfLeadingZeros(value);
	}
	

	public final static int log2floor(final long value) {	
	    return 63 - Long.numberOfLeadingZeros(value);
	}

	public final static int log2ceil(final int value) {
		return 32 - Integer.numberOfLeadingZeros(value-1);
	}
	

	public final static int log2ceil(final long value) {
	    return 64 - Long.numberOfLeadingZeros(value-1L);
	}


	public final static int log2round(final int value) {
		int pfloor = log2floor(value);
		int floor = 1<<pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor << 1) / value ? pfloor : pfloor + 1;
	}
	
	public final static int log2round(final long value) {
		int pfloor = log2floor(value);
		long floor = 1L << pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor << 1) / value ? pfloor : pfloor + 1;
	}


	public final static int pow2floor(final int value) { return Integer.highestOneBit(value); }
	

	public final static long pow2floor(final long value) { return Long.highestOneBit(value); }


	public final static int pow2ceil(final int value) { 
	    int floor = pow2floor(value);
	    return value == floor ? floor : floor << 1;
	}
	

	public final static long pow2ceil(final long value) {
	    long floor = pow2floor(value);
        return value == floor ? floor : floor << 1;
	}


	public final static int pow2round(final int value) { return 1 << log2round(value); }
	

	public final static long pow2round(final long value) { return 1L << log2round(value); }


	public final static int roundupRatio(int a, int b) { return (a + b - 1) / b; }
	

	public final static long roundupRatio(long a, long b) { return (a + b - 1L) / b; }
	

	public final static double standardAngle(final double angle) {
	    if(angle > -Math.PI) if(angle <= Math.PI) return angle;
		return Math.IEEEremainder(angle, Constant.twoPi);
	}

	
    public final double fibonacci(double n) {
    	return (Math.pow(Constant.goldenRatio, n) - Math.pow(1.0 - Constant.goldenRatio, n)) / Constant.sqrt5;
    }
    
}

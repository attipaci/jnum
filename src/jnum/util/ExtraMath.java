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
package jnum.util;

import jnum.math.specialfunctions.ErrorFunction;

// TODO: Auto-generated Javadoc
/**
 * The Class ExtraMath.
 */
public final class ExtraMath {
	
	

	/**
	 * Square.
	 *
	 * @param x the x
	 * @return the double
	 */
	public final static double square(final double x) {
		return x*x;
	}
	
	/**
	 * Cube.
	 *
	 * @param x the x
	 * @return the double
	 */
	public final static double cube(final double x) {
		return x*x*x;
	}


	// Inverse hyperbolic functions from:
	// http://www.devx.com/vb2themax/Tip/19026
	/**
	 * Asinh.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double asinh(final double value) {
		return Math.log(value + Math.sqrt(value * value + 1.0));
	}
	
	/**
	 * Acosh.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double acosh(final double value) {
		return Math.log(value + Math.sqrt(value * value - 1.0));
	}
	
	/**
	 * Atanh.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double atanh(final double value) {
		return acoth(1.0 / value);
	}
	
	/**
	 * Acoth.
	 *
	 * @param value the value
	 * @return the double
	 */
	public final static double acoth(final double value) {	
		return 0.5 * Math.log((value + 1.0) / (value - 1.0));
	}
		
	/**
	 * Sinc.
	 *
	 * @param x the x
	 * @return the double
	 */
	public final static double sinc(final double x) { return x == 0.0 ? 1.0 : Math.sin(x) / x; }

	/**
	 * Log2.
	 *
	 * @param x the x
	 * @return the double
	 */
	public final static double log2(final double x) { return Math.log(x) * Constant.ilog2; }

	/**
	 * Tan.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double
	 */
	public final static double tan(final double x, final double y) {
		if(x == 0.0) {
			if(y == 0.0) return Double.NaN;
			return y > 0.0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}
		return y / x;
	}

	/**
	 * Cos.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double
	 */
	public final static double cos(final double x, final double y) {
		return x / ExtraMath.hypot(x,  y);
	}

	/**
	 * Sin.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the double
	 */
	public final static double sin(final double x, final double y) {
		return y / ExtraMath.hypot(x,  y);
	}

	
	/**
	 * Error function.
	 *
	 * @param x the x
	 * @return the double
	 */
	public final static double erf(final double x) {
		return ErrorFunction.at(x);
	}
	
	/**
	 * Inverse error function.
	 *
	 * @param y the y
	 * @return the double
	 */
	public final static double ierf(final double y) {
		return ErrorFunction.inverseAt(y);
	}
	
	/**
	 * Sum of squares.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public final static double sumSquares(final double a, final double b) {
		return a*a + b*b;
	}

	/**
	 * Sum of squares.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the double
	 */
	public final static double sumSquares(final double a, final double b, final double c) {
		return a*a + b*b + c*c;
	}

	public final static double hypot(final double a, final double b) {
		return Math.sqrt(a*a + b*b);
	}
	
	
	/**
	 * Add three arguments in quadrature, without overflow.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the double
	 */
	public final static double hypot(final double a, final double b, final double c) {
		return Math.sqrt(a*a + b*b + c*c);
	}

	/**
	 * The most significant bit (MSB) in the integer.
	 *
	 * @param value the value
	 * @return the most significant bit
	 */
	public final static int msb(final int value) {
		return log2floor(value);
	}
	
	/**
	 * The most significant bit (MSB) in the long integer.
	 *
	 * @param value the value
	 * @return the most significant bit
	 */
	public final static long msb(final long value) {
		return log2floor(value);
	}

	// Edit this only!
	/**
	 * Log2floor.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int log2floor(final int value) {		
		return (int) Math.floor(log2(value));
		/*
		int bits = 0;
		while((value >> bits) > 0) bits++;
		return bits-1;
		*/
	}
	
	/**
	 * Log2floor.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long log2floor(final long value) {	
		return (long) Math.floor(log2(value));
	}
	/**
	 * Log2ceil.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int log2ceil(final int value) {
		int p = log2floor(value);
		return 1<<p == value ? p : p+1;
	}
	
	/**
	 * Log2ceil.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long log2ceil(final long value) {
		long p = log2floor(value);
		return 1<<p == value ? p : p+1;
	}

	/**
	 * Log2round.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int log2round(final int value) {
		int pfloor = log2floor(value);
		int floor = 1<<pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor<<1) / value ? pfloor : pfloor + 1;
	}
	
	/**
	 * Log2round.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long log2round(final long value) {
		long pfloor = log2floor(value);
		long floor = 1<<pfloor;
		if(value == floor) return pfloor;
		
		return (double) value / floor < (double)(floor<<1) / value ? pfloor : pfloor + 1;
	}

	/**
	 * Pow2floor.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int pow2floor(final int value) { return 1 << log2floor(value); }
	
	/**
	 * Pow2floor.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long pow2floor(final long value) { return 1 << log2floor(value); }

	/**
	 * Pow2ceil.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int pow2ceil(final int value) { return 1 << log2ceil(value); }
	
	/**
	 * Pow2ceil.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long pow2ceil(final long value) { return 1 << log2ceil(value); }

	/**
	 * Pow2round.
	 *
	 * @param value the value
	 * @return the int
	 */
	public final static int pow2round(final int value) { return 1 << log2round(value); }
	
	/**
	 * Pow2round.
	 *
	 * @param value the value
	 * @return the long
	 */
	public final static long pow2round(final long value) { return 1 << log2round(value); }

	
	/**
	 * Roundup ratio.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the int
	 */
	public final static int roundupRatio(int a, int b) { return (a + b - 1) / b; }
	
	/**
	 * Roundup ratio.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the long
	 */
	public final static long roundupRatio(long a, long b) { return (a + b - 1L) / b; }
	
	/**
	 * Return the standardized angle for a given angle.
	 *
	 * @param angle the angle
	 * @return an angle between -Pi and Pi.
	 */
	public final static double standardAngle(final double angle) {
		return Math.IEEEremainder(angle, Constant.twoPi);
	}

	
	 /**
     * Fibonacci.
     *
     * @param n 
     * @return the nth Fibonacci number.
     */
    public final double fibonacci(double n) {
    	return (Math.pow(Constant.goldenRatio, n) - Math.pow(1.0 - Constant.goldenRatio, n)) / Constant.sqrt5;
    }
    
}

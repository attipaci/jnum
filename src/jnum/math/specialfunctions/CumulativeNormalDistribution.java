/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.math.specialfunctions;

import jnum.Constant;

// TODO: Auto-generated Javadoc
// Tested 1/12/09.

/**
 * The Class CumulativeNormalDistribution.
 */
public final class CumulativeNormalDistribution {

	
	
	/**
	 * At.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double at(double x) {
		if(x < -TAIL) return 0.5 * ErrorFunction.complementAt(-x * Constant.isqrt2);
		return 0.5 * (1.0 + ErrorFunction.at(x * Constant.isqrt2));
	}
	
	/**
	 * Complement at.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double complementAt(double x) {
		
		return 0.5 * (highPrecision ? ErrorFunction.complementAt(x * Constant.isqrt2) : ErrorFunction.fastComplementAt(x * Constant.isqrt2));		
	}
	
	/**
	 * Fast complement at.
	 *
	 * @param x the x
	 * @return the double
	 */
	public static double fastComplementAt(double x) {
		return 0.5 * ErrorFunction.fastComplementAt(x * Constant.isqrt2);		
	}
	
	
	
	// Based on Peter J Acklam's algorithm...
	// see http://home.online.no/~pjacklam/notes/invnorm/
	/**
	 * Inverse at.
	 *
	 * @param P the p
	 * @return the double
	 */
	public static double inverseAt(final double P) {
		if(P <= 0.0 || P >= 1.0) {
			if(P == 0.0) return Double.NEGATIVE_INFINITY;
			else if(P == 1.0) return Double.POSITIVE_INFINITY;
			else throw new IllegalArgumentException("Argument outside of cumulative probability range.");
		}
			
		if(P < CRITICAL_P) return -inverseComplementAt(P);
		else if(1.0 - P < CRITICAL_P) return inverseComplementAt(1.0 - P);		
		else {			
			final double q = P - 0.5;
		    final double r = q*q; 
		    final double value = (((((a[0]*r+a[1])*r+a[2])*r+a[3])*r+a[4])*r+a[5])*q / (((((b[0]*r+b[1])*r+b[2])*r+b[3])*r+b[4])*r+1.0);
		    return refine(value, P);
		}
	}
	
	//if 0 < p < p_low
    //q <- sqrt(-2*log(p))
    //x <- (((((c(1)*q+c(2))*q+c(3))*q+c(4))*q+c(5))*q+c(6)) /
    //      ((((d(1)*q+d(2))*q+d(3))*q+d(4))*q+1)
	//endif


	
	/**
	 * Inverse complement at.
	 *
	 * @param Q the q
	 * @return the double
	 */
	public static double inverseComplementAt(final double Q) {
		if(Q <= 0.0 || Q >= 1.0) {
			if(Q == 1.0) return Double.NEGATIVE_INFINITY;
			else if(Q == 0.0) return Double.POSITIVE_INFINITY;
			else throw new IllegalArgumentException("Argument outside of cumulative probability range.");
		}
		
		if(Q < CRITICAL_P) {
			if(Q < 0.0 || Q > 1.0) throw new IllegalArgumentException("Argument outside of cumulative probability range.");
			final double q = Math.sqrt(-2.0*Math.log(Q));
		    final double value = (((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) / ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1.0);	
		    return refine(value, 1.0 - Q);
		}
		return inverseAt(1.0 - Q);
	}
	
	/**
	 * Refine.
	 *
	 * @param value the value
	 * @param P the p
	 * @return the double
	 */
	private static double refine(final double value, final double P) {
		if( P > 0.0 && P < 1.0) {
			final double erfc = highPrecision ? ErrorFunction.complementAt(-value * Constant.isqrt2) : ErrorFunction.fastComplementAt(-value/Constant.isqrt2);
			final double e = 0.5 * erfc - P;
			final double u = e * Constant.sqrtTwoPi * Math.exp(0.5*value*value);
			return value - u/(1.0 + 0.5 * value*u);
		}
		return value;
	}

	
	/** The Constant a. */
	public final static double[] a = { -3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02, 
						1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00 };
	
	/** The Constant b. */
	public final static double[] b = { -5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02, 
						6.680131188771972e+01, -1.328068155288572e+01 };
	
	/** The Constant c. */
	public final static double[] c = { 7.784894002430293e-03, 3.223964580411365e-01, 2.400758277161838e+00,
						2.549732539343734e+00, -4.374664141464968e+00, -2.938163982698783e+00 };

	/** The Constant d. */
	public final static double[] d = { 7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00, 
						3.754408661907416e+00 };


	/** The tail. */
	private static double TAIL = 3.0;
	
	/** The high precision. */
	public static boolean highPrecision = true;
	
	/** The critical p. */
	private static double CRITICAL_P = 0.02425;

	
}

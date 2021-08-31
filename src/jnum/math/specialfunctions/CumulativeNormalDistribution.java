/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.math.specialfunctions;

import jnum.Constant;


// Tested 1/12/09.

/**
 * <p>
 * The cumulative normal distibution function P(<i>x</i>) = 1/&radic;&pi; &int; exp(-<i>x</i><sup>2</sup>/2) d<i>x</i>.
 * It is the integral of the Gaussian distribution, and is related to the error function erf(), as 
 * 2P(<i>x</i>) = 1 + erf(<i>x</i><sup>2</sup> / &radic;2). P(<i>x</i>) represents the probability that some normally 
 * distributed random variate takes a value &lt;=<i>x</i>. The complement function Q(<i>x</i>) = 1 - P(<i>x</i>) is conversely 
 * the likelihood that the random variate takes a value &gt;<i>x</i> It is
 * commonly used in statistics and probability for calculating probabilities of outliers.
 * </p>
 * <p>
 * This class provides statics methods for calculating both P(<i>x</i>) and Q(<i>x</i>) efficiently.
 * </p>
 * 
 * @see ErrorFunction
 * 
 * @author Attila Kovacs
 *
 */
public final class CumulativeNormalDistribution {

    /** private constructor because we do not want to instantiate this class */
    private CumulativeNormalDistribution() {}
	
    /**
     * Gets the value of P(<i>x</i>) for some <i>x</i>, representing the probability that a Gaussian variate
     * is &lt;=<i>x</i>.
     * 
     * @param x     the argument
     * @return      P(<i>x</i>)
     * 
     * @see #complementAt(double)
     */
	public static double at(double x) {
		if(x < -TAIL) return 0.5 * ErrorFunction.complementAt(-x * Constant.isqrt2);
		return 0.5 * (1.0 + ErrorFunction.at(x * Constant.isqrt2));
	}

	 /**
     * Gets the value of Q(<i>x</i>) for some <i>x</i>, representing the probability that a Gaussian variate
     * is &gt;<i>x</i>.
     * 
     * @param x     the argument
     * @return      Q(<i>x</i>)
     * 
     * @see #at(double)
     * @see #fastComplementAt(double)
     */
	public static double complementAt(double x) {
		
		return 0.5 * (highPrecision ? ErrorFunction.complementAt(x * Constant.isqrt2) : ErrorFunction.fastComplementAt(x * Constant.isqrt2));		
	}
	

	 /**
     * Gets the value of Q(<i>x</i>) for some <i>x</i>, representing the probability that a Gaussian variate
     * is &gt;<i>x</i>, using a faster algorithms. The returned value is quatanteed to be accurate to &lt;1e-7
     * for all input values of <i>x</i>.
     * 
     * @param x     the argument
     * @return      Q(<i>x</i>)
     * 
     * @see #at(double)
     * @see #complementAt(double)
     */
	public static double fastComplementAt(double x) {
		return 0.5 * ErrorFunction.fastComplementAt(x * Constant.isqrt2);		
	}
	
	
	
	/**
	 * Inverse cumulative normal distribution. The implementation is based on 
	 * <a href="http://home.online.no/~pjacklam/notes/invnorm/">Peter J. Acklam's algorithm</a>.
	 * 
	 * @param P        the argument
	 * @return         <i>x</i> for which P(<i>x</i>) yields the argument.
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
	 * Inverse complement cumulative normal distribution. The implementation is based on 
     * <a href="http://home.online.no/~pjacklam/notes/invnorm/">Peter J. Acklam's algorithm</a>.
	 * 
	 * @param Q    the value of Q
	 * @return     <i>x</i> for which Q(<i>x</i>) yields the argument.
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
	 * Refines the inverse value. 
	 * 
	 * @param value        Approximate <i>x</i>.
	 * @param P            the value of P
	 * @return             the refined <i>x</i> for which P(<i>x</i>) yields P.
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

	/**
	 * Acklam's inverse coefficients.
	 * 
	 */
	public static final double[] a = { -3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02, 
						1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00 };
	
	/**
     * Acklam's inverse coefficients.
     * 
     */
	public static final double[] b = { -5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02, 
						6.680131188771972e+01, -1.328068155288572e+01 };

	/**
     * Acklam's inverse coefficients.
     * 
     */
	public static final double[] c = { 7.784894002430293e-03, 3.223964580411365e-01, 2.400758277161838e+00,
						2.549732539343734e+00, -4.374664141464968e+00, -2.938163982698783e+00 };

	/**
     * Acklam's inverse coefficients.
     * 
     */
	public static final double[] d = { 7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00, 
						3.754408661907416e+00 };


	private static double TAIL = 3.0;
	

	public static boolean highPrecision = true;
	

	private static double CRITICAL_P = 0.02425;

	
}

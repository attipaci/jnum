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
import jnum.math.Complex;
import jnum.math.ConvergenceException;

/**
 * The &Gamma; function, its logarithm, and regularized incomplete P and Q functions.
 * 
 * @author Attila Kovacs
 *
 */
public final class GammaFunction {

	/**
	 * Calculate the value of the &Gamma; function at the specified real argument.
	 *
	 * @param x the real-valued argument.
	 * @return &Gamma;(x).
	 */
	public static final double at(double x) { return Math.exp(logAt(x)); }

	/**
	 * Calculate the logarithm of the &Gamma; function at a real value. Adapted from Numerical Recipes in C.
	 *
	 * @param      x the real-valued argument.
	 * @return     log(&Gamma;(<i>x</i>))
	 */
	public static final double logAt(final double x) {
		double y = x + 5.5;
		y -= (x+0.5) * Math.log(y);
		final double z = 1.000000000190015 + 76.18009172947146 / (x+1.0) - 86.50532032941677 / (x+2.0) + 24.01409824083091 / (x+3.0)
				-1.231739572450155 / (x+4.0) + 0.1208650973866179e-2 / (x+5.0) - 0.5395239384953e-5 / (x+6.0);
		return Math.log(2.5066282746310005 * z / x) - y;
	}

	/**
	 * Calculate the complex value of the &Gamma; function at the specified complex argument.
	 *
	 * @param      z the complex argument
	 * @return     &Gamma;(<i>z</i>)
	 */
	public static final Complex at(final Complex z) {
		final Complex result = new Complex();
		evaluateAt(z, result);
		return result;
	}

	/**
	 * Calculate the complex logarithm of the &Gamma; function at the specified complex argument. 
	 * Adapted from Numerical Recipes in C.
	 *
	 * @param      z the complex argument
	 * @return     &Gamma;(<i>z</i>)
	 */
	public static final Complex logAt(final Complex z) {
		final Complex result = new Complex();
		evaluateLogAt(z, result);
		return result;		
	}

	/**
	 * Evaluate the complex &Gamma; function at the specified complex argument.
	 * 
	 * The result will be placed in the supplied second argument, thus avoiding the explicit creation of a new
	 * complex number for the return value. This makes it the preferred (faster) approach for evaluating the function over
	 * and over again, with just one (or few) user-created complex objects.
	 *
	 * @param z        the complex argument
	 * @param result   the complex number that is set to the value of &Gamma;(<i>z</i>)
	 */
	public static final void evaluateAt(final Complex z, final Complex result) {
		evaluateLogAt(z, result);
		// For real numbers use faster real exponential
		if(z.im() == 0.0) z.setRealPart(Math.exp(z.re()));
		else result.exp();		
	}

	/**
	 * Evaluate the logarithm of the complex &Gamma; function at the specified complex argument. It uses the Lanczos
	 * approximation with <i>g</i>=7.
	 * 
	 * The result will be placed in the supplied second argument, thus avoiding the explicit creation of a new
	 * complex number for the return value. This makes it the preferred (faster) approach for evaluating the function over
	 * and over again, with just one (or few) user-created complex objects.
	 *
	 * @param z        the complex argument
	 * @param result   the complex number that is set to the value of log(&Gamma;(<i>z</i>))
	 */
	public static final void evaluateLogAt(final Complex z, final Complex result) {	
		if(z == result) throw new IllegalArgumentException("Identical arguments.");

		final double zr = z.re();
		final double zi = z.im();
		
		// For real numbers use the faster real evaluation.
		if(zi == 0.0) {
			result.set(logAt(zr), 0.0);
			return;
		}

		if(zr < 0.5) {
			z.set(1.0 - zr, -zi);
			evaluateLogAt(z, result);

			z.set(zr, zi);
			z.scale(Math.PI);
			z.sin();
			z.log();

			result.subtract(z);
			result.add(Constant.logPi);

			z.set(zr, zi);
			return;
		}

		final int g = 7;
		final int n = g+2;

		double sumr = p[0];
		double sumi = 0.0;
		for(int i=n; --i > 0; ) {
			result.set(zr + i - 1.0, zi);
			result.inverse();
			result.scale(p[i]);
			sumr += result.re();
			sumi += result.im();
		}

		result.set(zr + g - 0.5, zi);
		result.log();	
		z.set(zr - 0.5, zi);
		result.multiplyBy(z);

		result.subtract(zr + g - 0.5);
		result.subtractImaginary(zi);

		z.set(sumr, sumi);
		z.log();
		result.add(z);

		result.add(0.5 * Constant.logTwoPi);

		// Return z back to its original value...
		z.set(zr, zi);	
	}



	/**
	 * Calculate the regularized Gamma P(<i>s</i>, <i>x</i>) function for a real-valued argument.
	 *  
	 * @param s    the real-valued argument
	 * @param x    the upper limit of the incomplete integral.
	 * @return     P(<i>s</i>, <i>x</i>)
	 */
	public static double P(double s, double x) {
		if (x < 0.0 || s <= 0.0) throw new IllegalArgumentException("Invalid arguments for GammaP(a,x).");
		return x < (s+1.0) ? P(s,x,logAt(s)) : 1.0 - Q(s,x,logAt(s));
	}

    /**
     * Calculate the regularized Gamma P(<i>s</i>, <i>x</i>) function for a real-valued argument, given an
     * already calculated value for the log(&Gamma;(<i>a</i>))
     *  
     * @param s         the real-valued argument
     * @param x         the upper limit of the incomplete lower integral.
     * @param lnGs      the readily available value for log(&Gamma;(<i>s</i>)).
     * @return          P(<i>s</i>, <i>x</i>)
     */
	public static double P(final double s, final double x, final double lnGs) {
		if(x == 0.0) return 0.0;
		if(x < 0.0) throw new IllegalStateException("Gamma P evaluated at x < 0.");

		double ap=s;
		double del=1.0/s;
		double sum = del;

		for (int i=MAX_ITERATIONS; --i >= 0; ) {
			++ap;
			del *= x/ap;
			sum += del;
			if (Math.abs(del) < Math.abs(sum)*EPS) return sum*Math.exp(-x+s*Math.log(x)-lnGs);
		}
		throw new ConvergenceException("Gamma P convergence not achieved: 'a' is too large, or need to iterate longer.");

	}


	/**
     * Calculate the regularized Gamma Q(<i>s</i>, <i>x</i>) function for a real-valued argument.
     *  
     * @param s    the real-valued argument
     * @param x    the lower limit of the incomplete upper integral.
     * @return     Q(<i>s</i>, <i>x</i>)
     */
	public static double Q(final double s, final double x) {
		if (x < 0.0 || s <= 0.0) throw new IllegalStateException("Invalid arguments for GammaQ(a,x).");
		return x < (s+1.0) ? 1.0 - P(s, x, logAt(s)) : Q(s, x, logAt(s));
	}

	/**
     * Calculate the regularized Gamma Q(<i>s</i>, <i>x</i>) function for a real-valued argument, given an
     * already calculated value for the log(&Gamma;(<i>a</i>))
     *  
     * @param s     the real-valued argument
     * @param x     the lower limit of the incomplete upper integral.
     * @param lnGs  the readily available value for log(&Gamma;(<i>s</i>)).
     * @return      Q(<i>s</i>, <i>x</i>)
     */
	public static double Q(final double s, final double x, final double lnGs) {
		double b = x + 1.0 - s;
		double c = 1.0 / FPMIN;
		double d = 1.0/b;
		double h = d;

		for(int i=1; i <= MAX_ITERATIONS; i++) {
			double an = -i * (i - s);
			b += 2.0;
			c = b + an / c;
			d = an * d + b;
			if(Math.abs(c) < FPMIN) c = FPMIN;
			if(Math.abs(d) < FPMIN) d = FPMIN;
			d = 1.0 / d;
			double del = d * c;
			h *= del;
			if(Math.abs(del-1.0) < EPS) return Math.exp(- x + s * Math.log(x) - lnGs) * h;
		}
		throw new ConvergenceException("Gamma Q convergence not achieved: 'a' is too large, or need to iterate longer.");
	}

	/**
	 * The maximum number of iterations to reach convergence.
	 * 
	 */
	public static int MAX_ITERATIONS = 300;

	public static double EPS = 3.0e-7;

	public static double FPMIN = 1.0e-300;

	/** Coefficients for Lanczos approximation, same as used by the GNU Scientific Library. */
	private static double[] p = {
		0.99999999999980993, 676.5203681218851, -1259.1392167224028,
		771.32342877765313, -176.61502916214059, 12.507343278686905,
		-0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
	};


}

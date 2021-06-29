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
import jnum.math.Complex;


/**
 * The Class ZetaFunction. Based on Gourdon and Sebah (2003).
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 */
public final class ZetaFunction {
	
	// Gourdon & Sebah (2003)
	// precalculated binomial sums for maximal precision...
	public static double at(final double x) {	
		if(x < 0.0) return Math.pow(Constant.twoPi, x) * Constant.iPi * Math.sin(Constant.rightAngle*x) * GammaFunction.at(1.0 - x) * at(1.0 - x);
		else if(x == 0.0) return -0.5;
		else if(x == 1.0) return Double.POSITIVE_INFINITY;
		
		final int n = 18;
		
		double sum1 = 0.0, sum2 = 0.0;
		for(int k=n; --k > 0; ) {
			sum1 += (k & 1) == 0 ? -Math.pow(k, -x) : Math.pow(k,  -x);
			sum2 += e[k] * Math.pow(k+n, -x);			
		}
		return (sum1 + sum2/(1<<n)) / (1.0 - Math.pow(2.0, 1.0-x));
	}
	

	public final static Complex at(final Complex z) {
		final Complex result = new Complex();
		evaluateAt(z, result);
		return result;
	}
	
	// Complex evaluation without creation of internal objects (complex numbers) for maximum speed.
	public static void evaluateAt(final Complex z, final Complex result) {
		if(z == result) throw new IllegalArgumentException("Identical arguments.");
		
		final double zr = z.re();
		final double zi = z.im();
		
		// If z is real, then use the faster real evaluation above...
		if(zi == 0.0) {
			result.set(at(zr), 0.0);
			return;
		}
				
		// For negative numbers use functional relation.
		if(zr < 0.0) {		
			// 2Pi^(z) / Pi = 2^z * Pi^(z-1)
			result.set(Constant.twoPi, 0.0);
			result.pow(z);
			result.scale(Constant.iPi);
	
			// sin(pi * z / 2)
			z.scale(Constant.rightAngle);
			z.sin();
			result.multiplyBy(z);

			double r = result.re();
			double i = result.im();
			
			// Gamma(1-z)
			z.set(1.0 - zr, -zi);
			GammaFunction.evaluateAt(z, result);
			z.set(r,  i);
			result.multiplyBy(z);
			
			r = result.re();
			i = result.im();
			
			// Zeta(1-z)
			z.set(1.0 - zr, -zi);
			evaluateAt(z, result);
			z.set(r,  i);
			result.multiplyBy(z);
			
			// return z to its original value
			z.set(zr, zi);
			return;
		}
		
		
		// Gourdon & Sebah (2003)
		final int n = 18;
		
		/*
		// Check to make sure the alternating series approximation is convergent...
		if(zr < -(n-1)) 
			throw new IllegalArgumentException("Zeta approximation is not convergent for Re(z) < " + (-(n-1)));
		*/
		
		// Temporarily replace z -> -z
		z.flip();

		double sum1r = 0.0, sum1i = 0.0, sum2r = 0.0, sum2i = 0.0;
		for(int k=n; --k > 0; ) {
			result.set(k, 0.0);
			result.pow(z);
			if((k & 1) == 0) result.flip();
			
			sum1r += result.re();
			sum1i += result.im();
			
			result.set(k+n, 0.0);
			result.pow(z);
			result.scale(e[k]);
			
			sum2r += result.re();
			sum2i += result.im();			
		}

		final double x = 1.0 / (1<<n);
		sum2r *= x;
		sum2i *= x;
		
		z.set(2.0, 0.0);
		result.set(1.0 - zr, -zi);
		z.pow(result);
		z.subtractX(1.0);
		z.flip();
		
		result.set(sum1r + sum2r, sum1i + sum2i);
		result.divideBy(z);
		
		// Return z back to its original value...
		z.set(zr,  zi);
	}
	
	
	/** The e. */
	private static int[] e = { 0, 262143, -262125, 261972, -261156, 258096, -249528, 230964, -199140, 155382,
		-106762, 63004, -31180, 12616, -4048, 988, -172, 19, -1 };

	
	
}
 

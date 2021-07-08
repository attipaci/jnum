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


// Error function is defined s.t.
//  erf(x) = 2/sqrt(pi) int{0, x} exp(-t^2) dt
//
//  erf(0) = 0
//  erf(inf) = 1
//  erf(-inf) = -1;

/**
 * The Gaussian error function, commonly denoted as erf(). The error function is the integral of the special sigmoid
 * function, that is erf(x) = 2/&radic;&pi; &int; exp(-t<sup>2</sup>) dt. It is commonly used in statistics for
 * obtaining probabilities of events. You may find the related {@link CumulativeNormalDistribution} even 
 * simpler to use in some cases.
 * 
 * @author Attila Kovacs
 *
 */
public final class ErrorFunction  {
	
    /**
     * Private constructor because we do not want to instantiate this class.
     * 
     */
    private ErrorFunction() {}
    
    /**
     * Evaluates the error function and the argument x.
     * 
     * @param x     the argument.
     * @return      erf(<i>x</i>).
     */
	public static double at(double x) {
		if(Double.isInfinite(x)) return x > 0 ? 1.0 : -1.0;
		return (x < 0.0 ? -1.0 : 1.0) * GammaFunction.P(0.5, x*x);
	}
	
	/**
	 * Gets the complement value to the error function, that is 1 - erf(<i>x</i>).
	 * 
	 * @param x    the argument.
	 * @return     1 - erf(<i>x</i>).
	 * 
	 * @see #fastComplementAt(double)
	 */
	public static double complementAt(double x) {
		if(Double.isInfinite(x)) return x > 0 ? -1.0 : 1.0;
		return x < 0.0 ? 1.0 + GammaFunction.P(0.5,x*x) : GammaFunction.Q(0.5,x*x);
	}

	/**
	 * Gets the complement value using a much faster calculation than {@link #complementAt(double)}.
	 * The returned value is guaranteed to be accurate to at least 1.2e-7 for any input argument.
	 * 
	 * @param x    the argument.
	 * @return     1 - erf(<i>x</i>), with a precision no less than 1.2e-7.
	 * 
	 * @see #complementAt(double)
	 */
	public static double fastComplementAt(double x) {
		if(Double.isInfinite(x)) return x > 0 ? -1.0 : 1.0;
		
		final double t=1.0/(1.0+0.5*Math.abs(x));
		final double value = t*Math.exp(-x*x-1.26551223+t*(1.00002368+t*(0.37409196+t*(0.09678418+
				t*(-0.18628806+t*(0.27886807+t*(-1.13520398+t*(1.48851587+t*(-0.82215223+t*0.17087277)))))))));
		return x >= 0.0 ? value : 2.0-value;		
	}
	
	/**
	 * Gets the inverse of the error function.
	 * 
	 * @param y    the error function's value
	 * @return     <i>x</i> for which the error function evaluates to <i>y</i>. I.e. <i>x</i> for which <i>y</i> = erf(<i>x</i>).
	 */
	public static double inverseAt(double y) {
		return CumulativeNormalDistribution.inverseAt(0.5 * (1.0 + y)) * Constant.isqrt2;
	}
	
}

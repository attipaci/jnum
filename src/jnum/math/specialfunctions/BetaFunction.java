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

import jnum.math.Complex;


public final class BetaFunction {


	public final static double at(double x, double y) {
		if(x <= 0 || y <= 0) throw new IllegalArgumentException("Beta function is undefined for negative arguments.");
		
		return Math.exp(logAt(x, y));		
	}
	

	public final static double logAt(double x, double y) {
		return GammaFunction.logAt(x) + GammaFunction.logAt(y) - GammaFunction.logAt(x+y);
	}
	

	public final static Complex at(final Complex x, final Complex y) {
		Complex result = new Complex();
		evaluateAt(x, y, result);
		return result;
	}
	

	public final static Complex logAt(final Complex x, final Complex y) {
		Complex result = new Complex();
		evaluateLogAt(x, y, result);
		return result;
	}
	

	public final static void evaluateAt(final Complex x, final Complex y, final Complex result) {
		evaluateLogAt(x, y, result);
		// If real, use faster real exponential.
		if(result.im() == 0.0) result.setRealPart(Math.exp(result.re()));
		else result.exp();
	}
		

	public final static void evaluateLogAt(final Complex x, final Complex y, final Complex result) {
		if(x == result) throw new IllegalArgumentException("Identical arguments: x & result.");
		if(y == result) throw new IllegalArgumentException("Identical arguments: y & result.");
		
		final double xr = x.re();
		final double xi = x.im();
		final double yr = y.re();
		final double yi = y.im();
		
		if(xr < 0.0 || yr < 0.0) throw new IllegalArgumentException("Beta function not defined for Re(x),Re(y) < 0");
		
		// For real numbers use the faster real evaluation.
		if(xi == 0.0 && yi == 0.0) {
			result.set(logAt(xr, yr), 0.0);
			return;
		}
		
		// Gamma(x)
		GammaFunction.evaluateLogAt(x, result);
		double r = result.re();
		double i = result.im();
		
		// Gamma(y)
		GammaFunction.evaluateLogAt(y, result);
		r += result.re();
		i += result.im();
		
		// Gamma(x+y)
		x.add(y);
		GammaFunction.evaluateLogAt(y, result);
		
		result.set(r - result.re(), i - result.im());
		
		// Set x and y back to their original values.
		x.set(xr,  xi);
		y.set(yr,  yi);		
	}
	
}

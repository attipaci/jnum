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
package jnum.math.specialfunctions;

import jnum.math.Function;

// TODO: Auto-generated Javadoc
/**
 * The Class LegendrePolynomial.
 */
public class LegendrePolynomial implements Function<Double, Double> {
	
	/** The m. */
	int l, m;
	
	/**
	 * Instantiates a new legendre polynomial.
	 *
	 * @param l the l
	 * @param m the m
	 */
	public LegendrePolynomial(int l, int m) {
		if(m < 0 || m > l) throw new IllegalArgumentException("Illegal orders for " + getClass().getSimpleName() + ".");
		this.l = l;
		this.m = m;		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.math.Function#valueAt(java.lang.Object)
	 */
	@Override
	public Double valueAt(final Double x) {
		return at(l, m, x);		
	}
	
	/**
	 * At.
	 *
	 * @param l the l
	 * @param m the m
	 * @param x the x
	 * @return the double
	 */
	public static double at(final int l, final int m, final double x) {
		if(m < 0 || m > l || Math.abs(x) > 1.0)
			throw new IllegalArgumentException("Illegal arguments to LegendrePolynomial.");

		double pmm=1.0;
		if(m > 0) {
			double somx2 = Math.sqrt((1.0-x)*(1.0+x));
			double fact = 1.0;
			for(int i=1; i <= m; i++) {
				pmm *= -fact*somx2;
				fact += 2.0;
			}
		}
		if(l == m) return pmm;
		else {
			double pmmp1 = x * (2*m+1) * pmm;
			if(l == (m+1)) return pmmp1;
			else {
				double pll = Double.NaN;
				for(int ll = m+2; ll <= l; ll++) {
					pll = (x * (2*ll-1) * pmmp1 - (ll+m-1) * pmm) / (ll-m);
					pmm=pmmp1;
					pmmp1=pll;
				}
				return pll;
			}
		}
	}

	
}

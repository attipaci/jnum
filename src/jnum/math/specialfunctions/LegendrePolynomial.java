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

import jnum.Function;


public class LegendrePolynomial implements Function<Double, Double> {
	

	int l, m;
	

	public LegendrePolynomial(int l, int m) {
		if(m < 0 || m > l) throw new IllegalArgumentException("Illegal orders for " + getClass().getSimpleName() + ".");
		this.l = l;
		this.m = m;		
	}
	
	/* (non-Javadoc)
	 * @see jnum.math.Function#valueAt(java.lang.Object)
	 */
	@Override
	public Double valueAt(final Double x) {
		return at(l, m, x);		
	}
	

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

		double pmmp1 = x * (2*m+1) * pmm;
		if(l == (m+1)) return pmmp1;

		double pll = Double.NaN;
		for(int ll = m+2; ll <= l; ll++) {
		    pll = (x * (2*ll-1) * pmmp1 - (ll+m-1) * pmm) / (ll-m);
		    pmm=pmmp1;
		    pmmp1=pll;
		}
		return pll;
	}
	
}

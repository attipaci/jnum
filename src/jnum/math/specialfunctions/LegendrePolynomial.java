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

import jnum.Function;

/**
 * Legendre polynomials P<sub>l</sub><sup>m</sup>(<i>x</i>). These polynomials constitute a part of {@link SphericalHarmonics}.
 * 
 * @author Attila Kovacs
 * 
 * @see SphericalHarmonics
 *
 */
public class LegendrePolynomial implements Function<Double, Double> {
	
    /** the index l for this instance. */
	private int l;
	/** the index m for this instance */
	private int m;
	
	/**
	 * Instantiates a new Legendre polynomial P<sub>l</sub><sup>m</sup>.
	 * 
	 * @param l    the polynomial index <i>l</i> for this instance.
	 * @param m    the polynomial index <i>m</i> for this instance.
	 * 
	 * @throws IllegalArgumentException if <i>l</i> or <i>m</i> are invalid.
	 */
	public LegendrePolynomial(int l, int m) throws IllegalArgumentException {
		if(m < 0) throw new IllegalArgumentException("m cannot be negative");
		if(m > l) throw new IllegalArgumentException("m cannot be greater than l");
		this.l = l;
		this.m = m;		
	}
	
	/**
     * Gets the index l for this P<sub>l</sub><sup>m</sup> instance.
     * 
     * @return     the index l for this P<sub>l</sub><sup>m</sup> instance.
     */
    public final int getL() { return l; }
    
    /**
     * Gets the index m for this P<sub>l</sub><sup>m</sup> instance.
     * 
     * @return     the index m for this P<sub>l</sub><sup>m</sup> instance.
     */
    public final int getM() { return m; }

	@Override
	public final Double valueAt(final Double x) {
		return at(l, m, x);		
	}
	
	/**
	 * Calculates P<sub>l</sub><sup>m</sup>(<i>x</i>).
	 * 
	 * @param l    the <i>l</i> index of of P<sub>l</sub><sup>m</sup>.
	 * @param m    the <i>m</i> index of of P<sub>l</sub><sup>m</sup>.
	 * @param x    the argument
	 * @return     P<sub>l</sub><sup>m</sup>(<i>xi</i>)
	 */
	public final static double at(final int l, final int m, final double x) {
		if(Math.abs(x) > 1.0) throw new IllegalArgumentException("|x| cannot exceed 1");

		double pmm=1.0;
		if(m > 0) {
			final double somx2 = Math.sqrt((1.0-x) * (1.0+x));
			double fact = -1.0;
			for(int i=1; i <= m; i++) {
				pmm *= fact * somx2;
				fact -= 2.0;
			}
		}
		if(l == m) return pmm;

		double pmmp1 = x * (2 * m + 1) * pmm;
		if(l == m + 1) return pmmp1;

		double pL = Double.NaN;
		for(int L = m+2; L <= l; L++) {
		    pL = (x * (2*L - 1) * pmmp1 - (L+m-1) * pmm) / (L-m);
		    pmm = pmmp1;
		    pmmp1 = pL;
		}
		return pL;
	}
	
}

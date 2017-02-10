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
import jnum.math.NumericalFunction;

// TODO: Auto-generated Javadoc
/**
 * The Class ExponentialIntegral.
 */
public class ExponentialIntegral implements NumericalFunction<Double, Double> {
	
	/** The order. */
	private int order;
	
	/** The precision. */
	private double precision = 1.0e-7;
	
	/**
	 * Instantiates a new exponential integral.
	 *
	 * @param order the order
	 */
	public ExponentialIntegral(int order) {
		setOrder(order);
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(int order) { this.order = order; }
	
	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() { return order; }
	
	/* (non-Javadoc)
	 * @see jnum.math.Function#valueAt(java.lang.Object)
	 */
	@Override
	public Double valueAt(Double x) {

		double ans;
		
		final int nm1 = order-1;
		if (order < 0 || x < 0.0 || (x == 0.0 && (order == 0 || order == 1)))
			throw new IllegalStateException("bad arguments in expint");
		else {
			if (order == 0) ans = Math.exp(-x)/x;
			else {
				double del;			
				if (x == 0.0) ans = 1.0 / nm1;
				else {
					if (x > 1.0) {
						double b = x + order;
						double c = 1.0 / FPMIN;
						double d = 1.0 / b;
						double h = d;
						for (int i = 1; i <= maxIteration; i++) {
							final double a = -i * (nm1 + i);
							b += 2.0;
							d = 1.0 / (a * d + b);
							c = b + a / c;
							del = c * d;
							h *= del;
							if (Math.abs(del-1.0) < precision) {
								ans = h * Math.exp(-x);
								return ans;
							}
						}
						throw new IllegalStateException("Continued fraction failed in " + getClass().getSimpleName() + ".");
					} 
					else {
						ans = (nm1 != 0 ? 1.0/nm1 : -Math.log(x) - Constant.euler);
						double fact = 1.0;
						for (int i = 1; i <= maxIteration; i++) {
							fact *= -x / i;
							if (i != nm1) del = -fact / (i - nm1);
							else {
								double psi = -Constant.euler;
								for (int ii = 1; ii <= nm1; ii++) psi += 1.0 / ii;
								del = fact * (-Math.log(x) + psi);
							}
							ans += del;
							if (Math.abs(del) < Math.abs(ans) * precision) return ans;
						}
						throw new IllegalStateException("Convergence not achieved in " + getClass().getSimpleName() + ".");
					}
				}
			}
		}
		return ans;
	}
	
	/* (non-Javadoc)
	 * @see jnum.math.NumericalFunction#getMaxPrecisionAt(java.lang.Object)
	 */
	@Override
	public int getMaxPrecisionAt(Double x) {
		return -(int)Math.ceil(Math.log10(precision));
	}

	/* (non-Javadoc)
	 * @see jnum.math.NumericalFunction#setPrecision(int)
	 */
	@Override
	public void setPrecision(int digits) {
		precision = Math.pow(0.1, digits);
	}
	
	/** The max iteration. */
	public static int maxIteration = 300;
	
	/** The fpmin. */
	public static double FPMIN = 1.0e-300;
	

}

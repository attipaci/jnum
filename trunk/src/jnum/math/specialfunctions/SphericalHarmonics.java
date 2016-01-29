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

import jnum.math.Complex;
import jnum.util.Constant;

// TODO: Auto-generated Javadoc
/**
 * The Class SphericalHarmonics.
 */
public class SphericalHarmonics {
	
	/** The m. */
	private int l, m;
	
	/** The k. */
	private double K;
	
	/**
	 * Instantiates a new spherical harmonics.
	 *
	 * @param l the l
	 * @param m the m
	 */
	public SphericalHarmonics(int l, int m) {
		setOrder(l, m);
	}
	
	/**
	 * Sets the order.
	 *
	 * @param l the l
	 * @param m the m
	 */
	public void setOrder(int l, int m) {
		this.l = l;
		this.m = m;
		K = Math.sqrt((2*l+1)/Constant.fourPi * Factorial.at(l-m)/Factorial.at(l+m));
	}
	
	/**
	 * Value at.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @return the complex
	 */
	public Complex valueAt(double theta, double phi) {
		Complex result = new Complex();
		evaluateAt(theta, phi, result);
		return result;
	}
	
	/**
	 * Evaluate at.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @param result the result
	 */
	public void evaluateAt(double theta, double phi, Complex result) {
		if(m < 0) {
			m *= -1;
			evaluateAt(theta, phi, result);
			m *= -1;
			if((m & 1) != 0) result.scale(-1.0);
			result.conjugate();
			return;
		}
		
		double r = K * LegendrePolynomial.at(l, m, Math.cos(theta));
		phi *= m;
		result.setPolar(r, phi);
	}
	
	/**
	 * At.
	 *
	 * @param l the l
	 * @param m the m
	 * @param theta the theta
	 * @param phi the phi
	 * @return the complex
	 */
	public Complex at(int l, int m, double theta, double phi) {
		Complex result = new Complex();
		evaluateAt(l, m, theta, phi, result);
		return result;		
	}
	
	/**
	 * Evaluate at.
	 *
	 * @param l the l
	 * @param m the m
	 * @param theta the theta
	 * @param phi the phi
	 * @param result the result
	 */
	public void evaluateAt(int l, int m, double theta, double phi, Complex result) {
		if(m < 0) {
			evaluateAt(l, -m, theta, phi, result);
			if((m & 1) != 0) result.scale(-1.0);
			result.conjugate();
			return;
		}
		
		final double K = Math.sqrt((2*l+1) / Constant.fourPi * Factorial.at(l-m)/Factorial.at(l+m));
		final double r = K * LegendrePolynomial.at(l, m, Math.cos(theta));
		phi *= m;
		result.setPolar(r, phi);
	}
	
}

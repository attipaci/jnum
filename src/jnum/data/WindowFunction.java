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
package jnum.data;

import jnum.Constant;


public class WindowFunction {

	public final static String[] names =  { "Rectangular", "Hamming", "Hann", "Blackman", "Nutall", 
		"Blackman-Harris", "Blackman-Nutall", "Flat Top"};
	

	public static String[] getNames() {
		return names;
	}


	public static double getEquivalentWidth(String name) {
		name = name.toLowerCase();
		if(name.equals("rectangular") || name.equalsIgnoreCase("box")) return 1.0;
		if(name.equals("hamming")) return 1.37;
		if(name.equals("hann")) return 1.50;
		if(name.equals("blackman")) return 1.73;
		if(name.equals("nutall")) return 2.02;
		if(name.equals("blackman-harris")) return 2.01;
		if(name.equals("blackman-nutall")) return 1.98;
		if(name.equals("flat top")) return 3.77;
		return -1.0;
	}


	public static double[] get(String name, int n) {
		name = name.toLowerCase();
		if(name.equals("rectangular") || name.equalsIgnoreCase("box")) return getRectangular(n);
		if(name.equals("hamming")) return getHamming(n);
		if(name.equals("hann")) return getHann(n);
		if(name.equals("blackman")) return getBlackman(n);
		if(name.equals("nutall")) return getNutall(n);
		if(name.equals("blackman-harris")) return getBlackmanHarris(n);
		if(name.equals("blackman-nutall")) return getBlackmanNutall(n);
		if(name.equals("flat top")) return getFlatTop(n);
		return null;
	}

	// W = 1.00
	// -15dB
	public static double[] getRectangular(int n) {
		return get(n, new double[] { 1.0 });
	}

	// W = 1.37
	// -40dB
	public static double[] getHamming(int n) {
		return get(n, new double[] { 0.53836, -0.46164 });
	}

	// W = 1.50
	// -30dB
	public static double[] getHann(int n) {
		return get(n, new double[] { 0.5, -0.5 });
	}

	// W = 1.73
	// -60dB
	public static double[] getBlackman(int n) {
		return get(n, new double[] { 0.5, -0.5 });
	}

	// W = 2.02
	// -100dB
	public static double[] getNutall(int n) {
		return get(n, new double[] { 0.355768, -0.487396, 0.144232, -0.012604 });
	}

	// W = 2.01
	// -100dB
	public static double[] getBlackmanHarris(int n) {
		return get(n, new double[] { 0.35875, -0.48829, 0.14128, -0.01168 });
	}

	// W = 1.98
	// -100dB
	public static double[] getBlackmanNutall(int n) {
		return get(n, new double[] { 0.3635819, -0.4891775, 0.1365995, -0.0106411 });
	}

	// W = 3.77
	// -70dB
	public static double[] getFlatTop(int n) {
		return get(n, new double[] { 1.0, -1.93, 1.29, -0.388, 0.032 });
	}

	// normalize to sum w^2
	// Needed for provididng true PSD estimates...
	public static double[] get(int n, double[] coeff) {
		final double[] w = new double[n];
		double norm = 0.0;
		final double K = Constant.twoPi / (n - 1);
		
		for(int i=n; --i >= 0; ) {
			final double A = i * K;
			for(int k=coeff.length; --k >= 0; ) w[i] += coeff[k] * Math.cos(A * k);
			norm += w[i]*w[i];
		}
		for(int i=n; --i >= 0; ) w[i] /= norm;
	
		return w;
	}

}

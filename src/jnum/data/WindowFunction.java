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

/**
 * Provides standard and custom window functions via static methods, e.g. for conditioning data prior to Fourier Transforms.
 * 
 * @author Attila Kovacs
 *
 * @see jnum.fft.FFT
 */
public final class WindowFunction {
    
    /** private constructor because we don't want to instantiate this class */
    private WindowFunction() {}
    
	private static final String[] names =  { "Boxcar", "Hamming", "Hann", "Blackman", "Nutall", 
		"Blackman-Harris", "Blackman-Nutall", "Flat Top"};
	
	/**
	 * Returns the names of supported window functions.
	 * 
	 * @return     the names of the standard window functions supported by this class.
	 * 
	 * @see #get(String, int)
	 * @see #getEquivalentWidth(String)
	 */
	public static String[] getNames() {
		return names;
	}

	/**
	 * Returns the equivalent width of the window function in Fourier space, relative to that of
	 * a boxcar of the same size (element count). That is it returns the full-width half-maximum
	 * (FWHM) of the response of the window function relative to the FWHM of the response to
	 * a boxcar window of the same size.
	 * 
	 * @param name     the name (case-insensitive) of a standard window function, such as "Nutall"
	 * @return         the relative width of the window functions response w.r.t. the response
	 *                 of a boxcar of the same size (element count).
	 * @throws IllegalArgumentException    if no standard window function is known by that name.
	 * 
	 * @see #getNames()
	 */
	
	public static double getEquivalentWidth(String name) throws IllegalArgumentException {
		name = name.toLowerCase();
		if(name.equals("boxcar") || name.equals("rectangular") || name.equalsIgnoreCase("box") || name.equalsIgnoreCase("uniform")) 
		    return 1.0;
		if(name.equals("hamming")) return 1.37;
		if(name.equals("hann")) return 1.50;
		if(name.equals("blackman")) return 1.73;
		if(name.equals("nutall")) return 2.02;
		if(name.equals("blackman-harris")) return 2.01;
		if(name.equals("blackman-nutall")) return 1.98;
		if(name.equals("flat top")) return 3.77;
		throw new IllegalArgumentException("No window function named '" + name + "'");
	}

	
	/**
	 * Returns a standard window function in an array of the specified size.
	 * 
	 * @param name     the name (case-insensitive) of a standard window function, such as "Nutall".
	 * @param n        the number of points, or size of the window function requested.
	 * @return         a new array of the specified size with the specified window function fitting that size.
	 * @throws IllegalArgumentException    if no standard window function is known by that name.
	 * 
	 * @see #get(int, double...)
	 * @see #getBoxcar(int)
	 * @see #getHamming(int)
	 * @see #getHann(int)
	 * @see #getNutall(int)
	 * @see #getBlackmanHarris(int)
	 * @see #getBlackmanNutall(int)
	 * @see #getFlatTop(int)
	 * @see #getEquivalentWidth(String)
	 */
	public static double[] get(String name, int n) {
		name = name.toLowerCase();
		if(name.equals("boxcar") || name.equals("rectangular") || name.equalsIgnoreCase("box") || name.equalsIgnoreCase("uniform")) 
		    return getBoxcar(n);
		if(name.equals("hamming")) return getHamming(n);
		if(name.equals("hann")) return getHann(n);
		if(name.equals("blackman")) return getBlackman(n);
		if(name.equals("nutall")) return getNutall(n);
		if(name.equals("blackman-harris")) return getBlackmanHarris(n);
		if(name.equals("blackman-nutall")) return getBlackmanNutall(n);
		if(name.equals("flat top") || name.equals("flattop")) return getFlatTop(n);
		throw new IllegalArgumentException("No window function named '" + name + "'");
	}

	/**
	 * Returns a boxcar window function in an array of the specified size. A Boxcar window function has
	 * the same uniform value for all elements.
	 * 
	 * @param n        the number of points, or size of the window function requested.
	 * @return         a new array of the specified size with uniform values.
	 * 
	 * @see #get(int, double...)
	 */
	public static double[] getBoxcar(int n) {
	    // W = 1.00
	    // -15dB
		return get(n, 1.0);
	}

	/**
     * Returns a Hamming window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Hamming window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getHamming(int n) {
	    // W = 1.37
	    // -40dB
		return get(n, 0.53836, -0.46164);
	}

	/**
     * Returns a Hann window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Hann window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getHann(int n) {
	    // W = 1.50
	    // -30dB
		return get(n, 0.5, -0.5);
	}

	/**
     * Returns a Blackman window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Blackman window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getBlackman(int n) {
	    // W = 1.73
	    // -60dB
		return get(n, 0.5, -0.5);
	}

	/**
     * Returns a Ntall window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Nutall window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getNutall(int n) {
	    // W = 2.02
	    // -100dB
		return get(n, 0.355768, -0.487396, 0.144232, -0.012604);
	}

	/**
     * Returns a Blackman-Harris window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Blackman-Harris window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getBlackmanHarris(int n) {
	    // W = 2.01
	    // -100dB
		return get(n, 0.35875, -0.48829, 0.14128, -0.01168);
	}

	/**
     * Returns a Blackman-Nutall window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a Blackman-Nutall window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getBlackmanNutall(int n) {
	    // W = 1.98
	    // -100dB
		return get(n, 0.3635819, -0.4891775, 0.1365995, -0.0106411);
	}

	/**
     * Returns a "flat top" window function in an array of the specified size. 
     * 
     * @param n        the number of points, or size of the window function requested.
     * @return         a new array of the specified size containing a "flat top" window.
     * 
     * @see #get(int, double...)
     */
	public static double[] getFlatTop(int n) {
	    // W = 3.77
	    // -70dB
		return get(n, 1.0, -1.93, 1.29, -0.388, 0.032);
	}

	/**
	 * <p>
	 * Returns a generic window function composed of sinusoidal components, of the form:
	 * </p>
	 * <p>
	 * <i>w</i><sub>i</sub> ~ &sum;<sub>k</sub> <i>c</i><sub>k</sub> cos(2&pi;<i>i</i><i>k</i>/<i>N</i>)
	 * </p>
	 * <p>for a window function with <i>N</i> samples.
	 * <p>
	 * The returned window function is normalized such that:
	 * </p>
	 * <p>
	 * &sum;<sub>i</sub> <i>w</i><sub>i</sub><sup>2</sup> = 1
	 * </p>
	 * 
	 * @param N        the number of points, or size of the window function requested.
	 * @param coeff    the coefficients <i>c</i><sub>k</sub>, as list or array.
	 * @return         a new array of the specified size with the specified window function fitting that size.
	 */
	public static double[] get(int N, double... coeff) {
		final double[] w = new double[N];
		double norm = 0.0;
		final double K = Constant.twoPi / (N - 1);
		
		for(int i=N; --i >= 0; ) {
			final double A = i * K;
			for(int k=coeff.length; --k >= 0; ) w[i] += coeff[k] * Math.cos(A * k);
			norm += w[i]*w[i];
		}

		// normalize to sum w^2
	    // Needed for provididng true PSD estimates...
		for(int i=N; --i >= 0; ) w[i] /= norm;
	
		return w;
	}

}

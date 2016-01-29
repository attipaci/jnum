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


import java.util.Vector;


// TODO: Auto-generated Javadoc
/**
 * The Class Factorial.
 */
public final class Factorial {
	
	/** The Constant store. */
	public final static Vector<Double> store = new Vector<Double>(100);
	static { store.add(1.0); }
	
	// Calculates factorials as needed, storing the values for quick lookup later.
	/**
	 * At.
	 *
	 * @param n the n
	 * @return the double
	 */
	public final static double at(int n) {
		if (n < 0) throw new IllegalArgumentException("Negative Factorial.");
		if (n < store.size()) return store.get(n);

		int m = store.size()-1;
		while (m <= n) store.add(store.get(m)*(++m));
		
		return store.get(m-1);
	}
	
	
	// Calculates values as needed, and stores them (up to some limit) for quick lookup later...
	// Thus repeated calls become very fast for n<MAX_LOGBUFFERED.
	// For larger n, calls to GammaFunction are made...
	/**
	 * Log at.
	 *
	 * @param n the n
	 * @return the double
	 */
	public final static double logAt(int n) {
		if (n < 0) throw new IllegalArgumentException("Negative Factorial.");
		if (n < logStore.size()) return logStore.get(n);

		int m = logStore.size()-1;
		if(n < MAX_LOGBUFFERED) while (m <= n) logStore.add(logStore.get(m) + Math.log(++m));
		else return GammaFunction.logAt(n+1.0);
		
		return logStore.get(m-1);
	}
	
	/** The max logbuffered. */
	static int MAX_LOGBUFFERED = 400;
	
	/** The Constant logStore. */
	public final static Vector<Double> logStore = new Vector<Double>(100);
	static { logStore.add(0.0); }	
	

}

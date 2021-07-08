/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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


/**
 * Factorials, efficiently. It stores previously calculated values so they can be
 * returned again without new calculation. Same goes for the logarithm of factorial values.
 * This feature enables using factorials e.g. in polynomial expansions without significant
 * computational cost.
 * 
 * @author Attila Kovacs
 *
 */
public final class Factorial {
   
    /** Private constructor because we do not want ot instantiate this class. */
    private Factorial() {}
    
    /**
     * Gets the maximum value for which a factorial is stored. (There may be more stored
     * values for the logarithms).
     * 
     * @return the maximum integer for which there can be a stored value.
     */
    public static int maxStored() {
        return store.length;
    }
    
	/**
	 * Gets the factorial of the input argument. It also stores the calculated value for quick lookup later.
	 * 
	 * @param n    the argument
	 * @return     <i>n</i>!
	 * @throws IllegalArgumentException if called with a negative integer
	 * @throws ArithmeticException if the result exceeds the range of numbers resperentable with double precision arithmetic.
	 * 
	 * @see #logAt(int)
	 */
	public static final double at(int n) throws IllegalArgumentException, ArithmeticException {
	    if(n == 0) return 1.0;
		if(n < 0) throw new IllegalArgumentException("Negative Factorial.");
		if(n > store.length) new ArithmeticException("Result exceeds double precision range.");     
		
		if(store[n] == 0.0) store[n] = n * at(n-1);
		return store[n];
	}

	
	/**
     * Gets the natural logarithm of the factorial of the input argument. 
     * It also stores the calculated log value, up to a point, for quick lookup later.
     * For input values exceeding the reserved store size (400), it will return the 
     * value calculated via {@link GammaFunction}.
     * 
     * @param n    the argument
     * @return     log(<i>n</i>!)
     * @throws IllegalArgumentException if called with a negative integer
     * 
     * @see #logAt(int)
     */
	public static final double logAt(int n) throws IllegalArgumentException {
	    if(n == 1) return 0.0;
	    if(n == 0) return 0.0;
        if(n < 0) throw new IllegalArgumentException("Negative Factorial.");
        if(n > logStore.length) return GammaFunction.logAt(n+1.0);
	    
        if(logStore[n] == 0.0) logStore[n] = Math.log(n) + logAt(n-1);
        return logStore[n];
	}
	
	/**
     * The maximum number n for which log(n!) is stored. 
     */
    private static int MAX_LOGBUFFERED = 400;

  
    /**
     * The store for looking up previously calculated factorial values.
     * 
     */
    private static final double[] store = new double[170];
    
    /**
     * The store for looking up previously calculated factorial logarithms.
     * 
     */
    private static final double[] logStore = new double[MAX_LOGBUFFERED];
}

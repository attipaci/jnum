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
 * Provides binomial coefficients (n k), aka <i>n-choose-k</i> fast. It hedges the fast factorial values 
 * provided by {@link Factorial}.
 * 
 * @see Factorial
 */
public final class BinomialCoefficient {

    /**
     * private constructor because we do not want to instantiate this class.
     */
    private BinomialCoefficient() {}
    
    /** 
     * Gets the binomial coefficient (n k) = n! / k! (n-k)!. 
     * 
     * @param n     the upper index (the number of elements to chose from)
     * @param k     the lower index (the number of elements chosen)
     * @return      (n k), that is <i>n-choose-k</i>, the number of ways <i>k</i> items can be selected from <i>n</i>.
     */
	public static final double at(int n, int k) {
	    if(n < 0) throw new IllegalArgumentException("negative n index.");
	    if(k < 0) throw new IllegalArgumentException("negative k index.");
	    if(k > n) throw new IllegalArgumentException("n must be greater or equal to k.");
	    
	    if(n > Factorial.maxStored()) return Math.round(Math.exp(Factorial.logAt(n) - Factorial.logAt(k) - Factorial.logAt(n-k)));
	    return Factorial.at(n) / (Factorial.at(k) * Factorial.at(n-k));
	}
	
}

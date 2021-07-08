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
import jnum.ConsiderateFunction;
import jnum.math.Complex;

/**
 * Hankel functions, H<sub>&nu;</sub><sup>(1)</sup> and H<sub>&nu;</sub><sup>(2)</sup>. Hankel Functions are related 
 * to the {@link Bessel} functions.
 * 
 * @author Attila Kovacs
 *
 * @see Bessel
 */
public final class Hankel {

    /** private constructor, because we do not want to instantiate this class. */
    private Hankel() {}
    
    /**
     * Evaluates a Hankel function of the first kind at the specified argument.
     * 
     * @param n     the index &nu; for the function H<sub>&nu;</sub><sup>(1)</sup>.
     * @param x     the argument
     * @return      H<sub>&nu;</sub><sup>(1)</sup>(<i>x</i>).
     */
    public static Complex H1(int n, double x) {
        final Complex result = new Complex();
        H1(n, x, result);
        return result;
    }
    
    /**
     * Evaluates a Hankel function of the second kind at the specified argument.
     * 
     * @param n     the index &nu; for the function H<sub>&nu;</sub><sup>(2)</sup>.
     * @param x     the argument
     * @return      H<sub>&nu;</sub><sup>(2)</sup>(<i>x</i>).
     */
    public static Complex H2(int n, double x) {
        final Complex result = new Complex();
        H2(n, x, result);
        return result;
    }
    
    /**
     * Evaluates a Hankel function of the first kind at the specified argument.
     * 
     * @param n         the index &nu; for the function H<sub>&nu;</sub><sup>(1)</sup>.
     * @param x         the argument
     * @param result    the complex value in which to return H<sub>&nu;</sub><sup>(1)</sup>(<i>x</i>).
     */
    public static void H1(int n, double x, Complex result) {
        result.set(Bessel.J(n, x), Bessel.Y(n,  x));
    }
    
    /**
     * Evaluates a Hankel function of the first kind at the specified argument.
     * 
     * @param n         the index &nu; for the function H<sub>&nu;</sub><sup>(1)</sup>.
     * @param x         the argument
     * @param result    the complex value in which to return H<sub>&nu;</sub><sup>(1)</sup>(<i>x</i>).
     */
    public static void H2(int n, double x, Complex result) {
        result.set(Bessel.J(n, x), -Bessel.Y(n,  x));
    }
    
    /**
     * A Hankel function of the first kind, H<sub>&nu;</sub><sup>(1)</sup>.
     * 
     * @author Attila Kovacs
     *
     */
	public static final class H1 implements Function<Double, Complex>, ConsiderateFunction<Double, Complex> {
	    /** the index of this Hankel function instance */
	    private int n;
	    
	    /**
	     * Instantiates a Hankel function of the first kind: H<sub>&nu;</sub><sup>(1)</sup>, forn integer
	     * index &nu;
	     * 
	     * @param n    The integer index &nu; for this H<sub>&nu;</sub><sup>(1)</sup> instance.
	     */
	    public H1(int n) {
	        this.n = n;
	    }
	    
	    /**
	     * Gets the index of this Hankel function instance.
	     * 
	     * @return     &nu; for this H<sub>&nu;</sub><sup>(1)</sup> instance.
	     */
	    public int getIndex() {
	        return n;
	    }
		

		@Override
        public final Complex valueAt(final Double x) {
			return H1(n, x);
		}

		@Override
        public final void evaluate(final Double x, final Complex result) {
			H1(n, x, result);
		}

     
	}
		

	/**
     * A Hankel functions of the second kind, H<sub>&nu;</sub><sup>(2)</sup>.
     * 
     * @author Attila Kovacs
     *
     */
	public static final class H2 implements Function<Double, Complex>, ConsiderateFunction<Double, Complex> {
	    /** the index of this Hankel function instance */
        private int n;

	    /**
         * Instantiates a Hankel function of the second kind: H<sub>&nu;</sub><sup>21)</sup>, forn integer
         * index &nu;
         * 
         * @param n    The integer index &nu; for this H<sub>&nu;</sub><sup>(2)</sup> instance.
         */
        public H2(int n) {
            this.n = n;
        }

        /**
         * Gets the index of this Hankel function instance.
         * 
         * @return     &nu; for this H<sub>&nu;</sub><sup>(1)</sup> instance.
         */
        public int getIndex() {
            return n;
        }
        
		@Override
        public final Complex valueAt(final Double x) {
		    return H2(n, x);
		}

		
		@Override
        public final void evaluate(final Double x, final Complex result) {
			H2(n, x, result);
		}
	}
		
}

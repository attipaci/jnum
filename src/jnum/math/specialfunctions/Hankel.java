/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.math.specialfunctions;

import jnum.math.Complex;

// TODO: Auto-generated Javadoc
/**
 * The Class Hankel.
 */
public final class Hankel {

	/**
	 * The Class H1.
	 */
	public final static class H1 {
		
		/**
		 * At.
		 *
		 * @param n the n
		 * @param x the x
		 * @return the complex
		 */
		public final static Complex at(final int n, final double x) {
			final Complex result = new Complex();
			evaluateAt(n, x, result);
			return result;
		}

		/**
		 * Evaluate at.
		 *
		 * @param n the n
		 * @param x the x
		 * @param result the result
		 */
		public final static void evaluateAt(final int n, final double x, final Complex result) {
			result.set(Bessel.J(n, x), Bessel.Y(n,  x));
		}
	}
		
	/**
	 * The Class H2.
	 */
	public final static class H2 {
	
		/**
		 * At.
		 *
		 * @param n the n
		 * @param x the x
		 * @return the complex
		 */
		public final static Complex at(final int n, final double x) {
			final Complex result = new Complex();
			evaluateAt(n, x, result);
			return result;
		}

		/**
		 * Evaluate at.
		 *
		 * @param n the n
		 * @param x the x
		 * @param result the result
		 */
		public final static void evaluateAt(final int n, final double x, final Complex result) {
			result.set(Bessel.J(n, x), -Bessel.Y(n,  x));
		}
	}
		
}

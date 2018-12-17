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

import jnum.math.Complex;


public final class Hankel {


	public final static class H1 {
		

		public final static Complex at(final int n, final double x) {
			final Complex result = new Complex();
			evaluateAt(n, x, result);
			return result;
		}


		public final static void evaluateAt(final int n, final double x, final Complex result) {
			result.set(Bessel.J(n, x), Bessel.Y(n,  x));
		}
	}
		

	public final static class H2 {
	

		public final static Complex at(final int n, final double x) {
			final Complex result = new Complex();
			evaluateAt(n, x, result);
			return result;
		}

		
		public final static void evaluateAt(final int n, final double x, final Complex result) {
			result.set(Bessel.J(n, x), -Bessel.Y(n,  x));
		}
	}
		
}

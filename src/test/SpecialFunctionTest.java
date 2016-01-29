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
package test;

import jnum.math.Complex;
import jnum.math.specialfunctions.CumulativeNormalDistribution;
import jnum.math.specialfunctions.GammaFunction;
import jnum.math.specialfunctions.ZetaFunction;
import jnum.util.ExtraMath;

// TODO: Auto-generated Javadoc
/**
 * The Class SpecialFunctionTest.
 */
public class SpecialFunctionTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.err.println("zeta(4) = " + ZetaFunction.at(4.0));
		System.err.println("zeta(4,~0) = " + ZetaFunction.at(new Complex(4.0, 1e-100)));
		System.err.println("zeta(i) = " + ZetaFunction.at(new Complex(0.0, 1.0)));
		System.err.println("zeta(-3) = " + ZetaFunction.at(-3.0));
		System.err.println("zeta(-3, 0) = " + ZetaFunction.at(new Complex(-3.0, 1e-100)));
		
		System.err.println("gamma(4) = " + GammaFunction.at(4.0));
		System.err.println("gamma(4,~0) = " + GammaFunction.at(new Complex(4.0, 1e-100)));
		System.err.println("gamma(i) = " + GammaFunction.at(new Complex(0.0, 1.0)));
		
		System.err.println("P(0.0) = " + CumulativeNormalDistribution.at(0.0));
		System.err.println("P(1.0) = " + CumulativeNormalDistribution.at(1.0));
		System.err.println("P(2.0) = " + CumulativeNormalDistribution.at(2.0));
		System.err.println("P(3.0) = " + CumulativeNormalDistribution.at(3.0));
		
		System.err.println("(P=0.85) at " + CumulativeNormalDistribution.inverseAt(0.85));
		System.err.println("(Q=0.15) at " + CumulativeNormalDistribution.inverseComplementAt(0.15));
	
		double y = Math.sinh(2.0);
		System.err.println("sinh(2.0) = " + y);
		System.err.println("asinh --> " + ExtraMath.asinh(y));
		
		y = Math.cosh(2.0);
		System.err.println("cosh(2.0) = " + y);
		System.err.println("acosh --> " + ExtraMath.acosh(y));
		
		y = Math.tanh(2.0);
		System.err.println("tanh(2.0) = " + y);
		System.err.println("atanh --> " + ExtraMath.atanh(y));
		
	}
	
}

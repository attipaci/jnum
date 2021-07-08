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

import jnum.Constant;
import jnum.math.ConvergenceException;
import jnum.math.NumericalFunction;

/**
 * Exponential integrals E<sub>n</sub>(<i>x</i>) = &int;<sub>1</sub><sup><i>x</i></sup> <i>e<sup>-xt</sup> t<sup>-n</sup> dt</i>. 
 * Based on <code>expint()</code> of Numerical Recipes for C Second Edition.
 * 
 * @author Attila Kovacs
 *
 */
public final class ExponentialIntegral implements NumericalFunction<Double, Double> {

    /** The index n for this instance of E<subn>n</sub> */
    private int n;
    
    /** precision to which to calculate E<sub>n</sub>. */
    private double precision = 1.0e-7;

    /**
     * Instantiates a new exponential integral E<sub>n</sub>
     * 
     * @param n     The index <i>n</i> for this E<sub>n</sub> instance.
     * @throws IllegalArgumentException if n is negative.
     */
    public ExponentialIntegral(int n) throws IllegalArgumentException {
        if(n < 0) throw new IllegalStateException("order cannot be negative");
        this.n = n; 
    }

    /**
     * Gets the order index <i>n</i> for this instance of E<sub>n</sub>.
     * 
     * @return  <i>n</i> for this instance of E<sub>n</sub>.
     */
    public final int getOrder() { return n; }

    @Override
    public final Double valueAt(Double x) throws IllegalArgumentException {        
        if (x < 0.0) throw new IllegalStateException("x cannot be negative.");
        if(x == 0.0) if(n == 0 || n == 1) throw new IllegalStateException("x cannot be 0 for order "+ n);

        
        double ans;
        final int nm1 = n-1;
        
        if (n == 0) ans = Math.exp(-x)/x;
        else {
            double del;			
            if (x == 0.0) ans = 1.0 / nm1;
            else {
                if (x > 1.0) {
                    double b = x + n;
                    double c = hugeValue;
                    double d = 1.0 / b;
                    double h = d;
                    
                    for (int i = 1; i <= maxIteration; i++) {
                        final double a = -i * (nm1 + i);
                        b += 2.0;
                        d = 1.0 / (a * d + b);
                        c = b + a / c;
                        del = c * d;
                        h *= del;
                        if (Math.abs(del-1.0) < precision) {
                            ans = h * Math.exp(-x);
                            return ans;
                        }
                    }
                    throw new ConvergenceException("Continued fraction failed in " + getClass().getSimpleName() + ".");
                } 

                ans = (nm1 != 0 ? 1.0/nm1 : -Math.log(x) - Constant.euler);
                double fact = 1.0;
                for (int i = 1; i <= maxIteration; i++) {
                    fact *= -x / i;
                    if (i != nm1) del = -fact / (i - nm1);
                    else {
                        double psi = -Constant.euler;
                        for (int ii = 1; ii <= nm1; ii++) psi += 1.0 / ii;
                        del = fact * (-Math.log(x) + psi);
                    }
                    ans += del;
                    if (Math.abs(del) < Math.abs(ans) * precision) return ans;
                }
                throw new ConvergenceException("Convergence not achieved in " + getClass().getSimpleName() + ".");
            }
        }

        return ans;
    }

    @Override
    public final int getMaxPrecisionAt(Double x) {
        return -(int) Math.ceil(Math.log10(precision));
    }

    @Override
    public final void setPrecision(int digits) {
        precision = Math.pow(0.1, digits);
    }

    /** Maximum nomber of iterations before throwing a ConvergenceException */
    public static int maxIteration = 300;
    /** A big numerical value to use in place of 1/0 */
    private static double hugeValue = 1.0e300;


}

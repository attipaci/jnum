/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.fitting;

import jnum.math.matrix.SquareMatrix;

// TODO: Auto-generated Javadoc
/**
 * Represents the Hessian Matrix for a {@link Parametric} function of {@link java.lang.Double} type, using either numerical 
 * differentiation, or an appropriate {@link CovarianceMatrix} object.
 */
public class HessianMatrix extends SquareMatrix {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7056277026515087299L;
    
    /** The parameters. */
    private Parameter[] parameters;
    
    /**
     * Instantiates a new Hessian matrix for a set of parameters on a real-valued {@link Parametric} expression.
     *
     * @param function the parametric function whose second derivatives will be estimated numerically.
     * @param p the parameters for which the Hessian is calculated.
     */
    public HessianMatrix(Parametric<Double> function, Parameter[] p) {
        this(function, p, DEFAULT_EPSILON);
    }
    
    /**
     * Instantiates a new Hessian matrix or a set of parameters on a real-valued {@link Parametric} expression, with
     * the specified fractional step-size used in the numerical differentiation.
     *
     * @param function the parametric function whose second derivatives will be estimated numerically.
     * @param p the parameters for which the Hessian is calculated.
     * @param stepFraction the fractional step size used when evaluating the fractional parameters. The function will be 
     *        evaluated at +/- h from the nominal parameter values, where h is this specified fraction times the value
     *        returned by {@link Parameter#getStepSize()}
     */
    public HessianMatrix(Parametric<Double> function, Parameter[] p, double stepFraction) {    
        this.parameters = p;
        calc(function, stepFraction);
    }
    
    /**
     * Instantiates a new Hessian matrix, as the inverse of the specified covariance matrix.
     *
     * @param C the covariance matrix for a set of parameters.
     */
    public HessianMatrix(CovarianceMatrix C) {    
        this.parameters = C.getParameters();
        setData(C.getSVDInverse().getData());
    }
    
    /**
     * Gets the parameters for whose Hessian this object represents.
     *
     * @return the parameters.
     */
    public Parameter[] getParameters() { return parameters; }

    /**
     * Calculates the Hessian using the appropriate numerical derivatives with the parameters being
     * stepped by the the specified fraction of their natural step size (see {@link Parameter#getStepSize()}).
     *
     * @param function the function whose second derivatives are to be calculated.
     * @param stepFraction the step size fraction
     */
    protected void calc(Parametric<Double> function, double stepFraction) {
       
        double[][] A = new double[parameters.length][parameters.length];
        
        double y0 = function.evaluate();
        
        for(int i=parameters.length; --i >= 0; ) {
            final Parameter pi = parameters[i];
            final double deltai = stepFraction * pi.getStepSize();
            final double xi = pi.value();
                
            // Calculate the diagonal elements first:
            pi.add(deltai); // x+
            
            double dy = function.evaluate() - y0;
            
            pi.subtract(2.0 * deltai); // x-
            dy += function.evaluate() - y0;
            
            pi.setValue(xi); // x0
            
            A[i][i] = dy / (deltai * deltai);  
           
            // Now the off-diagonals
            for(int j=i; --j >= 0; ) {
                final Parameter pj = parameters[j];
                final double deltaj = stepFraction * pj.getStepSize();
                final double xj = pj.value();
                
                pi.add(deltai);
                pj.add(deltaj);
                
                dy = function.evaluate();   // y++
                
                pj.subtract(2.0 * deltaj);
                
                dy -= function.evaluate();   // y+-
                
                pi.subtract(2.0 * deltai);
                
                dy += function.evaluate();   // y--
                
                pj.add(2.0 * deltaj);
                
                dy -= function.evaluate();   // y-+
                
                A[j][i] = A[i][j] = dy / (4.0 * deltai * deltaj);
                
                // return to center...
                pi.setValue(xi);
                pj.setValue(xj);
            }
        }    
         
        setData(A);
    }
    
    /**
     * Gets the covariance matrix from this Hessian.
     *
     * @return the covariance matrix
     */
    public CovarianceMatrix getCovarianceMatrix() {
        return new CovarianceMatrix(this);
    }
    
    /** The default epsilon. */
    public static double DEFAULT_EPSILON = 1e-8;
}

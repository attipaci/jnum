/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data.fitting;

import jnum.math.SquareMatrix;

public class HessianMatrix extends SquareMatrix {
    /**
     * 
     */
    private static final long serialVersionUID = 7056277026515087299L;
    
    private Parameter[] parameters;
    
    public HessianMatrix(Parametric<Double> function, Parameter[] p) {
        this(function, p, DEFAULT_EPSILON);
    }
    
    public HessianMatrix(Parametric<Double> function, Parameter[] p, double stepSizeFraction) {    
        this.parameters = p;
        calc(function, stepSizeFraction);
    }
    
    public HessianMatrix(CovarianceMatrix C) {    
        this.parameters = C.getParameters();
        setData(C.getSVDInverse().getData());
    }
    
    public Parameter[] getParameters() { return parameters; }

    protected void calc(Parametric<Double> function, double stepSizeFraction) {
       
        double[][] A = new double[parameters.length][parameters.length];
        
        double y0 = function.evaluate();
        
        for(int i=parameters.length; --i >= 0; ) {
            final Parameter pi = parameters[i];
            final double deltai = stepSizeFraction * pi.getStepSize();
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
                final double deltaj = stepSizeFraction * pj.getStepSize();
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
    
    public CovarianceMatrix getCovarianceMatrix() {
        return new CovarianceMatrix(this);
    }
    
    public static double DEFAULT_EPSILON = 1e-6;
}

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


import jnum.data.ArrayUtil;
import jnum.math.AbstractMatrix;
import jnum.math.SquareMatrix;

public class CovarianceMatrix extends SquareMatrix {
    /**
     * 
     */
    private static final long serialVersionUID = 1183263893625404802L;
    private Parameter[] parameters;
    
    public CovarianceMatrix(HessianMatrix A) {
        this.parameters = A.getParameters();
    }
    
    public CovarianceMatrix(CorrelationMatrix R) {
        this.parameters = R.getParameters();
    }
    
    public CovarianceMatrix(Parametric<Double> function, Parameter[] p) {
        this(function, p, HessianMatrix.DEFAULT_EPSILON);
    }
    
    public CovarianceMatrix(Parametric<Double> function, Parameter[] p, double stepSizeFraction) {    
        this.parameters = p;
        setData(new HessianMatrix(function, parameters, stepSizeFraction).getSVDInverse().getData());
    }
    
    @Override
    public AbstractMatrix<Double> copy(boolean withContents) {
        CovarianceMatrix P = (CovarianceMatrix) super.copy(withContents);
        if(parameters != null) {
            if(withContents) P.parameters = new Parameter[parameters.length];
            else {
                try { P.parameters = (Parameter[]) ArrayUtil.copyOf(parameters); }
                catch(Exception e) { P.parameters = null; }
            }
        }
        return P;
    }
  
    
    public Parameter[] getParameters() { return parameters; }
 
    public void setParameterErrors() {
        for(int i=size(); --i >= 0; ) parameters[i].setWeight(1.0 / getValue(i, i));
    }
    
    public CorrelationMatrix getCorrelationMatrix() {
        return new CorrelationMatrix(this);
    }
    
    public HessianMatrix getHessianMatrix() {
        return new HessianMatrix(this);
    }
  
}

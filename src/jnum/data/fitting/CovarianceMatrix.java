/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data.fitting;


import jnum.data.ArrayUtil;
import jnum.math.matrix.AbstractMatrix;
import jnum.math.matrix.SquareMatrix;

// TODO: Auto-generated Javadoc
/**
 * Represents a covariance matrix for a set of parameters, calculated either as the inverse of a {@link HessianMatrix}, or from 
 * a {@link CorrelationMatrix}.
 */
public class CovarianceMatrix extends SquareMatrix {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1183263893625404802L;
    
    /** The parameters. */
    private Parameter[] parameters;
    
    /**
     * Instantiates a new covariance matrix as the inverse of a Hessian matrix.
     *
     * @param A the Hessian matrix.
     */
    public CovarianceMatrix(HessianMatrix A) {
        this.parameters = A.getParameters();
    }
    
    /**
     * Instantiates a new covariance matrix from a correlation matrix.
     *
     * @param R the correlation matrix.
     */
    public CovarianceMatrix(CorrelationMatrix R) {
        this.parameters = R.getParameters();
    }
    
    /**
     * Instantiates a new covariance matrix, calculated based on the second derivatives of the specified function for
     * the given set of parameters. This is a shorthand for a numerical {@link HessianMatrix} first and using it
     * to instantiate a covariance matrix from it.
     *
     * @param function the parametric function on which the covariance is based.
     * @param p the parameters appearing in the covariance matrix.
     */
    public CovarianceMatrix(Parametric<Double> function, Parameter[] p) {
        this(function, p, HessianMatrix.DEFAULT_EPSILON);
    }
    
    /**
     * Instantiates a new covariance matrix.
     * @param function the parametric function on which the covariance is based.
     * @param p the parameters appearing in the covariance matrix.
     * @param stepFraction the fraction of the parameters' natural step size (see @link Parameter#geStepSize()}) used
     *        for evaluating the second derivatives.
     */
    public CovarianceMatrix(Parametric<Double> function, Parameter[] p, double stepFraction) {    
        this.parameters = p;
        setData(new HessianMatrix(function, parameters, stepFraction).getSVDInverse().getData());
    }
    
    /* (non-Javadoc)
     * @see jnum.math.AbstractMatrix#copy(boolean)
     */
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
  
    
    /**
     * Gets the parameters represented by this covariance matrix.
     *
     * @return the parameters
     */
    public Parameter[] getParameters() { return parameters; }
 
    /**
     * Set the uncertainties for all parameters.
     */
    public void setParameterErrors() {
        for(int i=size(); --i >= 0; ) parameters[i].setWeight(1.0 / getValue(i, i));
    }
    
    /**
     * Gets the correlation matrix that corresponds to this covariance matrix.
     *
     * @return the correlation matrix
     */
    public CorrelationMatrix getCorrelationMatrix() {
        return new CorrelationMatrix(this);
    }
    
    /**
     * Gets the hessian matrix that corresponds to this covariance matrix.
     *
     * @return the hessian matrix
     */
    public HessianMatrix getHessianMatrix() {
        return new HessianMatrix(this);
    }
  
}

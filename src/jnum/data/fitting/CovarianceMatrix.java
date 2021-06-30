/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.fitting;

import jnum.math.matrix.Matrix;
import jnum.math.matrix.SquareMatrixException;


/**
 * Represents a covariance matrix for a set of parameters, calculated either as the inverse of a {@link HessianMatrix}, or from 
 * a {@link CorrelationMatrix}.
 */
public class CovarianceMatrix extends Matrix {
    
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
        super(A.rows());
        setData(A.getSVDInverse().getData());
        this.parameters = A.getParameters();
    }
    
    /**
     * Instantiates a new covariance matrix from a correlation matrix.
     *
     * @param R the correlation matrix.
     */
    public CovarianceMatrix(CorrelationMatrix R) {
        super(R.rows());
        calcFrom(R);
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
        super(p.length);
        this.parameters = p;
        setData(new HessianMatrix(function, parameters, stepFraction).getSVDInverse().getData());
    }

    @Override
    public CovarianceMatrix copy(boolean withContents) {
        CovarianceMatrix P = (CovarianceMatrix) super.copy(withContents);
        if(parameters != null) {
            if(withContents) P.parameters = new Parameter[parameters.length];
            else P.parameters = Parameter.copyOf(parameters);
        }
        return P;
    }
  
    /**
     * Calculates the correlation matrix from a covariance matrix.
     *
     * @param C the covariance matrix
     */
    private void calcFrom(CorrelationMatrix R) {
        if(!R.isSquare()) throw new SquareMatrixException();
        
        
        assertSize(R.rows(), R.rows());

        double[] sigma = R.getSigmas();

        for(int i=rows(); --i >= 0; ) for(int j=rows(); --j >= 0; )
            set(i, j, R.get(i, j) * sigma[i] * sigma[j]);
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
        if(!isSquare()) throw new SquareMatrixException();
        for(int i=rows(); --i >= 0; ) parameters[i].setWeight(1.0 / get(i, i));
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

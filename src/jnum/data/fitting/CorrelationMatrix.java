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

import jnum.Util;
import jnum.math.matrix.SquareMatrix;

// TODO: Auto-generated Javadoc
/**
 * Represents a correlation matrix for a set of parameters, calculated from an appropriate {@link CovarianceMatrix} object.
 */
public class CorrelationMatrix extends SquareMatrix {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1272894374094189932L;
    
    /** The parameters. */
    private Parameter[] parameters;
    
    /** The inver standard deviations. */
    private double[] isigma;

    /**
     * Instantiates a new correlation matrix from the specified covariance matrix object.
     *
     * @param C the covariance matrix
     */
    public CorrelationMatrix(CovarianceMatrix C) {
        this.setSize(C.size());
       
        parameters = C.getParameters();
        isigma = new double[C.size()];
        
        calcFrom(C);
    }
    
    /**
     * Calculates the correlation matrix from a covariance matrix.
     *
     * @param C the covariance matrix
     */
    private void calcFrom(CovarianceMatrix C) {
        
       for(int i=size(); --i >= 0; ) isigma[i] = 1.0 / Math.sqrt(C.getValue(i, i));
      
       for(int i=size(); --i >= 0; ) for(int j=size(); --j >= 0; )
           setValue(i, j, C.getValue(i,  j) * isigma[i] * isigma[j]);
        
    }
   
    
    /**
     * A string list representation of the standard deviations of the represented parameters.
     *
     * @return the string
     */
    public String sigmasToString() {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i < size(); i++) buf.append(Util.s3.format(1.0/isigma[i]) + ", ");
        return new String(buf);
    }
    
    /**
     * Gets the covariance matrix corresponding to this correlation matrix.
     *
     * @return the covariance matrix
     */
    public CovarianceMatrix getCovarianceMatrix() {
        return new CovarianceMatrix(this);
    }
        
    /**
     * Gets the parameters represented by this correlation matrix.
     *
     * @return the parameters
     */
    public Parameter[] getParameters() { return parameters; }
    
}

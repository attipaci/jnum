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

import jnum.Util;
import jnum.math.SquareMatrix;

public class CorrelationMatrix extends SquareMatrix {

    /**
     * 
     */
    private static final long serialVersionUID = -1272894374094189932L;
    
    private Parameter[] parameters;
    private double[] isigma;

    public CorrelationMatrix(CovarianceMatrix C) {
        this.setSize(C.size());
       
        parameters = C.getParameters();
        isigma = new double[C.size()];
        
        calcFrom(C);
    }
    
    protected void calcFrom(CovarianceMatrix C) {
        
       for(int i=size(); --i >= 0; ) isigma[i] = 1.0 / Math.sqrt(C.getValue(i, i));
      
       for(int i=size(); --i >= 0; ) for(int j=size(); --j >= 0; )
           setValue(i, j, C.getValue(i,  j) * isigma[i] * isigma[j]);
        
    }
   
    
    public String sigmasToString() {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i < size(); i++) buf.append(Util.e3.format(1.0/isigma[i]) + ", ");
        return new String(buf);
    }
    
    public CovarianceMatrix getCovarianceMatrix() {
        return new CovarianceMatrix(this);
    }
        
    public Parameter[] getParameters() { return parameters; }
    
}

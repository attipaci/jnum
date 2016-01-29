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
package jnum.data.fitting;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class Minimizer.
 */
public abstract class Minimizer {
	
	/** The precision. */
	public double precision = 1e-6;
	
	/**
	 * Evaluate.
	 *
	 * @param tryparms the tryparms
	 * @return the double
	 */
	public abstract double evaluate(double[] tryparms);   

    /**
     * Minimize.
     *
     * @return the double
     */
    public abstract double minimize();

    /**
     * Gets the fit parameters.
     *
     * @return the fit parameters
     */
    public abstract double[] getFitParameters();
    
    /**
     * Gets the chi2.
     *
     * @return the chi2
     */
    public abstract double getChi2();
    
    /**
     * Gets the covariance matrix.
     *
     * @return the covariance matrix
     */
    public double[][] getCovarianceMatrix() {
    	return getCovarianceMatrix(1e4 * precision);
    }
    	
    /**
     * Gets the covariance matrix.
     *
     * @param epsilon the epsilon
     * @return the covariance matrix
     */
    public double[][] getCovarianceMatrix(double epsilon) {
    	double[][] A = getA();
    	double[][] M = new double[A.length][2*A.length];
    	
    	for(int i=0; i<A.length; i++) {
    		System.arraycopy(A[i], 0, M[i], 0, A.length);
    		M[i][A.length+i] = 1.0;
    	}
    	
    	gaussJordan(M);
    	
    	double[][] C = new double[A.length][A.length];
    	for(int i=0; i<A.length; i++) System.arraycopy(M[i], A.length, C[i], 0, A.length);
    	return C;
    }
    
    /**
     * Gets the a.
     *
     * @return the a
     */
    protected double[][] getA() {
    	return getA(1e4 * precision);
    }
    
    /**
     * Gets the a.
     *
     * @param epsilon the epsilon
     * @return the a
     */
    protected double[][] getA(double epsilon) {
    	double[] parameter = getFitParameters();
    	double[] delta = new double[parameter.length];
    	for(int i=0; i<parameter.length; i++) delta[i] = epsilon * parameter[i];
    	return getA(delta);
    }
  
    /**
     * Gets the a.
     *
     * @param delta the delta
     * @return the a
     */
    protected double[][] getA(double[] delta) {
    	double[] parameter = getFitParameters();    
    	double[] tryparm = new double[parameter.length];
    	System.arraycopy(parameter, 0, tryparm, 0, parameter.length);
    	double[][] A = new double[parameter.length][parameter.length];
    	double min = getChi2();
    	
    	// Make delta[i] 'exact' to avoud rounding errors...
    	for(int i=0; i<parameter.length; i++) {
    		final double temp = parameter[i] + delta[i];
    		delta[i] = parameter[i] - temp;
    	}
    	
    	for(int i=0; i<parameter.length; i++) {	
    		tryparm[i] += delta[i];
    		for(int j=i; j<parameter.length; j++) {
    			tryparm[j] += delta[j];
    			A[i][j] = (evaluate(tryparm) - min) / (delta[i] * delta[j]);
    			A[j][i] = A[i][j];
    			tryparm[j] = parameter[j];
    		}
    		tryparm[i] = parameter[i];
    	}
    	
    	//print(A);
    	
    	return A;
    }
    
    /**
     * Gauss jordan.
     *
     * @param M the m
     */
    public void gaussJordan(double[][] M) {
		int rows = M.length;
		int cols = M[0].length;

		int[] indxc = new int[rows];
		int[] indxr = new int[rows];
		int[] ipiv = new int[rows];
		
		Arrays.fill(ipiv, -1);

		for(int i=0;i<rows;i++) {
			int icol=-1, irow=-1;
			double big=0.0;
			for(int j=0;j<rows;j++)
				if(ipiv[j] != 0)
					for(int k=0;k<rows;k++) {
						if(ipiv[k] == -1) {
							if(Math.abs(M[j][k]) >= big) {
								big=Math.abs(M[j][k]);
								irow=j;
								icol=k;
							}
						} 
						else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular PrimitiveMatrix-1 during Gauss-Jordan elimination.");
					}
			++(ipiv[icol]);
			if(irow != icol) {
				for(int l=0;l<cols;l++) {
					double temp = M[irow][l];
					M[irow][l] = M[icol][l];
					M[icol][l] = temp;
				}
			}
			indxr[i]=irow;
			indxc[i]=icol;
			if(M[icol][icol] == 0.0) throw new IllegalArgumentException("Singular PrimitiveMatrix-2 during Gauss-Jordan elimination.");
			double pivinv=1.0 / M[icol][icol];
			M[icol][icol] = 1.0;
			for(int j=0; j<cols; j++) M[icol][j] *= pivinv;
			
			for(int ll=0;ll<rows;ll++)
				if(ll != icol) {
					double temp=M[ll][icol];
					M[ll][icol] = 0.0;
					for(int j=0; j<cols; j++) M[ll][j] -= temp * M[icol][j];
				}
		}
		for(int l=rows-1;l>=0;l--) {
			if(indxr[l] != indxc[l])
				for(int k=0;k<rows;k++) {
					double temp = M[k][indxr[l]];
					M[k][indxr[l]] = M[k][indxc[l]];
					M[k][indxc[l]] = temp;
				}
		}
	}
    
    /**
     * Prints the.
     *
     * @param M the m
     */
    public void print(double[][] M) {
    	System.out.println();
    	for(int i=0; i<M.length; i++) {
    		for(int j=0; j<M[i].length; j++) System.out.print(M[i][j] + "  ");
    		System.out.println();
    	}
    }
    
}

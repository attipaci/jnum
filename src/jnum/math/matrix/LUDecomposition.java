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
package jnum.math.matrix;

import java.util.Arrays;

import jnum.math.MathVector;
import jnum.math.Vector2D;
import jnum.math.Vector3D;


public class LUDecomposition implements MatrixInverter<Double>, RealMatrixSolver {
    private Matrix LU, inverse;
    private int[] index;
	private boolean evenChanges;
	
	public LUDecomposition(Matrix M) {
	    this(M, 1e-100);
	}
	
	public LUDecomposition(Matrix M, double tinyValue) {
	    if(!M.isSquare()) throw new SquareMatrixException();
        LU = M.copy();
        decompose(tinyValue);
	}
	
    public Matrix getLU() {
        return LU.copy();
    }
   
    public final int size() { return LU.rows(); }

	@Override
    public void solveFor(double[] y, double[] x) {
	    LU.assertSize(x.length, y.length);
	    System.arraycopy(y,  0,  x, 0, y.length);
        solve(x);

	}

	@Override
    public double[] solveFor(double[] y) {
	    LU.assertSize(y.length, y.length);
        double[] x = new double[y.length];
        solveFor(y, x);
        return x;
    }
	
    @Override
    public Double[] solveFor(Double[] y) {
        LU.assertSize(y.length, y.length);
        Double[] x = new Double[y.length];
        solveFor(y, x);
        return x;       
    }
    
    @Override
    public void solveFor(Double[] y, Double[] x) {
        LU.assertSize(x.length, y.length);
        double[] v = new double[y.length];
        for(int i=y.length; --i >= 0; ) v[i] = y[i];
        solve(v);
        for(int i=y.length; --i >= 0; ) x[i] = v[i]; 
    }


    @Override
    public RealVector solveFor(MathVector<Double> y) {
        LU.assertSize(y.size(), y.size());
        RealVector x = new RealVector(y.size());
        solve(x.getData());
        return x;        
    }
    
    @Override
    public void solveFor(MathVector<Double> y, MathVector<Double> x) {
        LU.assertSize(x.size(), y.size());
        double[] v = new double[size()];
        for(int i=size(); --i >=0 ; ) v[i] = y.getComponent(i);
        solve(v);
        for(int i=size(); --i >=0 ; ) x.setComponent(i, v[i]);
    }
    
    @Override
    public RealVector solveFor(RealVector y) {
        LU.assertSize(y.size(), y.size());
        RealVector x = new RealVector(size());
        solveFor(y, x);
        return x;
    }
    
    @Override
    public void solveFor(RealVector y, RealVector x) {
        solveFor(y.getData(), x.getData());
    }
    
    
    
    public Vector3D solveFor(Vector3D y) {
        LU.assertSize(3, 3);
        Vector3D x = new Vector3D();
        solveFor(y, x);
        return x;
    }
    
    public Vector2D solveFor(Vector2D y) {
        LU.assertSize(2, 2);
        Vector2D x = new Vector2D();
        solveFor(y, x);
        return x;
    }
    
    private void solve(double v[]) {
        int ii=-1;
        int n = size();
        
        for(int i=0; i<n; i++) {
            int ip = index[i];
            double sum = v[ip];
            v[ip] = v[i];
            if(ii != -1) for(int j=ii; j< i; j++) sum -= LU.get(i,j) * v[j];
            else if(sum != 0.0) ii = i;
            v[i] = sum;
        }
        for(int i=n; --i >= 0; ) {
            double sum = v[i];
            for(int j=i+1; j<n; j++) sum -= LU.get(i, j) * v[j];
            v[i] = sum / LU.get(i, i);
        }
    }
    
    
    
    @Override
    public Matrix getInverseMatrix() {
        if(inverse == null) {
            inverse = new Matrix(size());
            getInverseTo(inverse);
        }
        return inverse.copy();
    }

	@Override
    public void getInverseTo(AbstractMatrix<Double> inverse) {
	    if(!inverse.isSquare()) throw new SquareMatrixException();
	    
		final int n = size();
		
		if(inverse.rows() != n) throw new IllegalArgumentException("mismatched inverse matrix size.");
		
		double[] v = new double[n];
		
		for(int i=0; i<n; i++) {
			if(i > 0) Arrays.fill(v, 0.0);
			v[i] = 1.0;
			solve(v);
			for(int j=n; --j >= 0; ) inverse.set(j, i, v[j]);
		}
	}
	

    private void decompose(double tinyValue) {
        final int n = size();

        index = new int[n];
        evenChanges = true;
        
        final double[] v = new double[n];


        for(int i=n; --i >= 0; ) {
            double big = 0.0;
            for(int j=n; --j >= 0; ) {
                final double temp = Math.abs(LU.get(i, j));
                if(temp > big) big = temp;
            }
            if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
            v[i] = 1.0 / big;
        }
        for(int j=0; j<n; j++) {
            int imax = -1;

            for(int i=j; --i >= 0; ) {
                double sum = LU.get(i, j);
                for(int k=i; --k >= 0; ) sum -= LU.get(i, k) * LU.get(k, j);
                LU.set(i, j, sum);
            }
            double big = 0.0;
            for(int i=n; --i >= j; ) {
                double sum = LU.get(i, j);
                for(int k=j; --k >= 0; ) sum -= LU.get(i, k) * LU.get(k, j);
                LU.set(i, j, sum);
                
                final double temp = v[i] * Math.abs(sum);
                if (temp >= big) {
                    big=temp;
                    imax=i;
                }
            }
            if(j != imax) {
                LU.swapRows(imax, j);
                evenChanges = !evenChanges;
                v[imax] = v[j];
            }
            index[j] = imax;
            if(LU.get(j, j) == 0.0) LU.set(j, j, tinyValue);

            if(j != n-1) {
                double temp = 1.0 / LU.get(j, j);
                for(int i=n; --i > j; ) LU.scale(i, j, temp);
            }
        }
    }	
}

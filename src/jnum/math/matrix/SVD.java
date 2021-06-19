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


import jnum.math.MathVector;

// TODO: Auto-generated Javadoc
// Decomposes Matrix A as:
// A = U * diag(w) * V^T
// square A --> A^-1 = V * diag(1/w) * U^T 
// where U is column-orthogonal and V is orthogonal
public class SVD implements MatrixInverter<Double>, RealMatrixSolver {

    private Matrix u;
    private Matrix v;  // square

    private Matrix inverse;

    private double[] w;


    public SVD(Matrix M) {
        decompose(M);
    }

    
    public Matrix getU() { return u.copy(); }


    public Matrix getV() { return v.copy(); }

    private int cols() {
        return v.cols();
    }
    
    public double[] getW() { 
        double[] copy = new double[w.length];
        System.arraycopy(w, 0, copy, 0, w.length);
        return copy;	    
    }
    
    


    public void decompose(Matrix M) {
        int n = M.cols();
        u = M.copy();
        v = new Matrix(n);
        w = new double[n];
        u.SVD(w, v);
    }


    @Override
    public double[] solveFor(double y[]) {
        v.assertSize(y.length, cols());
        double[] x = new double[cols()];
        solveFor(y, x);
        return x;
    }


    @Override
    public void solveFor(double y[], double x[]) {
        v.assertSize(y.length, x.length);
        
        final int n = x.length;
        final int m = y.length;

        double[] tmp = new double[n];

        for (int j=n; --j >= 0; ) {
            double s = 0.0;
            if(w[j] != 0.0) {
                for(int i=m; --i >= 0; ) s += u.get(i, j) * y[i];
                s /= w[j];
            }
            tmp[j] = s;
        }
        for(int j=n; --j >= 0; ) {
            double s = 0.0;
            for(int jj=n; --jj >= 0; ) s += v.get(j, jj) * tmp[jj];
            x[j] = s;
        }

    }

    
    @Override
    public Double[] solveFor(Double[] y) {
        v.assertSize(y.length, cols());
        Double[] x = new Double[cols()];
        solveFor(y, x);
        return x;       
    }
    
    @Override
    public void solveFor(Double[] y, Double[] x) {
        v.assertSize(y.length, cols());
        double[] a = new double[y.length];
        for(int i=y.length; --i >= 0; ) a[i] = y[i];
        a = solveFor(a);
        for(int i=x.length; --i >= 0; ) x[i] = a[i]; 
    }
  
    @Override
    public RealVector solveFor(MathVector<Double> y) {
        v.assertSize(y.size(), cols());
        RealVector x = new RealVector(cols());
        solveFor(y, x);
        return x;
    }
    
  
    
    @Override
    public void solveFor(MathVector<Double> y, MathVector<Double> x) {
        v.assertSize(y.size(), x.size());
        double[] a = new double[y.size()];
        for(int i=y.size(); --i >=0 ; ) a[i] = y.getComponent(i);
        a = solveFor(a);
        for(int i=x.size(); --i >=0 ; ) x.setComponent(i, a[i]);
    }

    
    @Override
    public RealVector solveFor(RealVector y) {
        v.assertSize(y.size(), cols());
        RealVector x = new RealVector(cols());
        solveFor(y, x);
        return x;
    }
    
    @Override
    public void solveFor(RealVector y, RealVector x) {
        solveFor(y.getData(), x.getData());
    }
    
    
    public Matrix getReconstructedMatrix() {
        final int n = w.length;
        Matrix wvT = new Matrix(n);
        for(int i=n; --i >= 0; ) for(int j=n; --j >= 0; ) wvT.set(i, j, w[i] * v.get(j, i));
        return u.dot(wvT);
    }

    // square A --> A^-1 = V * diag(1/w) * U^T 
    @Override
    public Matrix getInverseMatrix() {
        if(inverse == null) {    
            inverse = new Matrix(w.length);
            getInverseTo(inverse);
        }
        return inverse.copy();		
    }


    @Override
    public void getInverseTo(AbstractMatrix<Double> inverse) {	
        final int n = w.length;
        if(u.rows() != n) throw new SquareMatrixException("Cannot invert non-square matrix.");
        Matrix iwuT = new Matrix(n);
        for(int i=n; --i >= 0; ) for(int j=n; --j >= 0; ) iwuT.set(i, j, 1.0 / w[i] * u.get(j, i));
        inverse.setProduct(v, iwuT);
    }


}

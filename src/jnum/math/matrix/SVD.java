/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.math.matrix;

import jnum.ExtraMath;
import jnum.math.ConvergenceException;
import jnum.math.MathVector;



// Decomposes Matrix A as:
// A = U * diag(w) * V^T
// square A --> A^-1 = V * diag(1/w) * U^T 
// where U is column-orthogonal and V is orthogonal


public class SVD implements MatrixInverter<Double>, MatrixSolver<Double>, RealMatrixSolver {

    private Matrix u;
    private Matrix v;  // square
    private double[] w;
    
    private Matrix inverse;
    
    public static int defaultMaxIterations = 100;

    public SVD(Matrix M) {
        this(M, defaultMaxIterations);
    }


    public SVD(Matrix M, int maxIterations) {
        int n = M.cols();
        u = M.copy();
        v = M.getMatrixInstance(n, n, false);
        w = new double[n];         
        decompose(maxIterations);
    }

    public Matrix getU() { return u.copy(); }

    public Matrix getV() { return v.copy(); }

    private int cols() {
        return u.cols();
    }

    public double[] getW() { 
        double[] copy = new double[w.length];
        System.arraycopy(w, 0, copy, 0, w.length);
        return copy;        
    }


    @Override
    public double[] solveFor(double y[]) {
        u.assertSize(y.length, cols());
        double[] x = new double[cols()];
        solveFor(y, x);
        return x;
    }


    @Override
    public void solveFor(double y[], double x[]) {
        u.assertSize(y.length, x.length);

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
        u.assertSize(y.length, cols());
        Double[] x = new Double[cols()];
        solveFor(y, x);
        return x;       
    }

    @Override
    public void solveFor(Double[] y, Double[] x) {
        u.assertSize(y.length, cols());
        double[] v = new double[y.length];
        for(int i=y.length; --i >= 0; ) v[i] = y[i];
        v = solveFor(v);
        for(int i=x.length; --i >= 0; ) x[i] = v[i]; 
    }

    @Override
    public RealVector solveFor(MathVector<? extends Double> y) {
        u.assertSize(y.size(), cols());
        RealVector x = new RealVector(cols());
        solveFor(y, x);
        return x;
    }

    @Override
    public void solveFor(MathVector<? extends Double> y, MathVector<Double> x) {
        RealVector vx = solveFor(y);
        for(int i=x.size(); --i >=0 ; ) x.setComponent(i, vx.getComponent(i));
    }


    @Override
    public RealVector solveFor(RealVector y) {
        u.assertSize(y.size(), cols());
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
            final int n = w.length;
            if(u.rows() != n) throw new SquareMatrixException("Cannot invert non-square matrix.");
            Matrix iwuT = u.getMatrixInstance(n, n, false);
            for(int i=n; --i >= 0; ) for(int j=n; --j >= 0; ) iwuT.set(i, j, 1.0 / w[i] * u.get(j, i));
            inverse = v.dot(iwuT);
        }
        return inverse.copy();      
    }

    // A = U * diag(w) * V^T
    // square A --> A^-1 = V * diag(1/w) * U^T
    // Based on Numerical Recipes in C (Press et al. 1989)
    private void decompose(int maxIterations) {
        final int m = u.rows();
        final int n = u.cols();

        double[] rv1 = new double[n];
        double g = 0.0, scale = 0.0, anorm = 0.0, s = 0.0;
        int l = -1;

        for(int i=0; i<n; i++) {
            l = i+1;
            rv1[i] = scale * g;
            g = s = scale = 0.0;
            if(i < m) {
                for(int k=i; k<m; k++) scale += Math.abs(u.get(k, i));
                if(scale != 0.0) {
                    double iscale = 1.0 / scale;
                    for(int k=i; k<m; k++) {
                        u.scale(k, i, iscale);
                        s += u.get(k, i) * u.get(k, i);
                    }
                    double f = u.get(i, i);
                    g = -Math.signum(f) * Math.sqrt(s);
                    double h = f*g-s;
                    u.set(i, i, f - g);
                    for(int j=l; j<n; j++) {
                        s = 0.0;
                        for(int k=i; k<m; k++) s += u.get(k, i) * u.get(k, j);
                        f = s / h;
                        for(int k=i; k<m; k++) u.add(k, j, f * u.get(k, i));
                    }
                    for(int k=i; k<m; k++) u.scale(k, i, scale);
                }
            }
            w[i] = scale * g;
            g = s = scale = 0.0;
            if(i < m && i != n-1) {
                for(int k=l; k<n; k++) scale += Math.abs(u.get(i, k));
                if(scale != 0.0) {
                    double iscale = 1.0 / scale;
                    for(int k=l; k<n; k++) {
                        u.scale(i, k, iscale);
                        s += u.get(i, k) * u.get(i, k);
                    }
                    double f = u.get(i, l);
                    g = -Math.signum(f) * Math.sqrt(s);
                    double h = f * g - s;
                    u.set(i, l, f - g);
                    for(int k=l; k<n; k++) rv1[k] = u.get(i, k) / h;
                    for(int j=l; j<m; j++) {
                        s = 0.0;
                        for(int k=l; k<n; k++) s += u.get(j, k) * u.get(i, k);
                        for(int k=l; k<n; k++) u.add(j, k, s * rv1[k]);
                    }
                    for(int k=l; k<n; k++) u.scale(i, k, scale);
                }
            }
            anorm = Math.max(anorm, Math.abs(w[i]) + Math.abs(rv1[i]));
        }

        for(int i=n; --i >= 0; ) {
            if(i < n-1) {
                if(g != 0.0) {
                    for(int j=l; j<n; j++) v.set(j, i, (u.get(i, j) / u.get(i, l)) / g);
                    for(int j=l; j<n; j++) {
                        s = 0.0;
                        for(int k=l; k<n; k++) s += u.get(i, k) * v.get(k, j);
                        for(int k=l; k<n; k++) v.add(k, j, s * v.get(k, i));
                    }
                }
                for(int j=l; j<n; j++) {
                    v.clear(i, j);
                    v.clear(j, i);
                }
            }
            v.set(i, i, 1.0);
            g = rv1[i];
            l = i;
        }
        for(int i = Math.min(m,n); --i >= 0; ) {
            l = i+1;
            g = w[i];
            for(int j=l; j<n; j++) u.clear(i, j);
            if(g != 0.0) {
                g=1.0/g;
                for(int j=l; j<n; j++) {
                    s = 0.0;
                    for(int k=l; k<m; k++) s += u.get(k, i) * u.get(k, j);
                    double f=(s / u.get(i, i)) * g;
                    for(int k=i; k<m; k++) u.add(k, j, f * u.get(k, i));
                }
                for(int j=i; j<m; j++) u.scale(j, i, g);
            } 
            else for(int j=i; j<m; j++) u.clear(j, i);
            u.add(i, i, 1.0);
        }
        for(int k=n; --k >= 0; ) {
            for(int its=1; its <= maxIterations; its++) {
                int flag = 1;
                int nm = -1;
                for(l=k; l >= 0; l--) {
                    nm = l-1;
                    if(Math.abs(rv1[l]) + anorm == anorm) {
                        flag = 0;
                        break;
                    }
                    if(Math.abs(w[nm]) + anorm == anorm) break;
                }
                if(flag != 0) {
                    double c=0.0;
                    s=1.0;
                    for(int i=l; i<=k; i++) {
                        double f = s * rv1[i];
                        rv1[i] = c * rv1[i];
                        if(Math.abs(f) + anorm == anorm) break;
                        g = w[i];
                        double h = ExtraMath.hypot(f,g);
                        w[i] = h;
                        h = 1.0 / h;
                        c = g * h;
                        s = -f * h;
                        for(int j=m; --j >= 0; ) {
                            double y = u.get(j, nm);
                            double z = u.get(j, i);
                            u.set(j, nm, y*c + z*s);
                            u.set(j, i, z*c - y*s);
                        }
                    }
                }
                double z = w[k];
                if(l == k) {
                    if(z < 0.0) {
                        w[k] = -z;
                        for(int j=n; --j >= 0; ) v.scale(j, k, -1.0);
                    }
                    break;
                }
                if(its >= maxIterations) throw new ConvergenceException("SVD did not converge in " + its + " steps.");
                double x = w[l];
                nm = k-1;
                double y = w[nm];
                g = rv1[nm];
                double h = rv1[k];
                double f = ((y-z)*(y+z) + (g-h)*(g+h)) / (2.0 * h * y);
                g = ExtraMath.hypot(f, 1.0);
                f = ((x-z)*(x+z) + h*((y / (f + Math.signum(f)*g)) - h)) / x;
                double c = s = 1.0;

                for (int j=l; j<=nm; j++) {
                    int i = j+1;
                    g = rv1[i];
                    y = w[i];
                    h = s * g;
                    g = c * g;
                    z = ExtraMath.hypot(f,h);
                    rv1[j] = z;
                    c = f / z;
                    s = h / z;
                    f = x*c + g*s;
                    g = g*c - x*s;
                    h = y * s;
                    y *= c;

                    for(int jj=n; --jj >= 0; ) {
                        x = v.get(jj, j);
                        z = v.get(jj, i);
                        v.set(jj, j, x*c + z*s);
                        v.set(jj, i, z*c - x*s);
                    }

                    z = ExtraMath.hypot(f,h);
                    w[j] = z;

                    if(z != 0.0) {
                        z = 1.0 / z;
                        c = f * z;
                        s = h * z;
                    }

                    f = c*g + s*y;
                    x = c*y - s*g;

                    for(int jj=m; --jj >= 0; ) {
                        y=u.get(jj, j);
                        z=u.get(jj, i);
                        u.set(jj, j, y*c + z*s);
                        u.set(jj, i, z*c - y*s);
                    }
                }
                rv1[l] = 0.0;
                rv1[k] = f;
                w[k] = x;
            }
        }
    }
}

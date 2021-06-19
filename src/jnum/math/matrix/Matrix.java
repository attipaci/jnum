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


import java.util.*;

import jnum.ExtraMath;
import jnum.ShapeException;
import jnum.ViewableAsDoubles;
import jnum.data.ArrayUtil;
import jnum.data.IndexedValues;
import jnum.data.fitting.ConvergenceException;
import jnum.data.image.Index2D;
import jnum.math.MathVector;
import jnum.util.HashCode;

import java.text.*;


public class Matrix extends AbstractMatrix<Double> implements ViewableAsDoubles, IndexedValues<Index2D, Double> {

    private static final long serialVersionUID = 1648081664701964671L;

    private double[][] data; 

    public Matrix(double[][] a) throws ShapeException { 
        checkShape(data);
        data = a; 
    }


    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }

    public Matrix(int size) {
        this(size, size);
    }


    public Matrix(String text, ParsePosition pos) throws ParseException, Exception {
        data = (double[][]) ArrayUtil.parse(text.substring(pos.getIndex()), getElementType());
    }


    @Override
    protected Matrix createMatrix(int rows, int cols, boolean initialize) {
        return new Matrix(rows, cols);
    }
    
    @Override
    public Matrix clone() {
        return (Matrix) super.clone();
    }

    @Override
    public Matrix copy(boolean withContent) {
        Matrix copy = clone();
        copy.data = new double[rows()][cols()];
        if(withContent) for(int i=rows(); --i >= 0; ) System.arraycopy(data[i], 0, copy.data[i], 0, cols());
        return copy;
    }

    @Override
    public Matrix copy() { return (Matrix) super.copy(); }
    
    @Override
    protected Double createElement() { 
        throw new UnsupportedOperationException("Cannot create matrix element object for matrix of double type.");
    }

    private void checkShape(double[][] x) throws ShapeException {
        if(x == null) return;
        if(x.length == 0) return;
        int m = x[0].length;
        for(int i=x.length; --i > 0; ) if(x[i].length != m) throw new ShapeException("Matrix has an irregular non-rectangular shape!");    
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.sampleFrom(data);
    }


    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Matrix)) return false;

        return Arrays.equals(data, ((Matrix) o).data);
    }


    @Override
    public final Class<Double> getElementType() { return double.class; }


    @Override
    public Object getData() { return data; }


    @Override
    public void setData(Object data) {
        if(data instanceof double[][]) setData((double[][]) data);
        else throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + "into double[][] format.");	
    }


    public void setData(double[][] data) throws ShapeException {
        assertSize(data.length, data[0].length);
        checkShape(data);
        this.data = data;
    }
    
    
    public void copyData(Object data) {
        if(data instanceof double[][]) copyData((double[][]) data);
        else if(data instanceof float[][]) copyData((float[][]) data);
        else if(data instanceof ViewableAsDoubles) copyData(((ViewableAsDoubles) data).viewAsDoubles());
        else throw new IllegalArgumentException(" Cannot convert " + data.getClass().getSimpleName() + " into double[][] format.");    
    }


    public void copyData(double[][] data) {
        assertSize(data.length, data[0].length);
        try { setData((double[][]) ArrayUtil.copyOf(data)); } catch (Exception e) { e.printStackTrace(); }
    }

    
    public void copyData(float[][] data) {
        assertSize(data.length, data[0].length);
        setData((double[][]) ArrayUtil.asDouble(data));		
    }

    @Override
    public final int cols() { return data[0].length; }

    @Override
    public final int rows() { return data.length; }

    @Override
    public final Double get(int row, int col) { return data[row][col]; }

    @Override
    protected final Double copyOf(int row, int col) { return get(row, col); }
    
    @Override
    public void clear(int i, int j) { data[i][j] = 0.0; }
    
    @Override
    public final void set(int row, int col, Double v) { data[row][col] = v; }    
    
    @Override
    public final void set(Index2D idx, Number v) { set(idx, v.doubleValue()); }
    
    @Override
    public void add(int i, int j, Double increment) { data[i][j] += increment; }
    
    @Override
    public final void add(Index2D idx, Number increment) { add(idx, increment.doubleValue()); }
    
    @Override
    public void addScaled(int i, int j, Double increment, double scaling) { data[i][j] += increment * scaling; }

    @Override
    public void scale(int i, int j, double factor) { data[i][j] *= factor; }

    @Override
    public void multiplyBy(int i, int j, Double factor) { data[i][j] *= factor; }

    
    @Override
    protected void addProduct(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {	
        assertSize(A.rows(), B.cols());

        for(int i=A.rows(); --i >= 0; ) {
            final double[] row = data[i];

            for(int k=A.cols(); --k >= 0; ) {
                final double a = A.get(i, k);
                if(a == 0.0) continue;

                for(int j=B.cols(); --j >= 0; ) {
                    final double b = B.get(k, j);
                    if(b == 0.0) continue;
                    row[j] += a * b;
                }
            }

        }
    }

    public static Matrix product(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {
        Matrix product = new Matrix(A.rows(), B.cols());
        product.setProduct(A, B);
        return product;
    }


    @Override
    public Matrix dot(AbstractMatrix<? extends Double> B) {
        return (Matrix) super.dot(B);
    }
    
    public double[] dot(double[] v) {
        double[] result = new double[rows()];
        dot(v, result);
        return result;
    }


    public void dot(double[] v, double[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        // TODO parallelize on i;
        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
            result[i] = sum;
        }
    }


    public float[] dot(float[] v) {
        float[] result = new float[rows()];
        dot(v, result);
        return result;
    }


    public void dot(float[] v, float[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
            result[i] = (float) sum;
        }
    }


    public RealVector dot(MathVector<Double> v) {
        RealVector result = new RealVector(rows());
        dot(v, result);
        return result;
    }


    public void dot(MathVector<Double> v, MathVector<Double> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) {
                double c = v.getComponent(j);
                if(c != 0.0) sum += row[j] * v.getComponent(j);
            }
            result.setComponent(i, sum);
        }
    }


    @Override
    public Matrix getTranspose() {		
        return (Matrix) super.getTranspose();
    }

    
    @Override
    public void zero() {
        if(data != null) for(double[] row : data) Arrays.fill(row, 0.0);
    }


    @Override
    public void addScaled(AbstractMatrix<? extends Double> o, double factor) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] += o.get(i, j) * factor;
    }


    @Override
    public void subtract(AbstractMatrix<? extends Double> o) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] -= o.get(i, j);
    }


    @Override
    public void add(AbstractMatrix<? extends Double> o) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] += o.get(i, j);		
    }


    @Override
    public void scale(double factor) {
        for(double[] row : data) for(int j=cols(); --j >= 0; ) row[j] *= factor;
    }


    @Override
    public double distanceTo(AbstractMatrix<? extends Double> o) {
        double d2 = 0.0;
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            double d = data[i][j] - o.get(i, j);
            d2 += d*d;
        }
        return Math.sqrt(d2);
    }


    // The b[] are vectors for which to solve for Ax = b
    // the matrix is inverted, and the solution vectors are returned in their place

    // Based on Numerical Recipes in C (Press et al. 1989)
    @Override
    public void gaussJordan() {
        final int rows = rows();
        final int cols = cols();

        final int[] indxc = new int[rows];
        final int[] indxr = new int[rows];
        final int[] ipiv = new int[rows];

        Arrays.fill(ipiv, -1);

        for(int i=rows; --i >= 0; ) {
            int icol=-1, irow=-1;
            double big=0.0;
            for(int j=rows; --j >= 0; ) if(ipiv[j] != 0) for(int k=rows; --k >= 0; ) {
                if(ipiv[k] == -1) {
                    if(Math.abs(data[j][k]) >= big) {
                        big=Math.abs(data[j][k]);
                        irow=j;
                        icol=k;
                    }
                } 
                else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular PrimitiveMatrix-1 during Gauss-Jordan elimination.");
            }
            ++(ipiv[icol]);
            if(irow != icol) for(int l=cols; --l >= 0; ) {
                double temp = data[irow][l];
                data[irow][l] = data[icol][l];
                data[icol][l] = temp;
            }

            indxr[i]=irow;
            indxc[i]=icol;
            if(data[icol][icol] == 0.0) throw new IllegalArgumentException("Singular PrimitiveMatrix-2 during Gauss-Jordan elimination.");
            double pivinv=1.0 / data[icol][icol];
            data[icol][icol] = 1.0;
            scaleRow(icol, pivinv);

            for(int ll=rows; --ll >= 0; ) if(ll != icol) {
                double temp=data[ll][icol];
                data[ll][icol] = 0.0;
                addMultipleOfRow(icol, -temp, ll);
            }
        }

        for(int l=rows; --l >=0; ) if(indxr[l] != indxc[l]) for(int k=rows; --k >= 0; ) {
            double temp = data[k][indxr[l]];
            data[k][indxr[l]] = data[k][indxc[l]];
            data[k][indxc[l]] = temp;
        }
    }


    // A = U * diag(w) * V^T
    // square A --> A^-1 = V * diag(1/w) * U^T
    // Based on Numerical Recipes in C (Press et al. 1989)
    protected void SVD(double[] w, Matrix V) {
        SVD(w, V, 100);
    }


    protected void SVD(double[] w, Matrix V, int maxIterations) {
        final int m = rows();
        final int n = cols();

        double[] rv1 = new double[n];
        double g = 0.0, scale = 0.0, anorm = 0.0, s = 0.0;
        int l = -1;

        for(int i=0; i<n; i++) {
            l = i+1;
            rv1[i] = scale * g;
            g = s = scale = 0.0;
            if(i < m) {
                for(int k=i; k<m; k++) scale += Math.abs(data[k][i]);
                if(scale != 0.0) {
                    for(int k=i; k<m; k++) {
                        data[k][i] /= scale;
                        s += data[k][i]*data[k][i];
                    }
                    double f = data[i][i];
                    g = -Math.signum(f) * Math.sqrt(s);
                    double h = f*g-s;
                    data[i][i] = f - g;
                    for(int j=l; j<n; j++) {
                        s = 0.0;
                        for(int k=i; k<m; k++) s += data[k][i] * data[k][j];
                        f = s / h;
                        for(int k=i; k<m; k++) data[k][j] += f * data[k][i];
                    }
                    for(int k=i; k<m; k++) data[k][i] *= scale;
                }
            }
            w[i] = scale * g;
            g = s = scale = 0.0;
            if(i < m && i != n-1) {
                for(int k=l; k<n; k++) scale += Math.abs(data[i][k]);
                if(scale != 0.0) {
                    for(int k=l; k<n; k++) {
                        data[i][k] /= scale;
                        s += data[i][k]*data[i][k];
                    }
                    double f = data[i][l];
                    g = -Math.signum(f) * Math.sqrt(s);
                    double h = f * g - s;
                    data[i][l] = f - g;
                    for(int k=l; k<n; k++) rv1[k]=data[i][k] / h;
                    for(int j=l; j<m; j++) {
                        s = 0.0;
                        for(int k=l; k<n; k++) s += data[j][k] * data[i][k];
                        for(int k=l; k<n; k++) data[j][k] += s * rv1[k];
                    }
                    for(int k=l; k<n; k++) data[i][k] *= scale;
                }
            }
            anorm = Math.max(anorm, Math.abs(w[i]) + Math.abs(rv1[i]));
        }

        for(int i=n; --i >= 0; ) {
            if(i < n-1) {
                if(g != 0.0) {
                    for(int j=l; j<n; j++) V.data[j][i] = (data[i][j] / data[i][l]) / g;
                    for(int j=l; j<n; j++) {
                        s = 0.0;
                        for(int k=l; k<n; k++) s += data[i][k] * V.data[k][j];
                        for(int k=l; k<n; k++) V.data[k][j] += s * V.data[k][i];
                    }
                }
                for(int j=l; j<n; j++) V.data[i][j] = V.data[j][i] = 0.0;
            }
            V.data[i][i] = 1.0;
            g = rv1[i];
            l = i;
        }
        for(int i = Math.min(m,n); --i >= 0; ) {
            l = i+1;
            g = w[i];
            for(int j=l; j<n; j++) data[i][j] = 0.0;
            if(g != 0.0) {
                g=1.0/g;
                for(int j=l; j<n; j++) {
                    s = 0.0;
                    for(int k=l; k<m; k++) s += data[k][i] * data[k][j];
                    double f=(s / data[i][i]) * g;
                    for(int k=i; k<m; k++) data[k][j] += f * data[k][i];
                }
                for(int j=i; j<m; j++) data[j][i] *= g;
            } 
            else for(int j=i; j<m; j++) data[j][i] = 0.0;
            data[i][i]++;
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
                            double y = data[j][nm];
                            double z = data[j][i];
                            data[j][nm] = y*c + z*s;
                            data[j][i] = z*c - y*s;
                        }
                    }
                }
                double z = w[k];
                if(l == k) {
                    if(z < 0.0) {
                        w[k] = -z;
                        for(int j=n; --j >= 0; ) V.data[j][k] *= -1;
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
                        x = V.data[jj][j];
                        z = V.data[jj][i];
                        V.data[jj][j] = x*c + z*s;
                        V.data[jj][i] = z*c - x*s;
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
                        y=data[jj][j];
                        z=data[jj][i];
                        data[jj][j] = y*c + z*s;
                        data[jj][i] = z*c - y*s;
                    }
                }
                rv1[l] = 0.0;
                rv1[k] = f;
                w[k] = x;
            }
        }
    }


    @Override
    public void getColumnTo(int j, Object buffer) {
        if(buffer instanceof double[]) getColumnTo(j, (double[]) buffer);
        else if(buffer instanceof float[]) getColumnTo(j, (float[]) buffer);
        else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
    }


    public void getColumnTo(int j, double[] buffer) {
        for(int i=rows(); --i >= 0; ) buffer[i] = data[i][j];		
    }


    public void getColumnTo(int j, float[] buffer) {
        for(int i=rows(); --i >=0; ) buffer[i] = (float) data[i][j];		
    }


    @Override
    public void getRowTo(int j, Object buffer) {
        if(buffer instanceof double[]) getRowTo(j, (double[]) buffer);
        else if(buffer instanceof float[]) getRowTo(j, (float[]) buffer);
        else throw new IllegalArgumentException(" Cannot get " + getClass().getSimpleName() + " column into  " + buffer.getClass().getSimpleName() + ".");
    }

    
    @Override
    public final double[] getRow(int i) { return data[i]; }

    public void getRowTo(int i, double[] buffer) {
        for(int j=cols(); --j >= 0; ) buffer[j] = data[i][j];		
    }


    public void getRowTo(int i, float[] buffer) {
        for(int j=cols(); --j >= 0; ) buffer[j] = (float) data[i][j];		
    }


    @Override
    public void setColumn(int j, Object value) throws IllegalArgumentException {		
        if(value instanceof double[]) setColumn(j, (double[]) value);
        else if(value instanceof float[]) setColumn(j, (float[]) value);
        else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " column.");
    }

    public void setColumn(int j, double[] value) throws ShapeException {
        if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
        for(int i=rows(); --i >= 0; ) data[i][j] = value[i];		
    }


    public void setColumn(int j, float[] value) throws ShapeException {
        if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
        for(int i=rows(); --i >= 0; ) data[i][j] = value[i];		
    }


    @Override
    public void setRow(int j, Object value) throws IllegalArgumentException {		
        if(value instanceof double[]) setRow(j, (double[]) value);
        else if(value instanceof float[]) setRow(j, (float[]) value);
        else throw new IllegalArgumentException(" Cannot use " + value.getClass().getSimpleName() + " to specify " + getClass().getSimpleName() + " row.");
    }


    public void setRow(int i, double[] value) throws ShapeException {
        if(value.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
        data[i] = value;
    }


    public void setRow(int i, float[] value) throws ShapeException {
        if(value.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
        for(int j=cols(); --j >= 0; ) data[i][j] = value[j];
    }


    @Override
    public final void swapRows(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;	
    }


    @Override
    public final void swapElements(int i1, int j1, int i2, int j2) {
        double temp = data[i1][j1];
        data[i1][j1] = data[i2][j2];
        data[i2][j2] = temp;	
    }

    @Override
    public void addMultipleOfRow(int row, double scaling, int toRow) {
        for(int j=cols(); --j >= 0; ) data[toRow][j] += scaling * data[row][j];
    }


    @Override
    public void addMultipleOfRow(int row, Double scaling, int toRow) {
        addMultipleOfRow(row, scaling.doubleValue(), toRow);
    }


    @Override
    public void addRow(int row, int toRow) {
        for(int j=cols(); --j >= 0; ) data[toRow][j] += data[row][j];	
    }


    @Override
    public void subtractRow(int row, int fromRow) {
        for(int j=cols(); --j >= 0; ) data[fromRow][j] -= data[row][j];		
    }


    @Override
    public void zeroRow(int i) {
        Arrays.fill(data[i], 0.0);
    }


    @Override
    public void scaleRow(int i, double factor) {
        for(int j=cols(); --j >= 0; ) data[i][j] *= factor;
    }


    @Override
    public void scaleRow(int i, Double factor) {
        scaleRow(i, factor.doubleValue());
    }


    public void offset(Double value) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] += value;
    }


    @Override
    public int getRank() {
        Matrix copy = copy();
        copy.gauss();
        int rank = 0;
        int col = 0;
        for(int i=0; i<rows(); i++) {
            double[] row = copy.data[i];
            for(int j=col; j<cols(); j++) {
                if(row[j] != 0.0) {
                    col = j+1;
                    break;
                }
            }
        }

        return rank;
    }


    @Override
    public VectorBasis getBasis() {
        VectorBasis basis = new VectorBasis();
        Matrix copy = copy();
        copy.gauss();
        int col = 0;
        for(int i=0; i<rows(); i++) {
            double[] row = copy.data[i];
            for(int j=col; j < cols(); j++) {
                if(row[j] != 0.0) {
                    RealVector v = new RealVector(cols());
                    getColumnTo(j, v.getData());
                    basis.add(v);
                    col = j+1;
                    break;
                }
            }
        }
        return basis;
    }


    @Override
    public boolean isNullRow(int i) {
        for(int j=cols(); --j >= 0; ) if(data[i][j] != 0.0) return false;
        return true;
    }


    @Override
    public String toString() {
        return getIDString() + ":\n" + ArrayUtil.toString(data);
    }


    @Override
    public String toString(NumberFormat nf) {
        return getIDString() + ":\n" + ArrayUtil.toString(data, nf);
    }


    @Override
    public String toString(int decimals) {
        return getIDString() + ":\n" + ArrayUtil.toString(data, decimals);
    }


    @Override
    public void setSum(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
        if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");			
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] = a.get(i, j) + b.get(i,  j);
    }


    @Override
    public void setDifference(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
        if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] = a.get(i, j) + b.get(i,  j);
    }


    @Override
    public Matrix getInverse() {
        return getLUInverse();
    }


    public Matrix getLUInverse() {
        return getLUDecomposition().getInverseMatrix();
    }


    public Matrix getSVDInverse() {
        return new SVD(this).getInverseMatrix();
    }

    // Invert via Gauss-Jordan elimination
    public Matrix getGaussInverse() {
        if(!isSquare()) throw new SquareMatrixException();

        int size = rows();
        Matrix combo = new Matrix(size, 2*size);
        for(int i=size; --i >= 0; ) combo.data[i][i+size] = 1.0;
        combo.paste(this, 0, 0);
        combo.gaussJordan();
        Matrix inverse = new Matrix((double[][]) ArrayUtil.subArray(combo.data, new int[] { 0, size }, new int[] { size, 2*size }));
        return inverse;
    }
    

    @Override
    public void invert() {
        data = getInverse().data;
    }


    @Override
    public void addIdentity(double scaling) {
        if(!isSquare()) throw new SquareMatrixException();
        for(int i=rows(); --i >= 0; ) data[i][i] += scaling;
    }

    @Override
    public LUDecomposition getLUDecomposition() {
        return new LUDecomposition(this);
    }
    
    
    @Override
    public int compare(Number a, Number b) {
        return Double.compare(a.doubleValue(), b.doubleValue());
    }

    
    @Override
    public Object viewAsDoubles() {
        return data;
    }


    @Override
    public void viewAsDoubles(Object view) throws IllegalArgumentException {
        if(!(view instanceof double[][])) throw new IllegalArgumentException("Expecting double[][], but got " + view.getClass().getSimpleName() + " instead.");
        double[][] d = (double[][]) view;
        assertSize(d.length, d[0].length);
        for(int i=rows(); --i>=0; ) System.arraycopy(data[i], 0, d[i], 0, cols());
    }


    @Override
    public void createFromDoubles(Object view) throws IllegalArgumentException {
        if(!(view instanceof double[][])) throw new IllegalArgumentException("Expecting double[][], but got " + view.getClass().getSimpleName() + " instead.");
        setData((double[][]) view);
    }
    

    public final static Matrix identity() {
        return new Matrix(new double[][] {{1.0}});
    }

    public final static Matrix identity(int size) {
        Matrix I = new Matrix(size);
        I.addIdentity(1.0);
        return I;
    }


}

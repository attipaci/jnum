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

import jnum.Copiable;
import jnum.ViewableAsDoubles;
import jnum.data.ArrayUtil;
import jnum.data.IndexedValues;
import jnum.data.fitting.ConvergenceException;
import jnum.data.image.Index2D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.Complex;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;
import jnum.math.SymmetryException;
import jnum.math.Vector2D;
import jnum.math.Vector3D;
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
    public Double newEntry() { 
        throw new UnsupportedOperationException("Cannot create matrix element object for matrix of double type.");
    }

    @Override
    public Element getElementInstance() {
        return new Element(0.0);
    }
   
    @Override
    public RealVector getVectorInstance(int size) {
        return new RealVector(size);
    }
    
    @Override
    public VectorBasis getVectorBasisInstance() {
        return new VectorBasis();
    }
    
    @Override
    public Matrix getMatrixInstance(int rows, int cols, boolean initialize) {
        return new Matrix(rows, cols);
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
    public double[][] getData() { return data; }


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
    public final Double copyOf(int row, int col) { return get(row, col); }

    @Override
    public void clear(int i, int j) { data[i][j] = 0.0; }

    @Override
    public final void set(int row, int col, Double v) { data[row][col] = v; }    

    @Override
    public final void set(Index2D idx, Number v) { set(idx, v.doubleValue()); }
    
    @Override
    public Matrix getTranspose() { return (Matrix) super.getTranspose(); }

    public boolean isSymmetric() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j > i; ) if(data[i][j] != data[j][i]) return false;
        return true;
    }

    public boolean isAntiSymmetric() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j > i; ) if((data[i][j] + data[j][i]) != 0.0) return false;
        return true;
    }

    
    
    @Override
    public void addScaled(AbstractMatrix<? extends Double> o, double factor) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] += o.get(i, j) * factor;
    }


    @Override
    public void subtract(AbstractMatrix<? extends Double> o) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] -= o.get(i, j);
    }


    @Override
    protected void addProduct(AbstractMatrix<? extends Double> A, AbstractMatrix<? extends Double> B) {	
        for(int i=A.rows(); --i >= 0; ) {
            final double[] row = data[i];

            for(int k=A.cols(); --k >= 0; ) {
                final double a = ((Number) A.get(i, k)).doubleValue();
                if(a == 0.0) continue;

                for(int j=B.cols(); --j >= 0; ) {
                    final double b = ((Number) B.get(k, j)).doubleValue();
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


    public Matrix dot(Matrix B) {
        return (Matrix) super.dot(B);
    }

    public <T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue>
    ObjectMatrix<T> dot(ObjectMatrix<T> B) {
        ObjectMatrix<T> P = B.getMatrixInstance(rows(), B.cols(), true);
       
            for(int k=cols(); --k >= 0; ) for(int j=B.cols(); --j >= 0; ) {
                final T b = B.get(j, k);
                if(!b.isNull()) for(int i=rows(); --i >= 0; ) {
                    final double a = get(i, k);
                    if(a != 0.0) { {
                        @SuppressWarnings("unchecked")
                        T p = (T) b.copy();
                        p.scale(a);
                        P.add(i, j, p);
                    }
                }
            }       
        }
        
        return P;
    }
    
    public ComplexMatrix dot(ComplexMatrix B) {
        return (ComplexMatrix) dot((ObjectMatrix<? extends Complex>) B);
    }
    
    @Override
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

    
    @Override
    public Double[] dot(Double[] v) {
        Double[] result = new Double[rows()];
        dot(v, result);
        return result;
    }


    @Override
    public void dot(Double[] v, Double[] result) {
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


    @Override
    public void dot(double[] v, MathVector<Double> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        // TODO parallelize on i;
        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
            result.setComponent(i, sum);
        }
    }

    
    @Override
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
    
    @Override
    public void dot(float[] v, MathVector<Double> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        // TODO parallelize on i;
        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) if(v[j] != 0.0) sum += row[j] * v[j];
            result.setComponent(i, sum);
        }
    }


    @Override
    public RealVector dot(MathVector<Double> v) {
        RealVector result = new RealVector(rows());
        dot(v, result);
        return result;
    }
    
    @Override
    public RealVector dot(RealVector v) {
        return dot((MathVector<Double>) v);
    }


    @Override
    public void dot(MathVector<Double> v, MathVector<Double> result) throws ShapeException {
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
    public void dot(RealVector v, MathVector<Double> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        // TODO parallelize on i;
        for(int i=rows(); --i >= 0; ) {
            final double[] row = data[i];
            double sum = 0.0;
            for(int j=cols(); --j >= 0; ) if(row[j] != 0.0) {
                double vj = v.getComponent(j);
                if(vj != 0.0) sum += row[j] * vj;
            }
            result.setComponent(i, sum);
        }
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
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");			
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] = a.get(i, j) + b.get(i, j);
    }


    @Override
    public void setDifference(AbstractMatrix<? extends Double> a, AbstractMatrix<? extends Double> b) {
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] = a.get(i, j) - b.get(i, j);
    }


    @Override
    public Matrix getInverse() {
        return (Matrix) super.getInverse();
    }

    @Override
    public Matrix getGaussInverse() {
        return (Matrix) super.getGaussInverse();
    }

    @Override
    public Matrix getLUInverse() {
        return (Matrix) super.getLUInverse();
    }


    public Matrix getSVDInverse() {
        return new SVD(this).getInverseMatrix();
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
    
    public JacobiTransform getJacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return new JacobiTransform();
    }
    
    public EigenSystem<Double, ?> getEigenSystem() throws SquareMatrixException, SymmetryException, ConvergenceException {
        return new JacobiTransform();
    }

    @Override
    public LU getLUDecomposition() {
        return new LU();
    }
    
    @Override
    public Gauss getGaussInverter() {
        return new Gauss();
    }

    public SVD getSVD() {
        return new SVD(this);
    }

    @Override
    public int compare(Number a, Number b) {
        return Double.compare(a.doubleValue(), b.doubleValue());
    }
    
    @Override
    public Matrix subspace(int[] rows, int[] cols) {
        return (Matrix) super.subspace(rows, cols);    
    }

    @Override
    public Matrix subspace(int fromRow, int fromCol, int toRow, int toCol) {
        return (Matrix) super.subspace(fromRow, fromCol, toRow, toCol);
    }

    @Override
    public Matrix subspace(Index2D from, Index2D to) {
        return (Matrix) super.subspace(from, to);
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



    @Override
    public void add(Index2D index, Number value) {
        add(index.i(), index.j(), value.doubleValue());
    }


    @Override
    public void add(int row, int col, Double v) {
        data[row][col] += v;
    }

    @Override
    public void add(int row, int col, double v) {
        data[row][col] += v;
    }
    

    @Override
    public void scale(int i, int j, double factor) {
        data[i][j] *= factor;
    }

    
    @Override
    public boolean isNull(int i, int j) {
        return data[i][j] == 0.0;
    }
   

    public final static Matrix identity(int size) {
        Matrix I = new Matrix(size);
        I.addIdentity(1.0);
        return I;
    }    
    

    public class Element extends MatrixElement<Double> {
        double value;

        public Element(double value) {
            this.value =  value;
        }

        @Override
        public Element fresh() {
            value = 0.0;
            return this;
        }

        
        @Override
        public void applyTo(int i, int j) {
            data[i][j] = value;
        }


        @Override
        public Double value() { return value; }


        @Override
        public Element from(Double value) {
            this.value = value;
            return this;
        }

        @Override
        public Element from(int i, int j) {
            value = Matrix.this.get(i, j);
            return this;
        }

        @Override
        public MatrixElement<Double> copy(int i, int j) {
            value = Matrix.this.get(i, j);
            return this;
        }

        @Override
        public MatrixElement<Double> copy(Double value) {
            this.value = value;
            return this;
        }

        
        @Override
        public Double copyOfValue() {
            return value;
        }

        @Override
        public void addScaled(Double o, double factor) {
            value += o * factor;
        }

        @Override
        public void zero() {
            value = 0.0;
        }

        @Override
        public boolean isNull() {
            return value == 0.0;
        }

        @Override
        public void scale(double factor) {
            value *= factor;
        }

        @Override
        public void add(Double o) {
            value += o;
        }

        @Override
        public void subtract(Double o) {
            value -= o;
        }

        @Override
        public void setSum(Double a, Double b) {
            value = a + b;
        }

        @Override
        public void setDifference(Double a, Double b) {
            value = a - b;
        }

        @Override
        public void multiplyBy(Double factor) {
            value *= factor;
        }

        @Override
        public void setProduct(Double a, Double b) {
            value = a * b;
        }

        @Override
        public void setIdentity() {
            value = 1.0;
        }

        @Override
        public Double getInverse() {
            return 1.0 / value;
        }

        @Override
        public void inverse() {
            value = 1.0 / value;
        }

        @Override
        public double abs() {
            return Math.abs(value);
        }

        @Override
        public double absSquared() {
            return value * value;
        }

        @Override
        public double distanceTo(Double point) {
            return Math.abs(point - value);
        }

        @Override
        public double normalize() {
            double l = abs();
            value = Math.signum(value);
            return l;
        }    

    }


    public class VectorBasis extends AbstractVectorBasis<Double> {
        private static final long serialVersionUID = 2039401048091817380L;

        public VectorBasis() {}

        /* (non-Javadoc)
         * @see kovacs.math.AbstractVectorBasis#asMatrix()
         */
        @Override
        public Matrix asMatrix() {
            Matrix M = new Matrix(size());
            asMatrix(M);
            return M;
        }

        @Override
        public AbstractVector<Double> getVectorInstance(int size) {
            return new RealVector(size);
        }
    }


    public class LU extends LUDecomposition<Double> implements RealMatrixSolver {

        public LU() { super(Matrix.this); }

        public LU(double tinyValue) {
            super(Matrix.this, tinyValue);
        }

        @Override
        public Matrix getMatrix() { return (Matrix) super.getMatrix(); }

        @Override
        public Matrix getInverseMatrix() { return (Matrix) super.getInverseMatrix(); }

        @Override
        public Double getDeterminant() {
            double D = evenChanges ? 1.0 : -1.0;
            for(int i=size(); --i >= 0; ) D *= LU.get(i, i);
            return D;
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

    } 
    
    
    public class Gauss extends GaussInverter<Double> implements RealMatrixSolver {
        
        public Gauss() {
            super(Matrix.this);
        }
        
        @Override
        public Matrix getI() {
            return (Matrix) super.getI();
        }
        
        @Override
        public Matrix getInverseMatrix() {
            return (Matrix) super.getInverseMatrix();
        }

        @Override
        public RealVector solveFor(MathVector<Double> y) {
            return getI().dot(y);
        }
        
        @Override
        public double[] solveFor(double[] y) {
            return getI().dot(y);
        }

        @Override
        public void solveFor(double[] y, double[] x) {
            getI().dot(y, x);
        }

        @Override
        public RealVector solveFor(RealVector y) {
            return getI().dot(y);
        }

        @Override
        public void solveFor(RealVector y, RealVector x) {
            getI().dot(y, x);
        }   
    }

    
    public class JacobiTransform implements EigenSystem<Double, Double> {
        private RealVector[] eigenVectors;
        private double[] d;
        
        public JacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {          
            this(100);   
        }
        
        public JacobiTransform(int maxIterations) throws SquareMatrixException, SymmetryException, ConvergenceException {          
            transform(Matrix.this, 100);   
        }
        
        @Override
        public RealVector getEigenValues() {
            RealVector l = new RealVector(size());
            System.arraycopy(d, 0, l.getData(), 0, size());
            return l;
        }
        
        @Override
        public RealVector[] getEigenVectors() {
            RealVector[] e = new RealVector[size()];
            for(int i=size(); --i >= 0; ) e[i] = eigenVectors[i].copy();
            return e;
        }
        
        private int size() {
            return d.length;
        }
        
        /**
         * Based on Numerical Recipes in C (second edition) jacobi() routine.
         * 
         * 
         * @param M                         Matrix to transform
         * @param maxIterations             Maximum number of iterations (~100 is typically sufficient)
         * @return      Number of rotation performed.
         * @throws SquareMatrixException    If the input matrix is not a square matrix
         * @throws SymmetryException        If the input matrix is not a symmetric matrix
         * @throws ConvergenceException     If the transformation is not complete within the set ceiling for iterations.
         */
        private int transform(Matrix M, int maxIterations) throws SquareMatrixException, SymmetryException, ConvergenceException {
            if(!M.isSquare()) throw new SquareMatrixException();
            if(!M.isSymmetric()) throw new SymmetryException();
            
            final int n = M.rows();
            
            d = new double[n];
            Matrix A = Matrix.this.copy();
            Matrix v = A.getMatrixInstance(n, n, true);
            v.addIdentity();
            
            final double[] b = new double[n];
            final double[] z = new double[n];
           
            for(int i = 0; i < n; i++) b[i] = d[i] = A.get(i, i);

            Rotation r = new Rotation();
            int nrot = 0;
            
            for(int k=0; k < maxIterations; k++) {
                double sum = 0.0;
                
                for (int i = 0; i < n-1; i++) for (int j = i+1; j < n; j++) sum += Math.abs(A.get(i, j));
                
                if(sum == 0.0) {
                    eigenVectors = new RealVector[size()];

                    for(int j=size(); --j >= 0; ) {
                        RealVector ej = new RealVector(size());
                        v.getColumnTo(j, ej.getData());
                        eigenVectors[j] = ej;
                    }

                    return nrot;
                }
                
                final double tresh = (k < 4) ? 0.2 * sum / (n * n) : 0.0;

                for(int i = 0; i < n-1; i++) for(int j = i+1; j < n; j++) {
                    final double g = 100.0 * Math.abs(A.get(i, j));
                 
                    if(k > 4 && (Math.abs(d[i]) + g) == Math.abs(d[i]) && (Math.abs(d[j]) + g) == Math.abs(d[j])) 
                        A.clear(i, j);
                    
                    else if (Math.abs(A.get(i, j)) < tresh) continue;

                    double h = d[j] - d[i];
                    double t = 0.0;
                    
                    if((Math.abs(h)+g) == Math.abs(h)) t = A.get(i, j) / h;
                    else {
                        double theta = 0.5 * h / (A.get(i, j));
                        t = 1.0 / (Math.abs(theta) + Math.sqrt(1.0 + theta*theta));
                        if (theta < 0.0) t = -t;
                    }
                    
                    final double c = 1.0 / Math.sqrt(1.0 + t*t);

                    r.s = t * c;
                    r.tau = r.s / (1.0 + c);

                    h = t * A.get(i, j);

                    z[i] -= h;
                    z[j] += h;
                    d[i] -= h;
                    d[j] += h;

                    A.clear(i, j);

                    for(int m = 0; m < i; m++) r.rotate(A, m, i, m, j);
                    for(int m = i+1; m < j; m++) r.rotate(A, i, m, m, j);
                    for(int m = j+1; m < n; m++) r.rotate(A, i, m, j, m);
                    for(int m = 0; m < n; m++) r.rotate(v, m, i, m, j);
                    
                    nrot++;
                }
                
                for(int ip = n; --ip >= 0; ) {
                    b[ip] += z[ip];
                    d[ip] = b[ip];
                    z[ip] = 0.0;
                }                
            }
            throw new ConvergenceException("Too many iterations");
        }
        
        private class Rotation {
            double s, tau;
            
            void rotate(Matrix M, int i, int j, int k, int l) {
                double g = M.get(i, j);
                double h = M.get(k, l);
                M.add(i,  j, -s * (h + g * tau));
                M.add(k,  l, s * (g - h * tau));
            }
        }
    }

    
}


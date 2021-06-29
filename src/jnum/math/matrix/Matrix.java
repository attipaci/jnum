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

/**
 * A matrix object containing real valued elements. The matrix is backed by a <code>double[][]</code> array storage.
 * 
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 */
public class Matrix extends AbstractMatrix<Double> implements ViewableAsDoubles, IndexedValues<Index2D, Double> {

    private static final long serialVersionUID = 1648081664701964671L;

    private double[][] data; 

    /**
     * Constructor with an underlying data array. The Matrix will be created referencing the supplied
     * array. As such any changes to the array's data will be reflected in the matrix.
     * 
     * @param a         2D array with to use as the matrix's backing storage.
     * @throws ShapeException       If the the supplied 2D array has an irregular (no-retangular) shape
     *                              with rows of different lengths.
     */
    public Matrix(double[][] a) throws ShapeException { 
        checkShape(data);
        data = a; 
    }

    
    /**
     * Constructor for a matrix with a fixed number of rows and columns. The matrix is created
     * containing all zeroes.
     * 
     * @param rows  Number of rows in matrix
     * @param cols  Number of columns in matrix
     */
    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }

    /** 
     * Constructor for a square matrix with the same number of rows and columns. The matrix
     * is creted with all zeroes.
     * 
     * @param size      The number of rows & columns in the matrix.
     */
    public Matrix(int size) {
        this(size, size);
    }


    /**
     * Constructor for a matrix from a text input. The input must be properly formatted for
     * 2D data, or else a {@link ParseException} will be thrown.
     * 
     * @param text          The input text containing the textual representation of a 2D array
     * @param pos           The position from where to parse the string. The position is updated
     *                      to the end position tht as been successfully parsed. 
     * @throws ParseException   If the textual dat could not be parsed into a proper 2D matrix.
     * @throws Exception    
     */
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


    /**
     * Changes the underlying backing storage to the specified 2D array. However, the new data cannot
     * be of a different size than the existing matrix.
     * 
     * @param data              The new backing data storage for this matrix.
     * @throws ShapeException   If the shape and/or size of the data object differs from that of this matrix
     */
    public void setData(double[][] data) throws ShapeException {
        assertSize(data.length, data[0].length);
        checkShape(data);
        this.data = data;
    }

    /**
     * Copies the data from the <code>double[][]</code> representation of some object with the same
     * size and shape as this matrix.
     * 
     * @param data      The <code>double[][]</code> representation of the new data.
     * @throws ShapeException   If the shape and/or size of the data object differs from that of this matrix
     */
    public void copyDataFrom(ViewableAsDoubles data) throws ShapeException {
        copyDataFrom((double[][]) data.viewAsDoubles());   
    }

    /**
     * Copies the data from another 2D array of the same size and shape as this matrix. The matrix will
     * be fully decoupled from the argument array, that is subsequent changes to the array's rows or
     * elements will not affect the matrix in any way, and vice-versa. 
     * 
     * @param data              The array that defines the new matrix elements.
     * @throws ShapeException   If the shape and/or size of the data object differs from that of this matrix
     */
    public void copyDataFrom(double[][] data) throws ShapeException {
        assertSize(data.length, data[0].length);
        try { setData((double[][]) ArrayUtil.copyOf(data)); } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Copies the data from another 2D array of the same size and shape as this matrix. The matrix will
     * be fully decoupled from the argument array, that is subsequent changes to the array's rows or
     * elements will not affect the matrix in any way, and vice-versa. 
     * 
     * @param data              The array that defines the new matrix elements.
     * @throws ShapeException   If the shape and/or size of the data object differs from that of this matrix
     */
    public void copyDataFrom(float[][] data) {
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

    /**
     * Checks if this matrix is symmetric, that is the element at (i, j) equals the element at (j, i) for all 
     * i, j indices in this matrix. Symmetric matrices can be Jacobi transformed and have all real eigenvalues.
     * 
     * @return  true is this matric is symmetric. Otherwise false.
     */
    public boolean isSymmetric() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j > i; ) if(data[i][j] != data[j][i]) return false;
        return true;
    }

    /**
     * Checks if this matric is anti-symmetric, thais is the element at (i, j) is the negative of the
     * element at (j, i).
     *  
     * @return  true if this matrix is anti-symmetric. Otherwise false.
     */
    public boolean isAntiSymmetric() {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j > i; ) if((data[i][j] + data[j][i]) != 0.0) return false;
        return true;
    }
    
    @Override
    public void addScaled(MatrixAlgebra<?, ? extends Double> o, double factor) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] += o.get(i, j) * factor;
    }


    @Override
    public void subtract(MatrixAlgebra<?, ? extends Double> o) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] -= o.get(i, j);
    }


    @Override
    protected void addProduct(MatrixAlgebra<?, ? extends Double> A, MatrixAlgebra<?, ? extends Double> B) {	
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

    /**
     * Gets the dot product of this matrix (M) with the matrix of the argument.
     * 
     * @param B     The right-hand side matrix of the dot product
     * @return      The (M dot B) product, where M is this matrix.
     */
    public Matrix dot(Matrix B) {
        return (Matrix) super.dot(B);
    }

    @Override
    public Matrix dot(DiagonalMatrix<Double> B) {
        return (Matrix) B.dotFromLeft(this);
    }
    
    @Override
    public Matrix dot(DiagonalMatrix.Real B) {
        return B.dotFromLeft(this);
    }
 
    /**
     * Gets the dot product of this matrix (M) with the matrix of generic
     * type of the argument.
     * 
     * @param B     The right-hand side generic type matrix of the dot product
     * @return      The (M dot B) product as a generic tpe matrix, where M is this matrix.
     */
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
 
   
    /**
     * Gets the dot product of this matrix (M) with the complex matrix of the argument.
     * 
     * @param B     The right-hand side complex matrix of the dot product
     * @return      The (M dot B) product as a complex matrix, where M is this matrix.
     */
    public ComplexMatrix dot(ComplexMatrix B) {
        return (ComplexMatrix) dot((ObjectMatrix<? extends Complex>) B);
    }
   
    /**
     * Gets the dot product of this matrix with 
     * 
     * @param <T>
     * @param B
     * @return
     */
    public <T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue>
    ObjectMatrix<T> dot(DiagonalMatrix.Generic<T> B) {
        return B.dot(this);
    }
    
    
    
    public <T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue>
    ComplexMatrix dot(DiagonalMatrix.Complex B) {
        return B.dot(this);
    }
    
    
    
    @Override
    public RealVector dot(double[] v) {
        return (RealVector) super.dot(v);
    }
    
    @Override
    public RealVector dot(float[] v) {
        return (RealVector) super.dot(v);
    }

    @Override
    public RealVector dot(RealVector v) {
        return (RealVector) super.dot(v);
    }
    
    @Override
    public RealVector dot(MathVector<? extends Double> v) {
        return (RealVector) super.dot(v);
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
    public void dot(Double[] v, MathVector<Double> result) {
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
    public void dot(MathVector<? extends Double> v, MathVector<Double> result) throws ShapeException {
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
    public double getMagnitude(int fromi, int fromj, int toi, int toj) {
        double mag = 0.0;
     
        for(int i=toi; --i >= fromi; ) for(int j=toj; --j >= toj; ) {
            double a = Math.abs(data[i][j]);
            if(a > mag) mag = a;
        }
        
        return mag;        
    }
    
    @Override
    public Double getTrace() throws SquareMatrixException {
        double sum = 0.0;
        for(int i=rows(); --i >= 0; ) sum += data[i][i];
        return sum;
    }

    @Override
    public boolean isTraceless() throws SquareMatrixException {
        double sum = 0.0, mag = 0.0;
        for(int i=rows(); --i >= 0; ) {
            double v = data[i][i];
            sum += v;
            if(Math.abs(v) > mag) mag = Math.abs(v);
        }
            
        return Math.abs(sum) <= 1e-12 * mag;
    }
    
    
    @Override
    public double distanceTo(MatrixAlgebra<?, ?> o) {
        if(o instanceof DiagonalMatrix) return ((DiagonalMatrix<?>) o).distanceTo(this);
        
        assertSize(o.rows(), o.cols());   
       
        if(Number.class.isAssignableFrom(o.getElementType())) {  
            double d2 = 0.0;
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
                double d = data[i][j] - ((Number) o.get(i, j)).doubleValue();
                d2 += d*d;
            }
            return Math.sqrt(d2);
        }
        
        return o.distanceTo(this);
    }
    
    @Override
    public final void swapRows(int i, int j) {
        double[] rowi = data[i];
        data[i] = data[j];
        data[j] = rowi;
    }

    /**
     * Gets the native row as referenced by this matrix. Any changes to the contents of the
     * returned row will necessarily affect the matrix and vice versa.
     * 
     * @param i     Matrix row index.
     * @return      The underlying array that stored the data of the matrix row.
     */
    public final double[] getRow(int i) {
        return data[i];
    }

    /**
     * Sets the underlying storage of a matrix row to a new array. 
     * 
     * @param i     Matrix row index
     * @param row   The nwew data array for the row.
     */
    public final void setRow(int i, double[] row) {
        if(row.length != cols()) throw new ShapeException("Cannot set mismatched matrix row.");
    }
    
    /**
     * Copies the contents of a matrix row into the supplied buffer.
     * 
     * @param i         Matrix row index whose data is to be retrieved
     * @param buffer    Array into which the column data is copied. The array is not checked for size.
     */
    public void copyRowTo(int i, double[] buffer) {
        for(int j=cols(); --j >= 0; ) buffer[j] = data[i][j];       
    }

    /**
     * Copies the contents of a matrix row into the supplied buffer.
     * 
     * @param i         Matrix row index whose data is to be retrieved
     * @param buffer    Array into which the column data is copied. The array is not checked for size.
     */
    public void copyRowTo(int i, float[] buffer) {
        for(int j=cols(); --j >= 0; ) buffer[j] = (float) data[i][j];       
    }
    

    /**
     * Copies the contents of the supplied array into a matrix row.
     * 
     * @param i         Matrix row index
     * @param v         Array with the new contents for the matrix row.
     */
    public void setRowData(int i, double[] v) throws ShapeException {
        if(v.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
        for(int j=cols(); --j >= 0; ) data[i][j] = v[j];
    }

    /**
     * Copies the contents of the supplied array into a matrix row.
     * 
     * @param i         Matrix row index
     * @param v         Array with the new contents for the matrix row.
     */
    public void setRowData(int i, float[] v) throws ShapeException {
        if(v.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
        for(int j=cols(); --j >= 0; ) data[i][j] = v[j];
    }


    
    /**
     * Copies the contents of a matrix column into the supplied buffer.
     * 
     * @param j         Matrix column index whose data is to be retrieved
     * @param buffer    Array into which the column data is copied. The array is not checked for size.
     */
    public void copyColumnTo(int j, double[] buffer) {
        for(int i=rows(); --i >= 0; ) buffer[i] = data[i][j];		
    }

    /**
     * Copies the contents of a matrix column into the supplied buffer.
     * 
     * @param j         Matrix column index whose data is to be retrieved
     * @param buffer    Array into which the column data is copied. The array is not checked for size.
     */
    public void copyColumnTo(int j, float[] buffer) {
        for(int i=rows(); --i >=0; ) buffer[i] = (float) data[i][j];		
    }
    
    /**
     * Sets the matrix column to the values provided in the argument. The argument itself is not referenced,
     * the primitive values from it are copied. As such subsequent changes to the supplied array after the 
     * call will in no way affect the data in this matrix.
     * 
     * @param j             The index of the column to update
     * @param value         Array containing that data that is to be copied into the matric column.
     * @throws ShapeException   If the supplied array does not match the matrix column in size.
     */
    public void setColumnData(int j, double[] value) throws ShapeException {
        if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
        for(int i=rows(); --i >= 0; ) data[i][j] = value[i];		
    }

    /**
     * Sets the matrix column to the values provided in the argument. The argument itself is not referenced,
     * the primitive values from it are copied. As such subsequent changes to the supplied array after the 
     * call will in no way affect the data in this matrix.
     * 
     * @param j             The index of the column to update
     * @param value         Array containing that data that is to be copied into the matric column.
     * @throws ShapeException   If the supplied array does not match the matrix column in size.
     */
    public void setColumnData(int j, float[] value) throws ShapeException {
        if(value.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
        for(int i=rows(); --i >= 0; ) data[i][j] = value[i];		
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
    public void setSum(MatrixAlgebra<?, ? extends Double> a, MatrixAlgebra<?, ? extends Double> b) {
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");			
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j] = a.get(i, j) + b.get(i, j);
    }


    @Override
    public void setDifference(MatrixAlgebra<?, ? extends Double> a, MatrixAlgebra<?, ? extends Double> b) {
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
    public void flip() {
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

    /**
     * The vector basis class for the parent matrix.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class VectorBasis extends AbstractVectorBasis<Double> {
        private static final long serialVersionUID = 2039401048091817380L;

        public VectorBasis() {
            super(Matrix.this.rows());
        }
        
        /* (non-Javadoc)
         * @see kovacs.math.AbstractVectorBasis#asMatrix()
         */
        @Override
        public Matrix asRowVector() {
            Matrix M = new Matrix(size());
            toMatrix(M);
            return M;
        }

        @Override
        public AbstractVector<Double> getVectorInstance() {
            return new RealVector(getVectorSize());
        }
    }


    /**
     * The LU decomposition class of the parent matrix.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class LU extends LUDecomposition<Double> implements RealMatrixSolver {
        private Matrix inverse;
        
        /**
         * Constructs an LU decomposition of the parent matrix.
         * 
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate) 
         */
        public LU() throws SquareMatrixException, SingularMatrixException { 
            super(Matrix.this);     
        }

        /**
         * Constructs an LU decomposition of the parent matrix, using the specified small
         * numerical value that is used for substituting zeroes in the arithmetic.
         * 
         * @param tinyValue     A very small numerical value to replce zeroes in the matrix
         *                      for algorithmic stability.
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public LU(double tinyValue) throws SquareMatrixException, SingularMatrixException {
            super(Matrix.this, tinyValue);
        }

        @Override
        public Matrix getMatrix() { return (Matrix) super.getMatrix(); }

        @Override
        public Matrix getInverseMatrix() { 
            if(inverse == null) {
                inverse = Matrix.this.getMatrixInstance(size(), size(), false);
                double[] v = new double[size()];

                for(int i=size(); --i >= 0; ) {
                    v[i] = 1.0;
                    solve(v);
                    inverse.setColumnData(i, v);
                    if(i > 0) Arrays.fill(v, 0.0);
                }
            }
            return inverse;
        }

        @Override
        public Double getDeterminant() {
            double D = evenChanges ? 1.0 : -1.0;
            for(int i=size(); --i >= 0; ) D *= LU.get(i, i);
            return D;
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
            double[] x =  new double[y.length];
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
        public RealVector solveFor(MathVector<? extends Double> y) {
            LU.assertSize(y.size(), y.size());
            RealVector x = getVectorInstance(y.size());
            solve(x.getData());
            return x;        
        }

        @Override
        public void solveFor(MathVector<? extends Double> y, MathVector<Double> x) {
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
    
    /**
     * The Gauss inverter class for the parent matrix.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class Gauss extends GaussInverter<Double> implements RealMatrixSolver {
        
        /**
         * Construct a matrix inverter object for the parent matrix using Gauss-Jordan elimination.
         * 
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for matrix inversion
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public Gauss() throws SquareMatrixException, SingularMatrixException {
            super(Matrix.this);
        }
        
        @Override
        public Matrix getMasterInverse() {
            return (Matrix) super.getMasterInverse();
        }
        
        @Override
        public Matrix getInverseMatrix() {
            return (Matrix) super.getInverseMatrix();
        }

        @Override
        public RealVector solveFor(MathVector<? extends Double> y) {
            return getMasterInverse().dot(y);
        }
        
        @Override
        public double[] solveFor(double[] y) {
            return getMasterInverse().dot(y).getData();
        }

        @Override
        public void solveFor(double[] y, double[] x) {
            getMasterInverse().dot(y, x);
        }

        @Override
        public RealVector solveFor(RealVector y) {
            return getMasterInverse().dot(y);
        }

        @Override
        public void solveFor(RealVector y, RealVector x) {
            getMasterInverse().dot(y, x);
        }

        @Override
        public Double[] solveFor(Double[] y) {
            getMasterInverse().assertSize(y.length, y.length);
            double[] Y = new double[y.length];
            double[] X = new double[y.length];
            solveFor(Y, X);
            Double[] x = new Double[X.length];
            for(int i=x.length; --i >= 0; ) x[i] = X[i];
            return x;
        }   
    }

    /**
     * The Jacobi transform class of the parent matrix.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */ 
    public class JacobiTransform implements EigenSystem<Double, Double> {
        private Matrix B, iB;
        private double[] eigenValues;
        
        public JacobiTransform() throws SquareMatrixException, SymmetryException, ConvergenceException {          
            this(100);   
        }
        
        public JacobiTransform(int maxIterations) throws SquareMatrixException, SymmetryException, ConvergenceException {          
            transform(Matrix.this, 100);   
        }
        
        @Override
        public RealVector getEigenValues() {
            RealVector l = new RealVector(size());
            System.arraycopy(eigenValues, 0, l.getData(), 0, size());
            return l;
        }
        
        @Override
        public RealVector[] getEigenVectors() {
            RealVector[] e = new RealVector[size()];

            for(int j=size(); --j >= 0; ) {
                RealVector ej = new RealVector(size());
                B.copyColumnTo(j, ej.getData());
                e[j] = ej;
            }

            return e;
        }
        
        @Override
        public Double getDeterminant() {
            double D = 1.0;
            for(int j=size(); --j >= 0; ) D *= eigenValues[j];
            return D;
        }
        
        @Override
        public Matrix toEigenBasis() {
            return B.copy();
        }
        
        @Override
        public Matrix fromEigenBasis() {
            if(iB == null) iB = B.getInverse();
            return iB.copy();
        }
        
        @Override
        public RealVector toEigenBasis(MathVector<? extends Double> v) {
            return B.dot(v);
        }
        
        @Override
        public RealVector fromEigenBasis(MathVector<? extends Double> v) {
            if(iB == null) iB = B.getInverse();
            return iB.dot(v);
        }
        
        @Override
        public DiagonalMatrix.Real getDiagonalMatrix() {
            return new DiagonalMatrix.Real(Arrays.copyOf(eigenValues, eigenValues.length));
        }
        
        
        public Matrix getReconstructedMatrix() {
            if(iB == null) iB = B.getInverse();
            return B.dot(new DiagonalMatrix.Real(eigenValues)).dot(iB);
        }
        
        private int size() {
            return eigenValues.length;
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
            
            eigenValues = new double[n];
            Matrix A = Matrix.this.copy();
            B = A.getMatrixInstance(n, n, true);
            B.addIdentity();
            
            final double[] b = new double[n];
            final double[] z = new double[n];
           
            for(int i = 0; i < n; i++) b[i] = eigenValues[i] = A.get(i, i);

            Rotation r = new Rotation();
            int nrot = 0;
            
            for(int k=0; k < maxIterations; k++) {
                double sum = 0.0;
                
                for (int i = 0; i < n-1; i++) for (int j = i+1; j < n; j++) sum += Math.abs(A.get(i, j));
                
                if(sum == 0.0) return nrot;
                
                final double tresh = (k < 4) ? 0.2 * sum / (n * n) : 0.0;

                for(int i = 0; i < n-1; i++) for(int j = i+1; j < n; j++) {
                    final double g = 100.0 * Math.abs(A.get(i, j));
                 
                    if(k > 4 && (Math.abs(eigenValues[i]) + g) == Math.abs(eigenValues[i]) && (Math.abs(eigenValues[j]) + g) == Math.abs(eigenValues[j])) 
                        A.clear(i, j);
                    
                    else if (Math.abs(A.get(i, j)) < tresh) continue;

                    double h = eigenValues[j] - eigenValues[i];
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
                    eigenValues[i] -= h;
                    eigenValues[j] += h;

                    A.clear(i, j);

                    for(int m = 0; m < i; m++) r.rotate(A, m, i, m, j);
                    for(int m = i+1; m < j; m++) r.rotate(A, i, m, m, j);
                    for(int m = j+1; m < n; m++) r.rotate(A, i, m, j, m);
                    for(int m = 0; m < n; m++) r.rotate(B, m, i, m, j);
                    
                    nrot++;
                }
                
                for(int ip = n; --ip >= 0; ) {
                    b[ip] += z[ip];
                    eigenValues[ip] = b[ip];
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


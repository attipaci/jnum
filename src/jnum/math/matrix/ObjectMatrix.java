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


import jnum.Copiable;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.data.image.Index2D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;

import java.lang.reflect.*;
import java.text.ParseException;
import java.text.ParsePosition;



/**
 * The Class ObjectMatrix.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("unchecked")
public class ObjectMatrix<T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue> 
extends AbstractMatrix<T> {

    private static final long serialVersionUID = -2705914561805806547L;

    private T[][] data; 
    private T identity;

    protected Class<T> type;

    /**
     * Instantiates a new generic matrix.
     */
    protected ObjectMatrix() {}

    /**
     * Instantiates a new generic matrix.
     *
     * @param type the type
     */
    public ObjectMatrix(Class<T> type) { 
        this.type = type; 
        identity = newEntry();
        identity.setIdentity();
    }


    /**
     * Instantiates a new generic matrix.
     *
     * @param a the a
     * @throws IllegalArgumentException the illegal argument exception
     */
    public ObjectMatrix(T[][] a) throws IllegalArgumentException { 
        this((Class<T>) a[0][0].getClass());
        assertSize(a.length, a[0].length);
        checkShape(a);
        data = a; 
    }

    /**
     * Instantiates a new generic matrix.
     *
     * @param type the type
     * @param rows the rows
     * @param cols the cols
     */
    public ObjectMatrix(Class<T> type, int rows, int cols) { 
        super(type, rows, cols);
        this.type = type;
    }


    public ObjectMatrix(Class<T> type, int size) { 
        super(type, size, size);
        this.type = type;
    }

    public ObjectMatrix(Class<T> type, String text, ParsePosition pos) throws ParseException, Exception {
        this(type);
        data = (T[][]) ArrayUtil.parse(text.substring(pos.getIndex()), type);
    }


    public ObjectMatrix(Class<T> type, Matrix M) {
        this(type, M.rows(), M.cols());

        for(int i=rows(); --i >= 0; ) for(int j = cols(); --j >= 0; ) {
            T v = get(i, j);
            v.setIdentity();
            v.scale(M.get(i, j));
        }
    }

    @Override
    public T newEntry() {
        try { return getElementType().getConstructor().newInstance(); }
        catch(Exception e) { 
            Util.error(this, e);
            return null;
        }   
    }

    @Override
    public MatrixElement<T> getElementInstance() {
        return new Element(newEntry());
    }

    @Override
    public ObjectVector<T> getVectorInstance(int size) {
        return new ObjectVector<>(getElementType(), size);
    }

    @Override
    public ObjectMatrix<T> getMatrixInstance(int rows, int cols, boolean initialize) {
        if(initialize) return new ObjectMatrix<>(getElementType(), rows, cols);
        ObjectMatrix<T> M = new ObjectMatrix<>();
        M.data =(T[][]) Array.newInstance(getElementType(), new int[] { rows, cols} );
        return M;
    }

    @Override
    public ObjectMatrix<T> clone() {
        return (ObjectMatrix<T>) super.clone();
    }

    @Override
    public ObjectMatrix<T> copy(boolean withContent) {
        ObjectMatrix<T> copy = clone();
        copy.data = (T[][]) Array.newInstance(getElementType(), new int[] { rows(), cols() });
        if(withContent) for(int i=rows(); --i >=0; ) for(int j = cols(); --j >= 0; ) copy.data[i][j] = (T) data[i][j].copy();
        return null;
    }

    @Override
    public ObjectMatrix<T> copy() { return (ObjectMatrix<T>) super.copy(); }


    @Override
    public final Class<T> getElementType() { return type; }	




    @Override
    public T[][] getData() { return data; }


    @Override
    public final T get(int row, int col) { return data[row][col]; }
    
    @Override
    public final T copyOf(int row, int col) { return (T) get(row, col).copy(); }

    @Override
    public final void set(int row, int col, T v) { data[row][col] = (v == null) ? newEntry() : v; }

    @Override
    public void clear(int i, int j) { data[i][j].zero(); }

    @Override
    public void add(int i, int j, T increment) { if(!isNull(increment)) data[i][j].add(increment); }

    @Override
    public void add(int i, int j, double increment) { if(increment != 0.0) data[i][j].addScaled(identity, increment); }

    @Override
    public void scale(int i, int j, double factor) { data[i][j].scale(factor); }


    @Override
    public boolean isNull(int i, int j) { return data[i][j].isNull(); }

    protected boolean isNull(T v) {
        if(v == null) return true;
        return v.isNull();
    }
    
    protected void checkShape(T[][] x) throws ShapeException {
        if(x == null) return;
        if(x.length == 0) return;
        int m = x[0].length;
        for(int i=x.length; --i > 0; ) if(x[i].length != m) throw new ShapeException("Matrix has an irregular non-rectangular shape!");    
    }



    @Override
    protected synchronized void addProduct(MatrixAlgebra<?, ? extends T> A, MatrixAlgebra<?, ? extends T> B) {		

        T product = newEntry();

        for(int i=A.rows(); --i >= 0; ) {
            final T[] row = data[i];

            for(int k=A.cols(); --k >= 0; ) {
                final T a = A.get(i, k);
                if(isNull(a)) continue;
                
                for(int j=B.cols(); --j >= 0; ) {
                    final T b = B.get(k, j);
                    if(isNull(b)) continue;
                
                    product.setProduct(a, b);
                    row[j].add(product);
                }
            }       
        }
    }

   
    @Override
    public double getMagnitude(int fromi, int fromj, int toi, int toj) {
        double mag2 = 0.0;
        
        for(int i=toi; --i >= fromi; ) for(int j=toj; --j >= toj; ) {
            double a2 = data[i][j].absSquared();
            if(a2 > mag2) mag2 = a2;
        }
        
        return Math.sqrt(mag2);        
    }
    
    @Override
    public T getTrace() throws SquareMatrixException {
        T sum = newEntry();
        for(int i=rows(); --i >= 0; ) sum.add(data[i][i]);
        return sum;
    }
    
    @Override
    public boolean isTraceless() throws SquareMatrixException {
        T sum = newEntry();
        double mag2 = 0.0;
        for(int i=rows(); --i >= 0; ) {
            T v = data[i][i];
            sum.add(v);
            double a2 = v.absSquared();
            if(a2 > mag2) mag2 = a2;
        }
            
        return sum.absSquared() <= 1e-24 * mag2;
    }
    
    @Override
    public ObjectMatrix<T> dot(MatrixAlgebra<?, ? extends T> B) {
        return (ObjectMatrix<T>) super.dot(B);
    }


    public ObjectMatrix<T> dot(Matrix B) {
        ObjectMatrix<T> P = getMatrixInstance(rows(), B.cols(), false);

        for(int i=rows(); --i >= 0; ) for(int k=cols(); --k >= 0; ) {
            final T a = get(i, k);
            if(isNull(a)) continue;
            
            for(int j=B.cols(); --j >= 0; ) {
                final double b = B.get(k, j);
                if(b == 0.0) continue;
                
                T p = (T) a.copy();
                p.scale(b);
                P.add(i, j, p);
            }
        }

        return P;
    }


    @Override
    public ObjectMatrix<T> dot(DiagonalMatrix.Real B) {
        return (ObjectMatrix<T>) B.dotFromLeft(this);
    }
    
    @Override
    public ObjectMatrix<T> dot(DiagonalMatrix<T> B) {
        return (ObjectMatrix<T>) B.dotFromLeft(this);
    }
    
    @Override
    public ObjectVector<T> dot(double[] v) {
        return (ObjectVector<T>) super.dot(v);
    }
    
    @Override
    public ObjectVector<T> dot(float[] v) {
        return (ObjectVector<T>) super.dot(v);
    }
    
    @Override
    public ObjectVector<T> dot(RealVector v) {
        return (ObjectVector<T>) super.dot(v);
    }

    @Override
    public ObjectVector<T> dot(MathVector<? extends T> v) {
        return (ObjectVector<T>) super.dot(v);
    }

    
   
    public void dot(double[] v, T[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result[i];
            to.zero();
            for(int j=cols(); --j >= 0; ) if(v[j] != 0.0) if(!row[j].isNull()) to.addScaled(data[i][j], v[j]);
        }
    }

    @Override
    public void dot(double[] v, MathVector<T> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result.getComponent(i);
            to.zero();
            for(int j=cols(); --j >= 0; ) if(v[j] != 0.0) if(!row[j].isNull()) to.addScaled(data[i][j], v[j]);
        }
    }


    public void dot(float[] v, T[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result[i];
            to.zero();
            for(int j=cols(); --j >= 0; ) if(v[j] != 0.0) if(!row[j].isNull()) to.addScaled(row[j], v[j]);
        }
    }

    @Override
    public void dot(float[] v, MathVector<T> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result.getComponent(i);
            to.zero();
            for(int j=cols(); --j >= 0; ) if(v[j] != 0.0) if(!row[j].isNull()) to.addScaled(data[i][j], v[j]);
        }
    }

    @Override
    public void dot(T[] v, T[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = newEntry();

        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result[i];

            to.zero();
            for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) if(!v[j].isNull()) {
                P.setProduct(data[i][j], v[j]);
                to.add(P);
            }
        }
    }

    @Override
    public void dot(T[] v, MathVector<T> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = newEntry();

        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result.getComponent(i);
            to.zero();
            for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) if(!v[j].isNull()) {
                P.setProduct(data[i][j], v[j]);
                to.add(P);
            }
        }
    }


    @Override
    public void dot(MathVector<? extends T> v, MathVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = newEntry();
      
        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T sum = result.getComponent(i);
            sum.zero();
            for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) {
                final T c = v.getComponent(j);
                if(c.isNull()) continue;
                P.setProduct(v.getComponent(j), row[j]);
                sum.add(P);
            }
        }
    }

    @Override
    public ObjectMatrix<T> getTranspose() {		
        return (ObjectMatrix<T>) super.getTranspose();
    }

    @Override
    public void addScaled(MatrixAlgebra<?, ? extends T> o, double factor) {
        assertSize(o.rows(), o.cols());

        if(Number.class.isAssignableFrom(o.getElementType())) 
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
                double v = ((Number) o.get(i, j)).doubleValue();
                if(v != 0.0) data[i][j].addScaled(identity, v * factor);
            }

        else for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) 
            data[i][j].addScaled((T) o.get(i, j), factor);
    }


    @Override
    public void subtract(MatrixAlgebra<?, ? extends T> o) {
        assertSize(o.rows(), o.cols());

        if(Number.class.isAssignableFrom(o.getElementType())) addScaled(o, -1.0);
        else for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].subtract((T) o.get(i, j));
    }
    
    @Override
    public double distanceTo(MatrixAlgebra<?, ?> o) {
        if(o instanceof DiagonalMatrix) return ((DiagonalMatrix<?>) o).distanceTo(this);
        if(o.getElementType().isAssignableFrom(type)) return o.distanceTo(this);
        
        assertSize(o.rows(), o.cols());   
        
        T v = newEntry();
        
        if(type.isAssignableFrom(o.getElementType())) {
            double d2 = 0.0;
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
                v.setDifference(get(i, j), (T) o.get(i, j));
                d2 += v.absSquared();
            }
            return Math.sqrt(d2);
        }
        
        if(Number.class.isAssignableFrom(o.getElementType())) {   
            double d2 = 0.0;
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
                v.setIdentity();
                v.scale(((Number) o.get(i, j)).doubleValue());
                v.subtract(get(i, j));
                d2 += v.absSquared();
            }
            return Math.sqrt(d2);
        }
        
        return Double.NaN;
    }


    @Override
    public final int cols() { return data[0].length; }


    @Override
    public final int rows() { return data.length; }


    @Override
    public void setData(Object data) {
        this.data = (T[][]) data;
    }


    @Override
    public final void swapRows(int i, int j) {
        T[] rowi = data[i];
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
    public final T[] getRow(int i) { return data[i]; }

    /**
     * Sets the underlying storage of a matrix row to a new array. 
     * 
     * @param i     Matrix row index
     * @param row   The nwew data array for the row.
     */
    public final void setRow(int i, T[] row) {
        if(row.length != cols()) throw new ShapeException("Cannot set mismatched row.");
        data[i] = row;
    }

    @Override
    public void setSum(MatrixAlgebra<?, ? extends T> a, MatrixAlgebra<?, ? extends T> b) {
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");

        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            if(data[i][j] == null) data[i][j] = newEntry();
            data[i][j].setSum(a.get(i, j), b.get(i,  j));
        }
    }


    @Override
    public void setDifference(MatrixAlgebra<?, ? extends T> a, MatrixAlgebra<?, ? extends T> b) {
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");

        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            if(data[i][j] == null) data[i][j] = newEntry();
            data[i][j].setDifference(a.get(i, j), b.get(i, j));
        }
    }



    @Override
    public ObjectMatrix<T> subspace(int[] rows, int[] cols) {
        return (ObjectMatrix<T>) super.subspace(rows, cols);
    }

    @Override
    public ObjectMatrix<T> subspace(int fromRow, int fromCol, int toRow, int toCol) {
        return (ObjectMatrix<T>) super.subspace(fromRow, fromCol, toRow, toCol);
    }

    @Override
    public ObjectMatrix<T> subspace(Index2D from, Index2D to) {
        return (ObjectMatrix<T>) super.subspace(from, to);
    }

    
    @Override
    public ObjectMatrix<T> getInverse() {
        return getLUInverse();
    }

    @Override
    public ObjectMatrix<T> getGaussInverse() {
        return (ObjectMatrix<T>) super.getGaussInverse();
    }


    @Override
    public ObjectMatrix<T> getLUInverse() {
        return (ObjectMatrix<T>) super.getLUInverse();
    }

    @Override
    public void invert() {
        data = getInverse().data;
    }

    @Override
    public void addIdentity(double scaling) {
        if(!isSquare()) throw new SquareMatrixException();
        T increment = newEntry();
        increment.setIdentity();
        increment.scale(scaling);
        for(int i=rows(); --i >= 0; ) data[i][i].add(increment);
    }


    @Override
    public LU getLUDecomposition() {
        return new LU();
    }
    
    @Override
    public Gauss getGaussInverter() {
        return new Gauss();
    }

    @Override
    public VectorBasis getVectorBasisInstance() {
        return new VectorBasis();
    }

    public class Element extends MatrixElement<T> {
        T value;

        public Element(T value) {
            this.value = value;
        }

        @Override
        public MatrixElement<T> fresh() {
            value = ObjectMatrix.this.newEntry();
            return this;
        }

        @Override
        public Element from(T value) {
            this.value = value;
            return this;
        }

        @Override
        public MatrixElement<T> from(int i, int j) {
            value = get(i, j);
            return this;
        }

        @Override
        public MatrixElement<T> copy(int i, int j) {
            value = copyOf(i, j);
            return this;
        }

        @Override
        public MatrixElement<T> copy(T value) {
            this.value = (T) value.copy();
            return this;
        }

        @Override
        public void applyTo(int i, int j) {
            set(i, j, value);
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public T copyOfValue() {
            return (T) value.copy();
        }


        @Override
        public void addScaled(T o, double factor) {
            value.addScaled(o, factor);
        }

        @Override
        public void zero() {
            value.zero();
        }

        @Override
        public boolean isNull() {
            return value.isNull();
        }

        @Override
        public void scale(double factor) {
            value.scale(factor);
        }

        @Override
        public void add(T o) {
            value.add(o);
        }

        @Override
        public void subtract(T o) {
            value.subtract(o);
        }

        @Override
        public void setSum(T a, T b) {
            value.setSum(a, b);
        }

        @Override
        public void setDifference(T a, T b) {
            value.setDifference(a, b);
        }

        @Override
        public void multiplyBy(T factor) {
            value.multiplyBy(factor);
        }

        @Override
        public void setProduct(T a, T b) {
            value.setProduct(a, b);
        }

        @Override
        public void setIdentity() {
            value.setIdentity();
        }

        @Override
        public T getInverse() {
            return (T) value.getInverse();
        }

        @Override
        public void inverse() {
            value.inverse();
        }

        @Override
        public double distanceTo(T point) {
            return value.distanceTo(point);
        }

        @Override
        public double abs() {
            return value.abs();
        }

        @Override
        public double absSquared() {
            return value.absSquared();
        }

        @Override
        public double normalize() {
            double l = abs();
            value.scale(1.0 / l);
            return l;
        }

    }

    /**
     * The vector basis class for the parent matrix of generic object elements
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class VectorBasis extends AbstractVectorBasis<T> {

        private static final long serialVersionUID = 196973970496491957L;

        public VectorBasis() {
            super(ObjectMatrix.this.rows());
        }
        
        @Override
        public AbstractMatrix<T> asRowVector() {
            AbstractMatrix<T> M = ObjectMatrix.this.getMatrixInstance(1, getVectorSize(), false);
            toMatrix(M);
            return M;
        }

        @Override
        public ObjectVector<T> getVectorInstance() {
            return ObjectMatrix.this.getVectorInstance(getVectorSize());
        }
    }



    /**
     * The LU decomposition class of the parent matrix of generic object elements.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class LU extends LUDecomposition<T> {
        private ObjectMatrix<T> inverse;

        /**
         * Constructs an LU decomposition of the parent matrix.
         * 
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for decomposition
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public LU() throws SquareMatrixException, SingularMatrixException {
            super(ObjectMatrix.this);
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
            super(ObjectMatrix.this, tinyValue);
        }

        @Override
        public ObjectMatrix<T> getMatrix() {
            return (ObjectMatrix<T>) super.getMatrix();
        }


        @Override
        public ObjectMatrix<T> getInverseMatrix() {
            if(inverse == null) {
                inverse = ObjectMatrix.this.getMatrixInstance(size(), size(), false);
                T[] v = (T[]) Array.newInstance(LU.getElementType(), size());
                for(int i=size(); --i >= 0; ) v[i] = LU.newEntry();
                
                for(int i=size(); --i >= 0; ) {
                    v[i].setIdentity();
                    solve(v);
                    inverse.setColumnData(i, v);
                    if(i > 0) for(int k=size(); --k >=0; ) v[k].zero();
                }

            }
            return inverse;
        }

        @Override
        public T getDeterminant() {
            T D = LU.newEntry();
            D.setIdentity();
            if(evenChanges) D.scale(-1.0);
            for(int i=size(); --i >= 0; ) D.multiplyBy(LU.get(i, i));
            return D;
        }

        @Override
        public T[] solveFor(T[] y) {
            LU.assertSize(y.length, y.length);
            T[] x = (T[]) Array.newInstance(LU.getElementType(), y.length);
            solveFor(y, x);
            return x;
        }

        @Override
        public void solveFor(T[] y, T[] x) {
            LU.assertSize(x.length, y.length);
            for(int i=y.length; --i >=0; ) x[i] = (T) y[i].copy();
            solve(x);
        }


        @Override
        public ObjectVector<T> solveFor(MathVector<? extends T> y) {
            LU.assertSize(y.size(), y.size());
            ObjectVector<T> x = new ObjectVector<>(LU.getElementType(), y.size());
            solveFor(x.getData());
            return x;
        }

        @Override
        public void solveFor(MathVector<? extends T> y, MathVector<T> x) {
            LU.assertSize(x.size(), y.size());
            T[] v = (T[]) Array.newInstance(LU.getElementType(), y.size());
            for(int i=size(); --i >=0; ) v[i] = (T) y.getComponent(i).copy();
            solve(v);
            for(int i=size(); --i >=0; ) x.setComponent(i, v[i]);
        }


        private void solve(T[] v) {
            int ii=-1;
            int n = size();

            T term = LU.newEntry();

            for(int i=0; i<n; i++) {
                int ip = index[i];
                T e = (T) v[ip].copy();
                v[ip] = v[i];
                if(ii != -1) for(int j=ii; j < i; j++) {
                    term.setProduct(LU.get(i, j), v[j]);
                    e.subtract(term);
                }
                else if(!e.isNull()) ii = i;
                v[i] = e;
            }
            for(int i=n; --i >= 0; ) {
                T e = v[i];
                for(int j=i+1; j<n; j++) {
                    term.setProduct(LU.get(i, j), v[j]);
                    e.subtract(term);
                }
                e.multiplyBy((T) LU.get(i, i).getInverse());
            }
        }


    }

    /**
     * The Gauss inverter class object for the parent matrix of generic object elements.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public class Gauss extends GaussInverter<T> {

        /**
         * Construct a matrix inverter object for the parent matrix using Gauss-Jordan elimination.
         * 
         * @throws SquareMatrixException    If the matrix argument is not of the required square shape for matrix inversion
         * @throws SingularMatrixException  If the matrix argument is singular (degenerate)
         */
        public Gauss() throws SquareMatrixException, SingularMatrixException {
            super(ObjectMatrix.this);
        }

        @Override
        public ObjectMatrix<T> getMasterInverse() {
            return (ObjectMatrix<T>) super.getMasterInverse();
        }

        @Override
        public ObjectMatrix<T> getInverseMatrix() {
            return (ObjectMatrix<T>) super.getInverseMatrix();
        }

        @Override
        public ObjectVector<T> solveFor(MathVector<? extends T> y) {
            return getMasterInverse().dot(y);
        }

        @Override
        public T[] solveFor(T[] y) {
            getMasterInverse().assertSize(y.length, y.length);
            T[] x = (T[]) Array.newInstance(getElementType(), y.length);
            solveFor(y, x);
            return x;
        }

    }

}

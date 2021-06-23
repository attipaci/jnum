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
import jnum.ShapeException;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;

import java.lang.reflect.*;
import java.text.ParseException;
import java.text.ParsePosition;



/**
 * The Class GenericMatrix.
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
        identity = getEntryInstance();
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
    public T getEntryInstance() {
        try { return getElementType().getConstructor().newInstance(); }
        catch(Exception e) { 
            Util.error(this, e);
            return null;
        }   
    }

    @Override
    public MatrixElement<T> getElementInstance() {
        return new Element(getEntryInstance());
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
    public final void set(int row, int col, T v) { data[row][col] = v; }

    @Override
    public void clear(int i, int j) { data[i][j].zero(); }

    @Override
    public void add(int i, int j, T increment) { data[i][j].add(increment); }

    @Override
    public void add(int i, int j, double increment) { if(increment != 0.0) data[i][j].addScaled(identity, increment); }

    @Override
    public void scale(int i, int j, double factor) { data[i][j].scale(factor); }


    @Override
    public boolean isNull(int i, int j) { return data[i][j].isNull(); }


    protected void checkShape(T[][] x) throws ShapeException {
        if(x == null) return;
        if(x.length == 0) return;
        int m = x[0].length;
        for(int i=x.length; --i > 0; ) if(x[i].length != m) throw new ShapeException("Matrix has an irregular non-rectangular shape!");    
    }



    public void setProduct(Matrix A, AbstractMatrix<? extends T> B) {
        // TODO
    }

    public void setProduct(AbstractMatrix<? extends T> A,Matrix B) {
        // TODO
    }

    public void setProduct(Matrix A, Matrix B) {
        // TODO
    }



    @Override
    protected synchronized void addProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {		

        T product = getEntryInstance();

        for(int i=A.rows(); --i >= 0; ) {
            final T[] row = data[i];

            for(int k=A.cols(); --k >= 0; ) {
                final T a = A.get(i, k);
                for(int j=B.cols(); --j >= 0; ) {
                    final T b = B.get(k, j);
                    product.setProduct(a, b);
                    row[j].add(product);
                }
            }       
        }
    }

    public ObjectMatrix<T> dot(ObjectMatrix<? extends T> B) {
        return (ObjectMatrix<T>) super.dot(B);
    }


    public ObjectMatrix<T> dot(Matrix B) {
        ObjectMatrix<T> P = getMatrixInstance(rows(), B.cols(), false);

        for(int i=rows(); --i >= 0; ) for(int k=cols(); --k >= 0; ) {
            final T a = get(i, k);
            if(!a.isNull()) for(int j=B.cols(); --j >= 0; ) {
                final double b = B.get(k, j);
                if(b != 0.0) {
                    T p = (T) a.copy();
                    p.scale(b);
                    P.add(i, j, p);
                }
            }
        }

        return P;
    }



    @Override
    public T[] dot(double[] v) {
        T[] result = (T[]) Array.newInstance(type, rows());
        dot(v, result);
        return result;
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

    @Override
    public T[] dot(float[] v) {
        T[] result = (T[]) Array.newInstance(type, rows());
        dot(v, result);
        return result;
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
    public T[] dot(T[] v) {
        T[] result = (T[]) Array.newInstance(type, rows());
        dot(v, result);
        return result;
    }


    @Override
    public void dot(T[] v, T[] result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = getEntryInstance();

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

    public void dot(T[] v, MathVector<T> result) {
        if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = getEntryInstance();

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
    public ObjectVector<T> dot(RealVector v) {
        ObjectVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }



    public void dot(RealVector v, ObjectVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        dot(v.getData(), result.getData());
    }

    @Override
    public void dot(RealVector v, MathVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            final T to = result.getComponent(i);
            to.zero();
            for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) {
                double vj = v.getComponent(j);
                if(vj != 0.0) to.addScaled(data[i][j], vj);
            }
        }
    }

    @Override
    public ObjectVector<T> dot(MathVector<T> v) {
        ObjectVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }


    @Override
    public void dot(MathVector<T> v, MathVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");

        final T P = getEntryInstance();
        final T sum = getEntryInstance();

        for(int i=rows(); --i >= 0; ) {
            final T[] row = data[i];
            sum.zero();
            for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) {
                final T c = v.getComponent(j);
                if(c.isNull()) continue;
                P.setProduct(v.getComponent(j), row[j]);
                sum.add(P);
            }
            result.setComponent(i, sum);
        }
    }

    @Override
    public ObjectMatrix<T> getTranspose() {		
        return (ObjectMatrix<T>) super.getTranspose();
    }

    @Override
    public void addScaled(AbstractMatrix<? extends T> o, double factor) {
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
    public void subtract(AbstractMatrix<? extends T> o) {
        assertSize(o.rows(), o.cols());

        if(Number.class.isAssignableFrom(o.getElementType())) addScaled(o, -1.0);
        else for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].subtract((T) o.get(i, j));
    }


    @Override
    public double distanceTo(AbstractMatrix<? extends T> o) {
        double d2 = 0.0;

        if(Number.class.isAssignableFrom(o.getElementType())) {
            T v = getEntryInstance();

            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
                v.setIdentity();
                v.scale(((Number) o.get(i, j)).doubleValue());

                double d = data[i][j].distanceTo(v);
                d2 += d*d;
            }
        }
        else for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            double d = data[i][j].distanceTo((T) o.get(i, j));
            d2 += d*d;
        }
        return Math.sqrt(d2);
    }


    @Override
    public final int cols() { return data[0].length; }


    @Override
    public final int rows() { return data.length; }


    @Override
    public void getColumnTo(int j, Object buffer) {
        T[] array = (T[]) buffer;
        for(int i=rows(); --i >= 0; ) array[i] = data[i][j];
    }


    @Override
    public final T[] getRow(int i) { return data[i]; }


    @Override
    public void getRowTo(int i, Object buffer) {
        T[] array = (T[]) buffer;
        for(int j=cols(); --j >= 0; ) array[j] = data[i][j];
    }


    @Override
    public void setColumn(int j, Object value) throws ShapeException {
        T[] array = (T[]) value;
        if(array.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
        for(int i=rows(); --i >= 0; ) data[i][j] = array[i];
    }


    @Override
    public void setData(Object data) {
        this.data = (T[][]) data;
    }


    @Override
    public void setRow(int i, Object value) {
        T[] array = (T[]) value;
        if(array.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
        data[i] = array;
    }


    @Override
    public void setSum(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
        if(!a.conformsTo(b))	throw new ShapeException("different size matrices.");

        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            if(data[i][j] == null) data[i][j] = getEntryInstance();
            data[i][j].setSum(a.get(i, j), b.get(i,  j));
        }
    }


    @Override
    public void setDifference(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
        if(!a.conformsTo(b)) throw new ShapeException("different size matrices.");

        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
            if(data[i][j] == null) data[i][j] = getEntryInstance();
            data[i][j].setDifference(a.get(i, j), b.get(i, j));
        }
    }



    @Override
    public ObjectMatrix<T> subspace(int[] rows, int[] cols) {
        return (ObjectMatrix<T>) super.subspace(rows, cols);
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
        T increment = getEntryInstance();
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
            value = ObjectMatrix.this.getEntryInstance();
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




    public class VectorBasis extends AbstractVectorBasis<T> {

        private static final long serialVersionUID = 196973970496491957L;


        public VectorBasis() {}

        /* (non-Javadoc)
         * @see kovacs.math.AbstractVectorBasis#asMatrix()
         */
        @Override
        public ObjectMatrix<T> asMatrix() {
            ObjectMatrix<T> M = new ObjectMatrix<>(get(0).getType());
            asMatrix(M);
            return M;
        }

        @Override
        public ObjectVector<T> getVectorInstance(int size) {
            return new ObjectVector<>(getElementType(), size());
        }
    }



    public class LU extends LUDecomposition<T> {


        public LU() {
            super(ObjectMatrix.this);
        }

        public LU(double tinyValue) {
            super(ObjectMatrix.this, tinyValue);
        }

        @Override
        public ObjectMatrix<T> getMatrix() {
            return (ObjectMatrix<T>) super.getMatrix();
        }


        @Override
        public ObjectMatrix<T> getInverseMatrix() {
            return (ObjectMatrix<T>) super.getInverseMatrix();
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
        public ObjectVector<T> solveFor(MathVector<T> y) {
            LU.assertSize(y.size(), y.size());
            ObjectVector<T> x = new ObjectVector<>(LU.getElementType(), y.size());
            solveFor(x.getData());
            return x;
        }

        @Override
        public void solveFor(MathVector<T> y, MathVector<T> x) {
            LU.assertSize(x.size(), y.size());
            T[] v = (T[]) Array.newInstance(LU.getElementType(), y.size());
            for(int i=size(); --i >=0; ) v[i] = (T) y.getComponent(i).copy();
            solve(v);
            for(int i=size(); --i >=0; ) x.setComponent(i, v[i]);
        }


        private void solve(T[] v) {
            int ii=-1;
            int n = size();


            T term = LU.getEntryInstance();

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

        @Override
        public void getInverseTo(AbstractMatrix<T> inverse) {       
            if(!inverse.isSquare()) throw new SquareMatrixException();
            final int n = size();

            if(inverse.rows() != n) throw new ShapeException("mismatched inverse matrix size.");

            T[] v = (T[]) Array.newInstance(LU.getElementType(), n);

            for(int i=0; i<n; i++) {
                v[i] = LU.getEntryInstance();

                if(i > 0) for(int k=n; --k >=0; ) v[k].zero();
                v[i].setIdentity();
                solve(v);
                for(int j=n; --j >= 0; ) inverse.set(j, i, v[j]);
            }

        }
    }


    public class Gauss extends GaussInverter<T> {

        public Gauss() {
            super(ObjectMatrix.this);
        }

        @Override
        public ObjectMatrix<T> getI() {
            return (ObjectMatrix<T>) super.getI();
        }

        @Override
        public ObjectMatrix<T> getInverseMatrix() {
            return (ObjectMatrix<T>) super.getInverseMatrix();
        }

        @Override
        public ObjectVector<T> solveFor(MathVector<T> y) {
            return getI().dot(y);
        }

    }

}

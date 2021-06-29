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

import java.lang.reflect.Array;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.Util;
import jnum.data.IndexedValues;
import jnum.data.image.Index2D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.MathVector;
import jnum.math.Metric;
import jnum.math.Scalable;


/**
 * A matrix class for diagonal matrices. As opposed to generic matrix implementations, such as {@link AbstractMatrix} and
 * it sub-classes, which have <i>N</i><sup>2</sup> matrix elements for square matrices of dimension <i>N</i>, diagonal
 * matrices have only <i>N</i> diagonal elements. Thus all matrix operations with diagonal matrices can be performed
 * on the order of <i>N</i> times faster than implementing the same diagonal matrix as a generic full-fledged 2D matrix
 * (with a whole lot of zero elements). This performance advantage is a main and only good reason why diagonal 
 * matrices are implemented as a separate class of their own.
 * 
 * You can nevertheless intermingle normal fully 2D matrices and diagonal matrices in dot products etc.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <T>   The generic type of the matrix elements in this diagonal matrix.
 */
public abstract class DiagonalMatrix<T> implements SquareMatrixAlgebra<DiagonalMatrix<? extends T>, T>,
Cloneable, CopiableContent<DiagonalMatrix<T>> {

    @Override
    @SuppressWarnings("unchecked")
    public DiagonalMatrix<T> clone() {
        try { return (DiagonalMatrix<T>) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public DiagonalMatrix<T> copy() {
        return copy(true);
    }
    
    @Override
    public abstract AbstractVector<T> getVectorInstance(int size);
    
    /**
     * Gets a new diagonal matrix of the same type as this matrix and of the specified size.
     * The new diagonal matrix is initialized with zeroes.
     * 
     * @param size  The size of the new diagonal matrix instance
     * @return      A new diagonal matrix of the same type as this matrix, and of the specified size.
     */
    public abstract DiagonalMatrix<T> getDiagonalMatrixInstance(int size);
    
    /**
     * Gets a full-2D matrix instance of the same generic type as this matrix and the
     * specified rectangular size. The new matrix can be initialized with zero elements
     * or created just as an an uninitialized container containing <code>null</code>
     * references in every slot.
     * 
     * @param rows          Number of matrix rows.
     * @param cols          Number of matrix columns.
     * @param initialize    Whether the matrix should be populated with 'zero' objects
     *                      or left empty with <code>null</code> references.
     * @return              A new full 2D matrix of the same type as this and the requested size.
     */
    public abstract AbstractMatrix<T> getMatrixInstance(int rows, int cols, boolean initialize);
    
    /**
     * Gets the size of this matrix, i.e. the number of diagonal elements it contains.
     * 
     * @return      The size (rows and columns) in this matrix.
     */
    public abstract int size();
    
    /**
     * Checks if this matrix is of the expected size before attempting an operation, and
     * throws an exception if the size if the sizes mismatch.
     * 
     * @param size      The expected size of this matrix
     * @throws ShapeException   If the size of this matrix does not match the specified size.
     */
    public void assertSize(int size) throws ShapeException {
        if(size != size()) throw new ShapeException("Wrong size " + size() + ". Expected " + size);
    }
    
    @Override
    public final int rows() { return size(); }

    @Override
    public final int cols() { return size(); }

    @Override
    public final boolean isSquare() { return true; }

    @Override
    public final boolean isDiagonal() { return true; }
    
    @Override
    public final int capacity() { return size(); }

    @Override
    public final int dimension() { return 2; }

    @Override
    public final Index2D getSize() { return new Index2D(size(), size()); }

    @Override
    public final T get(Index2D index) { return get(index.i(), index.j()); }

    @Override
    public final void set(Index2D index, T value) { set(index.i(), index.j(), value); }

    @Override
    public final Index2D getIndexInstance() { return new Index2D(); }
  
    @Override
    public final Index2D copyOfIndex(Index2D index) { return index.copy(); }

    @Override
    public final boolean conformsTo(Index2D size) { return isSize(size.i(), size.j()); }

    @Override
    public final boolean conformsTo(IndexedValues<Index2D, ?> data) { return conformsTo(data.getSize()); }

    @Override
    public String getSizeString() { return "[" + size() + "x" + size() + "]"; }

    @Override
    public boolean isSize(int rows, int cols) {
        if(rows != cols) return false;
        return rows == size();
    }

    @Override
    public boolean conformsTo(MatrixAlgebra<?, ?> M) {
        return M.isSize(size(), size());
    }
    
    @Override
    public final boolean containsIndex(Index2D index) {
        if(index.i() < 0.0) return false;
        if(index.i() > rows()) return false;
        if(index.j() < 0.0) return false;
        if(index.j() > cols()) return false;
        return true;
    }
    

    /**
     * Increments a value on the matrix diagonal with a scaled instance of the specified generic type value.
     * That is the value at <code>[i][i]</code> is incremented by <code>factor * value</code>.
     * 
     * @param i         The diagonal element index.
     * @param value     The generic type value part of the increment
     * @param factor    The scaling factor for the added value.
     */
    public abstract void addDiagonalScaled(int i, T value, double factor);
    
    /**
     * Multiplies (from the right) a diagonal element in this matrix with the specified generic type value
     * 
     * @param i         The diagonal element index.
     * @param value     The scaling factor for the added value.
     */
    public abstract void multiplyDiagonalBy(int i, T value);

    
    @Override
    public final void set(int i, int j, T value) {
        if(i == j) setDiagonal(i, value);
        throw new DiagonalMatrixException("Cannot set off-diagonal values in a diagonal matrix.");
    }
    
    @Override
    public void addIdentity() {
        addIdentity(1.0);
    }

    @Override
    public final void subtractIdentity() {
        addIdentity(-1.0);
    }
    
    @Override
    public final AbstractVector<T> dot(T[] v) {
        assertSize(v.length);
        AbstractVector<T> result = getVectorInstance(size());
        dot(v, result);
        return result;
    }
    
   
    @Override
    public AbstractVector<T> dot(double[] v) {
        assertSize(v.length);
        AbstractVector<T> result = getVectorInstance(size());
        dot(v, result);
        return result;
    }

    @Override
    public AbstractVector<T> dot(float[] v) {
        assertSize(v.length);
        AbstractVector<T> result = getVectorInstance(size());
        dot(v, result);
        return result;
    }
    

    @Override
    public AbstractVector<T> dot(RealVector v) {
        assertSize(v.size());
        AbstractVector<T> result = getVectorInstance(size());
        dot(v, result);
        return result;
    }
    
    @Override
    public AbstractVector<T> dot(MathVector<? extends T> v) throws ShapeException {
        assertSize(v.size());
        AbstractVector<T> result = getVectorInstance(size());
        dot(v, result);
        return result;
    }
    
    
    @Override
    public final void dot(RealVector v, MathVector<T> result) {
        dot(v.getData(), result);
    }
  
     
    
    @Override
    public void addScaled(DiagonalMatrix<? extends T> o, double factor) {
        if(factor == 0.0) return;
        for(int i=size(); --i >= 0; ) addDiagonalScaled(i, o.getDiagonal(i), factor);
    }

    @Override
    public void zero() {
        for(int i=size(); --i >= 0; ) zeroDiagonal(i);
    }

    @Override
    public boolean isNull() {
        for(int i=size(); --i >= 0; ) if(!isNullDiagonal(i)) return false;
        return true;
    }
    

    @Override
    public void scale(double factor) {
        for(int i=size(); --i >= 0; ) if(!isNullDiagonal(i)) scaleDiagonal(i, factor);
    }

    @Override
    public void flip() {
        scale(-1.0);
    }
    
    @Override
    public void add(DiagonalMatrix<? extends T> o) {
        for(int i=size(); --i >= 0; ) if(!o.isNullDiagonal(i)) addDiagonal(i, o.getDiagonal(i));
    }

    @Override
    public void subtract(DiagonalMatrix<? extends T> o) {
        for(int i=size(); --i >= 0; ) if(!o.isNullDiagonal(i)) addDiagonalScaled(i, o.getDiagonal(i), -1.0);
    }


    @Override
    public void setIdentity() {
        zero();
        setIdentity();
    }


    @Override
    public DiagonalMatrix<? extends T> dot(DiagonalMatrix<? extends T> righthand) {
        assertSize(righthand.size());
        DiagonalMatrix<T> P = copy();
        for(int i=size(); --i >= 0; ) P.multiplyDiagonalBy(i, righthand.getDiagonal(i));
        return P;
    }

    /**
     * Gets the dot product of this diagonal matrix with any other matrix.
     * 
     * @param righthand     The matrix (of any kind) on the right-hand side of product.
     * @return              The dot product with the specified other matrix on the righ-hand side.
     * @throws ShapeException   If the matrices have incompatible dimensions for a dot product.
     */
    public abstract AbstractMatrix<T> dot(MatrixAlgebra<?, ? extends T> righthand) throws ShapeException;
    
    /**
     * Gets the dot product of the other matrix (on the left) and this diagonal matrix (on the right).
     * 
     * @param lefthand      The matrix (of any kind) on the left-hand side of product.
     * @return              The dot product of the specified other matrix (on the left) and this matrix (on the right).
     * @throws ShapeException   If the matrices have incompatible dimensions for a dot product.
     */
    public abstract AbstractMatrix<T> dotFromLeft(MatrixAlgebra<?, ? extends T> lefthand) throws ShapeException;
    
    
    /**
     * Converts this diagonal matrix into a full 2D matrix, in which it is possible to set off-diagonal
     * elements also.
     * 
     * @return      The full 2D representation of this diagonal matrix.
     */
    public AbstractMatrix<T> toFullMatrix() {
        AbstractMatrix<T> M = getMatrixInstance(size(), size(), true);
        for(int i=size(); --i >= 0; ) M.setDiagonal(i, getDiagonal(i));        
        return M;
    }
    
    /**
     * Gets the real-valued identity matrix as a diagonal matrix.
     * 
     * @param size      The size (number of diagonal elements) of the new identity matrix.
     * @return          A new identity matrix.
     */
    public static DiagonalMatrix.Real indentity(int size) {
        DiagonalMatrix.Real I = new DiagonalMatrix.Real(size);
        I.addIdentity();
        return I;
    }
    
    
    /**
     * A class of diagonal matrices with real numbers (<code>double</code>) elements. It is backed by a
     * <code>double[]</code> array storage. 
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public static class Real extends DiagonalMatrix<Double> {
        private double[] data;
        
        /**
         * Constructs a real-valued diagonal matrix with the specified size (numbber of diagonal elements).
         * 
         * @param size      Number of diagonal elements in this matrix.
         */
        public Real(int size) {
            data = new double[size];
        }
        
        /**
         * Constructs a real-valued diagonal matrix with the specified array as the backing data.
         * The supplied array is referenced in the new matrix. As such any changes to the data
         * elements will necessarily affect the diagonal values of the matrix and vice versa.
         * 
         * @param data      The array that will be holding the diagonal values of this matrix.
         */
        public Real(double[] data) {
            this.data = data;
        }
        
        
        @Override
        public Real clone() {
            return (Real) super.clone();
        }
        
        @Override
        public Real copy() {
            return (Real) super.copy();
        }
        
        @Override
        public Real copy(boolean withContent) {
            Real copy = clone();
            copy.data = new double[data.length];
            if(withContent) System.arraycopy(data, 0, copy.data, 0, data.length);
            return copy;
        }
        
        @Override
        public RealVector getVectorInstance(int size) {
            return new RealVector(size);
        }
        
        
        @Override
        public RealVector dot(MathVector<? extends Double> v) throws ShapeException {
            return (RealVector) super.dot(v); 
        }

        @Override
        public RealVector dot(RealVector v) {
            return (RealVector) super.dot(v);
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
        public Real getDiagonalMatrixInstance(int size) {
            return new Real(size);
        }
        
        @Override
        public Real dot(DiagonalMatrix<? extends Double> righthand) {
            return (Real) super.dot(righthand);
        }

        @Override
        public Matrix getMatrixInstance(int rows, int cols, boolean initialize) {
            return new Matrix(rows, cols);
        }
        
        @Override
        public Matrix toFullMatrix() {
            return (Matrix) super.toFullMatrix();
        }
        
        @Override
        public Matrix dot(MatrixAlgebra<?, ? extends Double> righthand) {
            assertSize(righthand.rows());
            Matrix P = getMatrixInstance(righthand.rows(), righthand.cols(), false);
            for(int i=righthand.rows(); --i >= 0; ) for(int j=righthand.cols(); --j >= 0; ) {
                P.set(i, j, getDiagonal(i) * righthand.get(i, j));
            }
            return P; 
        }
        
        @Override
        public Matrix dotFromLeft(MatrixAlgebra<?, ? extends Double> lefthand) {
            assertSize(lefthand.cols());
            Matrix P = getMatrixInstance(lefthand.rows(), lefthand.cols(), false);
            for(int i=lefthand.rows(); --i >= 0; ) for(int j=lefthand.cols(); --j >= 0; ) {
                P.set(i, j, lefthand.get(i, j) * getDiagonal(i));
            }
            return P; 
        }
        
        /**
         * Returns the dot product of this diagonal matrix and a full 2D generic type matrix
         * on the right-hand side as a new 2D generic type matrix of the same class as the argument.
         * 
         * @param <T>       The generic type of elements in the specified matrix argument, and returned matrix.
         * @param righthand The matrix that multiplies this matrix from the right-hand side.
         * @return          A matrix of the same class as the argument, with the result of the dot product.
         */
        public <T extends Scalable> AbstractMatrix<T> dot(AbstractMatrix<T> righthand) {
            assertSize(righthand.rows());
            AbstractMatrix<T> P = righthand.getMatrixInstance(righthand.rows(), righthand.cols(), false);
            for(int i=righthand.rows(); --i >= 0; ) for(int j=righthand.cols(); --j >= 0; ) {
                T e = righthand.copyOf(i, j);
                e.scale(getDiagonal(i));
                P.set(i, j, e);
            }
            return P; 
        }
        
        
        /**
         * Returns the dot product of a full 2D generic type matrix (on the left) and this diagonal matrix
         * (on the right) as a new 2D generic type matrix of the same class as the argument.
         * 
         * @param <T>       The generic type of elements in the specified matrix argument, and returned matrix.
         * @param righthand The matrix that multiplies this matrix from the left-hand side.
         * @return          A matrix of the same class as the argument, with the result of the dot product.
         */
        public <T extends Scalable> AbstractMatrix<T> dotFromLeft(AbstractMatrix<T> lefthand) {
            assertSize(lefthand.cols());
            AbstractMatrix<T> P = lefthand.getMatrixInstance(lefthand.rows(), lefthand.cols(),  false);
            for(int i=lefthand.rows(); --i >= 0; ) for(int j=lefthand.cols(); --j >= 0; ) {
                T e = lefthand.copyOf(i, j);
                e.scale(getDiagonal(i));
                P.set(i, j, e);
            }
            return P; 
        }
        
        @Override
        public Double getTrace() {
            double sum = 0.0;
            for(int i = size(); --i >= 0; ) sum += data[i];
            return sum;
        }

        @Override
        public boolean isTraceless() throws SquareMatrixException {
            double sum = 0.0, mag = 0.0;
            for(int i=rows(); --i >= 0; ) {
                double v = data[i];
                sum += v;
                if(Math.abs(v) > mag) mag = Math.abs(v);
            }
                
            return Math.abs(sum) <= 1e-12 * mag;
        }
        
        @Override
        public Double getDeterminant() {
            double det = 1.0;
            for(int i = size(); --i >= 0; ) det *= data[i];
            return det;
        }
        
        @Override
        public final double getMagnitude() {
            double mag = 0.0;
            for(int i = size(); --i >= 0; ) if(Math.abs(data[i]) > mag) mag = Math.abs(data[i]);
            return mag;
        }
        
        @Override
        public final int getRank() {
            double tiny = 1e-12 * getMagnitude();
            
            int rank = 0;
            for(int i=rows(); --i >= 0; ) 
                for(int j=cols(); --j >= 0; ) if(Math.abs(data[i]) > tiny) {
                    rank++;
                    break;
                }
            
            return rank;
        }
        
        @Override
        public void sanitize() {
            double tiny = 1e-12 * getMagnitude();
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) if(Math.abs(data[i]) < tiny) data[i] = 0.0;
        }

        @Override
        public Real getInverse() {
            Real I = copy(false);
            for(int i = size(); --i >= 0; ) I.data[i] = 1.0 / data[i];
            return I;
        }

        @Override
        public void addIdentity(double scaling) {
            for(int i = size(); --i >= 0; ) data[i] += scaling;
        }

        @Override
        public Class<Double> getElementType() {
            return double.class;
        }

        @Override
        public void dot(MathVector<? extends Double> v, MathVector<Double> result) throws ShapeException {
            assertSize(v.size());
            assertSize(result.size());
            for(int i = size(); --i >= 0; ) result.setComponent(i, data[i] * v.getComponent(i));
        }

        @Override
        public void dot(Double[] v, Double[] result) {
            assertSize(v.length);
            assertSize(result.length);
            for(int i = size(); --i >= 0; ) result[i] = data[i] * v[i];
        }

        @Override
        public void dot(Double[] v, MathVector<Double> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i = size(); --i >= 0; ) result.setComponent(i, data[i] * v[i]);
        }
        
        @Override
        public void dot(double[] v, MathVector<Double> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i = size(); --i >= 0; ) result.setComponent(i, data[i] * v[i]);
        }

        @Override
        public void dot(float[] v, MathVector<Double> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i = size(); --i >= 0; ) result.setComponent(i, data[i] * v[i]);
        }

        @Override
        public void setSum(DiagonalMatrix<? extends Double> a, DiagonalMatrix<? extends Double> b) {
            assertSize(a.size());
            assertSize(b.size());
            for(int i = size(); --i >= 0; ) data[i] = a.getDiagonal(i) + b.getDiagonal(i);
        }

        @Override
        public void setDifference(DiagonalMatrix<? extends Double> a, DiagonalMatrix<? extends Double> b) {
            assertSize(a.size());
            assertSize(b.size());
            for(int i = size(); --i >= 0; ) data[i] = a.getDiagonal(i) - b.getDiagonal(i);            
        }


        @Override
        public final int size() {
            return data.length;
        }

        @Override
        public final Double get(int i, int j) {
            return (i == j) ? getDiagonal(i) : 0.0;
        }
   
        @Override
        public final Double getDiagonal(int i) {
            return data[i];
        }

        @Override
        public final Double copyOfDiagonal(int i) {
            return data[i];
        }

        @Override
        public final void setDiagonal(int i, Double value) {
            data[i] = value;            
        }

        @Override
        public final void addDiagonal(int i, Double value) {
            data[i] += value;
        }

        @Override
        public final void addDiagonalScaled(int i, Double value, double factor) {
            data[i] += factor * value;
        }

        @Override
        public final void scaleDiagonal(int i, double factor) {
            data[i] *= factor;
        }

        @Override
        public final void multiplyDiagonalBy(int i, Double factor) {
            data[i] *= factor;
        }

        @Override
        public final void zeroDiagonal(int i) {
            data[i] = 0.0;
        }

        @Override
        public boolean isNullDiagonal(int i) {
            return data[i] == 0.0;
        }

        /**
         * Gets the distance to another diagonal matrix of any generic type. The distance is
         * the square-root of the square sum of all elements in the difference matrix of this matrix 
         * and the supplied  matrix argument. It is a measure of how far the entries of the
         * two matrices differ from one another.
         * 
         * @param o     The other matrix to which the distance is to be measured
         * @return      The square-root of the square-sum of the difference of matrix elements.
         */
        public double distanceTo(DiagonalMatrix<?> o) {
            assertSize(o.size());
            
            if(Number.class.isAssignableFrom(o.getElementType())) {
                double sum2 = 0.0;
                for(int i=size(); --i >= 0; ) {
                    double d = data[i] - ((Number) o.getDiagonal(i)).doubleValue();
                    sum2 += d * d;
                }
                return Math.sqrt(sum2);
            }
            return o.distanceTo(this);
        }

        @Override
        public double distanceTo(MatrixAlgebra<?, ?> o) {
            if(o instanceof DiagonalMatrix) return distanceTo((DiagonalMatrix<?>) o);
            
            assertSize(o.rows());
            assertSize(o.cols());
            
           
            if(Number.class.isAssignableFrom(o.getElementType())) {
                double sum2 = 0.0;
                for(int i=size(); --i >= 0; ) {
                    double d = data[i] - ((Number) o.get(i, i)).doubleValue();
                    sum2 += d * d;
                    for(int j=size(); --j >= 0; ) if(j != i) {
                        d = ((Number) o.get(i, i)).doubleValue();
                        sum2 += d * d;
                    }
                }
                return Math.sqrt(sum2);
            }
            
            return o.distanceTo(this);
        }
        
    }
    
    
    /**
     * A class of diagonal matrices with non-primitive generic type values.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     * @param <T>       The generic type of elements in this diagonal matrix. The typer must be capable of supporting
     *                  a set of basic mathematical operations in order to support a matrix algebra.
     */
    public static class Generic<T extends Copiable<? super T> & AbstractAlgebra<? super T> & LinearAlgebra<? super T> & Metric<? super T> & AbsoluteValue> 
    extends DiagonalMatrix<T> {
        private Class<T> type;
        private T[] data;
        private T identity;

        /**
         * Constructs a new generic-valued diagonal matrix for a given class of elements and the specified
         * size (number of diagonal elements).
         * 
         * @param type      The class of objects contained in this matrix.
         * @param size      The size (number of diagonal elements) of this matrix.
         */
        @SuppressWarnings("unchecked")
        public Generic(Class<T> type, int size) {
            this(type, (T[]) Array.newInstance(type, size));
        }
        
        /**
         * Constructs a new generic-valued diagonal matrix a given class of elements and with the specified
         * object array holding the references to the diagonal matrix elements. Thus, changes to the elements
         * contained in the array will necessarily affect the diagonal values contained in this matrix
         * and vice versa.
         * 
         * @param type      The class of objects contained in this matrix.
         * @param data      The array that will be holding the diagonal elements in this matrix.
         */
        public Generic(Class<T> type, T[] data) {
            this.type = type;
            this.data = data;
        }
        
        @Override
        public Generic<T> clone() {
            return (Generic<T>) super.clone();
        }
        
        @Override
        public Generic<T> copy() {
            return (Generic<T>) super.copy();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Generic<T> copy(boolean withContent) {
            Generic<T> copy = clone();
            copy.data = (T[]) Array.newInstance(type, data.length);
            if(withContent) for(int i=size(); --i >= 0; ) copy.data[i] = (T) data[i].copy();
            return copy;
        }
        
        
        @Override
        public final int size() {
            return data.length;
        }

        @SuppressWarnings("cast")
        public T newEntry() {
            try { return (T) type.getConstructor().newInstance(); } 
            catch (Exception e) { Util.error(this, e); }
            return null;
        }

        
        @Override
        public final T getTrace() {
            T sum = newEntry();
            for(int i=size(); --i >= 0; ) sum.add(getDiagonal(i));
            return sum;
        }
        
        @Override
        public boolean isTraceless() throws SquareMatrixException {
            T sum = newEntry();
            double mag2 = 0.0;
            for(int i=rows(); --i >= 0; ) {
                T v = data[i];
                sum.add(v);
                double a2 = v.absSquared();
                if(a2 > mag2) mag2 = a2;
            }
                
            return sum.absSquared() <= 1e-24 * mag2;
        }

        @Override
        public T getDeterminant() {
            T det = newEntry();
            det.setIdentity();
            for(int i=size(); --i >= 0; ) det.multiplyBy(getDiagonal(i));
            return det;
        }
        
        @Override
        public final double getMagnitude() {
            double mag = 0.0;
            for(int i = size(); --i >= 0; ) {
                double a = data[i].abs();
                if(a > mag) mag = a;
            }
            return mag;
        }
        
        @Override
        public final int getRank() {
            double tiny2 = 1e-12 * getMagnitude();
            tiny2 *= tiny2;
            
            int rank = 0;
            for(int i=rows(); --i >= 0; ) 
                for(int j=cols(); --j >= 0; ) if(data[i].absSquared() > tiny2) {
                    rank++;
                    break;
                }
            
            return rank;
        }
        
        @Override
        public void sanitize() {
            double tiny2 = 1e-12 * getMagnitude();
            tiny2 *= tiny2;
            for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) if(data[i].absSquared() < tiny2) data[i].zero();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Generic<T> getInverse() {
            Generic<T> I = copy(false);
            for(int i=size(); --i >= 0; ) I.data[i] = (T) data[i].getInverse();
            return I;
        }

        @Override
        public void addIdentity(double scaling) {
            for(int i=size(); --i >= 0; ) addDiagonalScaled(i, identity, scaling);
        }

        @Override
        public Class<T> getElementType() {
            return type;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Generic<T> dot(DiagonalMatrix<? extends T> righthand) {
            return (Generic<T>) super.dot(righthand);
        }
        
        
        @Override
        public ObjectMatrix<T> dot(MatrixAlgebra<?, ? extends T> righthand) {
            assertSize(righthand.rows());
            ObjectMatrix<T> P = getMatrixInstance(righthand.rows(), righthand.cols(), true);
            for(int i=righthand.rows(); --i >= 0; ) for(int j=righthand.cols(); --j >= 0; ) {
                P.get(i, j).setProduct(getDiagonal(i), righthand.get(i, j));
            }
            return P;            
        }
        
        @Override
        public ObjectMatrix<T> dotFromLeft(MatrixAlgebra<?, ? extends T> lefthand) {
            assertSize(lefthand.cols());
            ObjectMatrix<T> P = getMatrixInstance(lefthand.rows(), lefthand.cols(), true);
            for(int i=lefthand.rows(); --i >= 0; ) for(int j=lefthand.cols(); --j >= 0; ) {
                P.get(i, j).setProduct(lefthand.get(i, j), getDiagonal(i));
            }
            return P;            
        }
             
        
        /**
         * Gets the dot product of this diagonal matrix and a real-valued other matrix.
         * 
         * @param M     The real-valued other matrix in the dot product.
         * @return      A full 2D matrix of the same element type as this one, with the result of the dot product.
         */
        public ObjectMatrix<T> dot(Matrix M) {
            assertSize(M.cols());
            ObjectMatrix<T> P = getMatrixInstance(size(), M.cols(), false);
            for(int i=size(); --i >= 0; ) for(int j=M.cols(); --j >= 0; ) {
                T e = copyOfDiagonal(i);
                e.scale(M.get(i, j));
                P.set(i, j, e);                
            }
            return P;   
        }
        
        @Override
        public ObjectVector<T> dot(MathVector<? extends T> v) throws ShapeException {
            return (ObjectVector<T>) super.dot(v); 
        }

        @Override
        public ObjectVector<T> dot(RealVector v) {
            return (ObjectVector<T>) super.dot(v);
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
        public void dot(MathVector<? extends T> v, MathVector<T> result) throws ShapeException {
            assertSize(v.size());
            assertSize(result.size());
            for(int i=size(); --i >= 0; ) result.getComponent(i).setProduct(getDiagonal(i), v.getComponent(i));
        }

        @Override
        public void dot(T[] v, T[] result) {
            assertSize(v.length);
            assertSize(result.length);
            for(int i=size(); --i >= 0; ) result[i].setProduct(getDiagonal(i), v[i]);            
        }
        
        @Override
        public void dot(T[] v, MathVector<T> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i=size(); --i >= 0; ) result.getComponent(i).setProduct(getDiagonal(i), v[i]);            
        }

        @Override
        public void dot(double[] v, MathVector<T> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i=size(); --i >= 0; ) {
                T p = copyOfDiagonal(i);
                p.scale(v[i]);
                result.setComponent(i, p);
            }
        }

        @Override
        public void dot(float[] v, MathVector<T> result) {
            assertSize(v.length);
            assertSize(result.size());
            for(int i=size(); --i >= 0; ) {
                T p = copyOfDiagonal(i);
                p.scale(v[i]);
                result.setComponent(i, p);
            }
        }

    
        @Override
        public void setSum(DiagonalMatrix<? extends T> a, DiagonalMatrix<? extends T> b) {
            assertSize(a.size());
            assertSize(b.size());
            for(int i=size(); --i >= 0; ) getDiagonal(i).setSum(a.getDiagonal(i), b.getDiagonal(i));
        }

        @Override
        public void setDifference(DiagonalMatrix<? extends T> a, DiagonalMatrix<? extends T> b) {
            assertSize(a.size());
            assertSize(b.size());
            for(int i=size(); --i >= 0; ) getDiagonal(i).setDifference(a.getDiagonal(i), b.getDiagonal(i));
        }

        @Override
        public ObjectVector<T> getVectorInstance(int size) {
            return new ObjectVector<>(type, size);
        }

        @Override
        public Generic<T> getDiagonalMatrixInstance(int size) {
            return new Generic<>(type, size);
        }

        @Override
        public ObjectMatrix<T> toFullMatrix() {
            return (ObjectMatrix<T>) super.toFullMatrix();
        }
        
        
        @SuppressWarnings("unchecked")
        @Override
        public ObjectMatrix<T> getMatrixInstance(int rows, int cols, boolean initialize) {
            if(initialize) return new ObjectMatrix<>(type, rows, cols);
  
            try { return new ObjectMatrix<>((T[][]) Array.newInstance(type, new int[] { rows, cols })); }
            catch(Exception e) { Util.error(this, e); }
            return null;
        }
        
        
        @SuppressWarnings("unchecked")
        public double distanceTo(DiagonalMatrix<?> o) {
            if(o.getElementType().isAssignableFrom(type)) return o.distanceTo(this);
            
            assertSize(o.size());
               
            T e = newEntry();
           
            if(type.isAssignableFrom(o.getElementType())) { 
                double sum2 = 0.0;
                for(int i=size(); --i >= 0; ) {
                    e.setDifference(data[i], (T) o.get(i, i));
                    sum2 += e.absSquared();
                }
                return Math.sqrt(sum2);
            }
           
            
            if(Number.class.isAssignableFrom(o.getElementType())) {  
                double sum2 = 0.0;
                for(int i=size(); --i >= 0; ) {
                    e.setIdentity();
                    e.scale(((Number) o.get(i, i)).doubleValue());
                    e.subtract(getDiagonal(i));
                    sum2 += e.absSquared();
                }
                return Math.sqrt(sum2);
            }
            
            return Double.NaN;
        }

        @SuppressWarnings("unchecked")
        @Override
        public double distanceTo(MatrixAlgebra<?, ?> o) {  
            if(o instanceof DiagonalMatrix) return distanceTo((DiagonalMatrix<?>) o);
            if(o.getElementType().isAssignableFrom(type)) return o.distanceTo(this);
            
            assertSize(o.rows());
            assertSize(o.cols());
            
            T e = newEntry();
            
            if(type.isAssignableFrom(o.getElementType())) {
                double sum2 = 0.0;
                for(int i=size(); --i >= 0; ) {
                    e.setDifference(data[i], (T) o.get(i, i));
                    sum2 += e.absSquared();
                    for(int j=size(); --j >= 0; ) if(j != i) sum2 += ((T) o.get(i, j)).absSquared();
                }
                return Math.sqrt(sum2);
            }
            
            if(Number.class.isAssignableFrom(o.getElementType())) {   
                double sum2 = 0.0;
                for(int i=rows(); --i >= 0; ) {
                    e.setIdentity();
                    e.scale(((Number) o.get(i, i)).doubleValue());
                    e.subtract(getDiagonal(i));
                    sum2 += e.absSquared();
                    for(int j=size(); --j >= 0; ) if(j != i) sum2 += ((T) o.get(i, j)).absSquared();
                }
                return Math.sqrt(sum2);
            }
            
            return Double.NaN;
        }
   
        @Override
        public final T get(int i, int j) {
            return (i == j) ? getDiagonal(i) : null;
        }
   

        @Override
        public final T getDiagonal(int i) { return data[i]; }

        @SuppressWarnings("unchecked")
        @Override
        public final T copyOfDiagonal(int i) { return (T) data[i].copy(); }

        @Override
        public final void setDiagonal(int i, T value) { data[i] = value; }

        @Override
        public final void addDiagonal(int i, T value) { data[i].add(value); }

        @Override
        public final void addDiagonalScaled(int i, T value, double factor) { data[i].addScaled(value, factor); }

        @Override
        public final void scaleDiagonal(int i, double factor) { data[i].scale(factor); }

        @Override
        public final void multiplyDiagonalBy(int i, T factor) { data[i].multiplyBy(factor); }

        @Override
        public final void zeroDiagonal(int i) { data[i].isNull(); }

        @Override
        public final boolean isNullDiagonal(int i) { return data[i].isNull(); }
        
    }
    
    /**
     * A diagonal matrix class with complex-valued elements.
     * 
     * @author Attila Kovacs <attila@sigmyne.com>
     *
     */
    public static class Complex extends Generic<jnum.math.Complex> {
        
        /**
         * Construct a new diagonal matrix with complex elements with the specified size (number of diagonal elements).
         * 
         * @param size      Number of diagonal elements in the matrix.
         */
        public Complex(int size) {
            super(jnum.math.Complex.class, size);
        }
 
        /**
         * Constructs a diagonal matrix with complex elements and with the specified object array holding the 
         * references to the diagonal matrix elements. Thus, changes to the elements contained in the array will 
         * necessarily affect the diagonal values contained in this matrix and vice versa.
         * 
         * @param data      The array that will be holding the diagonal elements in this matrix.
         */
        public Complex(jnum.math.Complex[] data) {
            super(jnum.math.Complex.class, data);
        }
        
        @Override
        public Complex clone() {
            return (Complex) super.clone();
        }
        
        @Override
        public Complex copy() {
            return (Complex) super.copy();
        }
        
        @Override
        public Complex copy(boolean withContent) {
            return (Complex) super.copy(withContent);
        }
        
        @Override
        public ComplexVector getVectorInstance(int size) {
            return new ComplexVector(size);
        }
        
        
        @Override
        public ComplexVector dot(MathVector<? extends jnum.math.Complex> v) throws ShapeException {
            return (ComplexVector) super.dot(v); 
        }

        @Override
        public ComplexVector dot(RealVector v) {
            return (ComplexVector) super.dot(v);
        }

        @Override
        public Complex getDiagonalMatrixInstance(int size) {
            return new Complex(size);
        }
        
        @Override
        public Complex dot(DiagonalMatrix<? extends jnum.math.Complex> righthand) {
            return (Complex) super.dot(righthand);
        }

        @Override
        public ComplexMatrix getMatrixInstance(int rows, int cols, boolean initialize) {
            if(initialize) return new ComplexMatrix(rows, cols);
            return new ComplexMatrix(new jnum.math.Complex[rows][cols]);
        }
        
        @Override
        public ComplexMatrix toFullMatrix() {
            return (ComplexMatrix) super.toFullMatrix();
        }
        
        
        @Override
        public ComplexMatrix dot(MatrixAlgebra<?, ? extends jnum.math.Complex> righthand) {
            return (ComplexMatrix) super.dot(righthand);
        }
        
        @Override
        public ComplexMatrix dot(Matrix M) {
            return (ComplexMatrix) super.dot(M);
        }
            
    }
    
}

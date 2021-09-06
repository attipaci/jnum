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



import java.text.*;
import java.util.Arrays;
import java.io.Serializable;

import jnum.CopiableContent;
import jnum.CopyCat;
import jnum.data.index.Index2D;
import jnum.data.index.IndexedValues;
import jnum.math.MathVector;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.util.ArrayUtil;


/**
 * An abstract Matrix class representing a matrix for some generic type element. It has two principal subclasses, 
 * {@link Matrix}, which is a real-valued matrix with essentially primitive <code>double</code> elements, and 
 * {@link ObjectMatrix}, which handles matrices for generic type objects as long as they provide the required algebra 
 * to support matrix operation. For example {@link ComplexMatrix} with {@link jnum.math.Complex} elements is an example subtype, 
 * but one could construct matrices e.g. with {@link jnum.data.WeightedPoint} or even {@link Matrix} elements (for example a 
 * matrix of matrices), or matrices with other more complex types...
 * 
 * @author Attila Kovacs
 *
 * @param <T>       The generic type of the elements in this matrix.
 */
public abstract class AbstractMatrix<T> implements SquareMatrixAlgebra<MatrixAlgebra<?, ? extends T>, T>, Serializable, 
Cloneable, CopiableContent<AbstractMatrix<T>>, CopyCat<AbstractMatrix<T>>, NumberFormating, DecimalFormating {

    /** */
	private static final long serialVersionUID = 8165960625207147822L;

	
	/**
	 * Constructor that subclasses can rely on but should never be publicly accessible...
	 */
	protected AbstractMatrix() {}
	
	/**
	 * Constructor of a matrix with a specific element class, and size.
	 * 
	 * @param type     Java class for elements. The class should have a constructor for new elements without
	 *                 arguments.
	 * @param rows     Matrix rows
	 * @param cols     Matric columns
	 */
	public AbstractMatrix(Class<? extends T> type, int rows, int cols) {
		setData(ArrayUtil.createArray(type, rows, cols));
	}

	
	@SuppressWarnings("unchecked")
    @Override
    public AbstractMatrix<T> clone() {
        try { return (AbstractMatrix<T>) super.clone(); } 
        catch(CloneNotSupportedException e) { return null; }
    }
	

	@SuppressWarnings("cast")
	@Override
	public AbstractMatrix<T> copy() {
	    return (AbstractMatrix<T>) copy(true);
	}

	@Override
	public void copy(AbstractMatrix<T> M) {
	    assertSize(M.rows(), M.cols());
	    for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >=0; ) set(i, j, M.copyOf(i, j));
	}

    
    /**
     * Returns the underlying data object (usually a 2D array of sorts) that holds the matrix elements
     * 
     * @return     The underlying data object of this matrix.
     */
    public abstract Object getData();
    
    /**
     * Sets the underlying data of this matrix to the specified value. Implementations should assert
     * that the supplied data is the same size as the matrix was designated to be at creation. If
     * not, the function should throw a {@link ShapeException}.
     * 
     * @param data The new underlying data or this matrix.
     */
    public abstract void setData(Object data) throws ShapeException;


    /**
     * Creates a new genetic type matrix entry.
     *
     * @return  A new generic type enty in this matrix.
     */
    public abstract T newEntry(); 
    
    /**
     * Creates a matrix element object that can be used to represent and manipulate values
     * contained in this matrix.
     * 
     * @return  
     */
    public abstract MatrixElement<T> getElementInstance();

    /**
     * Creates a vector of the same generic type as the elements in this matrix.
     * 
     * @param size  The size of the vector
     * @return      A new vector with the same generic type as the entries in this matrix.
     */
    @Override
    public abstract AbstractVector<T> getVectorInstance(int size);
    
    /**
     * Creates a vector basis object of the same generic type as the elements in this matrix.
     * 
     * @return      A new vector basis with the same generic type as the entries in this matrix.
     */
    public abstract AbstractVectorBasis<T> getVectorBasisInstance();
    
    /**
     * Creates a new matrix of the same generic type as this one.
     * 
     * @param rows          Number of rows in the new matrix
     * @param cols          Number of columns in the new matrix
     * @param initialize    If true, the matrix is going to be populated with the generic type elements
     *                      created with the standard generic type constructor. If false only the
     *                      Generic type container object (array) is created, but with the slots
     *                      populated with null values. Theoption hasd no effect for Matrices that
     *                      have backing arrays of primitive types, since those do not hold references
     *                      but rather primitive values which are always initialized to zero by
     *                      default at creation.
     *                      
     * @return
     */
    public abstract AbstractMatrix<T> getMatrixInstance(int rows, int cols, boolean initialize);

    /**
     * Gets the LU decomposition of this matrix of this matrix, if possible. LU decompositions
     * offer an efficient way to obtain the determinant of the matrix or to calculate its 
     * inverse. 
     * 
     * @return      The LU decomposition of this matrix.
     * @throws SquareMatrixException    If the operartion is attempted on a non-square matrix.  
     */
    public abstract LUDecomposition<T> getLUDecomposition() throws SquareMatrixException;

    /**
     * Gets a matrix inverter object for this matrix using Gauss-Jordan elimination to
     * calculate the inverse. The returned object can also readily provide the rank
     * of this matrix at no additional cost.
     * 
     * @return      An intermediate matrix inversion object with some additional benefits.
     */
    public abstract GaussInverter<T> getGaussInverter();
    
    @Override
    public abstract int rows();
    

    @Override
    public abstract int cols();

    
    @Override
    public final int capacity() { return rows() * cols(); }
    
    @Override
    public final int dimension() { return 2; }
    
    @Override
    public final Index2D getIndexInstance() {
        return new Index2D();
    }

    @Override
    public final Index2D copyOfIndex(Index2D index) {
         return index.copy();
    }

    @Override
    public final boolean conformsTo(Index2D size) {
        return size.i() == rows() && size.j() == cols();
    }

    @Override
    public final boolean conformsTo(IndexedValues<Index2D, ?> data) {
        return conformsTo(data.getSize());
    }

    @Override
    public final boolean containsIndex(Index2D index) {
        if(index.i() < 0.0) return false;
        if(index.i() > rows()) return false;
        if(index.j() < 0.0) return false;
        if(index.j() > cols()) return false;
        return true;
    }
   
    

    @Override
    public final boolean isSquare() { return rows() == cols(); }
     
    
    @Override
    public final boolean isDiagonal() {
        if(!isSquare()) return false;
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) if(i != j) if(!isNull(i, j)) return false;
        return true;
    }

    /**
     * Gets the dimensions (rows, cols) of this matrix.
     * 
     * @return     An index object in which the i and j indices are set to the number of rows and columns
     *             respectively in this matrix.
     */
    @Override
    public final Index2D getSize() { return new Index2D(rows(), cols()); }
    

    @Override
    public final boolean isSize(int rows, int cols) {
        return rows == rows() && cols == cols();        
    }
    
    /**
     * Checks if the matrix has the expected size for some operation. If not a {@link ShapeException} is thrown.
     * 
     * @param rows          The expected number of rows in this matrix
     * @param cols          The expected number of columns in this matrix.
     * @throws ShapeException   If the matrix has a different shape or size than what's expected.
     */
    public void assertSize(int rows, int cols) throws ShapeException {
        if(!isSize(rows, cols)) throw new ShapeException("Matrix has wrong size " + getSizeString() + ". Expected " + rows + "x" + cols +".");
    }
    


    @Override
    public final boolean conformsTo(MatrixAlgebra<?, ?> M) {
        return M.isSize(rows(), cols());
    }
       
	  

    @Override
    public abstract T get(int row, int col);
    
   
    
    /**
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param idx      The (row, col) index of matrix element
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    @Override
    public final T get(Index2D idx) { return get(idx.i(), idx.j()); }
   
 

    @Override
    public abstract void set(int row, int col, T v);
   
    
    /**
    * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
    * 
    * @param idx      The (row,col) index of matrix element
    * @param value    The new matrix element to set. (For object types the matrix will hold a reference
    *                 to the specified value).
    */
    @Override
    public final void set(Index2D idx, T value) { set(idx.i(),idx.j(), value); }
   
    
  
    /**
     * Gets an independent copy of an entry in this matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element
     * @return         A deep copy of the value at the specified location.
     */
    public abstract T copyOf(int row, int col);
    
    /**
     * Gets an independent copy of an entry in this matrix.
     * 
     * @param index    The (row,col) index of matrix element
     * @return         A deep copy of the value at the specified location.
     */
    public final T copyOf(Index2D index) { return copyOf(index.i(), index.j()); }
   
    /**
     * Adds a value of the same generic type to an element of this matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element
     */
    public abstract void add(int row, int col, T v);
    
    /**
     * Adds a value of the same generic type to an element of this matrix.
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final void add(Index2D idx, T value) { add(idx.i(),idx.j(), value); }
    

    /**
     * Adds a scalar value to an element of this matrix. For non-number types, this
     * means adding an identity element scaled by the specified scalar value. I.e.
     * <code>M[i][j]</code> is incremented by <code>v * I</code>, where I is the identity element.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element
     */
    public abstract void add(int row, int col, double v);
    
    /**
     * Adds a scalar value to an element of this matrix. For non-number types, this
     * means adding an identity element scaled by the specified scalar value. I.e.
     * <code>M[i][j]</code> is incremented by <code>v * I</code>, where I is the identity element.
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final void add(Index2D idx, double value) { add(idx.i(),idx.j(), value); }
    
    /**
     * Clears (sets to zeroes) an entry in this matrix.
     * 
     * @param i      row index of matrix element
     * @param j      column index of matrix element
     */
    public abstract void clear(int i, int j);
    
    /**
     * Clears (sets to zeroes) an entry in this matrix.
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final void clear(Index2D idx) { clear(idx.i(),idx.j()); }

    /**
     * Scales an entry in this matrix by the specified scalar factor.
     * 
     * @param i      row index of matrix element
     * @param j      column index of matrix element
     */
    public abstract void scale(int i, int j, double factor);
    
    /**
     * Scales an entry in this matrix by the specified scalar factor.
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final void scale(Index2D idx, double factor) { scale(idx.i(),idx.j(), factor); }

    /**
     * Checks if an entry in this matrix is a 'null' (zeroed) 
     * 
     * @param i      row index of matrix element
     * @param j      column index of matrix element
     */
    public abstract boolean isNull(int i, int j);
       
    /**
     * Checks if an entry in this matrix is a 'null' (zeroed) 
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final boolean isNull(Index2D idx) { return isNull(idx.i(), idx.j()); }
    
    
    @Override
    public final T getDiagonal(int i) {
        return get(i, i);
    }
    
    @Override
    public void setDiagonal(int i, T value) {
        set(i, i, value);
    }
    
    
    @Override
    public final T copyOfDiagonal(int i) {
        return get(i, i);
    }
    
    @Override
    public void addDiagonal(int i, T value) {
        add(i, i, value);
    }
    
    @Override
    public void scaleDiagonal(int i, double factor) {
        scale(i, i, factor);
    }
    
    @Override
    public void zeroDiagonal(int i) {
        clear(i, i);
    }
    
    @Override
    public boolean isNullDiagonal(int i) {
        return isNull(i, i);
    }
    
    
    
	/**
	 * Sets the data in the matrix to zeroes (or empty values). Same as {@link #zero()}.
	 * 
	 */
	public final void clear() { zero(); }
	
    @Override
    public final void zero() { for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) clear(i, j); }

    
    
    @Override
    public void add(MatrixAlgebra<?, ? extends T> o) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, o.get(i, j));
    }

    /**
     * Adds another matrix containing real values to this one. For matrices with non-number types
     * this means adding, for every element in this matrix, an identity element scaled by the 
     * matching element in the real valued matrix argument for each
     * element. I.e. <code>M[i][j]</code> is incremented by <code>o[i][j] * I</code>, 
     * where I is the identity element.
     * 
     * @param o     The real valued matrix to add to this one. 
     */
    public void add(Matrix o) {
        assertSize(o.rows(), o.cols()); 
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, o.get(i, j));       
    }

    /**
     * Adds another matrix containing real values to this one, with an overall scaling factor. 
     * For matrices with non-number types this means adding, for every element in this matrix, 
     * an identity element scaled by the product of the scaling factor and the matching element 
     * in the real valued matrix argument for each
     * element. I.e. <code>M[i][j]</code> is incremenred by <code>(factor * o[i][j]) * I</code>, 
     * where I is the identity element.
     * 
     * @param o     The real valued matrix to add to this one with the scaling factor. 
     */
    public void addScaled(Matrix o, double factor) {
        if(factor == 0.0) return;
        assertSize(o.rows(), o.cols()); 
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, o.get(i, j) * factor);
            
    }  
    
    
    /**
     * Subtracts another matrix containing real values to this one. For matrices with non-number types
     * this means subtracting, from every element in this matrix, an identity element scaled by the 
     * matching element in the real valued matrix argument for each
     * element. I.e. <code>M[i][j]</code> is decremented by <code>o[i][j] * I</code>, 
     * where I is the identity element.
     * 
     * @param o     The real valued matrix to subtract to this one. 
     */
    public void subtract(Matrix o) {
        assertSize(o.rows(), o.cols()); 
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, -o.get(i, j));
            
    }

    @Override
    public void scale(double factor) {
        if(factor == 0.0) clear();
        else if(factor == 1.0) return;
        else for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) scale(i, j, factor);
    }
    
    @Override
    public boolean isNull() {
        for(int i=0; i<rows(); i++) for(int j=cols(); --j >= 0; ) if(!isNull(i, j)) return false;
        return true;        
    }
    
  
    /**
     * Gets the largest absolute value from the matrix elements in a subspace of the matrix.
     * 
     * @param fromi     Inclusive row index of subspace start.
     * @param fromj     Inclusive column index of subspace start.
     * @param toi       Exclusive row index of subspace end.
     * @param toj       Exclusive colun index of subspace end.
     * @return          The largest absolute value among the elements in the subspace.
     */
    public abstract double getMagnitude(int fromi, int fromj, int toi, int toj);
    

    @Override
    public final double getMagnitude() {
        return getMagnitude(0, 0, rows(), cols());
    }
    
	@Override
    public final void setIdentity() {
        if(!isSquare()) throw new SquareMatrixException();
        zero();
        addIdentity(1.0);
    }
	
	@Override
    public final void addIdentity() { addIdentity(1.0); }
	
	@Override
    public final void subtractIdentity() { addIdentity(-1.0); }

	
	@SuppressWarnings("unchecked")
    @Override
    public AbstractMatrix<T> dot(MatrixAlgebra<?, ? extends T> B) {
	    if(B instanceof DiagonalMatrix) return (((DiagonalMatrix<T>) B).dot(this));
	    
        AbstractMatrix<T> P = getMatrixInstance(rows(), B.cols(), false);
        P.addProduct(this, B);
            
        return P;
    }
 
	/**
	 * Calculates the dot product of this matrix (<b>M</b>) with the diagonal matrix argument (<b>B</b>). That is
	 * it returns <b>M</b> <i>dot</i> <b>B</b>.
	 * 
	 * @param B        Diagonal matrix on the right-hand side of dot product
	 * @return         this matrix dotted with <b>B</b> from the right hand side.
	 */
	public AbstractMatrix<T> dot(DiagonalMatrix<T> B) {
	    return B.dot(this);
	}
	
	/**
	 * Calculates the dot product of this matrix (<b>M</b>) with the real-valued diagonal matrix argument 
	 * (<b>B</b>). That is it returns <b>M</b> <i>dot</i> <b>B</b>.
     * 
     * @param B        Real-valued diagonal matrix on the right-hand side of dot product
     * @return         this matrix dotted with <b>B</b> from the right hand side.
	 */
	public abstract AbstractMatrix<T> dot(DiagonalMatrix.Real B);
	
	
    @Override
    public AbstractVector<T> dot(double... v) {
        AbstractVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }
	
    
    @Override
    public AbstractVector<T> dot(float... v) {
        AbstractVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }

    
    @Override
    public AbstractVector<T> dot(T[] v) {
        AbstractVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }


    @Override
    public AbstractVector<T> dot(MathVector<? extends T> v) {
        AbstractVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }
    
    @Override
    public AbstractVector<T> dot(RealVector v) {
        AbstractVector<T> result = getVectorInstance(rows());
        dot(v, result);
        return result;
    }

    @Override
    public final void dot(RealVector v, MathVector<T> result) {
        dot(v.getData(), result);
    }
    
    
    /**
     * Add the product of matrices A and B to what is already contained in this matrix.
     * 
     * @param A    Left-hand matrix in product.
     * @param B    Right-hand matrix in product.
     */
    protected abstract void addProduct(MatrixAlgebra<?, ? extends T> A, MatrixAlgebra<?, ? extends T> B);


	/**
	 * Swaps two elements in this matrix
	 * 
	 * @param i1   row index of first element  
	 * @param j1   column index of first element
	 * @param i2   row index of second element
	 * @param j2   column index of second element
	 */
	public final void swapElements(int i1, int j1, int i2, int j2) {
		T i1j1 = get(i1, j1);
		set(i1, j1, get(i2, j2));
		set(i2, j2, i1j1);	
	}
	
	/**
	 * Swaps two elements in this matrix
	 * 
	 * @param i1   (row, col) index of first element
	 * @param i2   (row, col) index of second element.
	 */
	public final void swapElements(Index2D i1, Index2D i2) {
	    swapElements(i1.i(), i1.j(), i2.i(), i2.j());
	}
	

    /**
     * Swaps two rows in the matrix.
     * 
     * @param i    A row index
     * @param j    Another row index.
     */
    public abstract void swapRows(int i, int j);
	   
    /**
     * Copies a row of this matrix into the supplied vector of the same element type as 
     * that of this matrix. The vector will contain fully independent copies of the 
     * matrix's data, s.t. subsequent changes to the returned vector will not affect the 
     * matrix content.
     * 
     * @param i     row index
     * @param v     A vector into which to return the elements of the matrix row.  
     */
    public final void copyRowTo(int i, MathVector<T> v) throws ShapeException {
        for(int j=cols(); --j >= 0; ) v.setComponent(j, copyOf(i, j));
    }
    
    /**
     * Copies the contents of a matrix row into the supplied buffer. 
     * 
     * @param i         Matrix row index whose data is to be retrieved
     * @param v         Array into which the column data is copied. The array is not checked for size.
     */
    public void copyRowTo(int i, T[] v) {
        for(int j=cols(); --j >= 0; ) v[j] = copyOf(i, j);
    }

    /**
     * Returns the contents of the matrix row as a vector of the same generic type elements 
     * as this matrix. The underlying storage of the matrix row references that underlying
     * row array of the matrix. Thus, changes to the data in this row will result in
     * changes of the matrix directly, and vice versa.
     * 
     * @param i    Index of row to retrieve
     * @return     Reference to the underlying array that holds data for that row.
     */
    public final AbstractVector<T> copyOfRow(int i) {
        AbstractVector<T> v = getVectorInstance(cols());
        copyRowTo(i, v);
        return v;
    }

    
    /**
     * Copies the contents of a vector into a row of this matrix. For objects
     * type elements references are copied into the matrix.
     * 
     * @param i     row index
     * @param v     A vector holding the new data for the matrix row.  
     */
    public final void setRowData(int i, MathVector<T> v) throws ShapeException {
        if(v.size() != cols()) throw new ShapeException("Cannot set mismatched row.");
        for(int j=cols(); --j >= 0; ) set(i, j, v.getComponent(j));
    }

    /**
     * Sets the matrix row to the values provided in the argument. The argument itself is not referenced,
     * but the values in it are.
     * 
     * @param i             The index of the row to update
     * @param v             Array containing that data that is to be copied into the matrix row.
     * @throws ShapeException   If the supplied array does not match the matrix column in size.
     */
    public void setRowData(int i, T[] v) {
        if(v.length != cols()) throw new ShapeException("Cannot set mismatched row.");
        for(int j=cols(); --j >= 0; ) set(i, j, v[j]);
    }

    
	
	/**
     * Copies a column of this matrix into the supplied vector of the same element type
     * as that of this matrix. The vector will contain fully independent copies of the 
     * matrix's data, s.t. subsequent changes to the returned vector will not affect the 
     * matrix content.
     * 
     * @param j     column index
     * @param v     A vector into which to return the elements of the matrix column.   
     */
	public final void copyColumnTo(int j, MathVector<T> v) throws ShapeException {
	    for(int i=rows(); --i >= 0; ) v.setComponent(i, copyOf(i, j));
	}
	
	/**
     * Copies the contents of a matrix column into the supplied buffer.
     * 
     * @param j         Matrix column index whose data is to be retrieved
     * @param v         Array into which the column data is copied. The array is not checked for size.
     */
    public void copyColumnTo(int j, T[] v) {
        for(int i=rows(); --i >= 0; ) v[i] = copyOf(i, j);
    }
	
	/**
	 * Gets the data from a colum of this matrix as a vector of matching generic type.
	 * The vector will contain fully independent copies of the matrix's data, s.t.
	 * subsequent changes to the returned vector will not affect the matrix content.
	 * 
	 * @param j
	 * @return
	 */
	public final AbstractVector<T> copyOfColumn(int j) {
	    AbstractVector<T> v = getVectorInstance(rows());
	    copyColumnTo(j, v);
	    return v;
	}
	
	/**
     * Copies a the contents of a vector into a column of this matrix. For object type
     * elements, the references are copied into the matrix.
     * 
     * @param j     column index
     * @param v     A vector with the new data for the matrix column.
     */
	public final void setColumnData(int j, MathVector<T> v) throws ShapeException {
        if(v.size() != cols()) throw new ShapeException("Cannot set mismatched column.");
        for(int i=rows(); --i >= 0; ) set(i, j, v.getComponent(i));
	}

    
    /**
     * Sets the matrix column to the values provided in the argument. The argument itself is not referenced,
     * but the values in it are.
     * 
     * @param j             The index of the column to update
     * @param v             Array containing that data that is to be copied into the matrix column.
     * @throws ShapeException   If the supplied array does not match the matrix column in size.
     */
    public void setColumnData(int j, T[] v) {
        if(v.length != cols()) throw new ShapeException("Cannot set mismatched column.");
        for(int i=rows(); --i >= 0; ) set(i, j, v[i]);
    }
	
	/**
	 * Copies the data (references) of another matrix into a subscape of this matrix.
	 * 
	 * @param patch    The subspace matrix
	 * @param fromRow  The starting row index of the subspace in this matrix.
	 * @param fromCol  The starting colum index of the subspace in this matrix.
	 */
	public void paste(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		ArrayUtil.paste(patch.getData(), getData(), new int[] { fromRow, fromCol});
	}
	
	/**
	 * Returns a subspace of this matrix as a new matrix of the same class.
	 * 
	 * @param rows     An integer array containing the row indices in this matrix of the subspace.
	 * @param cols     An integer array containing the row indices in this matrix of the subspace.
	 * @return     A new matrix contaiing only the selected rows and columns of this matrix in the specified
	 *             order.
	 */
	public AbstractMatrix<T> subspace(int[] rows, int[] cols) {
	    AbstractMatrix<T> sub = getMatrixInstance(rows.length, cols.length, false);
	    for(int i=rows.length; --i >= 0; ) for(int j = cols.length; --j >= 0; ) sub.set(i,  j, copyOf(i, j));
	    return sub;
	}

	/**
	 * Returns a subspace of this matrix as a new matrix of the same class.
	 * 
	 * @param fromRow      Inclusive starting row index of subspace.
	 * @param fromCol      Inclusive starting column index of subspace.
	 * @param toRow        Exclusive ending row index of subspace.
	 * @param toCol        Exclusive ending column index of subspace.
	 * @return     A new matrix contaiing only the selected rows and columns of this matrix in the specified
     *             order.
	 */
	public AbstractMatrix<T> subspace(int fromRow, int fromCol, int toRow, int toCol) {
	    AbstractMatrix<T> sub = getMatrixInstance(toRow - fromRow, toCol - fromCol, false);
        for(int i=fromRow; i < toRow; i++) for(int j = fromCol; j < toCol; j++) sub.set(i,  j, copyOf(i, j));
        return sub;
	}

	/**
	 * Returns a subspace of this matrix as a new matrix of the same class.
	 * 
	 * @param from     Inclusive starting (row, col) index of subspace.
	 * @param to       Exclusive ending (row, col) index of subspace.
	 * @return     A new matrix contaiing only the selected rows and columns of this matrix in the specified
     *             order.
	 */
	public AbstractMatrix<T> subspace(Index2D from, Index2D to) {
	    return subspace(from.i(), from.j(), to.i(), to.j()); 
	}
	
	
	/**
	 * Gets a string representation of the size of this matrix.
	 * 
	 * @return     String representation of this matrix's size.
	 */
	@Override
    public String getSizeString() {
		return "[" + rows() + "x" + cols() + "]";
	}
	
	/**
	 * Gets a string representation of this matrix type and size.
	 * 
	 * @return     String representation of the type and size of this matrix.
	 */
	public String getIDString() {
		return getClass().getSimpleName() + getSizeString();
	}
	

	@Override
	public String toString() {
		return getIDString() + ":\n" + ArrayUtil.toString((Object[][]) getData());
	}
	

	@Override
	public String toString(NumberFormat nf) {
		return getIDString() + ":\n" + ArrayUtil.toString((NumberFormating[][]) getData(), nf);
	}
	

	@Override
	public String toString(int decimals) {
		return getIDString() + ":\n" + ArrayUtil.toString(getData(), decimals);
	}
		
	
	/**
	 * Gets the transpose of this matrix.
	 * 
	 * @return     A new matrix that is the transpose of this matrix. Two transpose should contain
	 *             copies of this matrices elements, s.t. any modifications to the transpose do
	 *             not affect the contents of this matrix and vice versa.
	 */
	public AbstractMatrix<T> getTranspose() {
	    AbstractMatrix<T> transpose = getMatrixInstance(cols(), rows(), false);
	    for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) transpose.set(j, i, copyOf(i, j));
	    return transpose;
	}


	/**
	 * Get the matrix into row-echelon form, using Gauss-Jordan elimination.
	 * 
	 */
	public void gaussJordan() { 
	    final Index2D[] idx = new Index2D[rows()];
	    final int[] ipiv = new int[rows()];

	    MatrixElement<T> e = getElementInstance();
	    MatrixElement<T> product = getElementInstance();
	    
	    Arrays.fill(ipiv, -1);

	    for(int i=rows(); --i >= 0; ) {
	        int icol=-1, irow=-1;
	        double big=0.0;
	        for(int j=rows(); --j >= 0; ) if(ipiv[j] != 0) for(int k=rows(); --k >= 0; ) {
	            if(ipiv[k] == -1) {
	                double mag = e.from(get(i, j)).abs();
	                if(mag >= big) {
	                    big=mag;
	                    irow=j;
	                    icol=k;
	                }
	            } 
	            else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular Matrix-1 during Gauss-Jordan elimination.");
	        }
	        ++(ipiv[icol]);
	        if(irow != icol) swapRows(irow, icol);

	        idx[i] = new Index2D(irow, icol);
	        
	        if(e.from(icol, icol).isNull()) throw new IllegalArgumentException("Singular Matrix-2 during Gauss-Jordan elimination.");

	        T pinv = e.getInverse();
	        e.setIdentity();
	        e.applyTo(icol, icol);
	        
	        for(int j=cols(); --j >= 0; ) {
	            product.setProduct(pinv, get(icol, j));
	            product.applyTo(icol, j);
	        }
	        
	        for(int ll=rows(); --ll >= 0; ) if(ll != icol) {
	            e.copy(ll, icol);
	            e.scale(-1.0);
	            
	            for(int j=cols(); --j >= 0; ) {
	                product.setProduct(e.value(), get(icol, j));
	                add(ll, j, product.value());
	            }
	        }
	    }
	    for(int l=rows(); -- l>= 0; ) {
	        Index2D index = idx[l];
	        if(index.i() != index.j()) for(int k=rows(); --k >= 0; ) swapElements(k, index.i(), k, index.j());
	    }
	}
	

	@Override
    public T getDeterminant() {
	    return getLUDecomposition().getDeterminant();
	}

    @Override
    public AbstractMatrix<T> getInverse() {
        return getLUInverse();
    }
   
    /**
     * Returns the inverse of this matrix, calculated via LU decomposition.
     * 
     * @return  The inverse of this matrix.
     */
    public AbstractMatrix<T> getLUInverse() {
        return getLUDecomposition().getInverseMatrix();
    }


    /**
     * Returns the inverse of this matrix calculated via Gauss-Jordan elimination,
     * 
     * @return  The inverse of this matrix.
     */
    public AbstractMatrix<T> getGaussInverse() {
        return getGaussInverter().getInverseMatrix();
    }
    
   
    @Override
    public final int getRank() {
        AbstractMatrix<T> copy = copy();
        copy.gaussJordan();
        
        double tiny2 = 1e-12 * copy.getMagnitude();
        tiny2 *= tiny2;
       
        MatrixElement<T> e = getElementInstance();
        
        int rank = 0;
        for(int i=rows(); --i >= 0; ) 
            for(int j=cols(); --j >= 0; ) if(e.from(i, j).absSquared() > tiny2) {
                rank++;
                break;
            }
        
        return rank;
    }
    
    @Override
    public void sanitize() {
        double tiny2 = 1e-12 * getMagnitude();
        tiny2 *= tiny2;
        
        MatrixElement<T> e = getElementInstance();
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) if(e.from(i, j).absSquared() < tiny2) clear(i, j);
    }
    
    /**
     * Gets the basis vectors of this matrix, that is the set of vectors on which this matrix acts
     * as a scalar with the corresponding eigenvalue.
     * 
     * @return  The vector basis for this matrix.
     */
    public final AbstractVectorBasis<T> getBasis() {
        AbstractVectorBasis<T> basis = getVectorBasisInstance();
        AbstractMatrix<T> copy = copy();
        copy.gaussJordan();

        double tiny2 = 1e-12 * copy.getMagnitude();
        tiny2 *= tiny2;
        
        MatrixElement<T> e = getElementInstance();
        
        int col = 0;
        for(int i=0; i<rows(); i++)
            for(int j=col; j<cols(); j++) if(e.from(i, j).absSquared() > tiny2) {
                AbstractVector<T> v = basis.getVectorInstance();
                copyColumnTo(j, v);
                basis.add(v);
                col = j+1;
                break;
            }
        
        return basis;
    }  
    
}


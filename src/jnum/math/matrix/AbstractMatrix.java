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



import java.text.*;
import java.io.Serializable;

import jnum.CopiableContent;
import jnum.CopyCat;
import jnum.ShapeException;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.data.IndexedEntries;
import jnum.data.IndexedValues;
import jnum.data.image.Index2D;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;


/**
 * An abstract Matrix class representing a matrix for some generic element type. It has two principal subclasses, {@link Matrix}, which
 * is a real-valued matrix with essentially primitive double elements, and {@link GenerixMatrix}, which handles matrices for generic type 
 * objects as long as they provide the required algebra to support matrix operation. For example {@link ComplexMatrix} with {@link Complex}
 * elements is an example subtype, but one could construct matrices e.g. with {@link Matrix} or {@link ObjectMatrix} elements (for a 
 * matrix of matrices), or matrices with other more complex types...
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <T>       The generic type of the elements in a matrix.
 */
public abstract class AbstractMatrix<T> implements IndexedEntries<Index2D, T>, MatrixAlgebra<AbstractMatrix<? extends T>>, SquareMatrixAlgebra<T>, Serializable, 
Cloneable, CopiableContent<AbstractMatrix<T>>, CopyCat<AbstractMatrix<T>>, NumberFormating, DecimalFormating {

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
		setData(ArrayUtil.createArray(type, new int[] {rows, cols}));
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
     * Gets the class of elements contained in this matrix.
     * 
     * @return     The class of elements contained in this matrix.
     */
    public abstract Class<T> getElementType();
    
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
     * Creates a new genetic type matrix element.
     *
     * @return the t
     */
    protected T createElement() {
        try { return getElementType().getConstructor().newInstance(); }
        catch(Exception e) { 
            Util.error(this, e);
            return null;
        }   
    }
    
    
    protected abstract AbstractMatrix<T> createMatrix(int rows, int cols, boolean initialize);
    
    /**
     * Gets the number of columns in this matrix
     * 
     * @return     Number of columns in this matrix.
     */
    public abstract int cols();

    /**
     * Gets the number of rows in this matrix.
     * 
     * @return     Nmber of rows in this matrix.
     */
    public abstract int rows();
    
    
    @Override
    public int capacity() { return rows() * cols(); }
    
    @Override
    public final int dimension() { return 2; }
    
    @Override
    public Index2D getIndexInstance() {
        return new Index2D();
    }

    @Override
    public Index2D copyOfIndex(Index2D index) {
         return index.copy();
    }

    @Override
    public boolean conformsTo(Index2D size) {
        return size.i() == rows() && size.j() == cols();
    }

    @Override
    public boolean conformsTo(IndexedValues<Index2D, ?> data) {
        return conformsTo(data.getSize());
    }

    @Override
    public boolean containsIndex(Index2D index) {
        if(index.i() < 0.0) return false;
        if(index.i() > rows()) return false;
        if(index.j() < 0.0) return false;
        if(index.j() > cols()) return false;
        return true;
    }
   
    
    /**
     * Checks if this is a square matrix (i.e. it has the same number of rows and columns.).
     * 
     * @return     <code>true</code> if this is a square matrix otherwise <code>false</code>.
     */
    public boolean isSquare() { return rows() == cols(); }
     
    

    /**
     * Checks if the matrix is effectively a scalar enclosed in a matrix object, i.e. if it is a 1x1 matrix with
     * a single element.
     * 
     * @return
     */
    public boolean isScalar() { return rows() * cols() == 1; }
    

    /**
     * Gets the dimensions (rows, cols) of this matrix.
     * 
     * @return     An index object in which the i and j indices are set to the number of rows and columns
     *             respectively in this matrix.
     */
    @Override
    public Index2D getSize() { return new Index2D(rows(), cols()); }
    
    /** 
     * Checks if the size matches what is expected
     * 
     * @param rows     Number of expected rows.
     * @param cols     Number of expected columns
     * @return         true if the matrix contains the same number of rows and columns as specified. Otherwise false.
     */
    public boolean isSize(int rows, int cols) {
        return rows == rows() && cols == cols();        
    }
    
    /**
     * Checks if the matrix has the expected size for some operation. If not a {@link ShapeException} is thrown.
     * 
     * @param rows
     * @param cols
     * @throws ShapeException
     */
    public void assertSize(int rows, int cols) throws ShapeException {
        if(!isSize(rows, cols)) throw new ShapeException("Matrix has wrong size " + getSizeString() + ". Expected " + rows + "x" + cols +".");
    }
    

    /**
     * Checks if this matrix is equal in size to another matrix. I.e. both matrices have the same number of rows
     * and columns. (But the two matrices could have elements of very different types...)
     * 
     * @param M
     * @return
     */
    public boolean isEqualSize(AbstractMatrix<?> M) {
        return M.isSize(rows(), cols());
    }
       
	   

    /**
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    public abstract T get(int row, int col);
    
    /**
     * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @param v        The new matrix element to set. (For object types the matrix will hold a reference
     *                 to the specified value).
     */
    public abstract void set(int row, int col, T v);
    
    /**
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param idx      The (row, col) index of matrix element
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    @Override
    public final T get(Index2D idx) { return get(idx.i(), idx.j()); }
    
    protected abstract T copyOf(int i, int j);
    
    
    
    /**
    * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
    * 
    * @param idx      The (row,col) index of matrix element
    * @param v        The new matrix element to set. (For object types the matrix will hold a reference
    *                 to the specified value).
    */
    @Override
    public final void set(Index2D idx, T value) { set(idx.i(),idx.j(), value); }
    
    public abstract void clear(int i, int j);
    
    public final void clear(Index2D index) {
        clear(index.i(), index.j());
    }
    
    /**
     * Adds a value to the matrix element at the specified row, column index in the matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @param v        The increment to the matrix element.
     */
    public abstract void add(int row, int col, T increment);

    /**
     * Adds a value to the matrix element at the specified row, column index in the matrix.
     * 
     * @param idx      The (row, col) index of the matrix element.
     * @param v        The increment to the matrix element.
     */
    public final void add(Index2D idx, T increment) { add(idx.i(),idx.j(), increment); }
    
    /**
     * Adds a scaled value to the matrix element at the specified row, column index in the matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element.
     * @param v        The increment to the matrix element.
     * @param scaling  Scaling factor for the increment value for adding to matrix element 
     */
    public abstract void addScaled(int row, int col, T increment, double scaling);

    /**
     * Adds a scaled value to the matrix element at the specified row, column index in the matrix.
     * 
     * @param row      row index of matrix element
     * @param idx      The (row, col) index of the matrix element to be incremented.
     * @param scaling  Scaling factor for the increment value for adding to matrix element 
     */
    public final void addScaledValue(Index2D idx, T increment, double scaling) { addScaled(idx.i(),idx.j(), increment, scaling); }
    
  
    public abstract void scale(int i, int j, double factor);
    
    
    public final void scale(Index2D idx, double factor) {
        scale(idx.i(),idx.j(), factor);
    }
    
    public abstract void multiplyBy(int i, int j, T factor);
    
    public final void multiplyBy(Index2D idx, T factor) {
        multiplyBy(idx.i(),idx.j(), factor);
    }

	/**
	 * Sets the data in the matrix to zeroes (or empty values).
	 * 
	 */
	public void clear() { zero(); }
	
    /**
     * Multiply all matrix elements by a factor of the same element type.
     * 
     * @param factor       The factor to scale matrix elements with.
     */
    public void scale(T factor) {
        for(int i=0; i<rows(); i++) scaleRow(i, factor);
    }
    
   
	
    @Override
    public boolean isNull() {
        for(int i=0; i<rows(); i++) if(!isNullRow(i)) return false;
        return true;        
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
	
	@Override
	public void setProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) { 
		if(A.isScalar()) {
			try { setData(ArrayUtil.copyOf(B.getData())); }
			catch(Exception e) { Util.error(this, e); }
			scale(A.get(0, 0));
		}
		else if(B.isScalar()) setProduct(B, A);
		else {
		    if(A.cols() != B.rows()) throw new ShapeException("Incompatible Dimensions: " + A + "," + B);
		    zero();
		    addProduct(A, B);
		}
	}
	
	/**
	 * Add the product of matrices A and B to what is already contained in this matrix.
	 * 
	 * @param A    Left-hand matrix in product.
	 * @param B    Right-hand matrix in product.
	 */
	protected abstract void addProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B);
	
	@Override
    public AbstractMatrix<T> dot(AbstractMatrix<? extends T> B) {
        AbstractMatrix<T> P = createMatrix(rows(), B.cols(), false);
        P.setProduct(this, B);
        return P;
    }


	public abstract Object getRow(int i);

	
	/**
	 * Copies a row of this matrix into the supplied buffer. The buffer is expected to be a 1D array
	 * (such as double[] or T[]) of size that matches the column dimension of this matrix.
	 * 
	 * @param i        row index
	 * @param buffer   A simple array of the underlying type (e.g. double[] or T[]) to hold the row's data. 
	 */
	public abstract void getRowTo(int i, Object buffer) throws ShapeException;
	

	/**
     * Copies a the contents of the supplied buffer into a row of this matrix. 
     * The buffer is expected to be a 1D array (such as double[] or T[]) of size that matches the column 
     * dimension of this matrix.
     * 
     * @param i         row index
     * @param buffer    A simple array of the underlying type (e.g. double[] or T[]) with the new row data. 
     */
	public abstract void setRow(int i, Object value) throws ShapeException;
	
	
	/**
	 * Swaps two rows in the matrix.
	 * 
	 * @param i    A row index
	 * @param j    Another row index.
	 */
	public void swapRows(int i, int j) {
		Object temp = getRow(i);
		setRow(i, getRow(j));
		setRow(j, temp);		
	}
	

	/**
	 * Swaps two elements in this matrix
	 * 
	 * @param i1   row index of first element  
	 * @param j1   column index of first element
	 * @param i2   row index of second element
	 * @param j2   column index of second element
	 */
	public void swapElements(int i1, int j1, int i2, int j2) {
		T temp = get(i1, j1);
		set(i1, j1, get(i2, j2));
		set(i2, j2, temp);	
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
	 * Add a scalar multiple of one row to another row. It's one of many row operations used e.g. for decompositions.
	 * 
	 * @param row          Index of the row that will be used for adding
	 * @param scaling      Scaling factor
	 * @param toRow        Index of row to which the scaled other row is added. 
	 */
	public abstract void addMultipleOfRow(int row, double scaling, int toRow); 
	

	/**
     * Add a the product of an element and a row's elements to another row. It's one of many row operations used e.g. for decompositions.
     * 
     * @param row          Index of the row that will be used for adding
     * @param scaling      Multiplying element element
     * @param toRow        Index of row to which the multiplied other row is added. 
     */
	public abstract void addMultipleOfRow(int row, T scaling, int toRow); 
	
	/**
	 * Adds a the contents of a row to those of another row in this matrix.
	 * 
	 * @param row          index of row to be added
	 * @param toRow        index of row to which the other row is added.
	 */
	public void addRow(int row, int toRow) {
		addMultipleOfRow(row, 1.0, toRow);		
	}
	

	/**
     * Subtracts a the contents of a row from those of another row in this matrix.
     * 
     * @param row          index of row to be subtracted
     * @param toRow        index of row from which the other row is subtracted.
     */
	public void subtractRow(int row, int fromRow) {
		addMultipleOfRow(row, -1.0, fromRow);		
	}
	
	/**
	 * Sets all elements of a row to zero (or empty values).
	 * 
	 * @param i        Index of row to be zeroed.
	 */
	public abstract void zeroRow(int i);
	
	/**
	 * Scales a row in this matrix by some scalar factor
	 * 
	 * @param i        Index of row to be rescaled
	 * @param factor   Scalar factor
	 */
	public abstract void scaleRow(int i, double factor);
	
	/**
	 * Multiplies all elements of a row with the an element of the same type.
	 * 
	 * @param i        Index of row to be multiplied
	 * @param factor   Multiplicative factor element.
	 */
	public abstract void scaleRow(int i, T factor);

	
	/**
     * Copies a column of this matrix into the supplied buffer. The buffer is expected to be a 1D array
     * (such as double[] or T[]) of size that matches the row dimension of this matrix.
     * 
     * @param j        column index
     * @param buffer   A simple array of the underlying type (e.g. double[] or T[]) to hold the column's data. 
     */
	public abstract void getColumnTo(int j, Object buffer);
	
	/**
     * Copies a the contents of the supplied buffer into a column of this matrix. 
     * The buffer is expected to be a 1D array (such as double[] or T[]) of size that matches the row 
     * dimension of this matrix.
     * 
     * @param j         column index
     * @param buffer    A simple array of the underlying type (e.g. double[] or T[]) with the new column data. 
     */
	public abstract void setColumn(int j, Object value) throws IllegalArgumentException;
	

	public void paste(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		ArrayUtil.paste(patch.getData(), getData(), new int[] { fromRow, fromCol});
	}
	
	
	public AbstractMatrix<T> subspace(int[] rows, int[] cols) {
	    AbstractMatrix<T> sub = createMatrix(rows.length, cols.length, false);
	    
	    for(int i=rows.length; --i >= 0; ) for(int j = cols.length; --j >= 0; )
	        sub.set(i,  j, copyOf(i, j));
	    
	    return sub;
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
	    AbstractMatrix<T> transpose = createMatrix(cols(), rows(), false);
	    for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) transpose.set(j, i, copyOf(i, j));
	    return transpose;
	}

	@Override
	public final void gauss() { gaussJordan(); }
	

	@Override
	public int getRank() {
		AbstractMatrix<T> copy = copy();
		copy.gauss();
		int rank = 0;
		for(int i=0; i<rows(); i++) if(!isNullRow(i)) rank++;
		return rank;
	}
	

	/**
	 * Gets the vector basis of this matrix.
	 * 
	 * @return
	 */
	public abstract AbstractVectorBasis<T> getBasis();
	
	/**
	 * Check if a row is empty (all zeroes or empty values).
	 * 
	 * @param i    Index of row
	 * @return     <code>true</code> if the row contains only zero (empty) elements. Otherwise <code>false</code>
	 */
	public abstract boolean isNullRow(int i);

	
	
}

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
import java.util.Arrays;

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.CopyCat;
import jnum.data.ArrayUtil;
import jnum.data.IndexedEntries;
import jnum.data.IndexedValues;
import jnum.data.image.Index2D;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;


/**
 * An abstract Matrix class representing a matrix for some generic type element. It has two principal subclasses, {@link Matrix}, which
 * is a real-valued matrix with essentially primitive double elements, and {@link GenerixMatrix}, which handles matrices for generic type 
 * objects as long as they provide the required algebra to support matrix operation. For example {@link ComplexMatrix} with {@link Complex}
 * elements is an example subtype, but one could construct matrices e.g. with {@link Matrix} or {@link ObjectMatrix} elements (for a 
 * matrix of matrices), or matrices with other more complex types...
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 * @param <T>       The generic type of the elements in a matrix.
 */
public abstract class AbstractMatrix<T> implements IndexedEntries<Index2D, T>, MatrixAlgebra<AbstractMatrix<? extends T>, T>, SquareMatrixAlgebra<T>, Serializable, 
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
     * Gets the number of rows in this matrix.
     * 
     * @return     Nmber of rows in this matrix.
     */
    public abstract int rows();
    
    
    /**
     * Gets the number of columns in this matrix
     * 
     * @return     Number of columns in this matrix.
     */
    public abstract int cols();

    
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
    public boolean conformsTo(AbstractMatrix<?> M) {
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
     * Gets the matrix element at the specified row, column index in the matrix.
     * 
     * @param idx      The (row, col) index of matrix element
     * @return         The matrix element at the specified row/col index. It is a reference to an object or
     *                 else a primitive value.
     */
    @Override
    public final T get(Index2D idx) { return get(idx.i(), idx.j()); }
   
    
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
    * Sets the matrix element at the specified row, column index in the matrix to the specified new value.
    * 
    * @param idx      The (row,col) index of matrix element
    * @param v        The new matrix element to set. (For object types the matrix will hold a reference
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
     * @param idx      The (row,col) index of matrix element
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
     * M[i][j] -> M[i][j] + v * I, where I is the identity element.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element
     */
    public abstract void add(int row, int col, double v);
    
    /**
     * Adds a scalar value to an element of this matrix. For non-number types, this
     * means adding an identity element scaled by the specified scalar value. I.e.
     * M[i][j] -> M[i][j] + v * I, where I is the identity element.
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final void add(Index2D idx, double value) { add(idx.i(),idx.j(), value); }
    
    /**
     * Clears (sets to zeroes) an entry in this matrix.
     * 
     * @param row      row index of matrix element
     * @param col      column index of matrix element
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
     * @param row      row index of matrix element
     * @param col      column index of matrix element
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
     * @param row      row index of matrix element
     * @param col      column index of matrix element
     */
    public abstract boolean isNull(int i, int j);
       
    /**
     * Checks if an entry in this matrix is a 'null' (zeroed) 
     * 
     * @param idx      The (row,col) index of matrix element
     */
    public final boolean isNull(Index2D idx) { return isNull(idx.i(), idx.j()); }
    
    
	/**
	 * Sets the data in the matrix to zeroes (or empty values). Same as {@link #zero()}
	 * 
	 */
	public final void clear() { zero(); }
	
    @Override
    public final void zero() { for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) clear(i, j); }

    
    @Override
    public void add(AbstractMatrix<? extends T> o) {
        assertSize(o.rows(), o.cols());
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, o.get(i, j));
    }

    /**
     * Adds another matrix containing real values to this one. For matrices with non-number types
     * this means adding, for every element in this matrix, an identity element scaled by the 
     * matching element in the real valued matrix argument for each
     * element. I.e. M[i][j] -> M[i][j] + o[i][j] * I, where I is the identity element.
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
     * element. I.e. M[i][j] -> M[i][j] + (factor * o[i][j]) * I, where I is the identity element.
     * 
     * @param o     The real valued matrix to add to this one with the scaling factor. 
     */
    public void addScaled(Matrix o, double factor) {
        assertSize(o.rows(), o.cols()); 
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, o.get(i, j) * factor);
            
    }  
    
    
    /**
     * Subtracts another matrix containing real values to this one. For matrices with non-number types
     * this means subtracting, from every element in this matrix, an identity element scaled by the 
     * matching element in the real valued matrix argument for each
     * element. I.e. M[i][j] -> M[i][j] - o[i][j] * I, where I is the identity element.
     * 
     * @param o     The real valued matrix to subtract to this one. 
     */
    public void subtract(Matrix o) {
        assertSize(o.rows(), o.cols()); 
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) add(i, j, -o.get(i, j));
            
    }

    @Override
    public void scale(double factor) {
        for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) scale(i, j, factor);
    }
    
    @Override
    public boolean isNull() {
        for(int i=0; i<rows(); i++) for(int j=cols(); --j >= 0; ) if(!isNull(i, j)) return false;
        return true;        
    }
    
    public double getMagnitude(int fromi, int fromj, int toi, int toj) {
        double mag = 0.0;
        MatrixElement<T> e = getElementInstance();
        
        for(int i=toi; --i >= fromi; ) for(int j=toj; --j >= toj; ) {
            double a2 = e.from(i, j).absSquared();
            if(a2 > mag) mag = a2;
        }
        
        return Math.sqrt(mag);        
    }
    
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
	
	@Override
	public void setProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {
	    if(A.cols() != B.rows()) throw new ShapeException("Incompatible product term sizes: " + A + " dot " + B);
	    assertSize(A.rows(), B.cols());
	    zero();
	    addProduct(A, B);
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
        AbstractMatrix<T> P = getMatrixInstance(rows(), B.cols(), false);
        P.setProduct(this, B);
        return P;
    }
 

	/**
	 * Returns the reference to the array (e.g. double[] or T[]) containing the underlying data
	 * of the row with the specified index. Changes to the data in this row will result in
	 * changes of the matrix directly.
	 * 
	 * @param i    Index of row to retrieve
	 * @return     Reference to the underlying array that holds data for that row.
	 */
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
	public final void swapRows(int i, int j) {
		Object rowi = getRow(i);
		setRow(i, getRow(j));
		setRow(j, rowi);		
	}

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
	 * Returns a subspace of this matrix as a new matrix.
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

	public AbstractMatrix<T> subspace(int fromRow, int fromCol, int toRow, int toCol) {
	    AbstractMatrix<T> sub = getMatrixInstance(toRow - fromRow, toCol - fromCol, false);
        for(int i=fromRow; i < toRow; i++) for(int j = fromCol; j < toCol; j++) sub.set(i,  j, copyOf(i, j));
        return sub;
	}

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
    
   
    
    /**
     * Gets the rank of the matrix, that is the dimension of the space the matrix spans, that
     * is also the number of independent rows in the matrix that cannot b expressed as a linear 
     * combination of other rows.
     * 
     * @return      the rank of this matrix
     */
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
                AbstractVector<T> v = basis.getVectorInstance(cols());
                getColumnTo(j, v.getData());
                basis.add(v);
                col = j+1;
                break;
            }
        
        return basis;
    }  
}


/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.math;



import java.text.*;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

import jnum.CopiableContent;
import jnum.data.ArrayUtil;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.text.Parser;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMatrix.
 *
 * @param <T> the generic type
 */
public abstract class AbstractMatrix<T> implements MatrixAlgebra<AbstractMatrix<? extends T>>, Serializable, Cloneable, CopiableContent<AbstractMatrix<T>>, Iterable<T>, NumberFormating, DecimalFormating, Parser {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8165960625207147822L;


	/**
	 * Instantiates a new abstract matrix.
	 */
	public AbstractMatrix() {}
	
	/**
	 * Instantiates a new abstract matrix.
	 *
	 * @param type the type
	 * @param rows the rows
	 * @param cols the cols
	 */
	public AbstractMatrix(Class<? extends T> type, int rows, int cols) {
		setData(ArrayUtil.createArray(type, new int[] {rows, cols}));
	}
	
	/**
	 * Sets the size.
	 *
	 * @param rows the rows
	 * @param cols the cols
	 */
	public void setSize(int rows, int cols) {
		setData(ArrayUtil.createArray(getType(), new int[] {rows, cols}));
	}
	
	/**
	 * Checks if is scalar.
	 *
	 * @return true, if is scalar
	 */
	public boolean isScalar() { return rows() * cols() == 1; }
	
	/**
	 * Checks if is size.
	 *
	 * @param rows the rows
	 * @param cols the cols
	 * @return true, if is size
	 */
	public boolean isSize(int rows, int cols) {
		return rows == rows() && cols == cols();		
	}
	
	/**
	 * Checks if is equal size.
	 *
	 * @param M the m
	 * @return true, if is equal size
	 */
	public boolean isEqualSize(AbstractMatrix<?> M) {
		return M.isSize(rows(), cols());
	}
	
	/**
	 * Assert size.
	 *
	 * @param rows the rows
	 * @param cols the cols
	 */
	public void assertSize(int rows, int cols) {
		if(!isSize(rows, cols)) setSize(rows, cols);
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract Class<T> getType();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); } 
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Clear.
	 */
	public void clear() { zero(); }
	
	/**
	 * Validate.
	 *
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void validate() throws IllegalArgumentException {
		try { checkShape(); }
		catch(IllegalStateException e) { 
			setData(null);
			throw new IllegalArgumentException(e.getMessage()); 
		}
	}
	

	/**
	 * Check shape.
	 *
	 * @throws IllegalStateException the illegal state exception
	 */
	protected abstract void checkShape() throws IllegalStateException;
	
	/* (non-Javadoc)
	 * @see kovacs.util.Copiable#copy()
	 */
	@Override
	public AbstractMatrix<T> copy() {
		return copy(true);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.CopiableContent#copy(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AbstractMatrix<T> copy(boolean withContents) {
		AbstractMatrix<T> copy = (AbstractMatrix<T>) clone();
		if(getData() == null) return copy;
		if(withContents) {			
			try { copy.setData(ArrayUtil.copyOf(getData())); }
			catch(Exception e) { e.printStackTrace(); }
		}
		else {
			copy.noData();
			copy.setSize(rows(), cols());
		}
		return copy;
	}
	
	/**
	 * No data.
	 */
	public abstract void noData();
	
	/* (non-Javadoc)
	 * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {
		if(A.isScalar()) {
			try { setData(ArrayUtil.copyOf(B.getData())); }
			catch(Exception e) { e.printStackTrace(); }
			T scalar = A.getValue(0, 0);		
			if(scalar instanceof Double) ArrayUtil.scale(getData(), ((Double) scalar).doubleValue());
			else ArrayUtil.scale(getData(), (Multiplicative<?>) scalar);
		}
		else if(B.isScalar()) setProduct(B, A);
		
		if(A.cols() != B.rows()) 
			throw new IllegalArgumentException("Incompatible Dimensions: " + A.toString() + "," + B.toString());
		
		int n = A.rows();
		int m = B.cols();
		
		if(getData() == null) setData(ArrayUtil.createArray(getType(), new int[] {n, m})); 
		else if(rows() != n || cols() != m) setData(ArrayUtil.createArray(getType(), new int[] {n, m})); 
		else zero();
		
		calcProduct(A, B, false);
	}
	
	/**
	 * Calc product.
	 *
	 * @param A the a
	 * @param B the b
	 * @param clearFirst the clear first
	 */
	protected abstract void calcProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B, boolean clearFirst);
	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#dot(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AbstractMatrix<T> dot(AbstractMatrix<? extends T> B) {
		AbstractMatrix<T> product = (AbstractMatrix<T>) clone();
		product.setProduct(this, B);
		return product;
	}
	
	
	// Keeps the underlying rows intact. Changes to the component rows will result in changes to
	// the resulting matrix!
	/**
	 * Adds the rows.
	 *
	 * @param b the b
	 */
	@SuppressWarnings("unchecked")
	public void addRows(AbstractMatrix<? extends T> b) {		
		if(cols() != b.cols()) throw new IllegalArgumentException("Mismatched matrix dimensions.");
		T[][] composite = (T[][]) Array.newInstance(getRow(0).getClass(), new int[] { rows() + b.rows() });
		System.arraycopy(getData(), 0, composite, 0, rows());
		System.arraycopy(b.getData(), 0, composite, rows(), b.rows());
	}
	
	// Resulting matrix is independent of the component ones. Changes to the components will not result in
	// changes to the result.
	/**
	 * Adds the columns.
	 *
	 * @param b the b
	 */
	@SuppressWarnings("unchecked")
	public void addColumns(AbstractMatrix<? extends T> b) {		
		if(rows() != b.rows())
			throw new IllegalArgumentException("Mismatched matrix dimensions.");
		T[][] composite = (T[][]) ArrayUtil.createArray(getType(), new int[] { rows(), cols() + b.cols() });
		
		for(int i=0; i<rows(); i++) {
			System.arraycopy(getRow(i), 0, composite[i], 0, cols());
			System.arraycopy(b.getRow(i), 0, composite[i], cols(), b.cols());
		}
	}
	
	
	/**
	 * Gets the dimensions.
	 *
	 * @return the dimensions
	 */
	public int[] getDimensions() { return new int[] { rows(), cols() }; }
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public abstract Object getData();
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public abstract void setData(Object data);
	
	/**
	 * Gets the row.
	 *
	 * @param j the j
	 * @return the row
	 */
	@SuppressWarnings("unchecked")
	public Object getRow(int j) {
		T[] row = null;
		try { 
			row = (T[]) Array.newInstance(getType(), cols()); 
			getRow(j, row);
		}
		catch(Exception e) { e.printStackTrace(); }
		return row;
	}
	
	
	/**
	 * Gets the row.
	 *
	 * @param i the i
	 * @param buffer the buffer
	 * @return the row
	 */
	public abstract void getRow(int i, Object buffer);
	
	/**
	 * Sets the row.
	 *
	 * @param i the i
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public abstract void setRow(int i, Object value) throws IllegalArgumentException;
	
	/**
	 * Switch rows.
	 *
	 * @param i the i
	 * @param j the j
	 */
	public void switchRows(int i, int j) {
		Object temp = getRow(i);
		setRow(i, getRow(j));
		setRow(j, temp);		
	}
	
	/**
	 * Switch elements.
	 *
	 * @param i1 the i1
	 * @param j1 the j1
	 * @param i2 the i2
	 * @param j2 the j2
	 */
	public void switchElements(int i1, int j1, int i2, int j2) {
		T temp = getValue(i1, j1);
		setValue(i1, j1, getValue(i2, j2));
		setValue(i2, j2, temp);	
	}
	
	/**
	 * Adds the multiple of row.
	 *
	 * @param row the row
	 * @param toRow the to row
	 * @param scaling the scaling
	 */
	public abstract void addMultipleOfRow(int row, int toRow, double scaling); 
	
	/**
	 * Adds the multiple of row.
	 *
	 * @param row the row
	 * @param toRow the to row
	 * @param scaling the scaling
	 */
	public abstract void addMultipleOfRow(int row, int toRow, T scaling); 
	
	/**
	 * Adds the row.
	 *
	 * @param row the row
	 * @param toRow the to row
	 */
	public void addRow(int row, int toRow) {
		addMultipleOfRow(row, toRow, 1.0);		
	}
	
	/**
	 * Subtract row.
	 *
	 * @param row the row
	 * @param fromRow the from row
	 */
	public void subtractRow(int row, int fromRow) {
		addMultipleOfRow(row, fromRow, -1.0);		
	}
	
	/**
	 * Zero row.
	 *
	 * @param i the i
	 */
	public abstract void zeroRow(int i);
	
	/**
	 * Scale row.
	 *
	 * @param i the i
	 * @param factor the factor
	 */
	public abstract void scaleRow(int i, double factor);
	
	/**
	 * Scale row.
	 *
	 * @param i the i
	 * @param factor the factor
	 */
	public abstract void scaleRow(int i, T factor);

	
	/**
	 * Gets the column.
	 *
	 * @param j the j
	 * @return the column
	 */
	@SuppressWarnings("unchecked")
	public Object getColumn(int j) {
		T[] column = null;
		try { 
			column = (T[]) Array.newInstance(getType(), rows()); 
			getColumn(j, column);
		}
		catch(Exception e) { e.printStackTrace(); }
		return column;
	}
	
	/**
	 * Gets the column.
	 *
	 * @param j the j
	 * @param buffer the buffer
	 * @return the column
	 */
	public abstract void getColumn(int j, Object buffer);
	
	/**
	 * Sets the column.
	 *
	 * @param j the j
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public abstract void setColumn(int j, Object value) throws IllegalArgumentException;
	
	
	/**
	 * Gets the value.
	 *
	 * @param row the row
	 * @param col the col
	 * @return the value
	 */
	public abstract T getValue(int row, int col);
	
	/**
	 * Sets the value.
	 *
	 * @param row the row
	 * @param col the col
	 * @param v the v
	 */
	public abstract void setValue(int row, int col, T v);
	
	/**
	 * Sets the scalar.
	 *
	 * @param value the new scalar
	 */
	public abstract void setScalar(T value);
	
	/**
	 * Adds the patch.
	 *
	 * @param patch the patch
	 * @param fromRow the from row
	 * @param fromCol the from col
	 */
	public void addPatch(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		addPatch(patch, fromRow, fromCol, 1.0);
	}
	
	/**
	 * Paste.
	 *
	 * @param patch the patch
	 * @param fromRow the from row
	 * @param fromCol the from col
	 */
	public void paste(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		ArrayUtil.paste(patch.getData(), getData(), new int[] { fromRow, fromCol});
	}
	
	/**
	 * Adds the patch.
	 *
	 * @param patch the patch
	 * @param fromRow the from row
	 * @param fromCol the from col
	 * @param scaling the scaling
	 */
	public void addPatch(AbstractMatrix<? extends T> patch, int fromRow, int fromCol, double scaling) {
		ArrayUtil.add(getData(), new int[] { fromRow, fromCol } , patch.getData());
	}
	
	/**
	 * Gets the patch.
	 *
	 * @param fromRow the from row
	 * @param fromCol the from col
	 * @param toRow the to row
	 * @param toCol the to col
	 * @return the patch
	 */
	@SuppressWarnings("unchecked")
	public AbstractMatrix<T> getPatch(int fromRow, int fromCol, int toRow, int toCol) {
		AbstractMatrix<T> patch = (AbstractMatrix<T>) clone();
		patch.setData(ArrayUtil.subArray(getData(), new int[] { fromRow, toRow }, new int[] { fromCol, toCol }));
		return patch;
	}
	
	// TODO Various decompositions.
	
	// TODO implement fast multiplication?
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() { return new MatrixIterator<T>(this); }
	
	
	/** The Constant ROW_VECTOR. */
	public final static int ROW_VECTOR = 0;
	
	/** The Constant COLUMN_VECTOR. */
	public final static int COLUMN_VECTOR = 1;


	/**
	 * Cols.
	 *
	 * @return the int
	 */
	public abstract int cols();

	/**
	 * Rows.
	 *
	 * @return the int
	 */
	public abstract int rows();
	 
	//public abstract void gaussJordan();
	
	/**
	 * Gets the dimension string.
	 *
	 * @return the dimension string
	 */
	public String getDimensionString() {
		return "[" + rows() + "x" + cols() + "]";
	}
	
	/**
	 * Gets the short string.
	 *
	 * @return the short string
	 */
	public String getShortString() {
		return getClass().getSimpleName() + getDimensionString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getShortString() + ":\n" + ArrayUtil.toString((Object[][]) getData());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.text.NumberFormating#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return getShortString() + ":\n" + ArrayUtil.toString((NumberFormating[][]) getData(), nf);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.text.DecimalFormating#toString(int)
	 */
	@Override
	public String toString(int decimals) {
		return getShortString() + ":\n" + ArrayUtil.toString(getData(), decimals);
	}
		
	
	/* (non-Javadoc)
	 * @see kovacs.text.Parser#parse(java.lang.String)
	 */
	@Override
	public void parse(String text) throws NumberFormatException, IllegalArgumentException {
		try { setData(ArrayUtil.parse(text, getType())); }
		catch(IllegalAccessException e) { e.printStackTrace(); }
		catch(InstantiationException e) { e.printStackTrace(); }
		catch(ParseException e) { throw new NumberFormatException(e.getMessage()); }
	}

	
	/**
	 * Gets the transposed.
	 *
	 * @return the transposed
	 */
	public abstract AbstractMatrix<T> getTransposed();
	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#transpose()
	 */
	@Override
	public final void transpose() {
		setData(getTransposed().getData());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#gauss()
	 */
	@Override
	public final void gauss() { gaussJordan(); }
	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#getRank()
	 */
	@Override
	public int getRank() {
		AbstractMatrix<T> copy = copy();
		copy.gauss();
		int rank = 0;
		for(int i=0; i<rows(); i++) if(!isNullRow(i)) rank++;
		return rank;
	}
	
	
	/**
	 * Gets the basis.
	 *
	 * @return the basis
	 */
	public abstract AbstractVectorBasis<T> getBasis();
	
	/**
	 * Checks if is null row.
	 *
	 * @param i the i
	 * @return true, if is null row
	 */
	public abstract boolean isNullRow(int i);
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(int i=0; i<rows(); i++) if(!isNullRow(i)) return false;
		return true;		
	}
	
	/**
	 * Scale.
	 *
	 * @param scalar the scalar
	 */
	public void scale(T scalar) {
		for(int i=0; i<rows(); i++) scaleRow(i, scalar);
	}
	
	
}

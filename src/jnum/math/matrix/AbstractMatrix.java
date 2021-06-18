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
import java.lang.reflect.*;

import jnum.CopiableContent;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.data.image.Index2D;
import jnum.math.Multiplicative;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.text.Parser;


public abstract class AbstractMatrix<T> implements MatrixAlgebra<AbstractMatrix<? extends T>>, SquareMatrixAlgebra<T>, Serializable, Cloneable, 
CopiableContent<AbstractMatrix<T>>, Iterable<T>, NumberFormating, DecimalFormating, Parser {

	private static final long serialVersionUID = 8165960625207147822L;


	public AbstractMatrix() {}
	

	public AbstractMatrix(Class<? extends T> type, int rows, int cols) {
		setData(ArrayUtil.createArray(type, new int[] {rows, cols}));
	}

    
    
	
	public void setSize(int rows, int cols) {
		setData(ArrayUtil.createArray(getType(), new int[] {rows, cols}));
	}
	

	public boolean isScalar() { return rows() * cols() == 1; }
	

	public boolean isSize(int rows, int cols) {
		return rows == rows() && cols == cols();		
	}
	

	public boolean isEqualSize(AbstractMatrix<?> M) {
		return M.isSize(rows(), cols());
	}
	

	public void assertSize(int rows, int cols) {
		if(!isSize(rows, cols)) setSize(rows, cols);
	}


	public abstract Class<T> getType();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
    @Override
	public AbstractMatrix<T> clone() {
		try { return (AbstractMatrix<T>) super.clone(); } 
		catch(CloneNotSupportedException e) { return null; }
	}
	

	public void clear() { zero(); }
	

	public void validate() throws IllegalArgumentException {
		try { checkShape(); }
		catch(IllegalStateException e) { 
			setData(null);
			throw new IllegalArgumentException(e.getMessage()); 
		}
	}
	

	protected abstract void checkShape() throws IllegalStateException;
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public AbstractMatrix<T> copy() {
		return copy(true);
	}
	
	/* (non-Javadoc)
	 * @see jnum.CopiableContent#copy(boolean)
	 */
	@Override
	public AbstractMatrix<T> copy(boolean withContents) {
		AbstractMatrix<T> copy = clone();
		if(getData() == null) return copy;
		if(withContents) {			
			try { copy.setData(ArrayUtil.copyOf(getData())); }
			catch(Exception e) { Util.error(this, e); }
		}
		else {
			copy.noData();
			copy.setSize(rows(), cols());
		}
		return copy;
	}
	

	public abstract void noData();
	
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
	
	/* (non-Javadoc)
	 * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {
		if(A.isScalar()) {
			try { setData(ArrayUtil.copyOf(B.getData())); }
			catch(Exception e) { Util.error(this, e); }
			T scalar = A.getValue(0, 0);		
			if(scalar instanceof Double) ArrayUtil.scale(getData(), ((Double) scalar).doubleValue());
			else ArrayUtil.scale(getData(), (Multiplicative<?>) scalar);
		}
		else if(B.isScalar()) setProduct(B, A);
		
		if(A.cols() != B.rows()) 
			throw new IllegalArgumentException("Incompatible Dimensions: " + A + "," + B);
		
		int n = A.rows();
		int m = B.cols();
		
		setData(ArrayUtil.createArray(getType(), new int[] {n, m}));
		addProduct(A, B);
	}
	

	protected abstract void addProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B);
	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#dot(java.lang.Object)
	 */
	@Override
	public AbstractMatrix<T> dot(AbstractMatrix<? extends T> B) {
		AbstractMatrix<T> product = clone();
		product.setProduct(this, B);
		return product;
	}
	
	
	// Keeps the underlying rows intact. Changes to the component rows will result in changes to
	// the resulting matrix!
	@SuppressWarnings("unchecked")
	public void addRows(AbstractMatrix<? extends T> b) {		
		if(cols() != b.cols()) throw new IllegalArgumentException("Mismatched matrix dimensions.");
		T[][] composite = (T[][]) Array.newInstance(getRow(0).getClass(), new int[] { rows() + b.rows() });
		System.arraycopy(getData(), 0, composite, 0, rows());
		System.arraycopy(b.getData(), 0, composite, rows(), b.rows());
	}
	
	// Resulting matrix is independent of the component ones. Changes to the components will not result in
	// changes to the result.
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
	
	
	public int[] getDimensions() { return new int[] { rows(), cols() }; }
	

	public abstract Object getData();
	

	public abstract void setData(Object data);
	

	@SuppressWarnings("unchecked")
	public Object getRow(int j) {
		T[] row = null;
		try { 
			row = (T[]) Array.newInstance(getType(), cols()); 
			getRow(j, row);
		}
		catch(Exception e) { Util.error(this, e); }
		return row;
	}
	

	public abstract void getRow(int i, Object buffer);
	

	public abstract void setRow(int i, Object value) throws IllegalArgumentException;
	

	public void switchRows(int i, int j) {
		Object temp = getRow(i);
		setRow(i, getRow(j));
		setRow(j, temp);		
	}
	

	public void switchElements(int i1, int j1, int i2, int j2) {
		T temp = getValue(i1, j1);
		setValue(i1, j1, getValue(i2, j2));
		setValue(i2, j2, temp);	
	}
	
	
	public void switchElements(Index2D i1, Index2D i2) {
	    switchElements(i1.i(), i1.j(), i2.i(), i2.j());
	}

	public abstract void addMultipleOfRow(int row, int toRow, double scaling); 
	

	public abstract void addMultipleOfRow(int row, int toRow, T scaling); 
	

	public void addRow(int row, int toRow) {
		addMultipleOfRow(row, toRow, 1.0);		
	}
	

	public void subtractRow(int row, int fromRow) {
		addMultipleOfRow(row, fromRow, -1.0);		
	}
	

	public abstract void zeroRow(int i);
	

	public abstract void scaleRow(int i, double factor);
	

	public abstract void scaleRow(int i, T factor);

	
	@SuppressWarnings("unchecked")
	public Object getColumn(int j) {
		T[] column = null;
		try { 
			column = (T[]) Array.newInstance(getType(), rows()); 
			getColumn(j, column);
		}
		catch(Exception e) { Util.error(this, e); }
		return column;
	}
	

	public abstract void getColumn(int j, Object buffer);
	

	public abstract void setColumn(int j, Object value) throws IllegalArgumentException;
	

	public abstract T getValue(int row, int col);
	
	public abstract void setValue(int row, int col, T v);
	
	public final T getValue(Index2D idx) { return getValue(idx.i(), idx.j()); }
    
    public final void setValue(Index2D idx, T value) { setValue(idx.i(),idx.j(), value); }
	
    
    public abstract void addValue(int row, int col, T increment);

    public final void addValue(Index2D idx, T increment) { addValue(idx.i(),idx.j(), increment); }
    
    public abstract void addScaledValue(int row, int col, T increment, double scaling);

    public final void addScaledValue(Index2D idx, T increment, double scaling) { addScaledValue(idx.i(),idx.j(), increment, scaling); }
    
	public void addPatch(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		addPatch(patch, fromRow, fromCol, 1.0);
	}
	

	public void paste(AbstractMatrix<? extends T> patch, int fromRow, int fromCol) {
		ArrayUtil.paste(patch.getData(), getData(), new int[] { fromRow, fromCol});
	}
	

	public void addPatch(AbstractMatrix<? extends T> patch, int fromRow, int fromCol, double scaling) {
		ArrayUtil.add(getData(), new int[] { fromRow, fromCol } , patch.getData());
	}
	

	public AbstractMatrix<T> getPatch(int fromRow, int fromCol, int toRow, int toCol) {
		AbstractMatrix<T> patch = clone();
		patch.setData(ArrayUtil.subArray(getData(), new int[] { fromRow, toRow }, new int[] { fromCol, toCol }));
		return patch;
	}
	
	// TODO Various decompositions.
	
	// TODO implement fast multiplication?
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public java.util.Iterator<T> iterator() { return new Iterator(); }
	

	public final static int ROW_VECTOR = 0;

	public final static int COLUMN_VECTOR = 1;



	public abstract int cols();


	public abstract int rows();
	
	public boolean isSquare() { return rows() == cols(); }
	 
	//public abstract void gaussJordan();
	

	public String getDimensionString() {
		return "[" + rows() + "x" + cols() + "]";
	}
	

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
	public void parse(String text, ParsePosition pos) throws IllegalArgumentException {
	    // TODO need to update parseposition...
	    try { setData(ArrayUtil.parse(text.substring(pos.getIndex()), getType())); }
		catch(ParseException e) { throw new NumberFormatException(e.getMessage()); }
	    catch(Exception e) { Util.error(this, e); }
	}

	

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
	

	public abstract AbstractVectorBasis<T> getBasis();
	

	public abstract boolean isNullRow(int i);
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(int i=0; i<rows(); i++) if(!isNullRow(i)) return false;
		return true;		
	}
	

	public void scale(T scalar) {
		for(int i=0; i<rows(); i++) scaleRow(i, scalar);
	}
	

    private class Iterator implements java.util.Iterator<T> {
        private int rows = rows(), cols = cols();
        private int i=0, j=-1;
        
        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public final boolean hasNext() {
            if(i < rows) return true;
            return j < cols;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public final T next() {
            j++;
            if(j >= cols) {
                j=0;
                i++;
            }
            return getValue(i, j);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public final void remove() {
            throw new UnsupportedOperationException("Cannot remove elements from a matrix type object.");
        }

    }
	
	
}

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

import java.util.Arrays;

import jnum.Copiable;
import jnum.ShapeException;
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
	public ObjectMatrix(Class<T> type) { this.type = type; }


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

	@Override
	protected ObjectMatrix<T> createMatrix(int rows, int cols, boolean initialize) {
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
    protected final T copyOf(int row, int col) { return (T) get(row, col).copy(); }

	@Override
	public final void set(int row, int col, T v) { data[row][col] = v; }
	
    @Override
    public void clear(int i, int j) { data[i][j].zero(); }
	
	@Override
    public void add(int i, int j, T increment) { data[i][j].add(increment); }
    
    @Override
    public void addScaled(int i, int j, T increment, double scaling) { data[i][j].addScaled(increment, scaling); }
	
    @Override
    public void scale(int i, int j, double factor) { data[i][j].scale(factor); }

    @Override
    public void multiplyBy(int i, int j, T factor) { data[i][j].multiplyBy(factor); }


	protected void checkShape(T[][] x) throws ShapeException {
	    if(x == null) return;
        if(x.length == 0) return;
        int m = x[0].length;
        for(int i=x.length; --i > 0; ) if(x[i].length != m) throw new ShapeException("Matrix has an irregular non-rectangular shape!");    
	}
	

	@Override
	protected synchronized void addProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {		
		final T P = createElement();
		
		// TODO parallelize on i.
        
        for(int i=A.rows(); --i >= 0; ) {
            final T[] row = data[i];
            
            for(int k=A.cols(); --k >= 0; ) {
                final T a = A.get(i, k);
                if(a.isNull()) continue;
                
                for(int j=B.cols(); --j >= 0; ) {
                    final T b = B.get(k, j);
                    if(b.isNull()) continue;
                    
                    P.setProduct(a, b);
                    row[j].add(P);
                }
            }       
        }
	}
	
	
	@Override
	public ObjectMatrix<T> dot(AbstractMatrix<? extends T> B) {
	    return (ObjectMatrix<T>) super.dot(B);
	}

	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the t[]
	 */
	public T[] dot(double[] v) {
		T[] result = (T[]) Array.newInstance(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
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
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the t[]
	 */
	public T[] dot(float[] v) {
		T[] result = (T[]) Array.newInstance(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
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
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the t[]
	 */
	public T[] dot(T[] v) {
		T[] result = (T[]) Array.newInstance(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public synchronized void dot(T[] v, T[] result) {
		if(v.length != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.length != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
		
		final T P = createElement();
		
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
	
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the generic vector
	 */
	public ObjectVector<T> dot(RealVector v) {
		ObjectVector<T> result = new ObjectVector<>(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(RealVector v, ObjectVector<T> result) {
		if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
		dot(v.getData(), result.getData());
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the generic vector
	 */
	public ObjectVector<T> dot(ObjectVector<T> v) {
		ObjectVector<T> result = new ObjectVector<>(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(ObjectVector<T> v, ObjectVector<T> result) {
		if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
		dot(v.getData(), result.getData());
	}
	
	public void dot(MathVector<T> v, MathVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        
        final T P = createElement();
        final T sum = createElement();
  
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
	public void zero() {
		if(data != null) for(int i=data.length; --i >= 0; ) for(int j=data[i].length; --j >= 0; ) {
			if(data[i][j] == null) data[i][j] = createElement();
			data[i][j].zero();
		}
	}
	


	@Override
	public void addScaled(AbstractMatrix<? extends T> o, double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].addScaled(o.get(i, j), factor);
	}


	@Override
	public void subtract(AbstractMatrix<? extends T> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].subtract(o.get(i, j));
	}


	@Override
	public void add(AbstractMatrix<? extends T> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].add(o.get(i, j));		
	}


	@Override
	public void scale(double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].scale(factor);
	}


	@Override
	public double distanceTo(AbstractMatrix<? extends T> o) {
		double d2 = 0.0;
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			double d = data[i][j].distanceTo(o.get(i, j));
			d2 += d*d;
		}
		return Math.sqrt(d2);
	}


	@Override
	public final int cols() { return data[0].length; }


	@Override
	public final int rows() { return data.length; }


	@Override
	public void gaussJordan() {
		int rows = rows();
		int cols = cols();
			
		int[] indxc = new int[rows];
		int[] indxr = new int[rows];
		int[] ipiv = new int[rows];

		Arrays.fill(ipiv, -1);
		
		T temp = createElement();
		T unused = createElement();

		for(int i=rows; --i >= 0; ) {
			int icol=-1, irow=-1;
			double big=0.0;
			for(int j=rows; --j >= 0; ) if(ipiv[j] != 0) for(int k=rows; --k >= 0; ) {
				if(ipiv[k] == -1) {
					if(data[j][k].abs() >= big) {
						big=data[j][k].abs();
						irow=j;
						icol=k;
					}
				} 
				else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular Matrix-1 during Gauss-Jordan elimination.");
			}
			++(ipiv[icol]);
			if(irow != icol) {
				for(int l=cols; --l >= 0; ) {
					temp = data[irow][l];
					data[irow][l] = data[icol][l];
					data[icol][l] = temp;
				}
			}
			indxr[i]=irow;
			indxc[i]=icol;
			if(data[icol][icol].isNull()) throw new IllegalArgumentException("Singular Matrix-2 during Gauss-Jordan elimination.");
			
			temp = data[icol][icol];
			temp.inverse();
			unused.setIdentity();
			data[icol][icol] = unused;
			scaleRow(icol, temp);
			unused = temp;
			
			for(int ll=rows; --ll >= 0; ) if(ll != icol) {
				temp = data[ll][icol];
				temp.scale(-1.0);
				unused.zero();
				data[ll][icol] = unused;
				addMultipleOfRow(icol, temp, ll);
				unused = temp;
			}
		}
		for(int l=rows; -- l>= 0; ) if(indxr[l] != indxc[l]) for(int k=rows; --k >= 0; ) {
			temp = data[k][indxr[l]];
			data[k][indxr[l]] = data[k][indxc[l]];
			data[k][indxc[l]] = temp;
		}
	}
	
	

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
	public final void swapRows(int i, int j) {
		T[] temp = data[i];
		data[i] = data[j];
		data[j] = temp;	
	}
	

	@Override
	public final void swapElements(int i1, int j1, int i2, int j2) {
		T temp = data[i1][j1];
		data[i1][j1] = data[i2][j2];
		data[i2][j2] = temp;	
	}
	
	@Override
	public void addMultipleOfRow(int row, double scaling, int toRow) {
		for(int j=cols(); --j >= 0; ) data[toRow][j].addScaled(data[row][j], scaling);
	}
	
	@Override
	public synchronized void addMultipleOfRow(int row, T scaling, int toRow) {
		T term = createElement();
		
		for(int j=cols(); --j >= 0; ) {
			term.setProduct(data[row][j], scaling);
			data[toRow][j].add(term);
		}
	}
	

	@Override
	public void addRow(int row, int toRow) {
		for(int j=cols(); --j >= 0; ) data[toRow][j].add(data[row][j]);	
	}
	

	@Override
	public void subtractRow(int row, int fromRow) {
		for(int j=cols(); --j >= 0; ) data[fromRow][j].subtract(data[row][j]);		
	}
	

	@Override
	public void zeroRow(int i) {
		for(int j=cols(); --j >= 0; ) {
			if(data[i][j] == null) data[i][j] = createElement();
			data[i][j].zero();
		}
	}
	

	@Override
	public void scaleRow(int i, double factor) {
		for(int j=0; j<cols(); j++) data[i][j].scale(factor);
	}
	

	@Override
	public void scaleRow(int i, T factor) {
		for(int j=cols(); --j >= 0; ) data[i][j].multiplyBy(factor);
	}

	/**
	 * Offset.
	 *
	 * @param value the value
	 */
	public void offset(T value) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) data[i][j].add(value);			
	}


	@Override
	public int getRank() {
		ObjectMatrix<T> copy = copy();
		copy.gauss();
		int rank = 0;
		int col = 0;
		for(int i=0; i<rows(); i++) {
			T[] row = copy.data[i];
			for(int j=col; j<cols(); j++) if(!row[j].isNull()) {
				col = j+1;
				break;
			}
		}
			
		return rank;
	}
	
	
	@Override
	public AbstractVectorBasis<T> getBasis() {
		ObjectVectorBasis<T> basis = new ObjectVectorBasis<>();
		ObjectMatrix<T> copy = copy();
		copy.gauss();

		int col = 0;
		for(int i=0; i<rows(); i++) {
			T[] row = copy.data[i];
			for(int j=col; j<cols(); j++) {
				if(!row[j].isNull()) {
					ObjectVector<T> v = new ObjectVector<>(type, cols());
					getColumnTo(j, v.getData());
					basis.add(v);
					col = j+1;
					break;
				}
			}
		}
		
		return basis;
	}
	

	@Override
	public boolean isNullRow(int i) {
		for(int j=0; j<cols(); j++) if(!data[i][j].isNull()) return false;
		return true;
	}


	@Override
	public void setSum(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
		if(!a.isEqualSize(b))	throw new ShapeException("different size matrices.");
		
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			if(data[i][j] == null) data[i][j] = createElement();
			data[i][j].setSum(a.get(i, j), b.get(i,  j));
		}
	}


	@Override
	public void setDifference(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
		if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");
		
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			if(data[i][j] == null) data[i][j] = createElement();
			data[i][j].setDifference(a.get(i, j), b.get(i,  j));
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
    
    // Invert via Gauss-Jordan elimination
    public ObjectMatrix<T> getGaussInverse() {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        
        ObjectMatrix<T> I = createMatrix(size, size, true);
        ObjectMatrix<T> combo = createMatrix(size, 2*size, false);
        for(int i=size; --i >= 0; ) I.data[i][i].setIdentity();
        
        combo.paste(this, 0, 0);
        combo.paste(I, 0, size);
        combo.gaussJordan();
        
        ObjectMatrix<T> inverse = createMatrix(size, size, false);
        for(int i=size; --i >= 0; ) for(int j=size; --j >= 0.0; ) set(i, j, combo.get(i, size+j));
        return inverse;
    }
    

    public ObjectMatrix<T> getLUInverse() {
        return getLUDecomposition().getInverseMatrix();
    }

    @Override
    public void invert() {
        data = getInverse().data;
    }
    
    @Override
    public void addIdentity(double scaling) {
        if(!isSquare()) throw new SquareMatrixException();
        T increment = createElement();
        increment.setIdentity();
        increment.scale(scaling);
        for(int i=rows(); --i >= 0; ) data[i][i].add(increment);
    }
    
    
    @Override
    public ObjectLUDecomposition<T> getLUDecomposition() {
        return new ObjectLUDecomposition<>(this);
    }



 
}

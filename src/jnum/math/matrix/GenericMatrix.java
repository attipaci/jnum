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
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.math.MathVector;

import java.lang.reflect.*;


// [row][col] format. This way dot products are on middle indices....

/**
 * The Class GenericMatrix.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("unchecked")
public class GenericMatrix<T extends Copiable<? super T> & LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue> extends AbstractMatrix<T> 
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2705914561805806547L;

	/** The entry. */
	public T[][] entry; 
	
	/** The type. */
	protected Class<T> type;
	
	/**
	 * Instantiates a new generic matrix.
	 */
	protected GenericMatrix() {}
	
	/**
	 * Instantiates a new generic matrix.
	 *
	 * @param type the type
	 */
	public GenericMatrix(Class<T> type) { this.type = type; }

	// Check for rectangular shape
	/**
	 * Instantiates a new generic matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public GenericMatrix(T[][] a) throws IllegalArgumentException { 
		this((Class<T>) a[0][0].getClass());
		entry = a; 
		validate();
	}

	/**
	 * Instantiates a new generic matrix.
	 *
	 * @param type the type
	 * @param rows the rows
	 * @param cols the cols
	 */
	public GenericMatrix(Class<T> type, int rows, int cols) { 
		super(type, rows, cols);
		this.type = type;
	}
	
	
	public GenericMatrix(Class<T> type, int size) { 
        super(type, size, size);
        this.type = type;
    }
	
	
	@Override
    public GenericMatrix<T> clone() {
	    return (GenericMatrix<T>) super.clone();
	}
	
	@Override
    public GenericMatrix<T> copy() {
        return (GenericMatrix<T>) super.copy();
    }
    
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getType()
	 */
	@Override
	public final Class<T> getType() { return type; }	
	
	/**
	 * New entry.
	 *
	 * @return the t
	 */
	public T createEntry() {
		try { return type.getConstructor().newInstance(); }
		catch(Exception e) { 
			Util.error(this, e);
			return null;
		}	
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#noData()
	 */
	@Override
	public void noData() { entry = null; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getData()
	 */
	@Override
	public Object getData() { return entry; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getValue(int, int)
	 */
	@Override
	public final T getValue(int row, int col) { return entry[row][col]; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setValue(int, int, java.lang.Object)
	 */
	@Override
	public final void setValue(int row, int col, T v) { entry[row][col] = v; }
	
	@Override
    public void addValue(int i, int j, T increment) { entry[i][j].add(increment); }
    
    @Override
    public void addScaledValue(int i, int j, T increment, double scaling) { entry[i][j].addScaled(increment, scaling); }
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) if(entry[i][j] == null) 
			throw new NullPointerException(getClass().getSimpleName() + " has null entries");
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#checkShape()
	 */
	@Override
	protected void checkShape() throws IllegalStateException {
		if(getData() == null) return;
		if(rows() == 0) return;
		int m = cols();
		for(int i=rows(); --i > 0; ) if(entry[i].length != m) throw new IllegalStateException("Matrix has non-rectangular shape!");	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#calcProduct(kovacs.math.AbstractMatrix, kovacs.math.AbstractMatrix, boolean)
	 */
	@Override
	protected synchronized void addProduct(AbstractMatrix<? extends T> A, AbstractMatrix<? extends T> B) {		
		final T P = createEntry();
		
		// TODO parallelize on i.
        
        for(int i=A.rows(); --i >= 0; ) {
            final T[] row = entry[i];
            
            for(int k=A.cols(); --k >= 0; ) {
                final T a = A.getValue(i, k);
                if(a.isNull()) continue;
                
                for(int j=B.cols(); --j >= 0; ) {
                    final T b = B.getValue(k, j);
                    if(b.isNull()) continue;
                    
                    P.setProduct(a, b);
                    row[j].add(P);
                }
            }       
        }
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
		    final T[] row = entry[i];
		    final T to = result[i];
			to.zero();
			for(int j=cols(); --j >= 0; ) if(v[j] != 0.0) if(!row[j].isNull()) to.addScaled(entry[i][j], v[j]);
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
		    final T[] row = entry[i];
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
		
		final T P = createEntry();
		
		for(int i=rows(); --i >= 0; ) {
		    final T[] row = entry[i];
			final T to = result[i];
			
			to.zero();
			for(int j=cols(); --j >= 0; ) if(!row[j].isNull()) if(!v[j].isNull()) {
				P.setProduct(entry[i][j], v[j]);
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
	public GenericVector<T> dot(RealVector v) {
		GenericVector<T> result = new GenericVector<>(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(RealVector v, GenericVector<T> result) {
		if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.getData() == null) result.setSize(rows());
		else if(result.size() != rows()) result.setSize(rows());
		dot(v.getData(), result.getData());
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @return the generic vector
	 */
	public GenericVector<T> dot(GenericVector<T> v) {
		GenericVector<T> result = new GenericVector<>(type, rows());
		dot(v, result);
		return result;
	}
	
	/**
	 * Dot.
	 *
	 * @param v the v
	 * @param result the result
	 */
	public void dot(GenericVector<T> v, GenericVector<T> result) {
		if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
		if(result.getData() == null) result.setSize(rows());
		else if(result.size() != rows()) result.setSize(rows());
		dot(v.getData(), result.getData());
	}
	
	public void dot(MathVector<T> v, MathVector<T> result) {
        if(v.size() != cols()) throw new ShapeException("Mismatched matrix/input-vector sizes.");
        if(result.size() != rows()) throw new ShapeException("Mismatched matrix/output-vector sizes.");
        
        final T P = createEntry();
        final T sum = createEntry();
  
        for(int i=rows(); --i >= 0; ) {
            final T[] row = entry[i];
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
    
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getTransposed()
	 */
	@Override
	public AbstractMatrix<T> getTransposed() {		
		GenericMatrix<T> M = new GenericMatrix<>(type, cols(), rows());
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) M.entry[j][i] = entry[i][j];
		return M;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#zero()
	 */
	@Override
	public void zero() {
		if(entry != null) for(int i=entry.length; --i >= 0; ) for(int j=entry[i].length; --j >= 0; ) {
			if(entry[i][j] == null) entry[i][j] = createEntry();
			entry[i][j].zero();
		}
	}
	

	// TODO Various decompositions.
	
	// TODO implement fast multiplication?

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addScaled(AbstractMatrix<? extends T> o, double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j].addScaled(o.getValue(i, j), factor);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#subtract(java.lang.Object)
	 */
	@Override
	public void subtract(AbstractMatrix<? extends T> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j].subtract(o.getValue(i, j));
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public void add(AbstractMatrix<? extends T> o) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j].add(o.getValue(i, j));		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Scalable#scale(double)
	 */
	@Override
	public void scale(double factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j].scale(factor);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(AbstractMatrix<? extends T> o) {
		double d2 = 0.0;
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			double d = entry[i][j].distanceTo(o.getValue(i, j));
			d2 += d*d;
		}
		return Math.sqrt(d2);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#cols()
	 */
	@Override
	public final int cols() { return entry[0].length; }

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#rows()
	 */
	@Override
	public final int rows() { return entry.length; }

	
	/* (non-Javadoc)
	 * @see kovacs.math.MatrixAlgebra#gaussJordan()
	 */
	@Override
	public void gaussJordan() {
		int rows = rows();
		int cols = cols();
			
		int[] indxc = new int[rows];
		int[] indxr = new int[rows];
		int[] ipiv = new int[rows];

		Arrays.fill(ipiv, -1);
		
		T temp = createEntry();
		T unused = createEntry();

		for(int i=rows; --i >= 0; ) {
			int icol=-1, irow=-1;
			double big=0.0;
			for(int j=rows; --j >= 0; ) if(ipiv[j] != 0) for(int k=rows; --k >= 0; ) {
				if(ipiv[k] == -1) {
					if(entry[j][k].abs() >= big) {
						big=entry[j][k].abs();
						irow=j;
						icol=k;
					}
				} 
				else if(ipiv[k] > 0) throw new IllegalArgumentException("Singular Matrix-1 during Gauss-Jordan elimination.");
			}
			++(ipiv[icol]);
			if(irow != icol) {
				for(int l=cols; --l >= 0; ) {
					temp = entry[irow][l];
					entry[irow][l] = entry[icol][l];
					entry[icol][l] = temp;
				}
			}
			indxr[i]=irow;
			indxc[i]=icol;
			if(entry[icol][icol].isNull()) throw new IllegalArgumentException("Singular Matrix-2 during Gauss-Jordan elimination.");
			
			temp = entry[icol][icol];
			temp.inverse();
			unused.setIdentity();
			entry[icol][icol] = unused;
			scaleRow(icol, temp);
			unused = temp;
			
			for(int ll=rows; --ll >= 0; ) if(ll != icol) {
				temp = entry[ll][icol];
				temp.scale(-1.0);
				unused.zero();
				entry[ll][icol] = unused;
				addMultipleOfRow(icol, ll, temp);
				unused = temp;
			}
		}
		for(int l=rows; -- l>= 0; ) if(indxr[l] != indxc[l]) for(int k=rows; --k >= 0; ) {
			temp = entry[k][indxr[l]];
			entry[k][indxr[l]] = entry[k][indxc[l]];
			entry[k][indxc[l]] = temp;
		}
	}
	
	

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getColumn(int, java.lang.Object)
	 */
	@Override
	public void getColumn(int j, Object buffer) {
		T[] array = (T[]) buffer;
		for(int i=rows(); --i >= 0; ) array[i] = entry[i][j];
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRow(int)
	 */
	@Override
	public T[] getRow(int i) {
		return entry[i];
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRow(int, java.lang.Object)
	 */
	@Override
	public void getRow(int i, Object buffer) {
		T[] array = (T[]) buffer;
		for(int j=cols(); --j >= 0; ) array[j] = entry[i][j];
	}
	

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setColumn(int, java.lang.Object)
	 */
	@Override
	public void setColumn(int j, Object value) throws ShapeException {
		T[] array = (T[]) value;
		if(array.length != rows()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " column.");
		for(int i=rows(); --i >= 0; ) entry[i][j] = array[i];
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) {
		entry = (T[][]) data;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#setRow(int, java.lang.Object)
	 */
	@Override
	public void setRow(int i, Object value) {
		T[] array = (T[]) value;
		if(array.length != cols()) throw new ShapeException("Cannot add mismatched " + getClass().getSimpleName() + " row.");
		entry[i] = array;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#switchRows(int, int)
	 */
	@Override
	public final void switchRows(int i, int j) {
		T[] temp = entry[i];
		entry[i] = entry[j];
		entry[j] = temp;	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#switchElements(int, int, int, int)
	 */
	@Override
	public final void switchElements(int i1, int j1, int i2, int j2) {
		T temp = entry[i1][j1];
		entry[i1][j1] = entry[i2][j2];
		entry[i2][j2] = temp;	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addMultipleOfRow(int, int, double)
	 */
	@Override
	public void addMultipleOfRow(int row, int toRow, double scaling) {
		for(int j=cols(); --j >= 0; ) entry[toRow][j].addScaled(entry[row][j], scaling);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addMultipleOfRow(int, int, java.lang.Object)
	 */
	@Override
	public synchronized void addMultipleOfRow(int row, int toRow, T scaling) {
		T term = createEntry();
		
		for(int j=cols(); --j >= 0; ) {
			term.setProduct(entry[row][j], scaling);
			entry[toRow][j].add(term);
		}
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#addRow(int, int)
	 */
	@Override
	public void addRow(int row, int toRow) {
		for(int j=cols(); --j >= 0; ) entry[toRow][j].add(entry[row][j]);	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#subtractRow(int, int)
	 */
	@Override
	public void subtractRow(int row, int fromRow) {
		for(int j=cols(); --j >= 0; ) entry[fromRow][j].subtract(entry[row][j]);		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#zeroRow(int)
	 */
	@Override
	public void zeroRow(int i) {
		for(int j=cols(); --j >= 0; ) {
			if(entry[i][j] == null) entry[i][j] = createEntry();
			entry[i][j].zero();
		}
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#scaleRow(int, double)
	 */
	@Override
	public void scaleRow(int i, double factor) {
		for(int j=0; j<cols(); j++) entry[i][j].scale(factor);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#scaleRow(int, java.lang.Object)
	 */
	@Override
	public void scaleRow(int i, T factor) {
		for(int j=cols(); --j >= 0; ) entry[i][j].multiplyBy(factor);
	}

	/**
	 * Offset.
	 *
	 * @param value the value
	 */
	public void offset(T value) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) entry[i][j].add(value);			
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getRank()
	 */
	@Override
	public int getRank() {
		GenericMatrix<T> copy = copy();
		copy.gauss();
		int rank = 0;
		int col = 0;
		for(int i=0; i<rows(); i++) {
			T[] row = copy.entry[i];
			for(int j=col; j<cols(); j++) if(!row[j].isNull()) {
				col = j+1;
				break;
			}
		}
			
		return rank;
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#getBasis()
	 */
	@Override
	public AbstractVectorBasis<T> getBasis() {
		GenericVectorBasis<T> basis = new GenericVectorBasis<>();
		GenericMatrix<T> copy = copy();
		copy.gauss();

		int col = 0;
		for(int i=0; i<rows(); i++) {
			T[] row = copy.entry[i];
			for(int j=col; j<cols(); j++) {
				if(!row[j].isNull()) {
					GenericVector<T> v = new GenericVector<>(type, cols());
					getColumn(j, v.getData());
					basis.add(v);
					col = j+1;
					break;
				}
			}
		}
		
		return basis;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractMatrix#isNullRow(int)
	 */
	@Override
	public boolean isNullRow(int i) {
		for(int j=0; j<cols(); j++) if(!entry[i][j].isNull()) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
		if(!a.isEqualSize(b))	throw new ShapeException("different size matrices.");
		
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			if(entry[i][j] == null) entry[i][j] = createEntry();
			entry[i][j].setSum(a.getValue(i, j), b.getValue(i,  j));
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(AbstractMatrix<? extends T> a, AbstractMatrix<? extends T> b) {
		if(!a.isEqualSize(b)) throw new ShapeException("different size matrices.");
		
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) {
			if(entry[i][j] == null) entry[i][j] = createEntry();
			entry[i][j].setDifference(a.getValue(i, j), b.getValue(i,  j));
		}
	}
	
	
	
	
	public GenericMatrix<T> getInverse() {
        return getLUInverse();
    }
    
    // Invert via Gauss-Jordan elimination
    public GenericMatrix<T> getGaussInverse() {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        GenericMatrix<T> combo = new GenericMatrix<>(type, size, 2*size);
        for(int i=size; --i >= 0; ) combo.entry[i][i+size].setIdentity();
        combo.paste(this, 0, 0);
        combo.gaussJordan();
        GenericMatrix<T> inverse = new GenericMatrix<>((T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, 2*size }));
        return inverse;
    }
    

    public GenericMatrix<T> getLUInverse() {
        return new GenericLUDecomposition<>(this).getInverse();
    }
    

    public void solve(GenericMatrix<T> inputVectors) {
        inputVectors.entry = getSolutionsTo(inputVectors.entry);
    }
    
    /* (non-Javadoc)
     * @see kovacs.math.SquareMatrixAlgebra#solve(kovacs.math.AbstractVector[])
     */
    @Override
    public void solve(AbstractVector<T>[] inputVectors) {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        GenericMatrix<T> combo = new GenericMatrix<>(type, size, size + inputVectors.length);
        combo.paste(this, 0, 0);
        
        for(int col=inputVectors.length; --col >= 0; ) {
            AbstractVector<T> v = inputVectors[col];
            for(int row=size; --row >= 0; ) combo.setValue(row, size + col, v.getComponent(row));
        }

        combo.gaussJordan();
        
        for(int col=inputVectors.length; --col >= 0; ) {
            AbstractVector<T> v = inputVectors[col];
            for(int row=size; --row >= 0; ) v.setComponent(row, combo.getValue(row, size + col));
        }
    }
    

    public T[][] getSolutionsTo(T[][] inputMatrix) {
        if(!isSquare()) throw new SquareMatrixException();
        int size = rows();
        GenericMatrix<T> combo = new GenericMatrix<>(type, size, size + inputMatrix[0].length);
        combo.paste(this, 0, 0);
        ArrayUtil.paste(inputMatrix, entry, new int[] { 0, size });
        combo.gaussJordan();
        return (T[][]) ArrayUtil.subArray(combo.entry, new int[] { 0, size }, new int[] { size, combo.cols() });
    }
    
    
    
    /* (non-Javadoc)
     * @see kovacs.math.Inversion#invert()
     */
    @Override
    public void invert() {
        entry = getInverse().entry;
    }
    
    @Override
    public void addIdentity(double scaling) {
        if(!isSquare()) throw new SquareMatrixException();
        T increment = createEntry();
        increment.setIdentity();
        increment.scale(scaling);
        for(int i=rows(); --i >= 0; ) entry[i][i].add(increment);
    }
    
    // indx is the row permutation, returns true/false for even/odd row exchanges...
    protected boolean decomposeLU(int[] index) { return decomposeLU(index, 1e-30); }
    

    protected boolean decomposeLU(int[] index, double tinyValue) {
        if(!isSquare()) throw new SquareMatrixException();
        final int n = rows();

        double[] v = new double[n];
        boolean evenChanges = true;
        
        T product = createEntry();
        
        for(int i=n; --i >= 0; ) {
            double big = 0.0;
            for(int j=n; --j >= 0; ) {
                final double tmp = entry[i][j].abs();
                if(tmp > big) big = tmp;
            }
            if(big == 0.0) throw new IllegalStateException("Singular matrix in LU decomposition.");
            v[i] = 1.0 / big;
        }
        for(int j=0; j<n; j++ ) {
            int imax = -1;
            
            for(int i=j; --i >= 0; ) {
                T sum = (T) entry[i][j].copy();
                for(int k=i; --k >= 0; ) {
                    product.setProduct(entry[i][k], entry[k][j]);
                    sum.subtract(product);
                }
                entry[i][j] = sum;
            }
            double big = 0.0;
            for(int i=n; --i >= j; ) {
                T sum = (T) entry[i][j].copy();
                for(int k=j; --k >= 0; ) {
                    product.setProduct(entry[i][k], entry[k][j]);
                    sum.subtract(product);
                }
                entry[i][j] = sum;
                final double tmp = v[i] * sum.abs();
                if (tmp >= big) {
                    big=tmp;
                    imax=i;
                }
            }
            if(j != imax) {
                for(int k=n; --k >= 0; ) {
                    T tmp = entry[imax][k];
                    entry[imax][k] = entry[j][k];
                    entry[j][k] = tmp;
                }
                evenChanges = !evenChanges;
                v[imax] = v[j];
            }
            index[j] = imax;
            
            T diag = entry[j][j];
            
            if(diag.isNull()) {
                diag.setIdentity();
                diag.scale(tinyValue);
            }
            
            if(j != n-1) {
                T tmp = (T) diag.getInverse();
                for(int i=n; --i > j; ) entry[i][j].multiplyBy(tmp);
            }
        }
        return evenChanges;
    }	
 
}

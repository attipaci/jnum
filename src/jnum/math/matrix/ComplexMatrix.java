/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
// (C)2007 Attila Kovacs <attila[AT]sigmyne.com>

package jnum.math.matrix;


import jnum.data.ArrayUtil;
import jnum.math.Complex;
import jnum.math.ComplexAddition;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexScaling;
import jnum.math.Multiplication;
import jnum.math.Scalar;



// TODO: Auto-generated Javadoc
/**
 * The Class ComplexMatrix.
 */
public class ComplexMatrix extends GenericMatrix<Complex> implements ComplexScaling, ComplexConjugate, Multiplication<Complex>, ComplexAddition {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8842229635231169797L;

	/**
	 * Instantiates a new complex matrix.
	 */
	public ComplexMatrix() { super(Complex.class); }

	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public ComplexMatrix(double[][] a) throws IllegalArgumentException { 
		this((Complex[][]) ArrayUtil.asComplex(a));
	}
	
	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public ComplexMatrix(float[][] a) throws IllegalArgumentException { 
		this((Complex[][]) ArrayUtil.asComplex(a));
	}
	
	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public ComplexMatrix(Scalar[][] a) throws IllegalArgumentException { 
		this((Complex[][]) ArrayUtil.asComplex(a));
	}
	
	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param a the a
	 */
	public ComplexMatrix(GenericMatrix<?> a) {
		this();
		setData(a.entry);		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.GenericMatrix#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) {
		if(data instanceof Complex[][]) entry = (Complex[][]) data;
		else entry = (Complex[][]) ArrayUtil.asComplex(data);
	}
	
	// Check for rectangular shape
	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param a the a
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public ComplexMatrix(Complex[][] a) throws IllegalArgumentException { 
		super(a);
	}

	/**
	 * Instantiates a new complex matrix.
	 *
	 * @param rows the rows
	 * @param cols the cols
	 */
	public ComplexMatrix(int rows, int cols) { 
		super(Complex.class, rows, cols);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.ComplexConjugate#conjugate()
	 */
	@Override
	public void conjugate() {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) getValue(i, j).conjugate(); 
	}
	
	/**
	 * Gets the hermitian.
	 *
	 * @return the hermitian
	 */
	public ComplexMatrix getHermitian() {
		ComplexMatrix transpose = (ComplexMatrix) getTransposed();
		transpose.conjugate();
		return transpose;
	}
	
	
	/**
	 * Product.
	 *
	 * @param A the a
	 * @param B the b
	 * @return the complex matrix
	 */
	public static ComplexMatrix product(ComplexMatrix A, ComplexMatrix B) {
		ComplexMatrix product = new ComplexMatrix();
		product.setProduct(A, B);
		return product;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Multiplication#multiplyBy(java.lang.Object)
	 */
	@Override
	public void multiplyBy(Complex factor) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) getValue(i, j).multiplyBy(factor);
	}

	/**
	 * Multiply by i.
	 */
	public void multiplyByI() {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) getValue(i, j).multiplyByI();
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexAddition#addComplex(kovacs.math.Complex)
	 */
	@Override
	public void addComplex(Complex z) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) getValue(i, j).addComplex(z);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexAddition#subtractComplex(kovacs.math.Complex)
	 */
	@Override
	public void subtractComplex(Complex z) {
		for(int i=rows(); --i >= 0; ) for(int j=cols(); --j >= 0; ) getValue(i, j).subtractComplex(z);
	}

	
}

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

import jnum.data.ArrayUtil;
import jnum.math.Complex;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexMultiplication;
import jnum.math.ComplexScaling;
import jnum.math.Scalar;




public class ComplexVector extends GenericVector<Complex> implements ComplexScaling, ComplexConjugate, ComplexMultiplication<ComplexVector> {

	private static final long serialVersionUID = 6672815439678434695L;


	public ComplexVector() { super(Complex.class); }
	

	public ComplexVector(int size) {
		super(Complex.class, size);
	}
	

	public ComplexVector(double[] data) {
		super(ArrayUtil.asComplex(data));
	}
	

	public ComplexVector(float[] data) {
		super(ArrayUtil.asComplex(data));
	}

	
	public ComplexVector(Scalar[] data) {
		super(ArrayUtil.asComplex(data));
	}
	

	public ComplexVector(Complex[] data) {
		super(data);
	}
	
	@Override
    public ComplexVector clone() {
        return (ComplexVector) super.clone();
    }
    
    @Override
    public ComplexVector copy() {
        return (ComplexVector) super.copy();
    }
	
	/* (non-Javadoc)
	 * @see kovacs.math.GenericVector#getType()
	 */
	@Override
	public Class<Complex> getType() { return Complex.class; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.ComplexConjugate#conjugate()
	 */
	@Override
	public void conjugate() {
		for(int i=size(); --i >= 0; ) getComponent(i).conjugate();
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexScaling#scale(kovacs.math.Complex)
	 */
	@Override
	public void scale(Complex factor) {
		for(int i=size(); --i >= 0; ) getComponent(i).scale(factor);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Multiplication#multiplyBy(java.lang.Object)
	 */
	@Override
	public void multiplyBy(Complex o) {
		for(int i=size(); --i >= 0; ) getComponent(i).multiplyBy(o);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setProduct(Complex a, ComplexVector v) {
		for(int i=size(); --i >= 0; ) getComponent(i).setProduct(a, v.getComponent(i));
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexMultiplication#multiplyByI()
	 */
	@Override
	public void multiplyByI() {
		for(int i=size(); --i >= 0; ) getComponent(i).multiplyByI();
	}

}

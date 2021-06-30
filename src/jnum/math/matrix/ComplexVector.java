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

import jnum.data.ArrayUtil;
import jnum.math.Complex;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexMultiplication;
import jnum.math.ComplexScaling;
import jnum.math.Scalar;



/**
 * A mathematical vector class containing complex components.
 * 
 * @author Attila Kovacs
 *
 */
public class ComplexVector extends ObjectVector<Complex> implements ComplexScaling, ComplexConjugate, ComplexMultiplication<ComplexVector> {

	private static final long serialVersionUID = 6672815439678434695L;

	/**
	 * Constructs complex vector with the specified number of components. The vector is
	 * initialized with all components set to zero.
	 * 
	 * @param size The size (number of complex components) in this vector.
	 */
	public ComplexVector(int size) {
		super(Complex.class, size);
	}
	
	/**
     * Constructs a complex vector with the specified complex array as its backing storage. The
     * new vector references the supplied array, and so, any subsequent changes to the array
     * will affect the contents of the vector, and vice-versa.
     * 
     * @param data     Complex backing storage for the new vector.
     */
    public ComplexVector(Complex[] data) {
        super(data);
    }
	
	/**
	 * Constructs a complex vector based on the supplied data providing its real values. The imaginary part
	 * of all components is initialized to zero.
	 * 
	 * @param data     Real-valued data that defines the real parts of the new complex vector.
	 */
	public ComplexVector(double[] data) {
		super(ArrayUtil.asComplex(data));
	}
	
	/**
     * Constructs a complex vector based on the supplied data providing its real values. The imaginary part
     * of all components is initialized to zero.
     * 
     * @param data     Real-valued data that defines the real parts of the new complex vector.
     */
	public ComplexVector(float[] data) {
		super(ArrayUtil.asComplex(data));
	}

	/**
     * Constructs a complex vector based on the supplied data providing its real values. The imaginary part
     * of all components is initialized to zero.
     * 
     * @param data     Real-valued data that defines the real parts of the new complex vector.
     */
	public ComplexVector(Scalar[] data) {
		super(ArrayUtil.asComplex(data));
	}
	
	
	
	@Override
    public ComplexVector clone() {
        return (ComplexVector) super.clone();
    }
    
    @Override
    public ComplexVector copy() {
        return (ComplexVector) super.copy();
    }
	
	@Override
	public Class<Complex> getComponentType() { return Complex.class; }
	
	@Override
	public void conjugate() {
		for(int i=size(); --i >= 0; ) getComponent(i).conjugate();
	}

	@Override
	public void scale(Complex factor) {
		for(int i=size(); --i >= 0; ) getComponent(i).scale(factor);
	}

	@Override
	public void multiplyBy(Complex o) {
		for(int i=size(); --i >= 0; ) getComponent(i).multiplyBy(o);
	}

	@Override
	public void setProduct(Complex a, ComplexVector v) {
		for(int i=size(); --i >= 0; ) getComponent(i).setProduct(a, v.getComponent(i));
	}

	@Override
	public void multiplyByI() {
		for(int i=size(); --i >= 0; ) getComponent(i).multiplyByI();
	}

}

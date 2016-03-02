/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

//package crush.util;

import java.text.NumberFormat;


// TODO: Auto-generated Javadoc
//Add parsing

/**
 * The class for handling complex numbers (x + i*y). 
 * 
 * Complex numbers are a natural extension of 2D vectors ({@link jnum.math.Vector2D})
 * with complex arithmetic operations added on top. 
 */
public class Complex extends Vector2D implements 
	RealAddition, AbstractAlgebra<Complex>, Division<Complex>, Ratio<Complex, Complex>, ComplexConjugate,
	ComplexMultiplication<Complex>, ComplexScaling, ComplexAddition, TrigonometricFunctions, HyperbolicFunctions, PowFunctions {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8864818368218420412L;

	/**
	 * Instantiates a new complex number.
	 */
	public Complex() { super(); }

	/**
	 * Instantiates a new complex number from the supplied real and imaginary parts.
	 *
	 * @param re the real part 
	 * @param im the imaginary part
	 */
	public Complex(final double re, final double im) { super(re, im); }

	/**
	 * Instantiates a new complex number, as a copy of the supplied template.
	 *
	 * @param template the complex number to be copied.
	 */
	public Complex(final Complex template) { super(template); }

	/**
	 * Instantiates a new complex representation of a real number.
	 *
	 * @param real Complex representation of this real value.
	 */
	public Complex(final double real) { super(real, 0.0); }

	/**
	 * Real part of the complex number. Same as {@link jnum.math.Vector2D#x() }.
	 *
	 * @return the real part.
	 * @see jnum.math.Vector2D#x()
	 */
	public final double re() { return x(); }

	/**
	 * Imaginary part of the Complex number. Same as {@link jnum.math.Vector2D#y() }.
	 *
	 * @return the imaginary part.
	 * @see jnum.math.Vector2D#y()
	 */
	public final double im() { return y(); }
	
	/**
	 * Set the real part.
	 *
	 * @param value the new real part
	 */
	public final void setRealPart(final double value) { setX(value); }
	
	/**
	 * Set the imaginary part.
	 *
	 * @param value the new imaginary part
	 */
	public final void setImaginaryPart(final double value) { setY(value); }
	
	/**
	 * Check if is real.
	 *
	 * @return true, if is real
	 */
	public final boolean isReal() { return y() == 0.0; }
	
	/**
	 * Check if is imaginary.
	 *
	 * @return true, if is imaginary
	 */
	public final boolean isImaginary() { return x() == 0.0; }
	
	
	/**
	 * Checks whether this complex number equals to the real argument.
	 *
	 * @param value the real value to compare to
	 * @return true, if the complex value equals to the real argument
	 */
	public final boolean equals(final double value) {
		if(y() != 0.0) return false;
		return x() == value;
	}
	
	/**
	 * Add a real value.
	 *
	 * @param value the value
	 */
	@Override
	public final void addReal(final double value) { addX(value); }
	
	/**
	 * Add an imaginary value.
	 *
	 * @param value the value
	 */
	public final void addImaginary(final double value) { addY(value); }
	
	/**
	 * Subtract a real value.
	 *
	 * @param value the value
	 */
	@Override
	public final void subtractReal(final double value) { subtractX(value); }
	
	/**
	 * Subtract an imaginary value.
	 *
	 * @param value the value
	 */
	public final void subtractImaginary(final double value) { subtractY(value); }

		
	/**
	 * Complex conjugate.
	 * 
	 * a + i*b becomes a - i*b.
	 */
	@Override
	public final void conjugate() { scaleY(-1.0); }

	/**
	 * Calculate the conjugate of a complex number.
	 *
	 * @param z a Complex number.
	 * @return the the conjugate of the argument.
	 */
	public static Complex conjugate(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.conjugate();
		return c;
	}

	/**
	 * Negate.
	 */
	public final void negate() { invert(); }
	
	/**
	 * 1/z (the inverse under multiplication).
	 */
	@Override
	public final void inverse() { 
		conjugate();
		scale(1.0 / norm());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.math.InverseValue#getInverse()
	 */
	@Override
	public final Complex getInverse() {
		final Complex c = (Complex) clone();
		c.inverse();
		return c;
	}
	
	/**
	 * Calculate the inverse of a complex number under multiplication.
	 *
	 * @param z a Complex number
	 * @return 1/z
	 */
	public static Complex getInverse(final Complex z) {
		return z.getInverse();
	}
	
	/**
	 * Complex multiplication.
	 *
	 * @param z the Complex number to multiply with.
	 */
	@Override
	public final void multiplyBy(final Complex z) {
		setProduct(this, z);
	}
	
	/**
	 * Set this complex number to be the product of the two complex arguments (a*b).
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public final void setProduct(final Complex a, final Complex b) {
		set(
				a.x() * b.x() - a.y() * b.y(),
				a.x() * b.y() + a.y() * b.x()
		);
	}
	
	/**
	 * Complex division.
	 *
	 * @param z the complex number to divide by.
	 */
	@Override
	public final void divideBy(final Complex z) {
		setRatio(this, z);
	}
	
	/**
	 * Set this complex number to be the ratio of the two complex arguments (a/b).
	 *
	 * @param a the complex numerator.
	 * @param b the complex denominator.
	 */
	@Override
	public final void setRatio(final Complex a, final Complex b) {
		final double A = 1.0 / b.norm();
		set(
				a.x() * b.x() + a.y() * b.y(),
				a.y() * b.x() - a.x() * b.y()
		);
		scale(A);
	}
	
	/**
	 * Set this complex number to be the result of the specified operation between the two complex arguments.
	 *
	 * @param a the first complex argument.
	 * @param op the operation: '+', '-', '*', '/' or '^'.
	 * @param b the second complex argument.
	 */
	public void set(final Complex a, final char op, final Complex b) {
		switch(op) {
		case '*' : setProduct(a, b); break;
		case '/' : setRatio(a, b); break;
		case '^' : copy(a); pow(b); break;
		default: super.set(a, op, b);
		}
	}
	
	
	
	/**
	 * Multiply by i.
	 */
	@Override
	public final void multiplyByI() {
		set(-y(), x());
	}
	
	/**
	 * Divide by i.
	 */
	public final void divideByI() {
		set(y(), -x());
	}


	/**
	 * z<sup>y</sup>. 
	 * 
	 * Raise this complex number to the y<sup>th</sup> power.
	 *
	 * @param y the power exponent.
	 */
	@Override
	public final void pow(final double y) {
		setPolar(Math.pow(length(), y), y * angle());
	}

	/**
	 * Raise this complex number to the z<sup>th</sup> complex power.
	 *
	 * @param z the complex power exponent
	 */
	public final void pow(final Complex z) {
		log();
		multiplyBy(z);
		exp();
	}
	
	/**
	 * Calculate the n<sup>th</sup> power of the complex argument.
	 *
	 * @param z the complex argument to raise.
	 * @param exp the power exponent.
	 * @return the requested power of the complex argument.
	 */
	public static Complex pow(final Complex z, final double exp) {
		final Complex c = (Complex) z.clone();
		c.pow(exp);
		return c;
	}

	/**
	 * Calculate the complex n<sup>th</sup> power of a complex number.
	 *
	 * @param z the complex argument
	 * @param exp the complex power exponent
	 * @return the z<sup>exp</sup>
	 */
	public static Complex pow(final Complex z, final Complex exp) {
		final Complex c = (Complex) z.clone();
		c.pow(exp);
		return c;
	}

	/**
	 * sqrt(z). 
	 * 
	 * 
	 * Take the square-root of this complex number.
	 */
	@Override
	public final void sqrt() { 
		setPolar(Math.sqrt(length()), 0.5 * angle());
	}

	/**
	 * cbrt(z). 
	 * 
	 * 
	 * Take the cubic root of this complex number.
	 */
	public final void cbrt() { 
		setPolar(Math.cbrt(length()), 0.33333333333333 * angle());
	}
	
	/**
	 * Return the square-root of the Complex argument.
	 *
	 * @param z the complex argument.
	 * @return the square-root of the argument.
	 */
	public static Complex sqrt(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.sqrt();
		return c;
	}	

	/**
	 * Return the cubic root of the Complex argument.
	 *
	 * @param z the complex argument.
	 * @return the cubic root of the argument.
	 */
	public static Complex cbrt(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cbrt();
		return c;
	}	
	
	
	/**
	 * exp(z).
	 */
	@Override
	public final void exp() {
		setPolar(Math.exp(x()), y());
	}	

	/**
	 * Calculates the exponential of the argument.
	 *
	 * @param z the Complex argument
	 * @return exp(z)
	 */
	public static Complex exp(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.exp();
		return c;
	}


	/**
	 * log(z).
	 * 
	 */
	@Override
	public final void log() {
		set(Math.log(length()), angle());
	}

	/**
	 * Calculates the natural logarithm of the complex argument.
	 *
	 * @param z the complex argument
	 * @return log(z).
	 */
	public static Complex log(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.log();
		return c;
	}


	/**
	 * cos(z).
	 */
	@Override
	public final void cos() {
		final Complex e = (Complex) clone();
		e.multiplyByI();
		e.exp();
		e.add(getInverse(e));
		e.scale(0.5);
	}

	/**
	 * Calculates the cosine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return the cos(z)
	 */
	public static Complex cos(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cos();
		return c;
	}

	/**
	 * sin(z).
	 */
	@Override
	public final void sin() {
		final Complex e = (Complex) clone();
		e.multiplyByI();
		e.exp();
		e.subtract(getInverse(e));
		e.scale(0.5);
		e.divideByI();
	}

	/**
	 * Calculates the sine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return sin(z)
	 */
	public static Complex sin(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.sin();
		return c;
	}

	/**
	 * tan(z).
	 */
	@Override
	public final void tan() {
		final Complex c = cos(this);
		sin();
		divideBy(c);
	}

	/**
	 * Calculates the tangent of the complex argument.
	 *
	 * @param z the complex argument.
	 * @return tan(z)
	 */
	public static Complex tan(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.tan();
		return c;
	}


	/**
	 * cosh(z).
	 */
	@Override
	public final void cosh() {
		final Complex e = exp(this);
		e.add(getInverse(e));
		e.scale(0.5);
	}

	/**
	 * Calculates the hyperbolic cosine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return the cosh(z)
	 */
	public static Complex cosh(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cos();
		return c;
	}

	/**
	 * sinh(z).
	 */
	@Override
	public final void sinh() {
		final Complex e = exp(this);
		e.subtract(getInverse(e));
		e.scale(0.5);
		e.divideByI();
	}

	/**
	 * Calculates the hyperbolic sine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return sinh(z)
	 */
	public static Complex sinh(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.sin();
		return c;
	}

	/**
	 * tanh(z).
	 */
	@Override
	public final void tanh() {
		final Complex c = cos(this);
		sin();
		divideBy(c);
	}

	/**
	 * Calculates the hyperbolic tangent of the complex argument.
	 *
	 * @param z the complex argument
	 * @return tanh(z)
	 */
	public static Complex tanh(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.tan();
		return c;
	}


	/**
	 * Performs the requested mathematical operation on this complex number object.
	 *
	 * The operation can be '+' or '-' ({@link jnum.math.Vector2D#math(char, Vector2D)}) and '*', '/', or '^' (for
	 * raising power).
	 *
	 * @param op the operator ('+', '-', '*', '/', '^').
	 * @param z the complex argument.
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public final void math(final char op, final Complex z) throws IllegalArgumentException {
		switch(op) {
		case '*': 
			multiplyBy(z);
			break;
		case '/': 
			divideBy(z);
			break;
		case '^':
			pow(z);
			break;
		default: 
			super.math(op, z);
		}
	}


	/**
	 * Calculates the requested mathematical operation between the two complex arguments.
	 *
	 * @param a the a
	 * @param op the operator, same as for {@link #math(char, Complex)}.
	 * @param b the b
	 * @return the complex result.
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Complex math(Complex a, char op, Complex b) throws IllegalArgumentException {
		final Complex result = (Complex) a.clone();
		result.math(op, b);
		return result;
	}


	/* (non-Javadoc)
	 * @see kovacs.util.Vector2D#math(char, double)
	 */
	@Override
	public final void math(char op, double b) throws IllegalArgumentException {
		switch(op) {
		case '+': addX(b); break;
		case '-': subtractX(b); break;
		case '^': pow(b); break;	    
		default: super.math(op, b);
		}
	}

	/**
	 * Calculates the requested mathematical operation between a complex and a real argument.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the complex
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Complex math(Complex a, char op, double b) throws IllegalArgumentException {
		Complex result = (Complex) a.clone();
		result.math(op, b);
		return result;
	}


	/**
	 * Converts the complex number to a string in the format 'a + bi' Using the provied decimal format. E.g. '1.123+0.451i'.
	 *
	 * @param nf the nf
	 * @return the string
	 */
	@Override
	public final String toString(NumberFormat nf) { return nf.format(x()) + (y() < 0 ? "" : "+") + nf.format(y()) + "i"; }

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#toString()
	 */
	@Override
	public final String toString() { return x() + (y() < 0 ? "" : "+") + y() + "i"; }

	/* (non-Javadoc)
	 * @see kovacs.util.math.IdentityValue#setIdentity()
	 */
	@Override
	public void setIdentity() {
		set(1.0, 0.0);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexScaling#scale(kovacs.math.Complex)
	 */
	@Override
	public void scale(Complex x) {
		multiplyBy(x);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexAddition#addComplex(kovacs.math.Complex)
	 */
	@Override
	public void addComplex(Complex x) {
		add(x);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.ComplexAddition#subtractComplex(kovacs.math.Complex)
	 */
	@Override
	public void subtractComplex(Complex x) {
		subtract(x);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#square()
	 */
	@Override
	public void square() {
		set(x() * x() - y() * y(), 2.0 * x() * y());
	}
		
	/**
	 * Creates the array.
	 *
	 * @param size the size
	 * @return the complex[]
	 */
	public static Complex[] createArray(int size) {
		Complex[] z = new Complex[size];
		for(int i=size; --i >= 0; ) z[i] = new Complex();
		return z;
	}
	
	
}

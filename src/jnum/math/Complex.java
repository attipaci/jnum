/*******************************************************************************
 * Copyright (c) 2020 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.math;

import java.text.NumberFormat;
import java.util.stream.IntStream;




/**
 * A class for handling complex numbers, with real and imaginary parts, i.e. as z = (a + i * b). 
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
	 * @param real     real value whose complex representation is to be constructed.
	 */
	public Complex(final double real) { super(real, 0.0); }

	@Override
    public Complex copy() {
	    return (Complex) super.copy();
	}
	
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
	 * Sets the real part of this complex number.
	 *
	 * @param value the new real part
	 */
	public final void setRealPart(final double value) { setX(value); }
	
	/**
	 * Sets the imaginary part of this complex number.
	 *
	 * @param value the new imaginary part
	 */
	public final void setImaginaryPart(final double value) { setY(value); }
	
	/**
	 * Checks if this complex number is a real number, i.e. if its imaginary part is zero.
	 *
	 * @return true, if is real
	 */
	public final boolean isReal() { return y() == 0.0; }
	
	/**
	 * Checks if this complex number is an imaginary number, i.e. if its real part is zero.
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
	

	@Override
	public final void add(final double value) { addX(value); }
	
	/**
	 * Adds an imaginary value to this complex number.
	 *
	 * @param value    increment to imaginary part.
	 */
	public final void addImaginary(final double value) { addY(value); }
	

	@Override
	public final void subtract(final double value) { subtractX(value); }
	
	/**
	 * Subtracts an imaginary value from this complex number.
	 *
	 * @param value    decrement to imaginary part.
	 */
	public final void subtractImaginary(final double value) { subtractY(value); }


	@Override
	public final void conjugate() { scaleY(-1.0); }

	
	/**
	 * Negates this complex number, i.e. z becomes -z.
	 */
	public final void negate() { flip(); }
	

	@Override
	public void inverse() { 
		conjugate();
		scale(1.0 / absSquared());
	}
	

	@Override
	public final Complex getInverse() {
		final Complex c = (Complex) clone();
		c.inverse();
		return c;
	}
	

	public static Complex getInverse(final Complex z) {
		return z.getInverse();
	}
	
	@Override
	public final void multiplyBy(final Complex z) {
		setProduct(this, z);
	}
	

	@Override
	public final void setProduct(final Complex a, final Complex b) {
		set(a.x() * b.x() - a.y() * b.y(), a.x() * b.y() + a.y() * b.x());
	}
	

	@Override
	public final void divideBy(final Complex z) {
		setRatio(this, z);
	}
	

	@Override
	public final void setRatio(final Complex a, final Complex b) {
		final double A = 1.0 / b.absSquared();
		set(
				a.x() * b.x() + a.y() * b.y(),
				a.y() * b.x() - a.x() * b.y()
		);
		scale(A);
	}
	
	/**
	 * Sets this complex number to be the result of the specified operation between the two complex arguments.
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
	
	
	@Override
	public final void multiplyByI() {
		set(-y(), x());
	}
	
	/**
	 * Divides by this complex number by i (the imaginary unit).
	 */
	public final void divideByI() {
		set(y(), -x());
	}


	@Override
	public void pow(final double y) {
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
     * Gets the conjugate of a complex number.
     *
     * @param z a Complex number.
     * @return the the conjugate of the argument.
     */
    public static Complex conjugateOf(final Complex z) {
        final Complex c = (Complex) z.clone();
        c.conjugate();
        return c;
    }
	
	/**
	 * Calculates the n<sup>th</sup> power of the complex argument.
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
	 * Calculates the complex n<sup>th</sup> power of a complex number.
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


	@Override
	public final void sqrt() { 
		setPolar(Math.sqrt(length()), 0.5 * angle());
	}

	/**
	 * Takes the cubic root of this complex number i.e., z becomes cbrt(z). 
	 * 
	 */
	public final void cbrt() { 
		setPolar(Math.cbrt(length()), 0.33333333333333 * angle());
	}
	
	/**
	 * Gets the square-root of the Complex argument.
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
	 * Gets the cubic root of the Complex argument.
	 *
	 * @param z the complex argument.
	 * @return the cubic root of the argument.
	 */
	public static Complex cbrt(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cbrt();
		return c;
	}	
	
	
	@Override
	public final void exp() {
		setPolar(Math.exp(x()), y());
	}	

	/**
	 * Gets the exponential of the argument.
	 *
	 * @param z the Complex argument
	 * @return exp(z)
	 */
	public static Complex exp(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.exp();
		return c;
	}


	@Override
    public final void expm1() {
        if(y() == 0) setX(Math.expm1(x()));
        else {
            final double siny2 = Math.sin(0.5*y());
            set(Math.expm1(x()) * Math.cos(y()) - 2.0 * siny2 * siny2, Math.exp(x()) * Math.sin(y()));
        }
    }
    
	/**
     * Calculates the exp(z)-1 of the argument.
     *
     * @param z the Complex argument
     * @return exp(z)-1
     */
    public static Complex expm1(final Complex z) {
        final Complex c = (Complex) z.clone();
        c.expm1();
        return c;
    }
	

	@Override
	public final void log() {
		set(Math.log(length()), angle());
	}

	/**
	 * Gets the natural logarithm of the complex argument.
	 *
	 * @param z the complex argument
	 * @return log(z).
	 */
	public static Complex log(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.log();
		return c;
	}

	@Override
    public final void log1p() {
        if(y() == 0.0) setX(Math.log1p(x()));
        else set(0.5 * Math.log1p(x()*(x()+2.0)+y()*y()), Math.atan2(y(), 1.0 + x()));
    }
	
	
	/**
     * Gets the natural log(1+z) of the complex argument z.
     *
     * @param z the complex argument
     * @return log(1+z).
     */
    public static Complex log1p(final Complex z) {
        final Complex c = (Complex) z.clone();
        c.log1p();
        return c;
    }
	

	@Override
	public final void cos() {
		final Complex e = (Complex) clone();
		e.multiplyByI();
		e.exp();
		e.add(getInverse(e));
		e.scale(0.5);
	}

	/**
	 * Gets the cosine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return the cos(z)
	 */
	public static Complex cos(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cos();
		return c;
	}

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
	 * Gets the sine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return sin(z)
	 */
	public static Complex sin(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.sin();
		return c;
	}


	@Override
	public final void tan() {
		final Complex c = cos(this);
		sin();
		divideBy(c);
	}

	/**
	 * Gets the tangent of the complex argument.
	 *
	 * @param z the complex argument.
	 * @return tan(z)
	 */
	public static Complex tan(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.tan();
		return c;
	}


	@Override
	public final void cosh() {
		final Complex e = exp(this);
		e.add(getInverse(e));
		e.scale(0.5);
	}

	/**
	 * Gets the hyperbolic cosine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return the cosh(z)
	 */
	public static Complex cosh(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.cos();
		return c;
	}


	@Override
	public final void sinh() {
		final Complex e = exp(this);
		e.subtract(getInverse(e));
		e.scale(0.5);
		e.divideByI();
	}

	/**
	 * Gets the hyperbolic sine of the complex argument.
	 *
	 * @param z the complex argument
	 * @return sinh(z)
	 */
	public static Complex sinh(final Complex z) {
		final Complex c = (Complex) z.clone();
		c.sin();
		return c;
	}

	
	@Override
	public final void tanh() {
		final Complex c = cos(this);
		sin();
		divideBy(c);
	}

	/**
	 * Gets the hyperbolic tangent of the complex argument.
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
	 * @param a the complex argument
	 * @param op the operator, e.g. '+', '-', '*', '/'
	 * @param b the real argument
	 * @return the complex result
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


	@Override
	public final String toString() { return x() + (y() < 0 ? "" : "+") + y() + "i"; }


	@Override
	public void setIdentity() {
		set(1.0, 0.0);
	}


	@Override
	public void scale(Complex x) {
		multiplyBy(x);
	}


	@Override
	public void add(Complex x) {
		add(x);
	}

	@Override
    public void add(double re, double im) {
	    super.add(re, im);
	}

	@Override
	public void subtract(Complex x) {
		subtract(x);
	}


	@Override
	public void square() {
		set(x() * x() - y() * y(), 2.0 * x() * y());
	}
		

	/**
	 * Creates a an initialized array of complex numbers and of the specified size. The
	 * Array is initialized with references to independent zero complex values in every slot.
	 * 
	 * @param size     the number of complex elements in the new array
	 * @return         a new initialized complex array. 
	 */
	public static Complex[] createArray(int size) {
		final Complex[] z = new Complex[size];
		IntStream.range(0,  z.length).parallel().forEach(i -> z[i] = new Complex());
		return z;
	}
	
	/**
	 * Gets a deep copy of a complex array, which is identical to the argument but shares
	 * no references with the original. In other words its returns a completely independent
	 * copy of the original complex array.
	 * 
	 * @param array    The complex array to copy.
	 * @return         An independent copy of the supplied original array.
	 */
    public static Complex[] copyOf(Complex[] array) {
        Complex[] copy = new Complex[array.length];
        IntStream.range(0, array.length).parallel().filter(i -> array[i] != null).forEach(i -> copy[i] = array[i].copy());
        return copy;
    }
	
	
}

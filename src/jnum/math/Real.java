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

import jnum.Copiable;
import jnum.CopyCat;
import jnum.ExtraMath;
import jnum.Util;
import jnum.data.BlankingValue;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.text.Parser;

import java.io.Serializable;
import java.lang.reflect.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Real.
 */
public class Real implements Serializable, LinearAlgebra<Real>, AbstractAlgebra<Real>, AbsoluteValue, Copiable<Real>, CopyCat<Real>, Cloneable, BlankingValue, 
	PowFunctions, TrigonometricFunctions, TrigonometricInverseFunctions, HyperbolicFunctions, HyperbolicInverseFunctions,
	NumberFormating, DecimalFormating, Parser, Metric<Real>, Comparable<Real> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4757211558088511549L;
	
	/** The value. */
	private double value;
	
	/**
	 * Instantiates a new real.
	 */
	public Real() {}
	
	/**
	 * Instantiates a new real.
	 *
	 * @param value the value
	 */
	public Real(double value) {
		setValue(value);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public final double getValue() { return value; }
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public final void setValue(double value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.CopyCat#copy(java.lang.Object)
	 */
	@Override
	public final void copy(Real template) {
		value = template.value;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public final void addMultipleOf(Real o, double factor) {
		value += factor * o.value;		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#subtract(java.lang.Object)
	 */
	@Override
	public final void subtract(Real o) {
		value -= o.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public final void add(Real o) {
		value += o.value;
	}
	
	/**
	 * Subtract.
	 *
	 * @param x the x
	 */
	public final void subtract(double x) {
		value -= x;
	}

	/**
	 * Adds the.
	 *
	 * @param x the x
	 */
	public final void add(double x) {
		value += x;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Scalable#scale(double)
	 */
	@Override
	public final void scale(double factor) {
		value *= factor;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbsoluteValue#abs()
	 */
	@Override
	public final double abs() {
		return Math.abs(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricInverseFunctions#acos()
	 */
	@Override
	public final void acos() {
		value = Math.acos(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricInverseFunctions#asin()
	 */
	@Override
	public final void asin() {
		value = Math.asin(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricInverseFunctions#atan()
	 */
	@Override
	public final void atan() {
		value = Math.atan(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricFunctions#cos()
	 */
	@Override
	public final void cos() {
		value = Math.cos(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicFunctions#cosh()
	 */
	@Override
	public final void cosh() {
		value = Math.cosh(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#exp()
	 */
	@Override
	public final void exp() {
		value = Math.exp(value);
	}

	/* (non-Javadoc)
     * @see kovacs.math.PowFunctions#expm1()
     */
    @Override
    public final void expm1() {
        value = Math.expm1(value);
    }
	
	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#invert()
	 */
	@Override
	public final void invert() {
		value = 1.0/value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#log()
	 */
	@Override
	public final void log() {
		value = Math.log(value);
	}

	/* (non-Javadoc)
     * @see kovacs.math.PowFunctions#log1p()
     */
    @Override
    public final void log1p() {
        value = Math.log1p(value);
    }
	
	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#pow(double)
	 */
	@Override
	public final void pow(double n) {
		value = Math.pow(value, n);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricFunctions#sin()
	 */
	@Override
	public final void sin() {
		value = Math.sin(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicFunctions#sinh()
	 */
	@Override
	public final void sinh() {
		value = Math.sinh(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#sqrt()
	 */
	@Override
	public final void sqrt() {
		value = Math.sqrt(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.PowFunctions#square()
	 */
	@Override
	public final void square() {
		value = value * value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.TrigonometricFunctions#tan()
	 */
	@Override
	public final void tan() {
		value = Math.tan(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicFunctions#tanh()
	 */
	@Override
	public final void tanh() {
		value = Math.tanh(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Multiplicative#multiplyBy(java.lang.Object)
	 */
	@Override
	public final void multiplyBy(Real o) {
		value *= o.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Copiable#copy()
	 */
	@Override
	public final Real copy() {
		return (Real) clone();
	}

	/* (non-Javadoc)
	 * @see kovacs.data.BlankingValue#isNaN()
	 */
	@Override
	public final boolean isNaN() {
		return Double.isNaN(value);
	}

	public final double value() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.text.NumberFormating#toString(java.text.NumberFormat)
	 */
	@Override
	public final String toString(NumberFormat nf) {
		return nf.format(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.text.DecimalFormating#toString(int)
	 */
	@Override
	public final String toString(int decimals) {
		return Util.f[decimals].format(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.text.Parser#parse(java.lang.String)
	 */
	@Override
	public final void parse(String text) throws NumberFormatException, IllegalArgumentException {
		value = Double.parseDouble(text);		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public final double distanceTo(Real point) {
		return point.value - value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.IdentityValue#setIdentity()
	 */
	@Override
	public final void setIdentity() {
		value = 1.0;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.InverseValue#getInverse()
	 */
	@Override
	public final Real getInverse() {
		return new Real(1.0 / value);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.InverseValue#inverse()
	 */
	@Override
	public void inverse() {
		value = 1.0 / value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setProduct(Real a, Real b) {
		value = a.value * b.value;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(Real o) {
		return Double.compare(this.value, o.value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		return value == 0.0;
	}


	/**
	 * Dot.
	 *
	 * @param scalar the scalar
	 * @return the real
	 */
	public Real dot(Real scalar) {
		return new Real(value * scalar.value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbsoluteValue#norm()
	 */
	@Override
	public double asquare() {
		return value * value;
	}

	/**
	 * Array from.
	 *
	 * @param value the value
	 * @return the real[]
	 */
	public static Real[] arrayFrom(double[] value) {
		Real[] array = new Real[value.length];
		for(int i=0; i<value.length; i++) array[i].value = value[i];
		return array;
	}
	
	/**
	 * Array from.
	 *
	 * @param value the value
	 * @return the real[]
	 */
	public static Real[] arrayFrom(float[] value) {
		Real[] array = new Real[value.length];
		for(int i=0; i<value.length; i++) array[i].value = value[i];
		return array;
	}
	
	/**
	 * Array from.
	 *
	 * @param value the value
	 * @return the object
	 */
	public static Object arrayFrom(Object value) {
		if(value instanceof double[]) return arrayFrom((double[]) value);
		else if(value instanceof float[]) return arrayFrom((float[]) value);
		else if(value instanceof Object[]) {
			Object[] array = (Object[]) value;
			Object[] realArray = null;
			for(int i=0; i<array.length; i++) {
				Object entry = arrayFrom(array[i]);
				if(realArray == null) {
					try { realArray = (Object[]) Array.newInstance(entry.getClass(), array.length); }
					catch(Exception e) { e.printStackTrace(); }		
				}
				realArray[i] = entry;				
			}
			return realArray;
		}
		return null;
	}
	
	
		
	/**
	 * To double array.
	 *
	 * @param array the array
	 * @return the double[]
	 */
	public double[] toDoubleArray(Real[] array) {
		double[] doubles = new double[array.length];
		for(int i=0; i<array.length; i++) doubles[i] = array[i].value;
		return doubles;
	}
	
	/**
	 * To float array.
	 *
	 * @param array the array
	 * @return the float[]
	 */
	public float[] toFloatArray(Real[] array) {
		float[] floats = new float[array.length];
		for(int i=0; i<array.length; i++) floats[i] = (float) array[i].value;
		return floats;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setSum(Real a, Real b) {
		value = a.value + b.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setDifference(Real a, Real b) {
		value = a.value - b.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#zero()
	 */
	@Override
	public final void zero() {
		value = 0.0;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicInverseFunctions#asinh()
	 */
	@Override
	public final void asinh() {
		value = ExtraMath.asinh(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicInverseFunctions#acosh()
	 */
	@Override
	public final void acosh() {
		value = ExtraMath.acosh(value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.HyperbolicInverseFunctions#atanh()
	 */
	@Override
	public final void atanh() {
		value = ExtraMath.atanh(value);
	}
	
	
}


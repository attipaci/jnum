/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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


import java.text.*;

import jnum.Copiable;
import jnum.CopyCat;
import jnum.ExtraMath;
import jnum.Util;
import jnum.data.BlankingValue;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.text.Parser;
import jnum.text.StringParser;
import jnum.util.HashCode;

import java.io.Serializable;
import java.lang.reflect.*;


public class Scalar extends Number implements Serializable, LinearAlgebra<Scalar>, AbstractAlgebra<Scalar>, AbsoluteValue, Copiable<Scalar>, CopyCat<Scalar>, Cloneable, BlankingValue, 
	PowFunctions, TrigonometricFunctions, TrigonometricInverseFunctions, HyperbolicFunctions, HyperbolicInverseFunctions,
	NumberFormating, DecimalFormating, Parser, Metric<Scalar>, Comparable<Scalar> {
	

	private static final long serialVersionUID = -4757211558088511549L;
	

	private double value;
	
	/**
	 * Instantiates a new scalar (real) value.
	 */
	public Scalar() {}
	
	/**
	 * Instantiates a new scala (real) value.
	 *
	 * @param value the value
	 */
	public Scalar(double value) {
		setValue(value);
	}
	
	
	@Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(value); }
	
	@Override
    public boolean equals(Object o) {
	    if(this == o) return false;
	    if(!(o instanceof Number)) return false;
	    
	    Number n = (Number) o;
	    if(!Util.equals(value, n.doubleValue())) return false;
	 
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Scalar clone() {
		try { return (Scalar) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	

	public final double getValue() { return value; }
	

	public final void setValue(double value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see jnum.CopyCat#copy(java.lang.Object)
	 */
	@Override
	public final void copy(Scalar template) {
		value = template.value;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public final void addScaled(Scalar o, double factor) {
		value += factor * o.value;		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#subtract(java.lang.Object)
	 */
	@Override
	public final void subtract(Scalar o) {
		value -= o.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public final void add(Scalar o) {
		value += o.value;
	}
	

	public final void subtract(double x) {
		value -= x;
	}


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
	public final void multiplyBy(Scalar o) {
		value *= o.value;
	}

	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public final Scalar copy() {
		return clone();
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
	public final void parse(String text, ParsePosition pos) throws IllegalArgumentException {
	    // TODO redo without Parser creation...
	    value = Double.parseDouble(new StringParser(text, pos).nextToken());		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public final double distanceTo(Scalar point) {
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
	public final Scalar getInverse() {
		return new Scalar(1.0 / value);
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
	public final void setProduct(Scalar a, Scalar b) {
		value = a.value * b.value;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public final int compareTo(Scalar o) {
		return Double.compare(this.value, o.value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		return value == 0.0;
	}


	public Scalar dot(Scalar scalar) {
		return new Scalar(value * scalar.value);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbsoluteValue#norm()
	 */
	@Override
	public double absSquared() {
		return value * value;
	}


	public static Scalar[] arrayFrom(double[] value) {
		Scalar[] array = new Scalar[value.length];
		for(int i=0; i<value.length; i++) array[i].value = value[i];
		return array;
	}
	

	public static Scalar[] arrayFrom(float[] value) {
		Scalar[] array = new Scalar[value.length];
		for(int i=0; i<value.length; i++) array[i].value = value[i];
		return array;
	}
	

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
					catch(Exception e) { 
					    Util.error(Scalar.class, e);
					    return null;
					}		
				}
				realArray[i] = entry;				
			}
			return realArray;
		}
		return null;
	}
	

	public double[] toDoubleArray(Scalar[] array) {
		double[] doubles = new double[array.length];
		for(int i=0; i<array.length; i++) doubles[i] = array[i].value;
		return doubles;
	}
	

	public float[] toFloatArray(Scalar[] array) {
		float[] floats = new float[array.length];
		for(int i=0; i<array.length; i++) floats[i] = (float) array[i].value;
		return floats;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setSum(Scalar a, Scalar b) {
		value = a.value + b.value;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setDifference(Scalar a, Scalar b) {
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

    /* (non-Javadoc)
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return (float) value;
    }

    /* (non-Javadoc)
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /* (non-Javadoc)
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return (long) value;
    }
	
	
}


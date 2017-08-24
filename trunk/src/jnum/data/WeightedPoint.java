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


package jnum.data;

import java.text.NumberFormat;

import jnum.Util;
import jnum.math.Division;
import jnum.math.LinearAlgebra;
import jnum.math.Multiplicative;
import jnum.math.Ratio;
import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class WeightedPoint.
 */
public class WeightedPoint extends RealValue implements Multiplicative<WeightedPoint>, Division<WeightedPoint>, Ratio<WeightedPoint, WeightedPoint>, LinearAlgebra<WeightedPoint>, 
Accumulating<WeightedPoint>
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6583109762992313591L;
	/** The weight. */
	private double weight;

	/**
	 * Instantiates a new weighted point.
	 */
	public WeightedPoint() {}

	/**
	 * Instantiates a new weighted point.
	 *
	 * @param template the template
	 */
	public WeightedPoint(final WeightedPoint template) {
		copy(template);
	}

	/**
	 * Instantiates a new weighted point.
	 *
	 * @param value the value
	 * @param weight the weight
	 */
	public WeightedPoint(final double value, final double weight) { 
		super(value);
		this.weight = weight;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof WeightedPoint)) return false;
		if(!super.equals(o)) return false;
		
		WeightedPoint p = (WeightedPoint) o;
		if(isExact()) if(!p.isExact()) return false;
		return Util.equals(weight, p.weight, 1e-6);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(weight);
	}
	
	/**
	 * Weight.
	 *
	 * @return the double
	 */
	public final double weight() { return weight; }	
	/**
	 * Sets the weight.
	 *
	 * @param w the new weight
	 */
	public final void setWeight(final double w) { this.weight = w; }
	
	
	/**
	 * Adds the weight.
	 *
	 * @param dw the dw
	 */
	public final void addWeight(double dw) { weight += dw; }
	
	/**
	 * Scale value.
	 *
	 * @param factor the factor
	 */
	public final void scaleValue(double factor) { super.scale(factor); }
	
	/**
	 * Scale weight.
	 *
	 * @param factor the factor
	 */
	public final void scaleWeight(double factor) { weight *= factor; }


	@Override
    public final void noData() { 
	    super.noData();
		weight = 0.0;
	}

	/**
	 * Checks if is na n.
	 *
	 * @return true, if is na n
	 */
	public final boolean isNaN() { return isNaN(this); }

	/**
	 * Checks if is na n.
	 *
	 * @param point the point
	 * @return true, if is na n
	 */
	public final static boolean isNaN(WeightedPoint point) { 
		return Double.isNaN(point.value()) || point.weight == 0.0;
	}

	/**
	 * Exact.
	 */
	public final void exact() { weight = Double.POSITIVE_INFINITY; }

	/**
	 * Checks if is exact.
	 *
	 * @return true, if is exact
	 */
	public final boolean isExact() { return Double.isInfinite(weight); }

	/**
	 * Copy.
	 *
	 * @param x the x
	 */
	public void copy(final WeightedPoint x) {
		setValue(x.value());
		weight = x.weight;
	}
	
	/**
	 * Adds the.
	 *
	 * @param x the x
	 */
	@Override
	public final void add(final WeightedPoint x) {
		setSum(this, x);
	}
		
	/**
	 * Subtract.
	 *
	 * @param x the x
	 */
	@Override
	public final void subtract(final WeightedPoint x) {
		setDifference(this, x);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(final WeightedPoint a, final WeightedPoint b) {
		final double w = a.weight * b.weight;
		weight = w > 0.0 ? w / (a.weight + b.weight) : 0.0;
		setValue(a.value() + b.value());
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(final WeightedPoint a, final WeightedPoint b) {
		final double w = a.weight * b.weight;
		weight = w > 0.0 ? w / (a.weight + b.weight) : 0.0;
		setValue(a.value() - b.value());
	}


	/* (non-Javadoc)
	 * @see jnum.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addScaled(final WeightedPoint x, final double factor) {
		add(factor * x.value());
		if(weight == 0.0) return;
		if(x.weight == 0.0) weight = 0.0;
		else weight = weight * x.weight / (x.weight + factor * factor * weight);
	}
	
	/**
	 * Average.
	 *
	 * @param x the x
	 */
	public void average(final WeightedPoint x) {
		average(x.value(), x.weight);
	}
	
	/**
	 * Average.
	 *
	 * @param v the v
	 * @param w the w
	 */
	public void average(final double v, final double w) {
		setValue(weight * value() + w * v);
		weight += w;
		if(weight > 0.0) setValue(value() / weight);		
	}
	
	@Override
    public void accumulate(WeightedPoint x, double w) {
	    add(w * x.weight * x.value());
	    weight += w * x.weight;
	}
	
	@Override
    public void accumulate(WeightedPoint x, double w, double gain) {
        add(w * x.weight * x.value() * gain);
        weight += w * x.weight * gain * gain;
    }
	
	@Override
    public void startAccumulation() {
	    setValue(value() * weight);
	}
	
	@Override
    public void endAccumulation() {
	    setValue(value() / weight);
	}
	
	/**
	 * Scale.
	 *
	 * @param x the x
	 */
	@Override
	public final void scale(final double x) {
		scaleValue(x);
		weight /= x*x;
	}

	
	/* (non-Javadoc)
	 * @see kovacs.math.Multiplicative#multiplyBy(java.lang.Object)
	 */
	@Override
	public final void multiplyBy(final WeightedPoint p) {
		setProduct(this, p);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void setProduct(final WeightedPoint a, final WeightedPoint b) {
		final double w = a.weight * b.weight;
		weight = w > 0.0 ? w / (a.value() * a.value() * a.weight + b.value() * b.value() * b.weight) : 0.0;
		setValue(a.value() * b.value());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Division#divideBy(java.lang.Object)
	 */
	@Override
	public final void divideBy(final WeightedPoint p) {
		setRatio(this, p);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Ratio#setRatio(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setRatio(final WeightedPoint a, final WeightedPoint b) {
		final double w = a.weight * b.weight;
		if(w > 0.0) {
			final double b2 = b.value() * b.value();	
			weight = b2 * b2 * w / (a.weight * a.value() * a.value() + b.weight * b2);
		}
		else weight = 0.0;
		setValue(a.value() / b.value());
	}
	
	/**
	 * Math.
	 *
	 * @param op the op
	 * @param x the x
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void math(final char op, final WeightedPoint x) throws IllegalArgumentException {
		switch(op) {
		case '+' : add(x); break;
		case '-' : subtract(x); break;
		case '*' : multiplyBy(x); break;
		case '/' : divideBy(x); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);
		}
	}

	/**
	 * Math.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the weighted point
	 */
	public static WeightedPoint math(final WeightedPoint a, final char op, final WeightedPoint b) {
		WeightedPoint result = new WeightedPoint(a);
		result.math(op, b);
		return result;
	}

	/**
	 * Math.
	 *
	 * @param op the op
	 * @param x the x
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void math(final char op, final double x) throws IllegalArgumentException {
		switch(op) {
		case '+' : add(x); break;
		case '-' : subtract(x); break;
		case '*' : scale(x); break;
		case '/' : scale(1.0/x); break;
		default: throw new IllegalArgumentException("Illegal Operation: " + op);
		}
	}

	/**
	 * Math.
	 *
	 * @param a the a
	 * @param op the op
	 * @param b the b
	 * @return the weighted point
	 */
	public static WeightedPoint math(final WeightedPoint a, final char op, final double b) {
		WeightedPoint result = new WeightedPoint(a);
		result.math(op, b);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(" +- ", ""); 
	}
	
	/**
	 * To string.
	 *
	 * @param before the before
	 * @param after the after
	 * @return the string
	 */
	public String toString(String before, String after) {
		return value() + before + Math.sqrt(1.0 / weight) + after; 
	}

	/**
	 * To string.
	 *
	 * @param df the df
	 * @return the string
	 */
	public String toString(final NumberFormat df) {
		return toString(df, " +- ", "");
	}
	
	/**
	 * To string.
	 *
	 * @param nf the nf
	 * @param before the before
	 * @param after the after
	 * @return the string
	 */
	public String toString(final NumberFormat nf, String before, String after) {
		return nf.format(value()) + before + nf.format(Math.sqrt(1.0 / weight)) + after; 
	}

	/**
	 * Creates the array.
	 *
	 * @param size the size
	 * @return the weighted point[]
	 */
	public static WeightedPoint[] createArray(int size) {
		WeightedPoint[] p = new WeightedPoint[size];
		for(int i=size; --i >= 0; ) p[i] = new WeightedPoint();
		return p;
	}
	
	/**
	 * Float values.
	 *
	 * @param data the data
	 * @return the float[]
	 */
	public static float[] floatValues(final WeightedPoint[] data) {
		final float[] fdata = new float[data.length];
		for(int i=data.length; --i >= 0; ) fdata[i] = (float) data[i].value();
		return fdata;
	}
	
	/**
	 * Values.
	 *
	 * @param data the data
	 * @return the double[]
	 */
	public static double[] values(final WeightedPoint[] data) {
		final double[] ddata = new double[data.length];
		for(int i=data.length; --i >= 0; ) ddata[i] = data[i].value();
		return ddata;
	}
	
	/**
	 * Float weights.
	 *
	 * @param data the data
	 * @return the float[]
	 */
	public static float[] floatWeights(final WeightedPoint[] data) {
		final float[] fdata = new float[data.length];
		for(int i=data.length; --i >= 0; ) fdata[i] = (float) data[i].weight;
		return fdata;
	}
	
	/**
	 * Weights.
	 *
	 * @param data the data
	 * @return the double[]
	 */
	public static double[] weights(final WeightedPoint[] data) {
		final double[] ddata = new double[data.length];
		for(int i=data.length; --i >= 0; ) ddata[i] = data[i].weight;
		return ddata;
	}
	
	/** The Constant NaN. */
	public final static WeightedPoint NaN = new WeightedPoint(0.0, 0.0);

	/* (non-Javadoc)
	 * @see jnum.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		return super.isNull() && isExact();
	}

	/* (non-Javadoc)
	 * @see jnum.math.LinearAlgebra#zero()
	 */
	@Override
	public void zero() {
		super.zero();
		exact();
	}

   
	
}

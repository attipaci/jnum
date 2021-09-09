/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/


package jnum.data;

import java.text.NumberFormat;
import java.util.stream.IntStream;

import jnum.CopyCat;
import jnum.ExtraMath;
import jnum.math.Division;
import jnum.math.HyperbolicFunctions;
import jnum.math.HyperbolicInverseFunctions;
import jnum.math.LinearAlgebra;
import jnum.math.Multiplicative;
import jnum.math.PowFunctions;
import jnum.math.Ratio;
import jnum.math.TrigonometricFunctions;
import jnum.math.TrigonometricInverseFunctions;
import jnum.util.HashCode;

/**
 * A class representing a weighted value. The class allows accumulation and mathematical operations 
 * (including most common math functions) with proper error propagation. I.e. the weight of the datum 
 * will be propagated as appropriate as the datum is being operated on.
 * 
 * The weight in this class can be in arbitrary units, such as number of occurences or measurements
 * or a signal strength in a different unit than the measurement itself. If you want to use proper noise
 * weights (w = 1/rms<sup>2</sup>) you should consider using {@link DataPoint} instead, but you don't
 * have to...
 * 
 * @author Attila Kovacs
 *
 */
public class WeightedPoint extends RealValue implements CopyCat<WeightedPoint>, Multiplicative<WeightedPoint>, Division<WeightedPoint>, 
    Ratio<WeightedPoint, WeightedPoint>, LinearAlgebra<WeightedPoint>, Accumulating<WeightedPoint>, 
    PowFunctions, TrigonometricFunctions, TrigonometricInverseFunctions, HyperbolicFunctions, HyperbolicInverseFunctions {

    private static final long serialVersionUID = -6583109762992313591L;

    private double weight;

    /**
     * Construct a new empty weighted value, with zero initial value and zero weight.
     * 
     */
    public WeightedPoint() {}

    /**
     * Construct a new weighted value that mimics another weigted value.
     * 
     * @param template      the weighted value that will be mimicked.
     */
    public WeightedPoint(final WeightedPoint template) {
        copy(template);
    }

    /**
     * Constructs a new weighted value with the specified measurement and associated weight.
     * 
     * @param value     the measurement value
     * @param weight    the associated weight (e.g. number of occurrences).
     */
    public WeightedPoint(final double value, final double weight) { 
        super(value);
        this.weight = weight;
    }


    @Override
    public WeightedPoint clone() {
        return (WeightedPoint) super.clone();
    }

    @Override
    public WeightedPoint copy() {
        return (WeightedPoint) super.copy();
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof WeightedPoint)) return false;
        if(!super.equals(o)) return false;

        WeightedPoint p = (WeightedPoint) o;
        if(isExact()) if(!p.isExact()) return false;
        return weight == p.weight;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(weight);
    }

    /**
     * Gets the weight associated with this value
     * 
     * @return  the associated weight (arbitrary units).
     */
    public final double weight() { return weight; }	

    /**
     * Sets a new weight to be associated with this value.
     * 
     * @param w the new weight (arbitrary units) to associate with the value.
     */
    public final void setWeight(final double w) { this.weight = w; }

    
    /**
     * Increments the weight of this value, e.g. to account for aggregating 
     * new measurements into this value.
     * 
     * @param dw    the weight increment.
     */
    public final void addWeight(double dw) { weight += dw; }

    /**
     * Scales the value by the specified factor while leaving the weight as is.
     * 
     * @param factor    the scaling factor for the value only.
     */
    public final void scaleValue(double factor) { super.scale(factor); }

    /**
     * Scales the weight by the specified factor while leaving the value as is.
     * 
     * @param factor    the scaling factor for the weight only.
     */
    public final void scaleWeight(double factor) { weight *= factor; }


    @Override
    public final void noData() { 
        super.noData();
        weight = 0.0;
    }

    /**
     * Checks if the represented value is NaN or if it has zero associated weight.
     * 
     * @return  <code>true</code> if the value is NaN. Otherwise <code>false</code>.
     */
    public final boolean isNaN() { return Double.isNaN(value()) || weight == 0.0; }

    /**
     * Makes this an exact value, that is a value with infinite weight.
     * 
     */
    public final void exact() { weight = Double.POSITIVE_INFINITY; }

    /**
     * Checks if the value is an exact value, i.e. one that has infinite weight.
     * 
     * @return  <code>true</code> if the value is exact. Otherwise <code>false</code>.
     */
    public final boolean isExact() { return Double.isInfinite(weight); }


    @Override
    public void copy(final WeightedPoint x) {
        setValue(x.value());
        weight = x.weight;
    }


    @Override
    public final void add(final WeightedPoint x) {
        setSum(this, x);
    }


    @Override
    public final void subtract(final WeightedPoint x) {
        setDifference(this, x);
    }

    @Override
    public void setSum(final WeightedPoint a, final WeightedPoint b) {
        final double w = a.weight * b.weight;
        weight = w > 0.0 ? w / (a.weight + b.weight) : 0.0;
        setValue(a.value() + b.value());
    }

    @Override
    public void setDifference(final WeightedPoint a, final WeightedPoint b) {
        final double w = a.weight * b.weight;
        weight = w > 0.0 ? w / (a.weight + b.weight) : 0.0;
        setValue(a.value() - b.value());
    }

    @Override
    public void addScaled(final WeightedPoint x, final double factor) {
        add(factor * x.value());
        if(weight == 0.0) return;
        if(x.weight == 0.0) weight = 0.0;
        else weight = weight * x.weight / (x.weight + factor * factor * weight);
    }

    /**
     * Averages this weighted value with another weighted value that has the same type (units) of weight.
     * 
     * @param x     the weighted other value to average this one with.
     */
    public void average(final WeightedPoint x) {
        average(x.value(), x.weight);
    }

    /**
     * Averages this weighted value with another weighted value that has the same type (units) of weight.
     * 
     * @param v     the other value
     * @param w     the associated weight of the other value.
     */
    public void average(final double v, final double w) {
        setValue(weight * value() + w * v);
        weight += w;
        if(weight > 0.0) setValue(value() / weight);		
    }


    @Override
    public final void accumulate(WeightedPoint x) {
        accumulate(x, 1.0);
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


    @Override
    public final void scale(final double x) {
        scaleValue(x);
        weight /= x*x;
    }

    @Override
    public final void multiplyBy(final WeightedPoint p) {
        setProduct(this, p);
    }

    @Override
    public final void setProduct(final WeightedPoint a, final WeightedPoint b) {
        final double w = a.weight * b.weight;
        weight = w > 0.0 ? w / (a.value() * a.value() * a.weight + b.value() * b.value() * b.weight) : 0.0;
        setValue(a.value() * b.value());
    }

    @Override
    public final void divideBy(final WeightedPoint p) {
        setRatio(this, p);
    }

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
     * Performs a simple binary operation on this weighted value and the specified other value, and
     * stores the result in this object.
     * 
     * @param op        the operator, e.g. '+', '-', '*', or '/'.
     * @param x         the other operand.
     * @throws IllegalArgumentException     if the op argument is not one listed above.
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
     * Gets a new weighted value with the result of a simple binary operation on 
     * two weighted values.
     * 
     * @param a         the first operand.
     * @param op        the operator, e.g. '+', '-', '*', or '/'.
     * @param b         the other operand.
     * @return          a new weighted data point with the result of the mathematical operation.
     * @throws IllegalArgumentException     if the op argument is not one listed above.
     */
    public static WeightedPoint math(final WeightedPoint a, final char op, final WeightedPoint b) {
        WeightedPoint result = new WeightedPoint(a);
        result.math(op, b);
        return result;
    }


    /**
     * Performs a simple binary operation on this weighted value and the specified number value, and
     * stores the result in this object.
     * 
     * @param op        the operator, e.g. '+', '-', '*', or '/'.
     * @param x         the number value.
     * @throws IllegalArgumentException     if the op argument is not one listed above.
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
     * Gets a new weighted value with the result of a simple binary operation on 
     * a weighted value and a number value.
     * 
     * @param a         the weighted value.
     * @param op        the operator, e.g. '+', '-', '*', or '/'.
     * @param b         the number value.
     * @return          a new weighted data point with the result of the mathematical operation.
     * @throws IllegalArgumentException     if the op argument is not one listed above.
     * 
     */
    public static WeightedPoint math(final WeightedPoint a, final char op, final double b) {
        WeightedPoint result = new WeightedPoint(a);
        result.math(op, b);
        return result;
    }
    
    @Override
    public boolean isNull() {
        return super.isNull() && isExact();
    }

    @Override
    public void zero() {
        super.zero();
        exact();
    }


    @Override
    public double abs() {
        return Math.abs(value());
    }
    
    @Override
    public double absSquared() {
        return value() * value();
    }


    @Override
    public void pow(double n) {
        double y = Math.pow(value(), n);
        double z = value()/(n*y);
        scaleWeight(z * z);
        setValue(y);        
    }


    @Override
    public void inverse() {
        double x2 = value() * value();
        setValue(1.0 / value());
        scaleWeight(x2 * x2);
    }


    @Override
    public void square() {
        scaleValue(value());
        scaleWeight(0.25 * value());
    }


    @Override
    public void sqrt() {
        scaleWeight(4.0 / value());
        setValue(Math.sqrt(value()));
    }


    @Override
    public void exp() {
        setValue(Math.exp(value()));
        scaleWeight(1.0 / (value() * value()));
    }


    @Override
    public void expm1() {
        scaleWeight(Math.exp(-2.0 * value()));
        setValue(Math.expm1(value()));
    }


    @Override
    public void log() {
        scaleWeight(value() * value());
        setValue(Math.log(value()));
    }


    @Override
    public void log1p() {
        scaleWeight((1.0 + value()) * (1.0 + value()));
        setValue(Math.log1p(value()));
    }


    @Override
    public void sin() {
        setValue(Math.sin(value()));
        scaleWeight(1.0 / (1.0 - value() * value()));
    }


    @Override
    public void cos() {
        setValue(Math.cos(value()));
        scaleWeight(1.0 / (1.0 - value() * value()));
    }


    @Override
    public void tan() {
        setValue(Math.tan(value()));
        scaleWeight(1.0 / (1.0 + value() * value()));
    }

    @Override
    public void asin() {
        scaleWeight(1.0 - value() * value());
        setValue(Math.asin(value()));
    }


    @Override
    public void acos() {
        scaleWeight(1.0 - value() * value());
        setValue(Math.acos(value()));
        
    }

    @Override
    public void atan() {
        scaleWeight(1.0 + value() * value());
        setValue(Math.atan(value()));
    }
    
    
    @Override
    public void sinh() {
        setValue(Math.sinh(value()));
        scaleWeight(1.0 / (1.0 + value() * value()));
    }


    @Override
    public void cosh() {
        setValue(Math.cosh(value()));
        scaleWeight(1.0 / (value() * value() - 1.0));
    }


    @Override
    public void tanh() {
        setValue(Math.tanh(value()));
        scaleWeight(1.0 / (1.0 - value() * value()));
    }


    @Override
    public void asinh() {
        scaleWeight(1.0 + value() * value());
        setValue(ExtraMath.asinh(value()));
        
    }


    @Override
    public void acosh() {
        scaleWeight(value() * value() - 1.0);
        setValue(ExtraMath.acosh(value()));
    }


    @Override
    public void atanh() {
        scaleWeight(1.0 - value() * value());
        setValue(ExtraMath.atanh(value()));
    }
    

    @Override
    public String toString() {
        return toString(" +- ", ""); 
    }


    public String toString(String before, String after) {
        return value() + before + Math.sqrt(1.0 / weight) + after; 
    }


    public String toString(final NumberFormat df) {
        return toString(df, " +- ", "");
    }


    public String toString(final NumberFormat nf, String before, String after) {
        return nf.format(value()) + before + nf.format(Math.sqrt(1.0 / weight)) + after; 
    }

    /**
     * Creates an initialized array of weighted values of the specified size. All elements of the
     * array are set to empty data (weight 0) initially.
     * 
     * @param size      Number of elements in the new array of weighted values.
     * @return          an initialized new array of weighted values- of the specified size.
     */
    public static WeightedPoint[] createArray(int size) {
        final WeightedPoint[] p = new WeightedPoint[size];
        IntStream.range(0, size).parallel().forEach(i -> p[i] = new WeightedPoint());
        return p;
    }


    /**
     * Extracts the values from an array of weighted values, and returns them as floats.
     * 
     * @param data      An array of weighted values
     * @return          the extracted values only.
     */
    public static float[] floatValues(final WeightedPoint[] data) {
        final float[] fdata = new float[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> fdata[i] = (float) data[i].value());
        return fdata;
    }

    /**
     * Extracts the values from an array of weighted values, and returns them as doubles.
     * 
     * @param data      An array of weighted values
     * @return          the extracted values only.
     */
    public static double[] values(final WeightedPoint[] data) {
        final double[] ddata = new double[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> ddata[i] = data[i].value());
        return ddata;
    }

    /**
     * Extracts the weightes from an array of weighted values, and returns them as floats.
     * 
     * @param data      An array of weighted values
     * @return          the extracted weights only.
     */
    public static float[] floatWeights(final WeightedPoint[] data) {
        final float[] fdata = new float[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> fdata[i] = (float) data[i].weight());
        for(int i=data.length; --i >= 0; ) fdata[i] = (float) data[i].weight;
        return fdata;
    }

    /**
     * Extracts the weightes from an array of weighted values, and returns them as doubles.
     * 
     * @param data      An array of weighted values
     * @return          the extracted weights only.
     */
    public static double[] weights(final WeightedPoint[] data) {
        final double[] ddata = new double[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> ddata[i] = data[i].weight());
        return ddata;
    }

    /**
     * 
     * 
     */
    public static final WeightedPoint NaN = new WeightedPoint(0.0, 0.0);   
}

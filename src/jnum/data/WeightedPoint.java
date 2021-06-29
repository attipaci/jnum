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


package jnum.data;

import java.text.NumberFormat;
import java.util.stream.IntStream;

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


public class WeightedPoint extends RealValue implements Multiplicative<WeightedPoint>, Division<WeightedPoint>, 
    Ratio<WeightedPoint, WeightedPoint>, LinearAlgebra<WeightedPoint>, Accumulating<WeightedPoint>, 
    PowFunctions, TrigonometricFunctions, TrigonometricInverseFunctions, HyperbolicFunctions, HyperbolicInverseFunctions {

    private static final long serialVersionUID = -6583109762992313591L;

    private double weight;


    public WeightedPoint() {}


    public WeightedPoint(final WeightedPoint template) {
        copy(template);
    }


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
        return weight == p.weight;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(weight);
    }


    public final double weight() { return weight; }	


    public final void setWeight(final double w) { this.weight = w; }


    public final void addWeight(double dw) { weight += dw; }


    public final void scaleValue(double factor) { super.scale(factor); }


    public final void scaleWeight(double factor) { weight *= factor; }


    @Override
    public final void noData() { 
        super.noData();
        weight = 0.0;
    }


    public final boolean isNaN() { return isNaN(this); }


    public final static boolean isNaN(WeightedPoint point) { 
        return Double.isNaN(point.value()) || point.weight == 0.0;
    }


    public final void exact() { weight = Double.POSITIVE_INFINITY; }


    public final boolean isExact() { return Double.isInfinite(weight); }


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


    public void average(final WeightedPoint x) {
        average(x.value(), x.weight);
    }


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


    public void math(final char op, final WeightedPoint x) throws IllegalArgumentException {
        switch(op) {
        case '+' : add(x); break;
        case '-' : subtract(x); break;
        case '*' : multiplyBy(x); break;
        case '/' : divideBy(x); break;
        default: throw new IllegalArgumentException("Illegal Operation: " + op);
        }
    }


    public static WeightedPoint math(final WeightedPoint a, final char op, final WeightedPoint b) {
        WeightedPoint result = new WeightedPoint(a);
        result.math(op, b);
        return result;
    }


    public void math(final char op, final double x) throws IllegalArgumentException {
        switch(op) {
        case '+' : add(x); break;
        case '-' : subtract(x); break;
        case '*' : scale(x); break;
        case '/' : scale(1.0/x); break;
        default: throw new IllegalArgumentException("Illegal Operation: " + op);
        }
    }


    public static WeightedPoint math(final WeightedPoint a, final char op, final double b) {
        WeightedPoint result = new WeightedPoint(a);
        result.math(op, b);
        return result;
    }
    
    
    
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
    
    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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


    public static WeightedPoint[] createArray(int size) {
        final WeightedPoint[] p = new WeightedPoint[size];
        IntStream.range(0, size).parallel().forEach(i -> p[i] = new WeightedPoint());
        return p;
    }


    public static float[] floatValues(final WeightedPoint[] data) {
        final float[] fdata = new float[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> fdata[i] = (float) data[i].value());
        return fdata;
    }


    public static double[] values(final WeightedPoint[] data) {
        final double[] ddata = new double[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> ddata[i] = data[i].value());
        return ddata;
    }


    public static float[] floatWeights(final WeightedPoint[] data) {
        final float[] fdata = new float[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> fdata[i] = (float) data[i].weight());
        for(int i=data.length; --i >= 0; ) fdata[i] = (float) data[i].weight;
        return fdata;
    }


    public static double[] weights(final WeightedPoint[] data) {
        final double[] ddata = new double[data.length];
        IntStream.range(0, data.length).parallel().forEach(i -> ddata[i] = data[i].weight());
        return ddata;
    }




    public final static WeightedPoint NaN = new WeightedPoint(0.0, 0.0);




   
}

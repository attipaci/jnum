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

package jnum.data;
import java.util.stream.IntStream;

import jnum.math.Complex;

/**
 * A complex number with an associated weight, such as a 1/&sigma;<sup>2</sup> noise weight.
 * 
 * @author Attila Kovacs
 *
 */
public class WeightedComplex extends Complex implements Weighting, Accumulating<WeightedComplex> {

    /**
     * 
     */
    private static final long serialVersionUID = 7854217607396193391L;

    private double weight = 0.0;
    
    @Override
    public WeightedComplex copy() {
        return (WeightedComplex) super.copy();
    }
    
    @Override
    public void setWeight(double w) {
        this.weight = w;
    }
    

    @Override
    public void addWeight(double w) {
        this.weight += w;
    }
    
    @Override
    public void exact() {
        setWeight(Double.POSITIVE_INFINITY);
    }
    
    @Override
    public final boolean isExact() {
        return Double.isInfinite(weight);
    }
    
    @Override
    public final double weight() {
        return weight;
    }

    /**
     * Sets this weighted complex value to match the argument.
     * 
     * @param z     another weighted complex value.
     * 
     */
    public void copy(WeightedComplex z) {
        super.copy(z);
        weight = z.weight;
    }
    
    /**
     * Averages this weighted complex value with the specified complex value and corresponding weight.
     * 
     * @param z     a complex value
     * @param w     the weight to use for the specified complex value
     * 
     * @see #average(WeightedComplex)
     */
    public void average(Complex z, double w) {
        set(weight * re() + w * z.re(), weight * im() + w * z.im());
        addWeight(w);
        if(weight > 0.0) scale(1.0 / weight);  
    }
    
    /**
     * Averages this weighted complex value with the argument
     * 
     * @param z     another weighted complex value.
     * 
     * @see #average(Complex, double)
     */
    public final void average(WeightedComplex z) {
        average(z, z.weight);
    }
    
    @Override
    public void noData() {
        zero();
        setWeight(0.0);
    }

    @Override
    public void accumulate(WeightedComplex x) {
        add(x.weight * x.re(), x.weight * x.im());
        addWeight(x.weight);
    }

    @Override
    public void accumulate(WeightedComplex x, double w) {
        w *= x.weight;
        add(w * x.re(), w *x.im());
        addWeight(w);
    }

    @Override
    public void accumulate(WeightedComplex x, double w, double G) {
        w *= x.weight * G;
        add(w * x.re(), w *x.im());
        addWeight(w * G);        
    }

    @Override
    public void startAccumulation() {
        noData();
    }

    @Override
    public void endAccumulation() {
        scale(1.0 / weight);
    }
    
    @Override
    public void inverse() {
        super.inverse();
        weight = 1.0 / weight;
    }

    @Override
    public void scale(double x) {
        super.scale(x);
        weight /= x * x;
    }
    
    @Override
    public void scaleValue(double x) {
        super.scale(x);
    }
    
    @Override
    public void scaleWeight(double x) {
        weight *= x;
    }
    
    /**
     * Returns a new array of weighted complex values of the specified element count, and with all
     * elements initialized to zero values and weights. 
     * 
     * @param size  the number of elements in the new array
     * @return      a new array of the specified size, initialized to zero values and weights.
     */
    public static WeightedComplex[] createArray(int size) {
        final WeightedComplex[] p = new WeightedComplex[size];
        IntStream.range(0, size).parallel().forEach(i -> p[i] = new WeightedComplex());
        return p;
    }
  
    
    // TODO error propagation on math functions...
}

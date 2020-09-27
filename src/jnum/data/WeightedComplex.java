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
import java.util.stream.IntStream;

import jnum.math.Complex;

public class WeightedComplex extends Complex implements Accumulating<WeightedComplex> {

    /**
     * 
     */
    private static final long serialVersionUID = 7854217607396193391L;

    private double weight = 0.0;
    
    @Override
    public WeightedComplex copy() {
        return (WeightedComplex) super.copy();
    }
    
    public void setWeight(double w) {
        this.weight = w;
    }
    
    public void addWeight(double w) {
        this.weight += w;
    }
    
    public void exact() {
        setWeight(Double.POSITIVE_INFINITY);
    }
    
    public final boolean isExact() {
        return Double.isInfinite(weight);
    }
    
    public final double weight() {
        return weight;
    }
    
    public void copy(WeightedComplex z) {
        super.copy(z);
        weight = z.weight;
    }
    
    
    public void average(Complex z, double w) {
        set(weight * re() + w * z.re(), weight * im() + w * z.im());
        addWeight(w);
        if(weight > 0.0) scale(1.0 / weight);  
    }
    
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
        addReal(x.weight * x.re());
        addImaginary(x.weight *x.im());
        addWeight(x.weight);
    }

    @Override
    public void accumulate(WeightedComplex x, double w) {
        w *= x.weight;
        addReal(w * x.re());
        addImaginary(w *x.im());
        addWeight(w);
    }

    @Override
    public void accumulate(WeightedComplex x, double w, double G) {
        w *= x.weight * G;
        addReal(w * x.re());
        addImaginary(w *x.im());
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
    
    public void scaleValue(double x) {
        super.scale(x);
    }
    
    public void scaleWeight(double x) {
        weight *= x;
    }
    
    public static WeightedComplex[] createArray(int size) {
        final WeightedComplex[] p = new WeightedComplex[size];
        IntStream.range(0, size).parallel().forEach(i -> p[i] = new WeightedComplex());
        return p;
    }
  
    
    // TODO error propagation on math functions...
}

/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math.matrix;


import java.util.Arrays;

import jnum.ShapeException;
import jnum.ViewableAsDoubles;
import jnum.data.IndexedValues;
import jnum.data.samples.Index1D;
import jnum.math.Coordinates;
import jnum.math.MathVector;




public class RealVector extends AbstractVector<Double> implements MathVector<Double>, IndexedValues<Index1D, Double>, ViewableAsDoubles {

    private static final long serialVersionUID = 1042626482476049050L;

    private double[] component;


    public RealVector() {}


    public RealVector(int size) {
        component = new double[size];
    }


    public RealVector(double[] data) { setData(data); }

    
    @Override
    public RealVector clone() {
        return (RealVector) super.clone();
    }
    
    
    @Override
    public RealVector copy(boolean withContent) {
        RealVector copy = clone();
        copy.component = new double[size()];
        if(withContent) System.arraycopy(component, 0, copy.component, 0, size());
        return copy;
    }
    
    @Override
    public final Double x() { return component[0]; }

    @Override
    public final Double y() { return component.length > 0 ? component[1] : 0.0; }

    @Override
    public final Double z() { return component.length > 1 ? component[2] : 0.0; }


    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#getType()
     */
    @Override
    public Class<Double> getType() { return double.class; }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#getData()
     */
    @Override
    public double[] getData() {
        return component;
    }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#setData(java.lang.Object)
     */
    @Override
    public void setData(Object data) { 
        double[] array = (double[]) data;
        component = new double[array.length];
        for(int i=array.length; --i >= 0; ) component[i] = array[i];		
    }

    
    public void setData(double[] data) { component = data; }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#size()
     */
    @Override
    public final int size() { return component.length; }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#getComponent(int)
     */
    @Override
    public final Double getComponent(int i) { return component[i]; }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#setComponent(int, java.lang.Object)
     */
    @Override
    public final void setComponent(int i, Double x) { component[i] = x; }
    
    public final void setComponent(int i, double x) { component[i] = x; }

    public final void incrementValue(int i, double x) { component[i] += x; }
    
    @Override
    public final void incrementValue(int i, Double x) { component[i] += x; }

    
    @Override
    public void copy(Coordinates<? extends Double> coords) {
        assertSize(coords.size());
        for(int i = size(); --i >= 0; ) component[i] = coords.getComponent(i);
    }
    
    @Override
    public void multiplyByComponentsOf(Coordinates<? extends Double> v) {
        for(int i=size(); --i >= 0; ) component[i] *= v.getComponent(i);
    }
    
    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#dot(kovacs.math.AbstractVector)
     */
    @Override
    public Double dot(Coordinates<? extends Double> v) {
        double sum = 0.0;
        for(int i=size(); --i >= 0; ) sum += component[i] * v.getComponent(i);
        return sum;
    }
    
    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#asRowVector()
     */
    @Override
    public Matrix asRowVector() { 
        double[][] array = new double[1][];
        array[0] = component;
        return new Matrix(array);
    }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#asColumnVector()
     */
    @Override
    public Matrix asColumnVector() {
        Matrix M = new Matrix(size(), 1);
        M.setColumn(0, component);
        return M;
    }

    /* (non-Javadoc)
     * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
     */
    @Override
    public void addScaled(MathVector<? extends Double> o, double factor) {
        for(int i=size(); --i >= 0; ) component[i] += o.getComponent(i) * factor;		
    }

    /* (non-Javadoc)
     * @see kovacs.math.LinearAlgebra#isNull()
     */
    @Override
    public boolean isNull() {
        for(int i=size(); --i >= 0; ) if(component[i] != 0.0) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see kovacs.math.LinearAlgebra#zero()
     */
    @Override
    public void zero() {
        for(int i=size(); --i >= 0; ) component[i] = 0.0;
    }

    /* (non-Javadoc)
     * @see kovacs.math.Additive#subtract(java.lang.Object)
     */
    @Override
    public void subtract(MathVector<? extends Double> o) {
        for(int i=size(); --i >= 0; ) component[i] -= o.getComponent(i);	
    }

    /* (non-Javadoc)
     * @see kovacs.math.Additive#add(java.lang.Object)
     */
    @Override
    public void add(MathVector<? extends Double> o) {
        for(int i=component.length; --i >= 0; ) component[i] += o.getComponent(i);	
    }

    /* (non-Javadoc)
     * @see kovacs.math.Scalable#scale(double)
     */
    @Override
    public void scale(double factor) {
        for(int i=component.length; --i >= 0; ) component[i] *= factor;		
    }

    /* (non-Javadoc)
     * @see kovacs.math.AbsoluteValue#norm()
     */
    @Override
    public double absSquared() {
        return dot(this);
    }

    /* (non-Javadoc)
     * @see kovacs.math.Metric#distanceTo(java.lang.Object)
     */
    @Override
    public double distanceTo(MathVector<? extends Double> v) {
        double d2 = 0.0;
        for(int i=size(); --i >= 0; ) {
            double d = component[i] - v.getComponent(i);
            d2 += d*d;
        }
        return Math.sqrt(d2);
    }

    /* (non-Javadoc)
     * @see kovacs.math.AbstractVector#orthogonalizeTo(kovacs.math.AbstractVector)
     */
    @Override
    public void orthogonalizeTo(MathVector<? extends Double> v) {
        addScaled(v, -dot(v) / (abs() * v.abs()));
    }

 
    @Override
    public final void projectOn(final MathVector<? extends Double> v) {
        double scaling = dot(v) / v.abs();
        copy(v);
        scale(scaling);
    }


    /* (non-Javadoc)
     * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setSum(MathVector<? extends Double> a, MathVector<? extends Double> b) {
        if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");
        for(int i=size(); --i >= 0; ) component[i] = a.getComponent(i) - b.getComponent(i);
    }

    /* (non-Javadoc)
     * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setDifference(MathVector<? extends Double> a, MathVector<? extends Double> b) {
        if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");

        for(int i=size(); --i >= 0; ) component[i] = a.getComponent(i) - b.getComponent(i);
    }

    @Override
    public void fill(Double value) {
        Arrays.fill(component, value);
    }

    @Override
    public void setValues(Double... values) {
        if(component != null) if(component.length != values.length) component = null;
        if(component == null) component = new double[values.length];
        for(int i=values.length; --i >= 0; ) component[i] = values[i];
    }


    @Override
    public Object viewAsDoubles() {
        return component;
    }


    @Override
    public void viewAsDoubles(Object view) throws IllegalArgumentException {
        if(!(view instanceof double[])) throw new IllegalArgumentException("Argument is class " + view.getClass().getSimpleName() + " instead of double[].");
        double[] d = (double[]) view;
        if(d.length != size()) throw new ShapeException("Size mismatch. " + d.length + " instead of expected " + size() + ".");
        
        System.arraycopy(component, 0, d, 0, size());
    }


    @Override
    public void createFromDoubles(Object array) throws IllegalArgumentException {
        if(!(array instanceof double[])) throw new IllegalArgumentException("Argument is class " + array.getClass().getSimpleName() + " instead of double[].");
     
        double[] d = (double[]) array;
        if(d.length != size()) throw new ShapeException("Size mismatch. " + d.length + " instead of expected " + size() + ".");
        
        System.arraycopy(d, 0, component, 0, size());
    }


    @Override
    public final Class<? extends Number> getElementType() {
        return double.class;
    }


    @Override
    public final int compare(Number a, Number b) {
        return Double.compare(a.doubleValue(), b.doubleValue());
    }


    @Override
    public void clear(Index1D index) {
        setComponent(index.i(), 0.0);
    }


    @Override
    public void scale(Index1D index, double factor) {
       component[index.i()] *=  factor;
    }


    @Override
    public void set(Index1D index, Number value) {
        setComponent(index.i(), value.doubleValue());
    }


    @Override
    public void add(Index1D index, Number value) {
        component[index.i()] += value.doubleValue();
    }

   

}

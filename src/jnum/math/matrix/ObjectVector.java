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


package jnum.math.matrix;

import java.lang.reflect.*;
import java.text.NumberFormat;
import java.util.Arrays;

import jnum.Copiable;
import jnum.Util;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.Coordinates;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.text.DecimalFormating;
import jnum.util.ArrayUtil;
import jnum.util.HashCode;
import jnum.math.MathVector;


@SuppressWarnings("unchecked")
public class ObjectVector<T extends Copiable<? super T> & LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue> 
extends AbstractVector<T> {
    /** */
    private static final long serialVersionUID = 4341703980593410457L;

    private T[] component;

    protected Class<T> type;

    /**
     * Instantiates a new generic vector.
     *
     * @param type the type
     */
    public ObjectVector(Class<T> type) {
        this.type = type;
    }

    /**
     * Instantiates a new generic vector.
     *
     * @param type the type
     * @param size the size
     */
    public ObjectVector(Class<T> type, int size) {
        this(type);
        component = (T[]) ArrayUtil.createArray(type, size);
    }

    /**
     * Instantiates a new generic vector.
     *
     * @param data the data
     */
    public ObjectVector(T[] data) { setData(data); }

    @Override
    public int hashCode() {
        return HashCode.from(component);
    }


    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null) return false;
        if(!(o instanceof ObjectVector)) return false;

        ObjectVector<?> coords = (ObjectVector<?>) o;
        for(int i=component.length; --i >= 0; ) if(!Util.equals(coords.getComponent(i), component[i])) return false;

        return true;
    }

    @Override
    public boolean equals(Coordinates<T> coords, double precision) {
        return equals(coords);
    }



    @Override
    public ObjectVector<T> clone() {
        return (ObjectVector<T>) super.clone();
    }


    @Override
    public ObjectVector<T> copy(boolean withContent) {
        ObjectVector<T> copy = clone();
        if(withContent) {
            try { 
                copy.component = (T[]) ArrayUtil.copyOf(component); 
                return copy;
            }
            catch(Exception e) { Util.error(this, e); }
        }
        else {
            try { 
                copy.setData(ArrayUtil.createArray(getComponentType(), size())); 
                return copy;
            }
            catch(Exception e) { Util.error(this, e); }
        }
        return null;
    }

    @Override
    public void copy(Coordinates<? extends T> v) {
        assertSize(v.size());
        for(int i=size(); --i >= 0; ) component[i] = (T) v.getComponent(i).copy();
    }

    public T newComponent() {
        try { return getComponentType().getConstructor().newInstance(); }
        catch(Exception e) { 
            Util.error(this, e);
            return null;
        }   
    }

    @Override
    public ObjectVector<T> getVectorInstance(int size) {
        return new ObjectVector<>(getComponentType(), size);
    }

    @Override
    public String toString(int i, NumberFormat nf) {
        if(nf == null || !DecimalFormating.class.isAssignableFrom(getComponentType())) return getComponent(i).toString();
        return ((DecimalFormating) getComponent(i)).toString(nf);
    }


    @Override
    public Class<T> getComponentType() { return (Class<T>) component[0].getClass(); }




    @Override
    public final T x() { return component[0]; }

    @Override
    public final T y() { return component[1]; }

    @Override
    public final T z() { return component[2]; }

    @Override
    public final T copyOf(int i) {
        return (T) component[i].copy();
    }


    @Override
    public T[] getData() { return component; }


    @Override
    public void setData(Object data) { 
        assertSize(((Object[]) data).length);
        component = (T[]) data; 
        type = (Class<T>) component[0].getClass();
    }


    @Override
    public final int size() { return component.length; }


    @Override
    public final T getComponent(int i) { return component[i]; }


    @Override
    public final void setComponent(int i, T x) { component[i] = x; }

    @Override
    public final void incrementValue(int i, T x) { component[i].add(x); }

    @Override
    public void multiplyByComponentsOf(Coordinates<? extends T> v) { 
        for(int i=component.length; --i >= 0; ) component[i].multiplyBy(v.getComponent(i));
    }


    @Override
    public T dot(MathVector<? extends T> v) {
        T p = newComponent();
        T sum = newComponent();

        sum.zero();

        for(int i=Math.min(v.size(), size()); --i >= 0; ) if(!component[i].isNull()) {
            T vi = v.getComponent(i);
            if(vi.isNull()) continue;
            p.setProduct(component[i], vi);
            sum.add(p);
        }
        return sum;
    }

    @Override
    public T dot(T[] v) {
        T p = newComponent();
        T sum = newComponent();

        for(int i=Math.min(v.length, size()); --i >= 0; ) if(!component[i].isNull()) {
            T vi = v[i];
            if(vi.isNull()) continue;
            p.setProduct(component[i], vi);
            sum.add(p);
        }

        return sum;
    }

    public final T dot(RealVector v) {
        return dot(v.getData());
    }

    @Override
    public T dot(double... v) {
        T sum = newComponent();        
        for(int i=Math.min(v.length, size()); --i >= 0; ) if(!component[i].isNull()) if(v[i] != 0.0) sum.addScaled(component[i], v[i]);
        return sum;
    }

    @Override
    public T dot(float... v) {  
        T sum = newComponent();        
        for(int i=Math.min(v.length, size()); --i >= 0; ) if(!component[i].isNull()) if(v[i] != 0.0) sum.addScaled(component[i], v[i]);
        return sum;
    }


    @Override
    public AbstractMatrix<T> asRowVector() {
        try { 
            T[][] array = (T[][]) Array.newInstance(component.getClass(), 1);
            array[0] = component;
            return new ObjectMatrix<>(array);
        }
        catch(Exception e) { return null; }

    }


    @Override
    public void addScaled(MathVector<? extends T> o, double factor) {
        for(int i=component.length; --i >= 0; ) {
            T vi = o.getComponent(i);
            if(vi == null) continue;
            component[i].addScaled(vi, factor);		
        }
    }


    @Override
    public boolean isNull() {
        for(int i=component.length; --i >= 0; ) if(!component[i].isNull()) return false;
        return true;
    }


    @Override
    public void zero() {
        for(int i=component.length; --i >= 0; ) {
            if(component[i] == null) component[i] = newComponent();
            component[i].zero();
        }
    }


    @Override
    public void subtract(MathVector<? extends T> o) {
        for(int i=component.length; --i >= 0; ) {
            T vi = o.getComponent(i);
            if(vi == null) continue;
            component[i].subtract(vi);	
        }
    }


    @Override
    public void add(MathVector<? extends T> o) {
        for(int i=component.length; --i >= 0; ) {
            T vi = o.getComponent(i);
            if(vi == null) continue;
            component[i].add(vi);	
        }
    }


    @Override
    public void scale(double factor) {
        for(int i=component.length; --i >= 0; ) component[i].scale(factor);		
    }


    @Override
    public double squareNorm() {
        double norm = 0.0;
        for(int i=component.length; --i >= 0; ) norm += getComponent(i).squareNorm();
        return norm;
    }


    @Override
    public double distanceTo(MathVector<? extends T> v) {
        double d2 = 0.0;
        for(int i=component.length; --i >= 0; ) {
            T vi = v.getComponent(i);
            if(vi == null) continue;
            double d = component[i].distanceTo(vi);
            d2 += d*d;
        }
        return Math.sqrt(d2);
    }


    @Override
    public void orthogonalizeTo(MathVector<? extends T> v) {
        addScaled(v, -dot(v).abs() / (abs() * v.abs()));
    }

    @Override
    public final void projectOn(final MathVector<? extends T> v) {
        double scaling = dot(v).abs() / v.abs();
        copy(v);
        scale(scaling);
    }


    @Override
    public void setSum(MathVector<? extends T> a, MathVector<? extends T> b) {
        if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");

        for(int i=component.length; --i >= 0; ) {
            if(component[i] == null) component[i] = newComponent();
            component[i].setSum(a.getComponent(i), b.getComponent(i));
        }

    }


    @Override
    public void setDifference(MathVector<? extends T> a, MathVector<? extends T> b) {
        if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");

        for(int i=component.length; --i >= 0; ) {
            if(component[i] == null) component[i] = newComponent();
            component[i].setDifference(a.getComponent(i), b.getComponent(i));
        }

    }

    @Override
    public void fill(T value) {
        Arrays.fill(component, value.copy());
    }

    @Override
    public void setValues(T... values) {
        component = values;
    }
}

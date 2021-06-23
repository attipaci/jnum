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

import java.lang.reflect.*;
import java.util.Arrays;

import jnum.Copiable;
import jnum.ShapeException;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.data.samples.Index1D;
import jnum.math.AbsoluteValue;
import jnum.math.AbstractAlgebra;
import jnum.math.Coordinates;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;
import jnum.math.MathVector;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericVector.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("unchecked")
public class ObjectVector<T extends Copiable<? super T> & LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue> extends AbstractVector<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4341703980593410457L;

	/** The component. */
	private T[] component;
	
	/** The type. */
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
	            copy.setData(ArrayUtil.createArray(getType(), size())); 
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
	
    public T getComponentInstance() {
        try { return getType().getConstructor().newInstance(); }
        catch(Exception e) { 
            Util.error(this, e);
            return null;
        }   
    }

    @Override
    public ObjectVector<T> getVectorInstance(int size) {
        return new ObjectVector<>(getType(), size);
    }
    
	
		
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getType()
	 */
	@Override
	public Class<T> getType() { return (Class<T>) component[0].getClass(); }
	
	
	@Override
    public final T x() { return component[0]; }
	
	@Override
    public final T y() { return component[1]; }
	
	@Override
    public final T z() { return component[2]; }
	
    @Override
    public final T copyOf(Index1D index) {
        return (T) get(index).copy();
    }

	
	/**
	 * New entry.
	 *
	 * @return the t
	 */
	public T newEntry() {
		try { return type.getConstructor().newInstance(); }
		catch(Exception e) {
			Util.error(this, e);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getData()
	 */
	@Override
	public T[] getData() { return component; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) { 
	    assertSize(((Object[]) data).length);
		component = (T[]) data; 
		type = (Class<T>) component[0].getClass();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#size()
	 */
	@Override
	public final int size() { return component.length; }

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getComponent(int)
	 */
	@Override
	public final T getComponent(int i) { return component[i]; }

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#setComponent(int, java.lang.Object)
	 */
	@Override
	public final void setComponent(int i, T x) { component[i] = x; }

    @Override
    public final void incrementValue(int i, T x) { component[i].add(x); }
	
    @Override
    public synchronized void multiplyByComponentsOf(Coordinates<? extends T> v) { 
        for(int i=component.length; --i >= 0; ) component[i].multiplyBy(v.getComponent(i));
    }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#dot(kovacs.math.AbstractVector)
	 */
	@Override
	public synchronized T dot(Coordinates<? extends T> v) {	
		T term = newEntry();
		T sum = newEntry();
		
		sum.zero();
		
		for(int i=component.length; --i >= 0; ) {
		    T vi = v.getComponent(i);
		    if(vi == null) continue;
			term.setProduct(component[i], vi);
			sum.add(term);
		}
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#asRowVector()
	 */
	@Override
	public AbstractMatrix<T> asRowVector() {
		try { 
			T[][] array = (T[][]) Array.newInstance(component.getClass(), 1);
			array[0] = component;
			return new ObjectMatrix<>(array);
		}
		catch(Exception e) { return null; }

	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#asColumnVector()
	 */
	@Override
	public AbstractMatrix<T> asColumnVector() {
		ObjectMatrix<T> M = new ObjectMatrix<>(getType(), component.length, 1);
		M.setColumn(0, component);
		return M;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addScaled(MathVector<? extends T> o, double factor) {
		for(int i=component.length; --i >= 0; ) {
		    T vi = o.getComponent(i);
            if(vi == null) continue;
		    component[i].addScaled(vi, factor);		
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(int i=component.length; --i >= 0; ) if(!component[i].isNull()) return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#zero()
	 */
	@Override
	public void zero() {
		for(int i=component.length; --i >= 0; ) {
			if(component[i] == null) component[i] = newEntry();
			component[i].zero();
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#subtract(java.lang.Object)
	 */
	@Override
	public void subtract(MathVector<? extends T> o) {
		for(int i=component.length; --i >= 0; ) {
		    T vi = o.getComponent(i);
            if(vi == null) continue;
		    component[i].subtract(vi);	
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public void add(MathVector<? extends T> o) {
		for(int i=component.length; --i >= 0; ) {
		    T vi = o.getComponent(i);
            if(vi == null) continue;
		    component[i].add(vi);	
		}
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Scalable#scale(double)
	 */
	@Override
	public void scale(double factor) {
		for(int i=component.length; --i >= 0; ) component[i].scale(factor);		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbsoluteValue#norm()
	 */
	@Override
	public double absSquared() {
		double norm = 0.0;
		for(int i=component.length; --i >= 0; ) norm += getComponent(i).absSquared();
		return norm;
	}


	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
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
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#orthogonalizeTo(kovacs.math.AbstractVector)
	 */
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


	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(MathVector<? extends T> a, MathVector<? extends T> b) {
		if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");
		
		for(int i=component.length; --i >= 0; ) {
			if(component[i] == null) component[i] = newEntry();
			component[i].setSum(a.getComponent(i), b.getComponent(i));
		}
		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(MathVector<? extends T> a, MathVector<? extends T> b) {
		if(size() != a.size() || size() != b.size()) throw new ShapeException("different size vectors.");
		
		for(int i=component.length; --i >= 0; ) {
			if(component[i] == null) component[i] = newEntry();
			component[i].setDifference(a.getComponent(i), b.getComponent(i));
		}
		
	}

    @Override
    public void fill(T value) {
        Arrays.fill(component, value);
    }

    @Override
    public void setValues(T... values) {
        component = values;
    }
    
    


}

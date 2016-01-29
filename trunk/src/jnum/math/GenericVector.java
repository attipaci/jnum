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

import java.lang.reflect.*;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericVector.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("unchecked")
public class GenericVector<T extends LinearAlgebra<? super T> & AbstractAlgebra<? super T> & Metric<? super T> & AbsoluteValue> extends AbstractVector<T> {
	
	/** The component. */
	public T[] component;
	
	/** The type. */
	protected Class<T> type;
		
	/**
	 * Instantiates a new generic vector.
	 *
	 * @param type the type
	 */
	public GenericVector(Class<T> type) {
		this.type = type;
	}
	
	/**
	 * Instantiates a new generic vector.
	 *
	 * @param type the type
	 * @param size the size
	 */
	public GenericVector(Class<T> type, int size) {
		this(type);
		setSize(size);
	}
	
	/**
	 * Instantiates a new generic vector.
	 *
	 * @param data the data
	 */
	public GenericVector(T[] data) { setData(data); }
	
		
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getType()
	 */
	@Override
	public Class<T> getType() { return (Class<T>) component[0].getClass(); }
	
	/**
	 * New entry.
	 *
	 * @return the t
	 */
	public T newEntry() {
		try { return type.newInstance(); }
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getData()
	 */
	@Override
	public Object getData() { return component; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) { 
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
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#dot(kovacs.math.AbstractVector)
	 */
	@Override
	public synchronized T dot(AbstractVector<? extends T> v) {
		checkMatching(v);
		
		T term = newEntry();
		T sum = newEntry();
		
		sum.zero();
		
		for(int i=component.length; --i >= 0; ) {
			term.setProduct(component[i], v.getComponent(i));
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
			return new GenericMatrix<T>(array);
		}
		catch(Exception e) { return null; }

	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#asColumnVector()
	 */
	@Override
	public AbstractMatrix<T> asColumnVector() {
		GenericMatrix<T> M = new GenericMatrix<T>(getType(), component.length, 1);
		M.setColumn(0, component);
		return M;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addMultipleOf(AbstractVector<? extends T> o, double factor) {
		checkMatching(o);
		for(int i=component.length; --i >= 0; ) component[i].addMultipleOf(o.getComponent(i), factor);		
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
	public void subtract(AbstractVector<? extends T> o) {
		checkMatching(o);
		for(int i=component.length; --i >= 0; ) component[i].subtract(o.getComponent(i));	
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public void add(AbstractVector<? extends T> o) {
		checkMatching(o);
		for(int i=component.length; --i >= 0; ) component[i].add(o.getComponent(i));	
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
	public double norm() {
		double norm = 0.0;
		for(int i=component.length; --i >= 0; ) norm += getComponent(i).norm();
		return norm;
	}


	/* (non-Javadoc)
	 * @see kovacs.math.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(AbstractVector<? extends T> v) {
		checkMatching(v);
		double d2 = 0.0;
		for(int i=component.length; --i >= 0; ) {
			double d = component[i].distanceTo(v.getComponent(i));
			d2 += d*d;
		}
		return Math.sqrt(d2);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#orthogonalizeTo(kovacs.math.AbstractVector)
	 */
	@Override
	public void orthogonalizeTo(AbstractVector<? extends T> v) {
		addMultipleOf(v,-dot(v).abs() / (abs() * v.abs()));
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#setSize(int)
	 */
	@Override
	public void setSize(int size) {
		try { component = (T[]) Array.newInstance(getType(), size ); }
		catch(Exception e) { e.printStackTrace(); }
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(AbstractVector<? extends T> a, AbstractVector<? extends T> b) {
		if(size() != a.size() || size() != b.size()) throw new IllegalArgumentException("different size vectors.");
		
		for(int i=component.length; --i >= 0; ) {
			if(component[i] == null) component[i] = newEntry();
			component[i].setSum(a.getComponent(i), b.getComponent(i));
		}
		
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(AbstractVector<? extends T> a, AbstractVector<? extends T> b) {
		if(size() != a.size() || size() != b.size()) throw new IllegalArgumentException("different size vectors.");
		
		for(int i=component.length; --i >= 0; ) {
			if(component[i] == null) component[i] = newEntry();
			component[i].setDifference(a.getComponent(i), b.getComponent(i));
		}
		
	}
	

}
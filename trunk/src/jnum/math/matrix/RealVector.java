/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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

import jnum.NonConformingException;
import jnum.math.Coordinates;
import jnum.math.Inversion;
import jnum.math.TrueVector;


// TODO: Auto-generated Javadoc
/**
 * The Class RealVector.
 */
public class RealVector extends AbstractVector<Double> implements TrueVector<Double>, Inversion {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1042626482476049050L;
	/** The component. */
	public double[] component;
	
	/**
	 * Instantiates a new real vector.
	 */
	public RealVector() {}
	
	/**
	 * Instantiates a new real vector.
	 *
	 * @param size the size
	 */
	public RealVector(int size) {
		component = new double[size];
	}
	
	
	@Override
    public final Double x() { return component[0]; }
	
	@Override
    public final Double y() { return component[1]; }
	
	@Override
    public final Double z() { return component[2]; }
	
	/**
	 * Instantiates a new real vector.
	 *
	 * @param data the data
	 */
	public RealVector(double[] data) { setData(data); }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getType()
	 */
	@Override
	public Class<Double> getType() { return double.class; }
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#getData()
	 */
	@Override
	public Object getData() {
		Double[] data = new Double[component.length];
		for(int i=size(); --i >= 0; ) data[i] = component[i];
		return data;
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
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
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
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#dot(kovacs.math.AbstractVector)
	 */
	@Override
	public Double dot(Coordinates<? extends Double> v) throws NonConformingException {
		checkMatching(v);
			
		double sum = 0.0;
		for(int i=size(); --i >= 0; ) sum += component[i] * v.getComponent(i);
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#asRowVector()
	 */
	@Override
	public AbstractMatrix<Double> asRowVector() { 
		double[][] array = new double[1][];
		array[0] = component;
		return new Matrix(array);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#asColumnVector()
	 */
	@Override
	public AbstractMatrix<Double> asColumnVector() {
		Matrix M = new Matrix(size(), 1);
		M.setColumn(0, component);
		return M;
	}

	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
	 */
	@Override
	public void addScaled(TrueVector<? extends Double> o, double factor) {
		checkMatching(o);
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
	public void subtract(TrueVector<? extends Double> o) {
		checkMatching(o);
		for(int i=size(); --i >= 0; ) component[i] -= o.getComponent(i);	
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#add(java.lang.Object)
	 */
	@Override
	public void add(TrueVector<? extends Double> o) {
		checkMatching(o);
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
	public double distanceTo(TrueVector<? extends Double> v) {
		checkMatching(v);
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
	public void orthogonalizeTo(TrueVector<? extends Double> v) {
		addScaled(v, -dot(v) / (abs() * v.abs()));
	}
	
	/**
	 * Norm of.
	 *
	 * @param v the v
	 * @return the double
	 */
	public static double normOf(double[] v) {
		double norm = 0.0;
		for(int i=v.length; --i >= 0; ) norm += v[i] * v[i];
		return norm;
	}
	
	/**
	 * Norm of.
	 *
	 * @param v the v
	 * @return the double
	 */
	public static double normOf(float[] v) {
		double norm = 0.0;
		for(int i=v.length; --i >= 0; ) norm += v[i] * v[i];
		return norm;
	}
	
	/**
	 * Length of.
	 *
	 * @param v the v
	 * @return the double
	 */
	public static double lengthOf(double[] v) {
		return Math.sqrt(normOf(v));
	}
	
	/**
	 * Length of.
	 *
	 * @param v the v
	 * @return the double
	 */
	public static double lengthOf(float[] v) {
		return Math.sqrt(normOf(v));
	}

	/* (non-Javadoc)
	 * @see kovacs.math.AbstractVector#setSize(int)
	 */
	@Override
	public void setSize(int size) {
		component = new double[size];		
	}

	
	
	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setSum(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setSum(TrueVector<? extends Double> a, TrueVector<? extends Double> b) {
		if(size() != a.size() || size() != b.size()) throw new IllegalArgumentException("different size vectors.");
		
		for(int i=size(); --i >= 0; ) component[i] = a.getComponent(i) - b.getComponent(i);
	}

	/* (non-Javadoc)
	 * @see kovacs.math.Additive#setDifference(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setDifference(TrueVector<? extends Double> a, TrueVector<? extends Double> b) {
		if(size() != a.size() || size() != b.size()) throw new IllegalArgumentException("different size vectors.");
		
		for(int i=size(); --i >= 0; ) component[i] = a.getComponent(i) - b.getComponent(i);
	}

    @Override
    public void invert() {
        for(int i=size(); --i >= 0; ) component[i] *= -1;
    }

	
}

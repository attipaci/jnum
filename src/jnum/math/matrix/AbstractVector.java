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

import java.io.Serializable;
import java.text.NumberFormat;

import jnum.CopiableContent;
import jnum.Util;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedValues;
import jnum.math.MathVector;
import jnum.text.DecimalFormating;


/**
 * An abstract vector class representing a mathematical vector for some generic type element. It has two principal 
 * subclasses, {@link RealVector}, which is a real-valued vector with essentially primitive <code>double</code> 
 * elements, and {@link ObjectVector}, which handles vector for generic type objects as long as they provide the 
 * required algebra to support matrix operation. For example {@link ComplexVector} with {@link jnum.math.Complex} elements 
 * is an example subtype, but one could construct vectors e.g. with {@link Matrix} or {@link ObjectMatrix} elements 
 * (for a vecor of matrices), or vectors with other more complex types...
 * 
 * @author Attila Kovacs
 *
 * @param <T>       The generic type of the elements in this vector.
 */
public abstract class AbstractVector<T> implements MathVector<T>, Serializable, Cloneable, CopiableContent<AbstractVector<T>>, DecimalFormating {

	private static final long serialVersionUID = 785522803183758105L;

	/**
	 * Gets the underlying data object, normally an simple array, either and object array of type T[]
	 * or a primitive array such as double[].
	 * 
	 * @return     The data object (array) that holds the underlying data of this vector.
	 */
	public abstract Object getData();
	
	/**
	 * Sets the underlying data in this vector to the specified data object, which is usually a simple
	 * array, either an object array T[], or a primitive array such as double[]. Whatever it is
	 * it expected the type of object as returned by {@link #getData()}.
	 * 
	 * @param data     The new data object (array) for this vector.
	 */
	public abstract void setData(Object data);

	/**
	 * Gets a new instance of a vector of the same type as this one, with the specified size. The new 
	 * vector is initialized with zero content.
	 * 
	 * @param size     The size (number of components) of the new vector instance.
	 * @return         A new vetor of the same type as this, with the specified size, and zero inital values.
	 */
	public abstract AbstractVector<T> getVectorInstance(int size);
	
	
	@SuppressWarnings("unchecked")
    @Override
	public AbstractVector<T> clone() {
		try { return (AbstractVector<T>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }		
	}
	
	@Override
    public AbstractVector<T> copy() {
	    return copy(true);
	}


	@Override
	public double abs() {
		return Math.sqrt(absSquared());
	}

	@Override
	public double normalize() {
	    double l = abs();
		scale(1.0/l);
		return l;
	}

	/**
     * Checks if the vector has the expected size for some operation. If not a {@link ShapeException} is thrown.
     * 
     * @param size
     * @throws ShapeException
     */
	public void assertSize(int size) { 
		if(size() != size) throw new ShapeException("Vector has wrong size " + size() + ". Expected " + size + ".");	
	}

	@Override
    public void flip() {
	    scale(-1.0);
	}
	
	
	   
    @Override
    public void reflectOn(final MathVector<? extends T> v) {
        AbstractVector<T> ortho = copy();
        ortho.orthogonalizeTo(v);
        addScaled(ortho, -2.0);        
    }
    
    @Override
    public final int capacity() {
        return size();
    }


    @Override
    public final int dimension() {
        return 1;
    }


    @Override
    public final Index1D getSize() {
        return new Index1D(size());
    }


    @Override
    public final T get(Index1D index) {
        return getComponent(index.i());
    }

    /**
     * Gets an independent copy of a component in this vector.
     * 
     * @param i        The index of the component
     * @return         A deep copy of the value at the specified location.
     */
    public abstract T copyOf(int i);
 
    /**
     * Gets an independent copy of a component in this vector.
     * 
     * @param idx      The index of the component
     * @return         A deep copy of the value at the specified location.
     */
    public final T copyOf(Index1D idx) {
        return copyOf(idx.i());
    }

    @Override
    public void set(Index1D index, T value) {
        setComponent(index.i(), value);
    }


    @Override
    public final Index1D getIndexInstance() {
        return new Index1D();
    }


    @Override
    public final Index1D copyOfIndex(Index1D index) {
        return index.copy();
    }


    @Override
    public final boolean conformsTo(Index1D size) {
        return size.i() == size();
    }


    @Override
    public final boolean conformsTo(IndexedValues<Index1D, ?> data) {
        return data.getSize().i() == size();
    }


    @Override
    public String getSizeString() {
        return "[" + size() + "]";
     }


    @Override
    public boolean containsIndex(Index1D index) {
        int i = index.getValue(0);
        if(i < 0) return false;
        if(i >= size()) return false;
        return true;
    }

    /**
     * Attempts to convert a component in this vector to a string of the specified number format.
     * If the component is not a {@link Number} type, or if it does not support 
     * {@link jnum.text.DecimalFormating} then then this method it will simply return the
     * component calling its default {@link Object#toString()} implementation.
     * 
     * @param i     Index of component
     * @param nf    Number formating specification. It can be null to simply call {@link Object#toString()} on the component.
     * @return      The string representation of the vector component.
     */
    public abstract String toString(int i, NumberFormat nf);
    
    /**
     * Same as {@link #toString(int, NumberFormat)} but with an {@link Index1D} specifying the
     * component index.
     * 
     * @param idx   Index of component
     * @param nf    Number formating specification. It can be null to simply call {@link Object#toString()} on the component.
     * @return      The string representation of the vector component.
     */
    public final String toString(Index1D idx, NumberFormat nf) {
        return toString(idx.i(), nf);
    }
    
    @Override
    public String toString() {
        return toString(Util.s4);
    }
    
    @Override
    public String toString(int decimals) {
        return toString(Util.s[decimals+1]);
    }
    
    @Override
    public String toString(NumberFormat nf) {
        StringBuffer buf = new StringBuffer();
        
        buf.append(getClass().getSimpleName());
        buf.append(getSizeString());
        buf.append(": {");
        for(int i=0; i<size(); i++) {
            if(i > 0) buf.append(',');
            buf.append(' ');
            buf.append(toString(i, nf));
        }
        buf.append(" }");
        return new String(buf);
    }
   
}

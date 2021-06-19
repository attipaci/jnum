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

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.ShapeException;
import jnum.data.IndexedValues;
import jnum.data.samples.Index1D;
import jnum.math.MathVector;



public abstract class AbstractVector<T> implements MathVector<T>, Serializable, Cloneable, CopiableContent<AbstractVector<T>> {

	private static final long serialVersionUID = 785522803183758105L;

	protected AbstractVector() {}

	public abstract Class<T> getType();
	
	public abstract Object getData();
	
	public abstract void setData(Object data);
		

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


	public void assertSize(int size) { 
		if(size() != size) throw new ShapeException("Vector has wrong size " + size() + ". Expected " + size + ".");	
	}

	@Override
    public void invert() {
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

    

    public final static int ROW_VECTOR = 0;

    public final static int COLUMN_VECTOR = 1;


	
}

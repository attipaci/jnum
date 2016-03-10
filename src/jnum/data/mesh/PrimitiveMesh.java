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
package jnum.data.mesh;

import jnum.Function;
import jnum.math.LinearAlgebra;



// TODO: Auto-generated Javadoc
/**
 * The Class PrimitiveArray.
 *
 * @param <T> the generic type
 */
public abstract class PrimitiveMesh<T extends Number> extends Mesh<T> implements LinearAlgebra<PrimitiveMesh<?>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8401121783922804093L;

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public PrimitiveMesh(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 */
	public PrimitiveMesh(Class<T> type) {
		super(type);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param data the data
	 */
	public PrimitiveMesh(Object data) {
		super(data);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#initialize()
	 */
	@Override
	public void initialize() {
		try { super.initialize(); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(Number value : this) if(value.doubleValue() != 0.0) return false;
		return true;
	}
	
	@Override
    public void zero() {
        PrimitiveArrayIterator<T> iterator = (PrimitiveArrayIterator<T>) iterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.setElement(zeroValue());
        }
    }
	
	@Override
    public void scale(double factor) {
        PrimitiveArrayIterator<T> iterator = (PrimitiveArrayIterator<T>) iterator();
        while(iterator.hasNext()) {
            T value = iterator.next();
            iterator.setElement(getScaled(value, factor));
        }
    }
	
	@Override
    public void add(PrimitiveMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        PrimitiveArrayIterator<T> i = (PrimitiveArrayIterator<T>) iterator();
        PrimitiveArrayIterator<? extends Number> i2 = (PrimitiveArrayIterator<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            T value = getSum(i.next(), i2.next());
            i.setElement(value);
        }
    }
 
	@Override
    public void subtract(PrimitiveMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        PrimitiveArrayIterator<T> i = (PrimitiveArrayIterator<T>) iterator();
        PrimitiveArrayIterator<? extends Number> i2 = (PrimitiveArrayIterator<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            T value = getDifference(i.next(), i2.next());
            i.setElement(value);
        }
    }
	
	@Override
	public void setSum(PrimitiveMesh<? extends Number> a, PrimitiveMesh<? extends Number> b) {
	    if(!a.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
	    if(!b.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
	    
        PrimitiveArrayIterator<T> i = (PrimitiveArrayIterator<T>) iterator();
        PrimitiveArrayIterator<? extends Number> iA = (PrimitiveArrayIterator<? extends Number>) a.iterator();
        PrimitiveArrayIterator<? extends Number> iB = (PrimitiveArrayIterator<? extends Number>) b.iterator();
        
        while(i.hasNext()) {
            i.next();
            i.setElement(getSum(iA.next(), iB.next()));
        }
         
	}

	@Override
	public void setDifference(PrimitiveMesh<? extends Number> a, PrimitiveMesh<? extends Number> b) {
	    if(!a.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        if(!b.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        PrimitiveArrayIterator<T> i = (PrimitiveArrayIterator<T>) iterator();
        PrimitiveArrayIterator<? extends Number> iA = (PrimitiveArrayIterator<? extends Number>) a.iterator();
        PrimitiveArrayIterator<? extends Number> iB = (PrimitiveArrayIterator<? extends Number>) b.iterator();
        
        while(i.hasNext()) {
            i.next();
            i.setElement(getDifference(iA.next(), iB.next()));
        }
	}
	
	@Override
    public void addMultipleOf(PrimitiveMesh<? extends Number> o, double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        PrimitiveArrayIterator<T> i = (PrimitiveArrayIterator<T>) iterator();
        PrimitiveArrayIterator<? extends Number> i2 = (PrimitiveArrayIterator<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            T value = getSum(i.next(), i2.next().doubleValue() * factor);
            i.setElement(value);
        }
    }
	
	protected abstract T getScaled(T value, double factor);
	
	protected abstract T getSum(Number a, Number b);
	
    protected abstract T getDifference(Number a, Number b);
    
    protected abstract T zeroValue();
    
    @Override
    protected T lineElementAt(Object linearArray, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setLineElementAt(Object linearArray, int index, T value) {
        // TODO Auto-generated method stub
        
    }

    protected abstract T baseLineElementAt(Object simpleArray, int index);
    
    protected abstract void setBaseLineElementAt(Object simpleArray, int index, T value);
    
	
    @Override
    public void addPatchAt(T point, double[] exactpos, double[] patchSize, Function<double[], Double> shape) {
        // TODO Auto-generated method stub
        
    }

   
  
}

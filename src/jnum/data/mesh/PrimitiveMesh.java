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
	 * @see kovacs.math.LinearAlgebra#isNull()
	 */
	@Override
	public boolean isNull() {
		for(Number value : this) if(value.doubleValue() != 0.0) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see jnum.math.LinearAlgebra#zero()
	 */
	@Override
    public void zero() {
        PrimitiveArrayIterator<T> iterator = (PrimitiveArrayIterator<T>) iterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.setElement(zeroValue());
        }
    }
	
	/* (non-Javadoc)
	 * @see jnum.math.Scalable#scale(double)
	 */
	@Override
    public void scale(double factor) {
        PrimitiveArrayIterator<T> iterator = (PrimitiveArrayIterator<T>) iterator();
        while(iterator.hasNext()) {
            T value = iterator.next();
            iterator.setElement(getScaled(value, factor));
        }
    }
	
	/**
	 * Adds the.
	 *
	 * @param o the o
	 * @throws NonConformingException the non conforming exception
	 */
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
 
	/**
	 * Subtract.
	 *
	 * @param o the o
	 * @throws NonConformingException the non conforming exception
	 */
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
	
	/**
	 * Sets the sum.
	 *
	 * @param a the a
	 * @param b the b
	 */
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

	/**
	 * Sets the difference.
	 *
	 * @param a the a
	 * @param b the b
	 */
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
	
	/**
	 * Adds the multiple of.
	 *
	 * @param o the o
	 * @param factor the factor
	 */
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
	
	/**
	 * Gets the scaled.
	 *
	 * @param value the value
	 * @param factor the factor
	 * @return the scaled
	 */
	protected abstract T getScaled(T value, double factor);
	
	/**
	 * Gets the sum.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the sum
	 */
	protected abstract T getSum(Number a, Number b);
	
    /**
     * Gets the difference.
     *
     * @param a the a
     * @param b the b
     * @return the difference
     */
    protected abstract T getDifference(Number a, Number b);
    
    /**
     * Zero value.
     *
     * @return the t
     */
    protected abstract T zeroValue();
    
    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#lineElementAt(java.lang.Object, int)
     */
    @Override
    protected T lineElementAt(Object linearArray, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#setLineElementAt(java.lang.Object, int, java.lang.Object)
     */
    @Override
    protected void setLineElementAt(Object linearArray, int index, T value) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Base line element at.
     *
     * @param simpleArray the simple array
     * @param index the index
     * @return the t
     */
    protected abstract T baseLineElementAt(Object simpleArray, int index);
    
    /**
     * Sets the base line element at.
     *
     * @param simpleArray the simple array
     * @param index the index
     * @param value the value
     */
    protected abstract void setBaseLineElementAt(Object simpleArray, int index, T value);
    
	
    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#addPatchAt(java.lang.Object, double[], double[], jnum.Function)
     */
    @Override
    public void addPatchAt(T point, double[] exactpos, double[] patchSize, Function<double[], Double> shape) {
        // TODO Auto-generated method stub
        
    }

   
  
}

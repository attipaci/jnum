/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
import jnum.NonConformingException;
import jnum.data.ArrayUtil;
import jnum.math.LinearAlgebra;



// TODO: Auto-generated Javadoc
/**
 * The Class PrimitiveMesh.
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
        Mesh.Iterator<T> iterator = iterator();
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
        Mesh.Iterator<T> iterator = iterator();
        while(iterator.hasNext()) {
            T value = iterator.next();
            iterator.setElement(getScaled(value, factor));
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
        
        Mesh.Iterator<T> i = iterator();
        Mesh.Iterator<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.setElement(getSumOf(i.next(), i2.next().doubleValue() * factor));
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
        
        Mesh.Iterator<T> i = iterator();
        Iterator<? extends Number> i2 = (Iterator<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            i.setElement(getSumOf(i.next(), i2.next()));
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
        
        Mesh.Iterator<T> i = iterator();
        Mesh.Iterator<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.setElement(getDifferenceOf(i.next(), i2.next()));
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
	    if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
	    if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
	    
	    Mesh.Iterator<T> i = iterator();
	    Mesh.Iterator<? extends Number> iA = a.iterator();
        Mesh.Iterator<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNextElement(getSumOf(iA.next(), iB.next()));
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
        
        Mesh.Iterator<T> i = iterator();
        Mesh.Iterator<? extends Number> iA = a.iterator();
        Mesh.Iterator<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNextElement(getDifferenceOf(iA.next(), iB.next()));
        }
	}
	
	
	/**
     * Adds the patch at.
     *
     * @param point the point
     * @param exactpos the exactpos
     * @param patchSize the patch size
     * @param shape the shape
     */
    public void addPatchAt(double[] exactOffset, Function<double[], T> shape, double[] patchSize) {
         
        final int[] from = new int[exactOffset.length];
        final int[] to = new int[exactOffset.length];
        final double[] d = new double[exactOffset.length];
       
        final int size[] = getSize();
        
        for(int i=from.length; --i >=0; ) {
            from[i] = Math.max(0, (int) Math.floor(exactOffset[i]));
            if(from[i] > size[i]) return; // The patch is outside of the available range...
            to[i] = Math.min(size[i], (int) Math.ceil(exactOffset[i] + patchSize[i]));
        }
        
        Mesh.Iterator<T> i = ArrayUtil.iterator(data, from, to);
        
        while(i.hasNext()) {
            int[] index = i.getIndex();
            for(int k=index.length; --k >= 0; ) d[k] = index[k] + exactOffset[k] - (from[k]<<1);
            i.setElement(getSumOf(i.next(), shape.valueAt(d)));
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
	protected abstract T getSumOf(Number a, Number b);
	
    /**
     * Gets the difference.
     *
     * @param a the a
     * @param b the b
     * @return the difference
     */
    protected abstract T getDifferenceOf(Number a, Number b);
    
    /**
     * Zero value.
     *
     * @return the t
     */
    protected abstract T zeroValue();
    
    
  

  
}

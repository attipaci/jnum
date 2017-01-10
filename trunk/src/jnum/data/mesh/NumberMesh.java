/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.data.mesh;


import jnum.Function;
import jnum.NonConformingException;
import jnum.math.Division;
import jnum.math.LinearAlgebra;
import jnum.math.Multiplicative;
import jnum.math.Ratio;



// TODO: Auto-generated Javadoc
/**
 * The Class PrimitiveMesh.
 *
 * @param <T> the generic type
 */
public abstract class NumberMesh<T extends Number> extends Mesh<T> implements LinearAlgebra<NumberMesh<?>>,
    Multiplicative<NumberMesh<?>>, Division<NumberMesh<?>>, Ratio<NumberMesh<?>, NumberMesh<?>>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8401121783922804093L;

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public NumberMesh(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param type the type
	 */
	public NumberMesh(Class<T> type) {
		super(type);
	}

	/**
	 * Instantiates a new primitive array.
	 *
	 * @param data the data
	 */
	public NumberMesh(Object data) {
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
        final MeshCrawler<T> iterator = iterator();
        final T zeroValue = zeroValue();
        while(iterator.hasNext()) iterator.setNext(zeroValue);
    }
	
	/* (non-Javadoc)
	 * @see jnum.math.Scalable#scale(double)
	 */
	@Override
    public void scale(double factor) {
        final MeshCrawler<T> iterator = iterator();
        while(iterator.hasNext()) {
            T value = iterator.next();
            iterator.setCurrent(getProductOf(value, factor));
        }
    }
	
	/**
     * Adds the multiple of.
     *
     * @param o the o
     * @param factor the factor
     */
    @Override
    public void addMultipleOf(NumberMesh<? extends Number> o, double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.setCurrent(getSumOf(i.next(), i2.next().doubleValue() * factor));
        }
       
    }
    
	
	/**
	 * Adds the.
	 *
	 * @param o the o
	 * @throws NonConformingException the non conforming exception
	 */
	@Override
    public void add(NumberMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = (MeshCrawler<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            i.setCurrent(getSumOf(i.next(), i2.next()));
        }
    }
 
	/**
	 * Subtract.
	 *
	 * @param o the o
	 * @throws NonConformingException the non conforming exception
	 */
	@Override
    public void subtract(NumberMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.setCurrent(getDifferenceOf(i.next(), i2.next()));
        }
    }
	
	/**
	 * Sets the sum.
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public void setSum(NumberMesh<? extends Number> a, NumberMesh<? extends Number> b) {
	    if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
	    
	    final MeshCrawler<T> i = iterator();
	    final MeshCrawler<? extends Number> iA = a.iterator();
        final MeshCrawler<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNext(getSumOf(iA.next(), iB.next()));
        }
         
	}

	/**
	 * Sets the difference.
	 *
	 * @param a the a
	 * @param b the b
	 */
	@Override
	public void setDifference(NumberMesh<? extends Number> a, NumberMesh<? extends Number> b) {
	    if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
	    
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> iA = a.iterator();
        final MeshCrawler<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNext(getDifferenceOf(iA.next(), iB.next()));
        }
	}
	
	
	@Override
    public void multiplyBy(NumberMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot multiply by array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = (MeshCrawler<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            i.setCurrent(getProductOf(i.next(), i2.next()));
        }
    }
	
	
	
	@Override
    public void setProduct(NumberMesh<? extends Number> a, NumberMesh<? extends Number> b) {
	    if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> iA = a.iterator();
        final MeshCrawler<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNext(getProductOf(iA.next(), iB.next()));
        }
    }
	
	@Override
    public void divideBy(NumberMesh<? extends Number> o) throws NonConformingException {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot multiply by array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = (MeshCrawler<? extends Number>) o.iterator();
        
        while(i.hasNext()) {
            i.setCurrent(getRatioOf(i.next(), i2.next()));
        }
    }
    
    
    
    @Override
    public void setRatio(NumberMesh<? extends Number> a, NumberMesh<? extends Number> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> iA = a.iterator();
        final MeshCrawler<? extends Number> iB = b.iterator();
        
        while(i.hasNext()) {
            i.setNext(getRatioOf(iA.next(), iB.next()));
        }
    }
	
	
	
    @Override
    public void addPatchAt(double[] exactOffset, Function<double[], T> shape, double[] patchSize) {
         
        final int[] from = new int[exactOffset.length];
        final int[] to = new int[exactOffset.length];
        final double[] d = new double[exactOffset.length];
       
        final int size[] = getSize();
        final int index[] = new int[size.length];
        
        for(int i=from.length; --i >=0; ) {
            from[i] = Math.max(0, (int) Math.floor(exactOffset[i]));
            if(from[i] > size[i]) return; // The patch is outside of the available range...
            to[i] = Math.min(size[i], (int) Math.ceil(exactOffset[i] + patchSize[i]));
        }
        
        final MeshCrawler<T> i = MeshCrawler.createFor(data, from, to);
        
        while(i.hasNext()) {
            i.getPosition(index);
            for(int k=index.length; --k >= 0; ) d[k] = index[k] + exactOffset[k] - (from[k]<<1);
            i.setCurrent(getSumOf(i.next(), shape.valueAt(d)));
        } 
    }
	
	
    /**
     * Zero value.
     *
     * @return the t
     */
    protected abstract T zeroValue();
    
    public abstract T convert(Number x);
    
    public abstract T getSumOf(Number a, Number b);
  
    public abstract T getDifferenceOf(Number a, Number b);
    
    public abstract T getProductOf(Number a, Number b);
    
    public abstract T getRatioOf(Number a, Number b);
    
    

  
}

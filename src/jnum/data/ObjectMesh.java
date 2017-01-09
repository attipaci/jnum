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
package jnum.data;

import jnum.Function;
import jnum.NonConformingException;
import jnum.math.Additive;
import jnum.math.LinearAlgebra;
import jnum.math.Scalable;
import jnum.text.Parser;


// TODO: Auto-generated Javadoc
/**
 * The Class ObjectMesh.
 *
 * @param <T> the generic type
 */
public class ObjectMesh<T> extends Mesh<T> implements LinearAlgebra<Mesh<T>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 86938797450633242L;

	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 */
	public ObjectMesh(Class<T> type) {
		super(type);
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public ObjectMesh(Class<T> type, int[] dimensions) {
		super(type, dimensions);
	}

	/**
	 * Instantiates a new generic array.
	 *
	 * @param data the data
	 */
	public ObjectMesh(Object data) {
		super(data);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#lineElementAt(java.lang.Object, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T linearElementAt(Object linearArray, int index) {
		return ((T[]) linearArray)[index];
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#setLineElementAt(java.lang.Object, int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setLinearElementAt(Object linearArray, int index, T value) {
		((T[]) linearArray)[index] = value;
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#parseElement(java.lang.String)
	 */
	@Override
	public T parseElement(String text) throws ClassCastException, InstantiationException, IllegalAccessException  {
		T value = elementClass.newInstance();
		((Parser) value).parse(text);
		return value;
	}

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<T> newInstance() {
        return new ObjectMesh<T>(elementClass);
    }
    
    
    @Override
    public void add(Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshIterator<T> i = iterator();
        final MeshIterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.add(i2.next());
        }
    }

    @Override
    public void subtract(Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot subtract array of different size/shape.");
        
        final MeshIterator<T> i = iterator();
        final MeshIterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.subtract(i2.next());
        }
    }

    @Override
    public void setSum(Mesh<T> a, Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshIterator<T> i = iterator();
        final MeshIterator<T> iA = a.iterator();
        final MeshIterator<T> iB = b.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.setSum(iA.next(), iB.next());
        }
    }

    @Override
    public void setDifference(Mesh<T> a, Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshIterator<T> i = iterator();
        final MeshIterator<T> iA = a.iterator();
        final MeshIterator<T> iB = b.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.setDifference(iA.next(), iB.next());
        }
    }

    @Override
    public void scale(double factor) {  
        final MeshIterator<T> i = iterator();
  
        while(i.hasNext()) ((Scalable) i.next()).scale(factor);
    }

    @Override
    public void addMultipleOf(Mesh<T> o, double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add scaled array of different size/shape.");
        
        final MeshIterator<T> i = iterator();
        final MeshIterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            value.addMultipleOf(i2.next(), factor);
        }
    }

    @Override
    public boolean isNull() {
        final MeshIterator<T> i = iterator();
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            if(!value.isNull()) return false;
        }
        return true;
    }

    @Override
    public void zero() {
        final MeshIterator<T> i = iterator();
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            value.zero();
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
        
        MeshIterator<T> i = iterator(from, to);
        
        while(i.hasNext()) {
            int[] index = i.getIndex();
            for(int k=index.length; --k >= 0; ) d[k] = index[k] + exactOffset[k] - (from[k]<<1);
            final Additive<? super T> value = (Additive<? super T>) i.next();
            value.add(shape.valueAt(d));
        } 
    }
    
    
    
    
    
    
   
  
}

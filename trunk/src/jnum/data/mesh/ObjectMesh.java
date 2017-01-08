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

import java.util.Arrays;
import java.util.NoSuchElementException;

import jnum.Function;
import jnum.NonConformingException;
import jnum.data.ArrayUtil;
import jnum.math.Additive;
import jnum.math.LinearAlgebra;
import jnum.math.Scalable;
import jnum.text.Parser;


// TODO: Auto-generated Javadoc
/**
 * The Class GenericArray.
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
	protected T lineElementAt(Object linearArray, int index) {
		return ((T[]) linearArray)[index];
	}

	/* (non-Javadoc)
	 * @see kovacs.data.AbstractArray#setLineElementAt(java.lang.Object, int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setLineElementAt(Object linearArray, int index, T value) {
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
            final Additive<? super T> value = (Additive<? super T>) i.next();
            value.add(shape.valueAt(d));
        } 
    }
    
    
    
    /**
     * The Class ObjectMesh.Iterator.
     *
     * @param <T> the generic type
     */
    public static class Iterator<T> extends Mesh.Iterator<T> {
        
        /** The child. */
        private Mesh.Iterator<T> child;
        
        /** The array. */
        private Object[] array;
        
        
        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         */
        public Iterator(Object[] data) {
            this(data, new int[ArrayUtil.getRank(data)], ArrayUtil.getShape(data));     
        }
        
        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         * @param depth the depth
         * @throws IllegalArgumentException the illegal argument exception
         */
        public Iterator(Object[] data, int depth) throws IllegalArgumentException {
            this(data, new int[depth], Arrays.copyOf(ArrayUtil.getShape(data), depth));     
        }
        
        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IllegalArgumentException the illegal argument exception
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        public Iterator(Object[] data, int[] from, int[] to) throws IllegalArgumentException, IndexOutOfBoundsException {
            super(from, to);
            array = data;
            
            if(from.length > ArrayUtil.getRank(data) || from.length != to.length) throw new IllegalArgumentException("Iteration indeces are higher rank than array.");
            if(fromIndex < 0 || toIndex > data.length) throw new IndexOutOfBoundsException("Iterator outside of array range.");
            if(toIndex <= fromIndex) throw new IndexOutOfBoundsException("Inverted or empty iterator range.");
            
            if(from.length > 1) {
                int[] childFrom = Arrays.copyOfRange(from, 1, from.length);
                int[] childTo = Arrays.copyOfRange(to, 1, to.length);
        
                Object element = array[fromIndex];
            
                if(element instanceof Object[]) child = new ObjectMesh.Iterator<T>((Object[]) element, childFrom, childTo);
                else child = new PrimitiveArrayIterator<T>(element, childFrom[0], childTo[0]);

                child.setParent(this);  
            }
            
            reset();
        }
        
        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#reset()
         */
        @Override
        public void reset() {
            if(child == null) setIndex(fromIndex-1);
            else {
                setIndex(fromIndex);
                child.reset();
            }
        }
        
        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            if(currentIndex+1 < toIndex) return true;
            if(child != null) return child.hasNext();
            return false;
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setArray(java.lang.Object)
         */
        @Override
        public void setArray(Object data) throws IllegalArgumentException {
            if(!(data instanceof Object[])) throw new IllegalArgumentException("Not and Object[].");
            array = (Object[]) data;        
        }
        
        // Set the next block of data, and propage down to all children...
        /**
         * Sets the next block.
         */
        public void setNextBlock() {
            if(++index[0] < toIndex) {
                child.setArray(array[currentIndex]);
                child.reset();
            }
            else parent.setNextBlock();
        }
        
        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        @SuppressWarnings("unchecked")
        public T next() throws NoSuchElementException {
            if(child == null) {
                currentIndex++;
                if(currentIndex < toIndex) setIndex(currentIndex);
                else throw new NoSuchElementException("Reached end of iterator range at " + currentIndex + ".");
                return (T) array[currentIndex];
            }
            else {
                try { return child.next(); }
                catch(NoSuchElementException e) {
                    if(currentIndex+1 < toIndex) {
                        setIndex(++currentIndex);
                        return child.next();
                    }
                    else throw e;
                }
            }
        }

        // Not implemented...
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setElement(java.lang.Object)
         */
        @Override
        public void setElement(T element) {
            if(child == null) array[currentIndex] = element;
            else child.setElement(element);     
        }
        

        
        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int[], int)
         */
        @Override
        protected void setIndex(int[] index, int pos) {
            setIndex(index[pos]);
            if(child != null) {
                child.setArray(array[currentIndex]);
                child.setIndex(index, pos+1);
            }
            
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int)
         */
        @Override
        protected void setIndex(int i) {
            currentIndex = i;
            index[0] = i;
            if(parent != null) parent.index[1] = i;
            
            if(child != null) {
                child.setArray(array[i]);
                child.reset();      
            }
        }

        /* (non-Javadoc)
         * @see kovacs.data.DataManager#getData()
         */
        @Override
        public Object getData() {
            return array;
        }
        
    }


    @Override
    public void add(Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final Mesh.Iterator<T> i = iterator();
        final Mesh.Iterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.add(i2.next());
        }
    }

    @Override
    public void subtract(Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot subtract array of different size/shape.");
        
        final Mesh.Iterator<T> i = iterator();
        final Mesh.Iterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.subtract(i2.next());
        }
    }

    @Override
    public void setSum(Mesh<T> a, Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final Mesh.Iterator<T> i = iterator();
        final Mesh.Iterator<T> iA = a.iterator();
        final Mesh.Iterator<T> iB = b.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.setSum(iA.next(), iB.next());
        }
    }

    @Override
    public void setDifference(Mesh<T> a, Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final Mesh.Iterator<T> i = iterator();
        final Mesh.Iterator<T> iA = a.iterator();
        final Mesh.Iterator<T> iB = b.iterator();
        
        while(i.hasNext()) {
            Additive<? super T> value = (Additive<? super T>) i.next();
            value.setDifference(iA.next(), iB.next());
        }
    }

    @Override
    public void scale(double factor) {  
        final Mesh.Iterator<T> i = iterator();
  
        while(i.hasNext()) ((Scalable) i.next()).scale(factor);
    }

    @Override
    public void addMultipleOf(Mesh<T> o, double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add scaled array of different size/shape.");
        
        final Mesh.Iterator<T> i = iterator();
        final Mesh.Iterator<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            value.addMultipleOf(i2.next(), factor);
        }
    }

    @Override
    public boolean isNull() {
        final Mesh.Iterator<T> i = iterator();
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            if(!value.isNull()) return false;
        }
        return true;
    }

    @Override
    public void zero() {
        final Mesh.Iterator<T> i = iterator();
        while(i.hasNext()) {
            LinearAlgebra<? super T> value = (LinearAlgebra<? super T>) i.next();
            value.zero();
        }   
    }

	
}

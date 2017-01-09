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

import java.util.Arrays;
import java.util.NoSuchElementException;



/**
 * The Class Mesh.Iterator.
 *
 * @param <T> the generic type
 */
public abstract class MeshIterator<T> implements DataIterator<T> {

    /** The parent. */
    private GenericIterator<T> parent;

    /** The to index. */
    protected int currentIndex, fromIndex, toIndex;

    /** The index. */
    protected int[] index;

    /**
     * Instantiates a new array iterator.
     */
    protected MeshIterator() {}

    /**
     * Instantiates a new array iterator.
     *
     * @param from the from
     * @param to the to
     */
    public MeshIterator(int[] from, int[] to) {
        fromIndex = from[0];
        toIndex = to[0];
        index = Arrays.copyOf(from, from.length);
    }

    /**
     * Sets the parent.
     *
     * @param iterator the new parent
     */
    private void setParent(GenericIterator<T> iterator) {
        parent = iterator;
    }

    protected GenericIterator<T> getParent() { return parent; }

    /* (non-Javadoc)
     * @see kovacs.data.DataManager#reset()
     */
    @Override
    public abstract void reset();

    /**
     * Sets the array.
     *
     * @param data the new array
     * @throws IllegalArgumentException the illegal argument exception
     */
    public abstract void setArray(Object data) throws IllegalArgumentException; 

    /* (non-Javadoc)
     * @see kovacs.data.DataManager#setElement(java.lang.Object)
     */
    @Override
    public abstract void setElement(T element);

    /**
     * Sets the next element.
     *
     * @param element the new next element
     * @throws NoSuchElementException the no such element exception
     */
    public void setNextElement(T element) throws NoSuchElementException {
        next();
        setElement(element);
    }


    /**
     * Sets the index.
     *
     * @param index the new index
     */
    protected abstract void setIndex(int index);

    /**
     * Position.
     *
     * @param index the index
     */
    public void position(int[] index) { setIndex(index, 0); }

    /**
     * Sets the index.
     *
     * @param index the index
     * @param depth the depth
     */
    protected abstract void setIndex(int[] index, int depth);

    // returned index should be read-only access, else it may be corrupted!!!!
    /**
     * Gets the index.
     *
     * @return the index
     */
    public int[] getIndex() { return index; }

    // void setRange(int[] from, int[] to);
    // int[][] getRange(); // [from|to][]


    // deep iterators can be used to provide write access to primite arrays as well via the setElement() method.
    // Careful thuogh, it may be slow if the required autoboxing/unboxing has overheads
    /**
     * Iterator.
     *
     * @param <T> the generic type
     * @param array the array
     * @return the array iterator
     */
    public static <T> MeshIterator<T> createFor(Object array) {
        if(array instanceof Object[]) return new GenericIterator<T>((Object[]) array);
        else return new PrimitiveArrayIterator<T>(array);
    }

    /**
     * Iterator.
     *
     * @param <T> the generic type
     * @param array the array
     * @param depth the depth
     * @return the array iterator
     */
    public static <T> MeshIterator<T> createFor(Object array, int depth) {
        if(depth >= ArrayUtil.getRank(array)) return createFor(array);
        else return new GenericIterator<T>((Object[]) array, depth);
    }

    /**
     * Iterator.
     *
     * @param <T> the generic type
     * @param array the array
     * @param fromIndex the from index
     * @param toIndex the to index
     * @return the array iterator
     */
    public static <T> MeshIterator<T> createFor(Object array, int[] fromIndex, int[] toIndex) {
        if(array instanceof Object[]) return new GenericIterator<T>((Object[]) array, fromIndex, toIndex);
        else return new PrimitiveArrayIterator<T>(array, fromIndex[0], toIndex[0]); 
    }   




    /**
     * The Class ObjectMesh.Iterator.
     *
     * @param <T> the generic type
     */
    private static class GenericIterator<T> extends MeshIterator<T> {

        /** The child. */
        private MeshIterator<T> child;

        /** The array. */
        private Object[] array;


        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         */
        private GenericIterator(Object[] data) {
            this(data, new int[ArrayUtil.getRank(data)], ArrayUtil.getShape(data));     
        }

        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         * @param depth the depth
         * @throws IllegalArgumentException the illegal argument exception
         */
        private GenericIterator(Object[] data, int depth) throws IllegalArgumentException {
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
        private GenericIterator(Object[] data, int[] from, int[] to) throws IllegalArgumentException, IndexOutOfBoundsException {
            super(from, to);
            array = data;

            if(from.length > ArrayUtil.getRank(data) || from.length != to.length) throw new IllegalArgumentException("Iteration indeces are higher rank than array.");
            if(fromIndex < 0 || toIndex > data.length) throw new IndexOutOfBoundsException("Iterator outside of array range.");
            if(toIndex <= fromIndex) throw new IndexOutOfBoundsException("Inverted or empty iterator range.");

            if(from.length > 1) {
                int[] childFrom = Arrays.copyOfRange(from, 1, from.length);
                int[] childTo = Arrays.copyOfRange(to, 1, to.length);

                Object element = array[fromIndex];

                if(element instanceof Object[]) child = new GenericIterator<T>((Object[]) element, childFrom, childTo);
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

        /**
         * Set the next block of data, and propagate down to all children...
         */
        /*
        public void setNextBlock() {
            if(++index[0] < toIndex) {
                child.setArray(array[currentIndex]);
                child.reset();
            }
            else parent.setNextBlock();
        }
         */


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

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove elements from a mesh.");
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
            if(getParent() != null) getParent().index[1] = i;

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





    public static class PrimitiveArrayIterator<T> extends MeshIterator<T> {

        /** The size. */
        private int size;

        /** The d array. */
        private double[] dArray;

        /** The array. */
        private float[] fArray;

        /** The l array. */
        private long[] lArray;

        /** The i array. */
        private int[] iArray;

        /** The s array. */
        private short[] sArray;

        /** The b array. */
        private byte[] bArray;

        /**
         * Instantiates a new primitive array iterator.
         *
         * @param data the data
         */
        public PrimitiveArrayIterator(Object data) {
            setArray(data);
            fromIndex = 0;
            toIndex = size;
            index = new int[1];
            reset();
        }

        /**
         * Instantiates a new primitive array iterator.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        public PrimitiveArrayIterator(Object data, int from, int to) throws IndexOutOfBoundsException {  
            super(new int[] {from}, new int[] {to});

            if(from < 0) throw new IndexOutOfBoundsException("Iterator outside of array range.");
            if(to <= from) throw new IndexOutOfBoundsException("Empty iterator range.");

            setArray(data);

            if(to > size) throw new IndexOutOfBoundsException("Iterator outside of array range.");      

            reset();
        }


        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setArray(java.lang.Object)
         */
        @Override
        public void setArray(Object data) throws IllegalArgumentException {
            if(data instanceof double[]) { type = DOUBLE; dArray = (double[]) data; size = dArray.length; }
            else if(data instanceof float[]) { type = FLOAT; fArray = (float[]) data; size = fArray.length; }
            else if(data instanceof long[]) { type = LONG; lArray = (long[]) data; size = lArray.length; }
            else if(data instanceof int[]) { type = INT; iArray = (int[]) data; size = iArray.length; }
            else if(data instanceof short[]) { type = SHORT; sArray = (short[]) data; size = sArray.length; }
            else if(data instanceof byte[]) { type = BYTE; bArray = (byte[]) data; size = bArray.length; }
            else throw new IllegalArgumentException(data.getClass().getSimpleName() + " is not a primitive type.");
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return currentIndex+1 < toIndex;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        @SuppressWarnings("unchecked")
        public T next() throws NoSuchElementException {
            currentIndex++;

            if(currentIndex < toIndex) setIndex(currentIndex);
            else throw new NoSuchElementException("Reached end of iterator range at " + currentIndex + ".");

            switch(type) {
            case DOUBLE: return (T) new Double(dArray[currentIndex]);
            case FLOAT: return (T) new Float(fArray[currentIndex]);
            case LONG: return (T) new Long(lArray[currentIndex]);
            case INT: return (T) new Integer(iArray[currentIndex]);
            case SHORT: return (T) new Short(sArray[currentIndex]);
            case BYTE: return (T) new Byte(bArray[currentIndex]);
            }
            return null;
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setElement(java.lang.Object)
         */
        @Override
        public void setElement(T element) {     
            Number number = (Number) element;
            switch(type) {
            case DOUBLE: dArray[currentIndex] = number.doubleValue(); break;
            case FLOAT: fArray[currentIndex] = number.floatValue(); break;
            case LONG: lArray[currentIndex] = number.longValue(); break;
            case INT: iArray[currentIndex] = number.intValue(); break;
            case SHORT: sArray[currentIndex] = number.shortValue(); break;
            case BYTE: bArray[currentIndex] = number.byteValue(); break;
            }

        }

        // Not implemented.
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#reset()
         */
        @Override
        public void reset() {
            setIndex(fromIndex-1);      
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int)
         */
        @Override
        protected void setIndex(int i) {
            currentIndex = i;
            index[0] = i;
            if(getParent() != null) getParent().index[1] = i;
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int[], int)
         */
        @Override
        protected void setIndex(int[] index, int depth) {
            setIndex(index[depth]);     
        }

        /** The type. */
        private int type;

        /** The Constant DOUBLE. */
        private final static int DOUBLE = 1;

        /** The Constant FLOAT. */
        private final static int FLOAT = 2;

        /** The Constant LONG. */
        private final static int LONG = 3;

        /** The Constant INT. */
        private final static int INT = 4;

        /** The Constant SHORT. */
        private final static int SHORT = 5;

        /** The Constant BYTE. */
        private final static int BYTE = 6;

        /* (non-Javadoc)
         * @see kovacs.data.DataManager#getData()
         */
        @Override
        public Object getData() {
            switch(type) {
            case DOUBLE: return dArray;
            case FLOAT: return fArray;
            case LONG: return lArray;
            case INT: return iArray;
            case SHORT: return sArray;
            case BYTE: return bArray;
            default: return null;   
            }
        }

    }





}   





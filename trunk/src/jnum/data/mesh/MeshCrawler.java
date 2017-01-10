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

import java.util.Arrays;
import java.util.NoSuchElementException;

import jnum.data.ArrayUtil;
import jnum.data.DataCrawler;



/**
 * An abstract class for crawling multidimensional arrays. 
 *
 * @param <T> the generic type
 */
public abstract class MeshCrawler<T> implements DataCrawler<T> {
   
    /** The to top-level index and its range. */
    protected int currentIndex, fromIndex, toIndex;

    /**
     * Instantiates a new array iterator.
     */
    protected MeshCrawler() {}


    protected void setLeadingRange(int from, int to) throws IndexOutOfBoundsException {
        if(from < 0) throw new IndexOutOfBoundsException("Iterator outside of array range.");
        if(to <= from) throw new IndexOutOfBoundsException("Empty iterator range.");
        
        this.fromIndex = from;
        this.toIndex = to;
    }
    
   
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
    protected abstract void setArray(Object data) throws IllegalArgumentException; 

    /* (non-Javadoc)
     * @see kovacs.data.DataManager#setElement(java.lang.Object)
     */
    @Override
    public abstract void setCurrent(T element);
    
    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from a mesh.");
    }

    /**
     * Sets the next element.
     *
     * @param element the new next element
     * @throws NoSuchElementException the no such element exception
     */
    public void setNext(T element) throws NoSuchElementException {
        next();
        setCurrent(element);
    }

    /**
     * Sets the index.
     *
     * @param index the new index
     */
    protected abstract void setLeadPosition(int index);

   
    /**
     * Sets the index.
     *
     * @param index the index
     * @param depth the depth
     */
    protected abstract void setPosition(int[] index, int depth);

    public void getPosition(int[] index) { 
        getPosition(index, 0);   
    }
      
    protected void getPosition(int[] index, int depth) {
        index[depth] = currentIndex;
    }

    /**
     * Iterator.
     *
     * @param <T> the generic type
     * @param array the array
     * @return the array iterator
     */
    @SuppressWarnings("unchecked")
    public static <T> MeshCrawler<T> createFor(Object array) {
        if(array instanceof Object[]) return new GenericCrawler<T>((Object[]) array);
        return (ArrayCrawler<T>) ArrayCrawler.forArray(array);       
    }

    /**
     * Iterator.
     *
     * @param <T> the generic type
     * @param array the array
     * @param depth the depth
     * @return the array iterator
     */
    public static <T> MeshCrawler<T> createFor(Object array, int depth) {
        if(depth >= ArrayUtil.getRank(array)) return createFor(array);
        else return new GenericCrawler<T>((Object[]) array, depth);
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
    @SuppressWarnings("unchecked")
    public static <T> MeshCrawler<T> createFor(Object array, int[] fromIndex, int[] toIndex) {
        if(array instanceof Object[]) return new GenericCrawler<T>((Object[]) array, fromIndex, toIndex);
        return (ArrayCrawler<T>) ArrayCrawler.forArray(array, fromIndex[0], toIndex[0]);
    }   




    /**
     * The Class ObjectMesh.Iterator.
     *
     * @param <T> the generic type
     */
    private static class GenericCrawler<T> extends MeshCrawler<T> {

        /** The child. */
        private MeshCrawler<T> child;

        /** The array. */
        private Object[] array;


        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         */
        private GenericCrawler(Object[] data) {
            this(data, new int[ArrayUtil.getRank(data)], ArrayUtil.getShape(data));     
        }

        /**
         * Instantiates a new object array iterator.
         *
         * @param data the data
         * @param depth the depth
         * @throws IllegalArgumentException the illegal argument exception
         */
        private GenericCrawler(Object[] data, int depth) throws IllegalArgumentException {
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
        @SuppressWarnings("unchecked")
        private GenericCrawler(Object[] data, int[] from, int[] to) throws IllegalArgumentException, IndexOutOfBoundsException {   
            if(from.length > ArrayUtil.getRank(data) || from.length != to.length) throw new IllegalArgumentException("Iteration indeces are higher rank than array.");
            
            setArray(data);
            setLeadingRange(from[0], to[0]);
            array = data;

            if(from.length > 1) {
                Object element = array[fromIndex];

                if(element instanceof Object[]) {
                    int[] childFrom = Arrays.copyOfRange(from, 1, from.length);
                    int[] childTo = Arrays.copyOfRange(to, 1, to.length);
                    child = new GenericCrawler<T>((Object[]) element, childFrom, childTo);
                }
                else {
                    child = (ArrayCrawler<T>) ArrayCrawler.forArray(element, from[1], to[1]);
                }
            }

            reset();
        }
        
        @Override
        public void setLeadingRange(int from, int to) throws IndexOutOfBoundsException {
            if(to > array.length) throw new IndexOutOfBoundsException("Iterator outside of array range.");  
            super.setLeadingRange(from, to);
        }

        @Override
        protected void getPosition(int[] index, int depth) {
            super.getPosition(index, depth);
            if(child != null) child.getPosition(index, depth+1);
        }
        
        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#reset()
         */
        @Override
        public void reset() {
            if(child == null) setLeadPosition(fromIndex-1);
            else {
                setLeadPosition(fromIndex);
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
                if(currentIndex < toIndex) setLeadPosition(currentIndex);
                else throw new NoSuchElementException("Reached end of iterator range at " + currentIndex + ".");
                return (T) array[currentIndex];
            }
            else {
                try { return child.next(); }
                catch(NoSuchElementException e) {
                    if(currentIndex+1 < toIndex) {
                        setLeadPosition(++currentIndex);
                        return child.next();
                    }
                    else throw e;
                }
            }
        }

     
        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setElement(java.lang.Object)
         */
        @Override
        public void setCurrent(T element) {
            if(child == null) array[currentIndex] = element;
            else child.setCurrent(element);     
        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int[], int)
         */
        @Override
        protected void setPosition(int[] index, int pos) {
            setLeadPosition(index[pos]);
            if(child != null) {
                child.setArray(array[currentIndex]);
                child.setPosition(index, pos+1);
            }

        }

        /* (non-Javadoc)
         * @see kovacs.data.ArrayIterator#setIndex(int)
         */
        @Override
        protected void setLeadPosition(int i) {
            currentIndex = i;
       
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

    
}   





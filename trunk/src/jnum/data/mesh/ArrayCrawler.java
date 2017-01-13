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

import java.util.NoSuchElementException;


/**
 * A class for crawling primitive 1-D Java arrays. 
 *
 * @param <T> the generic type
 */
public abstract class ArrayCrawler<T> extends MeshCrawler<T> {

    /**
     * Instantiates a new primitive array iterator.
     *
     * @param data the data
     */
    public ArrayCrawler(Object data) {
        setArray(data);
        setLeadingRange(0, size());
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
    public ArrayCrawler(Object data, int from, int to) throws IndexOutOfBoundsException {  
        setArray(data);
        setLeadingRange(from, to);       
        reset();
    }
    
    /* (non-Javadoc)
     * @see jnum.data.MeshCrawler#setLeadingRange(int, int)
     */
    @Override
    public void setLeadingRange(int from, int to) throws IndexOutOfBoundsException {
        if(to > size()) throw new IndexOutOfBoundsException("Iterator outside of array range.");  
        super.setLeadingRange(from, to);
    }

    /**
     * Size.
     *
     * @return the int
     */
    public abstract int size();

    /**
     * Gets the element.
     *
     * @return the element
     */
    abstract T getElement();

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
    public T next() throws NoSuchElementException {
        currentIndex++;

        if(currentIndex < toIndex) setLeadPosition(currentIndex);
        else throw new NoSuchElementException("Iterator out of range at index " + currentIndex + ".");

        return getElement();
    }

    /* (non-Javadoc)
     * @see kovacs.data.ArrayIterator#reset()
     */
    @Override
    public void reset() {
        setLeadPosition(fromIndex-1);      
    }

    /* (non-Javadoc)
     * @see kovacs.data.ArrayIterator#setIndex(int)
     */
    @Override
    protected void setLeadPosition(int i) {
        currentIndex = i;
    }
    
    /* (non-Javadoc)
     * @see kovacs.data.ArrayIterator#setIndex(int[], int)
     */
    @Override
    protected void setPosition(int[] index, int depth) {
        setLeadPosition(index[depth]);     
    }

    /**
     * For array.
     *
     * @param data the data
     * @return the array crawler
     */
    static ArrayCrawler<?> forArray(Object data) {
        // In order of commonality / size for faster lookups in expected usage scenarios...
        if(data instanceof float[]) return new FloatArrayCrawler((float[]) data);
        else if(data instanceof int[]) return new IntArrayCrawler((int[]) data);
        else if(data instanceof double[]) return new DoubleArrayCrawler((double[]) data);
        else if(data instanceof boolean[]) return new BooleanArrayCrawler((boolean[]) data);
        else if(data instanceof byte[]) return new ByteArrayCrawler((byte[]) data);
        else if(data instanceof short[]) return new ShortArrayCrawler((short[]) data);
        else if(data instanceof long[]) return new LongArrayCrawler((long[]) data);
        else if(data instanceof char[]) return new CharArrayCrawler((char[]) data);
        return null;
    }         
    
    /**
     * For array.
     *
     * @param data the data
     * @param from the from
     * @param to the to
     * @return the array crawler
     */
    static ArrayCrawler<?> forArray(Object data, int from, int to) {
        // In order of commonality / size for faster lookups in expected usage scenarios...
        if(data instanceof float[]) return new FloatArrayCrawler((float[]) data, from, to);
        else if(data instanceof int[]) return new IntArrayCrawler((int[]) data, from, to);
        else if(data instanceof double[]) return new DoubleArrayCrawler((double[]) data, from, to);
        else if(data instanceof boolean[]) return new BooleanArrayCrawler((boolean[]) data, from, to);
        else if(data instanceof byte[]) return new ByteArrayCrawler((byte[]) data, from, to);
        else if(data instanceof short[]) return new ShortArrayCrawler((short[]) data, from, to);
        else if(data instanceof long[]) return new LongArrayCrawler((long[]) data, from, to);
        else if(data instanceof char[]) return new CharArrayCrawler((char[]) data, from, to);
        return null;
    }     



    /**
     * The Class DoubleArrayCrawler.
     */
    private static class DoubleArrayCrawler extends ArrayCrawler<Double> {
        
        /** The data. */
        private double[] data;

        /**
         * Instantiates a new double array crawler.
         *
         * @param data the data
         */
        private DoubleArrayCrawler(double[] data) {
            super(data);
        }

        /**
         * Instantiates a new double array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private DoubleArrayCrawler(double[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Double getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Double element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (double[]) data;
        }
    }


    /**
     * The Class FloatArrayCrawler.
     */
    private static class FloatArrayCrawler extends ArrayCrawler<Float> {
        
        /** The data. */
        private float[] data;

        /**
         * Instantiates a new float array crawler.
         *
         * @param data the data
         */
        FloatArrayCrawler(float[] data) {
            super(data);
        }

        /**
         * Instantiates a new float array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        FloatArrayCrawler(float[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Float getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Float element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (float[]) data;
        }
    }

    /**
     * The Class LongArrayCrawler.
     */
    private static class LongArrayCrawler extends ArrayCrawler<Long> {
        
        /** The data. */
        private long[] data;

        /**
         * Instantiates a new long array crawler.
         *
         * @param data the data
         */
        private LongArrayCrawler(long[] data) {
            super(data);
        }

        /**
         * Instantiates a new long array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private LongArrayCrawler(long[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Long getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Long element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (long[]) data;
        }
    }

    /**
     * The Class IntArrayCrawler.
     */
    private static class IntArrayCrawler extends ArrayCrawler<Integer> {
        
        /** The data. */
        private int[] data;

        /**
         * Instantiates a new int array crawler.
         *
         * @param data the data
         */
        private IntArrayCrawler(int[] data) {
            super(data);
        }

        /**
         * Instantiates a new int array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private IntArrayCrawler(int[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Integer getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Integer element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (int[]) data;
        }
    }

    /**
     * The Class ShortArrayCrawler.
     */
    private static class ShortArrayCrawler extends ArrayCrawler<Short> {
        
        /** The data. */
        private short[] data;

        /**
         * Instantiates a new short array crawler.
         *
         * @param data the data
         */
        private ShortArrayCrawler(short[] data) {
            super(data);
        }

        /**
         * Instantiates a new short array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private ShortArrayCrawler(short[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Short getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Short element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (short[]) data;
        }
    }

    /**
     * The Class CharArrayCrawler.
     */
    private static class CharArrayCrawler extends ArrayCrawler<Character> {
        
        /** The data. */
        private char[] data;

        /**
         * Instantiates a new char array crawler.
         *
         * @param data the data
         */
        public CharArrayCrawler(char[] data) {
            super(data);
        }

        /**
         * Instantiates a new char array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        public CharArrayCrawler(char[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Character getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Character element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (char[]) data;
        }
    }

    /**
     * The Class ByteArrayCrawler.
     */
    private static class ByteArrayCrawler extends ArrayCrawler<Byte> {
        
        /** The data. */
        private byte[] data;

        /**
         * Instantiates a new byte array crawler.
         *
         * @param data the data
         */
        private ByteArrayCrawler(byte[] data) {
            super(data);
        }

        /**
         * Instantiates a new byte array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private ByteArrayCrawler(byte[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Byte getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Byte element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (byte[]) data;
        }
    }

    /**
     * The Class BooleanArrayCrawler.
     */
    private static class BooleanArrayCrawler extends ArrayCrawler<Boolean> {
        
        /** The data. */
        private boolean[] data;

        /**
         * Instantiates a new boolean array crawler.
         *
         * @param data the data
         */
        private BooleanArrayCrawler(boolean[] data) {
            super(data);
        }

        /**
         * Instantiates a new boolean array crawler.
         *
         * @param data the data
         * @param from the from
         * @param to the to
         * @throws IndexOutOfBoundsException the index out of bounds exception
         */
        private BooleanArrayCrawler(boolean[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#size()
         */
        @Override
        public final int size() { return data.length; }

        /* (non-Javadoc)
         * @see jnum.data.ArrayCrawler#getElement()
         */
        @Override
        final Boolean getElement() { return data[currentIndex]; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setCurrent(java.lang.Object)
         */
        @Override
        public final void setCurrent(Boolean element) { data[currentIndex] = element; }

        /* (non-Javadoc)
         * @see jnum.data.DataCrawler#getData()
         */
        @Override
        public final Object getData() { return data; }

        /* (non-Javadoc)
         * @see jnum.data.MeshCrawler#setArray(java.lang.Object)
         */
        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (boolean[]) data;
        }
    }

  
}

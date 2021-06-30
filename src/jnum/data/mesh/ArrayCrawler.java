/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.mesh;

import java.util.NoSuchElementException;


/**
 * A class for crawling primitive 1-D Java arrays. 
 *
 * @param <T> the generic type
 */
public abstract class ArrayCrawler<T> extends MeshCrawler<T> {

    public ArrayCrawler(Object data) {
        setArray(data);
        setLeadingRange(0, size());
        reset();
    }


    public ArrayCrawler(Object data, int from, int to) throws IndexOutOfBoundsException {  
        setArray(data);
        setLeadingRange(from, to);       
        reset();
    }

    @Override
    public void setLeadingRange(int from, int to) throws IndexOutOfBoundsException {
        if(to > size()) throw new IndexOutOfBoundsException("Iterator outside of array range.");  
        super.setLeadingRange(from, to);
    }

    public abstract int size();

    abstract T getElement();

    @Override
    public boolean hasNext() {
        return currentIndex+1 < toIndex;
    }

    @Override
    public T next() throws NoSuchElementException {
        currentIndex++;

        if(currentIndex < toIndex) setLeadPosition(currentIndex);
        else throw new NoSuchElementException("Iterator out of range at index " + currentIndex + ".");

        return getElement();
    }

    @Override
    public void reset() {
        setLeadPosition(fromIndex-1);      
    }

    @Override
    protected void setLeadPosition(int i) {
        currentIndex = i;
    }

    @Override
    protected void setPosition(int[] index, int depth) {
        setLeadPosition(index[depth]);     
    }

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




    private static class DoubleArrayCrawler extends ArrayCrawler<Double> {
        

        private double[] data;

        private DoubleArrayCrawler(double[] data) {
            super(data);
        }

        private DoubleArrayCrawler(double[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Double getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Double element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (double[]) data;
        }
    }



    private static class FloatArrayCrawler extends ArrayCrawler<Float> {

        private float[] data;

        FloatArrayCrawler(float[] data) {
            super(data);
        }

        FloatArrayCrawler(float[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Float getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Float element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (float[]) data;
        }
    }


    private static class LongArrayCrawler extends ArrayCrawler<Long> {

        private long[] data;

        private LongArrayCrawler(long[] data) {
            super(data);
        }

        private LongArrayCrawler(long[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Long getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Long element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (long[]) data;
        }
    }


    private static class IntArrayCrawler extends ArrayCrawler<Integer> {

        private int[] data;

        private IntArrayCrawler(int[] data) {
            super(data);
        }

        private IntArrayCrawler(int[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }
 
        @Override
        public final int size() { return data.length; }

        @Override
        final Integer getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Integer element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (int[]) data;
        }
    }


    private static class ShortArrayCrawler extends ArrayCrawler<Short> {

        private short[] data;

        private ShortArrayCrawler(short[] data) {
            super(data);
        }

        private ShortArrayCrawler(short[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Short getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Short element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (short[]) data;
        }
    }


    private static class CharArrayCrawler extends ArrayCrawler<Character> {

        private char[] data;

        public CharArrayCrawler(char[] data) {
            super(data);
        }

        public CharArrayCrawler(char[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Character getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Character element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (char[]) data;
        }
    }


    private static class ByteArrayCrawler extends ArrayCrawler<Byte> {

        private byte[] data;

        private ByteArrayCrawler(byte[] data) {
            super(data);
        }

        private ByteArrayCrawler(byte[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Byte getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Byte element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (byte[]) data;
        }
    }

    
    
    private static class BooleanArrayCrawler extends ArrayCrawler<Boolean> {

        private boolean[] data;

        private BooleanArrayCrawler(boolean[] data) {
            super(data);
        }

        private BooleanArrayCrawler(boolean[] data, int from, int to) throws IndexOutOfBoundsException {
            super(data, from, to);
        }

        @Override
        public final int size() { return data.length; }

        @Override
        final Boolean getElement() { return data[currentIndex]; }

        @Override
        public final void setCurrent(Boolean element) { data[currentIndex] = element; }

        @Override
        public final Object getData() { return data; }

        @Override
        public final void setArray(Object data) throws IllegalArgumentException {
            this.data = (boolean[]) data;
        }
    }

  
}

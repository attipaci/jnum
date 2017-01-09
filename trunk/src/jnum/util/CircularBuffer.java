/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;


// TODO: Auto-generated Javadoc
/**
 * The Class CircularBuffer.
 *
 * @param <Type> the generic type
 */
public class CircularBuffer<Type> implements Iterable<Type>, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3827954748223288982L;
	
	/** The data. */
	private Type[] data;
	
	/** The last index. */
	private int lastIndex = -1;
	
	/** The is filled. */
	private boolean isFilled = false;

	
	/**
	 * Instantiates a new circular buffer.
	 *
	 * @param backingBuffer the backing buffer
	 */
	public CircularBuffer(Type[] backingBuffer) {
		data = backingBuffer;
		Arrays.fill(data, null); 
	}
	
	/**
	 * Unwrap to.
	 *
	 * @param buffer the buffer
	 */
	public synchronized void unwrapTo(Type[] buffer) {
		if(buffer.length != data.length) throw new IllegalArgumentException("Cannot unwrap to array of different size.");
		final int n = lastIndex + 1;
		
		System.arraycopy(data, n, buffer, 0, data.length - n);
		System.arraycopy(data, 0, buffer, data.length - n, n);
	}
	
	/**
	 * Put.
	 *
	 * @param value the value
	 */
	public synchronized void put(Type value) {	
		lastIndex++;
		if(lastIndex == data.length) {
			lastIndex = 0;
			isFilled = true;
		}
		data[lastIndex] = value;
		
		notifyAll();
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public final boolean isEmpty() {
		return lastIndex < 0;
	}
	
	/**
	 * Checks if is filled.
	 *
	 * @return true, if is filled
	 */
	public final boolean isFilled() {
		return isFilled;
	}
	
	/**
	 * Clear.
	 */
	public synchronized void clear() {
		Arrays.fill(data, null);
		lastIndex = -1;
		isFilled = false;
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public final synchronized int size() {
		if(isFilled) return data.length;
		else return lastIndex + 1;
	}
	
	/**
	 * Capacity.
	 *
	 * @return the int
	 */
	public final int capacity() {
		return data.length;
	}
	
	/**
	 * At raw index.
	 *
	 * @param index the index
	 * @return the type
	 */
	public final Type atRawIndex(int index) {
		return data[index];
	}
	
	/**
	 * Underlying index of last.
	 *
	 * @return the int
	 */
	// The underlying index of the last entry or -1 if the buffer is empty.
	public final synchronized int underlyingIndexOfLast() {
		return lastIndex;
	}
	
	/**
	 * Underlying index of first.
	 *
	 * @return the int
	 */
	// The underlying index of the first entry or -1 if the buffer is empty.
	public final synchronized int underlyingIndexOfFirst() {
		if(isEmpty()) return -1;
		final int firstIndex = isFilled ? lastIndex + 1 : 0;
		return firstIndex == data.length ? 0 : firstIndex;
	}
	
	/**
	 * Gets the last.
	 *
	 * @return the last
	 */
	public final synchronized Type getLast() { 
		if(isEmpty()) return null;
		return data[underlyingIndexOfLast()];
	}
	
	/**
	 * Gets the first.
	 *
	 * @return the first
	 */
	public final synchronized Type getFirst() {
		if(isEmpty()) return null;
		return data[underlyingIndexOfFirst()];
	}
	
		
	/**
	 * To underlying index.
	 *
	 * @param i the i
	 * @return the int
	 */
	public final synchronized int toUnderlyingIndex(int i) {
		if(i < 0 || i >= size()) throw new ArrayIndexOutOfBoundsException("index " + i + "; size " + size());
		i += underlyingIndexOfFirst();
		if(i >= capacity()) i -= capacity();
		return i;
	}
	
	/**
	 * Reverse to underlying index.
	 *
	 * @param i the i
	 * @return the int
	 */
	public final synchronized int reverseToUnderlyingIndex(int i) {
		if(i < 0 || i >= size()) throw new ArrayIndexOutOfBoundsException("index " + i + "; size " + size());
		i = underlyingIndexOfLast() - i;
		if(i < 0) i += capacity();
		return i;
	}
		
	/**
	 * Gets the.
	 *
	 * @param i the i
	 * @return the type
	 */
	public final synchronized Type get(int i) {
		return data[toUnderlyingIndex(i)];
	}
	
	/**
	 * Gets the reverse.
	 *
	 * @param i the i
	 * @return the reverse
	 */
	public final synchronized Type getReverse(int i) {
		return data[reverseToUnderlyingIndex(i)];
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Type> iterator() {
		return new Iterator<Type>() {
			private int i = 0;
			private int j = underlyingIndexOfFirst();

			@Override
			public boolean hasNext() { return i < size(); }

			@Override
			public Type next() { 
				i++;
				if(j == capacity()) j = 0;
				return data[j++];
			}

			@Override
			public void remove() {
				int k = j-1;
				if(k < 0) k = capacity() - 1;
				data[k] = null;
			}
		};
	}
	
	/**
	 * Lock.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public synchronized void lock() throws InterruptedException {
		wait();
	}
	
	/**
	 * Release.
	 */
	public synchronized void release() {
		notifyAll();
	}
	
	/**
	 * Reverse iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<Type> reverseIterator() {
		return new Iterator<Type>() {
			private int i = 0;
			private int j = underlyingIndexOfLast();

			@Override
			public boolean hasNext() { return i < size(); }

			@Override
			public Type next() { 
				i++;
				if(j == 0) j = capacity() - 1;
				return data[j--];
			}

			@Override
			public void remove() {
				int k = j+1;
				if(k == capacity()) k = 0;
				data[k] = null;
			}
		};
	}
	
	
}

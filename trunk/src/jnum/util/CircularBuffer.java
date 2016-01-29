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
package jnum.util;

import java.util.Arrays;
import java.util.Iterator;


public class CircularBuffer<Type> implements Iterable<Type> {
	private Type[] data;
	private int lastIndex = -1;
	private boolean isFilled = false;

	
	public CircularBuffer(Type[] backingBuffer) {
		data = backingBuffer;
		Arrays.fill(data, null); 
	}
	
	public synchronized void unwrapTo(Type[] buffer) {
		if(buffer.length != data.length) throw new IllegalArgumentException("Cannot unwrap to array of different size.");
		final int n = lastIndex + 1;
		
		System.arraycopy(data, n, buffer, 0, data.length - n);
		System.arraycopy(data, 0, buffer, data.length - n, n);
	}
	
	public synchronized void put(Type value) {	
		lastIndex++;
		if(lastIndex == data.length) {
			lastIndex = 0;
			isFilled = true;
		}
		data[lastIndex] = value;
		
		notifyAll();
	}
	
	public final boolean isEmpty() {
		return lastIndex < 0;
	}
	
	public final boolean isFilled() {
		return isFilled;
	}
	
	public synchronized void clear() {
		Arrays.fill(data, null);
		lastIndex = -1;
		isFilled = false;
	}
	
	public final synchronized int size() {
		if(isFilled) return data.length;
		else return lastIndex + 1;
	}
	
	public final int capacity() {
		return data.length;
	}
	
	public final Type atRawIndex(int index) {
		return data[index];
	}
	
	// The underlying index of the last entry or -1 if the buffer is empty.
	public final synchronized int underlyingIndexOfLast() {
		return lastIndex;
	}
	
	// The underlying index of the first entry or -1 if the buffer is empty.
	public final synchronized int underlyingIndexOfFirst() {
		if(isEmpty()) return -1;
		final int firstIndex = isFilled ? lastIndex + 1 : 0;
		return firstIndex == data.length ? 0 : firstIndex;
	}
	
	public final synchronized Type getLast() { 
		if(isEmpty()) return null;
		return data[underlyingIndexOfLast()];
	}
	
	public final synchronized Type getFirst() {
		if(isEmpty()) return null;
		return data[underlyingIndexOfFirst()];
	}
	
		
	public final synchronized int toUnderlyingIndex(int i) {
		if(i < 0 || i >= size()) throw new ArrayIndexOutOfBoundsException("index " + i + "; size " + size());
		i += underlyingIndexOfFirst();
		if(i >= capacity()) i -= capacity();
		return i;
	}
	
	public final synchronized int reverseToUnderlyingIndex(int i) {
		if(i < 0 || i >= size()) throw new ArrayIndexOutOfBoundsException("index " + i + "; size " + size());
		i = underlyingIndexOfLast() - i;
		if(i < 0) i += capacity();
		return i;
	}
		
	public final synchronized Type get(int i) {
		return data[toUnderlyingIndex(i)];
	}
	
	public final synchronized Type getReverse(int i) {
		return data[reverseToUnderlyingIndex(i)];
	}
	
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
	
	public synchronized void lock() throws InterruptedException {
		wait();
	}
	
	public synchronized void release() {
		notifyAll();
	}
	
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

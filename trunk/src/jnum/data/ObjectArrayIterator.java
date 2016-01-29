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


package jnum.data;

import java.util.Arrays;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectArrayIterator.
 *
 * @param <T> the generic type
 */
public class ObjectArrayIterator<T> extends ArrayIterator<T> {
	
	/** The child. */
	private ArrayIterator<T> child;
	
	/** The array. */
	private Object[] array;
	
	
	/**
	 * Instantiates a new object array iterator.
	 *
	 * @param data the data
	 */
	public ObjectArrayIterator(Object[] data) {
		this(data, new int[ArrayUtil.getRank(data)], ArrayUtil.getShape(data));		
	}
	
	/**
	 * Instantiates a new object array iterator.
	 *
	 * @param data the data
	 * @param depth the depth
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public ObjectArrayIterator(Object[] data, int depth) throws IllegalArgumentException {
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
	public ObjectArrayIterator(Object[] data, int[] from, int[] to) throws IllegalArgumentException, IndexOutOfBoundsException {
		super(from, to);
		array = data;
		
		if(from.length > ArrayUtil.getRank(data) || from.length != to.length) throw new IllegalArgumentException("Iteration indeces are higher rank than array.");
		if(fromIndex < 0 || toIndex > data.length) throw new IndexOutOfBoundsException("Iterator outside of array range.");
		if(toIndex <= fromIndex) throw new IndexOutOfBoundsException("Inverted or empty iterator range.");
		
		if(from.length > 1) {
			int[] childFrom = Arrays.copyOfRange(from, 1, from.length);
			int[] childTo = Arrays.copyOfRange(to, 1, to.length);
	
			Object element = array[fromIndex];
		
			if(element instanceof Object[]) child = new ObjectArrayIterator<T>((Object[]) element, childFrom, childTo);
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

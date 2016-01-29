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

import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Class PrimitiveArrayIterator.
 *
 * @param <T> the generic type
 */
public class PrimitiveArrayIterator<T> extends ArrayIterator<T> {
	
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
		super(new int[] {from}, new int[] { to});
		
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
		if(parent != null) parent.index[1] = i;
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
	
	

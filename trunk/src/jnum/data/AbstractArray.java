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


import java.io.Serializable;
import java.util.*;

import jnum.Copiable;
import jnum.Function;
import jnum.text.ParseType;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractArray.
 *
 * @param <T> the generic type
 */
public abstract class AbstractArray<T> implements Serializable, Cloneable, Copiable<AbstractArray<T>>, Iterable<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1935368290016760524L;

	/** The data. */
	protected Object data;
	
	/** The type. */
	protected Class<T> type;
	
	/** The size. */
	private int[] size;
	
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param type the type
	 */
	public AbstractArray(Class<T> type) {
		this.type = type;
	}
	
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param data the data
	 */
	public AbstractArray(Object data) {
		setData(data);
	}

	// Returns an uninitialized array. Call initialize(), if want to fill with default elements.
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 */
	public AbstractArray(Class<T> type, int[] dimensions) {
		this.type = type;
		size = Arrays.copyOf(dimensions, dimensions.length);
		data = ArrayUtil.createArray(type, dimensions);
	}
	
	/**
	 * Initialize.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public void initialize() throws InstantiationException, IllegalAccessException {
		ArrayUtil.initialize(data);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.Copiable#copy()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AbstractArray<T> copy() {
		AbstractArray<T> copy = (AbstractArray<T>) clone();
		try { copy.data = ArrayUtil.copy(data); }
		catch(Exception e) { 
			copy.data = null; 
			e.printStackTrace();
		}
		copy.size = Arrays.copyOf(size, size.length);
		return copy();
	}
	
	/**
	 * Sets the size.
	 *
	 * @param dimensions the new size
	 */
	public void setSize(int[] dimensions) { 
		data = ArrayUtil.createArray(type, dimensions);
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	@SuppressWarnings("unchecked")
	public void setData(Object data) {
		this.data = data;
		type = (Class<T>) ArrayUtil.getClass(data);
		size = ArrayUtil.getShape(data);
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public final Class<T> getType() { return type; }

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public final Object getData() { return data; }

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public final int[] getSize() { return size; }
	
	/**
	 * Gets the size.
	 *
	 * @param dim the dim
	 * @return the size
	 */
	public final int getSize(int dim) { return size[dim]; }
	
	/**
	 * Gets the dimension.
	 *
	 * @return the dimension
	 */
	public final int getDimension() { return size.length; }
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public ArrayIterator<T> iterator() { return ArrayUtil.iterator(data); }

	/**
	 * Sets the element at.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public final void setElementAt(int[] index, T value) {
		setLineElementAt(rawSubArrayAt(index), index[index.length-1], value);
	}

	/**
	 * Element at.
	 *
	 * @param index the index
	 * @return the t
	 */
	public T elementAt(int[] index) {
		return lineElementAt(rawSubArrayAt(index), index[index.length-1]);
	}
	
	/**
	 * Raw sub array at.
	 *
	 * @param index the index
	 * @return the object
	 */
	public Object rawSubArrayAt(int[] index) {
		Object subarray = data;
		int depth = index.length;
		if(depth > size.length) throw new IllegalArgumentException("Index dimension exceeds that of the array.");
		if(depth == size.length) depth--;
		for(int i=0; i<depth; i++) subarray = ((Object[]) subarray)[index[i]];
		return depth == index.length ? subarray : lineElementAt(subarray, index[depth]);		
	}
	
	/**
	 * Line element at.
	 *
	 * @param linearArray the linear array
	 * @param index the index
	 * @return the t
	 */
	protected abstract T lineElementAt(Object linearArray, int index); 

	/**
	 * Sets the line element at.
	 *
	 * @param linearArray the linear array
	 * @param index the index
	 * @param value the value
	 */
	protected abstract void setLineElementAt(Object linearArray, int index, T value);
	
	/**
	 * Sub array at.
	 *
	 * @param index the index
	 * @return the abstract array
	 */
	public abstract AbstractArray<T> subArrayAt(int[] index); 
	
	// Slim clears private storage...
	/**
	 * Slim.
	 */
	public abstract void slim();

	/**
	 * Adds the patch at.
	 *
	 * @param point the point
	 * @param exactpos the exactpos
	 * @param patchSize the patch size
	 * @param shape the shape
	 */
	public abstract void addPatchAt(T point, double[] exactpos, double[] patchSize, Function<double[], Double> shape);

	/**
	 * Parses the element.
	 *
	 * @param text the text
	 * @return the t
	 * @throws Exception the exception
	 */
	public abstract T parseElement(String text) throws Exception;
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @throws Exception the exception
	 */
	public void parse(String text) throws Exception {
		GenericArray<String> stringArray = parseStringArray(text);
		setSize(stringArray.getSize());
		DataIterator<T> iterator = iterator();
		for(String entry : stringArray) {
			iterator.next();
			iterator.setElement(parseElement(entry));
		}
	}

	/**
	 * Parses the string array.
	 *
	 * @param text the text
	 * @return the generic array
	 */
	public static GenericArray<String> parseStringArray(String text) {	
		return new GenericArray<String>(parseStringArrayData(text));
	}
	
	
	/**
	 * Gets the parses the class.
	 *
	 * @param array the array
	 * @param lowest the lowest
	 * @return the parses the class
	 */
	public static Class<?> getParseClass(GenericArray<String> array, ParseType lowest) {				
		for(String value : array) lowest = ParseType.get(value, lowest);
		return lowest.getType();
	}

		
	/**
	 * Parses the string array data.
	 *
	 * @param text the text
	 * @return the object
	 */
	public static Object parseStringArrayData(String text) {	
		Vector<Object> elements = new Vector<Object>();
		
		// If array, it has to start with a { bracket...
		int from = text.indexOf('{') + 1, to = from;
		// Otherwise it's just a normal entry;
		if(from < 0) return text;
		
		int open = 0, size = text.length();
		
		// parse until reached end or final closing bracket...
		while(to < size && open >= 0) {
			char c = text.charAt(to);
			if(c == '{') open++; 
			else if(c == '}') open--;
			else if(c == ',') if(open == 0) if(to > from) {
				elements.add(parseStringArrayData(text.substring(from, to++)));
				from = to;
			}
		}
		
		return elements.toArray();		
	}
	
}

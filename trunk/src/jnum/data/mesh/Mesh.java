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


import java.io.Serializable;
import java.util.*;

import jnum.Copiable;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.data.DataIterator;
import jnum.text.ParseType;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractArray.
 *
 * @param <T> the generic type
 */
public abstract class Mesh<T> implements Serializable, Cloneable, Copiable<Mesh<T>>, Iterable<T> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1935368290016760524L;

	/** The data. */
	protected Object data;
	
	/** The type. */
	protected Class<T> elementClass;
	
	/** The size. */
	private int[] size;
	
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param elementClass the element class
	 */
	public Mesh(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param data the data
	 */
	public Mesh(Object data) {
		setData(data);
	}
	
	/**
	 * New instance.
	 *
	 * @return the mesh
	 */
	public abstract Mesh<T> newInstance();
	
	// Returns an uninitialized array. Call initialize(), if want to fill with default elements.
	/**
	 * Instantiates a new abstract array.
	 *
	 * @param elementClass the element class
	 * @param dimensions the dimensions
	 */
	public Mesh(Class<T> elementClass, int[] dimensions) {
		this(elementClass);
		try { setSize(dimensions); }
		catch(Exception e) {
		    throw new IllegalArgumentException("Cannot create elements of type: " + elementClass.getSimpleName());
		}
	}
	
	/**
	 * Conforms to.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean conformsTo(Mesh<?> o) {
	    return Arrays.equals(getSize(), o.getSize());
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
	public Mesh<T> copy() {
		Mesh<T> copy = (Mesh<T>) clone();
		try { copy.data = ArrayUtil.copyOf(data); }
		catch(Exception e) { 
			copy.data = null; 
			Util.error(this, e);
		}
		return copy();
	}
	
	/**
	 * Sets the size.
	 *
	 * @param dimensions the new size
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public void setSize(int[] dimensions) throws InstantiationException, IllegalAccessException { 
	    this.size = dimensions;
        data = ArrayUtil.createArray(elementClass, dimensions);
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	@SuppressWarnings("unchecked")
	public void setData(Object data) {
		this.data = data;
		elementClass = (Class<T>) ArrayUtil.getClass(data);
		size = ArrayUtil.getShape(data);
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public final Class<T> getType() { return elementClass; }

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
	public Iterator<T> iterator() { return ArrayUtil.iterator(data); }

	/**
	 * Sets the element at.
	 *
	 * @param index the index
	 * @param value the value
	 */
	public final void setElementAt(int[] index, T value) {
		setLineElementAt(subarrayDataAt(index), index[index.length-1], value);
	}

	/**
	 * Element at.
	 *
	 * @param index the index
	 * @return the t
	 */
	public T elementAt(int[] index) {
		return lineElementAt(subarrayDataAt(index), index[index.length-1]);
	}
	
	/**
	 * Raw sub array at.
	 *
	 * @param index the index
	 * @return the object
	 */
	public Object subarrayDataAt(int[] index) {
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
	public Mesh<T> subarrayAt(int[] index) {
	    Mesh<T> sub = newInstance();
	    sub.setData(subarrayDataAt(index));
	    return sub;
	}
	
	

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
		ObjectMesh<String> stringArray = parseStringArray(text);
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
	public static ObjectMesh<String> parseStringArray(String text) {	
		return new ObjectMesh<String>(parseStringArrayData(text));
	}
	
	
	/**
	 * Gets the parses the class.
	 *
	 * @param array the array
	 * @param lowest the lowest
	 * @return the parses the class
	 */
	public static Class<?> getParseClass(ObjectMesh<String> array, ParseType lowest) {				
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
	
	/**
	 * The Class Mesh.Iterator.
	 *
	 * @param <T> the generic type
	 */
	public abstract static class Iterator<T> implements DataIterator<T> {
	    
	    /** The parent. */
	    ObjectMesh.Iterator<T> parent;
	    
	    /** The to index. */
	    int currentIndex, fromIndex, toIndex;
	    
	    /** The index. */
	    int[] index;
	    
	    /**
	     * Instantiates a new array iterator.
	     */
	    protected Iterator() {}
	    
	    /**
	     * Instantiates a new array iterator.
	     *
	     * @param from the from
	     * @param to the to
	     */
	    public Iterator(int[] from, int[] to) {
	        fromIndex = from[0];
	        toIndex = to[0];
	        index = Arrays.copyOf(from, from.length);
	    }
	    
	    /**
	     * Sets the parent.
	     *
	     * @param iterator the new parent
	     */
	    public void setParent(ObjectMesh.Iterator<T> iterator) {
	        parent = iterator;
	    }
	    
	    /**
	     * Gets the parent.
	     *
	     * @return the parent
	     */
	    public ObjectMesh.Iterator<T> getParent() {
	        return parent;
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
	    
	}   
	

}

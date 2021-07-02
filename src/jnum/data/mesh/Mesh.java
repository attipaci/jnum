/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/


package jnum.data.mesh;


import java.io.Serializable;
import java.util.*;

import jnum.Copiable;
import jnum.NonConformingException;
import jnum.Util;
import jnum.data.ArrayUtil;
import jnum.text.ParseType;


public abstract class Mesh<T> implements Serializable, Cloneable, Copiable<Mesh<T>>, Iterable<T> {

	private static final long serialVersionUID = 1935368290016760524L;

	protected Object data;

	protected Class<T> elementClass;

	private int[] size;
	

	public Mesh(Class<T> elementClass) {
		this.elementClass = elementClass;
	}
	

	public Mesh(Object data) {
		setData(data);
	}
	

	public abstract Mesh<T> newInstance();
	
	// Returns an uninitialized array. Call initialize(), if want to fill with default elements.
	public Mesh(Class<T> elementClass, int... dimensions) {
		this(elementClass);
		try { setSize(dimensions); }
		catch(Exception e) {
		    throw new IllegalArgumentException("Cannot create elements of type: " + elementClass.getSimpleName());
		}
	}
	

	public boolean conformsTo(Mesh<?> o) {
	    return Arrays.equals(getSize(), o.getSize());
	}

	@SuppressWarnings("unchecked")
    @Override
	public Mesh<T> clone() {
		try { return (Mesh<T>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	@Override
	public Mesh<T> copy() {
		Mesh<T> copy = clone();
		try { copy.data = ArrayUtil.copyOf(data); }
		catch(Exception e) { 
			copy.data = null; 
			Util.error(this, e);
		}
		return copy;
	}
	

	public void setSize(int... dimensions) throws InstantiationException, IllegalAccessException { 
	    this.size = dimensions;
        data = ArrayUtil.createArray(elementClass, dimensions);
	}
	

	@SuppressWarnings("unchecked")
	public void setData(Object data) {
		this.data = data;
		elementClass = (Class<T>) ArrayUtil.getClass(data);
		size = ArrayUtil.getShape(data);
	}
	

	public final Class<T> getType() { return elementClass; }


	public final Object getData() { return data; }


	public final int[] getSize() { return size; }
	

	public final int getSize(int dim) { return size[dim]; }
	

	public final int getDimension() { return size.length; }
	

	@Override
	public MeshCrawler<T> iterator() { return MeshCrawler.createFor(data); }
	

    public MeshCrawler<T> iterator(int[] from, int[] to) { return MeshCrawler.createFor(data, from, to); }


	public final void setElementAt(T value, int... index) {
		setLinearElementAt(subarrayDataAt(index), index[index.length-1], value);
	}


	public T elementAt(int... index) {
		return linearElementAt(subarrayDataAt(index), index[index.length-1]);
	}
	

	public Object subarrayDataAt(int... index) {
		Object subarray = data;
		int depth = index.length;
		if(depth > size.length) throw new IllegalArgumentException("Index dimension exceeds that of the array.");
		if(depth == size.length) depth--;
		for(int i=0; i<depth; i++) subarray = ((Object[]) subarray)[index[i]];
		return depth == index.length ? subarray : linearElementAt(subarray, index[depth]);		
	}
	

	protected abstract T linearElementAt(Object linearArray, int index); 


	protected abstract void setLinearElementAt(Object linearArray, int index, T value);
	

	public Mesh<T> subarrayAt(int... index) {
	    Mesh<T> sub = newInstance();
	    sub.setData(subarrayDataAt(index));
	    return sub;
	}
	
	
	public void copyTo(Mesh<T> destination, int... dstOffset) { 
	    if(destination == this) throw new IllegalArgumentException("Cannot copy mesh onto itself");
	    
	    final int dim = getDimension();
	    if(destination.getDimension() != dim) throw new NonConformingException("patch of different dimensionality.");
	       
	    final int[] from = new int[dim];
	    final int[] to = new int[dim];
	    
	    // First figure out the range of destination indices...
	    for(int k=dim; --k >= 0; ) {
	        to[k] = Math.min(destination.getSize(k), dstOffset[k] + getSize(k));
	        from[k] = dstOffset[k] < 0 ? 0 : dstOffset[k];
	    }
	    
	    final MeshCrawler<T> iDst = destination.iterator(from, to);
	    
	    // Now, the range of corresponding source indices...
	    for(int k=dim; --k >= 0; ) {
            from[k] = dstOffset[k] < 0 ? -dstOffset[k] : 0;
            to[k] = to[k] - dstOffset[k];
        }
	    
	    final MeshCrawler<T> iSrc = iterator(from, to);
	    
	    // Do the actual copy...
	    while(iDst.hasNext()) iDst.setNext(iSrc.next()); 
	}
	

	public abstract T parseElement(String text) throws Exception;
	

	public void parse(String text) throws Exception {
		ObjectMesh<String> stringArray = parseStringArray(text);
		setSize(stringArray.getSize());
		MeshCrawler<T> iterator = iterator();
		for(String entry : stringArray) {
			iterator.setNext(parseElement(entry));
		}
	}


	public static ObjectMesh<String> parseStringArray(String text) {	
		return new ObjectMesh<>(parseStringArrayData(text));
	}
	
	
	public static Class<?> getParseClass(ObjectMesh<String> array, ParseType lowest) {				
		for(String value : array) lowest = ParseType.get(value, lowest);
		return lowest.getType();
	}


	public static Object parseStringArrayData(String text) {	
		ArrayList<Object> elements = new ArrayList<>();
		
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

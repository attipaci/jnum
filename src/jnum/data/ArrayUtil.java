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


package jnum.data;

// TODO check where wrappers & private methods are needed. 
// TODO use ValueAt instead of DataCursor?
// TODO arraycopy, fill(from-to), clear(from-to).
// TODO more generalized methods instead of double[][] and float[][] etc.
// TODO smooth(beam), smooth(beam, weight);
// TODO regrid(data, stretch, boolean smooth)
// TODO quick downSample... (smooth only at specific locations, then regrid w/o smooth)
// TODO Check arithmetic to skip NaNs.


// 27.06.07 Tested except regrid, smooth stretch 

import java.lang.reflect.Array;
import java.text.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jnum.Constant;
import jnum.Copiable;
import jnum.Util;
import jnum.ViewableAsDoubles;
import jnum.data.mesh.MeshCrawler;
import jnum.math.AbsoluteValue;
import jnum.math.Additive;
import jnum.math.Complex;
import jnum.math.LinearAlgebra;
import jnum.math.Multiplicative;
import jnum.math.Scalar;
import jnum.math.Scalable;
import jnum.text.BracketedListTokenizer;
import jnum.text.DecimalFormating;
import jnum.text.NumberFormating;
import jnum.text.Parser;

// TODO: Auto-generated Javadoc
// TODO Eliminate calls to getRank(), getClass(), and getDimension() inside recursive methods. 
// Also Arrays.copyOf() and Arrays.copyOfRange() on indeces...
// This should result in improved performance.


/**
 * The Class ArrayUtil.
 */
public final class ArrayUtil {
	
	// Generic Multi-dimensional arrays
	// Creation of arrays with int[] dimensions...
	/**
	 * Gets the rank.
	 *
	 * @param array the array
	 * @return the rank
	 */
	public static int getRank(Object array) {
		if(array == null) return 0;
		else if(array instanceof Object[]) return getRank(((Object[]) array)[0]) + 1;
		else if(array.getClass().isArray()) return 1;
		else return 0;
	}
	
	/**
	 * Gets the shape.
	 *
	 * @param array the array
	 * @return the shape
	 */
	public static int[] getShape(Object array) {
		int[] dimension = new int[getRank(array)];
		for(int i=0; i<dimension.length; i++) {
			dimension[i] = getNextSize(array);
			if(array instanceof Object[]) array = ((Object[]) array)[0];
		}
		return dimension;
	}
	
	/**
	 * Gets the next size.
	 *
	 * @param array the array
	 * @return the next size
	 */
	public static int getNextSize(Object array) {
		if(array instanceof Object[]) return ((Object[]) array).length;
		else if(array instanceof double[]) return ((double[]) array).length;
		else if(array instanceof float[]) return ((float[]) array).length;
		else if(array instanceof long[]) return ((long[]) array).length;
		else if(array instanceof int[]) return ((int[]) array).length;
		else if(array instanceof short[]) return ((short[]) array).length;
		else if(array instanceof byte[]) return ((byte[]) array).length;
		else if(array instanceof boolean[]) return ((boolean[]) array).length;
		else return -1;		
	}
	
	
	/**
	 * Gets the element count.
	 *
	 * @param array the array
	 * @return the element count
	 */
	public static int getElementCount(Object array) {
		int[] dimensions = getShape(array);
		int n = 1;
		for(int i=dimensions.length; --i >= 0; ) n *= dimensions[i];
		return n;
	}
	
	/**
	 * Gets the class.
	 *
	 * @param data the data
	 * @return the class
	 */
	public static Class<?> getClass(Object data) {
		if(data instanceof Object[]) {
			try { return getClass(((Object[]) data)[0]); }
			catch(NullPointerException e) { return data.getClass().getComponentType(); }
		}
		else if(data instanceof double[]) return double.class;
		else if(data instanceof float[]) return float.class;
		else if(data instanceof long[]) return long.class;
		else if(data instanceof int[]) return int.class;
		else if(data instanceof short[]) return short.class;
		else if(data instanceof byte[]) return byte.class;
		else if(data instanceof boolean[]) return boolean.class;
		
		return data.getClass();
	}
	
	/**
	 * Gets the class.
	 *
	 * @param data the data
	 * @param depth the depth
	 * @return the class
	 */
	public static Class<?> getClass(Object data, int depth) {
		if(depth > 0) {
			try { return getClass(((Object[]) data)[0], depth-1); }
			catch(NullPointerException e) { return data.getClass().getComponentType(); }			
		}
		else if(data instanceof double[]) return double.class;
		else if(data instanceof float[]) return float.class;
		else if(data instanceof long[]) return long.class;
		else if(data instanceof int[]) return int.class;
		else if(data instanceof short[]) return short.class;
		else if(data instanceof byte[]) return byte.class;
		else if(data instanceof boolean[]) return boolean.class;
		return data.getClass();		
	}
	
	/**
	 * First element.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object firstElement(Object data) {
		if(data instanceof Object[]) return firstElement(((Object[]) data)[0]);
		else if(data instanceof double[]) return ((double[]) data)[0];
		else if(data instanceof float[]) return ((float[]) data)[0];
		else if(data instanceof long[]) return ((long[]) data)[0];
		else if(data instanceof int[]) return ((int[]) data)[0];
		else if(data instanceof short[]) return ((short[]) data)[0];
		else if(data instanceof byte[]) return ((byte[]) data)[0];
		else if(data instanceof boolean[]) return ((boolean[]) data)[0];
		return data;				
	}
	
	/**
	 * First element.
	 *
	 * @param data the data
	 * @param depth the depth
	 * @return the object
	 */
	public static Object firstElement(Object data, int depth) {
		if(depth > 0) return firstElement(((Object[]) data)[0], depth-1);
		else if(data instanceof double[]) return ((double[]) data)[0];
		else if(data instanceof float[]) return ((float[]) data)[0];
		else if(data instanceof long[]) return ((long[]) data)[0];
		else if(data instanceof int[]) return ((int[]) data)[0];
		else if(data instanceof short[]) return ((short[]) data)[0];
		else if(data instanceof byte[]) return ((byte[]) data)[0];
		else if(data instanceof boolean[]) return ((boolean[]) data)[0];
		return data;				
	}
	
	/**
	 * Value at.
	 *
	 * @param array the array
	 * @param index the index
	 * @return the object
	 */
	public static Object valueAt(Object array, int[] index) {
		return valueAt(array, index, 0); 
	}
	
	/**
	 * Value at.
	 *
	 * @param array the array
	 * @param index the index
	 * @param depth the depth
	 * @return the object
	 */
	public static Object valueAt(Object array, int[] index, int depth) {
		if(array instanceof Object[][]) return valueAt(((Object[]) array)[index[depth]], index, depth++);
		else if(array instanceof Object[]) return ((Object[]) array)[index[depth]];
		else if(array.getClass().isArray()) {
			int k = index[depth];
			if(array instanceof double[]) return ((double[]) array)[k];
			else if(array instanceof float[]) return ((float[]) array)[k];
			else if(array instanceof long[]) return ((long[]) array)[k];
			else if(array instanceof int[]) return ((int[]) array)[k];
			else if(array instanceof short[]) return ((short[]) array)[k];
			else if(array instanceof byte[]) return ((byte[]) array)[k];
			else if(array instanceof boolean[]) return ((boolean[]) array)[k];			
		}
		return array;
	}
	
	/**
	 * Creates the array.
	 *
	 * @param type the type
	 * @param dimension the dimension
	 * @return the object
	 */
	public static Object createArray(Class<?> type, int dimension) {
		return Array.newInstance(type, dimension);
	}
	
	/**
	 * Creates the array.
	 *
	 * @param type the type
	 * @param dimensions the dimensions
	 * @return the object
	 */
	public static Object createArray(Class<?> type, int[] dimensions) {
		return Array.newInstance(type, dimensions);
	}
	
	/**
	 * Initialize.
	 *
	 * @param data the data
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static void initialize(Object data) throws InstantiationException, IllegalAccessException {	
		initialize(data, getClass(data));
	}
	
	/**
	 * Initialize.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static void initialize(Object data, int[] from, int[] to) throws InstantiationException, IllegalAccessException {	
		initialize(data, getClass(data), from, to);
	}
	
	/**
	 * Initialize.
	 *
	 * @param data the data
	 * @param type the type
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private static void initialize(Object data, Class<?> type) throws InstantiationException, IllegalAccessException {	
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			if(data instanceof Object[][]) for(int i=array.length; --i >= 0; ) initialize(array[i], type);
			else for(int i=array.length; --i >= 0; ) array[i] = type.newInstance();
		}
	}

	/**
	 * Initialize.
	 *
	 * @param data the data
	 * @param type the type
	 * @param from the from
	 * @param to the to
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private static void initialize(Object data, Class<?> type, int[] from, int[] to) throws InstantiationException, IllegalAccessException {	
		initialize(data, type, from, to, 0);
	}
	
	/**
	 * Initialize.
	 *
	 * @param data the data
	 * @param type the type
	 * @param from the from
	 * @param to the to
	 * @param depth the depth
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private static void initialize(Object data, Class<?> type, int[] from, int[] to, int depth) throws InstantiationException, IllegalAccessException {	
		if(data instanceof Object[]) {
			final int start = from[depth];
			final int end = to[depth];
			Object[] array = (Object[]) data;
			if(data instanceof Object[][]) for(int i=start; i<end; i++) initialize(array[i], type, from, to, depth+1);
			else for(int i=start; i < end; i++) array[i] = type.newInstance();
		}
	}
	
	
	// TODO
	/**
	 * Check consistency.
	 *
	 * @throws ClassCastException the class cast exception
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public void checkConsistency() throws ClassCastException, IndexOutOfBoundsException {
		// throw ClassCastException if elements are not the same class
		// throw IndexOutOfBoundsException if subarrays are not the size (not cubic!)
	}
	
	/**
	 * Sub array.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @return the object
	 */
	public static Object subArray(Object array, int[] from, int[] to) {	
		return subArray(array, from, to, 0);
	}
	
	/**
	 * Sub array.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @param depth the depth
	 * @return the object
	 */
	public static Object subArray(Object array, int[] from, int[] to, int depth) {
		final int start = from[depth];
		final int end = to[depth];
		
		if(depth == from.length-1) {
			if(array instanceof double[]) return Arrays.copyOfRange((double[]) array, start, end);
			else if(array instanceof float[]) return Arrays.copyOfRange((float[]) array, start, end);
			else if(array instanceof long[]) return Arrays.copyOfRange((long[]) array, start, end);
			else if(array instanceof int[]) return Arrays.copyOfRange((int[]) array, start, end);
			else if(array instanceof short[]) return Arrays.copyOfRange((short[]) array, start, end);
			else if(array instanceof byte[]) return Arrays.copyOfRange((byte[]) array, start, end);
			else if(array instanceof boolean[]) return Arrays.copyOfRange((boolean[]) array, start, end);
			else return Arrays.copyOfRange((Object[]) array, start, end);	
		}
		else {
			Object[] subarray = (Object[]) Array.newInstance( ((Object[]) array)[0].getClass(), end - start);
			for(int i=start; i<end; i++) subarray[i-start] = subArray(((Object[]) array)[i], from, to, depth+1);
			return subarray;
		}		
	}
	
	
	/**
	 * Paste.
	 *
	 * @param patch the patch
	 * @param array the array
	 * @param offset the offset
	 */
	public static void paste(Object patch, Object array, int[] offset) {
		int[] N = getShape(array);
		int[] beginning = new int[N.length];
		int[] patchN = getShape(patch);
		for(int i=N.length; --i >= 0; ) N[i] = Math.min(N[i]-offset[i], patchN[i]);
		arraycopy(patch, beginning, array, offset, N);	
	}
	
	/**
	 * Resize.
	 *
	 * @param data the data
	 * @param toSize the to size
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object resize(Object data, int[] toSize) throws IllegalArgumentException {
		Object resized = createArray(getClass(data), toSize);
		paste(data, resized, new int[toSize.length]);
		pad(resized, getClass(data), toSize, getShape(data));
		return resized;
	}

	/**
	 * Pad.
	 *
	 * @param data the data
	 * @param fromSize the from size
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object pad(Object data, int[] fromSize) throws IllegalArgumentException {
		Class<?> type = getClass(data);
		int[] toSize = getShape(data);
		pad(data, type, fromSize, toSize);
		return data;
	}
	
	// Used to pad data
	/**
	 * Pad.
	 *
	 * @param data the data
	 * @param type the type
	 * @param from the from
	 * @param to the to
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private static void pad(Object data, Class<?> type, int[] from, int[] to) throws IllegalArgumentException {
		pad(data, type, from, to, 0);
	}
	
	/**
	 * Pad.
	 *
	 * @param data the data
	 * @param type the type
	 * @param from the from
	 * @param to the to
	 * @param depth the depth
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private static void pad(Object data, Class<?> type, int[] from, int[] to, int depth) throws IllegalArgumentException {
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			int start = from[depth];
			int end = to[depth];
			for(int i=start; i<end; i++) clear(array[i]);
			for(int i=0; i<start; i++) pad(array[i], type, from, to, depth+1);
			for(int i=end; i<array.length; i++) pad(array[i], type, from, to, depth+1);
		}
		else if(type.isPrimitive()) clear(data, from, to, depth);
	}
	
	/**
	 * Arraycopy.
	 *
	 * @param src the src
	 * @param from the from
	 * @param dst the dst
	 * @param to the to
	 * @param N the n
	 */
	private static void arraycopy(Object src, int[] from, Object dst, int[] to, int[] N) {
		arraycopy(src, from, dst, to, N, 0);
	}
		
	/**
	 * Arraycopy.
	 *
	 * @param src the src
	 * @param from the from
	 * @param dst the dst
	 * @param to the to
	 * @param N the n
	 * @param depth the depth
	 */
	private static void arraycopy(Object src, int[] from, Object dst, int[] to, int[] N, int depth) {
		if(depth == from.length-1) System.arraycopy(src, from[depth], dst, to[depth], N[depth]);
		else {
			for(int n=0, srcIndex=from[0], dstIndex=to[0]; n < N[0]; n++, srcIndex++, dstIndex++)
				arraycopy(((Object[]) src)[srcIndex], from, ((Object[]) dst)[dstIndex], to, N, depth+1); 
		}			
	}
	
	/**
	 * Adds the.
	 *
	 * @param array the array
	 * @param offset the offset
	 * @param patch the patch
	 */
	public static void add(Object array, int[] offset, Object patch) {
		Class<?> type = getClass(array);
		
		if(!type.equals(getClass(patch))) throw new IllegalArgumentException("Mismatch of array types to add.");
		if(!(type.isPrimitive() || Additive.class.isAssignableFrom(type))) throw new ClassCastException("Array is not Additive or primitive.");
		
		int[] N = getShape(array);
		int[] patchN = getShape(patch);
		for(int i=0; i< N.length; i++) N[i] = Math.min(N[i]-offset[i], patchN[i]);
		add(array, offset, patch, type, N);	
	}
	
	/**
	 * Adds the.
	 *
	 * @param <T> the generic type
	 * @param array the array
	 * @param offset the offset
	 * @param patch the patch
	 * @param type the type
	 * @param N the n
	 * @throws ClassCastException the class cast exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	static <T> void add(Object array, int[] offset, Object patch, Class<T> type, int[] N) throws ClassCastException, IllegalArgumentException {			
		add(array, offset, patch, type, N, 0);
	}
	
	/**
	 * Adds the.
	 *
	 * @param <T> the generic type
	 * @param array the array
	 * @param offset the offset
	 * @param patch the patch
	 * @param type the type
	 * @param N the n
	 * @param depth the depth
	 * @throws ClassCastException the class cast exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	static <T> void add(Object array, int[] offset, Object patch, Class<T> type, int[] N, int depth) throws ClassCastException, IllegalArgumentException {			
		final int start = offset[depth];
		final int n = N[depth];
		
		if(depth == offset.length-1) {
			if(type.isPrimitive()) {
				if(type.equals(double.class)) {
					double[] element = (double[]) array;
					double[] patchElement = (double[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else if(type.equals(float.class)) {
					float[] element = (float[]) array;
					float[] patchElement = (float[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else if(type.equals(int.class)) {
					int[] element = (int[]) array;
					int[] patchElement = (int[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else if(type.equals(long.class)) {
					long[] element = (long[]) array;
					long[] patchElement = (long[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else if(type.equals(short.class)) {
					short[] element = (short[]) array;
					short[] patchElement = (short[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else if(type.equals(byte.class)) {
					byte[] element = (byte[]) array;
					byte[] patchElement = (byte[]) patch;
					for(int from=0, to=start; from < n; from++, to++) element[to] += patchElement[from];
				}
				else throw new ClassCastException("Addition of " + type.getName() + " is not implemented.");
			}
			else if(array instanceof Additive){
				@SuppressWarnings("unchecked")
				Additive<T>[] element = (Additive<T>[]) array;
				@SuppressWarnings("unchecked")
				T[] patchElement = (T[]) patch;
				for(int from=0, to=start; from < n; from++, to++) element[to].add(patchElement[from]);
			}
			else throw new IllegalArgumentException("Cannot add array type: " + array.getClass().getSimpleName());
		}
		else {
			for(int from=0, to=offset[0]; from < N[0]; from++, to++) 
				add(((Object[]) array)[to], offset, ((Object[]) patch)[from], type, N, depth+1); 
		}	
	}
	
	
	/**
	 * Copy.
	 *
	 * @param array the array
	 * @return the object
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static Object copyOf(Object array) throws InstantiationException, IllegalAccessException { 
	    if(array == null) return null;
	    else if(array instanceof Object[]) { 
			final Object[] original = (Object[]) array;
			final Object[] copy = (Object[]) original.clone();	
			for(int i=original.length; --i >= 0; ) copy[i] = original[i] instanceof Copiable ? ((Copiable<?>) original[i]).copy() : copyOf(original[i]);
			return copy;
		}
		else if(array instanceof double[]) return Arrays.copyOf((double[]) array, ((double[]) array).length);
		else if(array instanceof float[]) return Arrays.copyOf((float[]) array, ((float[]) array).length);
		else if(array instanceof long[]) return Arrays.copyOf((long[]) array, ((long[]) array).length);
		else if(array instanceof int[]) return Arrays.copyOf((int[]) array, ((int[]) array).length);
		else if(array instanceof short[]) return Arrays.copyOf((short[]) array, ((short[]) array).length);
		else if(array instanceof byte[]) return Arrays.copyOf((byte[]) array, ((byte[]) array).length);
		else if(array instanceof boolean[]) return Arrays.copyOf((boolean[]) array, ((boolean[]) array).length);
		else return new IllegalArgumentException("Cannot copy class " + array.getClass().getSimpleName());
	}
	
	
	// Scalable scale(0.0)
	
	/**
	 * Clear.
	 *
	 * @param array the array
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void clear(Object array) throws IllegalArgumentException {
		int[] size = getShape(array);
		clear(array, new int[size.length], size);
	}
		
	/**
	 * Clear.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void clear(Object array, int[] from, int[] to) throws IllegalArgumentException {
		clear(array, from, to, 0);
	}
	
	/**
	 * Clear.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @param depth the depth
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void clear(Object array, int[] from, int[] to, int depth) throws IllegalArgumentException {
		final int start = from[depth];
		final int end = to[depth];
		
		if(array instanceof LinearAlgebra[]) {
			LinearAlgebra<?>[] element = (LinearAlgebra<?>[]) array;	
			for(int i=start; i<end; i++) element[i].zero();			
		}
		else if(array instanceof Scalable[]) {
			Scalable[] element = (Scalable[]) array;	
			for(int i=start; i<end; i++) element[i].scale(0.0);			
		}
		else if(array instanceof Object[]) {
			Object[] subarray = (Object[]) array;			
			if(array instanceof Object[][]) for(int i=start; i<end; i++) clear(subarray[i], from, to, depth+1);
			else {
				Class<?> type = subarray[0].getClass();
				for(int i=start; i<end; i++) {
					try { subarray[i] = type.newInstance(); }
					catch(Exception e) { subarray[i] = null; }
				}
			}			
		}
		else if(array.getClass().isArray()) {
			if(array instanceof double[]) fill(array, from, to, 0.0, double.class);
			else if(array instanceof float[]) fill(array, from, to, 0.0F, float.class);
			else if(array instanceof long[]) fill(array, from, to, 0L, long.class);
			else if(array instanceof int[]) fill(array, from, to, 0, int.class);
			else if(array instanceof short[]) fill(array, from, to, (short) 0, short.class);
			else if(array instanceof byte[]) fill(array, from, to, (byte) 0, byte.class);
			else if(array instanceof boolean[]) fill(array, from, to, false, boolean.class);
		}
		else throw new IllegalArgumentException("Cannot clear type " + array.getClass().getName());
	}
	
	
	/**
	 * Fill.
	 *
	 * @param array the array
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void fill(Object array, Object value) throws IllegalArgumentException {		
		int[] size = getShape(array);
		fill(array, new int[size.length], size, value);
	}
	
	/**
	 * Fill.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @param value the value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void fill(Object array, int[] from, int[] to, Object value) throws IllegalArgumentException {
		Class<?> type = getClass(array);
		
		
		if(to.length < from.length) throw new IllegalArgumentException("Mismatched from and to indeces.");
		
		if(type.isPrimitive()) {
			boolean match = false;
			if(type.equals(double.class)) match = value instanceof Double;
			else if(type.equals(float.class)) match = value instanceof Float;
			else if(type.equals(long.class)) match = value instanceof Long;
			else if(type.equals(int.class)) match = value instanceof Integer;
			else if(type.equals(short.class)) match = value instanceof Short;
			else if(type.equals(byte.class)) match = value instanceof Byte;
			else if(type.equals(boolean.class)) match = value instanceof Boolean;
			
			if(!match) throw new IllegalArgumentException("Trying to fill array of " + type.getName() + " with value of " + value.getClass().getName());		
		}
		else {		
			if(!type.isInstance(value))
				throw new IllegalArgumentException("Trying to fill array of " + type.getName() + " with value of " + value.getClass().getName());
			if(!Copiable.class.isAssignableFrom(type)) 
				throw new IllegalArgumentException("Only arrays of primitives and Copiable types can be filled.");		
		}
			
		fill(array, from, to, value, type);
		
	}
	
	/**
	 * Fill.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @param value the value
	 * @param type the type
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void fill(Object array, int[] from, int[] to, Object value, Class<?> type) throws IllegalArgumentException {
		fill(array, from, to, value, type, 0);
	}
	
	/**
	 * Fill.
	 *
	 * @param array the array
	 * @param from the from
	 * @param to the to
	 * @param value the value
	 * @param type the type
	 * @param depth the depth
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void fill(Object array, int[] from, int[] to, Object value, Class<?> type, int depth) throws IllegalArgumentException {
		final int start = from[depth];
		final int end = to[depth];
		
		if(type.isPrimitive()) {
			if(array instanceof Object[]) {
				Object[] subarray = (Object[]) array;
				for(int i=start; i<end; i++) fill(subarray[i], from, to, value, type, depth+1);				
			}
			else if(type.equals(double.class)) Arrays.fill((double[]) array, start, end, (Double) value);
			else if(type.equals(float.class)) Arrays.fill((float[]) array, start, end, (Float) value);
			else if(type.equals(long.class)) Arrays.fill((long[]) array, start, end, (Long) value);
			else if(type.equals(int.class)) Arrays.fill((int[]) array, start, end, (Integer) value);
			else if(type.equals(short.class)) Arrays.fill((short[]) array, start, end, (Short) value);
			else if(type.equals(byte.class)) Arrays.fill((byte[]) array, start, end, (Byte) value);
			else if(type.equals(boolean.class)) Arrays.fill((boolean[]) array, start, end, (Boolean) value);			
		}
		
		else {
			if(array instanceof Object[][]) {
				Object[] subarray = (Object[]) array;
				for(int i=start; i<end; i++) fill(subarray[i], from, to, value, type, depth+1);			
			}
			else if(array instanceof Copiable[]){
				Object[] subarray = (Object[]) array;
				for(int i=start; i<end; i++) subarray[i] = ((Copiable<?>) value).copy();	
			}
			else {
				Object[] subarray = (Object[]) array;
				for(int i=start; i<end; i++) {
					try { subarray[i] = type.newInstance(); } 
					catch (Exception e) { throw new IllegalArgumentException("array fill error: " + e.getMessage()); } 
				}
			}
		}
	}
	
	
	//public static Object getElement(Object array, int[] index) {}
	
	//public static void setElement(Object array, int[] index, Object value) {}
	
	/*
	public static void switchElements(Object array, int[] i1, int[] i2) {
		Object temp = getElement(array, i1);
		setElement(array, i1, getElement(array, i2));
		setElement(array, i2, temp);		
	}
	*/
	
	// Coordinate rearrangments....
		
	/*
	// rotating is useful for methods that go thruogh all directions separately
	public static Object rotatedView(Object array) {
		// make last index into first
		
	}
	
	public static Object view(Object array, int[] order) {
		// Find the last disturbed index -- need to go only that deep;
		
	}
	
	public static Object swapCoordinates(Object array, int i, int j) {
		
		
	}
	*/
	
	// collapse those dimensions where index is positive to the indexed element
	// where index is negative, all elements are retained.
	/**
	 * Sub space.
	 *
	 * @param <T> the generic type
	 * @param array the array
	 * @param keepIndex the keep index
	 * @return the object
	 */
	public static <T> Object subSpace(Object array, int[] keepIndex) {
		int depth = keepIndex.length;
			
		int[] from = new int[depth];
		int[] to = new int[depth];
		int[] dimension = getShape(array);
		int keeps = dimension.length - depth;
		
		for(int i=0; i<depth; i++) {
			if(keepIndex[i] >= 0) { from[i] = keepIndex[i]; to[i] = from[i] + 1; dimension[i] = 1; }
			else { from[i] = 0; to[i] = dimension[i]; keeps++; }
		}
		
		int[] newshape = new int[keeps];
		for(int i=0, k=0; i<depth; i++) if(keepIndex[i] < 0) newshape[k++] = dimension[i];
		
		try {
			Object dest = createArray(getClass(array), newshape);

			MeshCrawler<T> sourceIterator = MeshCrawler.createFor(array, from, to);
			MeshCrawler<T> destIterator = MeshCrawler.createFor(dest, newshape.length);

			while(sourceIterator.hasNext()) destIterator.setNext(sourceIterator.next());  
				
			return dest;
		} catch(Exception e) { Util.error(ArrayUtil.class, e); }

		return null;
	}
	
	/**
	 * Expand.
	 *
	 * @param <T> the generic type
	 * @param array the array
	 * @param placement the placement
	 * @return the object
	 */
	public static <T> Object expand(Object array, boolean[] placement) {
		int[] dimension = getShape(array);
		if(placement.length < dimension.length) 
			throw new IllegalArgumentException("The placement array has to be bigger than the original dimension for expansion.");
		int n = 0;
		for(int i=0; i<placement.length; i++) if(placement[i]) n++;
		if(n != dimension.length) 
			throw new IllegalArgumentException("Mismatch between placements and argument dimension.");
		
		Class<?> type = getClass(array);
		
		int[] newdims = new int[placement.length];
		for(int i=0, k=0; i<placement.length; i++) newdims[i] = placement[i] ? dimension[k++] : 1;
		
		try {
			Object destination = createArray(type, newdims);

			MeshCrawler<T> sourceIterator = MeshCrawler.createFor(array);
			MeshCrawler<T> destIterator = MeshCrawler.createFor(destination);

			while(sourceIterator.hasNext()) {
				destIterator.next();
				destIterator.setCurrent(sourceIterator.next());
			}

			return destination;
		} catch(Exception e) { Util.error(ArrayUtil.class, e); }
		
		return null;
	}
	
	/**
	 * Collapse.
	 *
	 * @param array the array
	 * @return the object
	 */
	public static Object collapse(Object array) {
		int[] dimension = getShape(array);
		int[] keepIndex = new int[dimension.length];
		for(int i=0; i<dimension.length; i++) keepIndex[i] = dimension[i] > 1 ? -1 : 0; 
		return subSpace(array, keepIndex);
	}
	
	
	
	
	// Iterators....
	
	
	
	// TODO Rewrite without iterator? 
	/**
	 * Unfold.
	 *
	 * @param array the array
	 * @return the object
	 */
	public static Object unfold(Object[] array) {
		Class<?> type = getClass(array);
		int N = getElementCount(array);
		
		Object view = null;
		
		view = createArray(type, new int[] {N});
			
		Iterator<?> iterator = MeshCrawler.createFor(array, getRank(array)-1);
		int offset = 0;
		while(iterator.hasNext()) {
			Object element = iterator.next();
			int size = getShape(element)[0];
			System.arraycopy(element, 0, view, offset, size);
			offset += size;
		}
		
		return view;
	}
	
	
	/**
	 * Fold.
	 *
	 * @param <T> the generic type
	 * @param linearView the linear view
	 * @param dimensions the dimensions
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static <T> Object fold(Object linearView, int[] dimensions) throws IllegalArgumentException {
		int dstSize = 1;
		for(int i=0; i<dimensions.length; i++) dstSize *= dimensions[i];
		if(dstSize != getNextSize(linearView)) throw new IllegalArgumentException("Folding to a an array of different size.");
		
		Object folded = createArray(linearView.getClass().getComponentType(), dimensions);
		MeshCrawler<T> dstIterator = MeshCrawler.createFor(folded);
		MeshCrawler<T> srcIterator = MeshCrawler.createFor(linearView);
		
		while(srcIterator.hasNext()) dstIterator.setNext(srcIterator.next()); 

		return folded;
	}
	
	
	// Replacements...	
	
	/**
	 * Replace values.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	public static void replaceValues(Object data, Object from, Object to) {
		replaceValues(data, getClass(data), from, to);
	}
	
	/**
	 * Replace values.
	 *
	 * @param data the data
	 * @param type the type
	 * @param from the from
	 * @param to the to
	 */
	static void replaceValues(Object data, Class<?> type, Object from, Object to) {
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			if(type.isPrimitive() || data instanceof Object[][]) for(int i=0; i<array.length; i++) replaceValues(array[i], type, from, to);		
			else for(int i=0; i<array.length; i++) if(array[i].equals(from)) array[i] = to;
		}	
		else {
			if(data instanceof double[]) replaceValues((double[]) data, (Double) from, ((Double) to).doubleValue());
			else if(data instanceof float[]) replaceValues((float[]) data, (Float) from, ((Float) to).floatValue());				
			else throw new IllegalArgumentException("Replacement is not implemented for arrays of " + type);			
		}	
	}
	
	/**
	 * Replace values.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	public static void replaceValues(double[] data, Double from, double to) {
		for(int i=0; i<data.length; i++) if(from.equals(data[i])) data[i] = to;
	}
	
	/**
	 * Replace values.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	public static void replaceValues(float[] data, Float from, float to) {
		for(int i=0; i<data.length; i++) if(from.equals(data[i])) data[i] = to;
	}
	

	



	// Conversions	
	
	/**
	 * As double.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object asDouble(Object data) {		
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			Object[] doubleArray = null;
			for(int i=0; i<array.length; i++) {
				Object view = asDouble(array[i]);
				if(doubleArray == null) doubleArray = (Object[]) Array.newInstance(view.getClass(), array.length);
				doubleArray[i] = view;
			}
			return doubleArray;
		}
		else if(data instanceof float[]) return asDouble((float[]) data);
		else if(data instanceof ViewableAsDoubles) return ((ViewableAsDoubles) data).viewAsDoubles();
		else throw new IllegalArgumentException(data.getClass().getSimpleName() + " cannot be viewed as a double array.");		
	}
	
	/**
	 * As float.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object asFloat(Object data) {		
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			Object[] floatArray = null;
			for(int i=0; i<array.length; i++) {
				Object view = asFloat(array[i]);
				if(floatArray == null) floatArray = (Object[]) Array.newInstance(view.getClass(), array.length);
				floatArray[i] = view;
			}
			return floatArray;
		}
		else if(data instanceof double[]) return asFloat((double[]) data);
		else if(data instanceof ViewableAsDoubles) return asFloat(((ViewableAsDoubles) data).viewAsDoubles());
		else throw new IllegalArgumentException(data.getClass().getSimpleName() + " cannot be viewed as a float array.");		
	}
	
	/**
	 * As complex.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object asComplex(Object data) {
		if(data instanceof Scalar) return new Complex(((Scalar) data).getValue());
		else if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			Object[] complexArray = null;
			for(int i=0; i<array.length; i++) {
				Object view = asComplex(array[i]);
				if(complexArray == null) complexArray = (Object[]) Array.newInstance(view.getClass(), array.length);
				complexArray[i] = view;
			}
			return complexArray;
		}
		else if(data instanceof double[]) return asComplex((double[]) data);
		else if(data instanceof float[]) return asComplex((float[]) data);
		else throw new IllegalArgumentException(data.getClass().getSimpleName() + " cannot be viewed as a complex array.");		
	}
	
	/**
	 * As imaginary.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object asImaginary(Object data) {		
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			Object[] complexArray = null;
			for(int i=0; i<array.length; i++) {
				Object view = asImaginary(array[i]);
				if(complexArray == null) complexArray = (Object[]) Array.newInstance(view.getClass(), array.length);
				complexArray[i] = view;
			}
			return complexArray;
		}
		else if(data instanceof double[]) return asImaginary((double[]) data);
		else if(data instanceof float[]) return asImaginary((float[]) data);
		else throw new IllegalArgumentException(data.getClass().getSimpleName() + " cannot be viewed as a complex array.");		
	}
	
	
	/**
	 * View as.
	 *
	 * @param template the template
	 * @param doubles the doubles
	 * @return the object
	 * @throws ClassCastException the class cast exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static Object viewAs(ViewableAsDoubles template, Object doubles) throws ClassCastException, InstantiationException, IllegalAccessException {
		try {			
			ViewableAsDoubles view = template.getClass().newInstance();
			view.createFromDoubles(doubles);
			return view;
		}
		catch(IllegalArgumentException e) {
			if(doubles instanceof Object[]) {
				Object[] array = (Object[]) doubles;
				Object[] viewArray = null;
				for(int i=0; i<array.length; i++) {
					Object view = viewAs(template, array[i]);
					if(viewArray == null) Array.newInstance(view.getClass(), array.length);
					viewArray[i] = view;
				}
				return viewArray;
			}
			else throw new IllegalArgumentException(doubles.getClass().getSimpleName() + " cannot be viewed as " + template.getClass().getSimpleName());			
		}
	}
	
	
	/**
	 * As double.
	 *
	 * @param data the data
	 * @return the double[]
	 */
	public static double[] asDouble(float[] data) {
		double[] d = new double[data.length];
		for(int i=0; i<data.length; i++) d[i] = data[i];
		return d;
	}

	/**
	 * As float.
	 *
	 * @param data the data
	 * @return the float[]
	 */
	public static float[] asFloat(double[] data) {
		float[] f = new float[data.length];
		for(int i=0; i<data.length; i++) f[i] = (float)data[i];
		return f;
	}

	/**
	 * As complex.
	 *
	 * @param data the data
	 * @return the complex[]
	 */
	public static Complex[] asComplex(double[] data) {
		Complex[] c = new Complex[data.length];
		for(int i=0; i<data.length; i++) c[i] = new Complex(data[i], 0.0);
		return c;
	}

	/**
	 * As complex.
	 *
	 * @param data the data
	 * @return the complex[]
	 */
	public static Complex[] asComplex(float[] data) {
		Complex[] c = new Complex[data.length];
		for(int i=0; i<data.length; i++) c[i] = new Complex(data[i], 0.0);
		return c;
	}
	
	/**
	 * As complex.
	 *
	 * @param data the data
	 * @return the complex[]
	 */
	public static Complex[] asComplex(Scalar[] data) {
		Complex[] c = new Complex[data.length];
		for(int i=0; i<data.length; i++) c[i] = new Complex(data[i].getValue(), 0.0);
		return c;
	}

	/**
	 * As imaginary.
	 *
	 * @param data the data
	 * @return the complex[]
	 */
	public static Complex[] asImaginary(double[] data) {
		Complex[] c = new Complex[data.length];
		for(int i=0; i<data.length; i++) c[i] = new Complex(0.0, data[i]);
		return c;
	}

	/**
	 * As imaginary.
	 *
	 * @param data the data
	 * @return the complex[]
	 */
	public static Complex[] asImaginary(float[] data) {
		Complex[] c = new Complex[data.length];
		for(int i=0; i<data.length; i++) c[i] = new Complex(0.0, data[i]);
		return c;
	}





	
	
	// Scaling...

	/**
	 * Scale.
	 *
	 * @param data the data
	 * @param factor the factor
	 */
	public static void scale(double[] data, double factor) {
		for(int i=data.length; --i >= 0; ) data[i] *= factor;
	}

	/**
	 * Scale.
	 *
	 * @param data the data
	 * @param factor the factor
	 */
	public static void scale(float[] data, double factor) {
		for(int i=data.length; --i >= 0; ) data[i] *= factor;
	}


	/**
	 * Scale.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void scale(Object[] data, double factor) throws IllegalArgumentException {
		for(int i=data.length; --i >= 0; ) {
			if(data[i] instanceof double[]) scale((double[]) data[i], factor);
			else if(data[i] instanceof float[]) scale((float[]) data[i], factor);
			else if(data[i] instanceof Scalable) ((Scalable) data[i]).scale(factor);
			else if(data[i] instanceof Object[]) scale((Object[]) data[i], factor);
			else throw new IllegalArgumentException("Cannot scale type " + data[i].getClass().getSimpleName());
		}
	}

	/**
	 * Scale.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static void scale(Object data, double factor) throws IllegalArgumentException {
		if(data instanceof Object[]) scale((Object[]) data, factor);
		else if(data instanceof double[]) scale((double[]) data, factor);
		else if(data instanceof float[]) scale((float[]) data, factor);
	}
	
	
	/**
	 * Scale.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param factor the factor
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> void scale(Object data, T factor) throws IllegalArgumentException {
		if(data instanceof Multiplicative) ((Multiplicative<T>) data).multiplyBy(factor);
		else if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for(int i=array.length; --i >= 0; ) scale(array[i], factor);			
		}
		else throw new IllegalArgumentException("Cannot multiply type " + data.getClass().getSimpleName());
	}
	
	
	
	/**
	 * Scale.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param factor the factor
	 * @return the list
	 */
	public static <T extends Scalable> List<T> scale(List<T> data, double factor) {
		for(T element : data) element.scale(factor);
		return data;
	}

	// Cycling
	// This is useful for loading and unloading FFTs...
	/**
	 * Cycle.
	 *
	 * @param data the data
	 * @param n the n
	 * @return the double[]
	 */
	public static double[] cycle(double[] data, int n) {
		n %= data.length;
		double[] temp = new double[data.length];
		System.arraycopy(data, n, temp, 0, data.length-n);
		System.arraycopy(data, 0, temp, data.length-n, n);
		System.arraycopy(temp, 0, data, 0, data.length);
		return data;
	}
	
	// Cycling
	// This is useful for loading and unloading FFTs...
	/**
	 * Cycle.
	 *
	 * @param data the data
	 * @param n the n
	 * @return the float[]
	 */
	public static float[] cycle(float[] data, int n) {
		n %= data.length;
		float[] temp = new float[data.length];
		System.arraycopy(data, n, temp, 0, data.length-n);
		System.arraycopy(data, 0, temp, data.length-n, n);
		System.arraycopy(temp, 0, data, 0, data.length);
		return data;
	}

	/**
	 * Cycle.
	 *
	 * @param data the data
	 * @param n the n
	 */
	public static void cycle(Object[] data, int n) {
		n %= data.length;
		Object[] temp = new Object[data.length];
		System.arraycopy(data, n, temp, 0, data.length-n);
		System.arraycopy(data, 0, temp, data.length-n, n);
		System.arraycopy(temp, 0, data, 0, data.length);
	}	

	/**
	 * Cycle.
	 *
	 * @param data the data
	 * @param n the n
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static void cycle(Object[] data, int[] n) throws IndexOutOfBoundsException {
		cycle(data, n, 0);
	}	
	
	/**
	 * Cycle.
	 *
	 * @param data the data
	 * @param n the n
	 * @param depth the depth
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static void cycle(Object[] data, int[] n, int depth) throws IndexOutOfBoundsException {
		cycle(data, n[depth]);
		if(depth < n.length-1) for(int i=data.length; --i >= 0; ) {
			if(data[i] instanceof double[]) cycle((double[]) data[i], n[depth+1]);
			if(data[i] instanceof float[]) cycle((float[]) data[i], n[depth+1]);
			else if(data[i] instanceof Object[]) cycle((Object[]) data[i], n, depth+1);
		}
	}

	
	
	
	
	
	
	
	/**
	 * Offset.
	 *
	 * @param data the data
	 * @param constant the constant
	 * @return the double[]
	 */
	public static double[] offset(double[] data, double constant) {
		for(int i=data.length; --i >= 0; ) data[i] += constant;
		return data;
	}

	/**
	 * Offset.
	 *
	 * @param data the data
	 * @param constant the constant
	 * @return the float[]
	 */
	public static float[] offset(float[] data, double constant) {
		for(int i=data.length; --i >= 0; ) data[i] += constant;
		return data;
	}


	/**
	 * Offset.
	 *
	 * @param data the data
	 * @param constant the constant
	 * @return the object[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object[] offset(Object[] data, double constant) throws IllegalArgumentException {
		for(int i=data.length; --i >= 0; ) {
			if(data[i] instanceof double[]) offset((double[]) data[i], constant);
			else if(data[i] instanceof float[]) offset((float[]) data[i], constant);
			else if(data[i] instanceof Object[]) offset((Object[]) data[i], constant);
			else throw new IllegalArgumentException("Cannot add to type " + data[i].getClass().getSimpleName());
		}
		return data;
	}

	/**
	 * Offset.
	 *
	 * @param <T> the generic type
	 * @param constant the constant
	 * @param data the data
	 * @return the object
	 */
	public static <T extends Scalable> Object offset(double constant, T... data) {
		return scale(Arrays.asList(data), constant).toArray();
	}	

	/**
	 * Offset.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param constant the constant
	 * @return the list
	 */
	public static <T extends Scalable> List<T> offset(List<T> data, double constant) {
		for(T element : data) element.scale(constant);
		return data;
	}


	// Adding
	/**
	 * Adds the to.
	 *
	 * @param data the data
	 * @param offset the offset
	 * @return the double[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static double[] addTo(double[] data, double[] offset) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] += offset[i];
		return data;
	}

	/**
	 * Adds the to.
	 *
	 * @param data the data
	 * @param offset the offset
	 * @return the double[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static double[] addTo(double[] data, float[] offset) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] += offset[i];
		return data;
	}

	/**
	 * Adds the to.
	 *
	 * @param data the data
	 * @param offset the offset
	 * @return the float[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static float[] addTo(float[] data, double[] offset) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] += offset[i];
		return data;
	}

	/**
	 * Adds the to.
	 *
	 * @param data the data
	 * @param offset the offset
	 * @return the float[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static float[] addTo(float[] data, float[] offset) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] += offset[i];
		return data;
	}


	/**
	 * Adds the to.
	 *
	 * @param data the data
	 * @param offset the offset
	 * @return the object[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object[] addTo(Object[] data, double[] offset) throws IllegalArgumentException {
		for(int i=data.length; --i >= 0; ) {
			if(data[i] instanceof double[]) addTo((double[]) data[i], offset);
			else if(data[i] instanceof float[]) addTo((float[]) data[i], offset);
			else if(data[i] instanceof Object[]) addTo((Object[]) data[i], offset);
			else throw new IllegalArgumentException("Cannot add to type " + data[i].getClass().getSimpleName());
		}
		return data;
	}	

	// Multiplying...

	/**
	 * Multiply by.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @return the double[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static double[] multiplyBy(double[] data, double[] factor) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] *= factor[i];
		return data;
	}

	/**
	 * Multiply by.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @return the double[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static double[] multiplyBy(double[] data, float[] factor) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] *= factor[i];
		return data;
	}

	/**
	 * Multiply by.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @return the float[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static float[] multiplyBy(float[] data, double[] factor) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] += factor[i];
		return data;
	}

	/**
	 * Multiply by.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @return the float[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static float[] multiplyBy(float[] data, float[] factor) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i] *= factor[i];
		return data;
	}


	/**
	 * Multiply by.
	 *
	 * @param data the data
	 * @param factor the factor
	 * @return the object[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object[] multiplyBy(Object[] data, Object[] factor) throws IllegalArgumentException {
		for(int i=data.length; --i >= 0; ) {
			if(data[i] instanceof double[]) multiplyBy((double[]) data[i], (double[]) factor[i]);
			else if(data[i] instanceof float[]) multiplyBy((float[]) data[i], (double[]) factor[i]);
			else if(data[i] instanceof Object[]) multiplyBy((Object[]) data[i], (Object[]) factor[i]);
			else if(data[i] instanceof Multiplicative) ((Multiplicative) data[i]).multiplyBy(factor[i]);
			else throw new IllegalArgumentException("Cannot multiply type " + data[i].getClass().getSimpleName());
		}
		return data;
	}	
	
		
	
	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double[]
	 */
	public static double[] product(double[] a, double[] b) {
		double[] result = new double[a.length];
		for(int i=a.length; --i >= 0; ) result[i] = a[i] * b[i];
		return result;
	}
	
	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double[]
	 */
	public static double[] product(double[] a, float[] b) {
		double[] result = new double[a.length];
		for(int i=a.length; --i >= 0; ) result[i] = a[i] * b[i];
		return result;
	}
	
	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double[]
	 */
	public static double[] product(float[] a, double[] b) {
		return product(b, a);
	}
	
	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the float[]
	 */
	public static float[] product(float[] a, float[] b) {
		float[] result = new float[a.length];
		for(int i=a.length; --i >= 0; ) result[i] = a[i] * b[i];
		return result;
	}
		
	/**
	 * Product.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object product(Object a, Object b) throws IllegalArgumentException {
		if(a instanceof Object[]){
			Object result = null;
			try { result = copyOf(a); }
			catch(Exception e) { Util.error(ArrayUtil.class, e); }
			multiplyBy((Object[]) result, (Object[]) b);
			return result;
		}
		else if(a instanceof double[]) {
			if(b instanceof double[]) return product((double[]) a, (double[]) b);
			else if(b instanceof float[]) return product((double[]) a, (float[]) b);
		}
		else if(a instanceof float[]) {
			if(b instanceof float[]) return product((float[]) a, (float[]) b);
			else if(b instanceof double[]) return product((double[]) b, (float[]) a);
		}	
		else throw new IllegalArgumentException(a.getClass().getName() + " cannot create a product.");
		return null;
	}
	
	/**
	 * Dot.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object dot(Object a, Object b) throws IllegalArgumentException {
		if(a instanceof Object[]) {
			Object[] A = (Object[]) a;
			Object[] B = (Object[]) b;
			double sum = 0.0;
			for(int i=A.length; --i >= 0; ) sum += (Double) dot(A[i], B[i]);
			return new Double(sum);
		}
		else if(a instanceof double[]) {
			double[] A = (double[]) a;
			double[] B = (double[]) b;
			double sum = 0.0;
			for(int i=A.length; --i >= 0; ) if(!(Double.isNaN(A[i]) || Double.isNaN(B[i]))) sum += A[i]*B[i]; 
			return sum;
		}
		else if(a instanceof float[]) {
			float[] A = (float[]) a;
			float[] B = (float[]) b;
			double sum = 0.0;
			for(int i=A.length; --i >= 0; ) if(!(Float.isNaN(A[i]) || Float.isNaN(B[i]))) sum += A[i]*B[i]; 
			return sum;
		}
		else throw new IllegalArgumentException(a.getClass().getName() + " cannot be dotted by ArrayUtil.");
	}
	
	
	/**
	 * Multiply by.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param factor the factor
	 * @return the t[]
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static <T extends Multiplicative<? super T>> T[] multiplyBy(T[] data, T[] factor) throws IndexOutOfBoundsException {
		for(int i=data.length; --i >= 0; ) data[i].multiplyBy(factor[i]);
		return data;
	}

	
	
	// Absolute value. Also for Vector2D.
	/**
	 * Abs.
	 *
	 * @param data the data
	 * @return the double[]
	 */
	public static double[] abs(double[] data) {
		for(int i=data.length; --i >= 0; ) data[i] = Math.abs(data[i]);
		return data;
	}
	
	/**
	 * Abs.
	 *
	 * @param data the data
	 * @return the float[]
	 */
	public static float[] abs(float[] data) {
		for(int i=data.length; --i >= 0; ) data[i] = Math.abs(data[i]);
		return data;	
	}
	
	/**
	 * Abs.
	 *
	 * @param data the data
	 * @return the object
	 */
	public static Object abs(Object data) {
		if(data instanceof AbsoluteValue[]) {
			AbsoluteValue[] array = (AbsoluteValue[]) data;
			double[] abs = new double[array.length];
			for(int i=array.length; --i >= 0; ) abs[i] = array[i].abs();
			return abs;
		}
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for(int i=array.length; --i >= 0; ) abs(array[i]);
			return array;
		}
		else if(data.getClass().isArray()) {
			if(data instanceof double[]) return abs((double[]) data);
			else if(data instanceof float[]) return abs((float[]) data);
			else throw new IllegalArgumentException(data.getClass().getName() + " is not supported by array abs().");
			
		}
		else throw new IllegalArgumentException(data.getClass().getName() + " is not supported by array abs().");
		
	}
	

	
	
	
	
	
	// Very primitive for now. Simply returns the first n elements with the closest match to the sum
	/**
	 * Selection near sum.
	 *
	 * @param nearValue the near value
	 * @param array the array
	 * @return the int[]
	 */
	public static int[] selectionNearSum(double nearValue, double[] array) {
		double sum = array[0];
		if(sum >= nearValue) return new int[] {0};

		int last=1;
		while(sum < nearValue) sum += array[last++];
		if(nearValue - (sum - array[last-1]) < sum - nearValue) last--;

		int[] selection = new int[last];
		for(int i=selection.length; --i >= 0; ) selection[i] = i;

		return selection;	
	}

	
	
	
	
	
	
	

	

	
	
	
	/**
	 * Gets the default weights.
	 *
	 * @param data the data
	 * @return the default weights
	 */
	public static double[] getDefaultWeights(double[] data) {
		double[] weight = new double[data.length];
		for(int i=data.length; --i >= 0; ) weight[i] = Double.isNaN(data[i]) ? 0.0 : 1.0;
		return weight;
	} 
	
	/**
	 * Gets the default weights.
	 *
	 * @param data the data
	 * @return the default weights
	 */
	public static float[] getDefaultWeights(float[] data) {
		float[] weight = new float[data.length];
		for(int i=data.length; --i >= 0; ) weight[i] = Float.isNaN(data[i]) ? 0.0F : 1.0F;
		return weight;
	} 
	
	
	/**
	 * Gets the default weights.
	 *
	 * @param data the data
	 * @return the default weights
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object getDefaultWeights(Object data) throws IllegalArgumentException {
		if(data instanceof BlankingValue[]) {
			BlankingValue[] array = (BlankingValue[]) data;
			double[] weight = new double[array.length];
			for(int i=array.length; --i >= 0; ) weight[i] = array[i].isNaN() ? 0.0 : 1.0;
			return weight;
		}
		else if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			Object[] weight = new Object[array.length];
			for(int i=array.length; --i >= 0; ) weight[i] = getDefaultWeights(array[i]);
			return weight;
		}
		else if(data.getClass().isArray()) {
			if(data instanceof double[]) return getDefaultWeights((double[]) data);
			else if(data instanceof float[]) return getDefaultWeights((float[]) data);
			else throw new IllegalArgumentException(data.getClass().getSimpleName() + " cannot be assigned a default weight.");
		}	
		return null;
	}
	
	
	
	
	/**
	 * Gets the smoothed.
	 *
	 * @param signal the signal
	 * @param beam the beam
	 * @return the smoothed
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object getSmoothed(Object signal, Object beam) throws IllegalArgumentException {
		Class<?> type = getClass(signal);	
		if(!type.equals(double.class)) throw new IllegalArgumentException("Can smooth double arrays only.");
		return getSmoothed(signal, getDefaultWeights(signal), beam);
	}

	
	
	
	// Smoothing for real-valued beams only!!!
	/**
	 * Gets the smoothed.
	 *
	 * @param signal the signal
	 * @param weights the weights
	 * @param beam the beam
	 * @return the smoothed
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object getSmoothed(Object signal, Object weights, Object beam) throws IllegalArgumentException {
		Class<?> type = getClass(signal);	
		if(!type.equals(double.class)) throw new IllegalArgumentException("Can smooth double arrays only.");

		// check that types match
		// check that signal and weight dimensions match

		int[] size = getShape(signal);
		int[] position = new int[size.length];
		
		Object weightedSignal = product(signal, weights);	
		
		Object smoothed = null;
		
		try { smoothed = createArray(double.class, size); }
		catch(Exception e) { Util.error(ArrayUtil.class, e); }

		int[] beamSize = getShape(beam);
		int dimensions = size.length;

		//System.err.println(toString(beamSize));
		//System.err.println(toString(getShape(smoothed)));
		
		int[] delta = new int[dimensions];
		int[] n = new int[dimensions];
		int[] beginning = new int[dimensions];	
		int[] zero = new int[dimensions];	

		for(int i=dimensions; --i >= 0; ) delta[i] = (beamSize[i]-1)/2;

		try {
			Object weightedSignalPatch = createArray(double.class, beamSize);
			Object weightsPatch = createArray(double.class, beamSize);

			MeshCrawler<Double> iterator = MeshCrawler.createFor(smoothed);

			iterator.getPosition(position);
			int[] from = new int[position.length];
			int[] to = new int[position.length];

			while(iterator.hasNext()) {
				iterator.next();

				for(int i=0; i<position.length; i++) {
					from[i] = Math.max(position[i] - delta[i], 0);
					beginning[i] = from[i] + delta[i] - position[i];
					to[i] = Math.min(from[i] + beamSize[i] - beginning[i], size[i]);
					n[i] = to[i] - from[i];					
				}

				//System.err.println(toString(from) + " -> " + toString(to) + " : " + toString(beginning) + " : " + toString(n));
				
				
				// Copy the relevant bits into the patch.
				arraycopy(weightedSignal, from, weightedSignalPatch, beginning, n);
				arraycopy(weights, from, weightsPatch, beginning, n);

				// Set the remaining as no-data
				fill(weightedSignalPatch, zero, beginning, Double.NaN, double.class);
				fill(weightsPatch, zero, beginning, Double.NaN, double.class);				
				fill(weightedSignalPatch, n, beamSize, Double.NaN, double.class);
				fill(weightsPatch, n, beamSize, Double.NaN, double.class);

				// TODO...
				// check the math here... 

				double norm = (Double) dot(weightsPatch, beam);
				iterator.setCurrent((Double) dot(weightedSignalPatch, beam) / norm);		
			}

		} catch(Exception e) { Util.error(ArrayUtil.class, e); }

		return smoothed;
	}

	
	// Smooth LinearAlgebra arrays too...
	
	
	/**
	 * Gets the gaussian beam.
	 *
	 * @param FWHM the fwhm
	 * @param w the w
	 * @return the gaussian beam
	 */
	public static Object getGaussianBeam(double[] FWHM, double w) {	
		return getGaussianBeam(FWHM, 1.0, w, 0);
	}
	
	/**
	 * Gets the gaussian beam.
	 *
	 * @param FWHM the fwhm
	 * @param scaling the scaling
	 * @param w the w
	 * @param depth the depth
	 * @return the gaussian beam
	 */
	private static Object getGaussianBeam(double[] FWHM, double scaling, double w, int depth) {
		double sigma = FWHM[depth] / Constant.sigmasInFWHM;
		double A = -0.5/(sigma*sigma);
		
		int width = (int)Math.ceil(w*sigma);
		if(depth == FWHM.length-1) {
			double[] beam = new double[2*width+1];
			for(int i=0; i<beam.length; i++) {
				int d = i - width;
				if(sigma > 0.0) beam[i] = scaling * Math.exp(A*d*d);
				else if(d == 0) beam[i] = 1.0;
			}
			return beam;
		}
		else {
			Object[] beam = null;
			int size = 2*width+1;
			for(int i=0; i<size; i++) {
				int d = i - width;
				Object entry = getGaussianBeam(FWHM, scaling * Math.exp(A*d*d), w, depth+1);
				if(beam == null) beam = (Object[]) Array.newInstance(entry.getClass(), size);
				beam[i] = entry;
			}
			return beam;		
		}
		
	}
	
	/**
	 * Coarse regrid of.
	 *
	 * @param data the data
	 * @param stretch the stretch
	 * @return the double[]
	 */
	public static double[] coarseRegridOf(double[] data, double stretch) {
		double[] regridded = new double[(int) Math.ceil(stretch * data.length)];
		coarseRegrid(data, regridded);
		return regridded;
	}	
	
	/**
	 * Coarse regrid of.
	 *
	 * @param data the data
	 * @param stretch the stretch
	 * @return the float[]
	 */
	public static float[] coarseRegridOf(float[] data, double stretch) {
		float[] regridded = new float[(int) Math.ceil(stretch * data.length)];
		coarseRegrid(data, regridded);
		return regridded;
	}
	
	
	/**
	 * Coarse regrid.
	 *
	 * @param data the data
	 * @param dst the dst
	 */
	public static void coarseRegrid(double[] data, double[] dst) {
		double stretch = (double) dst.length / data.length;
		for(int i=data.length; --i >= 0; ) dst[(int)Math.floor(stretch * i)] += data[i];
	}	
	
	/**
	 * Coarse regrid.
	 *
	 * @param data the data
	 * @param dst the dst
	 */
	public static void coarseRegrid(float[] data, float[] dst) {
		double stretch = (double) dst.length / data.length;
		for(int i=data.length; --i >= 0; ) dst[(int)Math.floor(stretch * i)] += data[i];
	}
	
	
	
	/**
	 * Coarse regrid of.
	 *
	 * @param array the array
	 * @param stretch the stretch
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object coarseRegridOf(Object array, double[] stretch) throws IllegalArgumentException {
		int[] size = getShape(array);
		for(int i=size.length; --i >= 0; ) size[i] = (int)Math.round(stretch[i] * size[i]);
		
		Object dst = createArray(getClass(array), size);		
		coarseRegrid(array, dst);
		return dst;
	}
	
	/**
	 * Coarse regrid.
	 *
	 * @param src the src
	 * @param dst the dst
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void coarseRegrid(Object src, Object dst) {
		if(!src.getClass().equals(dst.getClass())) throw new IllegalArgumentException("src and dst are different types.");
		// For additive, do averaging...		
		if(src instanceof Additive[]) {
			Additive[] srcArray = (Additive[]) src;
			Additive[] dstArray = (Additive[]) dst;
			double stretch = (double) dstArray.length / srcArray.length; 
			for(int i=srcArray.length; --i >= 0; ) dstArray[(int)Math.floor(stretch * i)].add(srcArray[i]);
		}
		// Else just create a rough sampling of Object contents...
		else if(src instanceof Object[]) {
			Object[] srcArray = (Object[]) src;
			Object[] dstArray = (Object[]) dst;
			double stretch = (double) dstArray.length / srcArray.length;
			for(int i=srcArray.length; --i >= 0; ) coarseRegrid(srcArray[i], dstArray[(int)Math.round(stretch * i)]);			
		}
		else if(src.getClass().isArray()) {
			if(src instanceof double[]) coarseRegrid((double[]) src, (double[]) dst);
			else if(src instanceof float[]) coarseRegrid((float[]) src, (float[]) dst);
			else throw new IllegalArgumentException("Cannot regrid arrays of " + getClass(src));
		}
	}
	
	/**
	 * Smooth regrid of.
	 *
	 * @param array the array
	 * @param stretch the stretch
	 * @return the object
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Object smoothRegridOf(Object array, double[] stretch) throws IllegalArgumentException {
		Class<?> type = getClass(array);
		
		Object coarse;
		
		if(type.equals(double.class)) coarse = coarseRegridOf(array, stretch);
		else if(type.equals(float.class)) coarse = asDouble(coarseRegridOf(array, stretch));
		else throw new IllegalArgumentException("Cannot smoothly regrid arrays of " + type.getName());
		
		double[] smoothFWHM = new double[stretch.length];
		
		for(int i=smoothFWHM.length; --i >= 0; ) smoothFWHM[i] = stretch[i] <= 1.0 ? 0.0 : stretch[i];
		
		return getSmoothed(coarse, getGaussianBeam(smoothFWHM, 3.0+smoothFWHM.length));
	}
	
	// TODO resample via FFT
	// TODO convolve via FFT
	
	

	/**
	 * Adds the pin at.
	 *
	 * @param data the data
	 * @param point the point
	 * @param pos the pos
	 */
	public static void addPinAt(Object data, Object point, double[] pos) {
		addPinAt(data, point, 1.0, pos, 0);
	}
	
	/**
	 * Adds the pin at.
	 *
	 * @param <T> the generic type
	 * @param data the data
	 * @param point the point
	 * @param scale the scale
	 * @param pos the pos
	 * @param depth the depth
	 */
	@SuppressWarnings("unchecked")
	private static <T> void addPinAt(Object data, T point, double scale, double[] pos, int depth) {
		if(data instanceof LinearAlgebra) {
			((LinearAlgebra<T>) data).addScaled(point, scale);		
		}
		else {
			int ipos = (int) pos[depth];
	
			if(data instanceof Object[]) {
				Object[] array = (Object[]) data;	
				addPinAt(array[ipos], point, scale, pos, depth+1);		
			}
			else if(data.getClass().isArray()) {
				if(data instanceof double[]) {
					double[] array = (double[]) data;
					double value = (Double) point;
					array[ipos] += scale*value;			
				}		
			}
			else throw new IllegalArgumentException("Type " + data.getClass().getName() + "is not supported for scaled addition.");
		}
	}
	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString (double[] data) { return toString(data, Util.e3); }

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(double[] data, NumberFormat df) {
		return toString(data, df, ", ");
	}

	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString (double[][] data) { return toString(data, Util.e3); }

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(double[][] data, NumberFormat df) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i], df) + "\n";
		return text;
	}

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(double[] data, NumberFormat df, String separator) { 
		String text = new String();
		for(int i=0; i<data.length; i++) {
			text += df.format(data[i]); 
			if(i < data.length-1) text += separator;
		}
		return text;
	}	
	
	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString (float[] data) { return toString(data, Util.e3); }

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(float[] data, NumberFormat df) {
		return toString(data, df, ", ");
	}

	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString (float[][] data) { return toString(data, Util.e3); }

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(float[][] data, NumberFormat df) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i], df) + "\n";
		return text;
	}

	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(float[] data, NumberFormat df, String separator) { 
		String text = new String();
		for(int i=0; i<data.length; i++) {
			text += df.format(data[i]); 
			if(i < data.length-1) text += separator;
		}
		return text;
	}	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(NumberFormating[] data) { return toString(data, Util.e3); } 
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(NumberFormating[] data, NumberFormat df) {
		return toString(data, df, ", ");
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param nf the nf
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(NumberFormating[] data, NumberFormat nf, String separator) { 
		String text = new String();
		for(int i=0; i<data.length; i++) {
			text += data[i].toString(nf); 
			if(i < data.length-1) text += separator;
		}
		return text;
	}	

	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(NumberFormating[][] data) { return toString(data, Util.e3); }
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param nf the nf
	 * @return the string
	 */
	public static String toString(NumberFormating[][] data, NumberFormat nf) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i], nf) + "\n";
		return text;
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(Object[] data) { 
		return toString(data, ", ");
	}	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(Object[] data, String separator) { 
		String text = new String();
		for(int i=0; i<data.length; i++) {
			text += data[i].toString(); 
			if(i < data.length-1) text += separator;
		}
		return text;
	}	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(Object[][] data) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i]) + "\n";
		return text;
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(int[] data) {
		return toString(data, ", ");
	}
	
	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(int[] data, String separator) { 
		String text = "{";
		for(int i=0; i<data.length; i++) {
			text += data[i];
			if(i < data.length-1) text += separator;
		}
		return text + "}";
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(int[] data, NumberFormat df) {
		return toString(data, df, ", ");
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @param separator the separator
	 * @return the string
	 */
	public static String toString(int[] data, NumberFormat df, String separator) { 
		String text = new String();
		for(int i=0; i<data.length; i++) {
			text += df.format(data[i]); 
			if(i < data.length-1) text += separator;
		}
		return text;
	}	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(int[][] data) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i]) + "\n";
		return text;
	}
	
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param df the df
	 * @return the string
	 */
	public static String toString(int[][] data, NumberFormat df) {
		String text = new String();
		for(int i=0; i<data.length; i++) text += toString(data[i], df) + "\n";
		return text;
	}
	
	// TODO generic toString() representations:
	//	Type[d1][d2]...[dn] = { firstElement, ... , lastElement }
	//  verbose mode to list 1D and 2D arrays.                  
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toString(Object data) {
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			String text = "{";
			for(int i=0; i<array.length; i++) {
				text += toString(array[i]);
				if(i < array.length-1) text += ",";
			}
			return text + "}";
		}
		else if(data.getClass().isArray()) {
			if(data instanceof double[]) return "{" + toString((double[]) data) + "}";
			else if(data instanceof float[]) return "{" + toString((float[]) data) + "}";
			else if(data instanceof int[]) return "{" + toString((int[]) data) + "}";
			else return data.toString();
		}
		else return data.toString();
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param nf the nf
	 * @return the string
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static String toString(Object data, NumberFormat nf) throws IllegalArgumentException {
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			String text = "{";
			for(int i=0; i<array.length; i++) {
				text += toString(array[i]);
				if(i < array.length-1) text += ",";
			}
			return text + "}";
		}
		else if(data.getClass().isArray()) {
			if(data instanceof double[]) return "{" + toString((double[]) data, nf) + "}";
			else if(data instanceof float[]) return "{" + toString((float[]) data, nf) + "}";
			else if(data instanceof int[]) return "{" + toString((int[]) data, nf) + "}";
			else return data.toString();
		}
		else if(data instanceof NumberFormating) return ((NumberFormating) data).toString(nf);
		else throw new IllegalArgumentException("Array contains non NumberFormating elements.");
	}
	
	/**
	 * To string.
	 *
	 * @param data the data
	 * @param decimals the decimals
	 * @return the string
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static String toString(Object data, int decimals) throws IllegalArgumentException {
		if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			String text = "{";
			for(int i=0; i<array.length; i++) text += toString(array[i]) + ",";
			return text + "}";
		}
		else if(data.getClass().isArray()) {
			if(data instanceof double[]) return "{" + toString((double[]) data, Util.s[decimals]) + "}";
			else if(data instanceof float[]) return "{" + toString((float[]) data, Util.s[decimals]) + "}";
			else if(data instanceof int[]) return "{" + toString((int[]) data, Util.s[decimals]) + "}";
			else return data.toString();
		}
		else if(data instanceof DecimalFormating) return ((DecimalFormating) data).toString(decimals);
		else throw new IllegalArgumentException("Array contains non DecimalFormating elements.");
	}
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @param type the type
	 * @return the object
	 * @throws ParseException the parse exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InstantiationException the instantiation exception
	 */
	public static Object parse(String text, Class<?> type) throws ParseException, IllegalAccessException, InstantiationException {
		text.trim();
		
		// If it's an array, create storage and fill it with the parse elements...
		if(text.charAt(0) != '{' && text.charAt(text.length()-1) != '}') {
			BracketedListTokenizer tokens = new BracketedListTokenizer(text.substring(1,text.length()-1));
			int size = tokens.countTokens();
			Object element = parse(tokens.nextToken(), type);
			
			// If it's a simple array of numbers. Parse as necessary
			if(element instanceof Number) {
				Number number = (Number) element;
				if(type.equals(double.class)) {
					double[] array = new double[size];
					array[0] = number.doubleValue();
					for(int i=1; i<array.length; i++) array[i] = Double.parseDouble(tokens.nextToken());
					return array;
				}
				else if(type.equals(float.class)) {
					float[] array = new float[size];
					array[0] = number.floatValue();
					for(int i=1; i<array.length; i++) array[i] = Float.parseFloat(tokens.nextToken());
					return array;
				}
				else if(type.equals(int.class)) {
					int[] array = new int[size];
					array[0] = number.intValue();
					for(int i=1; i<array.length; i++) array[i] = Integer.decode(tokens.nextToken());
					return array;
				}
				else if(type.equals(long.class)) {
					long[] array = new long[size];
					array[0] = number.longValue();
					for(int i=1; i<array.length; i++) array[i] = Long.decode(tokens.nextToken());
					return array;
				}
				else if(type.equals(short.class)) {
					short[] array = new short[size];
					array[0] = number.shortValue();
					for(int i=1; i<array.length; i++) array[i] = Short.decode(tokens.nextToken());
					return array;
				}
				else if(type.equals(byte.class)) {
					byte[] array = new byte[size];
					array[0] = number.byteValue();
					for(int i=1; i<array.length; i++) array[i] = Byte.decode(tokens.nextToken());
					return array;
				}
				else throw new IllegalArgumentException("Cannot parse arrays of " + type.getName());
			}
			// If entries are booleans, then parse accordingly
			else if(element instanceof Boolean) {
				boolean[] array = new boolean[size];
				array[0] = ((Boolean) element).booleanValue();
				for(int i=1; i<array.length; i++) array[i] = Util.parseBoolean(text);
				return array;
			}
			// Otherwise parse as Objects...
			else {
				Object[] array = (Object[]) Array.newInstance(element.getClass(), size);
				array[0] = element;
				for(int i=1; i<array.length; i++) array[i] = parse(tokens.nextToken(), type);
				return array;
			}			
		}
		
		// If the text is not an array but an element, return the element...
		else if(text.charAt(0) != '{' && text.charAt(text.length()-1) != '}') {
			if(type.isPrimitive()) {
				if(type.equals(double.class)) return Double.parseDouble(text);
				else if(type.equals(float.class)) return Float.parseFloat(text);
				else if(type.equals(int.class)) return Integer.decode(text);
				else if(type.equals(long.class)) return Long.decode(text);
				else if(type.equals(short.class)) return Short.decode(text);
				else if(type.equals(byte.class)) return Byte.decode(text);
				else if(type.equals(boolean.class)) return Util.parseBoolean(text);
				else throw new IllegalArgumentException("Cannot parse arrays of " + type.getName());
			}
			else if(Parser.class.isAssignableFrom(type)) {
				Parser element = (Parser) type.newInstance();
				element.parse(text);
				return element;
			}
			else if(type.equals(String.class)) return text;				
			else throw new IllegalArgumentException("Cannot parse arrays of " + type.getName());
		}
		else throw new IllegalArgumentException("Parsing argument is not a well-formed array.");
	}
	

	

	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @param data the data
	 * @throws ParseException the parse exception
	 * @throws NumberFormatException the number format exception
	 */
	public static void parse(String text, Object data) throws ParseException, NumberFormatException {
		text.trim();
		if(data instanceof String) {
			data = text;
		}
		else if(data instanceof Parser) {
			((Parser) data).parse(text);
		}
		else if(data instanceof Object[]) {
			Object[] array = (Object[]) data;
			if(text.charAt(0) != '{' || text.charAt(text.length()-1) != '}') throw new IllegalArgumentException("Parsing argument is not a well-formed array.");
			BracketedListTokenizer tokens = new BracketedListTokenizer(text.substring(1,text.length()-1));
			if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
			for(int i=0; i<array.length; i++) parse(tokens.nextToken(), array[i]);
		}
		else if(data.getClass().isArray()) {
			Class<?> type = getClass(data);
			if(text.charAt(0) != '{' || text.charAt(text.length()-1) != '}') throw new IllegalArgumentException("Parsing argument is not a well-formed array.");
			BracketedListTokenizer tokens = new BracketedListTokenizer(text.substring(1,text.length()-1));
			
			if(type.equals(double.class)) {
				double[] array = (double[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Double.parseDouble(tokens.nextToken());
			}
			else if(type.equals(float.class)) {
				float[] array = (float[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Float.parseFloat(tokens.nextToken());
			}
			else if(type.equals(int.class)) {
				int[] array = (int[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Integer.decode(tokens.nextToken());
			}
			else if(type.equals(long.class)) {
				long[] array = (long[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Long.decode(tokens.nextToken());
			}
			else if(type.equals(short.class)) {
				short[] array = (short[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Short.decode(tokens.nextToken());
			}
			else if(type.equals(byte.class)) {
				byte[] array = (byte[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Byte.decode(tokens.nextToken());
			}
			else if(type.equals(boolean.class)) {
				boolean[] array = (boolean[]) data;
				if(tokens.countTokens() != array.length) throw new IllegalArgumentException("Mismatched text array and receiving data sizes.");
				for(int i=0; i<array.length; i++) array[i] = Util.parseBoolean(tokens.nextToken());
			}
			else throw new IllegalArgumentException("Cannot parse arrays of " + type.getName());	
		}
	}
	
}

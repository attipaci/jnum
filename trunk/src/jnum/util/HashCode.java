/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class HashCode.
 */
public final class HashCode {

	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int get(long value) {
		return (int)(value & 0xFFFF) ^ (int)((value >> 32) & 0xFFFF);
	}
	
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int get(float value) {
		return Float.floatToIntBits(value);
	}
	
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int get(double value) {
		return get(Double.doubleToLongBits(value));
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(boolean[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(boolean[] values, int from, int to) {
		int shift = 0;
		int hash = from ^ to;
		int current = 0;
		for(int i=from; i<to; i++, shift++) {
			if(shift == 32) {
				hash ^= current + i;
				shift = current = 0;
			}
			if(values[i]) current |= 1 << shift; 
		}
		return hash ^ current;
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(byte[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(byte[] values, int from, int to) {
		int shift = 0;
		int hash = from ^ to;
		int current = 0;
		for(int i=from; i<to; i++, shift+=8) {
			if(shift == 32) {
				hash ^= current + i;
				shift = current = 0;
			}
			current |= values[i] << shift; 
		}
		return hash ^ current;
		
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(short[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(short[] values, int from, int to) {
		int shift = 0;
		int hash = from ^ to;
		int current = 0;
		for(int i=from; i<to; i++, shift+=16) {
			if(shift == 32) {
				hash ^= current + i;
				shift = current = 0;
			}
			current |= values[i] << shift; 
		}
		return hash ^ current;
		
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(int[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(int[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= values[i] + i; 
		return hash;
	}
	
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(long[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(long[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= get(values[i]) + i; 
		return hash;
	}
	
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(float[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(float[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= get(values[i]) + i; 
		return hash;		
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(double[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(double[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= get(values[i]) + i; 
		return hash;		
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int get(Object[] values) { return get(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int get(Object[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) {
			Object entry = values[i];
			if(entry instanceof Object[]) hash ^= get((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= get((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= get((byte[]) entry);
			else if(entry instanceof short[]) hash ^= get((short[]) entry);
			else if(entry instanceof int[]) hash ^= get((int[]) entry);
			else if(entry instanceof float[]) hash ^= get((float[]) entry);
			else if(entry instanceof long[]) hash ^= get((long[]) entry);
			else if(entry instanceof double[]) hash ^= get((double[]) entry);
			else hash ^= entry.hashCode() + i; 
		}
		return hash;		
	}
	
	public static int get(List<?> values) { return get(values, 0, values.size()); }
	
	public static int get(List<?> values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) {
			Object entry = values.get(i);
			if(entry instanceof Object[]) hash ^= get((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= get((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= get((byte[]) entry);
			else if(entry instanceof short[]) hash ^= get((short[]) entry);
			else if(entry instanceof int[]) hash ^= get((int[]) entry);
			else if(entry instanceof float[]) hash ^= get((float[]) entry);
			else if(entry instanceof long[]) hash ^= get((long[]) entry);
			else if(entry instanceof double[]) hash ^= get((double[]) entry);
			else hash ^= entry.hashCode() + i; 
		}
		return hash;		
	}
	
	
	// Get the first 8, the last 8, and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(boolean[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		int samples = 0;
		for(int i=0, j=8; i<16; i++, j+=step) if(values[j]) samples |= 1 << i;
		return hash ^ samples;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(byte[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		int samples = 0;
		for(int i=0, j=8, shift=0; i<16; i++, j+=step, shift+=8) {
			if(shift == 32) {
				hash ^= samples;
				shift = samples = 0;
			}
			samples |= values[j] << shift;
		}
		return hash ^ samples;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(short[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		int samples = 0;
		for(int i=0, j=8, shift=0; i<16; i++, j+=step, shift+=16) {
			if(shift == 32) {
				hash ^= samples;
				shift = samples = 0;
			}
			samples |= values[j] << shift;
		}
		return hash ^ samples;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(int[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= values[j];
		return hash;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(long[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= get(values[j]);
		return hash;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(float[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= get(values[j]);
		return hash;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(double[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= get(values[j]);
		return hash;
	}
	
	// Get the first 8, last 8 and 16 scattered
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(Object[] values) {
		if(values.length < 32) return get(values, 0, values.length);
		int hash = get(values, 0, 8) ^ get(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) {
			Object entry = values[j];
			if(entry instanceof Object[]) hash ^= sampleFrom((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= sampleFrom((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= sampleFrom((byte[]) entry);
			else if(entry instanceof short[]) hash ^= sampleFrom((short[]) entry);
			else if(entry instanceof int[]) hash ^= sampleFrom((int[]) entry);
			else if(entry instanceof float[]) hash ^= sampleFrom((float[]) entry);
			else if(entry instanceof long[]) hash ^= sampleFrom((long[]) entry);
			else if(entry instanceof double[]) hash ^= sampleFrom((double[]) entry);
			else hash ^= entry.hashCode();
		}
		return hash;
	}
	
	public static int sampleFrom(List<?> values) {
		if(values.size() < 32) return get(values, 0, values.size());
		int hash = get(values, 0, 8) ^ get(values, values.size()-8, values.size());
		int step = (values.size() - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) {
			Object entry = values.get(j);
			if(entry instanceof Object[]) hash ^= sampleFrom((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= sampleFrom((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= sampleFrom((byte[]) entry);
			else if(entry instanceof short[]) hash ^= sampleFrom((short[]) entry);
			else if(entry instanceof int[]) hash ^= sampleFrom((int[]) entry);
			else if(entry instanceof float[]) hash ^= sampleFrom((float[]) entry);
			else if(entry instanceof long[]) hash ^= sampleFrom((long[]) entry);
			else if(entry instanceof double[]) hash ^= sampleFrom((double[]) entry);
			else hash ^= entry.hashCode();
		}
		return hash;
	}
	
}


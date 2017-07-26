/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.util;

import java.util.List;

import jnum.ExtraMath;
import jnum.data.image.Value2D;
import jnum.data.samples.Value1D;

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
	public static int from(long value) {
		return (int)(value & 0xFFFF) ^ (int)((value >> 32) & 0xFFFF);
	}
	
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int from(float value) {
		return Float.floatToIntBits(value);
	}
	
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int from(double value) {
		return from(Double.doubleToLongBits(value));
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(boolean[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(boolean[] values, int from, int to) {
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
	public static int from(byte[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(byte[] values, int from, int to) {
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
	public static int from(short[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(short[] values, int from, int to) {
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
	public static int from(int[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(int[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= values[i]; 
		return hash;
	}
	
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(long[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(long[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= from(values[i]); 
		return hash;
	}
	
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(float[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(float[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= from(values[i]); 
		return hash;		
	}
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(double[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(double[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) hash ^= from(values[i]); 
		return hash;		
	}
	

    /**
     * Gets the.
     *
     * @param values the values
     * @return the int
     */
    public static int from(Value1D values) { return from(values, 0, values.size()); }
    
    /**
     * Gets the.
     *
     * @param values the values
     * @param from the from
     * @param to the to
     * @return the int
     */
    public static int from(Value1D values, int from, int to) {
        int hash = from ^ to;
        for(int i=from; i<to; i++) hash ^= values.get(i).hashCode(); 
        return hash;        
    }
	
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(Object[] values) { return from(values, 0, values.length); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(Object[] values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) {
			Object entry = values[i];
			if(entry instanceof Object[]) hash ^= from((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= from((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= from((byte[]) entry);
			else if(entry instanceof short[]) hash ^= from((short[]) entry);
			else if(entry instanceof int[]) hash ^= from((int[]) entry);
			else if(entry instanceof float[]) hash ^= from((float[]) entry);
			else if(entry instanceof long[]) hash ^= from((long[]) entry);
			else if(entry instanceof double[]) hash ^= from((double[]) entry);
			else hash ^= entry.hashCode() + i; 
		}
		return hash;		
	}
	
	/**
	 * From.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int from(List<?> values) { return from(values, 0, values.size()); }
	
	/**
	 * Gets the.
	 *
	 * @param values the values
	 * @param from the from
	 * @param to the to
	 * @return the int
	 */
	public static int from(List<?> values, int from, int to) {
		int hash = from ^ to;
		for(int i=from; i<to; i++) {
			Object entry = values.get(i);
			if(entry instanceof Object[]) hash ^= from((Object[]) entry);
			else if(entry instanceof boolean[]) hash ^= from((boolean[]) entry);
			else if(entry instanceof byte[]) hash ^= from((byte[]) entry);
			else if(entry instanceof short[]) hash ^= from((short[]) entry);
			else if(entry instanceof int[]) hash ^= from((int[]) entry);
			else if(entry instanceof float[]) hash ^= from((float[]) entry);
			else if(entry instanceof long[]) hash ^= from((long[]) entry);
			else if(entry instanceof double[]) hash ^= from((double[]) entry);
			else hash ^= entry.hashCode() + i; 
		}
		return hash;		
	}
	
	public static int from(Value2D values) {
	    int hash = 0;
	    for(int i=values.sizeX(); --i >= 0; ) for(int j=values.sizeY(); --j >=0; ) hash ^= values.get(i,  j).hashCode();
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= from(values[j]);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= from(values[j]);
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
		int step = (values.length - 16) >> 4;
		for(int i=0, j=8; i<16; i++, j+=step) hash ^= from(values[j]);
		return hash;
	}
	
	
	// Get the first 8, last 8 and 16 scattered
    /**
     * Sample from.
     *
     * @param values the values
     * @return the int
     */
    public static int sampleFrom(Value1D values) {
        if(values.size() < 32) return from(values, 0, values.size());
        int hash = from(values, 0, 8) ^ from(values, values.size()-8, values.size());
        int step = (values.size() - 16) >> 4;
        for(int i=0, j=8; i<16; i++, j+=step) hash ^= values.get(j).hashCode();
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
		if(values.length < 32) return from(values, 0, values.length);
		int hash = from(values, 0, 8) ^ from(values, values.length-8, values.length);
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
	
	/**
	 * Sample from.
	 *
	 * @param values the values
	 * @return the int
	 */
	public static int sampleFrom(List<?> values) {
		if(values.size() < 32) return from(values, 0, values.size());
		int hash = from(values, 0, 8) ^ from(values, values.size()-8, values.size());
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
	
	
	public static int sampleFrom(Value2D values) {
	    final int maxSamples = 32;
	    int n = values.sizeX() * values.sizeY();
	    
        if(n < maxSamples) return from(values);
       
        int hash = 0;
        
        // Evenly sampled between first and last...
        for(int k=maxSamples; --k >= 0; ) {
            int m = ExtraMath.roundupRatio(k * (n-1), maxSamples);
            hash ^= values.get(m / values.sizeY(), m % values.sizeY()).hashCode();
        }
       
        return hash;
    }
   
	
}


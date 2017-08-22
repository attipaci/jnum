/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;

import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class Counter.
 */
public class Counter {
	
	/** The value. */
	public double value = 0;
	
	/**
	 * Instantiates a new counter.
	 */
	public Counter() {}
	
	/**
	 * Instantiates a new counter.
	 *
	 * @param start the start
	 */
	public Counter(double start) { value = start; }
	
	@Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(value); }
	
	@Override
    public boolean equals(Object o) {
	    if(o == this) return true;
	    if(!(o instanceof Counter)) return false;
	    
	    Counter c = (Counter) o;
	    return value == c.value;
	}
	
	/**
	 * Increment.
	 */
	public void increment() { value++; }
	
	/**
	 * Decrement.
	 */
	public void decrement() { value--; }
	
	/**
	 * Reset.
	 */
	public void reset() { value = 0; }

}
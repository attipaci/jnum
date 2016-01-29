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
// Copyright (c) 2010 Attila Kovacs 

package jnum.dirfile;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class SBitStore.
 */
public class SBitStore extends DataStore<Long> {
	
	/** The container. */
	DataStore<? extends Number> container;
	
	/** The cmask. */
	long mask = 0, cmask;
	
	/** The shift. */
	int shift;
	
	/**
	 * Instantiates a new s bit store.
	 *
	 * @param name the name
	 * @param bits the bits
	 * @param position the position
	 */
	public SBitStore(String name, DataStore<? extends Number> bits, int position) {
		super(name);
		this.container = bits;
		shift = position;
		cmask = 1; 
	}
	
	/**
	 * Instantiates a new s bit store.
	 *
	 * @param name the name
	 * @param bits the bits
	 * @param from the from
	 * @param n the n
	 */
	public SBitStore(String name, DataStore<? extends Number> bits, int from, int n) {
		super(name);
		this.container = bits;
		shift = from;
		for(int i=0; i<n-1; i++) mask |= 1 << i;
		cmask = 1 << (n-1);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Long get(long n) throws IOException {
		long value = container.get(n).longValue() >> shift;
		return (value & mask) - (value & cmask);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return container.getSamples();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return container.length();
	}
	

}

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

import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class BitStore.
 */
public class BitStore extends DataStore<Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6409444230976963190L;

	/** The container. */
	DataStore<? extends Number> container;
	
	/** The mask. */
	long mask = 0;
	
	/** The shift. */
	int shift;
	
	/**
	 * Instantiates a new bit store.
	 *
	 * @param name the name
	 * @param bits the bits
	 * @param position the position
	 */
	public BitStore(String name, DataStore<? extends Number> bits, int position) {
		super(name);
		this.container = bits;
		mask = 1 << position; 
		shift = position;
	}
	
	@Override
	public int hashCode() { return super.hashCode() ^ container.hashCode() ^ shift ^ HashCode.get(mask); }
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof BitStore)) return false;
		if(!super.equals(o)) return false;
		BitStore store = (BitStore) o;
		if(!container.equals(store.container)) return false;
		if(mask != store.mask) return false;
		if(shift != store.shift) return false;
		return true;
	}
	
	/**
	 * Instantiates a new bit store.
	 *
	 * @param name the name
	 * @param bits the bits
	 * @param from the from
	 * @param n the n
	 */
	public BitStore(String name, DataStore<? extends Number> bits, int from, int n) {
		super(name);
		this.container = bits;
		shift = from;
		for(int i=0; i<n; i++, from++) mask |= 1 << from;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Long get(long n) throws IOException {
		return (container.get(n).longValue() & mask) >> shift;
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

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
// Copyright (c) 2010 Attila Kovacs 

package jnum.io.dirfile;

import java.io.IOException;

import jnum.util.HashCode;


public class BitStore extends DataStore<Long> {
	

	private static final long serialVersionUID = 6409444230976963190L;

	DataStore<? extends Number> container;

	long mask = 0;

	int shift;
	

	public BitStore(String name, DataStore<? extends Number> bits, int position) {
		super(name);
		this.container = bits;
		mask = 1 << position; 
		shift = position;
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#hashCode()
	 */
	@Override
	public int hashCode() { return super.hashCode() ^ container.hashCode() ^ shift ^ HashCode.from(mask); }
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#equals(java.lang.Object)
	 */
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
	

	public BitStore(String name, DataStore<? extends Number> bits, int from, int n) {
		super(name);
		this.container = bits;
		shift = from;
		for(int i=0; i<n; i++, from++) mask |= 1 << from;
	}
	
	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#get(long)
	 */
	@Override
	public Long get(long n) throws IOException {
		return (container.get(n).longValue() & mask) >>> shift;
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return container.getSamples();
	}
	
	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return container.length();
	}


}

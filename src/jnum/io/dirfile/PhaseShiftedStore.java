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


public class PhaseShiftedStore<Type extends Number> extends DataStore<Type> {

	private static final long serialVersionUID = -5398091531703755110L;

	DataStore<Type> data;

	long shift;
	

	public PhaseShiftedStore(String name, DataStore<Type> data, long shift) {
		super(name);
		this.data = data;
		this.shift = shift;
	}

	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ data.hashCode() ^ HashCode.from(shift);
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof PhaseShiftedStore)) return false;
		if(!super.equals(o)) return false;
		PhaseShiftedStore<?> store = (PhaseShiftedStore<?>) o;
		if(!data.equals(store.data)) return false;
		if(shift != store.shift) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#get(long)
	 */
	@Override
	public Type get(long n) throws IOException {
		return data.get(n + shift);
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return data.getSamples();
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return data.length();
	}

}

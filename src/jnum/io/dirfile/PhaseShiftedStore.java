/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

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

	@Override
	public int hashCode() {
		return super.hashCode() ^ data.hashCode() ^ HashCode.from(shift);
	}

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

	@Override
	public Type get(long n) throws IOException {
		return data.get(n + shift);
	}

	@Override
	public int getSamples() {
		return data.getSamples();
	}

	@Override
	public long length() throws IOException {
		return data.length();
	}

}

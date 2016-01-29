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
 * The Class PhaseShiftedStore.
 *
 * @param <Type> the generic type
 */
public class PhaseShiftedStore<Type extends Number> extends DataStore<Type> {
	
	/** The data. */
	DataStore<Type> data;
	
	/** The shift. */
	long shift;
	
	/**
	 * Instantiates a new phase shifted store.
	 *
	 * @param name the name
	 * @param data the data
	 * @param shift the shift
	 */
	public PhaseShiftedStore(String name, DataStore<Type> data, long shift) {
		super(name);
		this.data = data;
		this.shift = shift;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Type get(long n) throws IOException {
		return data.get(n + shift);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return data.getSamples();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return data.length();
	}

}

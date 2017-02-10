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

// TODO: Auto-generated Javadoc
/**
 * The Class UByteStore.
 */
public class UByteStore extends RawStore<Short> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3209183933186574490L;

	/**
	 * Instantiates a new u byte store.
	 *
	 * @param path the path
	 * @param name the name
	 * @param arraySize the array size
	 */
	public UByteStore(String path, String name, int arraySize) {
		super(path, name, arraySize);
		bytes = 1;
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#get(long)
	 */
	@Override
	public Short get(long n) throws IOException {
		return getUnsignedByte(n);
	}

}

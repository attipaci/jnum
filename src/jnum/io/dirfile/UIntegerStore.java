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

package jnum.io.dirfile;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class UIntegerStore.
 */
public class UIntegerStore extends RawStore<Long> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2886396848639213765L;

	/**
	 * Instantiates a new u integer store.
	 *
	 * @param path the path
	 * @param name the name
	 * @param arraySize the array size
	 */
	public UIntegerStore(String path, String name, int arraySize) {
		super(path, name, arraySize);
		bytes = 4;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Long get(long n) throws IOException {
		return getUnsignedInt(n);
	}

}
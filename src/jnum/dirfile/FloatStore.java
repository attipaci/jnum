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
 * The Class FloatStore.
 */
public class FloatStore extends Raw<Float> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5365409325445573181L;

	/**
	 * Instantiates a new float store.
	 *
	 * @param path the path
	 * @param name the name
	 * @param arraySize the array size
	 */
	public FloatStore(String path, String name, int arraySize) {
		super(path, name, arraySize);
		bytes = 4;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Float get(long n) throws IOException {
		return getFloat(n);
	}

}

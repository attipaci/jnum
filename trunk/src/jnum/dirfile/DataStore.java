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
 * The Class DataStore.
 *
 * @param <Type> the generic type
 */
public abstract class DataStore<Type extends Number> {
	
	/** The name. */
	String name;
	
	/**
	 * Instantiates a new data store.
	 *
	 * @param name the name
	 */
	public DataStore(String name) {
		this.name = name; 
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }
	
	/**
	 * Gets the.
	 *
	 * @param n the n
	 * @return the type
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract Type get(long n) throws IOException;

	/**
	 * Gets the samples.
	 *
	 * @return the samples
	 */
	public abstract int getSamples();
	
	/**
	 * Length.
	 *
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract long length() throws IOException;
	
}

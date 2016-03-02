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

import jnum.data.SimpleInterpolator;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearInterpolatorStore.
 */
public class LinearInterpolatorStore extends DataStore<Double> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1972465553588256489L;

	/** The raw. */
	protected DataStore<?> raw;
	
	/** The file name. */
	protected String fileName;
	
	/** The table. */
	protected SimpleInterpolator table;
	
	/**
	 * Instantiates a new linear interpolator store.
	 *
	 * @param name the name
	 * @param value the value
	 * @param fileName the file name
	 */
	public LinearInterpolatorStore(String name, DataStore<?> value, String fileName) {
		super(name);
		this.fileName = fileName;
		raw = value;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ raw.hashCode() ^ fileName.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof LinearInterpolatorStore)) return false;
		if(!super.equals(o)) return false;
		LinearInterpolatorStore store = (LinearInterpolatorStore) o;
		if(!fileName.equals(store.fileName)) return false;
		if(!raw.equals(store.raw)) return false;
		return true;
	}
	
	// Load interpolation table only upon request...
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Double get(long n) throws IOException {
		if(table == null) load();
		return table.getValue(raw.get(n).doubleValue());
	}
	
	/**
	 * Load.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load() throws IOException {
		table = new SimpleInterpolator(fileName);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return raw.getSamples();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return raw.length();
	}
 	
}

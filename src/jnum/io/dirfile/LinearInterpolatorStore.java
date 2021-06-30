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
// Copyright (c) 2010 Attila Kovacs 

package jnum.io.dirfile;


import java.io.IOException;

import jnum.data.SimpleInterpolator;


public class LinearInterpolatorStore extends DataStore<Double> {

	private static final long serialVersionUID = 1972465553588256489L;

	protected DataStore<?> raw;

	protected String fileName;

	protected SimpleInterpolator table;
	

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
	@Override
	public Double get(long n) throws IOException {
		if(table == null) load();
		return table.getValue(raw.get(n).doubleValue());
	}
	

	public void load() throws IOException {
		table = new SimpleInterpolator(fileName);
	}
	
	@Override
	public int getSamples() {
		return raw.getSamples();
	}

	@Override
	public long length() throws IOException {
		return raw.length();
	}
 	
}

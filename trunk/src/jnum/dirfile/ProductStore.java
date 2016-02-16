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

import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class ProductStore.
 */
public class ProductStore extends DataStore<Double> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5702369503063112685L;

	/** The b. */
	DataStore<?> a,b;
	
	/** The index scale. */
	double indexScale;
	
	/**
	 * Instantiates a new product store.
	 *
	 * @param name the name
	 * @param a the a
	 * @param b the b
	 */
	public ProductStore(String name, DataStore<?> a, DataStore<?> b) {
		super(name);
		this.a = a;
		this.b = b;
		indexScale = b.getSamples() / a.getSamples();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ a.hashCode() ^ b.hashCode() ^ HashCode.get(indexScale);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof ProductStore)) return false;
		if(!super.equals(o)) return false;
		ProductStore store = (ProductStore) o;
		if(indexScale != store.indexScale) return false;
		if(!a.equals(store.a)) return false;
		if(!b.equals(store.b)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Double get(long n) throws IOException {
		return a.get(n).doubleValue() * b.get(Math.round(indexScale * n)).doubleValue();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return a.getSamples();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return a.length();
	}

}

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


public class ProductStore extends DataStore<Double> {

	private static final long serialVersionUID = 5702369503063112685L;

	DataStore<?> a,b;

	double indexScale;
	

	public ProductStore(String name, DataStore<?> a, DataStore<?> b) {
		super(name);
		this.a = a;
		this.b = b;
		indexScale = b.getSamples() / a.getSamples();
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ a.hashCode() ^ b.hashCode() ^ HashCode.from(indexScale);
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#equals(java.lang.Object)
	 */
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
	 * @see jnum.dirfile.DataStore#get(long)
	 */
	@Override
	public Double get(long n) throws IOException {
		return a.get(n).doubleValue() * b.get(Math.round(indexScale * n)).doubleValue();
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return a.getSamples();
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return a.length();
	}

}

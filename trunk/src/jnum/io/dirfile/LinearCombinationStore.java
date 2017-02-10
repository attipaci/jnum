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
import java.util.ArrayList;

import jnum.math.Vector2D;
import jnum.util.HashCode;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearCombinationStore.
 */
public class LinearCombinationStore extends DataStore<Double> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2557496368888356380L;

	/** The terms. */
	private ArrayList<DataStore<?>> terms = new ArrayList<DataStore<?>>();
	
	/** The coeffs. */
	private ArrayList<Vector2D> coeffs = new ArrayList<Vector2D>();
	
	/** The index scale. */
	private ArrayList<Double> indexScale = new ArrayList<Double>();

	/**
	 * Instantiates a new linear combination store.
	 *
	 * @param name the name
	 */
	public LinearCombinationStore(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		for(DataStore<?> term : terms) hash ^= term.hashCode();
		for(Vector2D coeff : coeffs) hash ^= coeff.hashCode();
		for(Double x : indexScale) hash ^= HashCode.from(x);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof LinearCombinationStore)) return false;
		if(!super.equals(o)) return false;
		LinearCombinationStore store = (LinearCombinationStore) o;
		
		if(terms.size() != store.terms.size()) return false;
		if(coeffs.size() != store.coeffs.size()) return false;
		if(indexScale.size() != store.indexScale.size()) return false;

		for(int i=terms.size(); --i >= 0; ) if(!terms.get(i).equals(store.terms.get(i))) return false;
		for(int i=coeffs.size(); --i >= 0; ) if(coeffs.get(i) != store.coeffs.get(i)) return false;
		for(int i=indexScale.size(); --i >= 0; ) if(indexScale.get(i) != store.indexScale.get(i)) return false;
		
		return true;
	}
	
	/**
	 * Adds the term.
	 *
	 * @param store the store
	 * @param a the a
	 * @param b the b
	 */
	public synchronized void addTerm(DataStore<?> store, double a, double b) {
		terms.add(store);
		coeffs.add(new Vector2D(a, b));
		if(indexScale.isEmpty()) indexScale.add(1.0);
		else indexScale.add((double) store.getSamples() / getSamples());
	}

	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#get(long)
	 */
	@Override
	public Double get(long n) throws IOException {
		double value = 0.0;
		
		for(int i=0; i<terms.size(); i++) {
			Vector2D coeff = coeffs.get(i);
			value += terms.get(i).get(Math.round(n * indexScale.get(i))).doubleValue() * coeff.x() + coeff.y();
		}
		
		return value;
	}
	
	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return terms.get(0).getSamples();
	}
	
	/* (non-Javadoc)
	 * @see jnum.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return terms.get(0).length();
	}

	
}

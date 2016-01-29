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
import java.util.ArrayList;

import jnum.math.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class LinearCombinationStore.
 */
public class LinearCombinationStore extends DataStore<Double> {
	
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
	 * @see kovacs.util.dirfile.DataStore#get(long)
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
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return terms.get(0).getSamples();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return terms.get(0).length();
	}

	
}
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

package jnum.io.dirfile;

import java.io.IOException;
import java.io.Serializable;


public class InterpolatedStore extends DataStore<Double> implements Serializable {

	private static final long serialVersionUID = -5872387045878806688L;

	DataStore<?> values;
	
	public InterpolatedStore(String name, DataStore<?> values) {
		super(name);
		this.values = values;
	}

	@Override
	public int hashCode() { return super.hashCode() ^ values.hashCode(); }

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof InterpolatedStore)) return false;
		if(!super.equals(o)) return false;
		return values.equals(((InterpolatedStore) o).values);
	}
	

	public Double get(double n) throws IOException {
		long k = (long) n;
		double f = n - k;
		
		if(f == 0.0) return values.get(k).doubleValue();
		
		return (1.0 - f) * values.get(k).doubleValue() + f * values.get(k+1).doubleValue();	
	}

	@Override
	public long length() throws IOException {
		return values.length();
	}

	@Override
	public Double get(long n) throws IOException {
		return values.get(n).doubleValue();
	}

	@Override
	public int getSamples() {
		return values.getSamples();
	}

}

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

import jnum.util.HashCode;


// TODO: Auto-generated Javadoc
/**
 * The Class Constant.
 */
public class ConstantStore extends DataStore<Number> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8499362540214314258L;

	/** The isFloating. */
	boolean isFloating = true;
	
	/** The i value. */
	long iValue;
	
	/** The value. */
	double fValue = Double.NaN;
	
	/**
	 * Instantiates a new constant.
	 *
	 * @param name the name
	 * @param type the type
	 * @param value the value
	 */
	public ConstantStore(String name, String type, String value) {	
		super(name);
		
		type = type.toLowerCase();
	
		switch(type.charAt(0)) {
		case 'u' : iValue = Long.decode(value); break;
		case 's' : iValue = Long.decode(value); break; 
		case 'i' : iValue = Long.decode(value); break;
		case 'f' : fValue = Float.parseFloat(value); break; 
		case 'd' : fValue = Double.parseDouble(value); break;
		default : throw new IllegalArgumentException("No constant type for " + type);
		}
		
		isFloating = !Double.isNaN(fValue);
	}
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#hashCode()
	 */
	@Override
	public int hashCode() { return super.hashCode() ^ HashCode.from(iValue) ^ HashCode.from(fValue) ^ (isFloating ? 1 : 0); }
	
	/* (non-Javadoc)
	 * @see jnum.io.dirfile.DataStore#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof ConstantStore)) return false;
		if(!super.equals(o)) return false;
		ConstantStore c = (ConstantStore) o;
		if(isFloating != c.isFloating) return false;
		if(iValue != c.iValue) return false;
		if(fValue != c.fValue) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#get(long)
	 */
	@Override
	public Number get(long n) throws IOException {
		return isFloating ? fValue : iValue;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		return 1L;
	}
	
	
	
}

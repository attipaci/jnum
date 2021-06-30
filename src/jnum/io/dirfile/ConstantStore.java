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


public class ConstantStore extends DataStore<Number> {

	private static final long serialVersionUID = 8499362540214314258L;

	boolean isFloating = true;

	long iValue;

	double fValue = Double.NaN;
	

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

	@Override
	public int hashCode() { return super.hashCode() ^ HashCode.from(iValue) ^ HashCode.from(fValue) ^ (isFloating ? 1 : 0); }

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

	@Override
	public Number get(long n) throws IOException {
		return isFloating ? fValue : iValue;
	}

	@Override
	public int getSamples() {
		return 1;
	}

	@Override
	public long length() throws IOException {
		return 1L;
	}
	
	
	
}

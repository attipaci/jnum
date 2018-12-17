/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.text;

import jnum.Util;
import jnum.math.Complex;



public enum ParseType {

	BOOLEAN (boolean.class),

	BYTE (byte.class), 

	SHORT (short.class),

	INT (int.class), 

	LONG (long.class),

	FLOAT (float.class),

	DOUBLE (double.class),

	COMPLEX (Complex.class),

	STRING (String.class);
	

	private Class<?> type;
	

	private ParseType(Class<?> type) {
		this.type = type;
	}


	public Class<?> getType() {
		return type;
	}
	

	public static ParseType get(String value) {
		return get(value, ParseType.BOOLEAN);
	}
		

	private static Complex complex = new Complex();
	
	// Returns the parsing type >= lowest
	public synchronized static ParseType get(String value, ParseType lowest) {
		switch(lowest) {
		case BOOLEAN :
			try { Util.parseBoolean(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.BYTE; }
		case BYTE :
			try { Byte.decode(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.SHORT; }
		case SHORT :
			try { Short.decode(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.INT; }
		case INT :
			try { Integer.decode(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.LONG; }
		case LONG :
			try { Long.decode(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.FLOAT; }
		case FLOAT :
			try { Float.parseFloat(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.DOUBLE; }
		case DOUBLE :
			try { Double.parseDouble(value); break; }
			catch(NumberFormatException e) { lowest = ParseType.COMPLEX; }
		case COMPLEX :
			try { complex.parse(value); break; }
			catch(NumberFormatException e) { return ParseType.STRING; }
		case STRING :
			return ParseType.STRING;
		}

		return lowest;
	}
	
}

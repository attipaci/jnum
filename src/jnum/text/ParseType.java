/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.text;

import jnum.Util;
import jnum.math.Complex;


// TODO: Auto-generated Javadoc
/**
 * The Enum ParseType.
 */
public enum ParseType {

	/** The boolean. */
	BOOLEAN (boolean.class),
	
	/** The byte. */
	BYTE (byte.class), 
	
	/** The short. */
	SHORT (short.class),
	
	/** The int. */
	INT (int.class), 
	
	/** The long. */
	LONG (long.class),
	
	/** The float. */
	FLOAT (float.class),
	
	/** The double. */
	DOUBLE (double.class),
	
	/** The complex. */
	COMPLEX (Complex.class),
	
	/** The string. */
	STRING (String.class);
	
	/** The type. */
	private Class<?> type;
	
	/**
	 * Instantiates a new parses the type.
	 *
	 * @param type the type
	 */
	private ParseType(Class<?> type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @return the parses the type
	 */
	public static ParseType get(String value) {
		return get(value, ParseType.BOOLEAN);
	}
		
	/** The complex. */
	private static Complex complex = new Complex();
	
	// Returns the parsing type >= lowest
	/**
	 * Gets the.
	 *
	 * @param value the value
	 * @param lowest the lowest
	 * @return the parses the type
	 */
	public synchronized static ParseType get(String value, ParseType lowest) {
		switch(lowest) {
		case BOOLEAN :
			try { Util.parseBoolean(value); }
			catch(NumberFormatException e) { lowest = ParseType.BYTE; }
		case BYTE :
			try { Byte.decode(value); }
			catch(NumberFormatException e) { lowest = ParseType.SHORT; }
		case SHORT :
			try { Short.decode(value); }
			catch(NumberFormatException e) { lowest = ParseType.INT; }
		case INT :
			try { Integer.decode(value); }
			catch(NumberFormatException e) { lowest = ParseType.LONG; }
		case LONG :
			try { Long.decode(value); }
			catch(NumberFormatException e) { lowest = ParseType.FLOAT; }
		case FLOAT :
			try { Float.parseFloat(value); }
			catch(NumberFormatException e) { lowest = ParseType.DOUBLE; }
		case DOUBLE :
			try { Double.parseDouble(value); }
			catch(NumberFormatException e) { lowest = ParseType.COMPLEX; }
		case COMPLEX :
			try { complex.parse(value); }
			catch(NumberFormatException e) { lowest = ParseType.STRING; }
		case STRING :
			lowest =  ParseType.STRING;
		}

		return lowest;
	}
	
}

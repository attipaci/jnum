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
package jnum.io.dirfile;

import java.util.ArrayList;
import java.util.Iterator;

// TODO: Auto-generated Javadoc
/**
 * The Class DirTokenizer.
 */
public class DirTokenizer extends ArrayList<String> implements Iterator<String> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3836594114797106754L;
	
	/** The line. */
	String line;
	
	/** The i. */
	int i = 0;
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return i < line.length();
	}
	
	// return the position after the parsing...
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public String next() {
		StringBuilder token = new StringBuilder();		
		boolean isQuote = false;
		
		for(; i<line.length(); i++) {
			char c = line.charAt(i);
			
			// First check for escape sequences
			if(c == '\\') {
				c = line.charAt(++i);
				if(c >= '0' && c <= '9') {
					String sequence = line.substring(i, i + 3);
					token.append(Integer.decode("0" + sequence).byteValue());
					i += 3;
				}
				else if(c == 'u') {
					// TODO UTF-8 byte sequence...
				}
				else switch(c) {
				case 'a' : token.append(0x07); break; // Alert bell
				case 'b' : token.append('\b'); break;
				case 'e' : token.append(0x1b); break; // Escape character
				case 'f' : token.append('\f'); break; // Form feed
				case 'n' : token.append('\n'); break;
				case 'r' : token.append('\r'); break;
				case 't' : token.append('\t'); break;
				case 'v' : token.append(0x0b); break; // Vertical tab
				case 'x' : token.append(Byte.decode("0x" + line.charAt(++i) + line.charAt(++i))); break;
				default : token.append(c); // treats '\ ' \#, \", \\ and escaped character default
				}
			}
			// Check if quote
			else if(c == '"') {
				if(isQuote) return new String(token);
				else isQuote = true;
			}
			// Check if unquoted white space
			else if(c == ' ' || c == '\t' || c == '\n') {
				if(!isQuote) return new String(token);
				else token.append(c);
			}	
			// else just add to the token
			else token.append(c);
		}
		
		return new String(token);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// Do nothing...
	}
	
}

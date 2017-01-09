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

import java.util.*;
import java.text.*;


// TODO: Auto-generated Javadoc
/**
 * The Class BracketedListTokenizer.
 */
public class BracketedListTokenizer {
	
	/** The list. */
	protected Vector<Object> list = new Vector<Object>();
	
	/** The next index. */
	private int nextIndex = 0;
	
	/** The opening. */
	protected static String opening = "({[";
	
	/** The closing. */
	protected static String closing = ")}]";

	/**
	 * Instantiates a new bracketed list tokenizer.
	 *
	 * @param str the str
	 * @param delim the delim
	 */
	public BracketedListTokenizer(String str, String delim) {
		init(str, delim);
	}
		
	/**
	 * Inits the.
	 *
	 * @param str the str
	 * @param delim the delim
	 */
	protected void init(String str, String delim) {
		boolean hasMore = false;
		
		int[] bounds = { 0, 0 };
		
		while(hasMore) {
			try {
				nextTokenLimits(str, delim, bounds);
				list.add(str.substring(bounds[0], bounds[1]).trim());
			}
			catch(ParseException e) { list.add(e); }
			catch(IndexOutOfBoundsException e) { hasMore = false; }
		}
	}

	/**
	 * Instantiates a new bracketed list tokenizer.
	 *
	 * @param str the str
	 */
	public BracketedListTokenizer(String str) {
		this(str, ",;");
	}

	/**
	 * Count tokens.
	 *
	 * @return the int
	 */
	public int countTokens() {
		return list.size();
	}

	/**
	 * Checks for more tokens.
	 *
	 * @return true, if successful
	 */
	public boolean hasMoreTokens() {
		return nextIndex < list.size();
	}

	/**
	 * Next token.
	 *
	 * @return the string
	 * @throws NoSuchElementException the no such element exception
	 * @throws ParseException the parse exception
	 */
	public String nextToken() throws NoSuchElementException, ParseException {
		try { 
			Object entry = list.get(nextIndex++); 
			if(entry instanceof String) return (String) entry;
			else if(entry instanceof ParseException) throw (ParseException) entry;
			else throw new IllegalStateException("Illegal entry on " + getClass().getName() + "'s list.");
		}
		catch(ArrayIndexOutOfBoundsException e) { throw new NoSuchElementException(e.getMessage()); }
	}
	
	/**
	 * Next token limits.
	 *
	 * @param source the source
	 * @param delim the delim
	 * @param bounds the bounds
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 * @throws ParseException the parse exception
	 */
	private static void nextTokenLimits(String source, String delim, int[] bounds) throws IndexOutOfBoundsException, ParseException {
		// TODO Use BracketedExpression...
		// A Token is either a closed bracketed expression, or separated by delimiters...
		// E.g. both are valid : {{1,0},{0,1}}
		//                       {{1,0}{0,1}}
		
		int pos = bounds[1];
		
		while(isDelimiter(source.charAt(pos), delim)) pos++;

		bounds[0] = pos;
		boolean complete = false;
		boolean escape = false;
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		String open = new String();

		while(!complete) {
			char c = source.charAt(pos);

			if(escape) {
				if(c == '\\') escape = false;
			}
			else {
				if(c == '\\') escape = true;
				else if(c == '"') inDoubleQuote = !inDoubleQuote;
				else if(c == '\'') inSingleQuote = !inSingleQuote;
				else if(!(inDoubleQuote || inSingleQuote)) {
					for(int i=0; i<opening.length(); i++) if(c == opening.charAt(i)) open += c;
					for(int i=0; i<closing.length(); i++) if(c == closing.charAt(i)) {
						if(open.charAt(open.length()-1) == opening.charAt(i)) open = open.substring(0, open.length()-1);
						else throw new ParseException("Illegal closing '" + c + "'.", pos);
					}
				}
				
				if(open.length() == 0) if(isDelimiter(c, delim)) complete = true;
			}

			pos++;
		}

		bounds[1] = pos;
	}		
		
	/**
	 * Checks if is delimiter.
	 *
	 * @param c the c
	 * @param delim the delim
	 * @return true, if is delimiter
	 */
	private static boolean isDelimiter(char c, String delim) {
		for(int i=0; i<delim.length(); i++) if(c == delim.charAt(i)) return true;
		return false;
	}
}

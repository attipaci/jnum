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
package jnum.text;


// TODO: Auto-generated Javadoc
/**
 * The Class QuoteTokenizer.
 */
public class QuoteTokenizer {
	
	/** The type. */
	int type = UNQUOTED;
	
	/** The line. */
	String line;
	
	/** The index. */
	int index = 0;

	/*
	public static void main(String[] args) {
		
		String line = "\"this is a 'quote'\" and 'this is another \"quote\"' and this quote \"has escaped \\\" in it.\"";
		//String line = "";
		//String line = "what about 'this' and \"this unfinished one?";
		QuoteTokenizer quotes = new QuoteTokenizer(line);
		
		while(quotes.hasMoreElements()) {
			System.err.println(quotes.type + " : " + quotes.nextToken());
		}
	}
	*/
	
	/**
	 * Instantiates a new quote tokenizer.
	 *
	 * @param line the line
	 */
	public QuoteTokenizer(String line) {
		this.line = line;
		// If starting with a quote, then no need to return what precedes the quote...
		if(line.length() == 0) return;
			
		char first = line.charAt(0);
		
		// Initialize, in case starting with a quote right away.
		// S.t. isNextQuote methods return the correct values...
		if(first == '\'') {
			type = SINGLE_QUOTE;
			index++;
		}
		else if(first == '"') {
			type = DOUBLE_QUOTE;
			index++;
		}
		
	}
	
	/**
	 * Checks if is next quote.
	 *
	 * @return true, if is next quote
	 */
	public boolean isNextQuote() {
		return type != UNQUOTED;
	}
	
	/**
	 * Checks if is next double quote.
	 *
	 * @return true, if is next double quote
	 */
	public boolean isNextDoubleQuote() {
		return type == DOUBLE_QUOTE;
	}
	
	/**
	 * Checks if is next single quote.
	 *
	 * @return true, if is next single quote
	 */
	public boolean isNextSingleQuote() {
		return type == SINGLE_QUOTE;
	}
	
	/**
	 * Checks for more elements.
	 *
	 * @return true, if successful
	 */
	public boolean hasMoreElements() {
		return index < line.length();
	}
	

	/**
	 * Find next.
	 *
	 * @param c the c
	 * @param from the from
	 * @return the int
	 */
	private int findNext(char c, int from) {
		int i = line.indexOf(c, from);

		if(i < 0) return line.length();
		else if(i > 0) if(line.charAt(i-1) == '\\') return findNext(c, i+1);

		return i;
	}
	
	/**
	 * Next token.
	 *
	 * @return the string
	 */
	public String nextToken() {
		if(line.length() == 0) return line;

		int nextSingle = findNext('\'', index);
		int nextDouble = findNext('"', index);

		int nextQuote = Math.min(nextSingle, nextDouble);
				
		String value = null;
	
		
		// If a single quote, then return until the closing single quote.
		if(type == SINGLE_QUOTE) {
			value = line.substring(index, nextSingle);
			type = UNQUOTED;
			index = nextSingle + 1;
		}
		// If double quote, then return until the next
		else if(type == DOUBLE_QUOTE) {
			value = line.substring(index, nextDouble);
			type = UNQUOTED;
			index = nextDouble + 1;
		}
		// Otherwise we will start a new quote. Return whatever precedes it
		// if it's not zero length. Otherwise jump to the next quote..
		else {
			// Set the type of the next quote...
			type = nextSingle < nextDouble ? SINGLE_QUOTE : DOUBLE_QUOTE;
			// Do not return an empty unquoted string...
			if(nextQuote == index) return nextToken();
			value = line.substring(index, nextQuote);
			index = nextQuote + 1;
		}
		
		return value;
	}
	
	/** The Constant UNQUOTED. */
	public final static int UNQUOTED = 0;
	
	/** The Constant SINGLE_QUOTE. */
	public final static int SINGLE_QUOTE = 1;
	
	/** The Constant DOUBLE_QUOTE. */
	public final static int DOUBLE_QUOTE = 2;
	
}

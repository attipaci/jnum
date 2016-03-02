/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


// TODO: Auto-generated Javadoc
/**
 * The Class FixedLengthFormat.
 */
public class FixedLengthFormat extends NumberFormat {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7874756229468621371L;
	
	/** The length. */
	int length;
	
	/** The nf. */
	NumberFormat nf;
	
	/** The to left. */
	boolean toLeft = false;
	
	/**
	 * Instantiates a new fixed length format.
	 *
	 * @param baseFormat the base format
	 * @param n the n
	 */
	public FixedLengthFormat(NumberFormat baseFormat, int n) {
		nf = baseFormat;
		length = n;
	}
	
	/**
	 * Left.
	 */
	public void left() { toLeft = true; }
	
	/**
	 * Right.
	 */
	public void right() { toLeft = false; }
	
	/**
	 * Checks if is left.
	 *
	 * @return true, if is left
	 */
	public boolean isLeft() { return toLeft; }
	
	/**
	 * Checks if is right.
	 *
	 * @return true, if is right
	 */
	public boolean isRight() { return !toLeft; }
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		pos.setBeginIndex(toAppendTo.length());
		
		String base = nf.format(number);
	
		// For shorter formats pad the beginning with empty spaces.
		if(base.length() < length) {
			int padding = length - base.length();
			if(toLeft) toAppendTo.append(base);
			for(int i=0; i<padding; i++) toAppendTo.append(" ");
			if(!toLeft) toAppendTo.append(base);
		}
		// For long exponentials, clip least significant figures as necessary
		else if(base.contains("E")) {
			String exp = base.substring(base.indexOf('E'));
			toAppendTo.append(base.substring(0, length - exp.length()) + exp);
		}
		// For other long formats, simply trim to length.
		else toAppendTo.append(base.substring(0, length));		
	
		pos.setEndIndex(toAppendTo.length());
		
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {

		pos.setBeginIndex(toAppendTo.length());
		
		String base = nf.format(number);
		
		// For shorter formats pad the beginning with empty spaces.
		if(base.length() < length) {
			int padding = length - base.length();
			if(toLeft) toAppendTo.append(base);
			for(int i=0; i<padding; i++) toAppendTo.append(" ");
			if(!toLeft) toAppendTo.append(base);
		}
		// For long exponentials, clip least significant figures as necessary
		else if(base.contains("E")) {
			String exp = base.substring(base.indexOf('E'));
			toAppendTo.append(base.substring(0, length - exp.length()) + exp);
		}
		// For other long formats, simply trim to length.
		else toAppendTo.append(base.substring(0, length));		
	
		pos.setEndIndex(toAppendTo.length());
		
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return nf.parse(source.trim(), parsePosition);
	}

}

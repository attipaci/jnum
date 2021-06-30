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

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class FixedLengthFormat extends NumberFormat {

	private static final long serialVersionUID = -7874756229468621371L;

	int length;

	NumberFormat nf;

	boolean toLeft = false;
	

	public FixedLengthFormat(NumberFormat baseFormat, int n) {
		nf = baseFormat;
		length = n;
	}
	

	public void left() { toLeft = true; }
	

	public void right() { toLeft = false; }
	

	public boolean isLeft() { return toLeft; }
	

	public boolean isRight() { return !toLeft; }
	

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

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return nf.parse(source.trim(), parsePosition);
	}

}

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

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

import jnum.Util;

// Chose a decimal format that keeps the most significant figures for the given length


public class FixedLengthDecimalFormat extends DecimalFormat {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6113058377335474979L;
	
	int length;
	String error = new String();
	boolean toLeft;
	
	public FixedLengthDecimalFormat(int n) {
		length = n;
		for(int i=0; i<n; i++) error += "#";
	}
	
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
	
			String formatted = null;
			pos.setBeginIndex(toAppendTo.length());
		
			int eN = length-2; // E#
			int fN = length-1; // .
			boolean positive = number >= 0.0;
			
			int order = (int)Math.floor(Math.log10(Math.abs(number)));
			int aOrder = order > 0 ? order : -order;
			int iN = order;
			
			// If too large than integer or floating pnt notation out of question...
			if(positive ? order >= length : order >= length-1) { iN = -1; fN = -1; } 
			
			// Count how many SF's can exponential form display...
			// Also for numbers with abs < 1.0, disable the integer format, and 
			// figure out how many figures can be displayed in the floating pnt format...
			if(order < 0) { 
				eN--; 
				fN = length - 1 + order; // 0.[...]
				iN = -1;
			}
			if(aOrder > 9) eN--;
			if(aOrder > 99) eN--;
			
			// Negative sign...
			if(!positive) { iN--; fN--; eN--; }
			
			if(eN > 1) eN--; // decimal point...
			
			if(eN <= 0 && fN <= 0 && iN <= 0) formatted = error;
			else if(eN > 0 && eN > fN && eN > iN) {
				formatted = Util.e[eN-1].format(number);
				int iExp = formatted.indexOf('E');
				formatted = removeTrailingZeroes(formatted.substring(0, iExp)) + formatted.substring(iExp);
			}
			else if(fN - order > 1 && fN > iN) formatted = removeTrailingZeroes(Util.f[fN - order - 1].format(number));
			else formatted = Math.round(number) + "";
			
			int pads = length - formatted.length();
			
			String padding = "";
			for(int i=0; i<pads; i++) padding += " ";
			
			if(!toLeft) toAppendTo.append(padding);
			toAppendTo.append(formatted);
			if(toLeft) toAppendTo.append(padding);
			
			pos.setBeginIndex(toAppendTo.length());
			
			return toAppendTo;
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String removeTrailingZeroes(String number) {
		int last = number.length() - 1;
		while(number.charAt(last) == '0') last--;
		if(number.charAt(last) == '.') last--;
		return number.substring(0, last+1);
	}
	
}

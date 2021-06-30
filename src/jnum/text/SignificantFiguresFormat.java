/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
// (C)2007 Attila Kovacs <attila[AT]sigmyne.com>

package jnum.text;

import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

import jnum.Util;



// Chooses the most compact format with the given number of significant figures
// Keeps at least the required number of significant figures.
// May keep more figures depending on the chosen format...

public class SignificantFiguresFormat extends NumberFormat {

	int digits;

	boolean trailingZeroes;

	private static final long serialVersionUID = 5240055256401076047L;

	public SignificantFiguresFormat(int n) {
		this(n, true);
	}
	

	public SignificantFiguresFormat(int n, boolean trailingZeroes) {
		digits = n;
		this.trailingZeroes = trailingZeroes;
	}
	

	public boolean hasTrailingZeroes() { return trailingZeroes; }

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
	    if(number == 0.0) return getZeroFormat().format(number, toAppendTo, pos);
	    return getFormat((int)Math.floor(Math.log10(Math.abs(number)))).format(number, toAppendTo, pos);
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
	    if(number == 0.0) return getZeroFormat().format(number, toAppendTo, pos);
	    return getFormat((int)Math.floor(Math.log10(number))).format(number, toAppendTo, pos);
	}

	public NumberFormat getZeroFormat() {
	    return trailingZeroes ? Util.f[digits-1] : Util.F[digits-1];
	}
	

	public NumberFormat getFormat(int order) {
		if(order < 0) {
			//int elength = 4+digits; // #.###E-#
			//int flength = 1+digits-order; // 0.####
			if(order > -3) return trailingZeroes ? Util.f[digits-order-1] : Util.F[digits-order-1];
			return trailingZeroes ? Util.e[digits-1] : Util.E[digits-1] ;
		}
		// ###.##
		// #.####E# or #.####E##
		else if(order > 9) {
			if(order > digits+3) return trailingZeroes ? Util.e[digits-1] : Util.E[digits-1];
			return trailingZeroes ? Util.f[digits-order-1] : Util.F[digits-order-1];
		}
		else if(order > digits+2) return trailingZeroes ? Util.e[digits-1] : Util.E[digits-1];
		else if(order > digits-1) return Util.d[order+1];
		else return trailingZeroes ? Util.f[digits-order-1] : Util.F[digits-order-1];
		
	}

	@Override
	// TODO parse only so many significant figures?
	public Number parse(String source, ParsePosition parsePosition) {
		return Util.e12.parse(source, parsePosition);
	}
	
}

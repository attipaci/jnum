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
package jnum.text;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class SignedFormat extends NumberFormat {

	private static final long serialVersionUID = -6900977697243233653L;

	private NumberFormat nf;

	public SignedFormat(NumberFormat nf) {
		this.nf = nf;
	}
	
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		int from = toAppendTo.length();
		if(number >= 0.0) toAppendTo.append(" ");
		nf.format(number, toAppendTo, pos);
		pos.setBeginIndex(from);
		
		return toAppendTo;
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		int from = toAppendTo.length();
		if(number >= 0.0) toAppendTo.append(" ");
		nf.format(number, toAppendTo, pos);
		pos.setBeginIndex(from);
		
		return toAppendTo;
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return nf.parse(source, parsePosition);
	}
	

}

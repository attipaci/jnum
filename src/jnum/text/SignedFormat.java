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
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		int from = toAppendTo.length();
		if(number >= 0.0) toAppendTo.append(" ");
		nf.format(number, toAppendTo, pos);
		pos.setBeginIndex(from);
		
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		int from = toAppendTo.length();
		if(number >= 0.0) toAppendTo.append(" ");
		nf.format(number, toAppendTo, pos);
		pos.setBeginIndex(from);
		
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return nf.parse(source, parsePosition);
	}
	

}

/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
import java.text.ParsePosition;

import jnum.Unit;


public class HourAngleFormat extends TimeFormat {

	private static final long serialVersionUID = -6260375852141250856L;

	public HourAngleFormat() { super(); setPositiveOnly(true); }
		
	public HourAngleFormat(int decimals) { super(decimals); setPositiveOnly(true); }

	@Override
	public StringBuffer format(double angle, StringBuffer toAppendTo, FieldPosition pos) {
		return super.format(angle / Unit.timeAngle, toAppendTo, pos);
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition, int format) {
		return super.parse(source, parsePosition, format).doubleValue() * Unit.timeAngle;
	}	

}

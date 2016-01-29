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
// Copyright (c) 2007 Attila Kovacs 

package jnum.text;

import java.text.FieldPosition;
import java.text.ParsePosition;

import jnum.Unit;


// TODO: Auto-generated Javadoc
/**
 * The Class HourAngleFormat.
 */
public class HourAngleFormat extends TimeFormat {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6260375852141250856L;
	
	/**
	 * Instantiates a new hour angle format.
	 */
	public HourAngleFormat() { super(); setOneSided(true); }
		
	/**
	 * Instantiates a new hour angle format.
	 *
	 * @param decimals the decimals
	 */
	public HourAngleFormat(int decimals) { super(decimals); setOneSided(true); }
	
	/* (non-Javadoc)
	 * @see kovacs.util.text.AngleFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double angle, StringBuffer toAppendTo, FieldPosition pos) {
		return super.format(angle / Unit.timeAngle, toAppendTo, pos);
	}
	
	@Override
	public Number parse(String source, ParsePosition parsePosition, int fromLevel) {
		return super.parse(source, parsePosition, fromLevel).doubleValue() * Unit.timeAngle;
	}	

}

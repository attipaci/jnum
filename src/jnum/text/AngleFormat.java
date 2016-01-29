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


import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import jnum.util.Constant;
import jnum.util.Symbol;
import jnum.util.Unit;
import jnum.util.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class AngleFormat.
 */
public class AngleFormat extends NumberFormat {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8006119682644201943L;
	
	/** The decimals. */
	private int decimals;
	
	/** The precision. */
	private double precision;
	
	/** The top level. */
	public int topLevel = DEGREE;
	
	/** The bottom level. */
	public int bottomLevel = SECOND;
	
		
	/** The Constant colonMarker. */
	protected static final char[] colonMarker = { ':', ':', 0};
	
	/** The Constant dmsMarker. */
	protected static final char[] dmsMarker = { 'd', 'm', 's'};
	
	/** The Constant symbolMarker. */
	protected static final char[] symbolMarker = { Symbol.degree, '\'', '"'};

	/** The unit. */
	protected double[] unit = { Unit.deg, Unit.arcmin, Unit.arcsec };	
	
	/** The marker. */
	protected char[] marker = dmsMarker;
	
	/** The wraparound. */
	protected double wraparound = Constant.twoPi;
	
	/** The one sided. */
	protected boolean wrap = true, oneSided = false;
	
	/**
	 * Instantiates a new angle format.
	 */
	public AngleFormat() {
		setDecimals(0);
	}
	
	/**
	 * Instantiates a new angle format.
	 *
	 * @param decimals the decimals
	 */
	public AngleFormat(int decimals) {
		setDecimals(decimals);
	}
	
	/**
	 * Sets the separator.
	 *
	 * @param type the new separator
	 */
	public void setSeparator(int type) {
		switch(type) {
		case COLONS: marker = colonMarker; break;
		case DMS: marker = dmsMarker; break;
		case SYMBOLS: marker = symbolMarker; break;
		default: marker = colonMarker;
		}	
	}
	
	/**
	 * Gets the separator.
	 *
	 * @return the separator
	 */
	public int getSeparator() {
		if(marker == colonMarker) return COLONS;
		else if(marker == dmsMarker) return DMS;
		else if(marker == symbolMarker) return SYMBOLS;	
		else return -1;
	}
	
	/**
	 * Colons.
	 */
	public void colons() { marker = colonMarker; }
	
	/**
	 * Letters.
	 */
	public void letters() { marker = dmsMarker; }
	
	/**
	 * Symbols.
	 */
	public void symbols() { marker = symbolMarker; }
	
	/**
	 * Sets the top level.
	 *
	 * @param level the new top level
	 */
	public void setTopLevel(int level) { 
		if(level < DEGREE || level > SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		topLevel = level; 	
	}
	
	/**
	 * Sets the bottom level.
	 *
	 * @param level the new bottom level
	 */
	public void setBottomLevel(int level) { 
		if(level < DEGREE || level > SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		bottomLevel = level; 		
	}
	
	/**
	 * Gets the top level.
	 *
	 * @return the top level
	 */
	public int getTopLevel() { return topLevel; }
	
	/**
	 * Gets the bottom level.
	 *
	 * @return the bottom level
	 */
	public int getBottomLevel() { return bottomLevel; }
	
	/**
	 * Sets the decimals.
	 *
	 * @param decimals the new decimals
	 */
	public void setDecimals(int decimals) { 
		this.decimals = decimals; 
		precision = decimals > 0 ? Math.pow(0.1, decimals) : 0.0;
	}
	
	/**
	 * Gets the decimals.
	 *
	 * @return the decimals
	 */
	public int getDecimals() { return decimals; }
	
	/**
	 * Sets the one sided.
	 *
	 * @param value the new one sided
	 */
	public void setOneSided(boolean value) { oneSided = value; }
	
	/**
	 * Checks if is one sided.
	 *
	 * @return true, if is one sided
	 */
	public boolean isOneSided() { return oneSided; }
	
	/**
	 * Wrap.
	 *
	 * @param value the value
	 */
	public void wrap(boolean value) { wrap = value; }
	
	/**
	 * Checks if is wrapping.
	 *
	 * @return true, if is wrapping
	 */
	public boolean isWrapping() { return wrap; }
	
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {		
		if(wrap) {
			number = Math.IEEEremainder(number, wraparound);
			if(oneSided && number < 0) number += wraparound;
		}
		
		pos.setBeginIndex(toAppendTo.length());
		toAppendTo.append(toString(number));
		pos.setEndIndex(toAppendTo.length());
		return toAppendTo;
	}
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Parse with any markers, and any level below the top level.
	// The top level is then readjusted to the 
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return parse(source, parsePosition, DEGREE);
	}
	
	/**
	 * Parses the.
	 *
	 * @param source the source
	 * @param parsePosition the parse position
	 * @param fromLevel the from level
	 * @return the number
	 */
	public Number parse(String source, ParsePosition parsePosition, int fromLevel) {
		int sign = 1;
		double angle = 0.0;
		
		if(marker[fromLevel] != 0) {
			int pos = parsePosition.getIndex();
			colons();
			if(source.indexOf(marker[fromLevel], pos) < 0) {
				letters();
				if(source.indexOf(marker[fromLevel], pos) < 0) {
					symbols();
					if(source.indexOf(marker[fromLevel], pos) < 0) return parse(source, parsePosition, fromLevel+1);
				}
			}
		}
		
		int i = parsePosition.getIndex();
		char c = source.charAt(i);
		
		while(c == ' ' || c == '\t' || c == '\n' || c == '\r') c = source.charAt(++i);
			
		if(c == '-') {
			sign = -1;
			i++;
		}
		parsePosition.setIndex(i);
		
		for(int level = fromLevel; level <= SECOND; level++) {
			if(marker[level] != 0) {			
				int pos = parsePosition.getIndex();
				int to = source.indexOf(marker[level], pos);
				if(to < 0) {
					bottomLevel = level;
					break;
				}
				if(level == SECOND) angle += Double.parseDouble(source.substring(pos, to)) * unit[level];
				else angle += Integer.parseInt(source.substring(pos, to)) * unit[level];
				
				parsePosition.setIndex(to+1);
			}
			else angle += Util.f[decimals].parse(source, parsePosition).doubleValue() * unit[SECOND];		
		} 
			
		return sign * angle;
	}
	
	/**
	 * To string.
	 *
	 * @param angle the angle
	 * @return the string
	 */
	public String toString(double angle) {
		StringBuilder text = new StringBuilder(13 + decimals); // 12 characters plus the decimals, plus 1 for good measure...
	
		if(angle < 0.0) {
			angle *= -1.0;
			text.append('-');	
		}
	
		// Round the angle to the formatting resolution (here, use the quick and dirty approach...)
		// This way the rounding is correctly propagated...
		// E.g. 1:20:59.9999 -> 1:21:00 instead of the wrong 1:20:60		
		angle += 0.5 * precision * unit[SECOND];
		
		for(int level = topLevel; level <= bottomLevel; level++) {
			if(level != SECOND) {
				int value = (int) (angle / unit[level]);
				angle -= value * unit[level];
				text.append(Util.d2.format(value));
				text.append(marker[level]);
			}
			else {
				angle /= unit[SECOND];
				double twodigits = 10.0 - 5.0 * Math.pow(0.1, decimals+1);
				if(angle < twodigits) text.append('0');
				text.append(Util.f[decimals].format(angle));
				if(marker[SECOND] != 0.0) text.append(marker[SECOND]);
			}			
		}
		
		return new String(text);		
	}
	
	/** The Constant DEGREE. */
	public static final int DEGREE = 0;
	
	/** The Constant MINUTE. */
	public static final int MINUTE = 1;
	
	/** The Constant SECOND. */
	public static final int SECOND = 2;
	
	/** The Constant COLONS. */
	public static final int COLONS = 0;
	
	/** The Constant DMS. */
	public static final int DMS = 1;
	
	/** The Constant SYMBOLS. */
	public static final int SYMBOLS = 2;
}

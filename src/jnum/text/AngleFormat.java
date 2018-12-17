/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.text;


import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import jnum.Constant;
import jnum.Symbol;
import jnum.Unit;
import jnum.Util;


public class AngleFormat extends NumberFormat {
	

	private static final long serialVersionUID = 8006119682644201943L;

	private int decimals;
	
	private double precision;

	public int topLevel = LEVEL_DEGREE;

	public int bottomLevel = LEVEL_SECOND;

	protected char[] marks = dmsMarks;

	protected boolean wrap = true, isPositiveOnly = false;
	

	public AngleFormat() {
		setDecimals(0);
	}
	

	public AngleFormat(int decimals) {
		setDecimals(decimals);
	}
	

	public void setMarks(int type) {
		marks = getMarkerChars(type);
	}
	

	public int getMarks() {
		if(marks == colonMarks) return FORMAT_COLONS;
		else if(marks == dmsMarks) return FORMAT_DMS;
		else if(marks == symbolMarks) return FORMAT_SYMBOLS;	
		else if(marks == fancyMarks) return FORMAT_FANCY;
		else return -1;
	}
	
	
	public char[] getMarkerChars(int type) {
	    switch(type) {
        case FORMAT_COLONS: return colonMarks;
        case FORMAT_DMS: return dmsMarks;
        case FORMAT_SYMBOLS: return symbolMarks;
        case FORMAT_FANCY: return fancyMarks;
        default: return colonMarks;
	    }  
	}


	public void colons() { marks = colonMarks; }
	
	public void letters() { marks = dmsMarks; }
	
	public void symbols() { marks = symbolMarks; }
	
    public void fancy() { marks = fancyMarks; }
	

	public void setTopLevel(int level) { 
		if(level < LEVEL_DEGREE || level > LEVEL_SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		topLevel = level; 	
	}

	public void setBottomLevel(int level) { 
		if(level < LEVEL_DEGREE || level > LEVEL_SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		bottomLevel = level; 		
	}
	

	public int getTopLevel() { return topLevel; }

	public int getBottomLevel() { return bottomLevel; }
	
	public void setDecimals(int decimals) { 
	    if(decimals < 0) decimals = MAX_DECIMALS;
		this.decimals = decimals; 
		precision = decimals >= 0 ? Math.pow(0.1, decimals) : 0.0;
	}
	

	public int getDecimals() { return decimals; }
	

	public void setPositiveOnly(boolean value) { isPositiveOnly = value; }
	

	public boolean isPositiveOnly() { return isPositiveOnly; }
	

	public void wrap(boolean value) { wrap = value; }
	

	public boolean isWrapping() { return wrap; }
	
	
	protected double getWrapValue() {
	    return Constant.twoPi;
	}
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {		
		if(wrap) {
			number = Math.IEEEremainder(number, getWrapValue());
			if(isPositiveOnly && number < 0) number += getWrapValue();
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
	

    @Override
    public final Number parse(String source) throws NumberFormatException {
	    return parse(source, new ParsePosition(0));
	}
	
	
	// Parse with any markers, and any level below the top level.
	// The top level is then readjusted to the 
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public final Number parse(String source, ParsePosition parsePosition) throws NumberFormatException {
	    for(int format=0; format<MAX_FORMAT; format++) {
	        try { return parse(source, parsePosition, format); }
	        catch(NumberFormatException e) {}
	    }
	    throw new NumberFormatException("Not an angle format: " + source);
	}

	// Parse around spaces and commas by default...
	public String getGenericDelimiters() {
	    return Util.getWhiteSpaceChars() + ",";
	}
	

	public Number parse(String source, ParsePosition pos, int formatStyle) throws NumberFormatException {
		int sign = 1;
		double angle = Double.NaN;
		
		source = source.toLowerCase();
        
		StringParser parser = new StringParser(source, pos);	
        String markers = new String(getMarkerChars(formatStyle));

        parser.skipWhiteSpaces();
      
		for(int level = LEVEL_DEGREE; level <= LEVEL_SECOND && pos.getIndex() < source.length(); level++) {
		    parser.skipWhiteSpaces();  
		    
		    int to = parser.nextIndexOf(getGenericDelimiters() + markers);
	            
		    if(to < 0) to = source.length();
		    else {
		        char delim = source.charAt(to);
		    
		        // Check if the parse level is marked differently from expected, and adjust as needed...
		        if(delim != markers.charAt(level)) for(int k = level+1; k <= LEVEL_SECOND; k++) if(delim == markers.charAt(k)) { 
		            level = k;
		            break;
		        }
		    }
	       
	        // Top level parse errors will throw an exception....
	        if(Double.isNaN(angle)) {
	            angle = Double.parseDouble(source.substring(pos.getIndex(), to)) * getUnit(level);
	            if(angle < 0.0) {
	                angle *= -1.0;
	                sign = -1;
	            }
	            pos.setIndex(to + 1);
	        }
	        // Sub-level parse error assume complete...
	        else {
	            try {
	                angle += Double.parseDouble(source.substring(pos.getIndex(), to)) * getUnit(level);
	                pos.setIndex(to + 1);
	            }
	            catch(NumberFormatException e) { return sign * angle; }
	        }
		}
		
		
		if(Double.isNaN(angle)) throw new NumberFormatException("Could not parse angle from " + source.substring(pos.getIndex()));
		
		return sign * angle;
	}
	

	public String toString(double angle) {
		StringBuilder text = new StringBuilder(13 + decimals); // 12 characters plus the decimals, plus 1 for good measure...
	
		if(angle < 0.0) {
			angle *= -1.0;
			text.append('-');	
		}
	
		// Round the angle to the formatting resolution (here, use the quick and dirty approach...)
		// This way the rounding is correctly propagated...
		// E.g. 1:20:59.9999 -> 1:21:00 instead of the incorrect 1:20:60		
		angle += 0.05 * precision * getUnit(bottomLevel);
		
		for(int level = topLevel; level <= bottomLevel; level++) {
			if(level != LEVEL_SECOND) {
				int value = (int) Math.floor(angle / getUnit(level));
				angle -= value * getUnit(level);
				text.append(Util.d2.format(value));
				text.append(marks[level]);
			}
			else {
				angle /= getUnit(LEVEL_SECOND);
				double twodigits = 10.0 - 5.0 * Math.pow(0.1, decimals+1);
				if(angle < twodigits) text.append('0');
				text.append(Util.f[decimals].format(angle));
				if(marks[LEVEL_SECOND] != 0.0) text.append(marks[LEVEL_SECOND]);
			}			
 		}
		
		return new String(text);		
	}

	public double getUnit(int level) {
	    switch(level) {
	    case LEVEL_DEGREE: return Unit.deg;
	    case LEVEL_MINUTE: return Unit.arcmin;
	    case LEVEL_SECOND: return Unit.arcsec;
	    default: return Double.NaN;
	    }
	    
	}
	

    protected static final char[] colonMarks = { ':', ':', 0};

    protected static final char[] dmsMarks = { 'd', 'm', 's'};

    protected static final char[] symbolMarks = { Symbol.degree, '\'', '"'};

    protected static final char[] fancyMarks = { Symbol.degree, Symbol.prime, Symbol.doublePrime};

	public static final int LEVEL_DEGREE = 0;

	public static final int LEVEL_MINUTE = 1;

	public static final int LEVEL_SECOND = 2;

	
	public final static int MAX_DECIMALS = -1;

	
	
	/** The Constant COLONS. */
	public static final int FORMAT_COLONS = 0;
	
	/** The Constant DMS. */
	public static final int FORMAT_DMS = 1;
	
	/** The Constant SYMBOLS. */
	public static final int FORMAT_SYMBOLS = 2;
	
	public static final int FORMAT_FANCY = 3;
	
	private final static int MAX_FORMAT = 4;
	
	

	
}

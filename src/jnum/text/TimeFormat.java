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

import jnum.Symbol;
import jnum.Unit;


// TODO: Auto-generated Javadoc
/**
 * The Class TimeFormat.
 */
public class TimeFormat extends AngleFormat {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6135295347868421521L;

		
	/**
	 * Instantiates a new time format.
	 */
	public TimeFormat() { defaults(); }
	
	/**
	 * Instantiates a new time format.
	 *
	 * @param decimals the decimals
	 */
	public TimeFormat(int decimals) { super(decimals); defaults(); }

	/**
	 * Defaults.
	 */
	public void defaults() {
		marks = hmsMarks;
		isPositiveOnly = true;
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setSeparator(int)
	 */
	@Override
	public void setMarks(int type) {
		switch(type) {
		case FORMAT_COLONS: marks = colonMarks; break;
		case FORMAT_HMS: marks = hmsMarks; break;
		case FORMAT_SYMBOLS: marks = symbolMarks; break;
		case FORMAT_FANCY: marks = fancyMarks; break;
		default: marks = colonMarks;
		}	
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#getSeparator()
	 */
	@Override
	public int getMarks() {
		if(marks == colonMarks) return FORMAT_COLONS;
		else if(marks == hmsMarks) return FORMAT_HMS;
		else if(marks == symbolMarks) return FORMAT_SYMBOLS;
		else if(marks == fancyMarks) return FORMAT_FANCY;
		else return -1;
	}
	
	@Override
    public double getUnit(int level) {
        switch(level) {
        case LEVEL_HOUR: return Unit.hour;
        case LEVEL_MINUTE: return Unit.min;
        case LEVEL_SECOND: return Unit.s;
        default: return Double.NaN;
        } 
    }
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#colons()
	 */
	@Override
	public void colons() { marks = colonMarks; }
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#letters()
	 */
	@Override
	public void letters() { marks = hmsMarks; }
	
	
	@Override
	public void symbols() { marks = symbolMarks; }
	
	
    @Override
    public void fancy() { marks = fancyMarks; }
    
    
    @Override
    public char[] getMarkerChars(int type) {
        switch(type) {
        case FORMAT_COLONS: return colonMarks;
        case FORMAT_HMS: return hmsMarks;
        case FORMAT_SYMBOLS: return symbolMarks;
        case FORMAT_FANCY: return fancyMarks;
        default: return colonMarks;
        }  
    }
    
    
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setTopLevel(int)
	 */
	@Override
	public void setTopLevel(int level) { 
		if(level < LEVEL_HOUR || level > LEVEL_SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		topLevel = level; 	
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setBottomLevel(int)
	 */
	@Override
	public void setBottomLevel(int level) { 
		if(level < LEVEL_HOUR || level > LEVEL_SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		bottomLevel = level; 		
	}
	
	
	@Override
    public double getWrapValue() {
	    return 24.0 * Unit.hour;
	}
    
    /** The Constant hmsMarker. */
    protected static final char[] hmsMarks = { 'h', 'm', 's'};
    
    /** The Constant symbolMarker. */
    protected static final char[] symbolMarks = { 'h', '\'', '"'};
    
    protected static final char[] fancyMarks = { 'h', Symbol.prime, Symbol.doublePrime};

	
	
	/** The Constant HOUR. */
	public static final int LEVEL_HOUR = AngleFormat.LEVEL_DEGREE;
	
	/** The Constant MINUTE. */
	public static final int LEVEL_MINUTE = AngleFormat.LEVEL_MINUTE;
	
	/** The Constant SECOND. */
	public static final int LEVEL_SECOND = AngleFormat.LEVEL_SECOND;
	
	/** The Constant COLONS. */
	public static final int FORMAT_COLONS = AngleFormat.FORMAT_COLONS;
	
	/** The Constant HMS. */
	public static final int FORMAT_HMS = AngleFormat.FORMAT_DMS;
	
	/** The Constant SYMBOLS. */
	public static final int FORMAT_SYMBOLS = AngleFormat.FORMAT_SYMBOLS;
	
	public static final int FORMAT_FANCY = AngleFormat.FORMAT_FANCY;


}

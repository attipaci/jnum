/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2007 Attila Kovacs 

package jnum.text;

import jnum.Unit;


// TODO: Auto-generated Javadoc
/**
 * The Class TimeFormat.
 */
public class TimeFormat extends AngleFormat {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6135295347868421521L;
	
	/** The Constant colonMarker. */
	protected static final char[] colonMarker = { ':', ':', 0};
	
	/** The Constant hmsMarker. */
	protected static final char[] hmsMarker = { 'h', 'm', 's'};
	
	/** The Constant symbolMarker. */
	protected static final char[] symbolMarker = { 'h', '\'', '"'};
		
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
		unit = new double[] { Unit.hour, Unit.min, Unit.sec };	
		marker = hmsMarker;	
		wraparound = 24.0 * Unit.hour;
		oneSided = true;
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setSeparator(int)
	 */
	@Override
	public void setSeparator(int type) {
		switch(type) {
		case COLONS: marker = colonMarker; break;
		case HMS: marker = hmsMarker; break;
		case SYMBOLS: marker = symbolMarker; break;
		default: marker = colonMarker;
		}	
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#getSeparator()
	 */
	@Override
	public int getSeparator() {
		if(marker == colonMarker) return COLONS;
		else if(marker == hmsMarker) return HMS;
		else if(marker == symbolMarker) return SYMBOLS;	
		else return -1;
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#colons()
	 */
	@Override
	public void colons() { marker = colonMarker; }
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#letters()
	 */
	@Override
	public void letters() { marker = hmsMarker; }
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#symbols()
	 */
	@Override
	public void symbols() { marker = symbolMarker; }
	
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setTopLevel(int)
	 */
	@Override
	public void setTopLevel(int level) { 
		if(level < HOUR || level > SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		topLevel = level; 	
	}
	
	/* (non-Javadoc)
	 * @see jnum.text.AngleFormat#setBottomLevel(int)
	 */
	@Override
	public void setBottomLevel(int level) { 
		if(level < HOUR || level > SECOND) throw new IllegalArgumentException("Undefined " + getClass().getSimpleName() + " level.");
		bottomLevel = level; 		
	}
	
	/** The Constant HOUR. */
	public static final int HOUR = AngleFormat.DEGREE;
	
	/** The Constant MINUTE. */
	public static final int MINUTE = AngleFormat.MINUTE;
	
	/** The Constant SECOND. */
	public static final int SECOND = AngleFormat.SECOND;
	
	/** The Constant COLONS. */
	public static final int COLONS = 0;
	
	/** The Constant HMS. */
	public static final int HMS = 1;
	
	/** The Constant SYMBOLS. */
	public static final int SYMBOLS = 2;


}

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

package jnum.astro;

import java.text.NumberFormat;

import jnum.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class BesselianEpoch.
 */
public class BesselianEpoch extends CoordinateEpoch {	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5133755849976402885L;

	/**
	 * Instantiates a new besselian epoch.
	 */
	public BesselianEpoch() { }

	/**
	 * Instantiates a new besselian epoch.
	 *
	 * @param epoch the epoch
	 */
	public BesselianEpoch(double epoch) { super(epoch); }
	
	/**
	 * Instantiates a new besselian epoch.
	 *
	 * @param epoch the epoch
	 * @param immutable the immutable
	 */
	protected BesselianEpoch(double epoch, boolean immutable) { super(epoch, immutable); }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CoordinateEpoch#getBesselianYear()
	 */
	@Override
	public double getBesselianYear() { return getYear(); }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CoordinateEpoch#getJulianYear()
	 */
	@Override
	public double getJulianYear() { return JulianEpoch.getYearForMJD(getMJD()); }

	/**
	 * Gets the julian epoch.
	 *
	 * @return the julian epoch
	 */
	public JulianEpoch getJulianEpoch() { 
		return new JulianEpoch(getBesselianYear());
	}
	
	//  B = 1900.0 + (JD - 2415020.31352) / 365.242198781
	/* (non-Javadoc)
	 * @see kovacs.util.astro.CoordinateEpoch#setMJD(double)
	 */
	@Override
	public void setMJD(double MJD) {
		setYear(getYearForMJD(MJD));
	}

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CoordinateEpoch#getMJD()
	 */
	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	
	/**
	 * Gets the year for mjd.
	 *
	 * @param MJD the mjd
	 * @return the year for mjd
	 */
	public static double getYearForMJD(double MJD) {
		return 1900.0 + (MJD - mjdB1900) / julianYear;
	}
	
	/**
	 * Gets the mJD for year.
	 *
	 * @param year the year
	 * @return the mJD for year
	 */
	public static double getMJDForYear(double year) {
		return (year - 1900.0) * julianYear + mjdB1900;
	}

	/**
	 * For mjd.
	 *
	 * @param MJD the mjd
	 * @return the besselian epoch
	 */
	public static BesselianEpoch forMJD(double MJD) {
		return new BesselianEpoch(getYearForMJD(MJD));		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { return toString(Util.f1); }
	
	/**
	 * To string.
	 *
	 * @param nf the nf
	 * @return the string
	 */
	public String toString(NumberFormat nf) {
		return "B" + nf.format(getYear());
	}

	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @throws NumberFormatException the number format exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void parse(String text) throws NumberFormatException, IllegalArgumentException {
		if(text.charAt(0) == 'B') setYear(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'J') setYear(getYearForMJD(JulianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else setYear(Double.parseDouble(text));
	}	
	
}

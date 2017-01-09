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

package jnum.astro;

import java.text.NumberFormat;

import jnum.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class JulianEpoch.
 */
public class JulianEpoch extends CoordinateEpoch {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8377319626045474497L;

	/**
	 * Instantiates a new julian epoch.
	 */
	public JulianEpoch() {}

	/**
	 * Instantiates a new julian epoch.
	 *
	 * @param epoch the epoch
	 */
	public JulianEpoch(double epoch) { super(epoch); }
	
	/**
	 * Instantiates a new julian epoch.
	 *
	 * @param epoch the epoch
	 * @param immutable the immutable
	 */
	protected JulianEpoch(double epoch, boolean immutable) { super(epoch, immutable); }

	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getJulianYear()
	 */
	@Override
	public double getJulianYear() { return getYear(); }

	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getBesselianYear()
	 */
	@Override
	public double getBesselianYear() { return BesselianEpoch.getYearForMJD(getMJD()); }

	/**
	 * Gets the besselian epoch.
	 *
	 * @return the besselian epoch
	 */
	public BesselianEpoch getBesselianEpoch() { 
		return new BesselianEpoch(getBesselianYear());
	}

	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#setMJD(double)
	 */
	@Override
	public void setMJD(double MJD) {
		setYear(getYearForMJD(MJD));
	}

	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getMJD()
	 */
	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	
	//  J = 2000.0 + (MJD - 51544) / 365.25
	/**
	 * Gets the year for mjd.
	 *
	 * @param MJD the mjd
	 * @return the year for mjd
	 */
	public static double getYearForMJD(double MJD) {
		return 2000.0 + (MJD - mjdJ2000) / julianYear;
	}

	/**
	 * Gets the mJD for year.
	 *
	 * @param year the year
	 * @return the mJD for year
	 */
	public static double getMJDForYear(double year) {
		return (year - 2000.0) * julianYear + mjdJ2000;
	}

	/**
	 * For mjd.
	 *
	 * @param MJD the mjd
	 * @return the julian epoch
	 */
	public static JulianEpoch forMJD(double MJD) {
		return new JulianEpoch(getYearForMJD(MJD));		
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
		return "J" + nf.format(getYear());
	}
	
	/**
	 * Parses the.
	 *
	 * @param text the text
	 * @throws NumberFormatException the number format exception
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void parse(String text) throws NumberFormatException, IllegalArgumentException {
		if(text.charAt(0) == 'J') setYear(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'B') setYear(getYearForMJD(BesselianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else setYear(Double.parseDouble(text));
	}	
	

}

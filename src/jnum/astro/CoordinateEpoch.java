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


package jnum.astro;

import java.io.Serializable;

import jnum.Copiable;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;

// TODO: Auto-generated Javadoc
/**
 * The Class CoordinateEpoch.
 */
public abstract class CoordinateEpoch implements Serializable, Cloneable, Copiable<CoordinateEpoch>, Comparable<CoordinateEpoch> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7090908739252631026L;

	/** The year. */
	private double year;
	
	/** The immutable. */
	private boolean immutable = false;

	/**
	 * Instantiates a new coordinate epoch.
	 */
	public CoordinateEpoch() {}

	/**
	 * Instantiates a new coordinate epoch.
	 *
	 * @param epoch the epoch
	 */
	public CoordinateEpoch(double epoch) { year = epoch; }
	
	/**
	 * Instantiates a new coordinate epoch.
	 *
	 * @param epoch the epoch
	 * @param immutable the immutable
	 */
	protected CoordinateEpoch(double epoch, boolean immutable) { this(epoch); this.immutable = immutable; }

	
	
	// The clone is always mutable...
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { 
			CoordinateEpoch clone = (CoordinateEpoch) super.clone(); 
			clone.immutable = false;
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }		
	}
	
	/* (non-Javadoc)
	 * @see jnum.Copiable#copy()
	 */
	@Override
	public CoordinateEpoch copy() { return (CoordinateEpoch) clone(); }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null) return false;
		if(!o.getClass().equals(getClass())) return false;
		return year == ((CoordinateEpoch) o).year;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(year);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CoordinateEpoch epoch) {
		double y1 = getJulianYear();
		double y2 = epoch.getJulianYear();
		if(Math.abs(y1 - y2) < precision) return 0;
		else return y1 < y2 ? -1 : 1;
	}
	
	/**
	 * Sets the immutable.
	 *
	 * @param value the new immutable
	 */
	public void setImmutable(boolean value) {
		immutable = value;
	}
	
	/**
	 * Checks if is immutable.
	 *
	 * @return true, if is immutable
	 */
	public boolean isImmutable() { return immutable; }
	
	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public double getYear() { return year; }
	
	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	protected void setYear(double year) {
		if(immutable) throw new UnsupportedOperationException("Cannot alter immutable coordinate epoch.");
		this.year = year;
	}
	
	/**
	 * Gets the julian year.
	 *
	 * @return the julian year
	 */
	public abstract double getJulianYear();

	/**
	 * Gets the besselian year.
	 *
	 * @return the besselian year
	 */
	public abstract double getBesselianYear();
	
	/**
	 * Sets the mjd.
	 *
	 * @param MJD the new mjd
	 */
	public abstract void setMJD(double MJD);
	
	/**
	 * Gets the mjd.
	 *
	 * @return the mjd
	 */
	public abstract double getMJD();
	
	/**
	 * For julian date.
	 *
	 * @param JD the jd
	 */
	public void forJulianDate(double JD) { setMJD(JD - 2400000.5); }
	
	/**
	 * Gets the julian date.
	 *
	 * @return the julian date
	 */
	public double getJulianDate() { return getMJD() + 2400000.5; }
	
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @throws HeaderCardException the header card exception
	 */
	public void edit(Header header) throws HeaderCardException { edit(header, ""); }
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @param alt the alt
	 * @throws HeaderCardException the header card exception
	 */
	public void edit(Header header, String alt) throws HeaderCardException {
		header.addLine(new HeaderCard("EQUINOX" + alt, year, "The epoch of the quoted coordinates"));
	}
	
	/**
	 * Parses the.
	 *
	 * @param header the header
	 */
	public void parse(Header header) { parse(header, ""); }

	/**
	 * Parses the.
	 *
	 * @param header the header
	 * @param alt the alt
	 */
	public void parse(Header header, String alt) {
		year = header.getDoubleValue("EQUINOX" + alt, this instanceof BesselianEpoch ? 1950.0 : 2000.0);
	}
	

	/**
	 * For string.
	 *
	 * @param text the text
	 * @return the coordinate epoch
	 * @throws NumberFormatException the number format exception
	 */
	public static CoordinateEpoch forString(String text) throws NumberFormatException {
		if(text.charAt(0) == 'B') return new BesselianEpoch(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'J') return new JulianEpoch(Double.parseDouble(text.substring(1)));
		else {
			double year = Double.parseDouble(text);
			if(year < 1984.0) return new BesselianEpoch(year);
			else return new JulianEpoch(year);
		}
	}
	
	/** The Constant besselianYear. */
	protected final static double besselianYear = 365.242198781;
	
	/** The Constant julianYear. */
	protected final static double julianYear = 365.25;
	
	/** The Constant mjdB1900. */
	protected final static double mjdB1900 = 15019.81352; // JD 2415020.31352
	
	/** The Constant mjdB1950. */
	protected final static double mjdB1950 = 33281.92345905; // JD 2433282.42345905
	
	/** The Constant mjdJ1900. */
	protected final static double mjdJ1900 = 15020.5; // JD 2415021.0 
	
	/** The Constant mjdJ2000. */
	protected final static double mjdJ2000 = 51544.5; // JD 2551545.0
	//  2451545.0 JD = 1 January 2000, 11:58:55.816 UT, or 11:59:27.816 TAI
	
	/** The Constant B1900. */
	public final static BesselianEpoch B1900 = new BesselianEpoch(1900.0, true);
	
	/** The Constant B1950. */
	public final static BesselianEpoch B1950 = new BesselianEpoch(1950.0, true);
	
	/** The Constant J2000. */
	public final static JulianEpoch J2000 = new JulianEpoch(2000.0, true);
	
	// The precision to which to epochs must match to be considered equal...
	/** The Constant precision. */
	public final static double precision = 1e-3; // in years...
}

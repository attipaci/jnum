/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.astro;

import java.io.Serializable;

import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsToolkit;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

/**
 * Astronomical coordinate epoch for equatorial and ecliptic coordinates. Equatorial and ecliptic coordinate
 * systems used in astronomy are directly or indirectly referenced to Earth's equator, which precesses with
 * a period of 26,000 years around the ecliptic pole. Thus, equatorial and ecliptic coordinates are most
 * commonly referenced to a specific point in time, called an epoch. Some of the most commonly used
 * epochs are B1950 (to which the FK4 reference syastem was fixed), and J2000 (to which the FK5
 * reference system was fixed. Epochs are also useful for tagging apparent coordinates, referenced to the
 * dynamical Earth equator at some time, or to a topocentric location and time.
 * 
 * @author Attila Kovacs
 * 
 * @see EquatorialCoordinates
 * @see EclipticCoordinates
 * @see EquatorialSystem
 *
 */
public abstract class CoordinateEpoch implements Serializable, Comparable<CoordinateEpoch>, FitsHeaderEditing {
    /**
     * 
     */
	private static final long serialVersionUID = -7090908739252631026L;

	/** The nominal year value for this coordinate epoch, e.g. 2000.0 */
	private double year;

	/** 
	 * Instantiates a new coordinate epoch for the specified nominal year for which it is defined 
	 *
	 * @param year     the nominal year of this epoch, e.g. 2000.0, or 1950.0, or 2021.345656456
	 */
	protected CoordinateEpoch(double year) { this.year = year; }


	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null) return false;
		if(!o.getClass().equals(getClass())) return false;
		return year == ((CoordinateEpoch) o).year;
	}
	

	@Override
	public int hashCode() {
		return HashCode.from(year);
	}
	

	@Override
	public int compareTo(CoordinateEpoch epoch) {
		double y1 = getJulianYear();
		double y2 = epoch.getJulianYear();
		if(Math.abs(y1 - y2) < precision) return 0;
		return y1 < y2 ? -1 : 1;
	}
	
	/**
	 * Returns the nominal year for which this epoch is defined.
	 * 
	 * @return     (yr) The nominal year of this epoch, which defines a point in time accoridng to the 
	 *             convention used by this epoch.
	 *             
	 * @see #getJulianYear()
	 * @see #getBesselianYear()
	 */
	public double getYear() { return year; }
	
	/**
	 * Returns the Julian year for which this epoch was defined, even if the epoch does not natively
	 * use Julian years (such as a {@link BesselianEpoch}). Julian year values are defined by J2000
	 * being 12 TT, 1 Jan 2000 = JD 2551545.0, and each Julian year being exactly 365.25 days from 
	 * that point of reference.
	 * 
	 * @return     (yr) The Julian year value for this epoch.
	 * 
	 * @see #getBesselianYear()
	 */
	public abstract double getJulianYear();

	/**
     * <p>
     * Returns the Besselian year for which this epoch was defined, even if the epoch does not natively
     * use Besselian years (such as a {@link JulianEpoch}). Besselian years start when the mean longitude
     * of the Sun is exactly 280 degrees. The Besselian year (B) can be calulated for a Julian date (JD)
     * by the formula provided by Lieske 1979:
     * </p>
     * 
     * <pre>
     *   B = 1900.0 + (JD − 2415020.31352) / 365.242198781
     * </pre>
     * 
     * @return     (yr) The Besselian year value for this epoch.
     * 
     * @see #getJulianYear()
     */
	public abstract double getBesselianYear();
	
	/**
	 * Returns the Modified Julian Date for the reference time of this epoch.
	 * 
	 * @return     (day) The Modified Julian Date of the reference point in time for this epoch.
	 * 
	 * @see #getJulianDate()
	 * @see #getJulianYear()
	 */
	public abstract double getMJD();
	
	/**
	 * Returns the Julian date for the reference time of this epoch.
	 * 
	 * @return     (day) the Julian date of the reference point in time for this epoch.
	 * 
	 * @see #getMJD()
     * @see #getJulianYear()
	 */
	public double getJulianDate() { return getMJD() + 2400000.5; }
	
	
	@Override
    public void editHeader(Header header) throws HeaderCardException { editHeader(header, ""); }
	
	/**
	 * Adds a description of this coordinate epoch as an EQUINOX<i>a</i> keyword in a FITS header.
	 * The <i>a</i> represent a variant for coexisting alternative coordinate system descriptions.
	 * The default FITS coordinate system has no variant (empty string), and non-default alternatives
	 * can be specified with letters starting from A and up to Z.
	 * 
	 * @param header       the FITS header in which to add the EQUINOX<i>a</i> keyword.
	 * @param alt          the FITS coordinate system alternative variant. For the default coordinate
	 *                     system, use an empty string, for alternatives use a single letter
	 *                     starting from A (and up to Z)
	 * @throws HeaderCardException     if there was an error trying to access the FITS header.
	 * 
	 * @see #editHeader(Header)
	 */
	public void editHeader(Header header, String alt) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard("EQUINOX" + alt, year, "The epoch of the quoted coordinates"));
	}
	
	/**
	 * Returns a new coordinate epoch based on the RADESYS and/or EQUINOX keys present in the
	 * FITS header.
	 * 
	 * @param header       the FITS header containing the relevant coordinate reference description
	 *                     for determining the coordinate epoch.
	 * @return     a new coordinate epoch instance defined in the FITS header, or the default
	 *             {@link #J2000} if the FITS header did not contain the relevant keywords.
	 *             
	 * @see #fromHeader(Header, String)
	 * @see #editHeader(Header)
	 */
    public static CoordinateEpoch fromHeader(Header header) { return fromHeader(header, ""); }

    /**
     * Returns a new coordinate epoch based on the RADESYS<i>a</i> and/or EQUINOX<i>a</i> keys present in the
     * FITS header.
     * 
     * @param header       the FITS header containing the relevant coordinate reference description
     *                     for determining the coordinate epoch.
     * @param alt          the FITS coordinate system alternative variant. For the default coordinate
     *                     system, use an empty string, for alternatives use a single letter
     *                     starting from A (and up to Z)
     * @return     a new coordinate epoch instance defined in the FITS header, or the default
     *             {@link #J2000} if the FITS header did not contain the relevant keywords.
     *             
     * @see #fromHeader(Header)
     * @see #editHeader(Header, String)
     */
    public static CoordinateEpoch fromHeader(Header header, String alt) {    
        double year = header.getDoubleValue("EQUINOX" + alt, Double.NaN);    
        String system = header.getStringValue("RADESYS");
        
        if(system == null) {
            if(Double.isNaN(year)) return CoordinateEpoch.J2000;
            if(year == 1900) return CoordinateEpoch.B1900;
            if(year == 1950) return CoordinateEpoch.B1950;
            if(year == 2000) return CoordinateEpoch.J2000;
            if(year < 1984.0) return new BesselianEpoch(year);
            return new JulianEpoch(year);
           
        }
        
        system = system.toUpperCase();
        
        if(system.startsWith("FK4")) {
            if(year == 1900) return CoordinateEpoch.B1900;
            if(year == 1950) return CoordinateEpoch.B1950;
            return new BesselianEpoch(Double.isNaN(year) ? 1950.0 : year);
        }
        
        if(year == 2000) return CoordinateEpoch.J2000;
        return new JulianEpoch(Double.isNaN(year) ? 2000.0 : year);
    }

    /**
     * Returns a new coordinate epoch based on a string representation of the epoch. The string
     * must have a year (integer or floating-point), and may be preceded by 'B' or 'J' (in
     * any case) indicating Besselian or Julian years repsectively. If the year component is
     * not preceded by one of those letters, the convention is to assume Besselian epochs for years
     * prior to 1984.0, and Julian epochs starting with 1984.0.
     * 
     * @param text      A string representation of epoch, e.g. "B1950", "J2000.0" or simply
     *                  1950, or 2000.0.
     *                  
     * @return     a new coordinate epoch instance corresponding to the string.
     *             
     * @throws NumberFormatException    if the string does not have an integer or decimal year
     *                                  value up front, or following a leading B or J.
     * @see #fromHeader(Header)
     * @see #editHeader(Header, String)
     */
	public static CoordinateEpoch forString(String text) throws NumberFormatException {
	    char first = text.charAt(0);
	    
		if(first == 'B' || first == 'b') return new BesselianEpoch(Double.parseDouble(text.substring(1)));
		else if(first == 'J' || first == 'j') return new JulianEpoch(Double.parseDouble(text.substring(1)));
		else {
			double year = Double.parseDouble(text);
			if(year < 1984.0) return new BesselianEpoch(year);
			return new JulianEpoch(year);
		}
	}
	
	/** (day) The length of a Besselian year */
	protected static final double besselianYear = 365.242198781;

	/** (day) The length of a Julian year */
	protected static final double julianYear = 365.25;

	/** Modified Julian Date for the B1900 epoch */
	protected static final double mjdB1900 = 15019.81352; // JD 2415020.31352

	/** Modified Julian Date for the B1950 epoch */
	protected static final double mjdB1950 = 33281.92345905; // JD 2433282.42345905

	/** Modified Julian Date for the J2000 epoch */
	protected static final double mjdJ2000 = 51544.5; // JD 2551545.0
	
	//  2451545.0 JD = 1 January 2000, 11:58:55.816 UT, or 11:59:27.816 TAI
	/** The B1900 epoch */
	public static final BesselianEpoch B1900 = new BesselianEpoch(1900.0);

	/** The B1950 epoch */
	public static final BesselianEpoch B1950 = new BesselianEpoch(1950.0);

	/** The J2000 epoch */
	public static final JulianEpoch J2000 = new JulianEpoch(2000.0);
	
	/** The precision to which to epochs must match to be considered equal... */
	public static final double precision = 1e-5; // in years...
}

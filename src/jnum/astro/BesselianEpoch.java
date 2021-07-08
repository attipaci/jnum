/* *****************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.text.NumberFormat;
import jnum.Util;
import jnum.text.NumberFormating;


/**
 * Besselian coordinate epoch, based on calendar date. Besselian epochs were commonly used prior to the FK5 reference system 
 * that was introduced in 1984, and which switched to Julian dates as its bases. Besselian epochs are normally marked with
 * 'B' in front, most commonly as <b>B1900</b> or <b>B1950</b>. If the type of epoch is not explicitly marked, the convention 
 * is to assume Besselian epochs for dates prior to 1984, and Julian epochs after that.
 * 
 * @author Attila Kovacs
 *
 * @see JulianEpoch
 */
public class BesselianEpoch extends CoordinateEpoch implements NumberFormating {	

	private static final long serialVersionUID = 5133755849976402885L;

	/**
	 * Constructs a Besselian epoch for the specified Besselian year.
	 * 
	 * @param epoch        Besselian epoch year, e.g. 1950.0.
	 */
	public BesselianEpoch(double epoch) { super(epoch); }


	@Override
	public double getBesselianYear() { return getYear(); }


	@Override
	public double getJulianYear() { return JulianEpoch.getYearForMJD(getMJD()); }

	/**
	 * Gets a Julian epoch that is equivalent to this Besselian epoch.
	 * 
	 * @return     the Julian epoch equivalent to this one.
	 */
	public JulianEpoch getJulianEpoch() { 
		return new JulianEpoch(getBesselianYear());
	}
	
	//  B = 1900.0 + (JD - 2415020.31352) / 365.242198781
	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	

    @Override
    public String toString() { return toString(Util.f1); }
    

    @Override
    public String toString(NumberFormat nf) {
        return "B" + nf.format(getYear());
    }

    /**
     * Returns a Besselian year equivalent to the specified Modified Julian Date.
     * 
     * @param MJD       (day) Modified Julian Date
     * @return          the Besselian epoch year equivalent to the same date,
     */
	public static double getYearForMJD(double MJD) {
		return 1900.0 + (MJD - mjdB1900) / julianYear;
	}
	
	/**
	 * Gets the Modified Julian Date for the specified Besselian epoch year
	 * 
	 * @param year     (yr) Besselian epoch year, e.g. 1950.0
	 * @return         the Modified Julian Date corresponding to the specified Besselian epoch year.
	 */
	public static double getMJDForYear(double year) {
		return (year - 1900.0) * julianYear + mjdB1900;
	}

	/**
	 * Creates a new Besselian epoch for the specified Modified Julian Date
	 * 
	 * @param MJD      (day) Modified Julian Date
	 * @return         A new Besselian epoch for the specified date.
	 */
	public static BesselianEpoch forMJD(double MJD) {
		return new BesselianEpoch(getYearForMJD(MJD));		
	}

	/**
     * Creates a new Besselian epoch for the specified Julian Date
     * 
     * @param JD       (day) Julian Date
     * @return         A new Besselian epoch for the specified date.
     */
	public static BesselianEpoch forJulianDate(double JD) { return forMJD(JD - 2400000.5); }

	/**
	 * Creates a news Besselian epoch from the textual representation of the epoch.
	 * 
	 * @param text    String representation of an epoch, such as "B1950", or "1950", or even "J2000.0" 
	 * @return        A new Besselian epoch for the string specification
	 * @throws NumberFormatException      If a Besselian epoch could not be parsed from the string.
	 */
	public static BesselianEpoch forString(String text) throws NumberFormatException {
		if(text.charAt(0) == 'B') return new BesselianEpoch(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'J') return new BesselianEpoch(getYearForMJD(JulianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else return new BesselianEpoch(Double.parseDouble(text));
	}	
	
}

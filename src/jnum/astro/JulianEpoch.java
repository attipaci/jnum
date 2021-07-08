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
 * Julian coordinate epoch, based on Julian date. Julian date is a widely used astronomical time measure because it is
 * strictly linear with time (no leaps of any sort, and coupled to terrestrial time). Julian epochs have been favored 
 * since their adoption in the FK5 system in 1984, with a marked change from the Besselian epochs used prior to FK5. 
 * The Julian year is exactly 365.25 days long.  Julian epochs are normally marked with a 'J', such as <b>J2000</b>, 
 * which is defined as 0 TT, 1 Jan 2000, or as JD 24151544.5. If the type of epoch is not explicitly marked, 
 * the convention is to assume Julian epochs for dates starting in 1984.
 * 
 * @author Attila Kovacs
 *
 * @see BesselianEpoch
 */
public class JulianEpoch extends CoordinateEpoch implements NumberFormating {

	private static final long serialVersionUID = 8377319626045474497L;

	/**
	 * Constructs a new Julian epoch for the specified Julian year.
	 * 
	 * @param epoch    Julian year, e.g. 2000 for J2000 (0 TT, 1 Jan 2000 = JD 24151544.5)
	 */
	public JulianEpoch(double epoch) { super(epoch); }


	@Override
	public double getJulianYear() { return getYear(); }


	@Override
	public double getBesselianYear() { return BesselianEpoch.getYearForMJD(getMJD()); }

	/**
	 * Gets a Besselian epoch for the same date as this one.
	 * 
	 * @return     The equivalent Besselian coordinate epoch.
	 */
	public BesselianEpoch getBesselianEpoch() { 
		return new BesselianEpoch(getBesselianYear());
	}


	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	

    @Override
    public String toString() { return toString(Util.f1); }
    
    
    @Override
    public String toString(NumberFormat nf) {
        return "J" + nf.format(getYear());
    }
	
	
	//  J = 2000.0 + (MJD - 51544) / 365.25
    /**
     * Returns a Julian year equivalent to the specified Modified Julian Date.
     * 
     * @param MJD       (day) Modified Julian Date
     * @return          the Julian epoch year equivalent to the same date,
     */
	public static double getYearForMJD(double MJD) {
		return 2000.0 + (MJD - mjdJ2000) / julianYear;
	}

	/**
     * Gets the Modified Julian Date for the specified Julian epoch year
     * 
     * @param year     (yr) Julian epoch year, e.g. 2000.0
     * @return         the Modified Julian Date corresponding to the specified Julian epoch year.
     */
	public static double getMJDForYear(double year) {
		return (year - 2000.0) * julianYear + mjdJ2000;
	}

	/**
     * Creates a new Julian epoch for the specified Modified Julian Date
     * 
     * @param MJD      (day) Modified Julian Date
     * @return         A new Julian epoch for the specified date.
     */
	public static JulianEpoch forMJD(double MJD) {
		return new JulianEpoch(getYearForMJD(MJD));		
	}
	
    /**
     * Creates a new Julian epoch for the specified Julian Date
     * 
     * @param JD       (day) Julian Date
     * @return         A new Julian epoch for the specified date.
     */
    public static JulianEpoch forJulianDate(double JD) { return forMJD(JD - 2400000.5); }
	
    /**
     * Creates a news Julian epoch from the textual representation of the epoch.
     * 
     * @param text    String representation of an epoch, such as "J2000", or even "B1950.0" 
     * @return        A new Julian epoch for the string specification
     * @throws NumberFormatException      If a Julian epoch could not be parsed from the string.
     */
	public static JulianEpoch forString(String text) throws NumberFormatException {
		if(text.charAt(0) == 'J') return new JulianEpoch(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'B') return new JulianEpoch(getYearForMJD(BesselianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else return new JulianEpoch(Double.parseDouble(text));
	}	
	

}

/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
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


public abstract class CoordinateEpoch implements Serializable, Comparable<CoordinateEpoch>, FitsHeaderEditing {

	private static final long serialVersionUID = -7090908739252631026L;

	private double year;


	public CoordinateEpoch(double epoch) { year = epoch; }


	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null) return false;
		if(!o.getClass().equals(getClass())) return false;
		return year == ((CoordinateEpoch) o).year;
	}
	

	@Override
	public int hashCode() {
		return super.hashCode() ^ HashCode.from(year);
	}
	

	@Override
	public int compareTo(CoordinateEpoch epoch) {
		double y1 = getJulianYear();
		double y2 = epoch.getJulianYear();
		if(Math.abs(y1 - y2) < precision) return 0;
		return y1 < y2 ? -1 : 1;
	}
	
	

	public double getYear() { return year; }
	

	public abstract double getJulianYear();


	public abstract double getBesselianYear();
	

	public abstract double getMJD();
	


	

	public double getJulianDate() { return getMJD() + 2400000.5; }
	
	
	@Override
    public void editHeader(Header header) throws HeaderCardException { editHeader(header, ""); }
	

	public void editHeader(Header header, String alt) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard("EQUINOX" + alt, year, "The epoch of the quoted coordinates"));
	}
	

    public static CoordinateEpoch fromHeader(Header header) { return fromHeader(header, ""); }


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
        
        system.toUpperCase();
        
        if(system.startsWith("FK4")) {
            if(year == 1900) return CoordinateEpoch.B1900;
            if(year == 1950) return CoordinateEpoch.B1950;
            return new BesselianEpoch(Double.isNaN(year) ? 1950.0 : year);
        }
        
        if(year == 2000) return CoordinateEpoch.J2000;
        return new JulianEpoch(Double.isNaN(year) ? 2000.0 : year);
    }


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
	

	protected final static double besselianYear = 365.242198781;

	protected final static double julianYear = 365.25;

	protected final static double mjdB1900 = 15019.81352; // JD 2415020.31352

	protected final static double mjdB1950 = 33281.92345905; // JD 2433282.42345905
	
	protected final static double mjdJ1900 = 15020.5; // JD 2415021.0 

	protected final static double mjdJ2000 = 51544.5; // JD 2551545.0
	
	//  2451545.0 JD = 1 January 2000, 11:58:55.816 UT, or 11:59:27.816 TAI
	public final static BesselianEpoch B1900 = new BesselianEpoch(1900.0);

	public final static BesselianEpoch B1950 = new BesselianEpoch(1950.0);

	public final static JulianEpoch J2000 = new JulianEpoch(2000.0);
	
	// The precision to which to epochs must match to be considered equal...
	public final static double precision = 1e-3; // in years...
}

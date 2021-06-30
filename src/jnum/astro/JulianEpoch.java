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



public class JulianEpoch extends CoordinateEpoch {

	private static final long serialVersionUID = 8377319626045474497L;


	public JulianEpoch(double epoch) { super(epoch); }


	@Override
	public double getJulianYear() { return getYear(); }


	@Override
	public double getBesselianYear() { return BesselianEpoch.getYearForMJD(getMJD()); }


	public BesselianEpoch getBesselianEpoch() { 
		return new BesselianEpoch(getBesselianYear());
	}


	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	

    @Override
    public String toString() { return toString(Util.f1); }
    

    public String toString(NumberFormat nf) {
        return "J" + nf.format(getYear());
    }
	
	
	//  J = 2000.0 + (MJD - 51544) / 365.25
	public static double getYearForMJD(double MJD) {
		return 2000.0 + (MJD - mjdJ2000) / julianYear;
	}


	public static double getMJDForYear(double year) {
		return (year - 2000.0) * julianYear + mjdJ2000;
	}


	public static JulianEpoch forMJD(double MJD) {
		return new JulianEpoch(getYearForMJD(MJD));		
	}
	


    public static JulianEpoch forJulianDate(double JD) { return forMJD(JD - 2400000.5); }
	

	public static JulianEpoch forString(String text) throws IllegalArgumentException {
		if(text.charAt(0) == 'J') return new JulianEpoch(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'B') return new JulianEpoch(getYearForMJD(BesselianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else return new JulianEpoch(Double.parseDouble(text));
	}	
	

}

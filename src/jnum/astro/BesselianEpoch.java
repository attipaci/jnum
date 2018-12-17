/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum.astro;

import java.text.NumberFormat;

import jnum.Util;



public class BesselianEpoch extends CoordinateEpoch {	

	private static final long serialVersionUID = 5133755849976402885L;

	public BesselianEpoch(double epoch) { super(epoch); }


	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getBesselianYear()
	 */
	@Override
	public double getBesselianYear() { return getYear(); }

	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getJulianYear()
	 */
	@Override
	public double getJulianYear() { return JulianEpoch.getYearForMJD(getMJD()); }

	public JulianEpoch getJulianEpoch() { 
		return new JulianEpoch(getBesselianYear());
	}
	
	//  B = 1900.0 + (JD - 2415020.31352) / 365.242198781


	/* (non-Javadoc)
	 * @see jnum.astro.CoordinateEpoch#getMJD()
	 */
	@Override
	public double getMJD() {
		return getMJDForYear(getYear());
	}
	
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { return toString(Util.f1); }
    

    public String toString(NumberFormat nf) {
        return "B" + nf.format(getYear());
    }

	public static double getYearForMJD(double MJD) {
		return 1900.0 + (MJD - mjdB1900) / julianYear;
	}
	

	public static double getMJDForYear(double year) {
		return (year - 1900.0) * julianYear + mjdB1900;
	}


	public static BesselianEpoch forMJD(double MJD) {
		return new BesselianEpoch(getYearForMJD(MJD));		
	}



	public static BesselianEpoch forJulianDate(double JD) { return forMJD(JD - 2400000.5); }

	public static BesselianEpoch forString(String text) throws NumberFormatException, IllegalArgumentException {
		if(text.charAt(0) == 'B') return new BesselianEpoch(Double.parseDouble(text.substring(1)));
		else if(text.charAt(0) == 'J') return new BesselianEpoch(getYearForMJD(JulianEpoch.getMJDForYear(Double.parseDouble(text.substring(1)))));
		else return new BesselianEpoch(Double.parseDouble(text));
	}	
	
}

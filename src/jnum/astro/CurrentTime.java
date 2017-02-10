/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.astro;


import jnum.Unit;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrentTime.
 */
public final class CurrentTime {

	/** The DU t1. */
	private static double DUT1 = 0.0;
	//public static double errorUT1 = 1.0 * Unit.s;
	
	/** The time. */
	private static AstroTime time = new AstroTime();
	
	/**
	 * Gets the dU t1.
	 *
	 * @return the dU t1
	 */
	public static double getDUT1() { return DUT1; }
	
	/**
	 * Sets the dU t1.
	 *
	 * @param value the new dU t1
	 */
	public static void setDUT1(double value) { DUT1 = value; }
	
	// Terrestrial Time is based on Atomic Time (not linked to Earth rotation)
	// TT = TAI + 32.184s
	/**
	 * Tt.
	 *
	 * @return the double
	 */
	public static synchronized double TT() {
		return time.now().getTT();	
	}
	
	/**
	 * Utc.
	 *
	 * @return the double
	 */
	public static synchronized double UTC() {
		return TAI() - LeapSeconds.getCurrentLeap();
	}
	
	/**
	 * Lst.
	 *
	 * @param longitude the longitude
	 * @return the double
	 */
	public static synchronized double LST(double longitude) {
		return time.now().getLMST(longitude);
	}
	
	// UT1 reflects most correctly the rotational position of Earth (i.e. to be used in astronomy)
	// needs DUT1 lookup from server via refreshData() to be accurate
	/**
	 * U t1.
	 *
	 * @return the double
	 */
	public static synchronized double UT1() {
		return UTC() + DUT1;
	}
	
	// UT2 is historical. It smoothes out seasonal variations of UT1
	// UT2 = UT1 + 0.022 sin(2*pi*T) - 0.012 cos(2*pi*T) - 0.006 sin(4*pi*T) + 0.007 cos(4*pi*T)  
	// where T is the date in Besselian years
	/**
	 * U t2.
	 *
	 * @return the double
	 */
	public static synchronized double UT2() {
		double UTC = UTC();
		double PIT = Math.PI * UTC / Unit.year;	
		return UTC + (0.022 * Math.sin(2.0*PIT) - 0.012 * Math.cos(2.0*PIT) - 0.006*Math.sin(4.0*PIT) + 0.007*Math.cos(4.0*PIT)) * Unit.s;
	}
	
	
	// TAI is atomic time, thus not linked to Earth's rotation...
	// TAI-UTC(BIPM) = 33.000 000 seconds    
	/**
	 * Tai.
	 *
	 * @return the double
	 */
	public static synchronized double TAI() {
		return time.now().getTAI();
	}
	
	// GPS time is the time used for GPS positioning. Based on Atomic time.
	/**
	 * Gpst.
	 *
	 * @return the double
	 */
	public static synchronized double GPST() {
		return time.now().getGPSTime();
	}
	
	// TCG is based on the Atomic Time but corrects for the gravitational dilation on Earth
	// Thus it is a good measure of time in space.
	// TT = TCG − LG × (JDTCG − 2443144.5003725) × 86400
	// LG = 6.969290134e-10
	/**
	 * Tcg.
	 *
	 * @return the double
	 */
	public static synchronized double TCG() {
		return time.now().getTCG();
	}
	
	
	static { refreshData(); }
	
	// TODO
	// Contact the www.iers.org for data
	// console error if it's not possible to obtain...
	/**
	 * Refresh data.
	 */
	public static void refreshData() {
		
		
	}
}

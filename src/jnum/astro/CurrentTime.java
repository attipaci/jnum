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


public final class CurrentTime {

	/** private constructor since we do not want to instantiate this class. */
	private CurrentTime() {}
	
	public static double getDUT1() { return dUT1; }
	
	public static void setDUT1(double value) { dUT1 = value; }
	
	// Terrestrial Time is based on Atomic Time (not linked to Earth rotation)
	// TT = TAI + 32.184s
	public static synchronized double TT() {
		return time.now().TT();	
	}
	
	public static synchronized double UTC() {
		return time.now().UTC();
	}
	
	public static synchronized double ERA() {
        return time.now().ERA(dUT1);
    }
	
	public static synchronized double GMST(double longitude) {
        return time.now().GMST(dUT1);
    }
    
    public static synchronized double GAST(double longitude) {
        return time.now().GAST(dUT1);
    }

	public static synchronized double LMST(double longitude) {
		return time.now().LMST(longitude, dUT1);
	}
	
	public static synchronized double LST(double longitude) {
        return time.now().LST(longitude, dUT1);
    }
    
	
	// UT1 reflects most correctly the rotational position of Earth (i.e. to be used in astronomy)
	// needs DUT1 lookup from server via refreshData() to be accurate
	public static double UT1() {
		return AstroTime.getTimeOfDay(UTC() + dUT1);
	}
	
	// UT2 is historical. It smoothes out seasonal variations of UT1
	// UT2 = UT1 + 0.022 sin(2*pi*T) - 0.012 cos(2*pi*T) - 0.006 sin(4*pi*T) + 0.007 cos(4*pi*T)  
	// where T is the date in Besselian years
	public static synchronized double UT2() {
	    return time.now().UT2(dUT1);
	}

	// TAI is atomic time, thus not linked to Earth's rotation...
	// TAI-UTC(BIPM) = 33.000 000 seconds    
	public static synchronized double TAI() {
		return time.now().TAI();
	}
	
	// GPS time is the time used for GPS positioning. Based on Atomic time.
	public static synchronized double GPST() {
		return time.now().GPSTime();
	}
	
	// TCG is based on the Atomic Time but corrects for the gravitational dilation on Earth
	// Thus it is a good measure of time in space.
	// TT = TCG − LG × (JDTCG − 2443144.5003725) × 86400
	// LG = 6.969290134e-10
	public static synchronized double TCG() {
		return time.now().TCG();
	}
	

    private static double dUT1 = 0.0;
    private static AstroTime time = new AstroTime();
}

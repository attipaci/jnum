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

import jnum.Unit;

/**
 * A class with a set of static methods providing various measures of the current time for use in astronomy.  
 * Among other things, it can provide present Earth rotation measures, such as Earth Rotation Angle (ERA),
 * Sidereal Times (GMST, GAST, LMST, LST) for some locatio, etc. In order to do that accurately, the user
 * should call {@link #setDUT1(double)} first to provide a current value of the UT1 - UTC time difference,
 * whose current (and historical) values are published in IERS Bulletin A. Additionally, you may want
 * to check that the leap seconds information, managed by {@link LeapSeconds} is up to date also.
 * 
 * 
 * @author Attila Kovacs
 *
 * @see AstroTime
 * @see LeapSeconds
 *
 */
public final class CurrentTime {

    /** private constructor since we do not want to instantiate this class. */
    private CurrentTime() {}

    /**
     * Sets a new value for the DUT1 = UT1 - UTC time difference, used for providing accurate Earth rotation
     * measures and sidereal times. DUT1 tracks changes in Earth rotation relative to Atomic Time (TAI) between
     * the introduction of full leap seconds. As such, the DUT1 values are per definition less that &plusmn;0.5s.
     * The current (and historical) values of DUT1 are published in IERS Bulletin A.
     * 
     * 
     * @param value        (s) The currently set value for the UT1 - UTC time difference in the range [-0.5:0.5].
     * @throws IllegalArgumentException    if the specified is not in the expected range of &plusmn;0.5s.
     */
    public static void setDUT1(double value) throws IllegalArgumentException { 
        if(!(Math.abs(value) <= 0.5 * Unit.s)) throw new IllegalArgumentException("DUT1 value must be in the range [-0.5:0.5].");
        dUT1 = value;
    }

    /**
     * Returns the DUT1 = UT1 - UTC time difference that is currently used by this class.
     * By default that will be 0.0, unless the user set a different value prior by
     * calling {@link #setDUT1(double)}.
     * 
     * @return     (s) The currently set value for the UT1 - UTC time difference in the range [-0.5:0.5].
     */
    public static double getDUT1() { return dUT1; }

    
    /**
     * Returns the current fractional Julian Date, measured from a mythical starting point at 12pm TT, January 1, 4713 BC.
     * Julian date measures are tied to Terrestrial Time, and advance strictly linearly.
     * 
     * @return      (day) The current Julian Date.
     * 
     * @see AstroTime#JD()
     * @see #MJD()
     * @see #TT()
     */
    public static synchronized double JD() {
        return time.now().MJD(); 
    }


    /**
     * Returns the current value of the Modified Julian Date.
     * 
     * @return  (day) The current Modified Julian Date.
     * 
     * @see AstroTime#MJD()
     * @see JD()
     * @see TT()
     */
    public static synchronized double MJD() {
        return time.now().MJD(); 
    }


    /**
     * Returns the current value of Terrestrial Time (TT) of day.
     * 
     * @return     (s) The current value of Terrestrial Time, in the range [0.0:86400.0].
     * 
     * @see AstroTime#TT()
     * @see #JD()
     * @see #MJD()
     */
    public static synchronized double TT() {
        return time.now().TT();	
    }

    /**
     * Returns the current value of Universal Coordinated Time (UTC) of day.
     * 
     * @return     (s) The current value of UTC, in the range [0.0:86400.0).
     * 
     * @see AstroTime#UTC()
     * @see #UT1()
     */
    public static synchronized double UTC() {
        return time.now().UTC();
    }

    /**
     * Returns the current value of the Earth Rotation Angle (ERA). For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @return     (rad) The current Earth Rotation Angle.
     * 
     * @see #setDUT1(double)
     * @see AstroTime#ERA(double)
     * @see #UT1()
     * @see #GMST()
     * @see #GAST()
     * @see #LMST(double)
     * @see #LST(double)
     */
    public static synchronized double ERA() {
        return time.now().ERA(dUT1);
    }

    /**
     * Returns the current value of the Greenwich Mean Sidereal Time (GMST). For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @return     (rad) The current value of GMST, in the range of [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#GMST(double)
     * @see #UT1()
     * @see #ERA()
     * @see #GAST()
     * @see #LMST(double)
     * @see #LST(double)
     */
    public static synchronized double GMST() {
        return time.now().GMST(dUT1);
    }

    /**
     * Returns the current value of the Greenwich Apparent Sidereal Time (GMST). For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @return     (rad) The current value of GAST, in the range of [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#GAST(double)
     * @see #UT1()
     * @see #ERA()
     * @see #GMST()
     * @see #LMST(double)
     * @see #LST(double)
     */
    public static synchronized double GAST() {
        return time.now().GAST(dUT1);
    }

    /**
     * Returns the current value of the Local Mean Sidereal Time (GMST) for the specified
     * location (longitude) on Earth. For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @param longitude (rad) The geodetic longitude (GPS, WGS84) of the observer on Earth.
     * @return          (rad) The current value of LMST, in the range of [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#LMST(double, double)
     * @see #UT1()
     * @see #ERA()
     * @see #GAST()
     * @see #LST(double)
     */
    public static synchronized double LMST(double longitude) {
        return time.now().LMST(longitude, dUT1);
    }

    /**
     * Returns the current value of the Local (Apparent) Sidereal Time (LST or LAST) for the specified
     * location (longitude) on Earth. For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @param longitude (rad) The geodetic longitude (GPS, WGS84) of the observer on Earth.
     * @return          (rad) The current value of LST, in the range of [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#LST(double, double)
     * @see #UT1()
     * @see #ERA()
     * @see #GAST()
     * @see #LMST(double)
     */
    public static synchronized double LST(double longitude) {
        return time.now().LST(longitude, dUT1);
    }


    // UT1 reflects most correctly the rotational position of Earth (i.e. to be used in astronomy)
    // needs DUT1 lookup from server via refreshData() to be accurate

    /**
     * Returns the current value of UT1 time of day. UT1 reflects the actual the rotational position of Earth.
     * It differs from UTC by a constantly chan ging DUT1 value, whose current (and historical) values
     * are published in IERC Bulletin A. For the value to
     * be precise the caller should set the UT1 - UTC time difference by calling {@link #setDUT1(double)}
     * once prior to this call.
     * 
     * @return     (s) The current value of UT1, in the range [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#UT1(double)
     * @see #UTC()
     */
    public static double UT1() {
        return AstroTime.getTimeOfDay(UTC() + dUT1);
    }

    /**
     * Returns the current value of UT2 time of day. UT2 was used to smooth out seasonal variations of UT1.
     * he UT1 - UTC time difference by calling {@link #setDUT1(double)} once prior to this call.
     * 
     * @return     (s) The current value of UT2, in the range [0.0:86400.0).
     * 
     * @see #setDUT1(double)
     * @see AstroTime#UT2(double)
     * @see #UTC()
     */
    public static synchronized double UT2() {
        return time.now().UT2(dUT1);
    }

    /**
     * Returns the current value of Atomic Time (TAI) of day.
     * 
     * @return     (s) The current value of TAI, in the range [0.0:86400.0).
     * 
     * @see AstroTime#TAI()
     * @see #TT()
     * @see #TCG()
     * @see #TDB()
     * @see #GPST()
     */
    public static synchronized double TAI() {
        return time.now().TAI();
    }

    /**
     * Returns the current value of the GPS time of day.
     * 
     * @return     (s) The current value of the GPS time, in the range [0.0:86400.0).
     * 
     * @see AstroTime#GPST()
     * @see #TAI()
     * @see #TT()
     * @see #TCG()
     * @see #TDB()
     */
    public static synchronized double GPST() {
        return time.now().GPST();
    }

    /**
     * Returns the current value of the TCG time of day. TCG is a measure of time in space, unaffected
     * by Earth's gravity.
     * 
     * @return     (s) The current value of TCG, in the range [0.0:86400.0).
     * 
     * @see AstroTime#GPST()
     * @see #TAI()
     * @see #TT()
     * @see #TDB()
     * @see #GPST()
     */
    public static synchronized double TCG() {
        return time.now().TCG();
    }

    /**
     * Returns the current value of the  Dynamic Barycentric Time (TDB) time of day. TDB is a measure of time at the Solar-System
     * Barycenter.
     * 
     * @return     (s) The current value of TDB, in the range [0.0:86400.0).
     * 
     * @see AstroTime#GPST()
     * @see #TAI()
     * @see #TT()
     * @see #TCG()
     * @see #GPST()
     */
    public static synchronized double TDB() {
        return time.now().TDB();
    }


    /** The UT1 - UTC time difference to use */
    private static double dUT1 = 0.0;

    /** The underlying astronomical time */
    private static AstroTime time = new AstroTime();
}

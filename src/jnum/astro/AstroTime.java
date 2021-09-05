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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import jnum.Constant;
import jnum.Unit;
import jnum.math.Vector2D;
import jnum.text.TimeFormat;
import jnum.util.HashCode;


/**
 * <P>
 * A class for representing the astronomical time of some observation as Terrestrial Time (TT). Astronomical times are strictly 
 * monotonic and have a linear progression. This class is suitable for representing time down to 0.1 &mu;s precision, due to the 
 * numerical resolution of the double-precision floating point precision of the underlying storage as a Modified Julian Date.
 * It provides methods for accurately representating time on Earth, or in space to that precision. 
 * </p>
 * <p>
 * The time represented by this class is not tied to Earth rotation or polar wobble. As such it does not provide the most
 * precise measure for Earth orientation or sidereal time. Obtaining those values precisely is required for conducting 
 * astronomical observation. Therefore, if needed, one should use the {@link CurrentTime} class instead, or alongside this class.
 * </p>
 * 
 * @see CurrentTime
 * 
 * @author Attila Kovacs
 *
 */
public class AstroTime implements Serializable, Comparable<AstroTime> {

    /** */
    private static final long serialVersionUID = 890383504654665623L;

    /** The time is internally stored as a Modified Julian Date */ 
    private double MJD;

    /**
     * Instantiates a new default time, with an initial NaN value. 
     */
    public AstroTime() {
        MJD = Double.NaN;   
    }

    /**
     * Instantiates a new time from a standard Java/UNIX time with millisecond precision.
     * 
     * @param millis    (ms) UNIX time as milliseconds since 1 Jan 1970.
     * 
     * @see #setUNIXMillis(long) 
     */
    public AstroTime(long millis) { setUNIXMillis(millis); }


    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(MJD);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof AstroTime)) return false;
        return MJD == ((AstroTime) o).MJD;
    }

    @Override
    public int compareTo(AstroTime time) {
        return Double.compare(MJD, time.MJD);
    }

    /**
     * Sets the current time.
     * 
     * @return  this time object.
     * 
     * @see #setUNIXMillis(long)
     */
    public AstroTime now() {
        setUNIXMillis(System.currentTimeMillis()); 
        return this;
    }

    /**
     * Gets the time represented by this object as a Java date.
     * 
     * @return  the date corresponding to the astronomical time represented by this object.
     * 
     * @see AstroTime#setTime(Date)
     */
    public final Date getDate() { return new Date(getUNIXMillis()); }

    /**
     * Sets a new time value as a Java date. 
     * 
     * @param date      the Java date for the new time value.
     * 
     * @see #getDate()
     */
    public void setTime(Date date) { setUNIXMillis(date.getTime()); }

 
    /**
     * Returns the UTC time represented by this object, as Java/UNIX time milliseconds (since 0 UTC 1 Jan 1970), 
     * accounting for historically introduced leap seconds as necessary.
     * 
     * @return  (ms) Java/UNIX time milliseconds since 0 UTC, 1 Jan 1970.
     * 
     * @see #setUNIXMillis(long)
     * @see #setUNIXTime(double)
     * @see LeapSeconds
     */
    public final long getUNIXMillis() { 
        return getUNIXMillis(MJD);
    }
    
    
    /**
     * Sets the time represented by this object to UTC milliseconds since J2000, accounting for
     * historically introduced leap seconds as necessary.
     * 
     * @param millis    (ms) UTC milliseconds since J2000.
     * 
     * @see #setUNIXTime(double)
     * @see #getUNIXMillis()
     * @see LeapSeconds
     */
    public void setUNIXMillis(long millis) {
        MJD = getMJD(millis);
    }

    /**
     * Sets the time represented by this object to UTC seconds since J2000, accounting for
     * historically introduced leap seconds as necessary.
     * 
     * @param utc    (s) Java/UNIX time seconds (since 0 UTC, 1 Jan 1970).
     * 
     * @see #setUNIXMillis(long)
     * @see #getUNIXMillis()
     * @see LeapSeconds
     */
    public final void setUNIXTime(double utc) {
        MJD = getMJD(1000.0 * utc);
    }

    /**
     * Returns the UTC time of day.
     * 
     * @return  (s) UTC time of day, in the [0:86400.0) range.
     * 
     * @see #setUNIXTime(double)
     * @see #getUNIXMillis()
     * @see #getTimeOfDay(double)
     * @see LeapSeconds
     */
    public final double UTC() {
        return 1e-3 * getUNIXMillis() % dayMillis;
    }
    
    /**
     * Returns the UT1 time of day.
     * 
     * @param dUT1      (s) UT1 - UTC time difference (within 0.5s), as published by IERS Bulletin A.
     * @return          (s) UT1 time of day, in the range of [0.0:86400.0).
     * 
     * @see #ERA(double)
     * @see #LST(double, double)
     * @see #LMST(double, double)
     * @see #GAST(double)
     * @see #GMST(double)
     */
    public final double UT1(double dUT1) {
        return UTC() + dUT1;
    }
    
    /**
     * Returns the UT2 time of day. UT2 is a time measure of historical importance. It smoothes out seasonal variations of UT1.
     * 
     * @param dUT1      (s) UT1 - UTC time difference (within 0.5s), as published by IERS Bulletin A.
     * @return          (s) UT2 time of day, in the range of [0.0:86400.0).
     * 
     * @see #UT1(double)
     */
    public final double UT2(double dUT1) {
        double UTC = UTC();
        double PIT = Math.PI * UTC / Unit.year; 
        // UT2 = UT1 + 0.022 sin(2*pi*T) - 0.012 cos(2*pi*T) - 0.006 sin(4*pi*T) + 0.007 cos(4*pi*T)  
        // where T is the date in Besselian years
        return AstroTime.getTimeOfDay(UTC + dUT1 + (0.022 * Math.sin(2.0*PIT) - 0.012 * Math.cos(2.0*PIT) - 0.006*Math.sin(4.0*PIT) + 0.007*Math.cos(4.0*PIT)) * Unit.s);
    }
    
    /**
     * Gets the Modified Julian Date (MJD) represented by this object.
     * 
     * @return      (day) The standatd TT-based Modified Julian Date. 
     * 
     * @see #TCG_MJD()
     * @see #UTC_MJD()
     * @see #TT()
     * @see #setTT2000(double)
     */
    public final double MJD() { return MJD; }
    
    /**
     * Gets the TCG-tied Modified Julian Date (MJD) represented by this object. This would be
     * the equivalent of a true MJD measure in space.
     * 
     * @return      (day) The Modified Julian Date that starts at TCG 0 (instead of TT 0), and is therefore
     *              a 'proper' measure of MJD in space.
     * 
     * @see #TCG_MJD()
     * @see #MJD()
     * @see #UTC()
     * @see #setUNIXTime(double)
     */
    public final double TCG_MJD() { return (MJD() - EMJD) / (1.0 - LG) + EMJD; }
    
    /**
     * Gets the UTC-tied Modified Julian Date (MJD) represented by this object. The canonical Julian day starts
     * at TT0, but this call returns a proxy version of the Julian date, in which the day starts at UTC 0.
     * While not a proper Julian Date, unfortunately this convention is often used by sloppy astronomers, and
     * hence we support it too.
     * 
     * @return      (day) The Modified Julian Date that unconventionally starts at UTC 0 (instead of TT 0).
     * 
     * @see #TCG_MJD()
     * @see #MJD()
     * @see #UTC()
     * @see #setUNIXTime(double)
     */
    public final double UTC_MJD() { 
        return MJDJ2000 + (getUNIXMillis() - millisJ2000) / Unit.day;
    }

    /**
     * Sets the time represented by this object to the Modified Julian Date (MJD) that properly starts at TT 0
     * 
     * @param date  (day) The proper (TT-based) Modified Julian Date.
     * 
     * @see #MJD()
     */
    public void setMJD(double date) { MJD = date; }

    /**
     * Returns the Julian Date for the time represented by this object. Apart from having a muthical origin
     * much further back in time than MJD, the Julian day also starts at TT 12h. Many astronomical libraries
     * commonly use JD as their principal time measure.
     * 
     * @return  (day) The Julian Date corresponding to the time represented.
     * 
     * @see #setJD(double)
     * @see #MJD()
     * @see AstroTime#JD_MJD0
     */
    public final double JD() { return JD_MJD0 + MJD; }

    /**
     * Sets a new time as a Julian Date.
     * 
     * @param JD    (day) The Julian Date corresponding to the time represented.
     * 
     * @see #JD()
     * @see #setMJD(double)
     * @see AstroTime#JD_MJD0
     */
    public void setJD(double JD) { setMJD(JD - JD_MJD0); }

    /**
     * Gets the Terrestrial Time (TT) seconds since midnight TT.
     * 
     * @return      (s) Terrestrial Time (TT) seconds since midnight TT, in the [0.0:86400.0) range.
     * 
     * @see #TT2000()
     * @see #MJD()
     */
    public final double TT() {
        return (MJD - (int)Math.floor(MJD)) * Unit.day;
    }
    
    /**
     * Gets the Terrestrial Time (TT) seconds since J2000.
     * 
     * @return      (s) Terrestrial Time (TT) seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @see #TT()
     * @see #MJD()
     * @see #setTT2000(double)
     */
    public final double TT2000() {
        return (MJD - MJDJ2000) * Unit.day;
    }

    /**
     * Sets a new time as Terrestrial Time (TT) seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @param TT    (s) the new time as Terrestrial Time seconds since J2000.
     * 
     * @see #TT2000()
     * @see #setMJD(double)
     */
    public void setTT2000(double TT) { MJD = Math.floor(MJD) + TT / Unit.day; }

    /**
     * Returns the Atomic Time (TAI) seconds since midnight TAI. TAI lags TT by a fixed amount
     * of {@value #TAI2TT} seconds.
     * 
     * @return      (s) Atomic Time (TAI) seconds since midnight TAI, in the [0.0:86400.0) range.
     * 
     * @see #TAI2000()
     * @see #TT()
     * @see #TAI2TT
     */
    public final double TAI() { return getTimeOfDay(TAI2000()); }

    
    /**
     * Returns the Atomic Time (TAI) seconds since J2000. TAI lags TT by a fixed amount
     * of {@value #TAI2TT} seconds.
     * 
     * @return      (s) Atomic Time (TAI) seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @see #setTAI2000(double)
     * @see #TT2000()
     * @see #TAI2TT
     */
    public final double TAI2000() { return TT2000() - TAI2TT; }
    
    /**
     * Sets a new time as Atomic Time (TAI) seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @param TAI   (s) the new time as Atomic Time seconds since J2000.
     * 
     * @see #TAI2000()
     * @see #TAI2TT
     */
    public void setTAI2000(double TAI) { setTT2000(TAI + TAI2TT); }

    /**
     * Returns TCG as seconds since midnight TCG. TCG is based on Terrestrial Time (TT) but corrects 
     * for the gravitational dilation on Earth. Thus TCG is a good measure of time in space.
     * 
     * @return      (s) TCG seconds since midnight TCG, in the [0.0:86400.0) range.
     * 
     * @see #TCG2000()
     * @see #TT()
     * @see #LG
     * @see #EMJD
     */
    public final double TCG() { return getTimeOfDay(TCG2000()); }
    
    /**
     * Returns TCG as seconds since J2000. TCG is based on Terrestrial Time (TT) but corrects 
     * for the gravitational dilation on Earth. Thus TCG is a good measure of time in space.
     * 
     * @return      (s) TCG seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @see #setTCG2000(double)
     * @see #TT2000()
     * @see #LG
     * @see #EMJD
     */
    public final double TCG2000() {
        // TT = TCG − LG × (JDTCG − 2443144.5003725) × 86400
        // LG = 6.969290134e-10
        return TT2000() + LG * (MJD - EMJD) * Unit.day;
    }

    /**
     * Sets a new time as TCG seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @param TCG   (s) the new time as TCG seconds since J2000.
     * 
     * @see #TCG2000()
     * @see #LG
     * @see #EMJD
     */
    public void setTCG2000(double TCG) {
        setTT2000(TCG - LG * (MJD - EMJD) * Unit.day);
    }

    /**
     * Returns the Barycentric Dynamic time (TDB) seconds since midnight TDB. TDB is based on 
     * Terrestrial Time (TT) but contains relativistic corrections that reference this
     * time measure at the Solar System Barycenter. Some astronomical calculations require
     * TDB for input. Since the difference to TT is tiny, it is generally OK to
     * substitute TT insteasd OF TDB, unless the extra precision is critical.
     * 
     * @return      (s) Barycentric Dynamic Time (TDB) seconds since midnight TDB, in the [0.0:86400.0) range.
     * 
     * @see #TDB2000()
     * @see #TT()
     * @see #LG
     * @see #EMJD
     */
    public final double TDB() { return getTimeOfDay(TDB2000()); }

    
    /**
     * Returns the Barycentric Dynamic time (TDB) seconds since J2000. TDB is based on 
     * Terrestrial Time (TT) but contains relativistic corrections that reference this
     * time measure at the Solar System Barycenter. Some astronomical calculations require
     * TDB for input. Since the difference to TT is tiny, it is generally OK to
     * substitute TT insteasd OF TDB, unless the extra precision is critical.
     * 
     * @return      (s) Barycentric Dynamic Time (TDB) seconds since midnight TDB, in the [0.0:86400.0) range.
     * 
     * @see #TT2000()
     * @see #LG
     * @see #EMJD
     */
    public final double TDB2000() {
        final double g = (357.53 + 0.9856003 * (MJD - MJDJ2000)) * Unit.deg;
        return TT2000() + 0.001658 * Math.sin(g) + 0.000014 * Math.sin(2.0*g);
    }
    
    /**
     * Returns GPS time as seconds since midnight GPS. GPS time is related to Atomic Time (TAI), 
     * and it is exactly 19 seconds ahead of TAI. 
     * 
     * @return      (s) GPS time seconds since midnight GPS, in the [0.0:86400.0) range.
     * 
     * @see #GPS2000()
     * @see #TAI()
     * @see #GPS2TAI
     */
    public final double GPSTime() { return getTimeOfDay(GPS2000()); }

    /**
     * Returns GPS time as seconds since midnight GPS. GPS time is related to Atomic Time (TAI), 
     * and it is exactly 19 seconds ahead of TAI. 
     * 
     * @return      (s) GPS time seconds since midnight GPS, in the [0.0:86400.0) range.
     * 
     * @see #setGPS2000(double)
     * @see #TAI2000()
     * @see #GPS2TAI
     */
    public final double GPS2000() { return TAI2000() - GPS2TAI; }
    
    /**
     * Sets a new time as GPS time seconds since J2000 (12h TT, 1 Jan 2000).
     * 
     * @param GPST  (s) the new time as GPS time seconds since J2000.
     * 
     * @see #GPS2000()
     * @see #setTAI2000(double)
     * @see #GPS2TAI
     */
    public void setGPS2000(double GPST) { setTAI2000(GPST + GPS2TAI); }


    /**
     * Returns the Earth Rotation Angle (ERA) at the time represented by this object.
     * See Eq. 5.15 IN IERS Technical Note 36, based on Capitaine et al. (2000), 
     * 
     * @param dUT1  (sec) UT1-UTC time difference (-0.5s &lt; dUT1 &lt; 0.5s).
     * 
     * @return      (rad) Earth rotation angle.
     * 
     * @see #UT1(double)
     * @see #GAST(double)
     * @see #GMST(double)
     */
    public final double ERA(double dUT1) {
        double Tu = 0.5 + UTC_MJD() + dUT1 / Unit.day;   // UT1 days since JD 2400000.0
        double ut1DayFrac = Math.IEEEremainder(Tu, 1.0);
        return Constant.twoPi * Math.IEEEremainder(ut1DayFrac + 0.7790572732640 + 0.00273781191135448 * Tu, 1.0);
    }
    
    /**
     * Returns the Greenwich Sidereal Time (GST), according to the IERS Technical Note 36 (2010).
     * 
     * @param dUT1  (sec) UT1-UTC time difference.
     * @return      (s) GST time
     * 
     * @see #UT1(double)
     * @see #ERA(double)
     */
    public final double GAST(double dUT1) {
        return (ERA(dUT1) - getEquationOfOrigins()) / Unit.timeAngle;
    }
    
    
    /**
     * Greenwich Mean Sidereal time (GMST), according to the IERS Technical Note 36 (2010).
     * 
     * @param dUT1  (sec) UT1-UTC time difference (-0.5s &lt; dUT1 &lt; 0.5s).
     * @return      (s) GMST time
     * 
     * @see #GAST(double)
     * @see #LMST(double, double)
     * @see #ERA(double)
     */
    public final double GMST(double dUT1) {
        final double t = (MJD() - MJDJ2000) / julianCenturyDays;
        double dmas = 14.506 + t * (-4612156.534 + t * (-1391.5817 + t * (0.00044 + t * (-0.029956 - t * 0.0000368))));
        return (ERA(dUT1) - dmas * Unit.mas) / Unit.timeAngle;
    }
    
    
    /**
     * Returns the Local (Apparent) Sidereal Time (LST, or LAST).
     * 
     * @param longitude    (radian) The geodetic longitude of the observer
     * @param dUT1         (s) UT1 - UTC time difference (-0.5s &lt; dUT1 &lt; 0.5s)
     * @return             (s) the LMST
     * 
     * @see #LMST(double, double)
     * @see #GAST(double)
     */
    public final double LST(double longitude, double dUT1) {
        double LST = Math.IEEEremainder(GAST(dUT1) + longitude / Unit.timeAngle, Unit.day);
        if(LST < 0.0) LST += Unit.day;
        return LST;
    }
  
    /**
     * Returns the Local Mean Sidereal Time.
     * 
     * @param longitude    (radian) The geodetic longitude of the observer
     * @param dUT1         (s) UT1 - UTC time difference (-0.5s &lt; dUT1 &lt; 0.5s)
     * @return             (s) the LMST
     * 
     * @see #LST(double, double)
     * @see #GMST(double)
     * 
     */
    public final double LMST(double longitude, double dUT1) {
        double LST = Math.IEEEremainder(GMST(dUT1) + longitude / Unit.timeAngle, Unit.day);
        if(LST < 0.0) LST += Unit.day;
        return LST;
    }


    /**
     * Calculates the 'equation of the origins', at the time represented by this object.
     * Based on Table 5.2e of IERS Technical Note 36 (2010).
     * 
     * @return      (rad)   The 'equations of the origings' angle.
     */
    final double getEquationOfOrigins() {
        final DelaunayArguments m = new DelaunayArguments(MJD());
        final double sinOmega = Math.sin(m.Omega());
        final double F2O = 2.0 * m.F() + m.Omega();
        final double D2 = 2.0 * m.D();
        final double F2OmD2 = F2O - D2;
        final double O2 = 2.0 * m.Omega();
        
        final double t = (MJD() - MJDJ2000) / julianCenturyDays;
        final double epsA = EquatorialCoordinates.eps0 + t * (-46.8150 + t * (-0.00059 + t * 0.001813));
        
        final Vector2D d = Nutation.getTruncated100uas(MJD());
        
        double EOuas = -14506 + t * (-4612156534.0 + t *(-1391581.7 + t * 0.44))
               - d.x() * Math.cos(epsA)
               + 2640.96 * sinOmega
               +  63.52 * Math.sin(O2)
               +  11.75 * Math.sin(F2OmD2 + O2)
               +  11.21 * Math.sin(F2OmD2)
               -   4.55 * Math.sin(F2OmD2 + m.Omega())
               +   2.02 * Math.sin(F2O    + O2)
               +   1.98 * Math.sin(F2O)
               -   1.72 * Math.sin(3.0 * m.Omega())
               -   1.41 * Math.sin(m.l1() + m.Omega())
               -   1.36 * Math.sin(m.l1() - m.Omega())
               -   0.63 * Math.sin(m.l() + m.Omega())
               -   0.63 * Math.sin(m.l() - m.Omega());
        
        return EOuas * Unit.uas;
    }
    
    /**
     * Returns the precise Besselian epoch corresponding to the time represented by this object.
     * Besselian epochs are based on calendar years, i.e. the time it takes Earth to orbit the Sun.
     * 
     * @return  the corresponding precise Besselian epoch.
     * 
     * @see #getJulianEpoch()
     */
    public BesselianEpoch getBesselianEpoch() {
        return BesselianEpoch.forMJD(MJD);
    }

    /**
     * Returns the precise Julian epoch corresponding to the time represented by this object.
     * Julian epochs are based on years that are exactly {@value #julianCenturyDays} days long.
     * 
     * @return  the corresponding precise Julian epoch.
     * 
     * @see #getBesselianEpoch()
     * @see #getJulianYear()
     */
    public JulianEpoch getJulianEpoch() { 
        return JulianEpoch.forMJD(MJD);
    }

    /**
     * Returns the precise Julian epoch year corresponding to the time represented by this object.
     * Julian epochs are based on years that are exactly {@value #julianCenturyDays} days long.
     * 
     * @return  (yr) the year of the precise Julian epoch. E.g. '2000.0', or '2021.6433'.
     * 
     * @see #getBesselianEpoch()
     * @see #getJulianYear()
     */
    public double getJulianYear() { 
        return 2000.0 + (MJD - MJDJ2000) / julianYearDays;
    }

    /**
     * Sets a new time for this object from parsing a standard ISO timestamp. ISO timestamps
     * have the format of {@value #ISOFormat}, e.g. '2021-09-04T00:15:33.234-0500'.
     * 
     * @param text          The ISO timestamp string.
     * @throws ParseException   if the time could not be parsed from the specified string.
     * 
     * @see #ISOFormat
     * @see #parseFITSTimeStamp(String)
     * @see #parseSimpleDate(String, DateFormat)
     */
    public void parseISOTimeStamp(String text) throws ParseException {  
        setUNIXMillis(getUTCDateFormat(ISOFormat).parse(text).getTime());
    }

    /**
     * Returns the standard ISO timestamp representation for this time object. ISO timestamps
     * have the format of {@value #ISOFormat}, e.g. '2021-09-04T00:15:33.234-0500'.
     * 
     * @return      The ISO timestamp string.
     * 
     * @see #ISOFormat
     * @see #getFITSTimeStamp()
     * @see #parseISOTimeStamp(String)
     */
    public String getISOTimeStamp() {
        return getUTCDateFormat(ISOFormat).format(getDate());
    }

    /**
     * Sets a new time for this object from parsing a standard FITS timestamp. FITS timestamps
     * are similar to ISO timestamps but are always expressed as UTC, and hence omit the timezone
     * specification. FITS timestamps have the canonical format of {@value #FITSFormat},
     * e.g. '2021-09-04T00:15:33.234'. However, FIT also allows flexibility, such as not using fewer
     * decimal seconds, or specifying only the date by not time (if so the time component may be
     * carried by a separate header keyword from that date).
     * 
     * @param text          The FITS timestamp string.
     * @throws ParseException   if the time could not be parsed from the specified string.
     * 
     * @see #FITSDateFormat
     * @see #FITSTimeFormat
     * @see #parseFITSDate(String)
     * @see #parseISOTimeStamp(String)
     * @see #parseSimpleDate(String, DateFormat)
     */
    public void parseFITSTimeStamp(String text) throws ParseException {
        // Set the MJD to 0 UTC of the date part...   
        setUNIXMillis(getUTCDateFormat(FITSDateFormat).parse(text.substring(0, FITSDateFormat.length())).getTime());

        // Add in the UT time component...
        if(text.length() > 11) {
            double UTC = 0.0;
            StringTokenizer tokens = new StringTokenizer(text.substring(11), ":");
            if(tokens.hasMoreTokens()) UTC += Integer.parseInt(tokens.nextToken()) * Unit.hour;
            if(tokens.hasMoreTokens()) UTC += Integer.parseInt(tokens.nextToken()) * Unit.min;
            if(tokens.hasMoreTokens()) UTC += Double.parseDouble(tokens.nextToken()) * Unit.s;
            MJD += UTC / Unit.day;	
        }
    }

    /**
     * Returns the standard FITS timestamp representation for this time object, such as '2021-09-04T00:15:33.234'.
     * 
     * @return      The FITS timestamp string.
     * 
     * @see #FITSFormat
     * @see #getISOTimeStamp()
     * @see #parseFITSTimeStamp(String)
     */
    public String getFITSTimeStamp() {
        long millis = getUNIXMillis();

        return getUTCDateFormat(FITSDateFormat).format(getDate()) + 'T' + FITSTimeFormat.format(1e-3 * (millis % dayMillis));
    }

    /**
     * Returns the abridged FITS date representation for this time object, such as '2021-09-04'.
     * 
     * @return      The FITS date string.
     * 
     * @see #FITSDateFormat
     * @see #getFITSTimeStamp()
     * @see #getISOTimeStamp()
     * @see #parseFITSTimeStamp(String)
     */
    public String getFITSShortDate() {
        return getUTCDateFormat(FITSDateFormat).format(getUNIXMillis());
    }

    /**
     * Sets a new time for this object from parsing only the date components of a FITS timestamp.
     * The time is set to 0 UTC of the date specified by the the string in the format of 
     * {@value #FITSDateFormat}, such as '2021-09-04'.
     * 
     * @param text          The FITS timestamp string containing a date.
     * @throws ParseException   if the date could not be parsed from the specified string.
     * 
     * @see #FITSDateFormat
     * @see #parseFITSTimeStamp(String)
     * @see #parseISOTimeStamp(String)
     * @see #parseSimpleDate(String, DateFormat)
     */
    public void parseFITSDate(String text) throws ParseException {
        parseSimpleDate(text, getUTCDateFormat(FITSDateFormat));
    }

    /**
     * Sets a new time for this object from parsing by parsing a string representation of that
     * time in the specified date format.
     * 
     * @param text          a string representation of time in the specified format.
     * @param format        the format specifier for how the date is expected to be represented in the string.
     * @throws ParseException   if the date could not be parsed from the specified string.
     * 
     * @see #parseFITSTimeStamp(String)
     * @see #parseISOTimeStamp(String)
     */
    public void parseSimpleDate(String text, DateFormat format) throws ParseException {
        setUNIXMillis(format.parse(text).getTime());
    }

    /**
     * Returns a Java {@link SimpleDateFormat} object for parsing UTC time string representations.
     * It is simply constructs a {@link SimpleDateFormat} object with the arguments, and sets
     * the timezone to UTC.
     * 
     * @param formatSpec        Time format specification to pass to {@link SimpleDateFormat} constructor.
     * @return                  The Java date format, suitable for parsing UTC time specifications.
     */
    public static final SimpleDateFormat getUTCDateFormat(String formatSpec) {
        SimpleDateFormat f = new SimpleDateFormat(formatSpec);
        f.setTimeZone(UTCZone);
        return f;
    }

    /**
     * Returns the time of day component for time elapsed since a reference time.
     * 
     * @param time      (s) Running seconds measured from a reference time that itself is assumed to coincide to the start of
     *                  a new day. E.g. UNIX time seconds (since 0 UT, 1 Jan 1970), or seconds since J2000 (12h TT, 1 Jan 2000).
     * @return          (s) The remainder of the input time with a 24-hour day, in the range of [0.0:86400.0).
     */
    public static double getTimeOfDay(double time) {
        return time - Unit.day * Math.floor(time / Unit.day);
    }
    
    /**
     * Returns the Modified Julian Date (MJD) calculated from the standard Java/UNIX time of milliseconds
     * since 1 Jan 1970.
     * 
     * @param millis        JAVA/UNIX time milliseconds (since 1 Jan 1970).  
     * @return              the corresponding Modified Julian Date with typical 0.1 &mu;s precision.
     */
    public static double getMJD(long millis) {
        return MJDJ2000 + (double)(millis - millisJ2000 + 1000L * (LeapSeconds.get(millis) - Leap2000)) / dayMillis;
    }

    /**
     * Returns the Modified Julian Date (MJD) calculated from the standard Java/UNIX time of milliseconds
     * since 1 Jan 1970.
     * 
     * @param millis        JAVA/UNIX time milliseconds (since 1 Jan 1970).  
     * @return              the corresponding Modified Julian Date with typical 0.1 &mu;s precision.
     */
    public static double getMJD(double millis) {
        return MJDJ2000 + (millis - millisJ2000 + 1000.0 * (LeapSeconds.get((long)millis) - Leap2000)) / dayMillis;
    }
    
    /**
     * Returns the Java/UNIX time as milliseconds since 0 UTC 1 Jan 1970. Java/UNIX times are essentially
     * measures of UTC time. As such they are not necesasarily monotonic, and do not advance in a steady rate, 
     * due to the introduction of leap second jumps by IERS. 
     * 
     * @param MJD   (day) Modified Julian Date
     * @return      (ms) the Java/UNIX time, as milliseconds since 0 UTC, 1 Jan 1970. 
     */
    public static final long getUNIXMillis(double MJD) { 
        final long TAI = millisJ2000 + leap2000Millis + (long)((MJD - MJDJ2000) * dayMillis);
        // Since leap seconds are relative to UTC, first get calculate UTC assuming
        // leap of UT. This UTC0 may be off by 1 second around a few seconds of a leap...
        final long UTC0 = TAI - 1000L * LeapSeconds.get(TAI);
        // By using UTC0 to recalculate the leap, UTC is always correct, except perhaps during the leap itself...
        return TAI - 1000L * LeapSeconds.get(UTC0);
    }

    /**
     * Returns a new astronomical time instance for the specified Modified Julian Day.
     * 
     * @param MJD       (day) the Modified Julian Date (days starting at 12h TT).
     * @return          a new astronomical time instance for the specified date.
     * 
     * @see #forISOTimeStamp(String)
     * @see #forFitsTimeStamp(String)
     */
    public static AstroTime forMJD(double MJD) {
        AstroTime t = new AstroTime();
        t.setMJD(MJD);
        return t;
    }
    
    /**
     * Returns a new astronomical time instance constructed from the specified ISO timestamp string.
     * 
     * @param text      the string representation of time in the ISO format of {@value #ISOFormat}.
     * @return          a new astronomical time instance for the specified date.
     * 
     * @see #parseISOTimeStamp(String)
     * @see #forMJD(double)
     * @see #forFitsTimeStamp(String)
     */
    public static AstroTime forISOTimeStamp(String text) throws ParseException {
        AstroTime time = new AstroTime();
        time.parseISOTimeStamp(text);
        return time;
    }

    /**
     * Returns a new astronomical time instance constructed from the specified FITS timestamp string.
     * 
     * @param text      the string representation of UTC time in the FITS format of {@value #FITSFormat}.
     * @return          a new astronomical time instance for the specified date.
     * 
     * @see #parseFITSTimeStamp(String)
     * @see #forMJD(double)
     * @see #forISOTimeStamp(String)
     */
    public static AstroTime forFitsTimeStamp(String text) throws ParseException {
        AstroTime time = new AstroTime();	
        time.parseFITSTimeStamp(text);
        return time;
    }

    /**
     * Returns a new AstroTime object for 0 UTC of the given calendar date. Only the first 10 characters
     * of the argument are parsed, containing the date in {@value #FITSDateFormat} format.
     * 
     * @param text      The string representation of the date in {@value #FITSDateFormat} format. Only the first 10 characters
     *                  constituting the date are parsed.
     * @return
     * @throws ParseException
     */
    public static AstroTime forStandardDate(String text) throws ParseException {
        AstroTime time = new AstroTime();   
        time.parseFITSTimeStamp(text.substring(0, 10));
        return time;
    }



    // J2000 = JD 2451545.0 = 12 TT, 1 January 2000 = 11:58:55.816 UTC or 11:59:27.816 TAI on 1 January 2000

    /** Leap seconds on 1 January 2000 */
    public static final int Leap2000 = 32;

    /** Leap seconds on 1 January 2000 as milliseconds */
    protected static final long leap2000Millis = 1000L * Leap2000;

    /** TT - TAI difference in milliseconds */
    protected static final long TAI2TTMillis = 32184L;

    /** GPS - TAI difference in milliseconds. */
    protected static final long TAI2GPSMillis = -19000L;

    /** Milliseconds in a day. */
    protected static final long dayMillis = 86400000L;

    /** UNIX time milliseconds at midnight UTC 1 January 2000 */
    public static final long millis0UTC1Jan2000 = 946684800000L;

    /**
     * <p>
     * Returns the Java/UNIX time for J2000 (12h TT, 1 Jan 2000). You can use it to construct Java/UNIX timestamps
     * for other time measures, e.g. for date formatting purposes of these. For example to get a Java timestamp
     * in TT, you can simply add this constant to the the result of {@link #TT2000()} multiplied by 1000. E.g.
     * </p>
     * 
     * <pre>
     *   AstroTime time = AstroTime.forMJD(63445.98645);
     * 
     *   // Java UNIX timestamp in Terrestrial Time (instead of UTC):
     *   long ttStamp = AstroTime.MillisJ2000 + 1000L * time.TT2000();
     * </pre>
     * 
     * @see #TT2000()
     * @see #TAI2000()
     * @see #GPS2000()
     * @see #TCG2000()
     * @see #TDB2000()
     */
    public static final long millisJ2000 = millis0UTC1Jan2000 + (dayMillis >>> 1) - leap2000Millis - TAI2TTMillis;

    /** MJD at J2000, i.e. 12h TT, 1 January 2000 */
    public static final double MJDJ2000 = 51544.5;	// 12h TT 1 January 2000

    public static final double JD_MJD0 = 24100000.5;   // JD date for MJD=0
    
    /** Milliseconds per Julian century, i.e. 36525.0 days */
    protected static final double JulianCenturyMillis = Unit.julianCentury / Unit.ms;

    public static final double julianYearDays = 365.25;
    
    /** Days in a Julian cenruty */
    public static final double julianCenturyDays = 100.0 * julianYearDays;

    /** The TAI to TT offset in seconds. */
    public static final double TAI2TT = TAI2TTMillis * Unit.ms;

    /** The GPS to TAI offset in seconds. */
    public static final double GPS2TAI = TAI2GPSMillis * Unit.ms;

    /** Gravitation time dilation constant, the difference between the advance rate of TT vs TCG */
    public static final double LG = 6.969290134e-10;

    /** MJD epoch at which TT and TCG are equal, i.e. TAI 1977-01-01T00:00:00.000 */
    public static final double EMJD = 43144.0003725;

    /** The UTC timezone. */
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /** The ISO date formatter. */
    public static final String ISOFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /** The ISO date formatter. */
    public static final String FITSFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    
    /** The FITS date (excluding time) formatter. */
    public static final String FITSDateFormat = "yyyy-MM-dd";

    /** The FITS time format. */
    public static final TimeFormat FITSTimeFormat = new TimeFormat(3); 

    /** The UTC timezone */
    public static final TimeZone UTCZone = TimeZone.getTimeZone("UTC");

    /** The sidereal clock rate relative to the UTC clock rate. */
    public static final double dSTdUT = 1.0 + Unit.day / Unit.year;

    static {
        FITSTimeFormat.colons();
    }

}

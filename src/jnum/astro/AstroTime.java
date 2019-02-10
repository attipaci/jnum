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


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import jnum.Unit;
import jnum.text.TimeFormat;
import jnum.util.HashCode;



/**
 *	
 */

/*
 *  UTC routines are approximate but consistent (btw. getUTC() and setUTC(), and currentTime())
 *  only UTC <===> (MJD, TT) conversion is approximate...
 *  Use (quadratic) fit to leap? This should give some accuracy for UTC...
 */
public class AstroTime implements Serializable, Comparable<AstroTime> {

    private static final long serialVersionUID = 890383504654665623L;

    private double MJD = Double.NaN; // Assuming that MJD goes with TT


    public AstroTime() {}

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(MJD);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof AstroTime)) return false;
        return MJD == ((AstroTime) o).MJD;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AstroTime time) {
        return Double.compare(MJD, time.MJD);
    }


    public AstroTime(long millis) { setUTCMillis(millis); }


    public AstroTime now() {
        setUTCMillis(System.currentTimeMillis()); 
        return this;
    }


    public Date getDate() { return new Date(getUTCMillis()); }


    public void setTime(Date date) { setUTCMillis(date.getTime()); }


    public final long getTAIMillis() {
        return getTAIMillis(MJD);
    }

    public final long getTTMillis() {
        return getTTMillis(MJD);
    }


    public final long getGPSMillis() {
        return getGPSMillis(MJD);
    }


    public final long getUTCMillis() { 
        return getUTCMillis(MJD);
    }


    public void setUTCMillis(long millis) {
        MJD = getMJD(millis);
    }

    public void setUTCMillis(double millis) {
        MJD = getMJD(millis);
    }

    public final void setUTC(double utc) {
        setUTCMillis(1000.0 * utc);
    }

    // UNIX clock measures UTC...
    public static double getMJD(long millis) {
        return MJDJ2000 + (double)(millis - MillisJ2000 + 1000L * (LeapSeconds.get(millis) - Leap2000)) / DayMillis;
    }

    public static double getMJD(double millis) {
        return MJDJ2000 + (millis - MillisJ2000 + 1000.0 * (LeapSeconds.get((long)millis) - Leap2000)) / DayMillis;
    }


    public double getMJD() { return MJD; }

    public double getTCGMJD() { return (getMJD() - EMJD) / (1.0 - LG) + EMJD; }

    public void setMJD(double date) { MJD = date; }

    public double getJD() { return 2400000.5 + MJD; }


    public void setJD(double JD) { setMJD(JD - 2400000.5); }

    // Terrestrial Time (based on Atomic Time TAI) in seconds
    public double getTT() {
        return (MJD - (int)Math.floor(MJD)) * Unit.day;
    }


    public void setTT(double TT) { MJD = Math.floor(MJD) + TT / Unit.day; }


    public double getTAI() { return getTT() - TAI2TT; }

    public void setTAI(double TAI) { setTT(TAI + TAI2TT); }

    // TCG is based on the Atomic Time but corrects for the gravitational dilation on Earth
    // Thus it is a good measure of time in space.
    // TT = TCG − LG × (JDTCG − 2443144.5003725) × 86400
    // LG = 6.969290134e-10
    public double getTCG() {
        return getTT() + LG * (MJD - EMJD) * Unit.day;
    }


    public void setTCG(double TCG) {
        setTT(TCG - LG * (MJD - EMJD) * Unit.day);
    }


    // Barycentric Dynamic time.
    // Relativistic corrections to reference to Solar system barycenter.
    public double getTDB() {
        double g = (357.53 + 0.9856003 * (MJD - MJDJ2000)) * Unit.deg;
        return getTT() + 0.001658 * Math.sin(g) + 0.000014 * Math.sin(2.0*g);
    }



    public double getGPSTime() { return getTAI() - GPS2TAI; }


    public void setGPSTime(double GPST) { setTAI(GPST + GPS2TAI); }


    public final double getUTC() {
        return 1e-3 * getUTCMillis() % DayMillis;
    }


    /**
     * Gets the Greenwich Mean Solar Time (GMST) at UT1 = 0h, i.e. the Mean Solar RA in time units (use for calculationg LST)
     *
     * @return the Greenwich Mean Solar Time (GMST). 
     */	
    public final double getGMST0() {
        // Ratio of mLST to UT1 = 0.997269566329084 − 5.8684×10−11T + 5.9×10−15T², 
        // where T is the number of Julian centuries of 36525 days each that have elapsed since JD 2451545.0 (J2000).[1]

        // From http://www.cv.nrao.edu/~rfisher/Ephemerides/times.html
        final double T = (MJD - MJDJ2000) / JulianCenturyDays;
        return (24110.54841 + T * (8640184.812866 + T * (0.093104 - T * 0.0000062))) * Unit.s;


    }

    // Greenwich Sidereal Time


    /**
     * Gets the Greenwich Mean Sidereal Time
     *
     * @param dUT1     (s) UT1 - UT time difference (-0.5s < dUT1 < 0.5s)  
     * @return         (s) the GMST
     */
    public final double getGMST(double dUT1) {
        return getGMST0() + dSTdUT * (getUTC() + dUT1);    // ~366.242189 / 365.242189
    }

    /**
     * Gets the Local Mean Sidereal Time
     * 
     * @param longitude    (radian) The geodetic longitude of the observer
     * @param dUT1         (s) UT1 - UT time difference (-0.5s < dUT1 < 0.5s)
     * @return             (s) the LMST
     */
    public final double getLMST(double longitude, double dUT1) {
        double LST = Math.IEEEremainder(getGMST(dUT1) + longitude / Unit.timeAngle, Unit.day);
        if(LST < 0.0) LST += Unit.day;
        return LST;
    }



    public BesselianEpoch getBesselianEpoch() {
        return BesselianEpoch.forMJD(MJD);
    }


    public JulianEpoch getJulianEpoch() { 
        return JulianEpoch.forMJD(MJD);
    }


    public void parseISOTimeStamp(String text) throws ParseException {  
        setUTCMillis(getDateFormat(ISOFormat).parse(text).getTime());
    }


    public String getISOTimeStamp() {
        return getDateFormat(ISOFormat).format(getDate());
    }


    public void parseFitsTimeStamp(String text) throws ParseException {
        // Set the MJD to 0 UTC of the date part...   
        setUTCMillis(getDateFormat(FITSDateFormat).parse(text.substring(0, FITSDateFormat.length())).getTime());

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


    public String getFitsTimeStamp() {
        long millis = getUTCMillis();

        return getDateFormat(FITSDateFormat).format(getDate()) + 'T' + FITSTimeFormat.format(1e-3 * (millis % DayMillis));
    }


    public String getFitsShortDate() {
        return getDateFormat(FITSDateFormat).format(getUTCMillis());
    }


    public void parseFitsDate(String text) throws ParseException {
        parseSimpleDate(text, getDateFormat(FITSDateFormat));
    }


    public void parseSimpleDate(String text) throws ParseException {
        parseSimpleDate(text, getDateFormat(DefaultFormat));
    }


    public void parseSimpleDate(String text, DateFormat format) throws ParseException {
        setUTCMillis(format.parse(text).getTime());
    }


    public String getSimpleDate() {
        return getDateFormat(DefaultFormat).format(getDate());
    }

    public final static DateFormat getDateFormat(String formatSpec) {
        DateFormat f = new SimpleDateFormat(formatSpec);
        f.setTimeZone(UTCZone);
        return f;
    }


    public final static long getTAIMillis(double MJD) {
        return MillisJ2000 + leap2000Millis + (long)((MJD - MJDJ2000) * DayMillis);
    }


    public final static long getTTMillis(double MJD) {
        return getTAIMillis(MJD) + MillisTAI2TT;
    }


    public final static long getGPSMillis(double MJD) {
        return getTAIMillis(MJD) + MillisTAI2GPS;
    }


    public final static long getUTCMillis(double MJD) { 
        final long TAI = getTAIMillis(MJD); 
        // Since leap seconds are relative to UTC, first get calculate UTC assuming
        // leap of UT. This UTC0 may be off by 1 second around a few seconds of a leap...
        final long UTC0 = TAI - 1000L * LeapSeconds.get(TAI);
        // By using UTC0 to recalculate the leap, UTC is always correct, except perhaps during the leap itself...
        return TAI - 1000L * LeapSeconds.get(UTC0);
    }



    public static AstroTime forISOTimeStamp(String text) throws ParseException {
        AstroTime time = new AstroTime();
        time.parseISOTimeStamp(text);
        return time;
    }


    public static AstroTime forFitsTimeStamp(String text) throws ParseException {
        AstroTime time = new AstroTime();	
        time.parseFitsTimeStamp(text);
        return time;
    }

    /**
     * Returns a new AstroTime object for 0 UTC of the given calendar date. Only the first 10 characters
     * of the argument are parsed, containing the date in <code>YYYY-mm-DD</code> format.
     * 
     * @param text      The string representation of the date in YYYY-mm-DD format. Only the first 10 characters
     *                  constituting the date are parsed.
     * @return
     * @throws ParseException
     */
    public static AstroTime forStandardDate(String text) throws ParseException {
        AstroTime time = new AstroTime();   
        time.parseFitsTimeStamp(text.substring(0, 10));
        return time;
    }

    public static AstroTime forSimpleDate(String text) throws ParseException {
        AstroTime time = new AstroTime();
        time.parseSimpleDate(text);
        return time;
    }


    public static double timeOfDay(double time) {
        return time - Unit.day * Math.floor(time / Unit.day);
    }

    // J2000 = JD 2451545.0 = 12 TT, 1 January 2000 = 11:58:55.816 UTC or 11:59:27.816 TAI on 1 January 2000

    /** Leap seconds on 1 January 2000 */
    public final static int Leap2000 = 32;

    /** Leap seconds on 1 January 2000 as milliseconds */
    protected final static long leap2000Millis = 1000L * Leap2000;

    /** TT - TAI difference in milliseconds */
    protected final static long MillisTAI2TT = 32184L;

    /** GPS - TAI difference in milliseconds. */
    protected final static long MillisTAI2GPS = -19000L;

    /** Milliseconds in a day. */
    protected final static long DayMillis = 86400000L;

    /** UNIX time milliseconds at midnight UTC 1 January 2000 */
    public final static long Millis0UTC1Jan2000 = 946684800000L;

    // 
    /** UNIX time (msec) for J2000 (12h TT, 1 Jan 2000). I.e. 12 UTC 1 Jan 2000 - leap2000 - TAI2TT */
    public final static long MillisJ2000 = Millis0UTC1Jan2000 + (DayMillis >>> 1) - leap2000Millis - MillisTAI2TT;

    /** MJD at J2000, i.e. 0 TT, 1 January 2000 */
    public final static double MJDJ2000 = 51544.5;	// 12 TT 1 January 2000

    /** Milliseconds per Julian century, i.e. 36525.0 days */
    protected final static double JulianCenturyMillis = Unit.julianCentury / Unit.ms;

    /** Days in a Julian cenruty */
    public final static double JulianCenturyDays = 36525.0;

    /** The TAI to TT offset in seconds. */
    public final static double TAI2TT = MillisTAI2TT * Unit.ms;

    /** The GPS to TAI offset in seconds. */
    public final static double GPS2TAI = MillisTAI2GPS * Unit.ms;

    /** Gravitation time dilation constant, the difference between the advance rate of TT vs TCG */
    public final static double LG = 6.969290134e-10;

    /** MJD epoch at which TT and TCG are equal, i.e. TAI 1977-01-01T00:00:00.000 */
    public final static double EMJD = 43144.0003725;

    /** The UTC timezone. */
    public final static TimeZone UTC = TimeZone.getTimeZone("UTC");

    /** The ISO date formatter. */
    public final static String ISOFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /** The ISO date formatter. */
    public final static String FITSFormat = new String("yyyy-MM-dd'T'HH:mm:ss.SSS");
    /** The FITS date (excluding time) formatter. */
    public final static String FITSDateFormat = new String("yyyy-MM-dd");

    /** The default date formatter. */
    public final static String DefaultFormat = new String("yyyy.MM.dd");

    /** The FITS time format. */
    public final static TimeFormat FITSTimeFormat = new TimeFormat(3); 

    /** The UTC timezone */
    public final static TimeZone UTCZone = TimeZone.getTimeZone("UTC");

    public final static double dSTdUT = 1.0 + Unit.day / Unit.year;

    static {
        FITSTimeFormat.colons();
    }

}

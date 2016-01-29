/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.astro;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import jnum.text.TimeFormat;
import jnum.util.Unit;



//	2451545.0 JD = 1 January 2000, 11:58:55.816 UT, or 11:59:27.816 TAI
//  UTC routines are approximate but consistent (btw. getUTC() and setUTC(), and currentTime())
//  only UTC <===> (MJD, TT) conversion is approximate...
//  Use (quadratic) fit to leap? This should give some accuracy for UTC...
/**
 * The Class AstroTime.
 */
public class AstroTime {
	
	/** The mjd. */
	private double MJD = Double.NaN; // Assuming that MJD goes with TT
	
	/**
	 * Instantiates a new astro time.
	 */
	public AstroTime() {}
	
	/**
	 * Instantiates a new astro time.
	 *
	 * @param millis the millis
	 */
	public AstroTime(long millis) { setUTCMillis(millis); }
	
	/**
	 * Now.
	 *
	 * @return the astro time
	 */
	public AstroTime now() {
		setUTCMillis(System.currentTimeMillis()); 
		return this;
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() { return new Date(getUTCMillis()); }

	
	/**
	 * Sets the time.
	 *
	 * @param date the new time
	 */
	public void setTime(Date date) { setUTCMillis(date.getTime()); }

	/**
	 * Gets the millis.
	 *
	 * @return the millis
	 */
	
	
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

	
	/**
	 * Sets the millis.
	 *
	 * @param millis the new millis
	 */
	public void setUTCMillis(long millis) {
		MJD = getMJD(millis);
	}

	// UNIX clock measures UTC...
	/**
	 * Gets the mjd.
	 *
	 * @param millis the millis
	 * @return the mjd
	 */
	public static double getMJD(long millis) {
		return mjdJ2000 + (double)(millis - millisJ2000 + 1000L * (LeapSeconds.get(millis) - leap2000)) / dayMillis;
	}

	/**
	 * Gets the mjd.
	 *
	 * @return the mjd
	 */
	public double getMJD() { return MJD; }

	/**
	 * Sets the mjd.
	 *
	 * @param date the new mjd
	 */
	public void setMJD(double date) { MJD = date; }
	
	/**
	 * Gets the jd.
	 *
	 * @return the jd
	 */
	public double getJD() { return 2400000.5 + MJD; }

	/**
	 * Sets the jd.
	 *
	 * @param JD the new jd
	 */
	public void setJD(double JD) { setMJD(JD - 2400000.5); }
	
	// Terrestrial Time (based on Atomic Time TAI) in seconds
	/**
	 * Gets the tt.
	 *
	 * @return the tt
	 */
	public double getTT() {
		return (MJD - (int)Math.floor(MJD)) * Unit.day;
	}

	/**
	 * Sets the tt.
	 *
	 * @param TT the new tt
	 */
	public void setTT(double TT) { MJD = Math.floor(MJD) + TT / Unit.day; }

	/**
	 * Gets the tai.
	 *
	 * @return the tai
	 */
	public double getTAI() { return getTT() - TAI2TT; }

	/**
	 * Sets the tai.
	 *
	 * @param TAI the new tai
	 */
	public void setTAI(double TAI) { setTT(TAI + TAI2TT); }

	// TCG is based on the Atomic Time but corrects for the gravitational dilation on Earth
	// Thus it is a good measure of time in space.
	// TT = TCG − LG × (JDTCG − 2443144.5003725) × 86400
	// LG = 6.969290134e-10
	/**
	 * Gets the tcg.
	 *
	 * @return the tcg
	 */
	public double getTCG() {
		return getTT() + 6.969290134e-10 * (MJD - 43144.5003725) * Unit.day;
	}

	/**
	 * Sets the tcg.
	 *
	 * @param TCG the new tcg
	 */
	public void setTCG(double TCG) {
		setTT(TCG - 6.969290134e-10 * (MJD - 43144.5003725) * Unit.day);
	}

	
	// Barycentric Dynamic time.
	// Relativistic corrections to reference to Solar system barycenter.
	/**
	 * Gets the Barycentric Dynamic Time (TDB), which is referenced to the Solar system barycenter.
	 *
	 * @return TDB in seconds.
	 */
	
	public double getTDB() {
		double g = (357.53 + 0.9856003 * (MJD - 51544.5)) * Unit.deg;
		return getTT() + 0.001658 * Math.sin(g) + 0.000014 * Math.sin(2.0*g);
	}
	
	
	/**
	 * Gets the GPS time.
	 *
	 * @return the GPS time
	 */
	public double getGPSTime() { return getTAI() - GPS2TAI; }

	/**
	 * Sets the GPS time.
	 *
	 * @param GPST the GPS time
	 */
	public void setGPSTime(double GPST) { setTAI(GPST + GPS2TAI); }
	
	/**
	 * Gets the utc.
	 *
	 * @return the utc
	 */
	public final double getUTC() {
		return 1e-3 * getUTCMillis() % dayMillis;
	}
	
	// Mean Fictive Equatorial Sun's RA in time units (use for calculationg LST)
    /**
	 * Gets the mean fictive equatorial sun time.
	 *
	 * @return the mean fictive equatorial sun time
	 */	
	// GMST at UT1 = 0
	public final double getGMST0() {
		// Ratio of mLST to UT1 = 0.997269566329084 − 5.8684×10−11T + 5.9×10−15T², 
		// where T is the number of Julian centuries of 36525 days each that have elapsed since JD 2451545.0 (J2000).[1]
    	//final double Tu = (MJD - mjdJ2000 + 0.5) / julianCenturyDays;
    	//return (24110.54841 + 8640184.812866 * Tu + 0.093104 * Tu * Tu) * Unit.s;
    	
		// From http://www.cv.nrao.edu/~rfisher/Ephemerides/times.html
		final double T = (MJD - 51544.5) / julianCenturyDays;
		return (24110.54841 + T * (8640184.812866 + T * (0.093104 - T * 0.0000062))) * Unit.s;
    }
	
	// Greenwich Sidereal Time
	/**
	 * Gets the gst.
	 *
	 * @return the gst
	 */
	public final double getGMST() {
		return getGMST(0.0);
	}
	
	public final double getGMST(double dUT1) {
		return getGMST0() + getUTC() + dUT1;
	}
	
	/**
	 * Gets the lst.
	 *
	 * @param longitude the longitude
	 * @return the lst
	 */
	public final double getLMST(double longitude) {
		return getLMST(longitude, 0.0);
	}
	
	public final double getLMST(double longitude, double dUT1) {
		double LST = Math.IEEEremainder(getGMST(dUT1) + longitude / Unit.timeAngle, Unit.day);
		if(LST < 0.0) LST += Unit.day;
		return LST;
	}
		
	
	/**
	 * Gets the besselian epoch.
	 *
	 * @return the besselian epoch
	 */
	public BesselianEpoch getBesselianEpoch() { 
		BesselianEpoch epoch = new BesselianEpoch();
		epoch.setMJD(MJD);
		return epoch;
	}

	/**
	 * Gets the julian epoch.
	 *
	 * @return the julian epoch
	 */
	public JulianEpoch getJulianEpoch() { 
		JulianEpoch epoch = new JulianEpoch();
		epoch.setMJD(MJD);
		return epoch;
	}
	
	
	/**
	 * Parses the iso time stamp.
	 *
	 * @param text the text
	 * @throws ParseException the parse exception
	 */
	public void parseISOTimeStamp(String text) throws ParseException {
		setUTCMillis(isoFormatter.parse(text).getTime());
	}
	
	/**
	 * Gets the iSO time stamp.
	 *
	 * @return the iSO time stamp
	 */
	public String getISOTimeStamp() {
		return isoFormatter.format(getDate());
	}
	
	/**
	 * Parses the fits time stamp.
	 *
	 * @param text the text
	 * @throws ParseException the parse exception
	 */
	public void parseFitsTimeStamp(String text) throws ParseException {
		// Set the MJD to 0 UTC of the date part...
		setUTCMillis(fitsDateFormatter.parse(text.substring(0,10)).getTime());
	
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
	 * Gets the fits time stamp.
	 *
	 * @return the fits time stamp
	 */
	public String getFitsTimeStamp() {
		long millis = getUTCMillis();
		return fitsDateFormatter.format(millis)	+ 'T' + fitsTimeFormat.format(1e-3 * (millis % dayMillis));
	}
	
	public String getFitsShortDate() {
		long millis = getUTCMillis();
		return fitsDateFormatter.format(millis);
	}
	
	/**
	 * Parses the simple date.
	 *
	 * @param text the text
	 * @throws ParseException the parse exception
	 */
	public void parseFitsDate(String text) throws ParseException {
		parseSimpleDate(text, fitsDateFormatter);
	}
	
	/**
	 * Parses the simple date.
	 *
	 * @param text the text
	 * @throws ParseException the parse exception
	 */
	public void parseSimpleDate(String text) throws ParseException {
		parseSimpleDate(text, defaultFormatter);
	}
	
	/**
	 * Parses the simple date.
	 *
	 * @param text the text
	 * @param format the format
	 * @throws ParseException the parse exception
	 */
	public void parseSimpleDate(String text, DateFormat format) throws ParseException {
		setUTCMillis(format.parse(text).getTime());
	}
	
	/**
	 * Gets the simple date.
	 *
	 * @return the simple date
	 */
	public String getSimpleDate() {
		return defaultFormatter.format(getDate());
	}
	
	
	
	public final static long getTAIMillis(double MJD) {
		return millisJ2000 + leap2000Millis + (long)((MJD - mjdJ2000) * dayMillis);
	}
	
	public final static long getTTMillis(double MJD) {
		return getTAIMillis(MJD) + millisTAI2TT;
	}
	
	public final static long getGPSMillis(double MJD) {
		return getTAIMillis(MJD) + millisTAI2GPS;
	}
	
	
	public final static long getUTCMillis(double MJD) { 
		final long TAI = getTAIMillis(MJD); 
		// Since leap seconds are relative to UTC, first get calculate UTC assuming
		// leap of UT. This UTC0 may be off by 1 second around a few seconds of a leap...
		final long UTC0 = TAI - 1000L * LeapSeconds.get(TAI);
		// By using UTC0 to recalculate the leap, UTC is always correct, except perhaps during the leap itself...
		return TAI - 1000L * LeapSeconds.get(UTC0);
	}
	
	
	
	/**
	 * For iso time stamp.
	 *
	 * @param text the text
	 * @return the astro time
	 * @throws ParseException the parse exception
	 */
	public static AstroTime forISOTimeStamp(String text) throws ParseException {
		AstroTime time = new AstroTime();
		time.parseISOTimeStamp(text);
		return time;
	}
	
	/**
	 * For fits time stamp.
	 *
	 * @param text the text
	 * @return the astro time
	 * @throws ParseException the parse exception
	 */
	public static AstroTime forFitsTimeStamp(String text) throws ParseException {
		AstroTime time = new AstroTime();	
		time.parseFitsTimeStamp(text.substring(0, 10));
		return time;
	}
	
	/**
	 * For simple date.
	 *
	 * @param text the text
	 * @return the astro time
	 * @throws ParseException the parse exception
	 */
	public static AstroTime forSimpleDate(String text) throws ParseException {
		AstroTime time = new AstroTime();
		time.parseSimpleDate(text);
		return time;
	}
	
	/**
	 * Time of day.
	 *
	 * @param time the time
	 * @return the double
	 */
	public static double timeOfDay(double time) {
		return time - Unit.day * Math.floor(time / Unit.day);
	}
	
	
	/** The Constant leap2000. */
	public final static int leap2000 = 32;
	
	protected final static long leap2000Millis = 1000L * leap2000;
	
	protected final static long millisTAI2TT = 32184L;
	
	protected final static long millisTAI2GPS = -19000L;
	
	/** The Constant dayMillis. */
	protected final static long dayMillis = 86400000L;

		
	public final static long millis0UTC1Jan2000 = 946684800000L;
	// millis of 2000 UT - leap2000 - tai2tt -> 2000 TT
	/** The Constant millisJ2000. */
	public final static long millisJ2000 = millis0UTC1Jan2000 - leap2000Millis - millisTAI2TT; // millis at TT 2000
	
	/** The Constant mjdJ2000. */
	public final static double mjdJ2000 = 51544.0;	// 0 TT 1 January 2000
	
	public final static double mjd0UTC1Jan2000 = mjdJ2000 + (leap2000Millis + millisTAI2TT) / dayMillis;
	
	/** The Constant mjdJ1970. */
	public final static double mjdJ1970 = 40587.0;
		
	/** The Constant julianCenturyMillis. */
	protected final static double julianCenturyMillis = Unit.julianCentury / Unit.ms;
	
	/** The Constant julianCenturyDays. */
	protected final static double julianCenturyDays = 36525.0;
		
	/** The TAI to TT offset in seconds. */
	public final static double TAI2TT = millisTAI2TT * Unit.ms;
	
	/** The GPS to TAI offset in seconds. */
	public final static double GPS2TAI = millisTAI2GPS * Unit.ms;
	
	/** The UTC timezone. */
	public final static TimeZone UTC = TimeZone.getTimeZone("UTC");
	
	/** The iso formatter. */
	public final static DateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	/** The fits date formatter. */
	public final static DateFormat fitsDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	
	/** The default formatter. */
	public final static DateFormat defaultFormatter = new SimpleDateFormat("yyyy.MM.dd");
	
	/** The fits time format. */
	public final static TimeFormat fitsTimeFormat = new TimeFormat(3); 
	
	static {
		isoFormatter.setTimeZone(UTC);
		fitsDateFormatter.setTimeZone(UTC);
		defaultFormatter.setTimeZone(UTC);
		fitsTimeFormat.colons();
	}
	
}

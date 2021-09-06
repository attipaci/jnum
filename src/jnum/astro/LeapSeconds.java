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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import jnum.Util;
import jnum.io.LineParser;
import jnum.text.SmartTokenizer;


/**
 * Manages and obtains historical leap-second data for time calculations. Before first use, one should load
 * the historical leap-second data ('leap-seconds.list') using {@link #read(String)} static method. 
 * The current leap second data is available from:
 * 
 *    ftp://time.nist.gov/pub/
 * or
 *    https://www.ietf.org/timezones/data/leap-seconds.list
 *
 * NOTE: Under no circumstances should one query a NIST server more frequently than once every 4 seconds!!!
 *
 * Once the historical data has been loaded, you can retrieve the leap seconds value for any given 
 * {@link java.util.Date} using the {@link #get(long)} method, or the current value using the 
 * {@link #getCurrentLeap()} method. The method {@link #isCurrent()} can be used for checking if the current 
 * leap second value is up-to-date or not.
 * 
 * @see AstroTime
 * @see CurrentTime
 * 
 */
public final class LeapSeconds {

    /** The list of historical leap seconds adjustments */
	private static ArrayList<Datum> list;

	/** UNIX milliseconds for 0 UTC 1 Jan 1900 */
	private static final long millis1900 = -2208988800000L; 

	/** The path to the leap-seconds.list file from which data was last parsed */
	public static String dataFile = null;

	/** The current leap seconds */
	private static int currentLeap = 37;

	/** Seconds since 1900 when the leap seconds was changed last */
	private static long currentEpoch = 3676924800L;        // 8 July 2016 -- seconds since 1900

	/** Seconds since 1900 until which the current leap seconds value is quaranteed to be valid */
	private static long expirationEpoch = 3849638400L;     // 28 December 2021 -- seconds since 1900
	
	/** A boolean switch to prevent repeated messages about not having historical leap second data */
	private static boolean warnedNoFile = false;
	
	/** A boolean switch to prevent repeated messages about expired leap second data */
	private static boolean warnedExpired = false;
	
	/**
	 * Gets the current leap seconds.
	 *
	 * @return the current leap seconds.
	 */
	public static int getCurrentLeap() { return currentLeap; }
	
	/**
	 * Returns the Java/UNIX time until which leap second information is guaranteed to be accurate.
	 * 
	 * @return     (ms) Java/UNIX time at which the current leap second data expires. Leap seconds may be
	 *             invalid beyond that date.
	 *             
	 * @see #isCurrent()
	 */
	public static final long getExpirationMillis() {
	    return millis1900 + 1000L * expirationEpoch;
	}
	
	/**
	 * Return the Java/UNIX time at which the last leap second change, known by this class, was introduced.
	 * 
	 * @return     (ms) Java/UNIX time of the last leap second change known to this class.
	 * 
	 * @see #isCurrent()
	 * @see #read(String)
	 */
	public static final long getCurrentEpochMillis() {
	    return millis1900 + 1000L * currentEpoch;
	}
	
	/**
	 * Return the Java/UNIX time at which the first (ever) leap second was introduced. 
	 * 
	 * @return     (ms) Java/UNIX time of the introduction of the very first leap second.
	 */
	private static final long getFirstLeapMillis() {
	    return millis1900 + 1000L * 2272060800L;   // 1 January 1972
	}
	
	
	/**
	 * Gets the leap seconds for a given {@link java.util.Date}
	 *
	 * @param timestamp    the standatd UNIX/Java timestamp (millisecs since 1970)
	 * @return             the historical leap seconds at the time.
	 * 
	 * @see #isCurrent()
	 * @see #read(String)
	 */
	public static int get(long timestamp) {
	
		if(timestamp >= getCurrentEpochMillis()) return currentLeap;
		if(timestamp < getFirstLeapMillis()) return 0;
		
		if(list == null) {
			if(dataFile == null) {
				if(!warnedNoFile) {
				    Util.info(LeapSeconds.class, "No historical leap-seconds data. Will use: " + currentLeap + " s.");
				    warnedNoFile = true;
				}
				return currentLeap;
			}
			
			try { read(dataFile); }
			catch(IOException e) {
			    Util.warning(LeapSeconds.class, "Could not read leap seconds data: " + dataFile + "\n"
					        + "Problem: " + e.getMessage() + "\n"
					        + "Will use current default value: " + currentLeap + " s.");
			    dataFile = null;
				return currentLeap;
			}
			
			if(timestamp >= getExpirationMillis()) {
				Util.warning(LeapSeconds.class, "Leap seconds data is no longer current. To fix it, update '" + dataFile + "'.");
				dataFile = null;
			}
		}
		
		
		if(timestamp > getExpirationMillis()) if(!warnedExpired) {
			Util.warning(LeapSeconds.class, "Leap data expired: " + dataFile + ". Will use the current default value: " + currentLeap + " s");
			warnedExpired = true;
		}
		
		int lower = 0, upper = list.size()-1;
		
		if(timestamp < list.get(lower).timestamp)
			return 0;
		
		if(timestamp > list.get(upper).timestamp) 
			return list.get(upper).leap;
		
		while(upper - lower > 1) {
			int i = (upper + lower) >> 1;
			long t = list.get(i).timestamp;
			if(timestamp >= t) lower = i;
			if(timestamp <= t) upper = i;
		}
		
		return list.get(lower).leap;
	}
	
	/**
	 * Checks if the current leap second value is valid.
	 *
	 * @return     <code>true</code>, if the current value is valid, <code>false</code> if it's out-dated.
	 * 
	 * @see #getCurrentEpochMillis()
	 * @see #getExpirationMillis()
	 * @see #read(String)
	 */
	public static boolean isCurrent() {
		return System.currentTimeMillis() < getExpirationMillis();
	}
	
	/**
	 * <p>
	 * Read the historical 'leap-seconds.list' data from the specified file.
	 * 
	 * The current leap second data is available from:
	 * 
	 *    <a href="ftp://time.nist.gov/pub/">ftp://time.nist.gov/pub/</a>
	 * or
	 *    <a href="https://www.ietf.org/timezones/data/leap-seconds.list">https://www.ietf.org/timezones/data/leap-seconds.list</a>
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE: Under no circumstances should one query a NIST server more frequently than once every 4 seconds!!!</b>
	 * </p>
	 *
	 *
	 * @param fileName the The path to the leap-seconds.list data file.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 * @see #isCurrent()
	 */
	public static void read(String fileName) throws IOException {
			
		if(list == null) list = new ArrayList<>();
		else list.clear();
		
		Util.debug(LeapSeconds.class, "Reading leap seconds table from " + fileName);
		
		new LineParser() {
		    @Override
            protected boolean parseComment(String line) throws Exception {
		        SmartTokenizer tokens = new SmartTokenizer(line);
		        tokens.nextToken();
                if(line.charAt(0) == '$') currentEpoch = tokens.nextLong();
                else if(line.charAt(0) == '@') expirationEpoch = tokens.nextLong();
		        return true;
		    }
		    
            @Override
            protected boolean parse(String line) throws Exception {
                if(line.length() < 3) return false;  
                SmartTokenizer tokens = new SmartTokenizer(line);
                Datum entry = new Datum();
                entry.timestamp = 1000L * tokens.nextLong() + millis1900;
                entry.leap = tokens.nextInt();
                list.add(entry); 
                return true;
            }
		    
		}.read(fileName);
	
		Collections.sort(list);
		
		Datum current = list.get(list.size() - 1);
		currentLeap = current.leap;
		
        warnedNoFile = false;
        warnedExpired = false;
		
        DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        tf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Util.debug(LeapSeconds.class, "--> Found " + list.size() + " leap-second entries.\n"
			        + "--> Released: " + tf.format(getCurrentEpochMillis()) + "\n"
			        + "--> Expires: " + tf.format(getExpirationMillis()));
	}	
	
	/**
	 * A class representing a single leap second introduction event.
	 * 
	 * @author Attila Kovacs
	 *
	 */
	private static class Datum implements Comparable<Datum> {
	    /** The UNIX timestamp at which the leap second change occurred. */
	    long timestamp;
	    
	    /** The new leap second value after the change */
	    int leap;
	    
	    @Override
	    public int compareTo(Datum other) {
	        if(timestamp == other.timestamp) return 0;
	        return timestamp < other.timestamp ? -1 : 1;
	    }
	}
}



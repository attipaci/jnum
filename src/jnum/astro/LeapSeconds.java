/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 * Manage and obtain historical leap-second data for time calculations. Before first use, one should load
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
 */
public final class LeapSeconds {
	
	/** The list. */
	private static ArrayList<Datum> list;
	
	/** The Constant millis1900. */
	public final static long millis1900 = -2208988800000L; // "1900-01-01T00:00:00.000" UTC
	
	/** The data file. */
	public static String dataFile = null;
	
	/** The verbose. */
	public static boolean verbose = false;
	
	/** The current leap. */
	private static int currentLeap = 37;
	
	/** The release epoch. */
	private static long releaseEpoch = 3676924800L;        // 8 July 2016 -- seconds since 1900
	
	/** The expiration epoch. */
	private static long expirationEpoch = 3739132800L;     // 28 Jul 2017 -- seconds since 1900
	
	/** The expiration millis. */
	private static long expirationMillis = millis1900 + 1000L * expirationEpoch;
	
	/** The current since millis. */
	private static long currentSinceMillis = millis1900 + 1000L * 3550089600L; 
	
	/** The Constant firstLeapMillis. */
	private final static long firstLeapMillis = millis1900 + 1000L * 2272060800L;	// 1 January 1972
	
	/** The is verbose. */
	private static boolean isVerbose = true;
	
	/**
	 * Gets the current leap seconds.
	 *
	 * @return the current leap seconds.
	 */
	public static int getCurrentLeap() { return currentLeap; }
	
	/**
	 * Sets the verbosity for warning messages if the leap-second data is incomplete or out-of-date.
	 *
	 * @param value the new verbosity for warnings.
	 */
	public static void setVerbose(boolean value) { isVerbose = value; }
	
	/**
	 * Gets the leap seconds for a given {@link java.util.Date}
	 *
	 * @param timestamp the timestamp
	 * @return the int
	 */
	public static int get(long timestamp) {
	
		if(timestamp >= currentSinceMillis) return currentLeap;
		if(timestamp < firstLeapMillis) return 0;
		
		if(list == null) {
			if(dataFile == null) {
				if(isVerbose) Util.warning(LeapSeconds.class, "No historical leap-seconds data. Will use: " + currentLeap + " s.");
				return currentLeap;
			}
			
			try { read(dataFile); }
			catch(IOException e) {
				if(isVerbose) {
					Util.warning(LeapSeconds.class, "Could not real leap seconds data: " + dataFile + "\n"
					        + "Problem: " + e.getMessage() + "\n"
					        + "Will use current default value: " + currentLeap + " s.");
				}
				return currentLeap;
			}
			
			if(timestamp >= expirationMillis) if(isVerbose) {
				Util.warning(LeapSeconds.class, "Leap seconds data is no longer current. To fix it, update '" + dataFile + "'.");
			}
		}
		
		
		if(timestamp > expirationMillis) if(isVerbose) {
			Util.warning(LeapSeconds.class, "Leap data expired: " + dataFile 
			        + ". Will use the current default value: " + currentLeap + " s");
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
	 * @return true, if the current value is valid, false if it's out-dated.
	 */
	public static boolean isCurrent() {
		return System.currentTimeMillis() < expirationMillis;
	}
	
	/**
	 * Read the historical 'leap-seconds.list' data from the specified file.
	 * 
	 * The current leap second data is available from:
	 * 
	 *    ftp://time.nist.gov/pub/
	 * or
	 *    https://www.ietf.org/timezones/data/leap-seconds.list
	 *
	 * NOTE: Under no circumstances should one query a NIST server more frequently than once every 4 seconds!!!
	 *
	 *
	 * @param fileName the The path to the leap-seconds.list data file.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void read(String fileName) throws IOException {
			
		if(list == null) list = new ArrayList<Datum>();
		else list.clear();
		
		if(verbose) Util.info(LeapSeconds.class, "Reading leap seconds table from " + fileName);
		
		new LineParser() {
		    @Override
            protected boolean parseComment(String line) throws Exception {
		        SmartTokenizer tokens = new SmartTokenizer(line);
		        tokens.nextToken();
                if(line.charAt(0) == '$') releaseEpoch = tokens.nextLong();
                else if(line.charAt(0) == '@') {
                    expirationEpoch = tokens.nextLong();
                    expirationMillis = 1000L * expirationEpoch + millis1900;
                }
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
		currentSinceMillis = current.timestamp;
		
		if(verbose) {
			DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			tf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Util.detail(LeapSeconds.class, "--> Found " + list.size() + " leap-second entries.\n"
			        + "--> Released: " + tf.format(1000L * releaseEpoch + millis1900) + "\n"
			        + "--> Expires: " + tf.format(expirationMillis));
		}
		
	}	
	
	private static class Datum implements Comparable<Datum> {
	    long timestamp;
	    int leap;
	    
	    @Override
	    public int compareTo(Datum other) {
	        if(timestamp == other.timestamp) return 0;
	        return timestamp < other.timestamp ? -1 : 1;
	    }
	}
}



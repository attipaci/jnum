/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc
// Last updated on 27 Mar 2015
//  -- Historical Leap seconds lookup added and fixed.
// get latest leap-seconds.list from ftp://time.nist.gov/pub/

// NOTE: Under no circumstances should one query a NIST server more frequently than once every 4 seconds!!!


/**
 * The Class LeapSeconds.
 */
public final class LeapSeconds {
	
	/** The list. */
	private static ArrayList<LeapEntry> list;
	
	/** The Constant millis1900. */
	public final static long millis1900 = -2208988800000L; // "1900-01-01T00:00:00.000" UTC
	
	/** The data file. */
	public static String dataFile = null;
	
	/** The verbose. */
	public static boolean verbose = false;
	
	/** The current leap. */
	private static int currentLeap = 36;
	
	/** The release epoch. */
	private static long releaseEpoch = 3535228800L;			// seconds since 1900
	
	/** The expiration epoch. */
	private static long expirationEpoch = 3597177600L;		// seconds since 1900
	
	/** The expiration millis. */
	private static long expirationMillis = millis1900 + 1000L * expirationEpoch;
	
	/** The current since millis. */
	private static long currentSinceMillis = millis1900 + 1000L * 3550089600L; 
	
	/** The Constant firstLeapMillis. */
	private final static long firstLeapMillis = millis1900 + 1000L * 2272060800L;	// 1 January 1972
	
	private static boolean isVerbose = true;
	
	/**
	 * Gets the current leap.
	 *
	 * @return the current leap
	 */
	public static int getCurrentLeap() { return currentLeap; }
	
	public static void setVerbose(boolean value) { isVerbose = value; }
	
	/**
	 * Gets the.
	 *
	 * @param timestamp the timestamp
	 * @return the int
	 */
	public static int get(long timestamp) {
	
		if(timestamp >= currentSinceMillis) return currentLeap;
		if(timestamp < firstLeapMillis) return 0;
		
		if(list == null) {
			if(dataFile == null) {
				if(isVerbose) System.err.println("WARNING! No historical leap-seconds data. Will use: " + currentLeap + " s.");
				return currentLeap;
			}
			
			try { read(dataFile); }
			catch(IOException e) {
				if(isVerbose) {
					System.err.println("WARNING! Could not real leap seconds data: " + dataFile);
					System.err.println("         Problem: " + e.getMessage());
					System.err.println("         Will use current default value: " + currentLeap + " s.");
				}
				return currentLeap;
			}
			
			if(timestamp >= expirationMillis) if(isVerbose) {
				System.err.println("WARNING! Leap seconds data is no longer current.");
				System.err.println("         To fix it, update '" + dataFile + "'.");
			}

		}
		
		if(timestamp > expirationMillis) if(isVerbose) {
			System.err.println("WARNING! Leap data expired: " + dataFile);
			System.err.println("         Will use the current default value: " + currentLeap + " s");
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
	 * Checks if is current.
	 *
	 * @return true, if is current
	 */
	public static boolean isCurrent() {
		return System.currentTimeMillis() < expirationMillis;
	}
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void read(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line = null;
		
		if(list == null) list = new ArrayList<LeapEntry>();
		else list.clear();
		
		if(verbose) System.err.println("Reading leap seconds table from " + fileName);
		
		while((line=in.readLine()) != null) if(line.length() > 2) {
			StringTokenizer tokens = new StringTokenizer(line);
			
			if(line.charAt(0) == '#') {
				tokens.nextToken();
				if(line.charAt(1) == '$') releaseEpoch = Long.parseLong(tokens.nextToken());
				else if(line.charAt(1) == '@') {
					expirationEpoch = Long.parseLong(tokens.nextToken());
					expirationMillis = 1000L * expirationEpoch + millis1900;
				}
			}
			else {
				LeapEntry entry = new LeapEntry();
				entry.timestamp = 1000L * Long.parseLong(tokens.nextToken()) + millis1900;
				entry.leap = Integer.parseInt(tokens.nextToken());
				list.add(entry);
			}
		}
		
		Collections.sort(list);
		
		LeapEntry current = list.get(list.size() - 1);
		currentLeap = current.leap;
		currentSinceMillis = current.timestamp;
		
		if(verbose) {
			System.err.println("--> Found " + list.size() + " leap-second entries.");
		
			DateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			tf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.err.println("--> Released: " + tf.format(1000L * releaseEpoch + millis1900));
			System.err.println("--> Expires: " + tf.format(expirationMillis));
		}
		
		in.close();
	}	
}

class LeapEntry implements Comparable<LeapEntry> {
	long timestamp;
	int leap;
	
	@Override
	public int compareTo(LeapEntry other) {
		if(timestamp == other.timestamp) return 0;
		return timestamp < other.timestamp ? -1 : 1;
	}
}

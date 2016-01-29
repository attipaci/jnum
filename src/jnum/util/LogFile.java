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
package jnum.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import jnum.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class LogFile.
 */
public class LogFile {
	
	/** The path. */
	String path;
	
	/** The format. */
	String format;
	
	/** The version. */
	int version = 0;
	
	/** The conflict policy. */
	int conflictPolicy = CONFLICT_DEFAULT;
	
	/**
	 * Instantiates a new log file.
	 *
	 * @param path the path
	 * @param format the format
	 * @param conflictPolicy the conflict policy
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public LogFile(String path, String format, int conflictPolicy) throws IOException {
		this.path = path;
		this.format = format;
		this.conflictPolicy = conflictPolicy;
		check();
	}
	
	/**
	 * Read header.
	 *
	 * @param in the in
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static String readHeader(BufferedReader in) throws IOException {
		String header = in.readLine();
		if(header == null) throw new IllegalStateException("Empty log file header.");
		if(header.charAt(0) != '#') throw new IllegalStateException("Illegal log file header.");
		if(header.length() < 2) throw new IllegalStateException("Empty file header.");
		return header.substring(2);
	}
		
	/**
	 * Check.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void check() throws IOException {
		File file = getFile();

		// If there is no prior log by that name, then simply create it and return....
		if(!file.exists()) {
			PrintWriter out = new PrintWriter(new FileOutputStream(file));
			out.println("# " + format);
			out.close();
			return;
		}
					
		// Otherwise check if the headers match...
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(getFile())));
		try { if(readHeader(in).equals(format)) return; }
		catch(IllegalStateException e) { System.err.println("WARNING! " + e.getMessage()); }
		
		// Conflict...
		if(conflictPolicy == CONFLICT_OVERWRITE) {
			// Delete the previous log file, and create a new one with a header...
			file.delete();
			check();
		}
		else if(conflictPolicy == CONFLICT_VERSION) {
			// Increment the version number until the conflict is avoided...
			version++;
			check();
		}
	}
	
	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return path + getVersionExtension();
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	protected File getFile() {
		return new File(getFileName());
	}

	
	/**
	 * Gets the version extension.
	 *
	 * @return the version extension
	 */
	protected String getVersionExtension() {
		return version == 0 ? "" : "." + version; 
	}
	
	/**
	 * Delete.
	 */
	public void delete() {
		File file = getFile();
		if(file.exists()) {
			System.err.println("Deleting " + file.getPath());
			file.delete();
		}
	}
	
	/**
	 * Delete all.
	 */
	public void deleteAll() {
		File directory = getFile().getParentFile();
		final String name = getFile().getName();
		if(directory == null) directory = new File(".");
		
		File[] matches = directory.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg) {
				String value = arg.getName();
				
				if(!value.startsWith(name)) return false;
				if(value.length() == name.length()) return true;
				String remainder = value.substring(name.length());
				if(remainder.charAt(0) != '.') return false;
				try {
					Integer.parseInt(remainder.substring(1));
					return true;
				}
				catch(NumberFormatException e) { return false; }
			}
		});
		
		for(File file : matches) {
			System.err.println("Deleting " + file.getPath());
		}
	}
	
	/**
	 * Gets the prints the stream.
	 *
	 * @return the prints the stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PrintStream getPrintStream() throws IOException {
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(getFile(), true)));
	}
	
	
	/**
	 * Adds the.
	 *
	 * @param entry the entry
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void add(String entry) throws IOException {
		PrintStream out = getPrintStream();
		out.println(entry);
		out.close();
	}
	
	
	/**
	 * The Class Entry.
	 */
	public static class Entry {
		
		/** The value. */
		private String key, value;
		
		/**
		 * Instantiates a new entry.
		 *
		 * @param key the key
		 * @param value the value
		 */
		public Entry(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * Gets the key.
		 *
		 * @return the key
		 */
		public String getKey() { return key; }
		
		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() { return value; }
		
		/**
		 * Gets the double.
		 *
		 * @return the double
		 */
		public double getDouble() { return Double.parseDouble(value); }
		
		/**
		 * Gets the int.
		 *
		 * @return the int
		 */
		public int getInt() { return Integer.parseInt(value); }
	}
	
	/*
	class Column extends ArrayList<String> {
		String name;
		String formatSpec;
		
		boolean getBoolean(int i) { return Util.parseBoolean(get(i)); }
		
		byte getByte(int i) { return Byte.decode(get(i)); }
		
		short getShort(int i) { return Short.decode(get(i)); }
		
		int getInt(int i) { return Integer.decode(get(i)); }
		
		long getLong(int i) { return Long.decode(get(i)); }
		
		float getFloat(int i) { return Float.parseFloat(get(i)); }
		
		double getDouble(int i) { return Double.parseDouble(get(i)); }
		
		Vector2D getVector2D(int i) { return new Vector2D(get(i)); }
		
	}
	*/

	/**
	 * The Class Row.
	 */
	public static class Row extends Hashtable<String, Entry> {	
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1708055526314357120L;

		/**
		 * Adds the.
		 *
		 * @param entry the entry
		 */
		public void add(Entry entry) {
			put(entry.getKey(), entry);
		}
	}
	
	
	/**
	 * Read.
	 *
	 * @param fileName the file name
	 * @return the array list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ArrayList<Row> read(String fileName) throws IOException {
		ArrayList<Row> data = new ArrayList<Row>();
		BufferedReader in = Util.getReader(fileName);
		
		String header = readHeader(in);
		StringTokenizer tokens = new StringTokenizer(header);
		ArrayList<String> labels = new ArrayList<String>();
		
		while(tokens.hasMoreTokens()) {
			String label = tokens.nextToken();
			if(label.contains("(")) label = label.substring(0, label.indexOf("("));
			labels.add(label);
		}
		
		//for(String label : labels) System.err.println("### label: " + label);
		
		String line = null;
		while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
			tokens = new StringTokenizer(line);
			int col = 0;
			Row row = new Row();
			
			while(tokens.hasMoreTokens()) row.add(new Entry(labels.get(col++), tokens.nextToken()));
			data.add(row);
		}
		
		return data;		
	}

	
	/** The Constant CONFLICT_OVERWRITE. */
	public final static int CONFLICT_OVERWRITE = 0;
	
	/** The Constant CONFLICT_VERSION. */
	public final static int CONFLICT_VERSION = 1;
	
	/** The Constant CONFLICT_DEFAULT. */
	public final static int CONFLICT_DEFAULT = CONFLICT_VERSION;
}

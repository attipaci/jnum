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

public class LogFile {

	String path;

	String format;

	int version = 0;

	int conflictPolicy = CONFLICT_DEFAULT;
	

	public LogFile(String path, String format, int conflictPolicy) throws IOException {
		this.path = path;
		this.format = format;
		this.conflictPolicy = conflictPolicy;
		check();
	}
	

	protected static String readHeader(BufferedReader in) throws IOException {
		String header = in.readLine();
		if(header == null) throw new IllegalStateException("Empty log file header.");
		if(header.charAt(0) != '#') throw new IllegalStateException("Illegal log file header.");
		if(header.length() < 2) throw new IllegalStateException("Empty file header.");
		return header.substring(2);
	}

	protected void check() throws IOException {
		File file = getFile();

		// If there is no prior log by that name, then simply create it and return....
		if(!file.exists()) {
		    try(PrintWriter out = new PrintWriter(new FileOutputStream(file))) {
		        out.println("# " + format);
		        out.close();
		    }
			return;
		}
					
		// Otherwise check if the headers match...
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(getFile())))) {
		    boolean pass = false;
		    if(readHeader(in).equals(format)) pass = true;
		    in.close();
		    if(pass) return;
		}
		catch(IllegalStateException e) { Util.warning(this, e); }
		
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
	

	public String getFileName() {
		return path + getVersionExtension();
	}
	

	protected File getFile() {
		return new File(getFileName());
	}


	protected String getVersionExtension() {
		return version == 0 ? "" : "." + version; 
	}
	

	public void delete() {
		File file = getFile();
		if(file.exists()) {
			Util.info(this, "Deleting " + file.getPath());
			file.delete();
		}
	}
	

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
		
		if(matches == null) return;
		
		for(File file : matches) {
			Util.info(this, "Deleting " + file.getPath());
		}
	}
	

	public PrintStream getPrintStream() throws IOException {
		return new PrintStream(new BufferedOutputStream(new FileOutputStream(getFile(), true)));
	}
	

	public void add(String entry) throws IOException {
	    try(PrintStream out = getPrintStream()) {
	        out.println(entry);
	        out.close();
	    }
	}
	

	
	public static class Entry {

		private String key, value;

		public Entry(String key, String value) {
			this.key = key;
			this.value = value;
		}
		

		public String getKey() { return key; }
		

		public String getValue() { return value; }
		

		public double getDouble() { return Double.parseDouble(value); }
		

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


	public static class Row extends Hashtable<String, Entry> {	

		private static final long serialVersionUID = -1708055526314357120L;

		public void add(Entry entry) {
			put(entry.getKey(), entry);
		}
	}
	

	public static ArrayList<Row> read(String fileName) throws IOException {
		ArrayList<Row> data = new ArrayList<>();
		
		try(BufferedReader in = Util.getReader(fileName)) {
		    String header = readHeader(in);
		    StringTokenizer tokens = new StringTokenizer(header);
		    ArrayList<String> labels = new ArrayList<>();

		    while(tokens.hasMoreTokens()) {
		        String label = tokens.nextToken();
		        if(label.contains("(")) label = label.substring(0, label.indexOf("("));
		        labels.add(label);
		    }

		    String line = null;
		    while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
		        tokens = new StringTokenizer(line);
		        int col = 0;
		        Row row = new Row();

		        while(tokens.hasMoreTokens()) row.add(new Entry(labels.get(col++), tokens.nextToken()));
		        data.add(row);
		    }

		    in.close();
		}
		
		return data;		
	}


	public final static int CONFLICT_OVERWRITE = 0;

	public final static int CONFLICT_VERSION = 1;

	public final static int CONFLICT_DEFAULT = CONFLICT_VERSION;
}

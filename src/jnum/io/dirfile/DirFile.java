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
// Copyright (c) 2010 Attila Kovacs 

package jnum.io.dirfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import jnum.Util;

// DirFile reading access
// For documentation on the dirfile standard, see:
//
// 		http://getdata.sourceforge.net/dirfile.html

// Supported directives: /INCLUDE /ENDIAN
// Supported field types: RAW, BIT, SBIT, CONST, LINCOM, LINTERP, PHASE, MULTIPLY, STRING
// Supported raw types, UINT8, UINT16, UINT32, INT8, INT16, INT32, INT64, FLOAT32 (FLOAT), FLOAT64 (DOUBLE)
// Partially supported: UNIT64 (w/o MSB -- as if positive INT64 only)
// Supported raw shorthands: c, s, S, u, U, i, f, d

// TODO unsupported directives: /PROTECT (all read only), /FRAMEOFFSET, /ENCODING, /META, /REFERENCE, /VERSION
// TODO unsupported field types: POLYNOM
// TODO unsupported field codes instead of numbers
// TODO unsupported types: COMPLEX64, COMPLEX128
//  --> ComplexCombinationStore
//  --> ComplexProductStore
//  --> ComplexConstant

// TODO Add /FRAMEOFFSET for v2 support
// TODO Add /VERSION for v5 support
// TODO and /META and /REFERENCE and /ENCODING (could do text ot gzip) for v6 support
// TODO Add COMPLEXnn and SBIT for v7 support



public class DirFile extends Hashtable<String, DataStore<?>> {

	private static final long serialVersionUID = 8563669618537254688L;

	Hashtable<String, String> strings = new Hashtable<>();

	Vector<String> pending = new Vector<>();

	String path;

	boolean isBigEndian = false;
	

	public static void main(String[] args) {
		try { 
			DirFile data = new DirFile(args[0]);
			
			String field = args[1]; 
			int from = Integer.parseInt(args[2]);
			int to = Integer.parseInt(args[3]);
			int step = args.length > 4 ? Integer.parseInt(args[4]) : 1;
			
			DataStore<?> store = data.get(field);
			
			Util.info(store, "Class " + store.getClass().getSimpleName());
			Util.info(store, "Samples: " + store.getSamples());
			Util.info(store, "Length: " + store.length());
			
			for(int i=from; i<to; i+=step) {
				Util.info(data, i + "\t" + store.get(i));				
			}
		
		}
		catch(Exception e) { Util.error(DirFile.class, e); }
	}
	

	public DirFile(String path) throws IOException {
		this(path, "format");
	}
		

	public DirFile(String path, String formatName) throws IOException {
		this.path = path;
		
		String endianness = System.getProperty("sun.cpu.endian");
		if(endianness == null) throw new IllegalStateException("Undefined endianness.");
		
		Util.info(this, "Native endianness: " + endianness);
		
		if(endianness.equalsIgnoreCase("big")) isBigEndian = true;
		else if(endianness.equalsIgnoreCase("big")) isBigEndian = false;
		
		parseFormat(formatName);
		
		Util.info(this, size() + " fields parsed.");
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ (isBigEndian ? 1 : 0) ^ path.hashCode() ^ strings.hashCode() ^ pending.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof DirFile)) return false;
		if(!super.equals(o)) return false;
		DirFile file = (DirFile) o;
		if(isBigEndian != file.isBigEndian) return false;
		if(!Util.equals(path, file.path)) return false;
		if(!strings.equals(file.strings)) return false;
		if(!pending.equals(file.pending)) return false;
		return true;
	}
	

	public void include(String fileName) throws IOException {
		// Check if the included file is specified together with a path (relative or absolute)
		// If so, read the subdirfile and then add it...
		if(fileName.contains(File.separator)) {					
			int i = fileName.lastIndexOf(File.separator);
			String dirName = fileName.substring(0, i);
			String formatName = fileName.substring(i + 1);

			// Try interpret as relative path, then as absolute path...
			File subdir = new File(path + File.separator + dirName);
			if(!subdir.exists()) subdir = new File(dirName);

			if(!subdir.exists()) { Util.warning(this, "Could not find inclusion: " + dirName); }
			else include(new DirFile(subdir.getPath(), formatName)); 
		}
		// Otherwise, just parse the extra format file in place...
		else parseFormat(fileName);
	}
	

	public void include(DirFile subdir) {
		putAll(subdir);
		strings.putAll(subdir.strings);
		pending.addAll(subdir.pending);
	}
	
	

	public void parseFormat(String name) throws IOException {		
	    try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path + File.separator + name)))) {
	        String line = null;

	        // Read lines in order...
	        // Those that have unresolved dependecies go to pending
	        while((line = in.readLine()) != null) if(line.length() > 0) if(line.charAt(0) != '#') {
	            try { add(getDataStore(line)); }
	            catch(NullPointerException e) { pending.add(line); }
	            catch(ClassCastException e) { Util.error(this, e); }
	        }

	        // Pending list is reduced after...
	        int initialSize;

	        do {
	            initialSize = pending.size();		
	            for(int i=pending.size(); i>=0; i--) {
	                try { 
	                    add(getDataStore(pending.get(i))); 
	                    pending.remove(i);
	                } 
	                catch(NullPointerException e) {}
	                catch(ClassCastException e) { Util.error(this, e); }
	            }
	        } while(pending.size() < initialSize);	

	        in.close();
	    }
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected DataStore<?> getDataStore(String spec) {
		StringTokenizer tokens = new StringTokenizer(spec);
		String name = tokens.nextToken();
		
		if(name.charAt(0) == '/') {
			String directive = name.toLowerCase();
			String value = tokens.nextToken();

			if(directive.equals("/include")) {
				try { include(tokens.nextToken()); }
				catch(IOException e) { 
					Util.warning(this, "Error encountered during inclusion:" + e.getMessage()); 
				}
			}
			else if(directive.equals("/endian")) {
				if(value.equalsIgnoreCase("big")) isBigEndian = true;
				else if(value.equalsIgnoreCase("little")) isBigEndian = false;
				Util.info(this, "Endianness set to: " + (isBigEndian ? "big" : "little"));
			}
			else Util.warning(this, "Directive " + directive + " is not supported.");
			
			return null;
		}
		
		String type = tokens.nextToken().toLowerCase();
		
		if(type.equals("raw")) {
			RawStore<?> data = RawStore.forSpec(path, name, tokens.nextToken(), Integer.parseInt(tokens.nextToken()));
			data.isBigEndian = isBigEndian;
			return data;
		}
		else if(type.equals("const")) return new ConstantStore(name, type, tokens.nextToken());
		else if(type.equals("bit")) return new BitStore(
				name,
				get(tokens.nextToken()), 
				Integer.parseInt(tokens.nextToken()),	 
				tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 1
			);
		else if(type.equals("sbit")) return new SBitStore(
				name,
				get(tokens.nextToken()), 
				Integer.parseInt(tokens.nextToken()),	 
				tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 1
			);
		else if(type.equals("lincom")) {
			int terms = Integer.parseInt(tokens.nextToken());
			LinearCombinationStore combo = new LinearCombinationStore(name);
			for(int i=0; i<terms; i++) {
				combo.addTerm(
						get(tokens.nextToken()),
						Double.parseDouble(tokens.nextToken()), 
						Double.parseDouble(tokens.nextToken())
					);
			}
			return combo;
		}
		else if(type.equals("linterp")) return new LinearInterpolatorStore(
				name, 
				get(tokens.nextToken()), 
				tokens.nextToken()
			);
		else if(type.equals("multiply")) return new ProductStore(
				name, 
				get(tokens.nextToken()), 
				get(tokens.nextToken())
			);
		else if(type.equals("phase")) return new PhaseShiftedStore(
				name, 
				get(tokens.nextToken()), 
				Integer.parseInt(tokens.nextToken())
			);
		else if(type.equals("string")) {
			strings.put(name, tokens.nextToken());
			return null;
		}
		else return null;
	}

	public void close() {
		for(DataStore<?> store : values()) {
			if(store instanceof RawStore<?>) {
				try { ((RawStore<?>) store).close(); }
				catch(IOException e) {}
			}			
		}
	}
	

	public void list() {
		
	}
	
	// Upon opening read the format definition for each value
	public void add(DataStore<?> field) {
		put(field.name, field); 
	}
	
	

	
}

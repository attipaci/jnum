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
// Copyright (c) 2010 Attila Kovacs 

package jnum.dirfile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import jnum.util.Util;

// TODO: Auto-generated Javadoc
// Reads Little-Endian from stream...
/**
 * The Class Raw.
 *
 * @param <Type> the generic type
 */
public abstract class Raw<Type extends Number> extends DataStore<Type> {
	
	/** The file. */
	RandomAccessFile file;
	
	/** The path. */
	String path;
		
	/** The bytes. */
	protected int bytes;
	
	/** The samples. */
	int samples;
	
	/** The is big endian. */
	boolean isBigEndian = false;
	
	/**
	 * Instantiates a new raw.
	 *
	 * @param path the path
	 * @param name the name
	 * @param arraySize the array size
	 */
	public Raw(String path, String name, int arraySize) {
		super(name);
		this.path = path;
		samples = arraySize;
	}
	
	/**
	 * Open.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void open() throws IOException {
		file = new RandomAccessFile(getFile(), "r");		
	}
	
	/**
	 * Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException {
		file.close();
		file = null;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return new File(path + File.separator + name);		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#getSamples()
	 */
	@Override
	public int getSamples() {
		return samples;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.dirfile.DataStore#length()
	 */
	@Override
	public long length() throws IOException {
		if(file != null) return file.length() / bytes;
		else return getFile().length() / bytes;
	}
	
	/**
	 * Gets the byte.
	 *
	 * @param n the n
	 * @return the byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected byte getByte(long n) throws IOException {
		if(file == null) open();
		file.seek(n);
		return file.readByte();
	}
	
	/**
	 * Gets the unsigned byte.
	 *
	 * @param n the n
	 * @return the unsigned byte
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected short getUnsignedByte(long n) throws IOException {
		return Util.unsigned(getByte(n));
	}
	
	/**
	 * Gets the short.
	 *
	 * @param n the n
	 * @return the short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected short getShort(long n) throws IOException {
		if(isBigEndian) return file.readShort();
		return (short) getUnsignedShort(n);
	}
	
	/**
	 * Gets the unsigned short.
	 *
	 * @param n the n
	 * @return the unsigned short
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected int getUnsignedShort(long n) throws IOException {
		if(file == null) open();
		file.seek(n<<1);	
		return isBigEndian ? Util.unsigned(file.readShort()) : Util.unsigned(Short.reverseBytes(file.readShort()));
	}
	
	/**
	 * Gets the int.
	 *
	 * @param n the n
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected int getInt(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 2);
		return isBigEndian ? file.readInt() : Integer.reverseBytes(file.readInt());
	}
	
	/**
	 * Gets the unsigned int.
	 *
	 * @param n the n
	 * @return the unsigned int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected long getUnsignedInt(long n) throws IOException {
		return Util.unsigned(getInt(n));
	}

	/**
	 * Gets the long.
	 *
	 * @param n the n
	 * @return the long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected long getLong(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 3);
		return isBigEndian ? file.readLong() : Long.reverseBytes(file.readLong());	}
	
	/**
	 * Gets the unsigned long.
	 *
	 * @param n the n
	 * @return the unsigned long
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected long getUnsignedLong(long n) throws IOException {
		return Util.pseudoUnsigned(getLong(n));
	}
	
	/**
	 * Gets the float.
	 *
	 * @param n the n
	 * @return the float
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected float getFloat(long n) throws IOException {
		return Float.intBitsToFloat(getInt(n));
	}
	
	/**
	 * Gets the double.
	 *
	 * @param n the n
	 * @return the double
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected double getDouble(long n) throws IOException {
		return Double.longBitsToDouble(getLong(n));
	}
	
	/**
	 * Gets the char.
	 *
	 * @param n the n
	 * @return the char
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected char getChar(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 1);
		return isBigEndian ? file.readChar() : (char) Short.reverseBytes(file.readShort());
	}
	

	/**
	 * For spec.
	 *
	 * @param path the path
	 * @param name the name
	 * @param type the type
	 * @param elements the elements
	 * @return the raw
	 */
	public static Raw<?> forSpec(String path, String name, String type, int elements) {
			
		if(type.length() == 1) switch(type.charAt(0)) {
		case 'u' : return new UShortStore(path, name, elements); 
		case 'U' : return new UIntegerStore(path, name, elements);
		case 's' : return new ShortStore(path, name, elements);
		case 'S' : return new IntegerStore(path, name, elements);
		case 'i' : return new IntegerStore(path, name, elements);
		case 'c' : return new UByteStore(path, name, elements);
		case 'f' : return new FloatStore(path, name, elements);
		case 'd' : return new DoubleStore(path, name, elements);
		default : return null;
		}
		
		type = type.toLowerCase();
		
		if(type.equals("float")) return new FloatStore(path, name, elements);
		else if(type.equals("double")) return new DoubleStore(path, name, elements);
		else if(type.startsWith("uint")) {
			int bits = Integer.parseInt(type.substring(4));
			switch(bits) {
			case 8 : return new UByteStore(path, name, elements); 
			case 16 : return new UShortStore(path, name, elements); 
			case 32 : return new UIntegerStore(path, name, elements); 
			case 64 : return new ULongStore(path, name, elements);
			default : return null;
			}
		}
		else if(type.startsWith("int")) {
			int bits = Integer.parseInt(type.substring(3));
			switch(bits) {
			case 8 : return new ByteStore(path, name, elements); 
			case 16 : return new ShortStore(path, name, elements); 
			case 32 : return new IntegerStore(path, name, elements); 
			case 64 : return new LongStore(path, name, elements); 
			default : return null;
			}
		}
		else if(type.startsWith("float")) {
			int bits = Integer.parseInt(type.substring(5));
			switch(bits) {
			case 32 : return new FloatStore(path, name, elements);
			case 64 : return new DoubleStore(path, name, elements);
			default : return null;
			}
		}
		else return null;
	}

}



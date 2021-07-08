/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.io.dirfile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import jnum.Util;


// Reads Little-Endian from stream...
public abstract class RawStore<Type extends Number> extends DataStore<Type> {

	private static final long serialVersionUID = 684293239178895163L;

	RandomAccessFile file;

	String path;

	protected int bytes;

	int samples;

	boolean isBigEndian = false;
	

	public RawStore(String path, String name, int arraySize) {
		super(name);
		this.path = path;
		samples = arraySize;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ file.hashCode() ^ bytes ^ samples ^ (isBigEndian ? 1 : 0);
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof RawStore)) return false;
		if(!super.equals(o)) return false;
		RawStore<?> store = (RawStore<?>) o;
		if(bytes != store.bytes) return false;
		if(samples != store.samples) return false;
		if(isBigEndian != store.isBigEndian) return false;
		if(!file.equals(store.file)) return false;
		return true;
	}
	

	public void open() throws IOException {
		file = new RandomAccessFile(getFile(), "r");		
	}

	public void close() throws IOException {
		file.close();
		file = null;
	}
	

	public File getFile() {
		return new File(path + File.separator + name);		
	}

	@Override
	public int getSamples() {
		return samples;
	}

	@Override
	public long length() throws IOException {
		if(file != null) return file.length() / bytes;
		return getFile().length() / bytes;
	}
	

	protected byte getByte(long n) throws IOException {
		if(file == null) open();
		file.seek(n);
		return file.readByte();
	}
	

	protected short getUnsignedByte(long n) throws IOException {
		return Util.unsigned(getByte(n));
	}
	

	protected short getShort(long n) throws IOException {
		if(isBigEndian) return file.readShort();
		return (short) getUnsignedShort(n);
	}
	

	protected int getUnsignedShort(long n) throws IOException {
		if(file == null) open();
		file.seek(n<<1);	
		return isBigEndian ? Util.unsigned(file.readShort()) : Util.unsigned(Short.reverseBytes(file.readShort()));
	}
	

	protected int getInt(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 2);
		return isBigEndian ? file.readInt() : Integer.reverseBytes(file.readInt());
	}
	

	protected long getUnsignedInt(long n) throws IOException {
		return Util.unsigned(getInt(n));
	}


	protected long getLong(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 3);
		return isBigEndian ? file.readLong() : Long.reverseBytes(file.readLong());	
	}
	

	protected long getUnsignedLong(long n) throws IOException {
		return Util.pseudoUnsigned(getLong(n));
	}
	

	protected float getFloat(long n) throws IOException {
		return Float.intBitsToFloat(getInt(n));
	}
	

	protected double getDouble(long n) throws IOException {
		return Double.longBitsToDouble(getLong(n));
	}
	

	protected char getChar(long n) throws IOException {
		if(file == null) open();
		file.seek(n << 1);
		return isBigEndian ? file.readChar() : (char) Short.reverseBytes(file.readShort());
	}
	

	public static RawStore<?> forSpec(String path, String name, String type, int elements) {
			
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



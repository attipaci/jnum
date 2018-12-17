/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.io;
import java.io.*;

import jnum.Util;


public class LittleEndianDataInputStream extends InputStream implements DataInput {

	private InputStream stream;

	private DataInputStream in;

	
	/**
	 * Instantiates a new little endian data input stream.
	 *
	 * @param stream the stream
	 */
	public LittleEndianDataInputStream(InputStream stream) {
		this.stream = stream;
		in = new DataInputStream(stream);
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
    public void close() throws IOException {
	    if(stream != null) stream.close();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public final int read() throws IOException {
		return stream.read();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readBoolean()
	 */
	@Override
	public final boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readByte()
	 */
	@Override
	public final byte readByte() throws IOException {
		return in.readByte();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readChar()
	 */
	@Override
	public final char readChar() throws IOException {
		return (char) readShort();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readDouble()
	 */
	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readFloat()
	 */
	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[])
	 */
	@Override
	public final void readFully(final byte[] arg0) throws IOException {
		in.readFully(arg0);
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readFully(byte[], int, int)
	 */
	@Override
	public final void readFully(final byte[] arg0, final int arg1, final int arg2) throws IOException {
		in.readFully(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readInt()
	 */
	@Override
	public final int readInt() throws IOException {
		return Integer.reverseBytes(in.readInt());
	}

	
	public final long readUnsignedInt() throws IOException {
		return Util.unsigned(readInt());
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readLine()
	 */
	@Override
	@Deprecated
	public final String readLine() throws IOException {
		return in.readLine();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readLong()
	 */
	@Override
	public final long readLong() throws IOException {
		return Long.reverseBytes(in.readLong());
	}
	
	/* (non-Javadoc)
	 * @see java.io.DataInput#readShort()
	 */
	@Override
	public final short readShort() throws IOException {
		return Short.reverseBytes(in.readShort());
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUTF()
	 */
	@Override
	public final String readUTF() throws IOException {
		return in.readUTF();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedByte()
	 */
	@Override
	public final int readUnsignedByte() throws IOException {
		return in.readUnsignedByte();
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#readUnsignedShort()
	 */
	@Override
	public final int readUnsignedShort() throws IOException {
		return Util.unsigned(readShort());
	}

	/* (non-Javadoc)
	 * @see java.io.DataInput#skipBytes(int)
	 */
	@Override
	public final int skipBytes(int arg0) throws IOException {
		return in.skipBytes(arg0);
	}
	

	public final short read2Bytes() throws IOException { return in.readShort(); }
	

	public final int read4Bytes() throws IOException { return in.readInt(); }
	

	public final long read8Bytes() throws IOException { return in.readLong(); }
	
	
}

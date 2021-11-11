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
package jnum.io;
import java.io.*;

import jnum.Util;


/**
 * A {@link DataInputStream} implementation for little-endian data format. It can be used to read
 * binary data, written in little-endian format (which is th native format of Intel CPUs!), using
 * the familiar API of a Java data input stream.
 * 
 * @author Attila Kovacs
 * 
 * @see DataInputStream
 * @see VAXDataInputStream
 *
 */
public class LittleEndianDataInputStream extends InputStream implements DataInput {

	/** The underlying (big-endian) data input stream */
	private DataInputStream in;

	
	/**
	 * Instantiates a new little endian data input stream.
	 *
	 * @param stream the stream
	 */
	public LittleEndianDataInputStream(InputStream stream) {
		in = new DataInputStream(stream);
	}

	@Override
    public void close() throws IOException {
	    in.close();
	}

	@Override
	public final int read() throws IOException {
		return in.read();
	}

	@Override
	public final boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	@Override
	public final byte readByte() throws IOException {
		return in.readByte();
	}

	@Override
	public final char readChar() throws IOException {
		return (char) readShort();
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public final void readFully(final byte[] arg0) throws IOException {
		in.readFully(arg0);
	}

	@Override
	public final void readFully(final byte[] arg0, final int arg1, final int arg2) throws IOException {
		in.readFully(arg0, arg1, arg2);
	}

	@Override
	public final int readInt() throws IOException {
		return Integer.reverseBytes(in.readInt());
	}

	@Override
	@Deprecated
	public final String readLine() throws IOException {
		return in.readLine();
	}

	@Override
	public final long readLong() throws IOException {
		return Long.reverseBytes(in.readLong());
	}

	@Override
	public final short readShort() throws IOException {
		return Short.reverseBytes(in.readShort());
	}

	@Override
	public final String readUTF() throws IOException {
		return in.readUTF();
	}

	@Override
	public final int readUnsignedByte() throws IOException {
		return in.read();
	}

	@Override
	public final int readUnsignedShort() throws IOException {
	    int l = in.read();
        int h = in.read();
        if (h < 0 || l < 0) {
            throw new EOFException();
        }
        return (h << 8) | l;
	}
	    
	/**
     * Reads an unsigned 32-bit integer value from the input, returning it as a Java <code>long</code>
     * 
     * @return      a Java <code>long</code> containing the unsigned 32-bit value.
     * @throws IOException      if there was an IO error.
     * 
     * @see Util#unsigned(int)
     */
    public final long readUnsignedInt() throws IOException {
        return Util.unsigned(readInt());
    }
	
	@Override
	public final int skipBytes(int arg0) throws IOException {
		return in.skipBytes(arg0);
	}
	
	
}

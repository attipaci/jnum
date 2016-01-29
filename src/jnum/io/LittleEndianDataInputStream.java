/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.io;
import java.io.*;

import jnum.util.Util;

public class LittleEndianDataInputStream extends InputStream implements DataInput {
	private InputStream stream;
	private DataInputStream in;

	
	public LittleEndianDataInputStream(InputStream stream) {
		this.stream = stream;
		in = new DataInputStream(stream);
	}

	@Override
	public final int read() throws IOException {
		return stream.read();
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
	
	public final long readUnsignedInt() throws IOException {
		return Util.unsigned(readInt());
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
		return in.readUnsignedByte();
	}

	@Override
	public final int readUnsignedShort() throws IOException {
		return Util.unsigned(readShort());
	}

	@Override
	public final int skipBytes(int arg0) throws IOException {
		return in.skipBytes(arg0);
	}
	
	public final short read2Bytes() throws IOException { return in.readShort(); }
	
	public final int read4Bytes() throws IOException { return in.readInt(); }
	
	public final long read8Bytes() throws IOException { return in.readLong(); }
	
	
}

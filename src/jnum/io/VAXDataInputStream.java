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

/**
 * Binary data input stream for data stored in VAX format. The VAX systems, popular in the 1990s have used
 * a floating point format that was quite different from the IEEE 754 type floats used on most platforms
 * today. More than just byte order differences, the VAX floating point formats have designated bits entirely
 * differently. It is however possible to construct IEEE 754 floating point types from the equivalent
 * 32-bit and 64-bit vax floats, by doing a set of bitwise operations And that exactly is the purpose of
 * this class, which makes binary data, written in VAX format available to Java applications.
 * 
 * @author Attila Kovacs
 *
 */
public class VAXDataInputStream extends LittleEndianDataInputStream {
	
    /**
     * Instantiates a new input stream for processing binary data written in native VAX format.
     * 
     * @param stream    The binary file containing native VAX types.
     */
	public VAXDataInputStream(InputStream stream) {
		super(stream);
	}

	@Override
	public final double readDouble() throws IOException {
		// The first 32-bits are the same as for a VAX float. So use the built-in float-to-double
		// conversion to create a double stem. Then convert to IEEE 754 bits
		// This representation will have 35-bits (not 32) since the IEEE double has 11 exponent bits vs
		// 8 in the VAX double.
		long l = Double.doubleToLongBits(readFloat());
		
		// Now paste in the remaining fraction bits. The fraction bits must shift down by 3 bits for 
		// proper alignment...
		int i = read4Bytes();		
		l |= (((i >>> 8 & 0x00FF00FF) | (i & 0x00FF00FF) << 8) >>> 3) & 0x2FFFFFFF;
		return Double.longBitsToDouble(l);	
	}

	@Override
	public final float readFloat() throws IOException {
		int i = read4Bytes();
		return 0.25F * Float.intBitsToFloat((i >>> 8 & 0x00FF00FF) | (i & 0x00FF00FF) << 8);
	}

	
}

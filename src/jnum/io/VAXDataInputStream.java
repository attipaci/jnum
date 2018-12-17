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


public class VAXDataInputStream extends LittleEndianDataInputStream {
	

	public VAXDataInputStream(InputStream stream) {
		super(stream);
	}

	/* (non-Javadoc)
	 * @see jnum.io.LittleEndianDataInputStream#readDouble()
	 */
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

	/* (non-Javadoc)
	 * @see jnum.io.LittleEndianDataInputStream#readFloat()
	 */
	@Override
	public final float readFloat() throws IOException {
		int i = read4Bytes();
		return 0.25F * Float.intBitsToFloat((i >>> 8 & 0x00FF00FF) | (i & 0x00FF00FF) << 8);
	}

	
}

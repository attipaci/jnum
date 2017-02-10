/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package test;

import java.awt.Color;

// TODO: Auto-generated Javadoc
/**
 * The Class ColorTest.
 */
public class ColorTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		System.err.println("R : " + Integer.toHexString(Color.RED.getRGB()));
		System.err.println("G : " + Integer.toHexString(Color.GREEN.getRGB()));
		System.err.println("B : " + Integer.toHexString(Color.BLUE.getRGB()));
		
		int N = 1000000;
		int r = 0, g = 0, b = 0;
		int result = 0;
		
		System.err.print("Running base test: ");		
		long basetime = -System.currentTimeMillis();
		for(int i=N; --i >= 0; ) {
			r = i & 0x00FF0000 >> 16;
			g = i & 0x0000FF00 >> 8;
			b = i & 0x000000FF;
			result ^= r;
		}
		basetime += System.currentTimeMillis();
		System.err.println(basetime + " ms");
		
		System.err.print("Running direct compose: ");
		long directtime = -System.currentTimeMillis();
		for(int i=N; --i >= 0; ) {
			r = i & 0x00FF0000 >> 16;
			g = i & 0x0000FF00 >> 8;
			b = i & 0x000000FF;
			result ^= 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		}
		directtime += System.currentTimeMillis() - basetime;
		System.err.println(directtime + " ms");
		
		System.err.print("Running Color compose: ");
		long colortime = -System.currentTimeMillis();
		for(int i=N; --i >= 0; ) {
			r = i & 0x00FF0000 >> 16;
			g = i & 0x0000FF00 >> 8;
			b = i & 0x000000FF;
			result ^= new Color(r, g, b).getRGB();
		}
		colortime += System.currentTimeMillis() - basetime;
		System.err.println(colortime + " ms");
		
		System.err.println(result);
		
	}
	
	
}

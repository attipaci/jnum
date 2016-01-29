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
package test;

import jnum.Constant;
import jnum.Util;
import jnum.fft.FFT;
import jnum.fft.FloatFFT;

// TODO: Auto-generated Javadoc
/**
 * The Class FloatFFTTest.
 */
public class FloatFFTTest {
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
			
		int threads = 1;
		float[] data = new float[64];
		
		FloatFFT fft = new FloatFFT();
		
		System.out.println("\ndelta(0):");
		data[0] = 1.0F;
	
		fft.complexTransform(data, FFT.FORWARD, threads); 
		print(data);
		
		
		System.out.println("\nconst:");
		for(int i=0; i<data.length; i += 2) {
			data[i] = 1.0F;
			data[i+1] = 0.0F;
		}
		
		fft.complexTransform(data, FFT.FORWARD, threads); 
		print(data);
		
		
		double k1 = Constant.twoPi / data.length;
		double k2 = 2.0 * k1;
		
		System.out.println("\ncos(2):");
		
		for(int i=0; i < data.length; i += 2) {
			data[i] = (float) Math.cos(k2 * i);
			data[i+1] = 0.0F;
		}
		 
		fft.complexTransform(data, FFT.FORWARD, threads); 
		print(data);
		
	
		System.out.println("\nsin(1):");
		
		for(int i=0; i<data.length; i+=2) {
			data[i] = (float) Math.sin(k1 * i);
			data[i+1] = 0.0F;
		}
		
		fft.complexTransform(data, FFT.FORWARD, threads); 
		print(data);
		
	}
	
	
	
	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	static void print(float[] data) {
		for(int i=0; i<data.length; i+=2) {
			System.out.println(" " + (i>>1) + ":\t" + Util.f3.format(data[i]) + ", " + Util.f3.format(data[i+1]));
		}
		System.out.println();	
	}
	
	
}

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

import jnum.ExtraMath;
import jnum.Util;
import jnum.fft.FloatFFT;

// TODO: Auto-generated Javadoc
/**
 * The Class ParFloatFFTTest.
 */
public class ParFloatFFTTest {

/**
 * The main method.
 *
 * @param args the arguments
 */
public static void main(String[] args) {
		System.err.println("FFT test for floats...");
	
		float[] data = new float[32];

		FloatFFT fft = new FloatFFT();
		fft.setSequential();
		
		System.err.println("delta[0]:");
		data[0] = 1.0F;
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("constant(1.0):");
		for(int i=0; i<data.length; i+=2)  {
			data[i] = 1.0F;
			data[i+1] = 0.0F;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		
		System.err.println("cos1:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = (float) Math.cos(2.0 * Math.PI * i / data.length);
			data[i+1] = 0.0F;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("sin2:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = (float) Math.sin(4.0 * Math.PI * i / data.length);
			data[i+1] = 0.0F;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("cos2:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = (float) Math.cos(4.0 * Math.PI * i / data.length);
			data[i+1] = 0.0F;
		}
		try { fft.realTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
			
		int m = 5, k = 7;
		System.err.println("amp real cos" + m + ", sin" + k);
		for(int i=0; i<data.length; i++) data[i] = (float) Math.cos(2.0 * m * Math.PI * i / data.length) + (float) Math.sin(2.0 * k * Math.PI * i / data.length);
		print(data);
		
		System.err.println("r2a:");
		try { fft.real2Amplitude(data); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("a2r:");
		try { fft.amplitude2Real(data); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
	
		fft.shutdown();
		
		int i = 1<<20;
		System.out.println("1<<20  :" + ExtraMath.log2floor(i) + ", " + ExtraMath.log2round(i) + ", " + ExtraMath.log2ceil(i));
		
		i-=1;
		System.out.println("1<<20-1:" + ExtraMath.log2floor(i) + ", " + ExtraMath.log2round(i) + ", " + ExtraMath.log2ceil(i));
		
		i+=2;
		System.out.println("1<<20+1:" + ExtraMath.log2floor(i) + ", " + ExtraMath.log2round(i) + ", " + ExtraMath.log2ceil(i));
		
	}

	
	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	public static void print(float[] data) {
		for(int i=0; i<data.length; i+=2) 
			System.out.println("  " + (i>>1) + ":\t" + Util.f6.format(data[i]) + ", " + Util.f6.format(data[i+1]));
		System.out.println();
	}
	
	
}

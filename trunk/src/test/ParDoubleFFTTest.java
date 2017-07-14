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

import jnum.Util;
import jnum.fft.DoubleFFT;
import jnum.parallel.ParallelTask;

// TODO: Auto-generated Javadoc
/**
 * The Class ParDoubleFFTTest.
 */
public class ParDoubleFFTTest {

/**
 * The main method.
 *
 * @param args the arguments
 */
public static void main(String[] args) {
		double[] data = new double[64];
		
		DoubleFFT fft = new DoubleFFT(ParallelTask.newDefaultParallelExecutor());
		fft.setParallel(3);
		fft.setTwiddleErrorBits(3);
		
		System.err.println("delta[0]:");
		data[0] = 1.0;
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("constant(1.0):");
		for(int i=0; i<data.length; i+=2)  {
			data[i] = 1.0;
			data[i+1] = 0.0;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		
		System.err.println("cos1:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = Math.cos(2.0 * Math.PI * i / data.length);
			data[i+1] = 0.0;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("sin2:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = Math.sin(4.0 * Math.PI * i / data.length);
			data[i+1] = 0.0;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		System.err.println("cos2:");
		for(int i=0; i<data.length; i+=2) {
			data[i] = Math.cos(4.0 * Math.PI * i / data.length);
			data[i+1] = 0.0;
		}
		try { fft.complexTransform(data, true); }
		catch(Exception e) { e.printStackTrace(); }
		print(data);
		
		
		int m = 8, k = 8;
		System.err.println("amp real cos" + m + ", sin" + k);
		for(int i=0; i<data.length; i++) data[i] = Math.cos(2.0 * m * Math.PI * i / data.length) + Math.sin(2.0 * k * Math.PI * i / data.length);
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
	}

	
	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	public static void print(double[] data) {
		for(int i=0; i<data.length; i+=2) 
			System.out.println("  " + (i>>1) + ":\t" + Util.f6.format(data[i]) + ", " + Util.f6.format(data[i+1]));
		System.out.println();
	}
	
	
}

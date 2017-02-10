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

import jnum.Constant;
import jnum.Util;
import jnum.fft.ComplexFFT;
import jnum.fft.FFT;
import jnum.math.Complex;

// TODO: Auto-generated Javadoc
/**
 * The Class ComplexFFTTest.
 */
public class ComplexFFTTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
			
		int threads = 1;
		Complex[] data = new Complex[32];
		for(int i=data.length; --i >= 0; ) data[i] = new Complex();
		
		ComplexFFT fft = new ComplexFFT();
		
		System.out.println("\ndelta(0):");
		data[0].set(1.0, 0.0);
		
		try { 
			fft.complexTransform(data, FFT.FORWARD, threads); 
			print(data);
		}
		catch(Exception e) { e.printStackTrace(); }
		
		
		
		System.out.println("\nconst:");
		for(int i=data.length; --i >= 0; ) data[i].set(1.0, 0.0);
		
		try { 
			fft.complexTransform(data, FFT.FORWARD, threads); 
			print(data);
		}
		catch(Exception e) { e.printStackTrace(); }
		
		
		
		double k1 = Constant.twoPi / data.length;
		double k2 = 2.0 * k1;
		double kn1 = ((data.length>>1) - 1) * k1;
		
		System.out.println("\ncos(2):");
		
		for(int i=data.length; --i >= 0; ) data[i].set(Math.cos(k2 * i), 0.0);
		
		try { 
			fft.complexTransform(data, FFT.FORWARD, threads); 
			print(data);
		}
		catch(Exception e) { e.printStackTrace(); }
		
		
		
		System.out.println("\nsin(1):");
		
		for(int i=data.length; --i >= 0; ) data[i].set(Math.sin(k1 * i), 0.0);
		
		try { 
			fft.complexTransform(data, FFT.FORWARD, threads); 
			print(data);
		}
		catch(Exception e) { e.printStackTrace(); }
		
		
		System.out.println("\ncos(nf-1):");
        
		
        for(int i=data.length; --i >= 0; ) data[i].set(Math.cos(kn1 * i), 0.0);
        
        try { 
            fft.complexTransform(data, FFT.FORWARD, threads); 
            print(data);
        }
        catch(Exception e) { e.printStackTrace(); }
		
	}
	
	/**
	 * Clear.
	 *
	 * @param data the data
	 */
	static void clear(Complex[] data) {
		for(Complex c : data) c.zero();
	}
	
	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	static void print(Complex[] data) {
		for(int i=0; i<data.length; i++) System.out.println(" " + i + ":\t" + data[i].toString(Util.f3));
		System.out.println();
		
	}
	
}
 

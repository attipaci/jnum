/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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
import jnum.fft.*;
import jnum.parallel.ParallelTask;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiFFTTest.
 */
public class MultiFFTTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		DoubleFFT dfft = new DoubleFFT.NyquistUnrolledReal(ParallelTask.newDefaultParallelExecutor());
		double[] ddata = new double[10];
		ddata[0] = 8;
		dfft.real2Amplitude(ddata);
		print(ddata);
		
		dfft.amplitude2Real(ddata);
		print(ddata);
		
		
		MultiFFT fft = new MultiFFT(ParallelTask.newDefaultParallelExecutor());
		fft.setParallel(1);
		
		double[][] data = new double[8][10];
			
		System.err.println("delta: ");
			
		data[6][8] = data.length * (data[0].length-2) / 2;
		fft.real2Amplitude(data);
		//fft.complexForward(data);
		print(data);
		
		fft.amplitude2Real(data);
		//fft.complexBack(data);
		print(data);
			
		
	}

	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	public static void print(double[][] data) {
		System.out.println();
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) System.out.print(Util.f3.format(data[i][j]) + "\t");
			System.out.println();
		}
		System.out.println();
	}

	
	/**
	 * Prints the.
	 *
	 * @param data the data
	 */
	public static void print(double[] data) {
		System.out.println();
		for(int i=0; i<data.length; i++) 
			System.out.println(Util.f3.format(data[i]) + "\t");
		System.out.println();
	}
	
}


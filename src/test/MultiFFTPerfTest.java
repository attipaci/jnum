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

import jnum.ExtraMath;
import jnum.Util;
import jnum.fft.MultiFFT;

// TODO: Auto-generated Javadoc
/**
 * The Class FloatFFTPerfTest.
 */
public class MultiFFTPerfTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		final int total = 256 * 1024 * 1024;
		final int n = args.length > 0 ? Integer.parseInt(args[0]) : 16;
		final int N = n * 1024;
		final int repeats = Math.max(total / N, 1);
		
	
		final int d0 = 1 << (int) Math.ceil(ExtraMath.log2(N) / 3.0);
		final int d1 = N / (d0*d0);
		
		//final long ops = repeats * N * Math.round((Math.log(N) / Math.log(2.0)));

		final double[][] data = new double[n][1024];
		System.err.println("D = " + n + " x 1024");
		
		//final float[][][] data = new float[d1][d0][d0];
		//System.err.println("D = " + d1 + " x " + d0 + " x " + d0);
		
		long time = 0L;
		double speed = Double.NaN;
		
		/*
		for(int i=0; i<data.length; i++) data[i] = Math.random();
		long time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) FFT.powerTransform(data, (k & 1) == 0);
		time += System.currentTimeMillis();
		double speed = repeats / (1e-3*time);
		System.err.println("sequent'l " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
		*/
		
		MultiFFT fft = new MultiFFT();
		if(args.length > 1) fft.setTwiddleErrorBits(Integer.parseInt(args[1]));		
		
		System.err.println("error bits: " + Util.f2.format(fft.getMaxErrorBitsFor(data)));
		System.err.println("precision: " + Util.e2.format(fft.getMinPrecisionFor(data)));
		System.err.println("dynamic range: " + Util.f1.format(-20.0 * Math.log10(fft.getMinPrecisionFor(data))) + " dB");
	
		
		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();	
		fft.setThreads(1);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println("1 thread  " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
		
			
		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();
		fft.setThreads(2);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println("2 threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");

		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();
		fft.setThreads(4);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println("4 threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
	
		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();
		fft.setThreads(8);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println("8 threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
	
	
		int cpus = Runtime.getRuntime().availableProcessors();
		if(cpus <= 4) { fft.shutdown(); return; }
		if(cpus == 8) { fft.shutdown(); return; }
		
		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();
		fft.setThreads(cpus/2);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println((cpus/2) + " threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
		
		for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) data[i][j] = (float) Math.random();
		//for(int i=data.length; --i >= 0; ) for(int j=data[0].length; --j >= 0; ) for(int k=data[0][0].length; --k >= 0; ) data[i][j][k] = (float) Math.random();
		fft.setThreads(cpus);
		time = -System.currentTimeMillis();
		for(int k=repeats; --k>=0; ) {
			try { fft.complexTransform(data, (k & 1) == 0); }
			catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}
		time += System.currentTimeMillis();
		speed = repeats / (1e-3*time);
		System.err.println(cpus + " threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
		
		fft.shutdown();
	}
}

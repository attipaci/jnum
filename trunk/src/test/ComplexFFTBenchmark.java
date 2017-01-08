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

import jnum.Util;
import jnum.fft.ComplexFFT;
import jnum.math.Complex;

// TODO: Auto-generated Javadoc
/**
 * The Class ComplexFFTPerfTest.
 */
public class ComplexFFTBenchmark {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		final int total = 128 * 1024 * 1024;
		final int n = args.length > 0 ? Integer.parseInt(args[0]) : 16;
		final int N = n * 512;
		final int repeats = Math.max(total / N, 1);

		int errorbits = args.length > 1 ? Integer.parseInt(args[1]) : 3;
		
		//final long ops = repeats * N * Math.round((Math.log(N) / Math.log(2.0)));

		final Complex[] data = new Complex[N];
		for(int i=data.length; --i >= 0; ) data[i] = new Complex();
		
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
		
		ComplexFFT fft = new ComplexFFT();
		fft.setTwiddleErrorBits(errorbits);
		System.err.println("error bits: " + Util.f2.format(fft.getMaxErrorBitsFor(data)));
		System.err.println("precision: " + Util.e2.format(fft.getMinPrecisionFor(data)));
		System.err.println("dynamic range: " + Util.f1.format(-20.0 * Math.log10(fft.getMinPrecisionFor(data))) + " dB");
		
		
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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
		
			
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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
		
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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
	
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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
		System.err.println("8 threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
	
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
		fft.setThreads(16);	
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
		System.err.println("16 threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
	
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
		fft.autoThread();	
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
		System.err.println("[auto] " + fft.getThreads() + " threads " + repeats + " x " + n + "k points: " + Util.f2.format(speed) + " FFTs/s");
		
		
		
		int cpus = Runtime.getRuntime().availableProcessors();
		if(cpus <= 4) { fft.shutdown(); return; }
		if(cpus == 8) { fft.shutdown(); return; }
		
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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
		
		for(int i=0; i<data.length; i++) data[i].set(Math.random(), Math.random());
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

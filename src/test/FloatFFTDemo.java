/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package test;


import jnum.ExtraMath;
import jnum.Util;
import jnum.fft.FFT;
import jnum.fft.FloatFFT;
import jnum.parallel.ParallelTask;


/**
 * The Class FloatFFTBenchmark.
 */
public class FloatFFTDemo {
    private int repeats = 1;
    private float[] data;
    private final FloatFFT fft = new FloatFFT(ParallelTask.newDefaultParallelExecutor());
     
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	    FloatFFTDemo demo = new FloatFFTDemo(args.length > 0 ? Integer.parseInt(args[0]) : 16); 
	    demo.benchmark();
	}
	
	private FloatFFTDemo(int nKiloSamples) {
		final int totalCount = 256 * 1024 * 1024;
		final int samples = nKiloSamples * 1024;
		
		data = new float[samples];
		repeats = Math.max(totalCount / samples, 1);
	
		Util.info(this, "error bits: " + Util.f2.format(fft.getMaxErrorBitsFor(data)));
		Util.info(this, "precision: " + Util.e2.format(fft.getMinPrecisionFor(data)));
		Util.info(this, "dynamic range: " + Util.f1.format(-20.0 * Math.log10(fft.getMinPrecisionFor(data))) + " dB");
	}
	
	public void benchmark() {     
	    final int cpus = Runtime.getRuntime().availableProcessors();
        
        for(int nThreads = 1; nThreads <= (cpus<<2); nThreads<<=1) benchmark(fft, nThreads);
    
        System.err.print("AUTO: ");
        benchmark(fft, FFT.AUTO_PARALLELISM);
        
        
        if(cpus == ExtraMath.pow2ceil(cpus)) { fft.shutdown(); return; }
        
        for(int nThreads = Math.max(1, cpus>>1); nThreads <= (cpus<<2); nThreads<<=1) benchmark(fft, nThreads);
   
             
        fft.shutdown();
	}
		
	public void benchmark(FloatFFT fft, final int nThreads) {
	    long time = 0L;
	 
	    for(int i=data.length; --i >= 0; ) data[i] = (float) Math.random();
        
	    fft.setParallel(nThreads);
        time = -System.currentTimeMillis();
        
        for(int k=repeats; --k >= 0; ) {
            try { fft.complexTransform(data, (k & 1) == 0); }
            catch(Exception e) { 
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        time += System.currentTimeMillis();
        
        final double speed = repeats / (1e-3*time);
        
        System.err.println(nThreads + " thread(s) " + repeats + " x " + (data.length>>>10) + "k points: " + Util.f2.format(speed) + " FFTs/s");
	}
}

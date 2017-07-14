/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.fft;


import java.util.concurrent.ExecutorService;

import jnum.ExtraMath;
import jnum.math.Complex;
import jnum.parallel.ParallelProcessing;


// TODO: Auto-generated Javadoc
/**
/* Split radix (2 & 4) FFT algorithms. For example, see Numerical recipes,
 * and Chu, E: Computation Oriented Parallel FFT Algorithms (COPF)
 * 
 * @author Attila Kovacs <attila[AT]sigmyne.com>
 *
 */

public class ComplexFFT extends FFT1D<Complex[]> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9178097963072050931L;

    public ComplexFFT() {
        super();
    }
    
    public ComplexFFT(ExecutorService executor) {
        super(executor);
    }

    public ComplexFFT(ParallelProcessing processing) {
        super(processing);
    }
    
    
    @Override
    protected final void swap(final Complex[] data, final int i, final int j) {
        Complex temp = data[i]; data[i] = data[j]; data[j] = temp;
    }

    // 8/16-byte headers (32/64-bit) + 16 byte content... 
    // Assume 64-bit model...
    @Override
    protected final int getPointSize(Complex[] data) { return 32; }

    @Override
    protected final int getPoints(Complex[] data) { return data.length; }


    /**
     * Forward.
     *
     * @param data the data
     * @param nyquist the nyquist
     * @throws InterruptedException the interrupted exception
     */
    public void forward(final Complex[] data, final Complex nyquist) throws InterruptedException { complexForward(data); }

    /**
     * Back.
     *
     * @param data the data
     * @param nyquist the nyquist
     * @throws InterruptedException the interrupted exception
     */
    public void back(final Complex[] data, final Complex nyquist) throws InterruptedException { complexBack(data); }



    // Blockbit is the size of a merge block in bit shifts (e.g. size 2 is bit 1, size 4 is bit 2, etc.)
    // Two consecutive blocks are merged by the algorithm into one larger block...
    /**
     * Merge2.
     *
     * @param data the data
     * @param from the from
     * @param to the to
     * @param isForward the is forward
     * @param blkbit the blkbit
     */
    @Override
    protected void radix2(final Complex[] data, int from, int to, final boolean isForward, int blkbit) {	

        // The Complex[] block size
        final int blk = 1 << blkbit;
        final int blkmask = blk - 1;

        // make from and to compactified indices for i1 (0...N/2)
        from >>>= 1;
        to >>>= 1; 

        // convert to sparse indices for i1...
        from = ((from & ~blkmask) << 1) | (from & blkmask);
        to = ((to & ~blkmask) << 1) | (to & blkmask);

        // <------------------ Processing Block Starts Here ------------------------>
        // 
        // This one calculates the twiddle factors on the fly, using generators,
        // with precision readjustments as necessary.

        final double theta = (isForward ? Math.PI : -Math.PI) / blk;
        final Complex winc = new Complex(Math.cos(theta), Math.sin(theta));

        final Complex w = new Complex(1.0, 0.0);

        int m = from & blkmask;
        if(m != 0) w.setUnitVectorAt(m * theta);

        final Complex x = new Complex();

        final int clcmask = getTwiddleMask();

        for(int i1=from; i1<to; i1++) {
            // Skip over the odd blocks...
            // These are the i2 indices...
            if((i1 & blk) != 0) {
                i1 += blk;
                if(i1 >= to) break;

                // Reset the twiddle factors
                m = i1 & blkmask;
                if(m != 0) w.setUnitVectorAt(m * theta);
                else w.set(1.0, 0.0);
            }

            // To keep the twiddle precision under control
            // recalculate every now and then...
            if((i1 & clcmask) == 0) w.setUnitVectorAt(i1 * theta);			

            final Complex d1 = data[i1];
            final Complex d2 = data[i1 + blk];

            x.setProduct(w, d2);

            d2.setDifference(d1, x);

            // Increment the twiddle factors...
            w.multiplyBy(winc);

            // --------------------------------
            // i1
            d1.add(x);			


            if((i1 & yieldMask) == 0) Thread.yield();
        }
        // <------------------- Processing Block Ends Here ------------------------->
    }






    // Blockbit is the size of a merge block in bit shifts (e.g. size 2 is bit 1, size 4 is bit 2, etc.)
    // Four consecutive blocks are merged by the algorithm into one larger block...
    /**
     * Merge4.
     *
     * @param data the data
     * @param from the from
     * @param to the to
     * @param isForward the is forward
     * @param blkbit the blkbit
     */
    @Override
    protected void radix4(final Complex[] data, int from, int to, final boolean isForward, int blkbit) {	

        // The Complex[] block size
        final int blk = 1 << blkbit;
        final int skip = 3 * blk;
        final int blkmask = blk - 1;

        // make from and to compactified indices for i1 (0...N/4)
        from >>>= 2;
        to >>>= 2;

        // convert to sparse indices for i1...	
        from = ((from & ~blkmask) << 2) | (from & blkmask);
        to = ((to & ~blkmask) << 2) | (to & blkmask);

        // <------------------ Processing Block Starts Here ------------------------>
        // 
        // This one calculates the twiddle factors on the fly, using generators,
        // with precision readjustments as necessary.

        final double theta = (isForward ? Math.PI : -Math.PI) / (blk<<1);
        final Complex winc = new Complex(Math.cos(theta), Math.sin(theta));


        final Complex w1 = new Complex(1.0, 0.0); 

        int m = from & blkmask;
        if(m != 0) w1.setUnitVectorAt(m * theta);

        final Complex w2 = new Complex();
        final Complex w3 = new Complex();

        final Complex f1 = new Complex();
        final Complex f2 = new Complex();
        final Complex f3 = new Complex();


        final int clcmask = getTwiddleMask();

        for(int i0=from; i0<to; i0++) {
            // Skip over the 2nd, 3rd, and 4th blocks...
            if((i0 & skip) != 0) {
                i0 += skip;
                if(i0 >= to) break;

                // Reset the twiddle factors
                m = i0 & blkmask;
                if(m != 0) w1.setUnitVectorAt(m * theta);
                else w1.set(1.0, 0.0);
            }

            //->0:    f0 = F0

            final Complex f0 = data[i0];

            // To keep the twiddle precision under control
            // recalculate every now and then...

            if((i0 & clcmask) == 0) w1.setUnitVectorAt(i0 * theta);				

            w2.setProduct(w1, w1);		
            w3.setProduct(w1, w2);			

            final int i1 = i0 + blk;
            final int i2 = i1 + blk;
            final int i3 = i2 + blk;

            f2.setProduct(w2, data[i1]);
            f1.setProduct(w1, data[i2]);
            f3.setProduct(w3, data[i3]);

            // Increment the twiddle factors...
            w1.multiplyBy(winc);

            w2.setDifference(f0, f2);
            w3.setDifference(f1, f3);
            w3.multiplyByI();

            if(isForward) {
                data[i3].setDifference(w2, w3);
                data[i1].setSum(w2, w3);
            }
            else {
                data[i3].setSum(w2, w3);		
                data[i1].setDifference(w2, w3);
            }

            w2.setSum(f0, f2);
            w3.setSum(f1, f3);

            data[i2].setDifference(w2, w3);
            data[i0].setSum(w2, w3);

            if((i1 & yieldMask) == 0) Thread.yield();

        }
        // <------------------- Processing Block Ends Here ------------------------->

    }

    /**
     * To amplitudes.
     *
     * @param data the data
     */
    public void toAmplitudes(final Complex[] data) {
        complexTransform(data, true);
        scale(data, 2.0 / data.length);
    }	

    /**
     * From amplitudes.
     *
     * @param data the data
     */
    public void fromAmplitudes(final Complex[] data) { 
        complexTransform(data, false); 
    }


    /**
     * Scale.
     *
     * @param data the data
     * @param value the value
     * @param threads the threads
     */
    private void scale(final Complex[] data, final double value) {
        if(getParallel() < 2) {
            for(int i=data.length; --i >= 0; ) data[i].scale(value);
            return;
        }
        else new PointFork(data, data.length) {				
            @Override
            protected final void process(final Complex[] data, final int i) {
                data[i].scale(value); 
            }
        }.process();
    }

    // Rewritten to skip costly intermediate Complex storage...
    /* (non-Javadoc)
     * @see jnum.fft.FFT#averagePower(java.lang.Object, double[])
     */
    @Override
    public double[] averagePower(final Complex[] data, final double[] w) {
        final int windowSize = w.length;
        final int stepSize = windowSize >>> 1;

        final Complex[] block = new Complex[ExtraMath.pow2ceil(w.length)];
        for(int i=block.length; --i >= 0; ) block[i] = new Complex();

        final int nF = block.length >>> 1;

        // Create the accumulated spectrum array
        double[] spectrum = null;

        int start = 0, N = 0;
        while(start + windowSize <= data.length) {

            for(int i=windowSize; --i >= 0; ) block[i].setMultipleOf(data[i+start], w[i]);	
            for(int i=windowSize; i<block.length; i++) block[i].zero();

            complexTransform(block, FORWARD);

            if(spectrum == null) spectrum = new double[nF];

            for(int i=nF; --i>=0; ) spectrum[i] += block[i].asquare();

            start += stepSize;

            N++;
        }

        // Should not use amplitude normalization here but power...
        // The spectral power per frequency component.
        final double norm = 1.0 / N;

        for(int i=spectrum.length; --i >= 0; ) spectrum[i] *= norm;

        return spectrum;	
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#addressSizeOf(java.lang.Object)
     */
    @Override
    final int addressSizeOf(final Complex[] data) { return data.length; }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#getPadded(java.lang.Object, int)
     */
    @Override
    public Complex[] getPadded(final Complex[] data, final int n) {
        if(data.length == n) return data;

        final Complex[] padded = new Complex[n];
        final int N = Math.min(data.length, n);
        System.arraycopy(data, 0, padded, 0, N);
        for(int i=N; i<padded.length; i++) padded[i] = new Complex();

        return padded;
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#getMaxErrorBitsFor(java.lang.Object)
     */
    @Override
    protected int countFlops(Complex[] data) {
        int addressBits = getAddressBits(data);

        // radix-4: 6 ops per 4 cycle
        // radix-2: 4 ops per 2 cycle

        // 3 operations per twiddle cycle.
        // merge block size varies from 1 to N/2, so N/4 on average...
        int ops = 3 * Math.min(1<<(addressBits-2), getTwiddleMask());

        if((addressBits & 1) != 0) {
            ops += 4;
            addressBits--;
        }
        while(addressBits > 0) {
            ops += 6;
            addressBits -= 2;
        }

        return ops;
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#getMaxSignificantBits()
     */
    @Override
    final int getMaxSignificantBitsFor(Complex[] data) {
        return 53;	
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT1D#sizeOf(java.lang.Object)
     */
    @Override
    public int sizeOf(Complex[] data) {
        return data.length;
    }



    private final static int yieldMask = 0xFFF;

}




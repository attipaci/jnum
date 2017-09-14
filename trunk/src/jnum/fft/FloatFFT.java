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


import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.parallel.Parallelizable;


/**
/* Split radix (2 & 4) FFT algorithms. For example, see Numerical recipes,
 * and Chu, E: Computation Oriented Parallel FFT Algorithms (COPF)
 * 
 * @author Attila Kovacs <attila[AT]sigmyne.com>
 *
 */

public class FloatFFT extends FFT1D<float[]> implements RealFFT<float[]> {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3189956387053186573L;


    public FloatFFT() { super(); }
    
    public FloatFFT(ExecutorService executor) {
        super(executor);
    }

    public FloatFFT(Parallelizable processing) {
        super(processing);
    }

    @Override
    final void wipeUnused(final float[] data, int address) {
        Arrays.fill(data, address << 1, data.length, Float.NaN);
    }
   

    @Override
    final void swap(final float[] data, int i, int j) {
        i <<= 1;
        j <<= 1;
        
        float temp = data[i]; data[i] = data[j]; data[j] = temp;
        temp = data[++i]; data[i] = data[++j]; data[j] = temp;
    }
    
    @Override
    public int getPoints(float[] data) {
        return ExtraMath.pow2floor(data.length);
    }
    

    @Override
    protected final int getPointSize(float[] data) { return 4; }


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
    protected void radix2(final float[] data, int from, int to, final boolean isForward, final int blkbit) {	

        // The double[] block size
        final int blk = 1 << (blkbit+1);
        final int blkmask = blk - 1;

        // from/to already run 0 -- N/2, since they are complex indices...

        // convert to sparse indices for i1...		
        from = ((from & ~blkmask) << 1) | (from & blkmask);
        to = ((to & ~blkmask) << 1) | (to & blkmask);

        // <------------------ Processing Block Starts Here ------------------------>
        // 
        // This one calculates the twiddle factors on the fly, using generators,
        // with precision readjustments as necessary.

        final double theta = (isForward ? Constant.twoPi : -Constant.twoPi) / blk;

        final double s = Math.sin(theta);
        final double c = Math.cos(theta);

        int m = (from & blkmask) >>> 1;
        double r = m == 0 ? 1.0 : Math.cos(m * theta);
        double i = m == 0 ? 0.0 : Math.sin(m * theta);


        for(int i1=from; i1<to; i1+=2) {
            // Skip over the odd blocks...
            // These are the i2 indices...
            if((i1 & blk) != 0) {
                i1 += blk;
                if(i1 >= to) break;

                // Reset the twiddle factors...
                m = (i1 & blkmask) >>> 1;
                r = m == 0 ? 1.0 : Math.cos(m * theta);
                i = m == 0 ? 0.0 : Math.sin(m * theta);
            }


            final float fr = (float) r;
            final float fi = (float) i;

            final float d1r = data[i1];
            final float d1i = data[i1 | 1];

            // --------------------------------
            // i2

            final int i2 = i1 + blk;
            final float d2r = data[i2];
            final float d2i = data[i2 | 1];

            final float xr = fr * d2r - fi * d2i;
            final float xi = fr * d2i + fi * d2r;

            data[i2] = d1r - xr;
            data[i2 | 1] = d1i - xi;

            // Increment the twiddle factors...
            final double temp = r;
            r = temp * c - i * s;
            i = i * c + temp * s;

            // --------------------------------
            // i1

            data[i1] = d1r + xr;
            data[i1 | 1] = d1i + xi;	


            //if((i1 & yieldMask) == 0) Thread.yield();	
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
    protected void radix4(final float[] data, int from, int to, final boolean isForward, final int blkbit) {	

        // The double[] block size
        final int blk = 1 << (blkbit+1);
        final int skip = 3 * blk;
        final int blkmask = blk - 1;

        // make from and to compactified indices for i1 (0...N/4)
        from >>>= 1;
        to >>>= 1;

        // convert to sparse indices for i1...		
        from = ((from & ~blkmask) << 2) | (from & blkmask);
        to = ((to & ~blkmask) << 2) | (to & blkmask);

        // <------------------ Processing Block Starts Here ------------------------>
        // 
        // This one calculates the twiddle factors on the fly, using generators,
        // with precision readjustments as necessary.

        final double theta = (isForward ? Constant.twoPi : -Constant.twoPi) / (blk << 1);

        final double s = Math.sin(theta);
        final double c = Math.cos(theta);

        int m = (from & blkmask) >>> 1;
        double w1r = Math.cos(m * theta);
        double w1i = Math.sin(m * theta);


        for(int i0=from; i0<to; i0 += 2) {
            // Skip over the 2nd, 3rd, and 4th blocks...
            if((i0 & skip) != 0) {
                i0 += skip;
                if(i0 >= to) break;

                // Reset the twiddle factors
                m = (i0 & blkmask) >>> 1;
                w1r = Math.cos(m * theta);
                w1i = Math.sin(m * theta);
            }

            //->0:    f0 = F0

            final float f0r = data[i0];
            final float f0i = data[i0 | 1];

            final float fw1r = (float) w1r;
            final float fw1i = (float) w1i;

            float fw2r = fw1r * fw1r - fw1i * fw1i;
            float fw2i = 2.0F * fw1r * fw1i;

            float fw3r = fw1r * fw2r - fw1i * fw2i;
            float fw3i = fw1r * fw2i + fw1i * fw2r;

            // Increment the twiddle factors...
            final double temp = w1r;
            w1r = temp * c - w1i * s;
            w1i = w1i * c + temp * s;

            final int i1 = i0 + blk;
            final int i2 = i1 + blk;
            final int i3 = i2 + blk;

            float dr = data[i1];
            float di = data[i1 | 1];
            final float f2r = fw2r * dr - fw2i * di;
            final float f2i = fw2r * di + fw2i * dr;

            dr = data[i2];
            di = data[i2 | 1];
            final float f1r = fw1r * dr - fw1i * di;
            final float f1i = fw1r * di + fw1i * dr;

            dr = data[i3];
            di = data[i3 | 1];
            final float f3r = fw3r * dr - fw3i * di;
            final float f3i = fw3r * di + fw3i * dr;


            fw2r = f0r - f2r;
            fw2i = f0i - f2i;

            fw3r = f1r - f3r;
            fw3i = f1i - f3i;

            if(isForward) {
                data[i3] = fw2r + fw3i;
                data[i3 | 1] = fw2i - fw3r;

                data[i1] = fw2r - fw3i;
                data[i1 | 1] = fw2i + fw3r;
            }
            else {
                data[i3] = fw2r - fw3i;
                data[i3 | 1] = fw2i + fw3r;

                data[i1] = fw2r + fw3i;
                data[i1 | 1] = fw2i - fw3r;
            }

            fw2r = f0r + f2r;
            fw2i = f0i + f2i;

            fw3r = f1r + f3r;
            fw3i = f1i + f3i;

            data[i2] = fw2r - fw3r;
            data[i2+1] = fw2i - fw3i;

            data[i0] = fw2r + fw3r;
            data[i0+1] = fw2i + fw3i;


            //if((i0 & yieldMask) == 0) Thread.yield();
        }
        // <------------------- Processing Block Ends Here ------------------------->


    }

    /**
     * Load real.
     *
     * @param data the data
     * @param length the length
     * @param from the from
     * @param to the to
     * @param isForward the is forward
     */
    private void loadReal(final float[] data, final int addressBits, int from, int to, final boolean isForward) {	
        final int length = 2<<addressBits;
        // Make from and to even indices 0...N/2
        from = Math.max(2, (from >>> 2) << 1);
        to = (to >>> 2) << 1;

        final double theta = (isForward ? Constant.twoPi : -Constant.twoPi) / length;
        final double s = Math.sin(theta);
        final double c = Math.cos(theta);

        final float sh = isForward ? 0.5F : -0.5F;

        double a = (from>>>1) * theta;
        double wr = from == 2 ? c : Math.cos(a);
        double wi = from == 2 ? s : Math.sin(a);

        for(int r1=from, r2=length-from; r1<to; r1+=2, r2-=2) {
            final int i1 = r1 | 1;
            final int i2 = r2 | 1;

            float hr = sh * (data[i1] + data[i2]);
            float hi = sh * (data[r2] - data[r1]);

            final float fwr = (float) wr;
            final float fwi = (float) wi;

            final float r = fwr * hr - fwi * hi;
            final float i = fwr * hi + fwi * hr;

            hr = 0.5F * (data[r1] + data[r2]);
            hi = 0.5F * (data[i1] - data[i2]);

            data[r1] = hr + r;
            data[i1] = hi + i;
            data[r2] = hr - r;
            data[i2] = i - hi;

            final double temp = wr;
            wr = temp * c - wi * s;
            wi = wi * c + temp * s;

            //if((r1 & yieldMask) == 0) Thread.yield();
        }				
    }



    /* (non-Javadoc)
     * @see kovacs.fft.RealFFT#realTransform(java.lang.Object, boolean, int)
     */
    @Override
    public void realTransform(final float[] data, final boolean isForward) {
        realTransform(data, getAddressBits(data), isForward);
    }

    /**
     * Real transform.
     *
     * @param data the data
     * @param addressBits the address bits
     * @param isForward the is forward
     * @param chunks the chunks
     */
    void realTransform(final float[] data, final int addressBits, final boolean isForward) {
        if(getParallel() < 2) sequentialRealTransform(data, addressBits, isForward);
        else parallelRealTransform(data, addressBits, isForward);
    }
    
    void parallelRealTransform(final float[] data, final int addressBits, final boolean isForward) {
        if(isForward) parallelComplexTransform(data, addressBits, FORWARD);

        new BlockFork(data, 2<<addressBits) {
            @Override
            public void process(final float[] data, final int from, final int to) { loadReal(data, addressBits, from, to, isForward); }
        }.process();

        final float d0 = data[0];

        if(isForward) {
            data[0] = d0 + data[1];
            data[1] = d0 - data[1];
        } 
        else {
            data[0] = 0.5F * (d0 + data[1]);
            data[1] = 0.5F * (d0 - data[1]);
            parallelComplexTransform(data, addressBits, BACK);
        }
    }

    /**
     * Sequential real transform.
     *
     * @param data the data
     * @param isForward the is forward
     */
    @Override
    public void sequentialRealTransform(final float[] data, final boolean isForward) {

    }

    /**
     * Sequential real transform.
     *
     * @param data the data
     * @param addressBits the address bits
     * @param isForward the is forward
     */
    void sequentialRealTransform(final float[] data, final int addressBits, final boolean isForward) {
        if(isForward) sequentialComplexTransform(data, addressBits, FORWARD);

        final int n = 2<<addressBits;
        loadReal(data, addressBits, 0, n, isForward);

        final float d0 = data[0];

        if(isForward) {
            data[0] = d0 + data[1];
            data[1] = d0 - data[1];
        } 
        else {
            data[0] = 0.5F * (d0 + data[1]);
            data[1] = 0.5F * (d0 - data[1]);
            sequentialComplexTransform(data, addressBits, BACK);
        }
    }


    /**
     * Scale.
     *
     * @param data the data
     * @param length the length
     * @param value the value
     * @param threads the threads
     */
    protected void scale(final float[] data, final int length, final float value) {
        if(getParallel() < 2) {
            for(int i=length; --i >= 0; ) data[i] *= value;
            return;
        }
        new PointFork(data, length) {					
                @Override
                protected final void process(final float[] data, final int i) {
                    data[i] *= value; 
                }
        }.process();
    }


    /* (non-Javadoc)
     * @see jnum.fft.RealFFT#real2Amplitude(java.lang.Object)
     */	
    @Override
    public void real2Amplitude(final float[] data) {
        final int addressBits = getAddressBits(data);
        final int n = 2 << addressBits;
        realTransform(data, addressBits, true);
        scale(data, n, 2.0F / n);
    }


    /* (non-Javadoc)
     * @see jnum.fft.RealFFT#amplitude2Real(java.lang.Object)
     */
    @Override
    public void amplitude2Real(final float[] data) { 
        realTransform(data, false); 
    }


    // Rewritten to skip costly intermediate Complex storage...
    /* (non-Javadoc)
     * @see jnum.fft.FFT#averagePower(java.lang.Object, double[])
     */
    @Override
    public double[] averagePower(float[] data, final double[] w) {
        int windowSize = w.length;
        int stepSize = windowSize >>> 1;
        final float[] block = new float[ExtraMath.pow2ceil(w.length)];
        final int nF = block.length >>> 1;

        // Create the accumulated spectrum array
        double[] spectrum = null;

        int start = 0, N = 0;
        while(start + windowSize <= data.length) {

            for(int i=windowSize; --i >= 0; ) block[i] = (float) w[i] * data[i+start];	
            Arrays.fill(block, windowSize, block.length, 0.0F);
            realTransform(block, FORWARD);

            if(spectrum == null) spectrum = new double[nF+1];

            spectrum[0] += block[0] * block[0];
            spectrum[nF] += block[1] * block[1];

            for(int i=nF, j=nF<<1; --i>=1; ) {
                spectrum[i] += block[--j] * block[j];
                spectrum[i] += block[--j] * block[j];
            }

            start += stepSize;

            N++;
        }

        // Should not use amplitude normalization here but power...
        // The spectral power per frequency component.
        final double norm = 1.0 / N;

        if(spectrum != null) for(int i=spectrum.length; --i >= 0; ) spectrum[i] *= norm;

        return spectrum;	
    }


    /* (non-Javadoc)
     * @see jnum.fft.FFT#addressSizeOf(java.lang.Object)
     */
    @Override
    final int addressSizeOf(final float[] data) { return getPoints(data) >>> 1; }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#getPadded(java.lang.Object, int)
     */
    @Override
    public float[] getPadded(final float[] data, final int n) {
        if(data.length == n) return data;

        final float[] padded = new float[n];
        final int N = Math.min(data.length, n);
        System.arraycopy(data, 0, padded, 0, N);

        return padded;
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT#getMaxErrorBitsFor(java.lang.Object)
     */
    @Override
    protected int countFlops(float[] data) {
        int addressBits = getAddressBits(data);

        // radix-4: 6 ops per 4 cycle
        // radix-2: 4 ops per 2 cycle

        int ops = 0;

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
    final int getMaxSignificantBitsFor(float[] data) {
        return 24;	
    }

    /* (non-Javadoc)
     * @see jnum.fft.FFT1D#sizeOf(java.lang.Object)
     */
    @Override
    public int sizeOf(float[] data) {
        return data.length;
    }


    /**
     * The Class NyquistUnrolledRealFT.
     */
    public static class NyquistUnrolledReal extends FloatFFT {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 3073121404450602358L;

        public NyquistUnrolledReal() {
            super();
        }
        
        public NyquistUnrolledReal(ExecutorService executor) {
            super(executor);
        }

        public NyquistUnrolledReal(Parallelizable processing) {
            super(processing);
        }
        
        
        @Override
        public int getPoints(float[] data) {
            return ExtraMath.pow2floor(data.length - 2);
        }
        
       
        /* (non-Javadoc)
         * @see jnum.fft.FloatFFT#realTransform(float[], int, boolean, int)
         */
        @Override
        void parallelRealTransform(final float[] data, final int addressBits, final boolean isForward) {
            if(getParallel() < 2)  {
                sequentialRealTransform(data, addressBits, isForward);
                return;
            }
            
            final int n = 2<<addressBits;

            if(!isForward) {
                data[1] = data[n];
                data[n] = data[n+1] = 0.0F;
            }

            super.parallelRealTransform(data, addressBits, isForward);

            if(isForward) {
                data[n] = data[1];
                data[1] = data[n+1] = 0.0F;
            }
        }

        /* (non-Javadoc)
         * @see jnum.fft.FloatFFT#sequentialRealTransform(float[], int, boolean)
         */
        @Override
        void sequentialRealTransform(final float[] data, final int addressBits, final boolean isForward) {
            final int n = 2<<addressBits;

            if(!isForward) {
                data[1] = data[n];
                data[n] = data[n+1] = 0.0F;
            }

            super.sequentialRealTransform(data, addressBits, isForward);

            if(isForward) {
                data[n] = data[1];
                data[1] = data[n+1] = 0.0F;
            }
        }
        
        @Override
        protected final void scale(final float[] data, final int length, final float value) {
            super.scale(data, length+2, value);
        }

        
    }



}




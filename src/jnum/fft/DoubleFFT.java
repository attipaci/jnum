/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
package jnum.fft;


import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.parallel.Parallelizable;


/**
 * Split radix (2 and 4) FFT for double-precision real-valued data with 2<sup>n</sup> elements. For example, see Numerical recipes,
 * and Chu, E: Computation Oriented Parallel FFT Algorithms (COPF)
 * 
 * @author Attila Kovacs
 *
 */

public class DoubleFFT extends FFT1D<double[]> implements RealFFT<double[]> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1904464241037659389L;

    /**
     * Intantiates an FFT for <code>double[]</code> arrays.
     */
    public DoubleFFT() { super(); }
    
    /**
     * Intantiates an FFT for <code>double[]</code> arrays, using the specified executor for parallel processing.
     * 
     * @param executor  the executor service to use for parallel processing.
     */
    public DoubleFFT(ExecutorService executor) {
        super(executor);
    }

    /**
     * Intantiates an FFT for <code>double[]</code> arrays, using the specified jnum parallel processing environment.
     * 
     * @param processing    the jnum parallel processing environment.
     */
    public DoubleFFT(Parallelizable processing) {
        super(processing);
    }
    
    @Override
    final void swap(final double[] data, int i, int j) {
        i <<= 1;
        j <<= 1;
        
        double temp = data[i]; data[i] = data[j]; data[j] = temp;
        temp = data[++i]; data[i] = data[++j]; data[j] = temp;
    }
    
    @Override
    final void discardFrom(final double[] data, int address) {
        Arrays.fill(data, address << 1, data.length, Double.NaN);
    }

    // 8 (2^3) bytes per value...
    @Override
    protected final int getPointSize(double[] data) { return 8; }

    @Override
    public int getPoints(double[] data) {
        return Integer.highestOneBit(data.length);
    }
    
    @Override
    protected int getTwiddleMask() {
        return (super.getTwiddleMask() << 1);
    }

    
    /**
     * Performs a radix-2 iteration on a section of the supplied data.
     *
     * @param data      The data to be transformed.
     * @param from      the data section starting index for the radix-2 processing.
     * @param to        the data section ending index (exclusive) for the radix-2 processing
     * @param isForward <code>true</code> if it is a forward transform, otherwise <code>false</code>
     * @param blkbit    the size of a merge block in bit shifts (e.g. size 2 is bit 1, size 4 is bit 2, etc.)
     *                  four consecutive blocks are merged by the algorithm into one larger block...
     */
    @Override
    protected void radix2(final double[] data, int from, int to, final boolean isForward, final int blkbit) {	

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

        final int clcmask = getTwiddleMask();

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

            // To keep the twiddle precision under control
            // recalculate every now and then...
            if((i1 & clcmask) == 0) {
                final double a = (i1 >>> 1) * theta;
                r = Math.cos(a);
                i = Math.sin(a);				
            }

            final double d1r = data[i1];
            final double d1i = data[i1 | 1];

            // --------------------------------
            // i2

            final int i2 = i1 + blk;
            final double d2r = data[i2];
            final double d2i = data[i2 | 1];

            final double xr = r * d2r - i * d2i;
            final double xi = r * d2i + i * d2r;

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

        }
        // <------------------- Processing Block Ends Here ------------------------->
    }



    /**
     * Performs a radix-4 iteration on a section of the supplied data.
     *
     * @param data      The data to be transformed.
     * @param from      the data section starting index for the radix-4 processing.
     * @param to        the data section ending index (exclusive) for the radix-4 processing
     * @param isForward <code>true</code> if it is a forward transform, otherwise <code>false</code>
     * @param blkbit    the size of a merge block in bit shifts (e.g. size 2 is bit 1, size 4 is bit 2, etc.)
     *                  four consecutive blocks are merged by the algorithm into one larger block...
     */
    @Override
    protected void radix4(final double[] data, int from, int to, final boolean isForward, final int blkbit) {	
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

        final int clcmask = getTwiddleMask();

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

            final double f0r = data[i0];
            final double f0i = data[i0 | 1];

            // To keep the twiddle precision under control
            // recalculate every now and then...

            if((i0 & clcmask) == 0) {
                final double a = (i0 >>> 1) * theta;
                w1r = Math.cos(a);
                w1i = Math.sin(a);				
            }

            double w2r = w1r * w1r - w1i * w1i;
            double w2i = 2.0 * w1r * w1i;

            double w3r = w1r * w2r - w1i * w2i;
            double w3i = w1r * w2i + w1i * w2r;


            final int i1 = i0 + blk;
            final int i2 = i1 + blk;
            final int i3 = i2 + blk;

            double dr = data[i1];
            double di = data[i1 | 1];
            final double f2r = w2r * dr - w2i * di;
            final double f2i = w2r * di + w2i * dr;

            dr = data[i2];
            di = data[i2 | 1];
            final double f1r = w1r * dr - w1i * di;
            final double f1i = w1r * di + w1i * dr;

            dr = data[i3];
            di = data[i3 | 1];
            final double f3r = w3r * dr - w3i * di;
            final double f3i = w3r * di + w3i * dr;

            // Increment the twiddle factors...
            final double temp = w1r;
            w1r = temp * c - w1i * s;
            w1i = w1i * c + temp * s;


            w2r = f0r - f2r;
            w2i = f0i - f2i;

            w3r = f1r - f3r;
            w3i = f1i - f3i;


            if(isForward) {
                data[i3] = w2r + w3i;
                data[i3 | 1] = w2i - w3r;

                data[i1] = w2r - w3i;
                data[i1 | 1] = w2i + w3r;
            }
            else {
                data[i3] = w2r - w3i;
                data[i3 | 1] = w2i + w3r;

                data[i1] = w2r + w3i;
                data[i1 | 1] = w2i - w3r;
            }

            w2r = f0r + f2r;
            w2i = f0i + f2i;

            w3r = f1r + f3r;
            w3i = f1i + f3i;

            data[i2] = w2r - w3r;
            data[i2 | 1] = w2i - w3i;

            data[i0] = w2r + w3r;
            data[i0 | 1] = w2i + w3i;

        }
        // <------------------- Processing Block Ends Here ------------------------->

    }

    /**
     * Loads the real data of N elements for a subsequent complex tranform of N/2 complex numbers.
     * 
     * 
     * @param data              the data to transform
     * @param addressBits       log<sub>2</sub> of the number of complex data elements (half of the number of real 
     *                          values to transform.
     * @param from              starting index of elements to process
     * @param to                ending (exclusive) index of elements to process.
     * @param isForward         if this is for a forward transform.
     */
    private void loadReal(final double[] data, final int addressBits, int from, int to, final boolean isForward) {
        int length = 2<<addressBits;
        to = Math.min(to, length);

        // Make from and to even indices 0...N/2
        from = Math.max(2, (from >>> 2) << 1);
        to = (to >>> 2) << 1;

        final double theta = (isForward ? Constant.twoPi : -Constant.twoPi) / length;
        final double s = Math.sin(theta);
        final double c = Math.cos(theta);

        final double sh = isForward ? 0.5 : -0.5;
        final int clcmask = getTwiddleMask();

        double a = (from>>>1) * theta;
        double wr = from == 2 ? c : Math.cos(a);
        double wi = from == 2 ? s : Math.sin(a);

        for(int r1=from, r2=length-from; r1<to; r1+=2, r2-=2) {
            final int i1 = r1 | 1;
            final int i2 = r2 | 1;

            double hr = sh * (data[i1] + data[i2]);
            double hi = sh * (data[r2] - data[r1]);

            // Recalculate the twiddle factors as needed to keep the precision under control...
            if((r1 & clcmask) == 0) {
                a = (r1 >>> 1) * theta;
                wr = Math.cos(a);
                wi = Math.sin(a);				
            }

            final double r = wr * hr - wi * hi;
            final double i = wr * hi + wi * hr;

            hr = 0.5 * (data[r1] + data[r2]);
            hi = 0.5 * (data[i1] - data[i2]);

            data[r1] = hr + r;
            data[i1] = hi + i;
            data[r2] = hr - r;
            data[i2] = i - hi;

            final double temp = wr;
            wr = temp * c - wi * s;
            wi = wi * c + temp * s;

        }				
    }

    
    @Override
    public final void realTransform(final double data[], final boolean isForward) {
        realTransform(data, getAddressBits(data), isForward);
    }


    /**
     * Real transform (multi-threaded).
     *
     * @param data          the data to transform
     * @param addressBits   the number address bits in use for data elements (log<sub>2</sub> of the number
     *                      of complex elements)
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    final void realTransform(final double[] data, final int addressBits, final boolean isForward) {	
        if(getParallel() < 2) sequentialRealTransform(data, addressBits, isForward);
        else parallelRealTransform(data, addressBits, isForward);
    }
     
    /**
     * Forward real transform (multi-threaded)
     * 
     * @param data          the data to transform
     * @param addressBits   the number address bits in use for data elements (log<sub>2</sub> of the number
     *                      of complex elements)
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    void parallelRealTransform(final double[] data, final int addressBits, final boolean isForward) {
        if(isForward) parallelComplexTransform(data, addressBits, FORWARD);

        new BlockFork(data, 2<<addressBits) {
            @Override
            public void processBlock(double[] data, int from, int to) { loadReal(data, addressBits, from, to, isForward); }
        }.process();

        final double d0 = data[0];

        if(isForward) {
            data[0] = d0 + data[1];
            data[1] = d0 - data[1];
        } 
        else {
            data[0] = 0.5 * (d0 + data[1]);
            data[1] = 0.5 * (d0 - data[1]);
            parallelComplexTransform(data, addressBits, BACK);
        }

    }

    @Override
    public final void sequentialRealTransform(final double[] data, final boolean isForward) {
        sequentialRealTransform(data, getAddressBits(data), isForward);
    }


    /**
     * Sequential real transform in a single thread. 
     *
     * @param data          the data to transform
     * @param addressBits   the number address bits in use for data elements
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    void sequentialRealTransform(final double[] data, final int addressBits, final boolean isForward) {
        if(isForward) sequentialComplexTransform(data, addressBits, FORWARD); 

        loadReal(data, addressBits, 0, 2<<addressBits, isForward);

        final double d0 = data[0];

        if(isForward) {
            data[0] = d0 + data[1];
            data[1] = d0 - data[1];
        } 
        else {
            data[0] = 0.5 * (d0 + data[1]);
            data[1] = 0.5 * (d0 - data[1]);
            sequentialComplexTransform(data, addressBits, BACK);
        }
    }


    /**
     * Scales a number of element in an array by a specified factor.
     *
     * @param data      the data to scale 
     * @param length    the number of elements to scale in data.
     * @param value     the scaling factor
     */
    protected void scale(final double[] data, final int length, final double value) {
        if(getParallel() < 2) {
            for(int i=length; --i >= 0; ) data[i] *= value;
            return;
        }
        new PointFork(data, length) {			
            @Override
            protected final void process(final double[] data, final int i) {
                data[i] *= value; 
            }
        }.process();
        
    }

    @Override
    public void real2Amplitude(final double[] data) {
        final int addressBits = getAddressBits(data);
        final int n = 2 << addressBits;
        realTransform(data, addressBits, FFT.FORWARD);
        scale(data, n, 2.0F / n);
    }

 
    @Override
    public void amplitude2Real(final double[] spectrum) { 
        realTransform(spectrum, FFT.BACK); 
    }

    @Override
    public double[] averagePower(final double[] data, final double[] w) {
        final int windowSize = w.length;
        final int stepSize = windowSize >>> 1;
        final double[] block = new double[ExtraMath.pow2ceil(w.length)];
        final int nF = block.length >>> 1;

        // Create the accumulated spectrum array
        double[] spectrum = null;

        int start = 0, N = 0;

        while(start + windowSize <= data.length) {

            for(int i=windowSize; --i >= 0; ) block[i] = w[i] * data[i+start];	
            Arrays.fill(block, windowSize, block.length, 0.0);
            realTransform(block, FORWARD);

            if(spectrum == null) spectrum = new double[nF+1];

            // Nyquist may be stored either NR style or in separate Nyquist value.
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

    @Override
    final int addressSizeOf(final double[] data) { return getPoints(data) >>> 1; }

    @Override
    public double[] getPadded(final double[] data, final int n) {
        if(data.length == n) return data;

        final double[] padded = new double[n];
        int N = Math.min(data.length, n);
        System.arraycopy(data, 0, padded, 0, N);

        return padded;
    }

    @Override
    protected int countFlops(double[] data) {
        int addressBits = getAddressBits(data);

        // radix-4: 6 ops per 4 cycle
        // radix-2: 4 ops per 2 cycle

        // 3 operations per twiddle cycle.
        // merge block size varies from 1 to N/2, so N/4 on average...
        int ops = 3 * Math.min(1<<(addressBits-2), getTwiddleMask()) >>> 1;

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

    @Override
    final int getMaxSignificantBitsFor(double[] data) {
        return 53;	
    }

    @Override
    public int sizeOf(double[] data) {
        return data.length;
    }

    /**
     * <p>
     * An FFT that operates on <code>double[]</code> arrays with 2<sup>n</sup>+2 elements. The extra pair of real values
     * at the end are used to store the amplitude at the Nyquist frequency <i>f</i><sub>N</sub> itself. 
     * This may be useful to alleviate
     * some confusion with the Numerical Recipes style packed real FFTs that operate on 2<sup>n</sup>
     * elements (or 2<sup>n-1</sup> complex amplitudes), since those routines pack the real valued <i>f</i><sub>0</sub> and
     * <i>f</i><sub>N</sub> amplitudes into the real and imaginary parts of the 'complex' amplitude at index 0. 
     * Thus routines that aren't careful to treat the first complex amplitude as different from the rest, or which
     * do not account for the fact the that Nyquist amplitude is stored as the imaginary part of the packed <i>f</i><sub>0</sub>
     * component, will end up with a garbage amplitude for <i>f</i><sub>0</sub>, and no amplitude for <i>f</i><sub>N</sub>. 
     * By unrolling the real amplitude of <i>f</i><sub>N</sub> into its own separate 'complex' bin at the end of the 
     * transformed array, the amplitude data have a more regular structure, that lends itself to proper processing more
     * easily.
     * </p>
     * 
     * <p>
     * <b>Tip:</b> The unrolled amplitudes can be most easily accessed and processed by wrapping the data into
     * a {@link jnum.data.FauxComplexArray.Double}, which exposes the real-valued amplitude pairs of the original data array
     * as {@link jnum.math.Complex} values.
     * </p>
     * 
     * @author Attila Kovacs
     * 
     * @see jnum.data.FauxComplexArray.Double
     *
     */
    public static class NyquistUnrolled extends DoubleFFT {

        /**
         * 
         */
        private static final long serialVersionUID = -896227818374947414L;

        /**
         * Instantiates a new FFT for 2<sup>n</sup>+2 real data arrays (containing N = 2<sup>2</sup> real values)
         * in which the Nyquist amplitudes are unrolled into the extra pair elements at the end, thus
         * yielding a regular array with complex amplitudes from f<sub>0</sub> to and inclusing f<sub>N/2</sub>.
         * 
         */
        public NyquistUnrolled() {
            super();
        }

        /**
         * Instantiates a new FFT, using the specified executor service, for 2<sup>n</sup>+2 real data arrays (containing N = 2<sup>2</sup> real values)
         * in which the Nyquist amplitudes are unrolled into the extra pair elements at the end, thus
         * yielding a regular array with complex amplitudes from f<sub>0</sub> to and inclusing f<sub>N/2</sub>.
         * 
         * 
         * @param executor  the executor service to use for parallel processing
         */
        public NyquistUnrolled(ExecutorService executor) {
            super(executor);
        }

        /**
         * Instantiates a new FFT, using the specified jnum parallel enviroment, for 2<sup>n</sup>+2 real data arrays (containing N = 2<sup>2</sup> real values)
         * in which the Nyquist amplitudes are unrolled into the extra pair elements at the end, thus
         * yielding a regular array with complex amplitudes from f<sub>0</sub> to and inclusing f<sub>N/2</sub>.
         * 
         * 
         * @param processing    the jnum parallel processor.
         */
        public NyquistUnrolled(Parallelizable processing) {
            super(processing);
        }
        
        @Override
        public int getPoints(double[] data) {
            return Integer.highestOneBit(data.length - 2);
        }
        
        @Override
        void parallelRealTransform(final double[] data, final int addressBits, final boolean isForward) {   
            final int n = 2<<addressBits;

            if(!isForward) {
                data[1] = data[n];
                data[n] = data[n+1] = 0.0;
            }

            super.parallelRealTransform(data, addressBits, isForward);
            
            if(isForward) {	
                data[n] = data[1];
                data[1] = data[n+1] = 0.0;
            }
        }

 
        @Override
        void sequentialRealTransform(final double[] data, final int addressBits, final boolean isForward) {
            final int n = 2<<addressBits;

            if(!isForward) {
                data[1] = data[n];
                data[n] = data[n+1] = 0.0;
            }

            super.sequentialRealTransform(data, addressBits, isForward);

            if(isForward) {
                data[n] = data[1];
                data[1] = data[n+1] = 0.0;
            }
        }
        
        

        @Override
        protected final void scale(final double[] data, final int length, final double value) {
            super.scale(data, length+2, value);
        }

    }



}




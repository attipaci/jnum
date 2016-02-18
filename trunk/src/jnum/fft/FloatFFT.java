/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.fft;


import java.util.Arrays;

import jnum.Constant;
import jnum.ExtraMath;

// TODO: Auto-generated Javadoc
/**
/* Split radix (2 & 4) FFT algorithms. For example, see Numerical recipes,
 * and Chu, E: Computation Oriented Parallel FFT Algorithms (COPF)
 * 
 * @author Attila Kovacs <attila@submm.caltech.edu>
 *
 */

public class FloatFFT extends FFT1D<float[]> implements RealFFT<float[]> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3189956387053186573L;


	/**
	 * Bit reverse.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	@Override
	protected void bitReverse(final float[] data, int from, int to) {
		// Change from complex indices to float[] indices.
		from <<= 1;
		to <<= 1;
		
		// The number of bits flipped is +1 on either side of the address sequence
		// due to the float[] packing...
		final int addressBits = getAddressBits(data) + 2;
		for(int i=from; i<to; i++) {
			int j = bitReverse(i, addressBits);
			if(j > i) {	
				float temp = data[i]; data[i] = data[j]; data[j] = temp;
				temp = data[++j]; data[j] = data[++i]; data[i] = temp;
			}
			else i++;
		}
	}
	

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
	protected void merge2(final float[] data, int from, int to, final boolean isForward, int blkbit) {	
		// Change from abstract index to double[] storage index (x2)
		blkbit++; 
	
		// The double[] block size
		final int blk = 1 << blkbit;
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
		
		final int m = (from & blkmask) >> 1;
		double r = m == 0 ? 1.0 : Math.cos(m * theta);
		double i = m == 0 ? 0.0 : Math.sin(m * theta);

	
		for(int i1=from; i1<to; i1+=2) {
			// Skip over the odd blocks...
			// These are the i2 indices...
			if((i1 & blk) != 0) {
				i1 += blk;
				if(i1 >= to) break;
				r = 1.0;
				i = 0.0;
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
	protected void merge4(final float[] data, int from, int to, final boolean isForward, int blkbit) {	
		// Change from abstract index to double[] storage index (x2)
		blkbit++; 

		// The double[] block size
		final int blk = 1 << blkbit;
		final int skip = 3 * blk;
		final int blkmask = blk - 1;

		// make from and to compactified indices for i1 (0...N/4)
		from >>= 1;
		to >>= 1;
		
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
		
		final int m = (from & blkmask) >> 1;
		double w1r = Math.cos(m * theta);
		double w1i = Math.sin(m * theta);
		
			
		for(int i0=from; i0<to; i0 += 2) {
			// Skip over the 2nd, 3rd, and 4th blocks...
			if((i0 & skip) != 0) {
				i0 += skip;
				if(i0 >= to) break;
				w1r = 1.0;
				w1i = 0.0;
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
			
			
			
		}
		// <------------------- Processing Block Ends Here ------------------------->


	}
	
	/**
	 * Load real.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param isForward the is forward
	 */
	private void loadReal(final float[] data, final int length, int from, int to, final boolean isForward) {	
		// Make from and to even indices 0...N/2
		from = Math.max(2, (from >> 2) << 1);
		to = (to >> 2) << 1;
		
		final double theta = (isForward ? Constant.twoPi : -Constant.twoPi) / length;
		final double s = Math.sin(theta);
		final double c = Math.cos(theta);
		
		final float sh = isForward ? 0.5F : -0.5F;
				
		double a = (from>>1) * theta;
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
			
		}				
	}


	/* (non-Javadoc)
	 * @see kovacs.util.fft.RealFFT#realTransform(java.lang.Object, boolean)
	 */
	@Override
	public final void realTransform(final float data[], final boolean isForward) {
		realTransform(data, getAddressBits(data), isForward);
	}
	
	final void realTransform(final float data[], final int addressBits, final boolean isForward) {	
		updateThreads(data);
		
		// Don't make more chunks than there are processing blocks...
		int chunks = Math.min(getChunks(), 1<<(addressBits-1));
		
		if(chunks == 1) sequentialRealTransform(data, addressBits, isForward);
		else realTransform(data, addressBits, isForward, chunks);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.fft.RealFFT#realTransform(java.lang.Object, boolean, int)
	 */
	@Override
	public void realTransform(final float[] data, final boolean isForward, int chunks) {
		realTransform(data, getAddressBits(data), isForward, chunks);
	}

	void realTransform(final float[] data, final int addressBits, final boolean isForward, int chunks) {
		
		if(isForward) complexTransform(data, addressBits, FORWARD, chunks);
		
		final int n = 2<<addressBits;
		
		class RealLoader extends Task {
			public RealLoader(final float[] data, final int from, final int to) { super(data, from, to); }
			@Override
			public void process(final float[] data, final int from, final int to) { loadReal(data, n, from, to, isForward); }
		}
		
		final Queue queue = new Queue();
		final double dn = (double) n / chunks;
		
		for(int k=chunks; --k >= 0; ) {
			final int from = (int)Math.round(k * dn);
			final int to = (int)Math.round((k+1) * dn);
			queue.add(new RealLoader(data, from, to));
		}
		queue.process();
		
		final float d0 = data[0];

		if(isForward) {
			data[0] = d0 + data[1];
			data[1] = d0 - data[1];
		} 
		else {
			data[0] = 0.5F * (d0 + data[1]);
			data[1] = 0.5F * (d0 - data[1]);
			complexTransform(data, addressBits, BACK, chunks);
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
	
	void sequentialRealTransform(final float[] data, final int addressBits, final boolean isForward) {
		if(isForward) sequentialComplexTransform(data, addressBits, FORWARD);

		final int n = 2<<addressBits;
		loadReal(data, n, 0, n, isForward);
		
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
	 * @param value the value
	 * @param threads the threads
	 */
	private void scale(final float[] data, final int length, final float value, int threads) {
		if(threads < 2) {
			for(int i=length; --i >= 0; ) data[i] *= value;
			return;
		}
		else {				
			class Scaler extends Task {		
				private float value;
				public Scaler(final float[] data, final int from, final int to, final float value) { 
					super(data, from, to); 
					this.value = value;
				}			
				@Override
				public void process(final float[] data, final int from, final int to) {
					for(int i=from; i<to; i++) data[i] *= value; 
				}
			}
			
			final Queue queue = new Queue();
			double dn = (double) length / threads;
			
			for(int k=threads; --k >= 0; ) {
				final int from = (int)Math.round(k * dn);
				final int to = (int)Math.round((k+1) * dn);
				queue.add(new Scaler(data, from, to, value));
			}
			queue.process();
		}
	}
	

	/* (non-Javadoc)
	 * @see kovacs.util.fft.RealFFT#real2Amplitude(java.lang.Object)
	 */	
	@Override
	public void real2Amplitude(final float[] data) {
		final int addressBits = getAddressBits(data);
		realTransform(data, addressBits, true);
		scale(data, getFFTLength(addressBits), 2.0F / (2<<addressBits), getChunks());
	}
	
	
	protected int getFFTLength(final int addressBits) {
		return 2<<addressBits;
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.fft.RealFFT#amplitude2Real(java.lang.Object)
	 */
	@Override
	public void amplitude2Real(final float[] data) { 
		realTransform(data, false); 
	}

	
	
	// Rewritten to skip costly intermediate Complex storage...
	/* (non-Javadoc)
	 * @see kovacs.util.fft.FFT#averagePower(java.lang.Object, double[])
	 */
	@Override
	public double[] averagePower(float[] data, final double[] w) {
		int windowSize = w.length;
		int stepSize = windowSize >> 1;
		final float[] block = new float[ExtraMath.pow2ceil(w.length)];
		final int nF = block.length >> 1;
		
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
	
		for(int i=spectrum.length; --i >= 0; ) spectrum[i] *= norm;
	
		return spectrum;	
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.fft.FFT#addressSizeOf(java.lang.Object)
	 */
	@Override
	int addressSizeOf(final float[] data) { return data.length>>1; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.fft.FFT#getPadded(java.lang.Object, int)
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
	 * @see kovacs.util.fft.FFT#getMaxErrorBitsFor(java.lang.Object)
	 */
	@Override
	public int countOps(float[] data) {
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
	 * @see kovacs.util.fft.FFT#getMaxSignificantBits()
	 */
	@Override
	final int getMaxSignificantBitsFor(float[] data) {
		return 24;	
	}
	
	@Override
	public int sizeOf(float[] data) {
		return data.length;
	}

	
	public static class NyquistUnrolledRealFT extends FloatFFT {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3073121404450602358L;

		/* (non-Javadoc)
		 * @see kovacs.util.fft.FFT#addressSizeOf(java.lang.Object)
		 */
		// Ignore the presence of an extra component, used for real transforms...
		@Override
		int addressSizeOf(final float[] data) { return super.addressSizeOf(data) & ~1; }
		
		@Override
		void realTransform(final float[] data, final int addressBits, final boolean isForward, int chunks) {
			final int n = 2<<addressBits;
				
			if(!isForward) {
				data[1] = data[n];
				data[n] = data[n+1] = 0.0F;
			}
				
			super.realTransform(data, addressBits, isForward, chunks);
			
			if(isForward) {
				data[n] = data[1];
				data[1] = data[n+1] = 0.0F;
			}
		}
		
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
		protected int getFFTLength(final int addressBits) {
			return super.getFFTLength(addressBits) + 2;
		}
		
		
	}
	
}



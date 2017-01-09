/*******************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.fft;

import java.lang.reflect.Array;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.data.FauxComplexArray;
import jnum.data.WindowFunction;
import jnum.math.Additive;
import jnum.math.Complex;
import jnum.math.ComplexMultiplication;
import jnum.math.Scalable;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericFFT.
 */
public class MultiFFT extends FFT<Object[]> implements RealFFT<Object[]> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3679294695088014282L;
	/** The child. */
	private FFT<?> lastChild;
	
	/** The last type. */
	private Class<?> lastType;
	
	/* (non-Javadoc)
	 * @see kovacs.fft.FFT#getOptimalThreadAddressBits(java.lang.Object)
	 */
	@Override
	protected int getOptimalThreadAddressBits(Object[] data) { 
		FFT<?> child = getChildFor(data[0]);
		
		if(child instanceof FloatFFT) 
			return ((FloatFFT) child).getOptimalThreadAddressBits((float[]) data[0]) - ((FloatFFT) child).getAddressBits((float[]) data[0]); 
		else if(child instanceof DoubleFFT) 
			return ((DoubleFFT) child).getOptimalThreadAddressBits((double[]) data[0]) - ((DoubleFFT) child).getAddressBits((double[]) data[0]); 
		else if(child instanceof ComplexFFT) 
			return ((ComplexFFT) child).getOptimalThreadAddressBits((Complex[]) data[0]) - ((ComplexFFT) child).getAddressBits((Complex[]) data[0]); 
		else if(child instanceof MultiFFT) 
			return ((MultiFFT) child).getOptimalThreadAddressBits((Object[]) data[0]) - ((MultiFFT) child).getAddressBits((Object[]) data[0]); 
		else return super.getOptimalThreadAddressBits(data);
	}
	
	/* (non-Javadoc)
	 * @see jnum.fft.FFT#getMaxErrorBitsFor(java.lang.Object)
	 */
	@Override
	public int countOps(Object[] data) {
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
		
		FFT<?> child = getChildFor(data[0]);
		if(child instanceof FloatFFT) ops += ((FloatFFT) child).countOps((float[]) data[0]);
		else if(child instanceof DoubleFFT) ops += ((DoubleFFT) child).countOps((double[]) data[0]);
		else if(child instanceof ComplexFFT) ops += ((ComplexFFT) child).countOps((Complex[]) data[0]);
		else if(child instanceof MultiFFT) ops += ((MultiFFT) child).countOps((Object[]) data[0]);
		
	
		return ops;
		
		
	}
	

	/* (non-Javadoc)
	 * @see jnum.fft.FFT#getMaxSignificantBits()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	final int getMaxSignificantBitsFor(Object[] data) {
		return ((FFT) getChildFor(data[0])).getMaxSignificantBitsFor(data[0]);
	}

	/* (non-Javadoc)
	 * @see kovacs.fft.FFT#addressSizeOf(java.lang.Object)
	 */
	@Override
	// TODO allowing n+1 size in first index....
	int addressSizeOf(Object[] data) {
		return data.length;
	}

	/* (non-Javadoc)
	 * @see kovacs.fft.FFT#shutdown()
	 */
	@Override
	public synchronized void shutdown() {
		super.shutdown();
		if(lastChild != null) lastChild.shutdown();
	}

	/**
	 * Gets the padded.
	 *
	 * @param data the data
	 * @param n the n
	 * @return the padded
	 */
	public Object[] getPadded(Object[] data, int[] n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Average power.
	 *
	 * @param data the data
	 * @param n the n
	 * @return the object
	 */
	public Object averagePower(Object[] data, int[] n) {
		double[][] windows = new double[n.length][];
		for(int i=n.length; --i >= 0; ) windows[i] = WindowFunction.getHamming(n[i]);
		return averagePower(data, windows);
	}

	
	/**
	 * Average power.
	 *
	 * @param data the data
	 * @param windows the windows
	 * @return the object
	 */
	public Object averagePower(final Object[] data, final double[][] windows) {
		// TODO
		return null;
	}
	
	
	/**
	 * Sets the child for.
	 *
	 * @param element the new child for
	 * @return the child for
	 */
	private synchronized FFT<?> getChildFor(Object element) {
		if(element.getClass().equals(lastType)) return lastChild;
		
		if(element instanceof float[]) lastChild = new FloatFFT.NyquistUnrolledRealFT();
		else if(element instanceof double[]) lastChild = new DoubleFFT.NyquistUnrolledRealFT();
		else if(element instanceof Complex[]) lastChild = new ComplexFFT();
		else if(element instanceof Object[]) lastChild = new MultiFFT();
		else throw new IllegalArgumentException("Not an FFT object: " + element.getClass().getSimpleName());
		
		lastType = element.getClass();
		return lastChild;
	}
	
	
	/**
	 * Gets the content.
	 *
	 * @param data the data
	 * @return the content
	 */
	private Object getContent(Object data) {
		if(data instanceof FauxComplexArray) return ((FauxComplexArray<?>) data).getData();
		return data;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.fft.FFT#sequentialComplexTransform(java.lang.Object, boolean)
	 */
	@Override
	void sequentialComplexTransform(final Object[] data, final int addressBits, final boolean isForward) {	
		// Perform FFT of each element
		FFT<?> child = getChildFor(data[0]);
		child.setTwiddleErrorBits(getTwiddleErrorBits());
		
		// Handle Complex[] arrays by their proper FFT directly...
		if(data instanceof Complex[]) {
			((ComplexFFT) child).sequentialComplexTransform((Complex[]) data, addressBits, isForward);
			return;
		}
		
		final int n = 1 << addressBits;
		
		if(child instanceof FloatFFT) 
			for(int i=n; --i >= 0; ) ((FloatFFT) child).sequentialComplexTransform((float[]) getContent(data[i]), isForward);
		else if(child instanceof DoubleFFT) 
			for(int i=n; --i >= 0; ) ((DoubleFFT) child).sequentialComplexTransform((double[]) getContent(data[i]), isForward);
		else if(child instanceof ComplexFFT) 
			for(int i=n; --i >= 0; ) ((ComplexFFT) child).sequentialComplexTransform((Complex[]) data[i], isForward);
		else if(child instanceof MultiFFT) 
			for(int i=n; --i >= 0; ) ((MultiFFT) child).sequentialComplexTransform((Object[]) data[i], isForward);
		else  if(data[0] instanceof FourierTransforming) 
			for(int i=n; --i >= 0; ) ((FourierTransforming) data[i]).complexTransform(isForward);
		
		
		super.sequentialComplexTransform(data, addressBits, isForward);
	}	
	
	/* (non-Javadoc)
	 * @see kovacs.fft.FFT#complexTransform(java.lang.Object, boolean, int)
	 */
	@Override
	void complexTransform(final Object[] data, final int addressBits, final boolean isForward, int chunks) {
		final FFT<?> child = getChildFor(data[0]);	
		child.setTwiddleErrorBits(getTwiddleErrorBits());
		
		// Handle Complex[] arrays by their proper FFT directly...
		if(data instanceof Complex[]) {
			((ComplexFFT) child).complexTransform((Complex[]) data, addressBits, isForward, chunks);
			return;
		}
		
		// Perform FFT of each element	
		final int n = 1 << addressBits;
		final int split = Math.min(chunks, n);
		final int childChunks = chunks > split ? ExtraMath.roundupRatio(chunks, split) : 1;
		
		if(pool != null) child.setThreads(ExtraMath.roundupRatio(pool.getCorePoolSize(), split));

		
		class Transformer extends Task {
			public Transformer(Object[] data, int from, int to) {
				super(data, from, to);
			}

			@Override
			public void process(final Object[] data, final int from, final int to) {
				if(child instanceof FloatFFT) 
					for(int i=from; i<to; i++) ((FloatFFT) child).complexTransform((float[]) getContent(data[i]), isForward, childChunks);
				else if(child instanceof DoubleFFT) 
					for(int i=from; i<to; i++) ((DoubleFFT) child).complexTransform((double[]) getContent(data[i]), isForward, childChunks);
				else if(child instanceof ComplexFFT) 
					for(int i=from; i<to; i++) ((ComplexFFT) child).complexTransform((Complex[]) data[i], isForward, childChunks);
				else if(child instanceof MultiFFT) 
					for(int i=from; i<to; i++) ((MultiFFT) child).complexTransform((Object[]) data[i], isForward, childChunks);		
				else if(data[0] instanceof FourierTransforming)
					for(int i=from; i<to; i++) ((FourierTransforming) data[i]).complexTransform(isForward);
				
			}	
		}
		
		final double dn = (double) n / split;	
		final Queue queue = new Queue();
			
		for(int i=split; --i >= 0; ) {
			int from = (int)Math.round(i * dn);
			int to = (int)Math.round((i+1) * dn);
			queue.add(new Transformer(data, from, to));
		}
		queue.process();

		super.complexTransform(data, addressBits, isForward, chunks);

	}
	
	
	/**
	 * Bit reverse.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	@Override
	protected void bitReverse(final Object[] data, final int from, final int to) {	
		final int addressBits = getAddressBits(data);
		for(int i=from; i<to; i++) {
			int j = bitReverse(i, addressBits);
			if(j > i) { Object temp = data[i]; data[i] = data[j]; data[j] = temp; }
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
	protected void merge2(final Object[] data, int from, int to, final boolean isForward, int blkbit) {	
		
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
	
		final Object x = getMatching(data[0]);
		
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
			
			final Object d1 = data[i1];
			final Object d2 = data[i1 + blk];

			// x = w * d2
			setProduct(x, w, d2);

			// d2 = d1 - x
			setDifference(d2, d1, x);

			// Increment the twiddle factors...
			w.multiplyBy(winc);

			// --------------------------------
			// i1

			// d1 = d1 + x
			setSum(d1, d1, x);			
			
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
	protected void merge4(final Object[] data, int from, int to, final boolean isForward, int blkbit) {
		
		/*
		merge2(data, from, to, isForward, blkbit);
		merge2(data, from, to, isForward, blkbit+1);
		if(true) return;
		*/
		
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
		
		final Object f1 = getMatching(data[0]);
		final Object f2 = getMatching(data[0]);
		final Object f3 = getMatching(data[0]);
		
		final Object x2 = getMatching(data[0]);
		final Object x3 = getMatching(data[0]);
		
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

			final Object f0 = data[i0];
			
			// To keep the twiddle precision under control
			// recalculate every now and then...

			if((i0 & clcmask) == 0) w1.setUnitVectorAt(i0 * theta);				
		
			w2.setProduct(w1, w1);		
			w3.setProduct(w1, w2);			
			
			final int i1 = i0 + blk;
			final int i2 = i1 + blk;
			final int i3 = i2 + blk;
			
			// f2 = w2 * d1...
			setProduct(f2, w2, data[i1]);
			setProduct(f1, w1, data[i2]);
			setProduct(f3, w3, data[i3]);

			// Increment the twiddle factors...
			w1.multiplyBy(winc);

			// x2 = f0 - f2
			// x3 = i * (f1 - f3) 
			setDifference(x2, f0, f2);
			setDifference(x3, f1, f3);
			multiplyByI(x3);
			
			if(isForward) {
				// d3 = x2 - x3
				setDifference(data[i3], x2, x3);
				// d1 = x2 + x3
				setSum(data[i1], x2, x3);
			}
			else {
				// d3 = x2 + x3
				setSum(data[i3], x2, x3);
				// d1 = x2 - x3
				setDifference(data[i1], x2, x3);
			}
			
			// x2 = f0 + f2
			// x3 = f1 + f3
			setSum(x2, f0, f2);
			setSum(x3, f1, f3);
			
			// d2 = x2 - x3
			// d0 = x2 + x3
			setDifference(data[i2], x2, x3);
			setSum(data[i0], x2, x3);
			
		}
		// <------------------- Processing Block Ends Here ------------------------->

	}
	
	
	/**
	 * Returns a matching uninitialized object to the argument.
	 * 
	 * Think of it as a copy operation, but one that is intended to return an uninitialized object. That is, the method
	 * will return an object of the same size and shape as the argument, but without without the contents of the
	 * argument (if possible).
	 *
	 * @param a the object to match. It can be any array whose base class is a float[], or double[], or any object
	 * that implements the {@link Copiable} or {@link CopiableContent} interface.
	 * @return an uninitialized object that matches the argument.
	 */
	private Object getMatching(final Object a) {
		if(a instanceof float[]) return new float[((float[]) a).length];
		else if(a instanceof double[]) return new double[((double[]) a).length];
		else if(a instanceof CopiableContent) return ((CopiableContent<?>) a).copy(false);
		else if(a instanceof Copiable) return ((Copiable<?>) a).copy();
		else if(a instanceof Object[]) {
			final Object[] array = (Object[]) a;
			final Object[] matching = (Object[]) Array.newInstance(array[0].getClass(), array.length);
			for(int i=array.length; --i >= 0; ) matching[i] = getMatching(array[i]);
			return matching;
		}
		throw new IllegalArgumentException("Cannot create matching object for " + a.getClass().getSimpleName());
	}
	
	/**
	 * Multiply the argument by i (the imaginary unit).
	 *
	 * @param a the a
	 */
	private void multiplyByI(final Object a) {
		if(a instanceof ComplexMultiplication) 
			((ComplexMultiplication<?>) a).multiplyByI();
		else if(a instanceof float[]) {
			final float[] A = (float[]) a;
			for(int i=A.length-2; i >= 0; i -= 2) {
				final float temp = A[i];
				A[i] = -A[i+1];
				A[i+1] = temp;				
			}
		}
		else if(a instanceof double[]) {
			final double[] A = (double[]) a;
			for(int i=A.length-2; i >= 0; i -= 2) {
				final double temp = A[i];
				A[i] = -A[i+1];
				A[i+1] = temp;				
			}	
		}
		else if(a instanceof FauxComplexArray) multiplyByI(((FauxComplexArray<?>) a).getData());
		else if(a instanceof Object[]) {
			final Object[] array = (Object[]) a;
			for(int i=array.length; --i >= 0; ) multiplyByI(array[i]);
		}	
		else throw new IllegalArgumentException("Not an FFT object: " + a.getClass().getSimpleName());
	}
	
	/**
	 * Set the last argument to be the complex product of a complex number with the second argument.
	 *
	 * @param <T> the generic type of the arguments
	 * @param result the result
	 * @param z the complex number to multiply with.
	 * @param a the second argument of the product
	 */
	@SuppressWarnings("unchecked")
	private <T> void setProduct(final T result, final Complex z, final T a) {
		if(a instanceof ComplexMultiplication) 
			((ComplexMultiplication<T>) result).setProduct(z, a);
		else if(a instanceof float[]) {
			final float zre = (float) z.re();
			final float zim = (float) z.im();
			final float[] A = (float[]) a;
			final float[] R = (float[]) result;
			for(int i=R.length-2; i >= 0; i -= 2) {
				final float im = A[i+1];
				final float re = A[i];
				R[i+1] = re * zim + im * zre;
				R[i] = re * zre - im * zim;
			}
		}
		else if(a instanceof double[]) {
			final double[] A = (double[]) a;
			final double[] R = (double[]) result;
			for(int i=R.length-2; i >= 0; i -= 2) {
				final double im = A[i+1];
				final double re = A[i];
				R[i+1] = re * z.im() + im * z.re(); 
				R[i] = re * z.re() - im * z.im();
			}
		}
		else if(a instanceof FauxComplexArray) 
			setProduct(((FauxComplexArray<?>) result).getData(),
					z, ((FauxComplexArray<?>) a).getData()
			);
		else if(a instanceof Object[]) {
			final Object[] A = (Object[]) a;
			final Object[] R = (Object[]) result;
			for(int i=R.length; --i >= 0; ) setProduct(R[i], z, A[i]);
		}
		else throw new IllegalArgumentException("Not an FFT object: " + a.getClass().getSimpleName());
	}

	
	/**
	 * Sets the sum.
	 *
	 * @param <T> the generic type
	 * @param result the result
	 * @param a the a
	 * @param b the b
	 */
	@SuppressWarnings("unchecked")
	private <T> void setSum(final T result, final T a, final T b) {	
		if(a instanceof Additive)
			((Additive<T>) result).setSum(a, b);
		else if(a instanceof float[]) {
			final float[] A = (float[]) a;
			final float[] B = (float[]) b;
			final float[] R = (float[]) result;
			for(int i=R.length; --i >= 0; ) R[i] = A[i] + B[i]; 
		}
		else if(a instanceof double[]) {
			final double[] A = (double[]) a;
			final double[] B = (double[]) b;
			final double[] R = (double[]) result;
			for(int i=R.length; --i >= 0; ) R[i] = A[i] + B[i]; 
		}
		else if(a instanceof FauxComplexArray) 
			setSum(((FauxComplexArray<?>) result).getData(),
					((FauxComplexArray<?>) a).getData(), ((FauxComplexArray<?>) b).getData()
			);
		else if(a instanceof Object[]) {
			final Object[] A = (Object[]) a;
			final Object[] B = (Object[]) b;
			final Object[] R = (Object[]) result;
			for(int i=R.length; --i >= 0; ) setSum(R[i], A[i], B[i]);
		}
		else throw new IllegalArgumentException("Not an FFT object: " + a.getClass().getSimpleName());
	}
	
	/**
	 * Sets the difference.
	 *
	 * @param <T> the generic type
 	 * @param result the result
	 * @param a the a
	 * @param b the b
	 */
	@SuppressWarnings({ "unchecked" })
	private <T> void setDifference(final T result, final T a, final T b) {

		if(a instanceof Additive)
			((Additive<T>) result).setDifference(a, b);
		else if(a instanceof float[]) {
			final float[] A = (float[]) a;
			final float[] B = (float[]) b;
			final float[] R = (float[]) result;
			for(int i=R.length; --i >= 0; ) R[i] = A[i] - B[i]; 
		}
		else if(a instanceof double[]) {
			final double[] A = (double[]) a;
			final double[] B = (double[]) b;
			final double[] R = (double[]) result;
			for(int i=R.length; --i >= 0; ) R[i] = A[i] - B[i]; 
		}
		else if(a instanceof FauxComplexArray) 
			setDifference(((FauxComplexArray<?>) result).getData(),
					((FauxComplexArray<?>) a).getData(), ((FauxComplexArray<?>) b).getData()
			);
		else if(a instanceof Object[]) {
			final Object[] A = (Object[]) a;
			final Object[] B = (Object[]) b;
			final Object[] R = (Object[]) result;
			for(int i=R.length; --i >= 0; ) setDifference(R[i], A[i], B[i]);
		}
		else throw new IllegalArgumentException("Not an FFT object: " + a.getClass().getSimpleName());
	}


	/* (non-Javadoc)
	 * @see kovacs.fft.RealFFT#realTransform(java.lang.Object, boolean)
	 */
	@Override
	public final void realTransform(Object[] data, boolean isForward) {
		updateThreads(data);

		// Don't make more chunks than there are processing blocks...
		final int chunks = Math.min(getChunks(), addressSizeOf(data));
		
		if(chunks == 1) sequentialRealTransform(data, isForward);
		else realTransform(data, isForward, chunks);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.fft.RealFFT#realTransform(java.lang.Object, boolean, int)
	 */
	@Override
	public void realTransform(final Object[] data, final boolean isForward, int chunks) {
		final int addressBits = getAddressBits(data);
		final int n = 1 << addressBits;
		final int split = Math.min(chunks, n);
		final int childChunks = chunks > split ? ExtraMath.roundupRatio(chunks, split) : 1;

		// Perform FFT of each element
		final FFT<?> child = getChildFor(data[0]);
		child.setTwiddleErrorBits(getTwiddleErrorBits());
			
		if(pool != null) child.setThreads(ExtraMath.roundupRatio(pool.getCorePoolSize(), split));
		
		if(!isForward) super.complexTransform(data, addressBits, FFT.BACK, chunks);
		
		class Transformer extends Task {
			public Transformer(Object[] data, int from, int to) {
				super(data, from, to);
			}

			@Override
			public void process(final Object[] data, final int from, final int to) {				
				if(child instanceof FloatFFT) 
					for(int i=from; i<to; i++) ((FloatFFT) child).realTransform((float[]) getContent(data[i]), isForward, childChunks);
				
				else if(child instanceof DoubleFFT)
					for(int i=from; i<to; i++) ((DoubleFFT) child).realTransform((double[]) getContent(data[i]), isForward, childChunks);
					
				else if(child instanceof MultiFFT)
					for(int i=from; i<to; i++) ((MultiFFT) child).realTransform((Object[]) data[i], isForward, childChunks);					
				
				else if(data[0] instanceof FourierTransforming.Real) 
					for(int i=from; i<to; i++) ((FourierTransforming.Real) data[i]).realTransform(isForward);	
			}	
		}
		
		final double dn = (double) data.length / split;	
		final Queue queue = new Queue();
		
		
		for(int i=split; --i >= 0; ) {
			int from = (int)Math.round(i * dn);
			int to = (int)Math.round((i+1) * dn);
			queue.add(new Transformer(data, from, to));
		}
		queue.process();
			
		if(isForward) super.complexTransform(data, addressBits, FFT.FORWARD, chunks);
	}
	
	

	/* (non-Javadoc)
	 * @see kovacs.fft.RealFFT#sequentialRealTransform(java.lang.Object, boolean)
	 */
	@Override
	public void sequentialRealTransform(final Object[] data, final boolean isForward) {
		final int addressBits = getAddressBits(data);
		final int n = 1 << addressBits;
		// Perform FFT of each element
		final FFT<?> child = getChildFor(data[0]);
		child.setTwiddleErrorBits(getTwiddleErrorBits());
		
		if(!isForward) super.sequentialComplexTransform(data, addressBits, FFT.BACK);
			
		if(child instanceof FloatFFT) 
			for(int i=n; --i >= 0; ) ((FloatFFT) child).sequentialRealTransform((float[]) getContent(data[i]), isForward);
				
		else if(child instanceof DoubleFFT)
			for(int i=n; --i >= 0; ) ((DoubleFFT) child).sequentialRealTransform((double[]) getContent(data[i]), isForward);
			
		else if(child instanceof MultiFFT) 	
			for(int i=n; --i >= 0; ) ((MultiFFT) child).sequentialRealTransform((Object[]) data[i], isForward);
		
		else if(data[0] instanceof FourierTransforming.Real) 
			for(int i=n; --i >= 0; ) ((FourierTransforming.Real) data[i]).realTransform(isForward);
		
		
		if(isForward) super.sequentialComplexTransform(data, addressBits, FFT.FORWARD);
	}
	
	
	/**
	 * Gets the box element count.
	 *
	 * @param data the data
	 * @return the box element count
	 */
	// TODO do without need to create new objects...
	public int getTransformingVolume(final Object[] data) {
		int addressBits = getAddressBits(data);
		
		Object element = getContent(data[0]);
			
		if(element instanceof FourierTransforming) return (1<<addressBits) * ((FourierTransforming) element).getVolumeCount();
		else if(element instanceof float[]) {
			if(floatFFT == null) floatFFT = new FloatFFT();
			addressBits += floatFFT.getAddressBits((float[]) element);
		}
		else if(element instanceof double[]) {
			if(doubleFFT == null) doubleFFT = new DoubleFFT();
			addressBits += doubleFFT.getAddressBits((double[]) element);
		}
		else if(element instanceof Object[]) addressBits += getTransformingVolume((Object[]) element);
		
		return 1<<addressBits;
	}
	
	/**
	 * Scale.
	 *
	 * @param data the data
	 * @param factor the factor
	 */
	public void scale(final Object data, final double factor) {
		if(data instanceof Scalable) ((Scalable) data).scale(factor);
		else if(data instanceof float[]) {
			final float f = (float) factor;
			final float[] A = (float[]) data;
			for(int i=A.length; --i >= 0; ) A[i] *= f;
		}
		else if(data instanceof double[]) {
			final double[] A = (double[]) data;
			for(int i=A.length; --i >= 0; ) A[i] *= factor;
		}
		else if(data instanceof Object[]) {
			final Object[] A = (Object[]) data;
			for(int i=A.length; --i >= 0; ) scale(A[i], factor);
		}
		else throw new IllegalArgumentException("Cannot scale: " + data.getClass().getSimpleName());
	}

	
	/* (non-Javadoc)
	 * @see jnum.fft.RealFFT#real2Amplitude(java.lang.Object)
	 */
	@Override
	public void real2Amplitude(Object[] data) {
		realTransform(data, FFT.FORWARD);
		final double norm = 1.0 / getTransformingVolume(data);
		scale(data, norm);
	}	
	
	
	/* (non-Javadoc)
	 * @see jnum.fft.RealFFT#amplitude2Real(java.lang.Object)
	 */
	@Override
	public void amplitude2Real(Object[] data) {	
		realTransform(data, FFT.BACK);
	}

	
	// These are used for calculating the normalization for real-to-amplitude transforms
	/** The float fft. */
	// and are created on-the-fly when needed.
	private static FloatFFT floatFFT;
	
	/** The double fft. */
	private static DoubleFFT doubleFFT;
	

	
}

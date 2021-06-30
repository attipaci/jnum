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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.data.FauxComplexArray;
import jnum.data.WindowFunction;
import jnum.math.Additive;
import jnum.math.Complex;
import jnum.math.ComplexMultiplication;
import jnum.math.Scalable;
import jnum.parallel.Parallelizable;

/**
 * FFT for multi-dimensional arrays, and objects of generic types.
 * 
 * @author Attila Kovacs <attila@sigmyne.com>
 *
 */
public class MultiFFT extends FFT<Object[]> implements RealFFT<Object[]> {

    private static final long serialVersionUID = -3679294695088014282L;


    private FFT<?> reuseChild;
    private Class<?> reuseType;

    public MultiFFT() {
        super();
    }

    public MultiFFT(ExecutorService executor) {
        super(executor);
    }

    public MultiFFT(Parallelizable processing) {
        super(processing);
    }
    
    @Override
    public MultiFFT clone() {
        MultiFFT clone = (MultiFFT) super.clone();
        clone.reuseChild = null;
        clone.reuseType = null;
        return clone;
    }

    @Override
    final void discardFrom(final Object[] data, final int address) {
        Arrays.fill(data, address, data.length, null);
    }
    
    @Override
    final void swap(final Object[] data, final int i, final int j) {
        final Object temp = data[i]; data[i] = data[j]; data[j] = temp;
    }


    @Override
    protected int getPointSize(Object[] data) { 
        if(data[0] instanceof FourierTransforming) return ((FourierTransforming) data[0]).getPointSize();
        return ((FFT) getChildFor(data[0])).getPointSize(data[0]);
    }

    @Override
    public final int getPoints(Object[] data) {
        return Integer.highestOneBit(data.length) * ((FFT) getChildFor(data[0])).getPoints(data[0]);
    }


    @Override
    protected int countFlops(Object[] data) {
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

        FFT child = getChildFor(data[0]);
        ops += child.countFlops(data[0]);

        return ops;


    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    final int getMaxSignificantBitsFor(Object[] data) {
        return ((FFT) getChildFor(data[0])).getMaxSignificantBitsFor(data[0]);
    }

    @Override
    // TODO allowing n+1 size in first index....
    int addressSizeOf(Object[] data) {
        return Integer.highestOneBit(data.length);
    }


    public Object[] getPadded(Object[] data, int[] n) {
        // TODO Auto-generated method stub
        return null;
    }


    public Object averagePower(Object[] data, int[] n) {
        double[][] windows = new double[n.length][];
        for(int i=n.length; --i >= 0; ) windows[i] = WindowFunction.getHamming(n[i]);
        return averagePower(data, windows);
    }


    public Object averagePower(final Object[] data, final double[][] windows) {
        // TODO
        return null;
    }


    private synchronized FFT<?> getChildFor(final Object element) throws FFTTypeException {
        if(element.getClass().equals(reuseType)) return reuseChild;

        if(element instanceof float[]) reuseChild = new FloatFFT.NyquistUnrolledReal(this);
        else if(element instanceof double[]) reuseChild = new DoubleFFT.NyquistUnrolledReal(this);
        else if(element instanceof Complex[]) reuseChild = new ComplexFFT(this);
        else if(element instanceof Object[]) reuseChild = new MultiFFT(this);
        else throw new FFTTypeException(element.getClass());

        reuseType = element.getClass();

        return reuseChild;
    }


    @Override
    void sequentialComplexTransform(final Object[] data, final int addressBits, final boolean isForward) {	
        // Perform FFT of each element
        FFT child = getChildFor(data[0]);
        child.setTwiddleErrorBits(getTwiddleErrorBits());

        // Handle Complex[] arrays by their proper FFT directly...
        if(data instanceof Complex[]) {
            ((ComplexFFT) child).sequentialComplexTransform((Complex[]) data, addressBits, isForward);
            return;
        }

        final int n = 1 << addressBits;

        for(int i=n; --i >= 0; ) {
            if(data[i] instanceof FourierTransforming) ((FourierTransforming) data[i]).complexTransform(isForward);
            else child.sequentialComplexTransform(data[i], isForward);   
        }
        
        super.sequentialComplexTransform(data, addressBits, isForward);
    }	


    @Override
    void parallelComplexTransform(final Object[] data, final int addressBits, final boolean isForward) {
        final FFT child = getChildFor(data[0]);	
        child.setTwiddleErrorBits(getTwiddleErrorBits());

        // Handle Complex[] arrays by their proper FFT directly...
        if(data instanceof Complex[]) {
            ((ComplexFFT) child).parallelComplexTransform((Complex[]) data, addressBits, isForward);
            return;
        }

        // Perform FFT of each element	
        final int threads = getParallel(data);
        final int split = Math.min(data.length, getParallel(data));

        child.setParallel(ExtraMath.roundupRatio(threads, split));


        new PointFork(data, 1<<addressBits) {
            @Override
            public void process(final Object[] data, final int i) {
                if(data[i] instanceof FourierTransforming) ((FourierTransforming) data[i]).complexTransform(isForward);
                else child.complexTransform(data[i], isForward);		 
            }
        }.process();

        super.parallelComplexTransform(data, addressBits, isForward);
    }



    // Blockbit is the size of a merge block in bit shifts (e.g. size 2 is bit 1, size 4 is bit 2, etc.)
    // Two consecutive blocks are merged by the algorithm into one larger block...
    @Override
    protected void radix2(final Object[] data, int from, int to, final boolean isForward, int blkbit) {	

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
    @Override
    protected void radix4(final Object[] data, int from, int to, final boolean isForward, int blkbit) {

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
    private Object getMatching(final Object a) throws FFTTypeException {
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
        throw new FFTTypeException(a.getClass());
    }

    /**
     * Multiply the argument by i (the imaginary unit).
     *
     * @param a the a
     */
    private void multiplyByI(final Object a) throws FFTTypeException {
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
        else throw new FFTTypeException(a.getClass());
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
    private <T> void setProduct(final T result, final Complex z, final T a) throws FFTTypeException {
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
        else throw new FFTTypeException(a.getClass());
    }


    @SuppressWarnings("unchecked")
    private <T> void setSum(final T result, final T a, final T b) throws FFTTypeException {	
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
        else throw new FFTTypeException(a.getClass());
    }


    @SuppressWarnings({ "unchecked" })
    private <T> void setDifference(final T result, final T a, final T b) throws FFTTypeException {

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
        else throw new FFTTypeException(a.getClass());
    }

    @Override
    public final void realTransform(final Object[] data, final boolean isForward) {
        if(getParallel() < 2) sequentialRealTransform(data, isForward);
        else parallelRealTransform(data, isForward);
    }


    public void parallelRealTransform(final Object[] data, final boolean isForward) throws FFTTypeException {
        final int addressBits = getAddressBits(data);
        final int n = 1 << addressBits;
        final int split = Math.min(getParallel(), n);

        // Perform FFT of each element
        final FFT<?> child = getChildFor(data[0]);
        child.setTwiddleErrorBits(getTwiddleErrorBits());
        child.setParallel(ExtraMath.roundupRatio(getParallel(), split));

        if(!isForward) super.parallelComplexTransform(data, addressBits, FFT.BACK);

        new PointFork(data, data.length) {
            @Override
            public void process(final Object[] data, final int i) throws Exception {				
                if(child instanceof RealFFT) ((RealFFT) child).realTransform(data[i], isForward);					
                else if(data[0] instanceof FourierTransforming.Real) ((FourierTransforming.Real) data[i]).realTransform(isForward);	
                else throw new FFTTypeException(data[i].getClass());
            }	
        }.process();


        if(isForward) super.parallelComplexTransform(data, addressBits, FFT.FORWARD);
    }


    @Override
    public void sequentialRealTransform(final Object[] data, final boolean isForward) throws FFTTypeException {
        final int addressBits = getAddressBits(data);
        final int n = 1 << addressBits;
        // Perform FFT of each element
        final FFT<?> child = getChildFor(data[0]);
        child.setTwiddleErrorBits(getTwiddleErrorBits());

        
        if(!isForward) super.sequentialComplexTransform(data, addressBits, FFT.BACK);

        for(int i=n; --i >= 0; ) {
            if(child instanceof RealFFT) ((RealFFT) child).sequentialRealTransform(data[i], isForward);
            else if(data[i] instanceof FourierTransforming.Real) ((FourierTransforming.Real) data[i]).realTransform(isForward);
            else throw new FFTTypeException(data[i].getClass());
        }

        if(isForward) super.sequentialComplexTransform(data, addressBits, FFT.FORWARD);
    }



    public void scale(final Object data, final double factor) throws IllegalArgumentException {
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


    @Override
    public void real2Amplitude(Object[] data) {
        realTransform(data, FFT.FORWARD);  
        final double norm = 2.0 / getPoints(data);
        scale(data, norm);
    }	


    @Override
    public void amplitude2Real(Object[] data) {	
        realTransform(data, FFT.BACK);
    }

}

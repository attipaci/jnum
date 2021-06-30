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

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import jnum.ExtraMath;
import jnum.parallel.ParallelObject;
import jnum.parallel.Parallelizable;





/**
 * The root class for various Fast Fourier Transforms (FFTs) working on some data type designated by the <Type> parameter.
 *
 * @param <Type> the data type for the FFT.
 */
public abstract class FFT<Type> extends ParallelObject implements Serializable {

    private static final long serialVersionUID = 3614284894169045332L;
    
    /** The twiddle error should never exceeds this number of bits. */
    private int twiddleErrorBits = 3;
    
    
    protected FFT() {}
    
    protected FFT(ExecutorService executor) {
        this();
        setExecutor(executor);
    }
    
    protected FFT(Parallelizable processing) {
        this();
        copyParallel(processing);
    }
    
 
    
    public int getParallel(Type data) {
        return getParallel() < 1 ? getAutoParallel(data) : getParallel();
    }
   
    
    /**
     * Get the twiddle mask. This mask is used for determining when it is necessary to calculate new precise twiddle factors
     * as opposed to propagating them by faster additions and multiplications alone. The mask is determined based on the 
     * twiddleErrorBits field, and it is hidden from the user. The user only sets the number of error bits tolerated in the
     * twiddle factors via setTwiddleErrorBits(). 
     *
     * @return the twiddle mask
     */
    protected int getTwiddleMask() {
        // 1 bit from single cycle arithmetic
        // n/2 - 1 bits from cycles
        // -1 bit from k index incrementing by 2
        // --> n/2 - 1 bits total...
        return (1 << ((twiddleErrorBits+1) << 1)) - 1;
    }

    /**
     * Set how many error bits are tolerated in the twiddle factors. The FFT routines will guarantee that the error in the
     * twiddle factors will never exceed this. 
     *
     * @param value the new twiddle error bits
     */
    public void setTwiddleErrorBits(int value) {
        if(value < 0) value = -1; // At most precise, the error bit is the unrecorded bit, i.e. -1.
        this.twiddleErrorBits = value;
    }

    /**
     * Gets the number of error bits that are tolarated in the twiddle factors.
     *
     * @return the      The number of error bits resulting from the way twiddle factors.
     */
    public int getTwiddleErrorBits() { return twiddleErrorBits; }

   
  

    public double getMaxErrorBitsFor(Type data) {
        return 0.5 * ExtraMath.log2(1 + countFlops(data));
    }

    /**
     * Count the necessary floating point operations (+,-,*,/) per element.
     *
     * @param data          the data to transform
     * @return the          number of operations per FFT element.
     */
    protected abstract int countFlops(Type data);


    abstract int getMaxSignificantBitsFor(Type data);

    
    /**
     * Complex transform (multi-threaded).
     *
     * @param data          the data to transform
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    public final void complexTransform(final Type data, final boolean isForward) {
        complexTransform(data, getAddressBits(data), isForward);
    }
   
    /**
     * Complex transform (multi-threaded).
     *
     * @param data          the data to transform
     * @param addressBits   the number address bits in use for data elements
     * @param isForward t   <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */ 
    private final void complexTransform(final Type data, final int addressBits, final boolean isForward) {    
        if(getParallel() == 1) sequentialComplexTransform(data, isForward);
        else parallelComplexTransform(data, addressBits, isForward);
    }
    

    /**
     * Sequential complex transform (in a single thread).
     *
     * @param data          the data to transform
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    public final void sequentialComplexTransform(final Type data, final boolean isForward) {	
        sequentialComplexTransform(data, getAddressBits(data), isForward);
    }

    /**
     * Sequential complex transform (in a single thread).
     *
     * @param data          the data to transform.
     * @param addressBits   the number address bits in use for data elements
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    void sequentialComplexTransform(final Type data, final int addressBits, final boolean isForward) {	
        final int n = 1<<addressBits;
        
        for(int i=n; --i >= 0; ) {
            final int j = Integer.reverse(i<<(32-addressBits));
            if(j > i) swap(data, i, j);
        }

        int blkbit = 0;

        if((addressBits & 1) != 0) radix2(data, 0, n, isForward, blkbit++);

        while(blkbit < addressBits) {	
            radix4(data, 0, n, isForward, blkbit);
            blkbit += 2;
        }
        
        discardFrom(data, n);
    }
  
    /**
     * Parallel complex transform (multi-threaded).
     *
     * @param data          the data to transform.
     * @param addressBits   the number address bits in use for data elements
     * @param isForward     <code>true</code> if it is a forward transform, otherwise <code>false</code>
     */
    void parallelComplexTransform(final Type data, final int addressBits, final boolean isForward) {	
        // Don't make more chunks than there are processing blocks...
        final int n = 1<<addressBits;    
       
        // Bit reversal...
        new PointFork(data, n) {
            @Override
            public void process(final Type data, final int i) {
                final int j = Integer.reverse(i << (32-addressBits));
                if(j > i) swap(data, i, j);
            }
        }.process();

        int blkbit = 0;
        
        if((addressBits & 1) != 0) {
            new Radix2(data, n, blkbit, isForward).process();
            blkbit++;		
        }
        
        while(blkbit < addressBits) {
            new Radix4(data, n, blkbit, isForward).process();
            blkbit += 2;
        }
        
        discardFrom(data, n);
    }
    
    
    /**
     * Swaps the data elements at two different locations.
     * 
     * @param data      The generic type array of the data
     * @param i         Index of an element
     * @param j         Index of the other element/
     */
    abstract void swap(Type data, int i, int j);
 
    /**
     * Discards (zeroes) the data beyond an address (data index).
     * 
     * @param data      A generic type array containing data
     * @param address   Index in array beyond which to zero out elements.
     */
    abstract void discardFrom(final Type data, final int address);

    /**
     * Gets the address bits in use in a generic type data array. That is, if the data of size 2<sup>n</sup> or
     * less than a factor of 2 below, it will return <i>n</i>.
     * 
     * @param data      A generic type array containing data
     * @return          The number of bits to in use for addressing elements in the supplied array.
     */
    abstract int addressSizeOf(Type data);


    public final void complexForward(Type data) { complexTransform(data, FORWARD); }


    public final void complexBack(Type data) { complexTransform(data, BACK); }


    protected abstract void radix2(final Type data, int from, int to, final boolean isForward, int blkbit);


    protected abstract void radix4(final Type data, int from, int to, final boolean isForward, int blkbit);


    public double getMinPrecisionFor(Type data) {
        return Math.pow(2.0, getMaxErrorBitsFor(data)) / Math.pow(2.0, getMaxSignificantBitsFor(data));
    }


    public double getMinSignificantBits(Type data) {
        return getMaxSignificantBitsFor(data) - getMaxErrorBitsFor(data); 
    }

    public double getDynamicRangedB(Type data) {
        return -20.0 * Math.log10(getMinPrecisionFor(data));
    }


    int getAddressBits(Type data) {
        return ExtraMath.log2floor(addressSizeOf(data));
    }	
    
    protected abstract int getPoints(Type data);
       
    protected abstract int getPointSize(Type data);

    public int getOptimalThreads() {
        /*
         * 2 threads per hyper-threaded CPU seems to be the magic...
         */
        return Runtime.getRuntime().availableProcessors() << 1;
    }
    
    public int getAutoParallel(Type data) {
        return Math.min(
                getOptimalThreads(),
                ExtraMath.roundupRatio(getPoints(data) * getPointSize(data), MIN_PARALLEL_SIZE)
        );
    }
    
   
  
  
    abstract class BlockFork extends Task<Void> {
        private Type data;
        private int points;
        

        public BlockFork(Type data, int points) {
            this.data = data;
            this.points = points;      
        }
        
        protected final Type getData() { return data; }
        
        protected final int getPoints() { return points; }
       
     
        protected int getBlockSize(int split) {
            return Math.min(
                    ExtraMath.roundupRatio(getPoints(), split), 
                    ExtraMath.roundupRatio(MAX_BLOCK_BYTES, getPointSize(data))
            );
        }
        
        @Override
        protected void processChunk(int i, int threads) throws Exception {
            final int blockSize = getBlockSize(threads);            
            final int superBlockSize = threads * blockSize;
             
            for(int offset = i * blockSize; offset < points; offset += superBlockSize) {
                processBlock(data, offset, Math.min(points, offset + blockSize));
            }
        }
        
        protected abstract void processBlock(Type data, int from, int to) throws Exception; 

    }
    

    abstract class PointFork extends BlockFork {
        public PointFork(Type data, int points) {
            super(data, points);
        }

        @Override
        protected final void processBlock(final Type data, int from, final int to) throws Exception {   
            for( ; from < to; from++) process(data, from);
        }
        
        protected abstract void process(final Type data, int i) throws Exception;
    }


    private class Radix2 extends BlockFork {
        private int blkbit;
        private boolean isForward;
  
        public Radix2(Type data, int points, int blkbit, boolean isForward) { 
            super(data, points); 
            this.blkbit = blkbit;
            this.isForward = isForward;
        }
        
        // 2 points per merge...
        @Override
        protected int getBlockSize(int split) { return (super.getBlockSize(split) + 1) & (~1); }
       
        @Override
        protected void processBlock(final Type data, final int from, final int to) throws Exception { 
            radix2(data, from, to, isForward, blkbit); 
        }
    }


    private class Radix4 extends BlockFork {
        private int blkbit;
        private boolean isForward;

        public Radix4(Type data, int points, int blkbit, boolean isForward) { 
            super(data, points); 
            this.blkbit = blkbit;
            this.isForward = isForward;
        }
        
        // 4 points per merge...
        @Override
        protected int getBlockSize(int split) { return (super.getBlockSize(split) + 3) & (~3); }
     
        @Override
        protected void processBlock(final Type data, final int from, final int to) throws Exception { 
            radix4(data, from, to, isForward, blkbit);    
        }
    }

  
    
    /*
     * The maximum size block (bytes) that can be processed in a single call. Larger blocks can be more 
     * efficient but only if they fit within the processor's cache. If multiple FFTs operate in parallel
     * they should all fit their data in the cache at the same time to avoid performance degradation.
     * Therefore, it is practical to set the maximum block size a factor of few below the the per-CPU cache
     * size...
     */
    private static final int MAX_BLOCK_BYTES = 1<<18; // 2^18 = 512 kB
    
    private static final int MIN_PARALLEL_SIZE = 1<<10;
  
    public static final int AUTO_PARALLELISM = 0;
    
    

    public static final boolean FORWARD = true;

    public static final boolean BACK = false;

 

}


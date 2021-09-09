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

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import jnum.ExtraMath;
import jnum.parallel.ParallelObject;
import jnum.parallel.Parallelizable;





/**
 * The base class for various Fast Fourier Transforms (FFTs) working on generic type data (usually arrays of sorts).
 * 
 * @param <Type> the data type for the FFT (usually an array of sorts).
 */
public abstract class FFT<Type> extends ParallelObject implements Serializable {

    /** */
    private static final long serialVersionUID = 3614284894169045332L;
    
    /** The twiddle error should never exceeds this number of bits. */
    private int twiddleErrorBits = 3;
    
    /**
     * Instantiates a new FFT. For use by sub-class implementation only.
     * 
     */
    protected FFT() {}
    
    /**
     * Instantiates a new FFT, using the specified executor service for calculating parallel transforms. 
     * For use by sub-class implementation only.
     * 
     * @param executor
     */
    protected FFT(ExecutorService executor) {
        this();
        setExecutor(executor);
    }
    
    /**
     * Instantiates a new  FFT, using the jnum parallel processign environment. For use by sub-class implementation only.
     * 
     * @param processing
     */
    protected FFT(Parallelizable processing) {
        this();
        copyParallel(processing);
    }
    
 
    /**
     * Gets the number of parallel threads the FFTS is configured to calculate transforms in for the
     * specified data object.
     * 
     * @param data      the data object to transform
     * @return          the number of parallel threads that will be used for the supplied data.
     * 
     * @see #getAutoParallel(Object)
     */
    public int getParallel(Type data) {
        return getParallel() < 1 ? getAutoParallel(data) : getParallel();
    }
   
    
    /**
     * Gets the twiddle mask. This mask is used for determining when it is necessary to calculate new precise twiddle factors
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
     * @return  the number of error bits resulting from the way twiddle factors.
     */
    public int getTwiddleErrorBits() { return twiddleErrorBits; }

   
    /**
     * Gets the maximum number of least-significant figure bits that may be noisy in the transformed data.
     * The effective dynamical range of the transformed data is reduced from that of {@link #getMaxSignificantBitsFor(Object)} by
     * the number of bits returned.
     * 
     * @param data      the data object to transform
     * @return          the number of LSB bits
     * 
     * @see #getMinSignificantBits(Object)
     * @see #getDynamicRangedB(Object)
     */
    public double getMaxErrorBitsFor(Type data) {
        return 0.5 * ExtraMath.log2(1 + countFlops(data));
    }

    /**
     * Counts the necessary floating point operations (+,-,*,/) per data element.
     *
     * @param data          the data to transform
     * @return the          number of operations per FFT element.
     */
    protected abstract int countFlops(Type data);

    /** 
     * Gets the number of significant bits normally available for the specified type of data object.
     * The built in type <code>float</code> has a total 23 bits of precision, while <code>double</code>
     * has 52. So, this method would return 23 for <code>float</code> type data objects and 52
     * for <code>double</code> type data objects.
     * 
     * @param data      that data to transform
     * @return          the maximum number of signigicant bits available in the underlying data type.
     */
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

    /**
     * Forward FFT for complex data types.
     * 
     * @param data      the data to transform
     */
    public final void complexForward(Type data) { complexTransform(data, FORWARD); }

    /**
     * Backward FFT for complex data types.
     * 
     * @param data      the data to transform
     */
    public final void complexBack(Type data) { complexTransform(data, BACK); }

    /**
     * Performs a radix-2 FFT iteration, on a chunk of data. When transforming in parallel, the full data
     * object is divided into distinct chunks, each of which are processed in separate threads, when performing
     * a radix-2 or radix-4 iteration.
     * 
     * @param data      the data to transform
     * @param from      the starting index of the data block to process by this radix-2 call.
     * @param to        the ending index (exclusive) of the data block to process by this radix-2 call.
     * @param isForward whether this is for a forward transform.
     * @param blkbit    the log<sub>2</sub> of that size of the datablock. That is, if this radix-2
     *                  iteration operates on a block size 3<sup><i>p</i></sup> then this argument
     *                  should be <i>p</i>. 
     */
    protected abstract void radix2(final Type data, int from, int to, final boolean isForward, int blkbit);


    /**
     * Performs a radix-4 FFT iteration, on a chunk of data. When transforming in parallel, the full data
     * object is divided into distinct chunks, each of which are processed in separate threads, when performing
     * a radix-2 or radix-4 iteration.
     * 
     * @param data      the data to transform
     * @param from      the starting index of the data block to process by this radix-4 call.
     * @param to        the ending index (exclusive) of the data block to process by this radix-4 call.
     * @param isForward whether this is for a forward transform.
     * @param blkbit    the log<sub>2</sub> of that size of the datablock. That is, if this radix-4
     *                  iteration operates on a block size 3<sup><i>p</i></sup> then this argument
     *                  should be <i>p</i>. 
     */
    protected abstract void radix4(final Type data, int from, int to, final boolean isForward, int blkbit);


    /**
     * Gets the relative precision (inverse dynamics range) that will be available when transforming
     * the specified data object. 
     * 
     * @param data      the data to transform
     * @return          2<sup>-m</sup>, if the transformed data is guaranteed to have at least <i>m</i>
     *                  significant bits.
     *                  
     * @see #getMinSignificantBits(Object)
     * @see #getDynamicRangedB(Object)
     */
    public double getMinPrecisionFor(Type data) {
        return Math.pow(2.0, getMaxErrorBitsFor(data)) / Math.pow(2.0, getMaxSignificantBitsFor(data));
    }

    /**
     * Gets the minimum number of significant bits available for the specified data object after
     * the transform. This is the difference of {@link #getMaxSignificantBitsFor(Object)} and
     * {@link #getTwiddleErrorBits()}.
     * 
     * @param data      the data to transform
     * @return          the number of significant bits guaranteed in the transformed data.
     * 
     * @see #getMinPrecisionFor(Object)
     */
    public double getMinSignificantBits(Type data) {
        return getMaxSignificantBitsFor(data) - getMaxErrorBitsFor(data); 
    }

    /**
     * Gets the dynamic range available, in dB (decibell) when transforming the specified data object.
     * 
     * @param data  the data to transform
     * @return      (dB) the dynamic range available in the transformed data.
     */
    public double getDynamicRangedB(Type data) {
        return -20.0 * Math.log10(getMinPrecisionFor(data));
    }

    /**
     * Gets the number of bits that are used in addressing elements in the specified data.
     * That is, if the data has <i>N</i> transformable elements where 2<sup><i>p+1</i></sup> &lt; <i>N</i> &lt;= 2<sup><i>p</i></sup>,
     * then this call would return <i>p</i>.
     * 
     * @param data  the data to transform
     * @return      the minimum number of bits needed to address all transformable elements of the data.
     */
    int getAddressBits(Type data) {
        return ExtraMath.log2floor(addressSizeOf(data));
    }	
    
    /**
     * Gets the number of transformable elements that will be used when transforming the data. The returned
     * value is 2 to the power of address bits used.
     * if the data has <i>N</i> transformable elements where 2<sup><i>p+1</i></sup> &lt; <i>N</i> &lt;= 2<sup><i>p</i></sup>,
     * then this call would return 2<sup><i>p</i></sup>.
     * 
     * @param data  the data to transform.
     * @return      the number of data points that will be transformed. For arbitrary data sizes, the
     *              transform will use a truncated set with 2<sup><i>p</i></sup> points.
     * 
     * @see #getAddressBits(Object)
     *
     */
    protected abstract int getPoints(Type data);
       
    /**
     * Gets the Java memory footprint of a single transforming element in the specified data. This is
     * used mianly to find optimal palallelization for different data types, assuming some typical
     * CPU cache size. The transforms will be fastest, if the simultaneous transforming chunks of data
     * can all fit in the CPU cache at once.
     * 
     * @param data  the data to transform
     * @return      the number of bytes each element will occupy on the Java heap.
     */
    protected abstract int getPointSize(Type data);
    
    /**
     * Calculates the optimum number of threads to use for best multi-threaded performance of large
     * data (small data objects may use fewer threads than that, depending on their size).
     * Typically, the best performance is achieved when using at least as many threads as CPUs are reported by the OS, 
     * or slightly (factor of 2) more...
     * 
     * @return  the optimal number of threads to use for large datasets.
     */
    public int getOptimalThreads() {
        /*
         * 2 threads per hyper-threaded CPU seems to be the magic...
         */
        return Runtime.getRuntime().availableProcessors() << 1;
    }
    
    /**
     * Gets the quasi-optimal number of parallel threads to use for the specified data object. It is based
     * on some assumtions on typical CPU cache sizes. Users may be able to eek out slightly better FFT
     * performance by manually setting the number of parallel threads to use, on a given platform and
     * given data size, but this call will return something reasonable without having to dig in too 
     * deep...
     * 
     * @param data  the data to transform
     * @return      the quasi-optimal thread count to use for transforming the specified data.
     */
    public int getAutoParallel(Type data) {
        return Math.min(
                getOptimalThreads(),
                ExtraMath.roundupRatio(getPoints(data) * getPointSize(data), MIN_PARALLEL_SIZE)
        );
    }
    
   
  
    /**
     * Parallel task for processing data in parallel blocks (chunks).
     * 
     * @author Attila Kovacs
     *
     */
    abstract class BlockFork extends Task<Void> {
        /** the data to transform */
        private Type data;
        
        /** the number of elements (leading index only) in data to transform. */
        private int points;
        
        /**
         * Instantiates a new block-wise parallel processing of data.
         * 
         * @param data      the data
         * @param points    the number of elements (leading index only) in data to transform.
         */
        public BlockFork(Type data, int points) {
            this.data = data;
            this.points = points;      
        }
        
        /**
         * Gets the data object which is being transformed.
         * 
         * @return  the data to transform.
         */
        protected final Type getData() { return data; }
        
        /**
         * Gets the number of points (leading data index only) which are to be transformed.
         * 
         * @return  the number of points to transform (along primary dimension).
         */
        protected final int getPoints() { return points; }
       
        /**
         * Gets the number of points (leading data index only) which are processed in each parallel thread.
         * 
         * @param split     the number of parallel threads to use, that is the
         *                  number of chunks the data is split into for parallel processing
         * @return          the size of each parallel block, but not exceeding {@@link #MAX_BLOCK_BYTES}.
         *                  for large data sets, this means processing will result in more blocks
         *                  then there are processing threads, and converselu each thread will
         *                  processes multiple smaller data block squentially. This is necessary
         *                  to get the best FFT perfomance when CPU cache size may be limited.
         */
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
        
        /**
         * Processes a block of data.
         * 
         * @param data      the dataset to transfrom
         * @param from      stating index (leading index only) of the data block
         * @param to        ending index (exclusive; leading only) of the data block.
         * @throws Exception    if there was an exception (of any kind) while attepting to process
         *                      the block of data.
         */
        protected abstract void processBlock(Type data, int from, int to) throws Exception; 

    }
    
    /**
     * Parallel task for processing elements along the first dimension independently and block-wise parallel. This
     * is useful when elements do not interact with one another in the processing, such as a re-scaling of
     * elements.
     * 
     * @author Attila Kovacs
     *
     */
    abstract class PointFork extends BlockFork {
        
        /**
         * Instantiates a per-element parallel task.
         * 
         * @param data
         * @param points
         */
        public PointFork(Type data, int points) {
            super(data, points);
        }

        @Override
        protected final void processBlock(final Type data, int from, final int to) throws Exception {   
            for( ; from < to; from++) process(data, from);
        }
        
        /**
         * Processes the data point at the specified leading index.
         * 
         * @param data          data to transform
         * @param i             leading index of element
         * @throws Exception    if there was an exception encountered while processing the element.
         */
        protected abstract void process(final Type data, int i) throws Exception;
    }

    /**
     * Parallel task for performing a radix-2 iteration, with data divided into parallel
     * blocks aling the leading data index.
     * 
     * @author Attila Kovacs
     *
     */
    private class Radix2 extends BlockFork {
        /** number of bits in block (log<sub>2</sub> block-size) */
        private int blkbit;
        
        /** whether it is for a forward transform. */
        private boolean isForward;
        
        /**
         * Instantiates a parallel radix-2 parallel processing step.
         * 
         * @param data          the data to transform.
         * @param points        number of data points to transform.
         * @param blkbit        number of bits in parallel block (log<sub>2</sub> block-size).
         * @param isForward     whether it is for a forward transform.
         */
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

    /**
     * Parallel task for performing a radix-4 iteration, with data divided into parallel
     * blocks aling the leading data index.
     * 
     * @author Attila Kovacs
     *
     */
    private class Radix4 extends BlockFork {
        /** number of bits in block (log<sub>2</sub> block-size) */
        private int blkbit;
        
        /** whether it is for a forward transform. */
        private boolean isForward;

        /**
         * Instantiates a parallel radix-2 parallel processing step.
         * 
         * @param data          the data to transform.
         * @param points        number of data points to transform.
         * @param blkbit        number of bits in parallel block (log<sub>2</sub> block-size).
         * @param isForward     whether it is for a forward transform.
         */
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

  
    
    /**
     * The maximum size block (bytes) that can be processed in a single call. Larger blocks can be more 
     * efficient but only if they fit within the processor's cache. If multiple FFTs operate in parallel
     * they should all fit their data in the cache at the same time to avoid performance degradation.
     * Therefore, it is practical to set the maximum block size a factor of few below the the per-CPU cache
     * size...
     */
    private static final int MAX_BLOCK_BYTES = 1<<18; // 2^18 = 512 kB
    
    /**
     * The minimum data size (bytes) that can be processed in parallel when using automatic
     * parallelization.
     * 
     */
    private static final int MIN_PARALLEL_SIZE = 1<<10;
  
    /**
     * Constant to use instead of an integer number of parallel threads, when intending to
     * specify that the FFT should use whatever optimal parallelization to get the fastest
     * transforms for data objects.
     * 
     */
    public static final int AUTO_PARALLELISM = 0;
    
    
    /**
     * Boolean constant to use for more readable code for <code>isForward</code> arguments. For example,
     * instead of:
     * 
     * <pre>
     *  fft.complexTransform(data, true);
     * </pre>
     * 
     * for a forward fransform you can write the more informative:
     * 
     * <pre>
     *  fft.complexTransform(data, FFT.FORWARD);
     * </pre>
     */
    public static final boolean FORWARD = true;


    /**
     * Boolean constant to use for more readable code for <code>isForward</code> arguments. For example,
     * instead of:
     * 
     * <pre>
     *  fft.complexTransform(data, false);
     * </pre>
     * for a forward fransform you can write the more informative:
     * 
     * <pre>
     *  fft.complexTransform(data, FFT.BACK);
     * </pre>
     */
    public static final boolean BACK = false;

 

}


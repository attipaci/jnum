/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jnum.ExtraMath;
import jnum.Util;


// TODO: Auto-generated Javadoc
/**
 * The root class for various Fast Fourier Transforms (FFTs) working on some data type designated by the <Type> parameter.
 *
 * @param <Type> the data type for the FFT.
 */
public abstract class FFT<Type> implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3614284894169045332L;

	/** The parallel processing pool used by this FFT. All parallel calls will be submitted to this queue. */
	protected ThreadPoolExecutor pool;
	
	/** The number of FFT chunks that are processed in the same thread. */
	private int chunksPerThread = 2;
	
	/** Whether on not to multithread automatically. */
	private boolean autoThread = false;
	
	/** The twiddle error should never exceeds this number of bits. */
	private int twiddleErrorBits = 3;
	
	/**
	 * Get the number of address bits (of the data elements) that defines an optimal chunk size for running in a thread.
	 * For optimal multi-threading, the optimal number of threads will be determined such that no thread will run on 
	 * a data chunk smaller than this address size.
	 *
	 * @param data the data
	 * @return the minimum number of address bits per thread for optimal performance.
	 */
	protected int getOptimalThreadAddressBits(Type data) { return 14; }

	/**
	 * Get the maximum number of parallel threads supported by this platform.
	 *
	 * @return the maximum number of threads that can run in parallel.
	 */
	int getMaxThreads() { return Runtime.getRuntime().availableProcessors(); }
	
	/**
	 * Gets the number of FFT chunks that will run on the same thread.
	 *
	 * @return the chunks per thread
	 */
	public int getChunksPerThread() { return chunksPerThread; }
	
	/**
	 * Sets the number of FFT chunks per thread.
	 *
	 * @param n the new chunks per thread
	 */
	public void setChunksPerThread(int n) { this.chunksPerThread = n; }
	
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
	 * Gets the twiddle error bits.
	 *
	 * @return the twiddle error bits
	 */
	public int getTwiddleErrorBits() { return twiddleErrorBits; }
	
	// TODO clone pools properly...
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { 
			@SuppressWarnings("unchecked")
			FFT<Type> clone = (FFT<Type>) super.clone(); 
			clone.pool = null;
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }
	}
	
	
	/**
	 * A class for queuing parallel FFT tasks. 
	 */
	protected class Queue {
		
		/** The tasks. */
		private Vector<Task> tasks = new Vector<Task>();
		
		/** The active. */
		private int active = 0;
		
		/**
		 * Submit a parallel FFT task to this queue.
		 *
		 * @param task the task
		 */
		protected void add(Task task) {
			task.setQueue(this);
			tasks.add(task); 
		}
		
		/**
		 * Process the queue. The call returns once all pending elements of the queue are processed.
		 */
		
		
		protected synchronized void process() {
			active += tasks.size();
			
			for(Task task : tasks) {
				if(pool == null) task.run();
				else pool.execute(task);
			}
			tasks.clear();
			while(active > 0) {
				try { wait(); }
				catch(InterruptedException e) {
					Util.warning(this, "Unexpected interrupt.");
					if(Util.debug) Util.trace(e);
				}
			}
		}
		
		/**
		 * When a task completes, it has to call checkout to notify the queue of its completion.
		 */
		protected synchronized void checkout() {
			active--;
			notifyAll();
		}
	}
	
	
	/**
	 * The a base class of FFT tasks that can be queued for parallel execution. 
	 */
	abstract class Task implements Runnable {
		
		/** The queue to which this task has been submitted. */
		private Queue queue;
		
		/** The data on which this FFT task operates. */
		private Type data;
		
		/** The data index range (from, to) on which this FFT task operates. */
		private int from, to;
		
		/**
		 * Instantiates a new task.
		 *
		 * @param data the data
		 * @param from the from index
		 * @param to the to index (exclusive).
		 */
		public Task(Type data, int from, int to) {
			this.data = data;
			this.from = from;
			this.to = to;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public final void run() {
			//Thread.yield();
			process(data, from, to);
			queue.checkout();
		}
		
		/**
		 * Sets the queue to which this task was submitted. This is normally called by the Queue.add() method.
		 *
		 * @param queue the new queue
		 */
		public void setQueue(Queue queue) {
			this.queue = queue;
		}
		
		/**
		 * Process this task on the specified index range.
		 *
		 * @param data the data to be processed
		 * @param from the from index
		 * @param to the to index (exclusive).
		 */
		public abstract void process(Type data, int from, int to);
	}
	
	
	/**
	 * Gets the max error bits for.
	 *
	 * @param data the data
	 * @return the max error bits for
	 */
	public double getMaxErrorBitsFor(Type data) {
		return 0.5 * ExtraMath.log2(1+countOps(data));
	}
	
	/**
	 * Count the necessary primitive operations (+,-,*,/) per element.
	 *
	 * @param data the data
	 * @return the number of operations per FFT element.
	 */
	public abstract int countOps(Type data);
	
	/**
	 * Gets the max significant bits.
	 *
	 * @param data the data
	 * @return the max significant bits
	 */
	abstract int getMaxSignificantBitsFor(Type data);
	
	
	/**
	 * Sequential complex transform.
	 *
	 * @param data the data
	 * @param isForward the is forward
	 */
	public final void sequentialComplexTransform(final Type data, final boolean isForward) {	
		sequentialComplexTransform(data, getAddressBits(data), isForward);
	}
	
	/**
	 * Sequential complex transform.
	 *
	 * @param data the data
	 * @param addressBits the address bits
	 * @param isForward the is forward
	 */
	void sequentialComplexTransform(final Type data, final int addressBits, final boolean isForward) {	
		final int n = 1<<addressBits;
		bitReverse(data, 0, n);
		
		int blkbit = 0;
		
		if((addressBits & 1) != 0) merge2(data, 0, n, isForward, blkbit++);

		while(blkbit < addressBits) {	
			merge4(data, 0, n, isForward, blkbit);
			blkbit += 2;
		}
	}
	
	
	/**
	 * Complex transform.
	 *
	 * @param data the data
	 * @param isForward the is forward
	 * @param chunks the chunks
	 */
	public final void complexTransform(final Type data, final boolean isForward, int chunks) {
		complexTransform(data, getAddressBits(data), isForward, chunks);
	}
		
	/**
	 * Complex transform.
	 *
	 * @param data the data
	 * @param addressBits the address bits
	 * @param isForward the is forward
	 * @param chunks the number of parallel chunks
	 */	
	void complexTransform(final Type data, final int addressBits, final boolean isForward, int chunks) {	
		// Don't make more chunks than there are processing blocks...
		final int n = 1<<addressBits;
		
		chunks = Math.min(chunks, n >>> 2);
		
		if(chunks == 1) {
			sequentialComplexTransform(data, isForward);
			return;
		}
		//setThreads(threads);
		
		final Queue queue = new Queue();
		
		// Make from and to always multiples of 8 (for radix-4 merge)
		final double dn = (double) (n >>> 2) / chunks;
		final int[] from = new int[chunks];
		final int[] to = new int[chunks];

		for(int k=chunks; --k >= 0; ) {
			from[k] = (int)Math.round(k * dn) << 2;
			to[k] = (int)Math.round((k+1) * dn) << 2;
		}
		
		for(int i=0; i<chunks; i++) queue.add(new BitReverser(data, from[i], to[i]));
		queue.process();

		int blkbit = 0;

		if((addressBits & 1) != 0) {
			for(int i=0; i<chunks; i++) {
				final Merger2 merger = new Merger2(data, from[i], to[i], isForward);
				merger.setBlkBit(blkbit);
				queue.add(merger);
			}
			queue.process();
			blkbit++;		
		}


		if(blkbit < addressBits) {
			final ArrayList<Merger4> mergers = new ArrayList<Merger4>(chunks);
			for(int i=0; i<chunks; i++) mergers.add(new Merger4(data, from[i], to[i], isForward));
			
			while(blkbit < addressBits) {
				for(int i=0; i<chunks; i++) {
					Merger4 merger = mergers.get(i);
					merger.setBlkBit(blkbit);
					queue.add(merger);
				}
				queue.process();
				blkbit += 2;
			}
		}
	}
	
	/**
	 * Address size of.
	 *
	 * @param data the data
	 * @return the int
	 */
	abstract int addressSizeOf(Type data);
		
	/**
	 * Complex forward.
	 *
	 * @param data the data
	 */
	public final void complexForward(Type data) { complexTransform(data, FORWARD); }
	
	/**
	 * Complex back.
	 *
	 * @param data the data
	 */
	public final void complexBack(Type data) { complexTransform(data, BACK); }
	
	/**
	 * Bit reverse.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 */
	protected abstract void bitReverse(final Type data, final int from, final int to);
	
	/**
	 * Merge2.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param isForward the is forward
	 * @param blkbit the blkbit
	 */
	protected abstract void merge2(final Type data, int from, int to, final boolean isForward, int blkbit);
	
	/**
	 * Merge4.
	 *
	 * @param data the data
	 * @param from the from
	 * @param to the to
	 * @param isForward the is forward
	 * @param blkbit the blkbit
	 */
	protected abstract void merge4(final Type data, int from, int to, final boolean isForward, int blkbit);
	
	/**
	 * Gets the min precision for.
	 *
	 * @param data the data
	 * @return the min precision for
	 */
	public double getMinPrecisionFor(Type data) {
		return Math.pow(2.0, getMaxErrorBitsFor(data)) / Math.pow(2.0, getMaxSignificantBitsFor(data));
	}
	
	/**
	 * Gets the min significant bits.
	 *
	 * @param data the data
	 * @return the min significant bits
	 */
	public double getMinSignificantBits(Type data) {
		return getMaxSignificantBitsFor(data) - getMaxErrorBitsFor(data); 
	}

	/**
	 * Gets the dynamic range.
	 *
	 * @param data the data
	 * @return the dynamic range
	 */
	public double getDynamicRange(Type data) {
		return -20.0 * Math.log10(getMinPrecisionFor(data));
	}


	/**
	 * Gets the address bits.
	 *
	 * @param data the data
	 * @return the address bits
	 */
	int getAddressBits(Type data) {
		return ExtraMath.log2floor(addressSizeOf(data));
	}	
	
	/**
	 * Complex transform.
	 *
	 * @param data the data
	 * @param isForward the is forward
	 */
	public final void complexTransform(Type data, boolean isForward) {
		updateThreads(data);
		int chunks = getChunks();
		
		if(chunks == 1) sequentialComplexTransform(data, isForward);
		else complexTransform(data, isForward, chunks);
	}
	
	/**
	 * Update threads.
	 *
	 * @param data the data
	 */
	void updateThreads(Type data) {	
		if(!autoThread) return;
		setThreads(getOptimalThreads(data));
		autoThread = true;
	}
	
	
	/**
	 * Gets the chunks.
	 *
	 * @return the chunks
	 */
	public int getChunks() { 
		return pool == null ? 1 : chunksPerThread * pool.getCorePoolSize();
	}
	
	
	/**
	 * Gets the threads.
	 *
	 * @return the threads
	 */
	public int getThreads() { 
		return pool == null ? 1 : pool.getCorePoolSize();
	}
	
	
	/**
	 * Sets the threads.
	 *
	 * @param threads the new threads
	 */
	public void setThreads(int threads) {	
		autoThread = false;
		
		if(pool != null) {
			if(pool.getCorePoolSize() != threads) {
				pool.shutdown();
				pool = null;
			}
			else return;
		}
		
		if(threads == 1) pool = null;
		else {
			pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
			//pool.prestartAllCoreThreads();
		}
	}
	
	/**
	 * Sets the pool.
	 *
	 * @param executor the new pool
	 */
	public void setPool(ThreadPoolExecutor executor) {
		this.pool = executor;
	}
	
	/**
	 * Sets the sequential.
	 */
	public void setSequential() {
		pool = null;
	}
	
	
	/**
	 * Auto thread.
	 */
	public void autoThread() { autoThread = true; }
	
	/**
	 * Checks if is auto threaded.
	 *
	 * @return true, if is auto threaded
	 */
	public boolean isAutoThreaded() { return autoThread; }
	
	/**
	 * Gets the optimal threads.
	 *
	 * @param data the data
	 * @return the optimal threads
	 */
	protected final int getOptimalThreads(Type data) {
		int addressBits = getAddressBits(data);		
		if(addressBits > getOptimalThreadAddressBits(data)) 
			return Math.min(getMaxThreads(), 1<<(addressBits - getOptimalThreadAddressBits(data)));
		else return 1;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		pool.shutdown();
		super.finalize();
	}
	
	/**
	 * Shutdown.
	 */
	public void shutdown() { 
		if(pool == null) return;
		pool.shutdown(); 
		pool = null;
	}
	
	/**
	 * Bit reverse.
	 *
	 * @param i the i
	 * @param bits the bits
	 * @return the int
	 */
	
	
	public static final int bitReverse(final int i, final int bits) {
		return Integer.reverse(i<<(32-bits));
	}
	
	/**
	 * Creates the for.
	 *
	 * @param data the data
	 * @return the fft
	 */
	public static FFT<?> createFor(Object data) {
		if(data instanceof float[]) return new FloatFFT();
		else if(data instanceof double[]) return new DoubleFFT();
		throw new IllegalArgumentException("No FFT for type " + data.getClass().getSimpleName() + ".");
	}
	
	/** The Constant FORWARD. */
	public static final boolean FORWARD = true;
	
	/** The Constant BACK. */
	public static final boolean BACK = false;

	/**
	 * The Class BitReverser.
	 */
	private class BitReverser extends Task {
		
		/**
		 * Instantiates a new bit reverser.
		 *
		 * @param data the data
		 * @param from the from
		 * @param to the to
		 */
		public BitReverser(final Type data, final int from, final int to) { super(data, from, to); }
		
		/* (non-Javadoc)
		 * @see kovacs.fft.FFT.Task#process(java.lang.Object, int, int)
		 */
		@Override
		public void process(final Type data, final int from, final int to) { bitReverse(data, from, to); }
	}

	
	/**
	 * The Class Merger2.
	 */
	private class Merger2 extends Task {
		
		/** The is forward. */
		private boolean isForward;
		
		/** The blkbit. */
		private int blkbit;
		
		/**
		 * Instantiates a new merger2.
		 *
		 * @param data the data
		 * @param from the from
		 * @param to the to
		 * @param isForward the is forward
		 */
		public Merger2(final Type data, final int from, final int to, boolean isForward) { 
			super(data, from, to); 
			this.isForward = isForward;
		}
		
		/**
		 * Sets the blk bit.
		 *
		 * @param blkbit the new blk bit
		 */
		public void setBlkBit(final int blkbit) { this.blkbit = blkbit; }
		
		/* (non-Javadoc)
		 * @see kovacs.fft.FFT.Task#process(java.lang.Object, int, int)
		 */
		@Override
		public void process(final Type data, final int from, final int to) { merge2(data, from, to, isForward, blkbit); }
	}

	
	/**
	 * The Class Merger4.
	 */
	private class Merger4 extends Task {
		
		/** The is forward. */
		private boolean isForward;
		
		/** The blkbit. */
		private int blkbit;
		
		/**
		 * Instantiates a new merger4.
		 *
		 * @param data the data
		 * @param from the from
		 * @param to the to
		 * @param isForward the is forward
		 */
		public Merger4(final Type data, final int from, final int to, boolean isForward) { 
			super(data, from, to); 
			this.isForward = isForward;
		}
		
		/**
		 * Sets the blk bit.
		 *
		 * @param blkbit the new blk bit
		 */
		public void setBlkBit(final int blkbit) { this.blkbit = blkbit; }
		
		/* (non-Javadoc)
		 * @see kovacs.fft.FFT.Task#process(java.lang.Object, int, int)
		 */
		@Override
		public void process(final Type data, final int from, final int to) { merge4(data, from, to, isForward, blkbit); }
	}

	
}
   

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

package jnum.parallel;


import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jnum.ExtraMath;
import jnum.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class Parallel.
 *
 * @param <ReturnType> the generic type
 */
public abstract class ParallelTask<ReturnType> implements Runnable, Cloneable {
	
	/** The index. */
	private int index;
	
	/** The is interrupted. */
	private boolean isAlive = false;
	
	/** The is complete. */
	private boolean isComplete = false;
	
	/** The is interrupted. */
	private boolean isInterrupted = false;
	
	/** The parallel. */
	private Processor processor;
	
	/** The reduction. */
	private ParallelReduction<ReturnType> reduction;
	
	/** The exception. */
	private Exception exception = null;
	
	
	/**
	 * Sets the reduction.
	 *
	 * @param reduction the new reduction
	 */
	public void setReduction(ParallelReduction<ReturnType> reduction) {
		this.reduction = reduction;
		reduction.setParallel(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ParallelTask<ReturnType> clone() {
		try { 
			@SuppressWarnings("unchecked")
            ParallelTask<ReturnType> clone = (ParallelTask<ReturnType>) super.clone(); 
			clone.isAlive = false;
			clone.isComplete = false;
			clone.isInterrupted = false;
			clone.exception = null;
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }
	}
	
    /**
     * Process.
     *
     * @param threadCount the thread count
     * @throws Exception the exception
     */
    public synchronized void process(int chunks) throws Exception {    
        process(chunks, null);
    }
    
    
    /**
     * Process.
     *
     * @param chunks the chunks
     * @param executor the executor
     * @throws Exception the exception
     */
    public synchronized void process(int chunks, ExecutorService executor) throws Exception { 
        
        /*
        StackTraceElement[] trace = new Throwable().getStackTrace();
        for(int i=1; i<trace.length; i++) {
            String text  =trace[i].toString();
            if(text.contains("Task")) continue;
            if(text.contains("Fork")) continue;
            System.err.println(" -- " + text);
            break;
        }
        */
                
        
        if(chunks <= 1 && executor == null) {
            processor = new Processor();
            processor.processSequential();
        }
        else {
            processor = submit(chunks, executor);    
            processor.waitComplete();    
        }
        
        for(ParallelTask<?> worker : getWorkers()) if(worker.exception != null) throw worker.exception;
        postProcess();
    }
   
    /**
     * Process.
     *
     * @param pool the pool
     * @throws Exception the exception
     */
    public void process(ThreadPoolExecutor pool) throws Exception {
        process(pool.getCorePoolSize(), pool);
    }
    


	/**
	 * Submit.
	 *
	 * @param chunks the chunks
	 * @param executor the executor
	 * @return the processor
	 */
	private Processor submit(int chunks, ExecutorService executor) {	 
		processor = new Processor();
		processor.submit(chunks, executor);
		return processor;
	}
	
	/**
	 * Post process.
	 */
	protected void postProcess() {}
	
	/**
	 * Gets the workers.
	 *
	 * @return the workers
	 */
	public final Iterable<ParallelTask<ReturnType>> getWorkers() {
		return processor.workers;
	}
	
	/**
	 * Gets the processor.
	 *
	 * @return the processor
	 */
	protected final Processor getProcessor() { return processor; }
	
		
	/**
	 * Inits the.
	 */
	protected void init() {}
		
	
	/**
	 * Interrupt all.
	 */
	public synchronized void interruptAll() {
		processor.interruptAll();
	}
	
	/**
	 * Interrupt.
	 */
	private synchronized void interrupt() {
		isInterrupted = true;
		notifyAll(); // Notify all blocked operations to make them aware of the interrupt.
	}
	
	/**
	 * Checks if is interrupted.
	 *
	 * @return true, if is interrupted
	 */
	protected boolean isInterrupted() { return isInterrupted; }
	
	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	private void setIndex(int index) {
		if(isAlive()) throw new IllegalThreadStateException("Cannot change task index while running.");
		this.index = index;
	}
	
	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() { return index; }
	
	/**
	 * Gets the partial result.
	 *
	 * @return the partial result
	 */
	public ReturnType getLocalResult() {
		return null;
	}
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public ReturnType getResult() {
		if(reduction != null) return reduction.getResult();
		return null;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		isAlive = true;
		isComplete = false;
		isInterrupted = false;
		
		// clear the exception for reuse...
		exception = null;
		
		try { 
		    init();
			// Don't even start in case it has been interrupted already
			if(!isInterrupted()) processChunk(index, processor.getThreadCount()); 	
		}
		catch(InterruptedException e) {
		    // Interrupted...
		}
		catch(Exception e) {
			Util.error(this, new Exception("Parallel processing error", e));
			exception = e;
			interruptAll();
		}
		
		// Clear the interrupt status for reuse...
		isInterrupted = false;
		
		// Wrap up, if an exception occurs, record it if not prior exception is recorded already...
		try { wrapup(); }
		catch(Exception e) { if(exception == null) exception = e; }
	}

	/**
	 * Checks if is alive.
	 *
	 * @return true, if is alive
	 */
	private boolean isAlive() { return isAlive; }

	/**
	 * Create synchronization among the worker threads. A call to synchronize() will wait 
	 * until all worker threads reach this point.
	 * 
	 * @return the arrival index of this thread's call to the synchronization point.
	 * @throws InterruptedException
	 * @throws BrokenBarrierException
	 */
	protected int synchronize() throws InterruptedException, BrokenBarrierException {
	    if(processor == null) return 0;
	    return processor.synchronizeThread();
	}
	
	/**
	 * Cleanup.
	 */
	protected void cleanup() {}
	
	/**
	 * Wrapup.
	 */
	private synchronized void wrapup() {
		isAlive = false;
		isComplete = true;
		notifyAll();
		cleanup();
	}
		
	/**
	 * Wait complete.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	private synchronized void waitComplete() throws InterruptedException {
		while(!isComplete) wait();
	}
	
	/**
	 * Process index.
	 *
	 * @param i the chunk index
	 * @param split the total number of workers used.
	 * @throws Exception the exception
	 */
	protected abstract void processChunk(int i, int split) throws Exception;
	
	protected int getTotalOps() { return -1; }
    
    protected int getRevisedChunks(int chunks, int minBlockSize) {
        int ops = getTotalOps();
        return ops <= 0 ? chunks : Math.min(chunks, ExtraMath.roundupRatio(3 + ops, minBlockSize));
    }

    
    
    public static ExecutorService newDefaultSequentialExecutor() {
        return Executors.newSingleThreadExecutor();
    }
    
    
    public static ExecutorService newDefaultParallelExecutor() {
        return newDefaultParallelExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    public static ExecutorService newDefaultParallelExecutor(int threads) {
        return Executors.newFixedThreadPool(threads);
    }
    
 
    public static int minExecutorBlockSize = 100;
    
	
	/**
	 * The Class Manager.
	 */
	public class Processor {
		
		/** The processes. */
		private Vector<ParallelTask<ReturnType>> workers;
		
		private CyclicBarrier barrier;
			
		/**
		 * Instantiates a new manager.
		 *
		 * @param task the task
		 */
		private Processor() {	
		}
		
		/**
		 * Gets the thread count.
		 *
		 * @return the thread count
		 */
		private int getThreadCount() { return workers.size(); }
		
		private int synchronizeThread() throws InterruptedException, BrokenBarrierException { 
		    if(barrier == null) return 0;
		    return barrier.await(); 
		}
		
		/**
		 * Submit.
		 *
		 * @param split the chunks
		 * @param executor the executor
		 */
		private synchronized void submit(int split, ExecutorService executor) {
		    
		    // If the split is 0 or negative...
		    if(split <= 0) {
		        // fill threadPoolExecutors to full capacity...
		        if(executor instanceof ThreadPoolExecutor) split = ((ThreadPoolExecutor) executor).getCorePoolSize();
		        // Otherwise, default to single-thread execution...
		        else split = 1;
		    }
		        
		    createProcesses(split);
		    
		    barrier = split > 1 ? new CyclicBarrier(split) : null;
			
		    // Use only copies of the task for calculation, leaving the template
		    // task in its original state, s.t. it may be reused again...
		    
		    for(ParallelTask<?> worker : workers) {
		        if(executor == null) new Thread(worker).start();
		        else executor.submit(worker); 
		    }
		   
		}
		
		/**
         * Runs a clone of the ParallelTask in the current Thread, thus bypassing thread creation
         * or executor queueing...
         *
         * @param count the count
         */
		private synchronized void processSequential() {
		    createProcesses(1);
		    workers.get(0).run();
		}
	
		/**
		 * Creates the processes.
		 *
		 * @param count the count
		 */
		private synchronized void createProcesses(int count) {	
			workers = new Vector<ParallelTask<ReturnType>>(count);
			
			// Use only copies of the task for calculation, leaving the template
			// task in its original state, s.t. it may be reused again...
			for(int i=0; i < count; i++) {
				ParallelTask<ReturnType> t = ParallelTask.this.clone();
				t.setIndex(i);
				workers.add(t);
			}
		}
		
		/**
		 * Wait until the processing is complete.
		 */
		public synchronized void waitComplete() {
			for(ParallelTask<?> task : workers) {
				try { task.waitComplete(); }
				catch(InterruptedException e) { 
					Util.error(this, new Exception("Parallel processing was unexpectedly interrupted.", e));
				}		
			}
		}
		
		/**
	     * Interrupt all.
	     */
	    public synchronized void interruptAll() {
	        for(ParallelTask<?> worker : workers) worker.interrupt();
	    }
		
	}

}

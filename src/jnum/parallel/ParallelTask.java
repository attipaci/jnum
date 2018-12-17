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


public abstract class ParallelTask<ReturnType> implements Runnable, Cloneable {

	private int index;

	private boolean isAlive = false;

	private boolean isComplete = false;

	private boolean isInterrupted = false;

	private Processor processor;

	private ParallelReduction<ReturnType> reduction;

	private Exception exception = null;
	
	
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
	

    public final synchronized void process(int chunks) throws Exception {    
        process(chunks, null);
    }
    
    

    public final void process(ThreadPoolExecutor pool) throws Exception {
        process(pool.getCorePoolSize(), pool);
    }
    

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
   
   
    

	private Processor submit(int chunks, ExecutorService executor) {	 
		processor = new Processor();
		processor.submit(chunks, executor);
		return processor;
	}

	
	protected void postProcess() {}
	

	public final Iterable<ParallelTask<ReturnType>> getWorkers() {
		return processor.workers;
	}
	

	protected final Processor getProcessor() { return processor; }
	
		
	protected void init() {}
		

	public synchronized void interruptAll() {
		processor.interruptAll();
	}
	

	private synchronized void interrupt() {
		isInterrupted = true;
		notifyAll(); // Notify all blocked operations to make them aware of the interrupt.
	}
	

	protected boolean isInterrupted() { return isInterrupted; }
	

	private void setIndex(int index) {
		if(isAlive()) throw new IllegalThreadStateException("Cannot change task index while running.");
		this.index = index;
	}
	

	public int getIndex() { return index; }
	

	public ReturnType getLocalResult() {
		return null;
	}
	

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
		
		cleanup();
		
		// Wrap up, if an exception occurs, record it if not prior exception is recorded already...
		try { wrapup(); }
		catch(Exception e) { if(exception == null) exception = e; }
	}


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
	
	protected void cleanup() {
	    
	}
	

	private synchronized void wrapup() {
		isAlive = false;
		isComplete = true;
		notifyAll();
	}
		

	private synchronized void waitComplete() throws InterruptedException {
		while(!isComplete) wait();
	}
	

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
    

	public class Processor {

		private Vector<ParallelTask<ReturnType>> workers;
		
		private CyclicBarrier barrier;
			

		private Processor() {	
		}
		

		private int getThreadCount() { return workers.size(); }
		
		private int synchronizeThread() throws InterruptedException, BrokenBarrierException { 
		    if(barrier == null) return 0;
		    return barrier.await(); 
		}
		

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
		

	    public synchronized void interruptAll() {
	        for(ParallelTask<?> worker : workers) worker.interrupt();
	    }
		
	}

	
	
}

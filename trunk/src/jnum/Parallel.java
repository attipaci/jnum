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
package jnum;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import jnum.parallel.ParallelReduction;

// TODO: Auto-generated Javadoc
/**
 * The Class Parallel.
 *
 * @param <ReturnType> the generic type
 */
public abstract class Parallel<ReturnType> implements Runnable, Cloneable {
	
	/** The thread. */
	//private Thread thread;
	
	/** The index. */
	private int index;
	
	/** The is interrupted. */
	private boolean isAlive = false;
	
	/** The is complete. */
	private boolean isComplete = false;
	
	/** The is interrupted. */
	private boolean isInterrupted = false;
	
	/** The parallel. */
	private Processor parallelProcessor;
	
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
	public Object clone() {
		try { 
			Parallel<?> clone = (Parallel<?>) super.clone(); 
			clone.isAlive = false;
			clone.isComplete = false;
			clone.isInterrupted = false;
			clone.exception = null;
			return clone;
		}
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Start.
	 *
	 * @param threadCount the thread count
	 * @return the processor
	 */
	// TODO to make it public add safety check to avoid multiple overlapping calls...
	private Processor start(int threadCount) {
		parallelProcessor = new Processor(this);
		parallelProcessor.start(threadCount);
		return parallelProcessor;
	}
	
	/**
	 * Submit.
	 *
	 * @param chunks the chunks
	 * @param executor the executor
	 * @return the processor
	 */
	private Processor submit(int chunks, ExecutorService executor) {
		if(executor == null) return start(chunks);
			
		if(chunks <= 0) {
			if(executor instanceof ThreadPoolExecutor) chunks = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
			else chunks = 1;
		}
		parallelProcessor = new Processor(this);
		parallelProcessor.submit(chunks, executor);
		return parallelProcessor;
	}
	
	/**
	 * Process.
	 *
	 * @param threadCount the thread count
	 * @throws Exception the exception
	 */
	public synchronized void process(int threadCount) throws Exception {
		Processor processor = start(threadCount);
		processor.waitComplete();
		for(Parallel<?> process : getWorkers()) if(process.exception != null) throw process.exception;
		postProcess();
	}
	
	/**
	 * Process.
	 *
	 * @param chunks the chunks
	 * @param executor the executor
	 * @throws Exception the exception
	 */
	public synchronized void process(int chunks, ExecutorService executor) throws Exception {
		Processor processor = submit(chunks, executor);
		processor.waitComplete();
		for(Parallel<?> process : getWorkers()) if(process.exception != null) throw process.exception;
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
	 * Post process.
	 */
	protected void postProcess() {}
	
	/**
	 * Gets the workers.
	 *
	 * @return the workers
	 */
	public Vector<Parallel<ReturnType>> getWorkers() {
		return parallelProcessor.processes;
	}
	
	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	protected Processor getProcessor() { return parallelProcessor; }
	
		
	/**
	 * Inits the.
	 */
	protected void init() {}
		
	
	
	/**
	 * Interrupt all.
	 */
	public synchronized void interruptAll() {
		for(Parallel<?> process : getWorkers()) process.interrupt();
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
	
	/**
	 * Start.
	 */
	public void start() {
		if(isAlive())
			throw new IllegalThreadStateException("Current thread is still running.");
		new Thread(this).start();	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public final void run() {
		isAlive = true;
		// clear the exception for reuse...
		exception = null;
	
		init();
	
		Thread.yield();
		
		try { 
			// Don't even start in case it has been interrupted already
			if(!isInterrupted()) processIndexOf(index, parallelProcessor.getThreadCount()); 	
		}
		catch(Exception e) {
			System.err.println("WARNING! Parallel processing error.");
			//e.printStackTrace();
			exception = e;
			interruptAll();
		}
		// Clear the interrupt status for reuse...
		isInterrupted = false;
		wrapup();
	}

	/**
	 * Checks if is alive.
	 *
	 * @return true, if is alive
	 */
	private boolean isAlive() { return isAlive; }

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
	 * @param i the i
	 * @param threadCount the thread count
	 * @throws Exception the exception
	 */
	protected abstract void processIndexOf(int i, int threadCount) throws Exception;
	
	
	/**
	 * The Class Manager.
	 */
	public class Processor {
		
		/** The template. */
		private Parallel<ReturnType> template;
		
		/** The processes. */
		public Vector<Parallel<ReturnType>> processes = new Vector<Parallel<ReturnType>>();
		
		/** The thread count. */
		private int threadCount;
		
		/**
		 * Instantiates a new manager.
		 *
		 * @param task the task
		 */
		private Processor(Parallel<ReturnType> task) {
			this.template = task;
		}
		
		/**
		 * Gets the thread count.
		 *
		 * @return the thread count
		 */
		private int getThreadCount() { return threadCount; }
		
		/**
		 * Start.
		 *
		 * @param threadCount the thread count
		 */
		private synchronized void start(int threadCount) {
			createProcesses(threadCount);			
			for(Parallel<?> task : processes) task.start();
		}
		
		/**
		 * Submit.
		 *
		 * @param chunks the chunks
		 * @param executor the executor
		 */
		private synchronized void submit(int chunks, ExecutorService executor) {
			createProcesses(chunks);	
			// Use only copies of the task for calculation, leaving the template
			// task in its original state, s.t. it may be reused again...
			for(int i=0; i < threadCount; i++) executor.submit(processes.get(i));
		}
		
		/**
		 * Creates the processes.
		 *
		 * @param count the count
		 */
		private synchronized void createProcesses(int count) {
			this.threadCount = count;
			
			processes.clear();
			processes.ensureCapacity(threadCount);
			
			// Use only copies of the task for calculation, leaving the template
			// task in its original state, s.t. it may be reused again...
			for(int i=0; i < threadCount; i++) {
				@SuppressWarnings("unchecked")
				Parallel<ReturnType> t = (Parallel<ReturnType>) template.clone();
				t.setIndex(i);
				processes.add(t);
			}
		}
		
		/**
		 * Wait complete.
		 */
		public synchronized void waitComplete() {
			for(Parallel<?> task : processes) {
				try { task.waitComplete(); }
				catch(InterruptedException e) { 
					System.err.println("WARNING! Parallel processing was unexpectedly interrupted.");
					System.err.println("         Please notify Attila Kovacs <attila@submm.caltech.edu>.");
					new Exception().printStackTrace();
				}		
			}
		}
		
	}

}

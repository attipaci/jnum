/* *****************************************************************************
 * Copyright (c) 2015 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.parallel;

import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;


/**
 * <p>
 * A processing queue, with support for parallel synchronization, and waiting on trigger events.
 * The processing queue is run as a separate thread. It will take tasks from the queue and
 * submit them to an executor service (if any) or else process them in ad-hoc threads.
 * </p>
 * 
 * <p>
 * The main features of the queue are the ability to add synchronization points (like CUDA
 * <code>__syncthreads()</code>), trigger points, events, and to pause/restart processing
 * on demand. When a synchronization point is
 * reached in the quue, the processing will wait for all previously submitted tasks to
 * complete before proceeding on with the remaining queued task. When a trigger point
 * is reached, the processing will wait for a trigger event (see {@link ProcessingEvent})
 * to proceed. Processing events (see {@link #addEvent()}) can be added to the queue
 * also, which can be waited on until all elements prior to the event in the queue
 * are have been processed.
 * </p>
 * 
 * @author Attila Kovacs
 *
 */
public class ProcessingQueue extends Thread {
    /** Whether the processing on this queue is currently enabled. */
	private boolean isEnabled = true;

	/** The task queue itself */
	private ArrayBlockingQueue<Entry> queue;

	/** The executor service to use (if any) to do the processing */
	private ExecutorService executor;

	/** A table with the currently active processes from this queue... */
	private Hashtable<Integer, Process> activeProcesses = new Hashtable<>();
	
	/**
	 * Creates a new processing queue with a fixed capacity, and processing in ad-hoc threads.
	 * 
	 * @param size         the maximum number of tasks that may be in the queue at any time (see {@link ArrayBlockingQueue}).
	 */
	public ProcessingQueue(int size) {
		queue = new ArrayBlockingQueue<>(size);
	}
	
	/**
     * Creates a new processing queue with a fixed capacity, and specified executor service for processing.
     * 
     * @param size      the maximum number of tasks that may be in the queue at any time (see {@link ArrayBlockingQueue}).
     * @param executor  the executor service to process tasks in the queue with, or <code>null</code>
     *                  to use ad-hoc threads instead.
     */
	public ProcessingQueue(int size, ExecutorService executor) {
		this(size);
		setExecutor(executor);
	}
	

	/**
	 * Sets a new executor service (if any) to use with this processing queue.
	 * 
	 * @param e        the executor service to process tasks in the queue with, or <code>null</code>
     *                 to use ad-hoc threads instead.
	 */
	public void setExecutor(ExecutorService e) {
		this.executor = e;
	}
	
	/**
	 * Gets the executor service (if any) that this queue uses for processing.
	 * 
	 * @return         the executor service used by this queue, or <code>null</code> if queue uses ad-hoc threads instead.
	 */
	public final ExecutorService getExecutor() { return executor; }
	

	@Override
	public void run() {
		while(!isInterrupted()) {			
			try { 
				while(!isEnabled) wait();
				queue.take().process(); 	
			}
			catch(InterruptedException e) { interrupt(); }
		}
	}
	
	/**
	 * Pauses processing the queue after finishing the tasks that are currently being processed.
	 * Processing can be restarted again using {@link #restart()}
	 * 
	 * @see #restart()
	 */
	public void pause() {
		isEnabled = false;
	}
	
	/**
	 * Restarts processing elements in the queue, after it was paused.
	 * 
	 * @see #pause()
	 */
	public synchronized void restart() {
	    if(isEnabled) return;
		isEnabled = true;
		notifyAll();
	}

	/**
	 * Adds a new task to the end of this queue,.
	 * 
	 * @param task     a parallel task
	 * @throws IllegalStateException   if the queue has reached its capacity and is unable to take new
	 *                                 submittions.
	 */
	public void add(Runnable task) throws IllegalStateException {
        queue.add(new Process(task));
    }
	
	/**
     * Adds a new event to the end of this queue. The caller can then use this event to wait until all previously
     * queued tasks have been executed (see {@link Event#waitFor()}), regardless of how many
     * tasks may have been submitted to the queue afterwards. It's an effective wat to conditionally
     * wait until specific tasks are completed before doing something that depends on the result of 
     * those tasks. 
     * 
     * @return      the event that can be waited on until all previously submitted tasks have been
     *              completed.
     * @throws IllegalStateException   if the queue has reached its capacity and is unable to take new
     *                                 submittions.
     */
	public Event addEvent() throws IllegalStateException {
		Event e = new Event();
		queue.add(e);
		return e;
	}
	
	/**
     * Adds a new triggered point to the end of this queue. When the trigger point is reached in the
     * queue all processing will halt until some process calls {@link Trigger#generate()} on the 
     * returned trigger object.
     * 
     * @return  the object to use for generating the trigger event that will resume processing
     *          beyond this point in the queue.
     *          
     * @throws IllegalStateException   if the queue has reached its capacity and is unable to take new
     *                                 submittions.
     * 
     * @see Trigger#generate()
     */
	public Trigger addTrigger() throws IllegalStateException {
	    synchronized(Trigger.class) { 
	        Trigger t = new Trigger(nextTriggerID++);
	        queue.add(t);
	        return t;
	    }
	}
	
	/**
	 * Adds a synchronization accross threads, waiting for all previously submitted threads to
	 * complete before moving on to the next task in the queue.
	 * 
	 * @throws IllegalStateException   if the queue has reached its capacity and is unable to take new
     *                                 submittions.
	 */
	public void addSynchronization() throws IllegalStateException {
		queue.add(new Synchronization());
	}
	
	/**
	 * Gets the number of elements in this queue, including processes, synchronizations, triggers, and events.
	 * 
	 * @return     the number of currently queued entries of any kind.
	 * 
	 * @see ArrayBlockingQueue#size()
	 */
	public int size() {
		return queue.size();
	}
	
	/**
	 * Gets the number of tasks currently being processed in parallel from this queue.
	 * 
	 * @return     the number of parallel tasks currently being processed from this queue.
	 */
	public int countActive() {
		return activeProcesses.size();
	}
	
	/**
	 * Waits until all tasks that are currently being processed are completed.
	 * 
	 * @throws InterruptedException    if an interrupt was received while waiting.
	 */
	public void waitCompleteCurrent() throws InterruptedException {
		final Collection<Process> processes = activeProcesses.values();
		for(Process p : processes) p.waitComplete();
	}
	

	
	private abstract class Entry {	
		
		abstract void process();
	}
	

	private class Process extends Entry implements Runnable {

		private int processID;

		private Runnable process;

		private boolean isComplete = false;
		

		private Process(Runnable r) {
			this.process = r;
			processID = nextProcessID++;
		}

		@Override
		void process() {
			if(isComplete) throw new IllegalStateException("Cannot re-run process.");
			activeProcesses.put(processID, this);
			executor.submit(this);
		}

		@Override
		public void run() {
			process.run();
			checkout();
		}
		

		private synchronized void checkout() {
			isComplete = true;
			activeProcesses.remove(processID);
			notifyAll();
		}
		

		public void waitComplete() throws InterruptedException {
			while(!isComplete) wait();
		}
		
	}
	

	private class Synchronization extends Entry {
		
		private Synchronization() {}

		@Override
		void process() {
			try { waitCompleteCurrent(); }
			catch(InterruptedException e) {}
		}
		
	}

	
	public class Event extends Entry implements Runnable {

		private boolean isActivated = false;
		
		private Event() {}
		
		public boolean isActivated() {
			return isActivated;
		}

		@Override
		void process() {
			new Thread(this).start();		
		} 

		@Override
		public void run() {
			try { waitCompleteCurrent(); }
			catch(InterruptedException e) {}
			generate();
		}
		

		synchronized void generate() {
			isActivated = true;
			notifyAll();
		}
		

		public void waitFor() throws InterruptedException {
			while(!isActivated) wait();
		}
	}
	

	// Generates an AWT ProcessingEvent...
	public class Trigger extends Event {

		private int eventID;
		

		private Trigger(int eventID) {
			this.eventID = eventID;
		}

		@Override
		void generate() {
			super.generate();
			new ProcessingEvent(ProcessingQueue.this, eventID);
		}
	}
	
	/** Local serial number for processes submitted to this queue */
	private int nextProcessID = 1;
	
	/** Local serial number for trigger events created across all processing queue */
	private static int nextTriggerID = 1;
		
}

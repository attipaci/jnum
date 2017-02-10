/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.parallel;

import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessingQueue.
 */
public class ProcessingQueue extends Thread {
	
	/** The is enabled. */
	private boolean isEnabled = true;
	
	/** The queue. */
	private ArrayBlockingQueue<Entry> queue;
	
	/** The executor. */
	private ExecutorService executor;
	
	/** The active processes. */
	private Hashtable<Integer, Process> activeProcesses = new Hashtable<Integer, Process>();
	
	/**
	 * Instantiates a new processing queue.
	 *
	 * @param size the size
	 */
	public ProcessingQueue(int size) {
		queue = new ArrayBlockingQueue<Entry>(size);
	}
	
	/**
	 * Instantiates a new processing queue.
	 *
	 * @param size the size
	 * @param executor the executor
	 */
	public ProcessingQueue(int size, ExecutorService executor) {
		this(size);
		setExecutor(executor);
	}
	
	/**
	 * Sets the executor.
	 *
	 * @param e the new executor
	 */
	public void setExecutor(ExecutorService e) {
		this.executor = e;
	}
	
	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public ExecutorService getExecutor() { return executor; }
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
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
	 * Pause.
	 */
	public void pause() {
		isEnabled = false;
	}
	
	/**
	 * Restart.
	 */
	public synchronized void restart() {
		isEnabled = true;
		notifyAll();
	}
	
	/**
	 * Adds the event.
	 *
	 * @return the event
	 */
	public Event addEvent() {
		Event e = new Event();
		queue.add(e);
		return e;
	}
	
	/**
	 * Adds the trigger.
	 *
	 * @param id the id
	 * @return the trigger
	 */
	public Trigger addTrigger(int id) {
		Trigger t = new Trigger(id);
		queue.add(t);
		return t;
	}
	
	/**
	 * Adds the synchronization.
	 */
	public void addSynchronization() {
		queue.add(new Synchronization());
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return queue.size();
	}
	
	/**
	 * Count active.
	 *
	 * @return the int
	 */
	public int countActive() {
		return activeProcesses.size();
	}
	
	/**
	 * Wait complete current.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void waitCompleteCurrent() throws InterruptedException {
		final Collection<Process> processes = activeProcesses.values();
		for(Process p : processes) p.waitComplete();
	}
	
	
	/**
	 * The Class Entry.
	 */
	private abstract class Entry {	
		
		/**
		 * Process.
		 */
		abstract void process();
	}
	
	/**
	 * The Class Process.
	 */
	public class Process extends Entry implements Runnable {
		
		/** The process id. */
		private int processID;
		
		/** The process. */
		private Runnable process;
		
		/** The is complete. */
		private boolean isComplete = false;
		
		/**
		 * Instantiates a new process.
		 *
		 * @param r the r
		 */
		private Process(Runnable r) {
			this.process = r;
			processID = nextProcessID++;
		}
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ProcessingQueue.Entry#process()
		 */
		@Override
		void process() {
			if(isComplete) throw new IllegalStateException("Cannot re-run process.");
			activeProcesses.put(processID, this);
			executor.submit(this);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			process.run();
			checkout();
		}
		
		/**
		 * Checkout.
		 */
		private synchronized void checkout() {
			isComplete = true;
			activeProcesses.remove(processID);
			notifyAll();
		}
		
		/**
		 * Wait complete.
		 *
		 * @throws InterruptedException the interrupted exception
		 */
		public void waitComplete() throws InterruptedException {
			while(!isComplete) wait();
		}
		
	}
	
	/**
	 * The Class Synchronization.
	 */
	public class Synchronization extends Entry {
		
		/**
		 * Instantiates a new synchronization.
		 */
		private Synchronization() {}
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ProcessingQueue.Entry#process()
		 */
		@Override
		void process() {
			try { waitCompleteCurrent(); }
			catch(InterruptedException e) {}
		}
		
	}
	
	/**
	 * The Class Event.
	 */
	public class Event extends Entry implements Runnable {
		
		/** The is activated. */
		private boolean isActivated = false;
		
		/**
		 * Instantiates a new event.
		 */
		private Event() {}
		
		/**
		 * Checks if is activated.
		 *
		 * @return true, if is activated
		 */
		public boolean isActivated() {
			return isActivated;
		}

		/* (non-Javadoc)
		 * @see jnum.parallel.ProcessingQueue.Entry#process()
		 */
		@Override
		void process() {
			new Thread(this).start();		
		} 
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try { waitCompleteCurrent(); }
			catch(InterruptedException e) {}
			generate();
		}
		
		/**
		 * Generate.
		 */
		synchronized void generate() {
			isActivated = true;
			notifyAll();
		}
		
		/**
		 * Wait for.
		 *
		 * @throws InterruptedException the interrupted exception
		 */
		public void waitFor() throws InterruptedException {
			while(!isActivated) wait();
		}
	}
	
	
	/**
	 * The Class Trigger.
	 */
	// Generates an AWT ProcessingEvent...
	public class Trigger extends Event {
		
		/** The event id. */
		private int eventID;
		
		/**
		 * Instantiates a new trigger.
		 *
		 * @param eventID the event id
		 */
		private Trigger(int eventID) {
			this.eventID = eventID;
		}
		
		/* (non-Javadoc)
		 * @see jnum.parallel.ProcessingQueue.Event#generate()
		 */
		@Override
		void generate() {
			super.generate();
			new ProcessingEvent(ProcessingQueue.this, eventID);
		}
	}
	
	
	
	/** The next process id. */
	private int nextProcessID = 1;
	
	
	
}

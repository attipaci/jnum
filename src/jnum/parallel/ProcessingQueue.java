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


public class ProcessingQueue extends Thread {

	private boolean isEnabled = true;

	private ArrayBlockingQueue<Entry> queue;

	private ExecutorService executor;

	private Hashtable<Integer, Process> activeProcesses = new Hashtable<>();
	

	public ProcessingQueue(int size) {
		queue = new ArrayBlockingQueue<>(size);
	}
	

	public ProcessingQueue(int size, ExecutorService executor) {
		this(size);
		setExecutor(executor);
	}
	

	public void setExecutor(ExecutorService e) {
		this.executor = e;
	}
	

	public ExecutorService getExecutor() { return executor; }
	

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
	

	public void pause() {
		isEnabled = false;
	}
	

	public synchronized void restart() {
		isEnabled = true;
		notifyAll();
	}
	

	public Event addEvent() {
		Event e = new Event();
		queue.add(e);
		return e;
	}
	

	public Trigger addTrigger(int id) {
		Trigger t = new Trigger(id);
		queue.add(t);
		return t;
	}
	

	public void addSynchronization() {
		queue.add(new Synchronization());
	}
	

	public int size() {
		return queue.size();
	}
	

	public int countActive() {
		return activeProcesses.size();
	}
	

	public void waitCompleteCurrent() throws InterruptedException {
		final Collection<Process> processes = activeProcesses.values();
		for(Process p : processes) p.waitComplete();
	}
	

	
	private abstract class Entry {	
		
		abstract void process();
	}
	

	public class Process extends Entry implements Runnable {

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
	

	public class Synchronization extends Entry {
		

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
	

	private int nextProcessID = 1;
		
}

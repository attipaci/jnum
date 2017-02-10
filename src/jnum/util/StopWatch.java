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
package jnum.util;

// TODO: Auto-generated Javadoc
/**
 * The Class StopWatch.
 */
public class StopWatch {
	
	/** The last. */
	private long start = -1L, last = -1L;
	
	/**
	 * Start.
	 */
	public synchronized void start() {
		start = last = System.currentTimeMillis();
	}
	
	/**
	 * Checks if is running.
	 *
	 * @return true, if is running
	 */
	public synchronized boolean isRunning() { return start > 0L; }
	
	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public synchronized long getTime() {
		long now = System.currentTimeMillis();
		return start > 0L ? now - start : -1L;
	}
	
	/**
	 * Lap time.
	 *
	 * @return the long
	 */
	public synchronized long lapTime() {
		long now = System.currentTimeMillis();
		long elapsed = -1;
		if(last > 0L) elapsed = now - last;
		last = now;		
		return elapsed;
	}
	
	
}

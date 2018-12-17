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


public class StopWatch {

	private long start = -1L, last = -1L;
	

	public synchronized void start() {
		start = last = System.currentTimeMillis();
	}
	

	public synchronized boolean isRunning() { return start > 0L; }
	

	public synchronized long getTime() {
		long now = System.currentTimeMillis();
		return start > 0L ? now - start : -1L;
	}
	

	public synchronized long lapTime() {
		long now = System.currentTimeMillis();
		long elapsed = -1;
		if(last > 0L) elapsed = now - last;
		last = now;		
		return elapsed;
	}
	
	
}

/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum;


/**
 * An class for signaling thay some operation failed because a lock on the object or value prevented the desired change.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class LockedException extends Exception {

	private static final long serialVersionUID = 5645978500509374376L;

	/**
	 * Instantiates a new locked exception with no specific message.
	 */
	public LockedException() { }
	
	/**
	 * Instantiates a new locked exception with a message describing the failure.
	 *
	 * @param message a simple message that describes what the lock/setting that failed.
	 */
	public LockedException(String message) { super(message); }
	
	
}

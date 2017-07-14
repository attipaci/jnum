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

// TODO: Auto-generated Javadoc
/**
 * The Class ParallelReduction.
 *
 * @param <ReturnType> the generic type
 */
public abstract class ParallelReduction<ReturnType> {
	
	/** The task. */
	private ParallelTask<ReturnType> task;
		
	/**
	 * Sets the parallel.
	 *
	 * @param task the new parallel
	 */
	public void setParallel(ParallelTask<ReturnType> task) {
		this.task = task;
	}
	
	/**
	 * Gets the parallel.
	 *
	 * @return the parallel
	 */
	public ParallelTask<ReturnType> getParallel() { return task; }
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public abstract ReturnType getResult();
}

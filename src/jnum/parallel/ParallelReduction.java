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

/**
 * A base class for parallel reductions, which combine partial results from independent parallel threads into a global
 * result. Reductions are the jnum analogue to collectors in the world of Java 8 streams. They define how the partial
 * results obtained from the independent paralle processing of sub-sections of data are combined into a global
 * result for the entire dataset.
 * 
 * @author Attila Kovacs
 *
 * @param <ReturnType>   the generic type of object returned by this reduction.
 */
public abstract class ParallelReduction<ReturnType> {

	private ParallelTask<ReturnType> task;
		
	/**
	 * Sets the task for which this reduction will provide a result.
	 * 
	 * @param task     the parallel task that will use this reduction for obtaining its result.
	 */
	public void setParallel(ParallelTask<ReturnType> task) {
		this.task = task;
	}
	
	/**
	 * Gets the parallel task object that is assigned to use this reduction to obtain its result.
	 * 
	 * @return     the parallel using this reduction for obtaining its result.
	 */
	public ParallelTask<ReturnType> getParallel() { return task; }
	
	/**
	 * Gets the final (aggregated) result from this parallel reduction.
	 * 
	 * @return     the final result obtained from this reduction.
	 */
	public abstract ReturnType getResult();
}

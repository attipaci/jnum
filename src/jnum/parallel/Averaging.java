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

import jnum.data.Accumulating;

/**
 * A reduction class for calculating averages in a parallel task.
 * 
 * @author Attila Kovacs
 *
 * @param <Type>      the generic type of object to be averaged by this reduction.
 */
public class Averaging<Type extends Accumulating<Type>> extends ParallelReduction<Type> {


	@Override
	public Type getResult() {
		Type result = null;
		for(ParallelTask<Type> task : getParallel().getWorkers()) {
			Type local = task.getLocalResult();
			if(result == null) result = local;
			else result.accumulate(local);
		}
		if(result != null) result.endAccumulation();
		return result;
	}
	
}

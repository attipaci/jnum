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

package jnum.parallel;

import jnum.Parallel;
import jnum.data.WeightedPoint;

// TODO: Auto-generated Javadoc
/**
 * The Class Averaging.
 *
 * @param <ReturnType> the generic type
 */
public class Averaging<ReturnType extends WeightedPoint> extends ParallelReduction<ReturnType> {

	/* (non-Javadoc)
	 * @see jnum.parallel.ParallelReduction#getResult()
	 */
	@Override
	public ReturnType getResult() {
		ReturnType result = null;
		for(Parallel<ReturnType> task : getParallel().getWorkers()) {
			ReturnType local = task.getPartialResult();
			if(result == null) result = local;
			else result.average(local);
		}
		return result;
	}
	
}

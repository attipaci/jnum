/* *****************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.data;

import java.util.ArrayList;

import jnum.ExtraMath;



public abstract class LocalAverage<DataType extends LocalizedData> extends ArrayList<DataType> {

	private static final long serialVersionUID = 786022102371734003L;

	public double span = 3.0;	
		

	public int indexBefore(Locality loc) throws ArrayIndexOutOfBoundsException {
		int i = 0;
		int step = ExtraMath.pow2ceil(size()) >>> 1;

		if(get(0).compareTo(loc) > 0) 
			throw new ArrayIndexOutOfBoundsException("Specified point precedes lookup range.");
		
		if(get(size() - 1).compareTo(loc) < 0) 
			throw new ArrayIndexOutOfBoundsException("Specified point is beyond lookup range.");
		
		while(step > 0) {
		    if(i + step < size()) if(get(i + step).compareTo(loc) < 0) i += step;
			step >>>= 1;
		}
		
		return i;
	}
	
	
	public double getRelativeWeight(double normalizedDistance) {
		return Math.exp(-0.5 * normalizedDistance * normalizedDistance);
	}
	

	public abstract DataType getLocalizedDataInstance();
	

	public final DataType getLocalAverage(Locality loc) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, null);
	}
	

	public final DataType getLocalAverage(Locality loc, Object env) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, env, null);
	}
		

	public DataType getLocalAverage(Locality loc, Object env, DataType consistencyReference) throws ArrayIndexOutOfBoundsException {
		int i0 = indexBefore(loc);
		
		DataType mean = getLocalizedDataInstance();
		mean.setLocality(loc);
		mean.measurements = 0;
			
		for(int i = i0; i >= 0; i--) {
		    DataType point = get(i);
		    if(point.sortingDistanceTo(loc) > span) break;		
			if(consistencyReference != null) if(!point.isConsistentWith(consistencyReference)) continue;
			
			mean.average(point, env, getRelativeWeight(point.distanceTo(loc)));
		}
	
		for(int i = i0+1; i < size(); i++) {
		    DataType point = get(i);
		    if(point.sortingDistanceTo(loc) > span) break;
			if(consistencyReference != null) if(point.isConsistentWith(consistencyReference)) continue;

			mean.average(point, env, getRelativeWeight(point.distanceTo(loc)));
		}
			
		return mean;
		
	}
	

	public final DataType getCheckedLocalAverage(Locality loc) throws ArrayIndexOutOfBoundsException {
		return getCheckedLocalAverage(loc, null);
	}
	

	public DataType getCheckedLocalAverage(Locality loc, Object env) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, env, getLocalAverage(loc, env, null));
	}
	
	
}

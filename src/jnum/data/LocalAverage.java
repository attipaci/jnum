/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.data;

import java.util.ArrayList;


// TODO: Auto-generated Javadoc
/**
 * The Class LocalAverage.
 *
 * @param <DataType> the generic type
 */
public abstract class LocalAverage<DataType extends LocalizedData> extends ArrayList<DataType> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 786022102371734003L;
	
	/** The span. */
	public double span = 3.0;	
		
	/**
	 * Index before.
	 *
	 * @param loc the loc
	 * @return the int
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public int indexBefore(Locality loc) throws ArrayIndexOutOfBoundsException {
		int i = 0;
		int step = size() >> 1;

		
		if(get(0).compareTo(loc) > 0) 
			throw new ArrayIndexOutOfBoundsException("Specified point precedes lookup range.");
		
		if(get(size() - 1).compareTo(loc) < 0) 
			throw new ArrayIndexOutOfBoundsException("Specified point is beyond lookup range.");
		
		
		while(step > 0) {
			if(get(i + step).compareTo(loc) < 0) i += step;
			step >>= 1;
		}
		
		return i;
	}
	
	
	/**
	 * Gets the relative weight.
	 *
	 * @param normalizedDistance the normalized distance
	 * @return the relative weight
	 */
	public double getRelativeWeight(double normalizedDistance) {
		return Math.exp(-0.5 * normalizedDistance * normalizedDistance);
	}
	
	/**
	 * Gets the localized data instance.
	 *
	 * @return the localized data instance
	 */
	public abstract DataType getLocalizedDataInstance();
	
	/**
	 * Gets the local average.
	 *
	 * @param loc the loc
	 * @return the local average
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public final DataType getLocalAverage(Locality loc) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, null);
	}
	
	/**
	 * Gets the local average.
	 *
	 * @param loc the loc
	 * @param env the env
	 * @return the local average
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */	
	public final DataType getLocalAverage(Locality loc, Object env) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, env, null);
	}
		
	/**
	 * Gets the local average.
	 *
	 * @param loc the loc
	 * @param env the env
	 * @param consistencyReference the consistency reference
	 * @return the local average
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public DataType getLocalAverage(Locality loc, Object env, DataType consistencyReference) throws ArrayIndexOutOfBoundsException {
		int i0 = indexBefore(loc);
	
		DataType mean = getLocalizedDataInstance();
		mean.setLocality(loc);
		mean.measurements = 0;
			
		for(int i = i0; i >= 0; i--) {
			if(get(i).sortingDistanceTo(loc) > span) break;		
			DataType point = get(i);
			if(consistencyReference != null) if(!point.isConsistentWith(consistencyReference)) continue;
			
			mean.average(point, env, getRelativeWeight(point.distanceTo(loc)));
		}
	
		for(int i = i0+1; i < size(); i++) {
			if(get(i).sortingDistanceTo(loc) > span) break;
			DataType point = get(i);
			if(consistencyReference != null) if(point.isConsistentWith(consistencyReference)) continue;
			
			mean.average(point, env, getRelativeWeight(point.distanceTo(loc)));
		}
			
		return mean;
		
	}
	
	/**
	 * Gets the checked local average.
	 *
	 * @param loc the loc
	 * @return the checked local average
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public final DataType getCheckedLocalAverage(Locality loc) throws ArrayIndexOutOfBoundsException {
		return getCheckedLocalAverage(loc, null);
	}
	
	/**
	 * Gets the checked local average.
	 *
	 * @param loc the loc
	 * @param env the env
	 * @return the checked local average
	 * @throws ArrayIndexOutOfBoundsException the array index out of bounds exception
	 */
	public DataType getCheckedLocalAverage(Locality loc, Object env) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, env, getLocalAverage(loc, env, null));
	}
	
	
}

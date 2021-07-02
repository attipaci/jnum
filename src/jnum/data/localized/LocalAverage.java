/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.data.localized;

import java.util.ArrayList;

import jnum.Copiable;
import jnum.ExtraMath;
import jnum.math.LinearAlgebra;
import jnum.math.Metric;


/**
 * A class for deriving localized averages
 * 
 * @author Attila Kovacs
 *
 * @param <L>   the generic type of objects that represents a location, such as a number or a vector.
 * @param <D>       the generic type of the object that accompanies the location.
 *
 */
public abstract class LocalAverage <L extends Locality, D extends LinearAlgebra<? super D> & Copiable<? super D> & Metric<? super D>> 
extends ArrayList<LocalizedData<L, D>> {

	private static final long serialVersionUID = 786022102371734003L;

	private double span = 3.0; // Maximum normalized distance (w.r.t. 1-sigma radius)

	/**
	 * Gets the data index that is just before the specified locality of interest along
	 * the 1D sorting direction (or path).
	 * 
	 * @param loc      the locality of interest
	 * @return         index of data that has a sotring value just below that of the locality of interest.
	 * @throws ArrayIndexOutOfBoundsException  if the sorting value for the locality of interest is outside the
	 *                 range of 1D sorting values in this dataset.
	 */
	public int indexBefore(L loc) throws ArrayIndexOutOfBoundsException {
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
	
	/**
	 * Gets the downweighting function for averaging data at different distances from the locality of interest.
	 * By default it is a Gaussian weighting with distance.
	 * 
	 * @param normalizedDistance       The normalized distance
	 * @return                         The corresponding relative weight to use when averaging data from that distance.
	 */
	public double getRelativeWeight(double normalizedDistance) {
		return Math.exp(-0.5 * normalizedDistance * normalizedDistance);
	}
	

		
	/**
	 * Accumulates a nearby datum into a fixed-locality average.
	 * 
	 * @param point        the other datum to consider for averaging with this one.
	 * @param radius       characteristic (1-sigma) radius for (Gaussian) downweighting with distance.
	 * @param reference    a reference value that is used for consistency checking (see {@link LocalizedData#isConsistentWith(LinearAlgebra)}),
	 *                     or <code>null</code> to skip consistency checking altogether.
	 * @param mean         the current mean value at the fixed location for which the mean is to be derived.
	 * @return             <code>true</code> if the point checked is within the distance span from the mean's location.
	 *                     Otherwise <code>false</code>. 
	 */
	private boolean average(LocalizedData<L, D> point, double radius, D reference, LocalizedData<L, D> mean) {
	    if(point.sortingDistanceTo(mean.getLocality()) > 3.0 * radius) return false;        
        if(reference != null) if(!point.isConsistentWith(reference)) return true;
        
        double d = point.distanceTo(mean.getLocality()) / radius;
        if(d > span) return true;
        
        mean.averageWith(point, getRelativeWeight(d));
        return true;
	}

	/**
	 * Gets a locally averaged value around some location of interest.
     * 
     * @param loc          a location for which we want to obtain a local average
     * @param radius       characteristic (1-sigma) radius for (Gaussian) downweighting with distance.
     * @param reference    a reference value that is used for consistency checking (see {@link LocalizedData#isConsistentWith(LinearAlgebra)}),
     *                     or <code>null</code> to skip consistency checking altogether.
     * @return      the distance-weighted mean value at the fixed location for which the mean is to be derived.
     * 
     * @see #getLocalAverage(Locality, double)
     * @see #getCheckedLocalAverage(Locality, double)
     * 
     */
	public LocalizedData<L, D> getLocalAverage(L loc, double radius, D reference) throws ArrayIndexOutOfBoundsException {
		int i0 = indexBefore(loc);
		
		LocalizedData<L, D> mean = get(i0).newInstanceAt(loc);
			
		for(int i = i0; i >= 0; i--) if(!average(get(i), radius, reference, mean)) break;
		for(int i = i0+1; i < size(); i++) if(!average(get(i), radius, reference, mean)) break;
			
		return mean;	
	}

	/**
     * Gets a locally averaged value around some location of interest without discriminating.
     * 
     * @param loc       a location for which we want to obtain a local average
     * @param radius    characteristic (1-sigma) radius for (Gaussian) downweighting with distance.
     * @return          the distance-weighted mean value at the fixed location for which the mean is to be derived.
     * 
     * @see #getLocalAverage(Locality, double, LinearAlgebra)
     * 
     */
    public LocalizedData<L, D> getLocalAverage(L loc, double radius) throws ArrayIndexOutOfBoundsException{
        return getLocalAverage(loc, radius, null);
    }

	/**
     * Gets a locally averaged value around some location of interest, discriminating outliers. This
     * method calculates two averages, and uses the first average to doscriminate outlying data
     * when calculating the second 'checked' average.
     * 
     * @param loc       a location for which we want to obtain a local average
     * @param radius    characteristic (1-sigma) radius for (Gaussian) downweighting with distance.
     * @return          the distance-weighted robust mean value at the fixed location for which the mean is to be derived.
     * 
     */
	public LocalizedData<L, D> getCheckedLocalAverage(L loc, double radius) throws ArrayIndexOutOfBoundsException {
		return getLocalAverage(loc, radius, getLocalAverage(loc, radius).getData());
	}

}

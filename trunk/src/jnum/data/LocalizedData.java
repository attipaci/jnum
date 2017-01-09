/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.data;

import java.io.Serializable;

import jnum.math.Metric;


// TODO: Auto-generated Javadoc
/**
 * The Class LocalizedData.
 */
public abstract class LocalizedData implements Serializable, Comparable<LocalizedData>, Metric<LocalizedData> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2776764506885561864L;
	/** The measurements. */
	public int measurements = 1;
	
	/**
	 * Gets the locality.
	 *
	 * @return the locality
	 */
	public abstract Locality getLocality();

	/**
	 * Sets the locality.
	 *
	 * @param loc the new locality
	 */
	public abstract void setLocality(Locality loc);
	
	/**
	 * Average.
	 *
	 * @param other the other
	 * @param env the env
	 * @param relativeWeight the relative weight
	 */
	public void average(LocalizedData other, Object env, double relativeWeight) {
		averageWidth(other, env, relativeWeight);
		measurements += other.measurements;
	}
	
	/**
	 * Average width.
	 *
	 * @param other the other
	 * @param env the env
	 * @param relativeWeight the relative weight
	 */
	protected abstract void averageWidth(LocalizedData other, Object env, double relativeWeight);
	
	
	/**
	 * Checks if is consistent with.
	 *
	 * @param other the other
	 * @return true, if is consistent with
	 */
	public boolean isConsistentWith(LocalizedData other) { return true; }
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LocalizedData other) { return getLocality().compareTo(other.getLocality()); }
	
	/**
	 * Compare to.
	 *
	 * @param loc the loc
	 * @return the int
	 */
	public int compareTo(Locality loc) { return getLocality().compareTo(loc); }
	
	/**
	 * Distance to.
	 *
	 * @param loc the loc
	 * @return the double
	 */
	public double distanceTo(Locality loc) {
		return getLocality().distanceTo(loc);
	}
	
	/* (non-Javadoc)
	 * @see jnum.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(LocalizedData other) {
		return distanceTo(other.getLocality());
	}

	/**
	 * Sorting distance to.
	 *
	 * @param loc the loc
	 * @return the double
	 */
	public double sortingDistanceTo(Locality loc) {
		return getLocality().sortingDistanceTo(loc);
	}
	
	/**
	 * Sorting distance to.
	 *
	 * @param other the other
	 * @return the double
	 */
	public double sortingDistanceTo(LocalizedData other) {
		return sortingDistanceTo(other.getLocality());
	}

	
}



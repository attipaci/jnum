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

// TODO: Auto-generated Javadoc
/**
 * The Interface Timed2D.
 */
public interface Timed2D {

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public double[][] getTime();
	
	/**
	 * Sets the time.
	 *
	 * @param image the new time
	 */
	public void setTime(double[][] image);
	
	/**
	 * Gets the time.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the time
	 */
	public double getTime(int i, int j);
	
	/**
	 * Sets the time.
	 *
	 * @param i the i
	 * @param j the j
	 * @param t the t
	 */
	public void setTime(int i, int j, double t);
	
	/**
	 * Increment time.
	 *
	 * @param i the i
	 * @param j the j
	 * @param dt the dt
	 */
	public void incrementTime(int i, int j, double dt);
	
}

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
 * The Interface Noise2D.
 */
public interface Noise2D extends Weighted2D {

	/**
	 * Gets the rms.
	 *
	 * @return the rms
	 */
	public double[][] getRMS();
	
	/**
	 * Sets the rms.
	 *
	 * @param image the new rms
	 */
	public void setRMS(double[][] image);
	
	/**
	 * Scale rms.
	 *
	 * @param factor the factor
	 */
	public void scaleRMS(double factor);
	
	/**
	 * Gets the rms.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the rms
	 */
	public double getRMS(int i, int j);
	
	/**
	 * Sets the rms.
	 *
	 * @param i the i
	 * @param j the j
	 * @param sigma the sigma
	 */
	public void setRMS(int i, int j, double sigma);
	
	/**
	 * Scale rms.
	 *
	 * @param i the i
	 * @param j the j
	 * @param factor the factor
	 */
	public void scaleRMS(int i, int j, double factor);

	/**
	 * Gets the s2 n.
	 *
	 * @return the s2 n
	 */
	public double[][] getS2N();
	
	/**
	 * Gets the s2 n.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the s2 n
	 */
	public double getS2N(int i, int j);
	
}



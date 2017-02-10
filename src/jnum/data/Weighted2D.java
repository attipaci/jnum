/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.data;

// TODO: Auto-generated Javadoc
/**
 * The Interface Weighted2D.
 */
public interface Weighted2D {

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public double[][] getWeight();
	
	/**
	 * Sets the weight.
	 *
	 * @param image the new weight
	 */
	public void setWeight(double[][] image);
	
	/**
	 * Gets the weight.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the weight
	 */
	public double getWeight(int i, int j);
	
	/**
	 * Scale weight.
	 *
	 * @param factor the factor
	 */
	public void scaleWeight(double factor);
	
	/**
	 * Sets the weight.
	 *
	 * @param i the i
	 * @param j the j
	 * @param weight the weight
	 */
	public void setWeight(int i, int j, double weight);
	
	/**
	 * Increment weight.
	 *
	 * @param i the i
	 * @param j the j
	 * @param dw the dw
	 */
	public void incrementWeight(int i, int j, double dw);
	
	/**
	 * Scale weight.
	 *
	 * @param i the i
	 * @param j the j
	 * @param factor the factor
	 */
	public void scaleWeight(int i, int j, double factor);

}

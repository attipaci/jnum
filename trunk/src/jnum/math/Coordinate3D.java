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
package jnum.math;

// TODO: Auto-generated Javadoc
/**
 * The Class Coordinate3D.
 */
public class Coordinate3D {
	
	/** The z. */
	private double x, y, z;
	
	/**
	 * Instantiates a new coordinate3 d.
	 */
	public Coordinate3D() {
		this(0.0, 0.0, 0.0);
	}
	
	/**
	 * Instantiates a new coordinate3 d.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public Coordinate3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double x() { return x; }
	
	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double y() { return y; }
	
	/**
	 * Gets the z.
	 *
	 * @return the z
	 */
	public double z() { return z; }
	
	/**
	 * Sets the x.
	 *
	 * @param value the new x
	 */
	public void setX(double value) { this.x = value; }
	
	/**
	 * Sets the y.
	 *
	 * @param value the new y
	 */
	public void setY(double value) { this.y = value; }
	
	/**
	 * Sets the z.
	 *
	 * @param value the new z
	 */
	public void setZ(double value) { this.z = value; }
	
}

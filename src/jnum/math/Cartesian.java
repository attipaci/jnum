/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.math;

// TODO: Auto-generated Javadoc
/**
 * The Class Cartesian.
 */
public class Cartesian extends CoordinateSystem {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 487281387200705838L;


	/**
	 * Instantiates a new cartesian.
	 *
	 * @param axes the axes
	 */
	public Cartesian(int axes) {
		super("Cartesian Coordinates");
		int n1 = Math.min(axes, labels.length);
		for(int i=0; i < n1; i++) add(new CoordinateAxis(labels[i]));
		for(int i=labels.length; i<axes; i++) add(new CoordinateAxis("t" + (i-labels.length+1)));
	}
	
	/**
	 * Instantiates a new cartesian.
	 *
	 * @param names the names
	 */
	public Cartesian(String[] names) {
		for(int i=0; i< names.length; i++) add(new CoordinateAxis(names[i]));		
	}
	
	/** The labels. */
	String[] labels = { "x", "y", "z", "u", "v", "w", "t" };
	
}

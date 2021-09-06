/* *****************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.math;

/**
 * A Cartesian coordinate system.
 * 
 * @author Attila Kovacs
 *
 */
public class CartesianSystem extends CoordinateSystem {
    /** */
	private static final long serialVersionUID = 487281387200705838L;

	/**
	 * Constructs a Cartesian coordinate system with the specified number of axes and 
	 * default axes labels (assigned in order as 'x', 'y', 'z', 'u', 'v', 'w', 't', 
	 * 't1', 't2'...).  
	 * 
	 * @param axes     The number of axes in this Cartesian system.
	 */
	public CartesianSystem(int axes) {
		super("Cartesian Coordinates");
		int n1 = Math.min(axes, defaultLabels.length);
		for(int i=0; i < n1; i++) add(new CoordinateAxis(defaultLabels[i]));
		for(int i=defaultLabels.length; i<axes; i++) add(new CoordinateAxis("t" + (i-defaultLabels.length+1)));
	}
	
	/**
	 * Constructs a Cartesian coordinate system with the named axes specified.
	 * 
	 * @param names    An array or comma-separated list of axis names.
	 */
	public CartesianSystem(String... names) {
	    for(int i=0; i< names.length; i++) add(new CoordinateAxis(names[i]));  
	}

	private final static String[] defaultLabels = { "x", "y", "z", "u", "v", "w", "t" };
	
}

/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.math;


public class CartesianSystem extends CoordinateSystem {

	private static final long serialVersionUID = 487281387200705838L;


	public CartesianSystem(int axes) {
		super("Cartesian Coordinates");
		int n1 = Math.min(axes, labels.length);
		for(int i=0; i < n1; i++) add(new CoordinateAxis(labels[i]));
		for(int i=labels.length; i<axes; i++) add(new CoordinateAxis("t" + (i-labels.length+1)));
	}
	

	public CartesianSystem(String[] names) {
		for(int i=0; i< names.length; i++) add(new CoordinateAxis(names[i]));		
	}

	String[] labels = { "x", "y", "z", "u", "v", "w", "t" };
	
}

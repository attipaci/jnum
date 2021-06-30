/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

import java.util.Vector;

/**
 * A class representation of a coordinate system a collection of Coordinate axes.
 * 
 * @author Attila Kovacs
 *
 */
public class CoordinateSystem extends Vector<CoordinateAxis> {

	private static final long serialVersionUID = 7965280172336615563L;

	private String name = "Default Coordinate System";

	public CoordinateSystem() {}
	

	public CoordinateSystem(String text) { name = text; }
	
	
	public CoordinateSystem(int dimension) {
		for(int i=0; i<dimension; i++)
			add(new CoordinateAxis(defaultLabel[i%defaultLabel.length] 
			        + (dimension > defaultLabel.length ? i/defaultLabel.length + "" : "")));
	}

	@Override
	public boolean add(CoordinateAxis axis) {
	    if(contains(axis.getShortLabel()) || contains(axis.getLabel()) || contains(axis.getFancyLabel()))
	        throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
		return super.add(axis);
	}

	@Override
	public void add(int index, CoordinateAxis axis) {
		if(contains(axis.getShortLabel()) || contains(axis.getLabel()) || contains(axis.getFancyLabel()))
		    throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
		super.add(index, axis);
	}

	@Override
	public void insertElementAt(CoordinateAxis axis, int index) {
		add(index, axis);
	}

	
	public CoordinateSystem(String text, int dimension) {
		this(dimension);
		name = text;
	}
	
	
	public CoordinateSystem(CoordinateSystem template) {
		copy(template);
	}
	

	public void copy(CoordinateSystem template) {
		setName(template.getName());
		for(CoordinateAxis axis : template) add(axis.clone());
	}
	

	public void setName(String text) { name = text; }


	public String getName() { return name; }


	public boolean contains(String name) {
		for(CoordinateAxis axis : this) {
		    if(axis.getLabel().equals(name)) return true;
		    if(axis.getFancyLabel().equals(name)) return true;
		    if(axis.getShortLabel().equals(name)) return true;
		}
		return false;
	}
	
	
	public CoordinateAxis getAxis(String name) {
		for(int i=size(); --i >= 0; ) {
		    if(get(i).getShortLabel().equals(name)) return get(i);
		    if(get(i).getLabel().equals(name)) return get(i);
		    if(get(i).getFancyLabel().equals(name)) return get(i);
		}
		return null;
	}
	

	protected static String[] defaultLabel = { "x", "y", "z", "u", "v", "w" };


}


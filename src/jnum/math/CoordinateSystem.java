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
// Copyright (c) 2007 Attila Kovacs 

package jnum.math;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class CoordinateSystem.
 */
public class CoordinateSystem extends Vector<CoordinateAxis> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7965280172336615563L;
	
	/** The name. */
	private String name = "Default Coordinate System";

	/**
	 * Instantiates a new coordinate system.
	 */
	public CoordinateSystem() {}
	
	/**
	 * Instantiates a new coordinate system.
	 *
	 * @param text the text
	 */
	public CoordinateSystem(String text) { name = text; }
	
	/**
	 * Instantiates a new coordinate system.
	 *
	 * @param dimension the dimension
	 */
	public CoordinateSystem(int dimension) {
		for(int i=0; i<dimension; i++)
			add(new CoordinateAxis(defaultLabel[i%defaultLabel.length] 
			        + (dimension > defaultLabel.length ? i/defaultLabel.length + "" : "")));
	}
	
	/* (non-Javadoc)
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	@Override
	public boolean add(CoordinateAxis axis) {
	    if(contains(axis.getShortLabel()) || contains(axis.getLongLabel()) || contains(axis.getFancyLabel()))
	        throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
		return super.add(axis);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	@Override
	public void add(int index, CoordinateAxis axis) {
		if(contains(axis.getShortLabel()) || contains(axis.getLongLabel()) || contains(axis.getFancyLabel()))
		    throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
		super.add(index, axis);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Vector#insertElementAt(java.lang.Object, int)
	 */
	@Override
	public void insertElementAt(CoordinateAxis axis, int index) {
		add(index, axis);
	}

	
	/**
	 * Instantiates a new coordinate system.
	 *
	 * @param text the text
	 * @param dimension the dimension
	 */
	public CoordinateSystem(String text, int dimension) {
		this(dimension);
		name = text;
	}
	
	/**
	 * Instantiates a new coordinate system.
	 *
	 * @param template the template
	 */
	public CoordinateSystem(CoordinateSystem template) {
		copy(template);
	}
	
	/**
	 * Copy.
	 *
	 * @param template the template
	 */
	public void copy(CoordinateSystem template) {
		setName(template.getName());
		for(CoordinateAxis axis : template) add((CoordinateAxis) axis.clone());
	}
	
	/**
	 * Sets the name.
	 *
	 * @param text the new name
	 */
	public void setName(String text) { name = text; }

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return name; }

	/**
	 * Contains.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	/*
	 * Contains.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean contains(String name) {
		for(CoordinateAxis axis : this) {
		    if(axis.getLongLabel().equals(name)) return true;
		    if(axis.getFancyLabel().equals(name)) return true;
		    if(axis.getShortLabel().equals(name)) return true;
		}
		return false;
	}
	
	
	/**
	 * Gets the axis.
	 *
	 * @param name the name
	 * @return the axis
	 */
	public CoordinateAxis getAxis(String name) {
		for(int i=size(); --i >= 0; ) {
		    if(get(i).getShortLabel().equals(name)) return get(i);
		    if(get(i).getLongLabel().equals(name)) return get(i);
		    if(get(i).getFancyLabel().equals(name)) return get(i);
		}
		return null;
	}
	
	/** The default label. */
	protected static String[] defaultLabel = { "x", "y", "z", "u", "v", "w" };


}


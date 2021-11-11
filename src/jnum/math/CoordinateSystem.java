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

package jnum.math;

import java.util.Vector;

import jnum.CopyCat;

/**
 * A coordinate system as a collection of coordinate axes.
 * 
 * @author Attila Kovacs
 *
 */
public class CoordinateSystem extends Vector<CoordinateAxis> implements CopyCat<CoordinateSystem>{
    /** */
    private static final long serialVersionUID = 7965280172336615563L;

    /** the name of the coordinate system */
    private String name = "Default Coordinate System";

    /** Instanbtiates a new coordinate system with the default name */
    public CoordinateSystem() {}

    /** 
     * Instantiates a new coordinate system with the specified name.
     * 
     * @param name     the name of the new coordinate system
     */
    public CoordinateSystem(String name) { this.name = name; }

    /**
     * Instantiates a new coordinate system with a set of default axes with the
     * specified dimension.
     * 
     * @param dimension    the dimension of the new coordinate system
     */
    public CoordinateSystem(int dimension) {
        for(int i=0; i<dimension; i++)
            add(new CoordinateAxis(defaultLabel[i%defaultLabel.length] 
                    + (dimension > defaultLabel.length ? i/defaultLabel.length + "" : "")));
    }

    public CoordinateSystem(String text, int dimension) {
        this(dimension);
        name = text;
    }


    public CoordinateSystem(CoordinateSystem template) {
        copy(template);
    }


    @Override
    public boolean add(CoordinateAxis axis) {
        if(containsAxis(axis.getShortLabel()) || containsAxis(axis.getLabel()) || containsAxis(axis.getFancyLabel()))
            throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
        return super.add(axis);
    }

    @Override
    public void add(int index, CoordinateAxis axis) {
        if(containsAxis(axis.getShortLabel()) || containsAxis(axis.getLabel()) || containsAxis(axis.getFancyLabel()))
            throw new IllegalArgumentException(getClass().getName() + " already has axis '" + axis.getShortLabel());
        super.add(index, axis);
    }

    @Override
    public void insertElementAt(CoordinateAxis axis, int index) {
        add(index, axis);
    }

    
    @Override
    public void copy(CoordinateSystem template) {
        setName(template.getName());
        for(CoordinateAxis axis : template) add(axis.clone());
    }

    /**
     * Sets a new name for this coordinate system.
     * 
     * @param name      the new name for the coordinate system.
     * 
     * @see #getName()
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the name of this coordinate system.
     * 
     * @return      the name of this coordinate system.
     * 
     * @see #setName(String)
     */
    public String getName() { return name; }

    /**
     * Checks if this coordinate system contains an axis by the specified name.
     * 
     * @param name      the axis name to check.
     * @return          <code>true</code> if this coordinate system has an axis by the specified name (case-sensitive),
     *                  or else <code>false</code>.
     *                  
     * @see #getAxis(String)
     * @see #add(CoordinateAxis)
     * @see #add(int, CoordinateAxis)
     */
    public boolean containsAxis(String name) {
        for(CoordinateAxis axis : this) {
            if(axis.getLabel().equals(name)) return true;
            if(axis.getFancyLabel().equals(name)) return true;
            if(axis.getShortLabel().equals(name)) return true;
        }
        return false;
    }

    /**
     * Returns the coordinate axis by the specified name.
     * 
     * @param name      the axis name.
     * @return          the coordinate axis by that name, or <code>null</code> if this coordinate system
     *                  contains no axis by the specified name.
     *                  
     * @see #containsAxis(String)
     * @see #add(CoordinateAxis)
     * @see #add(int, CoordinateAxis)
     */
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


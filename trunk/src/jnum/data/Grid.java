/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.io.Serializable;

import jnum.NonConformingException;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsHeaderParsing;
import jnum.math.CoordinateSystem;

// TODO: Auto-generated Javadoc
/**
 * The Interface Grid.
 *
 * @param <CoordinateType> the generic type
 * @param <OffsetType> the generic type
 */
public abstract class Grid<CoordinateType, OffsetType> implements Serializable, Cloneable, FitsHeaderEditing, FitsHeaderParsing {

    /**
     * 
     */
    private static final long serialVersionUID = -8132999541122592649L;

    private CoordinateSystem coordinateSystem;
    
    private int variant = 0;
    
    
    public final CoordinateSystem getCoordinateSystem() { return coordinateSystem; }


    public final void setCoordinateSystem(CoordinateSystem system) {
        if(system.size() != dimension()) throw new NonConformingException("coordinate system / grid mismatch.");
        coordinateSystem = system;
    }
    
   
   
    public final int getVariant() { return variant; }
    
    public final void setVariant(int index) { this.variant = index; }
    

    protected String getFitsID() {
        return getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
    }
    
    
    /**
     * Sets the reference.
     *
     * @param coords the new reference
     */
    public abstract void setReference(CoordinateType coords);

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public abstract CoordinateType getReference();

    /**
     * Sets the reference index.
     *
     * @param index the new reference index
     */
    public abstract void setReferenceIndex(OffsetType index);

    /**
     * Gets the reference index.
     *
     * @return the reference index
     */
    public abstract OffsetType getReferenceIndex();

    /**
     * Sets the resolution.
     *
     * @param delta the new resolution
     */
    public abstract void setResolution(OffsetType delta);

    /**
     * Gets the resolution.
     *
     * @return the resolution
     */
    public abstract OffsetType getResolution();



    public abstract void coordsAt(OffsetType index, CoordinateType toValue);
    
    public abstract void indexOf(CoordinateType value, OffsetType toIndex);
 

    public abstract int dimension();

   
    
    

}

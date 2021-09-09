/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data;

import java.io.Serializable;

import jnum.NonConformingException;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsHeaderParsing;
import jnum.math.CoordinateSystem;


/** 
 * A base class for coordinate grids, which provide a mapping between floating-point data indices and correasponsing physical
 * coordinates.
 *  
 * @author Attila Kovacs
 *
 * @param <CoordinateType>  the generic type of physical coordinates for location on this grid.
 * @param <OffsetType>      the generic type flat-space local coordinates, used for expressing projected offsets
 *                          and fractional grid indices alike.
 */
public abstract class Grid<CoordinateType, OffsetType> implements Serializable, Cloneable,
FitsHeaderEditing, FitsHeaderParsing {

    /**
     * 
     */
    private static final long serialVersionUID = -8132999541122592649L;

    /**
     * The coordinate system for this grid.
     * 
     */
    private CoordinateSystem coordinateSystem;
    
    /**
     * The variant index if the same data may be described by multiple grids. This is used
     * for example to disabiguate the different coordinate grids in the same FITS header.
     */
    private int variant = 0;
    
    /**
     * Gets the physical coordinate system for this grid. 
     * 
     * @return  the underlying physical coordinate system.
     */
    public final CoordinateSystem getCoordinateSystem() { return coordinateSystem; }

    /**
     * Sets a new physical coordinate system for this grid.
     * 
     * @param system    the new underlying physical coordinate system.
     *                  
     * @see #setReference(Object)
     * @see #setReferenceIndex(Object)
     */
    public final void setCoordinateSystem(CoordinateSystem system) {
        if(system.size() != dimension()) throw new NonConformingException("coordinate system / grid mismatch.");
        coordinateSystem = system;
    }
    
   
    /**
     * Gets the grid variant index if the same data may be described by multiple grids. When multiple
     * grids describe the same data they should each have a distinct variant index, with 0 being that
     * of the default coordinate grid. This is used for example to disabiguate the different coordinate 
     * grids in the same FITS header. The variant index is converted to a FITS variant letter for the
     * those keywords that describe coordinate systems., such as CTYPE1, which for variant=1 will
     * become CTYPE1B in FITS.
     * 
     * @return  the variant index for this grid. The default grid has index 0.
     * 
     * @see #setVariant(int)
     */
    public final int getVariant() { return variant; }
    
    /**
     * Sets a new grid variant index if the same data may be described by multiple grids. When multiple
     * grids describe the same data they should each have a distinct variant index, with 0 being that
     * of the default coordinate grid. This is used for example to disabiguate the different coordinate 
     * grids in the same FITS header. The variant index is converted to a FITS variant letter for the
     * those keywords that describe coordinate systems., such as CTYPE1, which for variant=1 will
     * become CTYPE1B in FITS.
     * 
     * @param index     the new variant index for this grid. The default grid has index 0.
     */
    public final void setVariant(int index) { this.variant = index; }
    
    /**
     * Returns the FITS coordinate variant ID, which is usually a letter code A through Z.
     * 
     * @return  the letter ID to tag FITS coordinate keywords with for the FITS description of this
     *          coordinate grid. E.g. if the ID is 'C' then the CDELT1 type FITS keyword for this grid
     *          CDETL1C. 
     */
    protected String getFitsVariant() {
        return getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
    }
    
    /**
     * Sets new coordinates for the reference point on this grid.
     * 
     * @param coords    the new coordinates for the reference point on this grid.
     * 
     * @see #setReferenceIndex(Object)
     * @see #setResolution(Object)
     */
    public abstract void setReference(CoordinateType coords);

    /**
     * Gets the coordinates for the reference point on this grid.
     * 
     * @return      the coordinates for the reference point on this grid.
     * 
     * @see #getReferenceIndex()
     * @see #getResolution()
     */
    public abstract CoordinateType getReference();

    /**
     * Sets a new data index for the reference point on this grid. The reference point need
     * not fall on an integer-indexed grid location, and can be in-between the fixed grid locations.
     * Hence the index for the reference point is represented by a vector type, with floating-point
     * index components. 
     * 
     * @param index     the new fractional data index of the reference point on this grid.
     * 
     * @see #setReference(Object)
     */
    public abstract void setReferenceIndex(OffsetType index);

    /**
     * Gets the data index for the reference point on this grid. The reference point need
     * not fall on an integer-indexed grid location, and can be in-between the fixed grid locations.
     * Hence the index for the reference point is represented by a vector type, with floating-point
     * index components. 
     * 
     * @return    the fractional data index of the reference point on this grid.
     * 
     * @see #getReference()
     */
    public abstract OffsetType getReferenceIndex();

    /**
     * Sets a new physical resolution for this grid along all grid dimensions, in the flat-space 
     * local coordinate system that is tangential to the reference point of this grid. The grid itself is
     * generally a flat-space projection of some coordinate space around the reference point. 
     * The resolution is the distance between regularly sampled locations in this flat
     * space.
     * 
     * @param delta     the grid's new physical resolution, that is the distance between the closest
     *                  neighboring grid locations on the projected flat-space around the
     *                  reference point along all grid dimensions.
     * 
     * @see #setReference(Object)
     */
    public abstract void setResolution(OffsetType delta);

    /**
     * Gets the physical resolution for this grid along all grid dimensions, in the flat-space 
     * local coordinate system that is tangential to the reference point of this grid. The grid itself is
     * generally a flat-space projection of some coordinate space around the reference point. 
     * The resolution is the distance between regularly sampled locations in this flat
     * space.
     * 
     * @return          the grid's physical resolution, that is the distance between the closest
     *                  neighboring grid locations on the projected flat-space around the
     *                  reference point along all grid dimensions.
     *                  
     * @see #getReference()
     */
    public abstract OffsetType getResolution();

    /**
     * Converts fractional indices on this grid to physical coordinates for the same location,
     * returning the result in the supplied coordinates. Because the result is returned
     * in an object supplied by the caller, the call avoid creating new Java object, thus
     * allowing for superior performance when called from a loop with the same
     * second argument, for example.
     * 
     * @param index     the fractional grid index of an arbitrary location on this grid, which
     *                  may be between integer-indexed grid positions.
     * @param toCoords  the coordinates in which the corresponding physical location is returned.
     * 
     * @see #indexOf(Object, Object)
     */
    public abstract void coordsAt(OffsetType index, CoordinateType toCoords);
    
    /**
     * Converts physical coordinate locations to fractional data indices on this grid,
     * returning result in the supplied coordinates. Because the result is returned
     * in an object supplied by the caller, the call avoid creating new Java object, thus
     * allowing for superior performance when called from a loop with the same
     * second argument, for example.
     * 
     * @param coords    the physical coordinates at some location    
     * @param toIndex   the vector in which to return the corresponding factional grid indices for the
     *                  same location.  
     */
    public abstract void indexOf(CoordinateType coords, OffsetType toIndex);
 
    /**
     * Returns the dimensions of the space that this grid describes. For example 2, if this grid is 
     * planar, or it it describes locations on a sphere.
     * 
     * @return  the dimensions of the space described by this grid.
     */
    public abstract int dimension();

}

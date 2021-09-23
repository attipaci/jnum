/* *****************************************************************************
 * Copyright (c) 2019 Attila Kovacs <attila[AT]sigmyne.com>.
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
package jnum.projection;

import java.io.Serializable;

import jnum.Copiable;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;

/**
 * <p>
 * A class for facilitating computationally efficient projection of coordinate pairs onto a 2D plane, and vice versa.
 * The class avoids the need to create on-the-fly objects for the conversion. However, for best performance each projector
 * instance should be used with only one thread to avoid concurrent blocking. 
 * </p>
 * <p>
 * The class also allows to use mirrored and/or upside down projections relative to that of the projection class it
 * uses, hence allowing for changing the coordinate directions relative to the orientation defined by the projection itself.
 * <p>
 * 
 * @author Attila Kovacs
 *
 * @param <CoordinateType>      the generic type of 2D coordinates that can be projected by this instance.
 */
public class Projector2D<CoordinateType extends Coordinate2D> implements Serializable, Cloneable, Copiable<Projector2D<CoordinateType>> {

    private static final long serialVersionUID = -1473954926270300168L;

    private Vector2D userOffset;
    
    private Vector2D projectedOffset;

    private Projection2D<CoordinateType> projection;

    private CoordinateType coords;

    private boolean isMirrored;
    
    private boolean isUpsideDown;
    
    /**
     * Instantiates a new 2D projector, to facilitate efficient transformation between
     * coordinates and projected (flat-space) offsets, especially for non-flat topologies.
     * 
     * @param projection        the 2D projection class to use for converting between coordinates and projected (flat-space) offsets.
     */
    @SuppressWarnings("unchecked")
    public Projector2D(Projection2D<CoordinateType> projection) {
        this.projection = projection;
        coords = (CoordinateType) projection.getReference().clone();
        userOffset = new Vector2D();
        projectedOffset = new Vector2D();
    }


    @SuppressWarnings("unchecked")
    @Override
    public Projector2D<CoordinateType> clone() {
        try {   
            Projector2D<CoordinateType> clone = (Projector2D<CoordinateType>) super.clone(); 
            if(userOffset != null) clone.userOffset = userOffset.copy();
            if(projectedOffset != null) clone.projectedOffset = projectedOffset.copy();
            if(coords != null) clone.coords = (CoordinateType) coords.copy(); 
            return clone;
        }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Projector2D<CoordinateType> copy() {
        Projector2D<CoordinateType> copy = clone();
        if(projection != null) copy.projection = projection.copy();
        return copy;
    }

    /**
     * Returns the coordinates that correspond to the current offsets.
     * 
     * @return The coordinates that match the current offsets.
     * 
     * @see #getOffset()
     * @see #setOffset(Vector2D)
     */
    public CoordinateType getCoordinates() { return coords; }

    /**
     * Returns the offsets that corresponds to the current coordinates.
     * 
     * @return The offsets corresponding to the current coordinates.
     * 
     * @see #getCoordinates()
     * @see #setCoordinates(Coordinate2D)
     */
    public Vector2D getOffset() { return userOffset; }

    /**
     * Changes whether or not the projection has inverted <i>x</i> offsets direction relative to the
     * definition of the projection class used. For example, the common convention of
     * spherical projections is to represent the projected surface as seen looking at the
     * sphere from the outside in. But, sky maps in astronomy are usually shown looking
     * out, hence appear in a mirrored orientation w.r.t. the canonical spherical projection.
     * 
     * @param value     <code>true</code> to use a mirrored <i>x</i> direction for offsets relative
     *                  to the projection class used, otherwise <code>false</code>
     *                  
     * @see #isMirrored()
     * @see #setUpsideDown(boolean)
     */
    public void setMirrored(boolean value) {
        isMirrored = value;
    }
    
    /**
     * Checks if the projector defines <i>x</i> offsets in the opposite (mirrored) direction relative
     * to the projection used.
     * 
     * @return          <code>true</code> if using a mirrored <i>x</i> direction for offsets relative
     *                  to the projection class used, otherwise <code>false</code>
     *                  
     * @see #setMirrored(boolean)
     * @see #isUpsideDown()
     */
    public final boolean isMirrored() { return isMirrored; }
    
    
    /**
     * Changes whether or not the projection has inverted <i>y</i> offsets direction relative to the
     * definition of the projection class used. For example the projection might be oriented
     * in azimuth/elevation, but we are using zenith angle coordinates and offsets which run
     * in opposide to the elevation direction.
     * 
     * @param value     <code>true</code> to use an upside-down <i>x</i> direction for offsets relative
     *                  to the projection class used, otherwise <code>false</code>
     *                  
     * @see #isUpsideDown()
     * @see #setMirrored(boolean)
     */
    public void setUpsideDown(boolean value) {
        isUpsideDown = value;
    }
    
    /**
     * Check if the projector defines <i>y</i> offsets in the opposite (upside-down) direction relative
     * to the projection used.
     * 
     * @return          <code>true</code> if using an upside-down <i>y</i> direction for offsets relative
     *                  to the projection class used, otherwise <code>false</code>
     *                  
     * @see #setUpsideDown(boolean)
     * @see #isMirrored()
     */
    public final boolean isUpsideDown() { return isUpsideDown; }
    
    
    /**
     * Sets the projector to the reference coordinates. Subsequent calls to {@link #getOffset()} will return null vectors accordingly.
     * 
     * @see #setCoordinates(Coordinate2D)
     * 
     */
    public void setReferenceCoords() {
        coords.copy(getProjection().getReference());
        userOffset.zero();
        projectedOffset.zero();
    }

    /**
     * Changes the orientation, that is it calculates inverted x and y offsets as necessary for 
     * mirror or upside-down projections.
     * 
     * @param in    the input offsets (left unchanged).
     * @param out   the reoriented output offsets calculated from the input.
     */
    private void reorient(Vector2D in, Vector2D out) {
        out.setX(isMirrored ? -in.x() : in.x());
        out.setY(isUpsideDown ? -in.y() : in.y());
    }
    
    /**
     * Sets the coordinates to the specified value and recalculates the projected offsets to match, so that
     * subsequent calls to {@link #getCoordinates()} and {@link #getOffset()} return consistent values.
     * 
     * @param coords       The coordinates to project.
     */
    public void setCoordinates(final CoordinateType coords) {
        if(this.coords != coords) this.coords.copy(coords);
        projection.project(coords, projectedOffset);
        reorient(projectedOffset, userOffset);
    }

    /**
     * Sets the projected offsets to the specified value and recalculates the coordinates to match, so that
     * subsequent calls to {@link #getCoordinates()} and {@link #getOffset()} return consistent values.
     * 
     * @param offset       The projected offsets to set.
     */
    public void setOffset(final Vector2D offset) {
        userOffset.copy(offset);
        reorient(userOffset, projectedOffset);
        projection.deproject(projectedOffset, coords);
    }

    /**
     * Recalculates the offset from the current values of the coordinates. This is useful if
     * the coordinates were obtained via {@link #getCoordinates()} and then modified.
     * 
     */
    public void reproject() { setCoordinates(getCoordinates()); }

    /**
     * Returns the underlying object handling the actual projection.
     * 
     * @return  the underlying projection implementation.
     */
    public Projection2D<CoordinateType> getProjection() { return projection; }
}

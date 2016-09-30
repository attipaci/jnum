/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.projection;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Offset2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Projected2D.
 *
 * @param <CoordinateType> the generic type
 */
public class Projected2D<CoordinateType extends Coordinate2D> extends Offset2D {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5475466255246701534L;
    
    /** The projection. */
    private Projection2D<CoordinateType> projection;

  
    /**
     * Instantiates a new projected 2 D.
     *
     * @param projection the projection
     * @param coords the coords
     */
    public Projected2D(Projection2D<CoordinateType> projection, CoordinateType coords) {
        super(projection.getReference());
        this.projection = projection;
        setCoordinates(coords);
    }

    /**
     * Instantiates a new projected 2 D.
     *
     * @param projection the projection
     */
    public Projected2D(Projection2D<CoordinateType> projection) {
        super(projection.getReference());
        this.projection = projection;
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Offset2D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Projected2D)) return false;
        Projected2D<?> offset = (Projected2D<?>) o;
        if(!Util.equals(offset.projection, projection)) return false;
        return super.equals(o);
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Offset2D#hashCode()
     */
    @Override
    public int hashCode() { return super.hashCode() ^ projection.hashCode(); }
    
    /**
     * Sets the coordinates.
     *
     * @param coords the new coordinates
     */
    public void setCoordinates(CoordinateType coords) {
        projection.project(coords, this);
    }
    
    /**
     * Gets the coordinates.
     *
     * @param toCoords the to coords
     * @return the coordinates
     */
    public void getCoordinates(CoordinateType toCoords) {
        projection.deproject(this, toCoords);
    }
    
    /**
     * Gets the coordinates.
     *
     * @return the coordinates
     */
    public final CoordinateType getCoordinates() {
        CoordinateType coords = projection.getCoordinateInstance();
        getCoordinates(coords);
        return coords;
    }
    
    /**
     * Gets the projection.
     *
     * @return the projection
     */
    public Projection2D<CoordinateType> getProjection() { return projection; }
    
   
    /**
     * Gets the reprojected.
     *
     * @param p the p
     * @param temp the temp
     * @return the reprojected
     */
    public Projected2D<CoordinateType> getReprojected(Projection2D<CoordinateType> p, CoordinateType temp) {
        projection.deproject(this, temp);
        return new Projected2D<CoordinateType>(p, temp); // TODO new Vector2D(this); ???
    }
   
    /**
     * Gets the reprojected.
     *
     * @param p the p
     * @return the reprojected
     */
    public final Projected2D<CoordinateType> getReprojected(Projection2D<CoordinateType> p) {
        return getReprojected(p, p.getCoordinateInstance());
    }

}

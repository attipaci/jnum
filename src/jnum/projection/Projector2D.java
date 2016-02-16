/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import java.io.Serializable;

import jnum.math.Coordinate2D;
import jnum.math.Vector2D;


// TODO: Auto-generated Javadoc
/**
 * The Class Projector2D.
 *
 * @param <CoordinateType> the generic type
 */
public class Projector2D<CoordinateType extends Coordinate2D> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1473954926270300168L;

	/** The offset. */
	public Vector2D offset = new Vector2D();
	
	/** The projection. */
	private Projection2D<CoordinateType> projection;
	
	/** The coords. */
	private CoordinateType coords;
	
	
	/**
	 * Instantiates a new projector2 d.
	 *
	 * @param projection the projection
	 */
	@SuppressWarnings("unchecked")
	public Projector2D(Projection2D<CoordinateType> projection) {
		this.projection = projection;
		coords = (CoordinateType) projection.getReference().clone();
	}

	/**
	 * Gets the coordinates.
	 *
	 * @return the coordinates
	 */
	public CoordinateType getCoordinates() { return coords; }
	
	/**
	 * Sets the reference coords.
	 */
	public void setReferenceCoords() {
		coords.copy(getProjection().getReference());
		//offset.zero();
	}
	
	/**
	 * Project.
	 */
	public void project() {
		projection.project(coords, offset);
	}
	
	/**
	 * Deproject.
	 */
	public void deproject() {
		projection.deproject(offset, coords);
	}
	
	/**
	 * Gets the projection.
	 *
	 * @return the projection
	 */
	public Projection2D<CoordinateType> getProjection() { return projection; }
}

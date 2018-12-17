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
package jnum.projection;

import java.io.Serializable;

import jnum.math.Coordinate2D;
import jnum.math.Vector2D;


public class Projector2D<CoordinateType extends Coordinate2D> implements Serializable {

	private static final long serialVersionUID = -1473954926270300168L;

	public Vector2D offset = new Vector2D();

	private Projection2D<CoordinateType> projection;

	private CoordinateType coords;
	

	@SuppressWarnings("unchecked")
	public Projector2D(Projection2D<CoordinateType> projection) {
		this.projection = projection;
		coords = (CoordinateType) projection.getReference().clone();
	}


	public CoordinateType getCoordinates() { return coords; }
	
	public void setReferenceCoords() {
		coords.copy(getProjection().getReference());
		//offset.zero();
	}

	public void project() {
		projection.project(coords, offset);
	}

	public void deproject() {
		projection.deproject(offset, coords);
	}
	
	public Projection2D<CoordinateType> getProjection() { return projection; }
}

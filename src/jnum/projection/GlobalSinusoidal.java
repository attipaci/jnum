/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2007 Attila Kovacs 

package jnum.projection;

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class RadioProjection.
 */
public class GlobalSinusoidal extends SphericalProjection {	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4620095512276967555L;

	/**
	 * Instantiates a new radio projection.
	 */
	public GlobalSinusoidal() {}

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "GLS"; }

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Global Sinusoidal"; }

	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#project(jnum.SphericalCoordinates, jnum.Coordinate2D)
	 */
	@Override
	public final void project(final SphericalCoordinates coords, final Coordinate2D toProjected) {
		toProjected.set(
				Math.IEEEremainder(coords.x() - getReference().x(), twoPI) * coords.cosLat(),
				coords.y() - getReference().y()
		);
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#deproject(jnum.Coordinate2D, jnum.SphericalCoordinates)
	 */
	@Override
	public final void deproject(final Coordinate2D projected, final SphericalCoordinates toCoords) {
		toCoords.setY(getReference().y() + projected.y());
		toCoords.setX(getReference().x() + projected.x() / toCoords.cosLat());
	}
	
	
	// These are not used thanks to the overriding of the projection equations...
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#getOffsets(double, double, jnum.Coordinate2D)
	 */
	@Override
	protected void getOffsets(final double theta, final double phi, Coordinate2D toOffset) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#phi(jnum.Coordinate2D)
	 */
	@Override
	protected void getPhiTheta(Coordinate2D offset, SphericalCoordinates phiTheta) {
		throw new UnsupportedOperationException("Not implemented.");
	}


}

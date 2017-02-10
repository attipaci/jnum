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

package jnum.projection;

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class SansonFlamsteed.
 */
public class SansonFlamsteed extends CylindricalProjection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1807113394531878268L;

	/**
	 * Instantiates a new Sanson-Flamsteed projection.
	 */
	public SansonFlamsteed() {}

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "SFL"; }

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Sanson-Flamsteed"; }

	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#phi(jnum.Coordinate2D)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		phiTheta.setY(offset.y());
		phiTheta.setX(offset.x() / phiTheta.cosLat());
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#getOffsets(double, double, jnum.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		toOffset.set(phi * Math.cos(theta), theta);
	}


	
}

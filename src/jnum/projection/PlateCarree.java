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
// Copyright (c) 2007 Attila Kovacs 

package jnum.projection;

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


// TODO: Auto-generated Javadoc
/**
 * The Class PlateCarree.
 */
public class PlateCarree extends CylindricalProjection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7736185332601427634L;

	/**
	 * Instantiates a new plate carree.
	 */
	public PlateCarree() {}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "CAR"; }

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Plate carree"; }

	/* (non-Javadoc)
	 * @see kovacs.util.SphericalProjection#phi(kovacs.util.Coordinate2D)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) { 
		phiTheta.copy(offset);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalProjection#getOffsets(double, double, kovacs.util.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		toOffset.set(phi, theta);
	}

	

	
}

/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila[AT]sigmyne.com>.
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

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class ParabolicProjection.
 */
public class ParabolicProjection extends CylindricalProjection {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9078430471620050294L;


	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getPhiTheta(kovacs.math.Coordinate2D, kovacs.math.SphericalCoordinates)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		final double Y = offset.y() / Math.PI;
		phiTheta.setNative(offset.x() / (1.0 - 4.0 * Y * Y), 3.0 * asin(Y));
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getOffsets(double, double, kovacs.math.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		toOffset.set(phi * (2.0 * Math.cos(twoThirds * theta) - 1.0), Math.PI * Math.sin(third * theta));
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return "PAR";
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return "Parabolic Projection";
	}

	
	/** The Constant twoThirds. */
	private final static double twoThirds = 2.0 / 3.0;
	
	/** The Constant third. */
	private final static double third = 1.0 / 3.0;
}

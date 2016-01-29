/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class Polyconic.
 */
public class Polyconic extends SphericalProjection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2600070844940523551L;

	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getPhiTheta(kovacs.math.Coordinate2D, kovacs.math.SphericalCoordinates)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		throw new UnsupportedOperationException("Polyconic deprojection not implemented");
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getOffsets(double, double, kovacs.math.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		double t = Math.tan(theta);
		final double cotTheta = Double.isInfinite(t) ? 0.0 : 1.0 / t;
		t = phi * Math.sin(theta);
		toOffset.set(cotTheta * Math.sin(t), theta + cotTheta * (1.0 - Math.cos(t)));
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return "PCO";
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return "Polyconic";
	}

}

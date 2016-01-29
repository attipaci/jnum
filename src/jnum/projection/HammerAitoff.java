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
 * The Class HammerAitoff.
 */
public class HammerAitoff extends CylindricalProjection {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5636733533889649074L;

	/**
	 * Instantiates a new hammer aitoff.
	 */
	public HammerAitoff() {}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "AIT"; }

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Hammer-Aitoff"; }

	/* (non-Javadoc)
	 * @see kovacs.util.SphericalProjection#phi(kovacs.util.Coordinate2D)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		final double Z2 = Z2(offset);
		final double Z = Math.sqrt(Z2);
		phiTheta.setNative(2.0 * Math.atan2(0.5 * Z * offset.x(), 2.0 * Z2 - 1.0), asin(offset.y() * Z));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalProjection#getOffsets(double, double, kovacs.util.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		final double gamma = gamma(theta, phi);
		toOffset.set(2.0 * gamma * Math.cos(theta) * Math.sin(0.5*phi), gamma * Math.sin(theta));
	}

	/**
	 * Z2.
	 *
	 * @param offset the offset
	 * @return the double
	 */
	private final double Z2(Coordinate2D offset) {
		return 1.0 - (offset.x() * offset.x()) / 16.0 - (offset.y() * offset.y()) / 4.0;
	}
	
	/**
	 * Gamma.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @return the double
	 */
	private final double gamma(double theta, double phi) {
		return Math.sqrt(2.0 / (1.0 + Math.cos(theta) * Math.cos(0.5*phi)));
	}

}

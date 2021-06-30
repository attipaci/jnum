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



public class HammerAitoff extends CylindricalProjection {

	private static final long serialVersionUID = -5636733533889649074L;

	public HammerAitoff() {}

	@Override
	public String getFitsID() { return "AIT"; }


	@Override
	public String getFullName() { return "Hammer-Aitoff"; }

	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		final double Z2 = Z2(offset);
		final double Z = Math.sqrt(Z2);
		phiTheta.setNative(2.0 * Math.atan2(0.5 * Z * offset.x(), 2.0 * Z2 - 1.0), asin(offset.y() * Z));
	}

	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		final double gamma = gamma(theta, phi);
		toOffset.set(2.0 * gamma * Math.cos(theta) * Math.sin(0.5*phi), gamma * Math.sin(theta));
	}


	private final double Z2(Coordinate2D offset) {
		return 1.0 - (offset.x() * offset.x()) / 16.0 - (offset.y() * offset.y()) / 4.0;
	}
	

	private final double gamma(double theta, double phi) {
		return Math.sqrt(2.0 / (1.0 + Math.cos(theta) * Math.cos(0.5*phi)));
	}

}

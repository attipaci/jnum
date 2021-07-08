/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.projection;

import jnum.ExtraMath;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;



public abstract class ZenithalProjection extends SphericalProjection {

	private static final long serialVersionUID = 6205931149858431357L;

	public ZenithalProjection() { 
		getNativeReference().setNative(0.0, rightAngle);
	}

	@Override 
	public void calcCelestialPole() {
		setCelestialPole(getReference());
	}

	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		phiTheta.setNative(Math.atan2(offset.x(), -offset.y()), thetaOfR(ExtraMath.hypot(offset.x(), offset.y())));
	}

	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		final double R = R(theta);
		// What is in Calabretta and Greisen 2002
		toOffset.set(R * Math.sin(phi), -R * Math.cos(phi));
	}

	protected abstract double R(double theta);

	protected abstract double thetaOfR(double value);

}

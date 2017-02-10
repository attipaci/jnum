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

import jnum.ExtraMath;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


// TODO: Auto-generated Javadoc
/**
 * The Class ZenithalProjection.
 */
public abstract class ZenithalProjection extends SphericalProjection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6205931149858431357L;

	/**
	 * Instantiates a new zenithal projection.
	 */
	public ZenithalProjection() { 
		getNativeReference().setNative(0.0, rightAngle);
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#calcCelestialPole()
	 */
	@Override 
	public void calcCelestialPole() {
		setCelestialPole(getReference());
	}
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#phi(jnum.Coordinate2D)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		phiTheta.setNative(Math.atan2(offset.x(), -offset.y()), thetaOfR(ExtraMath.hypot(offset.x(), offset.y())));
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.SphericalProjection#getOffsets(double, double, jnum.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		final double R = R(theta);
		// What is in Calabretta and Greisen 2002
		toOffset.set(R * Math.sin(phi), -R * Math.cos(phi));
	}
	
	
	/**
	 * R.
	 *
	 * @param theta the theta
	 * @return the double
	 */
	protected abstract double R(double theta);
	
	/**
	 * Theta of r.
	 *
	 * @param value the value
	 * @return the double
	 */
	protected abstract double thetaOfR(double value);
	

}

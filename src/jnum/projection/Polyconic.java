/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.projection;

import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


public class Polyconic extends SphericalProjection {

	private static final long serialVersionUID = 2600070844940523551L;

	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		throw new UnsupportedOperationException("Polyconic deprojection not implemented");
	}

	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		double t = Math.tan(theta);
		final double cotTheta = Double.isInfinite(t) ? 0.0 : 1.0 / t;
		t = phi * Math.sin(theta);
		toOffset.set(cotTheta * Math.sin(t), theta + cotTheta * (1.0 - Math.cos(t)));
	}

	@Override
	public String getFitsID() {
		return "PCO";
	}

	@Override
	public String getFullName() {
		return "Polyconic";
	}

}

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


public class ZenithalEqualArea extends ZenithalProjection {

	private static final long serialVersionUID = -1225375794601264165L;

	public ZenithalEqualArea() {}

	/* (non-Javadoc)
	 * @see jnum.astro.ZenithalProjection#R(double)
	 */
	@Override
	protected final double R(double theta) {
		return Math.sqrt(2.0*(1.0-Math.sin(theta)));
	}

	/* (non-Javadoc)
	 * @see jnum.astro.ZenithalProjection#thetaOfR(double)
	 */
	@Override
	protected final double thetaOfR(double value) {
		return rightAngle - 2.0*asin(0.5*value);
	}

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "ZEA"; }

	/* (non-Javadoc)
	 * @see jnum.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Zenithal Equal-Area"; }


	
}

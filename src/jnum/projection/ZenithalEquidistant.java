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

// TODO: Auto-generated Javadoc
/**
 * The Class ZenithalEquidistant.
 */
public class ZenithalEquidistant extends ZenithalProjection {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5179501947703322697L;

	/* (non-Javadoc)
	 * @see kovacs.projection.ZenithalProjection#R(double)
	 */
	@Override
	protected double R(final double theta) {
		return rightAngle - theta;
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.ZenithalProjection#thetaOfR(double)
	 */
	@Override
	protected double thetaOfR(final double value) {
		return rightAngle - value;
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return "ARC";
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return "Zenithal Equidistant";
	}

}

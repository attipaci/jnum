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

import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class Gnomonic.
 */
public class Gnomonic extends ZenithalProjection { 
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6424305737899780309L;

	/**
	 * Instantiates a new gnomonic.
	 */
	public Gnomonic() {}

	/* (non-Javadoc)
	 * @see kovacs.util.astro.ZenithalProjection#R(double)
	 */
	@Override
	protected final double R(final double theta) {
		if(SphericalCoordinates.equalAngles(theta, rightAngle)) return 0.0;
		return 1.0 / Math.tan(theta);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.astro.ZenithalProjection#thetaOfR(double)
	 */
	@Override
	protected final double thetaOfR(final double value) {
		return Math.atan2(1.0, value);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() { return "TAN"; }

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() { return "Gnomonic"; }	

}

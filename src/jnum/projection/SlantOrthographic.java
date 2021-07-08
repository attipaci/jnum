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


public class SlantOrthographic  extends ZenithalProjection {

	private static final long serialVersionUID = 731517061464195597L;

	public SlantOrthographic() { }

	@Override
	public String getFitsID() { return "SIN"; }

	@Override
	public String getFullName() { return "Slant Orthographic"; }

	@Override
	protected final double R(double theta) {
		return Math.cos(theta);
	}

	@Override
	protected final double thetaOfR(double value) {
		return acos(value);
	}


	
}

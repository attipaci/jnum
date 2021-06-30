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

import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class CylindricalEqualArea extends CylindricalProjection {

	private static final long serialVersionUID = -6111486646040480793L;

	double lambda = 1.0;

	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		phiTheta.setNative(offset.x(), asin(lambda * offset.y()));
	}

	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		toOffset.set(phi, Math.sin(theta) / lambda);
	}

	@Override
	public String getFitsID() {
		return "CEA";
	}

	@Override
	public String getFullName() {
		return "Cylindrical Equal Area";
	}

	@Override
	public void parseHeader(Header header, String alt) {
		super.parseHeader(header, alt);

		String parName = getLatitudeParameterPrefix() + "1" + alt;
		if(header.containsKey(parName)) lambda = header.getDoubleValue(parName);		
	}

	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {		
		super.editHeader(header, alt);
		String latPrefix = getLatitudeParameterPrefix();
		
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		c.add(new HeaderCard(latPrefix + "1" + alt, lambda, "lambda parameter for cylindrical equal area projection."));	
	}

}

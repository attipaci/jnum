/* *****************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.ExtraMath;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


public class CylindricalPerspective extends CylindricalProjection {

	private static final long serialVersionUID = 6778316198532804130L;

	double mu = 1.0, lambda = 1.0;
	

	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		final double eta = offset.y() / (mu + lambda);
		phiTheta.setNative(offset.x() / lambda, Math.atan2(eta, 1.0) + asin(eta * mu / ExtraMath.hypot(eta, 1.0)));
	}

	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		toOffset.set(lambda * phi, (mu + lambda) / (mu + Math.cos(theta)) * Math.sin(theta));
	}

	@Override
	public String getFitsID() {
		return "CYP";
	}

	@Override
	public String getFullName() {
		return "Cylindrical Perspective";
	}

	@Override
	public void parseHeader(Header header, String alt) {
		super.parseHeader(header, alt);

		String parName = getLatitudeParameterPrefix() + "1" + alt;
		if(header.containsKey(parName)) mu = header.getDoubleValue(parName);
		
		parName = getLatitudeParameterPrefix() + "2" + alt;
		if(header.containsKey(parName)) lambda = header.getDoubleValue(parName);
		
	}

	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {		
		super.editHeader(header, alt);
		String latPrefix = getLatitudeParameterPrefix();
		
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		c.add(new HeaderCard(latPrefix + "1" + alt, mu, "mu parameter for cylindrical perspective."));	
		c.add(new HeaderCard(latPrefix + "2" + alt, lambda, "lambda parameter for cylindrical perspective."));	
	}
	
}

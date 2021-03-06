/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.projection;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.ExtraMath;
import jnum.Unit;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


public class BonnesProjection extends SphericalProjection {

	private static final long serialVersionUID = 9018823339614736356L;

	private double theta1 = 0.0;

	private double Y0;
	
	public void setTheta1(double value) {
		if(theta1 == value) return;
		theta1 = value;
		getNativeReference().setY(theta1);
		Y0 = 1.0 / Math.tan(theta1) + theta1;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getPhiTheta(kovacs.math.Coordinate2D, kovacs.math.SphericalCoordinates)
	 */
	@Override
	protected final void getPhiTheta(final Coordinate2D offset, final SphericalCoordinates phiTheta) {
		final double R = Math.copySign(ExtraMath.hypot(offset.x(), Y0 - offset.y()), theta1);
		final double A = Math.atan2(offset.x(), Y0 - offset.y());
		phiTheta.setY(Y0 - R);
		phiTheta.setX(A * R / phiTheta.cosLat());

	}

	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#getOffsets(double, double, kovacs.math.Coordinate2D)
	 */
	@Override
	protected final void getOffsets(final double theta, final double phi, final Coordinate2D toOffset) {
		final double R = Y0 - theta;
		final double A = phi * Math.cos(theta) / R;
		toOffset.set(R * Math.sin(A), Y0 - R * Math.cos(A));
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return "BON";
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return "Bonne's Projection";
	}

	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parseHeader(Header header, String alt) {
		super.parseHeader(header, alt);
		String parName = getLatitudeParameterPrefix() + "1" + alt;
		if(header.containsKey(parName)) setTheta1(header.getDoubleValue(parName) * Unit.deg);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.projection.SphericalProjection#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {		
		super.editHeader(header, alt);
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard(getLatitudeParameterPrefix() + "1" + alt, theta1 / Unit.deg, "Theta1 parameter for Bonne's projection."));		
	}
	
}

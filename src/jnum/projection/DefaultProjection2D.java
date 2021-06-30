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

import jnum.math.Coordinate2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;


public class DefaultProjection2D extends Projection2D<Coordinate2D> {

	private static final long serialVersionUID = 2413285303727304456L;

	public DefaultProjection2D() {
	    this(new Coordinate2D());
	}
	
	public DefaultProjection2D(Coordinate2D reference) {
	    super();
	    setReference(reference);
	}

	@Override
	public Coordinate2D getCoordinateInstance() {
		return new Coordinate2D();
	}

	@Override
	public void project(Coordinate2D coords, Coordinate2D toProjected) {
		toProjected.copy(coords);
	}

	@Override
	public void deproject(Coordinate2D projected, Coordinate2D toCoords) {
		toCoords.copy(projected);
	}

	@Override
	public String getFitsID() {
		return null;
	}

	@Override
	public String getFullName() {
		return "Cartesian";
	}

	@Override
	public void parseHeader(Header header, String alt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {
		// TODO Auto-generated method stub
		
	}

}

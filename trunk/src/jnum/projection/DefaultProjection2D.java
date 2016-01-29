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
package jnum.projection;

import jnum.math.Coordinate2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultProjection2D.
 */
public class DefaultProjection2D extends Projection2D<Coordinate2D> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2413285303727304456L;

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getCoordinateInstance()
	 */
	@Override
	public Coordinate2D getCoordinateInstance() {
		return new Coordinate2D();
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#project(kovacs.util.Coordinate2D, kovacs.util.Coordinate2D)
	 */
	@Override
	public void project(Coordinate2D coords, Coordinate2D toProjected) {
		toProjected.copy(coords);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#deproject(kovacs.util.Coordinate2D, kovacs.util.Coordinate2D)
	 */
	@Override
	public void deproject(Coordinate2D projected, Coordinate2D toCoords) {
		toCoords.copy(projected);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFitsID()
	 */
	@Override
	public String getFitsID() {
		return null;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#getFullName()
	 */
	@Override
	public String getFullName() {
		return "Cartesian";
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parse(Header header, String alt) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Projection2D#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {
		// TODO Auto-generated method stub
		
	}

}

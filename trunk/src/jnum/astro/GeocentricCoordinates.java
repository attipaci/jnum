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

package jnum.astro;

import jnum.math.SphericalCoordinates;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class GeocentricCoordinates.
 */
public class GeocentricCoordinates extends SphericalCoordinates {	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 14070920003212901L;

	/**
	 * Instantiates a new geocentric coordinates.
	 */
	public GeocentricCoordinates() {}
	
	/**
	 * Instantiates a new geocentric coordinates.
	 *
	 * @param text the text
	 */
	public GeocentricCoordinates(String text) { super(text); }
	
	/**
	 * Instantiates a new geocentric coordinates.
	 *
	 * @param lon the lon
	 * @param lat the lat
	 */
	public GeocentricCoordinates(double lon, double lat) { super(lon, lat); }
	
	/** The Constant NORTH. */
	public final static int NORTH = 1;
	
	/** The Constant SOUTH. */
	public final static int SOUTH = -1;
	
	/** The Constant EAST. */
	public final static int EAST = 1;
	
	/** The Constant WEST. */
	public final static int WEST = -1;
	
	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {	
		super.edit(cursor, alt);	
		cursor.add(new HeaderCard("WCSNAME" + alt, getClass().getSimpleName(), "coordinate system description."));
	}

}

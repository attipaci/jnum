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


package jnum.astro;

import jnum.math.SphericalCoordinates;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;

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
	
	@Override
    public String getTwoLetterCode() { return "GC"; }
	
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
	public void edit(Header header, String alt) throws HeaderCardException {	
		super.edit(header, alt);	
		header.addLine(new HeaderCard("WCSNAME" + alt, getClass().getSimpleName(), "coordinate system description."));
	}

}

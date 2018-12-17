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

import jnum.fits.FitsToolkit;
import jnum.math.SphericalCoordinates;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class GeocentricCoordinates extends SphericalCoordinates {	

	private static final long serialVersionUID = 14070920003212901L;


	public GeocentricCoordinates() {}
	

	public GeocentricCoordinates(String text) { super(text); }
	

	public GeocentricCoordinates(double lon, double lat) { super(lon, lat); }
	
	@Override
    public String getTwoLetterCode() { return "GC"; }

	public final static int NORTH = 1;

	public final static int SOUTH = -1;

	public final static int EAST = 1;

	public final static int WEST = -1;
	
	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {	
		super.editHeader(header, keyStem, alt);	

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard("WCSNAME" + alt, getClass().getSimpleName(), "coordinate system description."));
	}

}

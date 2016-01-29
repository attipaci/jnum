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
package jnum.data;

import jnum.Unit;
import jnum.math.Cartesian;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.projection.DefaultProjection2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


// TODO: Auto-generated Javadoc
/**
 * The Class CartesianGrid.
 */
public class CartesianGrid2D extends Grid2D<Coordinate2D> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3604375577514529903L;


	@Override
	public void defaults() {
		super.defaults();
		setProjection(new DefaultProjection2D());	
		setCoordinateSystem(new Cartesian(2));
	}
	
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#parseProjection(nom.tam.fits.Header)
	 */
	@Override
	public void parseProjection(Header header) throws HeaderCardException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#getCoordinateInstanceFor(java.lang.String)
	 */
	@Override
	public Coordinate2D getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException {
		return new Coordinate2D();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#parseHeader(nom.tam.fits.Header)
	 */
	@Override
	public void parseHeader(Header header) throws HeaderCardException, InstantiationException, IllegalAccessException {
		super.parseHeader(header);
	
		
		CoordinateSystem system = getCoordinateSystem();
		
		if(header.containsKey("CTYPE1" + alt)) system.get(0).label = header.getStringValue("CTYPE1" + alt);
		if(header.containsKey("CTYPE2" + alt)) system.get(1).label = header.getStringValue("CTYPE2" + alt);
		
		if(header.containsKey("CUNIT1" + alt)) xUnit = Unit.get(header.getStringValue("CUNIT1" + alt));
		if(header.containsKey("CUNIT2" + alt)) yUnit = Unit.get(header.getStringValue("CUNIT2" + alt));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#editHeader(nom.tam.util.Cursor)
	 */
	@Override
	public void editHeader(Header header, Cursor<String, HeaderCard> cursor) throws HeaderCardException {		
		super.editHeader(header, cursor);
		
		CoordinateSystem system = getCoordinateSystem();
		
		if(system != null) {
			cursor.add(new HeaderCard("CTYPE1" + alt, system.get(0).label, "Coordinate name"));
			cursor.add(new HeaderCard("CTYPE2" + alt, system.get(1).label, "Coordinate name"));
		}
		
		if(xUnit != null) cursor.add(new HeaderCard("CUNIT1" + alt, xUnit.name(), "Unit on x-axis"));
		if(yUnit != null) cursor.add(new HeaderCard("CUNIT2" + alt, yUnit.name(), "Unit on y-axis"));
		
		
		
	}


}

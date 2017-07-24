/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.image;

import jnum.Unit;
import jnum.fits.FitsToolkit;
import jnum.math.CartesianSystem;
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


	/* (non-Javadoc)
	 * @see jnum.data.Grid2D#defaults()
	 */
	@Override
	public void defaults() {
		super.defaults();
		super.setProjection(new DefaultProjection2D());	
		setCoordinateSystem(new CartesianSystem(2));
	}
	
	/* (non-Javadoc)
	 * @see jnum.data.Grid2D#parseProjection(nom.tam.fits.Header)
	 */
	@Override
	public void parseProjection(Header header) throws HeaderCardException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jnum.data.Grid2D#getCoordinateInstanceFor(java.lang.String)
	 */
	@Override
	public Coordinate2D getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException {
		return new Coordinate2D();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#parseHeader(nom.tam.fits.Header)
	 */
	@Override
	public void parseHeader(Header header) throws InstantiationException, IllegalAccessException {
		super.parseHeader(header);
		
		CoordinateSystem system = getCoordinateSystem();
		
		if(header.containsKey("CTYPE1" + alt)) system.get(0).setShortLabel(header.getStringValue("CTYPE1" + alt));
		if(header.containsKey("CTYPE2" + alt)) system.get(1).setShortLabel(header.getStringValue("CTYPE2" + alt));
		
		if(header.containsKey("CUNIT1" + alt)) xUnit = Unit.get(header.getStringValue("CUNIT1" + alt));
		if(header.containsKey("CUNIT2" + alt)) yUnit = Unit.get(header.getStringValue("CUNIT2" + alt));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#editHeader(nom.tam.util.Cursor)
	 */
	@Override
	public void editHeader(Header header) throws HeaderCardException {		
		super.editHeader(header);
		
		CoordinateSystem system = getCoordinateSystem();
		
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		if(system != null) {
			c.add(new HeaderCard("CTYPE1" + alt, system.get(0).getShortLabel(), "Coordinate name"));
			c.add(new HeaderCard("CTYPE2" + alt, system.get(1).getShortLabel(), "Coordinate name"));
		}
		
		if(xUnit != null) c.add(new HeaderCard("CUNIT1" + alt, xUnit.name(), "Unit on x-axis"));
		if(yUnit != null) c.add(new HeaderCard("CUNIT2" + alt, yUnit.name(), "Unit on y-axis"));
		
		
		
	}


}

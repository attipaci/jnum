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

package jnum.data.image;

import jnum.fits.FitsToolkit;
import jnum.math.CartesianSystem;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.projection.DefaultProjection2D;
import jnum.projection.Projection2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


// TODO: Auto-generated Javadoc
/**
 * The Class CartesianGrid.
 */
public class FlatGrid2D extends Grid2D<Coordinate2D> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3604375577514529903L;


	/* (non-Javadoc)
	 * @see jnum.data.Grid2D#defaults()
	 */
	@Override
	public void defaults() {
	    setCoordinateSystem(new CartesianSystem(2));
		super.defaults();
		super.setProjection(new DefaultProjection2D());	

	}
	
	@Override
    public final int dimension() { return 2; }
	
	
	@Override
    public void setProjection(Projection2D<Coordinate2D> projection) {
	    if(!(projection instanceof DefaultProjection2D)) throw new IllegalStateException("Generic projections are not allowed here."); 
	    super.setProjection(projection);
	}
	
	
	
	/* (non-Javadoc)
	 * @see jnum.data.Grid2D#parseProjection(nom.tam.fits.Header)
	 */
	@Override
	public void parseProjection(Header header) throws HeaderCardException {
		
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
		
		String alt = getFitsID();
			
		if(header.containsKey("CTYPE1" + alt)) xAxis().setShortLabel(header.getStringValue("CTYPE1" + alt));
		if(header.containsKey("CTYPE2" + alt)) yAxis().setShortLabel(header.getStringValue("CTYPE2" + alt));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#editHeader(nom.tam.util.Cursor)
	 */
	@Override
	public void editHeader(Header header) throws HeaderCardException {		
		super.editHeader(header);
		
		String alt = getFitsID();	
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		// Override the axis names ignoring projection...
		c.add(new HeaderCard("CTYPE1" + alt, xAxis().getShortLabel(), "Axis-1 name"));
		c.add(new HeaderCard("CTYPE2" + alt, yAxis().getShortLabel(), "Axis-2 name"));
	
	}


}

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

package jnum.data.image;

import jnum.fits.FitsToolkit;
import jnum.math.CartesianSystem;
import jnum.math.Coordinate2D;
import jnum.projection.DefaultProjection2D;
import jnum.projection.Projection2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class FlatGrid2D extends Grid2D<Coordinate2D> {

	private static final long serialVersionUID = -3604375577514529903L;


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
	

	@Override
	public void parseProjection(Header header) throws HeaderCardException {
		
	}


	@Override
	public Coordinate2D getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException {
		return new Coordinate2D();
	}
	

	@Override
	public void parseHeader(Header header) throws Exception {
		super.parseHeader(header);
		
		String alt = getFitsVariant();
			
		if(header.containsKey("CTYPE1" + alt)) xAxis().setShortLabel(header.getStringValue("CTYPE1" + alt));
		if(header.containsKey("CTYPE2" + alt)) yAxis().setShortLabel(header.getStringValue("CTYPE2" + alt));
	}
	

	@Override
	public void editHeader(Header header) throws HeaderCardException {		
		super.editHeader(header);
		
		String alt = getFitsVariant();	
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		// Override the axis names ignoring projection...
		c.add(new HeaderCard("CTYPE1" + alt, xAxis().getShortLabel(), "Axis-1 name"));
		c.add(new HeaderCard("CTYPE2" + alt, yAxis().getShortLabel(), "Axis-2 name"));
	
	}


}

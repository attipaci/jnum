/*******************************************************************************
 * Copyright (c) 2014 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
package jnum.fits;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;
import nom.tam.fits.ImageHDU;
import jnum.data.Grid2D;
import jnum.data.GridImage2D;
import jnum.data.SphericalGrid;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;

// TODO: Auto-generated Javadoc
/**
 * The Class FitsImage.
 */
public class FitsImage {
	
	/** The grid. */
	Grid2D<?> grid;
	
	/** The coordinate system. */
	CoordinateSystem coordinateSystem;
	
	/** The offset system. */
	CoordinateSystem offsetSystem;
	
	/**
	 * Parses the header.
	 *
	 * @param header the header
	 * @param alt the alt
	 * @throws HeaderCardException the header card exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public void parseHeader(Header header, String alt) throws HeaderCardException, InstantiationException, IllegalAccessException {
		grid = Grid2D.fromHeader(header, alt);
			
		String type1 = header.getStringValue("CTYPE1" + alt);
		String type2 = header.getStringValue("CTYPE2" + alt);
		
		CoordinateAxis xAxis = null, yAxis = null;
		
		if(grid instanceof SphericalGrid) {
			type1 = type1.substring(0, type1.indexOf('-'));
			type2 = type2.substring(0, type1.indexOf('-'));	
		}
		else {			
			xAxis = new CoordinateAxis(type1 == null ? "x" : type1);
			yAxis = new CoordinateAxis(type2 == null ? "y" : type2);
		}
		coordinateSystem = new CoordinateSystem();
		coordinateSystem.add(xAxis);
		coordinateSystem.add(yAxis);
	
	}
	
	/**
	 * Read.
	 *
	 * @param hdu the hdu
	 * @param alt the alt
	 * @return the grid image
	 */
	public GridImage2D<?> read(ImageHDU hdu, String alt) {
		return null;
	}
	
	
}

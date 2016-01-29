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

import jnum.math.SphericalCoordinates;
import jnum.projection.SphericalProjection;
import jnum.util.Unit;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

// TODO: Auto-generated Javadoc
/**
 * The Class SphericalGrid.
 */
public class SphericalGrid extends Grid2D<SphericalCoordinates> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8833326375584103801L;

	/**
	 * Instantiates a new spherical grid.
	 */
	public SphericalGrid() { }
	
	@Override
	protected void defaults() {
		super.defaults();
		setCoordinateSystem(new SphericalCoordinates().getCoordinateSystem());
	}
	
	/**
	 * Instantiates a new spherical grid.
	 *
	 * @param reference the reference
	 */
	public SphericalGrid(SphericalCoordinates reference) {
		this();
		setReference(reference);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.data.Grid2D#setReference(kovacs.math.Coordinate2D)
	 */
	@Override
	public void setReference(SphericalCoordinates reference) {
		super.setReference(reference);
		setCoordinateSystem(reference.getCoordinateSystem());
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#isReverseX()
	 */
	@Override
	public boolean isReverseX() { return getReference().isReverseLongitude(); }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#isReverseY()
	 */
	@Override
	public boolean isReverseY() { return getReference().isReverseLatitude(); }
	
	
	@Override
	public Unit getDefaultFITSAxisUnit() { return SphericalCoordinates.degree; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#parseProjection(nom.tam.fits.Header)
	 */
	@Override
	public void parseProjection(Header header) throws HeaderCardException {
		String type = header.getStringValue("CTYPE1" + getFITSAlt());
	
		try { setProjection(SphericalProjection.forName(type.substring(5, 8))); }
		catch(Exception e) { System.err.println("ERROR! Unknown projection " + type.substring(5, 8)); }
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.data.Grid2D#getCoordinateInstanceFor(java.lang.String)
	 */
	@Override
	public SphericalCoordinates getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException {
		Class<? extends SphericalCoordinates> coordClass = SphericalCoordinates.getFITSClass(type);
		return coordClass.newInstance();
	}
	
	
}
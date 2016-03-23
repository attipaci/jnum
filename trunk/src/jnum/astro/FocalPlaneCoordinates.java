/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.astro;

import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.text.GreekLetter;

// TODO: Auto-generated Javadoc
/**
 * The Class FocalPlaneCoordinates.
 */
public class FocalPlaneCoordinates extends SphericalCoordinates {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6324566580599103464L;
	
	/** The y offset axis. */
	static CoordinateAxis xAxis, yAxis, xOffsetAxis, yOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

	static {
		defaultCoordinateSystem = new CoordinateSystem("Focal Plane Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Focal Plane Offsets");

		xAxis = new CoordinateAxis("Focal-plane X", "X", "X");
		yAxis = new CoordinateAxis("Focal-plane Y", "Y", "Y");
		xOffsetAxis = new CoordinateAxis("Focal-plane dX", "dX", GreekLetter.Delta + " X");
		yOffsetAxis = new CoordinateAxis("Focal-plane dY", "dY", GreekLetter.Delta + " Y");
		
		defaultCoordinateSystem.add(xAxis);
		defaultCoordinateSystem.add(yAxis);
		defaultLocalCoordinateSystem.add(xOffsetAxis);
		defaultLocalCoordinateSystem.add(yOffsetAxis);
		
		for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
	}

	/**
	 * Instantiates a new focal plane coordinates.
	 */
	public FocalPlaneCoordinates() {}

	/**
	 * Instantiates a new focal plane coordinates.
	 *
	 * @param text the text
	 */
	public FocalPlaneCoordinates(String text) { super(text); } 

	/**
	 * Instantiates a new focal plane coordinates.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public FocalPlaneCoordinates(double x, double y) { super(x, y); }

	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
	 */
	@Override
	public String getFITSLongitudeStem() { return "FLON"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "FLAT"; }
	
	
	/**
	 * Gets the coordinate system.
	 *
	 * @return the coordinate system
	 */
	@Override
	public CoordinateSystem getCoordinateSystem() { return defaultCoordinateSystem; }


	/**
	 * Gets the local coordinate system.
	 *
	 * @return the local coordinate system
	 */
	@Override
	public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }

	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {	
		super.edit(cursor, alt);	
		cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
	}
		
	
}

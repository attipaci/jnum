/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.fits.FitsToolkit;
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
	
	
	@Override
    public String getTwoLetterCode() { return "FP"; }
	
	@Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }
     
    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }
    
	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {	
		super.editHeader(header, alt);	

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		c.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
	}
	
	
    
    
    /** The default local coordinate system. */
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
		
    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Focal Plane Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Focal Plane Offsets");

        CoordinateAxis xAxis = new CoordinateAxis("Focal-plane X", "X", "X");
        CoordinateAxis yAxis = new CoordinateAxis("Focal-plane Y", "Y", "Y");
        CoordinateAxis xOffsetAxis = new CoordinateAxis("Focal-plane dX", "dX", GreekLetter.Delta + " X");
        CoordinateAxis yOffsetAxis = new CoordinateAxis("Focal-plane dY", "dY", GreekLetter.Delta + " Y");
        
        defaultCoordinateSystem.add(xAxis);
        defaultCoordinateSystem.add(yAxis);
        defaultLocalCoordinateSystem.add(xOffsetAxis);
        defaultLocalCoordinateSystem.add(yOffsetAxis);
        
        for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
    }

	
}

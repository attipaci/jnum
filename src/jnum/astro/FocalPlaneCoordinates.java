/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import java.text.ParseException;

import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.text.GreekLetter;

/**
 * Coordinates in which to represent an astronomical image projected onto the focal plane of
 * an instrument.
 * 
 * @author Attila Kovacs
 *
 */
public class FocalPlaneCoordinates extends SphericalCoordinates {

    /** */
    private static final long serialVersionUID = 6324566580599103464L;

    /**
     * Instantiates new default focal plane coordinates.
     */
    public FocalPlaneCoordinates() {}

    /**
     * Instantiates new focal plane coordinates, from a string representation of these. 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     * 
     * @see #parse(String, java.text.ParsePosition)
     */
    public FocalPlaneCoordinates(String text) throws ParseException { super(text); } 

    /**
     * Instantiates new Galactic Coordinates with the specified conventional longitude and latitude angles.
     * 
     * @param x         (rad) Focal-plane offset/angle in the <i>x</i> direction.
     * @param y         (rad) Focal-plane offset/angle in <i>y</i> direction.
     */
    public FocalPlaneCoordinates(double x, double y) { super(x, y); }

    @Override
    public FocalPlaneCoordinates clone() { return (FocalPlaneCoordinates) super.clone(); }

    @Override
    public FocalPlaneCoordinates copy() { return (FocalPlaneCoordinates) super.copy(); }


    @Override
    public String getFITSLongitudeStem() { return "FLON"; }


    @Override
    public String getFITSLatitudeStem() { return "FLAT"; }


    @Override
    public String getTwoLetterID() { return "FP"; }

   
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }
   
    /** The default coordinate system */
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem;
    
    /** The default local coordinate system */
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultLocalCoordinateSystem;


    static {  
        CoordinateAxis xAxis = createAxis("Focal-plane X", "x", GreekLetter.xi + "", af);  
        CoordinateAxis yAxis = createAxis("Focal-plane Y", "y", GreekLetter.eta + "", af);

        defaultCoordinateSystem = new CoordinateSystem("Focal Plane");
        defaultCoordinateSystem.add(xAxis);
        defaultCoordinateSystem.add(yAxis);

        CoordinateAxis xOffsetAxis = createOffsetAxis("Focal-plane dX", "dX", GreekLetter.Delta + " " + GreekLetter.xi);
        CoordinateAxis yOffsetAxis = createOffsetAxis("Focal-plane dY", "dY", GreekLetter.Delta + " " + GreekLetter.eta);

        defaultLocalCoordinateSystem = new CoordinateSystem("Focal Plane Offsets");
        defaultLocalCoordinateSystem.add(xOffsetAxis);
        defaultLocalCoordinateSystem.add(yOffsetAxis);

    }


}

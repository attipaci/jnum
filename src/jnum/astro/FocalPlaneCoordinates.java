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

import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.text.GreekLetter;


public class FocalPlaneCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = 6324566580599103464L;


    public FocalPlaneCoordinates() {}


    public FocalPlaneCoordinates(String text) { super(text); } 


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
   

    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


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

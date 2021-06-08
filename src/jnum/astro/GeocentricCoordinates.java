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


package jnum.astro;

import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.text.GreekLetter;


public class GeocentricCoordinates extends SphericalCoordinates {	

    private static final long serialVersionUID = 14070920003212901L;


    public GeocentricCoordinates() {}


    public GeocentricCoordinates(String text) { super(text); }


    public GeocentricCoordinates(double lon, double lat) { super(lon, lat); }

    @Override
    public GeocentricCoordinates clone() { return (GeocentricCoordinates) super.clone(); }

    @Override
    public GeocentricCoordinates copy() { return (GeocentricCoordinates) super.copy(); }


    @Override
    public String getTwoLetterCode() { return "GC"; }
    
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

    public final static int NORTH = 1;

    public final static int SOUTH = -1;

    public final static int EAST = 1;

    public final static int WEST = -1;

    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Geocentric");
        defaultLocalCoordinateSystem = new CoordinateSystem("Geocentric Offsets");

        CoordinateAxis longitudeAxis = createAxis("Longitude", "LON", "lon", af);
        CoordinateAxis latitudeAxis = createAxis("Latitude", "LAT", "lat", af);
        
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Longitude Offset", "dLON", GreekLetter.Delta + " lon");
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Latitude Offset", "dLAT", GreekLetter.delta + " lat");
           
        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);           
    }


}

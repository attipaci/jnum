/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.text.NumberFormat;

import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector3D;
import jnum.text.GreekLetter;


public class GeocentricCoordinates extends SphericalCoordinates {	

    private static final long serialVersionUID = 14070920003212901L;

    private double radius = Math.sqrt(GeodeticCoordinates.a * GeodeticCoordinates.b);
    
    public GeocentricCoordinates() {}

    public GeocentricCoordinates(String text) { super(text); }

    public GeocentricCoordinates(double lon, double lat, double radius) { 
        super(lon, lat); 
        this.radius = radius;
    }
    
    @Override
    public GeocentricCoordinates clone() { return (GeocentricCoordinates) super.clone(); }

    @Override
    public GeocentricCoordinates copy() { return (GeocentricCoordinates) super.copy(); }
    
    @Override
    public void toCartesian(Vector3D v) {
        super.toCartesian(v);
        v.scale(radius);
    }
 
    public void fromCartesian(Vector3D v) {
        super.fromCartesian(v);
        radius = v.length();
    }
    
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
    
    @Override
    public String toString(int decimals) {
        return super.toString(decimals) + " " + Util.f3.format(radius / Unit.km) + " km";
    }
    
    @Override
    public String toString(NumberFormat nf) {
        return super.toString(nf) + " " + Util.f3.format(radius / Unit.km) + " km";
    }
    
    
    public double radius() { return radius; }

    public static final int NORTH = 1;

    public static final int SOUTH = -1;

    public static final int EAST = 1;

    public static final int WEST = -1;

    @SuppressWarnings("hiding")
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

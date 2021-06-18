/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum.astro;

import java.text.NumberFormat;

import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.PolarVector2D;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector3D;
import jnum.text.GreekLetter;

// TODO: Auto-generated Javadoc
// TODO Needs updating
// ... rename to GeographicCoordinates
// ... toString() formatting, and parse with N,S,E,W


public class GeodeticCoordinates extends SphericalCoordinates {	

    private static final long serialVersionUID = -162411465069211958L;
    
    private double altitude = 0.0;

    public GeodeticCoordinates() {}

    public GeodeticCoordinates(String text) { super(text); }

    public GeodeticCoordinates(double lon, double lat, double altitude) { 
        super(lon, lat); 
        this.altitude = altitude;
    }
    
    public GeodeticCoordinates(GeocentricCoordinates geocentric) {
       fromGeocentric(geocentric);
    }
   
    public void fromGeocentric(GeocentricCoordinates geocentric) {
        setNativeLongitude(geocentric.x());
        setNativeLatitude(Math.atan((1.0 - f) * Math.tan(geocentric.latitude())));
        
        altitude = 0.0;
        PolarVector2D p = getGeocentricLatitudeVector();
        
        altitude = (geocentric.radius() - p.length()) * Math.cos(p.angle() - geocentric.latitude()); 
    }
    
    public PolarVector2D getGeocentricLatitudeVector() {
        double beta = Math.atan(1.0 / (1.0 - f) * Math.tan(latitude()));    // Geocentric latitude
        double ac2 = a * Math.cos(beta);
        double bs2 = b * Math.sin(beta);
        
        ac2 *= ac2;
        bs2 *= bs2;
        
        double R = Math.sqrt((a * a * ac2 + b * b * bs2) / (ac2 + bs2));
        return new PolarVector2D(R, beta);
    }
    
    @Override
    public void toCartesian(Vector3D v) {
        PolarVector2D p = getGeocentricLatitudeVector();
        double R = p.length();
        double cR = p.length() * Math.cos(p.angle());
        v.set(cR * Math.cos(longitude()), cR * Math.sin(longitude()), R * Math.sin(p.angle()));
    }
    
    public void fromCartesian(Vector3D v) {
        GeocentricCoordinates gc = new GeocentricCoordinates();
        gc.fromCartesian(v);
        fromGeocentric(gc);
    }

    @Override
    public GeodeticCoordinates clone() { return (GeodeticCoordinates) super.clone(); }

    @Override
    public GeodeticCoordinates copy() { return (GeodeticCoordinates) super.copy(); }


    @Override
    public String getTwoLetterCode() { return "GD"; }

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
        return super.toString(decimals) + " " + Util.f1.format(altitude) + "m";
    }
    
    @Override
    public String toString(NumberFormat nf) {
        return super.toString(nf) + " " + Util.f1.format(altitude) + "m";
    }
    

    public final static double a = 6378137.0 * Unit.m; // Earth major axis

    public final static double b = 6356752.3142 * Unit.m; // Earth minor axis

    public final static double f = 1.0 / 298.257223563; // Flattening WGS84

    public final static int NORTH = 1;

    public final static int SOUTH = -1;

    public final static int EAST = 1;

    public final static int WEST = -1;
    
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Geodetic");
        defaultLocalCoordinateSystem = new CoordinateSystem("Geodetic Offsets");
        
        CoordinateAxis longitudeAxis = createAxis("Latitude", "LAT", "lat", af);
        CoordinateAxis latitudeAxis = createAxis("Longitude", "LON", "lon", af);
         
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Longitude Offset", "dLON", GreekLetter.Delta + "lat");
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Latitude Offset", "dLAT", GreekLetter.delta + "lon");
           
        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);           
    }


    // TODO verify units of X...

    // See Wikipedia Geodetic System...

    // e^2 = 2f-f^2 = 1- (b/a)^2
    // e'^2 = f(2-f)/(1-f)^2 = (a/b)^2 - 1


    // Australian Geodetic Datum (1966) and (1984)
    // AGD66 & GDA84
    // a = 6378160.0 m
    // f = 1/298.25

    // Geodetic Reference System 1980 (GRS80)
    // a = 6378137 m
    // f = 1/298.257222101

    // World Geodetic System 1984 (WGS84)
    // used by GPS navigation
    // a = 6378137.0 m
    // f = 1/298.257223563


    // Geodetic (phi, lambda, h) -> geocentric phi'
    // chi = sqrt(1-e^2 sin^2(phi))
    //
    // tan(phi') = [(a/chi)(1-f)^2 + h] / [(a/chi) + h] tan(phi)


}

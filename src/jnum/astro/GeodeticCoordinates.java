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
import java.text.ParseException;

import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.MathVector;
import jnum.math.PolarVector2D;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector3D;
import jnum.text.GreekLetter;


// TODO 
// ... toString() formatting, and parse with N,S,E,W


/**
 * <p>
 * Geodetic coordinates (lon, lat, alt) are commonly used to represent locations on Earth's surface, relative
 * to the reference ellipsoid. The reference ellipsoid represents a good global approximation of Earth's 
 * equipotential surface (sea level) ignoring local gravitational anomalies. Thus geodetic coordinates
 * provide a close approximation for the orientation of the local horizon (or local vertical) directions. 
 * For this reason, positions on Earth are normally given as geodetic (not geocentric!) coordinates, 
 * since these are more natural for use in geodesy as well as in astronomy, for the simple reason that
 * a local vertical (or horizon) is easily detetmined, whereas pinpointing the location of the geocenter from
 * a location on Earth's surface is not trivial.
 * </p>
 * 
 * <p>
 * However, since {@link GeocentricCoordinates} are also useful for astronomy, e.g. for determining a local Earth
 * rotation speed vector, or for the apparent position of a near-Earth object, this class provides methods
 * to convert to and from geocentric coordinates.
 * </p>
 * 
 * @author Attila Kovacs
 * 
 * @see GeocentricCoordinates
 *
 */
public class GeodeticCoordinates extends SphericalCoordinates {

    /** */
    private static final long serialVersionUID = -162411465069211958L;
    
    /** (m) Height above the reference ellipsoid */
    private double altitude = 0.0;

    /**
     * Instantiates new default geocentric coordinates.
     */
    public GeodeticCoordinates() {}

    /**
     * Instantiates new geodetic coordinates, from a string representation of these. 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     * 
     * @see #parse(String, java.text.ParsePosition)
     */
    public GeodeticCoordinates(String text) throws ParseException { super(text); }

    /**
     * Instantiates new geodetic coordinates with the specified conventional longitude and latitude angles.
     * 
     * @param lon       (rad) Geodetic longitude angle.
     * @param lat       (rad) Geodetic latitude angle. I.e., the angle between of the local vertical over
     *                  the equatorial plane.
     * @param altitude  (m) Height above sea level (or more precisely, height above the reference ellipsoid
     *                  in the local vertical direction).
     */
    public GeodeticCoordinates(double lon, double lat, double altitude) { 
        super(lon, lat); 
        this.altitude = altitude;
    }
    
    /**
     * Instantiates new geodetic coordinates for the specified location relative to the geocenter.
     * 
     * @param geocentric    the geocentric coordinates that define the location.
     * 
     * @see #fromGeocentric(GeocentricCoordinates)
     */
    public GeodeticCoordinates(GeocentricCoordinates geocentric) {
       fromGeocentric(geocentric);
    }
   
    /**
     * Returns the altitude or height above the reference ellipsoide, along the local vertical.
     * 
     * @return      (m) the altitude / height above the reference ellipsoide along the direction of the local vertical.
     * 
     * @see #setAltitude(double)
     * @see #longitude()
     * @see #latitude()
     */
    public final double altitude() {
      return altitude;  
    }
    
    /**
     * Sets a new altitude / height above the reference ellipsoid, along the local vertical, in the same direction as before.
     * 
     * @param h     (m) the new altitude / height above the reference ellipsoid along the direction of the local vertical,
     *              and in the same direction as before.
     * @throws IllegalArgumentException if the radius is NaN or &lt; 0.
     * 
     * @see #altitude()
     * @see #longitude()
     * @see #latitude()
     */
    public void setAltitude(double h) {
        altitude = h;
    }
    
    /**
     * Sets new coordinates to match the specified location relative to the geocenter.
     * 
     * @param geocentric    the geocentric coordinates that define the new location.
     * 
     * @see #GeodeticCoordinates(GeocentricCoordinates)
     * @see #getGeocentricLatitudeVector()
     */
    public void fromGeocentric(GeocentricCoordinates geocentric) {
        setNativeLongitude(geocentric.x());
        setNativeLatitude(Math.atan((1.0 - f) * Math.tan(geocentric.latitude())));
        
        altitude = 0.0;
        PolarVector2D p = getGeocentricLatitudeVector();
        
        altitude = (geocentric.radius() - p.length()) * Math.cos(p.angle() - geocentric.latitude()); 
    }
    
    /**
     * Returns the 2D latitude vector (&phi; <i>r</i>) in the plabne defined by the pole and
     * the longitude of these coordinates. Such a vector is a useful intermediate for
     * the conversion to 3D Cartesian coordinates, but may be directly useful otherwise also.
     * 
     * @return      (rad, m) a 2D polar vector in the Cartesian system, in which the <i>x</i> axis points
     *              from the geocenter towards the equator at the set longitude, and the <i>y</i>
     *              axis is in the direction of the North pole from the geocenter.
     *               
     * @see #toCartesian(Vector3D)
     */
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
    
    
    @Override
    public double fromCartesian(MathVector<Double> v) {
        GeocentricCoordinates gc = new GeocentricCoordinates();
        double r = gc.fromCartesian(v);
        fromGeocentric(gc);
        return r;
    }

    @Override
    public GeodeticCoordinates clone() { return (GeodeticCoordinates) super.clone(); }

    @Override
    public GeodeticCoordinates copy() { return (GeodeticCoordinates) super.copy(); }


    @Override
    public String getTwoLetterID() { return "GD"; }

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
    
    /** (m) Major axis of the reference ellipsoid */
    public static final double a = 6378137.0 * Unit.m;

    /** (m) Minor axis of the reference ellipsoid */
    public static final double b = 6356752.3142 * Unit.m;

    /** The flattening parameter, determined by WGS84 */
    public static final double f = 1.0 / 298.257223563;

    /** The direction of North along the latitude coordinate. */
    public static final int NORTH = 1;

    /** The direction of South along the latitude coordinate. */
    public static final int SOUTH = -1;

    /** The direction of East along the longitude coordinate. */
    public static final int EAST = 1;

    /** The direction of West along the longitude coordinate. */
    public static final int WEST = -1;
    
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

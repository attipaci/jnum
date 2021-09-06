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
import jnum.math.SphericalCoordinates;
import jnum.math.Vector3D;
import jnum.text.GreekLetter;


//TODO 
//... toString() formatting, and parse with N,S,E,W

/**
 * <p>
 * Geocentric coordinates (lon, lat, radius). Essentially a spherical coordinate system whose origin is at the
 * geocenter. It is a proper 3D coordinate system, that includes a radial distance from the geocenter as the
 * 3rd coordinate component. Geocentric coordinates are often used in astronomy for representing 3D positions
 * on Earth, above Earth, on around Earth orbit.
 * </p>
 * <p>
 * The geocentric coordinate system, however, is not the best choice when trying to represent directions 
 * relative to the horizon or the local gravitational vertical. For such purposes one should intead use
 * {@link GeodeticCoordinates}, which specify the orientation of the local surface (or vertical) instead, based
 * on a referfence ellipsoid model.
 * </p>
 * 
 * @author Attila Kovacs
 * 
 * @see GeodeticCoordinates
 *
 */
public class GeocentricCoordinates extends SphericalCoordinates {	

    private static final long serialVersionUID = 14070920003212901L;

    private double radius = Math.sqrt(GeodeticCoordinates.a * GeodeticCoordinates.b);
    
    /**
     * Instantiates new default geocentric coordinates.
     */
    public GeocentricCoordinates() {}

    /**
     * Instantiates new geocentric coordinates, from a string representation of these. 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     * 
     * @see #parse(String, java.text.ParsePosition)
     */
    public GeocentricCoordinates(String text) throws ParseException { super(text); }

    /**
     * Instantiates new geocentric coordinates with the specified conventional longitude and latitude angles.
     * 
     * @param lon       (rad) Geocentric longitude angle.
     * @param lat       (rad) Geocentric latitude angle.
     * @param radius    (m) Distance from geocenter.
     */
    public GeocentricCoordinates(double lon, double lat, double radius) { 
        super(lon, lat); 
        this.radius = radius;
    }
    
    /**
     * Returns the radial distance component of the geocentric coordinates represented by this object.
     * 
     * @return      (m) The radial distance from geocenter.
     * 
     * @see #setRadius(double)
     * @see #longitude()
     * @see #latitude()
     */
    public final double radius() { return radius; }
    
    /**
     * Sets a new distance from the geocenter in the same direction as before.
     * 
     * @param r     (m) the new distance from geocenter.
     * @throws IllegalArgumentException if the radius is NaN or &lt; 0.
     * 
     * @see #radius()
     * @see #longitude()
     * @see #latitude()
     */
    public void setRadius(double r) throws IllegalArgumentException {
        if(!(r >= 0.0)) throw new IllegalArgumentException("Radius must be positive or 0.");
        radius = r;
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
 
    @Override
    public double fromCartesian(MathVector<Double> v) {
        super.fromCartesian(v);
        radius = v.abs();
        return radius;
    }
    
    @Override
    public String getTwoLetterID() { return "GC"; }
    
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
   

    /** The direction of North along the latitude coordinate */
    public static final int NORTH = 1;

    /** The direction of South along the latitude coordinate */
    public static final int SOUTH = -1;

    /** The direction of East along the longitude coordinate */
    public static final int EAST = 1;

    /** The direction of West along the longitude coordinate */
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

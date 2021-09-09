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


import java.text.ParseException;

import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;

/**
 * Spherical coordinates in the natural coordinate system of the telescope mount, which may or may not 
 * align to a horizontal or equatorial coordinate system. For telescopes in general, the latitude axis 
 * is usually referred to as 'elevation' (EL), while the longitude axis is known as 'cross-elevation' (XEL),
 * which is the convention used for this implementation.
 * 
 * @author Attila Kovacs
 * 
 * @see FocalPlaneCoordinates
 *
 */
public class TelescopeCoordinates extends SphericalCoordinates {

    /** */
    private static final long serialVersionUID = 5165681897613041311L;

    /**
     * Instantiates new default native telesscope coordinates.
     */
    public TelescopeCoordinates() {}

    /**
     * Instantiates new native telescope Coordinates, from a string representation of these. 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     *
     * @see #parse(String, java.text.ParsePosition)
     */
    public TelescopeCoordinates(String text) throws ParseException { super(text); } 

    /**
     * Instantiates new Galactic Coordinates with the specified conventional longitude and latitude angles.
     * 
     * @param xel       (rad) Telescope cross-elevation (longitude) angle.
     * @param el        (rad) Telescope elevation (latitude) angle.
     */
    public TelescopeCoordinates(double xel, double el) { super(xel, el); }

    
    @Override
    public TelescopeCoordinates clone() { return (TelescopeCoordinates) super.clone(); }
    
    @Override
    public TelescopeCoordinates copy() { return (TelescopeCoordinates) super.copy(); }
    

    @Override
    public String getFITSLongitudeStem() { return "TLON"; }


    @Override
    public String getFITSLatitudeStem() { return "TLAT"; }

    
    @Override
    public String getTwoLetterID() { return "TE"; }
    
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }
     
    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

    /**
     * Returns the telescope cross-elevation (longitude) coordinate component.
     * 
     * @return  (rad) the cross-elevation (longitude) angle.
     * 
     * @see #setXEL(double)
     * @see #EL()
     */
    public final double XEL() { return nativeLongitude(); }


    /**
     * Returns the telescope cross-elevation (longitude) coordinate component. It is the same as {@link #XEL()} but with 
     * a more expressive name.
     * 
     * @return  (rad) the cross-elevation (longitude) angle.
     * 
     * @see #XEL()
     * @see #setXEL(double)
     */
    public final double crossElevation() { return nativeLongitude(); }

    /**
     * Returns the telescope elevation (latitude) coordinate component.
     * 
     * @return  (rad) the elevation (latitude) angle.
     * 
     * @see #setEL(double)
     * @see #XEL()
     */
    public final double EL() { return nativeLatitude(); }

    /**
     * Returns the telescope elevation (latitude) coordinate component. It is the same as {@link #EL()} but with 
     * a more expressive name.
     * 
     * @return  (rad) the elevation (latitude) angle.
     * 
     * @see #EL()
     * @see #setEL(double)
     */
    public final double elevation() { return nativeLatitude(); }

    /**
     * Sets a new telescope cross-elevation (longitude) angle.
     * 
     * @param XEL   (rad) the new cross-elevation (longitude) angle.
     * 
     * @see #XEL()
     * @see #setEL(double)
     */
    public final void setXEL(double XEL) { setNativeLongitude(XEL); }

    /**
     * Sets a new telescope elevation (latitude) angle.
     * 
     * @param EL   (rad) the new elevation (latitude) angle.
     * 
     * @see #EL()
     * @see #setXEL(double)
     */
    public final void setEL(double EL) { setNativeLatitude(EL); }

    /**
     * Converts locally projected offsets around a reference position from the telescope coordinate system to
     * an equatorial coordinate system, given the position angle of the telescope's elevation
     * axis relative to the the equatorial declination axis.
     * 
     * @param offset    (rad) The local offset vector in the telescope's frame [in] to be rotated 
     *                  into the local equatorial coordinate frame [out].
     * @param telVPA    (rad) the telescope's elevation direction (counter-clockwise) relative to the declination axis, as seen
     *                  in the equatorial coordinate system.
     */
    public static void toEquatorialOffset(Vector2D offset, double telVPA) {
        offset.rotate(telVPA);
        offset.flipX();
    }
    
    /**
     * Converts locally projected offsets around a reference position from the local equatorial coordinate system to
     * the telescope's native coordinate system, given the position angle of the telescope's elevation
     * axis relative to the the equatorial declination axis.
     * 
     * @param offset    (rad) The local offset vector in the local equatorial coordinate system [in] to be rotated into
     *                  the telescope's frame [out].
     * @param telVPA    (rad) the telescope's elevation direction (counter-clockwise) relative to the declination axis, as seen
     *                  in the equatorial coordinate system.
     */
    public static void fromEquatorialOffset(Vector2D offset, double telVPA) {
        offset.flipX();
        offset.rotate(-telVPA);
    }
    
    /** the default coordinate system */
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem;
    
    /** the default local coordinate system */
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultLocalCoordinateSystem;
 
     
    static {
        defaultCoordinateSystem = new CoordinateSystem("Telescope");
        defaultLocalCoordinateSystem = new CoordinateSystem("Telescope Offsets");

        CoordinateAxis crossElevationAxis = createAxis("Telescope Cross-elevation", "XEL", "XEl", af);
        CoordinateAxis elevationAxis = createAxis("Telescope Elevation", "EL", "El", af);
        CoordinateAxis xelOffsetAxis = createOffsetAxis("Telescioe Cross-elevation Offset", "dXEL", GreekLetter.Delta + " XEl");
        CoordinateAxis elevationOffsetAxis = createOffsetAxis("Telescope Elevation Offset", "dEL", GreekLetter.Delta + " El");
        
        defaultCoordinateSystem.add(crossElevationAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(xelOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);
        
        for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
    }
       

}

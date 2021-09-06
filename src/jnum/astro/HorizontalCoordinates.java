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

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;

/**
 * Azimuth and elevation (that is Alt/Az) coordinates at some Earth observer location. Elevation angled are measured
 * from the horizon towards zenith, and azimuth angles are measured clockwise from the North. Horizontal coordinates
 * may represent the pointing of a ground-based telescope, or the momentary position of a celestial object as seen
 * from a specidic observer location at a specific time.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class HorizontalCoordinates extends SphericalCoordinates {

    /** */
    private static final long serialVersionUID = -3759766679620485628L;

    /**
     * Instantiates new default local horizontal coordinates for some Earth location.
     */
    public HorizontalCoordinates() {}

    /**
     * Instantiates new local horizontal coordinates for some Earth location, from a string representation of these. 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     * 
     * @see #parse(String, java.text.ParsePosition)    *
     */
    public HorizontalCoordinates(String text) throws ParseException { super(text); } 

    /**
     * Instantiates new local horizontal coordinates for some Earth location the specified azximuth and elevation angles.
     * 
     * @param az       (rad) azimuth angle (clockwise from North).
     * @param el       (rad) elevation latitude angle (above horizon).
     */
    public HorizontalCoordinates(double az, double el) { super(az, el); }

    
    @Override
    public HorizontalCoordinates clone() { return (HorizontalCoordinates) super.clone(); }

    @Override
    public HorizontalCoordinates copy() { return (HorizontalCoordinates) super.copy(); }


    @Override
    public String getFITSLongitudeStem() { return "ALON"; }


    @Override
    public String getFITSLatitudeStem() { return "ALAT"; }


    @Override
    public String getTwoLetterID() { return "HO"; }


    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

    /**
     * Returns the azimuth coordinate.
     * 
     * @return  (rad) azimuth angle, clockwise from North.
     * 
     * @see setAZ(double)
     * @see EL() 
     */
    public final double AZ() { return nativeLongitude(); }


    /**
     * Returns the azimuth coordinate. Same as {@link #AZ()} but with a more expressive name.
     * 
     * @return  (rad) azimuth angle, clockwise from North.
     * 
     * @see AZ()
     * @see setAZ(double)
     * @see EL() 
     */
    public final double azimuth() { return nativeLongitude(); }

    /**
     * Returns the elevation coordinate.
     * 
     * @return  (rad) elevation angle, above horizon towards zenith [-&pi;:&pi;].
     * 
     * @see setEL(double)
     * @see setZA(double)
     * @see AZ() 
     */
    public final double EL() { return nativeLatitude(); }

    /**
     * Returns the elevation coordinate. Same as {@link #EL()} but with a more expressive name.
     * 
     * @return  (rad) elevation angle, above horizon towards zenith [-&pi;:&pi;].
     * 
     * @see zenithAngle()
     * @see EL()
     * @see setEL(double)
     * @see setZA(double)
     * @see AZ() 
     */
    public final double elevation() { return nativeLatitude(); }

    /**
     * Returns the zenith angle.
     *  
     * @return  (rad) Zenith angle, measured from zenith to nadir [0:2&pi;]. Same as 90&deg; - {@link #EL()}.
     * 
     * @see EL()
     * @see setZA(double)
     * @see setEL(double)
     */
    public final double ZA() { return Constant.rightAngle - nativeLatitude(); }

    /**
     * Returns the zenith angle. Same as {@link #ZA()} but with a more expressive name.
     * 
     * @return  (rad) Zenith angle, measured from zenith to nadir [0:2&pi;]. Same as 90&deg; - {@link #EL()}.
     */
    public final double zenithAngle() { return ZA(); }

    /**
     * Sets a new azimuth angle.
     * 
     * @param AZ    (rad) new azimuth angle, clockwise from North.
     * 
     * @see AZ()
     */
    public final void setAZ(double AZ) { setNativeLongitude(AZ); }

    /**
     * Sets a new elevation angle.
     * 
     * @param EL    (rad) new elevation angle, above horizon towards zenith [-&pi;:&pi;].
     * 
     * @see EL()
     * @see setZA(double)
     */
    public final void setEL(double EL) { setNativeLatitude(EL); }

    /**
     * Sets a new zenith angle.
     * 
     * @param ZA    (rad) new zenith angle, measured from zenith to nadir [0:2&pi;].
     * 
     * @see ZA()
     * @see setEL(double)
     */
    public final void setZA(double ZA) { setNativeLatitude(Constant.rightAngle - ZA); }

    /**
     * Returns the apparent equatorial coordinates for the the location over the horizon at a given Earth
     * observer location and observing time. The returned equatorial coordinates are referenced in the
     * specificed equatorial reference system. Typically this reference system should be a topocentric
     * referfence system for a location and a time that are approcimately the same as from where the
     * observation was conducted from. A slightly less precise, but acceptable alternative is to reference
     * the returned equatorial coordinates to the dynamical equator around the time of observation, which 
     * would not include polar wobble corrections and but nevertheless still accurate to a fraction of an arcsecond. 
     * 
     * @param site          Earth location of observer
     * @param LST           (s) Local sidereal time
     * @param system        Equatorial coordinates system, typically a topocentric reference system for the 
     *                      same site (approximately) as for which the coordinates are being calculated.
     *                      
     * @return      Equatorial Coordinates of the celestial position corresponding to the direction in the horizontal
     *              frame over the specified Earth location and local sidereal time.
     *              
     * @see #toEquatorial(GeodeticCoordinates, double, EquatorialCoordinates)
     */
    public EquatorialCoordinates toEquatorial(GeodeticCoordinates site, double LST, EquatorialSystem system) {
        EquatorialCoordinates equatorial = new EquatorialCoordinates(system);
        toEquatorial(site, LST, equatorial);
        return equatorial;
    }

    /**
     * Calculates the apparent equatorial coordinates for the the location over the horizon at a given Earth
     * observer location and observing time, returning the result in the supplied equatorial coordinates objects. 
     * The returned equatorial coordinates are referenced in the system of the supplied equatorial coordinates. 
     * Typically this reference system should be a topocentric
     * referfence system for a location and a time that are approcimately the same as from where the
     * observation was conducted from. A slightly less precise, but acceptable alternative is to reference
     * the returned equatorial coordinates to the dynamical equator around the time of observation, which 
     * would not include polar wobble corrections and but nevertheless still accurate to a fraction of an arcsecond. 
     * 
     * @param site          Earth location of observer
     * @param LST           (s) Local sidereal time
     * @param equatorial    Equatorial coordinates in which to return the result. The reference system for these
     *                      coordinates should be ttypically a topocentric reference system for the 
     *                      same site (approximately) as for which the coordinates are being calculated.
     *              
     * @see #toEquatorial(GeodeticCoordinates, double, EquatorialSystem)
     */
    public void toEquatorial(GeodeticCoordinates site, double LST, EquatorialCoordinates equatorial) { 
        double cosAZ = Math.cos(AZ());
        equatorial.setNativeLatitude(SafeMath.asin(sinLat() * site.sinLat() + cosLat() * site.cosLat() * cosAZ));
        final double asinH = -Math.sin(AZ()) * cosLat();
        final double acosH = site.cosLat() * sinLat() - site.sinLat() * cosLat() * cosAZ;
        //final double acosH = (horizontal.sinLat() - equatorial.sinLat() * site.sinLat()) / site.cosLat();

        equatorial.setLongitude(LST * Unit.timeAngle + Math.atan2(asinH, acosH));        
    }

    /**
     * Returns the parallactic angle at the specified Earth location. The parallactic angle
     * is the position angle of the local meridian vs. the declination axis in the equatorial system, measured 
     * as a counter-clockwise rotation when looking inward towards the origin.
     * 
     * @param site  the geodetic location on Earth.
     * @return      (rad) the parallatic angle for these horizontal coordinates at the given site.
     * 
     * @see EquatorialCoordinates#getParallacticAngle(GeodeticCoordinates, double)
     * @see #toEquatorialOffset(Vector2D, double)
     */
    public double getParallacticAngle(GeodeticCoordinates site) {
        return Math.atan2(site.cosLat() * Math.sin(AZ()), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(AZ()));
    }

    /**
     * Convert local offsets around these cooridnates, from being expressed in the horizontal frame at some Earth
     * location of the observer and local sidereal time of observation, to equatorial offsets.
     * 
     * @param offset        (rad) Locally projected (SFL) offsets around these coordinates as seen in 
     *                      the horizontal frame of a given observer location and observation time [in], converted
     *                      to equatorial offsets [out] in the system of these coordinates.
     * @param site          Earth location of observer
     * 
     * @see EquatorialCoordinates#toHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #fromEquatorialOffset(Vector2D, double)
     * @see #getParallacticAngle(GeodeticCoordinates)
     * 
     */
    public void toEquatorialOffset(Vector2D offset, GeodeticCoordinates site) {
        toEquatorialOffset(offset, getParallacticAngle(site));
    }
    
    /**
     * Convert local equatorial offsets around these coordinates to horizontal offsets at some Earth
     * location of the observer and local sidereal time of observation.
     * 
     * @param offset        (rad) Locally projected (SFL) offsets around these coordinates [in], converted
     *                      to horizontal offsets [out] for a given observer location and observation time.
     * @param site          Earth location of observer
     * 
     * @see EquatorialCoordinates#fromHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #toEquatorialOffset(Vector2D, GeodeticCoordinates)
     * @see #getParallacticAngle(GeodeticCoordinates)
     */
    public void fromEquatorialOffset(Vector2D offset, GeodeticCoordinates site) {
        fromEquatorialOffset(offset, getParallacticAngle(site));
    }
    

    /**
     * Converts local projected (SFL) offsets from equatorial to horizontal given a parallactic angle.
     * 
     * @param offset    (rad) Offsets to convert.
     * @param PA        (rad) Parallactic angle.
     * 
     * @see EquatorialCoordinates#fromHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #toEquatorialOffset(Vector2D, double)
     * @see #getParallacticAngle(GeodeticCoordinates)
     */
    public static void fromEquatorialOffset(Vector2D offset, double PA) {
        offset.flipX();
        offset.rotate(-PA);
    }

    /**
     * Converts local projected (SFL) offsets from horizontal to equatorial given a parallactic angle.
     * 
     * @param offset    (rad) Offsets to convert.
     * @param PA        (rad) Parallactic angle.
     * 
     * @see EquatorialCoordinates#fromHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #fromEquatorialOffset(Vector2D, GeodeticCoordinates)
     * @see #getParallacticAngle(GeodeticCoordinates)
     */
    public static void toEquatorialOffset(Vector2D offset, double PA) {
        offset.rotate(PA);
        offset.flipX();
    }

    
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


    static {
        defaultCoordinateSystem = new CoordinateSystem("Horizontal");
        defaultLocalCoordinateSystem = new CoordinateSystem("Horizontal Offsets");

        CoordinateAxis azimuthAxis = createAxis("Azimuth", "AZ", "Az", af);
        CoordinateAxis elevationAxis = createAxis("Elevation", "EL", "El", af);
        CoordinateAxis azimuthOffsetAxis = createOffsetAxis("Azimuth Offset", "dAZ", GreekLetter.Delta + " Az");
        CoordinateAxis elevationOffsetAxis = createOffsetAxis("Elevation Offset", "dEL", GreekLetter.Delta + " El");

        azimuthAxis.setReverse(true);
        azimuthOffsetAxis.setReverse(true);
        
        defaultCoordinateSystem.add(azimuthAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(azimuthOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);

    }

}

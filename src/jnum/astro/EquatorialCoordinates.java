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

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import jnum.text.HourAngleFormat;


/**
 * Equatorial coordinates, such as RA/Dec (&alpha;, &delta;) in a specific coordinate system such as ICRS
 * or FK5(J2000), or J2021.4543. Equatorial coordinates are the most widely used celestial coordinates, 
 * since the equatorial system align with the the Earth equator (at some specific time/epoch, and within 
 * some limits of measurement uncertainty). However, a slight nuissance with equatorial coordinates
 * is that (a) Earth's equator and pole are not static but precess with a ~26,000 year period, 
 * (b) the true dynamical equator is further knocked around by the Moon and planets, (c) Earth's crust 
 * moves relative to it's rotation axis, and (d) the limiting precision that determined former reference
 * systems is inadequate by today's standards.
 * 
 * 
 * Therefore, we have not one, but several different families of equatorial coordinates. To distinguish
 * between them the equatorial coordinates are tagged with a specific coordinate system they use. See 
 * {@link EquatorialSystem} for details. Depending on how how your coordinates were defined, or how they
 * will be used, you may need to traverse between different systems. The {@link EquatorialTransform}
 * class can help you do exactly that with ease and efficiency.
 * 
 * 
 * You can also convert equatorial coordinates to any other type of {@link CelestialCoordinates}, or vice-versa
 * using the {@link #convertFrom(CelestialCoordinates)} and {@link #convertTo(CelestialCoordinates)}
 * methods of this class, or that of the other coordinate class.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class EquatorialCoordinates extends PrecessingCoordinates {

    /** */
    private static final long serialVersionUID = 3445122576647034180L;

    /**
     * Instantiates new empty equatorial coordinates in the ICRS frame
     * 
     * @see EquatorialSystem#ICRS
     * 
     */
    public EquatorialCoordinates() {}

    /**
     * Instantiates new default equatorial coordinates in the specified equatorial reference system.
     * 
     * @param system        the equatorial reference system for the new coordinates
     * 
     * @see #EquatorialCoordinates(double, double, EquatorialSystem)
     */
    public EquatorialCoordinates(EquatorialSystem system) { 
        super(system);
    }

    /**
     * Instantiates new equatorial coordinates from a string representation of the coordinates, including
     * a representation of the reference system (if available).
     * 
     * @param text              the string representation of the coordinates, including their reference system.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     */
    public EquatorialCoordinates(String text) throws ParseException { super(text); }


    /**
     * Instantiate new equatorial coordinates in the ICRS.
     * 
     * @param ra        (rad) Right ascension angle [0:2&pi;].
     * @param dec       (rad) Declination angle [-&pi;/2;&pi;/2].
     * 
     * @see #EquatorialCoordinates(double, double, EquatorialSystem)
     * @see EquatorialSystem#ICRS
     * 
     */
    public EquatorialCoordinates(double ra, double dec) { 
        super(ra, dec, EquatorialSystem.ICRS); 
    }

    /**
     * Instantiates new equatorial coordinates, in the specified equatorial reference system.
     * 
     * @param ra        (rad) Right ascension angle [0:2&pi;].
     * @param dec       (rad) Declination angle [-&pi;/2;&pi;/2].
     * @param system    the equatorial reference system for the new coordinates
     * 
     * @see #EquatorialCoordinates(double, double, String)
     * 
     */
    public EquatorialCoordinates(double ra, double dec, EquatorialSystem system) { 
        super(ra, dec, system); 
    }

    /**
     * Instantiates new equatorial coordinates, in the specified equatorial reference system.
     * 
     * @param ra        (rad) Right ascension angle [0:2&pi;].
     * @param dec       (rad) Declination angle [-&pi;/2;&pi;/2].
     * @param sysSpec   the string representation of the equatorial reference system, such as 'ICRS', 'J2000' or 'FK5'.
     * 
     * @see #EquatorialCoordinates(double, double, EquatorialSystem)
     * 
     */
    public EquatorialCoordinates(double ra, double dec, String sysSpec) { 
        super(ra, dec, sysSpec); 
    }

    /**
     * Instantiates a new set of equatorial coordinates, referenced to the ICRS equator, that represent 
     * the same location on sky as the specified other celestial coordinates
     * 
     * @param from      the coordinates of the sky location in some other celestial system.
     * 
     * @see CelestialCoordinates#fromEquatorial(EquatorialCoordinates)
     * @see CelestialCoordinates#toEquatorial()
     * @see EquatorialSystem#ICRS
     */
    public EquatorialCoordinates(CelestialCoordinates from) { super(from); }
    

    @Override
    public EquatorialCoordinates clone() { return (EquatorialCoordinates) super.clone(); }

    @Override
    public EquatorialCoordinates copy() { return (EquatorialCoordinates) super.copy(); }

    @Override
    public EquatorialCoordinates getTransformed(EquatorialTransform T) {
        return (EquatorialCoordinates) super.getTransformed(T);
    }
    
    @Override
    public EquatorialCoordinates getTransformedTo(EquatorialSystem system) {
        return (EquatorialCoordinates) super.getTransformedTo(system);
    }
        
    @Override
    public String getFITSLongitudeStem() { return "RA--"; }

    
    @Override
    public String getFITSLatitudeStem() { return "DEC-"; }


    @Override
    public String getTwoLetterID() { return "EQ"; }



    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

    /**
     * Returns the right ascension angle component.
     * 
     * @return  (rad) the right ascension angle [0:2&pi;].
     * 
     * @see DEC()
     * @see #setRA(double)
     */
    public final double RA() { return zeroToTwoPi(longitude()); }

    /**
     * Returns the right ascension angle component. Same as {@link #RA()} but with a more expressive name.
     * 
     * @return  (rad) the right ascension angle [0:2&pi;].
     * 
     * @see #RA()
     */
    public final double rightAscension() { return RA(); }

    /**
     * Returns the declination angle component.
     * 
     * @return  (rad) the declination angle [-&pi;:&pi;].
     * 
     * @see RA()
     * @see #setDEC(double)
     */
    public final double DEC() { return latitude(); }

    /**
     * Returns the declination angle component. Same as {@link #DEC()} buty with a more expressive name.
     * 
     * @return  (rad) the declination angle [-&pi;:&pi;].
     * 
     */
    public final double declination() { return DEC(); }

    /**
     * Sets a new right ascension coordinate component.
     * 
     * @param RA    (rad) the new right ascension angle [0:2&pi;].
     * 
     * @see #RA()
     * @see #setDEC(double)
     */
    public final void setRA(double RA) { setLongitude(RA); }

    /**
     * Sets a new declination coordinate component.
     * 
     * @param DEC    (rad) the new declination angle [-&pi;:&pi;].
     * 
     * @see #DEC()
     * @see #setRA(double)
     */
    public final void setDEC(double DEC) { setLatitude(DEC); }

    /**
     * Returns the parallactic angle at the specified Earth location and local sidereal time. The parallactic angle
     * is the position angle of the local meridian vs. the declination axis in the equatorial system, measured 
     * as a counter-clockwise rotation when looking inward towards the origin.
     * 
     * @param site  the geodetic location on Earth.
     * @param LST   (s) the Local Sidereal Time
     * @return      (rad) the parallatic angle for these coordinates at the given site and time.
     * 
     * @see HorizontalCoordinates#getParallacticAngle(GeodeticCoordinates)
     * @see #toHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #fromHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     */
    public double getParallacticAngle(GeodeticCoordinates site, double LST) {
        final double H = LST * Unit.timeAngle - RA();
        return Math.atan2(site.cosLat() * Math.sin(H), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(H));
    }

    @Override
    public void transform(EquatorialTransform t) {
        EquatorialCoordinates equatorial = toEquatorial();
        t.transform(equatorial);
        fromEquatorial(equatorial);
        setSystem(equatorial.getSystem());
    }

    
    /**
     * Returns the approximated position angle due to precession in the J2000 epoch, not too far from J2000 itself...
     * Based on  page 5 of E. E. Barnard's Micrometric Meaures of Star Clusters in Vol. VI 
     * of the Publications of the Yerkes Observatory, 1931
     * 
     * @return  the approximate position angle of the RA/DEC axes in this system in the FK5(J2000) system.
     * 
     * @see #getEquatorialPositionAngle()
     */
    public double getLocalEpochPosistionAngle() {
       return precessionPARate * (getSystem().getJulianYear() - 2000.0) * Math.sin(RA()) / cosLat();
    }


    @Override
    public void toEquatorial(EquatorialCoordinates equatorial) {
        equatorial.copy(this);	
    }

    @Override
    public void fromEquatorial(EquatorialCoordinates equatorial) {	
        copy(equatorial);
    }

    /**
     * Returns the Alt/Az horizontal position of these coordinates at a given Earth location and local sidereal time
     * of observation. Note that in order to get precise Alt/Az locations, you should be calling this method on
     * apparent equatorial coordinates for the time of observation, referenced to the true dynamical equator at
     * the time of observation, with polar wobble corrections applied for the topocentric observing location.
     * 
     * @param site      the observer location on Earth. For the highest precision, the equatorial coordinates
     *                  should also be referenced to the same topocentric location approximately.
     * @param LST       (s) the local sidereal time at the time of observation. For the highest precision, the 
     *                  equatorial coordinates should also be referenced to the same time approximately.
     * @return          Alt/Az horizontal coordinates of these equatorial coordinates.
     * 
     * @see #toHorizontal(GeodeticCoordinates, double, HorizontalCoordinates)
     * @see EquatorialSystem.Topocentric
     */
    public HorizontalCoordinates toHorizontal(GeodeticCoordinates site, double LST) {
        HorizontalCoordinates horizontal = new HorizontalCoordinates();
        toHorizontal(site, LST, horizontal);
        return horizontal;
    }


    /**
     * Calculates the Alt/Az horizontal position of these coordinates at a given Earth location and local sidereal time
     * of observation. Note that in order to get precise Alt/Az locations, you should be calling this method on
     * apparent equatorial coordinates for the time of observation, referenced to the true dynamical equator at
     * the time of observation, with polar wobble corrections applied for the topocentric observing location.
     * 
     * @param site          the observer location on Earth. For the highest precision, the equatorial coordinates
     *                      should also be referenced to the same topocentric location approximately.
     * @param LST           (s) the local sidereal time at the time of observation. For the highest precision, the 
     *                      equatorial coordinates should also be referenced to the same time approximately.
     * @param horizontal    horizontal coordinates in which to return the calculated Alt/Az position of these equatorial coordinates.
     * 
     * @see #toHorizontal(GeodeticCoordinates, double, HorizontalCoordinates)
     * @see EquatorialSystem.Topocentric
     */
    public void toHorizontal(GeodeticCoordinates site, double LST, HorizontalCoordinates horizontal) { 
        double H = LST * Unit.timeAngle - RA();  
        double cosH = Math.cos(H);
        horizontal.setLatitude(SafeMath.asin(sinLat() * site.sinLat() + cosLat() * site.cosLat() * cosH));
        double asinA = -Math.sin(H) * cosLat() * site.cosLat();
        double acosA = sinLat() - site.sinLat() * horizontal.sinLat();
        horizontal.setLongitude(Math.atan2(asinA, acosA));
    }
 

    /**
     * Convert local equatorial offsets around these coordinates to horizontal offsets at some Earth
     * location of the observer and local sidereal time of observation.
     * 
     * @param offset        (rad) Locally projected (SFL) offsets around these coordinates [in], converted
     *                      to horizontal offsets [out] for a given observer location and observation time.
     * @param site          Earth location of observer
     * @param LST           (s) Local sidereal time of observation.
     * 
     * @see HorizontalCoordinates#fromEquatorialOffset(Vector2D, double)
     * @see #getParallacticAngle(GeodeticCoordinates, double)
     */
    public final void toHorizontalOffset(Vector2D offset, GeodeticCoordinates site, double LST) {
        HorizontalCoordinates.fromEquatorialOffset(offset, getParallacticAngle(site, LST));
    }

    /**
     * Convert local offsets around these cooridnates, from being expressed in the horizontal frame at some Earth
     * location of the observer and local sidereal time of observation, to equatorial offsets.
     * 
     * @param offset        (rad) Locally projected (SFL) offsets around these coordinates as seen in 
     *                      the horizontal frame of a given observer location and observation time [in], converted
     *                      to equatorial offsets [out] in the system of these coordinates.
     * @param site          Earth location of observer
     * @param LST           (s) Local sidereal time of observation.
     * 
     * @see HorizontalCoordinates#toEquatorialOffset(Vector2D, double)
     * @see #fromHorizontalOffset(Vector2D, GeodeticCoordinates, double)
     * @see #getParallacticAngle(GeodeticCoordinates, double)
     * @see #toHorizontal(GeodeticCoordinates, double)
     * 
     */
    public final void fromHorizontalOffset(Vector2D offset, GeodeticCoordinates site, double LST) {
        HorizontalCoordinates.toEquatorialOffset(offset, getParallacticAngle(site, LST));
    }
   
    
    @Override
    public EquatorialCoordinates getEquatorialPole() { 
        EquatorialCoordinates p = new EquatorialCoordinates(0.0, Constant.rightAngle, getSystem());
        p.toICRS();
        return p;
    }

    @Override
    public double getZeroLongitude() { return 0.0; }

    
    @Override
    public NumberFormat getLongitudeFormat(int decimals) {
        return Util.haf[decimals > 0 ? decimals-1 : 0];
    }


    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    /**
     * The string formatter class to use to convert the right ascension coordinate to a string representation.
     * 
     */
    private static HourAngleFormat haf = new HourAngleFormat(2);



    static {
        defaultCoordinateSystem = new CoordinateSystem("Equatorial");
        defaultLocalCoordinateSystem = new CoordinateSystem("Equatorial Offsets");

        CoordinateAxis rightAscentionAxis = createAxis("Right Ascension", "RA", GreekLetter.alpha + "", haf);
        CoordinateAxis declinationAxis = createAxis("Declination", "DEC", GreekLetter.delta + "", af);

        CoordinateAxis rightAscentionOffsetAxis = createOffsetAxis("Right Ascension Offset", "dRA", GreekLetter.Delta + " " + GreekLetter.alpha);
        CoordinateAxis declinationOffsetAxis = createOffsetAxis("Declination Offset", "dDEC", GreekLetter.Delta + " " + GreekLetter.delta);

        defaultCoordinateSystem.add(rightAscentionAxis);
        defaultCoordinateSystem.add(declinationAxis);

        defaultLocalCoordinateSystem.add(rightAscentionOffsetAxis);
        defaultLocalCoordinateSystem.add(declinationOffsetAxis);        
    }	

    
    /**
     * Constant for position angle rate of change due to precession near J2000
     */
    private static final double precessionPARate = 20.05 * Unit.arcsec;
    

    /**
     * Earth equator obliquity (23&deg; 26&prime; 21.448&Prime;) from Lieske+1977
     * 
     */
    static final double eps0 = 23.4392911111111 * Unit.deg;
    
    
    public static final int NORTH = 1;

    public static final int SOUTH = -1;

    public static final int EAST = 1;

    public static final int WEST = -1;

}

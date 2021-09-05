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
import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;


/**
 * Sky coordinates in the ecliptic coordinate system (&lambda; &beta;). Because the origin of the ecliptic coordinate
 * system is fixed to the vernal equinox, the ecliptic coordinates a essentially tied to an equatorial system, 
 * and as such it is subject to precession as well as other parameters that affect the definition of the equator
 * at a given time (or site) of observation. Therefore, like {@link EquatorialCoordinates}, ecliptic coordinates
 * are referenced to an equatorial reference system also.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public class EclipticCoordinates extends PrecessingCoordinates {
    
    /** */
    private static final long serialVersionUID = 7687178545213533912L;


    /**
     * Instantiates new empty ecliptic coordinates tried to the ICRS frame.
     * 
     * @see EquatorialSystem#ICRS
     * 
     */
    public EclipticCoordinates() {
        super();
    }

    /**
     * Instantiates new default ecliptic coordinates from the specified celestial coordinates, tied to the ICRS.
     * 
     * @param from        the celeastial coordinates that define the direction if the ecliptic coordinates.
     * 
     * @see #EclipticCoordinates(double, double, EquatorialSystem)
     */
    public EclipticCoordinates(CelestialCoordinates from) {
        super(from);
    }

    /**
     * Instantiates new default ecliptic coordinates, tied to the specified equatorial reference system.
     *
     * @param system    the equatorial reference system for the new coordinates
     * 
     * @see #EclipticCoordinates(double, double, String)
     * 
     */
    public EclipticCoordinates(EquatorialSystem system) {
        super(system);
    }

    /**
     * Instantiates new ecliptic coordinates, tied to the specified equatorial reference system.
     * 
     * @param lon       (rad) Ecliptic longitude angle [-&pi;:&pi;].
     * @param lat       (rad) Ecliptic latitude angle [-&pi;/2;&pi;/2].
     * @param system    the equatorial reference system for the new coordinates
     * 
     * @see #EclipticCoordinates(double, double, String)
     * 
     */
    public EclipticCoordinates(double lon, double lat, EquatorialSystem system) {
        super(lon, lat, system);
    }
    
    /**
     * Instantiates new ecliptic coordinates, tied to the specified equatorial reference system.
     * 
     * @param lon       (rad) Ecliptic longitude angle [-&pi;:&pi;].
     * @param lat       (rad) Ecliptic latitude angle [-&pi;/2;&pi;/2].
     * @param sysSpec   the string representation of the equatorial reference system, such as 'ICRS', 'J2000' or 'FK5'.
     * 
     * @see #EclipticCoordinates(double, double, EquatorialSystem)
     * 
     */
    public EclipticCoordinates(double lon, double lat, String sysSpec) {
        super(lon, lat, sysSpec);
    }

    /**
     * Instantiates new ecliptic coordinates tied to the ICRS frame.
     * 
     * @param lon       (rad) Ecliptic longitude angle [-&pi;:&pi;].
     * @param lat       (rad) Ecliptic latitude angle [-&pi;/2;&pi;/2].
     * 
     * @see #EclipticCoordinates(double, double, EquatorialSystem)
     * @see EquatorialSystem#ICRS
     * 
     */
    public EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Instantiates new ecliptic coordinates from a string representation of the coordinates, including
     * a representation of the reference system (if available).
     * 
     * @param text              the string representation of the coordinates, including their reference system.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     */
    public EclipticCoordinates(String text) throws ParseException {
        super(text);
    }
    
    @Override
    public EclipticCoordinates clone() { return (EclipticCoordinates) super.clone(); }
    
    @Override
    public EclipticCoordinates copy() { return (EclipticCoordinates) super.copy(); }
    
    @Override
    public EclipticCoordinates getTransformed(EquatorialTransform T) {
        return (EclipticCoordinates) super.getTransformed(T);
    }
    
    @Override
    public EclipticCoordinates getTransformedTo(EquatorialSystem system) {
        return (EclipticCoordinates) super.getTransformedTo(system);
    }
    
    @Override
    public String getFITSLongitudeStem() { return "ELON"; }


    @Override
    public String getFITSLatitudeStem() { return "ELAT"; }

    @Override
    public String getTwoLetterID() { return "EC"; }
    

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

    @Override
    public EquatorialCoordinates getEquatorialPole() { return equatorialPole; }

    @Override
    public double getZeroLongitude() { return Constant.rightAngle; }

    @Override
    public NumberFormat getLongitudeFormat(int decimals) {
        return Util.Af[decimals];
    }
    
    @Override
    public void transform(EquatorialTransform t) {
        EquatorialCoordinates equatorial = toEquatorial();
        t.transform(equatorial);
        fromEquatorial(equatorial);
        setSystem(equatorial.getSystem());
    }


    /** The default local coordinate system. */
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Ecliptic");
        defaultLocalCoordinateSystem = new CoordinateSystem("Ecliptic Offsets");

        CoordinateAxis longitudeAxis = createAxis("Ecliptic Longitude", "ELON", GreekLetter.lambda + "", af);
        CoordinateAxis latitudeAxis = createAxis("Ecliptic Latitude", "ELAT", GreekLetter.beta + "", af);
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Ecliptic Longitude Offset", "dELON", GreekLetter.Delta + " " + GreekLetter.lambda);
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Ecliptic Latitude Offset", "dELAT", GreekLetter.Delta + " " + GreekLetter.beta);

        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);       

    }



    /** The Constant inclination. */
    public static final double inclination = 23.0 * Unit.deg + 26.0 * Unit.arcmin + 30.0 * Unit.arcsec; // to equatorial    

    /** The Constant equatorialPole. */
    public static final EquatorialCoordinates equatorialPole = CelestialCoordinates.getPole(inclination, 0.0);


}

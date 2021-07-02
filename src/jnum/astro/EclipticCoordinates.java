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

import jnum.Constant;
import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;


public class EclipticCoordinates extends PrecessingCoordinates {

    private static final long serialVersionUID = 7687178545213533912L;



    public EclipticCoordinates() {
        super();
    }

    public EclipticCoordinates(CelestialCoordinates from) {
        super(from);
    }

    public EclipticCoordinates(EquatorialSystem system) {
        super(system);
    }

    public EclipticCoordinates(double lon, double lat, EquatorialSystem system) {
        super(lon, lat, system);
    }

    public EclipticCoordinates(double lon, double lat, String sysSpec) {
        super(lon, lat, sysSpec);
    }

    public EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    public EclipticCoordinates(String text) {
        super(text);
    }
    
    @Override
    public EclipticCoordinates clone() { return (EclipticCoordinates) super.clone(); }
    
    @Override
    public EclipticCoordinates copy() { return (EclipticCoordinates) super.copy(); }
    

    @Override
    public String getFITSLongitudeStem() { return "ELON"; }


    @Override
    public String getFITSLatitudeStem() { return "ELAT"; }

    @Override
    public String getTwoLetterCode() { return "EC"; }
    

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

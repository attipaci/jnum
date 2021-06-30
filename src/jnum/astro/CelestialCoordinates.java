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

import jnum.Constant;
import jnum.IncompatibleTypesException;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


// This is an abstract class for coordinate systems that are fixed (except perhaps a precession)
// w.r.t the distant stars (quasars)...
public abstract class CelestialCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = 1991797523903701648L;

    private static EquatorialCoordinates reuseEquatorial = new EquatorialCoordinates();


    public CelestialCoordinates() { super(); }


    public CelestialCoordinates(String text) { super(text); }


    public CelestialCoordinates(double lon, double lat) { super(lon, lat); }


    public CelestialCoordinates(CelestialCoordinates from) {
        convert(from, this);
    }


    @Override
    public CelestialCoordinates clone() { return (CelestialCoordinates) super.clone(); }

    @Override
    public CelestialCoordinates copy() { return (CelestialCoordinates) super.copy(); }


    public abstract EquatorialCoordinates getEquatorialPole();


    public abstract double getZeroLongitude();


    public double getEquatorialPositionAngle() {
        EquatorialCoordinates equatorialPole = getEquatorialPole();
        return Math.atan2(-equatorialPole.cosLat() * Math.sin(x()), equatorialPole.sinLat() * cosLat() - equatorialPole.cosLat() * sinLat() * Math.cos(x()));
    }



    public EquatorialCoordinates toEquatorial() {
        EquatorialCoordinates equatorial = new EquatorialCoordinates();
        toEquatorial(equatorial);
        return equatorial;
    }

    /**
     * Convert to equatorial, placing the result in the supplied destination coordinates.
     *
     * @param equatorial the equivalent equatorial coordinates
     */
    public void toEquatorial(EquatorialCoordinates equatorial) {
        if(equatorial.getSystem() == null) equatorial.setSystem(EquatorialSystem.ICRS);

        final EquatorialCoordinates pole = getEquatorialPole();

        CelestialCoordinates.inverseTransform(this, pole, -getZeroLongitude(), equatorial);

        if(!Util.equals(equatorial.getSystem(), pole.getSystem())) {
            new EquatorialTransform(equatorial.getSystem(), pole.getSystem()).transform(equatorial);
        }

    }

    /**
     * Convert from the specified equatorial coordinates, keeping the argument's epoch when applicable.
     *
     * @param equatorial the equatorial coordinates.
     */
    public void fromEquatorial(EquatorialCoordinates equatorial) {
        final EquatorialCoordinates pole = getEquatorialPole();

        if(!Util.equals(equatorial.getSystem(), pole.getSystem())) {
            equatorial = equatorial.clone();
            new EquatorialTransform(equatorial.getSystem(), pole.getSystem()).transform(equatorial);
        }

        CelestialCoordinates.transform(equatorial, pole, -getZeroLongitude(), this);
    }

    @Override
    public void convertFrom(Coordinate2D coords) throws IncompatibleTypesException {
        if(coords instanceof CelestialCoordinates) convertFrom((CelestialCoordinates) coords);
        else super.convertFrom(coords);
    }


    public void convertFrom(CelestialCoordinates other) {
        convert(other, this);
    }


    public void convertTo(CelestialCoordinates other) {
        convert(this, other);
    }


    public void toEcliptic(EclipticCoordinates ecliptic) { convertTo(ecliptic); }


    public void toGalactic(GalacticCoordinates galactic) { convertTo(galactic); }


    public void toSuperGalactic(SuperGalacticCoordinates supergal) { convertTo(supergal); }


    public EclipticCoordinates toEcliptic() {
        EclipticCoordinates ecliptic = new EclipticCoordinates();
        convertTo(ecliptic);
        return ecliptic;
    }


    public GalacticCoordinates toGalactic() {
        GalacticCoordinates galactic = new GalacticCoordinates();
        convertTo(galactic);
        return galactic;
    }


    public SuperGalacticCoordinates toSuperGalactic() {
        SuperGalacticCoordinates supergal = new SuperGalacticCoordinates();
        convertTo(supergal);
        return supergal;
    }


    public static void convert(CelestialCoordinates from, CelestialCoordinates to) {

        // If converting to same type, then just copy, precessing as necessary;
        if(to.getClass().equals(from.getClass())) {
            if(from instanceof PrecessingCoordinates) {
                PrecessingCoordinates pFrom = (PrecessingCoordinates) from;
                PrecessingCoordinates pTo = (PrecessingCoordinates) to;
                pTo.copy(pFrom);
                pTo.transform(new EquatorialTransform(pFrom.getSystem(), pTo.getSystem()));
            }
            else to.copy(from);
        }

        if(from instanceof EquatorialCoordinates) {
            to.fromEquatorial((EquatorialCoordinates) from);
        }
        else if(to instanceof EquatorialCoordinates) {
            from.toEquatorial((EquatorialCoordinates) to);
        }
        else synchronized(reuseEquatorial) {
            from.toEquatorial(reuseEquatorial);
            to.fromEquatorial(reuseEquatorial);
        }

    }


    public static EquatorialCoordinates getPole(double inclination, double risingRA) {
        return new EquatorialCoordinates(risingRA - Constant.rightAngle, Constant.rightAngle - inclination);
    }


    public static EquatorialCoordinates getPole(CelestialCoordinates referenceSystem, double inclination, double risingLON) {
        referenceSystem.set(risingLON - Constant.rightAngle, Constant.rightAngle - inclination);
        return referenceSystem.toEquatorial();
    }


    public static double getZeroLongitude(CelestialCoordinates from, CelestialCoordinates to) {
        EquatorialCoordinates equatorialZero = from.toEquatorial();
        to.fromEquatorial(equatorialZero);
        return to.nativeLongitude();		
    }

}

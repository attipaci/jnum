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
import jnum.IncompatibleTypesException;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;


/**
 * Base class for celestial coordinates of all sorts.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class CelestialCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = 1991797523903701648L;

    private static EquatorialCoordinates reuseEquatorial = new EquatorialCoordinates();

    /**
     * Constructs new celestial coordinates with default values (zero coordinates)
     * 
     */
    protected CelestialCoordinates() { super(); }

    /**
     * Constructs new celestial coordinates with the specified longitude and latitude angles. The convention
     * is that lon/lat constitutes a right handed coordinate system when looking towards the origin.
     * 
     * @param lon   (rad) longitude coordinate
     * @param lat   (lat) latitude coordinate
     */
    protected CelestialCoordinates(double lon, double lat) { super(lon, lat); }

    /**
     * Constructs new celestial coordinates from another set of celestial coordinates of the same or different type.
     * 
     * @param from      the other coordinates, which are mimicked or converted.
     */
    protected CelestialCoordinates(CelestialCoordinates from) {
        convert(from, this);
    }
    
    /**
     * Constructs new celestial coordinates from an ASCII representation.
     * 
     * @param text      A textual representation
     * @throws ParseException if the text could not be interpreted to create these coordinate object.
     */
    protected CelestialCoordinates(String text) throws ParseException { super(text); }


    @Override
    public CelestialCoordinates clone() { return (CelestialCoordinates) super.clone(); }

    @Override
    public CelestialCoordinates copy() { return (CelestialCoordinates) super.copy(); }

    /**
     * Returns the ICRS (~J2000) equatorial coordinates of this coordinate system's celestial pole.
     * 
     * @return  the ICRS (~J2000) equatorial coordinates for this system's celestial pole.
     */
    public abstract EquatorialCoordinates getEquatorialPole();

    
    /**
     * Returns the longitude of the ICRS (~J2000) equatorial origin in this coordinate system.
     * 
     * @return      (rad) The longitude of the equatorial origin in this coordinate system.
     */
    public abstract double getZeroLongitude();

    /**
     * Gets the position angle of the local axes of these coordinates in the ICRS (~J2000) equatorial
     * system.
     * 
     * @return      (rad) the position angle of the local axes for these coordinates in the ICRS (~J2000) equatorial frame.
     */
    public double getEquatorialPositionAngle() {
        EquatorialCoordinates pole = getEquatorialPole();
        return Math.atan2(pole.cosLat() * Math.sin(x()), pole.sinLat() * cosLat() - pole.cosLat() * sinLat() * Math.cos(x()));
    }


    /**
     * Gets the equatorial coordinate equivalent of these celestial coordinates. The returned equatorial
     * coordinates are usually expressed in the ICRS frame, but they do not have to be...
     * 
     * @return      coordinates for the same position in a suitable equatorial system (typically ICRS).
     */
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

    /**
     * Makes these coordinates refer to the same positions as the argument coordinates of the same or different
     * type. That is the coordinates in this object will be converted as necessary from the argument.
     * 
     * @param other     the coordinates specifying the celestial position for this object.
     */
    public void convertFrom(CelestialCoordinates other) {
        convert(other, this);
    }

    /**
     * Changes the supplied coordinates in the argument to refer to the same positions as these set of coordinates.
     * That is the coordinates in this object will be converted as necessary from the argument.
     * 
     * @param other     the coordinates that will be set to the same position as defined by this object.
     * 
     * @see #convertTo(Class)
     */
    public void convertTo(CelestialCoordinates other) {
        convert(this, other);
    }


    /**
     * Returns the location represented by these coordinates in another celestial coordinate system.
     * 
     * @param toClass   the class of celestial coordinates we want.
     * @return          the location of these coordinates in the specified other celestial coordinate type.
     * 
     * @see #convertTo(CelestialCoordinates)
     */
    public CelestialCoordinates convertTo(Class<? extends CelestialCoordinates> toClass) {
        EquatorialCoordinates equatorial = toEquatorial();
        CelestialCoordinates to = null;
        
        try { 
            to = toClass.getConstructor().newInstance(); 
            to.fromEquatorial(equatorial);
        } 
        catch (Exception e) { e.printStackTrace(); }
        
        return to;      
    }
    
    /**
     * Converts these coordinates to ecliptic coordinates, returning the result in the argument. 
     * 
     * @param ecliptic  The ecliptic coordinates (which speficy the reference system), into which 
     *                  the equivalent position to this one is returned.
     */
    public void toEcliptic(EclipticCoordinates ecliptic) { convertTo(ecliptic); }


    /**
     * Converts these coordinates to galactic coordinates, returning the result in the argument. 
     *
     * @param galactic  The galactic coordinates, into which the equivalent position to this one is returned.
     */
    public void toGalactic(GalacticCoordinates galactic) { convertTo(galactic); }

    /**
     * Converts these coordinates to supergalactic coordinates, returning the result in the argument. 
     * 
     * @param supergal  The supergalactic coordinates, into which the equivalent position to this one is returned.
     */
    public void toSuperGalactic(SuperGalacticCoordinates supergal) { convertTo(supergal); }

    
    /**
     * Gets an equivalent representation of these coordinates in an appropriate equatorial system 
     * (such as ICRS, but it does not have to be).
     * 
     * @return      Equatorial coordinates of the same celestial position as this one.
     */
    public EclipticCoordinates toEcliptic() {
        EclipticCoordinates ecliptic = new EclipticCoordinates();
        convertTo(ecliptic);
        return ecliptic;
    }

    /**
     * Gets an equivalent representation of these coordinates in the Galactic coordinate system 
     * 
     * @return      Galactic coordinates of the same celestial position as this one.
     */
    public GalacticCoordinates toGalactic() {
        GalacticCoordinates galactic = new GalacticCoordinates();
        convertTo(galactic);
        return galactic;
    }

    /**
     * Gets an equivalent representation of these coordinates in the Supergalactic coordinate system 
     * 
     * @return      Suppergalactic coordinates of the same celestial position as this one.
     */
    public SuperGalacticCoordinates toSuperGalactic() {
        SuperGalacticCoordinates supergal = new SuperGalacticCoordinates();
        convertTo(supergal);
        return supergal;
    }

    /**
     * Converts a celestion positions from one coordinate system to another. 
     * 
     * @param from      The input coordinates and system
     * @param to        The output coordinates and system
     */
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

    /**
     * Gets the equatorial location of the celestial pole, given the right ascention of the rising intercept of the equators.
     * 
     * @param inclination       (rad) The inclination angle of the celestial system w.r.t. the equatorial plane.
     * @param risingRA          (rad) The right-ascention at which the celestial equator rises above the ICRS equator,
     *                          in the direction of rising R.A.
     *                           
     * @return  the ICRS equatorial location of the celestial pole.
     */
    public static EquatorialCoordinates getPole(double inclination, double risingRA) {
        return new EquatorialCoordinates(risingRA - Constant.rightAngle, Constant.rightAngle - inclination);
    }

    /**
     * Gets the celestial location in the specified coordinate system of another celestial pole, 
     * given the longitude of the rising intercept of the equators.
     * 
     * @param referenceSystem   The celestial coordinate system in which the pole is defined.
     * @param inclination       (rad) The inclination angle of the celestial system w.r.t. the specified reference system.
     * @param risingLON         (rad) The longitude (counter-clockwise seen from the pole) at which the celestial equator 
     *                          rises above the reference equator in the direction of rising longitude.
     *                           
     * @return  the ICRS equatorial location of the celestial pole.
     */
    public static EquatorialCoordinates getPole(CelestialCoordinates referenceSystem, double inclination, double risingLON) {
        referenceSystem.set(risingLON - Constant.rightAngle, Constant.rightAngle - inclination);
        return referenceSystem.toEquatorial();
    }



}

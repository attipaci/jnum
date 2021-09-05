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
import jnum.text.GreekLetter;

/**
 * Standard galactic coordinates (<i>l</i>,<i>b</i>). The origin of the Galactic coordinate system is approximately (but not exactly)
 * in the direction of the Galactic Center, and its equator is more or less aligned to the Galactic Plane.
 * 
 * @author Attila Kovacs
 *
 */
public class GalacticCoordinates extends CelestialCoordinates {

    /** */
	private static final long serialVersionUID = -942734735652370919L;

	/**
	 * Instantiates new default Galactic coordinates.
	 */
    public GalacticCoordinates() {}

    /**
     * Instantiates new Galactic Coordinates, from a string representation of these. 
     * 
     * 
     * @param text              the string representation of the coordinates.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     *
     */
    public GalacticCoordinates(String text) throws ParseException { super(text); }

    /**
     * Instantiates new Galactic Coordinates with the specified conventional longitude and latitude angles.
     * 
     * @param lon       (rad) Galactic longitude angle
     * @param lat       (rad) Galactic latitude angle
     */
    public GalacticCoordinates(double lon, double lat) { super(lon, lat); }
    
    /**
     * Instantiates a new set of Galactic coordinates that represent 
     * the same location on sky as the specified other celestial coordinates.
     * 
     * @param from      the coordinates of the sky location in some other celestial system.
     * 
     * @see CelestialCoordinates#fromEquatorial(EquatorialCoordinates)
     * @see CelestialCoordinates#toEquatorial()
     */
    public GalacticCoordinates(CelestialCoordinates from) { super(from); }
    
    
    @Override
    public GalacticCoordinates clone() { return (GalacticCoordinates) super.clone(); }
    
    @Override
    public GalacticCoordinates copy() { return (GalacticCoordinates) super.copy(); }
    

    @Override
	public String getFITSLongitudeStem() { return "GLON"; }
	

	@Override
	public String getFITSLatitudeStem() { return "GLAT"; }
    
	
	@Override
    public String getTwoLetterID() { return "GA"; }
	
	@Override
	public CoordinateSystem getCoordinateSystem() {
	    return defaultCoordinateSystem;
	}

	@Override
	public CoordinateSystem getLocalCoordinateSystem() {
	    return defaultLocalCoordinateSystem;
	}


    @Override
	public EquatorialCoordinates getEquatorialPole() {
    	return equatorialPole_ICRS;
	}


	@Override
	public double getZeroLongitude() {
		return phi0_ICRS;
	}
	
	@Override
	public NumberFormat getLongitudeFormat(int decimals) {
	    return Util.Af[decimals];
	}
	
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Galactic");
        defaultLocalCoordinateSystem = new CoordinateSystem("Galactic Offsets");
        
        CoordinateAxis longitudeAxis = createAxis("Galactic Longitude", "GLON", "l", af);
        CoordinateAxis latitudeAxis = createAxis("Galactic Latitude", "GLAT", "b", af);
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Galactic Longitude Offset", "dGLON", GreekLetter.Delta + " l");
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Galactic Latitude Offset", "dGLAT", GreekLetter.Delta + " b");
        
        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);   
    }
    
    /**
     * The equatorial location of the Galactic Pole, defined in B1950.
     */
    public static final EquatorialCoordinates equatorialPole_B1950 = 
            new EquatorialCoordinates(12.0 * Unit.hourAngle + 49.0 * Unit.minuteAngle, 27.4 * Unit.deg, EquatorialSystem.FK4.B1950);
    
    /**
     * The equatorial location of the Galactic Pole, calculated for ICRS.
     */
    public static EquatorialCoordinates equatorialPole_ICRS = equatorialPole_B1950.getTransformedTo(EquatorialSystem.ICRS);
    
    /** The galactic longitude of the equatorial origin, defined in B1950 */
    public static final double phi0_B1950 = 123.0 * Unit.deg;
    
    /** The galactic longitude of the equatorial origin, calculated for ICRS */
    public static double phi0_ICRS;
    
    // Calculate the ICRS pole and phi0, s.t. conversion to/from ICRS is faster...
    static { 
        GalacticCoordinates zero = new GalacticCoordinates(phi0_B1950, 0.0);
        EquatorialCoordinates equatorialZero = zero.toEquatorial();
        equatorialZero.transform(EquatorialTransform.B1950toICRS);
        zero.fromEquatorial(equatorialZero);
        phi0_ICRS = zero.longitude();
    }

}

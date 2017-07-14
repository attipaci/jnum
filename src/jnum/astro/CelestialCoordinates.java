/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import jnum.Constant;
import jnum.IncompatibleTypesException;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
// This is an abstract class for coordinate systems that are fixed (except perhaps a precession)
// w.r.t the distant stars (quasars)...
/**
 * The Class CelestialCoordinates.
 */
public abstract class CelestialCoordinates extends SphericalCoordinates {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1991797523903701648L;


    /** The reuse equatorial. */
    private static EquatorialCoordinates reuseEquatorial = new EquatorialCoordinates();

	
	/**
	 * Instantiates a new celestial coordinates.
	 */
	public CelestialCoordinates() { super(); }
	
	/**
	 * Instantiates a new celestial coordinates.
	 *
	 * @param text the text
	 */
	public CelestialCoordinates(String text) { super(text); }
	
	/**
	 * Instantiates a new celestial coordinates.
	 *
	 * @param lon the lon
	 * @param lat the lat
	 */
	public CelestialCoordinates(double lon, double lat) { super(lon, lat); }
	
	/**
	 * Instantiates a new celestial coordinates.
	 *
	 * @param from the from
	 */
	public CelestialCoordinates(CelestialCoordinates from) {
		convert(from, this);
	}
	
	/**
	 * Gets the equatorial pole.
	 *
	 * @return the equatorial pole
	 */
	public abstract EquatorialCoordinates getEquatorialPole();
	
	/**
	 * Gets the zero longitude.
	 *
	 * @return the zero longitude
	 */
	public abstract double getZeroLongitude();
	
	
	/**
	 * Gets the equatorial position angle.
	 *
	 * @return the equatorial position angle
	 */
	public double getEquatorialPositionAngle() {
		EquatorialCoordinates equatorialPole = getEquatorialPole();
		return Math.atan2(-equatorialPole.cosLat() * Math.sin(x()), equatorialPole.sinLat() * cosLat() - equatorialPole.cosLat() * sinLat() * Math.cos(x()));
	}
	
	
	
	/**
	 * Convert to equatorial.
	 *
	 * @return the equivalent equatorial coordinates
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
        if(equatorial.epoch == null) equatorial.epoch = CoordinateEpoch.J2000;
    
	    final EquatorialCoordinates pole = getEquatorialPole();
		
		CelestialCoordinates.inverseTransform(this, pole, getZeroLongitude(), equatorial);
		
		if(!Util.equals(equatorial.epoch, pole.epoch)) {
			final CoordinateEpoch epoch = equatorial.epoch;
			equatorial.epoch = pole.epoch;
			try { equatorial.precess(epoch); }
			catch(UndefinedEpochException e) {}
		}
		
	}
	
	/**
	 * Convert from the specified equatorial coordinates, keeping the argument's epoch when applicable.
	 *
	 * @param equatorial the equatorial coordinates.
	 */
	public void fromEquatorial(EquatorialCoordinates equatorial) {
		final EquatorialCoordinates pole = getEquatorialPole();
		
		if(!Util.equals(equatorial.epoch, pole.epoch)) {
			equatorial = (EquatorialCoordinates) equatorial.clone();
			try { equatorial.precess(pole.epoch); }
			catch(UndefinedEpochException e) {}
		}
		
		CelestialCoordinates.transform(equatorial, pole, getZeroLongitude(), this);
	}
	
	@Override
    public void convertFrom(Coordinate2D coords) throws IncompatibleTypesException {
	    if(coords instanceof CelestialCoordinates) convertFrom((CelestialCoordinates) coords);
	    else super.convertFrom(coords);
	}
	
	/**
	 * Convert from.
	 *
	 * @param other the other
	 */
	public void convertFrom(CelestialCoordinates other) {
		convert(other, this);
	}
	
	/**
	 * Convert to.
	 *
	 * @param other the other
	 */
	public void convertTo(CelestialCoordinates other) {
		convert(this, other);
	}
	
	/**
	 * To ecliptic.
	 *
	 * @param ecliptic the ecliptic
	 */
	public void toEcliptic(EclipticCoordinates ecliptic) { convertTo(ecliptic); }
	
	/**
	 * To galactic.
	 *
	 * @param galactic the galactic
	 */
	public void toGalactic(GalacticCoordinates galactic) { convertTo(galactic); }
	
	/**
	 * To super galactic.
	 *
	 * @param supergal the supergal
	 */
	public void toSuperGalactic(SuperGalacticCoordinates supergal) { convertTo(supergal); }
	
	/**
	 * To ecliptic.
	 *
	 * @return the ecliptic coordinates
	 */
	public EclipticCoordinates toEcliptic() {
		EclipticCoordinates ecliptic = new EclipticCoordinates();
		convertTo(ecliptic);
		return ecliptic;
	}
	
	/**
	 * To galactic.
	 *
	 * @return the galactic coordinates
	 */
	public GalacticCoordinates toGalactic() {
		GalacticCoordinates galactic = new GalacticCoordinates();
		convertTo(galactic);
		return galactic;
	}
		
	/**
	 * To super galactic.
	 *
	 * @return the super galactic coordinates
	 */
	public SuperGalacticCoordinates toSuperGalactic() {
		SuperGalacticCoordinates supergal = new SuperGalacticCoordinates();
		convertTo(supergal);
		return supergal;
	}
	
		
	/**
	 * Convert.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public static void convert(CelestialCoordinates from, CelestialCoordinates to) {
	    
	    // If converting to same type, then just copy, precessing as necessary;
		if(to.getClass().equals(from.getClass())) {
			if(from instanceof Precessing) {
				to.copy(from);
				try { ((Precessing) to).precess(((Precessing) to).getEpoch()); }
				catch(UndefinedEpochException e) {}
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
	 * Gets the pole.
	 *
	 * @param inclination the inclination
	 * @param risingRA the rising ra
	 * @return the pole
	 */
	public static EquatorialCoordinates getPole(double inclination, double risingRA) {
		return new EquatorialCoordinates(risingRA - Constant.rightAngle, Constant.rightAngle - inclination);
	}

	/**
	 * Gets the pole.
	 *
	 * @param referenceSystem the reference system
	 * @param inclination the inclination
	 * @param risingLON the rising lon
	 * @return the pole
	 */
	public static EquatorialCoordinates getPole(CelestialCoordinates referenceSystem, double inclination, double risingLON) {
		referenceSystem.set(risingLON - Constant.rightAngle, Constant.rightAngle - inclination);
		return referenceSystem.toEquatorial();
	}
	
	/**
	 * Gets the zero longitude.
	 *
	 * @param from the from
	 * @param to the to
	 * @return the zero longitude
	 */
	public static double getZeroLongitude(CelestialCoordinates from, CelestialCoordinates to) {
		EquatorialCoordinates equatorialZero = from.toEquatorial();
    	to.fromEquatorial(equatorialZero);
    	return to.nativeLongitude();		
	}

	
	
}

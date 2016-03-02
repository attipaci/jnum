/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2007 Attila Kovacs 

package jnum.astro;

import jnum.Constant;
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
	 * To equatorial.
	 *
	 * @return the equatorial coordinates
	 */
	public EquatorialCoordinates toEquatorial() {
		EquatorialCoordinates equatorial = new EquatorialCoordinates();
		toEquatorial(equatorial);
		return equatorial;
	}
	
	/**
	 * To equatorial.
	 *
	 * @param equatorial the equatorial
	 */
	public void toEquatorial(EquatorialCoordinates equatorial) {
		final EquatorialCoordinates pole = getEquatorialPole();
		
		CelestialCoordinates.inverseTransform(this, pole, getZeroLongitude(), equatorial);
		
		if(!equatorial.epoch.equals(pole.epoch)) {
			final CoordinateEpoch epoch = equatorial.epoch;
			equatorial.epoch = pole.epoch;
			equatorial.precess(epoch);			
		}
		
	}
	
	/**
	 * From equatorial.
	 *
	 * @param equatorial the equatorial
	 */
	public synchronized void fromEquatorial(EquatorialCoordinates equatorial) {
		final EquatorialCoordinates pole = getEquatorialPole();
		
		if(!equatorial.epoch.equals(pole.epoch)) {
			equatorial = (EquatorialCoordinates) equatorial.clone();
			equatorial.precess(pole.epoch);			
		}
		
		CelestialCoordinates.transform(equatorial, pole, getZeroLongitude(), this);
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
	
	
	/** The reuse equatorial. */
	private static EquatorialCoordinates reuseEquatorial = new EquatorialCoordinates();
	
	/**
	 * Convert.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public static synchronized void convert(CelestialCoordinates from, CelestialCoordinates to) {
		
		if(from.getClass().equals(to.getClass())) {
			if(from instanceof Precessing) {
				CoordinateEpoch toEpoch = ((Precessing) to).getEpoch();
				to.copy(from);
				((Precessing) to).precess(toEpoch);
			}
			else to.copy(from);
		}
		
		if(from instanceof EquatorialCoordinates) reuseEquatorial = (EquatorialCoordinates) from;
		else from.toEquatorial(reuseEquatorial);
		
		if(to instanceof EquatorialCoordinates) {
			if(!reuseEquatorial.epoch.equals(((EquatorialCoordinates) to).epoch)) reuseEquatorial.precess(((EquatorialCoordinates) to).epoch);
			to.copy(reuseEquatorial);
		}
		else to.fromEquatorial(reuseEquatorial);
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

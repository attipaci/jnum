/*******************************************************************************
 * Copyright (c) 201 Attila Kovacs <attila[AT]sigmyne.com>.
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


package jnum.projection;

import java.util.Hashtable;

import jnum.Constant;
import jnum.Unit;
import jnum.Util;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


// TODO: Auto-generated Javadoc
// TODO Read fits Headers (for extra information on projection parameters)...
// TODO Implement a few more projections...

// Based on Calabretta & Greisen 2002
/**
 * The Class SphericalProjection.
 */
public abstract class SphericalProjection extends Projection2D<SphericalCoordinates> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4978006433879954740L;

	// the reference in celestial (alpha0, delta0)
	/** The native reference. */
	private SphericalCoordinates nativeReference; // the reference in native (phi0, theta0)
	
	/** The native pole. */
	private SphericalCoordinates nativePole; // the pole in native (phip, thetap)
	
	/** The pole. */
	private SphericalCoordinates celestialPole; // the pole in celestial (alphap, deltap)
	
	/** The user pole. */
	private boolean userPole = false; // True if not using the default pole.
	
	/** The user reference. */
	private boolean userReference = false; // True if not using the default native reference.
	
	/** The inverted fits axes. */
	protected boolean invertedFITSAxes = false;	// Whether first axis is longitude...
	
	/** The select solution. */
	private int selectSolution = SELECT_NEAREST_POLE;	
	
	
	
	/**
	 * Instantiates a new spherical projection.
	 */
	public SphericalProjection() {
		nativeReference = new SphericalCoordinates(0.0, 0.0); // phi0, theta0;
		nativePole = new SphericalCoordinates(0.0, rightAngle); // phip, thetap;		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.projection.Projection2D#clone()
	 */
	@Override
	public Object clone() {
		SphericalProjection clone = (SphericalProjection) super.clone();
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof SphericalProjection)) return false;
		if(!super.equals(o)) return false;
		SphericalProjection projection = (SphericalProjection) o;
		
		if(projection.userPole != userPole) return false;
		if(!Util.equals(projection.nativeReference, nativeReference)) return false;
		if(!Util.equals(projection.nativePole, nativePole)) return false;
		if(isRightAnglePole()) if(!Util.equals(projection.celestialPole, celestialPole)) return false;
		return super.equals(o);		
	}
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode() ^ (userPole ? 1 : 0);
		if(nativeReference != null) hash ^= nativeReference.hashCode();
		if(!isRightAnglePole()) if(celestialPole != null) hash ^= celestialPole.hashCode();
		if(nativePole != null) hash ^= nativePole.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#copy()
	 */
	@Override
	public Projection2D<SphericalCoordinates> copy() {
		SphericalProjection copy = (SphericalProjection) super.copy();
		if(celestialPole != null) copy.celestialPole = (SphericalCoordinates) celestialPole.copy();
		if(nativeReference != null) copy.nativeReference = (SphericalCoordinates) nativeReference.copy();
		if(nativePole != null) copy.nativePole = (SphericalCoordinates) nativePole.copy();
		return copy;
	}

	
	/**
	 * Checks if is right angle pole.
	 *
	 * @return true, if is right angle pole
	 */
	public boolean isRightAnglePole() {
		return SphericalCoordinates.equalAngles(Math.abs(celestialPole.y()), rightAngle);
	}
	
	// Global projection
	/* (non-Javadoc)
	 * @see jnum.Projection2D#project(jnum.Coordinate2D, jnum.Coordinate2D)
	 */
	@Override
	public void project(final SphericalCoordinates coords, final Coordinate2D toProjected) {		
		final double dLON = coords.x() - celestialPole.x();
		double phi = Double.NaN, theta = Double.NaN;
		
		if(isRightAnglePole()) {
			if(celestialPole.y() > 0.0) {
				phi = nativePole.x() + dLON + Math.PI;
				theta = coords.y();
			}
			else {
				phi = nativePole.x() - dLON;
				theta = -coords.y();
			}	
		}
		else {
			final double cosdLON = Math.cos(dLON);
			
			phi = nativePole.x() + Math.atan2(
					-coords.cosLat() * Math.sin(dLON),
					coords.sinLat() * celestialPole.cosLat() - coords.cosLat() * celestialPole.sinLat() * cosdLON);
			
			theta = asin(coords.sinLat() * celestialPole.sinLat() + coords.cosLat() * celestialPole.cosLat() * cosdLON);
		}
	
		phi = Math.IEEEremainder(phi, twoPI);
		
		//System.err.println(Util.f2.format(phi/Unit.deg) + ", " + Util.f2.format(theta/Unit.deg));
		
		getOffsets(theta, phi, toProjected);
	}
	
	// Global deprojection
	/* (non-Javadoc)
	 * @see jnum.Projection2D#deproject(jnum.Coordinate2D, jnum.Coordinate2D)
	 */
	@Override
	public void deproject(final Coordinate2D projected, final SphericalCoordinates toCoords) {	
		getPhiTheta(projected, toCoords);
		
		final double dPhi = toCoords.x() - nativePole.x();
		
		if(isRightAnglePole()) {
			if(celestialPole.y() > 0.0) toCoords.setX(celestialPole.x() + dPhi - Math.PI);
			else {
				toCoords.setX(celestialPole.x() - dPhi);
				toCoords.invertY();
			}	
		}
		else {
			final double cosTheta = toCoords.cosLat();
			final double sinTheta = toCoords.sinLat();	
			final double cosdPhi = Math.cos(dPhi);
					
			toCoords.setX(celestialPole.x() + Math.atan2(
					-cosTheta * Math.sin(dPhi),
					sinTheta * celestialPole.cosLat() - cosTheta * celestialPole.sinLat() * cosdPhi));	
			
			toCoords.setY(asin(sinTheta * celestialPole.sinLat() + cosTheta * celestialPole.cosLat() * cosdPhi));
		}
		
		toCoords.standardize();
	}
	
	
	/**
	 * Convert offsets to phi, theta.
	 *
	 * @param offset the offset
	 * @param phiTheta the phi theta
	 * @return the double
	 */
	protected abstract void getPhiTheta(Coordinate2D offset, SphericalCoordinates phiTheta);

	
	/**
	 * Gets the offsets.
	 *
	 * @param theta the theta
	 * @param phi the phi
	 * @param toOffset the to offset
	 * @return the offsets
	 */
	protected abstract void getOffsets(double theta, double phi, Coordinate2D toOffset);
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#setReference(jnum.Coordinate2D)
	 */
	@Override
	public void setReference(SphericalCoordinates coords) {
		super.setReference(coords); 
		
		if(!userPole) setDefaultNativePole();	
		calcCelestialPole();	
	}
	
	/**
	 * Calc celestial pole.
	 */
	protected void calcCelestialPole() {
		celestialPole = new SphericalCoordinates();	
		SphericalCoordinates reference = getReference();
		
		final double dPhi = nativePole.x() - nativeReference.x();
		final double sindPhi = Math.sin(dPhi);
		final double cosdPhi = Math.cos(dPhi);
		
		double deltap1 = Math.atan2(nativeReference.sinLat(), nativeReference.cosLat() * cosdPhi);
		double cs = nativeReference.cosLat() * sindPhi;
		
		double deltap2 = acos(reference.sinLat() / Math.sqrt(1.0 - cs*cs));
		
		double deltaN = deltap1 + deltap2;
		double deltaS = deltap1 - deltap2;
		
		// make delta2 > delta1
		if(deltaN > deltaS) {
			double temp = deltaS;
			deltaS = deltaN;
			deltaN = temp;
		}
		
		int solutions = 0;
		// Or, the pole nearest to the native latitude specification...
		// (northern by default).
		if(Math.abs(deltaN) <= rightAngle) { celestialPole.setY(deltaN); solutions++; }
		if(Math.abs(deltaS) <= rightAngle) {
			solutions++;
			// If two solutions exists, chose the one closer to the native pole...
			if(solutions == 1) celestialPole.setY(deltaS); 
			else if(selectSolution == SELECT_SOUTHERN_POLE) celestialPole.setY(deltaS); 
			else if(selectSolution == SELECT_NEAREST_POLE) 
				if(Math.abs(deltaS - nativePole.y()) < Math.abs(deltaN - nativePole.y())) celestialPole.setY(deltaS); 
		}
		
		if(solutions == 0) throw new IllegalArgumentException("No solutions for celestial pole.");
		
		//System.err.println(solutions + " solution(s)");
		
		if(SphericalCoordinates.equalAngles(Math.abs(reference.y()), rightAngle)) 
			celestialPole.setX(reference.x());
		else if(isRightAnglePole()) {
			celestialPole.setX(reference.x() + (celestialPole.y() > 0.0 ? 
						nativePole.x() - nativeReference.x() - Math.PI :
						nativeReference.x() - nativePole.x()));
		}
		else {
			double sindLON = sindPhi * nativeReference.cosLat() / reference.cosLat();
			double cosdLON = (nativeReference.sinLat() - celestialPole.sinLat() * reference.sinLat()) / (celestialPole.cosLat() * reference.cosLat());
			celestialPole.setX(reference.x() - Math.atan2(sindLON, cosdLON));
		}
		
		celestialPole.standardize();
	}
	
	
		
	/**
	 * Gets the native pole.
	 *
	 * @return the native pole
	 */
	public SphericalCoordinates getNativePole() { return nativePole; }
	
	/**
	 * Gets the celestial pole.
	 *
	 * @return the celestial pole
	 */
	public SphericalCoordinates getCelestialPole() { return celestialPole; }
	
	/**
	 * Gets the native reference.
	 *
	 * @return the native reference
	 */
	protected SphericalCoordinates getNativeReference() { return nativeReference; }
	
	
	/**
	 * Sets the native pole.
	 *
	 * @param nativeCoords the new native pole
	 */
	public void setNativePole(SphericalCoordinates nativeCoords) {
		userPole = true;
		nativePole = nativeCoords;
	}
	
	/**
	 * Sets the default native pole.
	 */
	public void setDefaultNativePole() {
		userPole = false;
		SphericalCoordinates reference = getReference();
		if(reference != null) nativePole.setX(reference.y() >= nativeReference.y() ? 0 : Math.PI); 
	}
	
	/**
	 * Sets the celestial pole.
	 *
	 * @param coords the new celestial pole
	 */
	public void setCelestialPole(SphericalCoordinates coords) { this.celestialPole = coords; }
	
	
	/**
	 * Sets the default pole.
	 */
	public void setDefaultPole() {
		userPole = false;
		nativePole.zero();
		setReference(getReference());
	}
	

	/* (non-Javadoc)
	 * @see jnum.Projection2D#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String alt) throws HeaderCardException {		
		SphericalCoordinates reference = getReference();
		CoordinateSystem axes = getReference().getCoordinateSystem();
			
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
		c.add(new HeaderCard("CTYPE1" + alt, reference.getFITSLongitudeStem() + "-" + getFitsID(), axes.get(0).getShortLabel() + " in " + getFullName() + " projection."));
		c.add(new HeaderCard("CTYPE2" + alt, reference.getFITSLatitudeStem() + "-" + getFitsID(), axes.get(1).getShortLabel() + " in " + getFullName() + " projection."));
		
		if(userPole) {
			c.add(new HeaderCard("LONPOLE" + alt, nativePole.x() / Unit.deg, "The longitude (deg) of the native pole."));
			c.add(new HeaderCard("LATPOLE" + alt, nativePole.y() / Unit.deg, "The latitude (deg) of the native pole."));
		}
		if(userReference) {
			String lonPrefix = getLongitudeParameterPrefix();
			c.add(new HeaderCard(lonPrefix + "1" + alt, nativeReference.x() / Unit.deg, "The longitude (deg) of the native reference."));
			c.add(new HeaderCard(lonPrefix + "2" + alt, nativeReference.y() / Unit.deg, "The latitude (deg) of the native reference."));			
			// TODO should calculate and write PV0_j offsets
		}	
	}
	
	/**
	 * Gets the longitude parameter prefix.
	 *
	 * @return the longitude parameter prefix
	 */
	protected String getLongitudeParameterPrefix() {
		return "PV" + (invertedFITSAxes ? 2 : 1) + "_";
	}
	
	/**
	 * Gets the latitude parameter prefix.
	 *
	 * @return the latitude parameter prefix
	 */
	protected String getLatitudeParameterPrefix() {
		return "PV" + (invertedFITSAxes ? 1 : 2) + "_";
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parseHeader(Header header, String alt) {
		String axis1 = header.getStringValue("CTYPE1" + alt).toLowerCase();
		if(axis1.startsWith("dec")) invertedFITSAxes = true;
		else if(axis1.startsWith("lat")) invertedFITSAxes = true;
		else if(axis1.indexOf("lat") == 1) invertedFITSAxes = true;
		else invertedFITSAxes = false;
		
		String lonPrefix = getLongitudeParameterPrefix();
		
		String parName = lonPrefix + "3" + alt;
		if(header.containsKey(parName)) {
			userPole = true;
			nativePole.setX(header.getDoubleValue(parName) * Unit.deg);
		}
		else if(header.containsKey("LONPOLE" + alt)) {
			userPole = true;
			nativePole.setX(header.getDoubleValue("LONPOLE" + alt) * Unit.deg);
		}
		
		
		parName = lonPrefix + "4" + alt;
		if(header.containsKey(parName)) {
			userPole = true;
			setNativePoleLatitude(header.getDoubleValue(parName) * Unit.deg);			
		}
		else if(header.containsKey("LATPOLE" + alt)) {
			userPole = true;
			setNativePoleLatitude(header.getDoubleValue("LATPOLE" + alt) * Unit.deg);
		}
		
		
		parName = lonPrefix + "1" + alt;
		if(header.containsKey(parName)) {
			userReference = true;
			nativeReference.setX(header.getDoubleValue(parName) * Unit.deg);
		}
		
		parName = lonPrefix + "2" + alt;
		if(header.containsKey(parName)) {
			userReference = true;
			nativeReference.setY(header.getDoubleValue(parName) * Unit.deg);
		}
		// TODO reference offset PV0_j should be used also...		
	}
	
	/**
	 * Sets the native pole latitude.
	 *
	 * @param value the new native pole latitude
	 */
	private void setNativePoleLatitude(double value) {
		if(Math.abs(value) <= rightAngle) {
			nativePole.setY(value);
			selectSolution = SELECT_NEAREST_POLE;
		}
		else selectSolution = value > 0.0 ? SELECT_NORTHERN_POLE : SELECT_SOUTHERN_POLE;
	}
	
	/* (non-Javadoc)
	 * @see jnum.Projection2D#getCoordinateInstance()
	 */
	@Override
	public SphericalCoordinates getCoordinateInstance() {
		return new SphericalCoordinates();
	}
	
	// Safe asin and acos for when rounding errors make values fall outside of -1:1 range.
	/**
	 * Asin.
	 *
	 * @param value the value
	 * @return the double
	 */
	protected final static double asin(double value) {
		if(value < -1.0) value = -1.0;
		else if(value > 1.0) value = 1.0;
		return Math.asin(value);
	}
	
	/**
	 * Acos.
	 *
	 * @param value the value
	 * @return the double
	 */
	protected final static double acos(double value) {
		if(value < -1.0) value = -1.0;
		else if(value > 1.0) value = 1.0;
		return Math.acos(value);
	}
	
	/** The registry. */
	static Hashtable<String, Class<? extends SphericalProjection>> registry;
	
	static {
		registry = new Hashtable<String, Class<? extends SphericalProjection>>();
		register(new SlantOrthographic()); // SIN
		register(new Gnomonic()); // TAN
		register(new ZenithalEqualArea()); // ZEA
		register(new SansonFlamsteed()); // SFL
		register(new Mercator()); // MER
		register(new PlateCarree()); // CAR
		register(new HammerAitoff()); // AIT
		register(new GlobalSinusoidal()); // GLS	
		register(new Stereographic()); // STG
		register(new ZenithalEquidistant()); // ARC
		register(new Polyconic()); // PCO
		register(new BonnesProjection()); // BON
		register(new CylindricalPerspective()); // CYP
		register(new CylindricalEqualArea()); // CEA
		register(new ParabolicProjection()); // PAR
	}
	
	/**
	 * Register.
	 *
	 * @param projection the projection
	 */
	public static void register(SphericalProjection projection) {
		registry.put(projection.getFitsID(), projection.getClass());
		registry.put(projection.getFullName().toLowerCase(), projection.getClass());
		registry.put(projection.getClass().getSimpleName().toLowerCase(), projection.getClass());		
	}
	
	// Find projection by FITS name, full name, or class name...
	/**
	 * For name.
	 *
	 * @param name the name
	 * @return the spherical projection
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public static SphericalProjection forName(String name) throws InstantiationException, IllegalAccessException {
		Class<? extends SphericalProjection> projectionClass = registry.get(name);
		if(projectionClass != null) return projectionClass.newInstance();
		throw new InstantiationException("No projection " + name + " in registry.");
	}


	/** The Constant twoPI. */
	public final static double twoPI = Constant.twoPi;
	
	/** The Constant rightAngle. */
	public final static double rightAngle = Constant.rightAngle;

	/** The Constant SELECT_NEAREST_POLE. */
	public final static int SELECT_NEAREST_POLE = 0;
	
	/** The Constant SELECT_NORTHERN_POLE. */
	public final static int SELECT_NORTHERN_POLE = 1;
	
	/** The Constant SELECT_SOUTHERN_POLE. */
	public final static int SELECT_SOUTHERN_POLE = -1;
	
}

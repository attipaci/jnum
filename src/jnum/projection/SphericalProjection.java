/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum.projection;

import java.lang.reflect.InvocationTargetException;
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


// Based on Calabretta & Greisen 2002
public abstract class SphericalProjection extends Projection2D<SphericalCoordinates> {

	private static final long serialVersionUID = -4978006433879954740L;

	// the reference in celestial (alpha0, delta0)
	private SphericalCoordinates nativeReference; // the reference in native (phi0, theta0)

	private SphericalCoordinates nativePole; // the pole in native (phip, thetap)

	private SphericalCoordinates celestialPole; // the pole in celestial (alphap, deltap)

	private boolean userPole = false; // True if not using the default pole.

	private boolean userReference = false; // True if not using the default native reference.

	protected boolean invertedFITSAxes = false;	// Whether first axis is longitude...

	private int selectSolution = SELECT_NEAREST_POLE;	
	
	
	public SphericalProjection() {
		nativeReference = new SphericalCoordinates(0.0, 0.0); // phi0, theta0;
		nativePole = new SphericalCoordinates(0.0, rightAngle); // phip, thetap;		
	}
	

	@Override
	public SphericalProjection clone() {
		SphericalProjection clone = (SphericalProjection) super.clone();
		return clone;
	}
	

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
	
	@Override
	public int hashCode() {
		int hash = super.hashCode() ^ (userPole ? 1 : 0);
		if(nativeReference != null) hash ^= nativeReference.hashCode();
		if(!isRightAnglePole()) if(celestialPole != null) hash ^= celestialPole.hashCode();
		if(nativePole != null) hash ^= nativePole.hashCode();
		return hash;
	}
	

	@Override
	public SphericalProjection copy() {
		SphericalProjection copy = (SphericalProjection) super.copy();
		if(celestialPole != null) copy.celestialPole = celestialPole.copy();
		if(nativeReference != null) copy.nativeReference = nativeReference.copy();
		if(nativePole != null) copy.nativePole = nativePole.copy();
		return copy;
	}


	public boolean isRightAnglePole() {
		return SphericalCoordinates.equalAngles(Math.abs(celestialPole.y()), rightAngle);
	}
	
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
			
		getOffsets(theta, phi, toProjected);
	}
	

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
	

	protected abstract void getPhiTheta(Coordinate2D offset, SphericalCoordinates phiTheta);
	

	protected abstract void getOffsets(double theta, double phi, Coordinate2D toOffset);
	

	@Override
	public void setReference(SphericalCoordinates coords) {
		super.setReference(coords); 
		
		if(!userPole) setDefaultNativePole();	
		calcCelestialPole();	
	}
	

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
	

	public SphericalCoordinates getNativePole() { return nativePole; }
	

	public SphericalCoordinates getCelestialPole() { return celestialPole; }
	

	protected SphericalCoordinates getNativeReference() { return nativeReference; }
	

	public void setNativePole(SphericalCoordinates nativeCoords) {
		userPole = true;
		nativePole = nativeCoords;
	}
	

	public void setDefaultNativePole() {
		userPole = false;
		SphericalCoordinates reference = getReference();
		if(reference != null) nativePole.setX(reference.y() >= nativeReference.y() ? 0 : Math.PI); 
	}
	

	public void setCelestialPole(SphericalCoordinates coords) { this.celestialPole = coords; }


	public void setDefaultPole() {
		userPole = false;
		nativePole.zero();
		setReference(getReference());
	}
	

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
	

	protected String getLongitudeParameterPrefix() {
		return "PV" + (invertedFITSAxes ? 2 : 1) + "_";
	}
	

	protected String getLatitudeParameterPrefix() {
		return "PV" + (invertedFITSAxes ? 1 : 2) + "_";
	}
	

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
	
	private void setNativePoleLatitude(double value) {
		if(Math.abs(value) <= rightAngle) {
			nativePole.setY(value);
			selectSolution = SELECT_NEAREST_POLE;
		}
		else selectSolution = value > 0.0 ? SELECT_NORTHERN_POLE : SELECT_SOUTHERN_POLE;
	}
	
	@Override
	public SphericalCoordinates getCoordinateInstance() {
		return new SphericalCoordinates();
	}
	
	// Safe asin and acos for when rounding errors make values fall outside of -1:1 range.
	protected final static double asin(double value) {
		if(value < -1.0) value = -1.0;
		else if(value > 1.0) value = 1.0;
		return Math.asin(value);
	}

	protected final static double acos(double value) {
		if(value < -1.0) value = -1.0;
		else if(value > 1.0) value = 1.0;
		return Math.acos(value);
	}
	
	/** The registry. */
	static Hashtable<String, Class<? extends SphericalProjection>> registry;
	
	static {
		registry = new Hashtable<>();
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
	

	public static void register(SphericalProjection projection) {
		registry.put(projection.getFitsID(), projection.getClass());
		registry.put(projection.getFullName().toLowerCase(), projection.getClass());
		registry.put(projection.getClass().getSimpleName().toLowerCase(), projection.getClass());		
	}
	
	// Find projection by FITS name, full name, or class name...
	public static SphericalProjection forName(String name) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<? extends SphericalProjection> projectionClass = registry.get(name);
		if(projectionClass != null) return projectionClass.getConstructor().newInstance();
		throw new InstantiationException("No projection " + name + " in registry.");
	}


	/** the Constant 2*pi */
	public final static double twoPI = Constant.twoPi;
	
	/** The Constant for right angle (pi/2). */
	public final static double rightAngle = Constant.rightAngle;


	public final static int SELECT_NEAREST_POLE = 0;
	
	public final static int SELECT_NORTHERN_POLE = 1;

	public final static int SELECT_SOUTHERN_POLE = -1;
	
}

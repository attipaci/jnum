/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.astro;

import java.io.Serializable;

import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
/**
 * The Class AstroSystem.
 */
public class AstroSystem implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1426502427666674606L;
	
	/** The system. */
	private Class<? extends SphericalCoordinates> system;
	
	/**
	 * Instantiates a new astro system.
	 *
	 * @param coordType the coord type
	 */
	public AstroSystem(Class<? extends SphericalCoordinates> coordType) {
		this.system = coordType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof AstroSystem)) return false;
		if(!super.equals(o)) return false;
		AstroSystem a = (AstroSystem) o;
		if(a.system == null) return false;
		return a.system.equals(system); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(system != null) hash ^= system.hashCode();
		return hash;
	}
	
	/**
	 * Checks if is horizontal.
	 *
	 * @return true, if is horizontal
	 */
	public boolean isHorizontal() { return HorizontalCoordinates.class.isAssignableFrom(system); }

	/**
	 * Checks if it is focal plane coordinates.
	 *
	 * @return true, if focal plane coordinates
	 */
	public boolean isFocalPlane() { return FocalPlaneCoordinates.class.isAssignableFrom(system); }
	
	/**
	 * Checks if is equatorial.
	 *
	 * @return true, if is equatorial
	 */
	public boolean isEquatorial() { return EquatorialCoordinates.class.isAssignableFrom(system); }
	
	/**
	 * Checks if is ecliptic.
	 *
	 * @return true, if is ecliptic
	 */
	public boolean isEcliptic() { return EclipticCoordinates.class.isAssignableFrom(system); }

	/**
	 * Checks if is galactic.
	 *
	 * @return true, if is galactic
	 */
	public boolean isGalactic()  { return GalacticCoordinates.class.isAssignableFrom(system); }
	
	/**
	 * Checks if is super galactic.
	 *
	 * @return true, if is super galactic
	 */
	public boolean isSuperGalactic()  { return SuperGalacticCoordinates.class.isAssignableFrom(system); }
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getID() { return getID(system); }
	
	/**
	 * Gets the coordinate instance.
	 *
	 * @return the coordinate instance
	 */
	public SphericalCoordinates getCoordinateInstance() {
		if(system == null) return null;
		try { return system.newInstance(); } 
		catch(Exception e) { return null; }
	}
	
	/**
	 * Gets the id.
	 *
	 * @param coordType the coord type
	 * @return the id
	 */
	public static String getID(Class<? extends SphericalCoordinates> coordType) {
		if(HorizontalCoordinates.class.isAssignableFrom(coordType)) return "HO";
		else if(EquatorialCoordinates.class.isAssignableFrom(coordType)) return "EQ";
		else if(EclipticCoordinates.class.isAssignableFrom(coordType)) return "EC";
		else if(GalacticCoordinates.class.isAssignableFrom(coordType)) return "GL";
		else if(SuperGalacticCoordinates.class.isAssignableFrom(coordType)) return "SG";
		else return "--";
	}
	
	/**
	 * Gets the id.
	 *
	 * @param coords the coords
	 * @return the id
	 */
	public static String getID(AstroSystem coords) {
		if(coords.isHorizontal()) return "HO";
		if(coords.isEquatorial()) return "EQ";
		if(coords.isEcliptic()) return "EC";
		if(coords.isGalactic()) return "GL";
		if(coords.isSuperGalactic()) return "SG";
		return "--";
	}
	
	/**
	 * Gets the coordinate class.
	 *
	 * @param id the id
	 * @return the coordinate class
	 */
	public static Class<? extends SphericalCoordinates> getCoordinateClass(String id) {
		id = id.toUpperCase();
		if(id.equals("ho")) return HorizontalCoordinates.class;
		if(id.equals("eq")) return EquatorialCoordinates.class;
		if(id.equals("ec")) return EclipticCoordinates.class;
		if(id.equals("gl")) return GalacticCoordinates.class;
		if(id.equals("sg")) return SuperGalacticCoordinates.class;
		return null;
	}
	
	/**
	 * Gets the coordinate instance.
	 *
	 * @param id the id
	 * @return the coordinate instance
	 */
	public SphericalCoordinates getCoordinateInstance(String id) {
		Class<? extends SphericalCoordinates> coordType = getCoordinateClass(id);
		if(coordType == null) return null;
		try { return coordType.newInstance(); } 
		catch(Exception e) { return null; }
	}
	
	
}

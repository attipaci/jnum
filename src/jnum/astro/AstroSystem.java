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

import java.io.Serializable;

import jnum.math.SphericalCoordinates;


public class AstroSystem implements Serializable {
	

	private static final long serialVersionUID = 1426502427666674606L;

	private Class<? extends SphericalCoordinates> system;
	

	public AstroSystem(Class<? extends SphericalCoordinates> coordType) {
		this.system = coordType;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof AstroSystem)) return false;

		AstroSystem a = (AstroSystem) o;
		if(a.system == null) return false;
		return a.system.equals(system); 
	}
	

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(system != null) hash ^= system.hashCode();
		return hash;
	}
	

	public boolean isHorizontal() { return HorizontalCoordinates.class.isAssignableFrom(system); }

	
	public boolean isFocalPlane() { return FocalPlaneCoordinates.class.isAssignableFrom(system); }
	
	
    public boolean isTelescope()  { return TelescopeCoordinates.class.isAssignableFrom(system); }
    

	public boolean isEquatorial() { return EquatorialCoordinates.class.isAssignableFrom(system); }
	

	public boolean isEcliptic() { return EclipticCoordinates.class.isAssignableFrom(system); }


	public boolean isGalactic()  { return GalacticCoordinates.class.isAssignableFrom(system); }

	
	public boolean isSuperGalactic()  { return SuperGalacticCoordinates.class.isAssignableFrom(system); }


	

	public String getID() { return getID(system); }
	

	public SphericalCoordinates getCoordinateInstance() {
		if(system == null) return null;
		try { return system.getConstructor().newInstance(); } 
		catch(Exception e) { return null; }
	}
	

	public static String getID(Class<? extends SphericalCoordinates> coordType) {
		String id = SphericalCoordinates.getTwoLetterCodeFor(coordType);
		return id == null ? "--" : id;
	}
	

	public static Class<? extends SphericalCoordinates> getCoordinateClass(String id) {
		return SphericalCoordinates.getTwoLetterClass(id);
	}
	

	public SphericalCoordinates getCoordinateInstance(String id) {
		Class<? extends SphericalCoordinates> coordType = getCoordinateClass(id);
		if(coordType == null) return null;
		try { return coordType.getConstructor().newInstance(); } 
		catch(Exception e) { return null; }
	}
	
	
}

/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.astro;

import java.io.Serializable;

import jnum.math.SphericalCoordinates;
import nom.tam.fits.Header;

/**
 * Astronomical coordinate system type
 * 
 * @author Attila Kovacs
 *
 */
public class AstroSystem implements Serializable {
	

	private static final long serialVersionUID = 1426502427666674606L;

	private Class<? extends SphericalCoordinates> system;
	

	protected AstroSystem(Class<? extends SphericalCoordinates> coordType) {
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
	
	
	Class<? extends SphericalCoordinates> getType() {
	    return system;
	}
	
	public boolean isType(Class<? extends SphericalCoordinates> type) {
	    return system.isAssignableFrom(type);
	}
	

	public boolean isCompatibleWith(SphericalCoordinates coords) {
	    return isType(coords.getClass());
	}
	
	public String getName() { return system.getSimpleName(); }
	
	public boolean isHorizontal() { return HorizontalCoordinates.class.isAssignableFrom(system); }

	
	public boolean isFocalPlane() { return FocalPlaneCoordinates.class.isAssignableFrom(system); }
	
	
    public boolean isTelescope()  { return TelescopeCoordinates.class.isAssignableFrom(system); }
    

	public boolean isEquatorial() { return EquatorialCoordinates.class.isAssignableFrom(system); }
	

	public boolean isEcliptic() { return EclipticCoordinates.class.isAssignableFrom(system); }


	public boolean isGalactic()  { return GalacticCoordinates.class.isAssignableFrom(system); }

	
	public boolean isSuperGalactic()  { return SuperGalacticCoordinates.class.isAssignableFrom(system); }


	public String getID() {
	    String id = SphericalCoordinates.getTwoLetterIDFor(system);
        return id == null ? "--" : id;    
	}
	

	public SphericalCoordinates getCoordinateInstance() {
		if(system == null) return null;
		try { return system.getConstructor().newInstance(); } 
		catch(Exception e) { return null; }
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

	public static AstroSystem fromFitsHeader(Header header) {
	    return fromFitsHeader(header, "");
	}
	
	public static AstroSystem fromFitsHeader(Header header, String alt) {
	    Class<? extends SphericalCoordinates> cl = SphericalCoordinates.getFITSClass("CTYPE1" + alt);     
	    if(EquatorialCoordinates.class.isAssignableFrom(cl)) return new Equatorial(EquatorialSystem.fromHeader(header, alt));
	    if(EclipticCoordinates.class.isAssignableFrom(cl)) return new Equatorial(EquatorialSystem.fromHeader(header, alt));
	    return new AstroSystem(cl);
	}
	
	public static class Equatorial extends AstroSystem {
	    /**
         * 
         */
        private static final long serialVersionUID = 1566612346429869282L;
        private EquatorialSystem system;
	    
	    
	    public Equatorial(EquatorialSystem s) {
	        super(EquatorialCoordinates.class);
	        this.system = s;
	    }
	    
	    public EquatorialSystem getEquatorialSystem() {
	         return system;
	    }
	    
	    public void setEquatorialSystem(EquatorialSystem system) {
	        this.system = system;
	    }
	    
	    @Override
        public EquatorialCoordinates getCoordinateInstance() {
	        return new EquatorialCoordinates(system);
	    }
	}
	
	public static class Ecliptic extends AstroSystem {
        /**
         * 
         */
        private static final long serialVersionUID = 1566612346429869282L;
        private EquatorialSystem system;
        
        
        public Ecliptic(EquatorialSystem s) {
            super(EclipticCoordinates.class);
            this.system = s;
        }
        
        public EquatorialSystem getEquatorialSystem() {
             return system;
        }
        
        public void setEquatorialSystem(EquatorialSystem system) {
            this.system = system;
        }
        
        @Override
        public EclipticCoordinates getCoordinateInstance() {
            return new EclipticCoordinates(system);
        }
    }
	
	
	public static final AstroSystem horizontal = new AstroSystem(HorizontalCoordinates.class);
	
	public static final AstroSystem.Equatorial equatorialICRS = new AstroSystem.Equatorial(EquatorialSystem.ICRS);
	public static final AstroSystem.Equatorial equatorialFK5J2000 = new AstroSystem.Equatorial(EquatorialSystem.FK5.J2000);
	public static final AstroSystem.Equatorial equatorialFK4B1950 = new AstroSystem.Equatorial(EquatorialSystem.FK4.B1950);
	
	public static final AstroSystem.Ecliptic eclipticICRS = new AstroSystem.Ecliptic(EquatorialSystem.ICRS);
    public static final AstroSystem.Ecliptic eclipticFK5J2000 = new AstroSystem.Ecliptic(EquatorialSystem.FK5.J2000);
    public static final AstroSystem.Ecliptic eclipticFK4B1950 = new AstroSystem.Ecliptic(EquatorialSystem.FK4.B1950);
	
	public static final AstroSystem galactic = new AstroSystem(GalacticCoordinates.class);
	public static final AstroSystem superHalactic = new AstroSystem(SuperGalacticCoordinates.class);
	public static final AstroSystem telescope = new AstroSystem(TelescopeCoordinates.class);
	public static final AstroSystem focalPlane = new AstroSystem(FocalPlaneCoordinates.class);
	
}

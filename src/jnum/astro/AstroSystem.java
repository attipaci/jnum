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
 * Astronomical coordinate system type.
 * 
 * @author Attila Kovacs
 *
 */
public class AstroSystem implements Serializable {
	
    /** */
	private static final long serialVersionUID = 1426502427666674606L;

	/** the class of coordinates that define this system */
	private Class<? extends SphericalCoordinates> system;
	
	/** 
	 * Instantiates a new astronomical coordinate system from the class of coordinates.
	 * 
	 * @param coordType    the class of coordinates that define this system.
	 */
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
	
	/**
	 * Returns the Java class of ccoordinates that represent this system.
	 * 
	 * @return     the coordinates class of this system
	 */
	Class<? extends SphericalCoordinates> getType() {
	    return system;
	}
	
	
	/** 
	 * Checks if this system supports the specified coordinate type.
	 * 
	 * @param type     the coordinate class to check
	 * @return         <code>true</code> if the class of coordinates can be used to represent locations in this system,
	 *                 otherwise <code>false</code>
	 *                 
	 * @see #isCompatibleWith(SphericalCoordinates)
	 * @see #getName()
	 */
	public boolean isType(Class<? extends SphericalCoordinates> type) {
	    return system.isAssignableFrom(type);
	}
	

	/**
	 * Checks if the system is compatible with a set of coordinates, that is if the specified
	 * coordinates belong to this system.
	 * 
	 * @param coords   the set of coordinates
	 * @return         <code>true</code> if the coordinates can be used to represent locations in this system,
     *                 otherwise <code>false</code>
	 * 
	 * @see #isType(Class)
	 * @see #getName()
	 */
	public boolean isCompatibleWith(SphericalCoordinates coords) {
	    return isType(coords.getClass());
	}
	
	/** 
	 * Returns the name of the coordinate class supported by this system. 
	 * 
	 * @see #isType(Class)
	 * @see #isCompatibleWith(SphericalCoordinates)
	 *
	 * @return     the coordinate class name.
	 */
	public String getName() { return system.getSimpleName(); }
	
	/**
	 * Checks if this coordinate system is a local horizontal coordinate system at some Earth location.
	 * 
	 * @return     <code>true</code> if this system can be used to represent local horizontal coordinates
	 *             at some Earth location, otherwise <code>false</code>
	 *             
	 * @see HorizontalCoordinates
	 */
	public boolean isHorizontal() { return HorizontalCoordinates.class.isAssignableFrom(system); }

	/**
     * Checks if this coordinate system is a focal-plane coordinate system for some telescope instrument.
     * 
     * @return     <code>true</code> if this system can be used to represent focal-plane coordinates
     *             of a telescope instrument, otherwise <code>false</code>
     *             
     * @see FocalPlaneCoordinates
     */
	public boolean isFocalPlane() { return FocalPlaneCoordinates.class.isAssignableFrom(system); }
	
	/**
     * Checks if this coordinate system is the native coordinate system for some telescope mount.
     * 
     * @return     <code>true</code> if this system can be used to represent native telescope orientation
     *             for the telescope mount, otherwise <code>false</code>
     *             
     * @see TelescopeCoordinates
     */
    public boolean isTelescope()  { return TelescopeCoordinates.class.isAssignableFrom(system); }
    
    /**
     * Checks if this coordinate system is an equatorial coordinate system.
     * 
     * @return     <code>true</code> if this system is an equatorial coordinate system, otherwise <code>false</code>
     * 
     * @see EquatorialCoordinates
     */
	public boolean isEquatorial() { return EquatorialCoordinates.class.isAssignableFrom(system); }
	
	/**
     * Checks if this coordinate system is an ecliptic coordinate system.
     * 
     * @return     <code>true</code> if this system is an ecliptic coordinate system, otherwise <code>false</code>
     * 
     * @see EclipticCoordinates
     */
	public boolean isEcliptic() { return EclipticCoordinates.class.isAssignableFrom(system); }

	/**
     * Checks if this coordinate system is a galactic coordinate system.
     * 
     * @return     <code>true</code> if this system is a galactic coordinate system, otherwise <code>false</code>
     * 
     * @see GalacticCoordinates
     */
	public boolean isGalactic()  { return GalacticCoordinates.class.isAssignableFrom(system); }

	/**
     * Checks if this coordinate system is a supergalactic coordinate system.
     * 
     * @return     <code>true</code> if this system is a supergalactic coordinate system, otherwise <code>false</code>
     * 
     * @see SuperGalacticCoordinates
     */	
	public boolean isSuperGalactic()  { return SuperGalacticCoordinates.class.isAssignableFrom(system); }

	/**
	 * Returns the 2-letter ID for this coordinate system. Same as {@link SphericalCoordinates#getTwoLetterID()}.
	 * 
	 * @return     the 2-letter identifier for this system of coordinates.
	 * 
	 * @see SphericalCoordinates#getTwoLetterID()
	 */
	public String getID() {
	    String id = SphericalCoordinates.getTwoLetterIDFor(system);
        return id == null ? "--" : id;    
	}
	
	/**
	 * Returns a set of new coordinates in this system.
	 * 
	 * @return     new coordinates defined in this astrometric system.
	 */
	public SphericalCoordinates getCoordinateInstance() {
		if(system == null) return null;
		try { return system.getConstructor().newInstance(); } 
		catch(Exception e) { return null; }
	}
	
	/**
	 * Returns the class of coordinates for a 2-letter ID.
	 * 
	 * @param id       the 2-letter ID that defined the tpye of coordinates/system.
	 * @return         the coordinate class that matches the ID, or else <code>null</code> if there
	 *                 is no known system of coordinates for the specified ID.
	 */
	public static Class<? extends SphericalCoordinates> getCoordinateClass(String id) {
		return SphericalCoordinates.getTwoLetterClass(id);
	}
	
	/**
	 * Returns a new instance of coordinates for the specified 2-letter ID, initialized to the origin.
	 * 
	 * @param id       the 2-letter ID that defined the tpye of coordinates/system.
	 * @return         a new set of coordinates that match the ID, or else <code>null</code> if there
     *                 is no known system of coordinates for the specified ID.
	 */
	public SphericalCoordinates getCoordinateInstance(String id) {
		Class<? extends SphericalCoordinates> coordType = getCoordinateClass(id);
		if(coordType == null) return null;
		try { return coordType.getConstructor().newInstance(); } 
		catch(Exception e) { return null; }
	}

	/**
	 * Instantiates a new astrometric coordinate system based on the description in a FITS header.
	 * 
	 * @param header       the FITS header that describes the astrometric coordinate system.
	 * @return             a new astronomical coordinate system that is described in the FITS header.
	 * 
	 * @see #fromFitsHeader(Header, String)
	 */
	public static AstroSystem fromFitsHeader(Header header) {
	    return fromFitsHeader(header, "");
	}
	
	/**
     * <p>
     * Instantiates a new astrometric coordinate system based on a variant description in a FITS header.
     * FITS allows defining more than one coordinate system in the headers simultaneously. When
     * multiple coordinate descriptions are present in the FITS header, the alternatives are marked
     * with a letter code starting with A, and up to Z. The default coordinate system has no 
     * alternative marker. 
     * </p>
     * <p>
     * If the header does not contain a valid coordinate system description for the specified
     * variant, then the ICRS equatorial system is assumed an returned.
     * </p>
     * 
     * @param header       the FITS header that describes the astrometric coordinate system.
     * @param alt          the variant letter code of the alternative coordinate description in the
     *                     header. You can specify an empty string "" to use the default 
     *                     coordinate system, or "A" through "Z" for one of the variants.
     * @return             a new astronomical coordinate system that is described in the FITS header,
     *                     or the default ICRS equatorial system in case the header description
     *                     is insufficient. 
     * 
     * @see #fromFitsHeader(Header, String)
     */
	public static AstroSystem fromFitsHeader(Header header, String alt) {
	    Class<? extends SphericalCoordinates> cl = SphericalCoordinates.getFITSClass("CTYPE1" + alt);     
	    if(EquatorialCoordinates.class.isAssignableFrom(cl)) return new Equatorial(EquatorialSystem.fromHeader(header, alt));
	    if(EclipticCoordinates.class.isAssignableFrom(cl)) return new Equatorial(EquatorialSystem.fromHeader(header, alt));
	    return new AstroSystem(cl);
	}
	
	/** 
	 * Sub-class for representing a particular equatorial reference system from a family of these.
	 * 
	 * @author Attila Kovacs
	 *
	 */
	public static class Equatorial extends AstroSystem {
	    /**
         * 
         */
        private static final long serialVersionUID = 1566612346429869282L;
        
        /** The underlying equatorial system */
        private EquatorialSystem system;
	    
	    /** 
	     * Instantiates a new astronomical reference system that uses the specified equatorial reference
	     * system at its core.
	     * 
	     * @param s    the equatorial coordinate reference system for this astronomical reference system.
	     */
	    public Equatorial(EquatorialSystem s) {
	        super(EquatorialCoordinates.class);
	        this.system = s;
	    }
	    
	    /**
	     * Returns the underlying equaotrial reference system used for this astronomical coordinate
	     * reference system.
	     * 
	     * @return     the equatorial coordinate reference system of this astronomical reference system.
	     */
	    public EquatorialSystem getEquatorialSystem() {
	         return system;
	    }
	    

	    @Override
        public EquatorialCoordinates getCoordinateInstance() {
	        return new EquatorialCoordinates(system);
	    }
	}
	
	/** 
     * Sub-class for representing a particular ecliptic reference system from a family of these.
     * 
     * @author Attila Kovacs
     *
     */
	public static class Ecliptic extends AstroSystem {
        /**
         * 
         */
        private static final long serialVersionUID = -3139207318356179567L;
        
        /** The underlying equatorial system */
        private EquatorialSystem system;
        
        /** 
         * Instantiates a new ecliptic reference system that uses the specified equatorial reference
         * system at its core.
         * 
         * @param s    the equatorial coordinate reference system for this ecliptic reference system.
         */
        public Ecliptic(EquatorialSystem s) {
            super(EclipticCoordinates.class);
            this.system = s;
        }
        
        /**
         * Returns the underlying equaotrial reference system used for this astronomical coordinate
         * reference system.
         * 
         * @return     the equatorial coordinate reference system of this astronomical reference system.
         */
        public EquatorialSystem getEquatorialSystem() {
             return system;
        }
        
 
        @Override
        public EclipticCoordinates getCoordinateInstance() {
            return new EclipticCoordinates(system);
        }
    }
	
	/** The local horizontal reference system (for any location on Earth) */
	public static final AstroSystem horizontal = new AstroSystem(HorizontalCoordinates.class);
	
	/** The ICRS equatorial reference system */
	public static final AstroSystem.Equatorial equatorialICRS = new AstroSystem.Equatorial(EquatorialSystem.ICRS);
	
	/** The FK5/J2000 equatorial reference system */
	public static final AstroSystem.Equatorial equatorialFK5J2000 = new AstroSystem.Equatorial(EquatorialSystem.FK5.J2000);
	
	/** The FK4/B1950 equatorial reference system */
	public static final AstroSystem.Equatorial equatorialFK4B1950 = new AstroSystem.Equatorial(EquatorialSystem.FK4.B1950);
	
	/** The ICRS ecliptic reference system */
	public static final AstroSystem.Ecliptic eclipticICRS = new AstroSystem.Ecliptic(EquatorialSystem.ICRS);
	
	/** The FK5/J2000 ecliptic reference system */
    public static final AstroSystem.Ecliptic eclipticFK5J2000 = new AstroSystem.Ecliptic(EquatorialSystem.FK5.J2000);
    
    /** The FK4/B1950 ecliptic reference system */
    public static final AstroSystem.Ecliptic eclipticFK4B1950 = new AstroSystem.Ecliptic(EquatorialSystem.FK4.B1950);
	
    /** The Galactic coordinate reference system */
	public static final AstroSystem galactic = new AstroSystem(GalacticCoordinates.class);
	
	/** The Supergalactic coordinate reference system */
	public static final AstroSystem superGalactic = new AstroSystem(SuperGalacticCoordinates.class);
	
	/** A coordinate reference system tied to a telesope native mount (axes) */
	public static final AstroSystem telescope = new AstroSystem(TelescopeCoordinates.class);
	
	/** A coordinate reference system on the focal plane of a telescope instrument. */
	public static final AstroSystem focalPlane = new AstroSystem(FocalPlaneCoordinates.class);
	
}

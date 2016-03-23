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


import java.text.NumberFormat;

import jnum.Constant;
import jnum.Unit;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class EclipticCoordinates.
 */
public class EclipticCoordinates extends CelestialCoordinates implements Precessing {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7687178545213533912L;

	/** The epoch. */
	public CoordinateEpoch epoch;
	
	/** The latitude offset axis. */
	static CoordinateAxis longitudeAxis, latitudeAxis, longitudeOffsetAxis, latitudeOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
		
	static {
		defaultCoordinateSystem = new CoordinateSystem("Ecliptic Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Ecliptic Offsets");
		
		longitudeAxis = new CoordinateAxis("Ecliptic Longitude", "ELON", GreekLetter.lambda + "");
		longitudeAxis.setReverse(true);
		latitudeAxis = new CoordinateAxis("Ecliptic Latitude", "ELAT", GreekLetter.beta + "");
		longitudeOffsetAxis = new CoordinateAxis("Ecliptic Longitude Offset", "dELON", GreekLetter.Delta + " " + GreekLetter.lambda );
		longitudeOffsetAxis.setReverse(true);
		latitudeOffsetAxis = new CoordinateAxis("Ecliptic Latitude Offset", "dELAT", GreekLetter.Delta + " " + GreekLetter.beta);
		
		defaultCoordinateSystem.add(longitudeAxis);
		defaultCoordinateSystem.add(latitudeAxis);
		defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
		defaultLocalCoordinateSystem.add(latitudeOffsetAxis);		
		
		for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
	}

	
    /**
     * Instantiates a new ecliptic coordinates.
     */
    public EclipticCoordinates() { epoch = CoordinateEpoch.J2000; }

    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param text the text
     */
    public EclipticCoordinates(String text) { super(text); }
    
	/**
	 * Instantiates a new ecliptic coordinates.
	 *
	 * @param lon the lon
	 * @param lat the lat
	 */
	public EclipticCoordinates(double lon, double lat) { super(lon, lat); epoch = CoordinateEpoch.J2000; }

	/**
	 * Instantiates a new ecliptic coordinates.
	 *
	 * @param lon the lon
	 * @param lat the lat
	 * @param aEpoch the a epoch
	 */
	public EclipticCoordinates(double lon, double lat, double aEpoch) { super(lon, lat); epoch = aEpoch < 1984.0 ? new BesselianEpoch(aEpoch) : new JulianEpoch(aEpoch); }

	/**
	 * Instantiates a new ecliptic coordinates.
	 *
	 * @param lon the lon
	 * @param lat the lat
	 * @param epochSpec the epoch spec
	 */
	public EclipticCoordinates(double lon, double lat, String epochSpec) { super(lon, lat); epoch = CoordinateEpoch.forString(epochSpec); }
     
	/**
	 * Instantiates a new ecliptic coordinates.
	 *
	 * @param from the from
	 */
	public EclipticCoordinates(CelestialCoordinates from) { super(from); }
	
	/* (non-Javadoc)
	 * @see jnum.math.Coordinate2D#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(epoch != null) hash ^= epoch.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see jnum.math.Coordinate2D#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof EclipticCoordinates)) return false;
		if(!super.equals(o)) return false;
		EclipticCoordinates e = (EclipticCoordinates) o;
		if(!Util.equals(epoch, e.epoch)) return false;
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
	 */
	@Override
	public String getFITSLongitudeStem() { return "ELON"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "ELAT"; }
	
	/**
	 * Gets the coordinate system.
	 *
	 * @return the coordinate system
	 */
	@Override
	public CoordinateSystem getCoordinateSystem() { return defaultCoordinateSystem; }

	
	/**
	 * Gets the local coordinate system.
	 *
	 * @return the local coordinate system
	 */
	@Override
	public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }
	
	
	/**
	 * Copy.
	 *
	 * @return the coordinate2 d
	 */
	@Override
	public Coordinate2D copy() {
		EclipticCoordinates copy = (EclipticCoordinates) super.copy();
		copy.epoch = epoch.copy();
		return copy;
	}
	
  
    /* (non-Javadoc)
     * @see kovacs.util.SphericalCoordinates#copy(kovacs.util.Coordinate2D)
     */
    @Override
	public void copy(Coordinate2D coords) {
		super.copy(coords);
		if(coords instanceof EclipticCoordinates) {
			EclipticCoordinates equatorial = (EclipticCoordinates) coords;
			epoch = (CoordinateEpoch) equatorial.epoch.clone();	
		}
		else epoch = null;
	}
    
    /* (non-Javadoc)
     * @see kovacs.util.astro.CelestialCoordinates#toEquatorial(kovacs.util.astro.EquatorialCoordinates)
     */
    @Override
    public void toEquatorial(EquatorialCoordinates equatorial) {
    	super.toEquatorial(equatorial);
    }
    
    /* (non-Javadoc)
     * @see kovacs.util.astro.CelestialCoordinates#fromEquatorial(kovacs.util.astro.EquatorialCoordinates)
     */
    @Override
    public void fromEquatorial(EquatorialCoordinates equatorial) {
    	super.fromEquatorial(equatorial);
    	epoch = equatorial.epoch;
    }
    
    
    /* (non-Javadoc)
     * @see kovacs.util.SphericalCoordinates#toString()
     */
    @Override
	public String toString() {
		return super.toString() + " (" + (epoch == null ? "unknown" : epoch.toString()) + ")";	
	}
    
    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#toString(int)
     */
    @Override
	public String toString(int decimals) {
		return super.toString(decimals) + " (" + (epoch == null ? "unknown" : epoch.toString()) + ")";
    }
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return super.toString(nf) + " (" + (epoch == null ? "unknown" : epoch.toString()) + ")";	
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {
		super.edit(cursor, alt);
		cursor.add(new HeaderCard("RADESYS" + alt, epoch instanceof BesselianEpoch ? "FK4" : "FK5", "Reference convention."));
		epoch.edit(cursor, alt);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parse(Header header, String alt) {
		super.parse(header, alt);
		
		String system = header.getStringValue("RADESYS");
		if(system == null) system = header.getDoubleValue("EQUINOX" + alt) < 1984.0 ? "FK4" : "FK5";
		
		if(system.equalsIgnoreCase("FK4")) epoch = new BesselianEpoch();
		else if(system.equalsIgnoreCase("FK4-NO-E")) epoch = new BesselianEpoch();
		else epoch = new JulianEpoch();
		
		epoch.parse(header, alt);
	}
	
    
    /** The Constant inclination. */
    public final static double inclination = 23.0 * Unit.deg + 26.0 * Unit.arcmin + 30.0 * Unit.arcsec; // to equatorial    
    
    /** The Constant equatorialPole. */
    public final static EquatorialCoordinates equatorialPole = CelestialCoordinates.getPole(inclination, 0.0);
    
	/* (non-Javadoc)
	 * @see kovacs.util.astro.CelestialCoordinates#getEquatorialPole()
	 */
	@Override
	public EquatorialCoordinates getEquatorialPole() { return equatorialPole; }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CelestialCoordinates#getZeroLongitude()
	 */
	@Override
	public double getZeroLongitude() { return Constant.rightAngle; }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.Precessing#getEpoch()
	 */
	@Override
	public CoordinateEpoch getEpoch() { return epoch; }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.Precessing#setEpoch(kovacs.util.astro.CoordinateEpoch)
	 */
	@Override
	public void setEpoch(CoordinateEpoch epoch) { this.epoch = epoch; }
	
	/* (non-Javadoc)
	 * @see kovacs.util.astro.Precessing#precess(kovacs.util.astro.CoordinateEpoch)
	 */
	@Override
	public void precess(CoordinateEpoch toEpoch) {
		if(epoch.equals(toEpoch)) return;
		
		EquatorialCoordinates equatorial = toEquatorial();
		equatorial.precess(toEpoch);
		fromEquatorial(equatorial);
	}
	
    
}

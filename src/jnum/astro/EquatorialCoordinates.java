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

package jnum.astro;

import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import jnum.text.HourAngleFormat;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


// TODO: Auto-generated Javadoc
// x, y kept in longitude,latitude form
// use RA(), DEC(), setRA() and setDEC(functions) to for RA, DEC coordinates...

/**
 * The Class EquatorialCoordinates.
 */
public class EquatorialCoordinates extends CelestialCoordinates implements Precessing {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3445122576647034180L;

	/** The epoch. */
	public CoordinateEpoch epoch;
	
	/** The declination offset axis. */
	static CoordinateAxis rightAscentionAxis, declinationAxis, rightAscentionOffsetAxis, declinationOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
	
	private static HourAngleFormat hf = new HourAngleFormat(2);
	
	static {
		defaultCoordinateSystem = new CoordinateSystem("Equatorial Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Equatorial Offsets");
		
		rightAscentionAxis = new CoordinateAxis("Right Ascention");
		rightAscentionAxis.setReverse(true);
		rightAscentionAxis.setFormat(hf);

		declinationAxis = new CoordinateAxis("Declination");
		declinationAxis.setFormat(af);

		rightAscentionOffsetAxis = new CoordinateAxis(GreekLetter.Delta + "RA");
		rightAscentionOffsetAxis.setReverse(true);
	
		declinationOffsetAxis = new CoordinateAxis(GreekLetter.Delta + "DEC");
		
		defaultCoordinateSystem.add(rightAscentionAxis);
		defaultCoordinateSystem.add(declinationAxis);
		
		defaultLocalCoordinateSystem.add(rightAscentionOffsetAxis);
		defaultLocalCoordinateSystem.add(declinationOffsetAxis);		
	}
	
	
    /**
     * Instantiates a new equatorial coordinates.
     */
    public EquatorialCoordinates() { epoch = CoordinateEpoch.J2000; }

	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param text the text
	 */
	public EquatorialCoordinates(String text) { super(text); }

	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param ra the ra in radians
	 * @param dec the dec in radians
	 */
	public EquatorialCoordinates(double ra, double dec) { super(ra, dec); epoch = CoordinateEpoch.J2000; }

	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param ra the ra
	 * @param dec the dec
	 * @param aEpoch the a epoch
	 */
	public EquatorialCoordinates(double ra, double dec, double aEpoch) { super(ra, dec); epoch = aEpoch < 1984.0 ? new BesselianEpoch(aEpoch) : new JulianEpoch(aEpoch); }

	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param ra the ra
	 * @param dec the dec
	 * @param epochSpec the epoch spec
	 */
	public EquatorialCoordinates(double ra, double dec, String epochSpec) { super(ra, dec); epoch = CoordinateEpoch.forString(epochSpec); }
	
	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param ra the ra
	 * @param dec the dec
	 * @param epoch the epoch
	 */
	public EquatorialCoordinates(double ra, double dec, CoordinateEpoch epoch) { super(ra, dec); this.epoch = epoch; }
		
	
	/**
	 * Instantiates a new equatorial coordinates.
	 *
	 * @param from the from
	 */
	public EquatorialCoordinates(CelestialCoordinates from) { super(from); }
	
	@Override
	public String getFITSLongitudeStem() { return "RA--"; }
	
	@Override
	public String getFITSLatitudeStem() { return "DEC-"; }
	
	
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
	 * @return the equatorial coordinates
	 */
	@Override
	public Coordinate2D copy() {
		EquatorialCoordinates copy = (EquatorialCoordinates) super.copy();
		if(epoch != null) copy.epoch = epoch.copy();
		return copy;
	}
	

	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o)) return false;
		EquatorialCoordinates coords = (EquatorialCoordinates) o;
		if(!coords.epoch.equals(epoch)) return false;
		return true;		
	}
	
	/**
	 * Copy.
	 *
	 * @param coords the coords
	 */
	public void copy(SphericalCoordinates coords) {
		super.copy(coords);
		if(!(coords instanceof EquatorialCoordinates)) return;
		EquatorialCoordinates equatorial = (EquatorialCoordinates) coords;
		epoch = (CoordinateEpoch) equatorial.epoch.clone();
	}
	
	/**
	 * Ra.
	 *
	 * @return the double
	 */
	public final double RA() { return longitude(); }

	/**
	 * Right ascension.
	 *
	 * @return the double
	 */
	public final double rightAscension() { return longitude(); }

	/**
	 * Dec.
	 *
	 * @return the double
	 */
	public final double DEC() { return latitude(); }

	/**
	 * Declination.
	 *
	 * @return the double
	 */
	public final double declination() { return latitude(); }

	/**
	 * Sets the ra.
	 *
	 * @param RA the new ra
	 */
	public final void setRA(double RA) { setLongitude(RA); }

	/**
	 * Sets the dec.
	 *
	 * @param DEC the new dec
	 */
	public final void setDEC(double DEC) { setLatitude(DEC); }

	/**
	 * Gets the parallactic angle.
	 *
	 * @param site the site
	 * @param LST the lst
	 * @return the parallactic angle
	 */
	public double getParallacticAngle(GeodeticCoordinates site, double LST) {
		final double H = LST * Unit.timeAngle - RA();
		return Math.atan2(site.cosLat() * Math.sin(H), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(H));
	}
	
	@Override
	public final double getEquatorialPositionAngle() {
		return 0.0;
	}
	
	
	@Override
	public void toEquatorial(EquatorialCoordinates equatorial) {
		final CoordinateEpoch toEpoch = equatorial.epoch;
		equatorial.copy(this);	
		if(!epoch.equals(toEpoch)) equatorial.precess(toEpoch);			
	}
	
	@Override
	public void fromEquatorial(EquatorialCoordinates equatorial) {
		final CoordinateEpoch toEpoch = epoch;
		copy(equatorial);
		if(!epoch.equals(toEpoch)) precess(toEpoch);			
	}
	
	/**
	 * To horizontal.
	 *
	 * @param site the site
	 * @param LST the lst
	 * @return the horizontal coordinates
	 */
	public HorizontalCoordinates toHorizontal(GeodeticCoordinates site, double LST) {
		HorizontalCoordinates horizontal = new HorizontalCoordinates();
		toHorizontal(this, horizontal, site, LST);
		return horizontal;
	}
	
	/**
	 * To horizontal.
	 *
	 * @param toCoords the to coords
	 * @param site the site
	 * @param LST the lst
	 */
	public void toHorizontal(HorizontalCoordinates toCoords, GeodeticCoordinates site, double LST) { toHorizontal(this, toCoords, site, LST); }
	
	
	/**
	 * To horizontal offset.
	 *
	 * @param offset the offset
	 * @param site the site
	 * @param LST the lst
	 */
	public void toHorizontalOffset(Vector2D offset, GeodeticCoordinates site, double LST) {
		toHorizontalOffset(offset, getParallacticAngle(site, LST));
	}

	/**
	 * To horizontal offset.
	 *
	 * @param offset the offset
	 * @param PA the pa
	 */
	public static void toHorizontalOffset(Vector2D offset, double PA) {
		offset.scaleX(-1.0);
		offset.rotate(-PA);
	}
	
	/**
	 * To horizontal.
	 *
	 * @param equatorial the equatorial
	 * @param horizontal the horizontal
	 * @param site the site
	 * @param LST the lst
	 */
	
	/*
	public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {
		double H = LST * Unit.timeAngle - equatorial.RA();
		double cosH = Math.cos(H);
		horizontal.setNativeLatitude(asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
		double asinA = -Math.sin(H) * equatorial.cosLat();
		double acosA = site.cosLat() * equatorial.sinLat() - site.sinLat() * equatorial.cosLat() * cosH;
		horizontal.setLongitude(Math.atan2(asinA, acosA));
	}
	*/
	
	public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {	
		double H = LST * Unit.timeAngle - equatorial.RA();	
		double cosH = Math.cos(H);
		horizontal.setLatitude(SafeMath.asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
		double asinA = -Math.sin(H) * equatorial.cosLat() * site.cosLat();
		double acosA = equatorial.sinLat() - site.sinLat() * horizontal.sinLat();
		horizontal.setLongitude(Math.atan2(asinA, acosA));
	}
	

	/* (non-Javadoc)
	 * @see kovacs.util.astro.Precessing#precess(kovacs.util.astro.CoordinateEpoch)
	 */
	@Override
	public void precess(CoordinateEpoch newEpoch) {
		if(epoch.equals(newEpoch)) return;
		Precession precession = new Precession(epoch, newEpoch);
		precession.precess(this);
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#toString()
	 */
	@Override
	public String toString() {
		hf.setDecimals(getDefaultDecimals() + 1);
		return super.toString() + " (" + (epoch == null ? "unknown" : epoch.toString()) + ")";	
	}
	
	@Override
	public String toString(int decimals) {
		return Util.hf[decimals+1].format(longitude()) + " " + Util.af[decimals].format(latitude()) +  " (" + (epoch == null ? "unknown" : epoch.toString()) + ")";	
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return super.toString(nf) + " " + "(" + (epoch == null ? "unknown" : epoch.toString()) + ")";	
	}


	/* (non-Javadoc)
	 * @see kovacs.util.SphericalCoordinates#parse(java.lang.String)
	 */
	@Override
	public void parse(String coords) throws NumberFormatException, IllegalArgumentException {
		StringTokenizer tokens = new StringTokenizer(coords, ",() \t\r\n");
		super.parse(tokens.nextToken() + " " + tokens.nextToken());
		
		try { epoch = tokens.hasMoreTokens() ? CoordinateEpoch.forString(tokens.nextToken()) : null; }
		catch(NumberFormatException e) { epoch = null; }
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

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CelestialCoordinates#getEquatorialPole()
	 */
	@Override
	public EquatorialCoordinates getEquatorialPole() { return equatorialPole; }

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CelestialCoordinates#getZeroLongitude()
	 */
	@Override
	public double getZeroLongitude() { return 0.0; }
	
	/** The equatorial pole. */
	private static EquatorialCoordinates equatorialPole = new EquatorialCoordinates(0.0, Constant.rightAngle);

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
	
	/** The Constant NORTH. */
	public final static int NORTH = 1;
	
	/** The Constant SOUTH. */
	public final static int SOUTH = -1;
	
	/** The Constant EAST. */
	public final static int EAST = -1;
	
	/** The Constant WEST. */
	public final static int WEST = 1;

}

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
// Copyright (c) 2007 Attila Kovacs 

package jnum.astro;

import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.SafeMath;
import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;

// TODO: Auto-generated Javadoc
/**
 * The Class HorizontalCoordinates.
 */
public class HorizontalCoordinates extends SphericalCoordinates {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3759766679620485628L;

	/** The elevation offset axis. */
	static CoordinateAxis azimuthAxis, elevationAxis, azimuthOffsetAxis, elevationOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
	
	static {
		defaultCoordinateSystem = new CoordinateSystem("Horizontal Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Horizontal Offsets");

		azimuthAxis = new CoordinateAxis("Azimuth", "AZ", "Az");
		elevationAxis = new CoordinateAxis("Elevation", "EL", "El");
		azimuthOffsetAxis = new CoordinateAxis("Azimuth Offset", "dAZ", GreekLetter.Delta + " AZ");
		elevationOffsetAxis = new CoordinateAxis("Elevation Offset", "dEL", GreekLetter.Delta + " EL");
		
		defaultCoordinateSystem.add(azimuthAxis);
		defaultCoordinateSystem.add(elevationAxis);
		defaultLocalCoordinateSystem.add(azimuthOffsetAxis);
		defaultLocalCoordinateSystem.add(elevationOffsetAxis);
		
		for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
	}


	/**
	 * Instantiates a new horizontal coordinates.
	 */
	public HorizontalCoordinates() {}

	/**
	 * Instantiates a new horizontal coordinates.
	 *
	 * @param text the text
	 */
	public HorizontalCoordinates(String text) { super(text); } 

	/**
	 * Instantiates a new horizontal coordinates.
	 *
	 * @param az the az
	 * @param el the el
	 */
	public HorizontalCoordinates(double az, double el) { super(az, el); }

	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
	 */
	@Override
	public String getFITSLongitudeStem() { return "ALON"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "ALAT"; }
	
	
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
	 * Az.
	 *
	 * @return the double
	 */
	public final double AZ() { return nativeLongitude(); }

	/**
	 * Azimuth.
	 *
	 * @return the double
	 */
	public final double azimuth() { return nativeLongitude(); }

	/**
	 * El.
	 *
	 * @return the double
	 */
	public final double EL() { return nativeLatitude(); }

	/**
	 * Elevation.
	 *
	 * @return the double
	 */
	public final double elevation() { return nativeLatitude(); }

	/**
	 * Za.
	 *
	 * @return the double
	 */
	public final double ZA() { return 90.0 * Unit.deg - nativeLatitude(); }

	/**
	 * Zenith angle.
	 *
	 * @return the double
	 */
	public final double zenithAngle() { return ZA(); }

	/**
	 * Sets the az.
	 *
	 * @param AZ the new az
	 */
	public final void setAZ(double AZ) { setNativeLongitude(AZ); }

	/**
	 * Sets the el.
	 *
	 * @param EL the new el
	 */
	public final void setEL(double EL) { setNativeLatitude(EL); }

	/**
	 * Sets the za.
	 *
	 * @param ZA the new za
	 */
	public final void setZA(double ZA) { setNativeLatitude(90.0 * Unit.deg - ZA); }

	/**
	 * To equatorial.
	 *
	 * @param site the site
	 * @param LST the lst
	 * @return the equatorial coordinates
	 */
	public EquatorialCoordinates toEquatorial(GeodeticCoordinates site, double LST) {
		EquatorialCoordinates equatorial = new EquatorialCoordinates();
		toEquatorial(this, equatorial, site, LST);
		return equatorial;
	}
	
	/**
	 * To equatorial.
	 *
	 * @param toCoords the to coords
	 * @param site the site
	 * @param LST the lst
	 */
	public void toEquatorial(EquatorialCoordinates toCoords, GeodeticCoordinates site, double LST) { toEquatorial(this, toCoords, site, LST); }
	
	/**
	 * Gets the parallactic angle.
	 *
	 * @param site the site
	 * @return the parallactic angle
	 */
	public double getParallacticAngle(GeodeticCoordinates site) {
		return Math.atan2(-site.cosLat() * Math.sin(x()), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(x()));
	}
	
	/**
	 * To equatorial.
	 *
	 * @param horizontal the horizontal
	 * @param equatorial the equatorial
	 * @param site the site
	 * @param LST the lst
	 */
	public static void toEquatorial(HorizontalCoordinates horizontal, EquatorialCoordinates equatorial, GeodeticCoordinates site, double LST) {
		double cosAZ = Math.cos(horizontal.x());
		equatorial.setNativeLatitude(
		        SafeMath.asin(horizontal.sinLat() * site.sinLat() + horizontal.cosLat() * site.cosLat() * cosAZ));
		final double asinH = -Math.sin(horizontal.x()) * horizontal.cosLat();
		final double acosH = site.cosLat() * horizontal.sinLat() - site.sinLat() * horizontal.cosLat() * cosAZ;
		//final double acosH = (horizontal.sinLat() - equatorial.sinLat() * site.sinLat()) / site.cosLat();
		
		equatorial.setLongitude(LST * Unit.timeAngle - Math.atan2(asinH, acosH));
	}

	
	/**
	 * To equatorial.
	 *
	 * @param offset the offset
	 * @param site the site
	 */
	public void toEquatorial(Vector2D offset, GeodeticCoordinates site) {
		toEquatorialOffset(offset, getParallacticAngle(site));
	}

	/**
	 * To equatorial offset.
	 *
	 * @param offset the offset
	 * @param PA the pa
	 */
	public static void toEquatorialOffset(Vector2D offset, double PA) {
		offset.rotate(PA);
		offset.scaleX(-1.0);
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {	
		super.edit(cursor, alt);	
		cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
	}
}

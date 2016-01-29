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

package jnum.math;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.StringTokenizer;

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.projection.SphericalProjection;
import jnum.text.AngleFormat;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
// TODO add BinaryTableIO interface (with projections...)

/**
 * The Class SphericalCoordinates.
 */
public class SphericalCoordinates extends Coordinate2D implements Metric<SphericalCoordinates>, Inversion {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8343774069424653101L;

	/** The sin lat. */
	private double cosLat, sinLat;
		
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
	
	protected static AngleFormat af = new AngleFormat(2);
	
	static {
		
		defaultCoordinateSystem = new CoordinateSystem("Spherical Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Spherical Offsets");
		
		CoordinateAxis longitudeAxis = new CoordinateAxis("Latitude");
		longitudeAxis.setFormat(af);
	
		CoordinateAxis latitudeAxis = new CoordinateAxis("Longitude");
		latitudeAxis.setFormat(af);
		
		CoordinateAxis longitudeOffsetAxis = new CoordinateAxis("dLon");
		CoordinateAxis latitudeOffsetAxis = new CoordinateAxis("dLat");
		
		defaultCoordinateSystem.add(longitudeAxis);
		defaultCoordinateSystem.add(latitudeAxis);
		
		defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
		defaultLocalCoordinateSystem.add(latitudeOffsetAxis);			
	}
	

	public static void setDefaultDecimals(int decimals) { af.setDecimals(decimals); }
	
	public static int getDefaultDecimals() { return af.getDecimals(); }
	
	public String getFITSLongitudeStem() { return "LON-"; }
	
	public String getFITSLatitudeStem() { return "LAT-"; }
	
	/**
	 * Instantiates a new spherical coordinates.
	 */
	public SphericalCoordinates() {
		cosLat = 1.0;
		sinLat = 0.0;		
	}

	/**
	 * Instantiates a new spherical coordinates.
	 *
	 * @param longitude the longitude
	 * @param latitude the latitude
	 */
	public SphericalCoordinates(final double longitude, final double latitude) { set(longitude, latitude); }
	
	/**
	 * Instantiates a new spherical coordinates.
	 *
	 * @param text the text
	 */
	public SphericalCoordinates(String text) { parse(text); }
	
	/**
	 * Sin lat.
	 *
	 * @return the double
	 */
	public final double sinLat() { return sinLat; }
	
	/**
	 * Cos lat.
	 *
	 * @return the double
	 */
	public final double cosLat() { return cosLat; }
	
	/**
	 * Gets the coordinate system.
	 *
	 * @return the coordinate system
	 */
	public CoordinateSystem getCoordinateSystem() { return defaultCoordinateSystem; }

	
	/**
	 * Gets the local coordinate system.
	 *
	 * @return the local coordinate system
	 */
	public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }
	

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if(o.getClass().equals(getClass())) return false;
		SphericalCoordinates coords = (SphericalCoordinates) o;
		if(!equalAngles(coords.x(), x())) return false;
		if(!equalAngles(coords.y(), y())) return false;
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#copy(kovacs.util.Coordinate2D)
	 */
	@Override
	public void copy(Coordinate2D coords) {
		setNativeLongitude(coords.x());
		setNativeLatitude(coords.y());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Coordinate2D#setX(double)
	 */
	@Override
	public final void setX(final double value) { super.setX(value); }
	
	/* (non-Javadoc)
	 * @see kovacs.math.Coordinate2D#setY(double)
	 */
	@Override
	public final void setY(final double value) { 
		super.setY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(value);
		cosLat = Math.cos(value);
	}
	
	
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#addY(double)
	 */
	@Override
	public final void addY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}

	
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#addY(double)
	 */
	@Override
	public final void subtractY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#zero()
	 */
	@Override
	public void zero() { super.zero(); cosLat = 1.0; sinLat = 0.0; }

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#NaN()
	 */
	@Override
	public void NaN() { super.NaN(); cosLat = Double.NaN; sinLat = Double.NaN; }

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#set(double, double)
	 */
	@Override
	public void set(final double lon, final double lat) { setLongitude(lon); setLatitude(lat); }
		
	/**
	 * Sets the native.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setNative(final double x, final double y) { super.set(x,  y); }
	
	/**
	 * Native longitude.
	 *
	 * @return the double
	 */
	public final double nativeLongitude() { return x(); }
	
	/**
	 * Native latitude.
	 *
	 * @return the double
	 */
	public final double nativeLatitude() { return y(); }
	
	/**
	 * Checks if is reverse longitude.
	 *
	 * @return true, if is reverse longitude
	 */
	public final boolean isReverseLongitude() { return getCoordinateSystem().get(0).isReverse(); }
	
	/**
	 * Checks if is reverse latitude.
	 *
	 * @return true, if is reverse latitude
	 */
	public final boolean isReverseLatitude() { return getCoordinateSystem().get(1).isReverse(); }
	
	// Like long on lat except returns the actual directly formattable
	// coordinates for this system...
	/**
	 * Longitude.
	 *
	 * @return the double
	 */
	public final double longitude() { return isReverseLongitude() ? getCoordinateSystem().get(0).reverseFrom-nativeLongitude() : nativeLongitude(); }
	
	/**
	 * Latitude.
	 *
	 * @return the double
	 */
	public final double latitude() { return isReverseLatitude() ? getCoordinateSystem().get(1).reverseFrom-nativeLatitude() : nativeLatitude(); }
	
	/**
	 * Sets the native longitude.
	 *
	 * @param value the new native longitude
	 */
	public final void setNativeLongitude(final double value) { setX(value); }
		
	/**
	 * Sets the native latitude.
	 *
	 * @param value the new native latitude
	 */
	public final void setNativeLatitude(final double value) { setY(value); }

	/**
	 * Sets the longitude.
	 *
	 * @param value the new longitude
	 */
	public final void setLongitude(final double value) {
		setNativeLongitude(isReverseLongitude() ? getCoordinateSystem().get(0).reverseFrom-value : value);
	}
	
	/**
	 * Sets the latitude.
	 *
	 * @param value the new latitude
	 */
	public final void setLatitude(final double value) {
		setNativeLatitude(isReverseLatitude() ? getCoordinateSystem().get(1).reverseFrom-value : value);
	}
	
	/**
	 * Project.
	 *
	 * @param projection the projection
	 * @param toNativeOffset the to native offset
	 */
	public void project(final SphericalProjection projection, final Coordinate2D toNativeOffset) {
		projection.project(this, toNativeOffset);
	}
	
	/**
	 * Sets the projected.
	 *
	 * @param projection the projection
	 * @param fromNativeOffset the from native offset
	 */
	public void setProjected(final SphericalProjection projection, final Coordinate2D fromNativeOffset) {
		projection.deproject(fromNativeOffset, this);
	}
		
	/**
	 * Gets the projected.
	 *
	 * @param projection the projection
	 * @return the projected
	 */
	public final Coordinate2D getProjected(final SphericalProjection projection) { return projection.getProjected(this); }
	
	
	/**
	 * Adds the native offset.
	 *
	 * @param offset the offset
	 */
	public void addNativeOffset(final Vector2D offset) {
		addX(offset.x() / cosLat);
		addY(offset.y());
	}
	
	/**
	 * Adds the offset.
	 *
	 * @param offset the offset
	 */
	public void addOffset(final Vector2D offset) {
		if(isReverseLongitude()) subtractX(offset.x() / cosLat);
		else addX(offset.x() / cosLat);
		if(isReverseLatitude()) subtractY(offset.y());
		else addY(offset.y());
	}
	
	/**
	 * Subtract native offset.
	 *
	 * @param offset the offset
	 */
	public void subtractNativeOffset(final Vector2D offset) {
		subtractX(offset.x() / cosLat);
		subtractY(offset.y());
	}
	
	/**
	 * Subtract offset.
	 *
	 * @param offset the offset
	 */
	public void subtractOffset(final Vector2D offset) {
		if(isReverseLongitude()) addX(offset.x() / cosLat);
		else subtractX(offset.x() / cosLat);
		if(isReverseLatitude()) addY(offset.y());
		else subtractY(offset.y());
	}
	
	
	/**
	 * Gets the native offset from.
	 *
	 * @param reference the reference
	 * @return the native offset from
	 */
	public Vector2D getNativeOffsetFrom(SphericalCoordinates reference) {
		Vector2D offset = new Vector2D();
		getNativeOffsetFrom(reference, offset);
		return offset;
	}
	
	/**
	 * Gets the offset from.
	 *
	 * @param reference the reference
	 * @return the offset from
	 */
	public Vector2D getOffsetFrom(SphericalCoordinates reference) {
		Vector2D offset = new Vector2D();
		getOffsetFrom(reference, offset);
		return offset;
	}
	
	
	/**
	 * Gets the native offset from.
	 *
	 * @param reference the reference
	 * @param toOffset the to offset
	 * @return the native offset from
	 */
	public final void getNativeOffsetFrom(final SphericalCoordinates reference, final Vector2D toOffset) {
		toOffset.setX(Math.IEEEremainder(x() - reference.x(), Constant.twoPi) * reference.cosLat);
		toOffset.setY(y() - reference.y());
	}
	
	/**
	 * Gets the offset from.
	 *
	 * @param reference the reference
	 * @param toOffset the to offset
	 * @return the offset from
	 */
	public void getOffsetFrom(final SphericalCoordinates reference, final Vector2D toOffset) {
		getNativeOffsetFrom(reference, toOffset);
		if(isReverseLongitude()) toOffset.scaleX(-1.0);
		if(isReverseLatitude()) toOffset.scaleY(-1.0);
	}
		
	/**
	 * Standardize.
	 */
	public void standardize() {
		setX(Math.IEEEremainder(x(), Constant.twoPi));
		setY(Math.IEEEremainder(y(), Math.PI));
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#toString()
	 */
	@Override
	public String toString() {
		CoordinateSystem coords = getCoordinateSystem();
		return coords.get(0).format(x()) + " " + coords.get(1).format(y());
	}
	
	public String toString(int decimals) {
		return Util.af[decimals].format(longitude()) + " " + Util.af[decimals].format(latitude());	
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return nf.format(longitude()) + " " + nf.format(latitude());		
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#parse(java.lang.String)
	 */
	@Override
	public void parse(String coords) throws NumberFormatException, IllegalArgumentException {
		StringTokenizer tokens = new StringTokenizer(coords, ", \t\n");
		CoordinateSystem coordinateSystem = getCoordinateSystem();
		try {
			setLongitude(coordinateSystem.get(0).format.parse(tokens.nextToken()).doubleValue());
			setLatitude(coordinateSystem.get(1).format.parse(tokens.nextToken()).doubleValue());
		} 
		catch(ParseException e) { throw new NumberFormatException(e.getMessage()); }
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(SphericalCoordinates point) {
		double sindTheta = Math.sin(point.y() - y());
		double sindPhi = Math.sin(point.x() - x());
		return 2.0 * SafeMath.asin(Math.sqrt(sindTheta * sindTheta + cosLat * point.cosLat * sindPhi * sindPhi));
	}

	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {	
		// Always write longitude in the 0:2Pi range.
		// Some FITS utilities may require it, even if it's not required by the FITS standard...
		double lon = Math.IEEEremainder(longitude(), Constant.twoPi);
		if(lon < 0.0) lon += Constant.twoPi;

		cursor.add(new HeaderCard("CRVAL1" + alt, lon / Unit.deg, "The reference longitude coordinate (deg)."));
		cursor.add(new HeaderCard("CRVAL2" + alt, latitude() / Unit.deg, "The reference latitude coordinate (deg)."));
		
		//cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
		if(alt.length() == 0) cursor.add(new HeaderCard("WCSAXES", 2, "Number of celestial coordinate axes."));
	}
		
	/* (non-Javadoc)
	 * @see kovacs.util.Coordinate2D#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parse(Header header, String alt) {
		setLongitude(header.getDoubleValue("CRVAL1" + alt, 0.0) * Unit.deg);
		setLatitude(header.getDoubleValue("CRVAL2" + alt, 0.0) * Unit.deg);
		
		
		//String name = header.getStringValue("WCSNAME");
		//if(name != null) getCoordinateSystem().name = name;
	}
	
	
	/**
	 * Equal angles.
	 *
	 * @param a1 the a1
	 * @param a2 the a2
	 * @return true, if successful
	 */
	public static boolean equalAngles(double a1, double a2) {
		return Math.abs(Math.IEEEremainder(a1-a2, Constant.twoPi)) < angularAccuracy;
	}
	
	/** The Constant angularAccuracy. */
	public final static double angularAccuracy = 1e-12;
	
	
	/**
	 * Transform.
	 *
	 * @param from the from
	 * @param newPole the new pole
	 * @param phi0 the phi0
	 * @param to the to
	 */
	public static final void transform(final SphericalCoordinates from, final SphericalCoordinates newPole, final double phi0, final SphericalCoordinates to) {		
		final double dL = from.x() - newPole.x();
		final double cosdL = Math.cos(dL);	
		to.setNativeLatitude(SafeMath.asin(newPole.sinLat * from.sinLat + newPole.cosLat * from.cosLat * cosdL));
		to.setNativeLongitude(Constant.rightAngle - phi0 +
				Math.atan2(-from.sinLat * newPole.cosLat + from.cosLat * newPole.sinLat * cosdL, -from.cosLat * Math.sin(dL))
		);	
	}
	
	/**
	 * Inverse transform.
	 *
	 * @param from the from
	 * @param pole the pole
	 * @param phi0 the phi0
	 * @param to the to
	 */
	public static final void inverseTransform(final SphericalCoordinates from, final SphericalCoordinates pole, final double phi0, final SphericalCoordinates to) {		
		final double dL = from.x() + phi0;
		final double cosdL = Math.cos(dL);
		
		to.setNativeLatitude(SafeMath.asin(pole.sinLat * from.sinLat + pole.cosLat * from.cosLat * cosdL));
		to.setNativeLongitude(pole.x() + Constant.rightAngle + 
				Math.atan2(-from.sinLat * pole.cosLat + from.cosLat * pole.sinLat * cosdL, -from.cosLat * Math.sin(dL)));	
	}
	
	/**
	 * Gets the fits class.
	 *
	 * @param spec the spec
	 * @return the FITS class
	 */
	public static Class<? extends SphericalCoordinates> getFITSClass(String spec) {
		spec = spec.toUpperCase();
		
		if(spec.startsWith("RA")) return jnum.astro.EquatorialCoordinates.class;
		else if(spec.startsWith("DEC")) return jnum.astro.EquatorialCoordinates.class;
		else if(spec.substring(1).startsWith("LON")) {
			switch(spec.charAt(0)) {
			case 'G' : return jnum.astro.GalacticCoordinates.class;
			case 'E' : return jnum.astro.EclipticCoordinates.class;
			case 'S' : return jnum.astro.SuperGalacticCoordinates.class;
			case 'A' : return jnum.astro.HorizontalCoordinates.class;
			case 'F' : return jnum.astro.FocalPlaneCoordinates.class;
			}
		}
		else if(spec.substring(1).startsWith("LAT")) {
			switch(spec.charAt(0)) {
			case 'G' : return jnum.astro.GalacticCoordinates.class;
			case 'E' : return jnum.astro.EclipticCoordinates.class;
			case 'S' : return jnum.astro.SuperGalacticCoordinates.class;
			case 'A' : return jnum.astro.HorizontalCoordinates.class;
			case 'F' : return jnum.astro.FocalPlaneCoordinates.class;
			}
		}
		throw new IllegalArgumentException("Unknown Coordinate Definition " + spec);
	}

	/* (non-Javadoc)
	 * @see kovacs.util.math.Invertible#invert()
	 */
	@Override
	public void invert() {
		invertX(); invertY();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.math.Coordinate2D#invertY()
	 */
	@Override
	public void invertY() {
		super.invertY();
		sinLat *= -1.0;
	}
	

	public final static Unit degree = Unit.get("deg");
	public final static Unit arcmin = Unit.get("arcmin");
	public final static Unit arcsec = Unit.get("arcsec");
	
}

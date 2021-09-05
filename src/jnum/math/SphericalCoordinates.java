/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.math;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;

import jnum.Constant;
import jnum.ExtraMath;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.astro.*;
import jnum.fits.FitsToolkit;
import jnum.projection.SphericalProjection;
import jnum.text.AngleFormat;
import jnum.text.DecimalFormating;
import jnum.text.GreekLetter;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO add BinaryTableIO interface (with projections...)


/**
 * Spherical coordinates with longitude and latitude  angles. It is also the based class for many specific 
 * spherical coordinate types used in astronomy and geodesy. Because these are not Cartesian coordinates,
 * they have specific algebras for adding, differencing and measuring distances. And because the sine and
 * cosine of the latitudes is very commonly used in all types of calculations involving spherical coordinates,
 * these terms a pre-calculated for computationally efficient use.
 * 
 * @author Attila Kovacs
 *
 */
public class SphericalCoordinates extends Coordinate2D implements Metric<SphericalCoordinates>, Inversion, DecimalFormating {

    /**
     * 
     */
	private static final long serialVersionUID = -8343774069424653101L;

	/**
	 * The cosine of the latitude, conveniently pre-calculated for computationally efficient use.
	 */
	private double cosLat;
	
	/**
     * The cosine of the latitude, conveniently pre-calculated for computationally efficient use.
     */
	private double sinLat;
		
	/**
	 * Instantiates new spherical coordinates, with latitude and longitude both initialized to zero.
	 * 
	 */
	public SphericalCoordinates() {
		cosLat = 1.0;
		sinLat = 0.0;		
	}

	/**
	 * Instantiates new spherical coordinates using the specified longitude and latitude values.
	 * 
	 * @param longitude    (rad) the logitude coordinate, increasing to the right when looking at the
	 *                     sphere from the outside, from above the location.
	 * @param latitude     (rad) the latitude coordinate [-Pi:Pi]
	 * 
	 * @see #set(double...)
	 */
	public SphericalCoordinates(final double longitude, final double latitude) { set(longitude, latitude); }
	
	/**
	 * Instantiates new spherical coordinates from a Cartesian vector in the same space. The Cartesian <i>x</i>
	 * coordinate x is aling the direction of zero longitude and zero latitude. <i>u</i> is the direction
	 * of increasing latitude from the same spherical origin. <i>z</i> is the direction of 90&deg; (&pi;/2) latitude.
	 * the conversion of the 3D Cartesian vector to 2D spherical coordinates, preserves only the direction
	 * but not the magnitude of the argument vector.
	 * 
	 * 
	 * @param v    the Cartesian vector that defines the spherical coordinates in the same space.
	 */
	public SphericalCoordinates(Vector3D v) {
	    fromCartesian(v);
	}

	/**
	 * Instantiates new spherical coordinates from a string representation of them, if possible
	 * 
	 * @param text     String beginning with the textual representation of spherical coordinates 
	 * @throws IllegalArgumentException    If no spherical coordinates could be parsed from the string
	 * 
	 * @see #parse(String)
	 */
	public SphericalCoordinates(String text) throws IllegalArgumentException { parse(text); }
		
    @Override
    public void copy(Coordinates<? extends Double> coords) {
        setX(coords.x());
        setY(coords.y());
    }
    
    @Override
    public SphericalCoordinates copy() { return (SphericalCoordinates) super.copy(); }
    
    /**
     * Gets the precalculated sine of the latitude.
     * 
     * @return  sin(&theta;), where &theta; is the latitude coordinate.
     */
	public final double sinLat() { return sinLat; }
	

	/**
     * Gets the [recalculated cosine of the latitude.
     * 
     * @return  cos(&theta;), where &theta; is the latitude coordinate.
     */
	public final double cosLat() { return cosLat; }
	
	/**
	 * Gets the FITS value stem for the longitude coordinate of this type of spherical coordinates 
	 * to be used with the <code>CTYPE</code><i>n</i> type of FITS keywords. This is the part preceding 
	 * the projection component of the value for the same key, which is provided by 
	 * {@link jnum.projection.SphericalProjection} usually. For example, for equatorial coordinates 
	 * using the Gnomonic projection, the FITS keyword for the first coordinate (of the default coordinate set) 
	 * would be:
	 * 
	 * <pre>
	 *  
	 *   CTYPE1 = 'RA---TAN'
	 *   
	 * </pre>
	 * 
	 * In the above <code>RA--</code> is provided by this calls implenetation in {@link EquatorialCoordinates}
	 * class, while the last 3 letters <code>TAN</code> are provided by {@link jnum.projection.Gnomonic#getFitsID()}.
	 * Normally, the specific implementation of {@link jnum.projection.SphericalProjection} will call this
	 * methods to fill the appropriate values for the projection and reference coordinates in the
	 * FITS header.
	 * 
	 * @return The 4-letter coordinate type stem to be used when constructing a FITS <code>CTYPE</code><i>n</i> type
	 *         coordinate description.
	 *         
	 * @see #getFITSLatitudeStem()
	 * @see jnum.projection.SphericalProjection#getFitsID()
	 * 
	 */
    public String getFITSLongitudeStem() { return "LON-"; }
    
    
    /**
     * Gets the FITS value stem for the latitude coordinate of this type of spherical coordinates 
     * to be used with the <code>CTYPE</code><i>n</i> type of FITS keywords. This is the part preceding 
     * the projection component of the value for the same key, which is provided by 
     * {@link jnum.projection.SphericalProjection} usually. For example, for equatorial coordinates 
     * using the Gnomonic projection, the FITS keyword for the first coordinate (of the default coordinate set) 
     * would be:
     * 
     * <pre>
     *  
     *   CTYPE2 = 'DEC--TAN'
     *   
     * </pre>
     * 
     * In the above <code>DEC-</code> is provided by this calls implenetation in {@link EquatorialCoordinates}
     * class, while the last 3 letters <code>TAN</code> are provided by {@link jnum.projection.Gnomonic#getFitsID()}.
     * Normally, the specific implementation of {@link jnum.projection.SphericalProjection} will call this
     * methods to fill the appropriate values for the projection and reference coordinates in the
     * FITS header.
     * 
     * @return The 4-letter coordinate type stem to be used when constructing a FITS <code>CTYPE</code><i>n</i> type
     *         coordinate description.
     *         
     * @see #getFITSLatitudeStem()
     * @see jnum.projection.SphericalProjection#getFitsID()
     * 
     */
    public String getFITSLatitudeStem() { return "LAT-"; }
    
    /**
     * Gets a two-letter representation of this coordinate class. For example, {@link EquatorialCoordinates}
     * may return 'EQ', while {@link HorizontalCoordinates} may return 'HO'. It should be something
     * that is unique but easy for a humna to identify still. 
     * 
     * @return
     */
    public String getTwoLetterID() { return "SP"; }
	

    /**
     * Gets the coordinate system for these spherical coordinates, which define axes, labels, string
     * representations of angles etc. Coordinate systems can be used for plotting, or when printing
     * values from spherical coordinates, or e.g. when adding coordinate information into FITS headers.
     * 
     * @return  the coordinate system for this spherical coordinate instance.
     */
	public CoordinateSystem getCoordinateSystem() { return defaultCoordinateSystem; }

	/**
	 * Gets the longitude coordinate axis definition for this spherical coordinate instance,
	 * which define labels, string representations of the longitude etc. 
	 * 
	 * @return the longitude axis for this spherical coordinate instance.
	 */
	public final CoordinateAxis getLongitudeAxis() { return getCoordinateSystem().get(0); }
	
	/**
     * Gets the latitude coordinate axis definition for this spherical coordinate instance,
     * which define labels, string representations of the latitude etc. 
     * 
     * @return the latitude axis for this spherical coordinate instance.
     */
	public final CoordinateAxis getLatitudeAxis() { return getCoordinateSystem().get(1); }
	
	/**
	 * Gets the Cartesian local coordinate system around the position on the sphere represented
	 * by this spherical coordinate instance. The local coordinates <i>x,y</i> are aligned with the
	 * spherical longitude and latitude directions at the spherical coordinates represented
	 * by this instance. They can be used for local offsets projected (sinusoidally) to a plane.
	 * Like {@link #getCoordinateSystem()}, it defines labels and string formating of
	 * offsets for this instance of spherical coordinates.
	 * 
	 * @return the local Cartesian coordinate system tangential to the sphere at the location
	 *         represented by this instance.
	 */
	public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }

	@Override
	public final void setY(final double value) { 
		super.setY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(value);
		cosLat = Math.cos(value);
	}

	@Override
	public final void addY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}

	@Override
	public final void subtractY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}

	@Override
	public void zero() { super.zero(); cosLat = 1.0; sinLat = 0.0; }

	@Override
	public void NaN() { super.NaN(); cosLat = Double.NaN; sinLat = Double.NaN; }

	@Override
	public void set(double... v) { 
	    setLongitude(v[0]); 
	    if(v.length > 1) setLatitude(v[1]); 
	}
		
	/**
	 * Sets the native longitude and latitude coordinates contained in this instance. Native
	 * coordinates are always defined as a right-handed coordinate system (longitude increasing
	 * to the right) when looking at the sphere from above the locaton with the pole pointing
	 * upwards. 
	 * 
	 * @param lon  (rad) the native longitude (increases to the right when looking at the sphere from the outside, with pole up).
	 * @param lat  (rad) the native latitude (0 at equator, and increasing towards the pole).
	 */
	public void setNative(final double lon, final double lat) { super.set(lon, lat); }
	
	/**
	 * Gets the native longitude for these spherical coordinate instance. Native longitude is always
	 * defined as increasing to the right when looking at the sphere from above the location with pole
	 * being up. It is defined to be the same as {@link #x()}.
	 * 
	 * @return     (rad) the native longitude.
	 * '
	 * @see #isReverseLongitude()
	 */
	public final double nativeLongitude() { return x(); }
	
	/**
	 * Gets the native latitude for these spherical coordinate instance. Native latitude is
	 * always defined as 0 at the equator and increases towards the pole (North). 
	 * It is defined to be the same as {@link #y()}.
	 * 
	 * @return     (rad) the native latitude.
	 * 
	 * @see #isReverseLatitude()
	 */
	public final double nativeLatitude() { return y(); }
	
	/**
	 * Checks if the longitude axis is reversed relative to the native longitude axis.
	 * 
	 * @return     <code>true</code> if the longitude increases to the left when looking at the sphere from above
	 *             the location, with the pole up. Otherwise <code>false</code>.
	 * 
	 * @see #longitude()
	 * @see #nativeLongitude()
	 */
	public final boolean isReverseLongitude() { return getCoordinateSystem().get(0).isReverse(); }
	
	/**
     * Checks if the latitude axis is reversed relative to the native latitude axis.
     * 
     * @return      <code>true</code> if the latitude is zero at the pole ansd increases towards the equator. 
     *              Otherwise <code>false</code>.
     * 
     * @see #latitude()
     * @see #nativeLatitude()
     */
	public final boolean isReverseLatitude() { return getCoordinateSystem().get(1).isReverse(); }
	
	
	/**
	 * Gets the conventional longitude coordinate for this spherical coordinate instance. Unlike the {@link #nativeLongitude()},
	 * which has a well-defined direction, the conventional longitude may increase in either to the left or to the right
	 * when looking at the sphere from above the location with the pole (North) up, depending on the convention for the
     * type of spherical coordinates.
	 * 
	 * @return    (rad)  the conventional longitude coordinate for this spherical coordinate type.
	 * 
	 * @see #nativeLongitude()
	 * @see #isReverseLongitude()
	 */
	public final double longitude() { return isReverseLongitude() ? getCoordinateSystem().get(0).reverseFrom-nativeLongitude() : nativeLongitude(); }
	

	/**
     * Gets the conventional latitude coordinate for this spherical coordinate instance. Unlike the {@link #nativeLatitude()},
     * which is defined to be zero at the equator, the conventional longitude may has its origin at the pole, such as for
     * zenith angle, depending on the convention for the type of spherical coordinates.
     * 
     * @return      (rad)  the conventional latitude coordinate for this spherical coordinate type.
     * 
     * @see #nativeLatitude()
     * @see #isReverseLatitude()
     */
	public final double latitude() { return isReverseLatitude() ? getCoordinateSystem().get(1).reverseFrom-nativeLatitude() : nativeLatitude(); }
	

	/**
     * Sets a new native longitude for these spherical coordinate instance. Native longitude is always
     * defined as increasing to the right when looking at the sphere from above the location with pole
     * being up. It is defined to be the same as {@link #setX(double)}.
     * 
     * @param value    (rad) the new native longitude.
     * 
     * @see #nativeLongitude()
     * @see #setLongitude(double)
     * @see #isReverseLongitude()
     */
	public final void setNativeLongitude(final double value) { setX(value); }
		

	/**
     * Sets a new native latitude for these spherical coordinate instance. Native latitude is
     * always defined as 0 at the equator and increases towards the pole (North). 
     * It is defined to be the same as {@link #setY(double)}.
     * 
     * @param value     (rad) the new native latitude.
     * 
     * @see #nativeLatitude()
     * @see #setLatitude(double)
     * @see #isReverseLatitude()
     */
	public final void setNativeLatitude(final double value) { setY(value); }


	/**
     * Set a new conventional longitude coordinate for this spherical coordinate instance. Unlike the {@link #setNativeLongitude(double)},
     * which has a well-defined direction, the conventional longitude may increase in either to the left or to the right
     * when looking at the sphere from above the location with the pole (North) up, depending on the convention for the
     * type of spherical coordinates.
     * 
     * @param value     (rad) the new conventional longitude coordinate for this spherical coordinate type.
     * 
     * @see #longitude()
     * @see #setNativeLongitude(double)
     * @see #isReverseLongitude()
     */
	public final void setLongitude(final double value) {
		setNativeLongitude(isReverseLongitude() ? getCoordinateSystem().get(0).reverseFrom-value : value);
	}
	

	/**
     * Set a new conventional latitude coordinate for this spherical coordinate instance. Unlike the {@link #setNativeLongitude(double)},
     * which is measured from the equator, the conventional latitude may have its origin at the pole, e.g. a zenith angle, 
     * depending on the convention for the type of spherical coordinates.
     * 
     * @param value     (rad) the new conventional latitude coordinate for this spherical coordinate type.
     * 
     * @see #latitude()
     * @see #setNativeLatitude(double)
     * @see #isReverseLatitude()
     */
	public final void setLatitude(final double value) {
		setNativeLatitude(isReverseLatitude() ? getCoordinateSystem().get(1).reverseFrom-value : value);
	}
	
	/**
	 * Projects these coordinates onto a plane, using the specified projection.
	 * 
	 * @param projection           the spherical projection to use, including a reference point and other parameters.
	 * @param toNativeOffset       the projected planar offset coordinates (from the native coordinates).
	 * 
	 * @see #nativeLongitude()
	 * @see #nativeLatitude()
	 */
	public void project(final SphericalProjection projection, final Coordinate2D toNativeOffset) {
		projection.project(this, toNativeOffset);
	}

	/**
     * Gets the projected planar offsets of these coordinates, using the specified projection.
     * 
     * @param projection        the spherical projection to use, including a reference point and other parameters.
     * @return                  the projected planar offset coordinates (from the native coordinates).
     * 
     * @see #nativeLongitude()
     * @see #nativeLatitude()
     */
    public final Coordinate2D getProjected(final SphericalProjection projection) { return projection.getProjected(this); }
    
	
	/**
	 * Sets these coordinates from the specified projection and projected planar offsets.
	 * 
	 * @param projection           The spherical projection to use, including a reference point and other parameters.
	 * @param fromNativeOffset     the projected planar offset coordinates (from which to set the native coordinates).
	 * 
	 * @see #setNativeLongitude(double)
     * @see #setNativeLatitude(double)
	 */
	public void fromProjected(final SphericalProjection projection, final Coordinate2D fromNativeOffset) {
		projection.deproject(fromNativeOffset, this);
	}
		

	/**
	 * Adds an offset in the native longitude/latitude directions to these coordinates, 
	 * using the standard global sinusiodal (SFL) projection
	 * 
	 * @param offset   the local offset coordinates along the native longitude and latitude directions.
	 * 
	 * @see #subtractNativeOffset(Vector2D)
	 * @see #addOffset(Vector2D)
	 * @see #isReverseLongitude()
	 * @see #isReverseLatitude()
	 */
	public void addNativeOffset(final Vector2D offset) {
		addX(offset.x() / cosLat);
		addY(offset.y());
	}
	
	/**
     * Adds an offset in the conventional longitude and latitude directions of these spherical coordinates 
     * (which may be reversed relative to the native longitude latitude axes), using the standard global 
     * sinusiodal (SFL) projection.
     * 
     * @param offset   the local offset coordinates along the native longitude and latitude directions.
     * 
     * @see #subtractOffset(Vector2D)
     * @see #addNativeOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public void addOffset(final Vector2D offset) {
		if(isReverseLongitude()) subtractX(offset.x() / cosLat);
		else addX(offset.x() / cosLat);
		if(isReverseLatitude()) subtractY(offset.y());
		else addY(offset.y());
	}
	
	/**
     * Subtracts an offset in the native longitude and latitude directions from these coordinates, 
     * using the standard global sinusiodal (SFL) projection
     * 
     * @param offset   the local offset coordinates along the conventional longitude and latitude directions of these coordinates.
     * 
     * @see #addNativeOffset(Vector2D)
     * @see #subtractOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public void subtractNativeOffset(final Vector2D offset) {
		subtractX(offset.x() / cosLat);
		subtractY(offset.y());
	}
	
	/**
     * Subtracts an offset in the conventional longitude and latitude directions of these spherical coordinates 
     * (which may be reversed relative to the native longitude latitude axes), using the standard global 
     * sinusiodal (SFL) projection.
     * 
     * @param offset   the local offset coordinates along the conventional longitude and latitude directions of these coordinates.
     * 
     * @see #subtractOffset(Vector2D)
     * @see #addNativeOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public void subtractOffset(final Vector2D offset) {
		if(isReverseLongitude()) addX(offset.x() / cosLat);
		else subtractX(offset.x() / cosLat);
		if(isReverseLatitude()) addY(offset.y());
		else subtractY(offset.y());
	}
	

	/**
	 * Gets the projected (SFL) offset from some reference coordinates, in the directions of the native longitude
	 * and latitude axes.
	 * 
	 * @param reference    reference coordinates to measure offset from.
	 * @return             the local offset from the reference along the native longitude and latitude 
	 *                     directions of these coordinates.
	 * 
	 * @see #getNativeOffsetFrom(SphericalCoordinates, Vector2D)
	 * @see #getOffsetFrom(SphericalCoordinates)
	 * @see #addNativeOffset(Vector2D)
     * @see #subtractNativeOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
	 */
	public Vector2D getNativeOffsetFrom(SphericalCoordinates reference) {
		Vector2D offset = new Vector2D();
		getNativeOffsetFrom(reference, offset);
		return offset;
	}
	
	/**
     * Gets the projected (SFL) offset from some reference coordinates, in the directions of the conventional longitude
     * and latitude axes.
     * 
     * @param reference    reference coordinates to measure offset from.
     * @return             the local offset from the reference along the conventional longitude and latitude 
     *                     directions of these coordinates.
     * 
     * @see #getOffsetFrom(SphericalCoordinates, Vector2D)
     * @see #getNativeOffsetFrom(SphericalCoordinates)
     * @see #addOffset(Vector2D)
     * @see #subtractOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public Vector2D getOffsetFrom(SphericalCoordinates reference) {
		Vector2D offset = new Vector2D();
		getOffsetFrom(reference, offset);
		return offset;
	}
	
	/**
     * Gets the projected (SFL) offset from some reference coordinates, in the directions of the native longitude
     * and latitude axes. The projected offsets are returned in the supplied second argument.
     * 
     * @param reference    reference coordinates to measure offset from.
     * @param toOffset     a 2D vector in which to return the local offset from the reference along the native 
     *                     longitude and latitude directions of these coordinates.
     * 
     * @see #getOffsetFrom(SphericalCoordinates, Vector2D)
     * @see #getNativeOffsetFrom(SphericalCoordinates)
     * @see #addNativeOffset(Vector2D)
     * @see #subtractNativeOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public final void getNativeOffsetFrom(final SphericalCoordinates reference, final Vector2D toOffset) {
		toOffset.setX(Math.IEEEremainder(x() - reference.x(), Constant.twoPi) * reference.cosLat);
		toOffset.setY(y() - reference.y());
	}
	
	/**
     * Gets the projected (SFL) offset from some reference coordinates, in the directions of the conventional longitude
     * and latitude axes. The projected offsets are returned in the supplied second argument.
     * 
     * @param reference    reference coordinates to measure offset from.
     * @param toOffset     a 2D vector in which to return the local offset from the reference along the conventional 
     *                     longitude and latitude directions of these coordinates.
     * 
     * @see #getNativeOffsetFrom(SphericalCoordinates, Vector2D)
     * @see #getOffsetFrom(SphericalCoordinates)
     * @see #addOffset(Vector2D)
     * @see #subtractOffset(Vector2D)
     * @see #isReverseLongitude()
     * @see #isReverseLatitude()
     */
	public void getOffsetFrom(final SphericalCoordinates reference, final Vector2D toOffset) {
		getNativeOffsetFrom(reference, toOffset);
		if(isReverseLongitude()) toOffset.scaleX(-1.0);
		if(isReverseLatitude()) toOffset.scaleY(-1.0);
	}
		
	/**
	 * Standardizes the longitudes to -&pi;:&pi; range and latitude to the -&pi;/2:&pi;/2 range.
	 * 
	 */
	public void standardize() {
		super.setX(Math.IEEEremainder(x(), Constant.twoPi));
		super.setY(Math.IEEEremainder(y(), Math.PI));
	}
	
	/**
	 * Returns a number formater with the specified number of decimals that can be used for
	 * converting longitudes to string in the standard formating convention of these coordinates.
	 * 
	 * @param decimals     Number of decimal places to show.
	 * @return             Number formating object that can convert longitude values to conventional string
	 *                     representation of these coordinates.
	 */
	public NumberFormat getLongitudeFormat(int decimals) {
	    return Util.af[decimals];
	}

	/**
     * Returns a number formater with the specified number of decimals that can be used for
     * converting latitudes to string in the standard formating convention of these coordinates.
     * 
     * @param decimals     Number of decimal places to show.
     * @return             Number formating object that can convert latitude values to conventional string
     *                     representation of these coordinates.
     */
	public NumberFormat getLatitudeFormat(int decimals) {
	    return Util.af[decimals];
	}

	@Override
	public String toString() {
	    return toString(3);
	}
	

	@Override
    public String toString(int decimals) {
		return getLongitudeFormat(decimals).format(longitude()) + " " + getLatitudeFormat(decimals).format(latitude());	
	}

	@Override
	public String toString(NumberFormat nf) {
		return nf.format(longitude()) + " " + nf.format(latitude());		
	}

	@Override
	public void parse(StringParser parser) throws IllegalArgumentException {
	    parser.skipWhiteSpaces();
	    
	    SphericalCoordinates parseCoords = null; 
	    
	    if(parser.nextIndexOf(Util.getWhiteSpaceChars() + "(") - parser.getIndex() == 2) {
	        String id = parser.getString().substring(parser.getIndex(), parser.getIndex() + 2);
	        Class<? extends SphericalCoordinates> parseClass = SphericalCoordinates.getTwoLetterClass(id);
	        
	        if(parseClass != null) {
	            parser.skip(3);
	            parser.skipWhiteSpaces();
	            
	            try { parseCoords = parseClass.getConstructor().newInstance(); }
	            catch(Exception e) { Util.warning(this, e); }
	        }
	    }
	    
	    if(parseCoords == null) parseCoords = copy();
	    
	    parseCoords.parseDirect(parser);

	    convertFrom(parseCoords);
	}
	
	/**
	 * Direct parsing of two comma or space separated numerical values according to the standard formating
	 * rules of the axes.
	 * 
	 * @param parser       The string parser with the current parse position from which the 2 numbers 
	 *                     constituting the coordinates for this class are to be parsed from.
	 * @throws IllegalArgumentException    if the coordinates could not be parsed from the given string or position.
	 */
	protected void parseDirect(StringParser parser) throws IllegalArgumentException {
	    super.parse(parser);
	}
	
	@Override
    public void parseX(String spec) throws NumberFormatException {
	    try { setLongitude(getLongitudeAxis().format.parse(spec).doubleValue()); }
	    catch(ParseException e) { throw new NumberFormatException("Unparseable longitude: " + spec); }
	}
	 
	@Override
    public void parseY(String spec) {
        try { setLatitude(getLatitudeAxis().format.parse(spec).doubleValue()); }
        catch(ParseException e) { throw new NumberFormatException("Unparseable latitude: " + spec); }
    }

	@Override
	public double distanceTo(SphericalCoordinates point) {
	    final double cosphi2cosdl = point.cosLat * Math.cos(point.x() - x());
	    final double c = sinLat * point.sinLat + cosLat * cosphi2cosdl;
	    
	    // The simplest formula (law of cosines) is good for intermediate distances...
	    if(c < 0.9) if(c > -0.9) return Math.acos(c);
	
	    // Otherwise, Vincenty formula for better precision near and antipolar...
	    return Math.atan2(ExtraMath.hypot(point.cosLat * Math.sin(point.x() - x()), cosLat * point.sinLat - sinLat * cosphi2cosdl),  c);
	}

	@Override
	public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {	
		// Always write longitude in the 0:2Pi range.
		// Some FITS utilities may require it, even if it's not required by the FITS standard...
		double lon = Math.IEEEremainder(longitude(), Constant.twoPi);
		if(lon < 0.0) lon += Constant.twoPi;
		
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
		
        c.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));

		c.add(new HeaderCard(keyStem + "1" + alt, lon / Unit.deg, "The reference longitude coordinate (deg)."));
		c.add(new HeaderCard(keyStem + "2" + alt, latitude() / Unit.deg, "The reference latitude coordinate (deg)."));
		
		//cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
		if(alt.length() == 0) c.add(new HeaderCard("WCSAXES", 2, "Number of celestial coordinate axes."));
	}

	@Override
	public void parseHeader(Header header, String keyStem, String alt, Coordinate2D defaultValue) {
		setLongitude(header.getDoubleValue(keyStem + "1" + alt, defaultValue == null ? 0.0 : defaultValue.x()) * Unit.deg);
		setLatitude(header.getDoubleValue(keyStem + "2" + alt, defaultValue == null ? 0.0 : defaultValue.x()) * Unit.deg);
		
		
		//String name = header.getStringValue("WCSNAME");
		//if(name != null) getCoordinateSystem().name = name;
	}

    @Override
    public void flip() {
        flipX(); flipY();
    }

    @Override
    public void flipY() {
        super.flipY();
        sinLat = -sinLat;
    }

    /**
     * Converts these spherical coordinates to a Cartesian 3D unit vector representation. The 3D space
     * is aligned with the native axes, that is <i>x</i> is in the direction of 0 longitude and 
     * 0 latitude from the center of the sphere, <i>y</i> is in the direction of increasing native longitudes
     * (that is to the right as looking in at the sphere from the <i>x</i> direction), and <i>z</i> is in the
     * directon of the pole (North).
     * 
     * @return    a new 3D vector in which to return the 3D unit vector equivalent to these coordinates.
     * 
     * @see #toCartesian(Vector3D)
     * @see #fromCartesian(MathVector)
     */
    public final Vector3D toCartesian() {
        Vector3D v = new Vector3D();
        toCartesian(v);
        return v;
    }
    
    /**
     * Converts these spherical coordinates to a Cartesian 3D unit vector representation. The 3D space
     * is aligned with the native axes, that is <i>x</i> is in the direction of 0 longitude and 
     * 0 latitude from the center of the sphere, <i>y</i> is in the direction of increasing native longitudes
     * (that is to the right as looking in at the sphere from the <i>x</i> direction), and <i>z</i> is in the
     * directon of the pole (North).
     * 
     * @param v     a 3D vector in which to return the 3D unit vector equivalent to these coordinates.
     * 
     * @see #toCartesian()
     * @see #fromCartesian(MathVector)
     */
    public void toCartesian(Vector3D v) {     
        v.setX(cosLat() * Math.cos(x()));
        v.setY(cosLat() * Math.sin(x()));
        v.setZ(sinLat());
    }
	
    /**
     * Sets this vector to the direction specified by a 3D Cartesian vector. The 3D space
     * is aligned with the native axes, that is <i>x</i> is in the direction of 0 longitude and 
     * 0 latitude from the center of the sphere, <i>y</i> is in the direction of increasing native longitudes
     * (that is to the right as looking in at the sphere from the <i>x</i> direction), and <i>z</i> is in the
     * directon of the pole (North).
     * 
     * @param v     the 3D Carteian location whose direction from the origin will define these spherical coordinates.
     * @return      the radius of the input vector (which would otherwise be lost in the conversion).
     * 
     * @see #toCartesian()
     * @see #toCartesian(Vector3D)
     */
    public double fromCartesian(MathVector<Double> v) { 
        final double r = v.abs();
        
        if(r == 0.0) v.zero();
        else if(v.z() == r) set(0.0, Constant.rightAngle);
        else {
            final double xy = ExtraMath.hypot(v.x(), v.y());
            setY(Math.atan2(v.z(), xy));
            if(xy == 0.0) setX(0.0);
            else setX(Math.atan2(v.y(), v.x()));
        }
        return r;
    }

    /**
     * Checks if two angles are equal within the precision set by {@link #angularAccuracy}, but ignoring 
     * any multiples of 2&pi; that might be between the two angles. That is, the two angles are equal if
     * they essentially represent the same point on a circle within th tolerate precision.
     * 
     * @param a1    (rad) first angle
     * @param a2    (rad) second angle
     * @return      <code>true/</code> if the two angle represent the same point on a circle, within
     *              the tolerated precision. Otherwise <code>false</code>.
     */
	public static boolean equalAngles(double a1, double a2) {
		return Math.abs(Math.IEEEremainder(a1-a2, Constant.twoPi)) < angularAccuracy;
	}
	
	/**
	 * Rotates spherical coordinates into another spherical coordinate system.
	 * 
	 * @param from     the input coordinates to rotate
	 * @param newPole  the pole of the new coordinate system expressed in the in the coordinate system of the input coordinates.
	 * @param phi0     the longitude offset of the output coordinate system
	 * @param to       the input coordinates in the rotated system.
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
     * Rotates spherical coordinates from another spherical coordinate system.
     * 
     * @param from     the input coordinates in the other coordinate system
     * @param pole     the pole of the input coordinate system expressed in the in the output coordinate system.
     * @param phi0     the longitude offset of the input coordinate system
     * @param to       the input coordinates in the coordinate system in which the pole was defined.
     */
	public static final void inverseTransform(final SphericalCoordinates from, final SphericalCoordinates pole, final double phi0, final SphericalCoordinates to) {		
		final double dL = from.x() + phi0;
		final double cosdL = Math.cos(dL);
		
		to.setNativeLatitude(SafeMath.asin(pole.sinLat * from.sinLat + pole.cosLat * from.cosLat * cosdL));
		to.setNativeLongitude(pole.x() + Constant.rightAngle + 
				Math.atan2(-from.sinLat * pole.cosLat + from.cosLat * pole.sinLat * cosdL, -from.cosLat * Math.sin(dL)));	
	}
	
	/**
	 * Gets the specific spherical coordinate subclass which best matches the coordinates specified by a CTYPE<i>n</i>
	 * type FITS header coordinate specification.
	 * 
	 * @param spec     the FITS header value for the representative CTYPE<i>n</i> style header coordinate specifying keyword.
	 * @return         the spherical coordinate class that fits the description.
	 * @throws IllegalArgumentException if could not find a good match to the given coordinate specification.
	 */
	public static Class<? extends SphericalCoordinates> getFITSClass(String spec) throws IllegalArgumentException {	
	    if(fitsTypes == null) registerTypes();
	    
	    StringBuffer buf = new StringBuffer(4);
	    int l = Math.min(4, spec.length());
	    int i;
	    
        for(i=0; i<l; i++) {
            char c = spec.charAt(i);
            if(c == '-') break;
            buf.append(spec.charAt(i));
        }
        for(; i<4; i++) buf.append('-');
        spec = new String(buf);
	       
		Class<? extends SphericalCoordinates> coordClass = fitsTypes.get(spec.toUpperCase());
		if(coordClass == null) throw new IllegalArgumentException("Unknown Coordinate Definition " + spec);
		return coordClass;
	}

	/**
     * Gets the specific spherical coordinate subclass which matches the specified 2-letter ID (case insensitive).
     * Every spherical coordinate subclass has a unique 2-letter ID, such as 'EQ' for {@link EquatorialCoordinates}
     * or 'HO' for {@link HorizontalCoordinates}.  Only coordinate classes that were registered via 
     * {@link #register(SphericalCoordinates)} can be retrieved by this call.
     * 
     * @param id       the 2-letter ID of the spherical coordinate whose class we want.
     * @return         the spherical coordinate class that matches the ID (case insensitive).
     * @throws IllegalArgumentException if could not find a good match to the given coordinate specification.
     * 
     * @see #getTwoLetterID()
     * @see #getTwoLetterIDFor(Class)
     * @see #register(SphericalCoordinates)
     */
	public static Class<? extends SphericalCoordinates> getTwoLetterClass(String id) throws IllegalArgumentException {
	    if(ids == null) registerTypes();
	    Class<? extends SphericalCoordinates> coordClass = ids.get(id.toUpperCase());
        if(coordClass == null) throw new IllegalArgumentException("Unknown Coordinate Definition " + id);
        return coordClass;
	}

	 /**
     * Gets a two-letter representation of the specified spherical coordinate class. Every spherical coordinate 
     * subclass has a unique 2-letter ID, such as 'EQ' for {@link EquatorialCoordinates} or 'HO' for {@link HorizontalCoordinates}.
     * Only coordinate classes that were registered via {@link #register(SphericalCoordinates)} will have
     * a defined mapping.
     * 
     * @return  the 2-letter ID of the registered spherical coordinate class, or <code>null</code> if there
     *          if the argument class is not registered via {@link #register(SphericalCoordinates)}
     *          
     * @see #getTwoLetterID()
     * @see #getTwoLetterClass(String)
     * @see #register(SphericalCoordinates)
     */
	public static String getTwoLetterIDFor(Class<? extends Coordinate2D> coordinateClass) {
	    if(idLookup == null) registerTypes();
	    return idLookup.get(coordinateClass);
	}
	
	/** hashtable for spherical coordinate class to 2-letter ID mappings */
    private static Hashtable<Class<? extends SphericalCoordinates>, String> idLookup;
    
    /** hashtable for 2-letter ID to spherical coordinate class mappings */
    private static Hashtable<String, Class<? extends SphericalCoordinates>> ids;
    
    /** hashtable for FITS coordinate type header value to spherical coordinate class mappings */
    private static Hashtable<String, Class<? extends SphericalCoordinates>> fitsTypes;
	
    /**
     * Registers all known types of spherical coordinates within the jnum package.
     * External implementations can call {@link #register(SphericalCoordinates)} to 
     * add their implementation to the registry as well.
     * 
     */
	private static void registerTypes() {	    
	    idLookup = new Hashtable<>();
	    ids = new Hashtable<>();
	    fitsTypes = new Hashtable<>();

	    register(new SphericalCoordinates());
	    register(new HorizontalCoordinates());
	    register(new TelescopeCoordinates());
	    register(new FocalPlaneCoordinates());
	    register(new EquatorialCoordinates());
	    register(new EclipticCoordinates());
	    register(new GalacticCoordinates());
	    register(new SuperGalacticCoordinates());
	    register(new GeocentricCoordinates());
	    register(new GeodeticCoordinates());
	}
	
	/**
	 * Registers a new spherical coordinate implementation for mappings between 2-letter IDs,
	 * FITS header coordinate descriptions, and sherical coordinate implementations.
	 * 
	 * @param coords   An instance of the spherical coordinate implementation to add to the registry.
	 * 
	 * @see #getTwoLetterClass(String)
	 * @see #getFITSClass(String)
	 * @see #getTwoLetterIDFor(Class)
	 */
    protected static void register(SphericalCoordinates coords) {
        ids.put(coords.getTwoLetterID().toUpperCase(), coords.getClass());
        idLookup.put(coords.getClass(), coords.getTwoLetterID().toUpperCase());
        fitsTypes.put(coords.getFITSLongitudeStem().toUpperCase(), coords.getClass());
        fitsTypes.put(coords.getFITSLatitudeStem().toUpperCase(), coords.getClass());
    }
   
    
    /**
     * Creates a new local offset coordinate axis with the supplied parameters and the default formating (degrees to 3 decimals).
     * 
     * @param longLabel     a descriptive label, e.g. "Right Ascention (hours)".
     * @param shortLabel    an optional short label, such as "R.A. (h)", or <code>null</code>.
     * @param fancyLabel    an optional graphical label such as "&alpha;(h)", or <code>null</code>.
     * @return              a new coordinate axis instance with the specified parameters.
     */
    public static CoordinateAxis createOffsetAxis(String longLabel, String shortLabel, String fancyLabel) {
        return createAxis(longLabel, shortLabel, fancyLabel, arcsec, Util.f3);
    }
   
    
    /**
     * Creates a new spherical coordinate axis with the supplied parameters.
     * 
     * @param longLabel     a descriptive label, e.g. "Right Ascention (hours)".
     * @param shortLabel    an optional short label, such as "R.A. (h)", or <code>null</code>.
     * @param fancyLabel    an optional graphical label such as "&alpha;(h)", or <code>null</code>.
     * @param nf            the number formating that converts angles to strings on the new axis.
     * @return              a new coordinate axis instance with the specified parameters.
     */
    public static CoordinateAxis createAxis(String longLabel, String shortLabel, String fancyLabel, NumberFormat nf) {
        return createAxis(longLabel, shortLabel, fancyLabel, degree, nf);
    }
    
    /**
     * Creates a new spherical coordinate axis with the supplied parameters.
     * 
     * @param longLabel     a descriptive label, e.g. "Right Ascention (hours)".
     * @param shortLabel    an optional short label, such as "R.A. (h)", or <code>null</code>.
     * @param fancyLabel    an optional graphical label such as "&alpha;(h)", or <code>null</code>.
     * @param unit          the physical unit in which to print angles along this axis by default, or <code>null</code>
     *                      to use the default {@link #degree} unit. 
     * @param nf            the number formating that converts angles to strings on the new axis.
     * @return              a new coordinate axis instance with the specified parameters.
     */
    public static CoordinateAxis createAxis(String longLabel, String shortLabel, String fancyLabel, Unit unit, NumberFormat nf) {
        CoordinateAxis axis = new CoordinateAxis(longLabel, shortLabel, fancyLabel);
        axis.setUnit(unit == null ? degree : unit);
        if(nf != null) axis.setFormat(nf);
        return axis;
    }

    /**
     * Changes the number of decimal places to use with the default angle format {@link #af}.
     * 
     * @param decimals  the number of (sub-arcsecond) decimal places to use when converting angles to
     *                  strings in DDD:MM:SS.sss format with {@link #af}.
     */
    public static void setDefaultDecimals(int decimals) { af.setDecimals(decimals); }
    
    /**
     * Gets the number of decimal places used with the default angle format {@link #af}.
     * 
     * @return          the number of (sub-arcsecond) decimal places to use when converting angles to
     *                  strings in HH:MM:SS.sss format with {@link #af}.
     */
    public static int getDefaultDecimals() { return af.getDecimals(); }
    
    /**
     * Gets the standardized angle in the -&pi;:&pi; range for a given input angle of any value.
     * 
     * @param value     the input angle
     * @return          the same angle in the -&pi;:&pi; range.
     */
    public static double zeroToTwoPi(double value) {
        value = Math.IEEEremainder(value, Constant.twoPi);
        return value >= 0 ? value : value + Constant.twoPi;
    }
    
    /** The default unit for degrees */
    public static final Unit degree = Unit.get("deg");

    /** The default unit for arc-minutes */
    public static final Unit arcmin = Unit.get("arcmin");

    /** The default unit for arc-seconds */
    public static final Unit arcsec = Unit.get("arcsec");
    
    /** The default spherical coordinate system */
    public static CoordinateSystem defaultCoordinateSystem;
    
    /** The default planar local offset coordinate system */
    public static CoordinateSystem defaultLocalCoordinateSystem;

    /** The default format for angles in the DDD:MM:SS.ss style formating */
    protected static AngleFormat af = new AngleFormat(3);
    

    /** Static inutialized, which sets up the default coordinate system for spherical coordinates */
    static {
        defaultCoordinateSystem = new CoordinateSystem("Spherical");
        defaultLocalCoordinateSystem = new CoordinateSystem("Spherical Offsets");
        
        CoordinateAxis longitudeAxis = createAxis("Longitude", "LON", GreekLetter.phi + "", af);
        CoordinateAxis latitudeAxis = createAxis("Latitude", "LAT", GreekLetter.theta + "", af);
         
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Longitude Offset", "dLON", GreekLetter.Delta + " " + GreekLetter.phi + "");
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Latitude Offset", "dLAT", GreekLetter.delta + " " + GreekLetter.theta + "");
           
        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);           
    }
    
    /** (rad) The default precision to which two angles must agree with one another in their standardized forms
     * in order to be considered equal to one another.
     */
    public static final double angularAccuracy = 1e-12;

}

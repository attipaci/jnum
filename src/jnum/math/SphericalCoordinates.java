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

package jnum.math;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.astro.*;
import jnum.fits.FitsToolkit;
import jnum.projection.SphericalProjection;
import jnum.text.AngleFormat;
import jnum.text.GreekLetter;
import jnum.text.StringParser;
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
		
	
	/** The af. */
	protected static AngleFormat af = new AngleFormat(2);
	

	

	/**
	 * Sets the default decimals.
	 *
	 * @param decimals the new default decimals
	 */
	public static void setDefaultDecimals(int decimals) { af.setDecimals(decimals); }
	
	/**
	 * Gets the default decimals.
	 *
	 * @return the default decimals
	 */
	public static int getDefaultDecimals() { return af.getDecimals(); }
	
	/**
	 * Gets the FITS longitude stem.
	 *
	 * @return the FITS longitude stem
	 */
	public String getFITSLongitudeStem() { return "LON-"; }
	
	/**
	 * Gets the FITS latitude stem.
	 *
	 * @return the FITS latitude stem
	 */
	public String getFITSLatitudeStem() { return "LAT-"; }
	
	public String getTwoLetterCode() { return "SP"; }
	
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

	public final CoordinateAxis getLongitudeAxis() { return getCoordinateSystem().get(0); }
	
	public final CoordinateAxis getLatitudeAxis() { return getCoordinateSystem().get(1); }
	
	/**
	 * Gets the local coordinate system.
	 *
	 * @return the local coordinate system
	 */
	public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }
	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#copy(jnum.Coordinate2D)
	 */
	@Override
	public void copy(Coordinates<? extends Double> coords) {
		setX(coords.x());
		setY(coords.y());
	}
	
	@Override
    public SphericalCoordinates copy() { return (SphericalCoordinates) super.copy(); }
	
	
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
	 * @see jnum.Coordinate2D#addY(double)
	 */
	@Override
	public final void addY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}

	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#addY(double)
	 */
	@Override
	public final void subtractY(final double value) { 
		super.addY(Math.IEEEremainder(value, Math.PI));
		sinLat = Math.sin(y());
		cosLat = Math.cos(y());
	}
	
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#zero()
	 */
	@Override
	public void zero() { super.zero(); cosLat = 1.0; sinLat = 0.0; }

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#NaN()
	 */
	@Override
	public void NaN() { super.NaN(); cosLat = Double.NaN; sinLat = Double.NaN; }

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#set(double, double)
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
	 * @see jnum.Coordinate2D#toString()
	 */
	@Override
	public String toString() {
		CoordinateSystem coords = getCoordinateSystem();
		return coords.get(0).format(x()) + " " + coords.get(1).format(y());
	}
	
	/**
	 * To string.
	 *
	 * @param decimals the decimals
	 * @return the string
	 */
	public String toString(int decimals) {
		return Util.af[decimals].format(longitude()) + " " + Util.af[decimals].format(latitude());	
	}

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#toString(java.text.NumberFormat)
	 */
	@Override
	public String toString(NumberFormat nf) {
		return nf.format(longitude()) + " " + nf.format(latitude());		
	}

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#parse(java.lang.String)
	 */
	@Override
	public void parse(StringParser parser) throws NumberFormatException, IllegalArgumentException {
	    parser.skipWhiteSpaces();
	    
	    SphericalCoordinates parseCoords = null; 
	    
	    if(parser.nextIndexOf(Util.getWhiteSpaceChars() + "(") - parser.getIndex() == 2) {
	        String id = parser.getString().substring(parser.getIndex(), parser.getIndex() + 2);
	        Class<? extends SphericalCoordinates> parseClass = SphericalCoordinates.getTwoLetterClass(id);
	        
	        if(parseClass != null) {
	            parser.skip(3);
	            parser.skipWhiteSpaces();
	            
	            try { parseCoords = parseClass.newInstance(); }
	            catch(Exception e) { Util.warning(this, e); }
	        }
	    }
	    
	    if(parseCoords == null) parseCoords = copy();
	    
	    parseCoords.parseDirect(parser);

	    convertFrom(parseCoords);
	}
	
	protected void parseDirect(StringParser parser) throws NumberFormatException, IllegalArgumentException {
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
	
	
	/* (non-Javadoc)
	 * @see jnum.Metric#distanceTo(java.lang.Object)
	 */
	@Override
	public double distanceTo(SphericalCoordinates point) {
		double sindTheta = Math.sin(point.y() - y());
		double sindPhi = Math.sin(point.x() - x());
		return 2.0 * SafeMath.asin(Math.sqrt(sindTheta * sindTheta + cosLat * point.cosLat * sindPhi * sindPhi));
	}

	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#edit(nom.tam.util.Cursor, java.lang.String)
	 */
	@Override
	public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {	
		// Always write longitude in the 0:2Pi range.
		// Some FITS utilities may require it, even if it's not required by the FITS standard...
		double lon = Math.IEEEremainder(longitude(), Constant.twoPi);
		if(lon < 0.0) lon += Constant.twoPi;
		
		Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);

		c.add(new HeaderCard(keyStem + "1" + alt, lon / Unit.deg, "The reference longitude coordinate (deg)."));
		c.add(new HeaderCard(keyStem + "2" + alt, latitude() / Unit.deg, "The reference latitude coordinate (deg)."));
		
		//cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
		if(alt.length() == 0) c.add(new HeaderCard("WCSAXES", 2, "Number of celestial coordinate axes."));
	}
		
	/* (non-Javadoc)
	 * @see jnum.Coordinate2D#parse(nom.tam.fits.Header, java.lang.String)
	 */
	@Override
	public void parseHeader(Header header, String keyStem, String alt, Coordinate2D defaultValue) {
		setLongitude(header.getDoubleValue(keyStem + "1" + alt, defaultValue == null ? 0.0 : defaultValue.x()) * Unit.deg);
		setLatitude(header.getDoubleValue(keyStem + "2" + alt, defaultValue == null ? 0.0 : defaultValue.x()) * Unit.deg);
		
		
		//String name = header.getStringValue("WCSNAME");
		//if(name != null) getCoordinateSystem().name = name;
	}
	
	/* (non-Javadoc)
     * @see jnum.math.Invertible#invert()
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
	    if(fitsTypes == null) registerTypes();
	    
	    if(spec.length() > 4) spec = spec.substring(0, 4);
	    if(spec.length() < 4) {
	        StringBuffer buf = new StringBuffer(4);
	        buf.append(spec);
	        while(buf.length() < 4) buf.append('-');
	        spec = new String(buf);
	    }
	  
		Class<? extends SphericalCoordinates> coordClass = fitsTypes.get(spec.toUpperCase());
		if(coordClass == null) throw new IllegalArgumentException("Unknown Coordinate Definition " + spec);
		return coordClass;
	}

	public static Class<? extends SphericalCoordinates> getTwoLetterClass(String id) {
	    if(ids == null) registerTypes();
	    Class<? extends SphericalCoordinates> coordClass = ids.get(id.toUpperCase());
        if(coordClass == null) throw new IllegalArgumentException("Unknown Coordinate Definition " + id);
        return coordClass;
	}
	
	public static String getTwoLetterCodeFor(Class<? extends Coordinate2D> coordinateClass) {
	    if(idLookup == null) registerTypes();
	    return idLookup.get(coordinateClass);
	}
	
	 
    private static Hashtable<Class<? extends SphericalCoordinates>, String> idLookup;
    private static Hashtable<String, Class<? extends SphericalCoordinates>> ids;
    private static Hashtable<String, Class<? extends SphericalCoordinates>> fitsTypes;
	
	static void registerTypes() {
	    
	    idLookup = new Hashtable<Class<? extends SphericalCoordinates>, String>();
	    ids = new Hashtable<String, Class<? extends SphericalCoordinates>>();
	    fitsTypes = new Hashtable<String, Class<? extends SphericalCoordinates>>();

	    
	    register(new SphericalCoordinates());
	    register(new HorizontalCoordinates());
	    register(new TelescopeCoordinates());
	    register(new FocalPlaneCoordinates());
	    register(new EquatorialCoordinates());
	    register(new EclipticCoordinates());
	    register(new GalacticCoordinates());
	    register(new SuperGalacticCoordinates());
	}
	
	
	   
    public static void register(SphericalCoordinates coords) {
        ids.put(coords.getTwoLetterCode().toUpperCase(), coords.getClass());
        idLookup.put(coords.getClass(), coords.getTwoLetterCode().toUpperCase());
        fitsTypes.put(coords.getFITSLongitudeStem().toUpperCase(), coords.getClass());
        fitsTypes.put(coords.getFITSLatitudeStem().toUpperCase(), coords.getClass());
    }
    
    public static CoordinateAxis createAxis(String longLabel, String shortLabel, String fancyLabel, NumberFormat nf) {
        return createAxis(longLabel, shortLabel, fancyLabel, degree, nf);
    }
    
    public static CoordinateAxis createOffsetAxis(String longLabel, String shortLabel, String fancyLabel) {
        return createAxis(longLabel, shortLabel, fancyLabel, arcsec, Util.f3);
    }
    
    public static CoordinateAxis createAxis(String longLabel, String shortLabel, String fancyLabel, Unit unit, NumberFormat nf) {
        CoordinateAxis axis = new CoordinateAxis(longLabel, shortLabel, fancyLabel);
        axis.setUnit(unit == null ? degree : unit);
        if(nf != null) axis.setFormat(nf);
        return axis;
    }

    /** The Constant degree. */
    public final static Unit degree = Unit.get("deg");
    
    /** The Constant arcmin. */
    public final static Unit arcmin = Unit.get("arcmin");
    
    /** The Constant arcsec. */
    public final static Unit arcsec = Unit.get("arcsec");

    /** The default local coordinate system. */
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    
    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Spherical Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Spherical Offsets");
        
        CoordinateAxis longitudeAxis = createAxis("Latitude", "LAT", GreekLetter.phi + "", af);
        CoordinateAxis latitudeAxis = createAxis("Longitude", "LON", GreekLetter.theta + "", af);
         
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Longitude Offset", "dLON", GreekLetter.Delta + " " + GreekLetter.phi + "");
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Latitude Offset", "dLAT", GreekLetter.delta + " " + GreekLetter.theta + "");
           
        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);           
    }
    
  

    
    /** The Constant angularAccuracy. */
    public final static double angularAccuracy = 1e-12;


	
}

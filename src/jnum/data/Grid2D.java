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
package jnum.data;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.astro.EclipticCoordinates;
import jnum.astro.EquatorialCoordinates;
import jnum.astro.GalacticCoordinates;
import jnum.astro.HorizontalCoordinates;
import jnum.astro.SuperGalacticCoordinates;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;
import jnum.util.Copiable;
import jnum.util.HashCode;
import jnum.util.Unit;
import jnum.util.Util;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class Grid2D.
 *
 * @param <CoordinateType> the generic type
 */
public abstract class Grid2D<CoordinateType extends Coordinate2D> implements Serializable, Cloneable, Copiable<Grid2D<CoordinateType>> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8109608722575396734L;

	/** The projection. */
	private Projection2D<CoordinateType> projection;
		
	/** The ref index. */
	public Vector2D refIndex = new Vector2D();
	
	// These are transformation matrix elements to native offsets
	/** The i22. */
	private double m11, m12, m21, m22, i11, i12, i21, i22;
	
	/** The coordinate system. */
	private CoordinateSystem coordinateSystem;
	
	/** The alt. */
	protected String alt = ""; // The FITS alternative coordinate system specifier... 

	
	/** The preferred units (for storing in FITS). */
	public Unit xUnit, yUnit;
	
	
	public Grid2D() {
		defaults();
	}
	
	protected void defaults() {
		xUnit = getDefaultFITSAxisUnit();
		yUnit = getDefaultFITSAxisUnit();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return equals(o, 1e-8);
	}
	
	/**
	 * Equals.
	 *
	 * @param o the o
	 * @param precision the precision
	 * @return true, if successful
	 */
	public boolean equals(Object o, double precision) {
		if(!(o instanceof Grid2D)) return false;
		Grid2D<?> grid = (Grid2D<?>) o;
		
		if(!grid.projection.equals(projection)) return false;
		if(!grid.refIndex.equals(refIndex, precision)) return false;
		
		if(!Util.equals(m11, grid.m11, precision)) return false;
		if(!Util.equals(m12, grid.m12, precision)) return false;
		if(!Util.equals(m21, grid.m21, precision)) return false;
		if(!Util.equals(m22, grid.m22, precision)) return false;
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return projection.hashCode() ^ 
			~HashCode.get(m11) ^ HashCode.get(m22) ^ HashCode.get(m12) ^ ~HashCode.get(m21) ^
			refIndex.hashCode();
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Copy.
	 *
	 * @return the grid2 d
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Grid2D<CoordinateType> copy() {
		Grid2D<CoordinateType> copy = (Grid2D<CoordinateType>) clone();
		copy.projection = projection.copy();
		copy.refIndex = (Vector2D) refIndex.clone();
		copy.alt = new String(alt);
		return copy;
	}
	
	
	/**
	 * Gets the coordinate system.
	 *
	 * @return the coordinate system
	 */
	public CoordinateSystem getCoordinateSystem() { return coordinateSystem; }
	
	/**
	 * Sets the coordinate system.
	 *
	 * @param system the new coordinate system
	 */
	public void setCoordinateSystem(CoordinateSystem system) { coordinateSystem = system; }
	
	/**
	 * Gets the pixel area.
	 *
	 * @return the pixel area
	 */
	public double getPixelArea() { return Math.abs(m11 * m22 - m12 * m21); }
	
	/**
	 * Sets the resolution.
	 *
	 * @param delta the new resolution
	 */
	public void setResolution(double delta) {
		setResolution(delta, delta);
	}
	
	/**
	 * Gets the transform.
	 *
	 * @return the transform
	 */
	public final double[][] getTransform() {
		return new double[][] {{ m11, m12 }, { m21, m22 }};
	}
	
	public final double[][] getInverseTransform() {
		return new double[][] {{ i11, i12 }, { i21, i22 }};
	}
	
	public boolean isRectilinear() {
		return m12 == 0.0 && m21 == 0.0;
	}
	
	/**
	 * Sets the transform.
	 *
	 * @param M the new transform
	 */
	public void setTransform(double[][] M) {
		if(M.length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");
		if(M[0].length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");
		
		m11 = M[0][0];
		m12 = M[0][1];
		m21 = M[1][0];
		m22 = M[1][1];
		calcInverseTransform();
	}
	
	/**
	 * Sets the resolution.
	 *
	 * @param dx the dx
	 * @param dy the dy
	 */
	public void setResolution(double dx, double dy) {
		m11 = dx;
		m22 = dy;
		m21 = m12 = 0.0;
		calcInverseTransform();
	}
	
	/**
	 * Checks if is horizontal.
	 *
	 * @return true, if is horizontal
	 */
	public boolean isHorizontal() {
		return getReference() instanceof HorizontalCoordinates;
	}
	
	/**
	 * Checks if is equatorial.
	 *
	 * @return true, if is equatorial
	 */
	public boolean isEquatorial() {
		return getReference() instanceof EquatorialCoordinates;
	}
	
	/**
	 * Checks if is ecliptic.
	 *
	 * @return true, if is ecliptic
	 */
	public boolean isEcliptic() {
		return getReference() instanceof EclipticCoordinates;
	}
	
	/**
	 * Checks if is galactic.
	 *
	 * @return true, if is galactic
	 */
	public boolean isGalactic() {
		return getReference() instanceof GalacticCoordinates;
	}
	
	/**
	 * Checks if is super galactic.
	 *
	 * @return true, if is super galactic
	 */
	public boolean isSuperGalactic() {
		return getReference() instanceof SuperGalacticCoordinates;
	}
	
	/**
	 * Gets the local affine transform.
	 *
	 * @return the local affine transform
	 */
	public AffineTransform getLocalAffineTransform() {
		final double dx = m11 * refIndex.x() + m12 * refIndex.y();
		final double dy = m22 * refIndex.y() + m21 * refIndex.x();
		return new AffineTransform(m11, m12, m21, m22, -dx, -dy);
	}
	
	/**
	 * Gets the resolution.
	 *
	 * @return the resolution
	 */
	public Vector2D getResolution() {
		return new Vector2D(m11, m22);
	}
	
	/**
	 * Pixel size x.
	 *
	 * @return the double
	 */
	public double pixelSizeX() { return Math.abs(m11); }
	
	/**
	 * Pixel size y.
	 *
	 * @return the double
	 */
	public double pixelSizeY() { return Math.abs(m22); }
	
	/**
	 * Calc inverse transform.
	 */
	public void calcInverseTransform() {
		final double idet = 1.0 / getPixelArea();
		i11 = m22 * idet;
		i12 = -m12 * idet;
		i21 = -m21 * idet;
		i22 = m11 * idet;
	}
	
	// Generalize to non-square pixels...
	/**
	 * Rotate.
	 *
	 * @param angle the angle
	 */
	public void rotate(double angle) {
		if(angle == 0.0) return;
		
		final double c = Math.cos(angle);
		final double s = Math.sin(angle);
		final double a11 = m11, a12 = m12;
		final double a21 = m21, a22 = m22;
		
		m11 = c * a11 - s * a21;
		m12 = c * a12 - s * a22;
		m21 = s * a11 + c * a21;
		m22 = s * a12 + c * a22;
		
		calcInverseTransform();

	}
	
	/**
	 * Checks if is reverse x.
	 *
	 * @return true, if is reverse x
	 */
	public boolean isReverseX() { return false; }
	
	/**
	 * Checks if is reverse y.
	 *
	 * @return true, if is reverse y
	 */
	public boolean isReverseY() { return false; }
	
	
	/**
	 * Edits the header.
	 *
	 * @param cursor the cursor
	 * @throws HeaderCardException the header card exception
	 */
	public void editHeader(Header header, Cursor<String, HeaderCard> cursor) throws HeaderCardException {		
		// TODO 
		projection.edit(cursor, alt);
		projection.getReference().edit(cursor, alt);
		
		if(xUnit != Unit.unity) cursor.add(new HeaderCard("CUNIT1" + alt, xUnit.name(), "Coordinate axis unit."));
		if(yUnit != Unit.unity) cursor.add(new HeaderCard("CUNIT2" + alt, yUnit.name(), "Coordinate axis unit."));
		
		cursor.add(new HeaderCard("CRPIX1" + alt, refIndex.x() + 1, "Reference grid position"));
		cursor.add(new HeaderCard("CRPIX2" + alt, refIndex.y() + 1, "Reference grid position"));

		// Change from native to apparent for reverted axes.
		double a11 = m11, a12 = m12, a21 = m21, a22 = m22;
		if(isReverseX()) { a11 *= -1.0; a21 *= -1.0; }
		if(isReverseY()) { a22 *= -1.0; a12 *= -1.0; }
						
		if(m12 == 0.0 && m21 == 0.0) {	
			cursor.add(new HeaderCard("CDELT1" + alt, a11/xUnit.value(), "Grid spacing (deg)"));	
			cursor.add(new HeaderCard("CDELT2" + alt, a22/yUnit.value(), "Grid spacing (deg)"));		
		}
		else {		
			cursor.add(new HeaderCard("CD1_1" + alt, a11 / xUnit.value(), "Transformation matrix element"));
			cursor.add(new HeaderCard("CD1_2" + alt, a12 / xUnit.value(), "Transformation matrix element"));
			cursor.add(new HeaderCard("CD2_1" + alt, a21 / yUnit.value(), "Transformation matrix element"));
			cursor.add(new HeaderCard("CD2_2" + alt, a22 / yUnit.value(), "Transformation matrix element"));
		}
		
		
	}
	
	/**
	 * Parses the projection.
	 *
	 * @param header the header
	 * @throws HeaderCardException the header card exception
	 */
	public abstract void parseProjection(Header header) throws HeaderCardException;
		
	/**
	 * Gets the coordinate instance for.
	 *
	 * @param type the type
	 * @return the coordinate instance for
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public abstract CoordinateType getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException;
	
	public Unit getDefaultFITSAxisUnit() { return Unit.unity; }
	
	/**
	 * Parses the header.
	 *
	 * @param header the header
	 * @throws HeaderCardException the header card exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public void parseHeader(Header header) throws HeaderCardException, InstantiationException, IllegalAccessException {
		String type = header.getStringValue("CTYPE1" + alt);
	
		try { parseProjection(header); }
		catch(Exception e) { System.err.println("ERROR! Unknown projection " + type.substring(5, 8)); }
		
		CoordinateType reference = null;
		
		reference = getCoordinateInstanceFor(type);

		reference.parse(header, alt);
		setReference(reference);
		
		// Internally keep the transformation matrix unitary 
		// And have delta carry the pixel sizes...
		xUnit = header.containsKey("CUNIT1" + alt) ? Unit.get(header.getStringValue("CUNIT1" + alt)) : getDefaultFITSAxisUnit();
		yUnit = header.containsKey("CUNIT2" + alt) ? Unit.get(header.getStringValue("CUNIT2" + alt)) : getDefaultFITSAxisUnit();
		
		if(header.containsKey("CD1_1" + alt) || header.containsKey("CD1_2" + alt) || header.containsKey("CD2_1" + alt) || header.containsKey("CD2_2" + alt)) {
			// When the CDi_j keys are used the scaling is incorporated into the CDi_j values.
			// Thus, the deltas are implicitly assumed to be 1...	
			m11 = header.getDoubleValue("CD1_1" + alt, 1.0) * xUnit.value();
			m12 = header.getDoubleValue("CD1_2" + alt, 0.0) * xUnit.value();
			m21 = header.getDoubleValue("CD2_1" + alt, 0.0) * yUnit.value();
			m22 = header.getDoubleValue("CD2_2" + alt, 1.0) * yUnit.value();	
		}	
		else {
			// Otherwise, the scaling is set by CDELTi keys...
			double dx = header.getDoubleValue("CDELT1" + alt, 1.0) * xUnit.value();
			double dy = header.getDoubleValue("CDELT2" + alt, 1.0) * yUnit.value();
			
			// And the transformation is set by the PCi_j keys
			// Transform then scale...
			m11 = dx * header.getDoubleValue("PC1_1" + alt, 1.0);
			m12 = dx * header.getDoubleValue("PC1_2" + alt, 0.0);
			m21 = dy * header.getDoubleValue("PC2_1" + alt, 0.0);
			m22 = dy * header.getDoubleValue("PC2_2" + alt, 1.0);
			
			// Or the rotation of the latitude axis is set via CROTAi...
			if(header.containsKey("CROTA2" + alt)) {
				rotate(header.getDoubleValue("CROTA2" + alt) * Unit.deg);
			}		
		}	
			
		// Change from apparent to native for reverted axes...
		if(isReverseX()) { m11 *= -1.0; m21 *= -1.0; }
		if(isReverseY()) { m22 *= -1.0; m12 *= -1.0; }
		
		refIndex.setX(header.getDoubleValue("CRPIX1" + alt) - 1.0);
		refIndex.setY(header.getDoubleValue("CRPIX2" + alt) - 1.0);
		
		calcInverseTransform();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return toString(Util.s3, xUnit, yUnit);
	}
	
	public String toString(NumberFormat nf) {
		return toString(nf, xUnit, yUnit);
	}
	
	public String toString(Unit u) {
		return toString(Util.s3, u, u);
	}
	
	public String toString(NumberFormat nf, Unit ux, Unit uy) {	
		CoordinateType reference = projection.getReference();
		String projectionName = reference.getClass().getSimpleName();
		projectionName = projectionName.substring(0, projectionName.length() - "Coordinates".length());
		
		String xName = ux == Unit.unity ? "" : " " + ux.name();
		String yName = uy == Unit.unity ? "" : " " + uy.name();
 		
		String info =
			"  " + projectionName + ": " + reference.toString() + "\n" +
			"  Projection: " + projection.getFullName() + " (" + projection.getFitsID() + ")\n" + 
			"  Grid Spacing: " + (ux == uy ? 
					nf.format(m11 / ux.value()) + " x " + nf.format(m22 / uy.value()) + xName :
					nf.format(m11 / ux.value()) +  xName + " x " + nf.format(m22 / uy.value()) + yName
					) + ".\n" +
			"  Reference Pixel: " + refIndex.toString(nf) + " C-style, 0-based\n";			
		
		return info;
	}

	
	/**
	 * To index.
	 *
	 * @param offset the offset
	 */
	public final void toIndex(final Vector2D offset) {
		offsetToIndex(offset, offset);
	}
	
	/**
	 * Offset to index.
	 *
	 * @param offset the offset
	 * @param index the index
	 */
	public final void offsetToIndex(final Vector2D offset, final Vector2D index) {
		index.set(
				i11 * offset.x() + i12 * offset.y() + refIndex.x(),
				i21 * offset.x() + i22 * offset.y() + refIndex.y()
		);
	}
	
	/**
	 * To offset.
	 *
	 * @param index the index
	 */
	public final void toOffset(final Vector2D index) {
		indexToOffset(index, index);
	}
	
	/**
	 * Index to offset.
	 *
	 * @param index the index
	 * @param offset the offset
	 */
	public final void indexToOffset(final Vector2D index, final Vector2D offset) {		
		final double di = index.x() - refIndex.x();
		final double dj = index.y() - refIndex.y();		
		offset.set(m11 * di + m12 * dj, m21 * di + m22 * dj);
	}
	
    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public final CoordinateType getReference() { return projection.getReference(); }
    
    /**
     * Sets the reference.
     *
     * @param reference the new reference
     */
    public void setReference(CoordinateType reference) { projection.setReference(reference); }
    
    /**
     * Gets the reference index.
     *
     * @return the reference index
     */
    public Vector2D getReferenceIndex() { return refIndex; }
    
    /**
     * Sets the reference index.
     *
     * @param v the new reference index
     */
    public void setReferenceIndex(Vector2D v) { refIndex = v; }
    
    /**
     * Gets the projection.
     *
     * @return the projection
     */
    public final Projection2D<CoordinateType> getProjection() { return projection; }
    
    /**
     * Sets the projection.
     *
     * @param p the new projection
     */
    public void setProjection(Projection2D<CoordinateType> p) { this.projection = p; }
    
    /**
     * Gets the fITS alt.
     *
     * @return the fITS alt
     */
    public final String getFITSAlt() { return alt; }
    
    /**
     * Sets the fITS alt.
     *
     * @param ver the new fITS alt
     */
    public final void setFITSAlt(String ver) { this.alt = ver; }
    
    /**
     * Gets the index.
     *
     * @param coords the coords
     * @param index the index
     * @return the index
     */
    public final void getIndex(final CoordinateType coords, final Vector2D index) {
    	projection.project(coords, index);
    	toIndex(index);
    }
   
    /**
     * Gets the coords.
     *
     * @param index the index
     * @param coords the coords
     * @return the coords
     */
    public final void getCoords(final Vector2D index, final CoordinateType coords) {
    	final double i = index.x();
    	final double j = index.y(); 
    	
    	toOffset(index);
    	projection.deproject(index, coords);
    	
    	index.setX(i);
    	index.setY(j);
    }
    
    /**
     * Toggle native.
     *
     * @param offset the offset
     */
    public void toggleNative(final Vector2D offset) {
    	if(isReverseX()) offset.scaleX(-1.0);
    	if(isReverseY()) offset.scaleY(-1.0);
    }
    
    
    /**
     * Shift.
     *
     * @param offset the offset
     */
    public void shift(final Vector2D offset) {
    	toIndex(offset);
    	refIndex.add(offset);
    }
    
    /**
     * Gets the grid2 d for.
     *
     * @param header the header
     * @param alt the alt
     * @return the Grid2D for
     * @throws HeaderCardException the header card exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public static Grid2D<?> fromHeader(Header header, String alt) throws HeaderCardException, InstantiationException, IllegalAccessException {
    	Grid2D<?> grid = getGrid2DInstanceFor(header.getStringValue("CTYPE1" + alt), header.getStringValue("CTYPE2" + alt));  
    	grid.parseHeader(header);
    	return grid;
    }
    
    	
    /**
     * Gets the grid2 d instance for.
     *
     * @param type1 the type1
     * @param type2 the type2
     * @return the grid2 d instance for
     */
    private static Grid2D<?> getGrid2DInstanceFor(String type1, String type2) {
    	// Check if the types conform to projected WCS coordinates, according to Calabretta & Greisen (2002)
    	// If so, return a SphericalGrid, else a regular Cartesian grid.
    	if(type1 == null || type2 == null) return new CartesianGrid2D();
    	if(type1.length() < 6 || type2.length() < 6) return new CartesianGrid2D();
    
    	StringTokenizer tokens1 = new StringTokenizer(type1.toLowerCase(), "-");    	
    	if(tokens1.countTokens() != 2) return new CartesianGrid2D(); 
    	
    	StringTokenizer tokens2 = new StringTokenizer(type2.toLowerCase(), "-");
    	if(tokens2.countTokens() != 2) return new CartesianGrid2D(); 
    		
    	String xtype = tokens1.nextToken();
    	String px = tokens1.nextToken();
    	
    	String ytype = tokens2.nextToken();
    	String py = tokens2.nextToken();
    		
    	if(!px.equals(py)) return new CartesianGrid2D();
    	
    	if(xtype.equals("ra") && ytype.equals("dec")) return new SphericalGrid();
    	if(xtype.equals("dec") && ytype.equals("ra")) return new SphericalGrid();
    	if(xtype.equals("lon") && ytype.equals("lat")) return new SphericalGrid();
    	if(xtype.equals("lat") && ytype.equals("lon")) return new SphericalGrid();
    	
    	if(xtype.charAt(0) != ytype.charAt(0)) return new CartesianGrid2D();
    	
    	xtype = xtype.substring(1);
    	ytype = ytype.substring(1);
    	if(xtype.equals("lon") && ytype.equals("lat")) return new SphericalGrid();
    	if(xtype.equals("lat") && ytype.equals("lon")) return new SphericalGrid();
   
    	if(xtype.charAt(0) != ytype.charAt(0)) return new CartesianGrid2D();
    	
    	xtype = xtype.substring(1);
    	ytype = ytype.substring(1);
    	if(xtype.equals("ln") && ytype.equals("lt")) return new SphericalGrid();
    	if(xtype.equals("lt") && ytype.equals("ln")) return new SphericalGrid();
    	
    	return new CartesianGrid2D();
    }

    
}

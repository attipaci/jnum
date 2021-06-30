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
package jnum.data.image;

import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import jnum.Copiable;
import jnum.Unit;
import jnum.Util;
import jnum.astro.EclipticCoordinates;
import jnum.astro.EquatorialCoordinates;
import jnum.astro.GalacticCoordinates;
import jnum.astro.HorizontalCoordinates;
import jnum.astro.SuperGalacticCoordinates;
import jnum.data.FastGridAccess;
import jnum.data.Grid;
import jnum.fits.FitsToolkit;
import jnum.math.Coordinate2D;
import jnum.math.CoordinateAxis;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public abstract class Grid2D<CoordinateType extends Coordinate2D> extends Grid<CoordinateType, Vector2D> 
implements FastGridAccess<CoordinateType, Vector2D>, Copiable<Grid2D<CoordinateType>> {

    private static final long serialVersionUID = 8109608722575396734L;

    private Projection2D<CoordinateType> projection;

    private Vector2D refIndex = new Vector2D();

    // These are transformation matrix elements to native offsets
    /** The i22. */
    private double m11, m12, m21, m22, i11, i12, i21, i22;


    public Grid2D() {
        defaults();
    }

    protected void defaults() {
        m11 = m22 = i11 = i22 = 1.0;
        m12 = m21 = i12 = i21 = 0.0; 
        refIndex.zero();
    }


    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Grid2D)) return false;

        Grid2D<?> grid = (Grid2D<?>) o;

        if(!Util.equals(grid.projection, projection)) return false;
        if(!Util.equals(grid.refIndex, refIndex)) return false;

        if(!Util.equals(m11, grid.m11)) return false;
        if(!Util.equals(m12, grid.m12)) return false;
        if(!Util.equals(m21, grid.m21)) return false;
        if(!Util.equals(m22, grid.m22)) return false;

        return true;
    }


    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ HashCode.from(m11) ^ HashCode.from(m22) ^ HashCode.from(m12) ^ HashCode.from(m21);		
        if(projection != null) hash ^= projection.hashCode();
        if(refIndex != null) hash ^= refIndex.hashCode();
        return hash;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Grid2D<CoordinateType> clone() {
        try { return (Grid2D<CoordinateType>) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }


    @Override
    public Grid2D<CoordinateType> copy() {
        Grid2D<CoordinateType> copy = clone();
        copy.projection = projection.copy();
        copy.refIndex = refIndex.copy();
        return copy;
    }


    public Grid2D<CoordinateType> forResolution(double resolution) {
        return forResolution(new Vector2D(resolution, resolution));
    }

    public Grid2D<CoordinateType> forResolution(Vector2D resolution) {
        Grid2D<CoordinateType> grid = copy();
        grid.setResolution(resolution);
        grid.refIndex.scaleX(getResolution().x() / resolution.x());
        grid.refIndex.scaleY(getResolution().y() / resolution.y());
        return grid;
    }


    public double getPixelArea() { return Math.abs(m11 * m22 - m12 * m21); }


    public final void setResolution(double delta) {
        setResolution(delta, delta);
    }


    @Override
    public final void setResolution(Vector2D delta) { setResolution(delta.x(), delta.y()); }


    public final double[][] getTransform() {
        return new double[][] {{ m11, m12 }, { m21, m22 }};
    }


    public final double[][] getInverseTransform() {
        return new double[][] {{ i11, i12 }, { i21, i22 }};
    }


    public boolean isRectilinear() {
        return m12 == 0.0 && m21 == 0.0;
    }


    public void setTransform(float[][] M) {
        if(M.length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");
        if(M[0].length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");

        m11 = M[0][0];
        m12 = M[0][1];
        m21 = M[1][0];
        m22 = M[1][1];
        calcInverseTransform();
    }


    public void setTransform(double[][] M) {
        if(M.length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");
        if(M[0].length != 2) throw new IllegalArgumentException("Coordinate transform should a 2x2 matrix.");

        m11 = M[0][0];
        m12 = M[0][1];
        m21 = M[1][0];
        m22 = M[1][1];
        calcInverseTransform();
    }


    public void setResolution(double dx, double dy) {
        m11 = dx;
        m22 = dy;
        m21 = m12 = 0.0;
        calcInverseTransform();
    }


    public boolean isHorizontal() {
        return getReference() instanceof HorizontalCoordinates;
    }


    public boolean isEquatorial() {
        return getReference() instanceof EquatorialCoordinates;
    }


    public boolean isEcliptic() {
        return getReference() instanceof EclipticCoordinates;
    }


    public boolean isGalactic() {
        return getReference() instanceof GalacticCoordinates;
    }


    public boolean isSuperGalactic() {
        return getReference() instanceof SuperGalacticCoordinates;
    }


    public AffineTransform getLocalAffineTransform() {
        final double dx = m11 * refIndex.x() + m12 * refIndex.y();
        final double dy = m22 * refIndex.y() + m21 * refIndex.x();
        return new AffineTransform(m11, m12, m21, m22, -dx, -dy);
    }


    @Override
    public Vector2D getResolution() {
        return new Vector2D(m11, m22);
    }


    public double pixelSizeX() { return Math.abs(m11); }


    public double pixelSizeY() { return Math.abs(m22); }


    public void calcInverseTransform() {
        final double idet = 1.0 / getPixelArea();
        i11 = m22 * idet;
        i12 = -m12 * idet;
        i21 = -m21 * idet;
        i22 = m11 * idet;
    }

    // Generalize to non-square pixels...
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


    public boolean isReverseX() { return false; }


    public boolean isReverseY() { return false; }


    public Unit fitsXUnit() { return xAxis().getUnit(); }

    public Unit fitsYUnit() { return yAxis().getUnit(); }


    @Override
    public void editHeader(Header header) throws HeaderCardException {	
        String alt = getFitsID();

        // TODO 
        projection.editHeader(header, alt);

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);

        c.add(new HeaderCard("CRPIX1" + alt, refIndex.x() + 1, "Reference grid index"));
        c.add(new HeaderCard("CRPIX2" + alt, refIndex.y() + 1, "Reference grid index"));

        projection.getReference().editHeader(header, "CRVAL", alt);	

        // Change from native to apparent for reverted axes.
        double a11 = m11, a12 = m12, a21 = m21, a22 = m22;
        if(isReverseX()) { a11 *= -1.0; a21 *= -1.0; }
        if(isReverseY()) { a22 *= -1.0; a12 *= -1.0; }

        if(m12 == 0.0 && m21 == 0.0) {	
            c.add(new HeaderCard("CDELT1" + alt, a11 / fitsXUnit().value(), "Grid spacing (" + fitsXUnit().name() + ")"));	
            c.add(new HeaderCard("CDELT2" + alt, a22 / fitsYUnit().value(), "Grid spacing (" + fitsYUnit().name() + ")"));		
        }
        else {		
            c.add(new HeaderCard("CD1_1" + alt, a11 / fitsXUnit().value(), "Transformation matrix element"));
            c.add(new HeaderCard("CD1_2" + alt, a12 / fitsXUnit().value(), "Transformation matrix element"));
            c.add(new HeaderCard("CD2_1" + alt, a21 / fitsYUnit().value(), "Transformation matrix element"));
            c.add(new HeaderCard("CD2_2" + alt, a22 / fitsYUnit().value(), "Transformation matrix element"));
        }

        if(fitsXUnit() != Unit.unity) c.add(new HeaderCard("CUNIT1" + alt, fitsXUnit().name(), "Coordinate axis unit."));
        if(fitsYUnit() != Unit.unity) c.add(new HeaderCard("CUNIT2" + alt, fitsYUnit().name(), "Coordinate axis unit."));


    }


    public abstract void parseProjection(Header header) throws HeaderCardException;


    public abstract CoordinateType getCoordinateInstanceFor(String type) throws Exception;

    public CoordinateAxis xAxis() { return getCoordinateSystem().get(0); }

    public CoordinateAxis yAxis() { return getCoordinateSystem().get(1); }

    public Unit getDefaultUnit() { return Unit.unity; }


    @Override
    public void parseHeader(Header header) throws Exception {
        String alt = getFitsID();

        String type = header.getStringValue("CTYPE1" + alt);

        try { parseProjection(header); }
        catch(Exception e) { Util.error(this, "Unknown projection " + type.substring(5, 8)); }


        // Internally keep the transformation matrix unitary 
        // And have delta carry the pixel sizes...
        xAxis().setUnit(header.containsKey("CUNIT1" + alt) ? Unit.get(header.getStringValue("CUNIT1" + alt)) : getDefaultUnit());
        yAxis().setUnit(header.containsKey("CUNIT2" + alt) ? Unit.get(header.getStringValue("CUNIT2" + alt)) : getDefaultUnit());

        if(header.containsKey("CD1_1" + alt) || header.containsKey("CD1_2" + alt) || header.containsKey("CD2_1" + alt) || header.containsKey("CD2_2" + alt)) {
            // When the CDi_j keys are used the scaling is incorporated into the CDi_j values.
            // Thus, the deltas are implicitly assumed to be 1...	
            m11 = header.getDoubleValue("CD1_1" + alt, 1.0) * fitsXUnit().value();
            m12 = header.getDoubleValue("CD1_2" + alt, 0.0) * fitsXUnit().value();
            m21 = header.getDoubleValue("CD2_1" + alt, 0.0) * fitsYUnit().value();
            m22 = header.getDoubleValue("CD2_2" + alt, 1.0) * fitsYUnit().value();	
        }	
        else {
            // Otherwise, the scaling is set by CDELTi keys...
            double dx = header.getDoubleValue("CDELT1" + alt, 1.0) * fitsXUnit().value();
            double dy = header.getDoubleValue("CDELT2" + alt, 1.0) * fitsYUnit().value();

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

        Vector2D one = new Vector2D(1.0, 1.0);
        refIndex.parseHeader(header, "CRPIX", "", one);
        refIndex.subtract(one);

        CoordinateType reference= getCoordinateInstanceFor(type);

        reference.parseHeader(header, "CRVAL", alt, new Coordinate2D(0.0, 0.0));
        setReference(reference);

        // Change from apparent to native for reverted axes...
        if(isReverseX()) { m11 *= -1.0; m21 *= -1.0; }
        if(isReverseY()) { m22 *= -1.0; m12 *= -1.0; }

        calcInverseTransform();
    }


    @Override
    public String toString() {	
        return toString(Util.s3, xAxis().getUnit(), yAxis().getUnit());
    }


    public String toString(NumberFormat nf) {
        return toString(nf, xAxis().getUnit(), yAxis().getUnit());
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
                projectionName + ": " + reference + "\n" +
                        "Projection: " + projection.getFullName() + " (" + projection.getFitsID() + ")\n" + 
                        "Grid Spacing: " + (ux == uy ? 
                                nf.format(m11 / ux.value()) + " x " + nf.format(m22 / uy.value()) + xName :
                                    nf.format(m11 / ux.value()) +  xName + " x " + nf.format(m22 / uy.value()) + yName
                                ) + ".\n" +
                                "Reference Pixel: " + refIndex.toString(nf) + " C-style, 0-based\n";			

        return info;
    }


    public final void toIndex(final Vector2D offset) {
        offsetToIndex(offset, offset);
    }


    public final void offsetToIndex(final Vector2D offset, final Vector2D index) {
        index.set(
                i11 * offset.x() + i12 * offset.y() + refIndex.x(),
                i21 * offset.x() + i22 * offset.y() + refIndex.y()
                );
    }

    
    public final void toOffset(final Vector2D index) {
        indexToOffset(index, index);
    }


    public final void indexToOffset(final Vector2D index, final Vector2D offset) {		
        final double di = index.x() - refIndex.x();
        final double dj = index.y() - refIndex.y();		
        offset.set(m11 * di + m12 * dj, m21 * di + m22 * dj);
    }


    @Override
    public void indexOf(CoordinateType value, Vector2D toIndex) {
        projection.project(value, toIndex);
        toIndex(toIndex);
    }


    @Override
    public void coordsAt(Vector2D index, CoordinateType toValue) {
        toOffset(index);
        projection.deproject(index, toValue);
        toIndex(index);
    }


    @Override
    public final CoordinateType getReference() { return projection.getReference(); }


    @Override
    public void setReference(CoordinateType reference) { projection.setReference(reference); }


    @Override
    public Vector2D getReferenceIndex() { return refIndex; }


    @Override
    public void setReferenceIndex(Vector2D v) { refIndex = v; }


    public final Projection2D<CoordinateType> getProjection() { return projection; }


    public void setProjection(Projection2D<CoordinateType> p) { this.projection = p; }


    public final void getIndex(final CoordinateType coords, final Vector2D index) {
        projection.project(coords, index);
        toIndex(index);
    }


    public final void getCoords(final Vector2D index, final CoordinateType coords) {
        final double i = index.x();
        final double j = index.y(); 

        toOffset(index);
        projection.deproject(index, coords);

        index.setX(i);
        index.setY(j);
    }


    public final void getIndex(final Vector2D offset, final Index2D index) {
        final double x = offset.x();
        final double y = offset.y();
        toIndex(offset);
        index.set((int) Math.round(offset.x()), (int) Math.round(offset.y()));
        offset.set(x, y);
    }


    public final void getOffset(final Index2D index, final Vector2D offset) {
        offset.set(index.i(), index.j());
        toOffset(offset);       
    }


    public void toggleNative(final Vector2D offset) {
        if(isReverseX()) offset.scaleX(-1.0);
        if(isReverseY()) offset.scaleY(-1.0);
    }


    public Unit getPixelAreaUnit() { 
        return new Unit("pixel", Double.NaN) {
            private static final long serialVersionUID = -2483542207572304222L;

            @Override
            public double value() { return getPixelArea(); }
        };
    }


    /**
     * Creates a 2D grid from the supplied FITS header, using the specified alternative coordinate system label.
     *
     * @param header the header
     * @param alt the alt
     * @return the Grid2D for
     * @throws HeaderCardException the header card exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     */
    public static Grid2D<?> fromHeader(Header header, String alt) throws Exception {
        Grid2D<?> grid = getGrid2DInstanceFor(header.getStringValue("CTYPE1" + alt), header.getStringValue("CTYPE2" + alt));  
        grid.parseHeader(header);
        return grid;
    }


    private static Grid2D<?> getGrid2DInstanceFor(String type1, String type2) {
        // Check if the types conform to projected WCS coordinates, according to Calabretta & Greisen (2002)
        // If so, return a SphericalGrid, else a regular Cartesian grid.
        if(type1 == null || type2 == null) return new FlatGrid2D();
        if(type1.length() < 6 || type2.length() < 6) return new FlatGrid2D();

        StringTokenizer tokens1 = new StringTokenizer(type1.toLowerCase(), "-");    	
        if(tokens1.countTokens() != 2) return new FlatGrid2D(); 

        StringTokenizer tokens2 = new StringTokenizer(type2.toLowerCase(), "-");
        if(tokens2.countTokens() != 2) return new FlatGrid2D(); 

        String xtype = tokens1.nextToken();
        String px = tokens1.nextToken();

        String ytype = tokens2.nextToken();
        String py = tokens2.nextToken();

        if(!px.equals(py)) return new FlatGrid2D();

        if(xtype.equals("ra") && ytype.equals("dec")) return new SkyGrid();
        if(xtype.equals("dec") && ytype.equals("ra")) return new SkyGrid();
        if(xtype.equals("lon") && ytype.equals("lat")) return new SphericalGrid();
        if(xtype.equals("lat") && ytype.equals("lon")) return new SphericalGrid();

        if(xtype.charAt(0) != ytype.charAt(0)) return new FlatGrid2D();

        xtype = xtype.substring(1);
        ytype = ytype.substring(1);
        if(xtype.equals("lon") && ytype.equals("lat")) return new SkyGrid();
        if(xtype.equals("lat") && ytype.equals("lon")) return new SkyGrid();

        if(xtype.charAt(0) != ytype.charAt(0)) return new FlatGrid2D();

        xtype = xtype.substring(1);
        ytype = ytype.substring(1);
        if(xtype.equals("ln") && ytype.equals("lt")) return new SkyGrid();
        if(xtype.equals("lt") && ytype.equals("ln")) return new SkyGrid();

        return new FlatGrid2D();
    }


}

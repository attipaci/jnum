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

package jnum.data.image;

import java.io.Serializable;
import java.text.NumberFormat;

import jnum.Constant;
import jnum.Copiable;
import jnum.ExtraMath;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.data.image.overlay.Referenced2D;
import jnum.math.Coordinate2D;
import jnum.math.Division;
import jnum.math.Multiplication;
import jnum.math.Product;
import jnum.math.Ratio;
import jnum.math.Scalable;
import jnum.math.Vector2D;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;

// TODO: Auto-generated Javadoc
/**
 * The Class Gaussian2D.
 */
public class Gaussian2D implements Serializable, Cloneable, Copiable<Gaussian2D>, Scalable, Multiplication<Gaussian2D>, Division<Gaussian2D>, Product<Gaussian2D, Gaussian2D>, Ratio<Gaussian2D, Gaussian2D> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1182818146658831916L;

    /** The position angle. */
    private double majorFWHM, minorFWHM, positionAngle;


    /**
     * Instantiates a new point-spread function (PSF) with a Gaussian profile.
     */
    public Gaussian2D() {}

    /**
     * Instantiates a new point-spread function (PSF) with a Gaussian profile.
     *
     * @param FWHM the full-width half-maximum of a circular beam profile.
     */
    public Gaussian2D(double FWHM) { this(); setFWHM(FWHM); }


    /**
     * Instantiates a new point-spread function (PSF) with an elliptical Gaussian profile.
     *
     * @param a the full-width half-maximum of the primary elliptical axis.
     * @param b the full-width half-maximum of the secondary elliptical axis.
     * @param angle the position angle of the primary axis (radians).
     */
    public Gaussian2D(double a, double b, double angle) {
        this();
        set(a, b, angle);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(majorFWHM) ^ HashCode.from(minorFWHM) ^ HashCode.from(positionAngle);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof Gaussian2D)) return false;
      
        Gaussian2D psf = (Gaussian2D) other;
        if(psf.majorFWHM != majorFWHM) return false;
        if(psf.minorFWHM != minorFWHM) return false;
        if(psf.positionAngle != positionAngle) return false;
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    /* (non-Javadoc)
     * @see jnum.Copiable#copy()
     */
    @Override
    public Gaussian2D copy() {
        Gaussian2D copy = (Gaussian2D) clone();
        return copy;
    }



    /**
     * Sets the.
     *
     * @param FWHM the fwhm
     */
    public final void set(double FWHM) {
        set(FWHM, FWHM, 0.0);
    }

    /**
     * Sets the.
     *
     * @param a the full-width half-maximum of the primary elliptical axis.
     * @param b the full-width half-maximum of the secondary elliptical axis.
     * @param positionAngle the position angle
     */
    public final void set(double a, double b, double positionAngle) {
        if(b > a) {
            this.majorFWHM = b;
            this.minorFWHM = a;
            setPositionAngle(positionAngle + Constant.rightAngle);
        }
        else {
            this.majorFWHM = a;
            this.minorFWHM = b;
            setPositionAngle(positionAngle);
        }
    }

    /**
     * Sets the.
     *
     * @param psf the psf
     */
    public final void set(Gaussian2D psf) {
        majorFWHM = psf.majorFWHM;
        minorFWHM = psf.minorFWHM;
        positionAngle = psf.positionAngle;
    }

    /**
     * Encompass.
     *
     * @param FWHM the fwhm
     */
    public void encompass(double FWHM) {
        if(majorFWHM < FWHM) majorFWHM = FWHM;
        if(minorFWHM < FWHM) minorFWHM = FWHM;
    }

    /**
     * Encompass.
     *
     * @param psf the psf
     */
    public void encompass(Gaussian2D psf) {		
        double da = psf.positionAngle - this.positionAngle;

        double c = Math.cos(da);
        double s = Math.sin(da);

        double minMajor = ExtraMath.hypot(psf.getMajorFWHM() * c, psf.getMinorFWHM() * s);
        double minMinor = ExtraMath.hypot(psf.getMajorFWHM() * s, psf.getMinorFWHM() * c);

        if(majorFWHM < minMajor) majorFWHM = minMajor;
        if(minorFWHM < minMinor) minorFWHM = minMinor;

    }

    /**
     * Checks if is encompassing.
     *
     * @param psf the psf
     * @return true, if is encompassing
     */
    public boolean isEncompassing(Gaussian2D psf) {
        double da = psf.positionAngle - this.positionAngle;

        double c = Math.cos(da);
        double s = Math.sin(da);

        double minMajor = ExtraMath.hypot(psf.getMajorFWHM() * c, psf.getMinorFWHM() * s);
        double minMinor = ExtraMath.hypot(psf.getMajorFWHM() * s, psf.getMinorFWHM() * c);

        if(majorFWHM < minMajor) return false;
        if(minorFWHM < minMinor) return false;

        return true;
    }

    /**
     * Checks if is encompassing.
     *
     * @param FWHM the fwhm
     * @return true, if is encompassing
     */
    public boolean isEncompassing(double FWHM) {
        if(majorFWHM < FWHM) return false;
        if(minorFWHM < FWHM) return false;
        return true;
    }



    /**
     * Sets the position angle for the elliptical beam major axis.
     *
     * @param value the new position angle of the major axis.
     */
    public final void setPositionAngle(double value) {
        positionAngle = Math.IEEEremainder(value, Math.PI);
    }

    /**
     * Rotate.
     *
     * @param angle the angle
     */
    public void rotate(double angle) {
        setPositionAngle(positionAngle + angle);
    }

    /* (non-Javadoc)
     * @see kovacs.math.Scalable#scale(double)
     */
    @Override
    public void scale(double factor) {
        majorFWHM *= factor;
        minorFWHM *= factor;
    }

    /**
     * Sets the full-width half-maximum of a circular beam profile.
     *
     * @param value the new full-width half-maximum.
     */
    public void setFWHM(double value) { majorFWHM = minorFWHM = value; }

    /**
     * Gets the full-width half-maximum of the elliptical beam major axis.
     *
     * @return the major fwhm
     */
    public final double getMajorFWHM() { return majorFWHM; }

    /**
     * Gets the full-width half-maximum of the elliptical beam minor axis.
     *
     * @return the minor fwhm
     */
    public final double getMinorFWHM() { return minorFWHM; }

    /**
     * Gets the circular equivalent fwhm.
     *
     * @return the circular equivalent fwhm
     */
    public final double getCircularEquivalentFWHM() {
        return Math.sqrt(majorFWHM * minorFWHM);
    }

    /**
     * Gets the axis product.
     *
     * @return the axis product
     */
    public final double getAxisProduct() { return majorFWHM * getMinorFWHM(); }

    /**
     * Extent in x.
     *
     * @return the double
     */
    public final double extentInX() { return ExtraMath.hypot(Math.cos(positionAngle) * majorFWHM, Math.sin(positionAngle) * minorFWHM); }

    /**
     * Extent in y.
     *
     * @return the double
     */
    public final double extentInY() { return ExtraMath.hypot(Math.cos(positionAngle) * minorFWHM, Math.sin(positionAngle) * majorFWHM); }


    /**
     * Gets the effective beam area (i.e. beam integral).
     *
     * @return the effective beam area.
     */
    public final double getArea() { return areaFactor * minorFWHM * majorFWHM; }


    public void setArea(double A) {
        setFWHM(Math.sqrt(A / areaFactor)); 
    }

    // TODO proper 2D Gaussian equivalent beams...
    // for now we just assume amimuthal symmetry...
    public void setEquivalent(Value2D beam, Coordinate2D pixelSize) {
        // Smoothing beams are generally small, so no need to parallelize...
        double I = 0.0;
        for(int i=beam.sizeX(); --i >= 0; ) for(int j=beam.sizeY(); --j >= 0; ) if(beam.isValid(i, j)) {
            I += Math.abs(beam.get(i, j).doubleValue());
        }     
        setArea(I * pixelSize.x() * pixelSize.y());
    }



    /**
     * Gets the position angle of the elliptical beam major axis.
     *
     * @return the position angle
     */
    public final double getPositionAngle() { return positionAngle; }

    /**
     * Checks if this PSF is circular (major axis = minor axis).
     *
     * @return true, if is circular
     */
    public boolean isCircular() { return minorFWHM == majorFWHM; }

    public final double valueAt(Vector2D v) {
        return valueAt(v.x(), v.y());
    }
    
    public double valueAt(double dx, double dy) {
        final double s = Math.sin(positionAngle);
        final double c = Math.cos(positionAngle);
       
        final double devX = (c * dx + s * dy) * Constant.sigmasInFWHM / majorFWHM;
        final double devY = (c * dy - s * dx) * Constant.sigmasInFWHM / minorFWHM;
        
        return Math.exp(-0.5 * (devX * devX + devY * devY));
    }
    
    
    /**
     * Gets an image of the beam on the specified grid, with a default 3-sigma extent. The image
     * has odd number of points in both dimensions, and the center (reference) point of the beam
     * is its mid-points (i.e., for a dimension N, the midpoint is c = (N>>>1);
     *
     * @param grid the grid
     * @return the beam image.
     */
    public Referenced2D getBeam(Grid2D<?> grid) { return getBeam(grid, 3.0); }

    /**
     * Gets an image of the beam on the specified grid, with the specified extent. The image
     * has odd number of points in both dimensions, and the center (reference) point of the beam
     * is its mid-points (i.e., for a dimension N, the midpoint is c = (N>>>1); 
     *
     * @param grid the grid
     * @param sigmas the extent of the image in units of the standard-deviation (sigma).
     * @return the beam image.
     */
    public Referenced2D getBeam(Grid2D<?> grid, double sigmas) { 
        return getBeam(majorFWHM, minorFWHM, positionAngle, grid, sigmas);
    }

    /**
     * Invert.
     *
     * @param psf the psf
     */
    /*
	private void invert() {
		double temp = minorFWHM;
		minorFWHM = 1.0 / majorFWHM;
		majorFWHM = 1.0 / temp;
		setPositionAngle(positionAngle + Constant.rightAngle);
	}
     */


    /* (non-Javadoc)
     * @see kovacs.math.Multiplication#multiplyBy(java.lang.Object)
     */
    @Override
    public final void multiplyBy(Gaussian2D psf) {
        convolveWith(psf); 
    }

    /* (non-Javadoc)
     * @see kovacs.math.Product#setProduct(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setProduct(Gaussian2D a, Gaussian2D b) {
        set(a.majorFWHM, a.minorFWHM, a.positionAngle);
        multiplyBy(b);
    }

    /* (non-Javadoc)
     * @see kovacs.math.Ratio#setRatio(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setRatio(Gaussian2D numerator, Gaussian2D denominator) {
        set(numerator.majorFWHM, numerator.minorFWHM, numerator.positionAngle);
        divideBy(denominator);
    }

    /* (non-Javadoc)
     * @see kovacs.math.Division#divideBy(java.lang.Object)
     */
    @Override
    public void divideBy(Gaussian2D psf) {
        deconvolveWith(psf);
    }


    /**
     * Convolve this PSF with another Gaussian PSF profile.
     *
     * @param psf the Gaussian PSF profile to convolve with.
     */
    public void convolveWith(Gaussian2D psf) {
        combineWith(psf, false);
    }

    /**
     * Deconvolve this PSF with another Gaussian PSF profile.
     *
     * @param psf the Gaussian PSF profile to deconvolve with.
     */
    public void deconvolveWith(Gaussian2D psf) {
        combineWith(psf, true);
    }		

    /**
     * Combine with.
     *
     * @param psf the psf
     * @param deconvolve the deconvolve
     */
    private void combineWith(Gaussian2D psf, boolean deconvolve) {
        final double a2x = majorFWHM * majorFWHM;
        final double a2y = minorFWHM * minorFWHM;

        final double b2x = psf.majorFWHM * psf.majorFWHM;
        final double b2y = psf.minorFWHM * psf.minorFWHM;

        final int dir = deconvolve ? -1 : 1;

        final double a = (a2x - a2y);
        final double b = (b2x - b2y);
        final double delta = Math.IEEEremainder(2.0 * (psf.positionAngle - positionAngle), Math.PI);
        final double c = SafeMath.sqrt(a * a + b * b + 2.0 * dir * a * b * Math.cos(delta));
        final double B = a2x + a2y + dir * (b2x + b2y);

        majorFWHM = Math.sqrt(0.5 * (B + c));
        minorFWHM = Math.sqrt(0.5 * (B - c));

        // Rectify invalid deconvolutions...
        if(deconvolve) {
            if(Double.isNaN(majorFWHM)) majorFWHM = 0.0;
            if(Double.isNaN(minorFWHM)) minorFWHM = 0.0;
        }

        if(c == 0.0) positionAngle = 0.0;
        else {
            final double sinBeta = dir * Math.sin(delta) * b/c;
            setPositionAngle(positionAngle + 0.5 * SafeMath.asin(sinBeta) + (minorFWHM > majorFWHM ? Constant.rightAngle : 0.0));	
        }
    }
    

    // TODO check thoroughly...
    /*
	public void intersect(GaussianPSF psf) {

		double A1 = 0.5 * (majorFWHM - minorFWHM);
		double a2x = A1 * Math.cos(2.0 * positionAngle);
		double a2y = A1 * Math.sin(2.0 * positionAngle);

		double A2 = 0.5 * (psf.majorFWHM - psf.minorFWHM);
		double b2x = A2 * Math.cos(2.0 * psf.positionAngle);
		double b2y = A2 * Math.sin(2.0 * psf.positionAngle);

		double m2x = Math.abs(a2x) < Math.abs(b2x) ? a2x : b2x;
		double m2y = Math.abs(a2y) < Math.abs(b2y) ? a2y : b2y;

		double A = ExtraMath.hypot(m2x, m2y);
		double r = 0.5 * Math.min(majorFWHM + minorFWHM, psf.majorFWHM + psf.minorFWHM);

		set(r + A, r - A, 0.5 * Math.atan2(m2y,  m2x));
	}
     */


    /**
     * Parses the FITS header.
     *
     * @param header the FITS header
     */
    public void parseHeader(Header header, String fitsID, double sizeUnit) {
        if(!header.containsKey(fitsID + "BMAJ")) throw new IllegalStateException("FITS header contains no beam description for type '" + fitsID + "'");
        majorFWHM = header.getDoubleValue(fitsID + "BMAJ", Double.NaN) * sizeUnit;
        minorFWHM = header.getDoubleValue(fitsID + "BMIN", majorFWHM) * sizeUnit;
        positionAngle = header.getDoubleValue(fitsID + "BPA", 0.0) * Unit.deg;
    }	

    /**
     * Edits the FITS header.
     *
     * @param header the header
     * @param cursor the cursor where the FITS keys will be inserted into the header.
     * @throws HeaderCardException the header card exception
     */
    public void editHeader(Header header, String name, String fitsID, Unit sizeUnit) throws HeaderCardException {
        if(name != null) header.addLine(new HeaderCard(fitsID + "BNAM", name, "Beam name."));
        header.addLine(new HeaderCard(fitsID + "BMAJ", majorFWHM / sizeUnit.value(), "Beam major axis (" + sizeUnit.name() + ")."));
        header.addLine(new HeaderCard(fitsID + "BMIN", minorFWHM / sizeUnit.value(), "Beam minor axis (" + sizeUnit.name() + ")."));
        header.addLine(new HeaderCard(fitsID + "BPA", positionAngle / Unit.deg, "Beam position angle (deg)."));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(Util.s4);
    }

    /**
     * To string.
     *
     * @param nf the nf
     * @return the string
     */
    public String toString(NumberFormat nf) {
        if(isCircular()) return nf.format(majorFWHM);
        return nf.format(majorFWHM) + "x" + nf.format(minorFWHM) + " @ " + Util.f1.format(positionAngle / Unit.deg) + " deg.";
    }

    /**
     * To string.
     *
     * @param u the u
     * @return the string
     */
    public String toString(Unit u) {
        return toString(u, Util.s4);
    }

    /**
     * To string.
     *
     * @param u the u
     * @param nf the nf
     * @return the string
     */
    public String toString(Unit u, NumberFormat nf) {	
        if(isCircular()) return nf.format(majorFWHM / u.value()) + " " + u.name();
        return nf.format(majorFWHM / u.value()) + "x" + nf.format(minorFWHM / u.value()) + " " + u.name() + " @ " + Util.f1.format(positionAngle / Unit.deg) + " deg.";
    }



    public Unit getBeamUnit() {
        return new Unit("beam", Double.NaN) {            
            /**
             * 
             */
            private static final long serialVersionUID = 7593700995697181741L;

            @Override
            public double value() { return getArea(); }
        };
    }


    public static Gaussian2D getEquivalent(Value2D beam, Coordinate2D pixelSize) {
        Gaussian2D psf = new Gaussian2D();
        psf.setEquivalent(beam, pixelSize);
        return psf;
    }

    /**
     * Gets the beam.
     *
     * @param FWHM the fwhm
     * @param grid the grid
     * @return the beam
     */
    public static Referenced2D getBeam(double FWHM, Grid2D<?> grid) {
        return getBeam(FWHM, grid, 3.0);
    }	

    /**
     * Gets the beam.
     *
     * @param FWHM the fwhm
     * @param grid the grid
     * @param sigmas the n beams
     * @return the beam
     */
    public static Referenced2D getBeam(double FWHM, Grid2D<?> grid, double sigmas) {
        return getBeam(FWHM, FWHM, 0.0, grid, sigmas);
    }

    /**
     * Gets the beam.
     *
     * @param majorFWHM the major fwhm
     * @param minorFWHM the minor fwhm
     * @param angle the angle
     * @param grid the grid
     * @param sigmas the n beams
     * @return the beam
     */
    // TODO sheared grids...
    public static Referenced2D getBeam(double majorFWHM, double minorFWHM, double angle, Grid2D<?> grid, double sigmas) {
        if(!grid.isRectilinear()) throw new IllegalArgumentException("GaussianPSF supports rectilinear grids only.");

        Vector2D v = new Vector2D(majorFWHM, minorFWHM);
        v.rotate(angle);
     
        int sizeX = 2 * (int)Math.ceil(sigmas * Math.abs(v.x())/grid.pixelSizeX()) + 1;
        int sizeY = 2 * (int)Math.ceil(sigmas * Math.abs(v.y())/grid.pixelSizeY()) + 1;

        final Image2D image = Image2D.createType(Double.class, sizeX, sizeY);
        final double sigma1 = majorFWHM / Constant.sigmasInFWHM;
        final double sigma2 = minorFWHM / Constant.sigmasInFWHM;
        final double A1 = -0.5 * grid.pixelSizeX() * grid.pixelSizeX() / (sigma1 * sigma1);
        final double A2 = -0.5 * grid.pixelSizeY() * grid.pixelSizeY() / (sigma2 * sigma2);
        final double centerX = (sizeX-1) / 2.0;
        final double centerY = (sizeY-1) / 2.0;

        for(int i=sizeX; --i >= 0; ) for(int j=sizeY; --j >= 0; ) {
            double dx = i - centerX;
            double dy = j - centerY;

            v.set(dx, dy);
            v.rotate(-angle);

            image.set(i, j, Math.exp(A1*v.x()*v.x() + A2*v.y()*v.y()));
        }
            
        return new Referenced2D(image, new Coordinate2D(0.5 * (sizeX - 1), 0.5 * (sizeY - 1)));
    }



    /** The Constant areaFactor to convert from FWHM^2 to beam integral. */
    public final static double areaFactor = Constant.twoPi / (Constant.sigmasInFWHM * Constant.sigmasInFWHM);
   
    /** The fwhm2size. */
    public static double fwhm2size = Math.sqrt(areaFactor);   


}

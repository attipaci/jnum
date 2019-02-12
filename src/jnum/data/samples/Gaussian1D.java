/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.samples;

import java.io.Serializable;
import java.text.NumberFormat;

import jnum.Constant;
import jnum.Copiable;
import jnum.CopyCat;
import jnum.Unit;
import jnum.Util;
import jnum.data.Data;
import jnum.data.samples.overlay.Referenced1D;
import jnum.math.Division;
import jnum.math.Multiplication;
import jnum.math.Product;
import jnum.math.Ratio;
import jnum.math.Scalable;
import jnum.util.HashCode;


public class Gaussian1D implements Serializable, Cloneable, Copiable<Gaussian1D>, CopyCat<Gaussian1D>, Scalable, 
Multiplication<Gaussian1D>, Division<Gaussian1D>, Product<Gaussian1D, Gaussian1D>, Ratio<Gaussian1D, Gaussian1D> {
    /**
     * 
     */
    private static final long serialVersionUID = 3134715160049459485L;

    private double FWHM;


    public Gaussian1D() {}

    public Gaussian1D(double FWHM) { this(); setFWHM(FWHM); }

    @Override
    public int hashCode() {
        return super.hashCode() ^ HashCode.from(FWHM);
    }


    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof Gaussian1D)) return false;
      
        Gaussian1D psf = (Gaussian1D) other;
        if(psf.FWHM != FWHM) return false;
        
        return false;
    }


    @Override
    public Gaussian1D clone() {
        try { return (Gaussian1D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }

    @Override
    public Gaussian1D copy() {
        return clone();
    }


    public final void set(double value) {
        this.FWHM = value;
    }

    @Override
    public final void copy(Gaussian1D psf) {
        FWHM = psf.FWHM;
    }


    public void encompass(double FWHM) {
        if(FWHM < FWHM) this.FWHM = FWHM;
    }

    public void encompass(Gaussian1D psf) {     
        encompass(psf.FWHM);
    }

    public boolean isEncompassing(Gaussian1D psf) {
        if(FWHM < psf.FWHM) return false;
        return true;
    }

    public boolean isEncompassing(double FWHM) {
        if(this.FWHM < FWHM) return false;
        return true;
    }

    @Override
    public void scale(double factor) {
        FWHM *= factor;
    }


    public void setFWHM(double value) { this.FWHM = value; }

    public final double getFWHM() { return FWHM; }


    // TODO proper 2D Gaussian equivalent beams...
    // for now we just assume amimuthal symmetry...
    public void setEquivalent(final Data<Index1D> beam, double pixelSize) {
        setIntegral(beam.getAbsSum() * pixelSize);
    }

    
    public final double getIntegral() { return fwhm2size * FWHM; }


    public void setIntegral(double A) {
        setFWHM(Math.sqrt(A / fwhm2size)); 
    }

  
    public double valueAt(double dx) {
        final double dev = dx * Constant.sigmasInFWHM / FWHM;
        return Math.exp(-0.5 * (dev * dev));
    }
    
    
    public Referenced1D getBeam(double pixelSize) { return getBeam(pixelSize, 3.0); }


    
    @Override
    public final void multiplyBy(Gaussian1D psf) {
        convolveWith(psf); 
    }


    @Override
    public void setProduct(Gaussian1D a, Gaussian1D b) {
        set(a.FWHM);
        multiplyBy(b);
    }

    @Override
    public void setRatio(Gaussian1D numerator, Gaussian1D denominator) {
        set(numerator.FWHM);
        divideBy(denominator);
    }


    @Override
    public void divideBy(Gaussian1D psf) {
        deconvolveWith(psf);
    }


    public void convolveWith(Gaussian1D psf) {
        combineWith(psf, false);
    }

 
    public void deconvolveWith(Gaussian1D psf) {
        combineWith(psf, true);
    }       


    private void combineWith(Gaussian1D psf, boolean deconvolve) {
        if(deconvolve) set(Math.sqrt(FWHM * FWHM - psf.FWHM * psf.FWHM));
        else set(Math.sqrt(FWHM * FWHM + psf.FWHM * psf.FWHM));
    }
    

    @Override
    public String toString() {
        return toString(Util.s4);
    }


    public String toString(NumberFormat nf) {
        return nf.format(FWHM);
    }

    public String toString(Unit u) {
        return toString(u, Util.s4);
    }


    public String toString(Unit u, NumberFormat nf) {   
        return nf.format(FWHM / u.value()) + " " + u.name();
    }



    public Unit getBeamUnit() {
        return new Unit("beam", Double.NaN) {            
            /**
             * 
             */
            private static final long serialVersionUID = 7593700995697181741L;

            @Override
            public double value() { return getIntegral(); }
        };
    }


    public static Gaussian1D getEquivalent(Data<Index1D> beam, double pixelSize) {
        Gaussian1D psf = new Gaussian1D();
        psf.setEquivalent(beam, pixelSize);
        return psf;
    }


    public static Referenced1D getBeam(double FWHM, double pixelSize) {
        return getBeam(FWHM, pixelSize, 3.0);
    }   


    public static Referenced1D getBeam(double FWHM, double pixelSize, double sigmas) {     
        int size = 2 * (int)Math.ceil(sigmas * Math.abs(FWHM) / pixelSize) + 1;
       
        final Samples1D image = Samples1D.createType(Double.class, size);
        final double sigma = FWHM / Constant.sigmasInFWHM;
        final double A = -0.5 * pixelSize * pixelSize / (sigma * sigma);
        final double center = (size-1) / 2.0;

        for(int i=size; --i >= 0; ) {
            double dx = i - center;
            image.set(i, Math.exp(A*dx*dx));
        }
            
        return new Referenced1D(image, 0.5 * (size - 1));
    }

   
    /** The fwhm2size. */
    public static double fwhm2size = Math.sqrt(Constant.twoPi) / Constant.sigmasInFWHM;

}

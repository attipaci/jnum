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

package jnum.data;

import java.io.Serializable;

import jnum.Constant;
import jnum.Copiable;
import jnum.CopyCat;
import jnum.math.Division;
import jnum.math.Multiplication;
import jnum.math.Product;
import jnum.math.Ratio;
import jnum.math.Scalable;
import jnum.math.MathVector;

/**
 * A abstract base class for Gaussian point-spread functions (PSFs) in one or more dimensions.
 * The <i>n</i>-dimensional Gaussian PSF can be represented by an ellipsoid in <i>n</i> dimensions
 * which may have <i>n</i> different sizes along orthogonal directions (the axes of the ellipsoid).
 * The sizes of Gaissian PSFs are typically defined as full-width half-maximum sizes, that is
 * the separation between the points at which the 1D Gaussian is at half of its peak value.
 * The orientation of the <i>n</i>-dimensional ellipsoid in the user's Cartesian system (or 
 * local coordinate system) is defined by (<i>n</i>-1) position angles (see 
 * {@link #setPositionAngles(double...)}).
 * 
 * @author Attila Kovacs
 *
 * @param <V>   the generic type of vector in the space for which this Gaussian PSF is defined.
 */
public abstract class GaussianPSF<V extends MathVector<Double>> implements Serializable, Cloneable, Copiable<GaussianPSF<? extends V>>, 
CopyCat<GaussianPSF<? extends V>>, Scalable, Multiplication<GaussianPSF<? extends V>>, Division<GaussianPSF<? extends V>>, 
Product<GaussianPSF<? extends V>, GaussianPSF<? extends V>>, Ratio<GaussianPSF<? extends V>, GaussianPSF<? extends V>> {

    /**
     * 
     */
    private static final long serialVersionUID = -5732359019840087697L;

    private V axes;
    private double[] angles; 
    
    protected GaussianPSF() {}
    
    protected GaussianPSF(V fwhms) {
        this.axes = fwhms;     
    }
    
    protected GaussianPSF(V fwhms, double... angles) {
        this(fwhms);
        setPositionAngles(angles);
    }
    
    /**
     * Sets the full-width half-maximum size of the Gaissian PSF to the specified value along all dimensions.
     * 
     * @param fwhm  the new FWHM value in all directions
     * 
     * @see #setFWHM(MathVector)
     * @see #getFWHM()
     * @see #getFWHM(int)
     */
    public void setFWHM(double fwhm) {
        V fwhms = getVectorInstance();
        fwhms.fill(fwhm);
        setFWHM(fwhms);
    }
    
    /**
     * Sets new FWHM values along the ellipsoid axes. For example, 2D Gaussian could set different
     * PSF sizes along a major an minor axis of a 2D ellipsoid. The orientation of the ellipsoid,
     * in the user's coordinate system is defined separately by {@link #setPositionAngles(double...)}
     * 
     * @param fwhms     the new FWHM values along each of the ellipsoidal axes of the PSF.
     * 
     * @see #getFWHM(int)
     * @see #getFWHM()
     * @see #setPositionAngles(double...)
     */
    public void setFWHM(V fwhms) {
        this.axes = fwhms;
    }
    
    /**
     * Returns the FWHM size of the PSF ellipsoid, in the coordinate system of the ellipsoid
     * itself. The orientation of the PSF ellipsoide can ve retrieved separately via 
     * {@link #getPositionAngles()}, or individually via calls to {@link #getPositionAngle(int)}.
     * 
     * @return  the FWHM size of the ellipsoide along its axes.
     * 
     * @see #getFWHM(int)
     */
    public final V getFWHM() { return axes; }
    
    /**
     * Returns the FWHM size of the PSF's ellipsoid along the <i>i</i><sup>th</sup> axis of the
     * ellipsoid.
     * 
     * @param index     the ellipsoid's axis index.
     * @return          the FWHM size of the ellipsoide along the specified axis.
     * 
     * @see #getFWHM()
     * @see #setFWHM(MathVector)
     * @see #getPositionAngle(int)
     */
    public final double getFWHM(int index) {
        return axes.getComponent(index);
    }
    
    /**
     * Sets a new orientation for the PSF ellipsoid in the user's coordinate system. For
     * an ellpsoid in <i>n</i> dimensions, there are (<i>n</i>-1) angles that define its
     * orientation. For example, a 2D ellipsoid will have a single angle &phi; which specifies
     * the orientation of it's first axis relative to the user's <i>x</i> coordinate. 
     * For a 3D ellipsoide there are 2 orinetation angles (&phi, &Theta), and the 
     * transformation from the ellipsoid to the user coordinate syste is 
     * <i>R</i><sub>z</sub>(&phi;) &odot; <i>R</i><sub>y</sub>(&Theta;), etc.
     *  
     * @param angles    (rad) the new (<i>n</i>-1) position angles for a PSF ellpsoid <i>n</i> dimensions.
     *
     * @see #getPositionAngles()
     * @see #getPositionAngle(int)
     * @see #setFWHM(MathVector)
     *
     */
    public void setPositionAngles(double ... angles) {
        this.angles = angles;
    }
    
    /**
     * Returns the ellpsoid's orientation in the user's coordinate system. See {@link #setPositionAngles(double...)}
     * for more information on how the orientation is defined in general.
     * 
     * @return      (rad) the (<i>n</i>-1) position angles for a PSF ellpsoid <i>n</i> dimensions.
     * 
     * @see #getPositionAngle(int)
     * @see #setPositionAngles(double...)
     */
    public final double[] getPositionAngles() { return angles; }
    
    /**
     * Returns the <i>i</i><sup>th</sup> position angle of the PSF ellipsoid in the user's
     * coordinate system. See {@link #setPositionAngles(double...)}
     * for more information on how the orientation is defined in general.
     * 
     * @param i     the index of the orientation angle
     * @return      (rad) the <i>i</i><sup>th</sup> position angle of the PSF ellipsoid.
     * 
     * @see GaussianPSF#getPositionAngle(int)
     * @see #setPositionAngles(double...)
     */
    public final double getPositionAngle(int i) {
        if(angles == null) return 0.0;
        if(i >= angles.length) return 0.0;
        return angles[i];
    }
    
    /**
     * Returns a new instance of the type of position vector that is supported by this implementation.
     * 
     * @return      a new insatnce of the vector type supported by the implementation
     */
    public abstract V getVectorInstance();
    
    /**
     * Convolves this Gaussian PSF with another, changing the ellipsoidal parameters to
     * correspond to the size and orientation of the convolution product. In Fourier
     * domain, the convolution is a simple multiplication.
     * 
     * @param psf   the convolving other PSF.
     * 
     * @see #deconvolveWith(GaussianPSF)
     */
    public abstract void convolveWith(GaussianPSF<? extends V> psf);
    
    /**
     * Deonvolves this Gaussian PSF with another, changing the ellipsoidal parameters to
     * correspond to the size and orientation of the deconvolution product. In Fourier
     * domain, the deconvolution is a simple division of this PSF's transform with the
     * argument's transform.
     * 
     * @param psf   the deconvolving other PSF.
     * 
     * @see #convolveWith(GaussianPSF)
     */
    public abstract void deconvolveWith(GaussianPSF<? extends V> psf);
    
    /** The equivalent area lateral square size to a Gaussian beam with FWHM. */
    public static final double fwhm2size = Math.sqrt(Constant.twoPi) / Constant.sigmasInFWHM;   
    
    
}

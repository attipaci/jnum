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

public abstract class GaussianPSF<V extends MathVector<Double>> implements Serializable, Cloneable, Copiable<GaussianPSF<? extends V>>, 
CopyCat<GaussianPSF<? extends V>>, Scalable, Multiplication<GaussianPSF<? extends V>>, Division<GaussianPSF<? extends V>>, 
Product<GaussianPSF<? extends V>, GaussianPSF<? extends V>>, Ratio<GaussianPSF<? extends V>, GaussianPSF<? extends V>> {

    /**
     * 
     */
    private static final long serialVersionUID = -5732359019840087697L;

    private V axes;
    private double[] angles; 
    
    public GaussianPSF() {}
    
    public GaussianPSF(V fwhms) {
        this.axes = fwhms;     
    }
    
    public GaussianPSF(V fwhms, double ... angles) {
        this(fwhms);
        setPositionAngles(angles);
    }
    
    public void setFWHM(double fwhm) {
        V fwhms = getOffsetInstance();
        fwhms.fill(fwhm);
        setFWHM(fwhms);
    }
    
    public void setFWHM(V fwhms) {
        this.axes = fwhms;
    }
    
    public V getFWHM() { return axes; }
    
    public double getFWHM(int index) {
        return axes.getComponent(index);
    }
    
    public void setPositionAngles(double ... angles) {
        this.angles = angles;
    }
    
    public double[] getPositionAngles() { return angles; }
    
    public double getPositionAngle(int i) {
        if(angles == null) return 0.0;
        if(i >= angles.length) return 0.0;
        return angles[i];
    }
    
    public abstract V getOffsetInstance();
    
    public abstract void convolveWith(GaussianPSF<? extends V> psf);
    
    public abstract void deconvolveWith(GaussianPSF<? extends V> psf);
    
    /** The equivalent area lateral square size to a Gaussian beam with FWHM. */
    public static final double fwhm2size = Math.sqrt(Constant.twoPi) / Constant.sigmasInFWHM;   
    
    
}

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

package jnum.astro;

import jnum.Constant;

/**
 * A representation of an ideal black body source with a temperature (K) and a visible solid angle (d&Omega;).
 * 
 * @author Attila Kovacs
 *
 */
public class BlackBody {
    
    /** (sr) The solid angle of the black body */
    private double dOmega;
    
    /** (K) surface temperasture of body */
    private double T;
    
    /**
     * Returns the black body surface temperature.
     * 
     * @return  (K) the currently set surface temperature.
     * 
     * @see #setTemperature(double)
     */
    public double getTemperature() { return T; }
    
    /**
     * Sets a new surface temperature for this black body source.
     * 
     * @param T     (K) the new surface temperature.
     * 
     * @see #getTemperature()
     */
    public void setTemperature(double T) { this.T = T; }
    
    /**
     * Returns the apparent (visible) size of the black body as a solid angle.
     * 
     * @return  (sr) the solid angle visible to the observer.
     * 
     * @see #setSolidAngle(double)
     */
    public double getSolidAngle() { return dOmega; }
    
    /**
     * Sets a new apparent (visible) size for this black body source.
     * 
     * @param dOmega    (sr) the new solid angle visible to the observer.
     * 
     * @see #getSolidAngle()
     */
    public void setSolidAngle(double dOmega) { this.dOmega = dOmega; }
    
    /**
     * Returns the apparent brightness of the black body visible to the observer, calculated
     * using the Planck black body radiation formula.
     * 
     * @param freq  (Hz) The frequency at which the black body is observed
     * @return      (W/m<sup>2</sup>/Hz) The observed brightness of the black body source at the specified frequency.
     */
    public double getBrightness(final double freq) {
        return dOmega * B(freq, T);
    }
    
    /**
     * Returns the normalized (by solid angle) brightness of a black body source, using the
     * Planck formula evaluated at the specified arguments.
     * 
     * @param freq  (Hz) The frequency at which the black body is observed
     * @param T     (K) The temperature of the black
     * @return      (W/sr/m<sup>2</sup>/Hz) The normalized brighness of the black body source.
     */
    public static double B(final double freq, final double T) {
        return coeff * freq * freq * freq / Math.expm1(hOverk * freq / T);
    }
    
    /** The constant 2h / c<sup>2</sup> */
    private static double coeff = 2.0 * Constant.h / (Constant.c * Constant.c);
    
    /** The constant h/k */
    private static double hOverk = Constant.h / Constant.k;
    
}

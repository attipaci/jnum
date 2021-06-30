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

public class BlackBody {
    private double dOmega;
    private double T;
    
    public double getTemperature() { return T; }
    
    public void setTemperature(double T) { this.T = T; }
    
    public double getSolidAngle() { return dOmega; }
    
    public void setSolidAngle(double dOmega) { this.dOmega = dOmega; }
    
    public double getBrightness(final double freq) {
        return dOmega * B(freq, T);
    }
    
    public static double B(final double freq, final double T) {
        return coeff * freq * freq * freq / Math.expm1(hOverk * freq / T);
    }
    
    private static double coeff = 2.0 * Constant.h / (Constant.c * Constant.c);
    private static double hOverk = Constant.h / Constant.k;
    
}

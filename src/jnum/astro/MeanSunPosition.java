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

package jnum.astro;

import jnum.Constant;
import jnum.Unit;

public final class MeanSunPosition {

    /**
     * Sun's ecliptic longitude, good to within 0.5 arcmin between 1950 -- 2050
     * 
     * @param MJD
     * @return
     */
    public static double eclipticLongitudeAt(double MJD) {
        final double g = meanAnomalyAt(MJD);
        return (280.460 + 0.9856474 * (MJD - AstroTime.MJDJ2000) + 1.915 * Math.sin(g) + 0.020 * Math.sin(2.0 * g)) * Unit.deg;
    }
    
    private static double meanAnomalyAt(double MJD) {
        return (357.528 + 0.9856003 * (MJD - AstroTime.MJDJ2000)) * Unit.deg;
    }
    
    public static double distanceAt(double MJD) {
        final double g = meanAnomalyAt(MJD);
        return (1.00014 - 0.01671 * Math.cos(g) - 0.00014 * Math.cos(2.0 * g)) * Unit.AU;
    }
    
    public static double obliquityAt(double MJD) {
        final double T = (MJD - AstroTime.MJDJ2000) / AstroTime.JulianCenturyDays;
        double eps = epsCoeff[0];
        double term = T;
        for(int i=1; i<epsCoeff.length; i++) {
            eps += epsCoeff[i] * term;
            term *= T;
        }
        return eps;
    }
    
    public static void forMJD(double MJD, EclipticCoordinates ec) {
        ec.setLatitude(0.0);
        ec.setLongitude(Math.IEEEremainder(eclipticLongitudeAt(MJD), Constant.twoPi));
        ec.setEpoch(CoordinateEpoch.J2000);
    }
    
    public static void forMJD(double MJD, EquatorialCoordinates eq) {
        final double l = eclipticLongitudeAt(MJD);
        final double sinl = Math.sin(l);
        final double eps = obliquityAt(MJD);
        eq.setRA(Math.atan2(Math.cos(eps) * sinl, Math.cos(l)));
        eq.setDEC(Math.asin(Math.sin(eps) * sinl));
        eq.setEpoch(CoordinateEpoch.J2000);
    }
    
    public static EquatorialCoordinates equatorialAt(double MJD) {
        EquatorialCoordinates eq = new EquatorialCoordinates();
        forMJD(MJD, eq);
        return eq;
    }
  
    
    private final static double epsCoeff[] = {
            23 * Unit.deg + 26 * Unit.arcmin + 21.406 * Unit.arcsec,
            -46.836769 * Unit.arcsec, -0.0001831 * Unit.arcsec, 0.00200340 * Unit.arcsec, 
            -5.76e-7 * Unit.arcsec, -4.34e-8 * Unit.arcsec
    };

}

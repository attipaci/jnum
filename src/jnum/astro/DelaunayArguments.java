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

package jnum.astro;

import jnum.Constant;
import jnum.Unit;

/**
 * Delaunay arguments, for Sun and Moon positions, commonly used for nutation calculations.
 * See Eq. 5.32 in IERS Technical Note 36 (2010)
 * 
 * @author Attila Kovacs
 *
 */
public class DelaunayArguments {
    double f[];

    /**
     * Precise Delaunay arguments for a given time of observation, calculated according to
     * the prescription in Eq. 5.32 in IERS Technical Note 36 (2010)
     * 
     * @param mjd   (day) Modified Julian Date of observations.
     */
    public DelaunayArguments(double mjd) {
        this(mjd, false);
    }
    
    /**
     * Precise or approximate Delaunay arguments calculated for some time.
     * 
     * @param mjd           (day) Modified Julian Date of observations.
     * @param isTruncated   If true, the arguments are calculated with the linear approximation of
     *                      Capitaine &amp; Wallace, (2008).
     */
    public DelaunayArguments(double mjd, boolean isTruncated) {
        final double t = (mjd - AstroTime.MJDJ2000) / AstroTime.JulianCenturyDays;
        
        f = new double[5];
    
        if(isTruncated) {
            // Linear Delaunay arguments from Capitaine &amp; Wallace (2008)
            f[0] = 2.3555557435 + t * 8328.6914257191;
            f[1] = 6.2400601269 + t * 628.3019551714;
            f[2] = 1.6279050815 + t * 8433.4661569164;
            f[3] = 5.1984665887 + t * 7771.3771455937;
            f[4] = 2.1824391966 - t * 33.7570459536;
            
            for(int i=f.length; --i >= 0; ) f[i] = Math.IEEEremainder(f[i], Constant.twoPi);
        }
        else {
            f[0] = 485868.249036 + t * (1717915923.2178 + t * (31.8792 + t * (0.051635 - t * 0.00024470)));
            f[1] = 1287104.79305 + t * (129596581.0481 + t * (-0.5532 + t * (0.000136 - t * 0.00001149)));
            f[2] = 335779.526232 + t * (1739527262.8478 + t * (-12.7512 + t * (-0.001037 + t * 0.00000417)));
            f[3] = 1072260.70369 + t * (1602961601.2090 + t * (-6.3706 + t * (0.006593 - t * 0.00003169)));
            f[4] = 450160.398036 + t * (-6962890.5431 + t * (7.4722 + t * (0.007702 - t * 0.00005939)));
        
            for(int i=f.length; --i >= 0; ) f[i] = Math.IEEEremainder(f[i] * Unit.arcsec, Constant.twoPi);
        }
    }


    /// Mean anomaly of Moon
    double l() { return f[0]; }
    
    /// Mean anomaly of Sun
    double l1() { return f[1]; }
    
    /// Mean latitude of Moon from Sun
    double F() { return f[2]; }
    
    /// Mean elongation of Moon from Sun
    double D() { return f[3]; }
    
    // Mean longitude of the Moon's ascending node from Simon section 3.4(b.3), precession = 5028.8200 arcsec/cy
    double Omega() { return f[4]; }

}

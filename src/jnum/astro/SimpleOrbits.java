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
import jnum.math.Vector3D;


/**
 * Simple orbital parameters calculations for Sun/Earth and the Solar-System Barycenter.
 * Based on NOVAS C 3.1 solarsystem(), and sun_eph() functions in solsys3.c.
 * 
 * @author Attila Kovacs
 *
 */
public final class SimpleOrbits {
    
    /*
    The arrays below contain masses and orbital elements for the four
    largest planets -- Jupiter, Saturn, Uranus, and Neptune --  (see
    Explanatory Supplement (1992), p. 316) with angles in radians.  These
    data are used for barycenter computations only.
     */
    private static final double[] pm = {1047.349, 3497.898, 22903.0, 19412.2};
    private static final double[] pa = {5.203363, 9.537070, 19.191264, 30.068963};
    private static final double[] pe = {0.048393, 0.054151, 0.047168, 0.008586};
    private static final double[] pj = {0.022782, 0.043362, 0.013437, 0.030878};
    private static final double[] po = {1.755036, 1.984702, 1.295556, 2.298977};
    private static final double[] pw = {0.257503, 1.613242, 2.983889, 0.784898};
    private static final double[] pl = {0.600470, 0.871693, 5.466933, 5.321160};
    private static final double[] pn = {1.450138e-3, 5.841727e-4, 2.047497e-4, 1.043891e-4};

    
    private static final int N_PLANETS = 4;     // Giants only...

    private static double tmass;
    private static Vector3D[] a = Vector3D.createArray(N_PLANETS), b = Vector3D.createArray(N_PLANETS);
    
    private static double mjdBary;
    private static Vector3D pBary = new Vector3D(), vBary = new Vector3D();        // (m), (m/s)
    
    
    static {
        tmass = 1.0 + 5.977e-6;

        double se = Math.sin(EquatorialCoordinates.eps0);
        double ce = Math.cos(EquatorialCoordinates.eps0);

        for (int i = 0; i < N_PLANETS; i++) {
            tmass += 1.0 / pm[i];
            
            // Compute sine and cosine of orbital angles.
            double si = Math.sin(pj[i]);
            double ci = Math.cos(pj[i]);
            double sn = Math.sin(po[i]);
            double cn = Math.cos(po[i]);
            double sw = Math.sin(pw[i] - po[i]);
            double cw = Math.cos(pw[i] - po[i]);
            
            // Compute p and q vectors (see Brouwer & Clemence (1961), Methods of
            // Celestial Mechanics, pp. 35-36.)
            Vector3D p = a[i];
            p.setX(  cw * cn - sw * sn * ci);
            p.setY(( cw * sn + sw * cn * ci) * ce - sw * si * se);
            p.setZ(( cw * sn + sw * cn * ci) * se + sw * si * ce);
            p.scale(pa[i]);
            
            Vector3D q = b[i];
            q.setX( -sw * cn - cw * sn * ci);
            q.setY((-sw * sn + cw * cn * ci) * ce - cw * si * se);
            q.setZ((-sw * sn + cw * cn * ci) * se + cw * si * ce);
            q.scale(pa[i] * Math.sqrt(1.0 - pe[i] * pe[i]));
        }
    }

    
    private static synchronized void toSSBPos(double mjd, Vector3D pos) {
        if (Math.abs(mjd - mjdBary) > 1.0e-06) calcBary(mjd);
        pos.subtract(pBary);
    }
     
    
    private static synchronized void toSSBVel(double mjd, Vector3D vel) {
        if (Math.abs(mjd - mjdBary) > 1.0e-06) calcBary(mjd);
        vel.subtract(vBary);
    }
    
    private static synchronized void calcBary(double mjd) {
        pBary.zero();
        vBary.zero();
        
        Vector3D p = new Vector3D(), v = new Vector3D();
        
        for (int i = 0; i < N_PLANETS; i++) {
            // Compute mean longitude, mean anomaly, and eccentric anomaly.
            double e = pe[i];
            double mlon = pl[i] + pn[i] * (mjd - AstroTime.MJDJ2000);
            double ma = Math.IEEEremainder((mlon - pw[i]), Constant.twoPi);
            double u = ma + e * Math.sin(ma) + 0.5 * e * e * Math.sin(2.0 * ma);
            double sinu = Math.sin(u);
            double cosu = Math.cos(u);

            // Compute velocity factor.
            double anr = pn[i] / (1.0 - e * cosu);
            
            Vector3D A = a[i];
            Vector3D B = b[i];

            // Compute planet's position and velocity wrt eq & eq J2000.
            p.zero();
            p.addScaled(A, (cosu - e));
            p.addScaled(B, sinu);
            
            v.zero();
            v.addScaled(A,  -sinu);
            v.addScaled(B, cosu);
            v.scale(anr);
           
            // Compute mass factor and add in to total displacement.
            double f = 1.0 / (pm[i] * tmass);
            pBary.addScaled(p, f);
            vBary.addScaled(v, f);
        }
        
        pBary.scale(Unit.AU);
        vBary.scale(Unit.AU / Unit.day);
        
        mjdBary = mjd;
    }

    /**
     * An orbital body in the simple orbital model.
     * 
     * @author Attila Kovacs
     *
     */
    public static abstract class Body {
        private Body() {}
        
        /**
         * Gets the heliocentric equatorial rectangular coordinates of the orbiting body.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @return      (m) Heliocentric equatorial position vector.
         */
        public final Vector3D getPos(double mjd) {
            Vector3D pos = new Vector3D();
            getPos(mjd, pos);
            return pos;
        }
  
        /**
         * Gets the heliocentric equatorial rectangular velocity of the orbiting body.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @return      (m/s) Heliocentric equatorial velocity vector.
         */
        public final Vector3D getVel(double mjd) {
            Vector3D v = new Vector3D();
            getVel(mjd, v);
            return v;
        }
       
        /**
         * Gets the Solar-system barycentric equatorial rectangular coordinates of the orbiting body.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @return      (m) Solar-system barycentric equatorial position vector.
         */
        public final Vector3D getSSBPos(double mjd) {
            Vector3D pos = new Vector3D();
            getSSBPos(mjd, pos);
            return pos;
        }
  
        /**
         * Gets the Solar-system barycentric equatorial rectangular velocity of the orbiting body.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @return      (m/s) Solar-system barycentric equatorial velocity vector.
         */
        public final Vector3D getSSBVel(double mjd) {
            Vector3D v = new Vector3D();
            getSSBVel(mjd, v);
            return v;
        }
        
        
        /**
         * Gets the heliocentric equatorial rectangular coordinates of the orbiting body, and returns
         * the result in the supplied 3D vector.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param pos   (m) Heliocentric equatorial velocity vector.
         */
        public void getPos(double mjd, Vector3D pos) {
            if(mjd < 0.0 || mjd > 100000.0) throw new IllegalArgumentException("MJD " + mjd + " is out of range.");
            getSolarPos(mjd, pos);
        }

        /**
         * Gets the Solar-system barycentric equatorial rectangular coordinates of the orbiting body.
         * and returns the result in the supplied 3D vector.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param pos   (m) Solar-system barycentric equatorial position vector.
         */
        public void getSSBPos(double mjd, Vector3D pos) {
            getPos(mjd, pos);
            toSSBPos(mjd, pos);
        }

        /**
         * Gets the heliocentric equatorial rectangular coordinates of the orbiting body, and returns
         * the result in the supplied 3D vector. (MJD parameter is not checked for range).
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param pos   (m) Heliocentric equatorial velocity vector.
         */
        abstract void getSolarPos(double mjd, Vector3D pos);
        
        /**
         * Gets the heliocentric equatorial rectangular velocity of the orbiting body, and returns
         * the result in the supplied 3D vector.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param vel   (m/s) The returned heliocentric equatorial velocity vector.
         */
        public void getVel(double mjd, Vector3D vel) {
            if (mjd < 0.0 || mjd > 100000.0) throw new IllegalArgumentException("MJD " + mjd + " is out of range.");
            getSolarVel(mjd, vel);
        }

        /**
         * Gets the Solar-system barycentric equatorial rectangular velocity of the orbiting body.
         * and returns the result in the supplied 3D vector.
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param vel   (m/s) Solar-system barycentric equatorial velocity vector.
         */
        public void getSSBVel(double mjd, Vector3D vel) {
            getPos(mjd, vel);
            toSSBVel(mjd, vel);
        }
        
        /**
         * Gets the heliocentric equatorial rectangular velocity of the orbiting body, and returns
         * the result in the supplied 3D vector. (MJD parameter is not checked for range)
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param pos   (m/s) The 3D equatorial rectangular vector in which to return the Heliocentric velocity.
         */
        abstract void getSolarVel(double mjd, Vector3D pos);
    }


    /**
     * The Sun in the simple orbital model. Adapted from NOVAS C 3.1.
     * 
     * @author Attila Kovacs
     *
     */
    public static class Sun extends Body {
        
        private Sun() {}

        @Override
        void getSolarPos(double mjd, Vector3D pos) {
            pos.zero();
        }

        @Override
        void getSolarVel(double mjd, Vector3D vel) {
            vel.zero();
        }
        
        /**
         * Gets the approximate geocentric coordinates of the Sun in Dynamical (CIRS) coordinates of the date.
         * 
         * Note, however, that the coordinate system is tagged as FK5(J2000) for simplicity since it never gets
         * used outside of the context of the enclosing class anyway).
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @return      Equatorial coordinates of the sun
         */
        public EquatorialCoordinates getICRS(double mjd) {
            EquatorialCoordinates eq = new EquatorialCoordinates();
            getICRS(mjd, eq);
            return eq;
        }
        
        /**
         * Adapted from NOVAS 3.1 sun_eph() function in solsys3.c
         * 
         * @param mjd   (day) Modified Julian date of observation
         * @param eq    Equatorial coordinate object to populate with result.
         * @return      (m) Geocentric distance to Sun
         */
        public double getICRS(double mjd, EquatorialCoordinates eq) {
            double sum_lon = 0.0, sum_r = 0.0;
            
            // Define the time units 'u', measured in units of 10000 Julian years
            // from J2000.0, and 't', measured in Julian centuries from J2000.0.
            double t = (mjd - AstroTime.MJDJ2000) / AstroTime.julianCenturyDays;
            double u = 0.01 * t;

            // Compute longitude and distance terms from the series.
            for (double[] C : con) {
                double arg = C[ALPHA] + C[NU] * u;
                sum_lon += C[L] * Math.sin(arg);
                sum_r += C[R] * Math.cos(arg);
            }
            
            sum_lon *= 1e-7;
            sum_r *= 1e-7;

            // Compute longitude, latitude, and distance referred to mean equinox
            // and ecliptic of date.  Apply correction to longitude based on a
            // linear fit to DE405 in the interval 1900-2100.
            double lon = 4.9353929 + 62833.1961680 * u + sum_lon;
            lon += (-0.1371679461 - 0.2918293271 * t) * Unit.arcsec;

            lon = Math.IEEEremainder(lon, Constant.twoPi);
            if (lon < 0.0) lon += Constant.twoPi;

            // Compute mean obliquity of the ecliptic.
            double emean = (84381.406 + (-46.836769 + (-0.0001831 + 0.00200340 * t) * t) * t) * Unit.arcsec;

            // Compute equatorial spherical coordinates referred to the mean equator
            // and equinox of date.
            double sin_lon = Math.sin(lon);
            
            eq.setRA(Math.atan2((Math.cos(emean) * sin_lon), Math.cos(lon)));
            eq.setDEC(Math.asin(Math.sin(emean) * sin_lon));
            eq.setSystem(EquatorialSystem.FK5.J2000);
            
            return (1.0001026 + sum_r) * Unit.AU;
        }

        private static final int L = 0;
        private static final int R = 1;
        private static final int ALPHA = 2;
        private static final int NU = 3;
        
        
        // l, r, alpha, nu
        private final double[][] con = {
                {403406.0,      0.0, 4.721964,     1.621043},
                {195207.0, -97597.0, 5.937458, 62830.348067},
                {119433.0, -59715.0, 1.115589, 62830.821524},
                {112392.0, -56188.0, 5.781616, 62829.634302},
                {  3891.0,  -1556.0, 5.5474  , 125660.5691 },
                {  2819.0,  -1126.0, 1.5120  , 125660.9845 },
                {  1721.0,   -861.0, 4.1897  ,  62832.4766 },
                {     0.0,    941.0, 1.163   ,      0.813  },
                {   660.0,   -264.0, 5.415   , 125659.310  },
                {   350.0,   -163.0, 4.315   ,  57533.850  },
                {   334.0,      0.0, 4.553   ,    -33.931  },
                {   314.0,    309.0, 5.198   , 777137.715  },
                {   268.0,   -158.0, 5.989   ,  78604.191  },
                {   242.0,      0.0, 2.911   ,      5.412  },
                {   234.0,    -54.0, 1.423   ,  39302.098  },
                {   158.0,      0.0, 0.061   ,    -34.861  },
                {   132.0,    -93.0, 2.317   , 115067.698  },
                {   129.0,    -20.0, 3.193   ,  15774.337  },
                {   114.0,      0.0, 2.828   ,   5296.670  },
                {    99.0,    -47.0, 0.52    ,  58849.27   },
                {    93.0,      0.0, 4.65    ,   5296.11   },
                {    86.0,      0.0, 4.35    ,  -3980.70   },
                {    78.0,    -33.0, 2.75    ,  52237.69   },
                {    72.0,    -32.0, 4.50    ,  55076.47   },
                {    68.0,      0.0, 3.23    ,    261.08   },
                {    64.0,    -10.0, 1.22    ,  15773.85   },
                {    46.0,    -16.0, 0.14    ,  188491.03  },
                {    38.0,      0.0, 3.44    ,   -7756.55  },
                {    37.0,      0.0, 4.37    ,     264.89  },
                {    32.0,    -24.0, 1.14    ,  117906.27  },
                {    29.0,    -13.0, 2.84    ,   55075.75  },
                {    28.0,      0.0, 5.96    ,   -7961.39  },
                {    27.0,     -9.0, 5.09    ,  188489.81  },
                {    27.0,      0.0, 1.72    ,    2132.19  },
                {    25.0,    -17.0, 2.56    ,  109771.03  },
                {    24.0,    -11.0, 1.92    ,   54868.56  },
                {    21.0,      0.0, 0.09    ,   25443.93  },
                {    21.0,     31.0, 5.98    ,  -55731.43  },
                {    20.0,    -10.0, 4.03    ,   60697.74  },
                {    18.0,      0.0, 4.27    ,    2132.79  },
                {    17.0,    -12.0, 0.79    ,  109771.63  },
                {    14.0,      0.0, 4.24    ,   -7752.82  },
                {    13.0,     -5.0, 2.01    ,  188491.91  },
                {    13.0,      0.0, 2.65    ,     207.81  },
                {    13.0,      0.0, 4.98    ,   29424.63  },
                {    12.0,      0.0, 0.93    ,      -7.99  },
                {    10.0,      0.0, 2.21    ,   46941.14  },
                {    10.0,      0.0, 3.59    ,     -68.29  },
                {    10.0,      0.0, 1.50    ,   21463.25  },
                {    10.0,     -9.0, 2.55    ,  157208.40  }
        };


    }

    /**
     * Earth in the simple orbital model, including perturbation by the giants. Adapted from NOVAS C 3.1
     * 
     * @author Attila Kovacs
     *
     */
    public static class Earth extends Body {

        private Earth() {}

        @Override
        void getSolarPos(double mjd, Vector3D pos) {
            EquatorialCoordinates eq = new EquatorialCoordinates();
            Sun.getICRS(mjd, eq);
            eq.toCartesian(pos);
            pos.scale(-Unit.AU);
        }
        
        
        @Override
        void getSolarVel(double mjd, Vector3D vel) {           
            final double dt = 0.1;
            vel = Sun.getICRS(mjd + dt).toCartesian();
            vel.subtract(Sun.getICRS(mjd - dt).toCartesian());
            vel.scale(0.5 / (dt * Unit.day));
        }
    }
  
    /** The orbital model for the Sun. */
    public static final Sun Sun = new Sun();
    
    /** The orbital model for Earth. */
    public static final Earth Earth = new Earth();


}

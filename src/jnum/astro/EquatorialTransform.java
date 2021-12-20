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
import jnum.Util;
import jnum.astro.EquatorialSystem.*;
import jnum.math.Transform3D;
import jnum.math.Vector2D;
import jnum.math.Vector3D;


/**
 * Transformation of equatorial coordinates between different reference systems.
 * 
 * @author Attila Kovacs
 *
 */
public class EquatorialTransform extends Transform3D<EquatorialCoordinates> {
    /**
     * 
     */
    private static final long serialVersionUID = 3130499937543818368L;

    private EquatorialSystem from, to;
    private Precision precision;          // Precision of the nutation model, e.g. {@link Precsion#PRECISE} or {@link Precision#TRUNCATED_100UAS}...
    
    private int toGCRS = 0;         // > 0 if therev is a ICRS->GCRS tranform involved (possibly intermediate), or < 0 for reverse.
    private Vector3D dvEarth;       // Relative Earth velocity between frames (for aberration)
    private Vector3D dpEarth;       // Relative Earth position between frames (for parallax, and deflection)

    private Vector3D pEarth;        // Earth's position for gravitational deflection.
    private Vector3D pSun;          // Sun's position for gravitational deflection.

    
    /**
     * Creates a new tranform between two equatorial systems, with the specified precision constant for nutation.
     * 
     * @param from          Equatorial system for input coordinates/vectors
     * @param to            Equatorial system for output coordinates/vectors.
     * @param precision     Nutation precision constant (0 for default, or else {@link Precision#PRECISE} or {@link Precision#TRUNCATED_100UAS} etc...)
     */
    public EquatorialTransform(EquatorialSystem from, EquatorialSystem to, Precision precision) {
        this.from = from;
        this.precision = precision;
        
        dvEarth = new Vector3D();
        dpEarth = new Vector3D();
        
        transform(from, to);        
    }
    
    /**
     * Creates a new tranform between two equatorial systems, with the {@link Precision#TRUNCATED_100UAS} precision 
     * IAU2000AR06 nutation model. (I.e. nutation terms with amplitude smaller than 100uas omitted).
     * 
     * @param from          Equatorial system for input coordinates/vectors
     * @param to            Equatorial system for output coordinates/vectors.
     */
    public EquatorialTransform(EquatorialSystem from, EquatorialSystem to) {
        this(from, to, Precision.TRUNCATED_100UAS);
    }
    
    /**
     * Gets the system for input coordinates/vectors.
     * 
     * @return      Equatorial system of input coordinates
     */
    public EquatorialSystem fromSystem() { return from; }
    
    /**
     * Gets the system for input coordinates/vectors.
     * 
     * @return      Equatorial system of input coordinates
     */
    public EquatorialSystem toSystem() { return to; }
    
    @Override
    public void inverse() {
        super.inverse();
        toGCRS *= -1;
        dvEarth.flip();
        dpEarth.flip();
        EquatorialSystem tmp = from;
        from = to;
        to = tmp;
    }
    
    @Override
    public Vector3D getTransformed(Vector3D v) {
       return getTransformed(v, 0);
    }
    
    
    /**
     * Frame transformation, including annual aberration, gravitational deflection by the Sun, and
     * parallax correction for nearby stellar objects as needed.
     * 
     * @param v         (arb.u) 3D position vector defining direction (magnitude irrelevant) 
     * @param distance  (m) Distance of object. Parallax will be calculated as necessary for
     *                  objects more distant than 1 ly. A distance &lt;= 0 can be used for
     *                  extragalactic sources, or other distant objects for which parallax
     *                  can be neglected. 
     *                  
     * @return          the transformed rectangualr equatorial vector.
     */
    public Vector3D getTransformed(Vector3D v, double distance) {
        v = v.copy();

        if(from instanceof Topocentric) aberration(v, ((Topocentric) from).getEquatorialMotion(), -1);
        
        if(toGCRS > 0) {
            if(distance > Unit.lightYear) parallax(v, distance, dpEarth);
            aberration(v, dvEarth, 1);  // dvEarth carries the sign.
            bend(v, distance);
        }
 
        v = super.getTransformed(v);
        
        if(toGCRS < 0) {
            unbend(v, distance);
            aberration(v, dvEarth, 1);  // dVEarth carries the sign
            if(distance > Unit.lightYear) parallax(v, distance, dpEarth);
        }
        
        if(to instanceof Topocentric) aberration(v, ((Topocentric) to).getEquatorialMotion(), 1);
        
        return v;
    }
    
    
    /**
     * Frame transformation, including parallax correction for nearby stellar objects as needed.
     * 
     * @param eq        (rad) Equatorial coordinates to transform.
     * @param distance  (m) Distance of object. Parallax will be calculated as necessary for
     *                  objects more distant than 1 ly.
     */
    public void transform(EquatorialCoordinates eq, double distance) {
        if(!eq.getSystem().equals(from)) 
            throw new IllegalArgumentException("Transform expects " + from + " but coordinates are in " + eq.getSystem());
        eq.fromCartesian(getTransformed(eq.toCartesian(), distance));
        eq.setSystem(to);
    }
    
    @Override
    public void transform(EquatorialCoordinates eq) {
        if(!eq.getSystem().equals(from)) 
            throw new IllegalArgumentException("Transform expects " + from + " but coordinates are in " + eq.getSystem());
        super.transform(eq);
        eq.setSystem(to);
    }
    
 
    private void transform(EquatorialSystem from, EquatorialSystem to) {
        if(from.equals(to)) return; 

        // If same system, then the difference must be precession (and nutation) only...
        if(from.getClass().equals(to.getClass())) {
            if(to instanceof GCRS) {
                GCRStoICRS(from.getJulianYear());
                ICRStoGCRS(to.getJulianYear());
            }
            else if(to instanceof Topocentric) {
                topocentricToDynamical((Topocentric) from);
                dynamicalToTopocentric((Topocentric) to);
            }
            else if(to instanceof Dynamical) {
                precess(from.getJulianYear(), to.getJulianYear());
                nutate(from.getJulianYear(), to.getJulianYear());
            }
            else if((to instanceof FK4) || (to instanceof FK5)) precessFK5(from.getJulianYear(), to.getJulianYear());
            else {
                Util.warning(this, "Unimplemented transform from " + from.getClass().getSimpleName() + " to " + to.getClass().getSimpleName() + ".");
                return;
            }
        }
        
        else if(to.equals(ICRS)) {
            if(from instanceof GCRS) {
                GCRStoICRS(from.getJulianYear());
            }
            else if((from instanceof FK4) || (from instanceof FK5)) {
                precessFK5(from.getJulianYear(), 2000.0);
                FK5toICRS();
            }
            else {
                transform(from, Dynamical.J2000);            // GCRS(J2000) = ICRS
                J2000toICRS();
            }
        }
        
        else if(to instanceof GCRS) {
            if(!from.equals(ICRS)) transform(from, ICRS);
            ICRStoGCRS(to.getJulianYear());
        }

        else if(to instanceof Dynamical) {
            if(from instanceof Topocentric) {
                Topocentric local = (Topocentric) from;
                topocentricToDynamical(local);
                nutate(from.getJulianYear(), to.getJulianYear());
                precess(from.getJulianYear(), to.getJulianYear());
            }
            else {
                transform(from, ICRS);
                ICRStoGCRS(to.getJulianYear());
                ICRStoJ2000();
                transform(Dynamical.J2000, to);
            }
        }

        else if((to instanceof FK4) || (to instanceof FK5)) {
            if(!(from instanceof FK4) && !(from instanceof FK5)) {
                transform(from, ICRS);
                ICRStoFK5();
            }
            precessFK5(from.getJulianYear(), to.getJulianYear());
        }

        else if(to instanceof Topocentric) {
            Topocentric local = (Topocentric) to;
            transform(from, Dynamical.J2000);
            precess(from.getJulianYear(), to.getJulianYear());
            nutate(from.getJulianYear(), to.getJulianYear());
            dynamicalToTopocentric(local);
        }
        
        else throw new IllegalArgumentException("Transformation to " + to.getClass().getSimpleName() + " is not supported.");
        
        this.to = to;
    }
    
    
    private void ICRStoGCRS(double julianYear) {
        double mjd = AstroTime.MJDJ2000 + (julianYear - 2000.0) * AstroTime.julianYearDays;
         
        // For deflection
        pEarth = SimpleOrbits.Earth.getSSBPos(mjd); 
        pSun = SimpleOrbits.Sun.getSSBPos(mjd);
        
        // for aberration
        dvEarth.add(SimpleOrbits.Earth.getSSBVel(mjd));
        
        // for parallax
        dpEarth.add(pEarth);
        
        toGCRS++;
       
        // TODO deflection (Sun + Earth?)
    }
    
    private void GCRStoICRS(double julianYear) {
        double mjd = AstroTime.MJDJ2000 + (julianYear - 2000.0) * AstroTime.julianYearDays;
       
        // For reverse deflection
        pEarth = SimpleOrbits.Earth.getSSBPos(mjd);
        pSun = SimpleOrbits.Sun.getSSBPos(mjd);
        
        // For reverese aberration
        dvEarth.subtract(SimpleOrbits.Earth.getSSBVel(mjd));
        
        // for reverese parallax
        dpEarth.add(pEarth);
        
        toGCRS--;
        
        // TODO reverse deflection (Sun + Earth?)
    }
    
    private void dynamicalToTopocentric(Topocentric local) {
        AstroTime time = local.getTime();
        
        // Wobble correction (ITRS -> TIRS)
        Vector2D w = local.getPolarWobble();
        w.rotate(local.getApproximateERA());
        
        double s1 = -47.0 * Unit.uas * (time.MJD() - AstroTime.millisJ2000) / AstroTime.julianCenturyDays;
        smallRotate(-w.y(), -w.x(), s1);
    }
    
    
    private void topocentricToDynamical(Topocentric local) {
        AstroTime time = local.getTime();
       
        // Reverse wobble correction (ITRS -> TIRS)
        Vector2D w = local.getPolarWobble();
        w.rotate(-local.getApproximateERA());
        
        double s1 = -47.0 * Unit.uas * (time.MJD() - AstroTime.millisJ2000) / AstroTime.julianCenturyDays;
        smallRotate(w.y(), w.x(), -s1);
    }
    
    
    private void ICRStoJ2000() {
        smallRotate(eta0, -xi0, -dalpha0);
    }

    private void J2000toICRS() {
        smallRotate(-eta0, xi0, dalpha0);
    }
    
    private void ICRStoFK5() {
        smallRotate(-fk5X, -fk5Y, -fk5Z);
    }
    
    private void FK5toICRS() {
        smallRotate(fk5X, fk5Y, fk5Z);
    }
    
    
    /**
     * Adapted from SOFA iauAb routine, w/o gravitational bending, which is treated separately.
     * 
     * @param pos           (arb.u.) Source's position vector
     * @param vEquatorial   (m/s) Motion in equatorial rectangular coordinates
     * @param sign          >=0 to correct position for aberration due to motion, or <0 ro undo.
     */
    private void aberration(Vector3D pos, Vector3D vEquatorial, int sign) {
        if(vEquatorial == null) return;
        
        double v = vEquatorial.length();
        if(v == 0.0) return;
        
        double beta = v / Constant.c;
        double iGamma = Math.sqrt(1.0 - beta * beta);
        double pv = pos.dot(vEquatorial);
        if(sign < 0) pv *= -1;
        
        double w1 = 1.0 + pv/(1.0 + iGamma);
        
        Vector3D p1 = pos.copy();
        p1.scale(iGamma);
        p1.addScaled(vEquatorial, w1);
        p1.addScaled(pos,  -pv);
        p1.normalize();
    }
    
    /**
     * Parallax correction.
     * 
     * @param pos       (arb.u) Equatorial rectangular coordinates (for direction only)
     * @param distance  (m) Object's distance from Solar-system
     * @param pEarth    (m) Equatorial rectangular position of Earth relative to Solar-System Barycenter
     *                  at the time of observation.
     */
    private void parallax(Vector3D pos, double distance, Vector3D pEarth) {
        Vector3D par = pEarth.copy();
        par.projectOn(pos);
        Vector3D perp = Vector3D.differenceOf(pEarth, par);
        perp.scale(1.0 / distance);
        pos.subtract(perp);
    }
    
    /**
     * Gravitational deflection for observer (at geocenter). Currently for Sun only.
     * 
     * @param pos   (arb.u.) Equatorial rectangular coordinates of observer.
     */
    private void bend(Vector3D pos, double distance) {
        if(pSun != null) bend(pos, distance, pEarth, pSun, RMASS[10]);
    }
    
    /**
     * Undo gravitational deflection for observer (at geocenter). Currently for Sun only.
     * 
     * @param pos   (arb.u.) Equatorial rectangular coordinates of observer.
     */
    private void unbend(Vector3D pos, double distance) {
        if(pSun != null) bend(pos, distance, pEarth, pSun, -RMASS[10]);
    }
    
    /**
     * Gravitational bending by a solar-system body. Loosely based on NOVAS 3.1 grav_vec() function.
     * 
     * @param dir       (arb. u.) Direction of observed source (at the time of emission for solar-system objects)
     * @param distance  (m) Distance of object from observer. For extrasolar objects distances >1 ly or <=0 can both be used effectively.
     * @param obs       (m) Equatorial rectangualr coordinates of observer.
     * @param body      (m) Equatorial rectangular coordinates of deflecting mass (at the time of light passing).
     * @param rmass     Reciprocal relative mass of gravitating body in solar mass units, that is, Sun mass / body mass.
     *
     *
     */
    private void bend(Vector3D dir, double distance, Vector3D obs, Vector3D body, double rmass) {
        // Construct unit vector 'q' from gravitating body to observed object and
        // construct unit vector 'e' from gravitating body to observer.        
        Vector3D e = Vector3D.differenceOf(obs, body);
        Vector3D q;                     // Unit vector from source to gravitating body
        
        double le = e.length();
        if(le == 0.0) return;           // We are observing from the gravitating body. Nothing to do.
            
        // Make p the unit source vector.
        Vector3D p = dir.copy();
        double l = dir.length();
        if(l != 0.0) p.scale(1.0 / l);
        
        if(distance > 0.0 && distance < Unit.lightYear) {
            // For solar system body, calculate q as the actual source to gravitating body unit vector.
            Vector3D pos = p.copy();
            pos.scale(distance);
            q = Vector3D.sumOf(e, pos);
   
            double lq = q.length();
            if(lq == 0.0) return;       // We are observing the gravitating body. Nothing to do.
        }
        else {
            // For extrasolar, q is just the unit vector from the source...
            q = p.copy();
            q.flip();    
        }
        
        // Make e a unit vector from observer to gravitating body.
        e.scale(1.0 / le);
        
        double eq = e.dot(q);

        // If gravitating body is observed object, or is on a straight line
        // toward or away from observed object to within 1 arcsec, deflection
        // is set to zero; set 'pos2' equal to 'pos1'.
        if (eq < -0.99999999999) return;

        // Compute scalar factors.
        double f = 2.0 * GS / (Constant.c2 * le * rmass) / (1.0 + eq);

        // Construct corrected position vector 'pos2'.
        p.addScaled(e, f * p.dot(q));
        p.addScaled(q, f * e.dot(p));

        dir.copy(p);
        dir.scale(l);       // Restore original normalization (just in case...)
    }
    
    
    // Updated precession for IERS 2003 convention   
    // See e.g. Capitaine et al., A&A, 412, 567 (2003)
    // Constants from Lieske et al., A&A, 58, 1 (1977)
    private void precess(double fromEpoch, double toEpoch) {
        final double T = 0.01 * (fromEpoch - 2000.0);
        final double t = 0.01 * (toEpoch - fromEpoch);
         
        final double epsAbar = EquatorialCoordinates.eps0 + T * (-46.8150 + T * (-0.00059 + T * 0.001813)) * Unit.arcsec;
        final double omegaA = epsAbar + t * t * ((0.05127 - T * 0.009186) - t * 0.007726) * Unit.arcsec;
        final double psiA = t * ( ((5038.7784 + T * (0.49263 - T * 0.000124))) + t * ((-1.07259 - T * 0.001106) - t * 0.001147) ) * Unit.arcsec;      
        final double chiA = t * ( (10.5526 + T * (-1.88623 + T * 0.000096)) + t * ((-2.38064 - T * 0.000833) - t * 0.001125) ) * Unit.arcsec;
       
        R1(EquatorialCoordinates.eps0);
        R3(-psiA);
        R1(-omegaA);
        R3(chiA);
    }
    
    private void nutate(double fromEpoch, double toEpoch) {
        double fromMJD = AstroTime.MJDJ2000 + fromEpoch * 365.25; 
        double toMJD = AstroTime.MJDJ2000 + toEpoch * 365.25;
        
        final double T = 0.01 * (fromEpoch - 2000.0);
        final double t = 0.01 * (toEpoch - fromEpoch);
        
        final double epsAbar = EquatorialCoordinates.eps0 + T * (-46.8150 + T * (-0.00059 + T * 0.001813));
        final double epsA = epsAbar + t * ((-46.8150 + T * (-0.00117 + T * 0.005439)) + t * (-0.00059 + T * 0.005439 + t * 0.001813)); 
           
        Vector2D v = null;
        
        switch(precision) {
        case PRECISE: 
            v = Nutation.getPrecise(toMJD);
            v.subtract(Nutation.getPrecise(fromMJD));
            break;
        case TRUNCATED_1MAS:
            v = Nutation.getTruncated1mas(toMJD);
            v.subtract(Nutation.getTruncated1mas(fromMJD));
            break;
        case TRUNCATED_10MAS:
            v = Nutation.getTruncated10mas(toMJD);
            v.subtract(Nutation.getTruncated10mas(fromMJD));
            break;
        default:
            v = Nutation.getTruncated100uas(toMJD);
            v.subtract(Nutation.getTruncated100uas(fromMJD));
        }
       
        R1(epsA * Unit.arcsec);
        R3(-v.x());                         // R3(-dpsi)
        R1(-(epsA * Unit.arcsec + v.x()));  // R1(-(epsA + deps));
    }
    
    
    // Precession from Lederle & Schwan, Astronomy and Astrophysics, 134, 1-6 (1984)     
    private void precessFK5(double fromEpoch, double toEpoch) {
        final double T = 0.01 * (fromEpoch - 2000.0);
        final double t = 0.01 * (toEpoch - fromEpoch);

        final double eta = (2305.6997 + (1.39744 + 0.000060 * T) * T + (0.30201 - 0.000270 * T + 0.017996 * t) * t) * t;
        final double z = (2305.6997 + (1.39744 + 0.000060 * T) * T + (1.09543 + 0.000390 * T + 0.018326 * t) * t) * t;
        final double theta = (2003.8746 - (0.85405 + 0.000370 * T) * T - (0.42707 + 0.000370 * T + 0.041803 * t) * t) * t; 

        R3(-eta * Unit.arcsec);
        R2(theta * Unit.arcsec);
        R3(-z * Unit.arcsec);
    }
    
    
    // The astronomical convention from Mueller 1969 is to use clockwise rotations
    // For R1, R2, R2, when looking towards the origin...
    private void R1(double angle) { Rx(-angle); }
    
    private void R2(double angle) { Ry(-angle); }
    
    private void R3(double angle) { Rz(-angle); }
    
    /**
     * The ICRS reference system.
     * 
     */
    public static final EquatorialSystem ICRS = EquatorialSystem.ICRS;
    
    /**
     * Readily available transform for FK4(B1950) to FK5(J2000) coordinate transformations
     * 
     */
    public static final EquatorialTransform B1950toJ2000 = new EquatorialTransform(EquatorialSystem.FK4.B1950, EquatorialSystem.FK5.J2000);

    /**
     * Readily available transform for FK5(J2000) to FK4(B9150) coordinate transformations
     * 
     */
    public static final EquatorialTransform J2000toB1950 = new EquatorialTransform(EquatorialSystem.FK5.J2000, EquatorialSystem.FK4.B1950);
    
    /**
     * Readily available transform for FK4(B1950) to ICRS coordinate transformations
     * 
     */
    public static final EquatorialTransform B1950toICRS = new EquatorialTransform(EquatorialSystem.FK4.B1950, EquatorialSystem.ICRS);
    
    /**
     * Readily available transform for FK5(J2000) to ICRS coordinate transformations
     * 
     */
    public static final EquatorialTransform J2000toICRS = new EquatorialTransform(EquatorialSystem.FK5.J2000, EquatorialSystem.ICRS);
    
    /**
     * Readily available transform for ICRS to FK4(B1950) coordinate transformations
     * 
     */
    public static final EquatorialTransform ICRStoB1950 = new EquatorialTransform(EquatorialSystem.ICRS, EquatorialSystem.FK4.B1950);
    
    /**
     * Readily available transform for ICRS to FK5(J2000) coordinate transformations
     * 
     */
    public static final EquatorialTransform ICRStoJ2000 = new EquatorialTransform(EquatorialSystem.ICRS, EquatorialSystem.FK5.J2000);
      
    
    final private static double eta0 = -6.8192 * Unit.mas;     ///< Tilt of J2000 vs ICRS around x
    final private static double xi0 = -16.6170 * Unit.mas;     ///< Tilt of J2000 vs ICRS around y
    final private static double dalpha0 = -14.60 * Unit.mas;   ///< Tilt of J2000 vs ICRS around z


    /**
     * Orientation of Hipparcos ICRS (HCRS) relative to FK5
     */
    final private static double fk5X = -19.9 * Unit.mas;     ///< Tilt of FK5 vs ICRS(Hipparcos) around x
    final private static double fk5Y = -9.1 * Unit.mas;      ///< Tilt of FK5 vs ICRS(Hipparcos) around y
    final private static double fk5Z = 22.9 * Unit.mas;      ///< Tilt of FK5 vs ICRS(Hipparcos) around z
 
   
    // Heliocentric gravitational constant in meters^3 / second^2, from DE-405.
    // Borrowed from NOVAS 3.1
    private static final double GS = 1.32712440017987e+20;

    // Geocentric gravitational constant in meters^3 / second^2, from DE-405.
    // Borrowed from NOVAS 3.1
    //private static final double GE = 3.98600433e+14;
    
    // Reciprocal masses of solar system bodies, from DE-405 (Sun mass / body mass).
    // MASS[0] = Earth/Moon barycenter, MASS[1] = Mercury, ..., MASS[9] = Pluto, 
    // MASS[10] = Sun, MASS[11] = Moon.
    // borrowed from NOVAS 3.1
    private static final double[] RMASS = {328900.561400, 6023600.0, 408523.71,
       332946.050895, 3098708.0, 1047.3486, 3497.898, 22902.98,
       19412.24, 135200000.0, 1.0, 27068700.387534};
    
    /**
     * Type safe enumeration of the available astrometric precisions for coordinate transformations.
     * 
     * @author Attila Kovacs
     *
     */
    public static enum Precision {
        /**
         * Constant denoting the full-precision nutation calculation (requiring ~1400 terms in both directions).
         * It is is precise but computationally intensive. (Not an issue if you don't need to call it over
         * and over for different observation dates).
         */
        PRECISE(0.0),
        
        /**
         * Default nutation accuracy constant, with nutation terms smaller than 100 uas omitted. A good compromise
         * between speed and precision. as it requires ~15 times fewer calculations than the full-precison model, while
         * still maintaining ~milliarcsec accuracy until 2050...
         */
        TRUNCATED_100UAS(100.0 * Unit.uas),
        
        /**
         * Constant denoting a lesser precision but ~35 times faster nutation calculation than the full-precision
         * model.  
         */
        TRUNCATED_1MAS(1.0 * Unit.mas),
        
        /**
         * Constant denoting the fastest but least precise nutation model. The approximate nutation is calculated 
         * around 100x faster than the full-precision model, and yield sub-arcsecon precision until 2050.
         */
        TRUNCATED_10MAS(10.0 * Unit.mas);
        
        private double cutoff;
        
        Precision(double cutoff) {
            this.cutoff = cutoff;
        }
        
        /**
         * Returns the magnitude of the nutation term, at which the nutation series is truncated for this
         * precision instance.
         * 
         * @return  (rad/centrury) The magnitude of the term at which the nutation series is truncated.
         */
        public final double cutoff() {
            return cutoff;
        }
    }
    
}

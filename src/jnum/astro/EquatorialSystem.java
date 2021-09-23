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


import java.text.ParseException;
import java.util.StringTokenizer;

import jnum.Constant;
import jnum.Unit;
import jnum.Util;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsToolkit;
import jnum.math.PolarVector2D;
import jnum.math.Vector2D;
import jnum.math.Vector3D;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

/**
 * Equatorial coordinate reference system. Due to the precession and nutation of Earth's axis, as well as
 * the motion of Earth's crust and tidal variations, and our changing (improving) understanding of these with more
 * precise measurements (esp. VLBI), equatorial coordinates may be expressed relative to different orientations of
 * Earth's pole (equator), depending on time and/or convention. This class is the base class for the various commonly used
 * equatorial coordinate systems.
 * 
 * 
 * @author Attila Kovacs
 *
 */
public abstract class EquatorialSystem  implements FitsHeaderEditing {
    
    
    protected abstract boolean equals(EquatorialSystem other);

    /**
     * Gets the modified julian date at which the system is referenced. The coordinate system at that
     * date must be reasonably close to the true dynamical Earth equator, at least to the precision
     * that was available at the time the coordinate system convention was created.
     * 
     * @return  (day) Modified Julian Date at which system is referenced.
     */
    public abstract double getReferenceMJD();
    
    /**
     * Returns the standard value for the FITS RADESYSa keyword for representation of WCS in FITS files.
     * 
     * @return  The value to use in FITS for the RADESYSa keyword ofr this coordinate system.
     *          FITS has only a limited selection of admissible values. Not all coordinate systems have
     *          an equivalent FITS RADESYSa convention. When an exact match is not possible, the
     *          function will return the FITS standard value for the system that is the closest
     *          to what this coordinate system represents.
     */
    public abstract String getFITSRadesys();
    
    /**
     * Checks if this system is a precessing system (follows the Earth axis over time), or else if it is
     * a system that does not have a time dependence (e.g. ICRS).
     * 
     * @return  Whether the system has a time dependence that follows Earth precession motion.
     */
    public abstract boolean isPrecessing();

    /**
     * Returns the coordinate epoch for this instance of the coordinate system.
     * 
     * @return  Coordinate epoch object.
     */
    public abstract CoordinateEpoch getEpoch();
    
    /**
     * Checks if this coordinate system is a parent class of another coordinate system.
     * 
     * @param other     Another coordinate system
     * @return          true if the other coordinate system is a sub-class of this one.
     */
    protected boolean isAssignableFrom(EquatorialSystem other) {
        if(other == null) return false;
        return getClass().isAssignableFrom(other.getClass());
    }

    /**
     * Gets the Julian year at which this coordinate system is referenced.
     * 
     * @return  (yr) Julian year, e.g. 2021.3554
     */
    public final double getJulianYear() {
        return 2000.0 + (getReferenceMJD() - AstroTime.MJDJ2000) / AstroTime.julianYearDays;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() ^ HashCode.from(getReferenceMJD());
    }
   
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!getClass().isAssignableFrom(o.getClass())) return false;
        
        EquatorialSystem sys = (EquatorialSystem) o;
        if(!Util.equals(sys.getReferenceMJD(), getReferenceMJD(), 1e-6)) return false;
        return true;
    }
    
    
    
    @Override
    public void editHeader(Header header) throws HeaderCardException {
        editHeader(header, "");
    }
    
    /**
     * Adds a description of this coordinates system to a FITS header using the standard FITS
     * keyworkds for specifying an equatorial reference system.
     * 
     * @param header        The FITS header
     * @param alt           The coordinate system alternative designation (empty string for the
     *                      default coordinate system and 'A' thru 'Z' for the alternative
     *                      coordinate system definitions).
     * @throws HeaderCardException
     */
    public void editHeader(Header header, String alt) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard("RADESYS" + alt, getFITSRadesys(), "Reference convention."));
        if(isPrecessing()) c.add(new HeaderCard("EQUINOX" + alt, getEpoch().getYear(), "Reference epoch."));
    } 
   
    /**
     * Returns the most fitting coordinate system for a given coordinate epoch, either as an
     * FK4 or FK5 system. FK5 if the epoch is a JulianEpoch, or FK4 if epoch is a BesselianEpoch. 
     * For standard epochs (B1900, B1950, J2000) a reference to the matching standard system is 
     * returned. For all other epochs, a new coordinate system is returned.
     * 
     * @param epoch     The coordinate epoch
     * @return          The FK4 or FK5 system that best matches the epoch. 
     */
    public static EquatorialSystem forEpoch(CoordinateEpoch epoch) {
        if(epoch instanceof BesselianEpoch) {
            if(epoch.equals(CoordinateEpoch.B1900)) return FK4.B1900;
            if(epoch.equals(CoordinateEpoch.B1950)) return FK4.B1950;
            return new FK4((BesselianEpoch) epoch);
        }
        if(epoch instanceof JulianEpoch) {
            if(epoch.equals(CoordinateEpoch.J2000)) return FK5.J2000;
            return new FK5((JulianEpoch) epoch);
        }
        throw new IllegalArgumentException("Unsupported epoch: " + epoch.getClass().getSimpleName());
    }
    
    /**
     * Returns the most fitting coordinate system for a goven coordinate year, either as an
     * FK4 or FK5 system. FK5 is assumed for years at or after 1984.0, while FK4 is assumed
     * for epoch prior to that. For standard years (1900, 1950, 2000) a reference to
     * the matching standard system is returned. For all other epochs, a new coordinate
     * system is returned.
     * 
     * @param year      Epoch year, e.g. 2000 for FK5(J2000).
     * @return          A coordinate system appropriate for the given year.
     */
    public static EquatorialSystem forEpochYear(double year) {
        if(year == 1900.0) return new FK4(CoordinateEpoch.B1900);
        else if(year == 1950.0) return new FK4(CoordinateEpoch.B1950);
        else if(year < 1984.0) return new FK4(new BesselianEpoch(year));
        else if(year == 2000.0) return new FK5(CoordinateEpoch.J2000);
        return new FK5(new JulianEpoch(year));
    }
    
    /**
     * Returns the best match coordinate system to what's represented in a FITS header. 
     * 
     * @param header    FITS header
     * @return          The coordinate system described in the FITS header, or else assumed.
     */
    public static EquatorialSystem fromHeader(Header header) {
        return fromHeader(header, "");
    }
   
    
    /**
     * Returns the best match coordinate system to what's represented in a FITS header, with
     * the alternative designation. The function uses a combination of the RADESYSa, EQUINOXa,
     * MJD-OBS, and/or DATE-OBS keys to figure out the most appropriate coordinate system
     * to what is described in the header. If the header does not describe an equatorial
     * coordinate system sufficiently, the {@link #ICRS} will be assumed and returned.
     * 
     * @param header    FITS header
     * @param alt       Alternative coordinate system identifier, typically a letter "a" through "z". Or
     *                  "" (empty string) or null to use the primary coordinate system in FITS.
     *                  
     * @return          The coordinate system described in the FITS header, or else assumed.
     */
    public static EquatorialSystem fromHeader(Header header, String alt) {
        if(alt == null) alt = "";
         
        EquatorialSystem system = null; 
        String id = header.getStringValue("RADESYS" + alt);
        
        if(header.containsKey("EQUINOX" + alt)) id += " " + header.getDoubleValue("EQUINOX");
        
        try { system = EquatorialSystem.forString(id); }
        catch(IllegalArgumentException e) {
            if(header.containsKey("EQUINOX" + alt)) {
                double year = header.getDoubleValue("EQUINOX" + alt);
                system = forEpochYear(year);
            }
            else system = EquatorialSystem.ICRS;
        }
        
        // Use MJD-OBS, if available, to define epoch for Dynamical.
        if(system instanceof Dynamical) {
            Dynamical dynamical = (Dynamical) system;
        
            if(header.containsKey("MJD-OBS")) dynamical.epoch = JulianEpoch.forMJD(header.getDoubleValue("MJD-OBS"));
            else if(header.containsKey("DATE-OBS")) {
                try {
                    AstroTime time = AstroTime.forFitsTimeStamp(header.getStringValue("DATE-OBS"));
                    dynamical.epoch = JulianEpoch.forMJD(time.MJD());
                }
                catch(ParseException e) { Util.warning(EquatorialSystem.class, "No MJD-OBS or DATE-OBS in FITS. Using J" + Util.f12.format(system.getJulianYear()) + "."); }
            }
        }
        
        return system;
    }
    
    /**
     * Returns the best match coordinate system for a String describing the system. The parser is case insensitive. 
     * Some examples for valid strings descriptions are:
     * 
     * <pre>
     *   ICRS               
     *   HCRS                   Hipparcos equivalent to ICRS, mapped to ICRS
     *   BCRS                   mapped to ICRS (assuming proper motions are applied to coordinates)
     *   ""                     empty string defaults to ICRS
     *   FK5 J2000              FK5 at J2000   
     *   fk5 (J2021.333)        FK5 at J2021.3333
     *   FK5                    FK5 at default epoch (J2000)
     *   J2021.243              FK5 at J2021.243  
     *   2021.765               FK5 at 2021.765 (&gt;= 1984.0) 
     *   FK4(1900)              FK4 at B1900
     *   FK4                    FK4 at default epoch (B9150)
     *   FK4 1966.12            FK4 at B1966.12
     *   b1950                  FK4 at B1950 
     *   1966.12                FK4 at 1966.12 (&lt;1984.0)
     *   CIRS 2021.443          Dynamical (CIRS) at J2021.443
     *   ErS(J2021.443)         "
     *   GAPPT 2021.443         "
     *   App 2021.443           "
     *   apparent (J2021.443)   " 
     * </pre>
     * 
     * @param id    String representation of the coordinate system
     * @return      The best match coordinate system
     * @throws IllegalArgumentException  if the coordinate system could not be determined from the
     *                                  string argument.
     */
    public static EquatorialSystem forString(String id) throws IllegalArgumentException {
        id = id.toUpperCase();
        StringTokenizer tokens = new StringTokenizer(id, "()");
        
        if(!tokens.hasMoreTokens()) return null;
        
        
        String s = tokens.nextToken();
        
        
        // ICRS and what is effectively the same... 
        if(s.equals("ICRS")) return ICRS;
        if(s.equals("ICR")) return ICRS;
        if(s.equals("HCRS")) return ICRS;
        if(s.equals("HCR")) return ICRS;
        if(s.equals("BCRS")) return ICRS; // Provided proper motions have been applied 
        if(s.equals("BCR")) return ICRS;
        
        String Y = null;
        double year = Double.NaN;
        
        if(s.charAt(0) == 'J' || s.charAt(0) == 'B') {
            Y = s.substring(1);
            s = s.charAt(0) == 'B' ? s = "FK4" : "";
        }
        else if(s.charAt(0) == '1' || s.charAt(0) == '2') {
            year = Double.parseDouble(s);
            if(year < 1984) s = "FK4";
            else s = "";
        }
        else if(tokens.hasMoreTokens()) {
            Y = tokens.nextToken();
            if(Y.charAt(0) == 'J' || Y.charAt(0) == 'B') Y = Y.substring(1);
        }
        
        if(Double.isNaN(year)) if(Y != null) {
            try { year = Double.parseDouble(Y); }
            catch(NumberFormatException e) {}
        }
        
        if(s.equals("FK4")) {
            BesselianEpoch epoch = CoordinateEpoch.B1950;
            if(year == 1900) epoch = CoordinateEpoch.B1900;
            else if(!Double.isNaN(year)) epoch = new BesselianEpoch(year);
            return new FK4(epoch);
        }
                
                         
        JulianEpoch epoch = Double.isNaN(year) || year == 2000.0 ? CoordinateEpoch.J2000 : new JulianEpoch(year);
        
        // FITS RADESYSa standard names.
        if(s.equals("FK5")) return new FK5(epoch);
        if(s.equals("FK4")) return new FK4(new BesselianEpoch(epoch.getYear()));
        if(s.equals("GAPPT")) return new Dynamical(epoch);        /// Treat geocentric apparent as if CIRS for same date.
        
        // Standard system designation (but not in FITS...)
        if(s.equals("GCRS")) return new GCRS(epoch);
        if(s.equals("CIRS")) return new Dynamical(epoch);
        if(s.equals("ERS")) return new Dynamical(epoch);
        
        // Various other names that might be used...
        if(s.equals("APP")) return new Dynamical(epoch);
        if(s.equals("APPT")) return new Dynamical(epoch);
        if(s.equals("APPARENT")) return new Dynamical(epoch);
        
        // If no system is specified, assume FK5 if epoch is known, or ICRS if no epoch.
        if(s.equals("")) return Double.isNaN(year) ? ICRS : new FK5(epoch);    
        
        throw new IllegalArgumentException("Cannot create equatorial system for: " + id);
    }
         
    /**
     * A reference system that is aligned to the true dynamical equator at some 
     * point in time (epoch), accounting for both precession and nutation.
     * It is effectively the same as CIRS/ERS.
     * 
     */
    public static class Dynamical extends EquatorialSystem {
        private JulianEpoch epoch;
        
        /** 
         * Instantiates a new coordinate system, tied to the dynamical Earth equatorm at the specified Julian epoch. 
         * The epoch determines the location of Earth on its orbit around the Sun, and therefore any
         * parallax or annual aberration corrections needed for a GCRS system at that epoch.
         * 
         * @param epoch     the Julian epoch for which this GCRS system is defined.
         */
        public Dynamical(JulianEpoch epoch) { this.epoch = epoch; }

        @Override
        public JulianEpoch getEpoch() { return epoch; }
        
        @Override
        public double getReferenceMJD() {
            return epoch.getMJD();
        }

        @Override
        protected boolean equals(EquatorialSystem other) {
            if(!isAssignableFrom(other)) return false;
            return Util.equals(epoch.getYear(), ((Dynamical) other).epoch.getYear(), 1e-6);
        }
        
        @Override
        public boolean isPrecessing() { return true; }
        
        @Override
        public String getFITSRadesys() {
            return "GAPPT";                 // TODO There is no FITS designation of CIRS but geocentric apparent
                                            // comes closest, except that true GAPPT is polar wobble corrected.
                                            // Maybe FITS should add a CIRS' designation.
        }
        
        @Override
        public String toString() { return epoch.toString(); }
      
        /** The coordinate system tied to the dynamical Earth equator at J2000 */
        public static final Dynamical J2000 = new Dynamical(CoordinateEpoch.J2000);
    }


    /**
     * The FK4 reference system was used prior to 1984 (when FK5 was introduced to replace
     * it). Coordinates quoted in B1900 or B1950 are typically referenced to FK4.
     * 
     */
    public static  class FK4 extends EquatorialSystem {
        private BesselianEpoch epoch;
        
        /**
         * Instantiates a new FK4 coordinate reference system at the specified Besselian epoch.
         * 
         * @param epoch     the Besselian epoch for which this FK4 reference system is defined.
         */
        public FK4(BesselianEpoch epoch) { this.epoch = epoch; }
        
        @Override
        public BesselianEpoch getEpoch() { return epoch; }
        
        @Override
        public double getReferenceMJD() {
            return epoch.getMJD();
        }
        
        @Override
        protected boolean equals(EquatorialSystem other) {
            if(!isAssignableFrom(other)) return false;
            return Util.equals(epoch.getYear(), ((FK4) other).epoch.getYear(), 1e-6);
        }
        
        @Override
        public boolean isPrecessing() { return true; }
        
        @Override
        public String getFITSRadesys() {
            return "FK4";
        }
        
        @Override
        public String toString() { return "FK4(" + epoch.toString() + ")"; }
        
        /** The coordinate system for the (historically) commonly used B1900 epoch. */
        public static final FK4 B1900 = new FK4(CoordinateEpoch.B1900);
        
        /** The coordinate system for the commonly used B1950 epoch */
        public static final FK4 B1950 = new FK4(CoordinateEpoch.B1950);
    }


    /**
     * The FK5 reference system that was used after 1984, until ICRS came to replace it in 2003.
     * It is still common today. However, the FK5 precession model was imprecise, so it's
     * J2000 equator is not perfectly aligned to the true mean dynamical equator at J2000... 
     *
     */
    public static class FK5 extends EquatorialSystem {
        private JulianEpoch epoch;   
        
        /**
         * Instantiates a new FK5 coordinate reference system at the specified Julian epoch.
         * 
         * @param epoch     the Julian epoch for which this FK5 reference system is defined.
         */
        public FK5(JulianEpoch epoch) { this.epoch = epoch; }

        @Override
        public JulianEpoch getEpoch() { return epoch; }
        
        @Override
        public double getReferenceMJD() {
            return epoch.getMJD();
        }

        @Override
        protected boolean equals(EquatorialSystem other) {
            if(!isAssignableFrom(other)) return false;
            return Util.equals(epoch.getYear(), ((FK5) other).epoch.getYear(), 1e-6);
        }
        
        @Override
        public boolean isPrecessing() { return true; }
        
        @Override
        public String getFITSRadesys() {
            return "FK5";
        }
        
        @Override
        public String toString() { return "FK5(" + epoch.toString() + ")"; }
        
        /** The FK5 coordinate system at J2000 */
        public static final FK5 J2000 = new FK5(CoordinateEpoch.J2000);
    }


    /**
     * GCRS differs from ICRS by including annual aberration, light deflection, and parallax.
     * See Table 5.1, of IERS Technical Note 36, Ch. 5.
     *
     */
    public static class GCRS extends EquatorialSystem {
        private JulianEpoch epoch;
        
        /** 
         * Instantiates a new GCRS coordinate system at the specified Julian epoch. The epoch
         * determines the location of Earth on its orbit around the Sun, and therefore any
         * parallax or annual aberration corrections needed for a GCRS system at that epoch.
         * 
         * @param epoch     the Julian epoch for which this GCRS system is defined.
         */
        public GCRS(JulianEpoch epoch) { this.epoch = epoch; }

        @Override
        public JulianEpoch getEpoch() { return CoordinateEpoch.J2000; }
        
        @Override
        public double getReferenceMJD() {
            return epoch.getMJD();
        }

        @Override
        protected boolean equals(EquatorialSystem other) {
            if(!isAssignableFrom(other)) return false;
            return Util.equals(epoch.getYear(), ((GCRS) other).epoch.getYear(), 1e-6);
        }
        
        @Override
        public boolean isPrecessing() { return false; }
        
        @Override
        public String getFITSRadesys() {
            return "GCRS";
        }
        
        @Override
        public String toString() { return "GCRS(" + Util.f3.format(epoch.getJulianYear()) + ")"; }
        
        /** The GCRS coordinate system at J2000 */
        public static final GCRS J2000 = new GCRS(CoordinateEpoch.J2000);
    }
    
    
    /**
     * Essentially the a topocentric reference system, at a specific geodetic (~GPS) Earth location,
     * and at a specific time (MJD). Compared to the CIRS (Dynamical) system, it includes
     * polar wobble corrections, diurnal aberration corrections for Earth rotation at observer
     * location, as well as aberration correction for the observer's motion relative to the
     * surface (e.g. airplane or balloon).
     *
     */
    public static class Topocentric extends EquatorialSystem {
        private String name;
        private JulianEpoch epoch;
        private GeodeticCoordinates location;
        private Vector2D wobble;        ///< Polar wobble corrections dx, dy
        private AstroTime time;
        private Vector3D vEq;           ///< (m/s) Equatorial velocity vector relative to surface

        /**
         * Constructs a new Topocentric reference system, with the specified ID (e.g. observatory name)
         * Geodetic (~GPS or ~WGS84) location and time.
         * 
         * @param name      A short ID for the location, such as an observatory name, e.g. "CSO"
         * @param location  Geodetic Earth location (~GPS/WGS84). 
         * @param MJD       (day) Modified Julian Date of observation.
         */
        public Topocentric(String name, GeodeticCoordinates location, double MJD) {
            this(name, location, MJD, null);
        }
        
        
        /**
         * Constructs a new Topocentric reference system, with the specified ID (e.g. observatory name)
         * Geodetic (~GPS or ~WGS84) location and time, and polar wobble correction with the xp,yp
         * Parameters published in IERS Bulletin A (a.k.a. ser7.dat).
         * 
         * @param name      A short ID for the location, such as an observatory name, e.g. "CSO"
         * @param location  Geodetic Earth location (~GPS/WGS84). 
         * @param MJD       (day) Modified Julian Date of observation.
         * @param wobble    (rad) Polar wobble parameters (xp,yp) for the date of observation.
         *                  Polar wobble provides correction on the 0.1 arcsecond level.
         */
        public Topocentric(String name, GeodeticCoordinates location, double MJD, Vector2D wobble) {
            this.name = name;
            this.location = location;
         
            time = new AstroTime();
            time.setMJD(MJD);
         
            setPolarWobble(wobble);
            
            epoch = JulianEpoch.forMJD(MJD);
            setSurfaceVelocity(null);
        }
        
        @Override
        public JulianEpoch getEpoch() { return epoch; }
        
        /**
         * Specifies the polar wobble parameters to use to improve precision on the sub-arcsecond level.
         * 
         * @param wobble    (rad) Polar wobble parameters (xp,yp) for the date of observation.
         *                  Polar wobble provides correction on the 0.1 arcsecond level.
         */
        public void setPolarWobble(Vector2D wobble) { this.wobble = (wobble == null) ? new Vector2D() : wobble; }
        
        /**
         * Specify the observer's motion relative to the surface, (e.g. on an airplane or balloon)
         * 
         * @param vGeo  (m/s) Observer's surface motion in geocentric rectangular coordinates.
         */
        public void setSurfaceVelocity(Vector3D vGeo) {
            if(vGeo == null) {
                vEq = new Vector3D();
                return;
            }
            
            PolarVector2D p = location.getGeocentricLatitudeVector();
            double vrot = Constant.twoPi * p.length() * Math.cos(p.angle()) / Unit.siderealDay; 
           
            vEq = vGeo.copy();
            vEq.addY(vrot);       // Add Earth rotation
            vEq.rotateZ(getApproximateERA() + location.longitude());
        }
        
        /**
         * Gets the observers motion relative to the geocenter in equatorial rectangular coordinates.
         * 
         * @return  the equatorial rectangular speed vector of the observer relative to the geocenter.
         */
        public final Vector3D getEquatorialMotion() {
             return vEq;
        }
        
        /**
         * Returns the geodetic location of the observer, such as the observer's GPS/WGS84 coordinates.
         * 
         * @return  the geodetic coordinates of the observer.
         */
        public final GeodeticCoordinates getLocation() {
            return location;
        }
        
        /**
         * Returns the polar wobble dx,dy parameters. These are incrementan corrections over the IAU2000
         * nutation model to correct for un-anticipated polar motion of Earth's rotation axis. Current
         * dx/dy polar wobble values are published in IERS Bulletin A.
         * 
         * @return      (rad) the dx,dy polar wobble (Earth orientation) parameters, such as published in
         *              IERS Bulletin A.
         */
        public final Vector2D getPolarWobble() {
            return wobble;
        }
        
        /**
         * Returns the astrometric time for this topocentric coordinate system. Because topocentric
         * reference systems contain Earth polar wobble corrections and diurnal aberration corrections,
         * both of which rotate with Earth, the
         * topocentric systems precess with a daily period, and are not precise for observations
         * that are not around the time the system was defined (at least on the level of around an hour
         * or beyond.)
         * 
         * @return      the astrometric time for which this topocentric system was defined for.
         */
        public final AstroTime getTime() {
            return time;
        }
        
        /**
         * Returns the approximnate Earth Rotation Angle (ERA) for the tiem for which this topocentric
         * coordinate system was defined. The returned ERA does not contain a UT! correction (%plusmn; 0.5s),
         * which is why it's not a precise rotation measure. However, it is suitable for calculations 
         * where the precise orientation of Earth is irreleant.
         * 
         * @return      the approximate Earth Rotation Angle (ERA), without the UT1-UTC correction.
         */
        public double getApproximateERA() {
            return time.ERA(0.0);
        }
        
        @Override
        protected boolean equals(EquatorialSystem other) {
            if(!isAssignableFrom(other)) return false;
            Topocentric topo = (Topocentric) other;
            if(!Util.equals(location, topo.location)) return false;
            if(!Util.equals(wobble, topo.wobble)) return false;
            if(!Util.equals(topo.time.MJD(), time.MJD())) return false;
            return true;
        }

        @Override
        public double getReferenceMJD() {
            return time.MJD();
        }
        
        @Override
        public boolean isPrecessing() { return true; }
        
        @Override
        public String getFITSRadesys() {
            return "GAPPT";             // TODO There is no FITS designation for topocentric systems.
                                        // GAPPT comes closest except that GAPPT does not include
                                        // diurnal aberration or aberration due to observer's surface motion...
        }
        
        @Override
        public String toString() { return "APP(" + name + "-" + Util.f2.format(getJulianYear()) + ")"; }
    }

    /**
     * The ICRS system.
     * 
     */
    public static final EquatorialSystem ICRS = new EquatorialSystem() {

        @Override
        protected boolean equals(EquatorialSystem other) {
            return isAssignableFrom(other);
        }            
   
        @Override
        public JulianEpoch getEpoch() { return CoordinateEpoch.J2000; }
       
        @Override
        public double getReferenceMJD() {
            return JulianEpoch.J2000.getMJD();
        }

        @Override
        public boolean isPrecessing() { return false; }
        
        @Override
        public String getFITSRadesys() {
            return "ICRS";
        }
        
        @Override
        public String toString() { return "ICRS"; }
  
    };
    
    
 

}




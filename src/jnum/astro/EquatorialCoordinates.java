/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;


import java.text.NumberFormat;

import jnum.Constant;
import jnum.SafeMath;
import jnum.Unit;
import jnum.Util;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import jnum.text.HourAngleFormat;


// TODO: Auto-generated Javadoc
// x, y kept in longitude,latitude form
// use RA(), DEC(), setRA() and setDEC(functions) to for RA, DEC coordinates...

public class EquatorialCoordinates extends PrecessingCoordinates {

    private static final long serialVersionUID = 3445122576647034180L;


    public EquatorialCoordinates() {}

    public EquatorialCoordinates(EquatorialSystem system) { 
        super(system);
    }


    public EquatorialCoordinates(String text) { super(text); }


    public EquatorialCoordinates(double ra, double dec) { 
        super(ra, dec, EquatorialSystem.ICRS); 
    }


    public EquatorialCoordinates(double ra, double dec, String sysSpec) { 
        super(ra, dec, sysSpec); 

    }


    public EquatorialCoordinates(double ra, double dec, EquatorialSystem system) { 
        super(ra, dec, system); 
    }


    public EquatorialCoordinates(CelestialCoordinates from) { super(from); }
    

    @Override
    public EquatorialCoordinates clone() { return (EquatorialCoordinates) super.clone(); }

    @Override
    public EquatorialCoordinates copy() { return (EquatorialCoordinates) super.copy(); }



    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
     */
    @Override
    public String getFITSLongitudeStem() { return "RA--"; }

    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
     */
    @Override
    public String getFITSLatitudeStem() { return "DEC-"; }


    @Override
    public String getTwoLetterCode() { return "EQ"; }



    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }


    public final double RA() { return zeroToTwoPi(longitude()); }


    public final double rightAscension() { return RA(); }


    public final double DEC() { return latitude(); }


    public final double declination() { return DEC(); }


    public final void setRA(double RA) { setLongitude(RA); }


    public final void setDEC(double DEC) { setLatitude(DEC); }


    public double getParallacticAngle(GeodeticCoordinates site, double LST) {
        final double H = LST * Unit.timeAngle - RA();
        return Math.atan2(site.cosLat() * Math.sin(H), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(H));
    }

    @Override
    public void transform(EquatorialTransform t) {
        EquatorialCoordinates equatorial = toEquatorial();
        t.transform(equatorial);
        fromEquatorial(equatorial);
        setSystem(equatorial.getSystem());
    }
    

    
    /**
     * Returns the change in position angle due to precession, relative to the J2000 epoch.
     * 
     * @return
     */
    public double getEpochPosAngle() {
       return precessionPARate * (getSystem().getJulianYear() - 2000.0) * Math.sin(RA()) / cosLat();
    }
    
    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#getEquatorialPositionAngle()
     */
    @Override
    public final double getEquatorialPositionAngle() {
        return 0.0;
    }


    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#toEquatorial(jnum.astro.EquatorialCoordinates)
     */
    @Override
    public void toEquatorial(EquatorialCoordinates equatorial) {
        equatorial.copy(this);	
    }

    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#fromEquatorial(jnum.astro.EquatorialCoordinates)
     */
    @Override
    public void fromEquatorial(EquatorialCoordinates equatorial) {	
        copy(equatorial);
    }


    public HorizontalCoordinates toHorizontal(GeodeticCoordinates site, double LST) {
        HorizontalCoordinates horizontal = new HorizontalCoordinates();
        toHorizontal(this, horizontal, site, LST);
        return horizontal;
    }


    public void toHorizontal(HorizontalCoordinates toCoords, GeodeticCoordinates site, double LST) { toHorizontal(this, toCoords, site, LST); }


    public void toHorizontalOffset(Vector2D offset, GeodeticCoordinates site, double LST) {
        toHorizontalOffset(offset, getParallacticAngle(site, LST));
    }


    public static void toHorizontalOffset(Vector2D offset, double PA) {
        offset.scaleX(-1.0);
        offset.rotate(-PA);
    }


    /*
	public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {
		double H = LST * Unit.timeAngle - equatorial.RA();
		double cosH = Math.cos(H);
		horizontal.setNativeLatitude(asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
		double asinA = -Math.sin(H) * equatorial.cosLat();
		double acosA = site.cosLat() * equatorial.sinLat() - site.sinLat() * equatorial.cosLat() * cosH;
		horizontal.setLongitude(Math.atan2(asinA, acosA));
	}
     */

    public static void toHorizontal(EquatorialCoordinates equatorial, HorizontalCoordinates horizontal, GeodeticCoordinates site, double LST) {	
        double H = LST * Unit.timeAngle - equatorial.RA();	
        double cosH = Math.cos(H);
        horizontal.setLatitude(SafeMath.asin(equatorial.sinLat() * site.sinLat() + equatorial.cosLat() * site.cosLat() * cosH));
        double asinA = -Math.sin(H) * equatorial.cosLat() * site.cosLat();
        double acosA = equatorial.sinLat() - site.sinLat() * horizontal.sinLat();
        horizontal.setLongitude(Math.atan2(asinA, acosA));
    }

      
    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#getEquatorialPole()
     */
    @Override
    public EquatorialCoordinates getEquatorialPole() { return equatorialPole; }

    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#getZeroLongitude()
     */
    @Override
    public double getZeroLongitude() { return 0.0; }

    
    @Override
    public NumberFormat getLongitudeFormat(int decimals) {
        return Util.haf[decimals > 0 ? decimals-1 : 0];
    }


    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


    private static HourAngleFormat haf = new HourAngleFormat(2);



    static {
        defaultCoordinateSystem = new CoordinateSystem("Equatorial");
        defaultLocalCoordinateSystem = new CoordinateSystem("Equatorial Offsets");

        CoordinateAxis rightAscentionAxis = createAxis("Right Ascension", "RA", GreekLetter.alpha + "", haf);
        CoordinateAxis declinationAxis = createAxis("Declination", "DEC", GreekLetter.delta + "", af);

        CoordinateAxis rightAscentionOffsetAxis = createOffsetAxis("Right Ascension Offset", "dRA", GreekLetter.Delta + " " + GreekLetter.alpha);
        CoordinateAxis declinationOffsetAxis = createOffsetAxis("Declination Offset", "dDEC", GreekLetter.Delta + " " + GreekLetter.delta);

        defaultCoordinateSystem.add(rightAscentionAxis);
        defaultCoordinateSystem.add(declinationAxis);

        defaultLocalCoordinateSystem.add(rightAscentionOffsetAxis);
        defaultLocalCoordinateSystem.add(declinationOffsetAxis);        
    }	

    private static EquatorialCoordinates equatorialPole = new EquatorialCoordinates(0.0, Constant.rightAngle);

    /**
     * Constant for position angle rate of change due to precession near J2000
     */
    private static final double precessionPARate = 20.05 * Unit.arcsec;

    /**
     * Precession (Lieske+1977)
     * 
     */
    final static double eps0 = 23.4392911111111 * Unit.deg;  ///< Earth obliquity: 23d 26m 21.448s
    

    public final static int NORTH = 1;

    public final static int SOUTH = -1;

    public final static int EAST = 1;

    public final static int WEST = -1;

}

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

import jnum.SafeMath;
import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;


public class HorizontalCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = -3759766679620485628L;


    public HorizontalCoordinates() {}


    public HorizontalCoordinates(String text) { super(text); } 


    public HorizontalCoordinates(double az, double el) { super(az, el); }

    @Override
    public HorizontalCoordinates clone() { return (HorizontalCoordinates) super.clone(); }

    @Override
    public HorizontalCoordinates copy() { return (HorizontalCoordinates) super.copy(); }


    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
     */
    @Override
    public String getFITSLongitudeStem() { return "ALON"; }

    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
     */
    @Override
    public String getFITSLatitudeStem() { return "ALAT"; }


    @Override
    public String getTwoLetterCode() { return "HO"; }


    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }

 
    public final double AZ() { return nativeLongitude(); }


    public final double azimuth() { return nativeLongitude(); }


    public final double EL() { return nativeLatitude(); }

    public final double elevation() { return nativeLatitude(); }


    public final double ZA() { return 90.0 * Unit.deg - nativeLatitude(); }


    public final double zenithAngle() { return ZA(); }


    public final void setAZ(double AZ) { setNativeLongitude(AZ); }


    public final void setEL(double EL) { setNativeLatitude(EL); }


    public final void setZA(double ZA) { setNativeLatitude(90.0 * Unit.deg - ZA); }


    public EquatorialCoordinates toEquatorial(GeodeticCoordinates site, double LST) {
        EquatorialCoordinates equatorial = new EquatorialCoordinates();
        toEquatorial(this, equatorial, site, LST);
        return equatorial;
    }


    public void toEquatorial(EquatorialCoordinates toCoords, GeodeticCoordinates site, double LST) { toEquatorial(this, toCoords, site, LST); }


    public double getParallacticAngle(GeodeticCoordinates site) {
        return Math.atan2(-site.cosLat() * Math.sin(AZ()), site.sinLat() * cosLat() - site.cosLat() * sinLat() * Math.cos(AZ()));
    }


    public static void toEquatorial(HorizontalCoordinates horizontal, EquatorialCoordinates equatorial, GeodeticCoordinates site, double LST) {
        double cosAZ = Math.cos(horizontal.AZ());
        equatorial.setNativeLatitude(SafeMath.asin(horizontal.sinLat() * site.sinLat() + horizontal.cosLat() * site.cosLat() * cosAZ));
        final double asinH = -Math.sin(horizontal.AZ()) * horizontal.cosLat();
        final double acosH = site.cosLat() * horizontal.sinLat() - site.sinLat() * horizontal.cosLat() * cosAZ;
        //final double acosH = (horizontal.sinLat() - equatorial.sinLat() * site.sinLat()) / site.cosLat();

        equatorial.setLongitude(LST * Unit.timeAngle + Math.atan2(asinH, acosH));
    }


    public void toEquatorial(Vector2D offset, GeodeticCoordinates site) {
        toEquatorialOffset(offset, getParallacticAngle(site));
    }


    public static void toEquatorialOffset(Vector2D offset, double PA) {
        offset.rotate(PA);
        offset.scaleX(-1.0);
    }

    
    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;


    static {
        defaultCoordinateSystem = new CoordinateSystem("Horizontal");
        defaultLocalCoordinateSystem = new CoordinateSystem("Horizontal Offsets");

        CoordinateAxis azimuthAxis = createAxis("Azimuth", "AZ", "Az", af);
        CoordinateAxis elevationAxis = createAxis("Elevation", "EL", "El", af);
        CoordinateAxis azimuthOffsetAxis = createOffsetAxis("Azimuth Offset", "dAZ", GreekLetter.Delta + " Az");
        CoordinateAxis elevationOffsetAxis = createOffsetAxis("Elevation Offset", "dEL", GreekLetter.Delta + " El");

        azimuthAxis.setReverse(true);
        azimuthOffsetAxis.setReverse(true);
        
        defaultCoordinateSystem.add(azimuthAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(azimuthOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);

    }

}

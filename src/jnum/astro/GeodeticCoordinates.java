/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/


package jnum.astro;

import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;
import jnum.Unit;
import jnum.fits.FitsToolkit;
import jnum.math.SphericalCoordinates;

// TODO: Auto-generated Javadoc
// TODO Needs updating
// ... rename to GeographicCoordinates
// ... toString() formatting, and parse with N,S,E,W


public class GeodeticCoordinates extends SphericalCoordinates {	

    private static final long serialVersionUID = -162411465069211958L;


    public GeodeticCoordinates() {}


    public GeodeticCoordinates(String text) { super(text); }


    public GeodeticCoordinates(double lon, double lat) { super(lon, lat); }

    // Approximation for converting geocentric to geodesic coordinates.
    // Marik: Csillagaszat (1989)
    // based on Woolard & Clemence: Spherical Astronomy (1966)
    public GeodeticCoordinates(GeocentricCoordinates geocentric) {
        setNativeLongitude(geocentric.x());
        setNativeLatitude(geocentric.y() + Z * Math.sin(2.0 * geocentric.y()));
    }


    @Override
    public GeodeticCoordinates clone() { return (GeodeticCoordinates) super.clone(); }

    @Override
    public GeodeticCoordinates copy() { return (GeodeticCoordinates) super.copy(); }


    @Override
    public String getTwoLetterCode() { return "GD"; }


    /* (non-Javadoc)
     * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
     */
    @Override
    public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {	
        super.editHeader(header, keyStem, alt);	

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard("WCSNAME" + alt, getClass().getSimpleName(), "coordinate system description."));
    }


    public final static double a = 6378137.0 * Unit.m; // Earth major axis

    public final static double b = 6356752.3 * Unit.m; // Earth minor axis

    public final static double f = 1.0 / 298257.0; // Flattening of Earth (Marik: Csillagaszat)

    private final static double Z = 103132.4 * Unit.deg * (2.0 * f - f*f); // Approximation term for geodesic conversion (Marik: Csillagaszat)

    public final static int NORTH = 1;

    public final static int SOUTH = -1;

    public final static int EAST = 1;

    public final static int WEST = -1;

    // TODO verify units of X...

    // See Wikipedia Geodetic System...

    // e^2 = 2f-f^2 = 1- (b/a)^2
    // e'^2 = f(2-f)/(1-f)^2 = (a/b)^2 - 1


    // Australian Geodetic Datum (1966) and (1984)
    // AGD66 & GDA84
    // a = 6378160.0 m
    // f = 1/298.25

    // Geodetic Reference System 1980 (GRS80)
    // a = 6378137 m
    // f = 1/298.257222101

    // World Geodetic System 1984 (WGS84)
    // used by GPS navigation
    // a = 6378137.0 m
    // f = 1/298.257223563


    // Geodetic (phi, lambda, h) -> geocentric phi'
    // chi = sqrt(1-e^2 sin^2(phi))
    //
    // tan(phi') = [(a/chi)(1-f)^2 + h] / [(a/chi) + h] tan(phi)


}

/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.astro;


import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public class TelescopeCoordinates extends SphericalCoordinates {
    /**
     * 
     */
    private static final long serialVersionUID = 5165681897613041311L;

    /** The elevation offset axis. */
    static CoordinateAxis crossElevationAxis, elevationAxis, xelOffsetAxis, elevationOffsetAxis;
    
    /** The default local coordinate system. */
    static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Telescope Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Telescope Offsets");

        crossElevationAxis = new CoordinateAxis("Telescope Cross-elevation", "XEL", "XEL");
        elevationAxis = new CoordinateAxis("Telescope Elevation", "EL", "EL");
        xelOffsetAxis = new CoordinateAxis("Telescioe Cross-elevation Offset", "dXEL", GreekLetter.Delta + " XEL");
        elevationOffsetAxis = new CoordinateAxis("Telescope Elevation Offset", "dEL", GreekLetter.Delta + " EL");
        
        defaultCoordinateSystem.add(crossElevationAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(xelOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);
        
        for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
    }


    /**
     * Instantiates a new horizontal coordinates.
     */
    public TelescopeCoordinates() {}

    /**
     * Instantiates a new horizontal coordinates.
     *
     * @param text the text
     */
    public TelescopeCoordinates(String text) { super(text); } 

    /**
     * Instantiates a new horizontal coordinates.
     *
     * @param az the az
     * @param el the el
     */
    public TelescopeCoordinates(double az, double el) { super(az, el); }

    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
     */
    @Override
    public String getFITSLongitudeStem() { return "TLON"; }
    
    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
     */
    @Override
    public String getFITSLatitudeStem() { return "TLAT"; }
    
    
    /**
     * Gets the coordinate system.
     *
     * @return the coordinate system
     */
    @Override
    public CoordinateSystem getCoordinateSystem() { return defaultCoordinateSystem; }

    
    /**
     * Gets the local coordinate system.
     *
     * @return the local coordinate system
     */
    @Override
    public CoordinateSystem getLocalCoordinateSystem() { return defaultLocalCoordinateSystem; }
    


    /**
     * Az.
     *
     * @return the double
     */
    public final double XEL() { return nativeLongitude(); }

    /**
     * Azimuth.
     *
     * @return the double
     */
    public final double crossElevation() { return nativeLongitude(); }

    /**
     * El.
     *
     * @return the double
     */
    public final double EL() { return nativeLatitude(); }

    /**
     * Elevation.
     *
     * @return the double
     */
    public final double elevation() { return nativeLatitude(); }


    /**
     * Sets the az.
     *
     * @param AZ the new az
     */
    public final void setXEL(double XEL) { setNativeLongitude(XEL); }

    /**
     * Sets the el.
     *
     * @param EL the new el
     */
    public final void setEL(double EL) { setNativeLatitude(EL); }


    /**
     * To equatorial.
     *
     * @param offset the offset
     * @param site the site
     */
    public void toEquatorial(Vector2D offset, double telVPA) {
        toEquatorialOffset(offset, telVPA);
    }

    /**
     * To equatorial offset.
     *
     * @param offset the offset
     * @param PA the pa
     */
    public static void toEquatorialOffset(Vector2D offset, double telVPA) {
        offset.rotate(telVPA);
        offset.scaleX(-1.0);
    }
    
    
    /* (non-Javadoc)
     * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
     */
    @Override
    public void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException {    
        super.edit(cursor, alt);    
        cursor.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
    }
    
}

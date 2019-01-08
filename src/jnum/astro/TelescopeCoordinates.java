/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.astro;


import jnum.fits.FitsToolkit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


public class TelescopeCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = 5165681897613041311L;


    public TelescopeCoordinates() {}


    public TelescopeCoordinates(String text) { super(text); } 


    public TelescopeCoordinates(double az, double el) { super(az, el); }

    
    @Override
    public TelescopeCoordinates clone() { return (TelescopeCoordinates) super.clone(); }
    
    @Override
    public TelescopeCoordinates copy() { return (TelescopeCoordinates) super.copy(); }
    
    
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
    
    
    @Override
    public String getTwoLetterCode() { return "TE"; }
    
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }
     
    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }


    public final double XEL() { return nativeLongitude(); }


    public final double crossElevation() { return nativeLongitude(); }


    public final double EL() { return nativeLatitude(); }


    public final double elevation() { return nativeLatitude(); }


    public final void setXEL(double XEL) { setNativeLongitude(XEL); }


    public final void setEL(double EL) { setNativeLatitude(EL); }


    public void toEquatorial(Vector2D offset, double telVPA) {
        toEquatorialOffset(offset, telVPA);
    }

    
    public static void toEquatorialOffset(Vector2D offset, double telVPA) {
        offset.rotate(telVPA);
        offset.scaleX(-1.0);
    }
    
    
    /* (non-Javadoc)
     * @see kovacs.math.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
     */
    @Override
    public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {    
        super.editHeader(header, keyStem, alt);   

        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard("WCSNAME" + alt, getCoordinateSystem().getName(), "coordinate system description."));
    }
    

    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
 
     
    static {
        defaultCoordinateSystem = new CoordinateSystem("Telescope Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Telescope Offsets");

        CoordinateAxis crossElevationAxis = createAxis("Telescope Cross-elevation", "XEL", "XEL", af);
        CoordinateAxis elevationAxis = createAxis("Telescope Elevation", "EL", "EL", af);
        CoordinateAxis xelOffsetAxis = createOffsetAxis("Telescioe Cross-elevation Offset", "dXEL", GreekLetter.Delta + " XEL");
        CoordinateAxis elevationOffsetAxis = createOffsetAxis("Telescope Elevation Offset", "dEL", GreekLetter.Delta + " EL");
        
        defaultCoordinateSystem.add(crossElevationAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(xelOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);
        
        for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
    }
       

}

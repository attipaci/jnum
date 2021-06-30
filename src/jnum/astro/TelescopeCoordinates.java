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


import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.text.GreekLetter;

public class TelescopeCoordinates extends SphericalCoordinates {

    private static final long serialVersionUID = 5165681897613041311L;


    public TelescopeCoordinates() {}


    public TelescopeCoordinates(String text) { super(text); } 


    public TelescopeCoordinates(double az, double el) { super(az, el); }

    
    @Override
    public TelescopeCoordinates clone() { return (TelescopeCoordinates) super.clone(); }
    
    @Override
    public TelescopeCoordinates copy() { return (TelescopeCoordinates) super.copy(); }
    

    @Override
    public String getFITSLongitudeStem() { return "TLON"; }


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
    

    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
 
     
    static {
        defaultCoordinateSystem = new CoordinateSystem("Telescope");
        defaultLocalCoordinateSystem = new CoordinateSystem("Telescope Offsets");

        CoordinateAxis crossElevationAxis = createAxis("Telescope Cross-elevation", "XEL", "XEl", af);
        CoordinateAxis elevationAxis = createAxis("Telescope Elevation", "EL", "El", af);
        CoordinateAxis xelOffsetAxis = createOffsetAxis("Telescioe Cross-elevation Offset", "dXEL", GreekLetter.Delta + " XEl");
        CoordinateAxis elevationOffsetAxis = createOffsetAxis("Telescope Elevation Offset", "dEL", GreekLetter.Delta + " El");
        
        defaultCoordinateSystem.add(crossElevationAxis);
        defaultCoordinateSystem.add(elevationAxis);
        defaultLocalCoordinateSystem.add(xelOffsetAxis);
        defaultLocalCoordinateSystem.add(elevationOffsetAxis);
        
        for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
    }
       

}

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

import java.text.NumberFormat;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Coordinates;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

public abstract class PrecessingCoordinates extends CelestialCoordinates {
    /**
     * 
     */
    private static final long serialVersionUID = 4675743914273761865L;
    
    private EquatorialSystem system;


    public PrecessingCoordinates() { this(EquatorialSystem.ICRS); }

    public PrecessingCoordinates(EquatorialSystem system) { 
        setSystem(system);
    }
    
    

    public PrecessingCoordinates(String text) { super(text); }
    

    public PrecessingCoordinates(double lon, double lat) { 
        this(lon, lat, EquatorialSystem.ICRS); 
        
    }

    public PrecessingCoordinates(double lon, double lat, EquatorialSystem system) { 
        super(lon, lat);
        setSystem(system);
    }
   

    public PrecessingCoordinates(double lon, double lat, String sysSpec) { 
        this(lon, lat, EquatorialSystem.forString(sysSpec)); 
    }
     

    public PrecessingCoordinates(CelestialCoordinates from) { 
        super(from); 
        if(from instanceof PrecessingCoordinates) system = ((PrecessingCoordinates) from).getSystem();
        else system = EquatorialSystem.ICRS;
    }
    
    public EquatorialSystem getSystem() {
        return system;
    }
    
    public void setSystem(EquatorialSystem system) {
        this.system = system;
    }
    

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if(system != null) hash ^= system.hashCode();
        return hash;
    }
    

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof PrecessingCoordinates)) return false;
        if(!super.equals(o)) return false;
        PrecessingCoordinates e = (PrecessingCoordinates) o;
        if(!Util.equals(system, e.system)) return false;
        return true;
    }
    
    public double getEpochYear() { return system.getJulianYear(); }
   

    @Override
    public void copy(Coordinates<? extends Double> coords) {
        super.copy(coords);
        if(coords instanceof PrecessingCoordinates) {
            PrecessingCoordinates p = (PrecessingCoordinates) coords;
            system = p.getSystem();
        }
        else system = EquatorialSystem.ICRS;
    }
    
 
    @Override
    public void toEquatorial(EquatorialCoordinates equatorial) {
        super.toEquatorial(equatorial);  
        equatorial.setSystem(system);
    }
    

    @Override
    public void fromEquatorial(EquatorialCoordinates equatorial) {
        super.fromEquatorial(equatorial);
        setSystem(equatorial.getSystem());
    }
    
    
    public abstract void transform(EquatorialTransform T);
    
    public EquatorialTransform getTransformTo(EquatorialSystem toSystem) {
        return new EquatorialTransform(getSystem(), toSystem);
    }
    
    public void transformTo(EquatorialSystem toSystem) {
        if(getSystem().equals(toSystem)) return;
        transform(getTransformTo(toSystem));
    }
  
    
    public void toICRS() {
        transformTo(EquatorialSystem.ICRS);
    }    


    @Override
    public String toString(int decimals) {
        return super.toString(decimals) + (system == null ? "" : " " + system);
    }
    

    @Override
    public String toString(NumberFormat nf) {
        return super.toString(nf) + (system == null ? "" : " " + system);   
    }
    

    @Override
    public void parseDirect(StringParser parser) throws IllegalArgumentException {
        // Assume no epoch prior to parsing...
        EquatorialSystem origSystem = system;
        system = null; 
        
        // Parse the text as is...
        super.parseDirect(parser);
       
        if(parser.hasMoreTokens()) {   
            // If the parse epoch has not defaulted to something during possible conversion, then
            // see if an epoch is specified... 
            parser.skipWhiteSpaces();
            int pos = parser.getIndex();
            try { system = EquatorialSystem.forString(parser.nextToken(Util.getWhiteSpaceChars())); }
            catch(IllegalArgumentException e) { parser.setIndex(pos); }
        }
          
        // If the parsing does not provide a specific epoch, then assume that the user knows what they're
        // doing and set the epoch of this object to the desired value prior to parsing...
        // Thus, preserve the original epoch...
        if(system == null) system = origSystem == null ? EquatorialSystem.ICRS : origSystem;
    }
    
 
    @Override
    public void editHeader(Header header, String keyStem, String alt) throws HeaderCardException {
        super.editHeader(header, keyStem, alt);
        system.editHeader(header, alt);
    }
    

    @Override
    public void parseHeader(Header header, String keyStem, String alt, Coordinate2D defaultValue) {
        super.parseHeader(header, keyStem, alt, defaultValue);
        system = EquatorialSystem.fromHeader(header, alt);
    }
    
    
    
    
    
    
}

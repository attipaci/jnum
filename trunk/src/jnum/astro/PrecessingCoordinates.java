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

import java.text.NumberFormat;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;

public abstract class PrecessingCoordinates extends CelestialCoordinates  implements Precessing {
    /**
     * 
     */
    private static final long serialVersionUID = 4675743914273761865L;
    
    /** The epoch. */
    public CoordinateEpoch epoch;

    /**
     * Instantiates a new ecliptic coordinates.
     */
    public PrecessingCoordinates() { this(CoordinateEpoch.J2000); }

    public PrecessingCoordinates(CoordinateEpoch epoch) { 
        setEpoch(epoch);
    }
    
    
    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param text the text
     */
    public PrecessingCoordinates(String text) { super(text); }
    
    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param lon the lon
     * @param lat the lat
     */
    public PrecessingCoordinates(double lon, double lat) { 
        this(lon, lat, CoordinateEpoch.J2000); 
        
    }

    public PrecessingCoordinates(double lon, double lat, CoordinateEpoch epoch) { 
        super(lon, lat);
        setEpoch(epoch);
    }
    
    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param lon the lon
     * @param lat the lat
     * @param aEpoch the a epoch
     */
    public PrecessingCoordinates(double lon, double lat, double epochYear) { 
        this(lon, lat, epochYear < 1984.0 ? new BesselianEpoch(epochYear) : new JulianEpoch(epochYear)); 
    }

    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param lon the lon
     * @param lat the lat
     * @param epochSpec the epoch spec
     */
    public PrecessingCoordinates(double lon, double lat, String epochSpec) { 
        this(lon, lat, CoordinateEpoch.forString(epochSpec)); 
    }
     
    /**
     * Instantiates a new ecliptic coordinates.
     *
     * @param from the from
     */
    public PrecessingCoordinates(CelestialCoordinates from) { super(from); }
    
    /* (non-Javadoc)
     * @see jnum.math.Coordinate2D#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if(epoch != null) hash ^= epoch.hashCode();
        return hash;
    }
    
    /* (non-Javadoc)
     * @see jnum.math.Coordinate2D#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof PrecessingCoordinates)) return false;
        if(!super.equals(o)) return false;
        PrecessingCoordinates e = (PrecessingCoordinates) o;
        if(!Util.equals(epoch, e.epoch)) return false;
        return true;
    }
    
    
    /* (non-Javadoc)
     * @see jnum.astro.Precessing#getEpoch()
     */
    @Override
    public CoordinateEpoch getEpoch() { return epoch; }

    /* (non-Javadoc)
     * @see jnum.astro.Precessing#setEpoch(jnum.astro.CoordinateEpoch)
     */
    @Override
    public void setEpoch(CoordinateEpoch epoch) { this.epoch = epoch; }
    

    
    /**
     * Copy.
     *
     * @return the coordinate2 d
     */
    @Override
    public PrecessingCoordinates copy() {
        PrecessingCoordinates copy = (PrecessingCoordinates) super.copy();
        if(epoch != null) copy.epoch = epoch.copy();
        return copy;
    }
    
  
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#copy(jnum.Coordinate2D)
     */
    @Override
    public void copy(Coordinate2D coords) {
        super.copy(coords);
        if(coords instanceof PrecessingCoordinates) {
            PrecessingCoordinates precession = (PrecessingCoordinates) coords;
            epoch = precession.epoch == null ? null : (CoordinateEpoch) precession.epoch.clone(); 
        }
        else epoch = null;
    }
    
    
    @Override
    public final void precess(CoordinateEpoch newEpoch) throws UndefinedEpochException {
        if(Util.equals(epoch, newEpoch)) return;
        if(epoch == null) throw new UndefinedEpochException("Undefined from epoch.");
        if(newEpoch == null) throw new UndefinedEpochException("Undefined to epoch.");
        precessUnchecked(newEpoch);
    }
    
    protected abstract void precessUnchecked(CoordinateEpoch newEpoch); 

    @Override
    public void toEquatorial(EquatorialCoordinates equatorial) {
        super.toEquatorial(equatorial);  
        equatorial.epoch = epoch;
    }
    
    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#fromEquatorial(jnum.astro.EquatorialCoordinates)
     */
    @Override
    public void fromEquatorial(EquatorialCoordinates equatorial) {
        super.fromEquatorial(equatorial);
        epoch = equatorial.epoch;
    }
    
    
    
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#toString()
     */
    @Override
    public String toString() {
        return super.toString() + (epoch == null ? "" : " (" + epoch.toString() + ")"); 
    }
    
    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#toString(int)
     */
    @Override
    public String toString(int decimals) {
        return super.toString(decimals) + (epoch == null ? "" : " (" + epoch.toString() + ")");
    }
    
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#toString(java.text.NumberFormat)
     */
    @Override
    public String toString(NumberFormat nf) {
        return super.toString(nf) + (epoch == null ? "" : " (" + epoch.toString() + ")");   
    }
    

    
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#parse(java.lang.String)
     */
    @Override
    public void parseDirect(StringParser parser) throws NumberFormatException, IllegalArgumentException {
        // Assume no epoch prior to parsing...
        CoordinateEpoch origEpoch = epoch;
        epoch = null; 
        
        // Parse the text as is...
        super.parseDirect(parser);
       
        if(parser.hasMoreTokens()) {   
            // If the parse epoch has not defaulted to something during possible conversion, then
            // see if an epoch is specified... 
            parser.skipWhiteSpaces();
            int pos = parser.getIndex();
            try { epoch = CoordinateEpoch.forString(parser.nextToken(Util.getWhiteSpaceChars() + "()")); }
            catch(NumberFormatException e) { parser.setIndex(pos); }
        }
          
        // If the parsing does not provide a specific epoch, then assume that the user knows what they're
        // doing and set the epoch of this object to the desired value prior to parsing...
        // Thus, preserve the original epoch...
        if(epoch == null) epoch = origEpoch;
    }
    
 
    
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#edit(nom.tam.util.Cursor, java.lang.String)
     */
    @Override
    public void edit(Header header, String alt) throws HeaderCardException {
        super.edit(header, alt);
        header.addLine(new HeaderCard("RADESYS" + alt, epoch instanceof BesselianEpoch ? "FK4" : "FK5", "Reference convention."));
        epoch.edit(header, alt);
    }
    
    /* (non-Javadoc)
     * @see jnum.SphericalCoordinates#parse(nom.tam.fits.Header, java.lang.String)
     */
    @Override
    public void parse(Header header, String alt) {
        super.parse(header, alt);
        
        String system = header.getStringValue("RADESYS");
        if(system == null) system = header.getDoubleValue("EQUINOX" + alt) < 1984.0 ? "FK4" : "FK5";
        
        if(system.equalsIgnoreCase("FK4")) epoch = new BesselianEpoch();
        else if(system.equalsIgnoreCase("FK4-NO-E")) epoch = new BesselianEpoch();
        else epoch = new JulianEpoch();
        
        epoch.parse(header, alt);
    }
    
    
    
    
    
    
}

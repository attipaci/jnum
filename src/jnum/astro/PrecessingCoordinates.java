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
import java.text.ParseException;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Coordinates;
import jnum.text.StringParser;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

/**
 * A base subclass of celestial coordinates, which are tied to the equator, and hence precess
 * over time. These type of coordinates may be referenced to any specific definition of the
 * equator.
 * 
 * @author Attila Kovacs
 *
 */
public abstract class PrecessingCoordinates extends CelestialCoordinates {
    /**
     * 
     */
    private static final long serialVersionUID = 4675743914273761865L;
    
    /** The equatorial coordinate referecne system to which coordinates are referred to */
    private EquatorialSystem system;

    /**
     * Instantiates a new set of celestial coordinates, referenced to the ICRS equator.
     * 
     * @see #PrecessingCoordinates(EquatorialSystem)
     * 
     * @see EquatorialSystem#ICRS
     */
    protected PrecessingCoordinates() { this(EquatorialSystem.ICRS); }

    /**
     * Instantiates a new set of celestial coordinates, referenced to the equator of the 
     * specified reference system.
     * 
     * @param system    the equatorial system to which the coordinates are referenced to.
     * 
     * @see #PrecessingCoordinates(double, double, EquatorialSystem)
     */
    protected PrecessingCoordinates(EquatorialSystem system) { 
        setSystem(system);
    }
    
    
    /**
     * Instantiates a new set of celestial coordinates, from a string representation of these.
     * 
     * @param text              the string representation of the coordinates, including the reference system.
     * @throws ParseException   if the coordinates could not be properly determined / parsed from the supplied string.
     * 
     * @see #parse(String)
     */
    protected PrecessingCoordinates(String text) throws ParseException { super(text); }
    
    /**
     * Instantiates a new set of celestial coordinates, with the conventional longitude and latitude values
     * of these coordinates, referenced to the ICRS equator.
     * 
     * @param lon       (rad) longitude angle in the convention of the implementing class.
     * @param lat       (rad) latitude angle in the convention of the implementing class. 
     * 
     * @see #PrecessingCoordinates(double, double, EquatorialSystem)
     * @see #PrecessingCoordinates(double, double, String)
     * @see EquatorialSystem#ICRS
     */
    protected PrecessingCoordinates(double lon, double lat) { 
        this(lon, lat, EquatorialSystem.ICRS); 
        
    }

    /**
     * Instantiates a new set of celestial coordinates, with the conventional longitude and latitude values
     * of these coordinates, referenced to the equator of the specified reference system.
     * 
     * @param lon       (rad) longitude angle in the convention of the implementing class.
     * @param lat       (rad) latitude angle in the convention of the implementing class. 
     * @param system    the equatorial system to which the coordinates are referenced to.
     * 
     * @see #PrecessingCoordinates(double, double)
     * @see #PrecessingCoordinates(double, double, String)
     * @see #PrecessingCoordinates(CelestialCoordinates)
     */
    protected PrecessingCoordinates(double lon, double lat, EquatorialSystem system) { 
        super(lon, lat);
        setSystem(system);
    }
   

    /**
     * Instantiates a new set of celestial coordinates, with the conventional longitude and latitude values
     * of these coordinates, referenced to the equator of the reference system specified by its string
     * represenation.
     * 
     * @param lon       (rad) longitude angle in the convention of the implementing class.
     * @param lat       (rad) latitude angle in the convention of the implementing class. 
     * @param sysSpec   the string representation of the reference system, e.g. 'ICRS', 'J2000', 'B1950', 'FK5'...
     * 
     * @see #PrecessingCoordinates(double, double, EquatorialSystem)
     * @see #PrecessingCoordinates(CelestialCoordinates)
     */
    protected PrecessingCoordinates(double lon, double lat, String sysSpec) { 
        this(lon, lat, EquatorialSystem.forString(sysSpec)); 
    }
     

    /**
     * Instantiates a new set of celestial coordinates, referenced to the ICRS equator, that represent 
     * the same location on sky as the specified other coordinates
     * 
     * @param from      the coordinates of the sky location in some other celestial system.
     * 
     * @see CelestialCoordinates#fromEquatorial(EquatorialCoordinates)
     * @see CelestialCoordinates#toEquatorial()
     * @see EquatorialSystem#ICRS
     */
    protected PrecessingCoordinates(CelestialCoordinates from) { 
        super(from); 
        if(from instanceof PrecessingCoordinates) system = ((PrecessingCoordinates) from).getSystem();
        else system = EquatorialSystem.ICRS;
    }

    @Override
    public PrecessingCoordinates clone() {
        return (PrecessingCoordinates) super.clone();
    }
    
    @Override
    public PrecessingCoordinates copy() {
        return (PrecessingCoordinates) super.copy();
    }
    
    /**
     * Returns the equatorial reference system in which these coordinates are expressed.
     * 
     * @return      the equatorial reference system of these coordinates.
     * 
     * @see #setSystem(EquatorialSystem)
     */
    public EquatorialSystem getSystem() {
        return system;
    }
    
    /**
     * Sets the equatorial reference system in which these coordinates are to be expressed.
     * 
     * @param system    the new equatorial reference system of these coordinates.
     * 
     * @see #getSystem()
     */
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
    
    /**
     * Returns the Julian reference epoch for these coordinates as a decimal year. 
     * 
     * @return  (yr) the julian reference epoch of these coordinates. For example 2000.0, 1950.0, or 2021.655.
     */
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
    
    /**
     * Changes the equatorial reference system of these coordinates by applying the specified equatorial
     * transformation.
     * 
     * @param T     the equatorial transformation that converts from the current reference system to the new reference system.
     * 
     * @see #transformTo(EquatorialSystem)
     * @see #getTransformTo(EquatorialSystem)
     */
    public abstract void transform(EquatorialTransform T);
    
    /**
     * Returns the equatorial coordinate transform from the current reference system to the specified new
     * reference system.
     * 
     * @param toSystem      the new equatorial reference system.
     * @return              the equatorial transformation that converts from the current reference system to the new reference system.
     * 
     * @see #transformTo(EquatorialSystem)
     */
    public EquatorialTransform getTransformTo(EquatorialSystem toSystem) {
        return new EquatorialTransform(getSystem(), toSystem);
    }
    
    /**
     * Changes the equatorial reference system to the specified new system.
     * 
     * @param toSystem      the new equatorial reference system.
     * 
     * @see #getTransformTo(EquatorialSystem)
     * @see #transform(EquatorialTransform)
     * @see #toICRS()
     */
    public void transformTo(EquatorialSystem toSystem) {
        if(getSystem().equals(toSystem)) return;
        transform(getTransformTo(toSystem));
    }
  
    /**
     * Returns the same coordinates, transformed to another equatorial reference system by the specified transform.
     * 
     * @param T     the equatorial transformation for changing the reference system
     * @return      new coordinates of the same type as this, but transformed to another equatorial reference system.
     * 
     * @see #transform(EquatorialTransform)
     * @see #getTransformedTo(EquatorialSystem)
     */
    public PrecessingCoordinates getTransformed(EquatorialTransform T) {
        PrecessingCoordinates c = copy();
        c.transform(T);
        return c;
    }

    /**
     * Returns the same coordinates, transformed to the specified other reference system.
     * 
     * @param system    the reference system in which to return the same location.
     * @return          new coordinates of the same type as this, but in transformed to the specified equatorial reference system.
     * 
     * @see #transformTo(EquatorialSystem)
     * @see #getTransformed(EquatorialTransform)
     */
    public PrecessingCoordinates getTransformedTo(EquatorialSystem system) {
        PrecessingCoordinates c = copy();
        c.transformTo(system);
        return c;
    }
    
    /**
     * Changes the equatorial reference system to ICRS.
     * 
     * @see #transformTo(EquatorialSystem)
     * @see EquatorialSystem#ICRS
     */
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

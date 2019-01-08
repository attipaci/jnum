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

import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;



public class SuperGalacticCoordinates extends CelestialCoordinates {

    private static final long serialVersionUID = 5322669438151443525L;



    public SuperGalacticCoordinates() {}


    public SuperGalacticCoordinates(String text) { super(text); }


    public SuperGalacticCoordinates(double lat, double lon) { super(lat, lon); }


    public SuperGalacticCoordinates(CelestialCoordinates from) { super(from); }


    @Override
    public SuperGalacticCoordinates clone() { return (SuperGalacticCoordinates) super.clone(); }
    
    @Override
    public SuperGalacticCoordinates copy() { return (SuperGalacticCoordinates) super.copy(); }
    
    
    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
     */
    @Override
    public String getFITSLongitudeStem() { return "SLON"; }

    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
     */
    @Override
    public String getFITSLatitudeStem() { return "SLAT"; }


    @Override
    public String getTwoLetterCode() { return "SG"; }

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return defaultCoordinateSystem;
    }

    @Override
    public CoordinateSystem getLocalCoordinateSystem() {
        return defaultLocalCoordinateSystem;
    }


    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#getEquatorialPole()
     */
    @Override
    public EquatorialCoordinates getEquatorialPole() {
        return equatorialPole;
    }

    /* (non-Javadoc)
     * @see jnum.astro.CelestialCoordinates#getZeroLongitude()
     */
    @Override
    public double getZeroLongitude() {
        return phi0;
    }
  


    @SuppressWarnings("hiding")
    public static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;

    
    static {
        defaultCoordinateSystem = new CoordinateSystem("Supergalactic Coordinates");
        defaultLocalCoordinateSystem = new CoordinateSystem("Supergalactic Offsets");

        CoordinateAxis longitudeAxis = createAxis("Supergalactic Longitude", "SGL", "L", af);
        longitudeAxis.setReverse(true);
        CoordinateAxis latitudeAxis = createAxis("Supergalactic Latitude", "SGB", "B", af);
        CoordinateAxis longitudeOffsetAxis = createOffsetAxis("Supergalactic Longitude Offset", "dSGL", GreekLetter.Delta + " L");
        longitudeOffsetAxis.setReverse(true);
        CoordinateAxis latitudeOffsetAxis = createOffsetAxis("Supergalactic Latitude", "dSGB", GreekLetter.Delta + " B");

        defaultCoordinateSystem.add(longitudeAxis);
        defaultCoordinateSystem.add(latitudeAxis);
        defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
        defaultLocalCoordinateSystem.add(latitudeOffsetAxis);   
    }


    public final static GalacticCoordinates galacticPole = new GalacticCoordinates(47.37*Unit.deg, 6.32*Unit.deg);

    public final static GalacticCoordinates galacticZero = new GalacticCoordinates(137.37*Unit.deg, 0.0);

    public final static EquatorialCoordinates equatorialPole = galacticPole.toEquatorial(); 

    public static double phi0 = CelestialCoordinates.getZeroLongitude(galacticZero, new SuperGalacticCoordinates());



}

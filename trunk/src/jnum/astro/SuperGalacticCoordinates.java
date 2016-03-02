/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
// Copyright (c) 2007 Attila Kovacs 

package jnum.astro;

import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;


// TODO: Auto-generated Javadoc
/**
 * The Class SuperGalacticCoordinates.
 */
public class SuperGalacticCoordinates extends CelestialCoordinates {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5322669438151443525L;

	/** The latitude offset axis. */
	static CoordinateAxis longitudeAxis, latitudeAxis, longitudeOffsetAxis, latitudeOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
		
	static {
		defaultCoordinateSystem = new CoordinateSystem("Super-Galactic Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Super-Galactic Offsets");
		
		longitudeAxis = new CoordinateAxis("longitude");
		longitudeAxis.setReverse(true);
		latitudeAxis = new CoordinateAxis("latitude");
		longitudeOffsetAxis = new CoordinateAxis(GreekLetter.Delta + "SLON");
		longitudeOffsetAxis.setReverse(true);
		latitudeOffsetAxis = new CoordinateAxis(GreekLetter.Delta + "SLAT");
		
		defaultCoordinateSystem.add(longitudeAxis);
		defaultCoordinateSystem.add(latitudeAxis);
		defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
		defaultLocalCoordinateSystem.add(latitudeOffsetAxis);	
		
		for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
			
	}
	
    /**
     * Instantiates a new super galactic coordinates.
     */
    public SuperGalacticCoordinates() {}

    /**
     * Instantiates a new super galactic coordinates.
     *
     * @param text the text
     */
    public SuperGalacticCoordinates(String text) { super(text); }

    /**
     * Instantiates a new super galactic coordinates.
     *
     * @param lat the lat
     * @param lon the lon
     */
    public SuperGalacticCoordinates(double lat, double lon) { super(lat, lon); }
    
    /**
     * Instantiates a new super galactic coordinates.
     *
     * @param from the from
     */
    public SuperGalacticCoordinates(CelestialCoordinates from) { super(from); }
    
    
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
    
    
    /** The Constant galacticPole. */
    public final static GalacticCoordinates galacticPole = new GalacticCoordinates(47.37*Unit.deg, 6.32*Unit.deg);
    
    /** The Constant galacticZero. */
    public final static GalacticCoordinates galacticZero = new GalacticCoordinates(137.37*Unit.deg, 0.0);
    
    /** The Constant equatorialPole. */
    public final static EquatorialCoordinates equatorialPole = galacticPole.toEquatorial(); 
    
    /** The phi0. */
    public static double phi0 = CelestialCoordinates.getZeroLongitude(galacticZero, new SuperGalacticCoordinates());
    
    /* (non-Javadoc)
     * @see kovacs.util.astro.CelestialCoordinates#getEquatorialPole()
     */
    @Override
	public EquatorialCoordinates getEquatorialPole() {
		return equatorialPole;
	}

	/* (non-Javadoc)
	 * @see kovacs.util.astro.CelestialCoordinates#getZeroLongitude()
	 */
	@Override
	public double getZeroLongitude() {
		return phi0;
	}

}

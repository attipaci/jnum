/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila[AT]sigmyne.com>.
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
// Copyright (c) 2007 Attila Kovacs 

package jnum.astro;

import jnum.Unit;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import jnum.text.GreekLetter;

// TODO: Auto-generated Javadoc
/**
 * The Class GalacticCoordinates.
 */
public class GalacticCoordinates extends CelestialCoordinates {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -942734735652370919L;

	/** The latitude offset axis. */
	static CoordinateAxis longitudeAxis, latitudeAxis, longitudeOffsetAxis, latitudeOffsetAxis;
	
	/** The default local coordinate system. */
	static CoordinateSystem defaultCoordinateSystem, defaultLocalCoordinateSystem;
	
	static {
		defaultCoordinateSystem = new CoordinateSystem("Galactic Coordinates");
		defaultLocalCoordinateSystem = new CoordinateSystem("Galactic Offsets");
		
		longitudeAxis = new CoordinateAxis("Galactic Longitude", "GLON", "l");
		longitudeAxis.setReverse(true);
		latitudeAxis = new CoordinateAxis("Galactic Latitude", "GLAT", "b");
		longitudeOffsetAxis = new CoordinateAxis("Galactic Longitude Offset", "dGLON", GreekLetter.Delta + " l");
		longitudeOffsetAxis.setReverse(true);
		latitudeOffsetAxis = new CoordinateAxis("Galactic Latitude Offset", "dGLAT", GreekLetter.Delta + " b");
		
		defaultCoordinateSystem.add(longitudeAxis);
		defaultCoordinateSystem.add(latitudeAxis);
		defaultLocalCoordinateSystem.add(longitudeOffsetAxis);
		defaultLocalCoordinateSystem.add(latitudeOffsetAxis);	
		
		for(CoordinateAxis axis : defaultCoordinateSystem) axis.setFormat(af);
	}


    /**
     * Instantiates a new galactic coordinates.
     */
    public GalacticCoordinates() {}

    /**
     * Instantiates a new galactic coordinates.
     *
     * @param text the text
     */
    public GalacticCoordinates(String text) { super(text); }

    /**
     * Instantiates a new galactic coordinates.
     *
     * @param lat the lat
     * @param lon the lon
     */
    public GalacticCoordinates(double lat, double lon) { super(lat, lon); }
    
    /**
     * Instantiates a new galactic coordinates.
     *
     * @param from the from
     */
    public GalacticCoordinates(CelestialCoordinates from) { super(from); }
    
    
    /* (non-Javadoc)
     * @see jnum.math.SphericalCoordinates#getFITSLongitudeStem()
     */
    @Override
	public String getFITSLongitudeStem() { return "GLON"; }
	
	/* (non-Javadoc)
	 * @see jnum.math.SphericalCoordinates#getFITSLatitudeStem()
	 */
	@Override
	public String getFITSLatitudeStem() { return "GLAT"; }
    
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
    
    
    /** The Constant equatorialPole. */
    public static final EquatorialCoordinates equatorialPole = new EquatorialCoordinates(12.0 * Unit.hourAngle + 49.0 * Unit.minuteAngle, 27.4 * Unit.deg, "B1950.0");
    
    /** The phi0. */
    public static double phi0 = 123.0 * Unit.deg;

    // Change the pole and phi0 to J2000, s.t. conversion to J2000 is faster...
    static { 
    	GalacticCoordinates zero = new GalacticCoordinates(phi0, 0.0);
    	phi0 = 0.0;
    	EquatorialCoordinates equatorialZero = zero.toEquatorial();
    	equatorialZero.precess(CoordinateEpoch.J2000);
    	zero.fromEquatorial(equatorialZero);
    	phi0 = -zero.x();
    	equatorialPole.precess(CoordinateEpoch.J2000);
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

}

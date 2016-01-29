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
package jnum.astro;

import jnum.math.SphericalCoordinates;
import jnum.projection.Projection2D;
import jnum.projection.Projector2D;


// TODO: Auto-generated Javadoc
/**
 * The Class CelestialProjector.
 */
public class AstroProjector extends Projector2D<SphericalCoordinates> {
	
	/** The equatorial. */
	private EquatorialCoordinates equatorial;
	
	/** The celestial. */
	private CelestialCoordinates celestial;
	
	
	/**
	 * Instantiates a new celestial projector.
	 *
	 * @param projection the projection
	 */
	public AstroProjector(Projection2D<SphericalCoordinates> projection) {
		super(projection);

		// The equatorial is the same as coords if that is itself equatorial
		// otherwise it's used for converting to and from equatorial...
		if(getCoordinates() instanceof EquatorialCoordinates) 
			equatorial = (EquatorialCoordinates) getCoordinates();
		
		// celestial is the same as coords if coords itself is celestial
		// otherwise celestial is null, indicating horizontal projection...
		else if(getCoordinates() instanceof CelestialCoordinates) {
			celestial = (CelestialCoordinates) getCoordinates();
			equatorial = new EquatorialCoordinates();
		}

	}
	
	/**
	 * Gets the equatorial.
	 *
	 * @return the equatorial
	 */
	public EquatorialCoordinates getEquatorial() { return equatorial; }
	
	/**
	 * Checks if is horizontal.
	 *
	 * @return true, if is horizontal
	 */
	public final boolean isHorizontal() {
		return getCoordinates() instanceof HorizontalCoordinates;
	}
	
	/**
	 * Checks if is focal plane.
	 *
	 * @return true, if is focal plane
	 */
	public final boolean isFocalPlane() {
		return getCoordinates() instanceof FocalPlaneCoordinates;
	}
	
	/* (non-Javadoc)
	 * @see kovacs.util.Projector2D#setReferenceCoords()
	 */
	@Override
	public void setReferenceCoords() {
		super.setReferenceCoords();
		// Set the equatorial reference...
		if(celestial != null) celestial.toEquatorial(equatorial);		
	}

	/**
	 * Project from equatorial.
	 */
	public final void projectFromEquatorial() {
		if(celestial != null) celestial.fromEquatorial(equatorial);
		super.project();
	}
	
	/* (non-Javadoc)
	 * @see kovacs.projection.Projector2D#deproject()
	 */
	@Override
	public final void deproject() {
		super.deproject();
		if(celestial != null) celestial.toEquatorial(equatorial);
	}
}

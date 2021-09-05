/* *****************************************************************************
 * Copyright (c) 2019 Attila Kovacs <attila[AT]sigmyne.com>.
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

import jnum.Util;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;
import jnum.projection.Projector2D;


/**
 * A class for facilitating computationally efficient projection of spherical coordinates onto a 2D plane, and vice versa.
 * The class avoids the need to create on-the-fly objects for the conversion. However, for best performance each projector
 * instance should be used with only one thread to avoid concurrent blocking. 
 * 
 * @author Attila Kovacs
 *
 */
public class AstroProjector extends Projector2D<SphericalCoordinates> {

    /** */
	private static final long serialVersionUID = -6883179727775205645L;

	/** A local set of equatorial coordinates serving as an intermediate for projections */
	private EquatorialCoordinates equatorial;

	/**
	 * Instantiates a new astronomical coordinate projector.
	 * 
	 * @param projection   the spherical projection to use for this projector. 
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
			equatorial = new EquatorialCoordinates();
		}

	}
	
	@Override
    public AstroProjector clone() {
	    AstroProjector clone = (AstroProjector) super.clone();
	    if(equatorial != null) clone.equatorial = equatorial.copy();
	    return clone;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(equatorial != null) if(equatorial != getCelestial()) hash ^= equatorial.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof AstroProjector)) return false;
		if(!super.equals(o)) return false;
		AstroProjector p = (AstroProjector) o;
		if(!Util.equals(equatorial, p.equatorial)) return false;
		return true;
	}
	
	/**
	 * Returns the current equatorial coordinates, which either correspond to the coordinates or offsets that were set last.
	 * 
	 * @return     the current equatorial coordinates of the last position or offset.
	 * 
	 * @see #getCelestial()
	 * @see #setCoordinates(Coordinate2D)
	 * @see #setEquatorial(EquatorialCoordinates)
	 * @see #setOffset(Vector2D)
	 */
	public EquatorialCoordinates getEquatorial() { return equatorial; }
	
	/**
	 * Returns the current celestial coordinates, of the same type as the reference coordinates, corresponding
	 *  to the coordinates or offsets that were set last.
	 * 
	 * @return     the current celestial coordinates of the last position or offset, of the same type as the reference coordinates.
	 *
	 * @see #setReferenceCoords()
	 * @see #getEquatorial()
	 * @see #setCoordinates(Coordinate2D)
     * @see #setEquatorial(EquatorialCoordinates)
     * @see #setOffset(Vector2D)
	 */
	public CelestialCoordinates getCelestial() { return isCelestial() ? (CelestialCoordinates) getCoordinates() : null; }
	
	/**
	 * Checks if the reference coordinates of the projection are fixed to a celestial frame, that is not e.g.  horizontal,
	 * telescope-frame or focal-plane coordinates at some specific Earth location and time.
	 * 
	 * @return     <code>true</code> if the reference coordinates are celestial coordinates, or <code>false</code> otherwise.
	 *             
	 * @see isHorizontal()
	 * @see isFocalPlane()
	 */
	private final boolean isCelestial() { return getCoordinates() instanceof CelestialCoordinates; }
    
    
	@Override
	public void setReferenceCoords() {
		super.setReferenceCoords();
		// Set the equatorial reference...
		if(isCelestial()) getCelestial().toEquatorial(equatorial);		
	}

	/**
	 * Sets a new position specified by its equatorial coordinates.
	 * 
	 * @param equatorial   the equatorial coordinates of the new position. The reference system of the supplied equatorial
	 *                     coordinates is ignored. Instead, the coordinates assumed to be in ICRS or 
	 *                     else the same reference system as the reference coordinates.
	 */
	public final void setEquatorial(EquatorialCoordinates equatorial) {
	    this.equatorial.copy(equatorial);
		if(isCelestial()) getCelestial().fromEquatorial(equatorial);
		reproject();
	}
	
	@Override
	public final void setOffset(Vector2D offset) {
		super.setOffset(offset);
		if(isCelestial()) getCelestial().toEquatorial(equatorial);
	}
}

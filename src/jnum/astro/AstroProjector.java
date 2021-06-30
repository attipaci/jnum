/*******************************************************************************
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
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/
package jnum.astro;

import jnum.Util;
import jnum.math.SphericalCoordinates;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;
import jnum.projection.Projector2D;



public class AstroProjector extends Projector2D<SphericalCoordinates> {

	private static final long serialVersionUID = -6883179727775205645L;

	private EquatorialCoordinates equatorial;

	
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
	

	public EquatorialCoordinates getEquatorial() { return equatorial; }
	
	public CelestialCoordinates getCelestial() { return isCelestial() ? (CelestialCoordinates) getCoordinates() : null; }
	
	public final boolean isHorizontal() {
		return getCoordinates() instanceof HorizontalCoordinates;
	}
	

	public final boolean isFocalPlane() {
		return getCoordinates() instanceof FocalPlaneCoordinates;
	}
	
	public final boolean isCelestial() { return getCoordinates() instanceof CelestialCoordinates; }
	
	public final boolean isEquatorial() { return getCoordinates() instanceof EquatorialCoordinates; }
	

	@Override
	public void setReferenceCoords() {
		super.setReferenceCoords();
		// Set the equatorial reference...
		if(isCelestial()) getCelestial().toEquatorial(equatorial);		
	}


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

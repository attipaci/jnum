/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.projection;

import java.io.Serializable;

import jnum.Util;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsHeaderParsing;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;


public abstract class Projection2D<CoordinateType extends Coordinate2D> implements Serializable, Cloneable,
    FitsHeaderParsing, FitsHeaderEditing {

	private static final long serialVersionUID = 4215964283613898581L;

	private CoordinateType reference;

	@SuppressWarnings("unchecked")
    @Override
	public Projection2D<CoordinateType> clone() {
		try { return (Projection2D<CoordinateType>) super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Projection2D)) return false;
		
		Projection2D<?> projection = (Projection2D<?>) o;
		if(!Util.equals(projection.reference, reference)) return false;
		
		return true;		
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(reference != null) hash ^= reference.hashCode();
		return hash;
	}

	@SuppressWarnings("unchecked")
	public Projection2D<CoordinateType> copy() {
		Projection2D<CoordinateType> copy = clone();
		if(reference != null) copy.reference = (CoordinateType) reference.copy();
		return copy;
	}


	public abstract CoordinateType getCoordinateInstance();
	

	public abstract void project(CoordinateType coords, Coordinate2D toProjected);


	public abstract void deproject(Coordinate2D projected, CoordinateType toCoords);
	

	public abstract String getFitsID();
	

	public abstract String getFullName();
	

	public CoordinateType getReference() { return reference; }
	

	public void setReference(CoordinateType coordinates) {
		reference = coordinates;
	}
	

	public Coordinate2D getProjected(CoordinateType coords) {
		Coordinate2D offset = new Coordinate2D();
		project(coords, offset);
		return offset;		
	}
	

	public CoordinateType getDeprojected(Vector2D projected) {
		CoordinateType coords = getCoordinateInstance();
		deproject(projected, coords);
		return coords;		
	}
	

	@Override
    public void parseHeader(Header header) { parseHeader(header, ""); }
	

	public abstract void parseHeader(Header header, String alt);
	

	@Override
    public void editHeader(Header header) throws HeaderCardException { editHeader(header, ""); }
	

	public abstract void editHeader(Header header, String alt) throws HeaderCardException;
	
	
}

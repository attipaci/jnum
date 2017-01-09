/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.projection;

import java.io.Serializable;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

// TODO: Auto-generated Javadoc
/**
 * The Class Projection2D.
 *
 * @param <CoordinateType> the generic type
 */
public abstract class Projection2D<CoordinateType extends Coordinate2D> implements Serializable, Cloneable  {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4215964283613898581L;
	
	/** The reference. */
	private CoordinateType reference;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Projection2D)) return false;
		Projection2D<?> projection = (Projection2D<?>) o;
		if(!Util.equals(projection.reference, reference)) return false;
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(reference != null) hash ^= reference.hashCode();
		return hash;
	}
	

	/**
	 * Copy.
	 *
	 * @return the projection2 d
	 */
	@SuppressWarnings("unchecked")
	public Projection2D<CoordinateType> copy() {
		Projection2D<CoordinateType> copy = (Projection2D<CoordinateType>) clone();
		if(reference != null) copy.reference = (CoordinateType) reference.copy();
		return copy;
	}

	/**
	 * Gets the coordinate instance.
	 *
	 * @return the coordinate instance
	 */
	public abstract CoordinateType getCoordinateInstance();
	
	/**
	 * Project.
	 *
	 * @param coords the coords
	 * @param toProjected the to projected
	 */
	public abstract void project(CoordinateType coords, Coordinate2D toProjected);

	/**
	 * Deproject.
	 *
	 * @param projected the projected
	 * @param toCoords the to coords
	 */
	public abstract void deproject(Coordinate2D projected, CoordinateType toCoords);
	
	/**
	 * Gets the fits id.
	 *
	 * @return the fits id
	 */
	public abstract String getFitsID();
	
	/**
	 * Gets the full name.
	 *
	 * @return the full name
	 */
	public abstract String getFullName();
	
	/**
	 * Gets the reference.
	 *
	 * @return the reference
	 */
	public CoordinateType getReference() { return reference; }
	
	/**
	 * Sets the reference.
	 *
	 * @param coordinates the new reference
	 */
	public void setReference(CoordinateType coordinates) {
		reference = coordinates;
	}
	
	/**
	 * Gets the projected.
	 *
	 * @param coords the coords
	 * @return the projected
	 */
	public Coordinate2D getProjected(CoordinateType coords) {
		Coordinate2D offset = new Coordinate2D();
		project(coords, offset);
		return offset;		
	}
	
	/**
	 * Gets the deprojected.
	 *
	 * @param projected the projected
	 * @return the deprojected
	 */
	public CoordinateType getDeprojected(Vector2D projected) {
		CoordinateType coords = getCoordinateInstance();
		deproject(projected, coords);
		return coords;		
	}
	
	/**
	 * Parses the.
	 *
	 * @param header the header
	 */
	public void parse(Header header) { parse(header, ""); }
	
	/**
	 * Parses the.
	 *
	 * @param header the header
	 * @param alt the alt
	 */
	public abstract void parse(Header header, String alt);
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @throws HeaderCardException the header card exception
	 */
	public void edit(Cursor<String, HeaderCard> cursor) throws HeaderCardException { edit(cursor, ""); }
	
	/**
	 * Edits the.
	 *
	 * @param cursor the cursor
	 * @param alt the alt
	 * @throws HeaderCardException the header card exception
	 */
	public abstract void edit(Cursor<String, HeaderCard> cursor, String alt) throws HeaderCardException;
	
	
}

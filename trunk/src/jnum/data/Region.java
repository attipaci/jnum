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
package jnum.data;

import java.io.Serializable;
import java.text.ParseException;

import jnum.Util;
import jnum.math.Coordinate2D;


// TODO: Auto-generated Javadoc
/**
 * The Class Region.
 *
 * @param <CoordinateType> the generic type
 */
public abstract class Region<CoordinateType extends Coordinate2D> implements Serializable, Cloneable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3415481430856577090L;

	/** The id. */
	private String id;
	
	/** The comment. */
	private String comment = "";
	
	/** The counter. */
	public static int counter = 1;
	
	/**
	 * Instantiates a new region.
	 */
	public Region() { id = "[" + (counter++) + "]"; }
	
	/**
	 * Instantiates a new region.
	 *
	 * @param line the line
	 * @param format the format
	 * @param forImage the for image
	 * @throws ParseException the parse exception
	 */
	public Region(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException { parse(line, format, forImage); }
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(comment != null) hash ^= comment.hashCode();
		if(id != null) hash ^= id.hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Region)) return false;
		if(!super.equals(o)) return false;
		Region<?> r = (Region<?>) o;
		if(!Util.equals(comment, r.comment)) return false;
		if(!Util.equals(id, r.id)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getID() { return id; }
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setID(String id) { this.id = id; }
	
	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() { return comment; }
	
	/**
	 * Sets the comment.
	 *
	 * @param value the new comment
	 */
	public void setComment(String value) { comment = value; }
	
	/**
	 * Adds the comment.
	 *
	 * @param value the value
	 */
	public void addComment(String value) { comment += value; }
	
	/**
	 * Gets the bounds.
	 *
	 * @param image the image
	 * @return the bounds
	 */
	public abstract IndexBounds2D getBounds(GridImage2D<CoordinateType> image);
	
	/**
	 * Checks if is inside.
	 *
	 * @param grid the grid
	 * @param i the i
	 * @param j the j
	 * @return true, if is inside
	 */
	public abstract boolean isInside(Grid2D<CoordinateType> grid, double i, double j);	
	
	/**
	 * Parses the.
	 *
	 * @param line the line
	 * @param format the format
	 * @param forImage the for image
	 * @throws ParseException the parse exception
	 */
	public abstract void parse(String line, int format, GridImage2D<CoordinateType> forImage) throws ParseException;
	
	/**
	 * To string.
	 *
	 * @param image the image
	 * @return the string
	 */
	public abstract String toString(GridImage2D<CoordinateType> image);
	
	/**
	 * Gets the integral.
	 *
	 * @param map the map
	 * @return the integral
	 */
	public WeightedPoint getIntegral(GridImage2D<CoordinateType> map) {
		final IndexBounds2D bounds = getBounds(map);
		WeightedPoint sum = new WeightedPoint();
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) if(map.isUnflagged(i, j)) {
			sum.add(map.getValue(i, j));	
			sum.addWeight(1.0 / map.getWeight(i, j));
		}
		sum.setWeight(map.getPointsPerSmoothingBeam() / sum.weight());	
		return sum;
	}
	
	/**
	 * Gets the flux.
	 *
	 * @param map the map
	 * @return the flux
	 */
	public WeightedPoint getFlux(GridImage2D<CoordinateType> map) {
		WeightedPoint integral = getIntegral(map);
		integral.scale(map.getPixelArea() / map.getImageBeamArea());
		return integral;
	}
	
	/** The Constant FORMAT_CRUSH. */
	public final static int FORMAT_CRUSH = 0;
	
	/** The Constant FORMAT_OFFSET. */
	public final static int FORMAT_OFFSET = 3;
	
	/** The Constant FORMAT_GREG. */
	public final static int FORMAT_GREG = 1;
	
	/** The Constant FORMAT_DS9. */
	public final static int FORMAT_DS9 = 2;
	

	
}

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
package jnum.plot;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import jnum.math.Coordinate2D;
import jnum.math.Vector2D;



// TODO: Auto-generated Javadoc
/**
 * The Class ContentLayer.
 */
public abstract class ContentLayer extends PlotLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5434391089909200423L;
	
	
	/** The user offset. */
	private Vector2D userOffset = new Vector2D();
	
	/**
	 * Gets the user offset.
	 *
	 * @return the user offset
	 */
	public Vector2D getUserOffset() { return userOffset; }
	
	/**
	 * Gets the reference coordinate.
	 *
	 * @return the reference coordinate
	 */
	public Coordinate2D getReferenceCoordinate() {
		Rectangle2D bounds = getCoordinateBounds();
		Vector2D offset = getUserOffset();
		return new Coordinate2D(bounds.getMinX() + offset.x(), bounds.getMinY() + offset.y());
	}
	
	/**
	 * Sets the user offset.
	 *
	 * @param v the new user offset
	 */
	public void setUserOffset(Vector2D v) { this.userOffset = v; }
	
	/**
	 * Center.
	 */

	public void center() {	
		Rectangle2D bounds = getCoordinateBounds();
			
		// Get the nominal center as the middle of the bounding box
		// in the native coordinates...
		setUserOffset(new Vector2D(new Point2D.Double(
				0.5 * bounds.getWidth(),
				0.5 * bounds.getHeight()
		)));
	}

	/**
	 * Align.
	 */
	public void align() { userOffset.zero(); }
	
	/**
	 * Gets the coordinate bounds.
	 *
	 * @return the coordinate bounds
	 */
	public abstract Rectangle2D getCoordinateBounds();
	
	/**
	 * Initialize.
	 */
	public abstract void initialize();

}

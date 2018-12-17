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



public abstract class ContentLayer extends PlotLayer {

	private static final long serialVersionUID = 5434391089909200423L;

	private Vector2D userOffset = new Vector2D();
	
	public Vector2D getUserOffset() { return userOffset; }
	
	public Coordinate2D getReferenceCoordinate() {
		Rectangle2D bounds = getCoordinateBounds();
		Vector2D offset = getUserOffset();
		return new Coordinate2D(bounds.getMinX() + offset.x(), bounds.getMinY() + offset.y());
	}
	

	public void setUserOffset(Vector2D v) { this.userOffset = v; }
	
	public void center() {	
		Rectangle2D bounds = getCoordinateBounds();
			
		// Get the nominal center as the middle of the bounding box
		// in the native coordinates...
		setUserOffset(new Vector2D(new Point2D.Double(
				0.5 * bounds.getWidth(),
				0.5 * bounds.getHeight()
		)));
	}

	public void align() { userOffset.zero(); }
	
	public abstract Rectangle2D getCoordinateBounds();

	public abstract void initialize();

}

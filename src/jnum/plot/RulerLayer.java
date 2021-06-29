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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Vector2D;


// TODO: Auto-generated Javadoc
/**
 * The Class RulerLayer.
 */
public class RulerLayer extends PlotLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -792580560937914048L;
	
	/** The bottom. */
	private Ruler left, right, top, bottom;
	
	/** The mirror y. */
	private boolean mirrorX = true, mirrorY = true;
	
	/**
	 * Instantiates a new ruler layer.
	 */
	public RulerLayer() {
		left = new Ruler(Plot.LEFT_SIDE);
		right = new Ruler(Plot.RIGHT_SIDE);
		top = new Ruler(Plot.TOP_SIDE);
		bottom = new Ruler(Plot.BOTTOM_SIDE);
	}
	
	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#defaults()
	 */
	@Override
	public void defaults() {
		Scale xDivs = new Scale(1);
		Scale xSubs = new Scale(10);
		Scale yDivs = new Scale(1);
		Scale ySubs = new Scale(10);
		
		left.setMainDivisions(yDivs);
		left.setSubdivisions(ySubs);
		
		right.setMainDivisions(yDivs);
		right.setSubdivisions(ySubs);
		
		top.setMainDivisions(xDivs);
		top.setSubdivisions(xSubs);
		
		bottom.setMainDivisions(xDivs);
		bottom.setSubdivisions(xSubs);
		
		updateDivisions();
	}
	
	/**
	 * Update divisions.
	 */
	public void updateDivisions() {
		Rectangle bounds = this.getContentArea().getBounds();
		Range xRange = new Range(bounds.getMinX(), bounds.getMaxX());
		Range yRange = new Range(bounds.getMinY(), bounds.getMaxY());
		
		left.setRange(xRange);
		top.setRange(yRange);

		if(!mirrorY) right.setRange(xRange);
		if(!mirrorX) bottom.setRange(yRange);
	}
	
	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Make sure the rulers are the same size as the plot itself...
		Dimension size = getSize();
		
		left.setSize(size);
		right.setSize(size);
		top.setSize(size);
		bottom.setSize(size);
		
		// Now, go ahead and paint...
		left.paint(g);
		right.paint(g);
		top.paint(g);
		bottom.paint(g);
	}

	/**
	 * The Class Ruler.
	 */
	public class Ruler extends BasicRuler {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -3305810445444215892L;

		/**
		 * Instantiates a new ruler.
		 *
		 * @param edge the edge
		 */
		public Ruler(int edge) {
			super(edge);
		}
		
		/* (non-Javadoc)
		 * @see jnum.plot.BasicRuler#getPosition(double, java.awt.geom.Point2D)
		 */
		@Override
		public void getPosition(double value, Point2D pos) {
			ContentArea<?> contentArea = getContentArea(); 
			Coordinate2D offset = contentArea.getContentLayer().getReferenceCoordinate();

			// set the value at the reference position of the other coordinate...
			if(isHorizontal()) pos.setLocation(offset.x(), value);
			else if(isVertical()) pos.setLocation(value, offset.y());
			
			// convert to display coordinates...
			contentArea.toDisplay(pos);
		}

		/* (non-Javadoc)
		 * @see jnum.plot.BasicRuler#getValue(java.awt.geom.Point2D)
		 */
		@Override
		public double getValue(Point2D pos) {
			ContentArea<?> contentArea = getContentArea(); 
			Vector2D ref = contentArea.getReferencePoint();
			
			// Move the position to the reference of the other axis...
			if(isHorizontal()) pos.setLocation(pos.getX(), ref.y());
			else if(isVertical()) pos.setLocation(ref.x(), pos.getY());	
			
			// convert to content coordinates
			contentArea.toCoordinates(pos);
			
			// return the value for the appropriate axis...
			if(isHorizontal()) return pos.getX();
			else if(isVertical()) return pos.getY();
			else return Double.NaN;
		}
		
		/**
		 * Sets the plot.
		 *
		 * @param plot the new plot
		 */
		public void setPlot(Plot<?> plot) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}

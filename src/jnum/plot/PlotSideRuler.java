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
package jnum.plot;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import jnum.Unit;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;


// TODO: Auto-generated Javadoc
/**
 * The Class PlotSideRuler.
 */
public class PlotSideRuler extends FancyRuler {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3259644546352934697L;
	
	/** The plot. */
	private Plot<?> plot;
	
	
	/** The is inside. */
	boolean isInside = true;
	
	/**
	 * Instantiates a new plot side ruler.
	 *
	 * @param plot the plot
	 * @param edge the edge
	 */
	public PlotSideRuler(Plot<?> plot, int edge) {
		super(edge);
		
		getAxisLabel().showUnit(true);
		setPlot(plot);
		if(edge == Plot.TOP_SIDE || edge == Plot.RIGHT_SIDE) showLabels(false);
		
		//System.err.println("### created side ruler " + edge);
	}
	
	/**
	 * Gets the plot.
	 *
	 * @return the plot
	 */
	public Plot<?> getPlot() { return plot; }
	
	/**
	 * Sets the plot.
	 *
	 * @param plot the new plot
	 */
	public void setPlot(Plot<?> plot) { 
		this.plot = plot; 
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Container#validate()
	 */
	@Override
	public void validate() {		
		Rectangle2D bounds = plot.getCoordinateBounds(getSide());
		
		ContentArea<?> content = plot.getContent();
		
		if(isHorizontal()) {
			Unit u = content.getXUnit();
			if(u != null) setUnit(u);
			setRange(bounds.getMinX(), bounds.getMaxX());
			if(content.coordinateSystem != null) setName(content.coordinateSystem.get(0).getFancyLabel());
		}
		else if(isVertical()) {
			Unit u = content.getYUnit();
			if(u != null) setUnit(u);
			setRange(bounds.getMinY(), bounds.getMaxY());		
			if(content.coordinateSystem != null) setName(content.coordinateSystem.get(1).getFancyLabel());
		}
		
		super.validate();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		//System.err.println("### painting side ruler...");
		validate();
		super.paint(g);		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);	
	}
	
	
	/**
	 * Gets the content area.
	 *
	 * @return the content area
	 */
	public ContentArea<?> getContentArea() { return getPlot().getContent(); }
	
	/* (non-Javadoc)
	 * @see jnum.plot.BasicRuler#getPosition(double, java.awt.geom.Point2D)
	 */
	@Override
	public void getPosition(double value, Point2D pos) {
		ContentArea<?> contentArea = getContentArea(); 
			
		// set the value at the reference position of the other coordinate...
		Coordinate2D offset = contentArea.getContentLayer().getReferenceCoordinate();
		if(isHorizontal()) pos.setLocation(value, offset.y());
		else if(isVertical()) pos.setLocation(offset.x(), value);

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
	
	/* (non-Javadoc)
	 * @see kovacs.plot.FancyRuler#setRange(double, double)
	 */
	@Override
	public void setRange(double min, double max) {
		boolean isAutoAngles = isHorizontal() ? getContentArea().isAutoAngleX :  getContentArea().isAutoAngleY; 
		
		if(isAutoAngles) {
			double d = Math.abs(max - min);
			if(d > 3.0 * Unit.deg) unit = Unit.get("deg");
			else if(d > 3.0 * Unit.arcmin) unit = Unit.get("arcmin");
			else unit = Unit.get("arcsec");
		}
		
		super.setRange(min, max);
	}
	
}

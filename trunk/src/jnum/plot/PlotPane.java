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
package jnum.plot;

import java.awt.geom.Point2D;

import javax.swing.JComponent;


// TODO: Auto-generated Javadoc
/**
 * The Class PlotPane.
 */
public class PlotPane extends JComponent {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3614256696574188825L;
	
	/** The plot. */
	private Plot<?> plot;

	/**
	 * Instantiates a new plot pane.
	 *
	 * @param plot the plot
	 */
	public PlotPane(Plot<?> plot) { 
		setPlot(plot);
	}
	
	/**
	 * Sets the plot.
	 *
	 * @param plot the new plot
	 */
	public void setPlot(Plot<?> plot) { this.plot = plot; }
	
	/**
	 * Gets the plot.
	 *
	 * @return the plot
	 */
	public Plot<?> getPlot() { return plot; }
	
	/**
	 * Gets the content area.
	 *
	 * @return the content area
	 */
	public ContentArea<?> getContentArea() { return plot.getContent(); }
	
	/**
	 * To coordinates.
	 *
	 * @param point the point
	 */
	public void toCoordinates(Point2D point) { getContentArea().toCoordinates(point); }
	
	/**
	 * To display.
	 *
	 * @param point the point
	 */
	public void toDisplay(Point2D point) { getContentArea().toCoordinates(point); }

	
}

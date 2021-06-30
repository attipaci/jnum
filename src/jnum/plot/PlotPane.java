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

import javax.swing.JComponent;


public class PlotPane extends JComponent {

	private static final long serialVersionUID = 3614256696574188825L;

	private Plot<?> plot;

	public PlotPane(Plot<?> plot) { 
		setPlot(plot);
	}
	

	public void setPlot(Plot<?> plot) { this.plot = plot; }
	

	public Plot<?> getPlot() { return plot; }

	public ContentArea<?> getContentArea() { return plot.getContent(); }

	public void toCoordinates(Point2D point) { getContentArea().toCoordinates(point); }

	public void toDisplay(Point2D point) { getContentArea().toCoordinates(point); }

	
}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

// TODO: Auto-generated Javadoc
// TODO Enclosing box. Ticks handled by ruler...

/**
 * The Class CoordinateLayer.
 */
public class CoordinateLayer extends PlotLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -771338655043033632L;
	
	/** The pen color. */
	Color penColor = Color.BLACK;
	
	/** The border width. */
	int borderWidth = 0;
	
	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;	
		g2.setColor(penColor);
		g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		g2.draw(new Rectangle(0, 0, getContentArea().getWidth(), getContentArea().getHeight()));
	}

	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#defaults()
	 */
	@Override
	public void defaults() {

	}
	
}

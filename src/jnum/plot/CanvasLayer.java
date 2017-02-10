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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;


// TODO: Auto-generated Javadoc
/**
 * The Class CanvasLayer.
 */
public class CanvasLayer extends PlotLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6890933149678172692L;
	
	/** The color. */
	Color color = null;
	
	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;	
		
		g2.setColor(color);
		g2.setStroke(new BasicStroke(0));
		
		g2.fill(new Rectangle(0, 0, getContentArea().getWidth(), getContentArea().getHeight()));
	}

	/* (non-Javadoc)
	 * @see jnum.plot.PlotLayer#defaults()
	 */
	@Override
	public void defaults() {}
}

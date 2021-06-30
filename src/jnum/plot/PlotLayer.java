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

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;


public abstract class PlotLayer extends JComponent {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1331038094866703152L;

	private ContentArea<?> contentArea;

	public abstract void defaults();

	public ContentArea<?> getContentArea() { return contentArea; }

	public void setContentArea(ContentArea<?> area) { this.contentArea = area; }

	public AffineTransform toDisplay() { return getContentArea().toDisplay(); }

	public AffineTransform toCoordinates() { return getContentArea().toCoordinates(); }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);	
	}
	

}

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

// TODO: Auto-generated Javadoc
/**
 * The Class ImageArea.
 *
 * @param <ContentType> the generic type
 */
public class ImageArea<ContentType extends ImageLayer> extends ContentArea<ContentType> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2517432416174422671L;

	/**
	 * Instantiates a new image area.
	 */
	public ImageArea() {
		// Fit the image into the plottable area...
		setZoomMode(ZOOM_FIT);
		// Set the reference at the center of the plot...
		getReferencePoint().set(0.5, 0.5);
	}
	
}
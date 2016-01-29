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

import jnum.data.Data2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Data2DLayer.
 */
public class Data2DLayer extends BufferedImageLayer.Double {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5873926029025733309L;
	
	/** The image. */
	private Data2D image;
	

	
	/**
	 * Instantiates a new data2 d layer.
	 *
	 * @param image the image
	 */
	public Data2DLayer(Data2D image) {
		this.image = image;
		setData(image.getData());
	}
	
	/**
	 * Gets the data2 d.
	 *
	 * @return the data2 d
	 */
	public Data2D getData2D() { return image; }
	
	

	/* (non-Javadoc)
	 * @see kovacs.util.plot.ImageLayer.Double#getValue(int, int)
	 */
	@Override
	public double getValue(int i, int j) {
		return image.valueAtIndex(i + getSubarrayOffset().i(), j + getSubarrayOffset().j());
	}
	

	
}

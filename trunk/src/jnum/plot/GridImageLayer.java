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

import java.awt.geom.NoninvertibleTransformException;

import jnum.Util;
import jnum.data.Grid2D;
import jnum.data.GridImage2D;
import jnum.data.SphericalGrid;


// TODO: Auto-generated Javadoc
/**
 * The Class GridImageLayer.
 */
public class GridImageLayer extends Data2DLayer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5730801953668713086L;
	
	/** The grid. */
	private Grid2D<?> grid;
	
	/**
	 * Instantiates a new grid image layer.
	 *
	 * @param image the image
	 */
	public GridImageLayer(GridImage2D<?> image) {
		super(image);
			
		grid = image.getGrid();
		
		try { setCoordinateTransform(grid.getLocalAffineTransform()); }
		catch(NoninvertibleTransformException e) { Util.warning(this, e); }
	}
	
	/* (non-Javadoc)
	 * @see kovacs.plot.ImageLayer#setContentArea(kovacs.plot.ContentArea)
	 */
	@Override
	public void setContentArea(ContentArea<?> area) {
		super.setContentArea(area);
				
		area.setXUnit(grid.xUnit);
		area.setYUnit(grid.yUnit);
		
		if(grid instanceof SphericalGrid) area.isAutoAngleX = area.isAutoAngleY = true;
		
		area.coordinateSystem = grid.getCoordinateSystem();
		
				
	}
	
	/**
	 * Gets the grid.
	 *
	 * @return the grid
	 */
	public Grid2D<?> getGrid() { return grid; }
		
	/**
	 * Gets the grid image.
	 *
	 * @return the grid image
	 */
	public GridImage2D<?> getGridImage() { return (GridImage2D<?>) getData2D(); }
	
}

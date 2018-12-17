/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
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

import java.awt.geom.NoninvertibleTransformException;

import jnum.Util;
import jnum.data.image.Grid2D;
import jnum.data.image.SphericalGrid;
import jnum.data.image.Values2D;



public class GridImageLayer extends BufferedImageLayer {

	private static final long serialVersionUID = 5730801953668713086L;

	private Grid2D<?> grid;


	public GridImageLayer(Values2D data, Grid2D<?> grid) {
		super(data);
			
		setGrid(grid);
		
		try { setCoordinateTransform(grid.getLocalAffineTransform()); }
		catch(NoninvertibleTransformException e) { Util.warning(this, e); }
	}
	
	public void setGrid(Grid2D<?> grid) { this.grid = grid; }
	
	public Grid2D<?> getGrid() { return grid; }
	
	
	/* (non-Javadoc)
	 * @see kovacs.plot.ImageLayer#setContentArea(kovacs.plot.ContentArea)
	 */
	@Override
	public void setContentArea(ContentArea<?> area) {
		super.setContentArea(area);
				
		area.setXUnit(grid.xAxis().getUnit());
		area.setYUnit(grid.yAxis().getUnit());
		
		if(grid instanceof SphericalGrid) area.isAutoAngleX = area.isAutoAngleY = true;
		
		area.coordinateSystem = grid.getCoordinateSystem();
		
				
	}
	
	
	
}

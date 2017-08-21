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

package jnum.data.image;

import jnum.data.CartesianGrid;
import jnum.math.Coordinate2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

public class Grid2DPlus<BaseCoordType extends Coordinate2D, SubGridType extends CartesianGrid<?>> extends Grid2D<BaseCoordType> {
    /**
     * 
     */
    private static final long serialVersionUID = 4447299668551169567L;
    
    private Grid2D<BaseCoordType> baseGrid;
    private SubGridType subGrid;
    
    public Grid2D<BaseCoordType> getBaseGrid() { return baseGrid; }
    
    public void setBaseGrid(Grid2D<BaseCoordType> grid) {
        this.baseGrid = grid;
    }
    
    public SubGridType getSubGrid() { return subGrid; }
    
    public void setSubGrid(SubGridType grid) { this.subGrid = grid; }
    
    @Override
    public void parseProjection(Header header) throws HeaderCardException {
       baseGrid.parseProjection(header);
    }
    
    @Override
    public BaseCoordType getCoordinateInstanceFor(String type) throws InstantiationException, IllegalAccessException {
        return baseGrid.getCoordinateInstanceFor(type);
    }

    @Override
    public final int dimension() {
       return 3;
    }
   

}

/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube2;

import jnum.Unit;
import jnum.data.cube.Index3D;
import jnum.data.image.Flag2D;
import jnum.data.image.Grid2D;
import jnum.data.image.Map2D;
import jnum.data.samples.Grid1D;
import jnum.math.Coordinate2D;

public abstract class AbstractMap2D1<MapType extends Map2D> extends Resizable2D1<MapType> {
     
    private Class<? extends Number> dataType = Double.class;
    private int flagType = Flag2D.TYPE_INT;
    
    
    private Grid2D<Coordinate2D> grid2D;
    private Grid1D grid1D;
    private Unit unit;

    
    public AbstractMap2D1(Class<? extends Number> dataType, int flagType) {
        this.dataType = dataType;
        this.flagType = flagType;
    }
    
    @Override
    public final Class<? extends Number> getElementType() { return dataType; }
    
    public final int getFlagType() { return flagType; }
  
    
    public void flag(int i, int j, int k, long value) { getPlane(k).flag(i, j, value); }

    public final void flag(Index3D index, long value) { flag(index.i(), index.j(), index.k(), value); }

    public void unflag(int i, int j, int k, long value) { getPlane(k).unflag(i, j, value); }

    public final void unflag(Index3D index, long value) { unflag(index.i(), index.j(), index.k(), value); }


    public void flag(int i, int j, int k) { getPlane(k).flag(i, j); }

    public final void flag(Index3D index) { flag(index.i(), index.j(), index.k()); }

    public void unflag(int i, int j, int k) { getPlane(k).unflag(i, j); }

    public final void unflag(Index3D index) { unflag(index.i(), index.j(), index.k()); }
    
    
    
    @Override
    public void addPlane(MapType map) {
        super.addPlane(map);
        
        if(!getStack().isEmpty()) {
            map.setGrid(getGrid2D());
            map.setUnit(getUnit());
        }
    }
    
    
    public Grid2D<?> getGrid2D() {
        return grid2D; 
    }
    
    @SuppressWarnings("unchecked")
    public void setGrid2D(Grid2D<?> grid) {
        this.grid2D = (Grid2D<Coordinate2D>) grid;
        for(Map2D map : getStack()) map.setGrid(grid);
    }
    
    public Grid1D getGrid1D() {
        return grid1D;
    }
    
    public void setGrid1D(Grid1D grid) {
        this.grid1D = grid;
    }
    
    @Override
    public Unit getUnit() {
        return unit;
    }
    
    @Override
    public void setUnit(Unit u) {
        this.unit = u;
    }
    
    @Override
    public void setUnit(String spec) {
        getPlane().setUnit(spec);
        setUnit(getPlane().getUnit());
    }
    
    public Coordinate2D getReference2D() {
        return getPlane().getReference();
    }
    
    public void setReference2D(Coordinate2D coords) {
        grid2D.setReference(coords);
        for(Map2D map : getStack()) map.setReference(coords);
    }
    
    public double getReference1D() {
        return grid1D.getReference();
    }
    
    public void setReference1D(double value) {
        grid1D.setReference(value);
    }
   
}

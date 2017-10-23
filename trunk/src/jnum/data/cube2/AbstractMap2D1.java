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


import java.util.Hashtable;

import jnum.Unit;
import jnum.Util;
import jnum.data.cube.Index3D;
import jnum.data.image.Grid2D;
import jnum.data.image.Map2D;
import jnum.data.samples.Grid1D;
import jnum.math.Coordinate2D;


public abstract class AbstractMap2D1<MapType extends Map2D> extends Resizable2D1<MapType> {
         
    private MapType mapTemplate;
  
    private Class<? extends Number> dataType;
    private int flagType;
    
    private Grid1D grid1D;
  
    
    public AbstractMap2D1(Class<? extends Number> dataType, int flagType) {
        this.dataType = dataType;
        this.flagType = flagType;
        mapTemplate = getPlaneInstance();
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
    public void addLocalUnit(Unit u) {
        mapTemplate.addLocalUnit(u);
        for(MapType plane : getPlanes()) plane.addLocalUnit(u);
    }
    
    @Override
    public void addLocalUnit(Unit u, String altNames) {
        mapTemplate.addLocalUnit(u, altNames);
        for(MapType plane : getPlanes()) plane.addLocalUnit(u, altNames);
    }
    
    @Override
    public Hashtable<String, Unit> getLocalUnits() { return mapTemplate.getLocalUnits(); }

    
    private void applyTemplateTo(MapType map) {
        if(mapTemplate == null) return;
        
        map.getProperties().copy(mapTemplate.getProperties());     
        map.setCriticalFlags(mapTemplate.getCriticalFlags());
        
        for(Unit u : mapTemplate.getLocalUnits().values()) map.addLocalUnit(u);
        map.setUnit(mapTemplate.getUnit().name());
    }
    

    public void makeConsistent() {
        for(MapType map : getPlanes()) if(map != mapTemplate) applyTemplateTo(map);
    }
    
 
    @Override
    public void addPlane(MapType map) {
        applyTemplateTo(map);
        if(getPlanes().isEmpty()) mapTemplate = map;
        super.addPlane(map);  
    }
    
    @Override
    public MapType getPlane() {
        return mapTemplate;
    }
    
    
    public Grid2D<?> getGrid2D() {
        return mapTemplate.getGrid(); 
    }
    
    public void setGrid2D(Grid2D<?> grid) {
        mapTemplate.setGrid(grid);
        for(Map2D map : getPlanes()) map.setGrid(grid);
    }
    
    public Grid1D getGrid1D() {
        return grid1D;
    }
    
    public void setGrid1D(Grid1D grid) {
        this.grid1D = grid;
    }
    
    @Override
    public Unit getUnit() {
        return mapTemplate.getUnit();
    }
    
    @Override
    public void setUnit(Unit u) {
        mapTemplate.setUnit(u);
    }
    
    @Override
    public void setUnit(String spec) {
        mapTemplate.setUnit(spec);
    }
    
    public Coordinate2D getReference2D() {
        return getPlane().getReference();
    }
    
    public void setReference2D(Coordinate2D coords) {
        mapTemplate.setReference(coords);
        for(Map2D map : getPlanes()) map.setReference(coords);
    }
    
    public double getReference1D() {
        return grid1D.getReference().value();
    }
    
    public void setReference1D(double value) {
        grid1D.setReference(value);
    }
    
    public long getCriticalFlags() { return mapTemplate.getCriticalFlags(); }
    
    public void setCriticalFlags(long pattern) {
        mapTemplate.setCriticalFlags(pattern);
        for(Map2D map : getPlanes()) map.setCriticalFlags(pattern);
    }
    
    public final void renew() {
        mapTemplate.renew();
        for(Map2D map : getPlanes()) map.renew();
    }
    
    @Override
    public String getInfo() {
        Grid1D gridZ = getGrid1D();
        Unit specUnit = gridZ.getAxis().getUnit();
        return getPlane().getInfo() + "\n" +
                "Spectral: " + sizeZ() + " bins @ " + 
                Util.s3.format(gridZ.getReference().value() / specUnit.value()) + " with " +
                Util.s3.format(gridZ.getResolution().value() / specUnit.value()) + " resolution";
                
    }
   
}

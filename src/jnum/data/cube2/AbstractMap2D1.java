/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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
import jnum.data.Referenced;
import jnum.data.RegularData;
import jnum.data.cube.Index3D;
import jnum.data.image.Gaussian2D;
import jnum.data.image.Grid2D;
import jnum.data.image.Index2D;
import jnum.data.image.Map2D;
import jnum.data.image.Validating2D;
import jnum.data.samples.Grid1D;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.math.Vector3D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;


public abstract class AbstractMap2D1<MapType extends Map2D> extends Resizable2D1<MapType> implements Referenced<Index3D, Vector3D> {


    private Class<? extends Number> dataType;
    private int flagType;

    private Grid1D grid1D;


    public AbstractMap2D1(Class<? extends Number> dataType, int flagType) {
        this.dataType = dataType;
        this.flagType = flagType;
        grid1D = new Grid1D(3);
        setDefaultUnit();
    }
   

    @SuppressWarnings("unchecked")
    public AbstractMap2D1<MapType> copy(boolean withContents) {
        AbstractMap2D1<MapType> copy = (AbstractMap2D1<MapType>) super.clone();        
        for(int k=sizeZ(); --k >= 0; ) copy.setPlane(k, (MapType) getPlane(k).copy(withContents));
        return copy;
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
    public void cropXY(Index2D from, Index2D to) {
        for(Map2D plane : getPlanes()) plane.crop(from, to);
    }
    
    
    @Override
    public void addLocalUnit(Unit u) {
        getPlaneTemplate().addLocalUnit(u);
        for(MapType plane : getPlanes()) plane.addLocalUnit(u);
    }

    @Override
    public void addLocalUnit(Unit u, String altNames) {
        getPlaneTemplate().addLocalUnit(u, altNames);
        for(MapType plane : getPlanes()) plane.addLocalUnit(u, altNames);
    }

    @Override
    public Hashtable<String, Unit> getLocalUnits() { return getPlaneTemplate().getLocalUnits(); }


    @Override
    protected void applyTemplateTo(MapType map) {
        super.applyTemplateTo(map);
        
        MapType template = getPlaneTemplate();
        
        if(template == null) return;

        map.copyPropertiesFrom(template);     
        map.setValidatingFlags(template.getValidatingFlags());

        for(Unit u : template.getLocalUnits().values()) map.addLocalUnit(u);
        map.setUnit(template.getUnit().name());
    }


    public Grid2D<?> getGrid2D() {
        return getPlaneTemplate().getGrid();
    }

    public void setGrid2D(Grid2D<?> grid) {
        getPlaneTemplate().setGrid(grid);
        for(Map2D map : getPlanes()) map.setGrid(grid);
    }

    public Grid1D getGrid1D() {
        return grid1D;
    }

    public void setGrid1D(Grid1D grid) {
        grid.setFirstAxisIndex(3);
        this.grid1D = grid;
    }


    
    
    public Coordinate2D getReference2D() {
        return getPlaneTemplate().getReference();
    }

    public void setReference2D(Coordinate2D coords) {
        getPlaneTemplate().setReference(coords);
        for(Map2D map : getPlanes()) map.setReference(coords);
    }

    public double getReference1D() {
        return grid1D.getReference().value();
    }

    public void setReference1D(double value) {
        grid1D.setReference(value);
    }

    public long getCriticalFlags() { return getPlaneTemplate().getValidatingFlags(); }

    public void setCriticalFlags(long pattern) {
        getPlaneTemplate().setValidatingFlags(pattern);
        for(Map2D map : getPlanes()) map.setValidatingFlags(pattern);
    }

    public final void renew() {
        getPlaneTemplate().renew();
        for(Map2D map : getPlanes()) map.renew();
    }
    
  
    public final void crop(Vector3D from, Vector3D to) {
        cropZ(from.z(), to.z());
        cropXY(new Vector2D(from.x(), from.y()), new Vector2D(to.x(), to.y()));
    }
    
    public void cropXY(Vector2D from, Vector2D to) {
        for(Map2D plane : getPlanes()) plane.crop(from, to);
    }
    
    public void cropZ(double fromZ, double toZ) {
        Grid1D g = getGrid1D();
        int fromk = (int) Math.floor(g.indexOf(fromZ)); 
        cropZ(fromk, (int) Math.ceil(g.indexOf(toZ)));
        g.getReferenceIndex().subtract(fromk);
    }
    

    public void smoothXY(double FWHM) {
        for(Map2D plane : getPlanes()) plane.smooth(FWHM);
    }

    public void smoothXYTo(double FWHM) {
        for(Map2D plane : getPlanes()) plane.smoothTo(FWHM);
    }

    public void smoothXY(Gaussian2D psf) {
        for(Map2D plane : getPlanes()) plane.smooth(psf);
    }

    public void smoothXYTo(Gaussian2D psf) {
        for(Map2D plane : getPlanes()) plane.smoothTo(psf);
    }
    
    public void filterXYAbove(double FWHM) {
        for(Map2D plane : getPlanes()) plane.filterAbove(FWHM);
    }

    public void filterXYAbove(double FWHM, Validating2D validator) {
        for(Map2D plane : getPlanes()) plane.filterAbove(FWHM, validator);
    }
    

    public void filterBeamCorrect() {
        for(Map2D plane : getPlanes()) plane.filterBeamCorrect();
    }


    public void resetFiltering() {
        if(getPlaneTemplate() != null) getPlaneTemplate().resetFiltering();
        for(Map2D plane : getPlanes()) plane.resetFiltering();
    }
    
    public void resetProcessing() {
        if(getPlaneTemplate() != null) getPlaneTemplate().resetProcessing();
        for(Map2D plane : getPlanes()) plane.resetProcessing();
    }

    
    // TODO smoothZ(fwhm), smoothZTo(fwhm)
    // TODO filterZAbove(fwhm), filterZAbove(fwhm, validator)


    @Override
    public Vector3D getReferenceIndex() { 
        Vector3D refIndex = new Vector3D();
        Vector2D ref2D = getGrid2D().getReferenceIndex();
        
        refIndex.setX(ref2D.x());
        refIndex.setY(ref2D.y());
        refIndex.setZ(getGrid1D().getReferenceIndex().getValue());
        
        return refIndex;
    }
    
    @Override
    public void setReferenceIndex(Vector3D refIndex) { 
        Vector2D ref2D = getGrid2D().getReferenceIndex();
        
        ref2D.setX(refIndex.x());
        ref2D.setY(refIndex.y());
        getGrid1D().getReferenceIndex().setValue(refIndex.z());
    }  
    
    @Override
    public RegularData<Index3D, Vector3D> getData() { return this; }
    
    @Override
    public String getInfo() {
        Grid1D gridZ = getGrid1D();
        Unit specUnit = gridZ.getAxis().getUnit();
        return getPlaneTemplate().getInfo() + "\n" +
                "Spectral: " + sizeZ() + " bins @ " + 
                Util.s3.format(gridZ.getReference().value() / specUnit.value()) + " with " +
                Util.s3.format(gridZ.getResolution().value() / specUnit.value()) + " resolution";
    }
    
    
    @Override
    protected void editHeader(Header header) throws HeaderCardException {   
        grid1D.editHeader(header);
        MapType representative = sizeZ() > 0 ? getPlane(0) : getPlaneTemplate();
        representative.editHeader(header);    
        super.editHeader(header);
    }

}

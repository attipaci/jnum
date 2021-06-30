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

package jnum.data.samples;

import jnum.Util;
import jnum.data.CartesianGrid;
import jnum.math.CoordinateAxis;
import jnum.math.Scalar;



public class Grid1D extends CartesianGrid<Scalar> {

    private static final long serialVersionUID = -4841377680124156007L;

    private Scalar refIndex, refValue, resolution;
    
    public Grid1D() {
        super(1);
        refIndex = new Scalar();
        refValue = new Scalar();
        resolution = new Scalar();
    }
    
    public Grid1D(int axisIndexFromOne) {
        this();
        setFirstAxisIndex(axisIndexFromOne);
    }
    
    public final CoordinateAxis getAxis() { return getCoordinateSystem().get(0); }
    
    @Override
    public int hashCode() { return super.hashCode() ^ refIndex.hashCode() ^ refValue.hashCode() ^ resolution.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Grid1D)) return false;
        
        Grid1D grid = (Grid1D) o;
        if(!Util.equals(refIndex, grid.refIndex)) return false;
        if(!Util.equals(refValue, grid.refValue)) return false;
        if(!Util.equals(resolution, grid.resolution)) return false;
        
        return true;
    }

    @Override
    public Grid1D clone() {
        try { return (Grid1D) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public final int dimension() { return 1; }

    @Override
    public void indexOf(Scalar coord, Scalar toIndex) {
        toIndex.setValue(indexOf(coord.value()));
    }
    
    public double indexOf(double coord) {
        return (coord - refValue.value()) / resolution.value() + refIndex.value();
    }

    @Override
    public void coordsAt(Scalar index, Scalar toCoord) {
        toCoord.setValue(coordAt(index.value()));
    }
    
    public double coordAt(double index) {
        return (index - refIndex.value()) * resolution.value() + refValue.value();
    }

    @Override
    public void setReference(Scalar coords) {
        refValue = coords;
    }
    
    public void setReference(double value) {
        setReference(new Scalar(value));
    }

    @Override
    public Scalar getReference() {
        return refValue;
    }

    @Override
    public void setReferenceIndex(Scalar index) {
        refIndex = index;
    }
    
    public void setReferenceIndex(double index) {
        setReferenceIndex(new Scalar(index));
    }

    @Override
    public Scalar getReferenceIndex() {
        return refIndex;
    }

    @Override
    public void setResolution(Scalar delta) {
        resolution = delta;
    }
    
    public void setResolution(double delta) {
        setResolution(new Scalar(delta));
    }

    @Override
    public Scalar getResolution() {
       return resolution;
    }

 
    @Override
    public void setResolution(int axis, double resolution) {
        if(axis != 0) throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no axis " + axis);
        setResolution(resolution);
    }

    @Override
    public double getResolution(int axis) {
        if(axis != 0) throw new IndexOutOfBoundsException(getClass().getSimpleName() + " has no axis " + axis);
        return resolution.value();
    }

    @Override
    public double getReferenceIndex(int axis) {
        return axis == 0 ? refIndex.value() : Double.NaN;
    }

    @Override
    public void setReferenceIndex(int axis, double value) {
        if(axis == 0) refIndex.setValue(value);
    }

    @Override
    public double getReferenceValue(int axis) {
       return axis == 0 ? refValue.value() : Double.NaN;
    }

    @Override
    public void setReferenceValue(int axis, double value) {
        if(axis == 0) refValue.setValue(value);
    }

}

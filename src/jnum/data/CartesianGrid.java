/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data;


import jnum.Unit;
import jnum.fits.FitsToolkit;
import jnum.math.CartesianSystem;
import jnum.math.CoordinateAxis;
import jnum.math.CoordinateSystem;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;


/**
 * A class reresenting a Cartesian grid of coordinates of some generic type.
 * 
 * @author Attila Kovacs
 *
 * @param <CoordinateType>  The generic type of coordinates in this Cartesian grid.
 */
public abstract class CartesianGrid<CoordinateType> extends Grid<CoordinateType, CoordinateType> {
    /**
     * 
     */
    private static final long serialVersionUID = 1682113209739647771L;
    
    private int firstAxis = 1;
    
    
    /**
     * Constructs a new Cartesian grid with the with the specified dimensions (number of axes).
     * This constructor is available only for initializing subclass implementations. 
     * 
     * @param dimensions    The number of axes in the underlying Cartesian coordinate syste, 
     */
    protected CartesianGrid(int dimensions) {
        setCoordinateSystem(new CartesianSystem(dimensions));
    }
           
    /**
     * Gets the index of the first Cartesian axis, counted from 1, in a potentially heterogeneous
     * compound coordinate system and grid. For purely Cartesian systems/grids, this will
     * usually return 1. The index is mainly used when reporting or parsing grids from
     * files, such as FITS (hence the counting from 1 here...)
     * 
     * @return  the index of the first Cartesian axis in a possible compound grid, counting from 1.
     */
    public int getFirstAxisIndex() { return firstAxis; }

    /**
     * Sets the index of the first Cartesian axis, counted from 1, in a potentially heterogeneous
     * compound coordinate system and grid. For purely Cartesian systems/grids, this will
     * usually return 1. The index is mainly used when reporting or parsing grids from
     * files, such as FITS (hence the counting from 1 here...)
     * 
     * @param countedFromOne  the index of the first Cartesian axis in a possible compound grid, counting from 1.
     */
    public void setFirstAxisIndex(int countedFromOne) { this.firstAxis = countedFromOne; }
   
    /**
     * Gets the grid resolution in the direction of the specified axis.
     * 
     * @param axis  the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @return      the grid resolution along that axis.
     */
    public abstract double getResolution(int axis);
  
    /**
     * Sets a new grid resolution in the direction of the specified axis.
     * 
     * @param axis          the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @param resolution    the new grid resolution along that axis.
     */
    public abstract void setResolution(int axis, double resolution);
    
    /**
     * Gets the fractional grid index of the reference point on the specified axis of this grid.
     * 
     * @param axis  the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @return      the grid index of the reference point on the specified axis.
     */
    public abstract double getReferenceIndex(int axis);
    
    /**
     * Set a new fractional grid index for the reference point for the specified axis of this grid.
     * 
     * @param axis      the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @param value     the new grid index of the reference point on the specified axis.
     */
    public abstract void setReferenceIndex(int axis, double value);
    
    /**
     * Gets the coordinate of the reference point on the specified axis of this grid.
     * 
     * @param axis  the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @return      the coordinate of the reference point on the specified axis.
     */
    public abstract double getReferenceValue(int axis);
    
    
    /**
     * Sets a new coordinate value for the reference point on the specified axis of this grid.
     * 
     * @param axis      the axis index in this grid alone, without regard for any embedding, counted from zero.
     * @param value     the coordinate value for the reference point on the specified axis.
     */
    public abstract void setReferenceValue(int axis, double value);
   
    /**
     * Converts a fractional index location on this grid to physical coordinates.
     * 
     * @param index     the index location on the grid (input), which is transformed into physical coordinates (output).
     */
    public void indexToValue(CoordinateType index) { coordsAt(index, index); }
 
    /**
     * Converts a physical coordinate location to a fractional index on this grid.
     * 
     * @param value     the physical coordinates (input), which are tranformed into a fractional grid index (output).
     */
    public void valueToIndex(CoordinateType value) { indexOf(value, value); }

    
    @Override
    public void parseHeader(Header header) throws Exception {
        String alt = getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
        
        CoordinateSystem system = getCoordinateSystem();
        for(int i=0; i<system.size(); i++) {
            CoordinateAxis axis = system.get(i);
            int index = firstAxis + i;
            String id = index + alt;
            
            if(header.containsKey("CTYPE" + id)) axis.setShortLabel(header.getStringValue("CTYPE" + id));
            else axis.setShortLabel("Axis " + index);
            
            if(header.containsKey("CUNIT" + id)) axis.setUnit(Unit.get(header.getStringValue("CUNIT" + id)));
            else axis.setUnit(Unit.unity);         
                   
            setReferenceIndex(i, header.getDoubleValue("CRPIX" + id, 1.0) - 1.0);    
            setReferenceValue(i, header.getDoubleValue("CRVAL" + id, 0.0) * axis.getUnit().value());
            setResolution(i, header.getDoubleValue("CDELT" + id, 1.0) * axis.getUnit().value());         
        }
      
    }
    
    @Override
    public void editHeader(Header header) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        String alt = getFitsID();
        
        CoordinateSystem system = getCoordinateSystem();
        for(int i=0; i<system.size(); i++) {
            CoordinateAxis axis = system.get(i);
            int index = firstAxis + i;
            String id = index + alt;
            
            String name = axis.getShortLabel();
            String unitName = (axis.getUnit() == Unit.unity) ? "" : "(" +  axis.getUnit().name() + ") ";
            
            c.add(new HeaderCard("CTYPE" + id, name, "Axis type"));
            if(axis.getUnit() != null) c.add(new HeaderCard("CUNIT" + id, axis.getUnit().name(), name + " unit"));
            c.add(new HeaderCard("CRPIX" + id, getReferenceIndex(i) + 1.0, name + " reference grid index (1-based)"));
            c.add(new HeaderCard("CRVAL" + id, getReferenceValue(i) / axis.getUnit().value(), unitName + name + " value at reference index"));
            c.add(new HeaderCard("CDELT" + id, getResolution(i) / axis.getUnit().value(), unitName + name + " spacing."));            
        }
        
    }
   
    
    /**
     * A Cartesian grid with double-precision coordinate and index variables.
     * 
     * @author Attila Kovacs
     *
     */
    public static class Doubles extends CartesianGrid<double[]> {

        /**
         * 
         */
        private static final long serialVersionUID = 2696835013611823922L;
        
        private double[] refIndex, refValue, resolution;
        
        /**
         * Constructs a new Cartesian grid, with double precision coordinates and indices.
         * 
         * @param dimension
         */
        public Doubles(int dimension) {
            super(dimension);
            
            refIndex = new double[dimension];
            refValue = new double[dimension];
            resolution = new double[dimension];    
        }
        
        @Override
        public void setResolution(int axis, double resolution) {
            this.resolution[axis] = resolution;
        }

        @Override
        public double getResolution(int axis) {
            return resolution[axis];
        }

        @Override
        public void setReference(double[] coords) {
            refValue = coords;
        }

        @Override
        public double[] getReference() {
            return refValue;
        }

        @Override
        public void setReferenceIndex(double[] index) {
            refIndex = index;
        }

        @Override
        public double[] getReferenceIndex() {
            return refIndex;
        }

        @Override
        public void setResolution(double[] delta) {
            this.resolution = delta;
        }

        @Override
        public double[] getResolution() {
            return resolution;
        }

       
        @Override
        public void coordsAt(double[] index, double[] toValue) {
            for(int i=toValue.length; --i >= 0; ) {
                toValue[i] = (index[i] - refIndex[i]) * resolution[i] + refValue[i];
            }
        }
        
         
        @Override
        public final void indexOf(double[] value, double[] toIndex) {
            for(int i=toIndex.length; --i >= 0; ) {
                toIndex[i] = (value[i] - refValue[i]) / resolution[i] + refIndex[i];
            }
        }

        
        @Override
        public int dimension() {
            return refIndex.length;
        }

        @Override
        public double getReferenceIndex(int axis) {
            return refIndex[axis];
        }

        @Override
        public void setReferenceIndex(int axis, double value) {
           refIndex[axis] = value;
        }

        @Override
        public double getReferenceValue(int axis) {
            return refValue[axis];
        }

        @Override
        public void setReferenceValue(int axis, double value) {
            refValue[axis] = value;
        }
        
    }
    
    
    
    /**
     * A Cartesian grid with single-precision coordinate and index variables.
     * 
     * @author Attila Kovacs
     *
     */
    public static class Floats extends CartesianGrid<float[]> {

        /**
         * 
         */
        private static final long serialVersionUID = -1879946881203425985L;
        
        private float[] refIndex, refValue, resolution;
        
        /**
         * Constructs a new Cartesian grid, with single precision coordinates and indices.
         * 
         * @param dimension
         */
        public Floats(int dimension) {
            super(dimension);
            
            refIndex = new float[dimension];
            refValue = new float[dimension];
            resolution = new float[dimension];    
        }
        
        @Override
        public void setResolution(int axis, double resolution) {
            this.resolution[axis] = (float) resolution;
        }

        @Override
        public double getResolution(int axis) {
            return resolution[axis];
        }

        @Override
        public void setReference(float[] coords) {
            refValue = coords;
        }

        @Override
        public float[] getReference() {
            return refValue;
        }

        @Override
        public void setReferenceIndex(float[] index) {
            refIndex = index;
        }

        @Override
        public float[] getReferenceIndex() {
            return refIndex;
        }

        @Override
        public void setResolution(float[] delta) {
            this.resolution = delta;
        }

        @Override
        public float[] getResolution() {
            return resolution;
        }

       
        @Override
        public void coordsAt(float[] index, float[] toValue) {
            for(int i=toValue.length; --i >= 0; ) {
                toValue[i] = (index[i] - refIndex[i]) * resolution[i] + refValue[i];
            }
        }

        
        @Override
        public final void indexOf(float[] value, float[] toIndex) {
            for(int i=toIndex.length; --i >= 0; ) {
                toIndex[i] = (value[i] - refValue[i]) / resolution[i] + refIndex[i];
            }
        }
        
        @Override
        public int dimension() {
            return refIndex.length;
        }

        @Override
        public double getReferenceIndex(int axis) {
            return refIndex[axis];
        }

        @Override
        public void setReferenceIndex(int axis, double value) {
            refIndex[axis] = (float) value;
        }

        @Override
        public double getReferenceValue(int axis) {
            return refValue[axis];
        }

        @Override
        public void setReferenceValue(int axis, double value) {
            refValue[axis] = (float) value;
        }
        
    }
    
}

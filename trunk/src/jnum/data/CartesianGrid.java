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

public abstract class CartesianGrid<CoordinateType> extends Grid<CoordinateType, CoordinateType> {
    /**
     * 
     */
    private static final long serialVersionUID = 1682113209739647771L;
    
    private int firstAxis = 1;
    
    
   
    protected CartesianGrid(int dimensions) {
        setCoordinateSystem(new CartesianSystem(dimensions));
    }
           
    public int getFirstAxisIndex() { return firstAxis; }

    public void setFirstAxisIndex(int countedFromOne) { this.firstAxis = countedFromOne; }
    
  
    public abstract void setResolution(int axis, double resolution);
    
    public abstract double getResolution(int axis);
    

    public void indexToValue(CoordinateType index) { coordsAt(index, index); }
 
    public void valueToIndex(CoordinateType value) { indexOf(value, value); }

    
    @Override
    public void parseHeader(Header header) throws Exception {
        String alt = getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
        
        CoordinateSystem system = getCoordinateSystem();
        for(int i=0; i<system.size(); i++) {
            CoordinateAxis axis = system.get(i);
            int index = firstAxis + i;
            String spec = index + alt;
            
            if(header.containsKey("CTYPE" + spec)) axis.setShortLabel(header.getStringValue("CTYPE" + spec));
            else axis.setShortLabel("Axis " + index);
            
            if(header.containsKey("CUNIT" + spec)) axis.setUnit(Unit.get(header.getStringValue("CUNIT" + spec)));
            else axis.setUnit(Unit.unity);
            
            if(header.containsKey("CDELT" + spec)) setResolution(i, header.getDoubleValue("CDELT" + spec) * axis.getUnit().value());    
            else setResolution(i, axis.getUnit().value());
        }
      
    }
    
    @Override
    public void editHeader(Header header) throws HeaderCardException {
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        String alt = getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
        
        CoordinateSystem system = getCoordinateSystem();
        for(int i=0; i<system.size(); i++) {
            CoordinateAxis axis = system.get(i);
            int index = firstAxis + i;
            String spec = index + alt;
            String id = "Axis-" + index;
            
            c.add(new HeaderCard("CTYPE" + spec, axis.getShortLabel(), id + " name"));
            if(axis.getUnit() != null) c.add(new HeaderCard("CUNIT" + spec, axis.getUnit().name(), id + " unit"));
            c.add(new HeaderCard("CDELT" + spec, getResolution(i), id + " spacing."));
            
        }
        
    }
   
    
    public static class Doubles extends CartesianGrid<double[]> {

        /**
         * 
         */
        private static final long serialVersionUID = 2696835013611823922L;
        
        private double[] refIndex, refValue, resolution;
        
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
        
    }
    
    
    
    
    public static class Floats extends CartesianGrid<float[]> {

        /**
         * 
         */
        private static final long serialVersionUID = -1879946881203425985L;
        
        private float[] refIndex, refValue, resolution;
        
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
        
    }
    
}

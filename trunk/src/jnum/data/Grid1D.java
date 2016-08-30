/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data;


public class Grid1D implements Grid<Double, Double> {
    /**
     * 
     */
    private static final long serialVersionUID = -4841377680124156007L;
    private double refIndex, refValue, resolution;

    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public Double indexOf(Double value) {
        return (value - refValue) / resolution + refIndex;
    }
    
    @Override
    public Double valueAt(Double index) {
        return (index - refIndex) * resolution + refValue;
    }

    @Override
    public void setReference(Double coords) {
        refValue = coords;
    }

    @Override
    public Double getReference() {
        return refValue;
    }

    @Override
    public void setReferenceIndex(Double index) {
        refIndex = index;
    }

    @Override
    public Double getReferenceIndex() {
        return refIndex;
    }

    @Override
    public void setResolution(Double delta) {
        resolution = delta;
    }

    @Override
    public Double getResolution() {
       return resolution;
    }

   
    
    
}

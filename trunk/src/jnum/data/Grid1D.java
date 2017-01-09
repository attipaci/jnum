/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

package jnum.data;


// TODO: Auto-generated Javadoc
/**
 * The Class Grid1D.
 */
public class Grid1D implements Grid<Double, Double> {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4841377680124156007L;
    
    /** The resolution. */
    private double refIndex, refValue, resolution;

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        try { return super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    /* (non-Javadoc)
     * @see jnum.data.Grid#indexOf(java.lang.Object)
     */
    @Override
    public Double indexOf(Double value) {
        return (value - refValue) / resolution + refIndex;
    }
    
    /* (non-Javadoc)
     * @see jnum.data.Grid#valueAt(java.lang.Object)
     */
    @Override
    public Double valueAt(Double index) {
        return (index - refIndex) * resolution + refValue;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#setReference(java.lang.Object)
     */
    @Override
    public void setReference(Double coords) {
        refValue = coords;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#getReference()
     */
    @Override
    public Double getReference() {
        return refValue;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#setReferenceIndex(java.lang.Object)
     */
    @Override
    public void setReferenceIndex(Double index) {
        refIndex = index;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#getReferenceIndex()
     */
    @Override
    public Double getReferenceIndex() {
        return refIndex;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#setResolution(java.lang.Object)
     */
    @Override
    public void setResolution(Double delta) {
        resolution = delta;
    }

    /* (non-Javadoc)
     * @see jnum.data.Grid#getResolution()
     */
    @Override
    public Double getResolution() {
       return resolution;
    }

   
    
    
}

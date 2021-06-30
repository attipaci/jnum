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

import java.io.Serializable;

import jnum.NonConformingException;
import jnum.fits.FitsHeaderEditing;
import jnum.fits.FitsHeaderParsing;
import jnum.math.CoordinateSystem;


public abstract class Grid<CoordinateType, OffsetType> implements Serializable, Cloneable,
FitsHeaderEditing, FitsHeaderParsing {

    /**
     * 
     */
    private static final long serialVersionUID = -8132999541122592649L;

    private CoordinateSystem coordinateSystem;
    
    private int variant = 0;
    
    
    public final CoordinateSystem getCoordinateSystem() { return coordinateSystem; }


    public final void setCoordinateSystem(CoordinateSystem system) {
        if(system.size() != dimension()) throw new NonConformingException("coordinate system / grid mismatch.");
        coordinateSystem = system;
    }
    
   
   
    public final int getVariant() { return variant; }
    
    public final void setVariant(int index) { this.variant = index; }
    

    protected String getFitsID() {
        return getVariant() == 0 ? "" : Character.toString((char) ('A' + getVariant()));
    }
    
    
    public abstract void setReference(CoordinateType coords);


    public abstract CoordinateType getReference();


    public abstract void setReferenceIndex(OffsetType index);


    public abstract OffsetType getReferenceIndex();


    public abstract void setResolution(OffsetType delta);


    public abstract OffsetType getResolution();


    public abstract void coordsAt(OffsetType index, CoordinateType toValue);
    

    public abstract void indexOf(CoordinateType value, OffsetType toIndex);
 

    public abstract int dimension();

}

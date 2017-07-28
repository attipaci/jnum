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

package jnum.data.cube.overlay;

import jnum.data.cube.Value3D;
import jnum.math.Coordinate3D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

public class Referenced3D extends Overlay3D {
    private Coordinate3D referenceIndex;

    public Referenced3D() {}

    public Referenced3D(Value3D values) {
        super(values);
    }
    
    public Referenced3D(Value3D values, Coordinate3D refIndex) {
        this(values);
        setReferenceIndex(refIndex);
    }

    
    @Override
    public int hashCode() { return super.hashCode() ^ referenceIndex.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Referenced3D)) return false;
        
        Referenced3D r = (Referenced3D) o;
        if(!referenceIndex.equals(r.referenceIndex)) return false;
        
        return super.equals(o);
    }

    public Coordinate3D getReferenceIndex() { return referenceIndex; }
    
    public void setReferenceIndex(Coordinate3D index) { this.referenceIndex = index; }
    
    
    @Override
    public void editHeader(Header header) throws HeaderCardException {
        super.editHeader(header);
        referenceIndex.editHeader(header, "CRPIX", "");
    }
    
    @Override
    public void parseHeader(Header header) {
        super.parseHeader(header);
        referenceIndex.parseHeader(header, "CRPIX", "");
    }
    

}

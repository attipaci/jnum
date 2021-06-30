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

package jnum.data.cube.overlay;

import jnum.data.Referenced;
import jnum.data.RegularData;
import jnum.data.cube.Index3D;
import jnum.data.cube.Values3D;
import jnum.math.Coordinate3D;
import jnum.math.Vector3D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

public class Referenced3D extends Overlay3D implements Referenced<Index3D, Vector3D> {
    private Vector3D referenceIndex;

    public Referenced3D() {}

    public Referenced3D(Values3D values) {
        super(values);
    }
    
    public Referenced3D(Values3D values, Vector3D refIndex) {
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

    @Override
    public Vector3D getReferenceIndex() { return referenceIndex; }
    
    @Override
    public void setReferenceIndex(Vector3D index) { this.referenceIndex = index; }
    
    
    @Override
    public void editHeader(Header header) throws HeaderCardException {
        super.editHeader(header);
        new Coordinate3D(referenceIndex.x() + 1.0, referenceIndex.y() + 1.0, referenceIndex.z() + 1.0).editHeader(header, "CRPIX", "");
        
        referenceIndex.editHeader(header, "CRPIX", "");
    }
    
    @Override
    public void parseHeader(Header header) {
        super.parseHeader(header);
        referenceIndex.parseHeader(header, "CRPIX", "", new Coordinate3D(1.0, 1.0, 1.0));
        
        referenceIndex.subtractX(-1.0);
        referenceIndex.subtractY(-1.0);
        referenceIndex.subtractZ(-1.0);
    }

    @Override
    public RegularData<Index3D, Vector3D> getData() {
        return this;
    }
    

}

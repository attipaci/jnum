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

package jnum.data.image.overlay;

import jnum.data.Referenced;
import jnum.data.RegularData;
import jnum.data.image.Index2D;
import jnum.data.image.Values2D;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCardException;

public class Referenced2D extends Overlay2D implements Referenced<Index2D, Vector2D> {
    private Vector2D referenceIndex;

    public Referenced2D() {}

    public Referenced2D(Values2D values) {
        super(values);
    }
    
    public Referenced2D(Values2D values, Vector2D refIndex) {
        this(values);
        setReferenceIndex(refIndex);
    }

    
    @Override
    public int hashCode() { return super.hashCode() ^ referenceIndex.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Referenced2D)) return false;
        
        Referenced2D r = (Referenced2D) o;
        if(!referenceIndex.equals(r.referenceIndex)) return false;
        
        return super.equals(o);
    }

    @Override
    public Vector2D getReferenceIndex() { return referenceIndex; }
    
    @Override
    public void setReferenceIndex(Vector2D index) { this.referenceIndex = index; }

    @Override
    public void editHeader(Header header) throws HeaderCardException {
        super.editHeader(header);
        new Coordinate2D(referenceIndex.x() + 1.0, referenceIndex.y() + 1.0).editHeader(header, "CRPIX", "");
    }
    
    @Override
    public void parseHeader(Header header) {
        super.parseHeader(header);
        referenceIndex.parseHeader(header, "CRPIX", "", new Coordinate2D(1.0, 1.0));
        referenceIndex.subtractX(1.0);
        referenceIndex.subtractY(1.0);
        
    }

    @Override
    public RegularData<Index2D, Vector2D> getData() {
        return this;
    }
   
}

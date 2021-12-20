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

package jnum.data.samples.overlay;

import jnum.Util;
import jnum.data.Data;
import jnum.data.Referenced;
import jnum.data.RegularData;
import jnum.data.index.Index1D;
import jnum.data.samples.Values1D;
import jnum.fits.FitsToolkit;
import jnum.math.Position;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public class Referenced1D extends Overlay1D implements Referenced<Index1D, Position> {
    private Position referenceIndex;

    public Referenced1D() { this(null); }

    public Referenced1D(Values1D values) {
        this(values, 0.0);
    }
    
    public Referenced1D(Values1D values, double refIndex) {
        this(values, new Position(refIndex));
    }
    
    public Referenced1D(Values1D values, Position refIndex) {
        super(values);
        setReferenceIndex(refIndex);
    }
    
    @Override
    public Referenced1D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Referenced1D newInstance(Index1D size) {
        Referenced1D r = (Referenced1D) super.newInstance(size);
        r.referenceIndex = referenceIndex.copy();
        return r;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof Referenced1D) referenceIndex = ((Referenced1D) other).referenceIndex.copy();
    }

    
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(referenceIndex.x()); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Referenced1D)) return false;
        
        Referenced1D r = (Referenced1D) o;
        if(!Util.equals(referenceIndex, r.referenceIndex)) return false;
        
        return true;
    }

    @Override
    public Position getReferenceIndex() { return referenceIndex; }
    
    public void setReferenceIndex(double value) {
        referenceIndex.setValue(value);
    }
    
    @Override
    public void setReferenceIndex(Position index) { this.referenceIndex = index; }

    @Override
    public void editHeader(Header header) throws HeaderCardException {
        super.editHeader(header);
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard("CRPIX1", referenceIndex.value() + 1.0, "The reference x coordinate in SI units."));
        
    }
    
    @Override
    public void parseHeader(Header header) {
        super.parseHeader(header);
        referenceIndex.setValue(header.getDoubleValue("CRPIX1", 1.0) - 1.0);
    }

    @Override
    public RegularData<Index1D, Position> getData() {
        return this;
    }
    
}

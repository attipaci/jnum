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

package jnum.data.samples.overlay;

import jnum.data.IndexReferenced;
import jnum.data.samples.Value1D;
import jnum.fits.FitsToolkit;
import jnum.util.HashCode;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.HeaderCardException;
import nom.tam.util.Cursor;

public class Referenced1D extends Overlay1D implements IndexReferenced<Double> {
    private double referenceIndex;

    public Referenced1D() {}

    public Referenced1D(Value1D values) {
        super(values);
    }
    
    public Referenced1D(Value1D values, double refIndex) {
        this(values);
        setReferenceIndex(refIndex);
    }

    
    @Override
    public int hashCode() { return super.hashCode() ^ HashCode.from(referenceIndex); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Referenced1D)) return false;
        
        Referenced1D r = (Referenced1D) o;
        if(referenceIndex != r.referenceIndex) return false;
        
        return super.equals(o);
    }

    @Override
    public Double getReferenceIndex() { return referenceIndex; }
    
    @Override
    public void setReferenceIndex(Double index) { this.referenceIndex = index; }

    @Override
    public void editHeader(Header header) throws HeaderCardException {
        super.editHeader(header);
        Cursor<String, HeaderCard> c = FitsToolkit.endOf(header);
        c.add(new HeaderCard("CRPIX1", referenceIndex, "The reference x coordinate in SI units."));
        
    }
    
    @Override
    public void parseHeader(Header header) {
        super.parseHeader(header);
        referenceIndex = header.getDoubleValue("CRPIX1", 0.0);
    }
    
}

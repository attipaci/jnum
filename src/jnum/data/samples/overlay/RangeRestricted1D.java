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
import jnum.data.RangeRestricted;
import jnum.data.index.Index1D;
import jnum.data.samples.Values1D;
import jnum.math.Range;

public class RangeRestricted1D extends Overlay1D implements RangeRestricted {
    private Range validRange;
    
    
    public RangeRestricted1D(Values1D base, Range restriction) {
        super(base);
        setValidRange(restriction);
    }
    
    @Override
    public RangeRestricted1D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public RangeRestricted1D newInstance(Index1D size) {
        RangeRestricted1D r = (RangeRestricted1D) super.newInstance(size);
        r.validRange = validRange.copy();
        return r;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof RangeRestricted) validRange = ((RangeRestricted) other).getValidRange().copy();
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ validRange.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof RangeRestricted1D)) return false;
        RangeRestricted1D r = (RangeRestricted1D) o;
        if(!Util.equals(validRange, r.validRange)) return false;
        return true;
    }
    
    
    @Override
    public void setValidRange(Range r) { 
        validRange = r; 
    }
    
    @Override
    public Range getValidRange() { return validRange; }
    
    
    @Override
    public boolean isValid(int i) {
        if(!validRange.contains(get(i).doubleValue())) return false;
        return super.isValid(i);
    }

   
    
}

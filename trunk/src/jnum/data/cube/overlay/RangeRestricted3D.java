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
import jnum.math.Range;

public class RangeRestricted3D extends Overlay3D {
    private Range validRange;
    
    
    public RangeRestricted3D(Value3D base, Range restriction) {
        super(base);
        setValidRange(restriction);
    }
    
    public void setValidRange(Range r) { validRange = r; }
    
    public Range getValidRange() { return validRange; }
    
    
    @Override
    public boolean isValid(int i, int j, int k) {
        return validRange.contains(get(i, j, k).doubleValue()) && super.isValid(i, j, k);
    }

   
    
}

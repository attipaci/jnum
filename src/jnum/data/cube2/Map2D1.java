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

package jnum.data.cube2;


import jnum.data.FlagCompanion;
import jnum.data.image.Map2D;
import jnum.data.index.Index3D;


public class Map2D1 extends AbstractMap2D1<Map2D> {

      
    public Map2D1(Class<? extends Number> dataType, FlagCompanion.Type flagType) {
        super(dataType, flagType);
    }
    
    @Override
    public Map2D newPlaneInstance() { 
        return new Map2D(getElementType(), getFlagType()); 
    }
    
    @Override
    public Map2D1 newInstance() {
        return newInstance(getSize());
    }

    @Override
    public Map2D1 newInstance(Index3D size) {
        Map2D1 map = new Map2D1(getElementType(), getFlagType());
        map.copyPoliciesFrom(this);
        return map;
    }
 
}

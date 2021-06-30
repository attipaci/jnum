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

package jnum.data.image.transform;

import jnum.data.image.Grid2D;
import jnum.math.Transforming;
import jnum.math.Vector2D;

public class CartesianGridTransform2D implements Transforming<Vector2D> {
    private Grid2D<?> fromGrid;
    private Grid2D<?> toGrid;
    
    public CartesianGridTransform2D(Grid2D<?> from, Grid2D<?> to) {
        this.fromGrid = from;
        this.toGrid = to;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ fromGrid.hashCode() ^ toGrid.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CartesianGridTransform2D)) return false;
        
        CartesianGridTransform2D t = (CartesianGridTransform2D) o;
        if(!fromGrid.equals(t.fromGrid)) return false;
        if(!toGrid.equals(t.toGrid)) return false;
     
        return true;
    }
    
    @Override
    public void transform(Vector2D index) {
        fromGrid.toOffset(index);
        toGrid.toIndex(index);
    }

   

}

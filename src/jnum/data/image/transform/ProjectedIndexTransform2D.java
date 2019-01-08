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

package jnum.data.image.transform;

import jnum.data.Transforming;
import jnum.data.image.Grid2D;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.projection.Projector2D;

public class ProjectedIndexTransform2D<CoordinateType extends Coordinate2D> implements Transforming<Vector2D> {
    private Grid2D<CoordinateType> fromGrid;
    private Grid2D<CoordinateType> toGrid;
    
    private Projector2D<CoordinateType> fromProjector;
    private Projector2D<CoordinateType> toProjector;
    
    
    public ProjectedIndexTransform2D(Grid2D<CoordinateType> from, Grid2D<CoordinateType> to) {
        this.fromGrid = from;
        this.toGrid = to;
        
        fromProjector = new Projector2D<CoordinateType>(from.getProjection());
        toProjector = new Projector2D<CoordinateType>(to.getProjection());
    }
    
    @Override
    public int hashCode() { return super.hashCode() ^ fromGrid.hashCode() ^ toGrid.hashCode(); }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ProjectedIndexTransform2D)) return false;
        
        ProjectedIndexTransform2D<?> t = (ProjectedIndexTransform2D<?>) o;
        if(!fromGrid.equals(t.fromGrid)) return false;
        if(!toGrid.equals(t.toGrid)) return false;
        return true;
    }
    
    
    @Override
    public void transform(Vector2D index) {
        fromGrid.toOffset(index);
        transformOffset(index);
        toGrid.toIndex(index);
    }
    
    public void transformOffset(Vector2D offset) {
        fromProjector.setOffset(offset);
        toProjector.getCoordinates().convertFrom(fromProjector.getCoordinates());
        toProjector.reproject();
        offset.copy(toProjector.getOffset());
    }
    
}

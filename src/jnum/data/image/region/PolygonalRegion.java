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
package jnum.data.image.region;

import java.util.*;

import jnum.IncompatibleTypesException;
import jnum.Util;
import jnum.data.image.Grid2D;
import jnum.data.image.IndexBounds2D;
import jnum.math.Coordinate2D;
import jnum.math.Range;
import jnum.math.Vector2D;
import jnum.text.StringParser;

import java.text.ParseException;



public class PolygonalRegion extends Region2D {

	private static final long serialVersionUID = -7872681437465288221L;

	private Vector<Coordinate2D> corners = new Vector<>();
	

	public PolygonalRegion(Class<? extends Coordinate2D> coordinateType) { 
	    super(coordinateType);
	}

	// TODO better hashCode...
	@Override
	public int hashCode() { return super.hashCode() ^ corners.hashCode(); }
	

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof PolygonalRegion)) return false;
		if(!super.equals(o)) return false;
		PolygonalRegion p = (PolygonalRegion) o;
		if(corners.size() != p.corners.size()) return false;
		for(int i=corners.size(); --i >= 0; ) if(!Util.equals(corners.get(i), p.corners.get(i))) return false;
		return true;
	}

	

	
	@Override
	public void parse(StringParser parser, int format) throws ParseException {
		// TODO
	}
	 
	

    @Override
    public String toString(int format) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Region2D.Representation getRepresentation(Grid2D<?> grid) throws IncompatibleTypesException {
        return new Representation(grid);
    }
   
	
	
	public class Representation extends Region2D.Representation {
	    private Vector<Vector2D> indices;
	    
	    protected Representation(Grid2D<?> grid) throws IncompatibleTypesException {
	        super(grid);
	        indices = new Vector<>(corners.size());
	        for(int i=0; i<corners.size(); i++) indices.add(getIndex(corners.get(i)));
	    }
	    
        @Override
        public boolean isInside(double i, double j) {
            int cross = 0;
  
            for(int n=indices.size(); --n >= 0; ) {
                Vector2D from = indices.get(n);
                Vector2D to = indices.get((n+1) % corners.size());
                
                double mini = Math.min(from.x(), to.x());
                double maxi = Math.max(from.x(), to.x());
                double intersect = i < mini || i > maxi ? Double.NaN : 
                    from.y() + (to.y()-from.y())*(i-from.x())/(to.x() - from.x());

                if(intersect <= j) cross++;
            }
            
            return cross%2 != 0;
        }

        @Override
        public IndexBounds2D getBounds() {
            Range x = new Range();
            Range y = new Range();
                        
            for(Vector2D v : indices) {
                x.include(v.x());
                y.include(v.y());
            }
            
            return new IndexBounds2D(x.min(), y.min(), x.max(), y.max());
        }
	    
	}


	
}

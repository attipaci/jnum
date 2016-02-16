/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.data;

import java.util.*;

import jnum.Util;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.projection.Projection2D;

import java.text.ParseException;


public class PolygonalRegion<CoordinateType extends Coordinate2D> extends Region<CoordinateType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7872681437465288221L;

	Vector<CoordinateType> points = new Vector<CoordinateType>();
	
	//String name = "polygon";
	boolean isClosed = false;

	private Vector2D reuseFrom = new Vector2D(), reuseTo = new Vector2D();
	
	public PolygonalRegion() {}
	
	public PolygonalRegion(String fileName, int format, GridImage2D<CoordinateType> forImage) throws ParseException {
		parse(fileName, format, forImage); 
	}
	
	@Override
	public int hashCode() { return super.hashCode() ^ points.hashCode() ^ (isClosed ? 1 : 0); }
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof PolygonalRegion)) return false;
		if(!super.equals(o)) return false;
		PolygonalRegion<?> p = (PolygonalRegion<?>) o;
		if(isClosed != p.isClosed) return false;
		if(points.size() != p.points.size()) return false;
		for(int i=points.size(); --i >= 0; ) if(!Util.equals(points.get(i), p.points.get(i))) return false;
		return true;
	}
	
	@Override
	public Object clone() {
		PolygonalRegion<?> polygon = (PolygonalRegion<?>) super.clone();
		polygon.reuseFrom = new Vector2D();
		polygon.reuseTo = new Vector2D();
		return polygon;
	}
	
	public void close() { isClosed = true; }
	

	@Override
	public boolean isInside(Grid2D<CoordinateType> grid, double i, double j) {
		Projection2D<CoordinateType> projection = grid.getProjection();
		int below = 0;

		final Vector2D from = reuseFrom;
		final Vector2D to = reuseTo;
		
		for(int n=points.size(); --n >= 0; ) {
			projection.project(points.get(n), from);
			projection.project(points.get((n+1) % points.size()), to);
			
			grid.toIndex(from);
			grid.toIndex(to);
			
			double mini = Math.min(from.x(), to.x());
			double maxi = Math.max(from.x(), to.x());
			double intersect = i < mini || i > maxi ? 
					Double.NaN : 
					from.y() + (to.y()-from.y())*(i-from.x())/(to.x() - from.x());

			if(intersect <= j) below++;
		}
		
		return below%2 == 1;
	}
	

	@Override
	public IndexBounds2D getBounds(GridImage2D<CoordinateType> image) {
		Vector2D min = (Vector2D) points.get(0).clone();
		Vector2D max = (Vector2D) points.get(0).clone();
		
		Vector2D vertex = reuseFrom;
		Projection2D<CoordinateType> projection = image.getProjection();
		
		for(CoordinateType coords : points) {
			projection.project(coords, vertex);
			
			if(vertex.x() < min.x()) min.setX(vertex.x());
			else if(vertex.x() > max.x()) max.setX(vertex.x());
			
			if(vertex.y() < min.y()) min.setY(vertex.y());			
			else if(vertex.y() > max.y()) max.setY(vertex.y());				
		}
		
		Vector2D delta = image.getGrid().getResolution();
		min.scaleX(1.0 / delta.x());
		min.scaleY(1.0 / delta.y());
		max.scaleX(1.0 / delta.x());
		max.scaleY(1.0 / delta.y());
		
		IndexBounds2D bounds = new IndexBounds2D();
		bounds.fromi = (int) Math.floor(Math.min(min.x(), max.x()));
		bounds.toi = (int) Math.ceil(Math.max(min.x(), max.x()));
		bounds.fromj = (int) Math.floor(Math.min(min.y(), max.y()));
		bounds.toj = (int) Math.ceil(Math.max(min.y(), max.y()));
		
		return bounds;
	}
	
	@Override
	public WeightedPoint getFlux(GridImage2D<CoordinateType> image) {
		WeightedPoint flux = new WeightedPoint();
		
		IndexBounds2D bounds = getBounds(image);
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++)
			if(image.isUnflagged(i, j)) if(isInside(image.getGrid(), i, j)) {
				flux.add(image.getValue(i, j));
				flux.addWeight(image.getWeight(i, j));
			}
		
		flux.setWeight(1.0 / flux.weight());
		flux.scale(image.getPixelArea() / image.getImageBeamArea());
		
		return flux;
	}
	
	public double getInsideLevel(GridImage2D<CoordinateType> image) {
		double sum = 0.0, sumw = 0.0;
		IndexBounds2D bounds = getBounds(image);
		
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) 
			if(image.isUnflagged(i, j)) if(isInside(image.getGrid(), i, j)) {
				final double weight = image.getWeight(i, j);
				sum += weight * image.getValue(i, j);
				sumw += weight;
			}
			
		return sum / sumw;			
	}
	
	public double getRMS(GridImage2D<CoordinateType> image) {
		double level = getInsideLevel(image);
		IndexBounds2D bounds = getBounds(image);
		
		double var = 0.0;
		int n = 0;
		for(int i=bounds.fromi; i<=bounds.toi; i++) for(int j=bounds.fromj; j<=bounds.toj; j++) 
			if(image.isUnflagged(i, j)) if(isInside(image.getGrid(), i, j)) {
				double value = image.getValue(i, j) - level;
				var += value * value;
				n++;
			}
		var /= (n-1);
		
		return Math.sqrt(var);
	}
	

	
	@Override
	public void parse(String spec, int format, GridImage2D<CoordinateType> forImage) throws ParseException {
		points.clear();
		
		StringTokenizer tokens = new StringTokenizer(spec, ";\n");
		CoordinateType reference = forImage.getReference();
		
		while(tokens.hasMoreTokens()) {
			@SuppressWarnings("unchecked")
			CoordinateType coords = (CoordinateType) reference.clone();
			coords.parse(tokens.nextToken());
			points.add(coords);
		}
	}

	@Override
	public String toString(GridImage2D<CoordinateType> image) {
		// TODO Auto-generated method stub
		return null;
	}

}

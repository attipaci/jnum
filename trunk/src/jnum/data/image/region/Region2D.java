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

package jnum.data.image.region;

import java.io.Serializable;

import jnum.IncompatibleTypesException;
import jnum.Util;
import jnum.data.image.Flag2D;
import jnum.data.image.Grid2D;
import jnum.data.image.IndexBounds2D;
import jnum.data.image.Validating2D;
import jnum.data.image.Value2D;
import jnum.data.image.overlay.Viewport2D;
import jnum.math.Coordinate2D;
import jnum.math.Vector2D;
import jnum.text.StringParser;


public abstract class Region2D implements Serializable, Cloneable {

	private static final long serialVersionUID = 3415481430856577090L;

	private Class<? extends Coordinate2D> coordinateClass;
	
	private String id;

	private String comment = "";

	public static int counter = 1;

	public Region2D(Class<? extends Coordinate2D> coordinateType) { 
	    this.coordinateClass = coordinateType;
	    id = "[" + (counter++) + "]";    
	}
	
	public Class<? extends Coordinate2D> getCoordinateClass() { return coordinateClass; }
	
	protected void setCoordinateClass(Class<? extends Coordinate2D> type) { this.coordinateClass = type; }
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(comment != null) hash ^= comment.hashCode();
		if(id != null) hash ^= id.hashCode();
		return hash;
	}
	

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Region2D)) return false;
		
		Region2D r = (Region2D) o;
		if(!Util.equals(comment, r.comment)) return false;
		if(!Util.equals(id, r.id)) return false;
		return true;
	}
	

	@Override
	public Object clone() {
		try { return super.clone(); }
		catch(CloneNotSupportedException e) { return null; }
	}
	

	public String getID() { return id; }
	

	public void setID(String id) { this.id = id; }
	

	public String getComment() { return comment; }
	
	public void setComment(String value) { comment = value; }
	

	public void addComment(String value) { comment += value; }
	

    public final void parse(String line, int format) throws Exception{
        if(line == null) return;
        if(line.length() == 0) return;
        if(line.charAt(0) == '#' || line.charAt(0) == '!') return;
        
        parse(new StringParser(line), format);
    }
    
    public abstract void parse(StringParser parse, int format) throws Exception;
    
    
    @Override
    public String toString() {
        return toString(FORMAT_CRUSH);
    }    
    
    public abstract String toString(int format);
	
	
	public abstract Representation getRepresentation(Grid2D<?> grid) throws IncompatibleTypesException;	
	
	
	//public abstract void convertTo(Coordinate2D template) throws IncompatibleTypesException;

	
	/** The Constant FORMAT_CRUSH. */
	public final static int FORMAT_CRUSH = 0;
	
	/** The Constant FORMAT_DS9. */
	public final static int FORMAT_DS9 = 2;
	


	
	
	public abstract static class Representation {
	    private Grid2D<?> grid;
	    
	    protected Representation(Grid2D<?> grid) throws IncompatibleTypesException {
	        this.grid = grid;
	    }

	    public final Grid2D<?> getGrid() { return grid; }
	    
	    public abstract boolean isInside(double i, double j);
	    
	    public abstract IndexBounds2D getBounds();
	    
	    public Vector2D getIndex(Coordinate2D coords) throws IncompatibleTypesException {
	        Coordinate2D gridCoords = grid.getReference().copy();
            coords.convertTo(gridCoords);
            Vector2D v = new Vector2D();
            ((Grid2D) grid).getProjection().project(gridCoords, v);
            grid.toIndex(v);
            return v;
	    }
	    
	    public Coordinate2D getGridCoords(Vector2D index) {
	        Vector2D offset = new Vector2D();
            grid.indexToOffset(index, offset);
	        
            Coordinate2D gridCoords = grid.getReference().copy();
            ((Grid2D) grid).getProjection().deproject(offset, gridCoords);
            
            return gridCoords;
        }
         
	    
	    public Viewport2D getViewer() { 
	        return new Viewport2D(null, getBounds()) {       
	            @Override
                public boolean isValid(int i, int j) {
	                if(!super.isValid(i, j)) return false;
	                return isInside(i + fromi(), j + fromj());
	            }
	        };
	    }
	    
	    public Viewport2D getViewer(Value2D values) {
	        Viewport2D viewer = getViewer();
	        viewer.setBasis(values);
	        return viewer;	        
	    }
	    
	    public void flag(Flag2D flag, final long pattern) {
	        final Viewport2D viewer = getViewer(flag.getImage());
	        viewer.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    if(!viewer.isValid(i, j)) viewer.set(i, j, viewer.get(i, j).longValue() | pattern);
                }
	        }.process();
	    }
	    
	    public void unflag(Flag2D flag, final long pattern) {
            final Viewport2D viewer = getViewer(flag.getImage());
            final long clearPattern = ~pattern;
            viewer.new Fork<Void>() {
                @Override
                protected void process(int i, int j) {
                    if(!viewer.isValid(i, j)) viewer.set(i, j, viewer.get(i, j).longValue() & clearPattern);
                }
            }.process();   
        }
	        
	    
	    public final Validating2D getInsideValidator() { return insideValidator; }
	    
	    public final Validating2D getOutsideValidator() { return outsideValidator; }
	    
	    private Validating2D insideValidator = new Validating2D() {
	        @Override
	        public final boolean isValid(int i, int j) { return isInside(i, j); }

	        @Override
	        public void discard(int i, int j) {}
	    };
	     
	    private Validating2D outsideValidator = new Validating2D() {
            @Override
            public final boolean isValid(int i, int j) { return !isInside(i, j); }

            @Override
            public void discard(int i, int j) {}
        };
        
	    
	}
	
}

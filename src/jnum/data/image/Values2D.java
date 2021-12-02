/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.image;

import jnum.PointOp;
import jnum.data.index.Index2D;
import jnum.data.index.IndexedValues;
import jnum.math.IntRange;
import jnum.math.Vector2D;

public interface Values2D extends IndexedValues<Index2D, Number>, Validating2D {
    
    public int sizeX();
    
    public int sizeY();
  
    public Number get(int i, int j);   
 
    public void add(int i, int j, Number value);
       
    public void set(int i, int j, Number value);
    
    public double valueAtIndex(double ic, double jc);
 
    @Override
    public default Number get(Index2D index) {
        return get(index.i(), index.j());
    }
 
    @Override
    public default void add(Index2D index, Number value) {
        add(index.i(), index.j(), value);
    }
    
    @Override
    public default void set(Index2D index, Number value) {
        set(index.i(), index.j(), value);
    }
    
    @Override
    public default void clear(Index2D index) { clear(index.i(), index.j()); }
    
    public default void clear(int i, int j) { set(i, j, 0); }
    
    public default void scale(int i, int j, double factor) {
        set(i, j, get(i, j).doubleValue() * factor);
    }

    @Override
    public default void scale(Index2D index, double factor) { scale(index.i(), index.j(), factor); }
    
    public default double valueAtIndex(Vector2D v) {
        return valueAtIndex(v.x(), v.y());
    }
    
    @Override
    public default Index2D getSize() {
        return new Index2D(sizeX(), sizeY());
    }
    
    @Override
    public default int getSize(int i) {
        switch(i) {
        case 0: return sizeX();
        case 1: return sizeY();
        default: throw new IllegalArgumentException("there is no dimension " + i);
        }
    }
    
    @Override
    public default boolean conformsTo(Index2D size) {
        return conformsTo(size.i(), size.j());
    }
    
    public default boolean conformsTo(int sizeX, int sizeY) {
        if(sizeX() != sizeX) return false;
        if(sizeY() != sizeY) return false;
        return true;
    }

    
    @Override
    public default int dimension() { return 2; }
    
    @Override
    public default int capacity() {
        return sizeX() * sizeY();
    }
    
    @Override
    public default boolean containsIndex(Index2D index) {
        return containsIndex(index.i(), index.j());        
    }

    public default boolean containsIndex(final int i, final int j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= sizeX()) return false;
        if(j >= sizeY()) return false;
        return true;
    }
    
    @Override
    public default Index2D getIndexInstance() { return new Index2D(); }
    

    public default IntRange getXIndexRange() {
        int min = sizeX(), max = -1;
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) if(isValid(i, j)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public default IntRange getYIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) if(isValid(i, j)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }
    
    @Override
    public default <ReturnType> ReturnType loop(final PointOp<Index2D, ReturnType> op, Index2D from, Index2D to) {
        final Index2D index = new Index2D();
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) {
                index.set(i, j);
                op.process(index);
                if(op.exception != null) return null;
            }
        }
        return op.getResult();
    }
    
    
}

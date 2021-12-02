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

package jnum.data.cube;

import jnum.PointOp;
import jnum.data.index.Index3D;
import jnum.data.index.IndexedValues;
import jnum.math.IntRange;
import jnum.math.Vector3D;

public interface Values3D extends IndexedValues<Index3D, Number>, Validating3D {
   
    public int sizeX();
    
    public int sizeY();
    
    public int sizeZ();
    
    public Number get(int i, int j, int k);
    
    public void add(int i, int j, int k, Number value);
    
    public void set(int i, int j, int k, Number value);
    
    public double valueAtIndex(double i, double j, double k);
   
    @Override
    public default Number get(Index3D index) {
        return get(index.i(), index.j(), index.k());
    }
 
    @Override
    public default void add(Index3D index, Number value) {
        add(index.i(), index.j(), index.k(), value);
    }
    

    @Override
    public default void set(Index3D index, Number value) {
        set(index.i(), index.j(), index.k(), value);
    }
    
    @Override
    public default void clear(Index3D index) { clear(index.i(), index.j(), index.k()); }
    
    public default void clear(int i, int j, int k) { set(i, j, k, 0); }    

    @Override
    public default void scale(Index3D index, double factor) { scale(index.i(), index.j(), index.k(), factor); }
    
    public default void scale(int i, int j, int k, double factor) {
        set(i, j, k, get(i, j, k).doubleValue() * factor);
    }
    
    public default double valueAtIndex(Vector3D v) {
        return valueAtIndex(v.x(), v.y(), v.z());
    }
    
    @Override
    public default Index3D getSize() {
        return new Index3D(sizeX(), sizeY(), sizeZ());
    }
    
    @Override
    public default int getSize(int i) {
        switch(i) {
        case 0: return sizeX();
        case 1: return sizeY();
        case 2: return sizeZ();
        default: throw new IllegalArgumentException("there is no dimension " + i);
        }
    }
    
    @Override
    public default boolean conformsTo(Index3D index) {
        return conformsTo(index.i(), index.j(), index.k());
    }
    
    public default boolean conformsTo(int sizeX, int sizeY, int sizeZ) {
        if(sizeX() != sizeX) return false;
        if(sizeY() != sizeY) return false;
        if(sizeZ() != sizeZ) return false;
        return true;
    }

    
    @Override
    public default int dimension() { return 3; }

    @Override
    public default int capacity() {  
        return sizeX() * sizeY() * sizeZ(); 
    }
    
    @Override
    public default boolean containsIndex(Index3D index) {
        return containsIndex(index.i(), index.j(), index.k());        
    }

    public default boolean containsIndex(final int i, final int j, final int k) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(k < 0) return false;
        if(i >= sizeX()) return false;
        if(j >= sizeY()) return false;
        if(k >= sizeZ()) return false;
        return true;
    }
    
    
    @Override
    public default Index3D getIndexInstance() { return new Index3D(); }
    
    public default IntRange getXIndexRange() {
        int min = sizeX(), max = -1;
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public default IntRange getYIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }

    public default IntRange getZIndexRange() {
        int min = sizeY(), max = -1;
        for(int j=sizeY(); --j >= 0; ) for(int i=sizeX(); --i >= 0; ) for(int k=sizeZ(); --k >= 0; ) if(isValid(i, j, k)) {
            if(j < min) min = j;
            if(j > max) max = j;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }
    
    @Override
    public default <ReturnType> ReturnType loop(final PointOp<Index3D, ReturnType> op, Index3D from, Index3D to) {
        final Index3D index = new Index3D();
        for(int i=to.i(); --i >= from.i(); ) {
            for(int j=to.j(); --j >= from.j(); ) {
                for(int k=to.k(); --k >= from.k(); ) {
                    index.set(i, j, k);
                    op.process(index);
                    if(op.exception != null) return null;
                }
            }
        }
   
        return op.getResult();
    }
 
}

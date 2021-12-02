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

package jnum.data.samples;

import jnum.PointOp;
import jnum.data.index.Index1D;
import jnum.data.index.IndexedValues;
import jnum.math.IntRange;
import jnum.math.Position;


public interface Values1D extends IndexedValues<Index1D, Number>, Validating1D {
  
    public int size();
    
    public Number get(int i);
    
    public void add(int i, Number value);
    
    public void set(int i, Number value);
    
    public double valueAtIndex(double ic); 
    
    @Override
    public default Number get(Index1D index) {
        return get(index.i());
    }
 
    @Override
    public default void add(Index1D index, Number value) {
        add(index.i(), value);
    }
    

    @Override
    public default void set(Index1D index, Number value) {
        set(index.i(), value);
    }
    
    @Override
    public default void clear(Index1D index) { clear(index.i()); }
    
    public default void clear(int i) { set(i, 0); }
    
    
    @Override
    public default void scale(Index1D index, double factor) { scale(index.i(), factor); }
    
    public default void scale(int i, double factor) {
        set(i, get(i).doubleValue() * factor);
    }
    
    public default double valueAtIndex(Position p) {
        return valueAtIndex(p.value());
    }
    
    @Override
    public default Index1D getSize() {
        return new Index1D(size());
    }
    
    
    @Override
    public default int getSize(int i) {
        if(i != 0) throw new IllegalArgumentException("there is no dimension " + i);
        return size();
    }
    
    @Override
    public default boolean conformsTo(Index1D size) {
        return conformsTo(size.i());
    }
    
    public default boolean conformsTo(int size) {
        return size() == size;
    }
    
    
    @Override
    public default Index1D getIndexInstance() { return new Index1D(); }
    
    public default IntRange getIndexRange() {
        int min = size(), max = -1;
        for(int i=size(); --i >= 0; ) if(isValid(i)) {
            if(i < min) min = i;
            if(i > max) max = i;
            break;
        }
        return max > min ? new IntRange(min, max) : null;
    }
    
    @Override
    public default int capacity() { return size(); }
    
    @Override
    public default int dimension() { return 1; }
    
    @Override
    public default boolean containsIndex(Index1D index) {
        return containsIndex(index.i());
    }
    
    public default boolean containsIndex(int i) {
        if(i < 0) return false;
        if(i >= size()) return false;
        return true;
    }
    
    @Override  
    public default <ReturnType> ReturnType loop(final PointOp<Index1D, ReturnType> op, Index1D from, Index1D to) {
        final Index1D index = new Index1D();
        for(int i=to.i(); --i >= from.i(); ) {
            index.set(i);
            op.process(index);
            if(op.exception != null) return null;
        }
        return op.getResult();
    }   
}

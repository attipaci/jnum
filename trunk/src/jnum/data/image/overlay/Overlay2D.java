package jnum.data.image.overlay;

import jnum.Util;
import jnum.data.image.Data2D;
import jnum.data.image.Value2D;
import jnum.parallel.Parallelizable;

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

public class Overlay2D extends Data2D {
    private Value2D values;
  
    public Overlay2D() {}
    
    public Overlay2D(Value2D values) {
        setBasis(values);
        if(values instanceof Parallelizable) copyParallel(((Parallelizable) values));
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ values.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Overlay2D)) return false;
        
        Overlay2D overlay = (Overlay2D) o;
        if(!Util.equals(values, overlay.values)) return false;
        
        return super.equals(o);
    }
   
   
    public Value2D getBasis() { return values; }
    
    public void setBasis(Value2D base) {
        this.values = base;
    }
    
    @Override
    public boolean isValid(int i, int j) {
        return values.isValid(i, j);
    }

    @Override
    public void discard(int i, int j) {
        values.discard(i, j);
    }
    
    @Override
    public void clear(int i, int j) {
        values.set(i, j, 0);
    }

    @Override
    public final Class<? extends Number> getElementType() {
        return values.getElementType();
    }
    
    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public int sizeX() {
        return values == null ? 0 : values.sizeX();
    }

    @Override
    public int sizeY() {
       return values == null ? 0 : values.sizeY();
    }

    @Override
    public Number get(int i, int j) {
        return values.get(i, j);
    }

    @Override
    public void set(int i, int j, Number value) {
        values.set(i, j, value);
    }
    
    @Override
    public void add(int i, int j, Number value) {
        values.add(i, j, value);
    }
    
    @Override
    public final Number getLowestCompareValue() {
        return values.getLowestCompareValue();
    }

    @Override
    public final Number getHighestCompareValue() {
        return values.getHighestCompareValue();
    }

    @Override
    public final int compare(Number a, Number b) {
        return values.compare(a, b);
    }

}

package jnum.data.samples.overlay;

import jnum.Util;
import jnum.data.Data;
import jnum.data.samples.Data1D;
import jnum.data.samples.Values1D;
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

public class Overlay1D extends Data1D {
    private Values1D values;
  
    public Overlay1D() {}
    
    public Overlay1D(Values1D values) {
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
        if(!(o instanceof Overlay1D)) return false;
        
        Overlay1D overlay = (Overlay1D) o;
        if(!Util.equals(values, overlay.values)) return false;
        
        return super.equals(o);
    }
   
   
    public Values1D getBasis() { return values; }
    
    public void setBasis(Values1D base) {
        this.values = base;
    }
    
    @Override
    public boolean isValid(int i) {
        return values.isValid(i);
    }

    @Override
    public void discard(int i) {
        values.discard(i);
    }
    
    @Override
    public void clear(int i) {
        values.set(i, 0);
    }

    @Override
    public final Class<? extends Number> getElementType() {
        return values.getElementType();
    }
    
    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public int size() {
        return values == null ? 0 : values.size();
    }

    @Override
    public Number get(int i) {
        return values.get(i);
    }

    @Override
    public void set(int i, Number value) {
        values.set(i, value);
    }
    
    @Override
    public void add(int i, Number value) {
        values.add(i, value);
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
   
    @Override
    public double valueAtIndex(double i) {
        return values.valueAtIndex(i);
    }
    
    @Override
    public Object getUnderlyingData() {
        if(values instanceof Data) return ((Data<?>) values).getUnderlyingData();
        return getSamples().getUnderlyingData();
    }
    
}

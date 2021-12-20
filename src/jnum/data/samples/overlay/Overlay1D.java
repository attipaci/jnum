package jnum.data.samples.overlay;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.Util;
import jnum.data.Data;
import jnum.data.Overlayed;
import jnum.data.index.Index1D;
import jnum.data.samples.Data1D;
import jnum.data.samples.Samples1D;
import jnum.data.samples.Values1D;
import jnum.parallel.Parallelizable;

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

public class Overlay1D extends Data1D implements Overlayed<Values1D> {
    private Values1D values;
    
    public Overlay1D(Values1D values) {
        setBasis(values);
        if(values instanceof Parallelizable) copyParallel(((Parallelizable) values));
    }
    
    @Override
    public Overlay1D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Overlay1D newInstance(Index1D size) {
        Data1D base = (values instanceof Data1D) ? ((Data1D) values).newInstance(size) 
                : Samples1D.createType(values.getElementType(), size.i());
        Overlay1D o = new Overlay1D(base);
        o.copyPoliciesFrom(this);
        return o;
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

    @Override
    public Overlay1D copy(boolean withContent) {
        Overlay1D copy = (Overlay1D) clone();
        if(values != null) {
            if(values instanceof CopiableContent) copy.values = (Values1D) ((CopiableContent<?>) values).copy(withContent);
            else if(withContent) {
                if(values instanceof Copiable) copy.values = (Values1D) ((Copiable<?>) values).copy();
                else copy.values = Samples1D.createType(values.getElementType(), values.size());
            }
            else copy.values = new Overlay1D(values).getSamples();
        }
        return copy;
    }
   
   
    @Override
    public Values1D getBasis() { return values; }
    
    @Override
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
    public final int compare(Number a, Number b) {
        return values.compare(a, b);
    }
   
    @Override
    public double valueAtIndex(double i) {
        return values.valueAtIndex(i);
    }
    
    @Override
    public Object getCore() {
        if(values instanceof Data) return ((Data<?>) values).getCore();
        return getSamples().getCore();
    }


}

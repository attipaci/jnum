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

package jnum.data.overlay;

import java.util.ArrayList;

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.Destructible;
import jnum.PointOp;
import jnum.Util;
import jnum.data.Data;
import jnum.data.DataCrawler;
import jnum.data.Overlayed;
import jnum.data.Validating;
import jnum.data.index.Index;
import jnum.data.index.IndexedValues;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.FitsException;


public class Overlay<IndexType extends Index<IndexType>> extends Data<IndexType> 
implements Overlayed<IndexedValues<IndexType, ?>>, Destructible {
    IndexedValues<IndexType, ?> base;
    
    public Overlay(IndexedValues<IndexType, ?> data) {
        setBasis(data);
        if(data != null) if(data instanceof Data) copyPoliciesFrom((Data<?>) data);
    }
    
    @Override
    public IndexedValues<IndexType, ?> getBasis() {
        return base;
    }
    
    @Override
    public void setBasis(IndexedValues<IndexType, ?> data) {
        this.base = data;
    }
    
    @Override
    public Overlay<IndexType> newInstance() {
        return newInstance(getSize());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Overlay<IndexType> newInstance(IndexType size) {
        Data<IndexType> data = base instanceof Data ? ((Data<IndexType>) base).newInstance(size) : newImage(size, getElementType());
        data.copyPoliciesFrom(base instanceof Data ? (Data<?>) base : this);
        
        Overlay<IndexType> copy = (Overlay<IndexType>) super.copy();
        copy.setBasis(data);
        return copy;
    }
    
    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        if(base != null) hash ^= base.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!getClass().isAssignableFrom(o.getClass())) return false;
        
        Overlay<?> ov = (Overlay<?>) o;
        if(!Util.equals(base, ov.base)) return false;
        
        return true;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public Overlay<IndexType> copy(boolean withContent) {
        Overlay<IndexType> copy = (Overlay<IndexType>) super.copy();
        IndexedValues<IndexType, ?> values = null;
        
        if(base != null) {
            if(base instanceof CopiableContent) values = (IndexedValues<IndexType, ?>) ((CopiableContent<?>) base).copy(withContent);
            else if(withContent) {
                if(base instanceof Copiable) values = (IndexedValues<IndexType, ?>) ((Copiable<?>) base).copy();
                else {
                    Data<IndexType> data = newImage(base.getSize(), base.getElementType());
                    data.copyOf(base, false);
                    values = data;
                }
            }
            
            if(values == null) {
                Data<IndexType> data = newImage(base.getSize(), base.getElementType());
                if(withContent) data.copyOf(base, false);
                values = data;
            }
        }
        
        copy.setBasis(values);   
        return copy;
    }
   
    
    @Override
    public boolean isValid(IndexType index) {
        return base.isValid(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void discard(IndexType index) {
        if(base instanceof Validating) ((Validating<IndexType>) base).discard(index);
    }
    
    @Override
    public void clear(IndexType index) {
        base.set(index, 0);
    }

    @Override
    public final Class<? extends Number> getElementType() {
        return base.getElementType();
    }
    
    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public IndexType getSize() {
        return base == null ? null : base.getSize();
    }

    @Override
    public Number get(IndexType i) {
        return base.get(i);
    }

    @Override
    public void set(IndexType i, Number value) {
        base.set(i, value);
    }
    
    @Override
    public void add(IndexType i, Number value) {
        base.add(i, value);
    }
    

    @Override
    public Number get(int... idx) {
        return base.get(idx);
    }

    @Override
    public Object getCore() {
        if(base instanceof Data) return ((Data<?>) base).getCore();
        
        Data<IndexType> im = newImage();
        im.copyOf(base, false);
        return im.getCore();
    }

    @Override
    public void destroy() {
        if(getBasis() instanceof Destructible) ((Destructible) getBasis()).destroy();
    }

    @Override
    public int capacity() {
        return base == null ? 0 : base.capacity();
    }

    @Override
    public int dimension() {
        return base.dimension();
    }

    @Override
    public int getSize(int i) throws IllegalArgumentException {
        return base.getSize(i);
    }

    @Override
    public IndexType getIndexInstance() {
        return base.getIndexInstance();
    }

    @Override
    public boolean containsIndex(IndexType index) {
        return base.containsIndex(index);
    }

    @Override
    public <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        return base.loop(op, from, to);
    }


    @Override
    public Number getValid(IndexType index, Number defaultValue) {
        if(base.isValid(index)) return base.get(index);
        return defaultValue;
    }

    @Override
    public String getInfo() {
        // TODO
    }




}

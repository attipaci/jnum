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

import jnum.Copiable;
import jnum.CopiableContent;
import jnum.Destructible;
import jnum.NonConformingException;
import jnum.PointOp;
import jnum.data.Data;
import jnum.data.Overlayed;
import jnum.data.Validating;
import jnum.data.index.Index;
import jnum.data.index.IndexedValues;


public interface OverlayedData<IndexType extends Index<IndexType>> extends Overlayed<IndexedValues<IndexType, ?>>, IndexedValues<IndexType, Number>,
    CopiableContent<OverlayedData<IndexType>>, Validating<IndexType>, Destructible {
    
    default OverlayedData<IndexType> newInstance() {
        return newInstance(getSize());
    }
    
    @SuppressWarnings("unchecked")
    default OverlayedData<IndexType> newInstance(IndexType size) {
        IndexedValues<IndexType, ?> base = getBasis();
        Data<IndexType> data = null;
        
        if(base instanceof Data) {
            Data<IndexType> baseData = (Data<IndexType>) base;
            data = baseData.newInstance(size);
            data.copyPoliciesFrom(baseData);
        }
        else data = newImage(size, getElementType());
        
        OverlayedData<IndexType> copy = copy();
        copy.setBasis(data);
        return copy;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    default OverlayedData<IndexType> copy(boolean withContent) {
        OverlayedData<IndexType> copy = copy();
        IndexedValues<IndexType, ?> base = getBasis();
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
    default boolean isValid(IndexType index) {
        return getBasis().isValid(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    default void discard(IndexType index) {
        if(getBasis() instanceof Validating) ((Validating<IndexType>) getBasis()).discard(index);
    }
    
    @Override
    default void clear(IndexType index) {
        getBasis().set(index, 0);
    }

    @Override
    default Class<? extends Number> getElementType() {
        return getBasis().getElementType();
    }
    
    /**
     * Safe even if underlying object is resized...
     */
    @Override
    default IndexType getSize() {
        return getBasis() == null ? null : getBasis().getSize();
    }

    @Override
    default Number get(IndexType i) {
        return getBasis().get(i);
    }

    @Override
    default void set(IndexType i, Number value) {
        getBasis().set(i, value);
    }
    
    @Override
    public default void add(IndexType i, Number value) {
        getBasis().add(i, value);
    }
    
    @Override
    default Number get(int... idx) throws NonConformingException {
        return getBasis().get(idx);
    }

    default Object getCore() {
        if(getBasis() instanceof Data) return ((Data<?>) getBasis()).getCore();
        
        Data<IndexType> im = newImage();
        im.copyOf(getBasis(), false);
        return im.getCore();
    }

    @Override
    default void destroy() {
        if(getBasis() instanceof Destructible) ((Destructible) getBasis()).destroy();
    }

    @Override
    default int capacity() {
        return getBasis() == null ? 0 : getBasis().capacity();
    }

    @Override
    default int dimension() {
        return getBasis().dimension();
    }

    @Override
    default int getSize(int i) throws IllegalArgumentException {
        return getBasis().getSize(i);
    }

    @Override
    default IndexType getIndexInstance() {
        return getBasis().getIndexInstance();
    }

    @Override
    default boolean containsIndex(IndexType index) {
        return getBasis().containsIndex(index);
    }

    @Override
    default <ReturnType> ReturnType loop(PointOp<IndexType, ReturnType> op, IndexType from, IndexType to) {
        return getBasis().loop(op, from, to);
    }


    default Number getValid(IndexType index, Number defaultValue) {
        if(getBasis().isValid(index)) return getBasis().get(index);
        return defaultValue;
    }


}

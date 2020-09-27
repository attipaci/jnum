/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.Util;
import jnum.parallel.ParallelObject;

public abstract class FlagCompanion<IndexType extends Index<IndexType>> extends ParallelObject implements Cloneable, Serializable,
CopiableContent<FlagCompanion<? extends IndexType>> {
    /**
     * 
     */
    private static final long serialVersionUID = -3515015800957215451L;
    
    int type;
    
    public FlagCompanion(int type) {
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ type ^ getData().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FlagCompanion)) return false;
         
        FlagCompanion<?> f = (FlagCompanion<?>) o;
        if(type != f.type) return false;
        if(!Util.equals(getData(), f.getData())) return false;
        
        return true;
    }
    
    public final IndexType size() { return getData().getSize(); }
    
    public final int capacity() { return getData().capacity(); }

    public final boolean conformsTo(IndexType size) {
        return getData().conformsTo(size);
    }
    
    public final boolean conformsTo(IndexedValues<IndexType> data) {
        return conformsTo(data.getSize());
    }
    
    public final boolean conformsTo(FlagCompanion<IndexType> flags) {
        return conformsTo(flags.getData());
    }
  
    
    public void clear() {
        fill(0L);
    }
    

    public void fill(long pattern) {
        getData().fill(pattern);
    }

    
    public abstract Data<IndexType> getData();
    
    public final Class<? extends Number> getElementType() { return getData().getElementType(); }
    
       
    public final long get(IndexType index) { return getData().get(index).longValue(); }
    
    public final void set(IndexType index, long value) { getData().set(index, value); }
    
    public final void setBits(IndexType index, long pattern) { 
        getData().set(index, getData().get(index).longValue() | pattern); 
    }
    
    public final void clearBits(IndexType index, long pattern) { getData().set(index, 0L); }
    
    public final boolean isClear(IndexType index, long pattern) {
        return (getData().get(index).longValue() & pattern) == 0L;
    }
    
    public final void clear(IndexType index) { getData().set(index, 0L); }
    
    public final boolean isClear(IndexType index) { return getData().get(index).longValue() == 0L; }
    
    
    public final static int TYPE_BYTE = 0;
    public final static int TYPE_SHORT = 1;
    public final static int TYPE_INT = 2;
    public final static int TYPE_LONG = 3;
    
}

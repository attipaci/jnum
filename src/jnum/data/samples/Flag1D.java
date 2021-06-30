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

package jnum.data.samples;

import jnum.data.FlagCompanion;

public class Flag1D extends FlagCompanion<Index1D> implements Resizable1D {   
    /**
     * 
     */
    private static final long serialVersionUID = 3594904169932205060L;
    private Samples1D data;
    

    public Flag1D(int type) {
        super(type);
        switch(type) {
        case TYPE_BYTE: data = Samples1D.createType(Byte.class); break;
        case TYPE_SHORT: data = Samples1D.createType(Short.class); break;
        case TYPE_INT: data = Samples1D.createType(Integer.class); break;
        case TYPE_LONG: data = Samples1D.createType(Long.class); break;
        default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
    
    public Flag1D(int type, int size) {
        this(type);
        setSize(size);
    }

  
    @Override
    public Flag1D copy(boolean withContent) {
        Flag1D copy = (Flag1D) clone();
        copy.data = data.copy(withContent);
        return copy;
    }

    @Override
    public Flag1D copy() { return copy(true); }
    
    
   
    
    public void destroy() { data.destroy(); }
    
  
    @Override
    public final Samples1D getData() { return data; }
    
    
    public final void setSize(Index1D size) {
        setSize(size.i());
    }
    
    @Override
    public void setSize(int size) {
        data.setSize(size);
    }
    


    public final long get(int i) { return data.get(i).longValue(); }
   
    public final void set(int i, long value) { data.set(i, value); }

    public final void setBits(int i, long pattern) {
        data.set(i, data.get(i).longValue() | pattern);
    }

    public final void clearBits(int i, long pattern) {
        data.set(i, 0L);
    }
    
    public final boolean isClear(int i, long pattern) {
        return (data.get(i).longValue() & pattern) == 0L;
    }
  
    public final void clear(int i) {
        data.set(i, 0L);
    }

    
    public final boolean isClear(int i) {
        return data.get(i).longValue() == 0L;
    }

        


}

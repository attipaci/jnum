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

package jnum.data.image;

import java.io.Serializable;

import jnum.CopiableContent;
import jnum.ExtraMath;
import jnum.math.Coordinate2D;
import jnum.parallel.ParallelObject;


public class Flag2D extends ParallelObject implements Resizable2D, Cloneable, CopiableContent<Flag2D>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4967521177601467424L;

    int type;
    private Image2D data;


    public Flag2D(int type) {
        switch(type) {
        case TYPE_BYTE: data = Image2D.createType(Byte.class); break;
        case TYPE_SHORT: data = Image2D.createType(Short.class); break;
        case TYPE_INT: data = Image2D.createType(Integer.class); break;
        case TYPE_LONG: data = Image2D.createType(Long.class); break;
        default: throw new IllegalArgumentException("Unknown type: " + type);
        }
        this.type = type;
    }
    
    public Flag2D(int type, int sizeX, int sizeY) {
        this(type);
        setSize(sizeX, sizeY);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ type ^ data.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Flag2D)) return false;
         
        Flag2D f = (Flag2D) o;
        if(type != f.type) return false;
        if(!data.equals(f.data)) return false;
        
        for(int i=sizeX(); --i >= 0; ) for(int j=sizeY(); --j >= 0; )
            if(get(i,j) != f.get(i, j)) return false;
        
        return true;
    }
    
  
    @Override
    public Flag2D copy(boolean withContent) {
        Flag2D copy = (Flag2D) clone();
        copy.data = data.copy(withContent);
        return copy;
    }

    @Override
    public Flag2D copy() { return copy(true); }
  
    public Image2D getImage() { return data; }
    
    public final Class<? extends Number> getElementType() { return data.getElementType(); }

    @Override
    public void setSize(int sizeX, int sizeY) {
        data.setSize(sizeX, sizeY);
    }
    
    
    public void destroy() { data.destroy(); }

    public final int sizeX() { return data.sizeX(); }

    public final int sizeY() { return data.sizeY(); }

    public final long get(int i, int j) { return data.get(i, j).longValue(); }

    public void set(int i, int j, long value) { data.set(i, j, value); }

    public void setBits(int i, int j, long pattern) {
        data.set(i, j, data.get(i, j).longValue() | pattern);
    }

    public void clearBits(int i, int j, long pattern) {
        data.set(i, j, 0L);
    }

    public boolean isClear(int i, int j, long pattern) {
        return (data.get(i, j).longValue() & pattern) == 0L;
    }

    public void clear(int i, int j) {
        data.set(i, j, 0L);
    }

    public boolean isClear(int i, int j) {
        return data.get(i, j).longValue() == 0L;
    }


    public void clear() {
        fill(0L);
    }

    public void fill(long pattern) {
        data.fill(pattern);
    }

    public void crop(int imin, int jmin, int imax, int jmax) {
        data.crop(imin, jmin, imax, jmax);
    }

    public final static int TYPE_BYTE = 0;
    public final static int TYPE_SHORT = 1;
    public final static int TYPE_INT = 2;
    public final static int TYPE_LONG = 3;


    public void grow(final int pattern, final Coordinate2D indexRadius) {   
        data.new Fork<Void>() {
            @Override
            protected void process(int i, int j) {
                if(!isClear(i, j, pattern)) return;
                
                final int fromi1 = Math.max(0, (int) Math.floor(i - indexRadius.x()));
                final int fromj1 = Math.max(0, (int) Math.floor(j - indexRadius.y()));
                final int toi1 = Math.max(sizeX(), (int) Math.ceil(i + indexRadius.x()));
                final int toj1 = Math.max(sizeY(), (int) Math.ceil(j + indexRadius.y()));
                    
                // TODO for sheared grids...
                for(int i1 = toi1; --i1 >= fromi1; ) for(int j1 = toj1; --j1 >= fromj1; ) 
                    if(ExtraMath.hypot((i-i1) / indexRadius.x(), (j-j1) / indexRadius.y()) <= 1) 
                        setBits(i1, j1, pattern);
            }
        }.process();
    }
    


}

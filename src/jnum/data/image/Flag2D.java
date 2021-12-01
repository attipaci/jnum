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

package jnum.data.image;


import jnum.ExtraMath;
import jnum.data.FlagCompanion;
import jnum.data.Resizable;
import jnum.data.index.Index2D;
import jnum.math.Coordinate2D;
import jnum.parallel.ParallelPointOp;



public class Flag2D extends FlagCompanion<Index2D> implements Resizable<Index2D> {

    /**
     * 
     */
    private static final long serialVersionUID = 4967521177601467424L;

    private Image2D data;
    private Type type;


    public Flag2D(Type type) {
        super(type);
        switch(type) {
        case BYTE: data = Image2D.createType(Byte.class); break;
        case SHORT: data = Image2D.createType(Short.class); break;
        case INT: data = Image2D.createType(Integer.class); break;
        case LONG: data = Image2D.createType(Long.class); break;
        default: throw new IllegalArgumentException("Unknown type: " + type);
        }
        this.type = type;
    }
    
    
    public Flag2D(Type type, int sizeX, int sizeY) {
        this(type);
        setSize(sizeX, sizeY);
    }

 
    @Override
    public Flag2D copy(boolean withContent) {
        Flag2D copy = (Flag2D) clone();
        copy.data = data.copy(withContent);
        return copy;
    }

    @Override
    public Image2D getData() { return data; }
      
    public final Type getType() { return type; }
    
    @Override
    public final void setSize(Index2D size) {
        setSize(size.i(), size.j());
    }
    
    public void setSize(int sizeX, int sizeY) {
        data.setSize(sizeX, sizeY);
    }
    
    
    public void destroy() { data.destroy(); }

    public final int sizeX() { return data.sizeX(); }

    public final int sizeY() { return data.sizeY(); }
   
  
    public final long get(int i, int j) { return data.get(i, j).longValue(); }
   
    public final void set(int i, int j, long value) { data.set(i, j, value); }

    public final void setBits(int i, int j, long pattern) {
        data.set(i, j, data.get(i, j).longValue() | pattern);
    }

    public final void clearBits(int i, int j, long pattern) {
        data.set(i, j, 0L);
    }
    
    public final boolean isClear(int i, int j, long pattern) {
        return (data.get(i, j).longValue() & pattern) == 0L;
    }
  
    public final void clear(int i, int j) {
        data.set(i, j, 0L);
    }

    
    public final boolean isClear(int i, int j) {
        return data.get(i, j).longValue() == 0L;
    }



    public void crop(Index2D from, Index2D to) {
        data.crop(from, to);
    }

 
    public void grow(final int pattern, final Coordinate2D indexRadius) { 
       
        data.smartFork(new ParallelPointOp.Simple<Index2D>() {
            @Override
            public void process(Index2D point) {
                if(isClear(point, pattern)) return;
                
                final int fromi1 = Math.max(0, (int) Math.floor(point.i() - indexRadius.x()));
                final int fromj1 = Math.max(0, (int) Math.floor(point.j() - indexRadius.y()));
                final int toi1 = Math.max(sizeX(), (int) Math.ceil(point.i() + indexRadius.x()));
                final int toj1 = Math.max(sizeY(), (int) Math.ceil(point.j() + indexRadius.y()));
                    
                // TODO for sheared grids...
                for(int i1 = toi1; --i1 >= fromi1; ) for(int j1 = toj1; --j1 >= fromj1; ) 
                    if(ExtraMath.hypot((point.i()-i1) / indexRadius.x(), (point.j()-j1) / indexRadius.y()) <= 1) 
                        setBits(i1, j1, pattern);
            }
        });
    }
    


}

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

package jnum.data.image.overlay;

import jnum.data.Resizable;
import jnum.data.image.IndexBounds2D;
import jnum.data.image.Values2D;
import jnum.data.index.Index2D;

public class Viewport2D extends Overlay2D implements Resizable<Index2D> { 
    private int i0;
    private int j0;
    
    private int sizeX;
    private int sizeY;
    
    public Viewport2D() {
        this(null);
    }
    
    public Viewport2D(Values2D base) {
        this(base, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public Viewport2D(Values2D base, IndexBounds2D bounds) {
        this(base, bounds.fromi, bounds.fromj, bounds.toi, bounds.toj);
    }
    
    public Viewport2D(Values2D base, Index2D from, Index2D to) {
        this(base, from.i(), from.j(), to.i(), to.j());
    }
   
    public Viewport2D(Values2D base, int fromi, int fromj, int toi, int toj) {
        setBasis(base);
        setBounds(fromi, fromj, toi, toj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ i0 ^ j0 ^ sizeX ^ sizeY;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Viewport2D)) return false;
        
        Viewport2D v = (Viewport2D) o;
        if(sizeX != v.sizeX) return false;
        if(sizeY != v.sizeY) return false;
        if(i0 != v.i0) return false;
        if(j0 != v.j0) return false;
        
        return super.equals(o);
    }
    
    
    public final int fromi() { return i0; }
    
    public final int fromj() { return j0; }
 
    
    public void setBounds(IndexBounds2D bounds) {
        setBounds(bounds.fromi, bounds.fromj, bounds.toi, bounds.toj);
    }
    
    public void setBounds(Index2D from, Index2D to) {
        setBounds(from.i(), from.j(), to.i(), to.j());
    }
    
    public void setBounds(int fromi, int fromj, int toi, int toj) {
         
        i0 = Math.max(0, fromi);
        j0 = Math.max(0, fromj);
         
        setSize(toi - i0, toj - j0);
    }
    
 
    
    public void move(int di, int dj) {
        i0 += di;
        j0 += dj;
    }
    
    @Override
    public final void setSize(Index2D size) {
        setSize(size.i(), size.j());
    }
    
    public void setSize(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }
    
    
    
    @Override
    public boolean isValid(int i, int j) {
        return super.isValid(i + i0, j + j0);
    }

    @Override
    public void discard(int i, int j) {
        super.discard(i + i0, j + j0);
    }


    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public final int sizeX() {
        return Math.max(0, Math.min(sizeX, super.sizeX() - i0));
    }

    @Override
    public final int sizeY() {
       return Math.max(0, Math.min(sizeY, super.sizeY() - j0));
    }

    @Override
    public final Number get(int i, int j) {
        return super.get(i + i0, j + j0);
    }

    @Override
    public final void set(int i, int j, Number value) {
        super.set(i + i0, j + j0, value);
    }
    
    @Override
    public final void add(int i, int j, Number value) {
        super.add(i + i0, j + j0, value);
    }

   
    
 
}


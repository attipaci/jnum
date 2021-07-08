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

package jnum.data.cube.overlay;

import jnum.data.cube.IndexBounds3D;
import jnum.data.cube.Resizable3D;
import jnum.data.cube.Values3D;
import jnum.data.index.Index3D;

public class Viewport3D extends Overlay3D implements Resizable3D { 
    private int i0;
    private int j0;
    private int k0;
    
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    
    public Viewport3D() {
        this(null);
    }
    
    public Viewport3D(Values3D base) {
        this(base, 0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public Viewport3D(Values3D base, IndexBounds3D bounds) {
        this(base, bounds.fromi, bounds.fromj, bounds.fromk, bounds.toi, bounds.toj, bounds.tok);
    }
    
    public Viewport3D(Values3D base, Index3D from, Index3D to) {
        this(base, from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }
   
    public Viewport3D(Values3D base, int fromi, int fromj, int fromk, int toi, int toj, int tok) {
        setBasis(base);
        setBounds(fromi, fromj, fromk, toi, toj, tok);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ i0 ^ j0 ^ k0 ^ sizeX ^ sizeY ^ sizeZ;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Viewport3D)) return false;
        
        Viewport3D v = (Viewport3D) o;
        if(sizeX != v.sizeX) return false;
        if(sizeY != v.sizeY) return false;
        if(sizeZ != v.sizeZ) return false;
        if(i0 != v.i0) return false;
        if(j0 != v.j0) return false;
        if(k0 != v.k0) return false;
        
        return super.equals(o);
    }
    
    
    public final int fromi() { return i0; }
    
    public final int fromj() { return j0; }
 
    public final int fromk() { return k0; }
    
    public void setBounds(IndexBounds3D bounds) {
        setBounds(bounds.fromi, bounds.fromj, bounds.fromk, bounds.toi, bounds.toj, bounds.tok);
    }
    
    public void setBounds(Index3D from, Index3D to) {
        setBounds(from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }
    
    public void setBounds(int fromi, int fromj, int fromk, int toi, int toj, int tok) {
         
        i0 = Math.max(0, fromi);
        j0 = Math.max(0, fromj);
        k0 = Math.max(0, fromk);
         
        setSize(toi - i0, toj - j0, tok - k0);
    }
    
 
    
    public void move(int di, int dj, int dk) {
        i0 += di;
        j0 += dj;
        k0 += dk;
    }
    
    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }
    
    
    
    @Override
    public boolean isValid(int i, int j, int k) {
        return super.isValid(i + i0, j + j0, k + k0);
    }

    @Override
    public void discard(int i, int j, int k) {
        super.discard(i + i0, j + j0, k + k0);
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
    public final Number get(int i, int j, int k) {
        return super.get(i + i0, j + j0, k + k0);
    }

    @Override
    public final void set(int i, int j, int k, Number value) {
        super.set(i + i0, j + j0, k + k0, value);
    }
    
    @Override
    public final void add(int i, int j, int k, Number value) {
        super.add(i + i0, j + j0, k + k0, value);
    }

    @Override
    public final double valueAtIndex(double i, double j, double k) {
        return super.valueAtIndex(i + i0, j + j0, k + k0);
    }
   
    
  

    
 
}


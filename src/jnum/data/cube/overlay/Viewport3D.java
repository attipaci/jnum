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

import jnum.Util;
import jnum.data.Data;
import jnum.data.Windowed;
import jnum.data.cube.IndexBounds3D;
import jnum.data.cube.Values3D;
import jnum.data.index.Index3D;

public class Viewport3D extends Overlay3D implements Windowed<Index3D> { 
    private Index3D origin;
    private Index3D size;
    
    public Viewport3D(Values3D base) {
        this(base, 0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public Viewport3D(Values3D base, IndexBounds3D bounds) {
        this(base, bounds.fromi, bounds.fromj, bounds.fromk, bounds.toi, bounds.toj, bounds.tok);
    }
    
    public Viewport3D(Values3D base, int fromi, int fromj, int fromk, int toi, int toj, int tok) {
        super(base);
        setBounds(new Index3D(fromi, fromj, fromk), new Index3D(toi, toj, tok));
    }
    
    public Viewport3D(Values3D base, Index3D from, Index3D to) {
        super(base);
        setBounds(from, to);
    }


    @Override
    public Viewport3D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Viewport3D newInstance(Index3D size) {
        Viewport3D r = (Viewport3D) super.newInstance();
        r.origin = origin.copy();
        r.size = size.copy();
        return r;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof Viewport3D) {
            Viewport3D view = (Viewport3D) other;
            origin = view.origin.copy();
            size = view.size.copy();
        }
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ origin.hashCode() ^ size.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Viewport3D)) return false;
        
        Viewport3D v = (Viewport3D) o;
        if(!Util.equals(origin, v.origin)) return false;
        if(!Util.equals(size, v.size)) return false;
        
        return super.equals(o);
    }
    
    @Override
    public final Index3D getOrigin() {
        return origin;
    }
    
    @Override
    public final Index3D getSize() {
        return size;
    }
    
    public void setBounds(IndexBounds3D bounds) {
        setBounds(bounds.fromi, bounds.fromj, bounds.fromk, bounds.toi, bounds.toj, bounds.tok);
    }
    
    @Override
    public void setBounds(Index3D from, Index3D to) {
        setBounds(from.i(), from.j(), from.k(), to.i(), to.j(), to.k());
    }
    
    public void setBounds(int fromi, int fromj, int fromk, int toi, int toj, int tok) {
        origin.set(Math.max(0, fromi), Math.max(0, fromj), Math.max(0, fromk));
        size.set(toi - origin.i(), toj - origin.j(), tok - origin.k());
    }
    
    @Override
    public void move(Index3D delta) {
        move(delta.i(), delta.j(), delta.k());
    }
    
    public void move(int di, int dj, int dk) {
        origin.set(origin.i() + di, origin.j() + dj, origin.k() + dk);
    }

    
    @Override
    public boolean isValid(int i, int j, int k) {
        return super.isValid(i + origin.i(), j + origin.j(), k + origin.k());
    }

    @Override
    public void discard(int i, int j, int k) {
        super.discard(i + origin.i(), j + origin.j(), k + origin.k());
    }


    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public final int sizeX() {
        return Math.max(0, Math.min(size.i(), super.sizeX() - origin.i()));
    }

    @Override
    public final int sizeY() {
       return Math.max(0, Math.min(size.j(), super.sizeY() - origin.j()));
    }

    @Override
    public final int sizeZ() {
       return Math.max(0, Math.min(size.k(), super.sizeZ() - origin.k()));
    }
    
    @Override
    public final Number get(int i, int j, int k) {
        return super.get(i + origin.i(), j + origin.j(), k + origin.k());
    }

    @Override
    public final void set(int i, int j, int k, Number value) {
        super.set(i + origin.i(), j + origin.j(), k + origin.k(), value);
    }
    
    @Override
    public final void add(int i, int j, int k, Number value) {
        super.add(i + origin.i(), j + origin.j(), k + origin.k(), value);
    }

    @Override
    public final double valueAtIndex(double i, double j, double k) {
        return super.valueAtIndex(i + origin.i(), j + origin.j(), k + origin.k());
    }

   
    
  

    
 
}


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

import jnum.Util;
import jnum.data.Data;
import jnum.data.Windowed;
import jnum.data.image.IndexBounds2D;
import jnum.data.image.Values2D;
import jnum.data.index.Index2D;

public class Viewport2D extends Overlay2D implements Windowed<Index2D> { 
    private Index2D origin;
    private Index2D size;
    
    public Viewport2D() {
        this(null);
    }
    
    public Viewport2D(Values2D base) {
        this(base, new Index2D(), new Index2D(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    public Viewport2D(Values2D base, IndexBounds2D bounds) {
        this(base, new Index2D(bounds.fromi, bounds.fromj), new Index2D(bounds.toi, bounds.toj));
    }

    public Viewport2D(Values2D base, Index2D from, Index2D to) {
        super(base);
        origin = new Index2D();
        size = new Index2D();
        setBounds(from, to);
    }


    @Override
    public Viewport2D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Viewport2D newInstance(Index2D size) {
        Viewport2D r = (Viewport2D) super.newInstance();
        r.origin = origin.copy();
        r.size = size.copy();
        return r;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof Viewport2D) {
            Viewport2D view = (Viewport2D) other;
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
        if(!(o instanceof Viewport2D)) return false;
        
        Viewport2D v = (Viewport2D) o;
        if(!Util.equals(origin, v.origin)) return false;
        if(!Util.equals(size, v.size)) return false;
        
        return super.equals(o);
    }
    
    @Override
    public final Index2D getOrigin() {
        return origin;
    }
 
    @Override
    public final Index2D getSize() {
        return new Index2D(sizeX(), sizeY());
    }
    
    public void setBounds(IndexBounds2D bounds) {
        setBounds(bounds.fromi, bounds.fromj, bounds.toi, bounds.toj);
    }
    
    @Override
    public void setBounds(Index2D from, Index2D to) {
        setBounds(from.i(), from.j(), to.i(), to.j());
    }
    
    public void setBounds(int fromi, int fromj, int toi, int toj) {
        origin.set(Math.max(0, fromi), Math.max(0, fromj));
        size.set(toi - origin.i(), toj - origin.j());
    }
    
    @Override
    public void move(Index2D delta) {
        move(delta.i(), delta.j());
    }
    
    public void move(int di, int dj) {
        origin.set(origin.i() + di, origin.j() + dj);
    }

    @Override
    public boolean isValid(int i, int j) {
        return super.isValid(i + origin.i(), j + origin.j());
    }

    @Override
    public void discard(int i, int j) {
        super.discard(i + origin.i(), j + origin.j());
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
    public final Number get(int i, int j) {
        return super.get(i + origin.i(), j + origin.j());
    }

    @Override
    public final void set(int i, int j, Number value) {
        super.set(i + origin.i(), j + origin.j(), value);
    }
    
    @Override
    public final void add(int i, int j, Number value) {
        super.add(i + origin.i(), j + origin.j(), value);
    }

}


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

package jnum.data.samples.overlay;

import jnum.Util;
import jnum.data.Data;
import jnum.data.Windowed;
import jnum.data.index.Index1D;
import jnum.data.samples.Values1D;

public class Viewport1D extends Overlay1D implements Windowed<Index1D> { 
    private Index1D origin;
    private Index1D size;

    
    public Viewport1D() {
        this(null);
    }
    
    public Viewport1D(Values1D base) {
        this(base, 0, Integer.MAX_VALUE);
    }
 
   
    public Viewport1D(Values1D base, int from, int to) {
        super(base);
        origin = new Index1D();
        size = new Index1D();
        setBounds(from, to);
    }
    
    @Override
    public Viewport1D newInstance() {
        return newInstance(getSize());
    }
    
    @Override
    public Viewport1D newInstance(Index1D size) {
        Viewport1D r = (Viewport1D) super.newInstance();
        if(origin != null) r.origin = origin.copy();
        if(size != null) r.size = size.copy();
        return r;
    }
    
    @Override
    public void copyPoliciesFrom(Data<?> other) {
        super.copyPoliciesFrom(other);
        if(other instanceof Viewport1D) {
            Viewport1D view = (Viewport1D) other;
            origin = view.origin;
            size = view.size;
        }
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ origin.hashCode() ^ size.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Viewport1D)) return false;
        
        Viewport1D v = (Viewport1D) o;
        if(!Util.equals(origin, v.origin)) return false;
        if(!Util.equals(size, v.size)) return false;
        
        return super.equals(o);
    }
    
    @Override
    public final Index1D getOrigin() { return origin; }
    
    @Override
    public final Index1D getSize() { return new Index1D(size()); }
 
    @Override
    public void setBounds(Index1D from, Index1D to) {
        setBounds(from.i(), to.i());
    }
        
    public void setBounds(int from, int to) {
        origin.set(Math.max(0, from));
        size.set(to - origin.i());
    }
 
    @Override
    public final void move(Index1D delta) {
        move(delta.i());
    }
    
    public void move(int di) {
        origin.set(origin.i() + di);
    }
   
    @Override
    public boolean isValid(int i) {
        return super.isValid(i + origin.i());
    }

    @Override
    public void discard(int i) {
        super.discard(i + origin.i());
    }

    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public final int size() {
        return Math.max(0, Math.min(size.i(), super.size() - origin.i()));
    }

    @Override
    public final Number get(int i) {
        return super.get(i + origin.i());
    }

    @Override
    public final void set(int i, Number value) {
        super.set(i + origin.i(), value);
    }
    
    @Override
    public final void add(int i, Number value) {
        super.add(i + origin.i(), value);
    }
    
}


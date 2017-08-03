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

package jnum.data.samples.overlay;

import jnum.data.samples.Resizable1D;
import jnum.data.samples.Values1D;

public class Viewport1D extends Overlay1D implements Resizable1D { 
    private int i0;
    private int size;

    
    public Viewport1D() {
        this(null);
    }
    
    public Viewport1D(Values1D base) {
        this(base, 0, Integer.MAX_VALUE);
    }
 
   
    public Viewport1D(Values1D base, int from, int to) {
        setBasis(base);
        setBounds(from, to);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ i0 ^ size;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Viewport1D)) return false;
        
        Viewport1D v = (Viewport1D) o;
        if(size != v.size) return false;
        if(i0 != v.i0) return false;
        
        return super.equals(o);
    }
    
    
    public final int from() { return i0; }
     
 
    public void setBounds(int from, int to) {
         
        i0 = Math.max(0, from);
          
        setSize(to - i0);
    }
    
 
    
    public void move(int di) {
        i0 += di;
    }
    
    @Override
    public void setSize(int size) {
        this.size = size;
    }
    
    
    
    @Override
    public boolean isValid(Integer i) {
        return super.isValid(i + i0);
    }

    @Override
    public void discard(Integer i) {
        super.discard(i + i0);
    }


    /**
     * Safe even if underlying object is resized...
     */
    @Override
    public final Integer size() {
        return Math.max(0, Math.min(size, super.size() - i0));
    }


    @Override
    public final Number get(Integer i) {
        return super.get(i + i0);
    }

    @Override
    public final void set(Integer i, Number value) {
        super.set(i + i0, value);
    }
    
    @Override
    public final void add(Integer i, Number value) {
        super.add(i + i0, value);
    }

    
 
}


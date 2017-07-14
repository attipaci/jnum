/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila[AT]sigmyne.com>.
 * All rights reserved. 
 * 
 * This file is part of crush.
 * 
 *     crush is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     crush is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with crush.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila[AT]sigmyne.com> - initial API and implementation
 ******************************************************************************/

package jnum.data.cube2;

import jnum.data.image.Index2D;
import jnum.math.Vector3D;

public class Index2D1 extends Index2D { 
    /**
     * 
     */
    private static final long serialVersionUID = 876707605054574369L;
    private int k;

    public Index2D1() {}
    

    public Index2D1(int i, int j, int k) {
        set(i, j, k);
    }
    
    
    public Index2D1(Vector3D index) {
        this((int)Math.round(index.x()), (int)Math.round(index.y()), (int)Math.round(index.z()));
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ k;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Index2D1)) return false;    
        
        Index2D1 index = (Index2D1) o;
        if(index.k != k) return false;

        return super.equals(o);
    }
    
  
    public void set(int i, int j, int k) { super.set(i,  j); this.k = k; }
    

    public final int k() { return k; }
   
    @Override
    public String toString() {
        return super.toString() + "," + k;
    }
}

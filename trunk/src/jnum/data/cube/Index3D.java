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

package jnum.data.cube;

public class Index3D {

    private int i, j, k;
    
    public final int i() { return i; }
    
    public final int j() { return j; }
    
    public final int k() { return k; }
    
    public final void set(final int i, final int j, final int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
    
    public final void setI(final int value) {
        i = value;
    }
    
    public final void setJ(final int value) {
        j = value;
    }
    
    public final void setK(final int value) {
        k = value;
    }
}

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

package jnum.data.cube.overlay;

import jnum.data.cube.Values3D;

public class Transposed3D extends Overlay3D {

    public Transposed3D() {}
    
    public Transposed3D(Values3D values) {
        super(values);
    }
    
    @Override
    public boolean isValid(int i, int j, int k) {
        return super.isValid(k, j, i);
    }

    @Override
    public void discard(int i, int j, int k) {
        super.discard(k, j, i);
    }

    @Override
    public int sizeX() {
        return super.sizeY();
    }

    @Override
    public int sizeY() {
        return super.sizeX();
    }

    @Override
    public Number get(int i, int j, int k) {
       return super.get(k, j, i);
    }

    @Override
    public void add(int i, int j, int k, Number value) {
        super.add(k, j, i, value);
    }

    @Override
    public void set(int i, int j, int k, Number value) {
        super.set(k, j, i, value);
    }

    @Override
    public double valueAtIndex(double i, double j, double k) {
       return super.valueAtIndex(k, j, i);
    }



}

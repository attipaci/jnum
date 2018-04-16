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

import jnum.data.samples.Values1D;

public class Periodic1D extends Overlay1D {


    public Periodic1D() {}
    
    public Periodic1D(Values1D values) {
        super(values);
    }
   
    protected final int toBaseIndex(int i) {
        return toBaseIndex(i, getBasis().size());
    }
    
    
    private final int toBaseIndex(int i, int size) {
        if(i < 0) return size + (i % size);
        return i % size;
    }
    
    protected final double toBaseIndex(double i) {
        return toBaseIndex(i, getBasis().size());
    }
    
    
    private final double toBaseIndex(double i, int size) {
        double remainder = Math.IEEEremainder(i, size);
        return remainder < 0 ? remainder + size : remainder;
    }
    
    @Override
    public boolean isValid(int i) {
        return super.isValid(toBaseIndex(i));
    }

    @Override
    public void discard(int i) {
        super.discard(toBaseIndex(i));
    }

    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }

    public int period() {
        return super.size();
    }
    

    @Override
    public Number get(int i) {
        return super.get(toBaseIndex(i));
    }

    @Override
    public void set(int i, Number value) {
        super.set(toBaseIndex(i), value);
    }
    
    @Override
    public void add(int i, Number value) {
        super.add(toBaseIndex(i), value);
    }

    @Override
    public double valueAtIndex(double i) {
        return super.valueAtIndex(toBaseIndex(i));
    }
    
    
}

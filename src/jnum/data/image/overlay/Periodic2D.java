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

import jnum.data.image.Values2D;

public class Periodic2D extends Overlay2D {

    public Periodic2D(Values2D values) {
        super(values);
    }
   
    protected final int toBaseI(int i) {
        return toBaseIndex(i, getBasis().sizeX());
    }
    
    protected final int toBaseJ(int j) {
        return toBaseIndex(j, getBasis().sizeY());
    }
    
    private final int toBaseIndex(int i, int size) {
        if(i < 0) return size + (i % size);
        return i % size;
    }
    
    protected final double toBaseI(double i) {
        return toBaseIndex(i, getBasis().sizeX());
    }
    
    protected final double toBaseJ(double j) {
        return toBaseIndex(j, getBasis().sizeY());
    }
    
    private final double toBaseIndex(double i, int size) {
        double remainder = Math.IEEEremainder(i, size);
        return remainder < 0 ? remainder + size : remainder;
    }
    
    @Override
    public boolean isValid(int i, int j) {
        return super.isValid(toBaseI(i), toBaseJ(j));
    }

    @Override
    public void discard(int i, int j) {
        super.discard(toBaseI(i), toBaseJ(j));
    }

    @Override
    public int sizeX() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int sizeY() {
       return Integer.MAX_VALUE;
    }
    
    public int periodX() {
        return super.sizeX();
    }
    
    public int periodY() {
        return super.sizeY();
    }

    @Override
    public Number get(int i, int j) {
        return super.get(toBaseI(i), toBaseJ(j));
    }

    @Override
    public void set(int i, int j, Number value) {
        super.set(toBaseI(i), toBaseJ(j), value);
    }
    
    @Override
    public void add(int i, int j, Number value) {
        super.add(toBaseI(i), toBaseJ(j), value);
    }

    @Override
    public double valueAtIndex(double i, double j) {
        return super.valueAtIndex(toBaseI(i), toBaseJ(j));
    }
    
    
}

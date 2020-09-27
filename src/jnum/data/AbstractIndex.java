/*******************************************************************************
 * Copyright (c) 2020 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

import jnum.util.HashCode;

public abstract class AbstractIndex<T extends AbstractIndex<T>> implements Index<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -1273849343052525336L;

    
    @Override
    public int hashCode() {
        int hash = super.hashCode() ^ HashCode.from(dimension());
        for(int i=dimension(); --i >= 0; ) hash ^= HashCode.from(getValue(i));
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Index)) return false;
        
        Index<?> index = (Index<?>) o;
        if(index.dimension() != dimension()) return false;
        
        for(int i=dimension(); --i >= 0; ) if(index.getValue(i) != getValue(i)) return false;
        
        return true;        
    }
   
    
    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try { return (T) super.clone(); }
        catch(CloneNotSupportedException e) { return null; }
    }
    
    @Override
    public T copy() {
        return clone();
    }

    @Override
    public void reverseTo(T other) {
        int last = dimension()-1;
        for(int i=last+1; --i >= 0; ) other.setValue(last-i, getValue(i));
    }
    
    @Override
    public T getReversed() {
        T reversed = clone();
        reverseTo(reversed);
        return reversed;
    }
    
    @Override
    public double distanceTo(T index) {
        long sum = 0;
        
        for(int i=dimension(); --i >= 0; ) {
            int d = index.getValue(i) - getValue(i);
            sum += d*d;
        }
        
        return Math.sqrt(sum);
    }
    
    @Override
    public void fill(int value) {
        for(int i=dimension(); --i >= 0; ) setValue(i, value);
    }
    
    @Override
    public int increment(int index) {
        int i = getValue(index);
        setValue(index, ++i);
        return i;
    }
    
    @Override
    public int decrement(int index) {
        int i = getValue(index);
        setValue(index, --i);
        return i;
    }
    
    @Override
    public void zero() { fill(0); }
    
    @Override
    public String toString() {
       return toString(",");
    }
    
    @Override
    public String toString(String separator) {
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<dimension(); i++) buf.append((i > 0 ? separator : "") + getValue(i));
        return new String(buf);
    }
    
  
    
}

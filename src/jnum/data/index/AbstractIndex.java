/* *****************************************************************************
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
 *     Attila Kovacs  - initial API and implementation
 ******************************************************************************/

package jnum.data.index;

import jnum.util.HashCode;

/**
 * A base class for multi-dimensional data indices. It provides default implementations
 * for concrete index classes, which may in the future become default methods in the 
 * {@link Index} interface itself, once jnum is bumped to Java 9, eliminating the need
 * for this intermediary class.
 * 
 * @author Attila Kovacs
 *
 * @param <T>   the generic type of the implementing object.
 */
public abstract class AbstractIndex<T extends AbstractIndex<T>> implements Index<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -1273849343052525336L;

    
    @Override
    public int hashCode() {
        int hash = HashCode.from(dimension());
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
    public String toString() {
       return toString(",");
    }
    
    
}

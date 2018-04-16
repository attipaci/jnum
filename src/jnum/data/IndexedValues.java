/*******************************************************************************
 * Copyright (c) 2018 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data;

public interface IndexedValues<IndexType> extends Values {

    public int capacity();
    
    public int dimension();
    
    public IndexType getSize();
    
    public Number get(IndexType index);
    
    public void add(IndexType index, Number value);
    
    public void set(IndexType index, Number value);
    
    public IndexType getIndexInstance();
    
    public abstract IndexType copyOfIndex(IndexType index);
    
    public abstract boolean conformsTo(IndexType size);
         
    public boolean conformsTo(IndexedValues<IndexType> data);
    
    public abstract String getSizeString();
    
    public abstract boolean containsIndex(IndexType index);
    
    public abstract void clear(IndexType index);
    
    public abstract void scale(IndexType index, double factor);
      
}

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


public abstract class AbstractIndex<T extends AbstractIndex<T>> implements Index<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -1273849343052525336L;

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
    
    
}

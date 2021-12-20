/* *****************************************************************************
 * Copyright (c) 2021 Attila Kovacs <attila[AT]sigmyne.com>.
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

package jnum.data.overlay;

import jnum.data.Windowed;
import jnum.data.index.Index;
import jnum.data.index.IndexedValues;

public abstract class Viewport<IndexType extends Index<IndexType>> extends Overlay<IndexType> implements Windowed<IndexType> {
    private IndexType origin;
    private IndexType size;
    

    public Viewport(IndexedValues<IndexType, ?> data, IndexType from, IndexType to) { 
        super(data);
        setBounds(from, to);
    }
    
    @Override
    public IndexType getOrigin() {
        return origin;
    }
    
    @Override
    public IndexType getSize() {
        return size;
    }
    
    @Override
    public void setBounds(IndexType from, IndexType to) {
        origin = from.copy();
        size = to.copy();
        size.subtract(origin);
    }
    
    @Override
    public void move(IndexType delta) {
        origin.add(delta);
    }
    
}

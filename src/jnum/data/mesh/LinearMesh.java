/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.data.mesh;

import jnum.NonConformingException;
import jnum.math.LinearAlgebra;
import jnum.math.Scalable;

public class LinearMesh<T extends LinearAlgebra<? super T>> extends AdditiveMesh<T> implements LinearAlgebra<Mesh<T>>  {
    
    /**
     * 
     */
    private static final long serialVersionUID = -8649117834606776793L;

    public LinearMesh(Class<T> type, int[] dimensions) {
        super(type, dimensions);
    }

    public LinearMesh(Class<T> type) {
        super(type);
    }

    public LinearMesh(Object data) {
        super(data);
    }

    @Override
    public void scale(final double factor) {  
        final MeshCrawler<T> i = iterator();
  
        while(i.hasNext()) ((Scalable) i.next()).scale(factor);
    }

    @Override
    public void addScaled(final Mesh<T> o, final double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add scaled array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().addScaled(i2.next(), factor);
        }
    }

    @Override
    public boolean isNull() {
        final MeshCrawler<T> i = iterator();
        while(i.hasNext()) {
            if(!i.next().isNull()) return false;
        }
        return true;
    }

    @Override
    public void zero() {
        final MeshCrawler<T> i = iterator();
        while(i.hasNext()) {
            i.next().zero();
        }   
    }
}

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

package jnum.data.mesh;

import jnum.Function;
import jnum.NonConformingException;
import jnum.math.Additive;

public class AdditiveMesh<T extends Additive<? super T>> extends ObjectMesh<T> implements Additive<Mesh<T>>, Patchable<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -7062588300650505194L;

    public AdditiveMesh(Class<T> type, int[] dimensions) {
        super(type, dimensions);
    }

    public AdditiveMesh(Class<T> type) {
        super(type);
    }

    public AdditiveMesh(Object data) {
        super(data);
    }

    @Override
    public Mesh<T> newInstance() {
        return new AdditiveMesh<>(elementClass);
    }

    @Override
    public void add(final Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().add(i2.next());
        }
    }

    @Override
    public void subtract(final Mesh<T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot subtract array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<T> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().subtract(i2.next());
        }
    }

    @Override
    public void setSum(final Mesh<T> a, final Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<T> iA = a.iterator();
        final MeshCrawler<T> iB = b.iterator();
        
        while(i.hasNext()) {
            i.next().setSum(iA.next(), iB.next());
        }
    }

    @Override
    public void setDifference(final Mesh<T> a, final Mesh<T> b) {
        if(!a.conformsTo(this)) throw new NonConformingException("non-conforming first argument.");
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<T> iA = a.iterator();
        final MeshCrawler<T> iB = b.iterator();
        
        while(i.hasNext()) {
           i.next().setDifference(iA.next(), iB.next());
        }
    }

    public void add(final T offset) {
        final MeshCrawler<T> i = iterator();
        while(i.hasNext()) i.next().add(offset);
    }
   
    public void subtract(final T offset) {
        final MeshCrawler<T> i = iterator();
        while(i.hasNext()) i.next().subtract(offset);
    }
    
    
    @Override
    public void addPatchAt(double[] exactOffset, Function<double[], T> shape, double[] patchSize) {
         
        final int[] from = new int[exactOffset.length];
        final int[] to = new int[exactOffset.length];
        final double[] d = new double[exactOffset.length];
       
        final int size[] = getSize();
        final int index[] = new int[size.length];
        
        for(int i=from.length; --i >=0; ) {
            from[i] = Math.max(0, (int) Math.floor(exactOffset[i]));
            if(from[i] > size[i]) return; // The patch is outside of the available range...
            to[i] = Math.min(size[i], (int) Math.ceil(exactOffset[i] + patchSize[i]));
        }
        
        MeshCrawler<T> i = iterator(from, to);
        
        while(i.hasNext()) {
            i.getPosition(index);
            for(int k=index.length; --k >= 0; ) d[k] = index[k] + exactOffset[k] - (from[k]<<1);
            i.next().add(shape.valueAt(d));
        } 
    }
    
    
    
    
}

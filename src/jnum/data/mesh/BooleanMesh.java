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

import jnum.CopyCat;
import jnum.NonConformingException;
import jnum.Util;

public class BooleanMesh extends Mesh<Boolean> implements CopyCat<Mesh<Boolean>> {
    /**
     * 
     */
    private static final long serialVersionUID = -326775064571956665L;

    public BooleanMesh(int[] dimensions) {
        super(boolean.class, dimensions);
    }

    public BooleanMesh() {
        super(boolean.class);
    }

    public BooleanMesh(Object data) {
        super(data);
    }
    
    @Override
    public void copy(Mesh<Boolean> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot copy mesh of different size/shape.");

        final MeshCrawler<Boolean> i = iterator();
        final MeshCrawler<Boolean> oi = o.iterator();
        while(i.hasNext()) {
            i.setNext(oi.next());
        }
    }
    

    @Override
    protected Boolean linearElementAt(Object linearArray, int index) {
        return ((boolean[]) linearArray)[index];
    }

    @Override
    protected void setLinearElementAt(Object linearArray, int index, Boolean value) {
        ((boolean[]) linearArray)[index] = value;
    }

    @Override
    public Boolean parseElement(String text) throws Exception {
        return Util.parseBoolean(text);
    }
    
    @Override
    public Mesh<Boolean> newInstance() {
        return new BooleanMesh();
    }
    
    public boolean containsTrue() {
        final MeshCrawler<Boolean> i = iterator();
        while(i.hasNext()) if(i.next()) return true;
        return false;
    }
    
    public boolean containsFalse() {
        final MeshCrawler<Boolean> i = iterator();
        while(i.hasNext()) if(!i.next()) return true;
        return false;
    }
    
    public void not() {
        final MeshCrawler<Boolean> i = iterator();
        while(i.hasNext()) i.setCurrent(!i.next());
    }
    
    public void and(Mesh<Boolean> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot logical AND mesh of different size/shape.");        
        final MeshCrawler<Boolean> i = iterator();
        final MeshCrawler<Boolean> oi = o.iterator();
        while(i.hasNext()) i.setCurrent(i.next() && oi.next());
    }
    
    public void or(Mesh<Boolean> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot logical OR mesh of different size/shape.");        
        final MeshCrawler<Boolean> i = iterator();
        final MeshCrawler<Boolean> oi = o.iterator();
        while(i.hasNext()) i.setCurrent(i.next() || oi.next());
    }
    
  

}

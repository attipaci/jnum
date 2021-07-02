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

package jnum.data.mesh;

import jnum.CopyCat;
import jnum.NonConformingException;
import jnum.math.Vector2D;

public class Vector2DMesh<T extends Vector2D> extends LinearMesh<T> implements CopyCat<Mesh<? extends T>> {
  
    /**
     * 
     */
    private static final long serialVersionUID = 5934790128553730437L;

    public Vector2DMesh(Class<T> type, int... dimensions) {
        super(type, dimensions);
    }

    public Vector2DMesh(Class<T> type) {
        super(type);
    }

    public Vector2DMesh(Object data) {
        super(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void copy(Mesh<? extends T> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot copy mesh of different size/shape.");

        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends T> oi = o.iterator();
        while(i.hasNext()) {
            i.setNext((T) oi.next().copy());
        }
    }
    
    public void addScaledX(final Mesh<? extends Number> o, final double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot scaled add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().addX(i2.next().doubleValue() * factor);
        }
    }

    public void addX(final Mesh<? extends Number> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().addX(i2.next().doubleValue());
        }
    }

    public void subtractX(final Mesh<? extends Number> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot subtract array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().subtractX(i2.next().doubleValue());
        }
    }

    
    public void addScaledY(final Mesh<? extends Number> o, final double factor) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot scaled add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().addY(i2.next().doubleValue() * factor);
        }
    }

    public void addY(final Mesh<? extends Number> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot add array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().addY(i2.next().doubleValue());
        }
    }

    public void subtractY(final Mesh<? extends Number> o) {
        if(!o.conformsTo(this)) throw new NonConformingException("cannot subtract array of different size/shape.");
        
        final MeshCrawler<T> i = iterator();
        final MeshCrawler<? extends Number> i2 = o.iterator();
        
        while(i.hasNext()) {
            i.next().subtractY(i2.next().doubleValue());
        }
    }
    
    public DoubleMesh abs() {
        DoubleMesh A = new DoubleMesh(getSize());
        
        MeshCrawler<T> i = iterator();
        MeshCrawler<Double> ai = A.iterator();
        
        while(i.hasNext()) ai.setNext(i.next().abs());
        
        return A;  
    }
    
    public DoubleMesh angle() {
        DoubleMesh phi = new DoubleMesh(getSize());
        
        MeshCrawler<T> i = iterator();
        MeshCrawler<Double> ai = phi.iterator();
        
        while(i.hasNext()) ai.setNext(i.next().angle());
        
        return phi;  
    }
    
    public DoubleMesh asquare() {
        DoubleMesh A2 = new DoubleMesh(getSize());
        
        MeshCrawler<T> i = iterator();
        MeshCrawler<Double> ai = A2.iterator();
        
        while(i.hasNext()) ai.setNext(i.next().absSquared());
        
        return A2;  
    }
    
    
}

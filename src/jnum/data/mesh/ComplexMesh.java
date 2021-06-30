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

import jnum.NonConformingException;
import jnum.math.Complex;
import jnum.math.ComplexAddition;
import jnum.math.ComplexConjugate;
import jnum.math.ComplexMultiplication;
import jnum.math.ComplexScaling;


public class ComplexMesh extends Vector2DMesh<Complex> implements ComplexAddition, ComplexConjugate, ComplexScaling,
    ComplexMultiplication<ComplexMesh> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8988091835034713978L;

    public ComplexMesh(int[] dimensions) {
        super(Complex.class, dimensions);
        MeshCrawler<Complex> i = iterator();
        while(i.hasNext()) i.setNext(new Complex());   
    }

    public ComplexMesh() {
        super(Complex.class);
    }

    public ComplexMesh(Object data) {
        super(data);
    }
   
    @Override
    public Mesh<Complex> newInstance() {
        return new ComplexMesh();
    }

  
    
    @Override
    public void conjugate() {
        final MeshCrawler<Complex> iterator = iterator();
        while(iterator.hasNext()) {
            iterator.next().conjugate();
        }
    }

    @Override
    public void add(final Complex x) {
        super.add(x);
    }
    
    @Override
    public void add(double re, double im) {
        add(new Complex(re, im));
    }

    @Override
    public void subtract(final Complex x) {
       subtract(x);
    }

    @Override
    public void scale(final Complex x) {
        final MeshCrawler<Complex> iterator = iterator();
        while(iterator.hasNext()) {
            iterator.next().scale(x);
        }
    }

    @Override
    public void multiplyBy(final Complex factor) {
        scale(factor);
    }
    
    @Override
    public void multiplyByI() {
        final MeshCrawler<Complex> iterator = iterator();
        while(iterator.hasNext()) {
            iterator.next().multiplyByI();
        }
    }

    @Override
    public void setProduct(final Complex a, final ComplexMesh b) {
        if(!b.conformsTo(this)) throw new NonConformingException("non-conforming second argument.");
        
        final MeshCrawler<Complex> i = iterator();
        final MeshCrawler<Complex> iB = b.iterator();
        
        while(i.hasNext()) {
            i.next().setProduct(a, iB.next());
        }
    }

    public final void addScaledReal(final Mesh<? extends Number> o, final double factor) {
        addScaledX(o, factor);
    }

    public final void addReal(final Mesh<? extends Number> o) {
        addX(o);
    }

    public final void subtractReal(final Mesh<? extends Number> o) {
       subtractX(o);
    }

    
    public final void addScaledImaginary(final Mesh<? extends Number> o, final double factor) {
        addScaledY(o, factor);
    }

    public final void addImaginary(final Mesh<? extends Number> o) {
        addY(o);
    }

    public final void subtractImaginary(final Mesh<? extends Number> o) {
        subtractY(o);
    }
    
    
}
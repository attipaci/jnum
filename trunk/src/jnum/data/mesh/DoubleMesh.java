/*******************************************************************************
 * Copyright (c) 2017 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/

package jnum.data.mesh;

import java.lang.Double;

// TODO: Auto-generated Javadoc
/**
 * The Class DoubleMesh.
 */
public class DoubleMesh extends PrimitiveMesh<Double> {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6960364610546704958L;

    /**
     * Instantiates a new double mesh.
     *
     * @param dimensions the dimensions
     */
    public DoubleMesh(int[] dimensions) {
        super(Double.class, dimensions);
    }

    /**
     * Instantiates a new double mesh.
     */
    public DoubleMesh() {
        super(Double.class);
    }

    /**
     * Instantiates a new double mesh.
     *
     * @param data the data
     */
    public DoubleMesh(Object data) {
        this();
        setData(data);
    }   
     
    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getScaled(java.lang.Number, double)
     */
    @Override
    protected final Double getScaled(Double value, double factor) {
        return factor * value;
    }

  
    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getSum(java.lang.Number, java.lang.Number)
     */
    @Override
    protected final Double getSumOf(Number a, Number b) {
        return a.doubleValue() + b.doubleValue();
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getDifference(java.lang.Number, java.lang.Number)
     */
    @Override
    protected final Double getDifferenceOf(Number a, Number b) {
        return a.doubleValue() - b.doubleValue();
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected final Double zeroValue() {
        return 0.0;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected final Double lineElementAt(Object simpleArray, int index) {
        return ((double[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected final void setLineElementAt(Object simpleArray, int index, Double value) {
        ((double[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Double parseElement(String text) throws Exception {
        return Double.parseDouble(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Double> newInstance() {
        return new DoubleMesh();
    }



}

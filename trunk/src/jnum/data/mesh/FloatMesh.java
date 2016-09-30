/*******************************************************************************
 * Copyright (c) 2016 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
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

import java.lang.Float;

// TODO: Auto-generated Javadoc
/**
 * The Class FloatMesh.
 */
public class FloatMesh extends PrimitiveMesh<Float> {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4081004880332314702L;

    /**
     * Instantiates a new float mesh.
     *
     * @param dimensions the dimensions
     */
    public FloatMesh(int[] dimensions) {
        super(Float.class, dimensions);
    }

    /**
     * Instantiates a new float mesh.
     */
    public FloatMesh() {
        super(Float.class);
    }

    /**
     * Instantiates a new float mesh.
     *
     * @param data the data
     */
    public FloatMesh(Object data) {
        this();
        setData(data);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getScaled(java.lang.Number, double)
     */
    @Override
    protected Float getScaled(Float value, double factor) {
        return (float) factor * value;
    }

  
    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getSum(java.lang.Number, java.lang.Number)
     */
    @Override
    protected Float getSum(Number a, Number b) {
        return a.floatValue() + b.floatValue();
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#getDifference(java.lang.Number, java.lang.Number)
     */
    @Override
    protected Float getDifference(Number a, Number b) {
        return a.floatValue() - b.floatValue();
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected Float zeroValue() {
        return 0.0F;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected Float baseLineElementAt(Object simpleArray, int index) {
        return ((float[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected void setBaseLineElementAt(Object simpleArray, int index, Float value) {
        ((float[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Float parseElement(String text) throws Exception {
        return Float.parseFloat(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#subarrayAt(int[])
     */
    @Override
    public Mesh<Float> subarrayAt(int[] index) {
        return new FloatMesh(subarrayDataAt(index));
    }

    /* (non-Javadoc)
     * @see jnum.math.LinearAlgebra#addMultipleOf(java.lang.Object, double)
     */
    @Override
    public void addMultipleOf(PrimitiveMesh<?> o, double factor) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#add(java.lang.Object)
     */
    @Override
    public void add(PrimitiveMesh<?> o) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#subtract(java.lang.Object)
     */
    @Override
    public void subtract(PrimitiveMesh<?> o) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#setSum(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setSum(PrimitiveMesh<?> a, PrimitiveMesh<?> b) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jnum.math.Additive#setDifference(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setDifference(PrimitiveMesh<?> a, PrimitiveMesh<?> b) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Float> newInstance() {
        return new FloatMesh();
    }

}

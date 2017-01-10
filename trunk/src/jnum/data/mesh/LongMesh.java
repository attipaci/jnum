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

public class LongMesh extends NumberMesh<Long> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 7707405135422248118L;

    /**
     * Instantiates a new double mesh.
     *
     * @param dimensions the dimensions
     */
    public LongMesh(int[] dimensions) {
        super(Long.class, dimensions);
    }

    /**
     * Instantiates a new double mesh.
     */
    public LongMesh() {
        super(Long.class);
    }

    /**
     * Instantiates a new double mesh.
     *
     * @param data the data
     */
    public LongMesh(Object data) {
        this();
        setData(data);
    }   
    
    @Override
    public final Long convert(Number x) { return x.longValue(); }
    
    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#zeroValue()
     */
    @Override
    protected final Long zeroValue() {
        return 0L;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#baseLineElementAt(java.lang.Object, int)
     */
    @Override
    protected final Long linearElementAt(Object simpleArray, int index) {
        return ((long[]) simpleArray)[index];
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.PrimitiveMesh#setBaseLineElementAt(java.lang.Object, int, java.lang.Number)
     */
    @Override
    protected final void setLinearElementAt(Object simpleArray, int index, Long value) {
        ((long[]) simpleArray)[index] = value;
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#parseElement(java.lang.String)
     */
    @Override
    public Long parseElement(String text) throws Exception {
        return Long.decode(text);
    }

    /* (non-Javadoc)
     * @see jnum.data.mesh.Mesh#newInstance()
     */
    @Override
    public Mesh<Long> newInstance() {
        return new LongMesh();
    }
    
    @Override
    public final Long getSumOf(Number a, Number b) {
        return a.longValue() + b.longValue();
    }
    
    @Override
    public final Long getDifferenceOf(Number a, Number b) {
        return a.longValue() - b.longValue();
    }
    
    @Override
    public final Long getProductOf(Number a, Number b) {
        return a.longValue() * b.longValue();
    }
    
    @Override
    public final Long getRatioOf(Number a, Number b) {
        return a.longValue() / b.longValue();
    }

}
